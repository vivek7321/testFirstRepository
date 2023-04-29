package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.constant.QueryConstants;
import com.app.object.ReportTemplate;

public class ReportTemplateDao {
	
	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnection.class);

	/**
	 * Method to save a report template in database
	 * 
	 * @param reportTemplate
	 * @return
	 */
public int createReportTemplate(ReportTemplate reportTemplate,HttpServletRequest request){
	LOGGER.info("inside createReportTemplate");
		Connection conn = null;
		PreparedStatement stmnt = null;
		
		int i = 0;
		String rdbms = reportTemplate.getDatabase();
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			String userid = request.getSession().getAttribute("userId").toString();
			System.out.println("Testing StartDate-->"+reportTemplate.getStartDate()+"\nEndDate-->"+reportTemplate.getEndDate());
			String query = "Insert into report_template (template_type,report_template_name,report_type,chart_type,defined_query,customer_id,enable_drilldown, display_order, auto_update_interval, background_color, chart_rect_color,time_period,theme,userid,dashboardMapId,start_date,end_date,upload_logo,db_details_id) "
					+ "values(?,?,?,?,?,(select customer_id from user_account where email_id=?),?,?,?,?,?,?,?,?,?,?,?,?,"
					+ "(select  db.db_details_id from database_details db where db.rdbms_name = ? and db.customer_id = (select customer_id from user_account where email_id=?)";
			if(!(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER) ||rdbms.equalsIgnoreCase(QueryConstants.VERTICA)) ){
				query = query.concat("and db.db_schema_name = ?");
			}
			query = query.concat("))");
			stmnt = conn.prepareStatement(query);
			stmnt.setString(2, reportTemplate.getReportTemplateName());
			stmnt.setString(3, reportTemplate.getReportType());
			stmnt.setString(4, reportTemplate.getChartType());
			stmnt.setString(5, reportTemplate.getDefinedQuery());
			stmnt.setString(6, reportTemplate.getEmail());
			stmnt.setString(7, reportTemplate.getDrilldown());
			stmnt.setInt(8, reportTemplate.getDisplayOrder());
			stmnt.setInt(9, reportTemplate.getAutoUpdateInterval());
			stmnt.setString(10, reportTemplate.getBgcolor());
			stmnt.setString(11, reportTemplate.getRectcolor());
			stmnt.setString(12, reportTemplate.getTimePeriod());
			stmnt.setString(13, reportTemplate.getTheme());
			stmnt.setString(1, reportTemplate.getTemplateType());
			stmnt.setString(14, userid);
			stmnt.setInt(15, 1);
			
			stmnt.setString(19, reportTemplate.getDatabase());
			stmnt.setString(16, reportTemplate.getStartDate());
			stmnt.setString(17, reportTemplate.getEndDate());
			stmnt.setString(18, reportTemplate.getuploadLogoStatus());
			
			stmnt.setString(20, reportTemplate.getEmail());
			
			System.out.println("Inside ReportTemplateDao: TemplateType-->"+reportTemplate.getTemplateType()+"\nReportTemplateName-->"+reportTemplate.getReportTemplateName()+"\nReportType-->"+reportTemplate.getReportType()+
					"\nChartType-->"+reportTemplate.getChartType()+"\nDefinedQuery-->"+reportTemplate.getDefinedQuery()+"\nEmail-->"+reportTemplate.getEmail()+"\nDrilldown-->"+reportTemplate.getDrilldown()+
					"\nDisplayOrder-->"+reportTemplate.getDisplayOrder()+"\nAutoUpdateInterval-->"+reportTemplate.getAutoUpdateInterval()+"\nBgcolor-->"+ reportTemplate.getBgcolor()+"\nRectcolor-->"+reportTemplate.getRectcolor()
					+"\nTimePeriod-->"+reportTemplate.getTimePeriod()+"\nTheme-->"+reportTemplate.getTheme()+"\nTemplateType-->"+reportTemplate.getTemplateType()+"\nuserid-->"+userid+"\nDatabase-->"+reportTemplate.getDatabase()
					+"\nStartDate-->"+reportTemplate.getStartDate()+"\nEndDate-->"+reportTemplate.getuploadLogoStatus()+"\nEmail-->"+reportTemplate.getEmail());
			if(!(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER) ||rdbms.equalsIgnoreCase(QueryConstants.VERTICA)) ){
				if(reportTemplate.getDatabase().equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
					stmnt.setString(21, "");
				}else{
					stmnt.setString(21, reportTemplate.getSchema());
				}
			}
			
			System.out.println("TemplateQuery" + query);
			i = stmnt.executeUpdate();
		
			
		 }catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug(e.getMessage());
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
 * Method to fetch the list of reports existing for the customer
 * 
 * @param emailId
 * @return
 */
