
package com.brillio.dhi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "columnName",
    "dataType",
    "description",
    "aliasName"
})
public class DiscoveryModelColumns{

    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("description")
    private String description;
    @JsonProperty("aliasName")
    private String aliasName;
    
    @JsonProperty("readonlyValue")
    private boolean readonlyValue = true;
	
    
    public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String column1) {
		this.columnName = column1;
	}
	public String getDataType() {
		return dataType;
	}
	
	public void setDataType(String column2) {
		this.dataType = column2;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String column3) {
		this.description = column3;
	}
	
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public boolean isReadonlyValue() {
		return readonlyValue;
	}
	public void setReadonlyValue(boolean readonlyValue) {
		this.readonlyValue = readonlyValue;
	}
	
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasName == null) ? 0 : aliasName.hashCode());
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (readonlyValue ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscoveryModelColumns other = (DiscoveryModelColumns) obj;
		if (aliasName == null) {
			if (other.aliasName != null)
				return false;
		} else if (!aliasName.equals(other.aliasName))
			return false;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (readonlyValue != other.readonlyValue)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "DiscoveryModelColumns [columnName=" + columnName + ", dataType=" + dataType + ", description="
				+ description + ", aliasName=" + aliasName + ", readonlyValue=" + readonlyValue + "]";
	}

   

}
