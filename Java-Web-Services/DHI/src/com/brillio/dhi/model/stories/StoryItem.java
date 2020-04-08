
package com.brillio.dhi.model.stories;

import java.util.List;
import java.util.Map;

import com.brillio.dhi.model.ColumnType;
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
    
    @JsonProperty("sortObj")
    private Map<String,String> sortObj;
    
    @JsonProperty("tabularData")
    private TabularData tabularData;
    
    
    @JsonProperty("response")
	private boolean response;
    
    @JsonProperty("isMessage")
	private boolean isMessage;
    
    @JsonProperty("resdate")
	private String resdate;
    
    @JsonProperty("restime")
	private String restime;
    
    @JsonProperty("itemId")
	private String itemId;
    
    @JsonProperty("color")
	private String color;
    
    @JsonProperty("fontFamily")
	private String fontFamily;
    
    @JsonProperty("unit")
	private String unit;
    
    @JsonProperty("graphType")
	private String graphType;
    
    @JsonProperty("fontSize")
	private Integer fontSize;
    
    @JsonProperty("lastUpdatedTimestamp")
    private Long lastUpdatedTimestamp;
    
    @JsonProperty("flip")
    private String flip="inactive";
    
    @JsonProperty("userQueryMessage")
    private String userQueryMessage;
    
    @JsonProperty("storyItemComments")
    private String storyItemComments;
    
    @JsonProperty("columnWithTypes")
    private List<ColumnType> columnWithTypes;
    
    @JsonProperty("graphTypes")
    private List<String> graphTypes;
    
    @JsonProperty("columNames")
    private List<String> columNames = null;
    
    @JsonProperty("imageUrl")
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

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public boolean isMessage() {
		return isMessage;
	}

	public void setMessage(boolean isMessage) {
		this.isMessage = isMessage;
	}

	public String getResdate() {
		return resdate;
	}

	public void setResdate(String resdate) {
		this.resdate = resdate;
	}

	public String getRestime() {
		return restime;
	}

	public void setRestime(String restime) {
		this.restime = restime;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getGraphType() {
		return graphType;
	}

	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public List<ColumnType> getColumnWithTypes() {
		return columnWithTypes;
	}

	public void setColumnWithTypes(List<ColumnType> columnWithTypes) {
		this.columnWithTypes = columnWithTypes;
	}

	public List<String> getGraphTypes() {
		return graphTypes;
	}

	public void setGraphTypes(List<String> graphTypes) {
		this.graphTypes = graphTypes;
	}

	public List<String> getColumNames() {
		return columNames;
	}

	public void setColumNames(List<String> columNames) {
		this.columNames = columNames;
	}

	public Map<String, String> getSortObj() {
		return sortObj;
	}

	public void setSortObj(Map<String, String> sortObj) {
		this.sortObj = sortObj;
	}
	
	
	
	
    
    

}
