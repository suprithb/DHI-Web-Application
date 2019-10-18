package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"status",
	"statusCode",
	"description",
	"title",
	"searchShowImage",
	"searchShowJsonData",
	"serverTextMessage",
	"serverImageMessage",
	"tabularMessage",
	"userQueryMessage",
	"storyItemId"
})
public class QueryResponse extends GenericResponse{
	
	@JsonProperty("title")
	private String title;

	@JsonProperty("serverTextMessage")
	private String serverTextMessage;
	 
	@JsonProperty("serverImageMessage")
	private String serverImageMessage;
	
	@JsonProperty("userQueryMessage")
	private String userQueryMessage;
	
	@JsonProperty("tabularData")
    private TabularData tabularData;
	
	@JsonProperty("searchShowImage")
    private String searchShowImage;
	
	@JsonProperty("searchShowJsonData")
    private String searchShowJsonData;
	
	@JsonProperty("storyItemId")
    private String storyItemId;
	
	
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getStoryItemId() {
		return storyItemId;
	}

	public void setStoryItemId(String storyItemId) {
		this.storyItemId = storyItemId;
	}

	public String getUserQueryMessage() {
		return userQueryMessage;
	}

	public void setUserQueryMessage(String userQueryMessage) {
		this.userQueryMessage = userQueryMessage;
	}

	public String getSearchShowImage() {
		return searchShowImage;
	}

	public void setSearchShowImage(String searchShowImage) {
		this.searchShowImage = searchShowImage;
	}

	public String getSearchShowJsonData() {
		return searchShowJsonData;
	}

	public void setSearchShowJsonData(String searchShowJsonData) {
		this.searchShowJsonData = searchShowJsonData;
	}

	public String getServerTextMessage() {
		return serverTextMessage;
	}

	public void setServerTextMessage(String serverTextMessage) {
		this.serverTextMessage = serverTextMessage;
	}

	public String getServerImageMessage() {
		return serverImageMessage;
	}

	public void setServerImageMessage(String serverImageMessage) {
		this.serverImageMessage = serverImageMessage;
	}

	public TabularData getTabularData() {
		return tabularData;
	}

	public void setTabularData(TabularData tabularData) {
		this.tabularData = tabularData;
	}

	
	
	@Override
	public String toString() {
		return "QueryResponse [serverTextMessage=" + serverTextMessage + ", serverImageMessage=" + serverImageMessage
				+ ", tabularData=" + tabularData + ", searchShowImage=" + searchShowImage + ", searchShowJsonData="
				+ searchShowJsonData + "]";
	}

}
