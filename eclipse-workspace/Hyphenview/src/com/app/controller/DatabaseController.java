package com.app.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.constant.QueryConstants;
import com.app.dao.DatabaseDao;
import com.app.dao.FetchElementsDao;
import com.google.gson.Gson;

/**
 * Servlet implementation class DatabaseController
 */
@WebServlet("/DatabaseController")
public class DatabaseController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DatabaseController() {
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
		LOGGER.info("In post method of DatabaseController servlet...");
		String rdbms = request.getParameter("rdbms");
		LOGGER.info("rdbms --> " + rdbms);
		String userName = request.getParameter("userName");
		LOGGER.info("userName --> " + userName);
		
		List<String> schemaList = null;
		if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL) || rdbms.equalsIgnoreCase(QueryConstants.POSTGRESQL)){
			DatabaseDao databaseDao = new DatabaseDao();
			schemaList = databaseDao.fetchSchemaList(userName, rdbms);
		}else if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL) || rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER) || rdbms.equalsIgnoreCase(QueryConstants.VERTICA)){
			FetchElementsDao elementsDao = new FetchElementsDao();
			schemaList = elementsDao.fetchSchemasForDatabase(rdbms, userName);
		}
		LOGGER.info("schemaList --> " + schemaList);
		
		response.setContentType("application/json");
		response.getWriter().write(new Gson().toJson(schemaList));
	}

}
