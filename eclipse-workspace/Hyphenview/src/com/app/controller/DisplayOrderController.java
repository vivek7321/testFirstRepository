package com.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.ReportTemplateDao;

/**
 * Servlet implementation class DisplayOrderController
 */
@WebServlet("/DisplayOrderController")
public class DisplayOrderController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayOrderController() {
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
		LOGGER.info("In post method of DisplayOrderController..");
		String reportIdA = request.getParameter("reportIdA");
		String displayOrderA = request.getParameter("displayOrderA");
		String reportIdB = request.getParameter("reportIdB");
		String displayOrderB = request.getParameter("displayOrderB");
		System.out.println("displayOrderA" + displayOrderA);
		System.out.println("displayOrderB" + displayOrderB);
		
		ReportTemplateDao reportTemplateDao = new ReportTemplateDao();
		if (displayOrderA!="" && displayOrderB !="")
		{
		int updateA = reportTemplateDao.updateReportTemplateOrder(reportIdA, displayOrderB);
		int updateB = reportTemplateDao.updateReportTemplateOrder(reportIdB, displayOrderA);
		
		response.setContentType("application/text");
		if(updateA >0 && updateB > 0){
			response.getWriter().write("Orders swapped successfully..");
		}else{
			response.getWriter().write("Orders not swapped...");
		}
		}
	}

}
