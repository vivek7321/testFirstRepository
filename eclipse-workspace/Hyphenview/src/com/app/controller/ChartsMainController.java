package com.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class QueryReportController
 */
@WebServlet("/ChartsMainController")
public class ChartsMainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChartsMainController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of ChartsMainController..");

		
		
		if(request.getParameter("Back") != null || request.getParameter("Save") != null){
			LOGGER.info("Either Back or Save button is pressed...");
			LOGGER.info("Redirecting to QueryReportController...");
			getServletContext().getRequestDispatcher("/QueryReportController").forward(request, response);
		}else{
			LOGGER.info("Chart background color changed...");
			LOGGER.info("Redirecting to CreateChartsController...");
			getServletContext().getRequestDispatcher("/CreateChartsController").forward(request, response);
		}
	}

}
