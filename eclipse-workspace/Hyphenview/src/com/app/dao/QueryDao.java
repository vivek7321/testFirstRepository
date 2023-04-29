package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.constant.QueryConstants;
import com.app.object.Output;
import com.app.object.ReportTemplate;
import com.microsoft.sqlserver.jdbc.SQLServerException;

public class QueryDao {
	
	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnection.class);
	
	public Output compileQuery(String email, String query, String database, String schema){
		String resultMessage = "";
		String msgCode = null;
		String errMessage = "The query compilation failed. ";
		Output output = new Output();
		List<String> headerList = null;
		List<String> columnData = null;
		List<List<String>> rowData = null;
		List<String> chartTypeData = null;
		Connection conn = null;
		PreparedStatement stmnt = null;
		int rowcount=0;
		
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				conn = dbConn.getUserDB2ConnForElements(email, database);
			}
			else if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
				conn = dbConn.getUserVerticaConnElements(email, database);
			}else{
				conn = dbConn.getUserDBConnection(email, database, schema);
			}
			
			if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
				stmnt = conn.prepareStatement(query);				
			}
			else
			{
			stmnt = conn.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			}
			
			if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
				ResultSet resCount = stmnt.executeQuery();
				while(resCount.next())
				{
					rowcount++;
				}
				
			}
			ResultSet res = stmnt.executeQuery();
			
			boolean noData = true;
			
			
			
			
			int columnCount = res.getMetaData().getColumnCount();
			

			
			if(columnCount==1)
			{
				if(!database.equalsIgnoreCase(QueryConstants.VERTICA)){
				res.last();
				rowcount =res.getRow();
				}
				
				if(rowcount == 1)
				{
				int columnType = res.getMetaData().getColumnType(1);
				if(columnType == -6 || columnType == -5 || columnType == 2 || columnType == 3 || columnType == 4 ||
						columnType == 5 || columnType == 6  || columnType == 7  || columnType == 8){
				resultMessage = "The query is compiled successfully and it is suitable only for BOX Report Type.";
				msgCode = "1";
				chartTypeData = new ArrayList<String>();	
				chartTypeData.add("Box");
				}
				}
			
				else{
					resultMessage = "The query output is not structured properly and the column should be a number.";
					msgCode = "0";
					chartTypeData = new ArrayList<String>();	
					chartTypeData.add("Table");
					chartTypeData.add("Matrix");
				}
				if(!database.equalsIgnoreCase(QueryConstants.VERTICA)){
				
				res.beforeFirst();
				}
			}			
			
			else if(columnCount < 2 || columnCount > 2){
				if(columnCount == 3){
					int columnType = res.getMetaData().getColumnType(3);
					if(columnType == -6 || columnType == -5 || columnType == 2 || columnType == 3 || columnType == 4 ||
							columnType == 5 || columnType == 6  || columnType == 7  || columnType == 8){
					resultMessage = "The query is compiled successfully.";
					msgCode = "1";
					chartTypeData = new ArrayList<String>();	
					chartTypeData.add("Chart");
					chartTypeData.add("Table");
					chartTypeData.add("Matrix");
					
					int columnTypeFirst = res.getMetaData().getColumnType(1);
					if(columnTypeFirst == -6 || columnTypeFirst == -5 || columnTypeFirst == 2 || columnTypeFirst == 3 || columnTypeFirst == 4 ||
							columnTypeFirst == 5 || columnTypeFirst == 6  || columnTypeFirst == 7  || columnTypeFirst == 8){
						
					}
					}
					else{
						resultMessage = "The query output is not structured properly to create the chart and third column should be a number.";
						msgCode = "0";
						chartTypeData = new ArrayList<String>();	
						chartTypeData.add("Table");
						chartTypeData.add("Matrix");
					}
				}else{
					resultMessage = "The query output is not structured properly to create the chart.";
					msgCode = "0";
					chartTypeData = new ArrayList<String>();	
					chartTypeData.add("Table");
					chartTypeData.add("Matrix");
				}
			}else if(columnCount >= 2){
				int columnType = res.getMetaData().getColumnType(2);
				if(columnType == -6 || columnType == -5 || columnType == 2 || columnType == 3 || columnType == 4 ||
						columnType == 5 || columnType == 6  || columnType == 7  || columnType == 8){
				resultMessage = "The query is compiled successfully.";
				msgCode = "1";
				
				chartTypeData = new ArrayList<String>();
				chartTypeData.add("Chart");
				chartTypeData.add("Table");
				chartTypeData.add("Matrix");
				
				int columnTypeFirst = res.getMetaData().getColumnType(2);
				if(columnTypeFirst == -6 || columnTypeFirst == -5 || columnTypeFirst == 2 || columnTypeFirst == 3 || columnTypeFirst == 4 ||
						columnTypeFirst == 5 || columnTypeFirst == 6  || columnTypeFirst == 7  || columnTypeFirst == 8){
					
				}
				
				}
				else{
					resultMessage = "The query output is not structured properly to create the chart. The second column should be a number.";
					msgCode = "0";
					chartTypeData = new ArrayList<String>();	
					chartTypeData.add("Table");
					chartTypeData.add("Matrix");
				}
			}
			
			// Iterate the resultset and return the data
			if(columnCount>0){
				headerList = new ArrayList<String>();
				rowData = new ArrayList<>();
				for(int i=1;i<=columnCount; i++){
					headerList.add(res.getMetaData().getColumnName(i));
				}
				while(res.next())
				{
					noData = false;
					columnData = new ArrayList<>();
					for(int i=1;i<=columnCount; i++){
						columnData.add(res.getString(i));
					}
					rowData.add(columnData);
				}
			}
			output.setHeaderList(headerList);
			output.setData(rowData);
			
			if(noData){
				resultMessage = "No data is present to generate the report.";
				msgCode = "-1";
			}
			
		 }catch(SQLServerException sqlexception){
			 resultMessage = errMessage.concat(sqlexception.getMessage()).concat(".");
			 msgCode = "-1";
			 LOGGER.info(sqlexception.getMessage());
		 }
		catch(SQLException se){
			 se.printStackTrace();
			 resultMessage = errMessage.concat(se.getMessage()).concat(".");
			 msgCode = "-1";
			 LOGGER.info(se.getMessage());
		 } catch (Exception e) {
			e.printStackTrace();
			resultMessage = errMessage.concat(e.getMessage()).concat(".");
			msgCode = "-1";
			LOGGER.info(e.getMessage());
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
		}
		
		output.setMessage(resultMessage);
		output.setMessageCode(msgCode);
		output.setChartTypeData(chartTypeData);
		return output;
	}

	public Map<String, Object> executeQuery(String email, String query, String database, String schema){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		ResultSet res = null;
		Connection conn = null;
		
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				conn = dbConn.getUserDB2ConnForElements(email, database);
			}
			else if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
				conn = dbConn.getUserVerticaConnElements(email, database);
			}else{
				conn = dbConn.getUserDBConnection(email, database, schema);
			}
			PreparedStatement stmnt = conn.prepareStatement(query);
			res = stmnt.executeQuery();
			
			outputMap.put("connection", conn);
			outputMap.put("resultset", res);
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}
		return outputMap;
	}
	
	public ResultSet executeQueryRes(String email, String query, String database, String schema){
		ResultSet res = null;
		
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			Connection conn = null;
			if (database.equalsIgnoreCase("vertica"))
			{
				conn = dbConn.getUserVerticaConnElements(email, database);
			}
			else
			conn = dbConn.getUserDBConnection(email, database, schema);		
			
			PreparedStatement stmnt = conn.prepareStatement(query);
			res = stmnt.executeQuery();
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}	
	
	public ResultSet executeReportQuery(ReportTemplate reportTemplate){
		ResultSet res = null;
		String database = null;
		String schema = null;
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			Connection localConnection = dbConn.getLocalDBConnection();
			String dbQuery = "select * from database_details where db_details_id = ?";
			PreparedStatement stmnt = localConnection.prepareStatement(dbQuery);
			stmnt.setString(1, reportTemplate.getDatabaseId());
			ResultSet result = stmnt.executeQuery();
			while(result.next()){
				database = result.getString(2);
				schema = result.getString(7);
			}
			
			Connection conn = dbConn.getUserDBConnection(reportTemplate.getEmail(), database, schema);
			PreparedStatement ps = conn.prepareStatement(reportTemplate.getDefinedQuery());
			res = ps.executeQuery();
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
		
	
	public ResultSet compileWidgetQuery(String email, String customQuery, String database, String schema){
		ResultSet res = null;
		Connection conn = null;
		
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				conn = dbConn.getUserDB2ConnForElements(email, database);
			}
			else if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
				conn = dbConn.getUserVerticaConnElements(email, database);
			}else{
				conn = dbConn.getUserDBConnection(email, database, schema);
			}
			PreparedStatement stmnt = conn.prepareStatement(customQuery);
			res = stmnt.executeQuery();
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public Map<String, Object> executeReportQuery(ReportTemplate reportTemplate, String emailId){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		ResultSet res = null;
		String database = null;
		String schema = null;
		Connection localConnection = null;
		PreparedStatement stmnt = null;
		Connection conn = null;
		String query = null;
		String startDate = null;
		String endDate = null;
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			localConnection = dbConn.getLocalDBConnection();
			String dbQuery = "select * from database_details where db_details_id = ?";
			stmnt = localConnection.prepareStatement(dbQuery);
			stmnt.setString(1, reportTemplate.getDatabaseId());
			ResultSet result = stmnt.executeQuery();
			while(result.next()){
				database = result.getString(2);
				schema = result.getString(7);
			}
			
			if (database.equalsIgnoreCase("vertica"))
			{
				conn = dbConn.getUserVerticaConnElements(emailId, database);
			}
			else
			conn = dbConn.getUserDBConnection(emailId, database, schema);
			query = reportTemplate.getDefinedQuery();
			startDate = reportTemplate.getStartDate();
			endDate = reportTemplate.getEndDate();
			System.out.println("startDate inside QueryDao-->"+startDate+"\nendDate inside QueryDao-->"+endDate);
			if(startDate !=null || endDate !=null) {
				String query1 = query.replace("{?StartDate}", startDate);
				String query2 = query1.replace("{?EndDate}", endDate);
				System.out.println("query1-->"+query1+" query2-->"+query2);
				query = query2;
			}
			PreparedStatement ps = conn.prepareStatement(query);
			res = ps.executeQuery();
			outputMap.put("connection", conn);
			outputMap.put("resultset", res);
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != localConnection){localConnection.close();}
			}catch(Exception e){ }
		}
		return outputMap;
	}
	
	public Map<String, Object> executeDrilldownQuery(ReportTemplate reportTemplate, String emailId, String series, String category){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		ResultSet res = null;
		String database = null;
		String schema = null;
		Connection localConnection = null;
		PreparedStatement stmnt = null;
		Connection conn = null;
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			System.out.println("connection created for db fetch...");
			localConnection = dbConn.getLocalDBConnection();
			String dbQuery = "select * from database_details where db_details_id = ?";
			stmnt = localConnection.prepareStatement(dbQuery);
			stmnt.setString(1, reportTemplate.getDatabaseId());
			ResultSet result = stmnt.executeQuery();
			while(result.next()){
				database = result.getString(2);
				schema = result.getString(7);
			}
			
			if (database.equalsIgnoreCase("vertica"))
			{
				conn = dbConn.getUserVerticaConnElements(emailId, database);
			}
			else
			conn = dbConn.getUserDBConnection(emailId, database, schema);
			
			PreparedStatement ps = conn.prepareStatement(reportTemplate.getDefinedQuery());
			
			if(null == series || series.isEmpty() || series.equalsIgnoreCase("null")){
				ps.setString(1, category);
			}
			
			else{
				ps.setString(1, category);
				ps.setString(2, series);
			}
			res = ps.executeQuery();
			outputMap.put("connection", conn);
			outputMap.put("statement", ps);
			outputMap.put("resultset", res);
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != localConnection){localConnection.close();}
			}catch(Exception e){ }
			/*try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }*/
		}
		return outputMap;
	}

	
	public Map<String, Object> executeBoxDrilldownQuery(ReportTemplate reportTemplate, String emailId){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		ResultSet res = null;
		String database = null;
		String schema = null;
		Connection localConnection = null;
		PreparedStatement stmnt = null;
		Connection conn = null;
		try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			System.out.println("connection created for db fetch...");
			localConnection = dbConn.getLocalDBConnection();
			String dbQuery = "select * from database_details where db_details_id = ?";
			stmnt = localConnection.prepareStatement(dbQuery);
			stmnt.setString(1, reportTemplate.getDatabaseId());
			ResultSet result = stmnt.executeQuery();
			while(result.next()){
				database = result.getString(2);
				schema = result.getString(7);
			}
			
			if (database.equalsIgnoreCase("vertica"))
			{
				conn = dbConn.getUserVerticaConnElements(emailId, database);
			}
			else
			conn = dbConn.getUserDBConnection(emailId, database, schema);
			PreparedStatement ps = conn.prepareStatement(reportTemplate.getDefinedQuery());
			
			res = ps.executeQuery();
			outputMap.put("connection", conn);
			outputMap.put("statement", ps);
			outputMap.put("resultset", res);
		 }
		catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != localConnection){localConnection.close();}
			}catch(Exception e){ }
			/*try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }*/
		}
		return outputMap;
	}
	
