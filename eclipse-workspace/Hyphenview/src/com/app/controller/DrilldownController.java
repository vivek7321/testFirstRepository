package com.app.controller;

import java.io.IOException;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.QueryDao;
import com.app.object.ReportTemplate;
import com.google.gson.Gson;

/**
 * Servlet implementation class DrilldownController
 */
@WebServlet("/DrilldownController")
public class DrilldownController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DrilldownController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("DrilldownController's get method");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of DrilldownController servlet...");
		String series =  request.getParameter("series");
		LOGGER.info("customQuery input - series --> " + series);
		String category = request.getParameter("category");
		LOGGER.info("customQuery input - category --> " + category);
		String query = request.getParameter("query").toLowerCase();
		String databaseId = request.getParameter("databaseId");
		String emailId = request.getParameter("emailId");
		String customQuery = null;
		
		String tempWhere = query.substring(0, query.indexOf("from")-1).trim();
		
		String removeSelect[] = tempWhere.split(" ", 2); 
		
		String whereArr[] = removeSelect[1].split("[,]",0);
		
		String whereClause ="";
		
		if (whereArr.length >2)
		{
			whereClause = 	whereArr[0].concat(" = ? and ").concat(whereArr[1]).concat(" = ? ");
			
		}
		
		System.out.println("Where Clause for DrillDown" + whereClause) ;
		
		String temp = query.replace(query.substring(0, query.indexOf("from")-1), "select *");
		
		// customised for Postgres view_642754852.incident
		/*String selectClause = "select \"ID\",\"NAME\",\"PRIORITY\",\"PHASEID\",\"STATUS\",\"CURRENTASSIGNMENT\",\"DISPLAYLABEL\",\"ENTITYSETTINGS\",\"REQUESTEDBYPERSON\",\"SOLVEDBYPERSON\",\"CLOSEDBYPERSON\","
				+ "\"EMSCREATIONTIME\",\"SOLVEDTIME\",\"CLOSETIME\",\"OWNEDBYPERSON\",\"FIRSTTOUCH\",\"COMPLETIONCODE\",\"NEXTTARGETTIME\",\"SLTAGGREGATEDSTATUS\"";
		String temp = query.replace(query.substring(0, query.indexOf("from")-1), selectClause);*/
		
		if(query.contains("group by")){
			String temp1 = null;
			String replacement = "";
			int replaceIndex = 0;
			if(query.contains("order by")){
				replaceIndex = query.lastIndexOf("order by");
				temp1 = query.substring(query.lastIndexOf("group by")+9, replaceIndex);
			}else{
				replaceIndex = query.length();
				temp1 = query.substring(query.lastIndexOf("group by")+9,replaceIndex );
			}
			
			if(temp1.contains(",")){
				if(whereClause=="")
				{
				String[] array = temp1.split(",");
				int len = array.length;
				for ( int a=0; a < len; a++) {
					replacement = replacement.concat(array[a] + " = ? ");
					if(a < (len-1)){
						replacement = replacement.concat(" and ");
					}
				}
				}
				else
				{
					replacement = replacement.concat(whereClause);	
				}
			}else{
				series = null;
				replacement = replacement.concat(temp1 + " = ? ");
			}
			
			if(query.contains("where")){
				if (query.lastIndexOf("group by") < query.lastIndexOf("where"))
				customQuery = temp.replace(query.substring(query.lastIndexOf("group by"), replaceIndex)," and " + replacement);
				else
					customQuery = temp.replace(query.substring(query.lastIndexOf("group by"), replaceIndex)," where " + replacement);
			}else{
				customQuery = temp.replace(query.substring(query.lastIndexOf("group by"), replaceIndex)," where " + replacement);
			}
		}
		customQuery = customQuery.toUpperCase();
		LOGGER.info("customQuery in drilldown --> " + customQuery);
		
		ReportTemplate reportTemplate = new ReportTemplate();
		reportTemplate.setDatabaseId(databaseId);
		reportTemplate.setDefinedQuery(customQuery);
		reportTemplate.setSeries(series);
		reportTemplate.setCategory(category);
		reportTemplate.setEmail(emailId);
							
		/*QueryDao queryDao = new QueryDao();
		ResultSet res = queryDao.executeDrilldownQuery(reportTemplate, emailId, series, category);*/
		
		LOGGER.debug("setting in session...");
		request.getSession().setAttribute("drilldowndata", reportTemplate);
		LOGGER.debug("set in session...");
		response.setContentType("application/text");
		response.getWriter().write(new Gson().toJson("success"));
	}
	
