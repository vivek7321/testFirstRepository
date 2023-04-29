package com.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.CustomerDao;
import com.app.dao.UserAccountDao;
import com.app.object.Customer;
import com.app.object.UserAccount;

import era.util.encryption;

/**
 * Servlet implementation class ValidateUserController
 */
@WebServlet("/CreateHyphenViewProfile")
public class CreateHyphenViewProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateHyphenViewProfile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In the Get method of CreateHyphenViewProfile..");
		String uniqueId = request.getParameter("uniqueId");
		String email = request.getParameter("email");
		HttpSession session = request.getSession();
		
		CustomerDao customerDao = new CustomerDao();
		LOGGER.info("email passed.. - > " + email);
		Customer cust = customerDao.fetchCustomerByEmail(email);
		if(cust != null && cust.getEmailId() != null){
			LOGGER.info("Icustomer present..");
			if(uniqueId.equalsIgnoreCase(encryption.encrypt(cust.getUniqueId()))){
				LOGGER.info("unique id matched..");
				request.setAttribute("companyName", cust.getCustomerName());
				LOGGER.info("cust.getCustomerName() --> " + cust.getCustomerName());
				request.setAttribute("email", cust.getEmailId());
				request.setAttribute("customerId", cust.getCustomerId());
				getServletContext().getRequestDispatcher("/CreateProfile.jsp").forward(request, response);
			}else{
				LOGGER.info("unique id not matched..");
				UserAccountDao userAccountDao = new UserAccountDao();
				UserAccount userAccount = userAccountDao.fetchUserAccountByEmail(email);
				if(userAccount!= null && userAccount.getEmailId()!=null){
					session.setAttribute("Message", "Account already exists. Please Login");
					getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
				}
			}
			
		}
		
		LOGGER.info("customer nt present..");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In the Post method of CreateHyphenViewProfile..");
		String uniqueId = request.getParameter("uniqueId");
		String email = request.getParameter("email");
		HttpSession session = request.getSession();
		
		CustomerDao customerDao = new CustomerDao();
		Customer cust = customerDao.fetchCustomerByEmail(email);
		if(cust != null && cust.getEmailId() != null){
			if(uniqueId.equalsIgnoreCase(encryption.encrypt(cust.getUniqueId()))){
				getServletContext().getRequestDispatcher("/CreateProfile.jsp").forward(request, response);
			}else{
				UserAccountDao userAccountDao = new UserAccountDao();
				UserAccount userAccount = userAccountDao.fetchUserAccountByEmail(email);
				if(userAccount!= null && userAccount.getEmailId()!=null){
					session.setAttribute("Message", "Account already exists. Please Login");
					getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
				}
			}
			
		}
	}

}
