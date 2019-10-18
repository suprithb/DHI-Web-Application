package com.brillio.dhi.service.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import org.apache.commons.text.WordUtils;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.dao.FileUploadDataSourceDao;
import com.brillio.dhi.dao.StoryModelMapDao;
import com.brillio.dhi.dao.entity.FileUploadDataSource;
import com.brillio.dhi.dao.entity.StoryModelMap;
import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.ChatMessageRequest;
import com.brillio.dhi.model.ExportPPT;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.stories.ColumnValue;
import com.brillio.dhi.model.stories.Row;
import com.brillio.dhi.model.stories.Stories;
import com.brillio.dhi.model.stories.StoryItem;
import com.brillio.dhi.model.stories.StoryModel;
import com.brillio.dhi.model.stories.TabularData;
import com.brillio.dhi.service.DhiService;
import com.brillio.dhi.service.StoryService;
import com.brillio.dhi.util.WebServiceUtility;
import com.google.gson.Gson;

@Service
public class StoryServiceImpl implements StoryService {

	@Autowired
	DhiService dhiService;
	
	@Autowired
	FileUploadDataSourceDao fileUploadDataSourceDao;
	
	@Autowired
	StoryModelMapDao storyModelDao;

	@Override
	public GenericResponse addStory(String userName, StoryModel storyRequest)
			throws MissingMandatoryParameterException, NoRecordFoundException {

		// Check for mandatory parameters
		if (storyRequest == null || storyRequest.getStoryTitle() == null
				|| storyRequest.getStoryTitle().trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : storyName", "error",
					"dhi_missing_required_parameter");
		}

