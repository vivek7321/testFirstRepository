package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.object.Customer;

public class CustomerDao {
	
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Method to create an entry into the Customer table
	 * 
	 * @param customer
	 * @return
	 */
	public int createCustomer(Customer customer){
		
		Connection conn = null;
		PreparedStatement stmnt = null;
		int i = 0;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			
			String query = "Insert into customer (customer_name,email_id,unique_id) values(?,?,?)";
			stmnt = conn.prepareStatement(query);
			stmnt.setString(1, customer.getCustomerName());
			stmnt.setString(2, customer.getEmailId());
			stmnt.setString(3, customer.getUniqueId());
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
	 * Method to update Customer
	 * 
	 * @param customer
	 * @return
	 */
public int updateCustomer(Customer customer){
		
		Connection conn = null;
		PreparedStatement stmnt = null;
		int i = 0;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			
			String query = "Update customer set customer_name=?,contact_number=?,first_name=?,last_name=?,"
					+ "address=?,city=?,state=?,country=?, postal_code=?,gender=?,email_id=?,unique_id=null where customer_id=?";
			stmnt = conn.prepareStatement(query);
			stmnt.setString(1, customer.getCustomerName());
			stmnt.setString(2, customer.getContactNumber());
			stmnt.setString(3, customer.getFirstName());
			stmnt.setString(4, customer.getLastName());
			stmnt.setString(5, customer.getAddress());
			stmnt.setString(6, customer.getCity());
			stmnt.setString(7, customer.getState());
			stmnt.setString(8, customer.getCountry());
			stmnt.setString(9, customer.getPostalCode());
			stmnt.setString(10, customer.getGender());
			stmnt.setString(11, customer.getEmailId());
			stmnt.setString(12, customer.getCustomerId());
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
	 * Method to fetch the customer details based on the email id.
	 * 
	 * @param emailId
	 * @return
	 */
	public Customer fetchCustomerByEmail(String emailId){
		Customer customer = null;
		
		String query = null;
		Connection conn = null;
		PreparedStatement stmnt = null;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			query = "select * from customer where email_id = ?";
			stmnt = conn.prepareStatement(query);
			stmnt.setString(1, emailId);
			ResultSet res = stmnt.executeQuery();
	
			while(res.next()){
				if(customer == null){
					customer = new Customer();
				}
				customer.setCustomerId(res.getString(1));
				customer.setCustomerName(res.getString(2));
				customer.setContactNumber(res.getString(3));
				customer.setFirstName(res.getString(4));
				customer.setLastName(res.getString(5));
				customer.setAddress(res.getString(6));
				customer.setCity(res.getString(7));
				customer.setState(res.getString(8));
				customer.setCountry(res.getString(9));
				customer.setPostalCode(res.getString(10));
				customer.setGender(res.getString(12));
				customer.setEmailId(res.getString(11));
				customer.setUniqueId(res.getString(13));
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
		
		return customer;
	}


}
