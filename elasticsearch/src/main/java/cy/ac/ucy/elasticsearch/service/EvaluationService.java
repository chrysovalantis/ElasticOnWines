package cy.ac.ucy.elasticsearch.service;

import cy.ac.ucy.elasticsearch.form.EvaluationMetrics;
import cy.ac.ucy.elasticsearch.form.QueryEvaluation;
import cy.ac.ucy.elasticsearch.model.QueryResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EvaluationService {


    public static ArrayList<EvaluationMetrics> calculateEvaluationMetrics(ArrayList<QueryResponse> results, ArrayList<QueryEvaluation> evaluations){

        ArrayList<EvaluationMetrics> metricsList = new ArrayList<>();

        for (QueryResponse result : results){
            double tp = 0;                              //results that elastic returns and also are includes in the evaluation file
            double fp = 0;                              //results that elastic returns and not included in the evaluation file
            double fn = 0;                              //results that included in the evaluation file but elastic search did't bring them

            for (QueryEvaluation eval : evaluations){
                if (eval==null)
                    continue;
                if (result.getQ().getIndex() == eval.getIndex()){
                    for (int docID : result.getDocIDs()){
                        if (eval.getRelated_docs().containsKey(docID)){
                            tp++;
                        }else{
                            fp++;
                        }
                    }
                    fn = eval.getRelated_docs().size() - tp;
                    double precision = tp/(tp+fp);
                    double recall = tp/(tp+fn);
                    double f1score = 2 * (precision * recall)/(precision+recall);
                    if (Double.isNaN(f1score)){
                        f1score=0;
                    }
                    EvaluationMetrics metrics = new EvaluationMetrics(precision,recall,f1score);
                    System.out.println(metrics);
                    metricsList.add(metrics);
                    break;
                }
            }
        }

        return metricsList;
    }

    public static EvaluationMetrics calculateAverageMetrics(ArrayList<EvaluationMetrics> metrics){

        EvaluationMetrics averageMetrics = new EvaluationMetrics();
        double total_precision = 0;
        double total_recall = 0;
        double total_f1score = 0;
        int count = 0;

        for (EvaluationMetrics eval : metrics){
            total_precision += eval.getPrecision();
            total_recall += eval.getRecall();
            total_f1score += eval.getFscore();
            count++;
        }
        averageMetrics.setPrecision(total_precision/count);
        averageMetrics.setRecall(total_recall/count);
        averageMetrics.setFscore(total_f1score/count);

        return averageMetrics;
    }


}
