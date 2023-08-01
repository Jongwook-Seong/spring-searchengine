package sjw.spring.web.search;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SearchTest {

    private String filePath;
    /** termTable[term][startLocation] = df **/
    private HashMap<String, Integer[]> termTable = new HashMap<>();
    /** postingFileList[sequence][docId] = tfidf **/
    private List<HashMap<Integer, Double>> postingFileList = new ArrayList<>();

    public SearchTest(String filePath) {
        this.filePath = filePath;
    }

    private void readTermTableFile() throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(filePath + "termtable"));

        for (String line : lines) {
            String[] record = line.split(" ");
            termTable.put(record[0],
                    new Integer[]{Integer.parseInt(record[record.length - 2]),
                            Integer.parseInt(record[record.length - 1])});
        }

        System.out.println("readTermTableFile() complete.");
    }

    private void readPostingFile() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(filePath + "postingfile"));

        String postingfileData = reader.readLine();
        String[] data = postingfileData.split(" ");
        for (String datum : data) {
            int docId = Integer.parseInt(datum.split(":")[0]);
            double tfidf = Double.parseDouble(datum.split(":")[1]);
            HashMap<Integer, Double> v = new HashMap<>();
            v.put(docId, tfidf);
            postingFileList.add(v);
        }

        reader.close();
        System.out.println("readPostingFile() complete.");
    }

    private HashMap<Integer, Double> readDocumentVector(int docId) throws IOException {

        HashMap<Integer, Double> vectorOfDoc = new HashMap<>();

        List<String> lines = Files.readAllLines(Paths.get(filePath + "documentvector.dat"));
        String docIdVector = lines.get(docId - 1);

        if (docIdVector.equals("null")) return null;

        String[] elemofVector = docIdVector.split(" ");
        for (String elem : elemofVector) {
            int termId = Integer.parseInt(elem.split(":")[0]);
            double tfidf = Double.parseDouble(elem.split(":")[1]);
            vectorOfDoc.put(termId, tfidf);
        }

        System.out.println("readDocumentVector() complete.");
        return vectorOfDoc;
    }

    private QueryTermData analyzeQuery(String query, int numofDocs) {

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        KomoranResult analyzeResult = komoran.analyze(query);
        List<String> qNounList = analyzeResult.getNouns();
        Set<String> qNounSet = Set.copyOf(qNounList);

        HashMap<String, Integer> qtermCount = new HashMap<>();
        HashMap<String, Double> qtermWeight = new HashMap<>();

        for (String qterm : qNounList) {
            if (!qtermCount.containsKey(qterm)) qtermCount.put(qterm, 1);
            else qtermCount.put(qterm, qtermCount.get(qterm) + 1);
        }

        for (String qterm : qNounSet) {
            double qtf = 0, idf = 0;
            if (termTable.containsKey(qterm)) {
                int df = termTable.get(qterm)[1];
                idf = Math.log((double) numofDocs / (double)df) / Math.log(2);
                qtf = (double)qtermCount.get(qterm) / (double)qNounList.size();
            }
            double qtfidf = (0.5 + 0.5 * qtf) * idf;
            qtermWeight.put(qterm, qtfidf);
        }

        QueryTermData qtermData = new QueryTermData();
        qtermData.setQterms(new ArrayList<>(qNounSet));
        qtermData.setQtermWeight(qtermWeight);
        System.out.println("analyzeQuery() complete.");
        return qtermData;
    }

    private List<Integer> calcSimilarity(QueryTermData qtermData) throws IOException {

        List<Integer> hasTermDocIdList = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath + "words"));
        List<String> qterms = qtermData.getQterms();
        HashMap<String, Double> qtermWeight = qtermData.getQtermWeight();
        HashMap<Integer, Double> similarityOfDoc = new HashMap<>();

        // remove null term
        List<String> toRemove = new ArrayList<>();
        for (String qterm : qterms) {
            if (!termTable.keySet().contains(qterm)) {
                toRemove.add(qterm);
            }
        }
        for (String rterm : toRemove) {
            qterms.remove(rterm);
        }

        // check term of query which document it has
        for (String qterm : qterms) {

            int startLoc = termTable.get(qterm)[0];
            int df = termTable.get(qterm)[1];

            List<HashMap<Integer, Double>> postingFileSublist = postingFileList.subList(startLoc, startLoc + df + 1);

            for (HashMap<Integer, Double> pfelem : postingFileSublist) {
                Set<Integer> docIdSet = pfelem.keySet();
                for (Integer docId : docIdSet) {
                    if (!hasTermDocIdList.contains(docId))
                        hasTermDocIdList.add(docId);
                }
            }

            for (Integer docId : hasTermDocIdList) {
                // read document vector
                HashMap<Integer, Double> vectorOfDoc = readDocumentVector(docId);

                if (vectorOfDoc == null) {
                    similarityOfDoc.put(docId, 0.0);
                    continue;
                }

                List<Integer> termIdList = new ArrayList<>(vectorOfDoc.keySet());

                double sumofWeightSqrOfDoc = 0;
                double sumofWeightOfDocXQuery = 0;
                double sumofWeightSqrOfQuery = 0;

                // calculate cosine similarity
                for (Integer termId : termIdList) {
                    double weightOfDoc = vectorOfDoc.get(termId);
                    sumofWeightSqrOfDoc += weightOfDoc * weightOfDoc;
                    String term = lines.get(termId - 1);
                    if (qterms.contains(term))
                        sumofWeightOfDocXQuery += weightOfDoc * qtermWeight.get(term);
                }

                for (String qt : qterms)
                    sumofWeightSqrOfQuery += qtermWeight.get(qt) * qtermWeight.get(qt);
                double similarity = sumofWeightOfDocXQuery /
                        (Math.sqrt(sumofWeightSqrOfDoc) * Math.sqrt(sumofWeightSqrOfQuery));
                similarityOfDoc.put(docId, similarity);
            }
        }

        // sort by similarity reverse
        List<Double> simTempList = new ArrayList<>();
        List<Integer> sortedSimOfDocList = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : similarityOfDoc.entrySet()) {
            simTempList.add(entry.getValue());
        }
        Collections.sort(simTempList, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o2.compareTo(o1);
            }
        });
        for (Double sim : simTempList) {
            for (Map.Entry<Integer, Double> entry : similarityOfDoc.entrySet()) {
                if (entry.getValue().equals(sim)) {
                    sortedSimOfDocList.add(entry.getKey());
                }
            }
        }

        System.out.println("calcSimilarity() complete.");
        return sortedSimOfDocList;
    }

    private List<String> searchDocuments(QueryTermData qtermData, List<String> docNameList) throws IOException {

        List<String> sortedSearchDocNameList = new ArrayList<>();
        List<Integer> sortedSimOfDocList = calcSimilarity(qtermData);
        for (Integer docId : sortedSimOfDocList) {
            sortedSearchDocNameList.add(docNameList.get(docId - 1));
        }

        System.out.println("searchDocuments() complete.");
        return sortedSearchDocNameList;
    }

    private void readData() throws IOException {
        readTermTableFile();
        readPostingFile();
    }

    public List<String> Searching(String query, List<String> docNameList, int numofDocs) throws IOException {
        readData();
        QueryTermData qtermData = analyzeQuery(query, numofDocs);
        List<String> searchedDocResult = searchDocuments(qtermData, docNameList);
        return searchedDocResult;
    }
}