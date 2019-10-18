package com.brillio.dhi.service;

import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.model.UserFavouriteResponse;
import com.brillio.dhi.model.FavouriteRequestModel;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.UserFavourite;

public interface UserFavouriteService {

	UserFavouriteResponse saveUserFavourite(String userName, String fullUrl, String title) throws MissingMandatoryParameterException;

	UserFavouriteResponse getUserFavouriteByName(String userName) throws NoRecordFoundException;

	GenericResponse deleteUserFavourite(String userName, FavouriteRequestModel favouriteRequestModel)
			throws MissingMandatoryParameterException, NoRecordFoundException;

}
