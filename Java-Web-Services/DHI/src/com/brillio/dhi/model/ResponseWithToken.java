package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"status",
	"statusCode",
	"description",
	"firstName",
	"middleName",
	"lastName",
	"authToken",
	"chatRoomId"
})
public class ResponseWithToken extends GenericResponse{
	
	
	@JsonProperty("firstName")
	private String firstName;
	
	@JsonProperty("lastName")
	private String lastName;
	
	@JsonProperty("middleName")
	private String middleName;
	
	@JsonProperty("authToken")
	private String authToken;
	
	@JsonProperty("chatRoomId")
	private String chatRoomId;
	

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	@Override
	public String toString() {
		return "ResponseWithToken [firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName
				+ ", authToken=" + authToken + ", chatRoomId=" + chatRoomId + "]";
	}
	
	
	


}
