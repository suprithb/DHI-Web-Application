package com.brillio.dhi.exception;

public class InvalidDataException extends Exception{

	/**
	 * This is a generic exception which will be thrown to the controller,intermediate caller/layer should not handle it.
	 */
	private String status = null;
	private String statusCode = null;
	
	public InvalidDataException(String message){
		super(message);
	}
	
	public InvalidDataException(String message,String status){
		super(message);
		this.status = status;
	}
	
	public InvalidDataException(String message,String status,String statusCode){
		super(message);
		this.status = status;
		this.statusCode = statusCode;
	}

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

	
	
	

}
