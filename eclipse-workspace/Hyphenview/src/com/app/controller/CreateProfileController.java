package com.app.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.CustomerDao;
import com.app.dao.UserAccountDao;
import com.app.object.Customer;
import com.app.object.UserAccount;

import era.util.encryption;

/**
 * Servlet implementation class RegisterUserController
 */
@WebServlet("/CreateProfileController")
public class CreateProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateProfileController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In the Get method of CreateProfileController..");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In the Post method of CreateProfileController..");
		String customerName = request.getParameter("customerName");
		LOGGER.info("customerName in servlet --> " + customerName);
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String gender = request.getParameter("gender");
		LOGGER.info("gender in servlet --> " + gender);
		String email = request.getParameter("email");
		LOGGER.info("email in servlet --> " + email);
		String address1 = request.getParameter("address1");
		String address2 = request.getParameter("address2");
		String address = address1.concat(", " + address2);
		String city = request.getParameter("city");
		String zipCode = request.getParameter("zipCode");
		String country = request.getParameter("country");
		String state = request.getParameter("state");
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");
		String customerId = request.getParameter("customerId");
		LOGGER.info("customerId in servlet --> " + customerId);
		
		//create an entry in customer table
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setCustomerName(customerName);
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setGender(gender);
		customer.setAddress(address);
		customer.setCity(city);
		customer.setContactNumber(phone);
		customer.setCountry(country);
		customer.setState(state);
		customer.setPostalCode(zipCode);
		customer.setEmailId(email);
		CustomerDao customerDao = new CustomerDao();
		int rowsInserted = customerDao.updateCustomer(customer);
		
		if(rowsInserted > 0){
			//create an entry in user account table
			UserAccount userAccount = new UserAccount();
			userAccount.setUserName(firstName+ " " +lastName);
			userAccount.setPassword(encryption.encrypt(password));
			userAccount.setUserStatus(true);
			userAccount.setEmailId(email);
			UserAccountDao userAccountDao = new UserAccountDao();
			int count = userAccountDao.createUserAccount(userAccount);
			if(count > 0){
				request.setAttribute("Message", "Registration Successful. Login to proceed.");
				/*HttpSession session = request.getSession();
				session.setAttribute("emailId", email);
				session.setAttribute("username", email);*/
				getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
			}else{
				request.setAttribute("Message", "User Registration Failed. Please try again.");
				getServletContext().getRequestDispatcher("/CreateProfile.jsp").forward(request, response);
			}
		}
		else{
			request.setAttribute("Message", "User Registration Failed. Please try again.");
			getServletContext().getRequestDispatcher("/CreateProfile.jsp").forward(request, response);
		}
		
	}

}
