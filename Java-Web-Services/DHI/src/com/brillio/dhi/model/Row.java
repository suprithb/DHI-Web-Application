
package com.brillio.dhi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "rowNumber",
    "columnValues"
})
public class Row {

    @JsonProperty("rowNumber")
    private String rowNumber;
    @JsonProperty("columnValues")
    private List<ColumnValue> columnValues = null;

    @JsonProperty("rowNumber")
    public String getRowNumber() {
        return rowNumber;
    }

    @JsonProperty("rowNumber")
    public void setRowNumber(String rowNumber) {
        this.rowNumber = rowNumber;
    }

    @JsonProperty("columnValues")
    public List<ColumnValue> getColumnValues() {
        return columnValues;
    }

    @JsonProperty("columnValues")
    public void setColumnValues(List<ColumnValue> columnValues) {
        this.columnValues = columnValues;
    }

}
