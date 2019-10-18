
package com.brillio.dhi.model.stories;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "columNames",
    "row"
})
public class TabularData {

    @JsonProperty("columNames")
    private List<String> columNames = null;
    @JsonProperty("row")
    private List<Row> row = null;

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

}
