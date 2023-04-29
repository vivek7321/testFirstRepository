package com.app.rpt;


import java.util.Timer;



//Main class
public class SchedulerMail {
	//public static void main(String args[]) throws InterruptedException {
		
		public void callScheduler() throws InterruptedException
		{

		Timer time = new Timer(); // Instantiate Timer Object
		ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
		time.schedule(st, 0, 60000); // Create Repetitively task for every 1 Min

		
	}
}