		if (userName == null || userName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name", "error",
					"dhi_missing_required_parameter");
		}

		String storyName = storyRequest.getStoryTitle().trim();
		storyRequest.setStoryId(System.currentTimeMillis()+"");

		// create a file with the timestamp
		if (storyRequest.getTimestamp() == null) {
			storyRequest.setTimestamp(System.currentTimeMillis());
			storyRequest.setLastUpdatedTimestamp(storyRequest.getTimestamp());
		}
		

		Gson gson = new Gson();
		String storyJson = gson.toJson(storyRequest);

		// go to the specific directory for the user
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/" + "stories/";
		String fileName = storyName + ".json";

		writeUsingOutputStream(directory, fileName, storyJson);

		// write the json content into the file

		// initialize the story entity with the title, fileName etc

		// storyRepository.save(storyEntity);
		StoryModelMap storyModelMap = new StoryModelMap();
		FileUploadDataSource fileUploadDataSource = fileUploadDataSourceDao.getDefaultFileDataSourceByUserName(userName);
		UserProfileEntity userProfileEntity = new UserProfileEntity();
		userProfileEntity.setUsername(userName);
		try {
			StoryModelMap storyModelMapEntity = storyModelDao.getStoryUserNameAndStoryName(userName, storyName);
			
			//Call to update 
			storyModelMapEntity.setStoryId(storyRequest.getStoryId());
			storyModelMapEntity.setFileUploadDataSource(fileUploadDataSource);
			storyModelMapEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			storyModelDao.updateStoryModelMap(storyModelMapEntity);
		} catch (NoRecordFoundException e) {
			storyModelMap.setStoryId(storyRequest.getStoryId());
			storyModelMap.setFileUploadDataSource(fileUploadDataSource);
			storyModelMap.setStoryTitle(storyRequest.getStoryTitle());
			storyModelMap.setUserProfile(userProfileEntity);
			storyModelMap.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			storyModelDao.saveStoryModelMap(storyModelMap);
			
		}

		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("Success");
		genericResponse.setDescription("Story saved successfully.");
		return genericResponse;

	}

	@Override
	public Stories getStories(String userName, String offset, String order)
			throws MissingMandatoryParameterException, NoRecordFoundException {
		// Check for mandatory parameters
		if (userName == null || userName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name", "error",
					"dhi_missing_required_parameter");
		}

		// go to the specific directory for the user
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/" + "stories/";

		File directoryWithPath = new File(directory);
		if (!directoryWithPath.exists()) {
			throw new NoRecordFoundException("No story found for the provided user-name : " + userName, "error",
					"dhi_story_not_found_error");
		}

		// get the file list present in the folders
		List<String> filesPresentInThePath = getAllFileNamesFromDirectory(directory);
		Gson gson = new Gson();
		List<StoryModel> storyModelList = new ArrayList<StoryModel>();

		for (String fileName : filesPresentInThePath) {
			String jsonString = readingAFile(directory, fileName);
			StoryModel storyModel = new StoryModel();
			storyModel = gson.fromJson(jsonString, StoryModel.class);
			if(storyModel.getLastUpdatedTimestamp() == null) {
				storyModel.setLastUpdatedTimestamp(storyModel.getTimestamp());
			}
			storyModelList.add(storyModel);
		}
		if (order.equalsIgnoreCase("asc")) {
			Collections.sort(storyModelList, (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
		} else {
			Collections.sort(storyModelList, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
		}
		int offsetValue = -1;
		try {
			offsetValue = Integer.parseInt(offset);
		} catch (Exception e) {

		}

		if (storyModelList.size() > offsetValue && offsetValue != -1) {
			storyModelList = storyModelList.subList(0, offsetValue);
		}

		Stories stories = new Stories();
		stories.setStories(storyModelList);
		return stories;

	}

	@Override
	public GenericResponse removeStoryByName(String userName, String storyName)
			throws MissingMandatoryParameterException, ServerException {

		// Check for mandatory parameters
		if (storyName == null || storyName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : storyName", "error",
					"dhi_missing_required_parameter");
		}

		if (userName == null || userName.trim() == null) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name", "error",
					"dhi_missing_required_parameter");
		}

		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/" + "stories/";

		storyName = storyName.trim() + ".json";

		File storyFile = new File(directory + storyName);

		boolean isStoryDeleted = false;
		if (!storyFile.exists()) {
			throw new MissingMandatoryParameterException("No story found for the provided story name.", "error",
					"dhi_story_not_found_error");
		} else {
			isStoryDeleted = storyFile.delete();

		}
		GenericResponse genericResponse = new GenericResponse();
		if (isStoryDeleted) {
			genericResponse.setStatus("Success");
			genericResponse.setDescription("Successfully deleted the story with story-name : " + storyName);
		} else {
			throw new ServerException("Errro occured while deleting the story.", "error", "dhi_server_internal_error");
		}
		return genericResponse;

	}

	@Override
	public GenericResponse deleteStoryByStoryNameAndStoryItemId(String userName, String storyName, String storyItemId)
			throws MissingMandatoryParameterException, NoRecordFoundException {

		// Check for mandatory parameters
		if (storyName == null || storyName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : storyName", "error",
					"dhi_missing_required_parameter");
		}

		if (userName == null || userName.trim() == null) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name", "error",
					"dhi_missing_required_parameter");
		}

		if (storyItemId == null || storyItemId.trim() == null) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : storyItemId", "error",
					"dhi_missing_required_parameter");
		}

		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/" + "stories/";

		storyName = storyName.trim() + ".json";

		File storyFile = new File(directory + storyName);

		if (!storyFile.exists()) {
			throw new NoRecordFoundException("No story found for the provided story name.", "error",
					"dhi_story_not_found_error");
		}

		// Get the story and list of story items
		String storyJson = readingAFile(directory, storyName);

		Gson gson = new Gson();
		StoryModel storyModel = gson.fromJson(storyJson, StoryModel.class);

		// Iterate over story items and collect the queries and also id of it.
		List<StoryItem> storyItems = storyModel.getStoryItem();
		ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
		chatMessageRequest.setUserName(userName);

		List<StoryItem> updatedStoryItems = new ArrayList<StoryItem>();

		for (StoryItem storyItem : storyItems) {

			if (!(storyItem.getStoryItemId().equals(storyItemId))) {
				updatedStoryItems.add(storyItem);
			}
		}
		storyModel.setStoryItem(updatedStoryItems);
		GenericResponse genericResponse = addStory(userName, storyModel);

		Stories stories = getStories(userName, "null", "null");
		return stories;

	}

	@Override
	public GenericResponse refreshStoryByStoryName(String userName, String storyName)
			throws MissingMandatoryParameterException, NoRecordFoundException {

		// Check for mandatory parameters
		if (storyName == null || storyName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : storyName", "error",
					"dhi_missing_required_parameter");
		}

		if (userName == null || userName.trim() == null) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name", "error",
					"dhi_missing_required_parameter");
		}

		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/" + "stories/";

		String storyNameFileName = storyName.trim() + ".json";

		File storyFile = new File(directory + storyNameFileName);

		if (!storyFile.exists()) {
			throw new NoRecordFoundException("No story found for the provided story name.", "error",
					"dhi_story_not_found_error");
		}

		// Get the story and list of story items
		String storyJson = readingAFile(directory, storyNameFileName);

		Gson gson = new Gson();
		StoryModel storyModel = gson.fromJson(storyJson, StoryModel.class);

		// Iterate over story items and collect the queries and also id of it.
		List<StoryItem> storyItems = storyModel.getStoryItem();
		ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
		chatMessageRequest.setUserName(userName);
		
		
		
		StoryModelMap storyModelMapEntity = storyModelDao.getStoryUserNameAndStoryName(userName, storyName);
		String dataFileName = storyModelMapEntity.getFileUploadDataSource().getDataModelFileName();
		String aliasFileName = storyModelMapEntity.getFileUploadDataSource().getAliasFileName();
		chatMessageRequest.setDataFileName(dataFileName);
		chatMessageRequest.setAliasFileName(aliasFileName);
		
		List<StoryItem> updatedStoryItems = new ArrayList<StoryItem>();
		for (StoryItem storyItem : storyItems) {
			String userQuery = storyItem.getUserQueryMessage();
			chatMessageRequest.setClientTextMessage(userQuery);
			StoryItem item = replyMessage(chatMessageRequest);
			item.setUserQueryMessage(userQuery);
			item.setStoryItemId(storyItem.getStoryItemId());
			item.setLastUpdatedTimestamp(System.currentTimeMillis());
			updatedStoryItems.add(item);
		}
		storyModel.setStoryItem(updatedStoryItems);
		storyModel.setLastUpdatedTimestamp(System.currentTimeMillis());
		GenericResponse genericResponse = addStory(userName, storyModel);

		Stories stories = getStories(userName, "null", "null");
		return stories;

	}

	@Override
	public GenericResponse refreshStoryByStoryNameAndStoryItemId(String userName, String storyName, String storyItemId)
			throws MissingMandatoryParameterException, NoRecordFoundException {

		// Check for mandatory parameters
		if (storyName == null || storyName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : storyName", "error",
					"dhi_missing_required_parameter");
		}

		if (userName == null || userName.trim() == null) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name", "error",
					"dhi_missing_required_parameter");
		}

		if (storyItemId == null || storyItemId.trim() == null) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : storyItemId", "error",
					"dhi_missing_required_parameter");
		}

		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/" + "stories/";

		storyName = storyName.trim() + ".json";

		File storyFile = new File(directory + storyName);

		if (!storyFile.exists()) {
			throw new NoRecordFoundException("No story found for the provided story name.", "error",
					"dhi_story_not_found_error");
		}

		// Get the story and list of story items
		String storyJson = readingAFile(directory, storyName);

		Gson gson = new Gson();
		StoryModel storyModel = gson.fromJson(storyJson, StoryModel.class);

		// Iterate over story items and collect the queries and also id of it.
		List<StoryItem> storyItems = storyModel.getStoryItem();
		ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
		chatMessageRequest.setUserName(userName);

		List<StoryItem> updatedStoryItems = new ArrayList<StoryItem>();

		for (StoryItem storyItem : storyItems) {

			if (storyItem.getStoryItemId().equals(storyItemId)) {
				String userQuery = storyItem.getUserQueryMessage();
				chatMessageRequest.setClientTextMessage(userQuery);
				StoryItem item = replyMessage(chatMessageRequest);
				item.setUserQueryMessage(userQuery);
				item.setStoryItemId(storyItemId);
				item.setLastUpdatedTimestamp(System.currentTimeMillis());
				updatedStoryItems.add(item);
			} else {
				updatedStoryItems.add(storyItem);
			}
		}
		storyModel.setStoryItem(updatedStoryItems);
		GenericResponse genericResponse = addStory(userName, storyModel);

		Stories stories = getStories(userName, "null", "null");
		return stories;

	}

	/**
	 * Use Streams when you are dealing with raw data
	 * 
	 * @param data
	 */
	private static void writeUsingOutputStream(String filePath, String fileName, String data) {
		OutputStream os = null;
		try {
			File file = new File(filePath);

			try {
				file.mkdirs();
				boolean isFileCreated = new File(filePath + fileName).createNewFile();
				// System.out.println(isFileCreated);
			} catch (Exception e) {
				// throw new ServerException("Server Internal Error : Unable to create directory
				// with the path","error","dhi_internal_server_error");
				e.printStackTrace();
			}
			os = new FileOutputStream(new File(filePath + fileName));
			os.write(data.getBytes(), 0, data.length());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String readingAFile(String filePath, String fileName) {
		File file = new File(filePath + fileName);

		BufferedReader br = null;
		String jsonString = "";
		try {
			br = new BufferedReader(new FileReader(file));

			String st = "";

			while ((st = br.readLine()) != null) {
				jsonString += st;
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		System.out.println(jsonString);
		return jsonString;
	}

	/**
	 * Method to list all the files present in the directory
	 * 
	 * @param directoryName
	 * @return
	 */
	public static List<String> getAllFileNamesFromDirectory(String directoryName) {

		List<String> fileNameList = new ArrayList<String>();

		File folder = new File(directoryName);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileNameList.add(listOfFiles[i].getName());
			}
		}
		return fileNameList;
	}

	public StoryItem replyMessage(ChatMessageRequest chatMessageRequest)
			throws NoRecordFoundException, MissingMandatoryParameterException {
		String replyMessage = null;
		String userName = "";
		if (chatMessageRequest.getUserName() != null) {
			userName = chatMessageRequest.getUserName().trim();
		}

		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		String directory = uploadingDir + "/" + userName + "/";
		
		String dataModelFilePath = directory + chatMessageRequest.getDataFileName();
		String aliasFilePath = directory + chatMessageRequest.getAliasFileName();

		chatMessageRequest.setDataFileName(dataModelFilePath);
		chatMessageRequest.setAliasFileName(aliasFilePath);
		chatMessageRequest.setUserName(userName);

		StoryItem storyItem = null;
		if (replyMessage == null) {
			storyItem = invokeAI(chatMessageRequest);
			if (storyItem.getImageUrl() != null)
				storyItem.setServerImageMessage(storyItem.getImageUrl());

			if (storyItem.getImageUrl() != null && !("".equals(storyItem.getImageUrl()))) {
				String imageName = formatImageUrl(storyItem.getImageUrl());
				String imageUrl = PropertiesConfigurationReader.getServerProperty("visualization_graph_url");
				storyItem.setServerImageMessage(imageUrl += imageName);
			}else {
				storyItem.setServerImageMessage("");
			}
		}

		return storyItem;
	}

	public StoryItem invokeAI(ChatMessageRequest chatMessageRequest) {
		String message = chatMessageRequest.getClientTextMessage();
		chatMessageRequest.setClientTextMessage(message);
		Gson gson = new Gson();
		String jsonString = gson.toJson(chatMessageRequest);

		String webServiceUrl = PropertiesConfigurationReader.getWebServiceProperty("python_ai_chat_model_url");

		// TODO
		// webServiceUrl = "http://104.211.210.210/api/query/intelligence1";
		String responseString = WebServiceUtility.getPostResponseFromWebService(webServiceUrl, jsonString);
		StoryItem storyItem = gson.fromJson(responseString, StoryItem.class);

		return storyItem;
	}

	public String formatImageUrl(String url) {

		String[] value = url.split("\\\\");
		// System.out.println(value[1]);
		String[] value1 = value[1].split("\\.");
		// System.out.println(value1[0]);
		return value1[0];
	}

	@Override
	public GenericResponse generatePPT(String userName, String storyName) throws IOException, MissingMandatoryParameterException, NoRecordFoundException, ServerException {
		
		Stories stories = getStories(userName,"-1", "dsc");
		List<StoryModel> storyModelList =  stories.getStories();
		
		if(stories == null || storyModelList == null || storyModelList.size() <= 0) {
			throw new NoRecordFoundException("No story found for the provided story name.", "error","dhi_story_not_found_error");
		}
		StoryModel storyModel = null;
		for(StoryModel storyModelEntity : storyModelList) {
			if(storyModelEntity.getStoryTitle().equalsIgnoreCase(storyName)) {
				storyModel = storyModelEntity;
				break;
			}
		}
		if(storyModel == null) {
			throw new NoRecordFoundException("No story found for the provided story name.", "error","dhi_story_not_found_error");
		}
		//StoryModel storyModel = storyModelList.get(0);
		List<StoryItem> storyItemList = storyModel.getStoryItem();
		
		if(storyItemList == null || storyItemList.size() <= 0) {
			throw new NoRecordFoundException("No story items found for the provided story name.", "error","dhi_story_not_found_error");
		}
		
		
		// we are new PPTX Presentation
		XMLSlideShow ppt = new XMLSlideShow();
		// we add a new slide
		XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
		// retrieving a the slide layout 
		XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
		int counter = 0;
		for(StoryItem storyItem : storyItemList) {
			if(storyItem.getServerImageMessage() != null && !(storyItem.getServerImageMessage().trim().equalsIgnoreCase(""))) {
				
				// we create the first slide
				XSLFSlide slide = ppt.createSlide(layout);
				slide.getBackground().setFillColor(Color.decode("#CCFFE5"));
				XSLFTextShape title = slide.getPlaceholder(0);
				// we remove the predefined text
				title.clearText();

				// we create a new paragraph
				XSLFTextParagraph p = title.addNewTextParagraph();
				XSLFTextRun r = p.addNewTextRun();
				if(storyItem.getTitle() != null) {
					r.setText(WordUtils.capitalizeFully(storyItem.getTitle()));
					r.setFontColor(Color.BLACK);
					r.setFontSize(45.);
				}
				
				
				URL url = new URL(storyItem.getServerImageMessage().trim());
				InputStream is = url.openStream();

				// We add an image from resources in the PPTX
				/*
				 * InputStream is = Thread.currentThread().getContextClassLoader()
				 * .getResourceAsStream("test_user_bar_chart_hz_2019_07_22_22_35_32.png");
				 */
				byte[] pd = IOUtils.toByteArray(is);

				XSLFPictureData pictureData = ppt.addPicture(pd, PictureData.PictureType.PNG);
				// we define the position for the data
				XSLFPictureShape pictureShape = slide.createPicture(pictureData);
				pictureShape.setAnchor(new Rectangle(5, 100, 350, 250));
				
				//Adding a new picture
				String showTabularInformationInPpt = PropertiesConfigurationReader.getServerProperty("show_tabular_info_ppt");
				if("true".equalsIgnoreCase("true")) {
					
					TabularData tabularData = storyItem.getTabularData();
					List<String> columnNames = tabularData.getColumNames();
					String[] columnNameArray = new String[columnNames.size()];
					int index = 0;
					for(String columnName : columnNames) {
						columnNameArray[index] = WordUtils.capitalizeFully(columnName);
						index++;
					}
					
					List<Row> rows = tabularData.getRow();
					String[][] data = new String[rows.size()][columnNames.size()];
					int rowCounter = 0;
					for(Row row : rows) {
						List<ColumnValue> columnValues = row.getColumnValues();
						int colCounter = 0;
						for(ColumnValue columnValue : columnValues) {
							data[rowCounter][colCounter] = columnValue.getColumnValue();
							colCounter++;
						}
						rowCounter++;
					}
					
					BufferedImage bufferedImage = generateImageForTabularData(data, columnNameArray);
					
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage, "png", os);
					InputStream iss = new ByteArrayInputStream(os.toByteArray());
					byte[] pdv = IOUtils.toByteArray(iss);
					XSLFPictureData pictureDataValue = ppt.addPicture(pdv, PictureData.PictureType.PNG);
					// we define the position for the data
					XSLFPictureShape pictureShapeValue = slide.createPicture(pictureDataValue);
					pictureShapeValue.setAnchor(new Rectangle(355, 100, 350, 250));
				}
				
				
				// we define some paragraphs for the slide
				XSLFTextShape content = slide.getPlaceholder(1);
				content.clearText();
				content.setAnchor(new Rectangle(30, 360, 580, 100));
				XSLFTextParagraph contentParagraph = content.addNewTextParagraph();
				XSLFTextRun contentTextRun = contentParagraph.addNewTextRun();
				contentTextRun.setText(storyItem.getStoryItemComments());
				//contentTextRun.setFontColor(Color.decode("#c62828"));
				contentTextRun.setFontColor(Color.BLACK);
				contentTextRun.setFontSize(20.);
				//content.addNewTextParagraph().contentTextRun.setText(storyItem.getStoryItemComments());
				counter++;
			}
		}
		if(counter == 0) {
			if(ppt != null) {
				ppt.close();
			}
			throw new ServerException("Error occurred while geneating Powerpoint Template.", "error", "error_creating_ppt");
		}
		// go to the specific directory for the user
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/" + "ppt/";
		File file = new File(directory);
		file.mkdirs();
		
		// now, we can save our presentation
		FileOutputStream out = new FileOutputStream(directory + storyModel.getStoryTitle().replace(" ", "_") + ".pptx");
		ppt.write(out);
		out.close();
		ppt.close();
		
		ExportPPT exportPPT = new ExportPPT();
		exportPPT.setStatus("success");
		exportPPT.setDescription("Successfully created the ppt with the name as : " +  storyModel.getStoryTitle().replace(" ", "_") + ".pptx");
		String pptDownloadUri = PropertiesConfigurationReader.getServerProperty("ppt_download_base_uri");
		pptDownloadUri += storyModel.getStoryTitle().replace(" ", "_");
		exportPPT.setPptDownloadUri(pptDownloadUri);
		
		return exportPPT;
		

	}
	
	@Override
	public ByteArrayResource getPPTContent(String userName, String pptName) throws IOException
	{
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		String directory = uploadingDir + "/" + userName + "/" + "ppt/";
		File file = new File(directory + pptName + ".pptx");
		Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
		return resource;
	}
	
	public BufferedImage generateImageForTabularData(Object[][] data, String[] columns){
		try {
			JTable table = new JTable(data, columns);
	        JScrollPane scroll = new JScrollPane(table);

	        JPanel p = new JPanel(new BorderLayout());
	        p.add(scroll,BorderLayout.CENTER);

	        // JTable must have been added to a TLC in order to render
	        // correctly - go figure.
	        JFrame f = new JFrame("Never shown");
	        f.setContentPane(scroll);
	        f.pack();

	        JTableHeader h = table.getTableHeader();
	        Dimension dH = h.getSize();
	        Dimension dT = table.getSize();
	        int x = (int)dH.getWidth();
	        int y = (int)dH.getHeight() + (int)dT.getHeight();

	        BufferedImage bi = new BufferedImage(
	            (int)x,
	            (int)y,
	            BufferedImage.TYPE_INT_RGB
	            );

	        Graphics g = bi.createGraphics();
	        h.paint(g);
	        g.translate(0,h.getHeight());
	        table.paint(g);
	        g.dispose();

	        return bi;
		}catch(Exception e) {
			return null;
		}	 
	}

}
