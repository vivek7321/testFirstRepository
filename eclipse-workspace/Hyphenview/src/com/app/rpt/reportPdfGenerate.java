package com.app.rpt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.sql.DataSource;
import java.sql.Blob;
//import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.EraviewDBconnection;
//import com.businessobjects.reports.crprompting.CRPromptValue.DateTime;
import com.crystaldecisions.reports.sdk.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.Fields;
//import com.crystaldecisions.sdk.occa.report.application.DatabaseController;
import com.crystaldecisions.sdk.occa.report.data.IConnectionInfo;
import com.crystaldecisions.sdk.occa.report.data.IField;
import com.crystaldecisions.sdk.occa.report.data.IProcedure;
import com.crystaldecisions.sdk.occa.report.data.ITable;
import com.crystaldecisions.sdk.occa.report.data.ParameterField;
import com.crystaldecisions.sdk.occa.report.data.Tables;
import com.crystaldecisions.sdk.occa.report.definition.AreaSectionKind;
import com.crystaldecisions.sdk.occa.report.exportoptions.DataOnlyExcelExportFormatOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.ExcelExportFormatOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.ExportOptions;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.lib.IStrings;
//import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class reportPdfGenerate {
	
	
	

	String exportFileName = "/home/DownloadedFiles/exporttemp.pdf";
	String tempFileName ="/home/DownloadedFiles/Scheduletemp.rpt";
	final Logger LOGGER = LoggerFactory.getLogger(reportPdfGenerate.class);

	public ArrayList<String> reportGenerate(String reportname,String reportId,String reportInterval,Timestamp startDateReport , String reportTitle,String scheduleid,String mailBody) throws Exception {
		ArrayList<String> exportFileNames = new ArrayList<String>();
		 Calendar cal = Calendar.getInstance();
		 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		 System.out.println("Today's date is "+dateFormat.format(cal.getTime()));

		 cal.add(Calendar.DATE, -1);
		 System.out.println("Yesterday's date was "+dateFormat.format(cal.getTime()));  	
		
		
		DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		
		Date firstDate = df2.parse("01/01/2014");
		Date lastDate = new Date();	
		System.out.println("firstDate" + firstDate + "  test    " + new Date());
		System.out.println("Report Interval " + reportInterval);
		
		if (reportInterval.equalsIgnoreCase("Daily"))			
		{
            Calendar calEnd = new GregorianCalendar();
            calEnd.setTime(new Date());
            calEnd.set(Calendar.DAY_OF_YEAR, calEnd.get(Calendar.DAY_OF_YEAR)-1);
            calEnd.set(Calendar.HOUR_OF_DAY, 0);
            calEnd.set(Calendar.MINUTE, 0);
            calEnd.set(Calendar.SECOND, 0);
            calEnd.set(Calendar.MILLISECOND, 0);
            firstDate = calEnd.getTime();	
            calEnd.set(Calendar.HOUR_OF_DAY, 23);
            calEnd.set(Calendar.MINUTE, 59);
            calEnd.set(Calendar.SECOND, 59);
            calEnd.set(Calendar.MILLISECOND, 59);
            lastDate = calEnd.getTime();

            System.out.println("Daily - firstDate " + firstDate);

            System.out.println("Daily - lastDate " + lastDate);
		}
		if (reportInterval.equalsIgnoreCase("DailyMTD"))			
		{
	        Calendar calEndDailyMTD = new GregorianCalendar();
	        calEndDailyMTD.setTime(new Date());
	        calEndDailyMTD.set(Calendar.DAY_OF_YEAR, calEndDailyMTD.get(Calendar.DAY_OF_YEAR)-1);

	        calEndDailyMTD.set(Calendar.HOUR_OF_DAY, 23);
	        calEndDailyMTD.set(Calendar.MINUTE, 59);
	        calEndDailyMTD.set(Calendar.SECOND, 59);
	        calEndDailyMTD.set(Calendar.MILLISECOND, 59);
	        lastDate = calEndDailyMTD.getTime();
	        
	        calEndDailyMTD.set(Calendar.DAY_OF_MONTH,calEndDailyMTD.getActualMinimum(Calendar.DAY_OF_MONTH));
	        calEndDailyMTD.set(Calendar.HOUR_OF_DAY, 0);
	        calEndDailyMTD.set(Calendar.MINUTE, 0);
	        calEndDailyMTD.set(Calendar.SECOND, 0);
	        calEndDailyMTD.set(Calendar.MILLISECOND, 0);
	        firstDate = calEndDailyMTD.getTime();	
            System.out.println("DailyMTD - firstDate " + firstDate);
            System.out.println("DailyMTD - lastDate " + lastDate);
		}
		
		
		if (reportInterval.equalsIgnoreCase("Weekly"))			
		{
	        Calendar calWeekly = new GregorianCalendar();
	        calWeekly.setTime(new Date());
	        calWeekly.set(Calendar.DAY_OF_YEAR, calWeekly.get(Calendar.DAY_OF_YEAR)-1);
	        calWeekly.set(Calendar.HOUR_OF_DAY, 23);
	        calWeekly.set(Calendar.MINUTE,59);
	        calWeekly.set(Calendar.SECOND, 59);
	        calWeekly.set(Calendar.MILLISECOND, 59);
	        lastDate = calWeekly.getTime();	
	        
	        calWeekly.set(Calendar.DAY_OF_YEAR, calWeekly.get(Calendar.DAY_OF_YEAR)-6);

	        calWeekly.set(Calendar.HOUR_OF_DAY, 0);
	        calWeekly.set(Calendar.MINUTE, 0);
	        calWeekly.set(Calendar.SECOND, 0);
	        calWeekly.set(Calendar.MILLISECOND, 0);
	        firstDate = calWeekly.getTime();
	        
	       	DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime("04/02/2011 20:27:05");

	        System.out.println("Weekly - firstDate " + firstDate);
	        System.out.println("Weekly lastDate " + lastDate) ;  
	      }
		if (reportInterval.equalsIgnoreCase("Monthly"))			
		{
	        Calendar calMonthly = new GregorianCalendar();
	        calMonthly.set(Calendar.DATE, 1);
	        calMonthly.add(Calendar.DAY_OF_MONTH, -1);
	        calMonthly.set(Calendar.HOUR_OF_DAY, 23);
	        calMonthly.set(Calendar.MINUTE,59);
	        calMonthly.set(Calendar.SECOND, 59);
	        calMonthly.set(Calendar.MILLISECOND, 59);
	        lastDate = calMonthly.getTime();	
	        
	        calMonthly.set(Calendar.DAY_OF_MONTH,calMonthly.getActualMinimum(Calendar.DAY_OF_MONTH));

	        calMonthly.set(Calendar.HOUR_OF_DAY, 0);
	        calMonthly.set(Calendar.MINUTE, 0);
	        calMonthly.set(Calendar.SECOND, 0);
	        calMonthly.set(Calendar.MILLISECOND, 0);
	        firstDate = calMonthly.getTime();
	        System.out.println("Monthly - firstDate " + firstDate);
	        System.out.println("Monthly lastDate " + lastDate) ; 
	        
		}
		if(startDateReport!=null)
		{
			firstDate = new Date(startDateReport.getTime());
			System.out.println("Start Date has been Set as " + firstDate);
		}
		
		String report_name1 = "/reports/" + reportname + ".rpt";
		System.out.println(report_name1);
		
		ClassLoader classLoader = getClass().getClassLoader();
		//File file = new File(classLoader.getResource("/SchedulerMail.java").getFile());
		//System.out.println("filepath" + file.getAbsolutePath());
		
		//InputStream is = getClass().getResourceAsStream("/reports/Affected Services.rpt"); 
		
		//File file1 = new File (getClass().getResource("/reports/Affected Services.rpt").getFile());

		//System.out.println("filepath" + file1.getPath());
		//ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
	   //String file2 = classLoader1.getResourceAsStream("/reports/Affected Services.rpt").toString();
	    
	    System.out.println (report_name1);
	    EraviewDBconnection dbConn = null;
	    java.sql.Connection conn = null;
	    Statement stmnt = null; 
	    int fileSize =0;
	    if (mailBody.equalsIgnoreCase("N"))
	    {
	   try
	   {
		   dbConn = new EraviewDBconnection();
		   conn = dbConn.getLocalDBConnection();
		   stmnt = conn.createStatement();
	    String reportIdPDF = null , reportIdXLS = null ;
	    
	    ResultSet resPDF = stmnt.executeQuery("select * from scheduler_report_map_pdf where schduler_id="+scheduleid);
	    while(resPDF.next()){
	    	reportIdPDF = resPDF.getString(3);
	    	exportFileNames.add(reportfileGenerate(reportTitle,reportIdPDF,"PDF",firstDate, lastDate));
	    	fileSize++;
	    }
	    ResultSet resXLS = stmnt.executeQuery("select * from scheduler_report_map_xls where schduler_id="+scheduleid);
	    while(resXLS.next()){
	    	reportIdXLS = resXLS.getString(3);
	    	exportFileNames.add(reportfileGenerate(reportTitle,reportIdXLS,"XLS",firstDate, lastDate));
	    	fileSize++;
	    }
	   }
	    catch (Exception ex) {
			System.out.println("Exception" + ex);
			LOGGER.info("Exception" + ex);
		}finally{
			try{
				
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){}
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){}
		}
	    
	   System.out.println("exportFileNames" + exportFileNames.size() + exportFileNames.toString()); 
	    }
	    else // For Report EMail Body Content
	    {
	    	exportFileNames.add(reportfileGenerate(reportTitle,reportId,"PDF",firstDate, lastDate));
	    }
	    	
  return exportFileNames;
	}
	
