package com.brillio.dhi.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.brillio.dhi.dao.entity.FavouriteEntity;
import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.model.UserFavourite;
import com.brillio.dhi.model.UserProfile;



@Service
public class ValueObjectMapper {
	
	
	
	/**
	 * This method will map the UserProfile from InfraSetupEntity
	 * @param userProfileEntity
	 * @return userProfile
	 */
	public UserProfile mapUserProfileEntityToUserProfile(UserProfileEntity userProfileEntity){
		
		UserProfile userProfile = new UserProfile();
		userProfile.setFirstname(userProfileEntity.getFirstname());
		userProfile.setLastname(userProfileEntity.getLastname());
		userProfile.setMiddlename(userProfileEntity.getMiddlename());
		userProfile.setUsername(userProfileEntity.getUsername());
		userProfile.setPhoneNumber(userProfileEntity.getPhoneNumber());
		userProfile.setProfileCreatedDate(userProfileEntity.getProfileCreatedDate());
		userProfile.setProfileModifiedDate(userProfileEntity.getProfileModifiedDate());
		
		return userProfile;
	}
	
	
	public List<UserFavourite> mapUserFavouriteEntityListToUserFavouriteList(List<FavouriteEntity> userFavouriteEntityList) throws NoRecordFoundException{
		
		List<UserFavourite> userFavouriteList = new ArrayList<UserFavourite>();
		
		for(FavouriteEntity userFavouriteEntity : userFavouriteEntityList) {
			UserFavourite userFavourite = new UserFavourite();
			if(userFavouriteEntity.getFileName() != null && !(userFavouriteEntity.getFileName().trim().equalsIgnoreCase(""))) {
				userFavourite.setUrl(userFavouriteEntity.getFileName());
				userFavourite.setTitle(userFavouriteEntity.getTitle());
				userFavouriteList.add(userFavourite);
			}
		}
		if(userFavouriteList == null || userFavouriteList.size() <= 0) {
			throw new NoRecordFoundException("No record found for the provided user. ","error","dhi_data_not_found_error");
		}
		return userFavouriteList;
	}
}
