
package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"status",
	"title",
    "textMessage",
    "imageUrl",
    "tabularData",
    "generatedSqlQuery"
    
})
public class AIResponse {
	
	@JsonProperty("title")
	private String title;
    @JsonProperty("textMessage")
    private String textMessage;
    @JsonProperty("imageUrl")
    private String imageUrl;
    @JsonProperty("status")
    private String status;
    @JsonProperty("tabularData")
    private TabularData tabularData;
    @JsonProperty("generatedSqlQuery")
	private String generatedSqlQuery;

    @JsonProperty("textMessage")
    public String getTextMessage() {
        return textMessage;
    }

    @JsonProperty("textMessage")
    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    @JsonProperty("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

	public TabularData getTabularData() {
		return tabularData;
	}

	public void setTabularData(TabularData tabularData) {
		this.tabularData = tabularData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGeneratedSqlQuery() {
		return generatedSqlQuery;
	}

	public void setGeneratedSqlQuery(String generatedSqlQuery) {
		this.generatedSqlQuery = generatedSqlQuery;
	}
	
	
    
    

}
