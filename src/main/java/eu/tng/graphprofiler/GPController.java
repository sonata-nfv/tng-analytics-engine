/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.graphprofiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@RestController
public class GPController {

    @Autowired
    GPService graphProfilerService;

    @Value("${physiognomica.server.url}")
    String physiognomicaServerURL;

    @Value("${prometheus.url}")
    String prometheusURL;

    
    @RequestMapping("/")
    public String info() {
        return "Welcome to tng-profiler!";
    }
    
    @RequestMapping("/ping")
    public String ping() {
        return "pong";
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
    @RequestMapping(value = "/services/{nsr_id}/metrics", method = RequestMethod.GET)
    public String get5gtangoNetworkServiceMetrics(@PathVariable("nsr_id") String nsr_id) {

        List<String> filteredData = graphProfilerService.get5gtangoNetworkServiceMetrics(nsr_id);

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
    @RequestMapping(value = "/analytic_service/{nsr_id}", method = RequestMethod.POST)
    public String consumeAnalyticService(@PathVariable("nsr_id") String nsr_id, @RequestBody String analytic_service_info
    ) {

        JSONObject analytic_service = new JSONObject(analytic_service_info);
        String start = analytic_service.getString("start");
        String end = analytic_service.getString("end");
        String step = analytic_service.getString("step");//"'3m'"
        String name = analytic_service.getString("name"); //"/ocpu/library/Physiognomica/R/getChordDiagram"
        JSONArray metrics;
        if (analytic_service.has("metrics")) {
            metrics = analytic_service.getJSONArray("metrics");
        } else {
            List<String> metricslist = graphProfilerService.get5gtangoNetworkServiceMetrics(nsr_id);

            metrics = new JSONArray(metricslist);
        }
        
        System.out.println("metrics.toString()"+metrics.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map3 = new LinkedMultiValueMap<String, String>();
        map3.add("prometheus_url", "'" + prometheusURL + "'");
        map3.add("metrics", metrics.toString());
        map3.add("step", "'" + step + "'");
        map3.add("start", "'" + start + "'");
        map3.add("end", "'" + end + "'");
        map3.add("enriched", "true");

        HttpEntity<MultiValueMap<String, String>> physiognomicaRequest3 = new HttpEntity<>(map3, headers);

        String analytic_service_url = physiognomicaServerURL + name;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response3 = restTemplate
                .postForEntity(analytic_service_url, physiognomicaRequest3, String.class);

        if (null != response3 && null != response3.getStatusCode() && response3
                .getStatusCode()
                .is2xxSuccessful()) {

            System.out.println("response3" + response3.getBody());
        }

        return response3.getBody();

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
