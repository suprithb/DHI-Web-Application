package com.brillio.dhi.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.constants.PropertyConstant;
import com.brillio.dhi.dao.UserAuthDao;
import com.brillio.dhi.dao.UserProfileDao;
import com.brillio.dhi.dao.UserTokenDao;
import com.brillio.dhi.dao.entity.UserAuth;
import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.dao.entity.UserToken;
import com.brillio.dhi.exception.BdpMessagingException;
import com.brillio.dhi.exception.DataAlreadyExistException;
import com.brillio.dhi.exception.DhiSecurityException;
import com.brillio.dhi.exception.InvalidCredentialException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.RegExPatternMismatchException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.mapper.EntityObjectMapper;
import com.brillio.dhi.mapper.ValueObjectMapper;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.Login;
import com.brillio.dhi.model.ResponseWithToken;
import com.brillio.dhi.model.TokenInfo;
import com.brillio.dhi.model.UserList;
import com.brillio.dhi.model.UserProfile;
import com.brillio.dhi.model.UserRegistration;
import com.brillio.dhi.model.UserTokenModel;
import com.brillio.dhi.service.AuthenAuthorService;
import com.brillio.dhi.util.AuthHelper;
import com.brillio.dhi.util.MailServerUtil;
import com.brillio.dhi.util.ValidationUtil;
import com.google.gson.Gson;


@Service("authenAuthorService")
public class AuthenAuthorServiceImpl implements AuthenAuthorService{
	
	private static final Logger LOGGER = Logger.getLogger(AuthenAuthorServiceImpl.class);
	
	@Autowired
	EntityObjectMapper entityObjectMapper;
	
	@Autowired
	ValueObjectMapper valueObjectMapper;
	
	@Autowired
	UserProfileDao userProfileDao;
	
	@Autowired
	UserAuthDao userAuthDao;
	
	@Autowired
	AuthHelper authHelper;
	
	@Autowired
	UserTokenDao userTokenDao;
	
	
	
