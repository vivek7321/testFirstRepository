package com.app.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestartApp implements Runnable{
	
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Override
	public void run() {
		LOGGER.debug("In runnable class...");
		String directory = System.getenv("SERVER_HOME");
		
		LOGGER.debug("Creating the command to execute the jar file to restart the service..");
        //Execute the jar file to restart the tomcat service
        String[] execJar = {"cmd", "/c", "java -jar \"" + directory + "\\ServerRestart.jar\" >> RestartLog.txt"};
        try {
			Process p = Runtime.getRuntime().exec(execJar);
			p.waitFor();
        } catch (IOException e) {
			e.printStackTrace();
		} 	catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
	}

}
