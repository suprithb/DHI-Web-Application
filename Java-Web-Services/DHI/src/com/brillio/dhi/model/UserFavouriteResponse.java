package com.brillio.dhi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userFavourites"
})

public class UserFavouriteResponse extends GenericResponse{
	
	public List<UserFavourite> userFavourites = null;

	public List<UserFavourite> getUserFavourites() {
		return userFavourites;
	}

	public void setUserFavourites(List<UserFavourite> userFavourites) {
		this.userFavourites = userFavourites;
	}

	@Override
	public String toString() {
		return "UserFavouriteResponse [userFavourites=" + userFavourites + "]";
	}

	
	

}
