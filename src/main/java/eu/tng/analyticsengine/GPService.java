/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.analyticsengine;

import com.google.gson.Gson;
import eu.tng.analyticsengine.Messaging.LogsFormat;
import eu.tng.repository.dao.AnalyticResultRepository;
import eu.tng.repository.dao.AnalyticServiceRepository;
import eu.tng.repository.domain.AnalyticResult;
import eu.tng.repository.domain.AnalyticService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Service
public class GPService {

    @Value("${physiognomica.server.url}")
    private String physiognomicaServerURL;

    @Value("${prometheus.url}")
    private String prometheusURL;

    @Value("${monitoring.engine}")
    private String monitoringEngine;

    @Value("${repository.url}")
    private String repositoryURL;

    @Autowired
    LogsFormat logsFormat;

    @Autowired
    private AnalyticServiceRepository analyticServiceRepository;

    @Autowired
    private AnalyticResultRepository analyticResulteRepository;

    public String getPrometheusMetrics() {

        String prometheusMetricsURL = prometheusURL + "/api/v1/label/__name__/values";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(prometheusMetricsURL, HttpMethod.GET, entity, String.class);

        String myresponse = response.getBody();

        return myresponse;
    }

    //TODO get them with concatenated keywords
    public List<String> getFilteredPrometheusMetrics(String keyword) {

        String prometheusMetricsURL = prometheusURL + "/api/v1/label/__name__/values";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(prometheusMetricsURL, HttpMethod.GET, entity, String.class);

        JSONObject myresponse = new JSONObject(response.getBody());

        JSONArray data = myresponse.getJSONArray("data");

        List<String> dataList = IntStream.range(0, data.length()).mapToObj(i -> data.getString(i)).collect(Collectors.toList());

        Stream<String> onlymetricsiwant = dataList.stream().filter(s -> s.contains(keyword));

        List<String> filteredData = onlymetricsiwant.collect(Collectors.toList());

        return filteredData;
    }

