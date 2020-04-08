package com.brillio.dhi.dao;

import java.util.List;

import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface UserProfileDao {

	public boolean saveUserProfile(UserProfileEntity userProfile);

	public boolean updateUserProfile(UserProfileEntity userProfile);

	public List<UserProfileEntity> getAllUserProfiles();

	public UserProfileEntity getUserProfileByUserName(String userName) throws NoRecordFoundException;

	public boolean deleteUserProfileByUserName(String userName);

	public UserProfileEntity getUserProfileByPhoneNumber(String phoneNumber) throws NoRecordFoundException;


}
