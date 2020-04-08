package com.brillio.dhi.dao;

import java.util.List;

import com.brillio.dhi.dao.entity.StoryModelMap;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface StoryModelMapDao {

	boolean saveStoryModelMap(StoryModelMap storyModelMap);

	boolean updateStoryModelMap(StoryModelMap storyModelMap);

	List<StoryModelMap> getAllStoryUserName(String userName) throws NoRecordFoundException;

	StoryModelMap getStoryUserNameAndStoryName(String userName, String storyTitle) throws NoRecordFoundException;

}
