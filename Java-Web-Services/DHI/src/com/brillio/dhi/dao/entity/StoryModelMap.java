package com.brillio.dhi.dao.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * UserProfile generated by hbm2java
 */
@Entity
@Table(name = "STORY_MODEL_MAP")
public class StoryModelMap implements java.io.Serializable {

	private Long storyModelMapRowId;
	private FileUploadDataSource fileUploadDataSource;
	private UserProfileEntity userProfile;
	private String storyId;
	private String storyTitle;
	private Date createdDate;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getStoryModelMapRowId() {
		return storyModelMapRowId;
	}
	public void setStoryModelMapRowId(Long storyModelMapRowId) {
		this.storyModelMapRowId = storyModelMapRowId;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "FILE_UPLOAD_DATA_SOURCE")
	public FileUploadDataSource getFileUploadDataSource() {
		return fileUploadDataSource;
	}
	public void setFileUploadDataSource(FileUploadDataSource fileUploadDataSource) {
		this.fileUploadDataSource = fileUploadDataSource;
	}
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "USER_PROFILE")
	public UserProfileEntity getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfileEntity userProfile) {
		this.userProfile = userProfile;
	}
	
	@Column(name = "STORY_ID", nullable = false)
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	
	@Column(name = "STORY_TITLE", nullable = false)
	public String getStoryTitle() {
		return storyTitle;
	}
	public void setStoryTitle(String storyTitle) {
		this.storyTitle = storyTitle;
	}
	
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
}