package com.app.rpt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
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

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.EraviewDBconnection;
import com.app.dao.QueryDao;
import com.itextpdf.text.Header;
import com.itextpdf.text.Image;
 
public class TableToXLS {
 
    public static void main(String[] args) throws Exception {
        }
     
    public ArrayList<String> export(String scheduleid,String reportInterval, Timestamp startDateReport) throws Exception {
 
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
        	 
        	Statement stmntXLS = conn.createStatement();
        	  log.info("scheduleid-->"+scheduleid);
        	String reportIdxls = null;
      
        	ResultSet resXLS = stmntXLS.executeQuery("select * from dashboard_scheduler_report_map_xls where schduler_id="+scheduleid);
      
        	if (resXLS.next() ) {    
         	   reportIdxls = resXLS.getString(3);
         	   log.info("reportIdxls-->"+reportIdxls);
    	    	exportFileNames.add(reportfileGenerate(reportIdxls,finalfirstdate,finallastdate,reportInterval));
    	    	fileSize++;
    	    	  log.info("reportIdxls-->"+reportIdxls);
        	while(resXLS.next()){
        		reportIdxls = resXLS.getString(3);
        		  log.info("reportIdxls-->"+reportIdxls);
     	    	exportFileNames.add(reportfileGenerate(reportIdxls, finalfirstdate, finallastdate,reportInterval));
     	    	fileSize++;
     	    	  log.info("fileSize-->"+fileSize);
        			}
        		}
    		
    		return exportFileNames;
    	}
    	
        	
        	
        	public String reportfileGenerate(String reportIdxls, String finalfirstdate, String finallastdate, String reportInterval) throws Exception {
        	  EraviewDBconnection dbConn = new EraviewDBconnection();
      		    java.sql.Connection conn = dbConn.getLocalDBConnection();
      			Statement stmntreport = conn.createStatement();
      		     log.info("reportIdxls-->"+reportIdxls);	
      			
      			 ResultSet res = stmntreport.executeQuery("SELECT * FROM report_template where report_template_id='"+reportIdxls+"'");
      			if (!res.next() ) {    
    	     	   log.info("no Data"); 
    	     	}
      			 String dbID = res.getString(7);
      			 String startdate = res.getString(19);
    			 String enddate = res.getString(20);
    			 String uploadLogoStatus = res.getString(21);
      			 String report_template_name = res.getString(2);
                 String definedQuery = res.getString(5);
                 String customerIdforEmail = res.getString(6);
                 log.info(definedQuery);
                 log.info("startdate-->"+startdate+"enddate-->"+enddate);
                  res = stmntreport.executeQuery("SELECT * FROM database_details where db_details_id='"+dbID+"'");
       			if (!res.next() ) {    
     	     	   log.info("no Data"); 
     	     	}
       			String dbName = res.getString(2);
       			String Schema = res.getString(7);
       			log.info("database-->"+dbName+" schema-->"+Schema);
       			res = stmntreport.executeQuery("SELECT * FROM user_account where user_id='"+customerIdforEmail+"'");
       			if (!res.next() ) {    
     	     	   log.info("no Data"); 
     	     	}
       			String email = res.getString(4);
       			log.info("email--->"+email);
                 //putting dates in sql generic query  
       			int columnCount = 0;
       			ResultSetMetaData rsmd;
       			
       		 String excelFilePath = "C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+report_template_name+ ".xlsx";
             QueryDao queryDao = new QueryDao();
                 if(finalfirstdate != null && finallastdate != null ) {
     				definedQuery = definedQuery.replace("{?StartDate}",startdate);
     				definedQuery = definedQuery.replace("{?EndDate}", enddate);
     				log.info("definedQuery-->"+definedQuery);
     				 res =  queryDao.executeQueryRes(email, definedQuery, dbName,Schema);
     				 rsmd = res.getMetaData();
     				 columnCount = rsmd.getColumnCount();
                    log.info("Number of column: "+columnCount);
     				
                 }else {
                	  res =  queryDao.executeQueryRes(email, definedQuery, dbName,Schema);
                	  rsmd = res.getMetaData();
      				 columnCount = rsmd.getColumnCount();
                     log.info("Number of column: "+columnCount);
                     log.info("definedQuery-->"+definedQuery);
                 }
                 XSSFWorkbook workbook = new XSSFWorkbook();
                 XSSFSheet sheet = workbook.createSheet("Report_Sheet");
                 
               //fetching uploaded logo file from company_logo table
                 if(uploadLogoStatus.equalsIgnoreCase("true")) {
                 File file=new File("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+report_template_name+"_xlsxlogo.png");
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
     		 
                 //setting up header margin
                 InputStream inputStream1 = new FileInputStream("C:/apache-tomcat-9.0.56/downloadedFiles/"+report_template_name+"_xlsxlogo.png");
                 byte[] inputImageBytes1 = IOUtils.toByteArray(inputStream1);
                 int inputImagePictureID1 = workbook.addPicture(inputImageBytes1, workbook.PICTURE_TYPE_PNG);
                 XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
                 XSSFClientAnchor logoImage = new XSSFClientAnchor();
                 logoImage.setCol1(1); // Sets the column (0 based) of the first cell.
                 logoImage.setCol2(2); // Sets the column (0 based) of the Second cell.
                 logoImage.setRow1(0); // Sets the row (0 based) of the first cell.
                 logoImage.setRow2(1); // Sets the row (0 based) of the Second cell.
                 
                 drawing.createPicture(logoImage, inputImagePictureID1);
                 	}
                 
                 Row titleRow = sheet.createRow(1);
                 org.apache.poi.ss.usermodel.Cell
                 titleCell = titleRow.createCell(2);
                 titleCell.setCellValue(report_template_name+" - "+reportInterval);
                 sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 4));
                 
                 writeHeaderLine(sheet, columnCount, rsmd);
                 writeDataLines(res, workbook, sheet,columnCount, rsmd );
      
                 FileOutputStream outputStream = new FileOutputStream(excelFilePath);
                 workbook.write(outputStream);
                 workbook.close();
                 res.close();
                 stmntreport.close();
                 conn.close();
      
                 

		return report_template_name;
	}

    private void writeHeaderLine(XSSFSheet sheet, int columnCount, ResultSetMetaData rsmd) throws SQLException {
 
        Row headerRow = sheet.createRow(3);
        for (int i=0; i<columnCount; i++) {
        	org.apache.poi.ss.usermodel.Cell 
            
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(rsmd.getColumnName(i+1));
        	
        }
 
          }
 
    private void writeDataLines(ResultSet result, XSSFWorkbook workbook,
            XSSFSheet sheet, int columnCount, ResultSetMetaData rsmd) throws SQLException {
        int rowCount = 4;
 
        while (result.next()) {
        	Row row = sheet.createRow(rowCount++);
        	for(int i=0; i<columnCount; i++) {
        		String cellData = result.getString(rsmd.getColumnName(i+1));
        		int columnNumber = i;
        		org.apache.poi.ss.usermodel.Cell 
                cell = row.createCell(columnNumber++);
                cell.setCellValue(cellData);
        		
        		
        	}
        }
    }
    
    final Logger log = LoggerFactory.getLogger(TableToXLS.class);
}