package com.brillio.dhi.service;

import com.brillio.dhi.exception.DhiMessagingException;
import com.brillio.dhi.exception.DhiSecurityException;
import com.brillio.dhi.exception.DataAlreadyExistException;
import com.brillio.dhi.exception.InvalidCredentialException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.RegExPatternMismatchException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.Login;
import com.brillio.dhi.model.UserRegistration;

public interface AuthenAuthorService {
	
	
	
	public GenericResponse createAuthorizationToken(String userName) throws ServerException, SecurityException;

	public GenericResponse registerUser(UserRegistration userRegistration)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			MissingMandatoryParameterException, RegExPatternMismatchException, DhiMessagingException, DataAlreadyExistException, DhiSecurityException;

	public GenericResponse authenticateUser(Login login) throws NoRecordFoundException, InvalidCredentialException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, MissingMandatoryParameterException;

	public GenericResponse forgotPassword(String email)
			throws MissingMandatoryParameterException, NoRecordFoundException, DhiMessagingException, InvalidCredentialException, DhiSecurityException;

	public GenericResponse getAllRegisteredUser();

	public GenericResponse deleteRegisteredUser(String userName) throws MissingMandatoryParameterException;

	GenericResponse getUserNameByPhoneNumber(String phoneNumber) throws NoRecordFoundException;
	
	//public Users authenticateUser(String userId,String password) throws ServerException, SerurityException;
	
	//public String validateAuthorizationToken(String token,String pageName) throws SerurityException;

}
