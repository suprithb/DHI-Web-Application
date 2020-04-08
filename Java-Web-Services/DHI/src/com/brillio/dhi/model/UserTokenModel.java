package com.brillio.dhi.model;

import java.util.ArrayList;
import java.util.List;

public class UserTokenModel {
	
	List<String> userAliasFileNames = new ArrayList<String>();
	
	List<String> userDataFileNames = new ArrayList<String>();
	
	String chatRoomId = null;
	

	public List<String> getUserAliasFileNames() {
		return userAliasFileNames;
	}

	public void setUserAliasFileNames(List<String> userAliasFileNames) {
		this.userAliasFileNames = userAliasFileNames;
	}

	public List<String> getUserDataFileNames() {
		return userDataFileNames;
	}

	public void setUserDataFileNames(List<String> userDataFileNames) {
		this.userDataFileNames = userDataFileNames;
	}

	public String getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	@Override
	public String toString() {
		return "UserTokenModel [userAliasFileNames=" + userAliasFileNames + ", userDataFileNames=" + userDataFileNames
				+ ", chatRoomId=" + chatRoomId + "]";
	}

	

	

	
	
	

}
