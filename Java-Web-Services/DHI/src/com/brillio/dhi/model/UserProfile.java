package com.brillio.dhi.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"status",
	"statusCode",
	"description",
    "userName",
    "firstname",
    "middlename",
    "lastname",
    "phoneNumber",
    "profileCreatedDate",
    "profileModifiedDate"
})
public class UserProfile{
	
	@JsonProperty("userName")
	private String username;
	
	@JsonProperty("firstname")
	private String firstname;
	
	@JsonProperty("middlename")
	private String middlename;
	
	@JsonProperty("lastname")
	private String lastname;
	
	@JsonProperty("phoneNumber")
	private String phoneNumber;
	
	@JsonProperty("profileCreatedDate")
	private Date profileCreatedDate;
	
	@JsonProperty("profileModifiedDate")
	private Date profileModifiedDate;
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Date getProfileCreatedDate() {
		return profileCreatedDate;
	}
	public void setProfileCreatedDate(Date profileCreatedDate) {
		this.profileCreatedDate = profileCreatedDate;
	}
	public Date getProfileModifiedDate() {
		return profileModifiedDate;
	}
	public void setProfileModifiedDate(Date profileModifiedDate) {
		this.profileModifiedDate = profileModifiedDate;
	}
	@Override
	public String toString() {
		return "UserProfile [username=" + username + ", firstname=" + firstname + ", middlename=" + middlename
				+ ", lastname=" + lastname + ", phoneNumber=" + phoneNumber + ", profileCreatedDate="
				+ profileCreatedDate + ", profileModifiedDate=" + profileModifiedDate + "]";
	}
	
	

}
