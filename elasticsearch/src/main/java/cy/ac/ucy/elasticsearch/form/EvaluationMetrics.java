package cy.ac.ucy.elasticsearch.form;


/** This class is used to represent as object the evaluation metrics
 *
 * @author Chrysovalantis Christodoulous
 */
public class EvaluationMetrics {
    private double precision;
    private double recall;
    private double f1score;

    public EvaluationMetrics() {

        this.precision = 0;
        this.recall = 0;
        this.f1score = 0;
    }

    public EvaluationMetrics(double precision, double recall, double fscore) {
        this.precision = precision;
        this.recall = recall;
        this.f1score = fscore;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getFscore() {
        return f1score;
    }

    public void setFscore(double fscore) {
        this.f1score = fscore;
    }

    @Override
    public String toString() {
        return "EvaluationMetrics{" +
                ", precision=" + precision +
                ", recall=" + recall +
                ", fscore=" + f1score +
                '}';
    }
}
