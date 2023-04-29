package com.app.controller;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.dao.ReportFileDao;
import com.app.object.ReportFileDetails;

/**
 * Servlet implementation class ReportFileController
 */
@WebServlet("/ReportFileController")
public class ReportFileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportFileController() {
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
		LOGGER.info("In the Post method of  ReportFileController..");
		String uploadfilepath = request.getParameter("uploadfilepath");
		LOGGER.debug("uploadfilepath in  ReportFileController --> " + uploadfilepath);
		String email = request.getParameter("email");
		
		
		/*File file ;
		   int maxFileSize = 5000 * 1024;
		   int maxMemSize = 5000 * 1024;
		   ServletContext context = this.getServletContext();
		   String filePath ="C:\\DownloadedFiles\\";

		   // Verify the content type
		   String contentType = request.getContentType();
		   
		   if ((contentType.indexOf("multipart/form-data") >= 0)) {
		      DiskFileItemFactory factory = new DiskFileItemFactory();
		      // maximum size that will be stored in memory
		      factory.setSizeThreshold(maxMemSize);
		      
		      // Location to save data that is larger than maxMemSize.
		      factory.setRepository(new File("c:\\temp"));

		      // Create a new file upload handler
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      
		      // maximum file size to be uploaded.
		      upload.setSizeMax( maxFileSize );
		      
		      try { 
		         // Parse the request to get file items.
		         List fileItems = upload.parseRequest(request);

		         // Process the uploaded file items
		         Iterator i = fileItems.iterator();
		         while ( i.hasNext () ) {
		             FileItem fi = (FileItem)i.next();
		             if ( !fi.isFormField () ) {
		                // Get the uploaded file parameters
		                String fieldName = fi.getFieldName();
		                LOGGER.debug("fieldName --> " + fieldName);
		                String fileName = fi.getName();
		                LOGGER.debug("fileName --> " + fileName);
		                LOGGER.debug("fileName --> " + fi.getString());
		                boolean isInMemory = fi.isInMemory();
		                long sizeInBytes = fi.getSize();
		             
		                // Write the file
		                if( fileName.lastIndexOf("\\") >= 0 ) {
		                   file = new File( filePath + 
		                   fileName.substring( fileName.lastIndexOf("\\"))) ;
		                } else {
		                   file = new File( filePath + 
		                   fileName.substring(fileName.lastIndexOf("\\")+1)) ;
		                }
		                fi.write( file ) ;
		             }
		          }
		       } catch(Exception ex) {
		          System.out.println(ex);
		       }
		   }*/
		
		ReportFileDetails reportFileDetails = null;
		
		//check whether the file path is already added
		ReportFileDao fileDao = new ReportFileDao();
		reportFileDetails = fileDao.fetchReportFileDetails(uploadfilepath, email);
		
		if(null != reportFileDetails && null != reportFileDetails.getReportFileDetailsId()){
			LOGGER.debug("report file already added...");
			request.setAttribute("Message", "Report file has been added already.");
			getServletContext().getRequestDispatcher("/QueryReports.jsp").forward(request, response);
		}else{
			reportFileDetails = new ReportFileDetails();
			reportFileDetails.setReportFilePath(uploadfilepath);
			reportFileDetails.setReportFileName("");
			reportFileDetails.setEmailId(email);
			
			int maxOrder = fileDao.fetchMaxFileDisplayOrder(email);
			reportFileDetails.setDisplayOrder(maxOrder+1);
			
			// Insert the record
			fileDao.insertReportFile(reportFileDetails);
			
			LOGGER.info("Report File " + uploadfilepath + " added successfully.");
			request.setAttribute("Message", "Report File added successfully.");
			getServletContext().getRequestDispatcher("/QueryReports.jsp").forward(request, response);
		}
		
	}

}