//	public static void main(final String[] args) throws Exception 
//	{
//		String query ="select ta_month,sourceid, CpuUtill from (\r\n" + 
//				"(select ta_month,sourceid,cast((avg(avgcpuUtil)) as int) as \"CpuUtill\" , count(*) as \"NoOfTimes>80\" from(\r\n" + 
//				"select to_char(ta_period,'Month') as ta_month,(avgCPUUTIL),SOURCEID,CREATION_DATE from obr.sr_sm_node_res INNER JOIN OBR.DATETIME ON \r\n" + 
//				"(OBR.SR_SM_NODE_RES.ta_period=OBR.DATETIME.TIME_FULL_DATE) INNER JOIN obr.K_Shift ON (obr.Sr_SM_NODE_res.ShiftRef=obr.K_Shift.dsi_key_id )\r\n" + 
//				"where month(ta_period)=month(date_trunc('month',now()))-3\r\n" + 
//				"and shiftName='Default_Shift' and avgCPUUTIL >80\r\n" + 
//				"GROUP BY ta_month,SOURCEID,AVGCPUUTIL,CREATION_DATE,avgcpuUtil) abc\r\n" + 
//				"group by ta_month,abc.SOURCEId \r\n" + 
//				"order by ta_month limit 5) \r\n" + 
//				"union\r\n" + 
//				" (select ta_month,sourceid,cast((avg(avgcpuUtil)) as int) as \"CpuUtill\" , count(*) as \"NoOfTimes>80\" from(\r\n" + 
//				"select to_char(ta_period,'Month') as ta_month,(avgCPUUTIL),SOURCEID,CREATION_DATE from obr.sr_sm_node_res INNER JOIN OBR.DATETIME ON \r\n" + 
//				"(OBR.SR_SM_NODE_RES.ta_period=OBR.DATETIME.TIME_FULL_DATE) INNER JOIN obr.K_Shift ON (obr.Sr_SM_NODE_res.ShiftRef=obr.K_Shift.dsi_key_id )\r\n" + 
//				"where month(ta_period)=month(date_trunc('month',now()))-2\r\n" + 
//				"and shiftName='Default_Shift' and avgCPUUTIL >80\r\n" + 
//				"GROUP BY ta_month,SOURCEID,AVGCPUUTIL,CREATION_DATE,avgcpuUtil) abc\r\n" + 
//				"group by ta_month,abc.SOURCEId \r\n" + 
//				"order by ta_month limit 5)\r\n" + 
//				"union\r\n" + 
//				" (select ta_month,sourceid,cast((avg(avgcpuUtil)) as int) as \"CpuUtill\", count(*) as \"NoOfTimes>80\" from(\r\n" + 
//				"select to_char(ta_period,'Month') as ta_month,(avgCPUUTIL),SOURCEID,CREATION_DATE from obr.sr_sm_node_res INNER JOIN OBR.DATETIME ON \r\n" + 
//				"(OBR.SR_SM_NODE_RES.ta_period=OBR.DATETIME.TIME_FULL_DATE) INNER JOIN obr.K_Shift ON (obr.Sr_SM_NODE_res.ShiftRef=obr.K_Shift.dsi_key_id )\r\n" + 
//				"where month(ta_period)=month(date_trunc('month',now()))-1\r\n" + 
//				"and shiftName='Default_Shift' and avgCPUUTIL >80\r\n" + 
//				"GROUP BY ta_month,SOURCEID,AVGCPUUTIL,CREATION_DATE,avgcpuUtil) abc\r\n" + 
//				"group by ta_month,abc.SOURCEId \r\n" + 
//				"order by ta_month limit 5)) Last3Months\r\n" + 
//				"group by ta_month,SOURCEId,CpuUtill \r\n" + 
//				"order by month(to_date(ta_month,'Mon'))" ;
//		String drillQuery = query.replaceAll("\r\n", " ");
//		
//		String customQuery = null;
//		
//		String tempWhere = query.substring(0, query.indexOf("from")-1).trim();
//		
//		String removeSelect[] = tempWhere.split(" ", 2); 
//		
//		String whereArr[] = removeSelect[1].split("[,]",0);
//		
//		String whereClause ="";
//		
//		if (whereArr.length >2)
//		{
//			whereClause = 	whereArr[0].concat(" = ? and ").concat(whereArr[1]).concat(" = ? ");
//			
//		}
//		
//		System.out.println("Where Clause for DrillDown" + whereClause) ;
//		
//		String temp = query.replace(query.substring(0, query.indexOf("from")-1), "select *");
//		
//		System.out.println("temp" + temp) ;
//		// customised for Postgres view_642754852.incident
//		/*String selectClause = "select \"ID\",\"NAME\",\"PRIORITY\",\"PHASEID\",\"STATUS\",\"CURRENTASSIGNMENT\",\"DISPLAYLABEL\",\"ENTITYSETTINGS\",\"REQUESTEDBYPERSON\",\"SOLVEDBYPERSON\",\"CLOSEDBYPERSON\","
//				+ "\"EMSCREATIONTIME\",\"SOLVEDTIME\",\"CLOSETIME\",\"OWNEDBYPERSON\",\"FIRSTTOUCH\",\"COMPLETIONCODE\",\"NEXTTARGETTIME\",\"SLTAGGREGATEDSTATUS\"";
//		String temp = query.replace(query.substring(0, query.indexOf("from")-1), selectClause);*/
//		
//		if(query.contains("group by")){
//			String temp1 = null;
//			String replacement = "";
//			int replaceIndex = 0;
//			if(query.contains("order by")){
//				replaceIndex = query.lastIndexOf("order by");
//				temp1 = query.substring(query.lastIndexOf("group by")+9, replaceIndex);
//			}else{
//				replaceIndex = query.length();
//				temp1 = query.substring(query.lastIndexOf("group by")+9,replaceIndex );
//			}
//			
//			if(temp1.contains(",")){
//				if(whereClause=="")
//				{
//				String[] array = temp1.split(",");
//				int len = array.length;
//				for ( int a=0; a < len; a++) {
//					replacement = replacement.concat(array[a] + " = ? ");
//					if(a < (len-1)){
//						replacement = replacement.concat(" and ");
//					}
//				}
//				}
//				else
//				{
//					replacement = replacement.concat(whereClause);	
//				}
//			}else{
//				
//				replacement = replacement.concat(temp1 + " = ? ");
//			}
//			System.out.println("replacement" + replacement) ;
//			System.out.println("replacement" + temp) ;
//			System.out.println(query.lastIndexOf("group by"));
//			System.out.println(query.lastIndexOf("where"));
//			if(query.contains("where")){
//				if (query.lastIndexOf("group by") < query.lastIndexOf("where"))
//				customQuery = temp.replace(query.substring(query.lastIndexOf("group by"), replaceIndex)," and " + replacement);
//				else
//					customQuery = temp.replace(query.substring(query.lastIndexOf("group by"), replaceIndex)," where " + replacement);
//			}else{
//				customQuery = temp.replace(query.substring(query.lastIndexOf("group by"), replaceIndex)," where " + replacement);
//			}
//		}
//
//		System.out.println("customQuery in drilldown --> " + customQuery);
//		
//	}

}