public List<ReportTemplate> fetchReportsByEmail(String emailId,String roleName , String userId ,String dashboardId){
	List<ReportTemplate> reportTemplateList = null;
	String query = null;
	LOGGER.info("inside fetchReportsByEmail 1");
	Connection conn = null; 
	PreparedStatement stmnt = null;
	ResultSet res = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		if(roleName.toString().equalsIgnoreCase("SuperAdmin"))
		{
			query = "SELECT rt.* FROM report_template rt, user_account u where rt.customer_id = u.customer_id and u.email_id = ? and rt.dashboardMapId = ? order by rt.report_type, rt.template_type, rt.display_order";
			stmnt = conn.prepareStatement(query);
			stmnt.setString(1, emailId);
			stmnt.setString(2, dashboardId);
			res = stmnt.executeQuery();
		}
		else 
		{
			query = "select rt.* from report_template rt, user_account u,user_report_temp_map map where rt.customer_id = u.customer_id and rt.report_template_id = map.report_template_id and u.email_id = ? and map.user_id = ? and rt.dashboardMapId = ? order by  rt.report_type , rt.display_order";
			stmnt = conn.prepareStatement(query);
			stmnt.setString(1, emailId);
			stmnt.setString(2, userId);
			stmnt.setString(3, dashboardId);
			res = stmnt.executeQuery();
		}


		
		while(res.next())
		{
			if(reportTemplateList == null){
				reportTemplateList = new ArrayList<ReportTemplate>();
			}
			ReportTemplate reportTemplate = new ReportTemplate();
			reportTemplate.setReportTemplateId(res.getString(1));
			reportTemplate.setReportTemplateName(res.getString(2));
			reportTemplate.setReportType(res.getString(3));
			reportTemplate.setChartType(res.getString(4));
			reportTemplate.setDefinedQuery(res.getString(5));
			reportTemplate.setCustomerId(res.getString(6));
			reportTemplate.setDatabaseId(res.getString(7));
			reportTemplate.setDrilldown(res.getString(8));
			reportTemplate.setDisplayOrder(res.getInt(9));
			reportTemplate.setAutoUpdateInterval(res.getInt(10));
			reportTemplate.setEmail(emailId);
			reportTemplate.setBgcolor(res.getString(11));
			reportTemplate.setRectcolor(res.getString(12));
			reportTemplate.setTimePeriod(res.getString(13));
			reportTemplate.setTheme(res.getString(14));
			reportTemplate.setTemplateType(res.getString(18));
			reportTemplate.setStartDate(res.getString(19));
			reportTemplate.setEndDate(res.getString(20));
			reportTemplate.setuploadLogoStatus(res.getString(21));
			reportTemplateList.add(reportTemplate);
			
		}
		
	 }catch (Exception e) {
		e.printStackTrace();
	}finally{
		try{
			if(null != stmnt){stmnt.close();}
		}catch(Exception e){ }
		try{
			if(null != conn){conn.close();}
		}catch(Exception e){ }
	}
	
	return reportTemplateList;
}

/**
 * Method to fetch the list of reports existing for the customer
 * 
 * @param emailId
 * @return
 */
