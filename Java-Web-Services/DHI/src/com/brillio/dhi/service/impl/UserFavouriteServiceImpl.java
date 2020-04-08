package com.brillio.dhi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.dao.UserFavouriteDao;
import com.brillio.dhi.dao.UserProfileDao;
import com.brillio.dhi.dao.entity.FavouriteEntity;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.mapper.EntityObjectMapper;
import com.brillio.dhi.mapper.ValueObjectMapper;
import com.brillio.dhi.model.FavouriteRequestModel;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.UserFavourite;
import com.brillio.dhi.model.UserFavouriteResponse;
import com.brillio.dhi.service.UserFavouriteService;



@Service
public class UserFavouriteServiceImpl implements UserFavouriteService{
	
	
	
	@Autowired
	EntityObjectMapper entityObjectMapper;
	
	@Autowired
	ValueObjectMapper valueObjectMapper;
	
	@Autowired
	UserFavouriteDao userFavouriteDao;
	
	@Autowired
	UserProfileDao userProfileDao;
	
	
	
	@Override
	public GenericResponse deleteUserFavourite(String userName, FavouriteRequestModel favouriteRequestModel) throws MissingMandatoryParameterException, NoRecordFoundException {
		
		
		if(userName == null || userName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name","error","dhi_missing_required_parameter");
		}
		
		if(favouriteRequestModel == null || favouriteRequestModel.getFullUrl() == null || favouriteRequestModel.getFullUrl().trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : fullUrl","error","dhi_missing_required_parameter");
		}
		
		boolean isDeleted = userFavouriteDao.deleteUserFavouriteInformationByName(userName.trim(), favouriteRequestModel.getFullUrl().trim());
		
		GenericResponse genericResponse = new GenericResponse();
		
		if(!isDeleted) {
			throw new NoRecordFoundException("Error : Seems like record not present in the database.","error","dhi_data_not_found_error");
		}else {
			genericResponse.setStatus("error");
			genericResponse.setStatusCode("dhi_data_not_found_error");
			genericResponse.setDescription("Error : Seems like record not present in the database.");
		}
		genericResponse.setStatus("success");
		genericResponse.setDescription("Successfully removed the user-favourite");
		return genericResponse;
	}
	
	@Override
	public UserFavouriteResponse saveUserFavourite(String userName, String fullUrl, String title) throws MissingMandatoryParameterException {
		
		if(userName == null || userName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name","error","dhi_missing_required_parameter");
		}
		
		if(fullUrl == null || fullUrl.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : fullUrl","error","dhi_missing_required_parameter");
		}
		
		/*if(fileName == null || fileName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : fileName","error","dhi_missing_required_parameter");
		}*/
		
		
		/*String favouriteFileName = "";
		if(fullUrl != null && !("".equals(fullUrl.trim()))) {
			favouriteFileName = replaceFullFileUrl(fullUrl);
		}else if(fileName != null && !(fileName.trim().equals(""))) {
			favouriteFileName = fileName.trim();
		}*/
		
		FavouriteEntity userCalculatorInformationEntity = entityObjectMapper.mapUserFavouriteToUserFavouriteEntity(userName,fullUrl, title);
		
		boolean isSaved = userFavouriteDao.saveUserFavouriteInformation(userCalculatorInformationEntity);
		
		UserFavouriteResponse userFavouriteResponse = new UserFavouriteResponse();
		
		if(isSaved) {
			userFavouriteResponse.setStatus("success");
			userFavouriteResponse.setDescription("Successfully saved the provided information to the favourite ");
		}else {
			userFavouriteResponse.setStatus("error");
			userFavouriteResponse.setStatusCode("dhi_data_saving_error");
			userFavouriteResponse.setDescription("Error while saving the user favourite information.");
		}
		
		return userFavouriteResponse;
		
	}
	
	
	public String replaceFullFileUrl(String fullUrl) {
		
		String imageUrlPath = PropertiesConfigurationReader.getServerProperty("visualization_image_host");
		imageUrlPath += "/DHI/assets/";
		String imageName = fullUrl.replace(imageUrlPath, "");
		return imageName;
	}
	
	@Override
	public UserFavouriteResponse getUserFavouriteByName(String name) throws NoRecordFoundException {
		
		//userProfileDao.getUserProfileByUserName(name)
		List<FavouriteEntity> userCalculatorInformationEntityList = userFavouriteDao.getUserFavouriteInformationByName(name);
		List<UserFavourite> userFavouriteList = valueObjectMapper.mapUserFavouriteEntityListToUserFavouriteList(userCalculatorInformationEntityList);
		
		UserFavouriteResponse userFavouriteResponse = new UserFavouriteResponse();
		
		if(userFavouriteList != null && userFavouriteList.size() > 0) {
			userFavouriteResponse.setUserFavourites(userFavouriteList);
		}else {
			userFavouriteResponse.setStatus("error");
			userFavouriteResponse.setStatusCode("dhi_data_fetching_error");
			userFavouriteResponse.setDescription("Error while retrieving the user calcualation information.");
		}
		
		return userFavouriteResponse;
		
	}
	
	/*@Override
	public UserFavouriteResponse getUserCalculationInformationByEmail(String email) throws NoRecordFoundException {
		
		
		List<UserCalculatorInformationEntity> userCalculatorInformationEntityList = userCalculatorInformationDao.getUserCalculatorInformationByEmail(email);
		List<UserCalculatorScore> userCalculatorScoreList = valueObjectMapper.mapUserCalculatorInformationEntityListToUserCalculatorScoreList(userCalculatorInformationEntityList);
		
		UserFavouriteResponse userCalculatorScoreResponse = new UserFavouriteResponse();
		
		if(userCalculatorScoreList != null && userCalculatorScoreList.size() > 0) {
			userCalculatorScoreResponse.setUserInformations(userCalculatorScoreList);
		}else {
			userCalculatorScoreResponse.setStatus("error");
			userCalculatorScoreResponse.setStatusCode("dcim_data_fetching_error");
			userCalculatorScoreResponse.setDescription("Error while retrieving the user calcualation information.");
		}
		
		return userCalculatorScoreResponse;
		
	}
	
	@Override
	public UserFavouriteResponse getUserCalculationInformations() throws NoRecordFoundException {
		
		List<UserCalculatorInformationEntity> userCalculatorInformationEntityList = userCalculatorInformationDao.getAllUserCalculatorInformation();
		List<UserCalculatorScore> userCalculatorScoreList = valueObjectMapper.mapUserCalculatorInformationEntityListToUserCalculatorScoreList(userCalculatorInformationEntityList);
		
		UserFavouriteResponse userCalculatorScoreResponse = new UserFavouriteResponse();
		
		if(userCalculatorScoreList != null && userCalculatorScoreList.size() > 0) {
			userCalculatorScoreResponse.setUserInformations(userCalculatorScoreList);
		}else {
			userCalculatorScoreResponse.setStatus("error");
			userCalculatorScoreResponse.setStatusCode("dcim_data_fetching_error");
			userCalculatorScoreResponse.setDescription("Error while retrieving the user calcualation information.");
		}
		
		return userCalculatorScoreResponse;
		
	}*/

}
