package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.AESCrypt;
import com.app.constant.QueryConstants;

import era.database.connection;

public class FetchElementsDao {
	
	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnection.class);
	
	public List<String> fetchTables(String email, String database, String schema){
		List<String> tablesList = null;
		Connection conn = null;
		PreparedStatement stmnt = null;
		 String query = null;
		 if(database.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL) ){
			 query = QueryConstants.mysql_fetch_all_tables;
		 }else if(database.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
			 String tempQuery = QueryConstants.mssql_fetch_all_tables;
			 query = tempQuery.replaceAll("##", schema);
		 }else if(database.equalsIgnoreCase(QueryConstants.POSTGRESQL)){
			 query = QueryConstants.postgres_fetch_all_tables;
		 }else if(database.equalsIgnoreCase(QueryConstants.DB2SERVER)){
			 query = QueryConstants.db2_fetch_all_tables;
		 }else if(database.equalsIgnoreCase(QueryConstants.VERTICA)){
			 query = QueryConstants.vertica_fetch_all_tables;
		 }

		 try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				conn = dbConn.getUserDB2ConnForElements(email, database);
			}else{
				conn = dbConn.getUserDBConnection(email, database, schema);
			}
			LOGGER.info("Query to fetch the tables --> " + query);
			stmnt = conn.prepareStatement(query);
			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER) ){
				stmnt.setString(1,schema.toUpperCase());
			}
			ResultSet res = stmnt.executeQuery();
	
			while(res.next()){
				if(tablesList == null){
					tablesList = new ArrayList<String>();
				}
				tablesList.add(res.getString(1));
			}
		 }catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
		}
		
		return tablesList;
	}
	
	public List<String> fetchColumns(String email, String database, String schema, String tableName){
		List<String> columnsList = null;
		Connection conn = null;
		PreparedStatement stmnt = null;
		 try{
			
			String query = null;
			 if(database.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL) ){
				 query = QueryConstants.mysql_fetch_all_column_names;
			 }else if(database.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				 String tempQuery = QueryConstants.mssql_fetch_all_column_names;
				 query = tempQuery.replaceAll("##", schema);
			 }else if(database.equalsIgnoreCase(QueryConstants.POSTGRESQL) ){
				 query = QueryConstants.postgres_fetch_all_column_names;
			 }else if(database.equalsIgnoreCase(QueryConstants.DB2SERVER) ){
				 query = QueryConstants.db2_fetch_all_column_names;
			 }else if(database.equalsIgnoreCase(QueryConstants.VERTICA) ){
				 query = QueryConstants.vertica_fetch_all_column_names;
			 }
				 
			EraviewDBconnection dbConn = new EraviewDBconnection();
			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER) ){
				conn = dbConn.getUserDB2ConnForElements(email, database);
			} else{
				conn = dbConn.getUserDBConnection(email, database, schema);
			}
			LOGGER.info("Query to fetch the columns --> " + query);
			 stmnt = conn.prepareStatement(query);
			if(database.equalsIgnoreCase(QueryConstants.DB2SERVER) ){
				String table = null;
				if(tableName.contains(".")){
					String tokens[] = tableName.split("\\.");
					table = tokens[tokens.length-1];
				}else{
					table = tableName;
				}
				stmnt.setString(1,table);
			}else{
				stmnt.setString(1,tableName);
			}
			if(database.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL) ){
				stmnt.setString(2,schema);
			}
			ResultSet res = stmnt.executeQuery();
	
			while(res.next()){
				if(columnsList == null){
					columnsList = new ArrayList<String>();
				}
				columnsList.add(res.getString(1));
			}
		 }catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
		}
		
		return columnsList;
	}
	
	public List<String> fetchSchemas(String rdbms){
		List<String> schemaList = null;
		Connection conn = null;
		PreparedStatement stmnt = null;
		 String query = null;
		 if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL) ){
			 query = QueryConstants.mysql_fetch_all_schemas;
		 }else if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
			 query = QueryConstants.mssql_fetch_all_schemas;
		 }

		 try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			conn = dbConn.getDBConnection(rdbms, null);
			stmnt = conn.prepareStatement(query);
			ResultSet res = stmnt.executeQuery();
	
			while(res.next()){
				if(schemaList == null){
					schemaList = new ArrayList<String>();
				}
				schemaList.add(res.getString(1));
			}
		 }catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
		}
		
		return schemaList;
	}
	
	
	public List<String> fetchSchemasForDatabase(String rdbms, String email){
		List<String> schemaList = null;
		String query = null;
		Connection conn = null;
		Connection localConnection = null;
		PreparedStatement statement = null;
		PreparedStatement stmnt = null;
		
		if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
			query = QueryConstants.mssql_fetch_all_schemas;
		} else if(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
			query = QueryConstants.db2_fetch_all_schemas;
		}else if(rdbms.equalsIgnoreCase(QueryConstants.VERTICA)){
			query = QueryConstants.vertica_fetch_all_schemas;
		}

		 try{
			EraviewDBconnection dbConn = new EraviewDBconnection();
			
			String domain = null;
			String port = null;
			String schema = null;
			String username = null;
			String password = null;
			// get the database details of the customer from database_details table
			localConnection = dbConn.getLocalDBConnection();
			String dbQuery = "select * from database_details db, user_account ua where ua.email_id = ? and ua.customer_id = db.customer_id and db.rdbms_name = ?";
			statement = localConnection.prepareStatement(dbQuery);
			statement.setString(1, email);
			statement.setString(2, rdbms);
			ResultSet resultSet = statement.executeQuery();

			while(resultSet.next()){
				domain = resultSet.getString(3);
				port = resultSet.getString(4);
				username = resultSet.getString(5);
				password = resultSet.getString(6);
				schema = resultSet.getString(7);
			}
			
			// decrypt the database password
			AESCrypt aesCrypt = new AESCrypt();
			String decryptedPwd = aesCrypt.decrypt(password);
			
			LOGGER.info(domain + " " + port + " " + schema + " " +  username);
			if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				conn = connection.getSqlServerConnection(domain, port, null, username, decryptedPwd);
			} else if(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				conn = connection.getDB2Connection(domain, port, schema, username, decryptedPwd);
			}
			else if(rdbms.equalsIgnoreCase(QueryConstants.VERTICA)){
				conn = connection.getVerticaConnection(domain, port, schema, username, decryptedPwd);
			}
			stmnt = conn.prepareStatement(query);
			ResultSet res = stmnt.executeQuery();
	
			while(res.next()){
				if(schemaList == null){
					schemaList = new ArrayList<String>();
				}
				schemaList.add(res.getString(1));
			}
		 }catch(SQLException se){
			 se.printStackTrace();
		 } catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
			try{
				if(null != statement){statement.close();}
			}catch(Exception e){ }
			try{
				if(null != localConnection){localConnection.close();}
			}catch(Exception e){ }
		}
		
		return schemaList;
	}
	
	
}
