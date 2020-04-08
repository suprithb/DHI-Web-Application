package com.brillio.dhi.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.constants.URIConstants;
import com.brillio.dhi.exception.DhiMessagingException;
import com.brillio.dhi.exception.DataAlreadyExistException;
import com.brillio.dhi.exception.DhiSecurityException;
import com.brillio.dhi.exception.InvalidCredentialException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.RegExPatternMismatchException;
import com.brillio.dhi.model.DHIResponse;
import com.brillio.dhi.model.ForgotPassword;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.Login;
import com.brillio.dhi.model.UserRegistration;
import com.brillio.dhi.service.AuthenAuthorService;

/**
 * 
 * @author manmaya.champatiray This class is used to provide authentication and
 *         authorization functionality for the DHI application.
 *
 */

@Controller
public class AuthenAuthorController {

	private static final Logger LOGGER = Logger.getLogger(AuthenAuthorController.class);

	@Autowired
	AuthenAuthorService authenAuthorService;

	@RequestMapping(value = URIConstants.GENERATE_AUTH_TOKEN, method = RequestMethod.GET)

	/**
	 * This method is used to generate the JWT token for the given user.
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public @ResponseBody DHIResponse generateAuthToken(@RequestParam("userName") String userName,
			@RequestParam("password") String password) {
		LOGGER.debug("Entering generateAuthToken of AuthenAuthorController class");
		GenericResponse genericResponse = new GenericResponse();
		try {
			if (userName != null && !userName.equalsIgnoreCase(""))
				genericResponse = authenAuthorService.createAuthorizationToken(userName);
			else if (password != null && !password.equalsIgnoreCase(""))
				genericResponse = authenAuthorService.createAuthorizationToken(password);
		} catch (com.brillio.dhi.exception.ServerException e) {
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_SERVER_INTERNAL_ERROR);
			genericResponse.setDescription(e.getMessage());
		} catch (SecurityException e) {
			// LOGGER.error("Error while registering user " + e.getMessage());
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_SECURITY_EXCEPTION);
			genericResponse.setDescription(e.getMessage());
		}
		LOGGER.debug("Leaving generateAuthToken of AuthenAuthorController class with response : " + genericResponse);
		return genericResponse;
	}

	/**
	 * This method will be used for registering the new user.
	 * 
	 * @param userName
	 * @return
	 */
	@RequestMapping(value = URIConstants.USER_SIGN_UP, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody DHIResponse registerUser(@RequestBody UserRegistration userRegistration) {
		LOGGER.debug("Entering registerUser of AuthenAuthorController class");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse = authenAuthorService.registerUser(userRegistration);
			LOGGER.debug("Successfully registered the user with userName : " + userRegistration.getEmail());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			LOGGER.error("Error while registering user, Error occured due to reflection field are pubicly available.");
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_SERVER_INTERNAL_ERROR);
			genericResponse.setDescription("Error occured due to reflection field are pubicly not available.");
		} catch (MissingMandatoryParameterException | RegExPatternMismatchException | DhiMessagingException e) {
			LOGGER.error("Error while registering user " + e.getMessage());
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_BAD_REQUEST);
			genericResponse.setDescription(e.getMessage());
		} catch (DataAlreadyExistException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
		} catch (DhiSecurityException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
		}
		LOGGER.debug("Leaving registerUser of AuthenAuthorController class with response : " + genericResponse);
		return genericResponse;

	}

	/**
	 * This method will be used for login the user.
	 * 
	 * @param userName
	 * @return
	 */
	@RequestMapping(value = URIConstants.USER_LOGIN, method = RequestMethod.POST)
	public @ResponseBody DHIResponse loginUser(@RequestBody Login login) {
		LOGGER.debug("Entering loginUser of AuthenAuthorController class");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse = authenAuthorService.authenticateUser(login);
			LOGGER.debug("Successfully logged-in the user with userName : " + login.getUserName());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			LOGGER.error(
					"Error while registering user, Error occured due to reflection field are pubicly not available.");
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_SERVER_INTERNAL_ERROR);
			genericResponse.setDescription("Error occured due to reflection field are pubicly available.");
		} catch (InvalidCredentialException | MissingMandatoryParameterException e) {
			LOGGER.error("Error while login with userName " + login.getUserName());
			LOGGER.error("Error message : " + e.getMessage());
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_BAD_REQUEST);
			genericResponse.setDescription(e.getMessage());
		} catch (NoRecordFoundException e) {
			LOGGER.error("Error while login with userName " + login.getUserName());
			LOGGER.error("You have entered incorrect user-name or password.");
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_BAD_REQUEST);
			genericResponse.setDescription("You have entered incorrect user-name or password.");
		}

		LOGGER.debug("Leaving registerUser of loginUser class with response : " + genericResponse);
		return genericResponse;

	}

	/**
	 * This method will be used to retrieve the password set by the user.
	 * 
	 * @param ForgotPassword
	 * @return
	 */
	@RequestMapping(value = URIConstants.USER_FORGOT_PASSWORD, method = RequestMethod.POST)
	public @ResponseBody DHIResponse forgotPassword(@RequestBody ForgotPassword forgotPassword) {
		LOGGER.debug("Entering forgotPassword of AuthenAuthorController class");
		GenericResponse genericResponse = new GenericResponse();

		try {
			genericResponse = authenAuthorService.forgotPassword(forgotPassword.getEmail());
			LOGGER.debug(
					"Successfully send mail with userName and password to mailingId  : " + forgotPassword.getEmail());
		} catch (MissingMandatoryParameterException | NoRecordFoundException | DhiMessagingException e) {
			LOGGER.error(
					"Error while retreiving  userName and Password for email address :" + forgotPassword.getEmail());
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_BAD_REQUEST);
			genericResponse.setDescription(e.getMessage());
		} catch (InvalidCredentialException e) {
			LOGGER.error(
					"Error while retreiving  userName and Password for email address :" + forgotPassword.getEmail());
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_BAD_REQUEST);
			genericResponse.setDescription(e.getMessage());
		} catch (DhiSecurityException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
		}

		LOGGER.debug("Leaving forgotPassword of AuthenAuthorController class with response : " + genericResponse);
		return genericResponse;

	}

	/**
	 * This method is used for getting list of all registered users.
	 * 
	 * @return
	 */
	@RequestMapping(value = URIConstants.REGISTERED_USER_LIST, method = RequestMethod.GET)
	public @ResponseBody DHIResponse getAllRegisteredUser() {
		GenericResponse genericResponse = new GenericResponse();
		genericResponse = authenAuthorService.getAllRegisteredUser();
		return genericResponse;
	}

	@RequestMapping(value = "/api/phone-number/user-name", method = RequestMethod.GET)
	public @ResponseBody DHIResponse getUserNameByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse = authenAuthorService.getUserNameByPhoneNumber(phoneNumber);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(DHIConstants.ERROR);
			genericResponse.setStatusCode(DHIConstants.DHI_BAD_REQUEST);
			genericResponse.setDescription(e.getMessage());
		}
		return genericResponse;
	}

	@RequestMapping(value = "/api/working-status", method = RequestMethod.GET)
	public ResponseEntity<String> appWorkingStatus() {
		String body = "app is working";
		return new ResponseEntity<>(body, HttpStatus.OK);
	}
}
