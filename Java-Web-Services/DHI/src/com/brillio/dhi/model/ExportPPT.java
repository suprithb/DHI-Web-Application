package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "statusCode",
    "description",
    "pptDownloadUri"
})
public class ExportPPT extends GenericResponse{
	
	@JsonProperty("pptDownloadUri")
	private String pptDownloadUri;

	public String getPptDownloadUri() {
		return pptDownloadUri;
	}

	public void setPptDownloadUri(String pptDownloadUri) {
		this.pptDownloadUri = pptDownloadUri;
	}
	
	

}
