package com.app.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.ConstructDashboard;
import com.app.dao.ReportTemplateDao;
import com.app.object.ReportTemplate;

/**
 * Servlet implementation class QueryReportController
 */
@WebServlet("/QueryReportController")
public class QueryReportController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryReportController() {
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
		LOGGER.info("In post method of QueryReportController..");
		String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		String email = request.getParameter("email");
		LOGGER.info("email value --> " + request);
		String customQuery = request.getParameter("customQuery");
		LOGGER.info("customQuery in servlet --> " + customQuery);
		String reportType = request.getParameter("reportType");
		LOGGER.info("reportType in servlet --> " + reportType);
		String templateType = request.getParameter("templateType");
		LOGGER.info("templateType in servlet --> " + templateType);
		String chartType = request.getParameter("chartType");
		if(chartType == null || chartType.equalsIgnoreCase("null")){
			chartType="";
		}
		String chartName = request.getParameter("chartName");
		String dashboardId = "1";
		String theme ="";
		String drilldown = request.getParameter("drilldown");
		LOGGER.info("drilldown value in QueryReportController --> " + drilldown);
		if(null == drilldown || drilldown.isEmpty() || drilldown.equalsIgnoreCase("null")){
			drilldown = "no";
		}
		LOGGER.info("drilldown value in QueryReportController --> " + drilldown);
		int autoUpdateInterval = 0;
		String autoUpdate = request.getParameter("autoUpdate");
		LOGGER.info("autoUpdate value in QueryReportController --> " + autoUpdate);
		if(null != autoUpdate && !autoUpdate.isEmpty() && !autoUpdate.equalsIgnoreCase("null")){
			autoUpdateInterval = Integer.parseInt(request.getParameter("autoUpdate"));
		}
		LOGGER.info("autoUpdateInterval value in QueryReportController --> " + autoUpdateInterval);
		String bgcolor = request.getParameter("bgcolor");
		if(bgcolor == null || bgcolor.equalsIgnoreCase("null")){
			bgcolor="";
		}
		String rectcolor = request.getParameter("rectcolor");
		if(rectcolor == null || rectcolor.equalsIgnoreCase("null")){
			rectcolor="";
		}
		LOGGER.info("rectcolor value in QueryReportController --> " + rectcolor);

		String startDate = request.getParameter("startDate1");
		LOGGER.info("startDate value in QueryReportController --> " + startDate);
		String endDate = request.getParameter("endDate1");
		LOGGER.info("endDate value in QueryReportController --> " + endDate);
		String reportId = request.getParameter("reportId");
		String uploadLogo = request.getParameter("UploadLogo");
		LOGGER.info("uploadLogo value in QueryReportController --> " + uploadLogo);
		String timeperiod = request.getParameter("timeperiod");
		
		if(request.getParameter("Back") != null){
			LOGGER.info("Back button pressed...");
			request.setAttribute("customQuery", customQuery);
			getServletContext().getRequestDispatcher("/ShowQuery.jsp").forward(request, response);
		}else if(request.getParameter("Preview") != null){
			LOGGER.info("Preview button pressed...");
			LOGGER.info("Chart background color or cart rectangle color changed...");
			LOGGER.info("Redirecting to CreateChartsController...");
			getServletContext().getRequestDispatcher("/CreateChartsControllerNew").forward(request, response);
		}else if(request.getParameter("Save") != null){
			LOGGER.info("Save button pressed...");
			LOGGER.info("setReportType---"+reportType+"---uploadLogo->"+uploadLogo);
			ReportTemplate reportTemplate = new ReportTemplate();
			reportTemplate.setReportTemplateName(chartName);
			reportTemplate.setReportType(reportType);
			reportTemplate.setTemplateType(templateType);
			reportTemplate.setChartType(chartType);
			reportTemplate.setDefinedQuery(customQuery);
			reportTemplate.setEmail(email);
			reportTemplate.setDatabase(database);
			reportTemplate.setSchema(schema);
			reportTemplate.setDrilldown(drilldown);
			reportTemplate.setTheme(theme);
			reportTemplate.setDashboardId(dashboardId);
			reportTemplate.setStartDate(startDate);
			reportTemplate.setEndDate(endDate);
			reportTemplate.setuploadLogoStatus(uploadLogo);
			if(reportType.equalsIgnoreCase("chart")){ 
				LOGGER.info("before setAutoUpdateInterval...");
				reportTemplate.setAutoUpdateInterval(autoUpdateInterval);
			}else{
				reportTemplate.setAutoUpdateInterval(0);
			}
			reportTemplate.setBgcolor(bgcolor);
			reportTemplate.setRectcolor(rectcolor);
			reportTemplate.setTimePeriod(timeperiod);
			
			ReportTemplateDao templateDao = new ReportTemplateDao();
			int maxOrder = templateDao.fetchMaxDisplayOrder(email);
			reportTemplate.setDisplayOrder(maxOrder+1);
			
			if(null == reportId || reportId.isEmpty()){
				LOGGER.info("inside reportId check-if");
				templateDao.createReportTemplate(reportTemplate,request);
			}else{
				LOGGER.info("inside reportId check-else");
				reportTemplate.setReportTemplateId(reportId);
				templateDao.updateReportTemplateById(reportTemplate);
			}
			
			// update dash board content
			/*HttpSession session = request.getSession();
			ConstructDashboard constructDashboard = new ConstructDashboard();
			Map<String, String> dashMap = constructDashboard.getDashboardContent(email, session.getId());
			if(dashMap==null || dashMap.isEmpty()){
				session.setAttribute("Message", "Please add reports to be displayed in the dashboard.");
			}else{
				session.setAttribute("dashMap", dashMap);
			} */
			
			getServletContext().getRequestDispatcher("/QueryReports.jsp").forward(request, response);
		}
	}

}
