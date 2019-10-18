package com.brillio.dhi.service.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.dao.FileUploadDataSourceDao;
import com.brillio.dhi.dao.UserAuthDao;
import com.brillio.dhi.dao.UserDefinedColumnDao;
import com.brillio.dhi.dao.UserQueryDao;
import com.brillio.dhi.dao.UserTokenDao;
import com.brillio.dhi.dao.entity.FileUploadDataSource;
import com.brillio.dhi.dao.entity.UserAuth;
import com.brillio.dhi.dao.entity.UserDefinedColumns;
import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.dao.entity.UserQuery;
import com.brillio.dhi.dao.entity.UserToken;
import com.brillio.dhi.exception.InvalidDataException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.AIResponse;
import com.brillio.dhi.model.AutoSuggestionResponseModel;
import com.brillio.dhi.model.ChatMessageRequest;
import com.brillio.dhi.model.ColumnType;
import com.brillio.dhi.model.ColumnValue;
import com.brillio.dhi.model.DiscoveryModel;
import com.brillio.dhi.model.DiscoveryModelColumns;
import com.brillio.dhi.model.FileUploadResponse;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.MetaDataFileRequest;
import com.brillio.dhi.model.QueryResponse;
import com.brillio.dhi.model.Row;
import com.brillio.dhi.model.TabularData;
import com.brillio.dhi.model.UserTokenModel;
import com.brillio.dhi.service.DhiService;
import com.brillio.dhi.util.AuthHelper;
import com.brillio.dhi.util.DHIUtil;
import com.brillio.dhi.util.WebServiceUtility;
import com.google.gson.Gson;

@Service
public class DhiServiceImpl implements DhiService{
	
	
	
	private static final Logger LOGGER = Logger.getLogger(DhiServiceImpl.class);
	
	@Autowired
	AuthHelper authHelper;
	
	@Autowired
	UserTokenDao userTokenDao;
	
	@Autowired
	UserAuthDao userAuthDao;
	
	@Autowired
	FileUploadDataSourceDao fileUploadDataSourceDao;
	
	@Autowired
	UserQueryDao userQueryDao;
	
	@Autowired
	UserDefinedColumnDao userDefinedColumnDao;
	
/*	public static void main(String[] args) throws IOException {
		ApplicationContext context = new FileSystemXmlApplicationContext("D:\\Brillio_Stuffs\\Project_Development\\Proj_DHI\\DHI\\WebContent\\WEB-INF/dhi-servlet.xml");
		logInformation("manmaya.champatiray@brillio.com", "test-user-query", "test-ai-sql-query");
	}
	*/
	public static void logInformation(String userName, String userQuery, String aiSqlQuery) throws IOException {
		
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		
		String directory = uploadingDir + "/" + userName + "/" + "query-logs/";
		File directoryPath = new File(directory);
		directoryPath.mkdir();
		String fileName = java.time.LocalDate.now() + ".log";
		File logFile = new File(directory + fileName);
		FileWriter csvWriter = null;
		if(!logFile.exists()) {
			logFile.createNewFile();
			csvWriter = new FileWriter(logFile);
			csvWriter.append("User-Query");
			csvWriter.append(",");
			csvWriter.append("AI-Sql-Query");
			csvWriter.append(",");
			csvWriter.append("Date");
			csvWriter.append("\n");
		}
		if(csvWriter == null) {
			csvWriter = new FileWriter(logFile,true);
		}
		csvWriter.append(userQuery);
		csvWriter.append(",");
		csvWriter.append(aiSqlQuery);
		csvWriter.append(",");
		csvWriter.append(new Date() + "");
		csvWriter.append("\n");
		csvWriter.flush();
		csvWriter.close();
	}
	
	
	public String getUserNameFromPhoneNumber(String phoneNumber) throws NoRecordFoundException, MissingMandatoryParameterException{
		UserAuth userAuth = userAuthDao.getUserNameByPhoneNumber(phoneNumber);
		String userName = userAuth.getUsername();
		if(userName == null || userName.trim().equalsIgnoreCase("")) {
			throw new NoRecordFoundException("No record found in the database by the provided phone-number : " + phoneNumber,"error","dhi_token_not_found_error");
		}
		return userName;
	}
	
