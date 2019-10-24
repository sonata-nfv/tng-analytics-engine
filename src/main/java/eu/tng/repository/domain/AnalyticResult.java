/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tng.repository.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
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

    private String analyticProcessFriendlyName;

    private Map<String,String> metadata;

    private List results;

    //SUCCESS or ERROR
    private String status;

    private String executionMessage;

    private Date executionDate;

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

    public List getResults() {
        return results;
    }

    public void setResults(List results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecutionMessage() {
        return executionMessage;
    }

    public void setExecutionMessage(String executionMessage) {
        this.executionMessage = executionMessage;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public String getAnalyticProcessFriendlyName() {
        return analyticProcessFriendlyName;
    }

    public void setAnalyticProcessFriendlyName(String analyticProcessFriendlyName) {
        this.analyticProcessFriendlyName = analyticProcessFriendlyName;
    }

    public Map<String,String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String,String> metadata) {
        this.metadata = metadata;
    }

}
