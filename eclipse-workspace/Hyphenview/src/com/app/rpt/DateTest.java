package com.app.rpt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        Calendar calEnd = new GregorianCalendar();
		DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		try
		{
		Date Date123 = df2.parse("11/02/2019");
		
        calEnd.setTime(Date123);
        calEnd.set(Calendar.DAY_OF_YEAR, calEnd.get(Calendar.DAY_OF_YEAR)-1);
        calEnd.set(Calendar.HOUR_OF_DAY, 0);
        calEnd.set(Calendar.MINUTE, 0);
        calEnd.set(Calendar.SECOND, 0);
        calEnd.set(Calendar.MILLISECOND, 0);
        Date firstDate = calEnd.getTime();	
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        calEnd.set(Calendar.MILLISECOND, 59);
        Date lastDate = calEnd.getTime();
        
       // System.out.println("Daily - firstDate123day " + day);
       // calendar.set(year, month, day, 0, 0, 0);
        //firstDate = calendar.getTime();
        System.out.println("Daily - firstDate " + firstDate);
       // calendar.set(year, month, day, 23, 59, 59);
        System.out.println("Daily - lastDate " + lastDate);
        Calendar calEndDailyMTD = new GregorianCalendar();
        calEndDailyMTD.setTime(Date123);
        
        
        calEndDailyMTD.set(Calendar.DAY_OF_YEAR, calEndDailyMTD.get(Calendar.DAY_OF_YEAR)-1);

        calEndDailyMTD.set(Calendar.HOUR_OF_DAY, 23);
        calEndDailyMTD.set(Calendar.MINUTE, 59);
        calEndDailyMTD.set(Calendar.SECOND, 59);
        calEndDailyMTD.set(Calendar.MILLISECOND, 59);
        Date lastDate1 = calEndDailyMTD.getTime();
        
        calEndDailyMTD.set(Calendar.DAY_OF_MONTH,calEndDailyMTD.getActualMinimum(Calendar.DAY_OF_MONTH));
        calEndDailyMTD.set(Calendar.HOUR_OF_DAY, 0);
        calEndDailyMTD.set(Calendar.MINUTE, 0);
        calEndDailyMTD.set(Calendar.SECOND, 0);
        calEndDailyMTD.set(Calendar.MILLISECOND, 0);
        Date firstDate1 = calEndDailyMTD.getTime();	

        System.out.println("Dailymtd - firstDate1 " + firstDate1);
       // calendar.set(year, month, day, 23, 59, 59);
        System.out.println("DailyMTD - lastDate1 " + lastDate1) ; 
        
        Calendar calWeekly = new GregorianCalendar();
        calWeekly.setTime(new Date());
        calWeekly.set(Calendar.DAY_OF_YEAR, calWeekly.get(Calendar.DAY_OF_YEAR)-1);
        calWeekly.set(Calendar.HOUR_OF_DAY, 23);
        calWeekly.set(Calendar.MINUTE,59);
        calWeekly.set(Calendar.SECOND, 59);
        calWeekly.set(Calendar.MILLISECOND, 59);
        Date firstDate2 = calWeekly.getTime();	
        
        calWeekly.set(Calendar.DAY_OF_YEAR, calWeekly.get(Calendar.DAY_OF_YEAR)-6);

        calWeekly.set(Calendar.HOUR_OF_DAY, 0);
        calWeekly.set(Calendar.MINUTE, 0);
        calWeekly.set(Calendar.SECOND, 0);
        calWeekly.set(Calendar.MILLISECOND, 0);
        Date lastDate2 = calWeekly.getTime();
        


        System.out.println("Weekly - firstDate " + firstDate2);
       // calendar.set(year, month, day, 23, 59, 59);
        System.out.println("Weekly lastDate1 " + lastDate2) ;        
        Calendar calMonthly = new GregorianCalendar();
        calMonthly.set(Calendar.DATE, 1);
        calMonthly.add(Calendar.DAY_OF_MONTH, -1);
        calMonthly.set(Calendar.HOUR_OF_DAY, 23);
        calMonthly.set(Calendar.MINUTE,59);
        calMonthly.set(Calendar.SECOND, 59);
        calMonthly.set(Calendar.MILLISECOND, 59);
        Date lastDate3 = calMonthly.getTime();	
        
        calMonthly.set(Calendar.DAY_OF_MONTH,calMonthly.getActualMinimum(Calendar.DAY_OF_MONTH));

        calMonthly.set(Calendar.HOUR_OF_DAY, 0);
        calMonthly.set(Calendar.MINUTE, 0);
        calMonthly.set(Calendar.SECOND, 0);
        calMonthly.set(Calendar.MILLISECOND, 0);
        Date firstDate3 = calMonthly.getTime();
        


        System.out.println("Monthly - firstDate " + firstDate3);
       // calendar.set(year, month, day, 23, 59, 59);
        System.out.println("Monthly lastDate1 " + lastDate3) ;  
        
		}
		catch(Exception e)
		{
			
		}   
        
	}

}
