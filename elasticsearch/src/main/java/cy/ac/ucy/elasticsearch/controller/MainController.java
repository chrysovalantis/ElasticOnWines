package cy.ac.ucy.elasticsearch.controller;


import java.util.*;
import java.util.stream.Collectors;


import cy.ac.ucy.elasticsearch.exception.FileStorageException;
import cy.ac.ucy.elasticsearch.form.EvaluationMetrics;
import cy.ac.ucy.elasticsearch.form.QueryEvaluation;
import cy.ac.ucy.elasticsearch.form.RequestLogger;
import cy.ac.ucy.elasticsearch.form.SearchResp;
import cy.ac.ucy.elasticsearch.model.Aerodynamic;
import cy.ac.ucy.elasticsearch.model.Query;
import cy.ac.ucy.elasticsearch.model.QueryResponse;
import cy.ac.ucy.elasticsearch.service.EvaluationService;
import cy.ac.ucy.elasticsearch.service.FileStorageService;
import cy.ac.ucy.elasticsearch.service.PrepareTextService;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.Object;
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

import javax.xml.ws.Response;


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

    @RequestMapping(value = { "/general_evaluation/{directory}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EvaluationMetrics> evaluateIndex(@PathVariable String directory, @RequestParam(value= "size", required = false) Integer size){

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
        EvaluationMetrics avgMetrics = EvaluationService.calculateAverageMetrics(list_metrics);

        return new ResponseEntity<>(avgMetrics,HttpStatus.OK);
    }

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

    /** Print the inverted index of a given collection. You have the choice of JSON or string representation.
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

    /** Print the inverted index of a given collection. You have the choice of JSON or string representation.
     *
     *
     * @param directory
     * @param jsonOrText
     * @return
     */
    @RequestMapping(value = { "/print/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> print(@PathVariable String directory, @RequestParam(value= "json", required = false) Boolean jsonOrText){

        return null;
    }

    /** Prints just the terms of the inverted index of the given collection.
     *
     * @param directory
     * @return
     */
    @RequestMapping(value = { "/printTerms/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> printTerms(@PathVariable String directory) {


        return null;
    }

    /** Print the files of the given collection.
     *
     * @param directory
     * @return
     */
    @RequestMapping(value = { "/files/{directory}" }, method = RequestMethod.GET)
    public ResponseEntity<Object> printFiles(@PathVariable String directory)  {

        return null;
    }

    /** Create new collection.
     *
     * @param directory
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

    /** Upload a file to a given directory. The files uploaded first to an uploaded folder inside the server and
     *  then moved to a specific directory (C://Dionysos).
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

    /** Upload multiple files to a given directory.
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

    /** Search inside a given directory. The user gives a query as a parameter and get the answer as a response.
     *  Examples:
     *
     *  NOTE: The operation must be in capital letters.
     *
     * @param directory
     * @param query
     * @return
     */
    @RequestMapping(value = { "/search/{directory}" }, method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@PathVariable String directory, @RequestParam("query") String query, @RequestParam(value= "size", required = false) Integer size)  {

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


    /** Delete a given collection. Everything inside the collection is deleted.
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

    /** Delete a file inside a collection and update the inverted index of this collection.
     *
     * @param directory
     * @return
     */
    @RequestMapping(value = { "/deleteFile/" }, method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFile(@RequestParam String directory, @RequestParam String file_id) {

        String uri = "http://localhost:9200/"+directory+"/_doc/"+file_id;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(uri);
        return new ResponseEntity<>("File: "+file_id+" from index: "+ directory+" Deleted",HttpStatus.OK);
    }






}
