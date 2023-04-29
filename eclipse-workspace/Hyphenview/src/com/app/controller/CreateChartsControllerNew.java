package com.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.BuildAreaChart;
import com.app.common.BuildBarChart;
import com.app.common.BuildLineChart;
import com.app.common.BuildPieChart;
import com.app.common.BuildStackedAreaChart;
import com.app.common.BuildStackedBarChart;
import com.app.common.BuildStackedColumnChart;
import com.app.common.BuildTimeSeriesChart;
import com.app.dao.FetchElementsDao;
import com.app.dao.QueryDao;
import com.app.object.ReportTemplate;
import com.hyjavacharts.chart.Highchart;


/**
 * Servlet implementation class CreateChartsController
 */
@WebServlet("/CreateChartsControllerNew")
public class CreateChartsControllerNew extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateChartsControllerNew() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In get method of CreateChartsController...");
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of CreateChartsController servlet...");
		String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		String email = request.getParameter("email");
		LOGGER.info("email value --> " + email);
		String databaseId = request.getParameter("databaseId");
		
		String customQuery = request.getParameter("customQuery");
		String reportType = request.getParameter("reportType");
		String templateType = request.getParameter("templateType");
		String autoUpdate = request.getParameter("autoUpdate");
		LOGGER.info("reportType in servlet --> " + reportType);
		String reportId = request.getParameter("reportId");
		System.out.println(reportId);
		String chartType = request.getParameter("chartType");
		String dashboardId = "1";
		String folderName = (String) request.getParameter("sessionId");
		LOGGER.info("folderName -- sessionid --> " + folderName);
		String chartName = request.getParameter("chartName");
		//String rectcolor = request.getParameter("rectcolor");
		//LOGGER.info("rectcolor -- CreateChartsController --> " + rectcolor);
		/*Month Name 07 Mar 19 NO NEED TO ADD HERE */
		String timeperiod = request.getParameter("timeperiod");
		LOGGER.info("timeperiod -- CreateChartsController --> " + timeperiod);
		String theme = request.getParameter("theme");
		String newtheme = request.getParameter("newtheme");
		System.out.println("Theme in Param" +theme + "new" + newtheme);
		String startDate = request.getParameter("startDate1");
		LOGGER.info("startDate value in CreateChartsControllerNew --> " + startDate);
		String endDate = request.getParameter("endDate1");
		LOGGER.info("endDate value in CreateChartsControllerNew --> " + endDate);
		String uploadLogo = request.getParameter("UploadLogo");
		LOGGER.info("uploadLogo value in CreateChartsControllerNew --> " + uploadLogo);
		
		
		
		if(request.getParameter("Back") != null){
			LOGGER.info("Back button pressed...");
			FetchElementsDao fetchElementsDao = new FetchElementsDao();
			List<String> tablesList = fetchElementsDao.fetchTables(email, database, schema);
			/*String tables[] = (String[])request.getAttribute("selectedTabs");*/
			String tables[] = (String[])request.getParameterValues("selectedList");
			LOGGER.info("tables --> " + tables);
			List<String> columnsList = new ArrayList<String>();
			if(null != tables && tables.length != 0){
				for(int i = 0; i < tables.length; i++){
					String table = tables[i];
					if(tablesList.contains(table)){
						tablesList.remove(table);
					}
					String tableName = null;
					if(table.contains(".")){
						String tokens[] = table.split("\\.");
						tableName = tokens[tokens.length-1];
					}else{
						tableName = table;
					}
					
					List<String> tempList = null;
						tempList = fetchElementsDao.fetchColumns(email, database, schema, tableName);
						LOGGER.info(tables[i] + " tempList --> " + tempList);
						columnsList.addAll(tempList);
				}
			}
			request.setAttribute("tablesList", tablesList);
			request.setAttribute("preSelectedColsList", columnsList);
			getServletContext().getRequestDispatcher("/BuildQuery.jsp").forward(request, response);
		}else{
			LOGGER.info("Show Report button pressed...");
		
			QueryDao queryDao = new QueryDao();
		//	ResultSet res = queryDao.executeQuery(email, customQuery, database, schema);
			
			Connection conn = null;
			ResultSet res = null;
			if(startDate !=null || endDate !=null) {
				String query1 = customQuery.replace("{?StartDate}", startDate);
				String query2 = query1.replace("{?EndDate}", endDate);
				customQuery = query2;
			}
			System.out.println("customQuery in CreateChartsControllerNew-->"+customQuery);
			Map<String, Object> outputMap = queryDao.executeQuery(email, customQuery, database, schema);
			Iterator<Map.Entry<String, Object>> entries = outputMap.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, Object> entry = entries.next();
				if(entry.getKey().equalsIgnoreCase("connection")){
					conn = (Connection)entry.getValue();
				}else if(entry.getKey().equalsIgnoreCase("resultset")){
					res = (ResultSet)entry.getValue();
				}
			}
			
			if(reportType.equalsIgnoreCase("chart")){
				List<String> xList = new ArrayList<String>();
				List<String> yList = new ArrayList<String>();
				List<String> tempList = null;
				List<List<String>> dataList = null;
				try {
					if(res.getMetaData().getColumnCount() == 2){
						while(res.next())
						{
							xList.add(res.getString(1));
							yList.add(res.getString(2));
						}
					}else if(res.getMetaData().getColumnCount() == 3){
						while(res.next())
						{
							if(!yList.contains(res.getString(1))){
								yList.add(res.getString(1));
								if(null == dataList){
									dataList = new ArrayList<List<String>>();
								}
								if(null != tempList){
									dataList.add(tempList);
									tempList = null;
								}
							}
							if(!xList.contains(res.getString(2))){
								xList.add(res.getString(2));
							}
							if(null == tempList){
								tempList = new ArrayList<String>();
							}
							tempList.add(res.getString(3));
						}
						if(null == dataList){
							dataList = new ArrayList<List<String>>();
						}
						if(null != tempList){
							dataList.add(tempList);
							tempList = null;
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					try{
						if(null != res){res.close();}
					}catch(Exception e){}
					try{
						if(null != conn){conn.close();}
					}catch(Exception e){}
				}
				ReportTemplate reportTemplate = new ReportTemplate();
				reportTemplate.setChartType(chartType);
				reportTemplate.setReportTemplateName(chartName);
				reportTemplate.setAutoUpdateInterval(Integer.parseInt(autoUpdate));
				//reportTemplate.setRectcolor(rectcolor);
				reportTemplate.setTimePeriod(timeperiod);
				reportTemplate.setEmail(email);
				reportTemplate.setDatabase(database);
				reportTemplate.setSchema(schema);
				reportTemplate.setDefinedQuery(customQuery);
				reportTemplate.setReportTemplateId(reportId);
				reportTemplate.setDashboardId(dashboardId);
				System.out.println("startDate in CreateChartsControllerNew-->"+startDate);
				System.out.println("endDate in CreateChartsControllerNew-->"+endDate);
				reportTemplate.setStartDate(startDate);
				reportTemplate.setEndDate(endDate);
				
				if (theme==null)
				{
					theme = "DARK_UNICA";
				}
				reportTemplate.setTheme(theme);
		    	String globalOptionsJs = "";
		    	String chartOptionsJs = "";
				Highchart hc = null;
		    	
    	
				if (chartType.equalsIgnoreCase("Pie"))
				{
					BuildPieChart pie = new BuildPieChart();
					try {
						String chartOptionsJsArea = pie.configure(reportTemplate, "ChartDiv");
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}


				}
				if (chartType.equalsIgnoreCase("Line"))
				{
					
					BuildLineChart line	 = new BuildLineChart();
					try {
						String chartOptionsJsArea = line.configure(reportTemplate, "ChartDiv");
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}


				
				}
				if (chartType.equalsIgnoreCase("Area")) {
					BuildAreaChart area = new BuildAreaChart();
					try {
						String chartOptionsJsArea = area.configure(reportTemplate, "ChartDiv");
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (chartType.equalsIgnoreCase("Stacked Area")) {
					BuildStackedAreaChart area = new BuildStackedAreaChart();
					try {
						String chartOptionsJsArea = area.configure(reportTemplate, "ChartDiv");
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}					
				
				if (chartType.equalsIgnoreCase("Bar"))
				{
					BuildBarChart bar = new BuildBarChart();
					try {
						String chartOptionsJsArea = bar.configure(reportTemplate, "ChartDiv",false);
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				if (chartType.equalsIgnoreCase("Stacked Bar"))
				{
					
					BuildStackedBarChart sBar = new BuildStackedBarChart();
					try {
						String chartOptionsJsArea = sBar.configure(reportTemplate, "ChartDiv",false);
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				
				}					
				if (chartType.equalsIgnoreCase("column"))
				{
					BuildBarChart bar = new BuildBarChart();
					try {
						String chartOptionsJsArea = bar.configure(reportTemplate, "ChartDiv",true);
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				if (chartType.equalsIgnoreCase("Stacked Column"))
				{
					BuildStackedBarChart sColumn = new BuildStackedBarChart();
					try {
						String chartOptionsJsArea = sColumn.configure(reportTemplate, "ChartDiv",true);
						request.setAttribute("chartOptions", chartOptionsJsArea);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				
				}	
				if (chartType.equalsIgnoreCase("TimeSeries"))
				{
					
					BuildTimeSeriesChart StackedColumn = new BuildTimeSeriesChart	();
		    		chartOptionsJs = StackedColumn.configure(reportTemplate);
		    		request.setAttribute("globalOptions", globalOptionsJs);	
		    		request.setAttribute("chartOptions", chartOptionsJs);
				
				}			
				getServletContext().getRequestDispatcher("/ShowChartNew.jsp").forward(request, response);
			}else if(reportType.equalsIgnoreCase("matrix")){
				request.setAttribute("dataset", res);
				getServletContext().getRequestDispatcher("/ShowTable.jsp").forward(request, response);
			}
			else if(reportType.equalsIgnoreCase("table")){
				request.setAttribute("dataset", res);
				getServletContext().getRequestDispatcher("/ShowTable.jsp").forward(request, response);
			}
			else if(reportType.equalsIgnoreCase("Box")){
				request.setAttribute("dataset", res);
				getServletContext().getRequestDispatcher("/ShowBoxLayout.jsp").forward(request, response);
			}
		}
		
		/*response.setContentType("application/text");
		response.getWriter().write(folderName);*/
		
	}

}
