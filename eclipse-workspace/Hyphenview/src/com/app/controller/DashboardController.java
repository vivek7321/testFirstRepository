package com.app.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.ConstructDashboard;
import com.app.dao.ReportTemplateDao;
import com.app.object.ReportTemplate;

/**
 * Servlet implementation class QueryReportController
 */
@WebServlet("/DashboardController")
public class DashboardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashboardController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In get method of DashboardController..");
		String email = (String) request.getSession().getAttribute("emailId");
		String roleName = (String)request.getSession().getAttribute("roleName");
		String userId = (String)request.getSession().getAttribute("userId");
		
		 String  dashboardId=request.getParameter("dashBoardId");
		 
			if (dashboardId == null || dashboardId.equalsIgnoreCase("null")) 
				dashboardId = "1";
		 
		 System.out.println("dashboardId" + dashboardId);
			
		LOGGER.info("email value --> " + email);

			
		// update dash board content
		HttpSession session = request.getSession();
		ConstructDashboard constructDashboard = new ConstructDashboard();
		Map<String, String> dashMap = constructDashboard.getDashboardContent(email ,session,session.getId(),roleName ,userId,dashboardId);
		if(dashMap==null || dashMap.isEmpty()){
			session.setAttribute("Message", "Please add reports to be displayed in the dashboard.");
		}else{
			session.setAttribute("dashMap", dashMap);
		} 
		
		getServletContext().getRequestDispatcher("/Hyphenviewhome.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*LOGGER.info("In post method of DashboardController..");
		String email = request.getParameter("email");
		LOGGER.info("email value --> " + email);

			
		// update dash board content
		HttpSession session = request.getSession();
		ConstructDashboard constructDashboard = new ConstructDashboard();
		Map<String, String> dashMap = constructDashboard.getDashboardContent(email, session.getId());
		if(dashMap==null || dashMap.isEmpty()){
			session.setAttribute("Message", "Please add reports to be displayed in the dashboard.");
		}else{
			request.setAttribute("dashMap", dashMap);
		} 
		
		getServletContext().getRequestDispatcher("/Newhome.jsp").forward(request, response);*/
	}

}