public String reportfileGenerate(String reportTitle,String reportId, String reportType,Date firstDate ,Date lastDate ) throws Exception
{
	OutputStream out1 = null;
    EraviewDBconnection dbConn = new EraviewDBconnection();
    java.sql.Connection conn = dbConn.getLocalDBConnection();
	Statement stmntreport = conn.createStatement();
	String databaseID = "";
	ResultSet res = stmntreport.executeQuery("select * from reportinfo where reportid="+reportId);
    while(res.next()){
    		
    	reportTitle =res.getString(2).trim();
    		System.out.print(res.getString(2));
    		Blob blob = res.getBlob(9);
    		LOGGER.debug( "BOLB'S LENGTH ----> " + blob.length());
    		InputStream in = blob.getBinaryStream();
    		tempFileName = "/home/DownloadedFiles/"+reportTitle+ ".rpt";
    		out1 = new FileOutputStream(tempFileName);
    		//byte[] buff = blob.getBytes(1,5000000);
    		byte[] buff = blob.getBytes(1,(int)blob.length());
    		LOGGER.debug( "Buffer length" + buff.length);
    		out1.write(buff);
    		System.out.println( "Buffer" +buff.length);
    		System.out.println( "FileName" +tempFileName);
    		databaseID = res.getString(3);
    	
    }
    
    ReportClientDocument clientDoc = new ReportClientDocument();
	
	
	//clientDoc.open(getClass().getResource(report_name1).toURI().getPath(), ReportExportFormat._PDF);
	if(reportType.equalsIgnoreCase("PDF"))
	{
	clientDoc.open(tempFileName, ReportExportFormat._PDF);
	}
	ExcelExportFormatOptions ext = new ExcelExportFormatOptions();	
	ext.setExcelTabHasColumnHeadings(true);
	
	
	
	if(reportType.equalsIgnoreCase("XLS"))
	{
		clientDoc.open(tempFileName, ReportExportFormat._MSExcel);
	}
	// Obtain collection of tables from this database controller.
	try {
		ITable table = clientDoc.getDatabaseController().getDatabase().getTables().getTable(0);
		IConnectionInfo connectionInfo = table.getConnectionInfo();
		//ConnectionInfos connInfos = new ConnectionInfos();
		connectionInfo = dbConn.getDBConnectionInfos(connectionInfo,databaseID);
		table.setConnectionInfo(connectionInfo);
		clientDoc.getDatabaseController().setTableLocation(table, table);
		ITable origTable = null;
		ITable newTable = null;

		IStrings subNames = clientDoc.getSubreportController().getSubreportNames();
		System.out.println("SubNames" + subNames);
		for (int subNum=0;subNum<subNames.size();subNum++) {
		    Tables tables1 = clientDoc.getSubreportController().getSubreport(subNames.getString(subNum)).getDatabaseController().getDatabase().getTables();
		    for(int i = 0;i < tables1.size();i++){
		        origTable = tables1.getTable(i);
		        newTable = (ITable)origTable.clone(true);
		            newTable.setQualifiedName(origTable.getAlias());
		            connectionInfo = newTable.getConnectionInfo();
					connectionInfo = dbConn.getDBConnectionInfos(connectionInfo,databaseID);
		             clientDoc.getSubreportController().getSubreport(subNames.getString(subNum)).getDatabaseController().setTableLocation(origTable, newTable);
		   
		    }
		}
		com.crystaldecisions.reports.sdk.DatabaseController dbController = clientDoc.getDatabaseController();
		Tables tables = dbController.getDatabase().getTables();
		ITable table2 = tables.getTable(0);
		Hashtable listOfParameter = new Hashtable();
		ArrayList arr = new ArrayList();

		IStrings subNames1 = clientDoc.getSubreportController().getSubreportNames();
		System.out.println("SubNames" + subNames1);
		
		if (table instanceof com.crystaldecisions.sdk.occa.report.data.CommandTable) {
			IProcedure command = (IProcedure) table2;
			for (int i = 0; i < command.getParameters().size(); i++) {
				ParameterField commandParam = (ParameterField) command.getParameters().get(i);
				String paramName = commandParam.getName();
				String paramType = commandParam.getType().toString().substring(4);
				if (paramType.equalsIgnoreCase("decimal")) {
					paramType = "int";
				}
				paramType = paramType.toLowerCase();
				listOfParameter.put(paramName, paramType);
			}
		}else{
			Fields fields = clientDoc.getDataDefController().getDataDefinition().getParameterFields();
			System.out.println("fields size -->" + fields.size());
			for (int i = 0; i < fields.size(); i++) {
				ParameterField commandParam = (ParameterField) fields.get(i);
				String paramName = commandParam.getName();
				String paramType = commandParam.getType().toString().substring(4);
				if (paramType.equalsIgnoreCase("decimal")) {
					paramType = "int";
				}
				paramType = paramType.toLowerCase();
				listOfParameter.put(paramName, paramType);
			}
		}
		Enumeration e = listOfParameter.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			arr.add(key);
			System.out.println(key + " : " + listOfParameter.get(key));
		}
		
		String paramName ="";
		
		for (int param=0;param<arr.size();param++)
		{
			paramName = arr.get(param).toString().toUpperCase();
			
			System.out.println("Parameter for PDF Generate" +  paramName);
			
			if (paramName.contains("START") || paramName.contains("FIRST")) {
				clientDoc.getDataDefController().getParameterFieldController().setCurrentValue("", arr.get(param).toString(),
						firstDate);
			}
			if (paramName.contains("END") || paramName.contains("LAST")) {
				clientDoc.getDataDefController().getParameterFieldController().setCurrentValue("",arr.get(param).toString(),
						lastDate);
			}
			
	        for (int subNum1=0;subNum1<subNames1.size();subNum1++) {
	        	System.out.println("SubReport Name" + subNames1.getString(subNum1));
	        	Fields fieldssub = clientDoc.getSubreportController().getSubreport(subNames1.getString(subNum1)).getDataDefController().getDataDefinition().getParameterFields();
	        	Iterator fiter = fieldssub.iterator();
	        	  
	            while (fiter.hasNext()) {
	                IField ifld = (IField) fiter.next();
	                
					if (paramName.contains("START") || paramName.contains("FIRST")) {
						clientDoc.getDataDefController().getParameterFieldController().setCurrentValue(subNames1.getString(subNum1), arr.get(param).toString(),
								firstDate);
					}
					if (paramName.contains("END") || paramName.contains("LAST")) {
						clientDoc.getDataDefController().getParameterFieldController().setCurrentValue(subNames1.getString(subNum1), arr.get(param).toString(),
								lastDate);
					} 
	               
	                }
	        	
	        }		
			
		}
		
		

		
		  DataOnlyExcelExportFormatOptions excelOptions = new DataOnlyExcelExportFormatOptions	();
		  ExportOptions exportOptions = new ExportOptions();	

	        excelOptions.setBaseAreaType(AreaSectionKind.detail);
	      
	        excelOptions.setMaintainRelativeObjectPosition(true);
	        excelOptions.setExportImages(true);
	        excelOptions.setExportObjectFormatting(true);
	        excelOptions.setExportPageHeaderAndFooter(true);
	        excelOptions.setSimplifyPageHeaders(true);
	   	    exportOptions.setFormatOptions(excelOptions);
	        exportOptions.setExportFormatType(ReportExportFormat.recordToMSExcel);		
			

		ByteArrayInputStream bais=null;
		if(reportType.equalsIgnoreCase("PDF"))
		{
		 bais = (ByteArrayInputStream) clientDoc.getPrintOutputController()
				.export(ReportExportFormat.PDF);
		 exportFileName="/home/DownloadedFiles/"+reportTitle+ ".pdf";;
		}
		if(reportType.equalsIgnoreCase("XLS"))
		{
			bais = (ByteArrayInputStream) clientDoc.getPrintOutputController()
					.export(exportOptions);
			exportFileName="/home/DownloadedFiles/"+reportTitle+ ".xls";;
		}
		
		
		int size = bais.available();
		byte[] barray = new byte[size];
		
		File f = new File (exportFileName);
		f.setWritable(true);
		//f.delete();			
		FileOutputStream fos = new FileOutputStream(f,false);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
		int bytes = bais.read(barray, 0, size);
		
		baos.write(barray, 0, bytes);
		baos.writeTo(fos);
		//f.setWritable(true);
		fos.close();
		clientDoc.close();
		conn.close();
		baos.close();
		out1.close();
		bais.close();

		

	} catch (ReportSDKException ex) {
		System.out.println("ReportSDKException" + ex);
		LOGGER.info("ReportSDKException" + ex);
		
		
	} catch (Exception ex) {
		System.out.println("Exception" + ex);
		LOGGER.info("Exception" + ex);
	}finally{
		try{
			if(null != stmntreport){stmntreport.close();}
		}catch(Exception e){}
		try{
			if(null != conn){conn.close();}
		}catch(Exception e){}
	}
	System.out.println("exportFileName" + exportFileName);
	return exportFileName;
}
}
