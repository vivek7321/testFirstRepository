package com.app.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.common.AESCrypt;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.constant.QueryConstants;

import era.database.connection;

/**
 * Servlet implementation class DBConfigController
 */
@WebServlet("/DBConfigController")
public class DBConfigController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DBConfigController() {
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
		LOGGER.info("In the Post method of  DBConfigController..");
		String rdbms = request.getParameter("rdbms");
		String serverName = request.getParameter("serverName");
		String port = request.getParameter("port");
		String schema = request.getParameter("schema");
		String dbUserName = request.getParameter("dbUserName");
		String dbPassword = request.getParameter("dbPassword");
		String dbUrl = null;
		String driverClass = null;
		String encryptedPwd = null;
		//check whether the db connection details are valid
		Connection conn = null;
		try{
			if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				LOGGER.debug("trying to connect with MS SQL...");
				conn = connection.getSqlServerConnection(serverName, port, null, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
				LOGGER.debug("trying to connect with MY SQL...");
				conn = connection.getMySqlConnection(serverName, port, schema, dbUserName, dbPassword);
			}
		}catch(Exception e){
			LOGGER.debug("Exception trying to connect with DB...");
			e.printStackTrace();
		}
		try {
			// encrypt the database password before storing in database
			AESCrypt aesCrypt = new AESCrypt();
			encryptedPwd = aesCrypt.encrypt(dbPassword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(null == conn){
			LOGGER.debug("DB connection is null in DBConfigController...");
			request.setAttribute("Message", "Invalid Database Details. Kindly provide valid values.");
			getServletContext().getRequestDispatcher("/DBConfiguration.jsp").forward(request, response);
		}else{
			LOGGER.debug("DB connection is not null in AddDatabaseController...");
			try {
				String directory = System.getenv("SERVER_HOME");
				String configFilePath = directory+"/webapps/Hyphenview/WEB-INF/classes/properties/config.properties";
				LOGGER.debug("configFilePath... --> " + configFilePath);
				PropertiesConfiguration pc = new PropertiesConfiguration(configFilePath);
				pc.setProperty("dbRdbms", rdbms);
				pc.setProperty("dbusername", dbUserName);
				pc.setProperty("dbpassword", encryptedPwd);
				if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
					dbUrl = "jdbc:sqlserver://"+serverName+":"+port;
					driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
					dbUrl = "jdbc:mysql://"+serverName+":"+port+"/"+schema;
					driverClass = "com.mysql.jdbc.Driver";
				}
				pc.setProperty("dburl", dbUrl);
				pc.setProperty("dbport", port);
				pc.setProperty("dbservername", serverName);
				pc.setProperty("databaseName", schema);
				pc.save();
				
				// execute the script for tables
				String sqlFilePath = null;
				if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
					sqlFilePath = directory + "/webapps/Hyphenview/WEB-INF/classes/properties/Hyphenview.sql";
				}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
					sqlFilePath = directory + "/webapps/Hyphenview/WEB-INF/classes/properties/Hyphenview-MYSQL.sql";
				}
				LOGGER.debug("sqlFilePath... --> " + sqlFilePath);
				executeSql(driverClass, sqlFilePath, dbUrl, dbUserName, dbPassword);
			} catch (ConfigurationException e) {
				e.printStackTrace();
				LOGGER.debug(e.getMessage());
			}finally{
				try{
					if(null != conn){conn.close();}
				}catch(Exception e){ }
			}
			getServletContext().getRequestDispatcher("/RptDbConfiguration.jsp").forward(request, response);
		}
	}
	
	private void executeSql(String driverClass, String sqlFilePath, String dbUrl, String dbUserName, String dbPwd) {
	    final class SqlExecuter extends SQLExec {
	        public SqlExecuter() {
	            Project project = new Project();
	            project.init();
	            setProject(project);
	            setTaskType("sql");
	            setTaskName("sql");
	        }
	    }

	    SqlExecuter executer = new SqlExecuter();
	    executer.setSrc(new File(sqlFilePath));
	    executer.setDriver(driverClass);
	    executer.setPassword(dbPwd);
	    executer.setUserid(dbUserName);
	    executer.setUrl(dbUrl);
	    executer.execute();
	}

}
