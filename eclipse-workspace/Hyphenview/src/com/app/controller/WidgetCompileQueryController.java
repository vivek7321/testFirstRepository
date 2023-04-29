package com.app.controller;

import java.io.IOException;
import java.sql.ResultSet;

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
@WebServlet("/WidgetCompileQueryController")
public class WidgetCompileQueryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WidgetCompileQueryController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In get method of WidgetCompileQueryController servlet...");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of WidgetCompileQueryController servlet...");
		String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		LOGGER.info("schema --> " + schema);
		String email = request.getSession().getAttribute("emailId").toString();
		LOGGER.info("email value --> " + email);
		String customQuery = request.getParameter("customQuery");
		LOGGER.info("customQuery --> " + customQuery);
		
		QueryDao queryDao = new QueryDao();
		ResultSet output = queryDao.compileWidgetQuery(email, customQuery, database, schema);
		
		response.setContentType("application/json");
		response.getWriter().write(new Gson().toJson(output));
		
	}

}
