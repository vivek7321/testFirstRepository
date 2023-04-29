package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.ConstructDashboard;
import com.app.dao.EraviewDBconnection;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import era.util.encryption;

/**
 * Servlet implementation class LoginValidateController
 */
@WebServlet("/LoginValidateController")
public class LoginValidateController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginValidateController() {
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
		LOGGER.info("In the Post method of LoginValidateController..");

		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		p.load(is);

		String domainName = p.getProperty("domainName");
		String loginAuthentication = p.getProperty("loginAuthentication");
		LOGGER.info(loginAuthentication);
		String emailId = request.getParameter("emailId");
		HttpSession session = request.getSession();
		session.setAttribute("emailId", emailId);
		session.setAttribute("Message","");
		String pwd = request.getParameter("password");
		System.setProperty("java.net.preferIPv4Stack", "true");
		if(loginAuthentication.equalsIgnoreCase("AD")) {
		AdAuthentication AD = new AdAuthentication(domainName);
		Boolean UserAvl = false;
		try {
		UserAvl= AD.authenticate("super.admin", "Era@#123");
			
		} catch (LoginException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LOGGER.info("AD-->"+UserAvl);
		if(UserAvl) {
			try {
			EraviewDBconnection dbConn = new EraviewDBconnection();
			Connection conn = dbConn.getLocalDBConnection();
	
			InetAddress localhost = InetAddress.getLocalHost();
	
			LOGGER.info("System IP Address : " +(localhost.getHostAddress()).trim());
			LOGGER.info("Login Email ID : " +emailId);
			 
			Statement stmnt = conn.createStatement();
			ResultSet res = stmnt.executeQuery("select * from user_account where email_id = '" + emailId + "'");
	
			if(res.next()){ 
					String userName = res.getString(2);
					
					session.setAttribute("username", userName);
					session.setAttribute("roleId", res.getString(3));
					session.setAttribute("userId", res.getString(1));
					//session.setAttribute("emailId", emailId);
					
					Statement stmnt2 = conn.createStatement();
					String roleid = session.getAttribute("roleId").toString();
					
					ResultSet res2 = stmnt2.executeQuery("select * from  user_role a,access_group b where a.group_id = b.group_id and role_id='"+roleid +"'" );
	
					if(res2.next())
					{
						session.setAttribute("roleName", res2.getString(2));
						session.setAttribute("groupId", res2.getString(3));
					}
					LOGGER.info("roleName " + session.getAttribute("roleName").toString());
					LOGGER.info("Successfully Logged in with the " +emailId + " at "+ new java.util.Date());
					
					// To create individual user log files
					
					String folderPath = p.getProperty("logbackxmlpath");
					LOGGER.info("logback.xml path from properties file --> " + folderPath);

					//String directory = System.getenv("SERVER_HOME");
					String directory = "C:\\apache-tomcat-9.0.56";
					directory = directory.concat(folderPath);
					LOGGER.info("logback.xml full path --> " + directory);
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					System.out.println("Current Date" + cal.getTime());
						
					LoggerContext logcontext = (LoggerContext) LoggerFactory.getILoggerFactory();
					JoranConfigurator jc = new JoranConfigurator();
					jc.setContext(logcontext);
					logcontext.reset(); 	
					String currentdate = LocalDate.now().toString();	
					String logfileName = userName.concat("_").concat(currentdate);
					logcontext.putProperty("logFileName", logfileName);
					try {
						jc.doConfigure(directory);
					} catch (JoranException je) {
						je.printStackTrace();
						LOGGER.info(je.getMessage());
					} 
					
					LOGGER.debug("System IP Address : " +(localhost.getHostAddress()).trim());
					LOGGER.debug("Login Email ID : " +emailId);
					LOGGER.debug("roleName " + session.getAttribute("roleName").toString());
					String roleName = (String)request.getSession().getAttribute("roleName");
					String userId = (String)request.getSession().getAttribute("userId");
					String dasboardId = "1";
					// dash board content
					ConstructDashboard constructDashboard = new ConstructDashboard();
					Map<String, String> dashMap = constructDashboard.getDashboardContent(emailId,session, session.getId(),roleName,userId,dasboardId);
					if(dashMap==null || dashMap.isEmpty()){
						session.setAttribute("Message", "Please add reports to be displayed in the dashboard.");
					}else{
						session.setAttribute("dashMap", dashMap);
					}
					stmnt2.close();
					getServletContext().getRequestDispatcher("/Hyphenviewhome.jsp").forward(request, response);
				
			}
			
			}catch (Exception e) {
				LOGGER.info(e.getMessage());
				e.printStackTrace();
				getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			
			}
		}else{
			session.setAttribute("Message", "Invalid login details");
			LOGGER.info("Invalid login details");
			getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
		}
		}else {
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			Connection conn = dbConn.getLocalDBConnection();
	
			InetAddress localhost = InetAddress.getLocalHost();
	
			LOGGER.info("System IP Address : " +(localhost.getHostAddress()).trim());
			LOGGER.info("Login Email ID : " +emailId);
			 
			Statement stmnt = conn.createStatement();
			ResultSet res = stmnt.executeQuery("select * from user_account where email_id = '" + emailId + "'");
	
			if(res.next()){ 
				String passwordUser = encryption.encrypt(pwd);
				if(res.getString(5).equals(passwordUser)){
					
					String userName = res.getString(2);
					
					session.setAttribute("username", userName);
					session.setAttribute("roleId", res.getString(3));
					session.setAttribute("userId", res.getString(1));
					//session.setAttribute("emailId", emailId);
					
					Statement stmnt2 = conn.createStatement();
					String roleid = session.getAttribute("roleId").toString();
					
					ResultSet res2 = stmnt2.executeQuery("select * from  user_role a,access_group b where a.group_id = b.group_id and role_id='"+roleid +"'" );
	
					if(res2.next())
					{
						session.setAttribute("roleName", res2.getString(2));
						session.setAttribute("groupId", res2.getString(3));
					}
					LOGGER.info("roleName " + session.getAttribute("roleName").toString());
					LOGGER.info("Successfully Logged in with the " +emailId + " at "+ new java.util.Date());
					
					// To create individual user log files
					
					String folderPath = p.getProperty("logbackxmlpath");
					LOGGER.info("logback.xml path from properties file --> " + folderPath);

//					String directory = System.getenv("SERVER_HOME");
					String directory = "C:\\apache-tomcat-9.0.56";
					directory = directory.concat(folderPath);
					LOGGER.info("logback.xml full path --> " + directory);
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					System.out.println("Current Date" + cal.getTime());
						
					LoggerContext logcontext = (LoggerContext) LoggerFactory.getILoggerFactory();
					JoranConfigurator jc = new JoranConfigurator();
					jc.setContext(logcontext);
					logcontext.reset(); 	
					String currentdate = LocalDate.now().toString();	
					String logfileName = userName.concat("_").concat(currentdate);
					logcontext.putProperty("logFileName", logfileName);
					try {
						jc.doConfigure(directory);
					} catch (JoranException je) {
						je.printStackTrace();
						LOGGER.info(je.getMessage());
					} 
					
					LOGGER.debug("System IP Address : " +(localhost.getHostAddress()).trim());
					LOGGER.debug("Login Email ID : " +emailId);
					LOGGER.debug("roleName " + session.getAttribute("roleName").toString());
					String roleName = (String)request.getSession().getAttribute("roleName");
					String userId = (String)request.getSession().getAttribute("userId");
					String dasboardId = "1";
					// dash board content
					ConstructDashboard constructDashboard = new ConstructDashboard();
					Map<String, String> dashMap = constructDashboard.getDashboardContent(emailId,session, session.getId(),roleName,userId,dasboardId);
					if(dashMap==null || dashMap.isEmpty()){
						session.setAttribute("Message", "Please add reports to be displayed in the dashboard.");
					}else{
						session.setAttribute("dashMap", dashMap);
					}
					stmnt2.close();
					if(roleName.equalsIgnoreCase("ReportEndUser")) {
						getServletContext().getRequestDispatcher("/QueryOnlyReports.jsp").forward(request, response);
					}else
					getServletContext().getRequestDispatcher("/Hyphenviewhome.jsp").forward(request, response);
				}else{
					session.setAttribute("Message", "Invalid login details");
					LOGGER.info("Invalid login details");
					getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
				}
			}else{
				
				session.setAttribute("Message", "Invalid user name");
				LOGGER.info("Invalid User Name");
				getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
				}
			stmnt.close();
			conn.close();
		}catch(Exception e){
			LOGGER.info(e.getMessage());
			e.printStackTrace();
			getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
		}

		}
	}
}
