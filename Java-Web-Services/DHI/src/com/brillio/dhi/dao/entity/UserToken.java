package com.brillio.dhi.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * UserProfile generated by hbm2java
 */
@Entity
@Table(name = "USER_TOKEN")
public class UserToken implements java.io.Serializable {

	private String userName;
	private String token;
	private String remarks;
	
	private String chatRoomId;

	public UserToken() {
	}

	@Id
	@Column(name = "USER_NAME",  nullable = false)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "TOKEN", columnDefinition="TEXT", length = 600, nullable = false)
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column(name = "REMARKS",  nullable = false)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "CHAT_ROOM_ID",  nullable = true)
	public String getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	
	
	
	
	
}
