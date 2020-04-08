package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FavouriteRequestModel {
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fullUrl")
    private String fullUrl;
    @JsonProperty("title")
    private String title;
	
    
    
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
    
    

}
