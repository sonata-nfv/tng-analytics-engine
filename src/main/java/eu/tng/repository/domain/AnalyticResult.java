/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.repository.domain;

import org.json.JSONArray;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author eleni
 */
@Document(collection = "AnalyticResult")
public class AnalyticResult {

    @Id
    private String id;

    private String callbackid;
    
    private String analyticServiceName;

    private JSONArray results;
    

    public AnalyticResult() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCallbackid() {
        return callbackid;
    }

    public void setCallbackid(String callbackid) {
        this.callbackid = callbackid;
    }

    public String getAnalyticServiceName() {
        return analyticServiceName;
    }

    public void setAnalyticServiceName(String analyticServiceName) {
        this.analyticServiceName = analyticServiceName;
    }

    public JSONArray getResults() {
        return results;
    }

    public void setResults(JSONArray results) {
        this.results = results;
    }
    

   
}
