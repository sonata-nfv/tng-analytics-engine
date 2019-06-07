/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.graphprofiler;

import com.google.gson.Gson;
import eu.tng.api.exception.CustomNotFoundException;
import eu.tng.repository.dao.AnalyticResultRepository;
import eu.tng.repository.dao.AnalyticServiceRepository;
import eu.tng.repository.domain.AnalyticResult;
import eu.tng.repository.domain.AnalyticService;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@RestController
public class GPController {
    
    @Autowired
    GPService graphProfilerService;
    
    @Autowired
    private AnalyticServiceRepository analyticServiceRepository;
    
    @Autowired
    private AnalyticResultRepository analyticResulteRepository;
    
    @Value("${physiognomica.server.url}")
    String physiognomicaServerURL;
    
    @Value("${prometheus.url}")
    String prometheusURL;
    
    private static final Logger logger = Logger.getLogger(GPController.class.getName());

    //profiler pageland
    @RequestMapping("/")
    public String info() {
        return "Welcome to tng-profiler!";
    }

    //helthcheck call
    @RequestMapping("/ping")
    public String ping() {
        return "pong";
    }

    //demo callback api for testing
    @RequestMapping(value = "/analytic_service/{callbackid}/status", method = RequestMethod.POST)
    public String demoAnalyticsServiceCallback(@PathVariable("callbackid") String callbackid, @RequestBody String analytic_service_info) {
        String loginfo = "Analytic Service with id " + callbackid + " is completed";
        logger.info(loginfo);
        logger.info("--------------------");
        logger.info(analytic_service_info);
        return callbackid;
    }

    //Fetch all available analytic services
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getAnalyticServiceList() {
        
//        if (true) {
//            throw new CustomNotFoundException("Not found customer with name is ");
//        }
        
        analyticServiceRepository.deleteAll();

        // save a couple of customers
        AnalyticService as1 = new AnalyticService();
        as1.setName("correlogram");
        as1.setUrl("/ocpu/library/Physiognomica/R/correlogram");
        as1.setDescription("Provide a correlogram with high statistical correlations between metrics");
        as1.setConstraints("Select the set of metrics (more than one) to be used for the calculation of the correlation matrix");
        
        List<String> results1 = new LinkedList<>();
        results1.add("correlogram.html");
        as1.setResults(results1);
        analyticServiceRepository.save(as1);
        
        
        AnalyticService as5 = new AnalyticService();
        as5.setName("chord");
        as5.setUrl("/ocpu/library/Physiognomica/R/chord");
        as5.setDescription("Provide a correlogram with high statistical correlations between metrics via an interactive chord diagram");
        as5.setConstraints("Select the set of metrics (more than one) to be used for the calculation of the correlation matrix");
        List<String> results5 = new LinkedList<>();
        results5.add("correlation_page.html");
        as5.setResults(results5);
        analyticServiceRepository.save(as5);
        
        AnalyticService as2 = new AnalyticService();
        as2.setName("TimeSeriesDecomposition");
        as2.setUrl("/ocpu/library/Physiognomica/R/time_series_decomposition");
        as2.setDescription("Provide a decomposition of a time series in seasonal, trend, and remainder parts");
        as2.setConstraints("Select one metric to be used for time series decomposition");
        List<String> results2 = new LinkedList<>();
        results2.add("time_series_decomposition.html");
        as2.setResults(results2);      
        analyticServiceRepository.save(as2);
        
        AnalyticService as3 = new AnalyticService();
        as3.setName("linear_regression");
        as3.setUrl("/ocpu/library/Physiognomica/R/linear_regression");
        as3.setDescription("Provide a linear regression model along with a scatterplot");
        as3.setConstraints("Select the dependent and intenpendent variable for the linear regression model");
        List<String> results3 = new LinkedList<>();
        results3.add("linear_regression.html");
        as3.setResults(results3);
        analyticServiceRepository.save(as3);
       
        
        AnalyticService as4 = new AnalyticService();
        as4.setName("multiple_linear_regression");
        as4.setUrl("/ocpu/library/Physiognomica/R/multiple_linear_regression");
        as4.setDescription("Provide a multiple linear regression model along with composed visualizations");
        as4.setConstraints("Select the dependent and intenpendent variable for the linear regression model");
        List<String> results4 = new LinkedList<>();
        results4.add("multiple_linear_regression.html");
        as4.setResults(results4);
        analyticServiceRepository.save(as4);
        
        
        
        AnalyticService as6 = new AnalyticService();
        as6.setName("test");
        as6.setUrl("/ocpu/library/Physiognomica/R/test");
        as6.setDescription("test");
        as6.setConstraints("test");
        List<String> results6 = new LinkedList<>();
        results6.add("console");
        as6.setResults(results6);
        analyticServiceRepository.save(as6);
        
        
        List<AnalyticService> analyticServicesList = analyticServiceRepository.findAll();
        JSONArray asl = new JSONArray(analyticServicesList);
        
        return asl.toString();
    }

