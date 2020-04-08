package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "statusCode",
    "description"
})
public class GenericResponse extends DHIResponse{
	
	
	@JsonProperty("status")
	private String status;
	 
	@JsonProperty("statusCode")
	private String statusCode;
	 
	@JsonProperty("description")
	private String description;

	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "GenericResponse [status=" + status + ", statusCode=" + statusCode + ", description=" + description
				+ "]";
	}
	
	
	
	
	
	

}
