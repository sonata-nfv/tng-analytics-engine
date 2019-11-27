/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.analyticsengine;

import com.google.gson.Gson;
import eu.tng.analyticsengine.Messaging.LogsFormat;
import eu.tng.api.exception.CustomNotFoundException;
import eu.tng.repository.dao.AnalyticResultRepository;
import eu.tng.repository.dao.AnalyticServiceRepository;
import eu.tng.repository.domain.AnalyticResult;
import io.prometheus.client.Counter;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@RestController
@Api(value = "tng-analytics-engine", description = "SONATA Analytics Engine")
@RequestMapping("/")
public class GPController {

    @Autowired
    GPService graphProfilerService;

    @Autowired
    LogsFormat logsFormat;

    @Autowired
    private AnalyticServiceRepository analyticServiceRepository;

    @Autowired
    private AnalyticResultRepository analyticResultRepository;

    @Value("${prometheus.url}")
    String prometheusURL;

    private static final Logger logger = Logger.getLogger(GPController.class.getName());
    public static Counter requests = Counter.build().name("analytic_requests_total").help("Total analytic requests.").register();

    //profiler pageland
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String info() {
        return "Welcome to tng-analytics-engine!";
    }

    //helthcheck call
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logsFormat.createLogInfo("I", timestamp.toString(), "healthcheck", "ping analytics engine", "200");
        return "{ \"alive_now\": \"" + new Date() + "\"}";
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
        return new Gson().toJson(analyticServiceRepository.findAll());
    }

    //Fetch the whole list of the analytic results
    @RequestMapping(value = "/results/list", method = RequestMethod.GET)
    public String getAnalyticResultsList(@RequestParam Map<String, String> queryParameters) {

        if (queryParameters.containsKey("result_id")) {
            String result_id = queryParameters.get("result_id");
            Optional<AnalyticResult> analyticResult = analyticResultRepository.findById(result_id);
            if (!analyticResult.isPresent()) {
                logger.info("NOT FOUND");
                return new JSONObject().toString();

            }
            logger.info("analyticResult" + analyticResult.get().toString());
            Gson gson = new Gson();
            return gson.toJson(analyticResult.get());
        }

        List<AnalyticResult> analyticResultList = analyticResultRepository.findAll();
        JSONArray arl = new JSONArray(analyticResultList);

        return arl.toString();
    }

    //Fetch one analytic result
    @RequestMapping(value = "/results/{callback_id}", method = RequestMethod.GET)
    public String getAnalyticResults(@PathVariable("callback_id") String callback_id) {

        Optional<AnalyticResult> analyticResult = analyticResultRepository.findByCallbackid(callback_id);
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

    //Get all prometheus metrics for a specific test_id
    @RequestMapping(value = "/tests/vnv/{testr_uuid}/metrics", method = RequestMethod.GET)
    public String get5gtangoNetworkTestMetrics(@PathVariable("testr_uuid") String testr_uuid) {

        JSONObject test_metadata = graphProfilerService.get5gtangoVnVTestMetadata(testr_uuid);

        List<String> filteredData = graphProfilerService.get5gtangoVnVNetworkServiceMetrics(test_metadata.getString("nsr_id"));

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

    @RequestMapping(value = "/add_R_analytic_service", method = RequestMethod.POST)
    public String addRAnalyticService(@RequestBody String analytic_service_info
    ) throws IOException {
        String response = graphProfilerService.addRAnalyticService(analytic_service_info);
        //logger.info("response" + response);
        return response;
    }

    @RequestMapping(value = "/results/{result_id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteAnalyticProcessReuslt(@PathVariable("result_id") String result_id
    ) {
        logger.info("result_uuid to delete" + result_id);
        analyticResultRepository.deleteById(result_id);
        HttpHeaders responseHeaders = new HttpHeaders();
        JSONObject response = new JSONObject();
        response.put("message", "Analytic Result with id " + result_id + " is succesfully deleted");
        String responseAsString = response.toString();
        responseHeaders.set("Content-Length", String.valueOf(responseAsString.length()));
        ResponseEntity responseEntity = new ResponseEntity(responseAsString, responseHeaders, HttpStatus.OK);
        return responseEntity;
    }

}
