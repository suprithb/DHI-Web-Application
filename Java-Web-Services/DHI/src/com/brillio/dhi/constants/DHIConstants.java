package com.brillio.dhi.constants;

public class DHIConstants {
	
	public static final String USER_ID = "userName";
	public static final String USER_TOKEN_INFORMATION = "userTokenInformation";

	//Regular Expression Constants
	public static final String PERSON_NAME_VALIDATION_REGEX="";
	public static final String EMAIL_VALIDATION_REGEX="";
	public static final String PHONE_NUMBER_VALIDATION_REGEX="";
	public static final String PASSWORD_VALIDATION_REGEX="";

	//AuthenAuthor
	public static final int TOKEN_DURATIONDAYS=10000;
	public static final String INFO = "info";
	public static final String ISS="iss";
	public static final String IAT="iat";
	public static final String EXP="exp";
	
	
	public static final String ERROR="error";
	public static final String DHI_BAD_REQUEST="dhi_bad_request";
	public static final String DMIC_MISSING_TOKEN="dhi_missing_token";
	public static final String CA_INVALID_TOKEN="dhi_invalid_token";
	public static final String DHI_SERVER_INTERNAL_ERROR="dhi_server_internal_error";
	public static final String DHI_SECURITY_EXCEPTION="dhi_security_exception";
	
	
	public static final String DHI_USER_NOT_FOUND_ERROR="dhi_user_not_found_error";
	public static final String DHI_SUCCESS = "success";
	public static final String DHI_REPORT_ERROR = "dhi_report_error";
	
	public static final String DHI_ENTITY_DELETED = "dhi_entity_deleted";
	
	//Constants for key generation prefix name
	public static final String PRJ_ID = "PrjId";
	public static final String DS_ID = "DsId";
	public static final String INFRA_ID = "InfraId";
	public static final String REPORT_ID = "ReportId";
	public static final String SERVICE_ID = "ServiceId";
	
	public static final String USER_PROFILE_USERNAME = "userProfile.username";
	public static final String USER_PROJECT_PROJECT_ID = "userProjects.projectId";
	public static final String USER_PROFILE_PHONE_NUMBER = "phoneNumber";
	public static final String USER_PROFILE_USER_NAME = "username";
	public static final String USER_AUTH_USER_NAME = "username";
	
	public static final String FONT_FAMILY = "family";
	public static final String FONT_CATEGORY = "category";
	
	
	public static final String EMPTY_STRING = "";
	
	public static final String UTF_8 = "UTF-8";
	public static final String AES = "AES";
	public static final String AES_INSTANCE = "AES/CBC/PKCS5PADDING";
	
	
	public static final String FORWARD_SLASH="/";

	public static final String COMMA = ",";

	public static final CharSequence SPACE = " ";

	public static final CharSequence HYPEN = "-";
	
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	
	public static final String APPLICATION_JSON = "application/json";
	public static final String ACCEPT = "Accept";
	public static final String CONTENT_TYPE = "Content-Type";

}
