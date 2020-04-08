package com.brillio.dhi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.brillio.dhi.constants.DHIConstants;


/**
 * This is a utility class which is used for making all HTTP service related calls like Web-Service REST Calls (POST,GET,DELETE,PUT).
 * @author Manmaya.Champatiray
 *
 */
public class WebServiceUtility {
	
	
	
	private static final Logger LOGGER = Logger.getLogger(WebServiceUtility.class);
	
	/**
	 * This method is the GET request web-service, which will accept url as a string
	 * @param urlString
	 * @return
	 */
	public static String getWebService(String urlString) {
		String response ="";
		HttpURLConnection conn = null;
		  try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(DHIConstants.GET);
			conn.setRequestProperty(DHIConstants.ACCEPT,DHIConstants.APPLICATION_JSON);
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				response += output;
				
			}
			System.out.println(response);
		  } catch (MalformedURLException e) {
			 LOGGER.error("In getWebService() of WebServiceUtility, encountered with  MalformedURLException  for url : " + urlString);
		  } catch (IOException e) {
			 LOGGER.error("In getWebService() of WebServiceUtility, encountered with  IOException for url : " + urlString);
			 LOGGER.error("In getWebService() of WebServiceUtility, encountered with  IOException cause as : " + e.getCause() + " and Message : " + e.getMessage() );
		  }finally{
			  if(conn != null){
				  conn.disconnect(); 
				  LOGGER.debug("In getWebService() of WebServiceUtility, closing the opened connection" );
			  }
		  }
		  if(LOGGER.isDebugEnabled()){
			  LOGGER.debug("In getWebService() of WebServiceUtility, returning with response as : " + response + "\n for query string as : " + urlString);
		  }
		  return response;
		}
	
	
	/**
	 * This method is the GET request web-service, which will accept url as a string, headerKey and headerValues
	 * @param urlString
	 * @param headerKey
	 * @param headerValues
	 * @return
	 */
	public static String getWebService(String urlString,String headerKey,String headerValues) {
		String response ="";
		HttpURLConnection conn = null;
		  try {

			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(DHIConstants.GET);
			conn.setRequestProperty(DHIConstants.ACCEPT,DHIConstants.APPLICATION_JSON);
			conn.setRequestProperty(headerKey, headerValues);
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response += output;
			}
		  }catch (MalformedURLException e) {
			  LOGGER.error("In getWebService() of WebServiceUtility, encountered with  MalformedURLException  for url : " + urlString);
		  }catch (IOException e) {
			 LOGGER.error("In getWebService() of WebServiceUtility, encountered with  IOException for url : " + urlString);
			 LOGGER.error("In getWebService() of WebServiceUtility, encountered with  IOException cause as : " + e.getCause() + " and Message : " + e.getMessage() );
		}finally{
			if(conn != null){
				conn.disconnect(); 
				LOGGER.debug("In getWebService() of WebServiceUtility, closing the opened connection" );
			}
		}
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("In getWebService() of WebServiceUtility, returning with response as : " + response + "\n for query string as : " + urlString);
		}
		 return response;
	}
	
public static String getPUTResponseFromWebService(String sourceUrl,String request) {
		
		String response="";
		String responseString ="";
		  try {

			URL url = new URL(sourceUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(DHIConstants.PUT);
			conn.setRequestProperty(DHIConstants.CONTENT_TYPE, DHIConstants.APPLICATION_JSON);

			if(request != null){
				OutputStream os = conn.getOutputStream();
				os.write(request.getBytes());
				os.flush();
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			while ((response = br.readLine()) != null) {
				responseString += response;
			}
			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		 }
		return responseString;

	}


	public static String getPostResponseFromWebService(String sourceUrl, String request) {

		String response = "";
		String responseString = "";
		try {
			//String encodedKeyword = URLEncoder.encode(request, "UTF-8");
			URL url = new URL(sourceUrl);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			

			//if (encodedKeyword != null) {
				OutputStream os = conn.getOutputStream();
				//os.write(encodedKeyword.getBytes());
				os.write(request.getBytes());
				os.flush();
			//}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			while ((response = br.readLine()) != null) {
				responseString += response;
				// System.out.println(output);
			}
			
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;

	}
		
	}
	

