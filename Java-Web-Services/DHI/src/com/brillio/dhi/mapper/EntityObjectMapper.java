package com.brillio.dhi.mapper;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brillio.dhi.dao.UserProfileDao;
import com.brillio.dhi.dao.entity.UserAuth;
import com.brillio.dhi.dao.entity.FavouriteEntity;
import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.model.UserFavourite;
import com.brillio.dhi.model.UserRegistration;

@Service
public class EntityObjectMapper {
	
	private static final Logger LOGGER = Logger.getLogger(EntityObjectMapper.class);
	
	public EntityObjectMapper(){
		
	}
	
	@Autowired
	UserProfileDao userProfileDao;
	
	
	/**
	 * This method will map all the properties of UserRegistration class to UserAuth entity object
	 * @param userRegistration
	 * @param userAuth
	 * @return
	 */
	public UserAuth mapUserRegistrationToUserAuthEntity(UserRegistration userRegistration, UserProfileEntity userProfile){
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		UserAuth userAuth = new UserAuth();
		userAuth.setUsername(userRegistration.getEmail());
		userAuth.setPassword(userRegistration.getPassword());
		userAuth.setPhoneNumber(userRegistration.getMobileNumber());
		userAuth.setIsActive("false");
		userAuth.setPasswordModifiedDate(timestamp);
		
		return userAuth;
	}
	
	
	/**
	 * This method will mapp all the properties from UserRegistration class to UserProfile enity object.
	 * @param userRegistration
	 * @return userProfile
	 */
	public UserProfileEntity mapUserRegistrationToUserProfile(UserRegistration userRegistration){
		UserProfileEntity userProfile = new UserProfileEntity();
		userProfile.setUsername(userRegistration.getEmail());
		userProfile.setFirstname(userRegistration.getFirstName());
		userProfile.setLastname(userRegistration.getLastName());
		userProfile.setMiddlename(userRegistration.getMiddleName());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		userProfile.setProfileCreatedDate(timestamp);
		userProfile.setProfileModifiedDate(timestamp);
		userProfile.setPhoneNumber(userRegistration.getMobileNumber());
		return userProfile;
	}
	
	
	public FavouriteEntity mapUserFavouriteToUserFavouriteEntity(String userName, String fileName, String title){
		FavouriteEntity favouriteEntity = new FavouriteEntity();
		favouriteEntity.setUserName(userName);
		favouriteEntity.setFileName(fileName);
		favouriteEntity.setTitle(title);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		favouriteEntity.setProfileCreatedDate(timestamp);
		
		return favouriteEntity;
	}
	
	
	

}
