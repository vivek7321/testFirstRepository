package com.app.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.CreateXmlFile;
import com.app.dao.QueryDao;
import com.app.dao.ReportTemplateDao;
import com.app.object.ReportTemplate;

/**
 * Servlet implementation class DisplayOrderController
 */
@WebServlet("/DivRefreshController")
public class DivRefreshController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DivRefreshController() {
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
		LOGGER.info("In post method of DivRefreshController..");
		String divId = request.getParameter("divId");
		String reportId = request.getParameter("reportId");
		String emailId = request.getSession().getAttribute("emailId").toString();
		
		String count = null;
		if(divId.contains("table")){
			count = divId.substring(divId.lastIndexOf("table")+5, divId.length());
		}
		
		ReportTemplateDao reportTemplateDao = new ReportTemplateDao();
		ReportTemplate reportTemplate = reportTemplateDao.fetchReportById(reportId);
		
		String htmlBody = "";
		if(reportTemplate.getReportType().equalsIgnoreCase("table")){
			String tableId = "dataTable" + count;
			htmlBody = htmlBody.concat(" <div class=\"card-header\" style=\"background-color: #ffffff;border:none;\">");
			htmlBody = htmlBody.concat(reportTemplate.getReportTemplateName());
			htmlBody = htmlBody.concat("</div> \n");
			htmlBody = htmlBody.concat("<div class=\"card-body\"> ");
			
			// Generate the chart
			QueryDao queryDao = new QueryDao();
		//	ResultSet res = queryDao.executeReportQuery(reportTemplate, emailId);
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
			
			htmlBody = htmlBody.concat("<table id=\"").concat(tableId).concat("\" class=\"table table-striped table-bordered table-hover dt-responsive datatables\" style=\"font-size:10px\">");
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
				        	htmlBody = htmlBody.concat("<td>" ).concat(resData).concat("</td>");
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
			htmlBody = htmlBody.concat("</div>\n");
		}
		LOGGER.debug("In Div refresh controller --> " + htmlBody);
		response.setContentType("application/text");
		response.getWriter().write(htmlBody);
	}
	
	private void WriteToFile(String fileContent, String fileName) throws IOException {

			String directory = System.getenv("SERVER_HOME");
	        LOGGER.debug("Tomcat directory --> " + directory);
	        
	        InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
			Properties p = new Properties();
			p.load(is);
			String htmlPath = p.getProperty("htmlpath"); 
			
			String tempFile = directory + htmlPath + File.separator + fileName;
	
			System.out.println(tempFile);
			File file = new File(tempFile);
			boolean isFileCreated = file.createNewFile();
	        LOGGER.debug("Is file created successfully?? --> " + isFileCreated);
	        LOGGER.debug("HTML file created in --> " + file.getAbsolutePath());
	
			//write to file with OutputStreamWriter
			OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
			Writer writer=new OutputStreamWriter(outputStream);
			writer.write(fileContent);
			writer.close();

		}

}
