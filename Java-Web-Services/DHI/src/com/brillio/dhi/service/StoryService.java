package com.brillio.dhi.service;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;

import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.stories.Stories;
import com.brillio.dhi.model.stories.StoryModel;

public interface StoryService {

	GenericResponse addStory(String userName, StoryModel storyRequest) throws MissingMandatoryParameterException, NoRecordFoundException;

	Stories getStories(String userName, String offset, String order) throws MissingMandatoryParameterException, NoRecordFoundException;

	GenericResponse removeStoryByName(String userName, String storyName)
			throws MissingMandatoryParameterException, ServerException;

	GenericResponse refreshStoryByStoryName(String userName, String storyName)
			throws MissingMandatoryParameterException, NoRecordFoundException;

	GenericResponse refreshStoryByStoryNameAndStoryItemId(String userName, String storyName, String storyItemId)
			throws MissingMandatoryParameterException, NoRecordFoundException;

	GenericResponse deleteStoryByStoryNameAndStoryItemId(String userName, String storyName, String storyItemId)
			throws MissingMandatoryParameterException, NoRecordFoundException;

	GenericResponse generatePPT(String userName, String storyName)
			throws IOException, MissingMandatoryParameterException, NoRecordFoundException, ServerException;

	ByteArrayResource getPPTContent(String userName, String pptName) throws IOException;

}
