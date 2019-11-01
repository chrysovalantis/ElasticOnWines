package cy.ac.ucy.elasticsearch.controller;


import java.util.*;


import cy.ac.ucy.elasticsearch.exception.FileStorageException;
import cy.ac.ucy.elasticsearch.form.EvaluationMetrics;
import cy.ac.ucy.elasticsearch.form.QueryEvaluation;
import cy.ac.ucy.elasticsearch.form.RequestLogger;
import cy.ac.ucy.elasticsearch.model.Aerodynamic;
import cy.ac.ucy.elasticsearch.model.Query;
import cy.ac.ucy.elasticsearch.model.QueryResponse;
import cy.ac.ucy.elasticsearch.service.EvaluationService;
import cy.ac.ucy.elasticsearch.service.FileStorageService;
import cy.ac.ucy.elasticsearch.service.PrepareTextService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


/** This class is the main controller of my project. Contains all the endpoints.
 *
 * @author Chrysovalantis Christodoulous
 */
@RestController
@RequestMapping("/collections")
public class MainController {


    @Autowired
    private FileStorageService fileStorageService;							 //Initialize file storage service

    /** Just a Hello World test example.
     *
     * @param model
     * @return
     */
    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {

        return "index";
    }

    /** Gets the results from the individual evaluation and then calculates the average precision, recall and F1-score.
     *
     * @param directory
     * @param size
     * @return
     */
    @RequestMapping(value = { "/general_evaluation/{directory}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EvaluationMetrics> evaluateIndex(@PathVariable String directory, @RequestParam(value= "size", required = false) Integer size){

        String filename = "cran.qry";
        ArrayList<QueryEvaluation> qevaluation = PrepareTextService.readQueryEvaluation();
        ArrayList<Query> queries = PrepareTextService.textToJsonQuery(filename);
        ArrayList<QueryResponse> results = new ArrayList<>();
        String result = null;
        //get all the queries from the file and search throw eleasticsearch
        for (Query q : queries){
            QueryResponse qr = new QueryResponse(q);
            if (size!=null) {
                result = search(directory, q.getQuery(), size).getBody();
            }
            else{
                result = search(directory, q.getQuery(),10).getBody();
            }
            try {
                //Gets the response documents for each query
                JSONObject response = new JSONObject(result);
                JSONObject hits = response.getJSONObject("hits");
                JSONArray hits_array = hits.getJSONArray("hits");
                for (int i=0; i < hits_array.length(); i++) {
                    JSONObject o = hits_array.getJSONObject(i);
                    JSONObject source = o.getJSONObject("_source");
                    int index = source.getInt("index");
                    qr.getDocIDs().add(index);
                }

            }catch (JSONException err){
                System.out.println(("Error: "+ err.toString()));
            }
            results.add(qr);
        }
        ArrayList<EvaluationMetrics> list_metrics = EvaluationService.calculateEvaluationMetrics(results,qevaluation);  //metrics for each query
        EvaluationMetrics avgMetrics = EvaluationService.calculateAverageMetrics(list_metrics);                         //calculate the average metrics for the whole index

        return new ResponseEntity<>(avgMetrics,HttpStatus.OK);
    }

    /**Gets the evaluation query file (cran.qry) which contains some queries and the results query files (cranqrel)
     * which contains the index of the query, the relevant document and how relevant it is. Then gets each query from
     * cran.qry file and search it throw the given index and then regarding the results, it produce the metrics of
     * each query individual.
     *
     * @param directory the index name
     * @param size      the return size of the query
     * @return
     */
    @RequestMapping(value = { "/individual_evaluation/{directory}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayList<EvaluationMetrics>> individualEvaluateIndex(@PathVariable String directory, @RequestParam(value= "size", required = false) Integer size){

        String filename = "cran.qry";
        ArrayList<QueryEvaluation> qevaluation = PrepareTextService.readQueryEvaluation();
        ArrayList<Query> queries = PrepareTextService.textToJsonQuery(filename);
        ArrayList<QueryResponse> results = new ArrayList<>();
        String result = null;
        for (Query q : queries){
            QueryResponse qr = new QueryResponse(q);
            if (size!=null) {
                result = search(directory, q.getQuery(), size).getBody();
            }
            else{
                result = search(directory, q.getQuery(),10).getBody();
            }
            try {
                JSONObject response = new JSONObject(result);
                JSONObject hits = response.getJSONObject("hits");
                JSONArray hits_array = hits.getJSONArray("hits");
                for (int i=0; i < hits_array.length(); i++) {
                    JSONObject o = hits_array.getJSONObject(i);
                    JSONObject source = o.getJSONObject("_source");
                    int index = source.getInt("index");
                    qr.getDocIDs().add(index);
                }

            }catch (JSONException err){
                System.out.println(("Error: "+ err.toString()));
            }
            results.add(qr);
        }
        ArrayList<EvaluationMetrics> list_metrics = EvaluationService.calculateEvaluationMetrics(results,qevaluation);

        return new ResponseEntity<>(list_metrics,HttpStatus.OK);
    }

    /** Prints each query from the cran.qry file with the result documents return from the elasticsearch
     *
     * @param directory the index name
     * @param size      the return size of the query
     * @return
     */
    @RequestMapping(value = { "/evaluation_docs/{directory}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayList<QueryResponse>> evaluation_docs(@PathVariable String directory, @RequestParam(value= "size", required = false) Integer size){

        String filename = "cran.qry";
        ArrayList<QueryEvaluation> qevaluation = PrepareTextService.readQueryEvaluation();
        ArrayList<Query> queries = PrepareTextService.textToJsonQuery(filename);
        ArrayList<QueryResponse> results = new ArrayList<>();
        String result = null;
        for (Query q : queries){
            QueryResponse qr = new QueryResponse(q);
            if (size!=null) {
                result = search(directory, q.getQuery(), size).getBody();
            }
            else{
                result = search(directory, q.getQuery(),10).getBody();
            }
            try {
                JSONObject response = new JSONObject(result);
                JSONObject hits = response.getJSONObject("hits");
                JSONArray hits_array = hits.getJSONArray("hits");
                for (int i=0; i < hits_array.length(); i++) {
                    JSONObject o = hits_array.getJSONObject(i);
                    JSONObject source = o.getJSONObject("_source");
                    int index = source.getInt("index");
                    qr.getDocIDs().add(index);
                }

            }catch (JSONException err){
                System.out.println(("Error: "+ err.toString()));
            }
            results.add(qr);
        }

        return new ResponseEntity<>(results,HttpStatus.OK);
    }

    /** Prints all the indices inside ElasticSearch.
     *
     *
     * @return
     */
    @RequestMapping(value = { "/print_indices" }, method = RequestMethod.GET)
    public ResponseEntity<String> printIndices(){
        String uri = "http://localhost:9200/_cat/indices";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /** Prints information about the given index.
     *
     *
     * @param directory     the index name
     * @return
     */
    @RequestMapping(value = { "/print/{directory}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> print(@PathVariable String directory){

        String uri = "http://localhost:9200/{directory}";
        Map<String, String> params = new HashMap<String, String>();
        params.put("directory",directory);

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class,params);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }



    /** Creates new collection.
     *
     * @param directory     the name of the index
     * @return
     */
    @RequestMapping(value = { "/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<String> createNewCollection(@PathVariable String directory)  {

        String uri = "http://localhost:9200/{directory}";
        Map<String, String> params = new HashMap<String, String>();
        params.put("directory",directory);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(uri,"",params);
        return new ResponseEntity<>("Collection: "+directory+" Created!",HttpStatus.OK);

    }

    /** Uploads the content of the given file to the given index. Note: The structure of the document must be the same
     *  as the given cran.all.1400.
     *
     * @param directory
     * @param file
     * @return
     */
    @RequestMapping(value = { "/uploadfile/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<ArrayList<Aerodynamic>> uploadFile(@PathVariable String directory, @RequestParam("file") MultipartFile file) throws FileStorageException {


        String fileName = null;
        fileName = fileStorageService.storeFile(file,directory);
        ArrayList<Aerodynamic> data = PrepareTextService.textToJson(fileName);

        String uri = "http://localhost:9200/"+directory+"/_doc";
        System.out.println(uri);
        RestTemplate restTemplate = new RestTemplate();
        for (Aerodynamic d : data){
            String result = restTemplate.postForObject( uri, d, String.class);
            System.out.println(result);
        }

        return new ResponseEntity<>(data,HttpStatus.OK);
    }

    /** Uploads multiple files to the given index. The structure of the file must be the same as the given cran.all.1400.
     *
     * @param directory
     * @param files
     * @return
     */
    @RequestMapping(value = { "/uploadMultipleFiles/{directory}" }, method = RequestMethod.POST)
    public ResponseEntity<List<?>> uploadMultipleFiles(@PathVariable String directory, @RequestParam("files") MultipartFile[] files) throws FileStorageException {
        List<ArrayList<Aerodynamic>> list = new ArrayList<>();
        for (MultipartFile file : Arrays.asList(files)) {
            ResponseEntity<ArrayList<Aerodynamic>> uploadFileResponse = uploadFile(directory, file);
            list.add(uploadFileResponse.getBody());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /** Search throw a given index using the query parameter. The syntax of the query parameter is just the same as the
     *  lucene syntax and supports anything you can imagine. Some of my examples shows how to search inside the index.
     *  One of them used to search a document inside the index and another one is used to print all the documents inside
     *  the index.
     *
     *
     * @param directory     the name of the index
     * @param query         lucene query syntax
     * @return
     */
    @RequestMapping(value = { "/search/{directory}" }, method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@PathVariable String directory, @RequestParam(value = "query", required = false) String query, @RequestParam(value= "size", required = false) Integer size)  {

        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        System.out.println(query);
        String uri = "http://localhost:9200/"+directory+"/_search";
        // Query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                // Add query parameter
                .queryParam("q", query);
        if (size != null){
            builder.queryParam("size", size);
        }
        System.out.println(builder.buildAndExpand(params).toUri());

        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new RequestLogger()));

        String result = restTemplate.getForObject(builder.buildAndExpand(params).toUri(), String.class);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /** Delete a given index. Everything inside the index is deleted.
     *
     * @param directory
     * @return
     */
    @RequestMapping(value = { "/deleteDirectory/{directory}" }, method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteDirectory(@PathVariable String directory) {

        String uri = "http://localhost:9200/"+directory;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(uri);
        return new ResponseEntity<>("Index "+directory+" Deleted",HttpStatus.OK);
    }

    /** Removes a file from the given index. The file is defined by the index of the file.
     *
     * @param directory      the name of the index
     * @param file_id        the id of the file you want delete
     * @return
     */
    @RequestMapping(value = { "/deleteFile/" }, method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFile(@RequestParam String directory, @RequestParam String file_id) {

        // Search the file id in order to get the index key id.
        String query = "index:"+file_id;
        String result = search(directory, query, 1).getBody();
        String id ="";
        try{
            JSONObject response = new JSONObject(result);
            JSONObject hits = response.getJSONObject("hits");
            JSONArray hits_array = hits.getJSONArray("hits");
            for (int i=0; i < hits_array.length(); i++) {
                JSONObject o = hits_array.getJSONObject(i);
                id = o.getString("_id");
            }

        }catch (JSONException err){
            System.out.println(("Error: "+ err.toString()));
        }
        //setting up the request headers
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);

        if(id.compareTo("")==0){
            return new ResponseEntity<>("There is no file with this name!" ,HttpStatus.NOT_FOUND);
        }
        String uri = "http://localhost:9200/"+directory+"/_doc/"+id;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE,requestEntity, String.class);
        return response;
    }






}
