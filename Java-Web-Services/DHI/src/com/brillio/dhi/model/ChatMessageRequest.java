package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessageRequest {
	
	
	@JsonProperty("clientTextMessage")
	private String clientTextMessage;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("dataFileName")
	private String dataFileName;
	
	@JsonProperty("aliasFileName")
	private String aliasFileName;

	public String getClientTextMessage() {
		return clientTextMessage;
	}

	public void setClientTextMessage(String clientTextMessage) {
		this.clientTextMessage = clientTextMessage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public String getAliasFileName() {
		return aliasFileName;
	}

	public void setAliasFileName(String aliasFileName) {
		this.aliasFileName = aliasFileName;
	}

	@Override
	public String toString() {
		return "ChatMessageRequest [clientTextMessage=" + clientTextMessage + ", userName=" + userName
				+ ", dataFileName=" + dataFileName + ", aliasFileName=" + aliasFileName + "]";
	}
	
	
	
	
	

}
