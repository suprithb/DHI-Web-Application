
package com.brillio.dhi.model.stories;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"storyTitle",
	"lastUpdatedTimestamp",
    "story"
})
public class StoryModel {
	
	@JsonProperty("storyId")
	private String storyId = null;
	
	@JsonProperty("storyTitle")
	private String storyTitle = null;
	
	@JsonProperty("timestamp")
	private Long timestamp;
	
	@JsonProperty("lastUpdatedTimestamp")
	private Long lastUpdatedTimestamp;

    @JsonProperty("storyItem")
    private List<StoryItem> storyItem = null;

    @JsonProperty("storyItem")
    public List<StoryItem> getStoryItem() {
        return storyItem;
    }

    @JsonProperty("storyItem")
    public void setStoryItem(List<StoryItem> storyItem) {
        this.storyItem = storyItem;
    }

	public String getStoryTitle() {
		return storyTitle;
	}

	public void setStoryTitle(String storyTitle) {
		this.storyTitle = storyTitle;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Long getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	public void setLastUpdatedTimestamp(Long lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
    
	
	
	
    

}
