package sjw.spring.web.search;

import java.io.*;
import java.util.*;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

public class IndexTest {

    private String docPath;
    private List<String> docList;
    private int numofDocs;
    /** documentFrequency[term] = term_frequency **/
    private HashMap<String, Integer> documentFrequency = new HashMap<>();
    /** documentVector[docId][termId] = tfidf **/
    private HashMap<Integer, HashMap<String, Map<Integer, Double>>> documentVector = new HashMap<>();
    /** forwardIndexTable[docId][term] = term_frequency **/
    private HashMap<Integer, Map<String, Integer>> forwardIndexTable = new HashMap<>();
    /** backwardIndexTable[term][docId] = term_frequency **/
    private HashMap<String, Map<Integer, Integer>> backwardIndexTable = new HashMap<>();
    /** termList[sequence(termId)] = term **/
    private List<String> termList = new ArrayList<>();
    /** termTable[term][startLocation] = df **/
    private HashMap<String, Integer[]> termTable = new HashMap<>();
    /** postingFileList[sequence][docId] = tfidf **/
    private List<HashMap<Integer, Double>> postingFileList = new ArrayList<>();

    public IndexTest(String docPath, List<String> docList, int numofDocs) {
        this.docPath = docPath;
        this.docList = docList;
        this.numofDocs = numofDocs;
    }

    private void forwardIndexing() throws IOException {

        File wordsFile = new File("D:\\Spring Projects\\enginetest\\words");
        wordsFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordsFile));
        int docId = 1;

        for (String docName : docList) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(docPath + docName), "euc-kr"));
            String text = "";
            String content = "";
            while ((text = reader.readLine()) != null) {
                content += text;
            }
            reader.close();

            // extract nouns
            Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
            KomoranResult analyzeResult = komoran.analyze(content);
            List<String> nouns = analyzeResult.getNouns();

            // set term list, words file and document frequency
            for (String noun : nouns) {
                if (termList.contains(noun) && documentFrequency.containsKey(noun)) {
                    Integer freq = documentFrequency.get(noun);
                    documentFrequency.put(noun, freq + 1);
                    continue;
                } else {
                    termList.add(noun);
                    documentFrequency.put(noun, 1);
                    writer.write(noun + '\n');
                }
            }

            // count frequency of each terms and set forward index table
            Set<String> nounset = Set.copyOf(nouns);
            Map<String, Integer> v = new HashMap<>();
            for (String noun : nounset) {
                int cnt = (int)nouns.stream().filter(n -> n.equals(noun)).count();
                v.put(noun, cnt);
            }
            forwardIndexTable.put(docId, v);
            docId += 1;
        }

        writer.flush();
        writer.close();

        System.out.println("forwardIndexing() complete.");
    }

    private void setDocumentVector() throws IOException {

        File documentVectorFile = new File("D:\\Spring Projects\\enginetest\\documentvector.dat");
        documentVectorFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(documentVectorFile));

        for (int docId = 1; docId <= numofDocs; docId++) {

            if (forwardIndexTable.get(docId).isEmpty()) {
                writer.write("null\n");
                continue;
            }

            HashMap<String, Map<Integer, Double>> vector = new HashMap<>();

            // calculate tf-idf
            for (int termId = 1; termId <= termList.size(); termId++) {
                String term = termList.get(termId - 1);
                Double idf = Math.log((double)numofDocs / (double)documentFrequency.get(term)) / Math.log(2);

                if (forwardIndexTable.get(docId).get(term) == null) continue;
                int termFreq = forwardIndexTable.get(docId).get(term);
                int totalNumofTerm = 0;

                List<Integer> fitValuesList = new ArrayList<>(forwardIndexTable.get(docId).values());
                for (Integer freq : fitValuesList) {
                    totalNumofTerm += freq;
                }

                Double tf = (double)termFreq / (double)totalNumofTerm;
                Double tfidf = tf * idf;

                // make document vector
                if (termFreq != 0) {
                    Map<Integer, Double> v = new HashMap<>();
                    v.put(termId, tfidf);
                    vector.put(term, v);
                }
                documentVector.put(docId, vector);
            }

            // document vector file write
            for (String term : termList) {
                if (vector.containsKey(term)) {
                    int termId = termList.indexOf(term) + 1;
                    double tfidf = vector.get(term).get(termId);
                    writer.write(termId + ":" + tfidf + " ");
                }
            }
            writer.write("\n");
        }

        writer.flush();
        writer.close();

        System.out.println("setDocumentVector() complete.");
    }

    private void backwardIndexing() {

        for (String term : termList) {

            Map<Integer, Integer> invvector = new HashMap<>();

            // set vector (docId, tf)
            for (int docId = 1; docId <= numofDocs; docId++) {
                Map<String, Integer> docIdMap = forwardIndexTable.get(docId);
                if (docIdMap.containsKey(term)) {
                    int tf = docIdMap.get(term);
                    invvector.put(docId, tf);
                }
            }

            // set backward index table of this term
            backwardIndexTable.put(term, invvector);
        }

        System.out.println("backwardIndexing() complete.");
    }

    private void setInvertedFile() throws IOException {

        int startLoc = 0;

        // write term table file
        File termtableFile = new File("D:\\Spring Projects\\enginetest\\termtable");
        termtableFile.createNewFile();
        BufferedWriter ttwriter = new BufferedWriter(new FileWriter(termtableFile));

        for (String term : termList) {

            // set posting file list
            int termId = termList.indexOf(term) + 1;
            Set<Integer> docIdSet = backwardIndexTable.get(term).keySet();
            List<Integer> docIdList = new ArrayList<>(docIdSet);
            Collections.sort(docIdList);
            HashMap<Integer, Double> docTfidfMap = new HashMap<>();

            for (Integer docId : docIdList) {

                HashMap<String, Map<Integer, Double>> vector = documentVector.get(docId);

                if (!vector.containsKey(term)) continue;
                double tfidf = vector.get(term).get(termId);
                docTfidfMap.put(docId, tfidf);
            }

            postingFileList.add(docTfidfMap);

            // set term table { 'term': (start location, number of document) }
            int df = backwardIndexTable.get(term).size();
            termTable.put(term, new Integer[]{startLoc, df});

            // save term table file
            ttwriter.write(term + " " + startLoc + " " + df + "\n");
            startLoc += df;
        }

        ttwriter.flush();
        ttwriter.close();

        // write posting file
        File postingFile = new File("D:\\Spring Projects\\enginetest\\postingfile");
        postingFile.createNewFile();
        BufferedWriter pfwriter = new BufferedWriter(new FileWriter(postingFile));

        for (HashMap<Integer, Double> docTfidfMap : postingFileList) {
            List<Integer> docIdList = new ArrayList<>(docTfidfMap.keySet());
            Collections.sort(docIdList);
            for (Integer docId : docIdList) {
                pfwriter.write(docId + ":" + docTfidfMap.get(docId) + " ");
            }
        }

        pfwriter.flush();
        pfwriter.close();

        System.out.println("setInvertedFile() complete.");
    }

    public void Indexing() throws IOException {
        forwardIndexing();
        setDocumentVector();
        backwardIndexing();
        setInvertedFile();
    }
}