    //TODO get attributes that may miss certain category dimensions
    public List<String> getPrometheousMetricDimensions(String metric) {
        List<String> metricDimensions = new ArrayList();
        try {
            System.out.println("getPrometheousMetricDimensions is triggered for metric" + metric);
            String prometheusMetricsURL = prometheusURL + "/api/v1/series?match[]=" + metric;

            System.out.println("prometheusMetricsURL" + prometheusMetricsURL);

            URL obj = new URL(prometheusMetricsURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + prometheusMetricsURL);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
            JSONObject myresponse = new JSONObject(response.toString());

            JSONArray data = myresponse.getJSONArray("data");

            List<JSONObject> dataList = IntStream.range(0, data.length()).mapToObj(i -> data.getJSONObject(i)).collect(Collectors.toList());

            metricDimensions = dataList.stream().map(x -> (x.getString("__name__")
                    + "{"
                    + "chart='" + (x.has("chart") ? x.getString("chart") : "") + "',"
                    + "dimension='" + (x.has("dimension") ? x.getString("dimension") : "") + "',"
                    + "family='" + (x.has("family") ? x.getString("family") : "") + "',"
                    + "instance='" + (x.has("instance") ? x.getString("instance") : "") + "',"
                    + "job='" + (x.has("job") ? x.getString("job") : "")) + "'}"
            ).collect(Collectors.toList());

        } catch (ProtocolException ex) {
            Logger.getLogger(GPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(GPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GPService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metricDimensions;

    }

    public List<String> getFilteredPrometheusMetricswithAllDimensions(String keyword) {

        List<String> filteredData = this.getFilteredPrometheusMetrics(keyword);
        List<String> metricswithAllDimensions = new ArrayList();

        filteredData.forEach(metric -> {

            this.getPrometheousMetricDimensions(metric).stream().sequential().collect(Collectors.toCollection(() -> metricswithAllDimensions));

        });

        return metricswithAllDimensions;

    }

    public List<String> get5gtangoVnVNetworkServiceMetrics(String nsr_id) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<String> metricswithDimensions = new ArrayList();

        String monitoringEngineURL = monitoringEngine + "/api/v2/services/" + nsr_id + "/metrics";
        logsFormat.createLogInfo("I", timestamp.toString(), "Connection to monitoring manager", monitoringEngineURL, "200");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(monitoringEngineURL, HttpMethod.GET, entity, String.class);

        JSONObject myresponse = new JSONObject(response.getBody());

        if (!myresponse.getString("status").equalsIgnoreCase("Success")) {
            logsFormat.createLogWarn("W", timestamp.toString(), "No monitoring metrics available", "No monitoring metrics available for nsr_id " + nsr_id, "200");
            return metricswithDimensions;
        }

        JSONArray vnfs = myresponse.getJSONArray("vnfs");

        for (int i = 0; i < vnfs.length(); i++) {
            JSONObject vnf = vnfs.getJSONObject(i);
            JSONArray vdus = vnf.getJSONArray("vdus");
            for (int k = 0; k < vdus.length(); k++) {
                JSONObject vdu = vdus.getJSONObject(k);

                JSONArray metrics = vdu.getJSONArray("metrics");

                for (int n = 0; n < metrics.length(); n++) {
                    JSONObject metric = metrics.getJSONObject(n);

                    String metricwithDimensions = metric.getString("__name__");

                    if (metric.has("name")) {
                        metricwithDimensions += "{name='" + metric.getString("name") + "'}";

                    } else if (metric.has("resource_id")) {
                        metricwithDimensions += "{resource_id='" + metric.getString("resource_id") + "'}";
                    }

                    //String metricwithDimensions = metric.getString("__name__") + "{"
                    //        + "name='" + (metric.has("name") ? metric.getString("name") : "") + "'}";
                    metricswithDimensions.add(metricwithDimensions);
                }

            }
        }

        return metricswithDimensions;
    }

    public JSONObject get5gtangoVnVTestMetadata(String testr_uuid) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        System.out.println("repositoryURL" + repositoryURL);
        ResponseEntity<String> response = restTemplate.exchange(repositoryURL + "/trr/test-suite-results/" + testr_uuid, HttpMethod.GET, entity, String.class);
        JSONObject myresponse = new JSONObject(response.getBody());
        String nsr_id = myresponse.getString("instance_uuid");
        String start = myresponse.getString("started_at");
        String end = myresponse.getString("ended_at");

        JSONObject responseObject = new JSONObject();
        responseObject.put("nsr_id", nsr_id);
        responseObject.put("start", start);
        responseObject.put("end", end);
        responseObject.put("test_uuid", myresponse.getString("test_uuid"));

        return responseObject;
    }

    public List<String> get5gtangoSPNetworkServiceMetrics(String nsr_id) {
        List<String> metricswithDimensions = new ArrayList();

        String monitoringEngineURL = monitoringEngine + "/api/v2/services/" + nsr_id + "/metrics";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(monitoringEngineURL, HttpMethod.GET, entity, String.class);

        JSONObject myresponse = new JSONObject(response.getBody());

        JSONArray vnfs = myresponse.getJSONArray("vnfs");

        for (int i = 0; i < vnfs.length(); i++) {
            JSONObject vnf = vnfs.getJSONObject(i);
            JSONArray vdus = vnf.getJSONArray("vdus");
            for (int k = 0; k < vdus.length(); k++) {
                JSONObject vdu = vdus.getJSONObject(k);

                JSONArray metrics = vdu.getJSONArray("metrics");

                for (int n = 0; n < metrics.length(); n++) {
                    JSONObject metric = metrics.getJSONObject(n);

                    String metricwithDimensions = metric.getString("__name__") + "{"
                            + "project_id='" + (metric.has("project_id") ? metric.getString("project_id") : "") + "',"
                            + "resource_id='" + (metric.has("resource_id") ? metric.getString("resource_id") : "") + "'}";

                    metricswithDimensions.add(metricwithDimensions);
                }

            }
        }

        return metricswithDimensions;
    }

    @Async
    public void consumeAnalyticService(String analytic_service_info) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Gson gson = new Gson();
        Map<String, String> metadata = new HashMap<String, String>();
        JSONObject analytic_service = new JSONObject(analytic_service_info);
        JSONArray periods = new JSONArray();
        if (analytic_service.has("periods")) {
            periods = analytic_service.getJSONArray("periods");
        }
        String step = analytic_service.getString("step");//"'3m'"
        String name = analytic_service.getString("name");
        String vendor = analytic_service.getString("vendor");

        RestTemplate restTemplate = new RestTemplate();

        JSONArray metrics = null;
        if (vendor.equalsIgnoreCase("5gtango.vnv")) {

            if (analytic_service.has("testr_uuid")) {

                String testr_uuid = analytic_service.getString("testr_uuid");
                JSONObject test_metadata = this.get5gtangoVnVTestMetadata(testr_uuid);
                String nsr_id = test_metadata.getString("nsr_id");

                if (analytic_service.has("metrics")) {
                    metrics = analytic_service.getJSONArray("metrics");
                } else {
                    List<String> metricslist = this.get5gtangoVnVNetworkServiceMetrics(nsr_id);
                    metrics = new JSONArray(metricslist);
                }
                //System.out.println("metrics--> " + metrics);
                JSONObject periodOne = new JSONObject();
                periodOne.put("start", test_metadata.getString("start"));
                periodOne.put("end", test_metadata.getString("end"));
                periods.put(periodOne);
                //System.out.println("periods------------" + periods.toString());

                metadata.put("testr_uuid", testr_uuid);
                metadata.put("test_uuid", test_metadata.getString("test_uuid"));

            }

        } else if (analytic_service.has("metrics")) {
            metrics = analytic_service.getJSONArray("metrics");
            //System.out.println("metrics--> " + metrics);
        } else {
            //TODO log no metrics requested
            logsFormat.createLogError("E", timestamp.toString(), "No metrics are provided", "", "200");

        }
        logsFormat.createLogInfo("I", timestamp.toString(), "Connect to prometheus", prometheusURL, "200");
        logsFormat.createLogInfo("I", timestamp.toString(), "Fetch Metrics from prometheus", metrics.toString(), "200");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map3 = new LinkedMultiValueMap<String, String>();
        map3.add("prometheus_url", "'" + prometheusURL + "'");
        map3.add("metrics", metrics.toString());
        map3.add("step", "'" + step + "'");
        map3.add("periods", periods.toString());
        map3.add("enriched", "true");

        HttpEntity<MultiValueMap<String, String>> physiognomicaRequest3 = new HttpEntity<>(map3, headers);

        AnalyticService as = analyticServiceRepository.findByName(name);

        String analytic_service_url = physiognomicaServerURL + as.getUrl();
        logsFormat.createLogInfo("I", timestamp.toString(), "Request analytic service", analytic_service_url, "200");
        ResponseEntity<String> response3 = restTemplate.postForEntity(analytic_service_url, physiognomicaRequest3, String.class);

        String myresponse = "";
        if (null != response3 && null != response3.getStatusCode() && response3
                .getStatusCode()
                .is2xxSuccessful()) {

            //save the analytic result            
            AnalyticResult analyticresult = new AnalyticResult();
            analyticresult.setStatus("SUCCESS");
            analyticresult.setExecutionMessage("The analytic service has succesfully completed.");
            analyticresult.setAnalyticServiceName(name);
            analyticresult.setAnalyticProcessFriendlyName(analytic_service.getString("process_friendly_name"));
            analyticresult.setMetadata(metadata);
            analyticresult.setExecutionDate(new Date());
            //analyticresult.setResults(response.toList());
            //analyticresult.setUuid(UUID.randomUUID());
            AnalyticResult savedanalyticresult = analyticResulteRepository.save(analyticresult);

            myresponse = response3.getBody();
            myresponse = myresponse.replace("/ocpu/tmp/", physiognomicaServerURL + "/ocpu/tmp/");

            String lines[] = myresponse.split("\\r?\\n");
            JSONArray response = new JSONArray();
            List<String> resultslist = as.getResults();

            for (String line : lines) {
                if (resultslist.stream().anyMatch(s -> line.contains(s))) {
                    JSONObject result = new JSONObject();
                    if (line.contains("html")) {
                        result.put("type", "html");
                    } else if (line.contains("csv")) {
                        result.put("type", "csv");
                    } else if (line.contains("json")) {
                        result.put("type", "json");
                    } else if (line.contains("jpg") || line.contains("png")) {
                        result.put("type", "img");
                    } else {
                        result.put("type", "txt");
                    }
                    //save results at tng-analytics-engine
                    String[] line_parts = line.split("/");
                    String filename = line_parts[line_parts.length - 1];
                    saveFileFromURL(line, savedanalyticresult.getId(), filename);
                    result.put("result", "/" + savedanalyticresult.getId() + "/" + filename);
                    response.put(result);
                }

            }

            savedanalyticresult.setResults(response.toList());
            analyticResulteRepository.save(savedanalyticresult);

            logsFormat.createLogInfo("I", timestamp.toString(), "Response from analytic server", response.toString(), "200");

            //update the callback url if any
            if (analytic_service.has("callback")) {
                String callback_url = analytic_service.getString("callback");

                HttpHeaders callbackHeaders = new HttpHeaders();
                callbackHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                logsFormat.createLogInfo("I", timestamp.toString(), "Request callback url", callback_url, "200");

                //logger.info("analyticresult  " + gson.toJson(analyticresult));
                //ResponseEntity<String> callback_response = restTemplate.postForEntity(callback_url, gson.toJson(analyticresult), String.class);
                String payload = gson.toJson(analyticresult);
                StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(callback_url);
                request.setEntity(entity);

                HttpResponse testresponse = httpClient.execute(request);
                int statuscode = testresponse.getStatusLine().getStatusCode();

                if (statuscode == 200) {
                    org.apache.http.HttpEntity entity1 = testresponse.getEntity();
                    // Read the contents of an entity and return it as a String.
                    String content = EntityUtils.toString((org.apache.http.HttpEntity) entity1);
                    String callback_uuid = content;

                    Optional<AnalyticResult> existing_as = analyticResulteRepository.findByCallbackid(callback_uuid);
                    if (existing_as.isPresent()) {
                        logsFormat.createLogError("E", timestamp.toString(), "duplicate callback_uuid. Analytic service is not saved", callback_url, "200");

                        analyticResulteRepository.delete(savedanalyticresult);
                        return;
                    } else {
                        savedanalyticresult.setCallbackid(callback_uuid);
                        analyticResulteRepository.save(savedanalyticresult);
                    }

                } else {
                    savedanalyticresult.setStatus("ERROR");
                    savedanalyticresult.setExecutionMessage("callback url returned HTTP error " + statuscode);
                    analyticResulteRepository.save(savedanalyticresult);
                }
            }

        }
    }

    public void saveFileFromURL(String urlString, String folderName, String resultName) {

        //System.out.println(urlString + "-------------" + folderName + "-----------------------" + resultName);
        BufferedReader br = null;
        try {
            //URL oracle = new URL(urlString);
            String content = sendGet(urlString);

            String fileName = "/var/www/html/" + folderName;
            Path path = Paths.get(fileName);

            Files.createDirectories(path);

            Files.write(Paths.get(fileName + "/" + resultName), content.getBytes());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GPService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(GPService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private String sendGet(String url) throws Exception {

        URL obj = new URL(url);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
        return response.toString();

    }

    @ExceptionHandler({IOException.class})
    public String handleError(java.io.IOException e) {

        JSONObject response = new JSONObject();

        response.put("code", "503");
        response.put("message", e.getMessage());
        return response.toString();
    }

}
