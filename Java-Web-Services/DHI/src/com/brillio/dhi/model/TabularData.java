
package com.brillio.dhi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "displayType",
    "columNames",
    "columnWithTypes",
    "graphTypes",
    "row"
})
public class TabularData {

    @JsonProperty("displayType")
    private String displayType;
    @JsonProperty("columNames")
    private List<String> columNames = null;
    
    @JsonProperty("columnWithTypes")
    private List<ColumnType> columnWithTypes;
    
    @JsonProperty("graphTypes")
    private List<String> graphTypes;
    
    @JsonProperty("row")
    private List<Row> row = null;

    @JsonProperty("displayType")
    public String getDisplayType() {
        return displayType;
    }

    @JsonProperty("displayType")
    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    @JsonProperty("columNames")
    public List<String> getColumNames() {
        return columNames;
    }

    @JsonProperty("columNames")
    public void setColumNames(List<String> columNames) {
        this.columNames = columNames;
    }

    @JsonProperty("row")
    public List<Row> getRow() {
        return row;
    }

    @JsonProperty("row")
    public void setRow(List<Row> row) {
        this.row = row;
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
    
    

}
