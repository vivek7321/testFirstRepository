package com.app.rpt;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.EraviewDBconnection;
import com.crystaldecisions.reports.sdk.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.*;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

public class reportGetRPTParameter {
	
	static final Logger LOGGER = LoggerFactory.getLogger(reportGetRPTParameter.class);

	public Hashtable getReportParameters(String reportname,String reportId) throws Exception {
		
		
		
		Hashtable listOfParameter = new Hashtable();
		String tempFileName ="/home/DownloadedFiles/paramtemp.rpt";
		OutputStream out1 = null;
		EraviewDBconnection dbConn = new EraviewDBconnection();
	    java.sql.Connection conn = dbConn.getLocalDBConnection();
	    Statement stmnt = conn.createStatement();
	    String databaseID = "";
	    ResultSet res = stmnt.executeQuery("select * from reportinfo where reportid="+reportId);
	    while(res.next()){
	    		
	    		reportname =res.getString(6).trim();
	    		System.out.print(res.getString(6));
	    		Blob blob = res.getBlob(9);
	    		LOGGER.debug( "BOLB'S LENGTH ----> " + blob.length());
	    		InputStream in = blob.getBinaryStream();
	    		LOGGER.debug( "Input stream ----> " + in.toString());
	    		out1 = new FileOutputStream(tempFileName);
	    		//byte[] buff = blob.getBytes(1,5000000);
	    		byte[] buff = blob.getBytes(1,(int)blob.length());
	    		LOGGER.debug( "Buffer length" + buff.length);
	    		out1.write(buff);
	    		System.out.println( "Buffer" +buff.length);
	    		databaseID = res.getString(3);
	    	
	    }

		ReportClientDocument clientDoc = new ReportClientDocument();
			
		clientDoc.open(tempFileName, ReportExportFormat._PDF);
		//clientDoc.open(getClass().getResource(report_name1).toURI().getPath(), ReportExportFormat._PDF);

		// Obtain collection of tables from this database controller.
		try {
			ITable table = clientDoc.getDatabaseController().getDatabase().getTables().getTable(0);

			IConnectionInfo connectionInfo = table.getConnectionInfo();
			
			connectionInfo = dbConn.getDBConnectionInfos(connectionInfo,databaseID);

			//ConnectionInfos connInfos = new ConnectionInfos();
			
			table.setConnectionInfo(connectionInfo);
			// Update old table in the report with the new table.
			clientDoc.getDatabaseController().setTableLocation(table, table);

			com.crystaldecisions.reports.sdk.DatabaseController dbController = clientDoc.getDatabaseController();
			Tables tables = dbController.getDatabase().getTables();
			ITable table2 = tables.getTable(0);
			System.out.println("tables.size --> " + tables.size());
			System.out.println("table2.getName() --> " + table2.getName());
			ArrayList arr = new ArrayList();

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
				System.out.println("param list --> " + key + " : " + listOfParameter.get(key));
			}

			  return listOfParameter;		

		} catch (ReportSDKException ex) {
			System.out.println("ReportSDKException" + ex);
			LOGGER.info("ReportSDKException" + ex);
			
		} catch (Exception ex) {
			System.out.println("Exception in reportGetParameters" + ex);
			
			LOGGER.info("Exception" + ex);
			ex.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){}
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){}
		}
		  return listOfParameter;
	}
}