public List<ReportTemplate> fetchChartsByEmail(String emailId){
	List<ReportTemplate> reportTemplateList = null;
	String query = null;
	LOGGER.info("inside fetchReportsByEmail 2");
	Connection conn = null;
	PreparedStatement stmnt = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "SELECT rt.* FROM hyphen.report_template rt, user_account u where rt.customer_id = u.customer_id and u.email_id = ? and rt.report_type = 'Chart'";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, emailId);
		ResultSet res = stmnt.executeQuery();
		
		while(res.next())
		{
			if(reportTemplateList == null){
				reportTemplateList = new ArrayList<ReportTemplate>();
			}
			ReportTemplate reportTemplate = new ReportTemplate();
			reportTemplate.setReportTemplateId(res.getString(1));
			reportTemplate.setReportTemplateName(res.getString(2));
			reportTemplate.setReportType(res.getString(3));
			reportTemplate.setChartType(res.getString(4));
			reportTemplate.setDefinedQuery(res.getString(5));
			reportTemplate.setCustomerId(res.getString(6));
			reportTemplate.setDatabaseId(res.getString(7));
			reportTemplate.setDrilldown(res.getString(8));
			reportTemplate.setBgcolor(res.getString(11));
			reportTemplate.setRectcolor(res.getString(12));
			reportTemplate.setEmail(emailId);
			reportTemplate.setTimePeriod(res.getString(13));
			reportTemplate.setTemplateType(res.getString(18));
			reportTemplate.setStartDate(res.getString(19));
			reportTemplate.setEndDate(res.getString(20));
			reportTemplateList.add(reportTemplate);
		}
		
		conn.close();
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
	
	return reportTemplateList;
}


/**
 * Method to fetch the list of reports existing for the customer
 * 
 * @param emailId
 * @return
 */
public List<ReportTemplate> fetchTablesByEmail(String emailId){
	List<ReportTemplate> reportTemplateList = null;
	String query = null;
	
	Connection conn = null;
	PreparedStatement stmnt = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "SELECT rt.* FROM hyphen.report_template rt, user_account u where rt.customer_id = u.customer_id and u.email_id = ? and rt.report_type = 'Table'";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, emailId);
		ResultSet res = stmnt.executeQuery();
		
		while(res.next())
		{
			if(reportTemplateList == null){
				reportTemplateList = new ArrayList<ReportTemplate>();
			}
			ReportTemplate reportTemplate = new ReportTemplate();
			reportTemplate.setReportTemplateId(res.getString(1));
			reportTemplate.setReportTemplateName(res.getString(2));
			reportTemplate.setReportType(res.getString(3));
			reportTemplate.setChartType(res.getString(4));
			reportTemplate.setDefinedQuery(res.getString(5));
			reportTemplate.setCustomerId(res.getString(6));
			reportTemplate.setDatabaseId(res.getString(7));
			reportTemplate.setDrilldown(res.getString(8));
			reportTemplate.setBgcolor(res.getString(11));
			reportTemplate.setRectcolor(res.getString(12));
			reportTemplate.setEmail(emailId);
			reportTemplate.setTimePeriod(res.getString(13));
			reportTemplate.setTemplateType(res.getString(18));
			reportTemplate.setStartDate(res.getString(19));
			reportTemplate.setEndDate(res.getString(20));
			reportTemplateList.add(reportTemplate);
		}
		
		conn.close();
	 }catch (Exception e) {
		 LOGGER.info(e.getMessage());
		e.printStackTrace();
	}finally{
		try{
			if(null != stmnt){stmnt.close();}
		}catch(Exception e){ }
		try{
			if(null != conn){conn.close();}
		}catch(Exception e){ }
	}
	
	return reportTemplateList;
}

public int fetchMaxDisplayOrder(String emailId){
	String query = null;
	int maxOrder = 0;
	
	Connection conn = null;
	PreparedStatement stmnt = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "select coalesce( max(display_order),0) from report_template where customer_id = (select customer_id from user_account where email_id=?)";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, emailId);
		ResultSet res = stmnt.executeQuery();
		
		while(res.next())
		{
			maxOrder = res.getInt(1);
		}
		
		conn.close();
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
	
	return maxOrder;
}

public int updateReportTemplateOrder(String reportId, String displayOrder){
	
	Connection conn = null;
	PreparedStatement stmnt = null;
	int i = 0;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		
		String query = "update report_template set display_order = ? where report_template_id=?";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, displayOrder);
		stmnt.setString(2, reportId);
		i = stmnt.executeUpdate();
		
		conn.close();
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

