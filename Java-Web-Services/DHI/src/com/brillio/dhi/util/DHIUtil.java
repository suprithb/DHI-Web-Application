package com.brillio.dhi.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.exception.DhiSecurityException;
import com.brillio.dhi.exception.InvalidDataException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.model.DiscoveryModelColumns;
import com.brillio.dhi.model.IDHI;
import com.brillio.dhi.model.TokenInfo;

@Component
public class DHIUtil {
	
	private static final Logger LOGGER = Logger.getLogger(DHIUtil.class);
	
	
	/**
	 * This method will trim the source string 
	 * @param src
	 * @return
	 */
	public static String trimString(String src){
		
		if(src != null)
			src.trim();
		return src;
	}
	
	/**
	 * This method will generate a string by appending the source string and timestamp separated by hypen "-".
	 * @param src
	 * @return
	 */
	public static String generateStringWithTimestamp(String src){
		src += " - ";
		src +=System.currentTimeMillis();
		
		return src;
		
	}

	
	
	/**
	 * This method will extract the userName from the token
	 * @param token
	 * @return
	 * @throws DhiSecurityException
	 */
	public static String getUserNameFromToken(String token) throws DhiSecurityException{
	   TokenInfo tokenInfo = null;
	   try{
		   tokenInfo =  AuthHelper.verifyToken(token);
	    	if(null != tokenInfo.getUserId() || !tokenInfo.getUserId().isEmpty() || !tokenInfo.getUserId().trim().isEmpty()|| !tokenInfo.getUserId().equals("NoDataProvided")){
				return tokenInfo.getUserId();
	    	}
	   }catch(SecurityException e){
	    	throw new DhiSecurityException("The user is not authorized. Please contact to BDP Administrator.",DHIConstants.ERROR,DHIConstants.CA_INVALID_TOKEN);
	    }
	    throw new DhiSecurityException("The user is not authorized. Please contact to BDP Administrator.",DHIConstants.ERROR,DHIConstants.DMIC_MISSING_TOKEN);
	}
	
	
	
	public static byte[] getImage(String folderPath,String imageName,String extension) throws IOException
	{
			//Prepare buffered image.
		 	BufferedImage img = ImageIO.read(new File(folderPath+"/"+imageName+"."+extension));
		 	
	        // Create a byte array output stream.
	        ByteArrayOutputStream bao = new ByteArrayOutputStream();

	        // Write to output stream
	        ImageIO.write(img,extension , bao);
	        bao.close();
	        return bao.toByteArray();
	}
	
		
	/**
	 * This method takes an input as IEkg,String array and throws exception if mandatory value is not present
	 * @param src
	 * @return
	 * @throws MissingMandatoryParameterException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static boolean validateMandatoryParameter(IDHI src, String[] attributeList) throws MissingMandatoryParameterException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		//LOGGER.debug("Entering validateMandatoryParameter method of class VUClipUtil");
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
			throw new MissingMandatoryParameterException(message, "Required Parameter missing", "InvalidRequest");
		}
		//LOGGER.debug("Leaving validateMandatoryParameter method of class VUClipUtil with response : " + true);
		return true;
	}
	
	
	
	public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
	
	
	/**
	 * This method will try to match the source string to the provided regex, if match found then returns true or else returns false.
	 * @param sourceString
	 * @param regex
	 * @return
	 */
	public boolean validateUsingRegex(String sourceString, String regex) {
		return sourceString.matches(regex);
	}
	
	public static List<DiscoveryModelColumns> readCSVFileForDiscoveryTab(String csvFile) throws InvalidDataException {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<DiscoveryModelColumns> discoveryModelColumnList = new ArrayList<DiscoveryModelColumns>();
        int counter = 0;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	counter++;
            	if(counter <= 1 ) {
            		continue;
            	}

                // use comma as separator
                String[] alias = line.split(cvsSplitBy);
                DiscoveryModelColumns discoveryModelColumns = new DiscoveryModelColumns();
                try {
                	if(alias[0] == null) {
                		discoveryModelColumns.setColumnName("");
                	}else {
                		discoveryModelColumns.setColumnName(alias[0]);
                	}
                	
                }catch(ArrayIndexOutOfBoundsException e) {
                	discoveryModelColumns.setColumnName("");
                }
                
                try {
                	if(alias[1] == null) {
                		discoveryModelColumns.setDataType("");
                	}else {
                		discoveryModelColumns.setDataType(alias[1]);
                	}
                	
                }catch(ArrayIndexOutOfBoundsException e) {
                	discoveryModelColumns.setDataType("");
                }
                
                try {
                	if(alias[2] == null) {
                		discoveryModelColumns.setDescription("");
                	}else {
                		discoveryModelColumns.setDescription(alias[2]);
                	}
                	
                }catch(ArrayIndexOutOfBoundsException e) {
                	discoveryModelColumns.setDescription("");
                }
                
                try {
                	if(alias[3] == null) {
                		discoveryModelColumns.setAliasName("");
                	}else {
                		discoveryModelColumns.setAliasName(alias[3]);
                	}
                	
                }catch(ArrayIndexOutOfBoundsException e) {
                	discoveryModelColumns.setAliasName("");
                }
                discoveryModelColumnList.add(discoveryModelColumns);
            }

        } catch (FileNotFoundException e) {
        	throw new InvalidDataException("Error : Seems like \"Alias\" file not available for the requested user.","error","dhi_file_not_found_error"); 
        } catch (IOException e) {
        	throw new InvalidDataException("Error: Unable to open or read the file for discovery", "IO Exception", "serverError");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                	throw new InvalidDataException("Error: Unable to open or read the file for discovery", "IO Exception", "serverError");
                }
            }
        }
        return discoveryModelColumnList;
    }
}
