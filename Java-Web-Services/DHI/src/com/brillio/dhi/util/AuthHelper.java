package com.brillio.dhi.util;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.constants.PropertyConstant;
import com.brillio.dhi.dao.UserTokenDao;
import com.brillio.dhi.dao.entity.UserToken;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.TokenInfo;
import com.brillio.dhi.model.UserTokenModel;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;


@Service
public class AuthHelper {

	private static final Logger LOGGER = Logger.getLogger(AuthHelper.class);
	
    private static final String AUDIENCE = "BrillioDataPlatform";

    private static final String ISSUER = "BrillioTechnologies";

    private static final String SIGNING_KEY = "LongAndHardToGuessValueWithSpecialCharacters@^($%*$%";
    
    
    @Autowired
    UserTokenDao userTokenDao;
   	
    
    /**
     * Creates a json web token which is a digitally signed token that contains a payload (e.g. userId to identify 
     * the user). The signing key is secret. That ensures that the token is authentic and has not been modified.
     * Using a jwt eliminates the need to store authentication session information in a database.
     * @param userId
     * @param durationDays
     * @return
     * @throws SerurityException 
     */
    public static String createJsonWebToken(String userName, Long durationDays, String userTokenInformation) throws SecurityException    {
    	
        //Current time and signing algorithm
        Calendar cal = Calendar.getInstance();
        HmacSHA256Signer signer;
        try {
            signer = new HmacSHA256Signer(ISSUER, null, SIGNING_KEY.getBytes());
                //Configure JSON token
            JsonToken token = new net.oauth.jsontoken.JsonToken(signer);
            token.setAudience(AUDIENCE);
            token.setIssuedAt(new org.joda.time.Instant(cal.getTimeInMillis()));
            token.setExpiration(new org.joda.time.Instant(cal.getTimeInMillis() + 1000L * 60L * 60L * 60L * durationDays));
        
            //Configure request object, which provides information of the item
            JsonObject request = new JsonObject();
            request.addProperty(DHIConstants.USER_ID, userName);
            request.addProperty(DHIConstants.USER_TOKEN_INFORMATION, userTokenInformation);
            long timeInMillis = System.currentTimeMillis();
            request.addProperty("sessionTimestamp", timeInMillis+"");
           // request.addProperty(EKGConstants.AUTH_VALUES, authValues);

            JsonObject payload = token.getPayloadAsJsonObject();
            payload.add(DHIConstants.INFO, request);

            return token.serializeAndSign();
       
        	} 
        catch (SignatureException | InvalidKeyException e)
        {
        	 throw new SecurityException();
        }
    }

