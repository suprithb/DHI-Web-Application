package com.brillio.dhi.model.stories;

import java.util.List;

import com.brillio.dhi.model.GenericResponse;

public class Stories extends GenericResponse{
	
	private List<StoryModel> stories =  null;
	

	public List<StoryModel> getStories() {
		return stories;
	}

	public void setStories(List<StoryModel> stories) {
		this.stories = stories;
	}	
	

}
