package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.object.ReportFileDetails;

public class ReportFileDao {
	
	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnection.class);
	
public int insertReportFile(ReportFileDetails reportFileDetails){
		
		Connection conn = null;
		int i = 0;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			
			String query = "Insert into report_file_details (report_file_path,report_file_name,report_file_type,customer_id,file_display_order) "
					+ "values(?,?,?,(select customer_id from user_account where email_id=?),?)";
			PreparedStatement stmnt = conn.prepareStatement(query);
			stmnt.setString(1, reportFileDetails.getReportFilePath());
			stmnt.setString(2, reportFileDetails.getReportFileName());
			stmnt.setString(3, reportFileDetails.getReportFileType());
			stmnt.setString(4, reportFileDetails.getEmailId());
			stmnt.setInt(5, reportFileDetails.getDisplayOrder());
			i = stmnt.executeUpdate();
			
		 }catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception in Inserting record in report_file_details :: " + e.getMessage());
		}
		return i;
	}

	/**
	 * Get the Maximum display order of flood files
	 * 
	 * @param emailId
	 * @return
	 */
	public int fetchMaxFileDisplayOrder(String emailId){
		String query = null;
		int maxOrder = 0;
		
		Connection conn = null;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			query = "select coalesce( max(file_display_order),0) from report_file_details where customer_id = (select customer_id from user_account where email_id=?)";
			PreparedStatement stmnt = conn.prepareStatement(query);
			stmnt.setString(1, emailId);
			ResultSet res = stmnt.executeQuery();
			
			while(res.next())
			{
				maxOrder = res.getInt(1);
			}
			
			conn.close();
		 }catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception while fetching the Maximum display order of flood files :: " + e.getMessage());
		}
		
		return maxOrder;
	}
	
	/**
	 * 
	 * Method to fetch the ReportFileDetails for the given filepath
	 * 
	 * @param filePath
	 * @param email
	 * @return
	 */
	public ReportFileDetails fetchReportFileDetails(String filePath, String email){
		
		ReportFileDetails fileDetails = null;
		
		Connection conn = null;
		String query = null;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			query = "select rfd.* from report_file_details rfd, user_account ua "
					+ "where ua.email_id = ? and ua.customer_id = rfd.customer_id and rfd.report_file_path = ?";
			PreparedStatement stmnt = conn.prepareStatement(query);
			stmnt.setString(1, email);
			stmnt.setString(2, filePath);
			ResultSet res = stmnt.executeQuery();

			while(res.next()){
				fileDetails = new ReportFileDetails();
				fileDetails.setReportFileDetailsId(res.getString(1));
				fileDetails.setReportFilePath(res.getString(2));
				fileDetails.setReportFileName(res.getString(3));
				fileDetails.setReportFileType(res.getString(4));
				fileDetails.setCustomerId(res.getString(5));
				fileDetails.setDisplayOrder(res.getInt(6));
				fileDetails.setEmailId(email);
			}
		}catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception while fetching the ReportFileDetails for the given file path :: " + e.getMessage());
		}
		
		return fileDetails;
		
	}
	
	public List<ReportFileDetails> fetchReportFiles(String email){
		List<ReportFileDetails> reportFileDetailsList = null;
		String query = null;
		
		Connection conn = null;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			query = "SELECT rfd.* FROM report_file_details rfd, user_account u where rfd.customer_id = u.customer_id and u.email_id = ? order by rfd.display_order";
			PreparedStatement stmnt = conn.prepareStatement(query);
			stmnt.setString(1, email);
			ResultSet res = stmnt.executeQuery();
			
			while(res.next())
			{
				if(reportFileDetailsList == null){
					reportFileDetailsList = new ArrayList<ReportFileDetails>();
				}
				ReportFileDetails fileDetails = new ReportFileDetails();
				fileDetails = new ReportFileDetails();
				fileDetails.setReportFileDetailsId(res.getString(1));
				fileDetails.setReportFilePath(res.getString(2));
				fileDetails.setReportFileName(res.getString(3));
				fileDetails.setReportFileType(res.getString(4));
				fileDetails.setCustomerId(res.getString(5));
				fileDetails.setDisplayOrder(res.getInt(6));
				fileDetails.setEmailId(email);
				reportFileDetailsList.add(fileDetails);
			}
			
			conn.close();
		 }catch (Exception e) {
			e.printStackTrace();
		}

		return reportFileDetailsList;
		
	}

}
