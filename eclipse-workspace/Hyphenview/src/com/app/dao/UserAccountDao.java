package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.object.UserAccount;

public class UserAccountDao {
	
	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnection.class);
	
public int createUserAccount(UserAccount userAccount){
		
	Connection conn = null;
	PreparedStatement stmnt = null;
		int i = 0;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			
			String query = "Insert into user_account (user_name,role_id,user_password,user_created_on,user_status,customer_id, email_id) "
					+ "values(?,(select role_id from user_role where role_name = 'Guest'),?,?,?,(select customer_id from customer where email_id = ?),?)";
			stmnt = conn.prepareStatement(query);
			stmnt.setString(1, userAccount.getUserName());
			stmnt.setString(2, userAccount.getPassword());
			Date date = new Date();
			Timestamp timestamp = new java.sql.Timestamp(date.getTime());
			stmnt.setTimestamp(3, timestamp);
			stmnt.setBoolean(4, userAccount.isUserStatus());
			stmnt.setString(5, userAccount.getEmailId());
			stmnt.setString(6, userAccount.getEmailId());
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

public UserAccount fetchUserAccountByEmail(String emailId){
	
	UserAccount userAccount = null;
	
	String query = null;
	Connection conn = null;
	PreparedStatement stmnt = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "select * from user_account where email_id = ?";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, emailId);
		ResultSet res = stmnt.executeQuery();

		while(res.next()){
			if(userAccount == null){
				userAccount = new UserAccount();
			}
			userAccount.setUserId(res.getString(1));
			userAccount.setUserName(res.getString(2));
			userAccount.setRoleId(res.getString(3));
			userAccount.setEmailId(res.getString(4));
			userAccount.setPassword(res.getString(5));
			userAccount.setCreatedOn(res.getDate(6));
			userAccount.setUserStatus(res.getBoolean(7));
			userAccount.setCustomerId(res.getString(8));
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
	
	return userAccount;
	}

}
