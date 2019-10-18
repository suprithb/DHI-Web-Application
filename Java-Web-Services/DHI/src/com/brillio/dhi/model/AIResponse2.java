
package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "status_code",
    "description",
    "serverTextMessage",
    "serverImageMessage",
    "tabularData"
})
public class AIResponse2 {

    @JsonProperty("status")
    private String status;
    @JsonProperty("status_code")
    private String statusCode;
    @JsonProperty("description")
    private String description;
    @JsonProperty("serverTextMessage")
    private String serverTextMessage;
    @JsonProperty("serverImageMessage")
    private String serverImageMessage;
    @JsonProperty("tabularMessage")
    private TabularData tabularData;

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("status_code")
    public String getStatusCode() {
        return statusCode;
    }

    @JsonProperty("status_code")
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("serverTextMessage")
    public String getServerTextMessage() {
        return serverTextMessage;
    }

    @JsonProperty("serverTextMessage")
    public void setServerTextMessage(String serverTextMessage) {
        this.serverTextMessage = serverTextMessage;
    }

    @JsonProperty("serverImageMessage")
    public String getServerImageMessage() {
        return serverImageMessage;
    }

    @JsonProperty("serverImageMessage")
    public void setServerImageMessage(String serverImageMessage) {
        this.serverImageMessage = serverImageMessage;
    }

    @JsonProperty("tabularData")
    public TabularData getTabularData() {
        return tabularData;
    }

    @JsonProperty("tabularData")
    public void setTabularData(TabularData tabularData) {
        this.tabularData = tabularData;
    }

}