//	public Output compileQuery(String email, String query, String database, String schema, String startDate, String endDate){
//		String resultMessage = "";
//		String msgCode = null;
//		String errMessage = "The query compilation failed. ";
//		Output output = new Output();
//		List<String> headerList = null;
//		List<String> columnData = null;
//		List<List<String>> rowData = null;
//		List<String> chartTypeData = null;
//		Connection conn = null;
//		PreparedStatement stmnt = null;
//		System.out.println("startDate inside QueryDao-->"+startDate+"\nendDate inside QueryDao-->"+endDate);
//		String query1 = query.replace("{?StartDate}", startDate);
//		String query2 = query1.replace("{?EndDate}", endDate);
//		System.out.println("query1-->"+query1+"    query2-->"+query2);
//		query = query2;
//		int rowcount=0;
//		
//		try{
//			EraviewDBconnection dbConn = new EraviewDBconnection();
//			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER)){
//				conn = dbConn.getUserDB2ConnForElements(email, database);
//			}
//			else if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
//				conn = dbConn.getUserVerticaConnElements(email, database);
//			}else{
//				conn = dbConn.getUserDBConnection(email, database, schema);
//			}
//			
//			if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
//				stmnt = conn.prepareStatement(query);				
//			}
//			else
//			{
//			stmnt = conn.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
//			}
//			
//			if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
//				ResultSet resCount = stmnt.executeQuery();
//				while(resCount.next())
//				{
//					rowcount++;
//				}
//				
//			}
//			ResultSet res = stmnt.executeQuery();
//			
//			boolean noData = true;
//			
//			
//			
//			
//			int columnCount = res.getMetaData().getColumnCount();
//			
//
//			
//			if(columnCount==1)
//			{
//				if(!database.equalsIgnoreCase(QueryConstants.VERTICA)){
//				res.last();
//				rowcount =res.getRow();
//				}
//				
//				if(rowcount == 1)
//				{
//				int columnType = res.getMetaData().getColumnType(1);
//				if(columnType == -6 || columnType == -5 || columnType == 2 || columnType == 3 || columnType == 4 ||
//						columnType == 5 || columnType == 6  || columnType == 7  || columnType == 8){
//				resultMessage = "The query is compiled successfully and it is suitable only for BOX Report Type.";
//				msgCode = "1";
//				chartTypeData = new ArrayList<String>();	
//				chartTypeData.add("Box");
//				}
//				}
//			
//				else{
//					resultMessage = "The query output is not structured properly and the column should be a number.";
//					msgCode = "0";
//					chartTypeData = new ArrayList<String>();	
//					chartTypeData.add("Table");
//					chartTypeData.add("Matrix");
//				}
//				if(!database.equalsIgnoreCase(QueryConstants.VERTICA)){
//				
//				res.beforeFirst();
//				}
//			}			
//			
//			else if(columnCount < 2 || columnCount > 2){
//				if(columnCount == 3){
//					int columnType = res.getMetaData().getColumnType(3);
//					if(columnType == -6 || columnType == -5 || columnType == 2 || columnType == 3 || columnType == 4 ||
//							columnType == 5 || columnType == 6  || columnType == 7  || columnType == 8){
//					resultMessage = "The query is compiled successfully.";
//					msgCode = "1";
//					chartTypeData = new ArrayList<String>();	
//					chartTypeData.add("Chart");
//					chartTypeData.add("Table");
//					chartTypeData.add("Matrix");
//					
//					int columnTypeFirst = res.getMetaData().getColumnType(1);
//					if(columnTypeFirst == -6 || columnTypeFirst == -5 || columnTypeFirst == 2 || columnTypeFirst == 3 || columnTypeFirst == 4 ||
//							columnTypeFirst == 5 || columnTypeFirst == 6  || columnTypeFirst == 7  || columnTypeFirst == 8){
//						
//					}
//					}
//					else{
//						resultMessage = "The query output is not structured properly to create the chart and third column should be a number.";
//						msgCode = "0";
//						chartTypeData = new ArrayList<String>();	
//						chartTypeData.add("Table");
//						chartTypeData.add("Matrix");
//					}
//				}else{
//					resultMessage = "The query output is not structured properly to create the chart.";
//					msgCode = "0";
//					chartTypeData = new ArrayList<String>();	
//					chartTypeData.add("Table");
//					chartTypeData.add("Matrix");
//				}
//			}else if(columnCount >= 2){
//				int columnType = res.getMetaData().getColumnType(2);
//				if(columnType == -6 || columnType == -5 || columnType == 2 || columnType == 3 || columnType == 4 ||
//						columnType == 5 || columnType == 6  || columnType == 7  || columnType == 8){
//				resultMessage = "The query is compiled successfully.";
//				msgCode = "1";
//				
//				chartTypeData = new ArrayList<String>();
//				chartTypeData.add("Chart");
//				chartTypeData.add("Table");
//				chartTypeData.add("Matrix");
//				
//				int columnTypeFirst = res.getMetaData().getColumnType(2);
//				if(columnTypeFirst == -6 || columnTypeFirst == -5 || columnTypeFirst == 2 || columnTypeFirst == 3 || columnTypeFirst == 4 ||
//						columnTypeFirst == 5 || columnTypeFirst == 6  || columnTypeFirst == 7  || columnTypeFirst == 8){
//					
//				}
//				
//				}
//				else{
//					resultMessage = "The query output is not structured properly to create the chart. The second column should be a number.";
//					msgCode = "0";
//					chartTypeData = new ArrayList<String>();	
//					chartTypeData.add("Table");
//					chartTypeData.add("Matrix");
//				}
//			}
//			
//			// Iterate the resultset and return the data
//			if(columnCount>0){
//				headerList = new ArrayList<String>();
//				rowData = new ArrayList<>();
//				for(int i=1;i<=columnCount; i++){
//					headerList.add(res.getMetaData().getColumnName(i));
//				}
//				while(res.next())
//				{
//					noData = false;
//					columnData = new ArrayList<>();
//					for(int i=1;i<=columnCount; i++){
//						columnData.add(res.getString(i));
//					}
//					rowData.add(columnData);
//				}
//			}
//			output.setHeaderList(headerList);
//			output.setData(rowData);
//			
//			if(noData){
//				resultMessage = "No data is present to generate the report.";
//				msgCode = "-1";
//			}
//			
//		 }catch(SQLServerException sqlexception){
//			 resultMessage = errMessage.concat(sqlexception.getMessage()).concat(".");
//			 msgCode = "-1";
//			 LOGGER.info(sqlexception.getMessage());
//		 }
//		catch(SQLException se){
//			 se.printStackTrace();
//			 resultMessage = errMessage.concat(se.getMessage()).concat(".");
//			 msgCode = "-1";
//			 LOGGER.info(se.getMessage());
//		 } catch (Exception e) {
//			e.printStackTrace();
//			resultMessage = errMessage.concat(e.getMessage()).concat(".");
//			msgCode = "-1";
//			LOGGER.info(e.getMessage());
//		}finally{
//			try{
//				if(null != stmnt){stmnt.close();}
//			}catch(Exception e){ }
//			try{
//				if(null != conn){conn.close();}
//			}catch(Exception e){ }
//		}
//		
//		output.setMessage(resultMessage);
//		output.setMessageCode(msgCode);
//		output.setChartTypeData(chartTypeData);
//		return output;
//	}
	
	
}
