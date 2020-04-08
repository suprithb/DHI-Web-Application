package com.brillio.dhi.model;

public class MetaDataFileRequest {
	
	private String dataFilePath;

	public String getDataFilePath() {
		return dataFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	@Override
	public String toString() {
		return "MetaDataFileRequest [dataFilePath=" + dataFilePath + "]";
	}
	
	

}
