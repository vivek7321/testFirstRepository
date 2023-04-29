package com.app.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.QueryDao;
import com.app.dao.ReportTemplateDao;
import com.app.object.ReportTemplate;
import com.hyjavacharts.chart.Highchart;

public class ConstructDashboard2 {
	
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public Map<String, String> getDashboardContent(String emailId,HttpSession session, String sessionId,String roleName , String userId , String dashboardId){
		
		Map<String, String> dashMap = new HashMap<String, String>();
		String htmlBody = "";
		String script = "";
		String exportTableList = "";
		String refreshScript = "";	
		
		String chartOptionsJs = "";
		/*String csvTables = "";
		String csvScript = "";*/
		ReportTemplateDao reportTemplateDao = new ReportTemplateDao();
		

		List<ReportTemplate> reportsList = reportTemplateDao.fetchReportsByEmail(emailId,roleName,userId ,dashboardId );
		
		if(null != reportsList && reportsList.size() > 0){
			int reportsCount = reportsList.size();
			LOGGER.info("reportsCount --> " + reportsCount);
			
			boolean flag = true;
			boolean tableflag = true;
			boolean matrixflag = true;
			for(int i=0; i<reportsCount; i++){
				ReportTemplate reportTemplate = reportsList.get(i);
				/*month name included for chart title 07-03-19*/
				//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-YY");
				
				
				LocalDate now = LocalDate.now(); // 2019-03-07
				LocalDate previousmonth = now.minusMonths(1); // 2019-02-07
				LocalDate lastthreemonth = now.minusMonths(3); // 2018-12-07
				LocalDate lastsixmonth = now.minusMonths(6); // 2018-09-07
				
				now.getMonth(); // java.time.Month = March
				now.getYear(); // 2019
				previousmonth.getMonth(); // java.time.Month = Feb
				previousmonth.getYear(); // 2019
				lastthreemonth.getMonth(); // java.time.Month = Dec
				lastthreemonth.getYear(); // 2018
				lastsixmonth.getMonth(); // java.time.Month = Sep
				lastsixmonth.getYear(); // 2018
				String monName = "";
				String mon_name = reportTemplate.getTimePeriod();
				if(null != mon_name && !mon_name.isEmpty()) {
				switch(mon_name) 
		        { 
		            case "previous month": 
		                monName =  (previousmonth.getMonth().toString()).substring(0, 3)+" "+Integer.toString(previousmonth.getYear()).substring(2, 4);
		                break; 
		            case "last three months": 
		                monName =  (lastthreemonth.getMonth().toString()).substring(0, 3)+" "+Integer.toString(lastthreemonth.getYear()).substring(2, 4)+" - "+(previousmonth.getMonth().toString()).substring(0, 3)+" "+Integer.toString(previousmonth.getYear()).substring(2, 4); 
		                break; 
		            case "last six months":  
		                monName =  (lastsixmonth.getMonth().toString()).substring(0, 3)+" "+Integer.toString(lastsixmonth.getYear()).substring(2, 4)+" - "+(previousmonth.getMonth().toString()).substring(0, 3)+" "+Integer.toString(previousmonth.getYear()).substring(2, 4); 
		                break;
		            case "current month":
		            	monName =  (now.getMonth().toString()).substring(0, 3)+" "+Integer.toString(now.getYear()).substring(2, 4);
		                break; 
		            default: 
		                System.out.println(""); 
		        }
				}
				
				if(reportTemplate.getReportType().equalsIgnoreCase("Box"))
				{					
				
					
					LOGGER.info("inside box flag1-->"+reportTemplate.getTemplateType());
					if(reportTemplate.getTemplateType().equalsIgnoreCase("Enlarge")) {
						 htmlBody =  htmlBody.concat("\n <div class=\"col-md-6 col-lg-4 col-xlg-3\"> ");
					}else {
						htmlBody =  htmlBody.concat("\n <div class=\"col-md-6 col-lg-2 col-xlg-3\"> ");
					}
					  		
					htmlBody = htmlBody.concat("<div id=\"").concat("card"+i).
							//concat("\" class=\"col-lg-5 panel panel-default\"  draggable=\"true\" ondragstart=\"drag(event)\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event)\" style=\"border:none;border-color:transparent;background-color: transparent;min-width:485px;\" ")
							concat("\" class=\"card\"  draggable=\"true\" ondragstart=\"drag(event)\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event) \"" )
							.concat( "reportId=\"").concat(reportTemplate.getReportTemplateId()).concat("\" displayOrder=\"").concat(Integer.toString(reportTemplate.getDisplayOrder())).concat("\" ")
							.concat( ">\n").concat(" <div class=\"card-header\" >");
					htmlBody = htmlBody.concat(reportTemplate.getReportTemplateName()+" "+monName);
					htmlBody = htmlBody.concat("</div> \n");
					String chartScriptId = "card" + i + "_script";
					htmlBody = htmlBody.concat("<div class=\"card-body\" id=\"").concat(chartScriptId).concat("\" align=\"centre\"> ");
		

					htmlBody= htmlBody.concat("<h1 class=\"card-text\" style = \"text-align:center; color:").concat(reportTemplate.getRectcolor()).concat(";\">");
					
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
					
					try
					{
					
					
					while(res.next())
					{
					htmlBody = htmlBody.concat(res.getString(1));
					htmlBody = htmlBody.concat("</h1>");	
					}
					}
					catch(Exception e)
					{
						
					}
					String drillQuery = reportTemplate.getDefinedQuery();
					 String x= "Hello (Java)";
					 Matcher m = Pattern.compile("\\((.*?)\\)").matcher(x);
					 	
					if(drillQuery.contains("")){
						drillQuery = drillQuery.replaceAll("\"", "\\\\\"");
						LOGGER.debug("drillQuery with quotes replaced --> " + drillQuery);
					} 
					
					drillQuery = drillQuery.replaceFirst("count","");
					drillQuery = drillQuery.replaceFirst("\\(","");
					drillQuery = drillQuery.replaceFirst("\\)","");
				
					String url = null;
					if(reportTemplate.getDrilldown().equalsIgnoreCase("yes")){
						url = "javascript:drilldown" + i + "()";
						//url = "http://localhost/hyphenII/DrilldownController?query=" +drillQuery+"&databaseId="+reportTemplate.getDatabaseId()+ "&emailId="+emailId;
							
						script = script.concat("<script type=\"text/javascript\"> \n");
						script = script.concat("function drilldown" + i).concat("(){\n");
						script = script.concat("var url=\"DrilldownControllerBox\";\n ");
						script = script.concat("$.ajax({\n ");
						script = script.concat("url: url,\n ");
						script = script.concat("type: \"Post\",\n ");
						script = script.concat("data: {\"query\":\"").concat(drillQuery).
								concat("\", \"databaseId\":\"").concat(reportTemplate.getDatabaseId()).concat("\", \"emailId\":\"").concat(emailId).concat("\"},\n ");
						script = script.concat("dataType: \"text\",\n ");
						script = script.concat("success : function(msg) {\n");
						script = script.concat("return popitup('ShowDrilldown.jsp'); \n }\n");
						script = script.concat("});\n }\n");
						script = script.concat("</script>\n");
						
					}
					


					
					htmlBody = htmlBody.concat("</div>\n");
					
					htmlBody = htmlBody.concat("</div>\n");
					htmlBody = htmlBody.concat("</div>\n");
										
				}
				
				else if(reportTemplate.getReportType().equalsIgnoreCase("Chart")){
	           
					if (flag)
					{
						htmlBody = htmlBody.concat("</div>\n");
						htmlBody = htmlBody.concat("<div class=\"row\">");
						flag=false;
					}
					htmlBody = htmlBody.concat("\n <div class=\"col-md-6 col-lg-4 col-xlg-3\"> ");

					String chartScriptId = "chart" + i + "_script";
					htmlBody = htmlBody.concat("<div class=\"card\" id=\"").concat(chartScriptId).concat("\">");
					
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
					
					String drillQuery = reportTemplate.getDefinedQuery();
					if(drillQuery.contains("\"")){
						drillQuery = drillQuery.replaceAll("\"", "\\\\\"");
						LOGGER.debug("drillQuery with quotes replaced --> " + drillQuery);
					} 
				
					String url = null;
					if(reportTemplate.getDrilldown().equalsIgnoreCase("yes")){
						url = "javascript:drilldown" + i + "(_series_, _category_)";
						//url = "http://localhost/hyphenII/DrilldownController?query=" +drillQuery+"&databaseId="+reportTemplate.getDatabaseId()+ "&emailId="+emailId;
							
						script = script.concat("<script type=\"text/javascript\"> \n");
						script = script.concat("function drilldown" + i).concat("(series, category){\n");
						script = script.concat("var url=\"DrilldownController\";\n ");
						script = script.concat("$.ajax({\n ");
						script = script.concat("url: url,\n ");
						script = script.concat("type: \"Post\",\n ");
						script = script.concat("data: {\"series\":series, \"category\":category, \"query\":\"").concat(drillQuery).
								concat("\", \"databaseId\":\"").concat(reportTemplate.getDatabaseId()).concat("\", \"emailId\":\"").concat(emailId).concat("\"},\n ");
						script = script.concat("dataType: \"text\",\n ");
						script = script.concat("success : function(msg) {\n");
						script = script.concat("return popitup('ShowDrilldown.jsp'); \n }\n");
						script = script.concat("});\n }\n");
						script = script.concat("</script>\n");
						
					}
					
					String fileName = reportTemplate.getReportTemplateName();
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
					
					String theme = reportTemplate.getTheme();
					String chartType = reportTemplate.getChartType();
					
					if (theme==null)
					{
						theme = "DARK_UNICA";
					}
					reportTemplate.setTheme(theme);
			    	String globalOptionsJs = "";
			    	
					Highchart hc = null;
			    	

					if (chartType.equalsIgnoreCase("Line"))
					{
						
			    		BuildLineChartNew line1 = new BuildLineChartNew();
			    		chartOptionsJs = line1.configure(reportTemplate);
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}
					if (chartType.equalsIgnoreCase("area"))
					{
			    		BuildAreaChart area = new BuildAreaChart();
			    		//chartOptionsJs = area.configure(reportTemplate,"");
			    		//session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}	
					
					if (chartType.equalsIgnoreCase("Stacked Area"))
					{
			    		BuildStackedAreaChart stackedArea = new BuildStackedAreaChart();
			    		//chartOptionsJs = stackedArea.configure(reportTemplate);
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}					
					
					if (chartType.equalsIgnoreCase("Bar"))
					{
						
			    		
			    		BuildBarChart bar = new BuildBarChart();
			    		//chartOptionsJs = bar.configure(reportTemplate,"");
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}
					if (chartType.equalsIgnoreCase("Stacked Bar"))
					{
						
			    		
			    		BuildStackedBarChart2 StackedBar = new BuildStackedBarChart2();
			    		chartOptionsJs = StackedBar.configure(reportTemplate);
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}					
					if (chartType.equalsIgnoreCase("column"))
					{
						
			    		
			    		BuildColumnChart column = new BuildColumnChart();
			    		chartOptionsJs = column.configure(reportTemplate);
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}
					if (chartType.equalsIgnoreCase("Stacked Column"))
					{
						
			    		
			    		BuildStackedColumnChart StackedColumn = new BuildStackedColumnChart	();
			    		chartOptionsJs = StackedColumn.configure(reportTemplate);
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}	
					if (chartType.equalsIgnoreCase("HeatMap"))
					{
						
			    		
			    		BuildHeatMapChart heatMap = new BuildHeatMapChart	();
			    		chartOptionsJs = heatMap.configure(reportTemplate);
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}
					if (chartType.equalsIgnoreCase("TimeSeries"))
					{
						
			    		
			    		BuildTimeSeriesChart heatMap = new BuildTimeSeriesChart();
			    		chartOptionsJs = heatMap.configure(reportTemplate);
			    		session.setAttribute("chartOptionsJS", chartOptionsJs);
					
					}					
					String reportScript = "<script type=\"text/javascript\"> \n" +
					 "Highcharts.chart(\'" + chartScriptId + "\', " + chartOptionsJs +"); \n" +
					 "</script>\n" ; 
					
					if (chartType.equalsIgnoreCase("Pie"))
					{
						
						BuildPieChartXchart exampleChart = new BuildPieChartXchart();
					    PieChart chart = exampleChart.getChart(reportTemplate);
					    new SwingWrapper<PieChart>(chart).displayChart();

					}
					
					htmlBody = htmlBody.concat("</div>\n");
					
					htmlBody = htmlBody.concat("</div>\n");
					htmlBody = htmlBody.concat(reportScript);
					
					session.setAttribute("chartOptionsJS", chartOptionsJs);
					
				}
				else if(reportTemplate.getReportType().equalsIgnoreCase("matrix"))
				
				{
					
					if (matrixflag)
					{
						htmlBody = htmlBody.concat("</div>\n");
						htmlBody = htmlBody.concat("<div class=\"row\">");
						matrixflag=false;
					}
					

					
					exportTableList = exportTableList.concat("matrix"+i);
					if(i!=(reportsCount-1)){
						exportTableList = exportTableList.concat(",");
					}
					htmlBody = htmlBody.concat("\n <div class=\"col-md-6 col-lg-12 col-xlg-3\"> ");
					String tableId = "dataTable" + i;
//					htmlBody = htmlBody.concat("<div class=\"row\" style=\"border:0px;align:center;margin-right: auto;margin-left: auto; \">\n");
				//	htmlBody = htmlBody.concat("<div class=\"export\" style=\"border-radius: 20px;background-color: #fbfbfb;\">\n");
					htmlBody = htmlBody.concat("<div id=\"").concat("table"+i).
							concat("\" class=\"card\"  draggable=\"true\" ondragstart=\"drag(event)\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event)\" style=\"height: 400px; width: 530px;margin-bottom: 10px; display:table; border-radius: 20px; background-color: #ffffff;\" ")
							.concat( "reportId=\"").concat(reportTemplate.getReportTemplateId()).concat("\" displayOrder=\"").concat(Integer.toString(reportTemplate.getDisplayOrder())).concat("\" ")
							.concat( ">\n").concat(" <div class=\"card-header\" style=\"background-color: #ffffff;border:none;\">");
					htmlBody = htmlBody.concat(reportTemplate.getReportTemplateName()+" "+monName);
					htmlBody = htmlBody.concat("</div> \n");
					htmlBody = htmlBody.concat("<div class=\"card-body\"> ");
					
					
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
					
					htmlBody = htmlBody.concat("<table id=\"").concat(tableId).concat("\" class=\"table table-striped table-bordered table-hover dt-responsive datatables\">");
					try{
				    	int columnCount = res.getMetaData().getColumnCount();
						if(columnCount>0){
							htmlBody = htmlBody.concat("<thead> <tr> ");
							for(int k=1;k<=columnCount; k++){
								htmlBody = htmlBody.concat("<th").concat(res.getMetaData().getColumnName(k).toUpperCase().replaceAll("_", " ") ).concat("</th>");
							}
							htmlBody = htmlBody.concat("</tr> </thead> <tbody>");
							String resData = null;
							while(res.next())
							{
								htmlBody = htmlBody.concat("<tr style=\"height:2px;\">");
						        for(int l=1;l<=columnCount; l++){
						        	if(null != res.getString(l)){
						        		resData = res.getString(l);
						        	}else{
						        		resData = "";
						        	}
						        	htmlBody = htmlBody.concat("<td>").concat(resData).concat("</td>");
						         }
						        htmlBody = htmlBody.concat("</tr>");
						    }
						}
					}catch(SQLException se){
						LOGGER.debug(se.getMessage());
						se.printStackTrace();
					}finally{
						try{
							if(null != res){res.close();}
						}catch(Exception e){}
						try{
							if(null != conn){conn.close();}
						}catch(Exception e){}
					}
					htmlBody = htmlBody.concat("</tbody> </table> ");
					htmlBody = htmlBody.concat("</div></div>\n");
					
					//Datatable script
					
					script = script.concat("<script type=\"text/javascript\">\n$('#").concat(tableId).concat("').DataTable( {\n");
					script = script.concat("destroy : true, \n dom: 'Brtp',pageLength:5,ordering: false, \n buttons: [ { extend: 'excelHtml5', text: '<i class=\"fas fa-file-excel\" style=\"color:green;font-weight:bold;\"></i>', titleAttr: 'Excel'} ] });");
					script = script.concat("</script>");
					//data refresh script for table
					refreshScript = refreshScript.concat("setInterval(function() {\n ")
							.concat("var table = $('#").concat(tableId).concat("').DataTable();")
							.concat("var url=\"DivRefreshController\"; \n $.ajax({ \n ");
					refreshScript = refreshScript.concat("url: url, \n type: \"Post\", \n data: {\"divId\": \"").concat("matrix"+i).
													concat("\", \"reportId\":\"").concat(reportTemplate.getReportTemplateId()).concat("\"},");
					refreshScript = refreshScript.concat("dataType: \"html\", \n success : function(html) { \n");
					refreshScript = refreshScript.concat("$('#").concat("matrix"+i).concat("').html(html); ");
					refreshScript = refreshScript.concat("\n var table = $('#").concat(tableId).concat("').DataTable( {\n");
					refreshScript = refreshScript.concat("destroy : true, \n dom: 'Brtp',pageLength:5,ordering: false, \n buttons: [ { extend: 'excelHtml5', text: '<i class=\"fas fa-file-excel\" style=\"color:green;font-weight:bold;\"></i>', titleAttr: 'Excel'} ] });");
					refreshScript = refreshScript.concat("table.draw();");
					refreshScript = refreshScript.concat("\n } \n });}, 60000); \n");
					
					
				}
				else{
					if (tableflag)
					{
						htmlBody = htmlBody.concat("</div>\n");
						htmlBody = htmlBody.concat("<div class=\"row\">");
						tableflag=false;
					}
					exportTableList = exportTableList.concat("table"+i);
					if(i!=(reportsCount-1)){
						exportTableList = exportTableList.concat(",");
					} 
					htmlBody = htmlBody.concat("\n <div class=\"col-md-6 col-lg-12 col-xlg-3\"> ");
					String tableId = "dataTable" + i;
//					htmlBody = htmlBody.concat("<div class=\"row\" style=\"border:0px;align:center;margin-right: auto;margin-left: auto; \">\n");
				//	htmlBody = htmlBody.concat("<div class=\"export\" style=\"border-radius: 20px;background-color: #fbfbfb;\">\n");
					htmlBody = htmlBody.concat("<div id=\"").concat("table"+i).
							concat("\" class=\"card\"  draggable=\"true\" ondragstart=\"drag(event)\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event)\" style=\"margin-bottom: 30px; display:table; border-radius: 20px;background-color: #ffffff;\" ")
							.concat( "reportId=\"").concat(reportTemplate.getReportTemplateId()).concat("\" displayOrder=\"").concat(Integer.toString(reportTemplate.getDisplayOrder())).concat("\" ")
							.concat( ">\n").concat(" <div class=\"card-header\" style=\"background-color: #ffffff;border:none;\">");
					htmlBody = htmlBody.concat(reportTemplate.getReportTemplateName()+" "+monName);
					htmlBody = htmlBody.concat("</div> \n");
					htmlBody = htmlBody.concat("<div class=\"card-body\"> ");
					
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
					
					htmlBody = htmlBody.concat("<table id=\"").concat(tableId).concat("\" class=\"table table-striped table-bordered table-hover dt-responsive datatables\" style=\"font-size:10px;\">");
					try{
				    	int columnCount = res.getMetaData().getColumnCount();
						if(columnCount>0){
							htmlBody = htmlBody.concat("<thead> <tr> ");
							for(int k=1;k<=columnCount; k++){
								htmlBody = htmlBody.concat("<th>").concat(res.getMetaData().getColumnName(k).toUpperCase().replaceAll("_", " ") ).concat("</th>");
							}
							htmlBody = htmlBody.concat("</tr> </thead> <tbody>");
							String resData = null;
							while(res.next())
							{
								htmlBody = htmlBody.concat("<tr>");
						        for(int l=1;l<=columnCount; l++){
						        	if(null != res.getString(l)){
						        		resData = res.getString(l);
						        	}else{
						        		resData = "";
						        	}
						        	htmlBody = htmlBody.concat("<td>").concat(resData).concat("</td>");
						         }
						        htmlBody = htmlBody.concat("</tr>");
						    }
						}
					}catch(SQLException se){
						LOGGER.debug(se.getMessage());
						se.printStackTrace();
					}finally{
						try{
							if(null != res){res.close();}
						}catch(Exception e){}
						try{
							if(null != conn){conn.close();}
						}catch(Exception e){}
					}
					htmlBody = htmlBody.concat("</tbody> </table> ");
					htmlBody = htmlBody.concat("</div></div></div>\n");
					
					//Datatable script
					script = script.concat("<script type=\"text/javascript\">\n$('#").concat(tableId).concat("').DataTable( {\n");
					script = script.concat("destroy : true, \n dom: 'Brtp', \n buttons: [ { extend: 'excelHtml5', text: '<i class=\"fas fa-file-excel\" style=\"color:green;font-weight:bold;\"></i>', titleAttr: 'Excel'} ] });");
					script = script.concat("</script>");
					
					//data refresh script for table
					refreshScript = refreshScript.concat("setInterval(function() {\n ")
							.concat("var table = $('#").concat(tableId).concat("').DataTable();")
							.concat("var url=\"DivRefreshController\"; \n $.ajax({ \n ");
					refreshScript = refreshScript.concat("url: url, \n type: \"Post\", \n data: {\"divId\": \"").concat("table"+i).
													concat("\", \"reportId\":\"").concat(reportTemplate.getReportTemplateId()).concat("\"},");
					refreshScript = refreshScript.concat("dataType: \"html\", \n success : function(html) { \n");
					refreshScript = refreshScript.concat("$('#").concat("table"+i).concat("').html(html); ");
					refreshScript = refreshScript.concat("\n var table = $('#").concat(tableId).concat("').DataTable( {\n");
					refreshScript = refreshScript.concat("destroy : true, \n dom: 'Brtp', \n buttons: [ { extend: 'excelHtml5', text: '<i class=\"fas fa-file-excel\" style=\"color:green;font-weight:bold;\"></i>', titleAttr: 'Excel'} ] });");
					refreshScript = refreshScript.concat("table.draw();");
					refreshScript = refreshScript.concat("\n } \n });}, 60000); \n");
					
					/*refreshScript = refreshScript.concat("setInterval(function() {\n ");
					refreshScript = refreshScript.concat("$('#").concat("table"+i).concat("').load(\"DivRefreshController\",{divId: ").concat("table"+i).
													concat(", reportId:").concat(reportTemplate.getReportTemplateId()).concat("}); \n ");
					refreshScript = refreshScript.concat("}, 60000); \n");*/
					
						
					
					/*refreshScript = refreshScript.concat("setInterval(function() {\n ")
							.concat("var table = $('#").concat(tableId).concat("').DataTable(); \n table.fnDraw();");
							refreshScript = refreshScript.concat("}, 60000); \n");*/
				}
				
				if(((i+1)%2) != 0){
				LOGGER.info(i+ " is a odd number...");
				/*htmlBody = htmlBody.concat("<div class=\"col-lg-1\" style=\"max-width: 60px;\"></div>");*/
				}
				
			} 
		}
		LOGGER.debug("htmlbody --> " + htmlBody);
		LOGGER.debug("script --> " + script);
		LOGGER.debug("refreshScript --> " + refreshScript);
		
		// read csv files
		/*Map<String, String> csvMap = getHtmlContentForFiles();
		Iterator<Map.Entry<String, String>> entries = csvMap.entrySet().iterator();
		while (entries.hasNext()) {
				Map.Entry<String, String> entry = entries.next();
				if(entry.getKey().equalsIgnoreCase("csvTables")){
					csvTables = entry.getValue();
				}else if(entry.getKey().equalsIgnoreCase("csvScript")){
					csvScript = entry.getValue();
				}
		}
		htmlBody = htmlBody.concat(csvTables);
		LOGGER.debug("htmlBody after appending csvTables :: " + htmlBody);
		LOGGER.debug("csvScript :: " + csvScript);*/
		
		dashMap.put("htmlBody", htmlBody);
		dashMap.put("script", script);
		dashMap.put("exportTableList", exportTableList);
		dashMap.put("refreshScript", refreshScript);
	//	dashMap.put("csvScript", csvScript);
		System.out.println("exportTableList in java... " +exportTableList);
		session.setAttribute("chartOptionsJS", chartOptionsJs);
		
		return dashMap;
	}
	
	
	public Map<String, String> getHtmlContentForFiles(){
		
		Map<String, String> csvMap = new HashMap<String, String>();
		String csvTables = "";
		String csvScript = "";
		
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
			Properties p = new Properties();
			p.load(is);
			String folderPath = p.getProperty("inputpath");
			LOGGER.debug("Downloaded path of SFTP files :: " + folderPath);
			
			 // To create folder structure with the csv data file
	         String directory = System.getenv("SERVER_HOME");
	         LOGGER.debug("directory for data folder creation --> " + directory);
	         
	         File dataDir = new File(directory + "/webapps/Hyphenview/data");
	         if(dataDir.exists() && dataDir.isDirectory()){
	        	 boolean dirDeleted = dataDir.delete();
	        	 LOGGER.debug("Directory " + dataDir.getAbsolutePath() + " got deleted?? --> " + dirDeleted);
	        	 if(!dirDeleted){
	        		 File[] deleteFiles = dataDir.listFiles();
	        		 if(null != deleteFiles && deleteFiles.length > 0){
	 					//Iterate through the sub folders
	 					for (File temp : deleteFiles) {
	 						boolean isDeleted = temp.delete();
	 						LOGGER.debug("The file " + temp.getAbsolutePath() + " got deleted?? --> " + isDeleted);
	 					}
	 				}
	        	 }
	         }else if(!dataDir.exists()){
		         boolean dirCreated = dataDir.mkdirs();
	        	 LOGGER.debug("Directory " + dataDir.getAbsolutePath() + " got created?? --> " + dirCreated);
	         }
			
			File downloadedDir = new File(folderPath);
			if(null != downloadedDir && downloadedDir.exists() && downloadedDir.isDirectory()){
				//get the list of sub directories(Incident/Service Request)
				File[] listOfFolders = downloadedDir.listFiles();
				if(null != listOfFolders && listOfFolders.length > 0){
					//Iterate through the sub folders
					for (File folder : listOfFolders) {
						if(folder.isDirectory()){
							LOGGER.debug("sub folder within the Infosec directory --> " + folder.getName());
							// get the list of files inside the sub directory
							File[] listOfFiles = folder.listFiles();
							if(null != listOfFiles && listOfFiles.length > 0){
								
								// To create a single file with data from all the files
						         File newFile = new File(dataDir.getAbsolutePath() + "/" + folder.getName() + ".csv");
						         LOGGER.debug("Created new file in data folder --> " + newFile.getName());
						         BufferedWriter bfw = new BufferedWriter(new FileWriter(newFile));
						         
						         String line = null;
						         int fileCount = 0;
								// iterate through the files list in the sub-directory
								for (File file : listOfFiles) {
								    if (file.isFile()) {
								    	LOGGER.debug("Inside sub folder --> " + folder.getName());
								    	LOGGER.debug("Processing File :: " + file.getAbsolutePath() + " in construct dashboard");
								         
								         // Read from the input file
								         BufferedReader bfr = new BufferedReader(new FileReader(file));
								         line = bfr.readLine();
								         if(fileCount>0){
								        	 line = bfr.readLine();
								         }
								         while(line != null) {
								             //write into the new file in data folder
								        	 bfw.write(line);
								        	 bfw.newLine();
								             line = bfr.readLine();
								         }
								         bfr.close();
								         fileCount++;
								    }
								}
								bfw.flush();
						        bfw.close();
							}
						}
					}
				}
				
				// Read the data directory and construct the html data
				File[] dataFiles = dataDir.listFiles();
				int i = 0;
				if(null != dataFiles && dataFiles.length > 0){
					for (File csvFile : dataFiles) {
					    if (csvFile.isFile()) {
					         String fileName = csvFile.getName();
					         LOGGER.debug("Reading file data folder --> " + fileName);
					         String tableHeader = null;
					         if(fileName.contains(".")){
								String tokens[] = fileName.split("\\.");
								tableHeader = tokens[0];
					         }
								         
					         //construct the html content for the file
					         csvTables = csvTables.concat("<div id=\"").concat("csvtableDiv"+i).
										concat("\" class=\"col-lg-5 panel panel-default\"  style=\"display:table; border-radius: 20px;background-color: #ffffff;\" ")
										.concat( ">\n").concat(" <div class=\"panel-heading\" style=\"background-color: #ffffff;border:none;\">");
					         csvTables = csvTables.concat(tableHeader);
					         csvTables = csvTables.concat("</div> \n");
					         csvTables = csvTables.concat("<div class=\"panel-body\"> ");
					         csvTables = csvTables.concat("<table id=\"").concat("csvtable"+i).concat("\" class=\"table table-striped table-bordered table-hover dt-responsive datatables\" style=\"width:100%;height: 330px;font-size:10px;width:500px\">");
					         csvTables = csvTables.concat("</table> ");
					         csvTables = csvTables.concat("</div></div>\n");
								
								//Datatable script
					         csvScript = csvScript.concat("\n$.ajax({\n url:\"/Hyphenview/data/")
										.concat(fileName).concat("\",\ndataType:\"text\",\nsuccess:function(data)\n")
								.concat("{	processData").concat(Integer.toString(i)).concat("(data); }\n});\n function processData").concat(Integer.toString(i)).concat("(allText) {\nvar all_lines = allText.split(/\\n/); \n")
								.concat("var headers = all_lines[0].split(',');\nvar table_data = new Array();\nfor(var i = 1; i < all_lines.length; i++){\n")
								.concat("table_data[i-1] = all_lines[i].split(',');\n}\n var header_data = new Array();\nfor(var count = 0; count < headers.length; count++){\n")
								.concat("var temp_data = {'title' : headers[count]};\nheader_data[count] = temp_data;\n}\n")
								.concat("$('#").concat("csvtable"+i).concat("').DataTable( {\n");
							csvScript = csvScript.concat("destroy : true, \n dom: 'Brtp', \n buttons: [ { extend: 'excelHtml5', text: '<i class=\"fas fa-file-excel\" style=\"color:green;font-weight:bold;\"></i>', titleAttr: 'Excel'} ],\n")
									.concat("data: table_data,\n columns: header_data,\n defaultContent: '<i>Not Set</i>' \n }); \n }");
							
							i++;
						}
					}
				}
				
			}
			
			csvMap.put("csvTables", csvTables);
			csvMap.put("csvScript", csvScript);
		}catch(Exception e){
			LOGGER.debug("Exception while constructing HTML content for the CSV file :: " + e.getMessage());
			e.printStackTrace();
		}
		
		return csvMap;
	}
	
	

}
