package com.app.rpt;

import java.awt.Canvas;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.EraviewDBconnection;
import com.app.dao.ReportTemplateDao;
import com.app.object.ReportTemplate;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class TableToPDF {
	public static void main(String[] args) throws Exception {
	
		
		
	}
		public ArrayList<String> export( String scheduleid, String reportInterval, Timestamp startDateReport, String emailBodyReport) throws Exception {
/* Create Connection objects */
		 int fileSize =0;
		ArrayList<String> exportFileNames = new ArrayList<String>();
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 log.info("Today's date is "+dateFormat.format(cal.getTime()));

		 cal.add(Calendar.DATE, -1);
		 log.info("Yesterday's date was "+dateFormat.format(cal.getTime()));  	
		
		

	        Date today = new Date();  
	        String finalfirstdate = null,  finallastdate = null;
	        Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(today);  
	        if (reportInterval.equalsIgnoreCase("Daily"))			
			{
	        calendar.add(Calendar.DATE, -1); 
	        calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);

	        Date firstDate = calendar.getTime();  

	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	         finalfirstdate = sdf.format(firstDate); 
	        log.info("firstdate: " + (finalfirstdate)); 
		 
	        calendar.set(Calendar.HOUR_OF_DAY, 23);
	        calendar.set(Calendar.MINUTE, 59);
	        calendar.set(Calendar.SECOND, 59);
	        calendar.set(Calendar.MILLISECOND, 59);

	        Date lastDate = calendar.getTime();  
	       finallastdate = sdf.format(lastDate);
	       log.info("lastdate: " + (finallastdate)); 
			}
	        
	        if (reportInterval.equalsIgnoreCase("DailyMTD"))			
			{
	        calendar.set(Calendar.HOUR_OF_DAY, 23);
	        calendar.set(Calendar.MINUTE, 59);
	        calendar.set(Calendar.SECOND, 59);
	        calendar.set(Calendar.MILLISECOND, 59);

	        Date lastDate = calendar.getTime();  
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	       
	        finallastdate = sdf.format(lastDate);
	        log.info("Last dateof daily MTD: " + (finallastdate)); 
	        
	        calendar.set(Calendar.DAY_OF_MONTH,1);
	        calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);

	        Date firstDate = calendar.getTime();  
	       
	       finalfirstdate = sdf.format(firstDate); 
	       log.info("first Date of daily MTD: " + (finalfirstdate)); 
			}
	       
			if (reportInterval.equalsIgnoreCase("Weekly"))			
    		{
			  calendar.set(Calendar.HOUR_OF_DAY, 23);
		        calendar.set(Calendar.MINUTE, 59);
		        calendar.set(Calendar.SECOND, 59);
		        calendar.set(Calendar.MILLISECOND, 59);

		        Date lastDate = calendar.getTime();  
		        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		        
		        finallastdate = sdf.format(lastDate);
		        log.info("Last dateof week: " + (finallastdate));
		        
		        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)-6);

		        calendar.set(Calendar.HOUR_OF_DAY, 0);
		        calendar.set(Calendar.MINUTE, 0);
		        calendar.set(Calendar.SECOND, 0);
		        calendar.set(Calendar.MILLISECOND, 0);
		        Date firstDate = calendar.getTime();
		        finalfirstdate = sdf.format(firstDate); 
			       
		        log.info("first dateof week: " + (finalfirstdate)); 
    		}
			
	        if (reportInterval.equalsIgnoreCase("Monthly"))			
			{
	        calendar.add(Calendar.MONTH, -1);
	        calendar.set(Calendar.HOUR_OF_DAY, 23);
	        calendar.set(Calendar.MINUTE, 59);
	        calendar.set(Calendar.SECOND, 59);
	        calendar.set(Calendar.MILLISECOND, 59); 
	        
	        Date lastDate = calendar.getTime();  
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	        
	        finallastdate = sdf.format(lastDate);
	        log.info("Last date of last month: " + (finallastdate)); 
	        
	        calendar.set(Calendar.DAY_OF_MONTH,1);
	        calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);

	        Date firstDate = calendar.getTime();  
	       
	       finalfirstdate = sdf.format(firstDate); 
	       log.info("first Date of last month: " + (finalfirstdate)); 
			}
	        
		if(startDateReport!=null)
			{
			Date firstDate = new Date(startDateReport.getTime());
			log.info("Start Date has been Set as " + firstDate);
			}
	 
		EraviewDBconnection dbConn = new EraviewDBconnection();
    	Connection conn = dbConn.getLocalDBConnection();
    	   	
        /* Define the SQL query */
    	Statement stmntPDF = conn.createStatement();
    	log.info("scheduleid-->"+scheduleid);
    	String reportIdPDF = null;
    	if(emailBodyReport.equalsIgnoreCase("N")){
    	ResultSet resPDF = stmntPDF.executeQuery("select * from dashboard_scheduler_report_map_pdf where schduler_id="+scheduleid);
    	if (resPDF.next() ) {    
     	   reportIdPDF = resPDF.getString(3);
	    	log.info("reportIdPDF-->"+reportIdPDF);
	    	exportFileNames.add(reportfileGenerate(reportIdPDF,finalfirstdate, finallastdate,reportInterval));
	    	fileSize++;
    	while(resPDF.next()){
 	    	reportIdPDF = resPDF.getString(3);
 	    	log.info("reportIdPDF for another file-->"+reportIdPDF);
 	    	exportFileNames.add(reportfileGenerate(reportIdPDF,finalfirstdate, finallastdate,reportInterval));
 	    	fileSize++;
 	    	log.info("fileSize-->"+fileSize);
 	    	
 	    }}
    	}else {
    		//Here in scheduleId we are getting reportIDEb
    		log.info("scheduleid-->"+scheduleid);
    		exportFileNames.add(reportfileGenerate(scheduleid,finalfirstdate, finallastdate,reportInterval));
    	}
		return exportFileNames;
	}
	
	 public String reportfileGenerate(String reportIdPDF,String finalfirstdate ,String finallastdate, String reportInterval) throws Exception {
		    EraviewDBconnection dbConn = new EraviewDBconnection();
		    java.sql.Connection conn = dbConn.getLocalDBConnection();
			Statement stmntreport = conn.createStatement();
			PreparedStatement stmnt = null;
			log.info("reportIdPDF-->"+reportIdPDF);
			ResultSet res = stmntreport.executeQuery("select * from report_template where report_template_id='"+reportIdPDF+"'");
			if (!res.next() ) {    
	     	   log.info("No data");
			}
	     	  String dbID = res.getString(7);
	     	 String definedQuery = res.getString(5);
	     	 String startdate = res.getString(19);
			 String enddate = res.getString(20);
			 String reportTemplate = res.getString(2);
			  String customerIdforEmail = res.getString(6);
			  String uploadLogoStatus = res.getString(21);
			  log.info("customerIdforEmail-->"+customerIdforEmail +"uploadLogoStatus-->"+uploadLogoStatus);
			 res = stmntreport.executeQuery("SELECT * FROM user_account where user_id='"+customerIdforEmail+"'");
			 if (!res.next() ) {    
		     	   log.info("No data");
				}
			 String email = res.getString(4);
    			log.info("email--->"+email);
		 	 res = StringReplaceAndexecuteReportQuery(email,dbID,definedQuery,startdate,enddate);
		 	 
		 	 //fetching uploaded logo file from company_logo table
		 	if(uploadLogoStatus.equalsIgnoreCase("true")) {
		 	File file=new File("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+reportTemplate+"_logo.png");
		 	FileOutputStream fos=new FileOutputStream(file);
		 	byte b[];
		 	Blob blob = null;
		 	
		 	ResultSet res_logo = stmntreport.executeQuery("SELECT logo_file FROM company_logo where customer_id='"+customerIdforEmail+"'");
		 	if(res_logo.next()){
		 		Blob imageBlob = res_logo.getBlob(1);
			 	b=imageBlob.getBytes(1,(int)imageBlob.length());
			 	fos.write(b);
			 	
			 	fos.close();
		 	}
		 	
		 	 }
		 	ResultSetMetaData rsmd = res.getMetaData();
	        int columnCount = rsmd.getColumnCount();
	       log.info("columnCount-"+columnCount);
	        /* Step-2: Initialize PDF documents - logical objects */
	       Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.BOLD, new CMYKColor( 100, 100, 37, 100));
	       Font fontForDate = FontFactory.getFont(FontFactory.COURIER_BOLD, 12,new CMYKColor(100, 100, 100, 100));
	        Document my_pdf_report = new Document();
	        PdfWriter.getInstance(my_pdf_report, new FileOutputStream("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+reportTemplate+ ".pdf"));
	        my_pdf_report.open(); 
	       
	        //Creating columns in table
	        PdfPTable table = new PdfPTable(columnCount);
	      
	       //Adding title in report
	        Paragraph pdfHeader = new Paragraph(reportTemplate+"  "+reportInterval, font);
	        Paragraph startEndDate =null;
	        log.info(" start date "+startdate+" end date "+enddate);
	        if((startdate.equalsIgnoreCase(null) && enddate.equalsIgnoreCase(null)) || (startdate.equalsIgnoreCase("") && enddate.equalsIgnoreCase(""))){
	        	log.info("interval start date "+finalfirstdate+" end date "+finallastdate);
	        	   startEndDate = new Paragraph("From : "+finalfirstdate+"      "+"To : "+finallastdate, fontForDate);
	        }else {
	        startEndDate = new Paragraph("From : "+startdate+"      "+"To : "+enddate, fontForDate);
	        }
	        pdfHeader.setSpacingBefore(-40);
	       pdfHeader.setSpacingAfter(20);
	       pdfHeader.setAlignment(Element.ALIGN_CENTER);
	       startEndDate.setAlignment(Element.ALIGN_CENTER);
	       if(uploadLogoStatus.equalsIgnoreCase("true")) {
	        Image image = Image.getInstance("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+reportTemplate+"_logo.png");
	       image.scaleToFit(60,60); 
	       my_pdf_report.add(image);}
	       writeDataPdf(table,columnCount,res,rsmd);
	        
	        /* Attach report table to PDF */
	        my_pdf_report.add(pdfHeader);
	        
	        if(!reportInterval.equalsIgnoreCase("")) {
	        	my_pdf_report.add(startEndDate);
	        }
	        my_pdf_report.add(Chunk.NEWLINE);
	        my_pdf_report.add(table);                       
	        my_pdf_report.close();
	        
	        /* Close all DB related objects */
	        res.close();
	        stmntreport.close(); 
	        conn.close();
			//return my_pdf_report;             

	       
		return reportTemplate;
	}

	private void writeDataPdf(PdfPTable table, int columnCount,ResultSet res, ResultSetMetaData rsmd) throws SQLException {
		table.setHeaderRows(1);
		PdfPCell cell;
		for(int i=0;i<columnCount;i++) {
			String cellData = rsmd.getColumnName(i+1);
       		cell=new PdfPCell(new Phrase(cellData));
               table.addCell(cell);	
		}
			while (res.next()) {  
			    for(int i=0; i<columnCount; i++) {
			      String cellData = res.getString(rsmd.getColumnName(i+1));
			       cell=new PdfPCell(new Phrase(cellData));
			         table.addCell(cell);
			       	}
			}
		}
	
	public ResultSet StringReplaceAndexecuteReportQuery(String email,String dbId, String definedQuery, String startdate, String enddate){
		ResultSet res = null;
		String database = null;
		String schema = null;
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			Connection localConnection = dbConn.getLocalDBConnection();
			String dbQuery = "select * from database_details where db_details_id = ?";
			PreparedStatement stmnt = localConnection.prepareStatement(dbQuery);
			stmnt.setString(1, dbId);
			ResultSet result = stmnt.executeQuery();
			while(result.next()){
				database = result.getString(2);
				schema = result.getString(7);
			}
			log.info("database-->"+database+" schema-->"+schema);
			Connection conn = dbConn.getUserDBConnection(email, database, schema);
			
				definedQuery = definedQuery.replace("{?StartDate}", startdate);
				definedQuery = definedQuery.replace("{?EndDate}", enddate);
			 
			PreparedStatement ps = conn.prepareStatement(definedQuery);
			res = ps.executeQuery();
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	 
	final Logger log = LoggerFactory.getLogger(TableToPDF.class);
	
}