	public String getUserNameFromChatRoomId(String chatRoomId) throws NoRecordFoundException, MissingMandatoryParameterException {
		UserToken userToken = userTokenDao.getUserTokenByChatRoomId(chatRoomId);
		String userName = userToken.getUserName();
		if(userName == null || userName.trim().equalsIgnoreCase("")) {
			throw new NoRecordFoundException("No record found in the database by the provided chat-room-id : " + chatRoomId,"error","dhi_token_not_found_error");
		}
		return userName;
	}
	
	
	@Override
	public QueryResponse replyMessage(String chatRoomId, String phoneNumber, ChatMessageRequest chatMessageRequest) throws NoRecordFoundException, MissingMandatoryParameterException, InvalidDataException {
		String replyMessage = null;
		
		//UserToken userToken = userTokenDao.getUserTokenByChatRoomId(chatRoomId);
		//String userName = userToken.getUserName();
		String channel = "web";
		String userName = null;
		if(chatRoomId != null && !(chatRoomId.trim().equalsIgnoreCase(""))) {
			userName = getUserNameFromChatRoomId(chatRoomId);
		}else if(phoneNumber != null && !(phoneNumber.trim().equalsIgnoreCase(""))) {
			userName = getUserNameFromPhoneNumber(phoneNumber);
			channel = "phone";
		}
		
		
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		String directory = uploadingDir + "/" + userName + "/";
		
		FileUploadDataSource fileUploadDataSource = fileUploadDataSourceDao.getDefaultFileDataSourceByUserName(userName);
		
		String dataModelFilePath = directory + fileUploadDataSource.getDataModelFileName();
		String aliasFilePath = directory + fileUploadDataSource.getAliasFileName();
		
		
		File file = new File(aliasFilePath);
		
		if(!file.exists()) {
			throw new InvalidDataException("Error : Alias information not exist","error","data_not_found_exception");
		}
		
		file = new File(dataModelFilePath);
		if(!file.exists()) {
			throw new InvalidDataException("Error : Model Data does not exist","error","data_not_found_exception");
		}
		
		
		//Code for saving the query message in the database
		UserQuery userQuery = new UserQuery();
		userQuery.setChannel(channel);
		userQuery.setCount(1);
		userQuery.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		userQuery.setQuery(chatMessageRequest.getClientTextMessage());
		UserProfileEntity userProfileEntity = new UserProfileEntity();
		userProfileEntity.setUsername(userName);
		userQuery.setUserProfile(userProfileEntity);
		try {
			userQuery = userQueryDao.getUserQueryByUserNameAndQuery(userName, chatMessageRequest.getClientTextMessage());
			long count = userQuery.getCount();
			userQuery.setCount(++count);
			userQuery.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			userQueryDao.updateUserQuery(userQuery);
		}catch(NoRecordFoundException e) {
			userQueryDao.saveUserQuery(userQuery);
		}
		
		
		
		
		chatMessageRequest.setDataFileName(dataModelFilePath);
		chatMessageRequest.setAliasFileName(aliasFilePath);
		chatMessageRequest.setUserName(userName);
		
		AIResponse aiResponse = null;
		QueryResponse queryResponse = new QueryResponse();
		queryResponse.setUserQueryMessage(chatMessageRequest.getClientTextMessage());
		queryResponse.setStoryItemId(System.currentTimeMillis() + "");
		if(replyMessage == null) {
			aiResponse = invokeAI(chatMessageRequest);
			if(aiResponse.getImageUrl() != null)
				queryResponse.setServerTextMessage(aiResponse.getTextMessage());
			
			if(aiResponse.getImageUrl() != null && !("".equals(aiResponse.getImageUrl()))) {
				String imageName = formatImageUrl(aiResponse.getImageUrl());
				String imageUrl = PropertiesConfigurationReader.getServerProperty("visualization_graph_url");
				imageUrl += imageName;
				queryResponse.setServerImageMessage(imageUrl);
			}else {
				queryResponse.setServerImageMessage("");
			}
			
			if(aiResponse.getTabularData() != null){
				queryResponse.setTabularData(aiResponse.getTabularData());
				queryResponse.setTitle(aiResponse.getTitle());
			}
		}
		String aiSqlQuery = null;
		if(aiResponse != null)
			aiSqlQuery = aiResponse.getGeneratedSqlQuery();
			
		try {
			logInformation(userName, chatMessageRequest.getClientTextMessage(), aiSqlQuery);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			String clientMessage = chatMessageRequest.getClientTextMessage();
			if(clientMessage != null) {
				String autoSugesstionFile = "auto-suggestion-query.txt";
				String autoSugesstionDirectory = uploadingDir + "/" + userName + "/";
				if(new File(autoSugesstionDirectory + autoSugesstionFile).exists()) {
					clientMessage = "," + clientMessage; 
				}
				writeUsingOutputStream(autoSugesstionDirectory, autoSugesstionFile, clientMessage);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		String showImage = PropertiesConfigurationReader.getServerProperty("search_show_image");
		String showJsonData =  PropertiesConfigurationReader.getServerProperty("search_show_json_data");
		queryResponse.setStatusCode("200");
		queryResponse.setStatus("success");
		queryResponse.setSearchShowImage(showImage);
		queryResponse.setSearchShowJsonData(showJsonData);
		
		return queryResponse;
	}
	
	
	
	@Override
	public QueryResponse replyMessageV2(String chatRoomId, String phoneNumber, ChatMessageRequest chatMessageRequest) throws NoRecordFoundException, MissingMandatoryParameterException, InvalidDataException {
		String replyMessage = null;
		
		//UserToken userToken = userTokenDao.getUserTokenByChatRoomId(chatRoomId);
		//String userName = userToken.getUserName();
		String channel = "web";
		String userName = null;
		if(chatRoomId != null && !(chatRoomId.trim().equalsIgnoreCase(""))) {
			userName = getUserNameFromChatRoomId(chatRoomId);
		}else if(phoneNumber != null && !(phoneNumber.trim().equalsIgnoreCase(""))) {
			userName = getUserNameFromPhoneNumber(phoneNumber);
			channel = "phone";
		}
		
		
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		String directory = uploadingDir + "/" + userName + "/";
		
		FileUploadDataSource fileUploadDataSource = fileUploadDataSourceDao.getDefaultFileDataSourceByUserName(userName);
		
		String dataModelFilePath = directory + fileUploadDataSource.getDataModelFileName();
		String aliasFilePath = directory + fileUploadDataSource.getAliasFileName();
		
		
		File file = new File(aliasFilePath);
		if(!file.exists()) {
			throw new InvalidDataException("Error : Alias information not exist","error","data_not_found_exception");
		}
		
		file = new File(dataModelFilePath);
		if(!file.exists()) {
			throw new InvalidDataException("Error : Model Data does not exist","error","data_not_found_exception");
		}
		
		
		//Code for saving the query message in the database
		UserQuery userQuery = new UserQuery();
		userQuery.setChannel(channel);
		userQuery.setCount(1);
		userQuery.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		userQuery.setQuery(chatMessageRequest.getClientTextMessage());
		UserProfileEntity userProfileEntity = new UserProfileEntity();
		userProfileEntity.setUsername(userName);
		userQuery.setUserProfile(userProfileEntity);
		try {
			userQuery = userQueryDao.getUserQueryByUserNameAndQuery(userName, chatMessageRequest.getClientTextMessage());
			long count = userQuery.getCount();
			userQuery.setCount(++count);
			userQuery.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			userQueryDao.updateUserQuery(userQuery);
		}catch(NoRecordFoundException e) {
			userQueryDao.saveUserQuery(userQuery);
		}
		
		
		
		
		chatMessageRequest.setDataFileName(dataModelFilePath);
		chatMessageRequest.setAliasFileName(aliasFilePath);
		chatMessageRequest.setUserName(userName);
		
		AIResponse aiResponse = null;
		QueryResponse queryResponse = new QueryResponse();
		queryResponse.setUserQueryMessage(chatMessageRequest.getClientTextMessage());
		queryResponse.setStoryItemId(System.currentTimeMillis() + "");
		if(replyMessage == null) {
			aiResponse = invokeAI(chatMessageRequest);
			if(aiResponse.getImageUrl() != null)
				queryResponse.setServerTextMessage(aiResponse.getTextMessage());
			
			if(aiResponse.getImageUrl() != null && !("".equals(aiResponse.getImageUrl()))) {
				String imageName = formatImageUrl(aiResponse.getImageUrl());
				String imageUrl = PropertiesConfigurationReader.getServerProperty("visualization_graph_url");
				imageUrl += imageName;
				queryResponse.setServerImageMessage(imageUrl);
			}else {
				queryResponse.setServerImageMessage("");
			}
			
			if(aiResponse.getTabularData() != null){
				queryResponse.setTabularData(aiResponse.getTabularData());
				queryResponse.setTitle(aiResponse.getTitle());
				
				//Added the changes for specifying the data-types for the column names

				if(aiResponse.getTabularData().getColumNames() != null) {
					
					List<String> columnNames = aiResponse.getTabularData().getColumNames();
					
					DiscoveryModel discoveryModel = getDiscoveryInfo(userName, "");
					List<DiscoveryModelColumns> discoveryModelColums = discoveryModel.getDiscovery();
					
					List<ColumnType> columnWithTypes = new ArrayList<ColumnType>();
					
					for(String name : columnNames) {
						for(DiscoveryModelColumns dmc : discoveryModelColums) {
							if(name.equalsIgnoreCase(dmc.getColumnName()) || name.equalsIgnoreCase(dmc.getAliasName())) {
								ColumnType columnType = new ColumnType();
								columnType.setColumnName(name);
								columnType.setDataType(dmc.getDataType());
								columnWithTypes.add(columnType);
							}
						}
					}
					aiResponse.getTabularData().setColumnWithTypes(columnWithTypes);
					aiResponse.getTabularData().setGraphTypes(Arrays.asList("bar_chart_vr"));
				}
			}
		}
		String aiSqlQuery = null;
		if(aiResponse != null)
			aiSqlQuery = aiResponse.getGeneratedSqlQuery();
			
		try {
			logInformation(userName, chatMessageRequest.getClientTextMessage(), aiSqlQuery);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			String clientMessage = chatMessageRequest.getClientTextMessage();
			if(clientMessage != null) {
				String autoSugesstionFile = "auto-suggestion-query.txt";
				String autoSugesstionDirectory = uploadingDir + "/" + userName + "/";
				if(new File(autoSugesstionDirectory + autoSugesstionFile).exists()) {
					clientMessage = "," + clientMessage; 
				}
				writeUsingOutputStream(autoSugesstionDirectory, autoSugesstionFile, clientMessage);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		String showImage = PropertiesConfigurationReader.getServerProperty("search_show_image");
		String showJsonData =  PropertiesConfigurationReader.getServerProperty("search_show_json_data");
		queryResponse.setStatusCode("200");
		queryResponse.setStatus("success");
		queryResponse.setSearchShowImage(showImage);
		queryResponse.setSearchShowJsonData(showJsonData);
		
		return queryResponse;
	}
	
	
	
	public String formatImageUrl(String url) {
		
		String[] value  = url.split("\\\\");
		System.out.println(value[1]);
		String[] value1 = value[1].split("\\.");
		System.out.println(value1[0]);
		return value1[0];
	}
	
	
	
	public String isGreetingMessage(String message) {
		String possibleUserGreeting = PropertiesConfigurationReader.getServerProperty("greeting_messages_type_from_client");
		List<String> userGreentigList = Arrays.asList(possibleUserGreeting);
		message = message.trim();
		
		if("good morning".equalsIgnoreCase(message.toLowerCase())) {
			
		}else if("good evening".equalsIgnoreCase(message.toLowerCase())) {
			
		}
		if(userGreentigList.contains(message.toLowerCase())) {
			return "Hello";
		}
		return null;
	}
	
	
	public String isExitMessage(String message) {
		
		
		String[] possibleUserGreeting = {"bye","see you","exit"};
		List<String> userGreentingList = Arrays.asList(possibleUserGreeting);
		message = message.trim();
		
		if(userGreentingList.contains(message.toLowerCase())) {
			return "Bye";
		}
		return null;
	}
	
	public AIResponse invokeAI(ChatMessageRequest chatMessageRequest) {
		String message = chatMessageRequest.getClientTextMessage();
		chatMessageRequest.setClientTextMessage(message);
		Gson gson = new Gson();
		String jsonString = gson.toJson(chatMessageRequest);
		
		String webServiceUrl = PropertiesConfigurationReader.getWebServiceProperty("python_ai_chat_model_url");
		
		String responseString = WebServiceUtility.getPostResponseFromWebService(webServiceUrl, jsonString);
		System.out.println(responseString);
		AIResponse aiResponse= gson.fromJson(responseString, AIResponse.class);
		return aiResponse;
	}
	
	
	
	@Override
	public DiscoveryModel getDiscoveryInfo(String userName, String dataFileName) throws InvalidDataException, MissingMandatoryParameterException, NoRecordFoundException 
	{
		//LOGGER.debug("Entering getClipImage method of class LauncherMetadataServiceImpl");
		if(userName == null || userName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name","error","dhi_missing_required_parameter");
		}
		
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");

		userName = userName.trim();
		String directory = uploadingDir + "/" + userName + "/";
		
		FileUploadDataSource fileUploadDataSource = fileUploadDataSourceDao.getDefaultFileDataSourceByUserName(userName);
		
		String aliasFile = directory  + fileUploadDataSource.getAliasFileName();
	/*	
		File file = new File(aliasFile);
		
		if(!file.exists()) {
			aliasFile = directory + "alias.csv";
		}*/
		
		List<DiscoveryModelColumns> discoveryModelColumnsList = DHIUtil.readCSVFileForDiscoveryTab(aliasFile);
		DiscoveryModel discoveryModel= new DiscoveryModel();
		discoveryModel.setDiscovery(discoveryModelColumnsList);
		discoveryModel.setStatus("success");
		return discoveryModel;
	}
	
	
	
	@Override
	public GenericResponse updateAliasFileForDiscovery(String userName, DiscoveryModel discoveryModel) throws MissingMandatoryParameterException, NoRecordFoundException, InvalidDataException {
		//Always write to the temp file, the original file will be the file before modification made by the user.
		if(userName == null || userName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name","error","dhi_missing_required_parameter");
		}
		
		if(discoveryModel == null || discoveryModel.getDiscovery().size() <= 0) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : discovery-values","error","dhi_missing_required_parameter");
		}
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		
		FileUploadDataSource fileUploadDataSource = fileUploadDataSourceDao.getDefaultFileDataSourceByUserName(userName);
		
		
		DiscoveryModel previousDiscoveryModel =  getDiscoveryInfo(userName, null);
		updateAliasInDatabase(userName, discoveryModel, previousDiscoveryModel);
		
		String directory = uploadingDir + "/" + userName + "/" + fileUploadDataSource.getAliasFileName();
		writeToCSV(directory,discoveryModel);
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setDescription("Successfully updated the alias file");
		
		try {
			updateAutoSuggestionForColumns(userName);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return genericResponse;
	}
	
	public void updateAliasInDatabase(String userName, DiscoveryModel updatedDiscoveryModel, DiscoveryModel previousDiscoveryModel) {
		List<DiscoveryModelColumns> prevDiscoveryModelColumnList = previousDiscoveryModel.getDiscovery();
		
		Map<String, DiscoveryModelColumns> prevDiscoveryModelMap = new HashMap<String,DiscoveryModelColumns>();
		for(DiscoveryModelColumns prevDiscoveryModelColumn : prevDiscoveryModelColumnList) {
			prevDiscoveryModelMap.put(prevDiscoveryModelColumn.getColumnName(), prevDiscoveryModelColumn);
		}
		
		UserDefinedColumns userDefinedColumns = new UserDefinedColumns();
		UserProfileEntity userProfileEntity = new UserProfileEntity();
		userProfileEntity.setUsername(userName);
		List<DiscoveryModelColumns> updatedDiscoveryModelColumnList = updatedDiscoveryModel.getDiscovery();
		
		for(DiscoveryModelColumns updatedDiscoveryModelColumn : updatedDiscoveryModelColumnList) {
			DiscoveryModelColumns  prevDiscoveryModelColumns = prevDiscoveryModelMap.get(updatedDiscoveryModelColumn.getColumnName());
			if(!updatedDiscoveryModelColumn.equals(prevDiscoveryModelColumns)) {
				//Updated in the database
				userDefinedColumns.setAiGeneratedName("");
				userDefinedColumns.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				userDefinedColumns.setDataColumnName(updatedDiscoveryModelColumn.getColumnName());
				userDefinedColumns.setDataType(updatedDiscoveryModelColumn.getDataType());
				userDefinedColumns.setDescription(updatedDiscoveryModelColumn.getDescription());
				userDefinedColumns.setUserDefinedName(updatedDiscoveryModelColumn.getAliasName());
				userDefinedColumns.setUserProfile(userProfileEntity);
				userDefinedColumnDao.saveUserDefinedColumn(userDefinedColumns);
			}
		}
		
	}
	
	
	
	@Override
	public byte[] getContentImage(String imageName) throws IOException
	{
		//LOGGER.debug("Entering getClipImage method of class LauncherMetadataServiceImpl");

		String imageFolderPath=PropertiesConfigurationReader.getServerProperty("image_folder_path");
		String imageExtention=PropertiesConfigurationReader.getServerProperty("image_extension");
		byte[] imageInByte = null;
		File file = new File(imageFolderPath + imageName + "." + imageExtention);
		System.out.println("path to image queried : " + imageFolderPath + imageName + "." + imageExtention);
		BufferedImage bImage = ImageIO.read(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(bImage, imageExtention, bos );
		imageInByte = bos.toByteArray();
		
		//LOGGER.debug("Leaving getClipImage method of class LauncherMetadataServiceImpl");
		return imageInByte;
	}
	
	/**
	 * This method will upload the requested by the user, with specific to folder created by user-name
	 * @param file
	 * @param userName
	 * @return
	 * @throws MissingMandatoryParameterException
	 * @throws ServerException
	 */
	@Override
	public FileUploadResponse saveMultipartFile(MultipartFile file, String userName) throws MissingMandatoryParameterException, ServerException{
		
		LOGGER.debug("In saveMultipartFile of DhiServiceImpl method");
		
		if(file == null) {
			throw new MissingMandatoryParameterException("Seems like file is not attached!!! Please check","error","dhi_file_upload_error");
		}
		
		if(userName == null || userName.trim().equalsIgnoreCase("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name","error","dhi_missing_required_parameter");
		}
		
		FileUploadResponse fileUploadResponse = new FileUploadResponse();
		
		String description = "";
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		String directory = uploadingDir + "/" + userName + "/";
		try {
			new File(directory).mkdirs();
		}catch(Exception e) {
			throw new ServerException("Server Internal Error : Unable to create directory with the path - " + directory,"error","dhi_internal_server_error");
		}
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		String fileName = file.getOriginalFilename();
		String[] fileFirstName = fileName.split("\\.");
		System.out.println(fileFirstName[0]);
		
		
		List<String> uploadingFileErrorList = new ArrayList<String>();
		List<String> uploadingFileSuccessList = new ArrayList<String>();
		 
		 //Creating alias file path
		 String aliasFileName = fileFirstName[0]+"_alias.csv";
		 File aliasFile = new File(directory + aliasFileName);
		 aliasFile.delete();
		 
		 //Creating data-model file
		 File dataModelFile = new File(directory + file.getOriginalFilename());
		 dataModelFile.delete();
        try {
        	file.transferTo(dataModelFile);
        	uploadingFileSuccessList.add(file.getOriginalFilename());
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			uploadingFileErrorList.add(file.getName());
			throw new ServerException("Server Internal Error : Unable to upload file(s) to the server - "  + e.getMessage(),"error","dhi_internal_server_error");	
		}
        
        //Save the data-file information
        FileUploadDataSource fileUploadDataSource = new FileUploadDataSource();
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setUsername(userName);
        fileUploadDataSource.setUserProfile(userProfileEntity);
        fileUploadDataSource.setAliasFileName(aliasFileName);
        fileUploadDataSource.setDataModelFileName(file.getOriginalFilename());
        fileUploadDataSource.setCreatedDate(timestamp);
        fileUploadDataSource.setDefaultFlag("true");
        fileUploadDataSource.setIsActive("true");
        
        try {
        	FileUploadDataSource fileUploadDataSourceEntity = fileUploadDataSourceDao.getFileDataSource(fileUploadDataSource.getUserProfile().getUsername(), fileUploadDataSource.getDataModelFileName());
        	fileUploadDataSourceEntity.setCreatedDate(timestamp);
        	fileUploadDataSourceEntity.setDefaultFlag(fileUploadDataSource.getDefaultFlag());
        	fileUploadDataSourceDao.updateFileDataSource(fileUploadDataSourceEntity);
        	fileUploadDataSourceDao.updateDefaultFlagByUserName(fileUploadDataSource.getUserProfile().getUsername(), fileUploadDataSource.getDataModelFileName());
		} catch (NoRecordFoundException e1) {
			fileUploadDataSourceDao.saveFileDataSource(fileUploadDataSource);
			fileUploadDataSourceDao.updateDefaultFlagByUserName(fileUploadDataSource.getUserProfile().getUsername(), fileUploadDataSource.getDataModelFileName());
		}
    
 
        
        
        //Get the token from the database
        String token = null;
		try {
			token = authHelper.getUserTokenByUserName(userName);
			UserTokenModel userTokenModel = authHelper.getUserTokenModelFromToken(token);
			userTokenModel.getUserDataFileNames().addAll(uploadingFileSuccessList);
			Gson gson = new Gson();
			String userTokenModelString = gson.toJson(userTokenModel);
			token = authHelper.createJsonWebToken(userName, new Long(DHIConstants.TOKEN_DURATIONDAYS),userTokenModelString);
			UserToken userToken = userTokenDao.getUserTokenByName(userName);
			userToken.setRemarks("Added data files");
			userToken.setToken(token);
			userTokenDao.saveOrUpdateUserToken(userToken);
		}catch(NoRecordFoundException e) {
			//Not required to re-throw
		}
				
		if(uploadingFileErrorList != null && uploadingFileErrorList.size() > 0) {
			fileUploadResponse.setStatus("error");
			description = "Occurred error while uploading file with names : " + uploadingFileErrorList;
			fileUploadResponse.setStatusCode("dhi_file_upload_error");
			
		}else {
			String pythonAIMetaDataUrl = PropertiesConfigurationReader.getServerProperty("python_ai_meta_data_url");
			String dataFilePath = directory + file.getOriginalFilename();
			MetaDataFileRequest metaDataFileRequest = new MetaDataFileRequest();
			metaDataFileRequest.setDataFilePath(dataFilePath);
			Gson gson = new Gson();
			String requestBody = gson.toJson(metaDataFileRequest);
			
			// Call to separate thread for indexing the content for search api
			try{
				//LOGGER.debug("In createOrUpdatePlaylist() of class PlaylistSyncServiceImpl, calling the search indexing with a separate thread for PlaylistId : " + playlistItem.getId());
				FileMetadataThread fileMetadataThread = new FileMetadataThread(pythonAIMetaDataUrl,requestBody,directory + aliasFileName);
				fileMetadataThread.start();
				fileMetadataThread.join();
			}catch(Exception e){
				//LOGGER.error("In createOrUpdatePlaylist() of class PlaylistSyncServiceImpl, error occurred while calling the playlist indexing, please check immediately and resolve the probrem ASAP, for PlaylistId : " + playlistItem.getId());
			}
			
			fileUploadResponse.setStatus("success");
			description = "Successfully uploaded the files with names : " + uploadingFileSuccessList;
			fileUploadResponse.setDescription(description);
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		try {
			updateAutoSuggestionForColumns(userName);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return fileUploadResponse;
	}
	
	@Override
	public GenericResponse updateAutoSuggestionForColumns(String userName) throws InvalidDataException, MissingMandatoryParameterException, NoRecordFoundException {
		
		if (userName == null || userName.trim().equals("")) {
			throw new MissingMandatoryParameterException("Missing mandatory parameter : user-name","error","dhi_missing_required_parameter");
		}
		
		DiscoveryModel discoveryModel = getDiscoveryInfo(userName, null);
		List<DiscoveryModelColumns> discoveryModelColumns = discoveryModel.getDiscovery();
		String keywords = "";
		for(DiscoveryModelColumns discoveryModelColumnsItem : discoveryModelColumns) {
			keywords += discoveryModelColumnsItem.getColumnName();
			keywords += ",";
			if(discoveryModelColumnsItem.getAliasName() != null && !(discoveryModelColumnsItem.getAliasName().trim().equals(""))){
				keywords += discoveryModelColumnsItem.getAliasName();
			}
		}
		
		// go to the specific directory for the user
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();

		String directory = uploadingDir + "/" + userName + "/";
		String fileName = "auto-suggestion-keywords.txt";
		
		
		File file = new File(directory + fileName);
		if(file.exists()) {
			file.delete();
		}

		writeUsingOutputStream(directory, fileName, keywords);
		
		
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setDescription("Successfully updated the auto-suggestions keywords and queries.");
		
		return genericResponse;
	}
	
	@Override
	public AutoSuggestionResponseModel getAutoSuggestionByUserName(String userName) throws MissingMandatoryParameterException {
		
		// go to the specific directory for the user
		String uploadingDir = PropertiesConfigurationReader.getServerProperty("path_to_uploaded_csv");
		userName = userName.trim();
		Set<String> autoSuggestionsKeywordList = new HashSet<String>();
		
		String directory = uploadingDir + "/" + userName + "/";
		String fileName = "auto-suggestion-keywords.txt";
		String autoSuggestionQueriesFile = "auto-suggestion-query.txt";
		
		if(new File(directory +  autoSuggestionQueriesFile).exists()) {
			String autoSuggestionQueries = readingAFile(directory, autoSuggestionQueriesFile);
			if(autoSuggestionQueries != null && !(autoSuggestionQueries.trim().equals(""))) {
				String[] keywords = autoSuggestionQueries.split(",");
				autoSuggestionsKeywordList.addAll(Arrays.asList(keywords));
			}
		}
		

		if(new File(directory +  fileName).exists()) {
			String autoSuggestionKeywords = readingAFile(directory, fileName);
			if(autoSuggestionKeywords != null && !(autoSuggestionKeywords.trim().equals(""))) {
				String[] keywords = autoSuggestionKeywords.split(",");
				autoSuggestionsKeywordList.addAll(Arrays.asList(keywords));
			}
		}
		
		
		AutoSuggestionResponseModel autoSuggestionResponseModel = new AutoSuggestionResponseModel();
		autoSuggestionResponseModel.setAutoSuggestionKeywords(autoSuggestionsKeywordList);
		
		return autoSuggestionResponseModel;
	}
	
	
	/**
	 * 
	 * @author manmaya.champatiray
	 *
	 */
	
	class FileMetadataThread extends Thread{
			
			private String requestBody =  null;
			private String sourceUrl = null;
			private String userDirectory = null;
			
			public FileMetadataThread() {
				
			}
			
			public FileMetadataThread(String sourceUrl, String requestBody) {
				this.requestBody = requestBody;
				this.sourceUrl = sourceUrl;
			}
			
			public FileMetadataThread(String sourceUrl, String requestBody, String userDirectory) {
				this.requestBody = requestBody;
				this.sourceUrl = sourceUrl;
				this.userDirectory = userDirectory;
			}
			
			public void run(){
				String responseString =  WebServiceUtility.getPostResponseFromWebService(sourceUrl, requestBody);
				
				if(responseString != null) {
					Gson gson = new Gson();
					DiscoveryModel discoveryModel = gson.fromJson(responseString,DiscoveryModel.class);
					//userDirectory += "alias.csv";
					writeToCSV(userDirectory,discoveryModel);
				}
				
			}
			
			public void writeToCSV(String filePath, DiscoveryModel discoverModel) {
				
				List<DiscoveryModelColumns> discoveryColumns = discoverModel.getDiscovery();
				// first create file object for file placed at location 
				// specified by filepath 
				//filePath += "alias.csv";
				
				try (PrintWriter writer = new PrintWriter(new File(filePath))) {

					 StringBuilder sb = new StringBuilder();
				      sb.append("col_name");
				      sb.append(',');
				      sb.append("col_type");
				      sb.append(',');
				      sb.append("col_description");
				      sb.append(',');
				      sb.append("alias");
				      sb.append('\n');
				      
				      for(DiscoveryModelColumns discoveryModelColumns : discoveryColumns) {
				    	
				        	String columnName = discoveryModelColumns.getColumnName();
				        	  if(columnName ==  null || columnName.equalsIgnoreCase("null")) {
				        		  columnName =  "";
				        	  }
				        	String dataType = discoveryModelColumns.getDataType();
				        	if(dataType ==  null || dataType.equalsIgnoreCase("null")) {
				        		dataType =  "";
				        	  }
				        	String description = discoveryModelColumns.getDescription();
				        	if(description ==  null || description.equalsIgnoreCase("null")) {
				        		description =  "";
				        	  }
				        	String alias = discoveryModelColumns.getAliasName();
				        	if(alias ==  null || alias.equalsIgnoreCase("null")) {
				        		alias =  "";
				        	  }
				        	
				        	 sb.append(columnName);
						     sb.append(',');
						     sb.append(dataType);
						     sb.append(',');
						     sb.append(description);
						     sb.append(',');
						     sb.append(alias);
						     sb.append('\n');
				        }

				      writer.write(sb.toString());

				      System.out.println("done!");

				    } catch (FileNotFoundException e) {
				      System.out.println(e.getMessage());
				      
				    }
			}

		}
	
	public void writeToCSV(String filePath, DiscoveryModel discoverModel) {
		
		List<DiscoveryModelColumns> discoveryColumns = discoverModel.getDiscovery();
		// first create file object for file placed at location 
		// specified by filepath 
		//filePath += "alias.csv";
		
		try (PrintWriter writer = new PrintWriter(new File(filePath))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append("col_name");
		      sb.append(',');
		      sb.append("col_type");
		      sb.append(',');
		      sb.append("col_description");
		      sb.append(',');
		      sb.append("alias");
		      sb.append('\n');
		      
		      for(DiscoveryModelColumns discoveryModelColumns : discoveryColumns) {
		        	String columnName = discoveryModelColumns.getColumnName();
		        	String dataType = discoveryModelColumns.getDataType();
		        	String description = discoveryModelColumns.getDescription();
		        	String alias = discoveryModelColumns.getAliasName();
		        	
		        	 sb.append(columnName);
				     sb.append(',');
				     sb.append(dataType);
				     sb.append(',');
				     sb.append(description);
				     sb.append(',');
				     sb.append(alias);
				     sb.append('\n');
		        }

		      writer.write(sb.toString());

		      System.out.println("done!");

		    } catch (FileNotFoundException e) {
		      System.out.println(e.getMessage());
		      
		    }
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
				System.out.println(isFileCreated);
			} catch (Exception e) {
				// throw new ServerException("Server Internal Error : Unable to create directory
				// with the path","error","dhi_internal_server_error");
				e.printStackTrace();
			}
			os = new FileOutputStream(new File(filePath + fileName), true);
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

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(br != null)
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

	@Override
	public List<JSONObject> dataFormattingForDownload(QueryResponse queryResponse) {
		
		List<JSONObject> list = new ArrayList<JSONObject>();
		if(queryResponse == null) {
			return list;
		}
		TabularData tabularData = queryResponse.getTabularData();
		
		List<String> columnNames = tabularData.getColumNames();
		List<Row> rowList = tabularData.getRow();
		
		String openingSquareBracket = "[";
		String closingSquareBracket = "]";
		
		
		
		String openningCurlyBrace = "{";
		String closingCurlyBrace = "}";
		
		for(Row row : rowList) {
			List<ColumnValue> columnValueList = row.getColumnValues();
			boolean isCommaRequired = false;
			//String jsonRow = "";
			JSONObject jsonRow = new JSONObject();
			for(ColumnValue columnValue : columnValueList) {
				jsonRow.put(columnValue.getColumnName(), columnValue.getColumnValue());
			}
			list.add(jsonRow);
				
		}
		return list;
		
	}
	


	
	
	
}
