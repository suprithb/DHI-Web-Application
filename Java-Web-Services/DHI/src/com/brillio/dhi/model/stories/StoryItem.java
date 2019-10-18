
package com.brillio.dhi.model.stories;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"storyItemId",
    "title",
    "order",
    "serverTextMessage",
    "serverImageMessage",
    "tabularData",
    "flip",
    "storyItemComments",
    "userQueryMessage",
    "lastUpdatedTimestamp"
})
public class StoryItem {

    @JsonProperty("title")
    private String title;
    @JsonProperty("storyItemId")
    private String storyItemId;
    @JsonProperty("order")
    private Integer order;
    @JsonProperty("serverTextMessage")
    private String serverTextMessage;
    @JsonProperty("serverImageMessage")
    private String serverImageMessage;
    @JsonProperty("tabularData")
    private TabularData tabularData;
    
    @JsonProperty("lastUpdatedTimestamp")
    private Long lastUpdatedTimestamp;
    
    @JsonProperty("flip")
    private String flip="inactive";
    
    @JsonProperty("userQueryMessage")
    private String userQueryMessage;
    
    @JsonProperty("storyItemComments")
    private String storyItemComments;
    
    private String imageUrl;

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("order")
    public Integer getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(Integer order) {
        this.order = order;
    }

    @JsonProperty("serverTextMessage")
    public String getServerTextMessage() {
        return serverTextMessage;
    }

    @JsonProperty("serverTextMessage")
    public void setServerTextMessage(String serverTextMessage) {
        this.serverTextMessage = serverTextMessage;
    }

    @JsonProperty("serverImageMessage")
    public String getServerImageMessage() {
        return serverImageMessage;
    }

    @JsonProperty("serverImageMessage")
    public void setServerImageMessage(String serverImageMessage) {
        this.serverImageMessage = serverImageMessage;
    }

    @JsonProperty("tabularData")
    public TabularData getTabularData() {
        return tabularData;
    }

    @JsonProperty("tabularData")
    public void setTabularData(TabularData tabularData) {
        this.tabularData = tabularData;
    }

    @JsonProperty("flip")
	public String getFlip() {
		return flip;
	}

	public void setFlip(String flip) {
		this.flip = flip;
	}

	@JsonProperty("storyItemComments")
	public String getStoryItemComments() {
		return storyItemComments;
	}

	public void setStoryItemComments(String storyItemComments) {
		this.storyItemComments = storyItemComments;
	}

	@JsonProperty("userQueryMessage")
	public String getUserQueryMessage() {
		return userQueryMessage;
	}

	public void setUserQueryMessage(String userQueryMessage) {
		this.userQueryMessage = userQueryMessage;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getStoryItemId() {
		return storyItemId;
	}

	public void setStoryItemId(String storyItemId) {
		this.storyItemId = storyItemId;
	}

	public Long getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	public void setLastUpdatedTimestamp(Long lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}
	
	
	
	
    
    

}
