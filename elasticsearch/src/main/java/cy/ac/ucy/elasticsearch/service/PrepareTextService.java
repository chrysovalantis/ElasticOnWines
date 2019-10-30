package cy.ac.ucy.elasticsearch.service;

import cy.ac.ucy.elasticsearch.form.QueryEvaluation;
import cy.ac.ucy.elasticsearch.model.Aerodynamic;
import cy.ac.ucy.elasticsearch.model.Query;
import cy.ac.ucy.elasticsearch.model.QueryResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;


@Service
public class PrepareTextService {

    private static final String upload_dir = "Uploads";

    public static ArrayList<Aerodynamic> textToJson(String filename){
        String strCurrentLine;
        Path path = Paths.get(upload_dir,filename);
        ArrayList<Aerodynamic> dataArray = new ArrayList<Aerodynamic>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(path.toFile()));
            int index = 0;
            String title= "";
            String author = "";
            String bibliography = "";
            String text ="";
            while ((strCurrentLine = bf.readLine()) != null) {
//                System.out.println(strCurrentLine);
                if (strCurrentLine.contains(".I")){
                    index = Integer.parseInt(strCurrentLine.split(" ")[1]);
                }
                if (strCurrentLine.contains(".T")){
                    title ="";
                    while (!(strCurrentLine = bf.readLine()).contains(".A")) {
                        title = title.concat(strCurrentLine);
                    }
                }
                if (strCurrentLine.contains(".A")){
                    strCurrentLine = bf.readLine();
                    author = strCurrentLine;
                    if (author.contains(".B")){
                        author = "";
                    }
                }
                if (strCurrentLine.contains(".B")){
                    strCurrentLine = bf.readLine();
                    bibliography = strCurrentLine;
                    if (bibliography.contains(".W")){
                        bibliography = "";
                    }
                }
                if (strCurrentLine.contains(".W")){
                    text = "";
                    while ( (strCurrentLine = bf.readLine()) !=null && !strCurrentLine.contains(".I")){
                        text= text.concat(strCurrentLine);
                    }
                    Aerodynamic row = new Aerodynamic(index, title, author, bibliography, text);
//                    System.out.println(row);
                    dataArray.add(row);
                    if (strCurrentLine!=null && strCurrentLine.contains(".I")){
//                        System.out.println(strCurrentLine);
                        index = Integer.parseInt(strCurrentLine.split(" ")[1]);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataArray;
    }

    public static ArrayList<Query> textToJsonQuery(String filename){
        String strCurrentLine;
//        Path path = Paths.get(upload_dir,filename);
        ArrayList<Query> dataArray = new ArrayList<Query>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            int index = 0;
            String query ="";
            while ((strCurrentLine = bf.readLine()) != null) {
//                System.out.println(strCurrentLine);
                if (strCurrentLine.contains(".I")){
                    index = Integer.parseInt(strCurrentLine.split(" ")[1]);
                }
                if (strCurrentLine.contains(".W")){
                    query = "";
                    while ( (strCurrentLine = bf.readLine()) !=null && !strCurrentLine.contains(".I")){
                        query= query.concat(strCurrentLine);
                    }
                    Query row = new Query(index, query);
                    System.out.println(row);
                    dataArray.add(row);
                    if (strCurrentLine!=null && strCurrentLine.contains(".I")){
//                        System.out.println(strCurrentLine);
                        index = Integer.parseInt(strCurrentLine.split(" ")[1]);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataArray;
    }

    public static ArrayList<QueryEvaluation> readQueryEvaluation(){
        String filename = "cranqrel.txt";
        String strCurrentLine;
        ArrayList<QueryEvaluation> dataArray = new ArrayList<QueryEvaluation>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            int index = 0;
            int docID = 0;
            int relevance = 0;
            QueryEvaluation qeval = null;
            while ((strCurrentLine = bf.readLine()) != null) {
                System.out.println(strCurrentLine);
                String [] line = strCurrentLine.split(" ");
                if(Integer.parseInt(line[0]) != index){
                    dataArray.add(qeval);
                    index = Integer.parseInt(line[0]);
                    qeval = new QueryEvaluation(index);
                }
                docID = Integer.parseInt(line[1]);
                relevance = Integer.parseInt(line[2]);
                if (relevance == -1)
                    continue;

                qeval.getRelated_docs().put(docID,relevance);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataArray;
    }


    public static void main(String[] args){
        String filename = "cranqrel.txt";
        System.out.println(readQueryEvaluation());
    }
}
