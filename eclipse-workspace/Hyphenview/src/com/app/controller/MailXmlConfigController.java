package com.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.common.AESCrypt;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.RestartApp;

/**
 * Servlet implementation class DBConfigController
 */
@WebServlet("/MailXmlConfigController")
public class MailXmlConfigController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MailXmlConfigController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In the Post method of  MailXmlConfigController..");
		String license = request.getParameter("license");
		String smtpServer = request.getParameter("smtpServer");
		String smtpPort = request.getParameter("smtpPort");
		final String mailUser = request.getParameter("mailUser");
		final String mailPassword = request.getParameter("mailPassword");
		String encryptedPwd = null;
		try {
			//validate the mail configuration details
			Properties config = new Properties();
	        config.put("mail.smtp.auth", "true");
	        config.put("mail.smtp.starttls.enable", "true");
	        config.put("mail.smtp.host", smtpServer);
	        config.put("mail.smtp.port", smtpPort);
	        config.put("mail.smtp.ssl.trust", smtpServer);
			Session session = Session.getInstance(config, new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(mailUser, mailPassword);
	            }
	        });
			
			try{
				Transport transport = session.getTransport("smtp");
				transport.connect();
				transport.close();
			}catch(Exception e){
				request.setAttribute("Message", "Invalid Mail Configuration Details. Kindly provide valid values.");
				getServletContext().getRequestDispatcher("/MailXmlConfiguration.jsp").forward(request, response);
			}
			
			// encrypt the database password before storing in database
			AESCrypt aesCrypt = new AESCrypt();
			encryptedPwd = aesCrypt.encrypt(mailPassword);
			
			String directory = System.getenv("SERVER_HOME");
			PropertiesConfiguration pc = new PropertiesConfiguration(directory+"/webapps/Hyphenview/WEB-INF/classes/properties/config.properties");
			pc.setProperty("xmlLicense", license);
			pc.setProperty("mailsmtpserver", smtpServer);
			pc.setProperty("mailsmtpport", smtpPort);
			pc.setProperty("mailaccountusr", mailUser);
			pc.setProperty("mailaccountpwd", encryptedPwd);
			pc.save();
			
			// create folder DownloadedFiles in C drive for RPT reports
			File rptDir = new File("/home/DownloadedFiles/");
	         if(!rptDir.exists()){
	        	 boolean dirCreated = rptDir.mkdirs();
	        	 LOGGER.debug("Directory " + rptDir + " got created?? --> " + dirCreated);
	         }
	         
	      // start the thread to restart the tomcat service
	         LOGGER.debug("creating thread to restart the tomcat service...");
	         Thread restartThread = new Thread(new RestartApp());
	         restartThread.start();	
	         
	         getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
	    } catch (ConfigurationException e) {
			e.printStackTrace();
			getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
	    }catch (Exception e) {
				e.printStackTrace();
		}
		
	}

}
