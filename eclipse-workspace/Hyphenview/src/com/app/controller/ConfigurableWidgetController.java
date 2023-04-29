package com.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.ReportWidgetDao;
import com.app.object.ReportWidget;

/**
 * Servlet implementation class QueryReportController
 */
@WebServlet("/ConfigurableWidgetController")
public class ConfigurableWidgetController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfigurableWidgetController() {
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
		LOGGER.info("In post method of ConfigurableWidgetController..");
		String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		String email = request.getParameter("email");
		LOGGER.info("email value --> " + email);
		String customQuery = request.getParameter("customQuery");
		LOGGER.info("customQuery in servlet --> " + customQuery);
		String widgetName = request.getParameter("widgetName");
		LOGGER.info("widgetName in servlet --> " + widgetName);
		String widgetType = request.getParameter("widgetType");
		LOGGER.info("widgetType in ConfigurableWidgetController --> " + widgetType);

		String widgetId = request.getParameter("widgetId");
		LOGGER.info("widgetId in servlet..." + widgetId);
		
		if(request.getParameter("Preview") != null){
			LOGGER.info("Preview button pressed...");
			LOGGER.info("Chart background color or cart rectangle color changed...");
			LOGGER.info("Redirecting to CreateChartsController...");
			getServletContext().getRequestDispatcher("/ConfigurableWidget.jsp").forward(request, response);
		}else if(request.getParameter("Save") != null){
			LOGGER.info("Save button pressed...");
			ReportWidget reportWidget = new ReportWidget();
			reportWidget.setWidgetName(widgetName);
			reportWidget.setWidgetType(widgetType);
			reportWidget.setDatabase(database);
			reportWidget.setSchema(schema);
			reportWidget.setEmail(email);
			reportWidget.setDefinedQuery(customQuery);
			
			ReportWidgetDao reportWidgetDao = new ReportWidgetDao();
			
			if(null == widgetId || widgetId.equalsIgnoreCase("null") || widgetId.isEmpty()){
				LOGGER.debug("Widget record insert triggered");
				reportWidgetDao.createReportWidget(reportWidget);
			}else{
				LOGGER.debug("Widget record update triggered");
				reportWidget.setWidgetId(widgetId);
				reportWidgetDao.updateReportWidgetById(reportWidget);
			}
			
			
			getServletContext().getRequestDispatcher("/ManageWidgets.jsp").forward(request, response);
		}
	}

}
