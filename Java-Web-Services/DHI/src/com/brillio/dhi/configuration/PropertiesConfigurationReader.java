package com.brillio.dhi.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.constants.PropertyConstant;


public class PropertiesConfigurationReader {
	
	public static String configBaseLocation = null;
	public static PropertiesConfiguration userProfile = null;
	public static PropertiesConfiguration webService = null;
	public static PropertiesConfiguration server = null;
	public static PropertiesConfiguration graphProperty = null;
	public static PropertiesConfiguration credential = null;
	public static PropertiesConfiguration database = null;
	
	
	
	
	
	/**
	 * This method will return the properties for the database.properties file based on the property key name
	 * @param key
	 * @return
	 */
	public static String getDatabaseProperty(String key)
	{
		if(database==null)
		{
			try {
				System.out.println("Database Configuration path: " + getBaseProperty()  + PropertyConstant.DATABASE_PROPERTIES);
				database=new PropertiesConfiguration(getBaseProperty()  + PropertyConstant.DATABASE_PROPERTIES);
				
				System.out.println("Key : " + key);
				FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
				strategy.setRefreshDelay(1000);
				database.setReloadingStrategy(strategy);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		return database.getString(key);
		
	}
	
	
	
	/**
	 * This method will return the properties for the dashboard.properties file based on the property key name
	 * @param key
	 * @return
	 */
	public static String getCredentialProperty(String key)
	{
		if(credential==null)
		{
			try {
				credential=new PropertiesConfiguration(getBaseProperty()  + PropertyConstant.CREDENTIAL_PROPERTIES);
				FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
				strategy.setRefreshDelay(1000);
				credential.setReloadingStrategy(strategy);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		return credential.getString(key);
		
	}

	
	/**
	 * This method will return the properties for the dashboard.properties file based on the property key name
	 * @param key
	 * @return
	 */
	public static String getGraphProperty(String key)
	{
		if(graphProperty==null)
		{
			try {
				graphProperty=new PropertiesConfiguration(getBaseProperty()  + PropertyConstant.GRAPH_PROPERTIES);
				//offerManagement.setDelimiterParsingDisabled(true);
				FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
				strategy.setRefreshDelay(1000);
				graphProperty.setReloadingStrategy(strategy);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		return graphProperty.getString(key);
		
	}
	
	
	
	/**
	 * This method will return the properties for the server.properties file based on the property key name
	 * @param key
	 * @return
	 */
	public static String getServerProperty(String key)
	{
		System.out.println("Server Property path : " + getBaseProperty()  + PropertyConstant.SERVER_PROPERTIES);
		System.out.println("Key : " + key);
		System.out.println("==============================================");
		if(server==null)
		{
			try {
				System.out.println("Server Property path : " + getBaseProperty()  + PropertyConstant.SERVER_PROPERTIES);
				System.out.println("Key : " + key);
				server=new PropertiesConfiguration(getBaseProperty()  + PropertyConstant.SERVER_PROPERTIES);
				
				FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
				strategy.setRefreshDelay(1000);
				server.setReloadingStrategy(strategy);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		return server.getString(key);
		
	}
	
	
	

	/**
	 * This method will return the properties for the user_profile.properties file based on the property key name
	 * @param key
	 * @return
	 */
	public static String getWebServiceProperty(String key)
	{
		if(webService==null)
		{
			try {
				webService=new PropertiesConfiguration(getBaseProperty()  + PropertyConstant.WEB_SERVICE_PROPERTIES);
				FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
				strategy.setRefreshDelay(1000);
				webService.setReloadingStrategy(strategy);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		return webService.getString(key);
		
	}
	
	
	
	/**
	 * This method will return the properties for the user_profile.properties file based on the property key name
	 * @param key
	 * @return
	 */
	public static String getUserProfileProperty(String key)
	{
		if(userProfile==null)
		{
			try {
				userProfile=new PropertiesConfiguration(getBaseProperty() + PropertyConstant.USER_PROFILE_PROPERTIES);
				FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
				strategy.setRefreshDelay(1000);
				userProfile.setReloadingStrategy(strategy);
				//LOGGER.info("Reloading file connectionStringProperty file");
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		return userProfile.getString(key);
		
	}
	
	/**
	 * This method will return the base location for the bdp configurations based on the property key name
	 * @return
	 */
	public static String getBaseProperty()
	{
		if(configBaseLocation==null)
		{
			configBaseLocation= System.getProperty(PropertyConstant.BRILLIO_DHI_BASE_CONFIG_PROPERTIES);
			if(configBaseLocation != null)
				configBaseLocation += DHIConstants.FORWARD_SLASH;
			System.out.println("Config Base location : " + configBaseLocation);
		}
		return configBaseLocation;
	}

}
