package cy.ac.ucy.elasticsearch.model;

import java.util.HashSet;


/** This class is used to represent a query and the relative documents that elastic search returns
 *
 * @author Chrysovalantis Christodoulous
 */
public class QueryResponse {

    private Query q;                            //the query we sent to elasticsearch
    private HashSet<Integer> docIDs;            //the relative document from elasticsearch response

    public QueryResponse(Query q) {
        this.q = q;
        this.docIDs = new HashSet<>();
    }

    public Query getQ() {
        return q;
    }

    public void setQ(Query q) {
        this.q = q;
    }

    public HashSet<Integer> getDocIDs() {
        return docIDs;
    }

    public void setDocIDs(HashSet<Integer> docIDs) {
        this.docIDs = docIDs;
    }

    @Override
    public String toString() {
        return "QueryResponse{" +
                "q=" + q +
                ", docIDs=" + docIDs +
                '}';
    }
}
