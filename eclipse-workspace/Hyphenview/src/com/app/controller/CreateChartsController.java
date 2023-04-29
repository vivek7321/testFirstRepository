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
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.CreateXmlFile;
import com.app.dao.FetchElementsDao;
import com.app.dao.QueryDao;
import com.app.object.ReportTemplate;

/**
 * Servlet implementation class CreateChartsController
 */
@WebServlet("/CreateChartsController")
public class CreateChartsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateChartsController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In get method of CreateChartsController...");
		/*String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		String email = (String) request.getSession().getAttribute("email");
		String customQuery = request.getParameter("customQuery");
		String reportType = request.getParameter("reportType");
		LOGGER.info("reportType in servlet --> " + reportType);
		String chartType = request.getParameter("chartType");
		String folderName = (String) request.getParameter("sessionId");
		LOGGER.info("folderName -- sessionid --> " + folderName);
		String fileName = request.getParameter("chartName");
		String drilldown = request.getParameter("drilldown");
		
		QueryDao queryDao = new QueryDao();
		ResultSet res = queryDao.executeQuery(email, customQuery, database, schema);
		
		if(reportType.equalsIgnoreCase("chart")){
			List<String> xList = new ArrayList<String>();
			List<String> yList = new ArrayList<String>();
			try {
				while(res.next())
				{
					xList.add(res.getString(1));
					yList.add(res.getString(2));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			String url = null;
			if(null != drilldown && drilldown.equalsIgnoreCase("yes")){
				url = "javascript:drilldown( _category_)";
			}
			
			CreateXmlFile createXmlFile = new CreateXmlFile();
			createXmlFile.CreateSourceXml(xList, yList, fileName, chartType, folderName, fileName, null);
			request.setAttribute("folderName", folderName);
			request.setAttribute("fileName", fileName);
			getServletContext().getRequestDispatcher("/ShowChart.jsp").forward(request, response);
		}else if(reportType.equalsIgnoreCase("table")){
			request.setAttribute("dataset", res);
			getServletContext().getRequestDispatcher("/ShowTable.jsp").forward(request, response);
		}*/
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
		LOGGER.info("templateType in servlet --> " + templateType);
		String reportId = request.getParameter("reportId");
		System.out.println(reportId);
		String chartType = request.getParameter("chartType");
		String folderName = (String) request.getParameter("sessionId");
		LOGGER.info("folderName -- sessionid --> " + folderName);
		String fileName = request.getParameter("chartName");
		String rectcolor = request.getParameter("rectcolor");
		LOGGER.info("rectcolor -- CreateChartsController --> " + rectcolor);
		/*Month Name 07 Mar 19 NO NEED TO ADD HERE */
		String timeperiod = request.getParameter("timeperiod");
		LOGGER.info("timeperiod -- CreateChartsController --> " + timeperiod);
		
		
		
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
				reportTemplate.setReportTemplateName(fileName);
				reportTemplate.setTemplateType(templateType);
				reportTemplate.setAutoUpdateInterval(Integer.parseInt(autoUpdate));
				reportTemplate.setRectcolor(rectcolor);
				reportTemplate.setTimePeriod(timeperiod);
				reportTemplate.setEmail(email);
				reportTemplate.setDatabase(database);
				reportTemplate.setSchema(schema);
				reportTemplate.setDefinedQuery(customQuery);
				reportTemplate.setReportTemplateId(reportId);
				CreateXmlFile createXmlFile = new CreateXmlFile();
				createXmlFile.CreateSourceXml(folderName, null, reportTemplate);
				request.setAttribute("folderName", folderName);
				request.setAttribute("fileName", fileName);
				getServletContext().getRequestDispatcher("/ShowChart.jsp").forward(request, response);
			}else if(reportType.equalsIgnoreCase("matrix")){
				request.setAttribute("dataset", res);
				getServletContext().getRequestDispatcher("/ShowTable.jsp").forward(request, response);
			}
			else if(reportType.equalsIgnoreCase("table")){
				request.setAttribute("dataset", res);
				getServletContext().getRequestDispatcher("/ShowTable.jsp").forward(request, response);
			}
		}
		
		/*response.setContentType("application/text");
		response.getWriter().write(folderName);*/
		
	}

}
