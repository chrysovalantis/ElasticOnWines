package cy.ac.ucy.elasticsearch.form;

import java.util.TreeMap;

/** This class is used to represent as object the cranqrel document content
 *
 * @author Chrysovalantis Christodoulous
 */
public class QueryEvaluation {
    private int index;                                      //query id
    private TreeMap<Integer, Integer> related_docs;         //key: doc id, value: relative value (-1,4)

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
