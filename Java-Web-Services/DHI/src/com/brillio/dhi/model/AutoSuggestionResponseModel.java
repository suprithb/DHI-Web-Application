package com.brillio.dhi.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"status",
    "statusCode",
    "description",
    "autoSuggestionKeywords",
    "autoSuggestionQuery",
    
})
public class AutoSuggestionResponseModel extends GenericResponse{
	
	@JsonProperty("autoSuggestionKeywords")
	public Set<String> autoSuggestionKeywords = null;
	
	@JsonProperty("autoSuggestionQuery")
	public Set<String> autoSuggestionQuery = null;

	public Set<String> getAutoSuggestionKeywords() {
		return autoSuggestionKeywords;
	}

	public void setAutoSuggestionKeywords(Set<String> autoSuggestionKeywords) {
		this.autoSuggestionKeywords = autoSuggestionKeywords;
	}

	public Set<String> getAutoSuggestionQuery() {
		return autoSuggestionQuery;
	}

	public void setAutoSuggestionQuery(Set<String> autoSuggestionQuery) {
		this.autoSuggestionQuery = autoSuggestionQuery;
	}
	
	
	

}
 