package com.app.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import era.database.connection;

public class TestPostgres {

	public static void main(String[] args) {
	      Connection c = null;
	      try {
	         /*Class.forName("org.postgresql.Driver");
	         c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/HPSMTest", "postgres", "Admin123*");*/
	    	  
	    	  // Postgres connection using ERA lib
	    	 // c = connection.getPostgreSqlConnection("localhost", "5432", "HPSMTest", "postgres", "Admin123*");
	    	  
	    	  // DB2 Connection
	    	  /*Class.forName("com.ibm.db2.jcc.DB2Driver");
		      c = DriverManager.getConnection("jdbc:db2://localhost:50000/sample", "db2admin", "Admin123*");*/
		      
		      // connection using ERA jar
		      c = connection.getDB2Connection("localhost", "50000", "sample", "db2admin", "Admin123*");
	    	  
	    	  System.out.println("Opened database successfully");
	    	  c.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      
	   }


}
