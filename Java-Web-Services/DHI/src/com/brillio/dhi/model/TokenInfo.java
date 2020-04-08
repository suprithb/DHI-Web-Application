package com.brillio.dhi.model;

import java.sql.Timestamp;

import org.joda.time.DateTime;

public class TokenInfo extends GenericResponse{
	
	private String token;
	private String chatRoomId;
    private String userId;
    private DateTime issued;
    private DateTime expires;
    private String userInformation;
    
	private Timestamp sessionTimestamp;
	
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public DateTime getIssued() {
		return issued;
	}
	public void setIssued(DateTime issued) {
		this.issued = issued;
	}
	public DateTime getExpires() {
		return expires;
	}
	public void setExpires(DateTime expires) {
		this.expires = expires;
	}
	
	public Timestamp getSessionTimestamp() {
		return sessionTimestamp;
	}
	public void setSessionTimestamp(Timestamp sessionTimestamp) {
		this.sessionTimestamp = sessionTimestamp;
	}
	public String getUserInformation() {
		return userInformation;
	}
	public void setUserInformation(String userInformation) {
		this.userInformation = userInformation;
	}
	
	@Override
	public String toString() {
		return "TokenInfo [token=" + token + ", chatRoomId=" + chatRoomId + ", userId=" + userId + ", issued=" + issued
				+ ", expires=" + expires + ", userInformation=" + userInformation + ", sessionTimestamp="
				+ sessionTimestamp + "]";
	}
	
    

    
    
}