	/**
	 * This method will activate/enable the user by its userName 
	 * @param userName
	 * @return
	 * @throws MissingMandatoryParameterException
	 * @throws NoRecordFoundException
	 */
	public GenericResponse enableUserByUserName(String userName) throws MissingMandatoryParameterException, NoRecordFoundException{
		
		//Step-1 : Check for mandatory parameter
		ValidationUtil.validateMandatoryParameter(userName);
		
		//Step-2 : Check whether user exist or not
		UserAuth userAuth = userAuthDao.getUserAuthByUserName(userName);
		
		//Step-3 :  Make UserAuth with isActive enable
		userAuth.setIsActive("true");
		
		//Step-4 : Save the userAuth object in db
		userAuthDao.saveUserAuth(userAuth);
		
		//Step-5 : Form the response for activating the user
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setStatusCode("dmic_user_activated");
		genericResponse.setDescription("User successfully activated");
		
		return genericResponse;
		
	}
	
	
	/**
	 * This method will activate/enable the user by its userName 
	 * @param userName
	 * @return
	 * @throws MissingMandatoryParameterException
	 * @throws NoRecordFoundException
	 */
	public GenericResponse disableUserByUserName(String userName) throws MissingMandatoryParameterException, NoRecordFoundException{
		
		//Step-1 : Check for mandatory parameter
		ValidationUtil.validateMandatoryParameter(userName);
		
		//Step-2 : Check whether user exist or not
		UserAuth userAuth = userAuthDao.getUserAuthByUserName(userName);
		
		//Step-3 :  Make UserAuth with isActive enable
		userAuth.setIsActive("false");
		
		//Step-4 : Save the userAuth object in db
		userAuthDao.saveUserAuth(userAuth);
		
		//Step-5 : Form the response for activating the user
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setStatusCode("dmic_user_de-activated");
		genericResponse.setDescription("User successfully de-activated");
		
		return genericResponse;
		
	}
	
	
	/**
	 * This method will accept the Users and creates the token which have the access privileges and user information.
	 * @param user
	 * @return authorization token as String object
	 * @throws ServerException 
	 * @throws SerurityException 
	 * @throws DataAccessException 
	 */
	@Override
	public GenericResponse createAuthorizationToken(String userName) throws ServerException, SecurityException {
		LOGGER.debug("Entering createAuthorizationToken of AuthenAuthorServiceImpl class");
		//Step 1 : Check for the login object is null or not
		
		//Step 2 : Create the authorization token 
		 String token = AuthHelper.createJsonWebToken(userName,new Long(DHIConstants.TOKEN_DURATIONDAYS),"");
		 //System.out.println("Token values  : " + token);
		  TokenInfo tokenInfo = AuthHelper.verifyToken(token);
		  tokenInfo.setToken(token);
		 
		 
		return tokenInfo;
	}
	
	
	

	
	/**
	 * This method is used to register the user to BDP platform
	 * @param userRegistration
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws MissingMandatoryParameterException
	 * @throws RegExPatternMismatchException
	 * @throws BdpMessagingException 
	 * @throws DataAlreadyExistException 
	 * @throws DhiSecurityException 
	 */
	@Override
	public GenericResponse registerUser(UserRegistration userRegistration) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, MissingMandatoryParameterException, RegExPatternMismatchException, BdpMessagingException, DataAlreadyExistException, DhiSecurityException{
		
		boolean isUserProfileSaved = false;
		boolean isUserAuthSaved = false;
		String verificationLink = null;
		String password = userRegistration.getPassword();
		boolean isVerficationMailSentSuccessfully = false;
		
		//Step-1: Validate the captcha inputted by the user
		
		
		//Step-2: Get mandatory parameter validation attribute list
		String[] attributeList = PropertiesConfigurationReader.getUserProfileProperty(PropertyConstant.USER_REGISTRATION_VALIDATION_ATTRIBUTES).split(",");
		
		//Step-3: Validate all the mandatory parameter by passing list of attributes 
		ValidationUtil.validateMandatoryParameter(userRegistration, attributeList);
		
		//Step-4: Get the specified RegEx pattern which need to be applied on the parameter values
		String[] regExAttributeList = PropertiesConfigurationReader.getUserProfileProperty(PropertyConstant.USER_REGISTRATION_REGEX_PATTERN).split(",");
		
		//Step-5: Validate the parameter values with the specified RegEx pattern
		ValidationUtil.validateParameterWithRegex(userRegistration, regExAttributeList);
		
		
		//Step-6 : Encrypt password for the register user
		String encryptedPassword = AuthHelper.encryptAes(PropertiesConfigurationReader.getCredentialProperty(PropertyConstant.PASSWORD_KEY), PropertiesConfigurationReader.getCredentialProperty(PropertyConstant.PASSWORD_ENCRYPT_INITIALIZER), userRegistration.getPassword());
		if(encryptedPassword != null){
			LOGGER.error("Got Error while encrpting the password for the userName : "+ userRegistration.getEmail());
			userRegistration.setPassword(encryptedPassword);
		}
			
		//Step-8: Map userRegistration object to UserProfile and UserAuth entity object
		UserProfileEntity userProfile = entityObjectMapper.mapUserRegistrationToUserProfile(userRegistration);
		UserAuth userAuth = entityObjectMapper.mapUserRegistrationToUserAuthEntity(userRegistration, userProfile);
		
		//Step-9 : Check Whether the profile i.e userName and mobile number is already saved or not
		try {
			UserProfileEntity duplicateUserProfile = userProfileDao.getUserProfileByUserName(userRegistration.getEmail().trim());
			throw new DataAlreadyExistException("Email is already registered. Please choose a different email-id",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			LOGGER.debug("Valid request sent for the userNae as : " + userRegistration.getEmail());
		}
		
		//Step-10 : Check for the unique constraint mobile number
		try {
			UserProfileEntity duplicateUserProfile = userProfileDao.getUserProfileByPhoneNumber(userRegistration.getMobileNumber().trim());
			throw new DataAlreadyExistException("Phone Number is already registered. Please choose a different mobile/phone number",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			LOGGER.debug("Valid request sent for the userName with phone Number as  : " + userRegistration.getMobileNumber());
		}
		
		
		//Step-11: Save the instance of userProfile in database to User_profile table
		isUserProfileSaved = userProfileDao.saveUserProfile(userProfile);
		
		//Step-12: Save the instance of userAuth in database to User_profile table
		isUserAuthSaved = userAuthDao.saveUserAuth(userAuth);
		
		
		//Step-13: Check for whether both the profiles are saved, 
		/*if(isUserAuthSaved && isUserProfileSaved){
			//Step-13.1: Create an link to verify the account.
			verificationLink = AuthHelper.generateAccountVerificationLink(userAuth.getUsername(),password);
			
			//Step-13.2: Send email to the provided mailing address.
			 String subject = PropertiesConfigurationReader.getServerProperty(PropertyConstant.MAILING_SUBJECT_LINE_FOR_VERIFICATION_LINK);
			isVerficationMailSentSuccessfully = MailServerUtil.sendEmailWithOutAttachments(userAuth.getUsername(),verificationLink,subject);
		}*/
		
		
		
		//Step-14: If successfully sent the email, then form the response object
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setStatusCode("200");
		//genericResponse.setDescription("Email has been sent to your email-id. Please verify your account on clickling to the link.");
		genericResponse.setDescription("Account registered successfully.");
		return genericResponse;
	}
	
	
	/**
	 * This method will validate the user name and password wrt to user input
	 * @param userName
	 * @param password
	 * @return Users object
	 * @throws ServerException 
	 * @throws DataAccessException 
	 * @throws NoRecordFoundException 
	 * @throws InvalidCredentialException 
	 * @throws MissingMandatoryParameterException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */

	@Override
	public GenericResponse authenticateUser(Login login) throws NoRecordFoundException, InvalidCredentialException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, MissingMandatoryParameterException {
		//Step 1 : Get the userId and password from the controller
		LOGGER.debug("Entering authenticateUser of AuthenAuthorServiceImpl class");
		UserAuth  userAuth = null;
		UserProfileEntity userProfile = null;
		//Step-2: Get mandatory parameter validation attribute list
		String[] attributeList = PropertiesConfigurationReader.getUserProfileProperty(PropertyConstant.USER_LOGIN_VALIDATION_ATTRIBUTES).split(",");
				
		//Step-3: Validate all the mandatory parameter by passing list of attributes 
		ValidationUtil.validateMandatoryParameter(login, attributeList);
		
		//Step 2 : Based on the userId get the password,userName,role by calling to the appropiate dao methods
		userAuth = userAuthDao.getUserAuthByUserName(login.getUserName());	
		
		userProfile = userProfileDao.getUserProfileByUserName(login.getUserName());	
		
		//Step 3 : Validate the userId and Password by using some utility methods
		boolean isValidUser = false;
		if(userAuth != null)
			isValidUser = ValidationUtil.validateUserNameAndPassword(userAuth, login);
		String token = null;
		try {
			token = authHelper.getUserTokenByUserName(login.getUserName());
		}catch(NoRecordFoundException e) {
			//Not required to re-throw
		}
		
		
		String roomId = System.currentTimeMillis() + "";
		
		
		if(token == null || token.equalsIgnoreCase("")) {
			//Create a new token
			
			UserTokenModel userTokenModel = new UserTokenModel();
			userTokenModel.setChatRoomId(roomId);
			Gson gson = new Gson();
			String userTokenModelString = gson.toJson(userTokenModel);
			token = authHelper.createJsonWebToken(userAuth.getUsername(), new Long(DHIConstants.TOKEN_DURATIONDAYS),userTokenModelString);
			
			UserToken userToken = new UserToken();
			userToken.setUserName(userAuth.getUsername());
			userToken.setRemarks("New Token");
			userToken.setToken(token);
			userToken.setChatRoomId(roomId);
			userTokenDao.saveUserToken(userToken);
		}
		
		
		//Step 4 : Based on the validation response generate the message and send it back to the controller
		//String validationMessage = "User name and password does not match";
		
		if(isValidUser){
			//Step-5 : Form the success response
			ResponseWithToken responseWithToken = new ResponseWithToken();
			responseWithToken.setStatus("success");
			responseWithToken.setStatusCode("200");
			responseWithToken.setDescription("Successfully validated the user");
			responseWithToken.setFirstName(userProfile.getFirstname());
			responseWithToken.setLastName(userProfile.getLastname());
			responseWithToken.setAuthToken(token);
			responseWithToken.setChatRoomId(roomId);
			
			UserToken userToken = userTokenDao.getUserTokenByName(userAuth.getUsername());
			userToken.setChatRoomId(roomId);
			userTokenDao.saveOrUpdateUserToken(userToken);
			return responseWithToken;
		}
		LOGGER.debug("Leaving authenticateUser of AuthenAuthorServiceImpl class");
		return null;
	}
	
	
	
	/**
	 * This method is used for sending userName and password in user forgots his/her password
	 * @param email
	 * @return
	 * @throws MissingMandatoryParameterException
	 * @throws NoRecordFoundException
	 * @throws BdpMessagingException
	 * @throws InvalidCredentialException 
	 * @throws DhiSecurityException 
	 */
	@Override
	public GenericResponse forgotPassword(String email) throws MissingMandatoryParameterException, NoRecordFoundException, BdpMessagingException, InvalidCredentialException, DhiSecurityException{
		
		UserAuth userAuth = null;
		//Step-1 : Validdate the mandatory parameter for forgot password
		if(null == email || email.isEmpty() || email.trim().isEmpty()|| email.equals("NoDataProvided")){
			String message = "Missing Required parameter(s) in the request : " + email;
			LOGGER.error("Got Exception while validating the mandatory parameters.");
			LOGGER.error(message);
			throw new MissingMandatoryParameterException(message, "Required Parameter missing", "InvalidRequest");
		}
		//Step-2 : Query from the UserAuth table and get the password
		userAuth = userAuthDao.getUserAuthByUserName(email.trim());	
			
		//Step-3 : If email does not exist, send error message saying email is not registered
		
		
		//Step-4 : Decrypt the password 
		String decryptedPassword = AuthHelper.decryptAes(PropertiesConfigurationReader.getCredentialProperty(PropertyConstant.PASSWORD_KEY), PropertiesConfigurationReader.getCredentialProperty(PropertyConstant.PASSWORD_ENCRYPT_INITIALIZER), userAuth.getPassword());
    	if(decryptedPassword == null){
    		LOGGER.error("Got Error while dencrpting the password for the userName : "+ userAuth.getUsername());
    		throw new InvalidCredentialException("You have entered incorrect user-name or password.",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
    	}
		
		//Step-4: Create an email body for forgot password.
		String emailBody = AuthHelper.getForgotPasswordMailBody(userAuth.getUsername(),decryptedPassword);
			
		//Step-5: Send email to the provided mailing address.
		String subject = PropertiesConfigurationReader.getServerProperty(PropertyConstant.MAILING_SUBJECT_LINE_FOR_FORGOT_PASSWORD);
		boolean isMailSentSuccessfully = MailServerUtil.sendEmailWithOutAttachments(userAuth.getUsername(),emailBody,subject);
	
		// Step-10: If successfully sent the email, then form the response
		// object
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setStatusCode("200");
		genericResponse.setDescription("Email has been sent to your email-id with user-name and password. Please check your email.");
		
		return genericResponse;
		
	}
	
	/**
	 * This method will return list of all registered users 
	 * @return
	 */
	@Override
	public GenericResponse getAllRegisteredUser(){
		List<UserProfileEntity> userProfileEntityList = userProfileDao.getAllUserProfiles();
		List<UserProfile> userProfileList = new ArrayList<UserProfile>();
		for(UserProfileEntity userProfileEntity : userProfileEntityList){
			UserProfile userProfile = valueObjectMapper.mapUserProfileEntityToUserProfile(userProfileEntity);
			userProfileList.add(userProfile);
		}
		
		UserList userList = new UserList();
		userList.setUserProfileList(userProfileList);
		return userList;
	}
	
	
	
	
	
	/*
	 * This method will delete and deregister the user from BDP
	 */
	@Override
	public GenericResponse deleteRegisteredUser(String userName) throws MissingMandatoryParameterException{
		
		GenericResponse genericResponse = new GenericResponse();
		
		//Step-1 : Validate for mandatory parameter 
		ValidationUtil.validateMandatoryParameter(userName);
		
		//Step-2 : check whether the user exist or not, if yes delete the user
		
		//Step-3 : If user does not exist throw error message saying user does not exist 
		boolean isUserProfileDeleted = userProfileDao.deleteUserProfileByUserName(userName);
		boolean isUserAuthDeleted = userAuthDao.deleteUserAuthByUserName(userName);
		
		if(isUserProfileDeleted && isUserAuthDeleted){
			genericResponse.setStatus(DHIConstants.CA_SUCCESS);
			genericResponse.setStatus(DHIConstants.CA_ENTITY_DELETED);
			genericResponse.setDescription("Successfully deleted the profile.");
		}
		
		genericResponse.setStatus(DHIConstants.ERROR);
		genericResponse.setStatus(DHIConstants.DHI_BAD_REQUEST);
		genericResponse.setDescription("Unable to delete the profile");
		
		//Step-4 : Else send success message
		return genericResponse;
		
	}
	
	
	@Override
	public GenericResponse getUserNameByPhoneNumber(String phoneNumber) throws NoRecordFoundException {
		UserProfileEntity userProfileEntity = userProfileDao.getUserProfileByPhoneNumber(phoneNumber);
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setDescription(userProfileEntity.getFirstname());
		return genericResponse;
		
	}
	
	public void getUserInformationFromToken() {
		
		//df
	}
		
	
	
	
}
