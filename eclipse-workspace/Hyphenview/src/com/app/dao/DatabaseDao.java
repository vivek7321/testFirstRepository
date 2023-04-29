package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.object.DatabaseDetails;

public class DatabaseDao {
	
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

/**
 * Method to insert an entry into the database_details table, regarding the database details provided by the user.
 * 
 * @param dbDetails
 * @return
 */
public int createDatabase(DatabaseDetails dbDetails){
		
		Connection conn = null;
		PreparedStatement stmnt = null;
		int i = 0;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			
			String query = "Insert into database_details (rdbms_name,domain_name,db_port,db_user_name,db_password,db_schema_name,customer_id) "
					+ "values(?,?,?,?,?,?,?)";
			stmnt = conn.prepareStatement(query);
			stmnt.setString(1, dbDetails.getRdbms());
			stmnt.setString(2, dbDetails.getDomainName());
			stmnt.setString(3, dbDetails.getPortNumber());
			stmnt.setString(4, dbDetails.getUserName());
			stmnt.setString(5, dbDetails.getPassword());
			stmnt.setString(6, dbDetails.getSchemaName());
			stmnt.setString(7, dbDetails.getCustomerId());
			i = stmnt.executeUpdate();
			
		 }catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(e.getMessage());
		}finally{
			try{
				if(null != stmnt){stmnt.close();}
			}catch(Exception e){ }
			try{
				if(null != conn){conn.close();}
			}catch(Exception e){ }
		}
		return i;
	}

/**
 * Method to fetch the list of schemas based on the user name and the RDBMS.
 * 
 * @param userName
 * @param rdbms
 * @return
 */
public List<String> fetchSchemaList(String userName, String rdbms){
	 List<String> schemaList = null;
	
	 String query = null;
	 Connection conn = null;
	 PreparedStatement stmnt = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "select db.db_schema_name from database_details db, user_account ua "
				+ "where ua.email_id = ? and ua.customer_id = db.customer_id and db.rdbms_name = ?";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, userName);
		stmnt.setString(2, rdbms);
		ResultSet res = stmnt.executeQuery();

		while(res.next()){
			if(schemaList == null){
				schemaList = new ArrayList<String>();
			}
			schemaList.add(res.getString(1));
		}
	 }catch(SQLException se){
		 se.printStackTrace();
		 LOGGER.info(se.getMessage());
	 } catch (Exception e) {
		e.printStackTrace();
		LOGGER.info(e.getMessage());
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

public DatabaseDetails fetchDbDetails(String rdbms, String schema, String email){
	DatabaseDetails databaseDetails = null;
	String query = null;
	 Connection conn = null;
	 PreparedStatement stmnt = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "select db.* from database_details db, user_account ua "
				+ "where ua.email_id = ? and ua.customer_id = db.customer_id and db.rdbms_name = ? and db.db_schema_name = ?";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, email);
		stmnt.setString(2, rdbms);
		stmnt.setString(3, schema);
		ResultSet res = stmnt.executeQuery();

		while(res.next()){
			databaseDetails = new DatabaseDetails();
			databaseDetails.setDbDetailsId(res.getString(1));
			databaseDetails.setRdbms(res.getString(2));
			databaseDetails.setDomainName(res.getString(3));
			databaseDetails.setPortNumber(res.getString(4));
			databaseDetails.setUserName(res.getString(5));
			databaseDetails.setPassword(res.getString(6));
			databaseDetails.setSchemaName(res.getString(7));
			databaseDetails.setCustomerId(res.getString(8));
		}
	 }catch(SQLException se){
		 se.printStackTrace();
		 LOGGER.info(se.getMessage());
	 } catch (Exception e) {
		e.printStackTrace();
		LOGGER.info(e.getMessage());
	}finally{
		try{
			if(null != stmnt){stmnt.close();}
		}catch(Exception e){ }
		try{
			if(null != conn){conn.close();}
		}catch(Exception e){ }
	}
	return databaseDetails;
}

}
