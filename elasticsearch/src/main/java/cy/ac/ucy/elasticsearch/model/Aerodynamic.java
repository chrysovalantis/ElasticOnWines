package cy.ac.ucy.elasticsearch.model;

/** This class is used to represent as object the content of the cran.all.1400 file
 *
 * @author Chrysovalantis Christodoulous
 */
public class Aerodynamic {

    private int index;
    private String title;
    private String author;
    private String bibliography;
    private String text;

    public Aerodynamic(int index, String title, String author, String bibliography, String text) {
        this.index = index;
        this.title = title;
        this.author = author;
        this.bibliography = bibliography;
        this.text = text;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBibliography() {
        return bibliography;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Aerodynamic{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", bibliography='" + bibliography + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
