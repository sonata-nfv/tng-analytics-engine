/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.graphprofiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Service
public class GPService {

    @Value("${physiognomica.server.url}")
    String physiognomicaServerURL;

    @Value("${prometheus.url}")
    String prometheusURL;

    @Value("${monitoring.engine}")
    String monitoringEngine;

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

    public List<String> get5gtangoNetworkServiceMetrics(String nsr_id) {
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
}
