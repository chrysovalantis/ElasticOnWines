package cy.ac.ucy.elasticsearch.form;

import java.util.HashSet;
import java.util.TreeMap;

public class QueryEvaluation {
    private int index;
    private TreeMap<Integer, Integer> related_docs;

    public QueryEvaluation(int index) {
        this.index = index;
        this.related_docs = new TreeMap<>();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TreeMap<Integer, Integer> getRelated_docs() {
        return related_docs;
    }

    public void setRelated_docs(TreeMap<Integer, Integer> related_docs) {
        this.related_docs = related_docs;
    }

    @Override
    public String toString() {
        return "QueryEvaluation{" +
                "index=" + index +
                ", related_docs=" + related_docs +
                '}';
    }
}
