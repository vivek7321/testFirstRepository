package com.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.FetchElementsDao;
import com.google.gson.Gson;

/**
 * Servlet implementation class QueryElementsController
 */
@WebServlet("/QueryElementsController")
public class QueryElementsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryElementsController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In get method of servlet...");
		/*String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		String email = request.getParameter("email");
		FetchElementsDao fetchElementsDao = new FetchElementsDao();
		List<String> tablesList = fetchElementsDao.fetchTables(email, database, schema);
		request.setAttribute("tablesList", tablesList);
		
		getServletContext().getRequestDispatcher("/BuildQuery.jsp").forward(request, response);*/
//		response.sendRedirect("/QueryTest/pages/selectQueryOptions.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of QueryElementsController servlet...");
		String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		String email = request.getParameter("email");
		LOGGER.info("email + database + schema --> " + email + "##" + database + "##" + schema);
		FetchElementsDao fetchElementsDao = new FetchElementsDao();
		
		if(request.getParameter("custom") != null){
			getServletContext().getRequestDispatcher("/ShowQuery.jsp").forward(request, response);
		}else{
			LOGGER.info("tablesList in post --> " + request.getParameter("tablesList"));
			String tablenames = request.getParameter("tablesList");
			if(tablenames == null || tablenames.equalsIgnoreCase("")){
				List<String> tablesList = fetchElementsDao.fetchTables(email, database, schema);
				request.setAttribute("tablesList", tablesList);
				
				LOGGER.info("fetched tableslist in servlet--> " + tablesList);
				getServletContext().getRequestDispatcher("/BuildQuery.jsp").forward(request, response);
			}
			else {
				String[] tablesList = tablenames.split(",");
				LOGGER.info("tablesList in post servlet --> " + tablesList);
				if(tablesList != null && tablesList.length > 0){
					List<String> tempList = null;
					List<String> columnsList = new ArrayList<String>();
					LOGGER.info("tablesList.length --> " + tablesList.length);
					for(int i=0; i < tablesList.length; i++){
						String table = tablesList[i];
						String tableName = null;
						if(table.contains(".")){
							String tokens[] = table.split("\\.");
							tableName = tokens[tokens.length-1];
						}else{
							tableName = table;
						}
						LOGGER.info("Table name passed --> " + tableName);
						tempList = fetchElementsDao.fetchColumns(email, database, schema, tableName);
						LOGGER.info(tablesList[i] + " tempList --> " + tempList);
						columnsList.addAll(tempList);
					}
					LOGGER.info("columnsList in post servlet --> " + columnsList);
					response.setContentType("application/json");
					response.getWriter().write(new Gson().toJson(columnsList));
				}
			}
		}
		
	}

}
