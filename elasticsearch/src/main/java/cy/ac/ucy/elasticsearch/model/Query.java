package cy.ac.ucy.elasticsearch.model;

public class Query {

    private int index;
    private String query;

    public Query(int index, String query) {
        this.index = index;
        this.query = query;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "Query{" +
                "index=" + index +
                ", query='" + query + '\'' +
                '}';
    }
}
