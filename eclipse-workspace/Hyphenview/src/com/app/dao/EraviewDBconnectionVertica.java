package com.app.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.AESCrypt;
import com.app.constant.QueryConstants;
import com.crystaldecisions.sdk.occa.report.data.ConnectionInfoKind;
import com.crystaldecisions.sdk.occa.report.data.ConnectionInfos;
import com.crystaldecisions.sdk.occa.report.data.IConnectionInfo;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;

import era.database.connection;


public class EraviewDBconnectionVertica {

	// Get Database Connection for SQL Server

	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnectionVertica.class);

	public java.sql.Connection getDBConnection(String database, String schema) throws Exception {

		String dbURL =null;
		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		p.load(is);

		java.sql.Connection conn = null;
		try {
			String dbClass = p.getProperty(database + ".dbClass"); 
			String tempURL = p.getProperty(database + ".dburl");
			if(schema != null && !database.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				dbURL = tempURL.concat("/").concat(schema);
			}else{
				dbURL = tempURL;
			}
			
			String username = p.getProperty(database + ".dbusername");
			String password = p.getProperty(database + ".dbpassword");
			AESCrypt aesCrypt = new AESCrypt();
			String decryptedPwd = aesCrypt.decrypt(password);
			Class.forName(dbClass);
			
			LOGGER.info("DB URL : " +dbURL);
			conn = DriverManager.getConnection(dbURL, username, decryptedPwd);

			LOGGER.info("Successfully Connected to Database");

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception in DB Connection" + e.getMessage());
		}
		return conn;
	}
	
	
	public Connection getLocalDBConnection() throws Exception {

		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		p.load(is);

		Connection conn = null;
		try {
			String rdbms = p.getProperty("dbRdbms");
			String domain = p.getProperty("dbservername");
			String port = p.getProperty("dbport");
			String schema = p.getProperty("databaseName");
			String username = p.getProperty("dbusername");
			String password = p.getProperty("dbpassword");
			
			AESCrypt aesCrypt = new AESCrypt();
			String decryptedPwd = aesCrypt.decrypt(password);
			
			//LOGGER.info(domain + " " + port + " " + schema + " " +  username );
			if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				//conn = connection.getSqlServerConnection(domain, port, schema, username, decryptedPwd,data);
				try {
					String dbClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; 
					String dbURL = "jdbc:sqlserver://" + domain + ":" + port + ";databaseName=" + schema ;
					Class.forName(dbClass);
					conn = DriverManager.getConnection(dbURL, username, decryptedPwd);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
				conn = connection.getMySqlConnection(domain, port, schema, username, decryptedPwd);
			}
			//System.out.println("Hello"+decryptedPwd);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception in DB Connection" + e.getMessage());
		}
		return conn;
	}

	public Connection getUserDBConnection(String email, String database, String schema) throws Exception {

		String dbURL =null;

		Connection conn = null;
		Connection userConnection = null;
		PreparedStatement stmnt = null;
		String dbClass = null; 
		String domain = null;
		String port = null;
		String username = null;
		String password = null;
		try {
			String dbDetailsQuery = "select db.domain_name, db.db_port, db.db_user_name, db.db_password "
					+ "from database_details db, user_account u "
					+ "where db.rdbms_name = ? and db.db_schema_name = ? "
					+ "and u.email_id = ? and u.customer_id = db.customer_id";
			conn = this.getLocalDBConnection();
			stmnt = conn.prepareStatement(dbDetailsQuery);
			stmnt.setString(1, database);
			if(database.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL) || database.equalsIgnoreCase(QueryConstants.VERTICA)){
				stmnt.setString(2, "");
			}else{
				stmnt.setString(2, schema);
			}
			stmnt.setString(3, email);
			ResultSet res = stmnt.executeQuery();
			while(res.next()){
				domain = res.getString(1);
				port = res.getString(2);
				username = res.getString(3);
				password = res.getString(4);
			}
			
			// decrypt the database password
			AESCrypt aesCrypt = new AESCrypt();
			String decryptedPwd = aesCrypt.decrypt(password);
			
			if(database.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
				dbClass = "com.mysql.jdbc.Driver";
				dbURL = ("jdbc:mysql://").concat(domain).concat(":").concat(port).concat("/").concat(schema);
			}else if(database.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				dbClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				dbURL = ("jdbc:sqlserver://").concat(domain).concat(":").concat(port);
			}else if(database.equalsIgnoreCase(QueryConstants.POSTGRESQL)){
				dbClass = "org.postgresql.Driver";
				dbURL = ("jdbc:postgresql://").concat(domain).concat(":").concat(port).concat("/").concat(schema);
			}else if(database.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				dbClass = "com.ibm.db2.jcc.DB2Driver";
				dbURL = ("jdbc:db2://").concat(domain).concat(":").concat(port).concat("/").concat(schema);
			}
			else if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
			dbClass = "com.vertica.jdbc.Driver";
			schema ="";
			dbURL = ("jdbc:vertica://").concat(domain).concat(":").concat(port).concat("/").concat(schema);
		}
			
			Class.forName(dbClass);
			
			LOGGER.info("DB URL : " +dbURL );
			userConnection = DriverManager.getConnection(dbURL, username, decryptedPwd);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception in DB Connection" + e.getMessage());
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
		}
		return userConnection;
	}
	
	public Connection getUserDB2ConnForElements(String email, String database) throws Exception {

		String dbURL =null;

		Connection conn = null;
		Connection userConnection = null;
		PreparedStatement stmnt = null;
		String dbClass = null; 
		String domain = null;
		String port = null;
		String username = null;
		String password = null;
		try {
			String dbDetailsQuery = "select db.domain_name, db.db_port, db.db_user_name, db.db_password, db.db_schema_name "
					+ "from database_details db, user_account u "
					+ "where db.rdbms_name = ? "
					+ "and u.email_id = ? and u.customer_id = db.customer_id";
			conn = this.getLocalDBConnection();
			stmnt = conn.prepareStatement(dbDetailsQuery);
			stmnt.setString(1, database);
			stmnt.setString(2, email);
			ResultSet res = stmnt.executeQuery();
			while(res.next()){
				domain = res.getString(1);
				port = res.getString(2);
				username = res.getString(3);
				password = res.getString(4);
				database = res.getString(5);
			}
			
			// decrypt the database password
			AESCrypt aesCrypt = new AESCrypt();
			String decryptedPwd = aesCrypt.decrypt(password);
			
			dbClass = "com.ibm.db2.jcc.DB2Driver";
			dbURL = ("jdbc:db2://").concat(domain).concat(":").concat(port).concat("/").concat(database);
			
			Class.forName(dbClass);
			
			LOGGER.info("DB URL : " +dbURL );
			userConnection = DriverManager.getConnection(dbURL, username, decryptedPwd);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception in DB Connection" + e.getMessage());
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
		}
		return userConnection;
	}

	
	
