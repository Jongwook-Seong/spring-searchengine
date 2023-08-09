package sjw.spring.web.searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryTermData {

    private List<String> qterms = new ArrayList<>();
    private HashMap<String, Double> qtermWeight = new HashMap<>();

    public List<String> getQterms() {
        return qterms;
    }

    public void setQterms(List<String> qterms) {
        this.qterms = qterms;
    }

    public HashMap<String, Double> getQtermWeight() {
        return qtermWeight;
    }

    public void setQtermWeight(HashMap<String, Double> qtermWeight) {
        this.qtermWeight = qtermWeight;
    }
}