public int updateReportTemplateById(ReportTemplate reportTemplate){
	LOGGER.info("inside updateReportTemplateById");
	Connection conn = null;
	PreparedStatement stmnt = null;
	int i = 0;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		
		String query = "update report_template set report_template_name = ?, report_type = ?, chart_type = ?, defined_query = ?, "
				+ "enable_drilldown = ?, auto_update_interval = ?,background_color=?, chart_rect_color=?,time_period=?,template_type=?,start_date=?,end_date=?,upload_logo=? where report_template_id=?";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, reportTemplate.getReportTemplateName());
		stmnt.setString(2, reportTemplate.getReportType());
		stmnt.setString(3, reportTemplate.getChartType());
		stmnt.setString(4, reportTemplate.getDefinedQuery());
		stmnt.setString(5, reportTemplate.getDrilldown());
		stmnt.setInt(6, reportTemplate.getAutoUpdateInterval());
		stmnt.setString(7, reportTemplate.getBgcolor());
		stmnt.setString(8, reportTemplate.getRectcolor());
		stmnt.setString(14, reportTemplate.getReportTemplateId());
		stmnt.setString(9, reportTemplate.getTimePeriod());
		stmnt.setString(10,reportTemplate.getTemplateType());
		stmnt.setString(11,reportTemplate.getStartDate());
		stmnt.setString(12,reportTemplate.getEndDate());
		stmnt.setString(13,reportTemplate.getuploadLogoStatus());
		i = stmnt.executeUpdate();
		LOGGER.info("reportTemplate.getTemplateType---"+reportTemplate.getReportType());
		LOGGER.info("reportTemplate.getStartDate---"+reportTemplate.getStartDate());
		LOGGER.info("reportTemplate.getEndDate---"+reportTemplate.getEndDate());
		conn.close();
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

public static void main(String args[]){  
	/*String str = "select customer.customer_name,sum(orders.quantity) from customer,orders "
			+ "where customer.customer_id=orders.customer_id and orders.quantity>1 group by customer.customer_name";  
	System.out.println(str.indexOf("select"));
		
	LOGGER.info(str.indexOf("from"));
	String temp = str.replace(str.substring(0, str.indexOf("from")-1), "select *");
	LOGGER.info(temp);
	LOGGER.info(str.indexOf("group by"));
	String temp1 = str.substring(str.lastIndexOf("group by")+9, str.length());
	LOGGER.info(temp1);
	
	String temp2 = temp.replace(str.substring(str.lastIndexOf("group by"), str.length()),"and " + temp1 + "=?");
	LOGGER.info(temp2);*/
	
	String table = "HPSM.dbo.INCIDENTSM1";
	String tableName = null;
	
	if(table.contains(".")){
		System.out.println("input table --> " + table);
		String tokens[] = table.split("\\.");
		System.out.println(tokens.length);
		for (String temparray: tokens){
	          System.out.println(temparray);
	       }
		tableName = tokens[tokens.length-1];
	}else{
		tableName = table;
	}
	System.out.println(tableName);
	
   }


public ReportTemplate fetchReportById(String reportId){
	ReportTemplate reportTemplate = null;
	String query = null;
	
	Connection conn = null;
	PreparedStatement stmnt = null;
	try{
		EraviewDBconnection dBconnection = new EraviewDBconnection();
		conn = dBconnection.getLocalDBConnection();
		query = "select * from report_template where report_template_id = ?";
		stmnt = conn.prepareStatement(query);
		stmnt.setString(1, reportId);
		ResultSet res = stmnt.executeQuery();
		
		while(res.next())
		{
			reportTemplate = new ReportTemplate();
			reportTemplate.setReportTemplateId(res.getString(1));
			reportTemplate.setReportTemplateName(res.getString(2));
			reportTemplate.setReportType(res.getString(3));
			reportTemplate.setChartType(res.getString(4));
			reportTemplate.setDefinedQuery(res.getString(5));
			reportTemplate.setCustomerId(res.getString(6));
			reportTemplate.setDatabaseId(res.getString(7));
			reportTemplate.setDrilldown(res.getString(8));
			reportTemplate.setDisplayOrder(res.getInt(9));
			reportTemplate.setAutoUpdateInterval(res.getInt(10));
			reportTemplate.setBgcolor(res.getString(11));
			reportTemplate.setRectcolor(res.getString(12));
			reportTemplate.setTimePeriod(res.getString(13));
			reportTemplate.setTemplateType(res.getString(18));
			reportTemplate.setStartDate(res.getString(19));
			reportTemplate.setEndDate(res.getString(20));
		}
		
		conn.close();
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
	return reportTemplate;
}


}