    //Fetch the whole list of the analytic results
    @RequestMapping(value = "/results/list", method = RequestMethod.GET)
    public String getAnalyticResultsList() {
        
        List<AnalyticResult> analyticResultList = analyticResulteRepository.findAll();
        JSONArray arl = new JSONArray(analyticResultList);
        
        return arl.toString();
    }

    //Fetch one analytic result
    @RequestMapping(value = "/results/{callback_id}", method = RequestMethod.GET)
    public String getAnalyticResults(@PathVariable("callback_id") String callback_id) {
        
        Optional<AnalyticResult> analyticResult = analyticResulteRepository.findByCallbackid(callback_id);
        logger.info(callback_id);
        return new Gson().toJson(analyticResult.get());
    }

    //Fetch all metrics available at prometheus
    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public String getPrometheusMetrics() {
        return graphProfilerService.getPrometheusMetrics();
    }

    //Fetch metrics that contain a specific keyword
    @RequestMapping(value = "/metrics/{keyword}", method = RequestMethod.GET)
    public String getFilteredPrometheusMetrics(@PathVariable("keyword") String keyword) {
        
        List<String> filteredData = graphProfilerService.getFilteredPrometheusMetrics(keyword);
        
        JSONObject result = new JSONObject();
        result.put("status", "success");
        result.put("data", filteredData);
        
        return result.toString();
        
    }

    //Fetch all dimensions for a specific metrioc
    @RequestMapping(value = "/metric/{metric}/dimensions", method = RequestMethod.GET)
    public String getPrometheousMetricDimensions(@PathVariable("metric") String metric) {
        
        List<String> filteredData = graphProfilerService.getPrometheousMetricDimensions(metric);
        
        JSONObject result = new JSONObject();
        result.put("status", "success");
        result.put("data", filteredData);
        
        return result.toString();
        
    }

    //Fetch all dimensions for all metrics that contain a specific keyword
    @RequestMapping(value = "/metrics/{keyword}/dimensions", method = RequestMethod.GET)
    public String getFilteredPrometheusMetricswithAllDimensions(@PathVariable("keyword") String keyword) {
        
        List<String> filteredData = graphProfilerService.getFilteredPrometheusMetricswithAllDimensions(keyword);
        
        JSONObject result = new JSONObject();
        result.put("status", "success");
        result.put("data", filteredData);
        
        return result.toString();
        
    }

    //Get all prometheus metrics for a specific nsr_id
    @RequestMapping(value = "/services/vnv/{nsr_id}/metrics", method = RequestMethod.GET)
    public String get5gtangoNetworkServiceMetrics(@PathVariable("nsr_id") String nsr_id) {
        
        List<String> filteredData = graphProfilerService.get5gtangoVnVNetworkServiceMetrics(nsr_id);
        
        JSONObject result = new JSONObject();
        result.put("status", "success");
        result.put("data", filteredData);
        
        return result.toString();
        
    }

    //Consume an Analytic Service (a.k.a execute a profiling process) for a specific nsr_id 
    //Request body JSONObject includes
    //start: start datetime
    //end: end datetime
    //step: frequence step to get data from prometheus
    //name: name of the analytic service to consume
    //metrics: OPTIONAL set of metric names as they are available at prometheus. if is not selected a set of metrics then all metrics of the network service participate to the analysis
    //Example of prometheus query execution: http://212.101.173.101:9090/api/v1/query_range?query=cpu{resource_id=%27091db7f2-68b5-4487-b37c-27282b3381cf%27}&start=2019-02-28T10:10:30.781Z&end=2019-02-28T16:11:00.781Z&step=15s
    @RequestMapping(value = "/analytic_service", method = RequestMethod.POST)
    public void consumeAnalyticService(@RequestBody String analytic_service_info
    ) throws IOException {
        graphProfilerService.consumeAnalyticService(analytic_service_info);
    }
    
   

    /*
    if (true) {
            throw new CustomNotFoundException("Not found customer with name is ");
        }
     */
    @ExceptionHandler(CustomNotFoundException.class)
    public String handleError(CustomNotFoundException e) {
        
        return "adfadfadfadfasdfadsf " + e.getMessage();
    }