public IConnectionInfo getDBConnectionInfos(IConnectionInfo connectionInfo) throws Exception {
		
		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		p.load(is);
		//getting values from config.properties file	
		String rdbms = p.getProperty("rpt.rdbms");
		String SERVERNAME = p.getProperty("rpt.dbservername");
		String DATABASE_NAME = p.getProperty("rpt.databaseName");
		String PORT = p.getProperty("rpt.dbport");
		String username = p.getProperty("rpt.dbusername");
		String password = p.getProperty("rpt.dbpassword");
		
		// Decrypt the password
		AESCrypt aesCrypt = new AESCrypt();
		String decryptedPwd = aesCrypt.decrypt(password);
		
		String URI = null;
		
		if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
			URI = "!com.microsoft.sqlserver.jdbc.SQLServerDriver!jdbc:sqlserver://" + SERVERNAME + ":" + PORT
				+ ";databaseName=" + DATABASE_NAME;
		}else if(rdbms.equalsIgnoreCase(QueryConstants.POSTGRESQL)){
			URI = "!org.postgresql.Driver!jdbc:postgresql://" + SERVERNAME + ":" + PORT
					+ ";databaseName=" + DATABASE_NAME;
		}else if(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
			URI = "!com.ibm.db2.jcc.DB2Driver!jdbc:db2://" + SERVERNAME + ":" + PORT
					+ ";databaseName=" + DATABASE_NAME;
		}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
			URI = "!com.mysql.jdbc.Driver!jdbc:mysql://" + SERVERNAME + ":" + PORT
					+ ";databaseName=" + DATABASE_NAME;
		}
		
		// + ";integratedSecurity=true;"
		
		String DATABASE_DLL = "crdb_jdbc.dll";

		// IConnectionInfo connectionInfo = table.getConnectionInfo();
		PropertyBag propertyBag = connectionInfo.getAttributes();
		propertyBag.clear();
		// Overwrite any existing properties with updated values.
		propertyBag.put("Trusted_Connection", "true");
		propertyBag.put("Server Name", SERVERNAME); // Optional property.
		propertyBag.put("Database Name", DATABASE_NAME);
		propertyBag.put("Server Type", "JDBC (JNDI)");
		propertyBag.put("URI", URI);
		propertyBag.put("Use JDBC", "true");
		propertyBag.put("Database DLL", DATABASE_DLL);
		connectionInfo.setAttributes(propertyBag);
		ConnectionInfos connInfos = new ConnectionInfos();
		System.out.println("In EravievConnectionDB class");
		// Set database username and password.
		// NOTE: Even if these the username and password properties don't change when
		// switching databases, the
		// database password is *not* saved in the report and must be set at runtime if
		// the database is secured.
		connectionInfo.setUserName(username);
		connectionInfo.setPassword(decryptedPwd);
		connectionInfo.setKind(ConnectionInfoKind.SQL);
		return connectionInfo;
	}
public static void main(String args[]) {
	
	String dbURL =null;

	Connection conn = null;
	Connection userConnection = null;
	PreparedStatement stmnt = null;
	String dbClass = null; 
	String domain = null;
	String port = null;
	String username = null;
	String password = null;
	
	dbClass = "com.vertica.jdbc.Driver";
	//dbURL = ("jdbc:vertica://").concat(domain).concat(":").concat(port).concat("/").concat(schema);
	try {
		
		Properties myProp = new Properties();
        myProp.put("user", "verticadba");
        myProp.put("password", "vertica@1234");
        myProp.put("loginTimeout", "30");
        myProp.put("ConnectionLoadBalance", "1");
		
		Class.forName(dbClass);	
	//conn = DriverManager.getConnection( "jdbc:vertica://192.168.0.122:5433/verticadba", 
			//myProp);
	conn = DriverManager.getConnection("jdbc:vertica://192.168.0.122:5433/verticadba?user=verticadba&password=vertica@1234");
	
	System.out.println("Connec" + conn);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}

	
}
