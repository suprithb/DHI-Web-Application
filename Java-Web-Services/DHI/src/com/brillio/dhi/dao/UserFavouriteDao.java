package com.brillio.dhi.dao;

import java.util.List;

import com.brillio.dhi.dao.entity.FavouriteEntity;
import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface UserFavouriteDao {

	public boolean saveUserFavouriteInformation(FavouriteEntity userFavouriteInformation);

	//public List<FavouriteEntity> getAllUserCalculatorInformation();

	public List<FavouriteEntity> getUserFavouriteInformationByName(String Name) throws NoRecordFoundException;

	boolean deleteUserFavouriteInformationByName(String name, String fullUrl) throws MissingMandatoryParameterException;

	//public List<FavouriteEntity> getUserCalculatorInformationByEmail(String email) throws NoRecordFoundException;



}