    //Consume an Analytic Service
    //Request body JSONObject includes
    //start: start datetime
    //end: end datetime
    //step: frequence step to get data from prometheus
    //name: name of the analytic service to consume
    //metrics: set of metric names (withour their respectful dimensions)
//    @RequestMapping(value = "/analytic_service", method = RequestMethod.POST)
//    public String consumeAnalyticServiceWithPreselectedMetrics(@RequestBody String analytic_service_info
//    ) {
//
//        JSONObject analytic_service = new JSONObject(analytic_service_info);
//        String start = analytic_service.getString("start");
//        String end = analytic_service.getString("end");
//        String step = analytic_service.getString("step");//"'3m'"
//        String name = analytic_service.getString("name");; //"/ocpu/library/Physiognomica/R/getChordDiagram"
//        JSONArray metrics = analytic_service.getJSONArray("metrics");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> map3 = new LinkedMultiValueMap<String, String>();
//        map3.add("prometheus_url", "'" + prometheusURL + "'");
//        map3.add("metrics", metrics.toString());
//        map3.add("step", "'" + step + "'");
//        map3.add("start", "'" + start + "'");
//        map3.add("end", "'" + end + "'");
//
//        HttpEntity<MultiValueMap<String, String>> physiognomicaRequest3 = new HttpEntity<>(map3, headers);
//
//        String analytic_service_url = physiognomicaServerURL + name;
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response3 = restTemplate
//                .postForEntity(analytic_service_url, physiognomicaRequest3, String.class);
//
//        if (null != response3 && null != response3.getStatusCode() && response3
//                .getStatusCode()
//                .is2xxSuccessful()) {
//
//            System.out.println("response3" + response3.getBody());
//        }
//
//        return response3.getBody();
//
//    }
    // Can not work as black box for netdata but is still necessary as a generic aproach (maybe it will be used for sdk)
//    @RequestMapping(value = "/metrics/{keyword}/dimensions/analytic_service", method = RequestMethod.POST)
//    public String consumeAnalyticServiceWithFullSetOfMetricsBasedonAKeyword(@PathVariable("keyword") String keyword, @RequestBody String analytic_service_info
//    ) {
//
//        JSONObject analytic_service = new JSONObject(analytic_service_info);
//        String start = analytic_service.getString("start");
//        String end = analytic_service.getString("end");
//        String step = analytic_service.getString("step");//"'3m'"
//        String name = analytic_service.getString("name");//"/ocpu/library/Physiognomica/R/getChordDiagram"
//
//        JSONObject result = new JSONObject();
//        List<String> filteredData = new ArrayList<String>();
//        if (analytic_service.has("preselected_metric_types")) {
//
//            System.out.println("dfadfafd");
//            filteredData = graphProfilerService.getFilteredPrometheusMetrics(keyword);
//
//            JSONArray preselected_metric_types = analytic_service.getJSONArray("preselected_metric_types");
//
//            List<String> preselected_metric_types_names = IntStream.range(0, preselected_metric_types.length()).mapToObj(i -> preselected_metric_types.getJSONObject(i).getString("name")).collect(Collectors.toList());
//
//            Stream<String> newfilteredDataStream = filteredData.stream().filter(f -> (preselected_metric_types_names.stream().anyMatch(p -> f.contains(p))));
//
//            filteredData = newfilteredDataStream.collect(Collectors.toList());
//
//        } else {
//
//            filteredData = graphProfilerService.getFilteredPrometheusMetrics(keyword);
//
//        }
//        JSONArray filteredDataArray = new JSONArray(filteredData);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> map3 = new LinkedMultiValueMap<String, String>();
//        map3.add("prometheus_url", "'" + prometheusURL + "'");
//        map3.add("metrics", filteredDataArray.toString());
//        map3.add("step", "'" + step + "'");
//        map3.add("start", "'" + start + "'");
//        map3.add("end", "'" + end + "'");
//
//        HttpEntity<MultiValueMap<String, String>> physiognomicaRequest3 = new HttpEntity<>(map3, headers);
//
//        String analytic_service_url = physiognomicaServerURL + name;
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response3 = restTemplate
//                .postForEntity(analytic_service_url, physiognomicaRequest3, String.class);
//
//        if (null != response3 && null != response3.getStatusCode() && response3.getStatusCode().is2xxSuccessful()) {
//
//            result.put("status", "success");
//            result.put("data", response3.getBody());
//
//        } else {
//            result.put("status", "failure");
//            result.put("data", response3.getBody());
//        }
//
//        System.out.println(result.toString());
//
//        return result.toString();
//
//    }
}
