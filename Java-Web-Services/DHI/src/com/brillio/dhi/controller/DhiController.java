package com.brillio.dhi.controller;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.exception.InvalidDataException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.ChatMessageRequest;
import com.brillio.dhi.model.DiscoveryModel;
import com.brillio.dhi.model.FavouriteRequestModel;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.QueryResponse;
import com.brillio.dhi.service.DhiService;
import com.brillio.dhi.service.UserFavouriteService;

@Controller
public class DhiController {
	
	@Autowired
	DhiService dhiService;
	
	@Autowired
	UserFavouriteService userFavouriteService;
	
	
	private static final Logger LOGGER = Logger.getLogger(DhiController.class);
	
    @RequestMapping(value="/query/{room-id}", method = RequestMethod.POST)    
    public ResponseEntity <GenericResponse> chatRoom(@PathVariable("room-id") String id, @RequestBody ChatMessageRequest chatMessageRequest){    
    	GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse = dhiService.replyMessage(id,null,chatMessageRequest);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (InvalidDataException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);    
    }
    
    @RequestMapping(value="/v2/query/{room-id}", method = RequestMethod.POST)    
    public ResponseEntity <GenericResponse> chatRoomV2(@PathVariable("room-id") String id, @RequestBody ChatMessageRequest chatMessageRequest){    
    	GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse = dhiService.replyMessageV2(id,null,chatMessageRequest);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (InvalidDataException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);    
    }
    
    
    @RequestMapping(value="/query/by-phone/{phone-number}", method = RequestMethod.POST)    
    public ResponseEntity <GenericResponse> chatRoomByPhoneNumber(@PathVariable("phone-number") String phoneNumber, @RequestBody ChatMessageRequest chatMessageRequest){    
    	GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse = dhiService.replyMessage(null,phoneNumber,chatMessageRequest);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (InvalidDataException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);    
    }
    
    
    /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/upload/csv",method = RequestMethod.POST)
	public ResponseEntity <GenericResponse> uploadConnectFile(@RequestHeader(value="user-name") String userName, @RequestParam("file") MultipartFile file)  {
		LOGGER.debug("Entering uploadConnectFile method of class DhiController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= dhiService.saveMultipartFile(file,userName);
		}catch(MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (ServerException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.debug("Leaving uploadConnectFile method of class DhiController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    
    @RequestMapping(value="/api/discovery",method = RequestMethod.GET)
	public ResponseEntity <GenericResponse> getInfoForDiscovery(@RequestHeader(value="user-name") String userName, @RequestParam(value="data-file-name") String dataFileName)  {
		LOGGER.debug("Entering getInfoForDiscovery method of class ViuLauncherControlller");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= dhiService.getDiscoveryInfo(userName,dataFileName);
		} catch (InvalidDataException e) {
			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}catch (MissingMandatoryParameterException e) {
			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} 
		//LOGGER.debug("Leaving uploadAppSourceExcel method of class ViuLauncherControlller");*/
		//return ResponseEntity.ok("Success");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    
    
    @RequestMapping(value="/api/discovery",method = RequestMethod.POST)
   	public ResponseEntity <GenericResponse> getInfoForDiscovery(@RequestHeader(value="user-name") String userName, @RequestBody DiscoveryModel discoveryModel)  {
   		LOGGER.debug("Entering postInfoForDiscovery method of class ViuLauncherControlller");
   		GenericResponse genericResponse = new GenericResponse();
   		try {
   			genericResponse= dhiService.updateAliasFileForDiscovery(userName,discoveryModel);
   		} catch (MissingMandatoryParameterException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
   		} catch (NoRecordFoundException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (InvalidDataException e) {
			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
   		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
   		
   	}
    
    
    
    @RequestMapping(value="/api/favourite",method = RequestMethod.POST)
   	public ResponseEntity <GenericResponse> addToFavourite(@RequestHeader(value="user-name") String userName, @RequestBody FavouriteRequestModel favouriteRequestModel)  {
   		LOGGER.debug("Entering addToFavourite method of class DhiLauncherControlller");
   		GenericResponse genericResponse = new GenericResponse();
   		try {
   			genericResponse= userFavouriteService.saveUserFavourite(userName, favouriteRequestModel.getFullUrl(), favouriteRequestModel.getTitle());
   		} catch (MissingMandatoryParameterException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
   		}
   		LOGGER.debug("Leaving addToFavourite method of class DhiControlller");
   		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
   		
   	}
    
    
    /**
     * This method will get the details for the added favourite stuffs by user.
     * @param userName
     * @return
     */
    @RequestMapping(value="/api/favourite",method = RequestMethod.GET)
   	public ResponseEntity <GenericResponse> getFavourite(@RequestHeader(value="user-name") String userName)  {
   		LOGGER.debug("Entering getFavourite method of class DhiControlller");
   		GenericResponse genericResponse = new GenericResponse();
   		try {
   			genericResponse= userFavouriteService.getUserFavouriteByName(userName);
   		} catch (NoRecordFoundException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
   		}
   		LOGGER.debug("Leaving getFavourite method of class DhiControlller");
   		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
   		
   	}
    
    
    @RequestMapping(value="/api/favourite",method = RequestMethod.DELETE)
   	public ResponseEntity <GenericResponse> deleteFromFavourite(@RequestHeader(value="user-name") String userName, @RequestBody FavouriteRequestModel favouriteRequestModel)  {
   		LOGGER.debug("Entering deleteFromFavourite method of class DhiLauncherControlller");
   		GenericResponse genericResponse = new GenericResponse();
   		try {
   			genericResponse= userFavouriteService.deleteUserFavourite(userName, favouriteRequestModel);
   		} catch (MissingMandatoryParameterException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
   		}catch (NoRecordFoundException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
   		}
   		LOGGER.debug("Leaving deleteFromFavourite method of class DhiControlller");
   		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
   		
   	}
    
    
    @RequestMapping(value="/api/auto-suggestions",method = RequestMethod.GET)
   	public ResponseEntity <GenericResponse> getInfoForDiscovery(@RequestHeader(value="user-name") String userName)  {
   		LOGGER.debug("Entering postInfoForDiscovery method of class ViuLauncherControlller");
   		GenericResponse genericResponse = new GenericResponse();
   		try {
   			genericResponse= dhiService.getAutoSuggestionByUserName(userName);
   		} catch (MissingMandatoryParameterException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
   		}
   		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
   		
   	}
    
    @RequestMapping(value="/api/auto-suggestions",method = RequestMethod.PUT)
   	public ResponseEntity <GenericResponse> updateInfoForDiscovery(@RequestHeader(value="user-name") String userName)  {
   		LOGGER.debug("Entering postInfoForDiscovery method of class ViuLauncherControlller");
   		GenericResponse genericResponse = new GenericResponse();
   		try {
   			genericResponse= dhiService.updateAutoSuggestionForColumns(userName);
   		} catch (MissingMandatoryParameterException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
   		} catch (InvalidDataException e) {
   			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			genericResponse.setDescription(e.getMessage());
   			genericResponse.setStatus(e.getStatus());
   			genericResponse.setStatusCode(e.getStatusCode());
   			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
   		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
   		
   	}
    
    
    @RequestMapping(value="/api/data-formatting/query-data", method = RequestMethod.POST)    
    public ResponseEntity <List<JSONObject>> formatDataForDownload(@RequestBody QueryResponse queryResponse){    
  
    	List<JSONObject> list = dhiService.dataFormattingForDownload(queryResponse);
		return new ResponseEntity<>(list, HttpStatus.OK);    
    }
    
    
    @RequestMapping(value="/assets/{imageName}",method = RequestMethod.GET, produces = "image/jpg")
	public @ResponseBody byte[] getAppImage(@PathVariable String imageName)
	{
		 try {	
			 	//#LOGGER.debug("Entering getAppImage method of class ViuLauncherControlller");
			 	byte[] imageInByteFormat;
			 	imageInByteFormat=dhiService.getContentImage(imageName);
			 	//#LOGGER.debug("Leaving getAppImage method of class ViuLauncherControlller");
			 	return imageInByteFormat;
		    } catch (IOException e) {
		 		
		    }
		return null;
	}
    
  /*  
    @RequestMapping(value="/assets/fonts",method = RequestMethod.GET)
 	public @ResponseBody List<Map<String, String>> getAvailableFonts()
 	{
    	FileReader fr = null;
		try {
			fr = new FileReader(PropertiesConfigurationReader.getDatabaseProperty("font_file_path"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	BufferedReader br = new BufferedReader(fr);
    	String st = null;
    	List<Map<String,String>> fontListWithFamiltyAndCategory = new ArrayList<Map<String,String>>();
    	
    	
    	try {
    		Map<String,String> familyAndCategoryMap = null;
			while((st = br.readLine()) != null) {
				
				if(st.contains("\"family\":") ) {
					familyAndCategoryMap = new HashMap<String,String>();
					String val = st.replace("\"family\":", "");
					val = val.replace("\"", "");
					val = val.replace(",", "");
					val = val.trim();
					
					familyAndCategoryMap.put("family", val);
					//System.out.println(st);
				}else if(st.contains("\"category\":")) {
					String val = st.replace("\"category\":", "");
					val = val.replace("\"", "");
					val = val.replace(",", "");
					val = val.trim();
					if(familyAndCategoryMap != null) {
						familyAndCategoryMap.put("category", val);
					}
					
				}
				if(familyAndCategoryMap != null && familyAndCategoryMap.size() == 2) {
					fontListWithFamiltyAndCategory.add(familyAndCategoryMap);
					familyAndCategoryMap = null;
				}
			}
			br.close();
	    	fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return fontListWithFamiltyAndCategory;
 	}*/
    
    
   /* public static void main(String[] args) throws IOException {
		
    	
    	FileReader fr = new FileReader("D:\\font.txt");
    	
    	BufferedReader br = new BufferedReader(fr);
    	String st = null;
    	while((st = br.readLine()) != null) {
    		if(st.contains("\"family\":") ) {
    			System.out.println(st);
    		}else if(st.contains("\"category\":")) {
    			System.out.println(st);
    			System.out.println("=====\n\n");
    		}
    	}
    	br.close();
    	fr.close();
    	
    	
	}*/
}
