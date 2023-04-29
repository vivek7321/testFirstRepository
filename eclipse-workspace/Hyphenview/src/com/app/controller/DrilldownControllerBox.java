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
import com.app.object.ReportTemplate;
import com.google.gson.Gson;

/**
 * Servlet implementation class DrilldownController
 */
@WebServlet("/DrilldownControllerBox")
public class DrilldownControllerBox extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DrilldownControllerBox() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("DrilldownController's get method");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of DrilldownControllerBox servlet...");
	
		String query = request.getParameter("query");
		String databaseId = request.getParameter("databaseId");
		String emailId = request.getParameter("emailId");
		String customQuery = null;
		
		String temp = query.replace(query.substring(0, query.indexOf("from")-1), "select * ");
		
		// customised for Postgres view_642754852.incident
		/*String selectClause = "select \"ID\",\"NAME\",\"PRIORITY\",\"PHASEID\",\"STATUS\",\"CURRENTASSIGNMENT\",\"DISPLAYLABEL\",\"ENTITYSETTINGS\",\"REQUESTEDBYPERSON\",\"SOLVEDBYPERSON\",\"CLOSEDBYPERSON\","
				+ "\"EMSCREATIONTIME\",\"SOLVEDTIME\",\"CLOSETIME\",\"OWNEDBYPERSON\",\"FIRSTTOUCH\",\"COMPLETIONCODE\",\"NEXTTARGETTIME\",\"SLTAGGREGATEDSTATUS\"";
		String temp = query.replace(query.substring(0, query.indexOf("from")-1), selectClause);*/
		
	
		customQuery = temp;
		LOGGER.info("customQuery in drilldown --> " + customQuery);
		System.out.println("customQuery in drilldownBox --> " + customQuery);
		ReportTemplate reportTemplate = new ReportTemplate();
		reportTemplate.setDatabaseId(databaseId);
		reportTemplate.setDefinedQuery(customQuery);
		//reportTemplate.setSeries(series);
		//reportTemplate.setCategory(category);
		reportTemplate.setEmail(emailId);
							
		/*QueryDao queryDao = new QueryDao();
		ResultSet res = queryDao.executeDrilldownQuery(reportTemplate, emailId, series, category);*/
		
		LOGGER.debug("setting in session...");
		request.getSession().setAttribute("drilldowndata", reportTemplate);
		LOGGER.debug("set in session...");
		response.setContentType("application/text");
		response.getWriter().write(new Gson().toJson("success"));
	}

}
