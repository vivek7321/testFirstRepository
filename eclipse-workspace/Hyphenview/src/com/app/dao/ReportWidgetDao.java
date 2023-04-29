package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.constant.QueryConstants;
import com.app.object.ReportWidget;

public class ReportWidgetDao {
	
	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnection.class);


	/**
	 * Method to insert a record in the report_widget table
	 * 
	 * @param reportWidget
	 * @return
	 */
	public int createReportWidget(ReportWidget reportWidget){
		
		Connection conn = null;
		int i = 0;
		String rdbms = reportWidget.getDatabase();
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			LOGGER.debug("Insertng Widget record ");
			String query = "Insert into report_widget (widget_name,widget_type,defined_query,customer_id,db_details_id) "
					+ "values(?,?,?,(select customer_id from user_account where email_id=?),"
					+ "(select  db.db_details_id from database_details db where db.rdbms_name = ? and db.customer_id = (select customer_id from user_account where email_id=?)";
			if(!rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				query = query.concat("and db.db_schema_name = ?");
			}
			query = query.concat("))");
			PreparedStatement stmnt = conn.prepareStatement(query);
			stmnt.setString(1, reportWidget.getWidgetName());
			stmnt.setString(2, reportWidget.getWidgetType());
			stmnt.setString(3, reportWidget.getDefinedQuery());
			stmnt.setString(4, reportWidget.getEmail());
			stmnt.setString(5, reportWidget.getDatabase());
			stmnt.setString(6, reportWidget.getEmail());
			if(!rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				if(reportWidget.getDatabase().equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
					stmnt.setString(7, "");
				}else{
					stmnt.setString(7, reportWidget.getSchema());
				}
			}
			
			i = stmnt.executeUpdate();
			LOGGER.debug("Widget record inserted successfully");
			conn.close();
		 }catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug(e.getMessage());
		}
		return i;
	}

/**
 * 
 * Method to fetch the list of widgets available for the given email id
 * @param emailId
 * @return List<ReportWidget>
 */
public List<ReportWidget> fetchWidgetsByEmail(String emailId){
	List<ReportWidget> reportWidgetList = null;
	String query = null;
	
	Connection conn = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "SELECT rt.* FROM report_widget rt, user_account u where rt.customer_id = u.customer_id and u.email_id = ?";
		PreparedStatement stmnt = conn.prepareStatement(query);
		stmnt.setString(1, emailId);
		ResultSet res = stmnt.executeQuery();
		
		while(res.next())
		{
			if(reportWidgetList == null){
				reportWidgetList = new ArrayList<ReportWidget>();
			}
			ReportWidget  reportWidget = new ReportWidget();
			reportWidget.setWidgetId(res.getString(1));
			reportWidget.setWidgetName(res.getString(2));
			reportWidget.setWidgetType(res.getString(3));
			reportWidget.setDefinedQuery(res.getString(4));
			reportWidget.setDatabaseId(res.getString(6));
			reportWidget.setCustomerId(res.getString(5));
			
			reportWidgetList.add(reportWidget);
		}
		
		conn.close();
	 }catch (Exception e) {
		e.printStackTrace();
	}
	
	return reportWidgetList;
}


/**
 * Method to update the report widget
 * 
 * @param reportWidget
 * @return
 */
public int updateReportWidgetById(ReportWidget reportWidget){
	
	Connection conn = null;
	int i = 0;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		LOGGER.debug(" Updating Widget record");
		String query = "update report_widget set widget_name = ?, widget_type = ?, defined_query = ? where widget_id=?";
		PreparedStatement stmnt = conn.prepareStatement(query);
		stmnt.setString(1, reportWidget.getWidgetName());
		stmnt.setString(2, reportWidget.getWidgetType());
		stmnt.setString(3, reportWidget.getDefinedQuery());
		stmnt.setString(4, reportWidget.getWidgetId());
		i = stmnt.executeUpdate();
		LOGGER.debug("Widget record updated successfully");
		conn.close();
	 }catch (Exception e) {
		e.printStackTrace();
		LOGGER.info(e.getMessage());
	}
	
	return i;
}



public ReportWidget fetchWidgetById(String widgetId){
	ReportWidget reportWidget = null;
	String query = null;
	
	Connection conn = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "select * from report_widget where widget_id = ?";
		PreparedStatement stmnt = conn.prepareStatement(query);
		stmnt.setString(1, widgetId);
		ResultSet res = stmnt.executeQuery();
		
		while(res.next())
		{
			reportWidget = new ReportWidget();
			reportWidget.setWidgetId(res.getString(1));
			reportWidget.setWidgetName(res.getString(2));
			reportWidget.setWidgetType(res.getString(3));
			reportWidget.setDefinedQuery(res.getString(4));
			reportWidget.setDatabaseId(res.getString(6));
			reportWidget.setCustomerId(res.getString(5));
		}
		
		conn.close();
	 }catch (Exception e) {
		e.printStackTrace();
		LOGGER.info(e.getMessage());
	}
	return reportWidget;
}


}