    /**
     * Verifies a json web token's validity and extracts the user id and other information from it. 
     * @param token
     * @return
     * @throws SerurityException 
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    public static TokenInfo verifyToken(String token) throws SecurityException  
    {
        try {
            final Verifier hmacVerifier = new HmacSHA256Verifier(SIGNING_KEY.getBytes());

            VerifierProvider hmacLocator = new VerifierProvider() {

                @Override
                public List<Verifier> findVerifier(String id, String key){
                    return Lists.newArrayList(hmacVerifier);
                }
            };
            VerifierProviders locators = new VerifierProviders();
            locators.setVerifierProvider(SignatureAlgorithm.HS256, hmacLocator);
            net.oauth.jsontoken.Checker checker = new net.oauth.jsontoken.Checker(){

                @Override
                public void check(JsonObject payload) throws SignatureException {
                    // don't throw - allow anything
                }

            };
            //Ignore Audience does not mean that the Signature is ignored
            JsonTokenParser parser = new JsonTokenParser(locators,
                    checker);
            JsonToken jt = null;
        	   jt = parser.verifyAndDeserialize(token);
               
            
            JsonObject payload = jt.getPayloadAsJsonObject();
            TokenInfo t = new TokenInfo();
            String issuer = payload.getAsJsonPrimitive(DHIConstants.ISS).getAsString();
            String userIdString =  payload.getAsJsonObject(DHIConstants.INFO).getAsJsonPrimitive(DHIConstants.USER_ID).getAsString();
            String userTokenInformation =  payload.getAsJsonObject(DHIConstants.INFO).getAsJsonPrimitive(DHIConstants.USER_TOKEN_INFORMATION).getAsString();
            String userSessionTimestamp = payload.getAsJsonObject(DHIConstants.INFO).getAsJsonPrimitive("sessionTimestamp").getAsString();
            
           // String authValues =  payload.getAsJsonObject(EKGConstants.INFO).getAsJsonPrimitive(EKGConstants.AUTH_VALUES).getAsString();
            if (issuer.equals(ISSUER) && userIdString !=DHIConstants.EMPTY_STRING)
            {
                t.setUserId(new String(userIdString));
                t.setUserInformation(userTokenInformation);
                t.setSessionTimestamp(new Timestamp(new Long(userSessionTimestamp)));
               // t.setAuthValues(authValues);
                t.setIssued(new DateTime(payload.getAsJsonPrimitive(DHIConstants.IAT).getAsLong()));
                t.setExpires(new DateTime(payload.getAsJsonPrimitive(DHIConstants.EXP).getAsLong()));
                return t;
            }
            else
            {
                return null;
            }
        } catch (InvalidKeyException | SignatureException  e) {
        	 throw new SecurityException();
        }catch(Exception e){
        	throw new SecurityException();
        }
    }
    
   
    
    /**
     * This method will create the verification link
     * @param userName
     * @return
     */
    public static String generateAccountVerificationLink(String userName,String password){
    	LOGGER.debug("Entering generateAccountVerificationLink of AuthHelper class");
    	//Step-1 : Get the base url to which user will click to perform verification
    	String baseUrl = PropertiesConfigurationReader.getWebServiceProperty(PropertyConstant.WEB_SITE_URL);
    	
    	String verficationToken = createJsonWebToken(userName, new Long(DHIConstants.TOKEN_DURATIONDAYS),"");
    	
    	baseUrl += "?hash="+ verficationToken;
    	
    	String message = "Thanks for signing up!\t\n\n\nYour account has been created, you can login with the following credentials after you have activated your account by clicking the url below.\t\n";
    			 
    	String uidPwd	= "\t\n\n------------------------ \t\n\nUsername:"+userName +"\t\n\nPassword: "+ password +"\t\n\n------------------------";
    			 
    	String clickActivateString = " \t\nPlease click this link to activate your account: \t\n\n" + baseUrl + "\t\n\n";
    	message += uidPwd;
    	message += clickActivateString;
    	LOGGER.debug("Leaving generateAccountVerificationLink of AuthHelper class");
    	return message;
    }
    
    
    /**
     * This method will send userName and password
     * @param userName
     * @return
     */
    public static String getForgotPasswordMailBody(String userName,String password){
    	LOGGER.debug("Entering generateAccountVerificationLink of AuthHelper class");
 
    	String message = "Your login credential is : \t\n";
    			 
    	String uidPwd	= "\t\n------------------------ \t\n\nUsername:"+userName +"\t\n\nPassword: "+ password +"\t\n------------------------";
    			 
    	String clickActivateString = " \t\nPlease use the above credentials to login your account: \t\n\n" ;
    	message += uidPwd;
    	message += clickActivateString;
    	LOGGER.debug("Leaving generateAccountVerificationLink of AuthHelper class");
    	return message;
    }
    
    /**
     * This mehthod will perform the aes encryption
     * @param value
     * @return
     */
    public static String encryptAes(String privateKey,String publicKey,String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes(DHIConstants.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(DHIConstants.UTF_8), DHIConstants.AES);

            Cipher cipher = Cipher.getInstance(DHIConstants.AES_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
       /*     System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted));*/

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    
    /**
     * This method is to decrypt the aes encrypted string
     * @param key
     * @param initVector
     * @param encrypted
     * @return
     */
    public static String decryptAes(String privateKey,  String publicKey, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes(DHIConstants.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(DHIConstants.UTF_8), DHIConstants.AES);

            Cipher cipher = Cipher.getInstance(DHIConstants.AES_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    
    /**
     * This method will get the user-token for the provided userName, which is saved in the database.
     * @param userName
     * @return
     * @throws NoRecordFoundException
     */
    public String getUserTokenByUserName(String userName) throws NoRecordFoundException {
    	
    	UserToken userToken = userTokenDao.getUserTokenByName(userName);
    	String token = userToken.getToken();
    	
    	if(token == null || token.trim().equalsIgnoreCase("")) {
    		throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_token_not_found_error");
    	}
    	return token;
    }
    
    
    /**
     * This method will return the user information, which is present in the token.
     * @param token
     * @return
     * @throws ServerException
     * @throws NoRecordFoundException
     */
    public UserTokenModel getUserTokenModelFromToken(String token) throws ServerException, NoRecordFoundException {
    	TokenInfo tokenInfo = AuthHelper.verifyToken(token);
    	
    	String userInformation = tokenInfo.getUserInformation();
    	
    	if(userInformation == null || userInformation.trim().equalsIgnoreCase("")) {
    		throw new NoRecordFoundException("User token information not found in the provided token","error","dhi_user_information_not_found_error");
    	}
    	
    	Gson gson = new Gson();
    	UserTokenModel userTokenModel  = null;
    	try {
    		userTokenModel = gson.fromJson(userInformation, UserTokenModel.class);
    	}catch(JsonSyntaxException e) {
    		throw new ServerException("Errro occured while mapping with UserTokenModel, Json syntax error", "error", "dhi_token_mapping_error");
    	}
    	
    	if(userTokenModel == null) {
    		throw new NoRecordFoundException("User token information not found in the provided token","error","dhi_user_information_not_found_error");
    	}
    	return userTokenModel;
    }
    
    
    

}