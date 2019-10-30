package cy.ac.ucy.elasticsearch.form;

public class SearchResp {

    private String response;

    public SearchResp(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "SearchResp{" +
                "response='" + response + '\'' +
                '}';
    }
}
