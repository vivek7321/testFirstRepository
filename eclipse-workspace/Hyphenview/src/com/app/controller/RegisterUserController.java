package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.common.SendEmailOffice365;
import com.app.dao.CustomerDao;
import com.app.object.Customer;

import era.util.encryption;

/**
 * Servlet implementation class RegisterUserController
 */
@WebServlet("/RegisterUserController")
public class RegisterUserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterUserController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In the Get method of RegisterUserController..");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In the Post method of RegisterUserController..");
		String customerName = request.getParameter("companyName");
		String email = request.getParameter("email");
		HttpSession session = request.getSession();
		
		// check if an existing user
		CustomerDao customerDao = new CustomerDao();
		Customer cust = customerDao.fetchCustomerByEmail(email);
		if(cust != null && cust.getEmailId() != null){
			session.setAttribute("Message", "Email Id already exists.");
		}else{
			//create an entry in customer table
			Customer customer = new Customer();
			customer.setCustomerName(customerName);
			customer.setEmailId(email);
			UUID uuid = UUID.randomUUID();
	        String randomUUIDString = uuid.toString();
			customer.setUniqueId(randomUUIDString);
			LOGGER.info("the unique id generated is --> " + randomUUIDString);;
			
			int rowsInserted = customerDao.createCustomer(customer);
			String encryptedUUID = encryption.encrypt(randomUUIDString);
			//fetch the register url from properties file
			InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
			Properties p = new Properties();
			p.load(is);
			String registerUrl = p.getProperty("registerUrl");
			String url = registerUrl + "?email="+email + "&uniqueId="+encryptedUUID;
			if(rowsInserted > 0){
				SendEmailOffice365 emailOffice365 = new SendEmailOffice365();
				emailOffice365.sendRegistrationEmail(url, email);
				session.setAttribute("Message", "Email has been sent to the registered e-mail id. Kindly click on the link provided in the email to proceed.");
			}else{
				session.setAttribute("Message", "User Registration Failed. Please try again.");
			}
		}
		getServletContext().getRequestDispatcher("/Register.jsp?id=0").forward(request, response);
		
	}

}
