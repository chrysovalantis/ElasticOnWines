package cy.ac.ucy.elasticsearch.model;

import java.util.Arrays;
import java.util.HashSet;

public class QueryResponse {

    private Query q;
    private HashSet<Integer> docIDs;

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
