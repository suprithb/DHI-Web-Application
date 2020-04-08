package com.brillio.dhi.model;

import java.util.List;


public class UserList extends GenericResponse {
	
	List<UserProfile> userProfileList = null;

	public List<UserProfile> getUserProfileList() {
		return userProfileList;
	}

	public void setUserProfileList(List<UserProfile> userProfileList) {
		this.userProfileList = userProfileList;
	}

	@Override
	public String toString() {
		return "UserList [userProfileList=" + userProfileList + "]";
	}
	
	
	
	

}
