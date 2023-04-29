package com.app.rpt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.TimerTask;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.SendEmailOffice365;
import com.app.dao.EraviewDBconnection;

// Create a class extends with TimerTask
public class ScheduledTask extends TimerTask {

	static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTask.class);
	Boolean resFlag = true;
	Boolean dashQuery = false ;
	Date now; // to display current time
	String toAddress, reportname, interval, reportId, scheduleid;
	Timestamp scheduledtime, actualTime, startDateReport;
	String ccAddress = "";
	String reportTitle = "" , reportIDEB="" ,emailBodyContent ="";
	
	// Add your task here
	public void run() {
		now = new Date(); // initialize date
      
		try {
			Context ctx = new InitialContext();

			EraviewDBconnection dbconn = new EraviewDBconnection();
			java.sql.Connection conn = dbconn.getLocalDBConnection();
			Statement stmnt = conn.createStatement();
			System.out.println("Start Time is :" + now); // Display current time
			actualTime = new Timestamp(new Date().getTime());
			String RPTQuery = "select scheduleid,reportid,scheduledtime,scheduledIntervalDays,scheduledIntervalTime,status,emailid,SchedulerPeriod,a.reportid,a.emailcc,a.startDate,a.reportTitle,a.reportIDEB,a.emailBodyContent from schedulerinfo a";
			//String DashboardQuery = "select scheduleid,scheduledtime,scheduledIntervalDays,scheduledIntervalTime,status,emailid,SchedulerPeriod,a.emailcc,a.startDate,a.reportTitle,a.reportIDEB,a.emailBodyContent from dashboard_schedulerinfo a";
			String DashboardQuery = "select * from dashboard_schedulerinfo";
			if(resFlag){
				LOGGER.info("inside RPTQuery dataset");
				ResultSet res = stmnt.executeQuery(RPTQuery);
				initiatingScheduler(res,dashQuery);
				}
			if(!resFlag){
 				LOGGER.info("inside DASHQuery dataset");
				dashQuery = true;
				ResultSet res = stmnt.executeQuery(DashboardQuery);
				initiatingScheduler(res,dashQuery);}
			LOGGER.info("End Time is :" + now); // Display current time
			stmnt.close();
			conn.close();
		} catch (Exception e) {
			
			LOGGER.info("Exception in mail" + e.getMessage());

		}


	}
	
	public void initiatingScheduler(ResultSet res, boolean dashQuery ) throws SQLException, Exception {
		LOGGER.info("dashquery flag-->"+dashQuery);
		while (res.next()) {
			scheduleid = res.getString(1);
			scheduledtime = res.getTimestamp(2);
			toAddress = res.getString(6);
			
			
			interval = res.getString(7);

			reportId = res.getString(8);
			ccAddress = res.getString(9);
			startDateReport = res.getTimestamp(10);
			reportTitle = res.getString(11);
			reportIDEB = res.getString(12);
			emailBodyContent = res.getString(13); 
			LOGGER.info("scheduleid->"+scheduleid+"\ntoAddress->"+toAddress	+"\nscheduledtime"+scheduledtime+"\ninterval"+interval+"\nreportId"+reportId+"\nccAddress"+ccAddress+
					"\nstartDateReport"+startDateReport+"\nreportTitle"+reportTitle+
					"\nreportIDEB"+reportIDEB+"\nemailBodyContent"+emailBodyContent+"\n"+"dashQuery-"+dashQuery);
		//	if (actualTime.after(scheduledtime) || actualTime.equals(scheduledtime)) {
				if (interval.equalsIgnoreCase("Daily") || interval.equalsIgnoreCase("DailyMTD")) {
					LOGGER.info("inside daily check actualTime->"+actualTime+" "+scheduledtime);
				//	if (actualTime.getHours() == scheduledtime.getHours()) {
						LOGGER.info("inside actualTime& scheduledtime check");
						//if (actualTime.getMinutes() == scheduledtime.getMinutes()) {
							System.out.println("Mail is going to be triggered" + scheduledtime.getHours());
							SendEmailOffice365 mailObject = new SendEmailOffice365();
							if(dashQuery) {
								System.out.println("inside dashreports Daily check");
								mailObject.sendEmailDashReports(toAddress, reportId, interval, ccAddress, startDateReport, reportTitle, scheduleid, reportIDEB, emailBodyContent);
							}else {
							mailObject.sendEmail(toAddress, reportname, reportId, interval, ccAddress,
									startDateReport, reportTitle, scheduleid,reportIDEB , emailBodyContent,dashQuery);}
							LOGGER.info("Mail Triggered TO :" + toAddress);
					//	}
					}
					
			//	}
				if (interval.equalsIgnoreCase("Weekly")) {
					if (actualTime.getDay() == scheduledtime.getDay()) {
						if (actualTime.getHours() == scheduledtime.getHours()) {
							if (actualTime.getMinutes() == scheduledtime.getMinutes()) {
								System.out.println("Mail is going to be triggered Weekly" + scheduledtime.getHours());
								SendEmailOffice365 mailObject = new SendEmailOffice365();
								if(dashQuery){
									System.out.println("inside dashreports weekly check");
									mailObject.sendEmailDashReports(toAddress, reportId, interval, ccAddress, startDateReport, reportTitle, scheduleid, reportIDEB, emailBodyContent);
								}else{
								mailObject.sendEmail(toAddress, reportname, reportId, interval, ccAddress,
										startDateReport, reportTitle, scheduleid,reportIDEB , emailBodyContent,dashQuery);}
								LOGGER.info("Mail Triggered TO :" + toAddress);
								}
						}
					}

				}
				if (interval.equalsIgnoreCase("Monthly")) {
					if (actualTime.getDate() == scheduledtime.getDate()) {
						if (actualTime.getHours() == scheduledtime.getHours()) {
							if (actualTime.getMinutes() == scheduledtime.getMinutes()) {
								System.out.println("Mail is going to be triggeredMonthly " + scheduledtime.getHours());
								SendEmailOffice365 mailObject = new SendEmailOffice365();
								if(dashQuery){
									System.out.println("inside dashreports Monthly check");
									mailObject.sendEmailDashReports(toAddress, reportId, interval, ccAddress, startDateReport, reportTitle, scheduleid, reportIDEB, emailBodyContent);
								}else{
								mailObject.sendEmail(toAddress, reportname, reportId, interval, ccAddress,
										startDateReport, reportTitle, scheduleid,reportIDEB , emailBodyContent,dashQuery);}
								LOGGER.info("Mail Triggered TO :" + toAddress);
							}
						}
					}

				}
			//}

		   }
		resFlag = false;
	}
	
}
