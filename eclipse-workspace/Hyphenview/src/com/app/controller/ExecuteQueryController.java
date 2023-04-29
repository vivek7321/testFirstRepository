package com.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.QueryDao;
import com.app.object.Output;
import com.google.gson.Gson;

/**
 * Servlet implementation class QueryElementsController
 */
@WebServlet("/ExecuteQueryController")
public class ExecuteQueryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExecuteQueryController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*LOGGER.info("In get method of servlet...");
		String database = request.getParameter("database");
		FetchElementsDao fetchElementsDao = new FetchElementsDao();
		List<String> tablesList = fetchElementsDao.fetchTables(database);
		request.setAttribute("tablesList", tablesList);
		
		getServletContext().getRequestDispatcher("/pages/selectQueryOptions.jsp").forward(request, response);*/
//		response.sendRedirect("/QueryTest/pages/selectQueryOptions.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of servlet...");
		String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		String action = request.getParameter("action");
		LOGGER.info("schema --> " + schema+"database-->"+database);
		String email = request.getParameter("email");
		LOGGER.info("action value --> " + action);
		LOGGER.info("email value --> " + email);
		String customQueryADP = request.getParameter("customQueryADP");
		LOGGER.info("customQueryADP --> " + customQueryADP);
		String startDate = request.getParameter("startDate");
		LOGGER.info("startDate --> " + startDate);
		String endDate = request.getParameter("endDate");
		LOGGER.info("endDate --> " + endDate);
		String uploadLogo = request.getParameter("UploadLogo");
		LOGGER.info("uploadLogo value in QueryReportController --> " + uploadLogo);
		String customQuery = request.getParameter("customQuery");
		if(startDate !=null || endDate !=null) {
			String query1 = customQuery.replace("{?StartDate}", startDate);
			String query2 = query1.replace("{?EndDate}", endDate);
			System.out.println("customQuery inside ExecuteQueryController-->"+query2);
			customQuery = query2;
		}
		LOGGER.info("customQuery --> " + customQuery);
		Output output= null;
		
		QueryDao queryDao = new QueryDao();
		if(action.equalsIgnoreCase("ADP")) {
			output = queryDao.compileQuery(email, customQueryADP, database, schema);
		}	
		else {output = queryDao.compileQuery(email, customQuery, database, schema);
		}
		
		
		response.setContentType("application/json");
		response.getWriter().write(new Gson().toJson(output));
		
		/*response.setContentType("application/text");
		response.getWriter().write(output.getMessage());*/
	}

}
