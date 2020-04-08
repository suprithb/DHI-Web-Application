
package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "columnName",
    "columnValue"
})
public class ColumnValue {

    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("columnValue")
    private String columnValue;

    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonProperty("columnValue")
    public String getColumnValue() {
        return columnValue;
    }

    @JsonProperty("columnValue")
    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

}
