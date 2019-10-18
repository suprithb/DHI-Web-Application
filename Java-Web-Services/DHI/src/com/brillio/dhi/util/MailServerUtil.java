package com.brillio.dhi.util;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.brillio.dhi.configuration.PropertiesConfigurationReader;
import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.constants.PropertyConstant;
import com.brillio.dhi.exception.BdpMessagingException;
import com.brillio.dhi.exception.DhiSecurityException;
import com.brillio.dhi.model.TokenInfo;

public class MailServerUtil {
	
	/*send a mail to multiple receipients without attachements*/
    public static boolean sendEmailWithOutAttachments(String toEmail,String message,String subject) throws BdpMessagingException, DhiSecurityException {
    
          
        String encrypteduserName = PropertiesConfigurationReader.getServerProperty(PropertyConstant.MAILING_USER_NAME);
        String userName = "";
  		try {
  			TokenInfo tokenInfo = AuthHelper.verifyToken(encrypteduserName);
  			userName = tokenInfo.getUserId();
  		}catch(SecurityException e) {
  			throw new DhiSecurityException("Seems like token is manipulated or expired.",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
  		}
  		
  		final String decryptedUserName = userName;
  		
          String encryptedPassword = PropertiesConfigurationReader.getServerProperty(PropertyConstant.MAILING_USER_PASSWORD);
          String password = "";
    	try {
    		TokenInfo tokenInfo = AuthHelper.verifyToken(encryptedPassword);
    		password = tokenInfo.getUserId();
    	}catch(SecurityException e) {
    		throw new DhiSecurityException("Seems like token is manipulated or expired.",DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
    	}
    	
    	final String decryptedPassword = password;
          
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", PropertiesConfigurationReader.getServerProperty(PropertyConstant.MAILING_HOST));
        properties.put("mail.smtp.port", PropertiesConfigurationReader.getServerProperty(PropertyConstant.MAILING_PORT));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", decryptedUserName);
        properties.put("mail.password", decryptedPassword);
 
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(decryptedUserName, decryptedPassword);
            }
        };
        Session session = Session.getInstance(properties, auth);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
        
        	try {
				msg.setFrom(new InternetAddress(decryptedUserName));
			//===================================
				
			
		         
				msg.setRecipients(Message.RecipientType.TO,
		                InternetAddress.parse(toEmail));
		          //  message.setSubject("Test");
		         //   message.setText("HI");

		         //  Transport.send(message);
//
		        //    System.out.println("Done");
				
			//===================================
        	   
            /* InternetAddress[] mailAddress_TO = new InternetAddress [toAddress.length];
              for(int i=0;i<toAddress.length;i++){
              	mailAddress_TO[i] = new InternetAddress(toAddress[i]);
             }
             
              msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(
                      "manmaya.champatiray@brillio.com"));
              msg.addRecipient(Message.RecipientType.CC, new InternetAddress(
                      "shruthi.m@brillio.com"));*/
              
              msg.setSubject(subject);
              msg.setSentDate(new Date());
       
              // creates message part
              MimeBodyPart messageBodyPart = new MimeBodyPart();
              messageBodyPart.setContent(message, "text/plain");
       
              // creates multi-part
              Multipart multipart = new MimeMultipart();
              multipart.addBodyPart(messageBodyPart);
       
              // sets the multi-part as e-mail's content
              msg.setContent(multipart);
       
              // sends the e-mail
              Transport.send(msg);
        	} catch (MessagingException e) {
				e.printStackTrace();
				throw new BdpMessagingException("Got Error while sending email");
			}
 
        
        return true;
 
    }
    
    
    /*send mail to multiple recipients with multiple attachements*/
    public static void sendEmailWithAttachments(
             String toAddress[],String[] attachFiles)
            throws AddressException, MessagingException {
    	
    	
    	  String host = "outlook.office365.com";
          String port = "587";
          final String userName = "shruthi.m@brillio.com";
          final String password = "brillio@2020";
          
          String subject = "New email with attachments";
          String message = "I have some attachments for you.";
          
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);
 
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] mailAddress_TO = new InternetAddress [toAddress.length];
        for(int i=0;i<toAddress.length;i++){
        	mailAddress_TO[i] = new InternetAddress(toAddress[i]);
        	msg.addRecipient(Message.RecipientType.TO, mailAddress_TO[i]);
      }
     
        
        /*msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(
                "manmaya.champatiray@brillio.com"));
        msg.addRecipient(Message.RecipientType.CC, new InternetAddress(
                "shruthi.m@brillio.com"));*/
        
        msg.setSubject(subject);
        msg.setSentDate(new Date());
 
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");
 
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
 
        // adds attachments
        if (attachFiles != null && attachFiles.length > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();
 
                try {
                    attachPart.attachFile(filePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
 
                multipart.addBodyPart(attachPart);
            }
        }
 
        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
 
        // sends the e-mail
        Transport.send(msg);
 
    }
 
    
  
    /*public static void main(String[] args) {
        // SMTP info
      
 
      
        String[] mailTo = new String[3];
        mailTo[0] = "shruthi.m@brillio.com";
        mailTo[1] = "sneha.sahay@brillio.com";
        mailTo[2] = "manmaya.champatiray@brillio.com";
        

 
        // attachments
        String[] attachFiles = new String[1];
        attachFiles[0] = "D:/links.txt";
     
 
        try {
        	
        	
         sendEmailWithAttachments( mailTo,attachFiles);
            
          
         sendEmailWithOutAttachments( mailTo);
            
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }
	*/
   
}
