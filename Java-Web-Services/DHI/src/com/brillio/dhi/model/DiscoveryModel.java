package com.brillio.dhi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"status",
	"statusCode",
	"description",
    "discovery"
})
public class DiscoveryModel extends GenericResponse{
	
	@JsonProperty("discovery")
	private List<DiscoveryModelColumns> discovery;

	public List<DiscoveryModelColumns> getDiscovery() {
		return discovery;
	}

	public void setDiscovery(List<DiscoveryModelColumns> discovery) {
		this.discovery = discovery;
	}

	@Override
	public String toString() {
		return "DiscoveryModel [discovery=" + discovery + "]";
	}
	
	
	
	

}
