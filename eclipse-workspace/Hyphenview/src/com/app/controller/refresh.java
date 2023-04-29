package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.ConstructDashboard;
import com.app.dao.EraviewDBconnection;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import era.util.encryption;

/**
 * Servlet implementation class refresh
 */
@WebServlet("/refresh")
public class refresh extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public refresh() {
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
		// TODO Auto-generated method stub
		
		
		
		HttpSession session = request.getSession();
		String emailId=(String) session.getAttribute("emailId");
		String roleName = (String)request.getSession().getAttribute("roleName");
		String userId = (String)request.getSession().getAttribute("userId");
		String dasboardId = "1";
		// dash board content
		ConstructDashboard constructDashboard = new ConstructDashboard();
		Map<String, String> dashMap = constructDashboard.getDashboardContent(emailId,session,session.getId(),roleName,userId,dasboardId);
		if(dashMap==null || dashMap.isEmpty()){
			session.setAttribute("Message", "Please add reports to be displayed in the dashboard.");
		}else{
			session.setAttribute("dashMap", dashMap);
		}
		
		
	}

}
