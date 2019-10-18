package com.brillio.dhi.util;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.constants.PropertyConstant;
import com.brillio.dhi.dao.entity.UserAuth;
import com.brillio.dhi.exception.InvalidCredentialException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.RegExPatternMismatchException;
import com.brillio.dhi.model.IDHI;
import com.brillio.dhi.model.Login;


/**
 * This class is will be used for only validating the mandatory parameters and also to validating all the possible values that a parameter can have.
 * @author manmaya.champatiray
 *
 */
public class ValidationUtil {
	
	
	private static final Logger LOGGER = Logger.getLogger(ValidationUtil.class);
	
	/**
	 * This method takes an input as BDP (Base Interface),String array and throws exception if mandatory value is not present
	 * @param src
	 * @return
	 * @throws MissingMandatoryParameterException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static boolean validateMandatoryParameter(IDHI src, String[] attributeList) throws MissingMandatoryParameterException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		LOGGER.debug("Entering validateMandatoryParameter method of class ValidationUtil");
		boolean isValidRequest = true;
		String message = null;
		
		Class srcClass = src.getClass();
		for(String keyWithAttribute:attributeList){
			String[] keyWithAttrArray = keyWithAttribute.split(":");
			Field srcField = srcClass.getField(keyWithAttrArray[0]);
			String attribute = (String)srcField.get(src);
			if(null == attribute || attribute.isEmpty() || attribute.trim().isEmpty()|| attribute.equals("NoDataProvided")){
				if(isValidRequest){
					message = "Missing Required parameter(s) in the request : " + keyWithAttrArray[1];
					isValidRequest = false;
				}else{
					message += ", ";
					message += keyWithAttrArray[1];
				}	
			}
		}
		if(isValidRequest == false){
			LOGGER.error("Got Exception while validating the mandatory parameters.");
			LOGGER.error(message);
			throw new MissingMandatoryParameterException(message, "Required Parameter missing", "InvalidRequest");
		}
		LOGGER.debug("Leaving validateMandatoryParameter method of class ValidationUtil with response : " + true);
		return true;
	}
	
	
	/**
	 * This method will validate a string, if it is null or empty then it will throw an exception
	 * @param src
	 * @return
	 * @throws MissingMandatoryParameterException
	 */
	public static boolean validateMandatoryParameter(String src) throws MissingMandatoryParameterException{
		LOGGER.debug("Entering validateMandatoryParameter method of class ValidationUtil");
		boolean isValidRequest = true;
		String message = null;
		if(null == src || src.isEmpty() || src.trim().isEmpty()|| src.equals("NoDataProvided")){
			LOGGER.error("Got error in validateMandatoryParameter method of class ValidationUtil : Missing Required parameter(s) in the request ");
			throw new MissingMandatoryParameterException("Missing Required parameter(s) in the request ",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
		}
		LOGGER.debug("Entering validateMandatoryParameter method of class ValidationUtil");
		return true;
	}
	
	
	
	/**
	 * This method takes an input as BDP (Base Interface),String array and throws exception if the input value does not match with REGEX pattern
	 * @param src
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws RegExPatternMismatchException 
	 */
	public static boolean validateParameterWithRegex(IDHI src, String[] attributeList) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, RegExPatternMismatchException{
		LOGGER.debug("Entering validateParameterWithRegex method of class ValidationUtil");
		boolean isValidRequest = true;
		String message = "Pattern mismatch error in the request for the parameter : ";
		Class srcClass = src.getClass();
		for(String keyWithAttribute:attributeList){
			String[] keyWithAttrArray = keyWithAttribute.split(":");
			Field srcField = srcClass.getField(keyWithAttrArray[0]);
			String attribute = (String)srcField.get(src);
			if(null != keyWithAttrArray[0] || !keyWithAttrArray[0].isEmpty() || !keyWithAttrArray[0].trim().isEmpty()|| !keyWithAttrArray[0].equals("NoDataProvided")){
				switch(keyWithAttrArray[0]){
				case PropertyConstant.PERSON_NAME_REGEX:
					isValidRequest = validateName(keyWithAttrArray[0]);
					break;
				case PropertyConstant.EMAIL_REGEX:
					isValidRequest = validateEmail(keyWithAttrArray[0]);
					break;
				case PropertyConstant.PHONE_NUMBER_REGEX:
					isValidRequest = validateMobileNumber(keyWithAttrArray[0]);
					break;
				case PropertyConstant.PASSWORD_REGEX:
					isValidRequest = validatePassword(keyWithAttrArray[0]);
					break;
				
				}
				if(isValidRequest == false){
					message += attribute;
					LOGGER.error("Got Exception while validating parameter values for the Regular Expression ");
					LOGGER.error(message);
					throw new RegExPatternMismatchException(message, "Pattern Mismatch Exception", "InvalidRequest");
				}
			}
		}
		
		LOGGER.debug("Leaving validateParameterWithRegex method of class ValidationUtil with response : " + true);
		return true;
	}
	
	
	
	
	 /**
     * This method will validate the userName and password with user input and data with database
     * @param userAuth
     * @param loginUser
     * @return
	 * @throws InvalidCredentialException 
     */
    public static boolean validateUserNameAndPassword(UserAuth userAuth,Login loginUser) throws InvalidCredentialException{
		
    	//Step-6 : Encrypt password for the register user
    	String decryptedPassword = AuthHelper.decryptAes(PropertiesConfigurationReader.getCredentialProperty(PropertyConstant.PASSWORD_KEY), PropertiesConfigurationReader.getCredentialProperty(PropertyConstant.PASSWORD_ENCRYPT_INITIALIZER), userAuth.getPassword());
    	if(decryptedPassword == null){
    		LOGGER.error("Got Error while dencrpting the password for the userName : "+ userAuth.getUsername());
    		throw new InvalidCredentialException("You have entered incorrect user-name or password.",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
    	}
    	if(DHIUtil.trimString(userAuth.getUsername()).equals(DHIUtil.trimString(loginUser.getUserName())) && DHIUtil.trimString(decryptedPassword).equals(DHIUtil.trimString(loginUser.getPassword()))){
    		return true;
    	}
    	throw new InvalidCredentialException("You have entered incorrect user-name or password.",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);	
    	
    }
	
	

	
	
	/**
	 * This method will validate the input email with the REGEX pattern as ...
	 * @param email
	 * @return
	 * 
	 */
	public static boolean validateEmail(String email){
		
		return false;
	}
	
	/**
	 * This method will validate the input, person name with the REGEX pattern as ...
	 * @param name
	 * @return
	 * 
	 */
	public static boolean validateName(String name){
		
		return false;
	}
	
	
	/**
	 * This method will validate the input, password with the REGEX pattern as ...
	 * @param password
	 * @return
	 * 
	 */
	public static boolean validatePassword(String password){
		
		return false;
	}
	
	
	/**
	 * This method will validate the input, mobileNumber with the REGEX pattern as ...
	 * @param mobileNumber
	 * @return
	 * 
	 */
	public static boolean validateMobileNumber(String mobileNumber){
		
		return false;
	}
	
	

}
