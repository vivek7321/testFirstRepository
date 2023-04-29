package com.app.controller;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.AESCrypt;
import com.app.constant.QueryConstants;
import com.app.dao.CustomerDao;
import com.app.dao.DatabaseDao;
import com.app.object.Customer;
import com.app.object.DatabaseDetails;

import era.database.connection;

/**
 * Servlet implementation class AddDatabaseController
 */
@WebServlet("/AddDatabaseController")
public class AddDatabaseController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddDatabaseController() {
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
		LOGGER.info("In the Post method of  AddDatabaseController..");
		String rdbms = request.getParameter("rdbms");
		String serverName = request.getParameter("serverName");
		String port = request.getParameter("port");
		String schema = request.getParameter("schema");
		String dbUserName = request.getParameter("dbUserName");
		String dbPassword = request.getParameter("dbPassword");
		String email = request.getParameter("email");
		
		//check whether the db connection details are valid
		Connection conn = null;
		try{
			if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				LOGGER.debug("trying to connect with MS SQL...");
				conn = connection.getSqlServerConnection(serverName, port, schema, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
				LOGGER.debug("trying to connect with MY SQL...");
				conn = connection.getMySqlConnection(serverName, port, schema, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.POSTGRESQL)){
				LOGGER.debug("trying to connect with PostgreSQL Server...");
				conn = connection.getPostgreSqlConnection(serverName, port, schema, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				LOGGER.debug("trying to connect with DB2 Server...");
				conn = connection.getDB2Connection(serverName, port, schema, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.VERTICA)){
				LOGGER.debug("trying to connect with VERTICA...");
				conn = connection.getVerticaConnection(serverName, port, schema, dbUserName, dbPassword);
			}
		}catch(Exception e){
			LOGGER.debug("Exception trying to connect with DB...");
			e.printStackTrace();
		}
		
		if(null == conn){
			LOGGER.debug("DB connection is null in AddDatabaseController...");
			request.setAttribute("Message", "Invalid Database Details. Kindly provide valid values.");
			getServletContext().getRequestDispatcher("/AddDatabase.jsp").forward(request, response);
		}else{
			LOGGER.debug("DB connection is not null in AddDatabaseController...");
			LOGGER.info("email --> " + email);
			// fetch the customer id based on the email/username
			CustomerDao customerDao = new CustomerDao();
			Customer customer = customerDao.fetchCustomerByEmail(email);
			LOGGER.info("customer -- > " + customer);
			// create an entry in the database details table
			DatabaseDetails dbDetails = new DatabaseDetails();
			dbDetails.setCustomerId(customer.getCustomerId());
			dbDetails.setDomainName(serverName);
			
			try {
				// encrypt the database password before storing in database
				AESCrypt aesCrypt = new AESCrypt();
				String encryptedPwd = aesCrypt.encrypt(dbPassword);
				dbDetails.setPassword(encryptedPwd);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try{
					if(null != conn){conn.close();}
				}catch(Exception e){ }
			}
			
			dbDetails.setPortNumber(port);
			dbDetails.setRdbms(rdbms);
			dbDetails.setSchemaName(schema);
			dbDetails.setUserName(dbUserName);
			
			DatabaseDao databaseDao = new DatabaseDao();
			DatabaseDetails databaseDetails = databaseDao.fetchDbDetails(rdbms, schema, email);
			if(null == databaseDetails || null == databaseDetails.getCustomerId()){
				int i = databaseDao.createDatabase(dbDetails);
				
				if(i>0){
					request.setAttribute("Message", "Database added successfully.");
					getServletContext().getRequestDispatcher("/selectrdbms.jsp").forward(request, response);
				}else{
					request.setAttribute("Message", "Database is not added successfully.");
					getServletContext().getRequestDispatcher("/AddDatabase.jsp").forward(request, response);
				}
			}else{
				LOGGER.debug("Schema already exists...");
				request.setAttribute("Message", "Database already exists.");
				getServletContext().getRequestDispatcher("/AddDatabase.jsp").forward(request, response);
			}
		}
		
	}

}
