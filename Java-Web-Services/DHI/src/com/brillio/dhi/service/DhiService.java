package com.brillio.dhi.service;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.brillio.dhi.exception.InvalidDataException;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.exception.ServerException;
import com.brillio.dhi.model.AutoSuggestionResponseModel;
import com.brillio.dhi.model.ChatMessageRequest;
import com.brillio.dhi.model.DiscoveryModel;
import com.brillio.dhi.model.FileUploadResponse;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.model.QueryResponse;
import com.brillio.dhi.model.TabularData;

public interface DhiService {

	//QueryResponse replyMessage(String chatRoomId, ChatMessageRequest chatMessageRequest) throws NoRecordFoundException, MissingMandatoryParameterException, InvalidDataException;

	byte[] getContentImage(String imageName) throws IOException;

	DiscoveryModel getDiscoveryInfo(String userName, String dataFileName) throws InvalidDataException, MissingMandatoryParameterException, NoRecordFoundException;

	FileUploadResponse saveMultipartFile(MultipartFile file, String userName)
			throws MissingMandatoryParameterException, ServerException;

	GenericResponse updateAliasFileForDiscovery(String userName, DiscoveryModel discoveryModel) throws MissingMandatoryParameterException, NoRecordFoundException, InvalidDataException;

	GenericResponse updateAutoSuggestionForColumns(String userName)
			throws InvalidDataException, MissingMandatoryParameterException, NoRecordFoundException;

	AutoSuggestionResponseModel getAutoSuggestionByUserName(String userName) throws MissingMandatoryParameterException;

	List<JSONObject> dataFormattingForDownload(QueryResponse queryResponse) ;

	QueryResponse replyMessage(String chatRoomId, String phoneNumber, ChatMessageRequest chatMessageRequest)
			throws NoRecordFoundException, MissingMandatoryParameterException, InvalidDataException;

	QueryResponse replyMessageV2(String chatRoomId, String phoneNumber, ChatMessageRequest chatMessageRequest)
			throws NoRecordFoundException, MissingMandatoryParameterException, InvalidDataException;
	
}
