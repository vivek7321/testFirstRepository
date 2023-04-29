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

import com.app.common.ReturnXmlData;
import com.app.dao.QueryDao;
import com.app.dao.ReportTemplateDao;
import com.app.object.ReportTemplate;

/**
 * Servlet implementation class ChartRefreshController
 */
@WebServlet("/ChartRefreshController")
public class ChartRefreshController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChartRefreshController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 LOGGER.info("In get method of ChartRefreshController..");
		String reportId = request.getParameter("reportId");
		 LOGGER.debug("reportId in ChartRefreshController.. --> " + reportId);
		 String emailId = request.getSession().getAttribute("emailId").toString();
			
			String xmlData = null;
			
			ReportTemplateDao reportTemplateDao = new ReportTemplateDao();
			ReportTemplate reportTemplate = reportTemplateDao.fetchReportById(reportId);
			reportTemplate.setEmail(emailId);
			if(reportTemplate.getReportType().equalsIgnoreCase("chart")){
				// Generate the chart
				QueryDao queryDao = new QueryDao();
				//ResultSet res = queryDao.executeReportQuery(reportTemplate, emailId);
				Connection conn = null;
				ResultSet res = null;
				Map<String, Object> outputMap = queryDao.executeReportQuery(reportTemplate, emailId);
				Iterator<Map.Entry<String, Object>> entries = outputMap.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, Object> entry = entries.next();
					if(entry.getKey().equalsIgnoreCase("connection")){
						conn = (Connection)entry.getValue();
					}else if(entry.getKey().equalsIgnoreCase("resultset")){
						res = (ResultSet)entry.getValue();
					}
				}
			
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
					LOGGER.debug(e.getMessage());
					e.printStackTrace();
				}finally{
					try{
						if(null != res){res.close();}
					}catch(Exception e){}
					try{
						if(null != conn){conn.close();}
					}catch(Exception e){}
				}
				ReturnXmlData returnXmlData = new ReturnXmlData();
				xmlData = returnXmlData.returnUpdatedData(xList, yList, dataList, reportTemplate);
			}
			
			response.setContentType("application/xml");
			response.getWriter().write(xmlData);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of ChartRefreshController..");
	}

}
