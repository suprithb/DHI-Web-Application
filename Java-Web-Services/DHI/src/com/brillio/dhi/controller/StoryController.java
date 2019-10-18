package com.brillio.dhi.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.stories.StoryModel;
import com.brillio.dhi.service.StoryService;

@Controller
public class StoryController{
	
	private static final Logger LOGGER = Logger.getLogger(StoryController.class);
	
	@Autowired
	StoryService storyService;
	
	 /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/stories",method = RequestMethod.POST)
	public ResponseEntity <GenericResponse> addStory(@RequestHeader(value="user-name") String userName, @RequestBody StoryModel storyRequest)  {
		LOGGER.debug("Entering addStory method of class StoryController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= storyService.addStory(userName, storyRequest);
		}catch(MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
		LOGGER.debug("Leaving addStory method of class StoryController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/stories",method = RequestMethod.GET)
	public ResponseEntity <GenericResponse> addStory(@RequestHeader(value="user-name") String userName, @RequestParam(value="offset", defaultValue="-1") String offset, @RequestParam(value="order", defaultValue="dsc") String order)  {
		LOGGER.debug("Entering addStory method of class StoryController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= storyService.getStories(userName, offset, order);
		}catch(MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
		LOGGER.debug("Leaving addStory method of class StoryController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    
    /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/stories/{story-name}",method = RequestMethod.DELETE)
	public ResponseEntity <GenericResponse> deleteStory(@RequestHeader(value="user-name") String userName, @PathVariable("story-name") String storyName)  {
		LOGGER.debug("Entering addStory method of class StoryController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= storyService.removeStoryByName(userName, storyName);
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
		LOGGER.debug("Leaving addStory method of class StoryController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    
    /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/stories/{story-name}/{story-item-id}",method = RequestMethod.DELETE)
	public ResponseEntity <GenericResponse> deleteStoryItemFromStory(@RequestHeader(value="user-name") String userName, @PathVariable("story-name") String storyName, @PathVariable("story-item-id") String storyItemId)  {
		LOGGER.debug("Entering addStory method of class StoryController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= storyService.deleteStoryByStoryNameAndStoryItemId(userName, storyName,storyItemId);
		}catch(MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.debug("Leaving addStory method of class StoryController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    
    /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/stories/refresh/{story-name}",method = RequestMethod.GET)
	public ResponseEntity <GenericResponse> refreshStory(@RequestHeader(value="user-name") String userName, @PathVariable("story-name") String storyName)  {
		LOGGER.debug("Entering addStory method of class StoryController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= storyService.refreshStoryByStoryName(userName, storyName);
		}catch(MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}  catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}
		LOGGER.debug("Leaving addStory method of class StoryController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/stories/refresh/{story-name}/{story-item-id}",method = RequestMethod.GET)
	public ResponseEntity <GenericResponse> refreshStory(@RequestHeader(value="user-name") String userName, @PathVariable("story-name") String storyName,  @PathVariable("story-item-id") String storyItemId)  {
		LOGGER.debug("Entering addStory method of class StoryController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= storyService.refreshStoryByStoryNameAndStoryItemId(userName, storyName, storyItemId);
		}catch(MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.debug("Leaving addStory method of class StoryController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    
    /**
     * 
     * @param userName
     * @param file
     * @return
     */
    @RequestMapping(value="/api/stories/ppt/{story-name}",method = RequestMethod.GET)
	public ResponseEntity <GenericResponse> generatePPTByStoryName(@RequestHeader(value="user-name") String userName, @PathVariable("story-name") String storyName)  {
		LOGGER.debug("Entering generatePPTByStoryName method of class StoryController");
		GenericResponse genericResponse = new GenericResponse();
		try {
			genericResponse= storyService.generatePPT(userName, storyName);
		}catch(MissingMandatoryParameterException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		}  catch (NoRecordFoundException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			genericResponse.setStatus("error");
			genericResponse.setStatusCode("server_internal_error");
			genericResponse.setDescription("Error occurred while geneating Powerpoint Template.");
			return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (ServerException e) {
			genericResponse.setStatus(e.getStatus());
			genericResponse.setStatusCode(e.getStatusCode());
			genericResponse.setDescription(e.getMessage());
			return new ResponseEntity<>(genericResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.debug("Leaving generatePPTByStoryName method of class StoryController");
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
		
	}
    
    @RequestMapping(value="/assets/ppt/{file-name}",method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@RequestHeader(value="user-name") String userName,@PathVariable("file-name") String pptName) throws IOException {
    	//@RequestHeader(value="user-name") String userName,
    	//String userName = "manmaya.champatiray@brillio.com";
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+pptName+".pptx");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        ByteArrayResource resource = storyService.getPPTContent(userName, pptName);
        return new ResponseEntity<>(resource,header,HttpStatus.OK);
    
    }

}
