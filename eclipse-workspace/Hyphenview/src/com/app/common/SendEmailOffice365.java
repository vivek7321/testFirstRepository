package com.app.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.PDFToImage;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.rpt.TableToPDF;
import com.app.rpt.TableToXLS;
import com.app.rpt.reportPdfGenerate;
import com.crystaldecisions.sdk.occa.report.formatteddefinition.model.Image;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfImage;

public class SendEmailOffice365 {

	static final Logger LOGGER = LoggerFactory.getLogger(SendEmailOffice365.class);

	private static String SERVER_SMTP = "";
	private static String SMTP_CONNECTION = "";
	public static int PORT_SERVER_SMTP = 465;
	private static String STD_ACCOUNT = "";
	private static String STD_ACCOUNT_PASSWORD = "";
	
//    private final String from = "nayan.kumar@erasmith.com";

	public Properties getEmailProperties() {

		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
			Properties p = new Properties();
			p.load(is);
			SERVER_SMTP = p.getProperty("mailsmtpserver");
			PORT_SERVER_SMTP = new Integer(p.getProperty("mailsmtpport"));
			SMTP_CONNECTION = p.getProperty("mailConnection");
			
			
			System.out.println("PORT_SERVER_SMTP" + PORT_SERVER_SMTP);
			System.out.println("SERVER_SMTP" + SERVER_SMTP);

		} catch (Exception ex) {
			LOGGER.info("Error in getting E-Mail properties from Config file" + ex.getMessage(), ex);
		}

		final Properties config = new Properties();
		config.put("mail.smtp.auth", "true");
		
		config.put("mail.smtp.host", SERVER_SMTP);
		config.put("mail.smtp.port", PORT_SERVER_SMTP);
		
		
		 if (SMTP_CONNECTION.equalsIgnoreCase("TLS")) {
			 config.put("mail.smtp.ssl.protocols", "TLSv1.2");
			 config.put("mail.smtp.ssl.trust", SERVER_SMTP);
			 config.put("mail.smtp.starttls.enable", "true");
	           
	    } else{
	    	config.put("mail.smtp.socketFactory.port", PORT_SERVER_SMTP);
	    	config.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    	System.out.println("PORT_SERVER_SMTP" + PORT_SERVER_SMTP);
	    	config.put("mail.smtp.port", PORT_SERVER_SMTP);
	    }

		

		return config;
	}

	public void sendEmailResetPass(String password, String toAddress, String username) throws Exception {

		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		p.load(is);

		SERVER_SMTP = p.getProperty("mailsmtpserver");
		PORT_SERVER_SMTP = new Integer(p.getProperty("mailsmtpport"));
		STD_ACCOUNT = p.getProperty("mailaccountusr");
		STD_ACCOUNT_PASSWORD = p.getProperty("mailaccountpwd");

		// decrypt the password
		AESCrypt aesCrypt = new AESCrypt();
		final String decryptedPwd = aesCrypt.decrypt(STD_ACCOUNT_PASSWORD);

		final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(STD_ACCOUNT, decryptedPwd);
			}

		});

		try {

			String subject = "HyphenView - Forgot Password";
			String signature = "mail to: hyphenview.support@erasmith.com";
			final Message message = new MimeMessage(session);
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			message.setFrom(new InternetAddress(STD_ACCOUNT));
			message.setSubject(subject);
			/*
			 * messageBodyPart.
			 * setContent("<img src=\"http://www.rgagnon.com/images/jht.gif\">",
			 * "text/html");
			 */
			messageBodyPart.setText("Dear " + username + "," + "\n" + "\n"
					+ "As per your request, the password of your HyphenView account has been reset. Please use the password system123 for next login."
					+ "\n" + "\n"

					+ "Regards,"

					+ "\n" + "Hyphenview Support Team" + "\n" + signature + "\n" + "Support: +918 826 969 282" + "\n\n"
					+ "------------------------------------------------------------" + "\n"
					+ "This is a system generated mail. Do-not reply. ");

			/* messageBodyPart.setContent("", "text/html"); */
			/*
			 * messageBodyPart.setContent("<html>\n" + "<body>\n" + "\n" +
			 * "<a href=\"https://www.hyphen.software\">\n" + "This is a link</a>\n" + "\n"
			 * + "</body>\n" + "</html>", "text/html");
			 */
			/*
			 * messageBodyPart.
			 * setText("Your Password has been reset. Please use the password " + password
			 * +" for next login.");
			 */
			/* messageBodyPart.setText("For Support: +918 826 969 282"); */
			Multipart multipart = new MimeMultipart();

			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);

			System.out.print("mail for forgot password " + toAddress + " with password " + password);
			message.setSentDate(new Date());
			Transport.send(message);
		} catch (final Exception ex) {

			System.out.println("Error" + ex);
			LOGGER.info("Error: " + ex.getMessage(), ex);
		}
	}

	public void sendRegistrationEmail(String url, String toAddress) {

		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		try {
			p.load(is);

			SERVER_SMTP = p.getProperty("mailsmtpserver");
			PORT_SERVER_SMTP = new Integer(p.getProperty("mailsmtpport"));
			STD_ACCOUNT = p.getProperty("mailaccountusr");
			STD_ACCOUNT_PASSWORD = p.getProperty("mailaccountpwd");

			// decrypt the password
			AESCrypt aesCrypt = new AESCrypt();
			final String decryptedPwd = aesCrypt.decrypt(STD_ACCOUNT_PASSWORD);

			final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(STD_ACCOUNT, decryptedPwd);
				}

			});

			String subject = "HyphenView - New user registration";

			String signature = "mail to: hyphenview.support@erasmith.com";
			final Message message = new MimeMessage(session);
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			message.setFrom(new InternetAddress(STD_ACCOUNT));
			message.setSubject(subject);
			message.setText("html");

			messageBodyPart.setText("Welcome!!! " + "<br>" + "<br>"
					+ "Dear User, <br><br>Kindly click on the below link to proceed with the registration for Hyphenview.<br><br>"
					+ "<a href=\"" + url + "\">CLICK HERE</a>" + "<br><br>" + "Regards,"

					+ "<br>" + "Hyphenview Support Team" + "<br>" + signature + "<br>" + "Support: +918 826 969 282"
					+ "<br><br>" + "------------------------------------------------------------" + "<br>"
					+ "This is a system generated mail. Do-not reply."

					, "UTF-8", "html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart, "text/html");

			System.out.print("mail for registration " + toAddress + " with url " + url);
			message.setSentDate(new Date());
			Transport.send(message);
		} catch (final Exception ex) {

			System.out.println("Error" + ex);
			LOGGER.info("Error: " + ex.getMessage(), ex);
		}
	}
	
	public void sendEmail(String toAddress, String schreportname, String reportId, String schreportInterval,
			String ccAddress, Timestamp startDateReport, String reportTitle, String scheduleid,String reportIDEB , String emailBodyContent, Boolean dashQuery) throws Exception {
      
		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		p.load(is);

		SERVER_SMTP = p.getProperty("mailsmtpserver");
		PORT_SERVER_SMTP = new Integer(p.getProperty("mailsmtpport"));
		STD_ACCOUNT = p.getProperty("mailaccountusr");
		STD_ACCOUNT_PASSWORD = p.getProperty("mailaccountpwd");

		// decrypt the password
		AESCrypt aesCrypt = new AESCrypt();
		final String decryptedPwd = aesCrypt.decrypt(STD_ACCOUNT_PASSWORD);

		final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(STD_ACCOUNT, decryptedPwd);
			}

		});

		try {
			String signature = "mail to: hyphenview.support@erasmith.com";

			String to = toAddress;
			String reportname = schreportname;
			String reportInterval = schreportInterval;
			String subject = reportTitle;
			//String signature = "mail to: hyphenview.support@erasmith.com";
			String cc = ccAddress;
			
			String reportIDEVal = reportIDEB;

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			String todaydate = sdf.format(calendar.getTime()).toUpperCase();

			subject = subject + " - " + todaydate;

			final Message message = new MimeMessage(session);

			reportPdfGenerate pdfGenerate = new reportPdfGenerate();
			//MimeBodyPart messageBodyPart = new MimeBodyPart();
			MimeBodyPart attachFilePart = new MimeBodyPart();
			String fileNameForAttach = null;
			MimeMultipart mp = new MimeMultipart("related");
			BodyPart messageBodyPart = new MimeBodyPart ();
			
			
			String emailBodyContentText = "Dear User, <br><br>Please find the attached scheduled report.<br>";
			
			//System.out.println("emailBodyContent" + emailBodyContent);
			
			if (emailBodyContent!=null)
			{
				if (!emailBodyContent.trim().equalsIgnoreCase(""))
					emailBodyContentText = emailBodyContent;	
			}
			
			//System.out.println("emailBodyContent1" + emailBodyContent);
			
			String[] listofIDS = { to };

			for (String tolist : listofIDS) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(tolist));
			}
			String[] listofIDSCC = { cc };

			for (String cclist : listofIDSCC) {
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cclist));
			}

			// message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setFrom(new InternetAddress(STD_ACCOUNT));
			message.setSubject(subject);
			String fileNameImage = "";
			ArrayList<String> fileNametoEmailBodyArray = new ArrayList<String>();
			int reportIDEValInt =0;
			 log.info("reportIDEB-->" + reportIDEVal);
			
			emailBodyContentText = emailBodyContentText.replaceAll("DD-MM-YY", todaydate);
			if(reportIDEVal!=null && dashQuery!=true)
			{
		    reportIDEValInt = new Integer (reportIDEVal).intValue();
		   
		   log.info("reportIDEValInt" + reportIDEValInt);
			
			}
			
			if (reportIDEValInt == 0)
			{

				
				
						
		    	String htmltext1 = "<font face=\"Arial\"><pre><span style = \"FONT-FAMILY: Arial\">" + emailBodyContentText	+"</span></pre><br>" + "Regards,"


						+ "<br><b>"+ "HyphenView Support" + "</b><br>" 
						+ "<br> </font>";
				
				/*String htmltext1 = "<font face=\"Arial\"><pre><span style = \"FONT-FAMILY: Arial\">" + emailBodyContentText	+"</span></pre><br>"

				+ "Regards,"

				+ "<br>"+ "Hyphenview Support Team" + "<br>" + signature + "<br>"  + "<br>"
				+ "------------------------------------------------------------" + "<br>"
				+ "This is a system generated mail. Do-not reply. ";*/
				messageBodyPart.setContent(htmltext1, "text/html; charset=ISO-8859-1");	
				 mp.addBodyPart(messageBodyPart);

				
				
			}
			else 
			{
				
				fileNametoEmailBodyArray = pdfGenerate.reportGenerate(reportname, reportIDEB, reportInterval, startDateReport, reportTitle,
						scheduleid,"Y");
			
			String fileNametoEmailBody = fileNametoEmailBodyArray.get(0);
			try (final PDDocument document = PDDocument
					.load(new File(fileNametoEmailBody))) {
				PDFRenderer pdfRenderer = new PDFRenderer(document);
				/*for (int page = 0; page < document.getNumberOfPages(); ++page) {
					BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300.001f);
					fileNameImage = "C:\\DownloadedFiles\\" + "REGION WISE AVAILABILITY REPORT -" + page + ".png";
					ImageIOUtil.writeImage(bim, fileNameImage, 300);
				}*/
				
				BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300.001f);
				System.out.println(fileNametoEmailBody.indexOf("."));
				
				fileNametoEmailBody = fileNametoEmailBody.substring(0, fileNametoEmailBody.length() - 4);
				System.out.println("fileNametoEmailBody" + fileNametoEmailBody);
				
				fileNameImage =fileNametoEmailBody + ".png";
				
				
				ImageIOUtil.writeImage(bim, fileNameImage, 300);
				document.close();
			} catch (IOException e) {
				System.err.println("Exception while trying to create pdf document - " + e);
			}
			


			// bodyPart.setContent (content, "text/html");
			// multiPart.addBodyPart (bodyPart);
			
					
		   String htmltext = "<font face=\"Arial\"><pre><span style = \"FONT-FAMILY: Arial\">" + emailBodyContentText	+ "</span></pre><br>"  +"<table width=\"1000px\" ><tr><td><img src=\"cid:image\" width=\"100%\" ></img></td></tr></table>" + "<br><br>"
					+ "Regards,"


						+ "<br><b>"+ "HyphenView Support" + "</b><br>" 
						+ "<br> </font>"; 
		
			/*String htmltext = "<font face=\"Arial\"><pre><span style = \"FONT-FAMILY: Arial\">" + emailBodyContentText	+ "</span></pre><br>"  +"<table width=\"1000px\" ><tr><td><img src=\"cid:image\" width=\"100%\" ></img></td></tr></table>" + "<br><br>"
					
				+ "Regards,"

				+ "<br>"+ "Hyphenview Support Team" + "<br>" + signature + "<br>" + "Support: +918 826 969 282" + "<br><br>"
				+ "------------------------------------------------------------" + "<br>"
				+ "This is a system generated mail. Do-not reply. ";*/

			
			messageBodyPart.setContent(htmltext, "text/html");	
			mp.addBodyPart(messageBodyPart);
			BodyPart imgPart = new MimeBodyPart();
			DataSource img_data = new FileDataSource(fileNameImage);
			imgPart.setDataHandler(new DataHandler(img_data));
			imgPart.setDisposition(MimeBodyPart.INLINE);
			imgPart.setHeader("Content-ID", "<image>");
			mp.addBodyPart(imgPart);
						}
			
			ArrayList<String> fileNames = new ArrayList<String>();
			fileNames = pdfGenerate.reportGenerate(reportname, reportId, reportInterval, startDateReport, reportTitle,
					scheduleid,"N");// change accordingly
			
			
			for (int i = 0; i < fileNames.size(); i++) {
				System.out.println(fileNames.size());
				attachFilePart = new MimeBodyPart();

				FileDataSource source = new FileDataSource(fileNames.get(i).toString());
				

				fileNameForAttach = fileNames.get(i).toString().substring(22);
				System.out.println("fileNameForAttach" + fileNameForAttach);
				System.out.println("fileNameForAttach-Source" + source);
				
				attachFilePart.setDataHandler(new DataHandler(source));
				attachFilePart.setFileName(fileNameForAttach);
			
			    mp.addBodyPart(attachFilePart);

			}		
	       



			
			// add the multipart to the message
			message.setContent(mp,"charset=ISO-8859-1");

			// add code for attachment here!!
			System.out.println("Setting attachment...");

			// define a multipart, and add the text body part and the attachment body part
			// to it
			System.out.println("Building message...");

			// send the email
			System.out.println("Sending message...");
			Transport.send(message);
			System.out.println("Email has been sent!");

		} catch (final Exception ex) {
			LOGGER.info("Error " + ex.getMessage(), ex);
		}
	}

	
	public void sendEmailDashReports(String toAddress, String reportId, String schreportInterval,
			String ccAddress, Timestamp startDateReport, String reportTitle, String scheduleid,String reportIDEB , String emailBodyContent) throws Exception {
		
 		System.out.println("inside sendEmailDashReports method");
		InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
		Properties p = new Properties();
		p.load(is);
		
		SERVER_SMTP = p.getProperty("mailsmtpserver");
		PORT_SERVER_SMTP = new Integer(p.getProperty("mailsmtpport"));
		STD_ACCOUNT = p.getProperty("mailaccountusr");
		STD_ACCOUNT_PASSWORD = p.getProperty("mailaccountpwd");
		
		// decrypt the password
				AESCrypt aesCrypt = new AESCrypt();
				final String decryptedPwd = aesCrypt.decrypt(STD_ACCOUNT_PASSWORD);

				final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(STD_ACCOUNT, decryptedPwd);
					}

				});
				
				
				try {
					String signature = "mail to: hyphenview.support@erasmith.com";

					String to = toAddress;
					String reportInterval = schreportInterval;
					String subject = reportTitle;
					//String signature = "mail to: hyphenview.support@erasmith.com";
					String cc = ccAddress;
					

					SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					String todaydate = sdf.format(calendar.getTime()).toUpperCase();

					subject = subject + " - " + todaydate;

					final Message message = new MimeMessage(session);
					
					message.setFrom(new InternetAddress(STD_ACCOUNT));
					message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
					 message.setSubject(subject);
					 
					 // Creating the message part
			         BodyPart messageBodyPart = new MimeBodyPart();
			         messageBodyPart.setText("Dear User, "
			         		+ "Please find the attached scheduled report.");
			     	String htmltext1 = "<font face=\"Arial\"><pre><span style = \"FONT-FAMILY:  Arial\"></span></pre><br>" + "Regards,"


						+ "<br><b>"+ "HyphenView Support" + "</b><br>" 
						+ "<br> </font>";
				
			        log.info("report title-->"+reportTitle);
			         MimeMultipart multipart = new MimeMultipart("related");
			      // multipart.addBodyPart(messageBodyPart);
			         
			         // attachment
			     	String fileNameImage = null;
			         TableToPDF ttp = new TableToPDF();
			         ArrayList<String> fileNametoEmailBodyArray = new ArrayList<String>();
			          
			        	 log.info("reportIdEB--"+reportIDEB);
			        	 if(!reportIDEB.equalsIgnoreCase("0")){
			        	 //passing reportIDEB in place of scheduleID
			        	 fileNametoEmailBodyArray = ttp.export(reportIDEB,reportInterval,startDateReport,"Y");


			        	 
			        	 String fileNametoEmailBody = fileNametoEmailBodyArray.get(0);
			        	 log.info("fileNametoEmailBody--"+fileNametoEmailBody);
			 			try (final PDDocument document = PDDocument
			 					.load(new File("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+fileNametoEmailBody+ ".pdf"))) {
			 				PDFRenderer pdfRenderer = new PDFRenderer(document);
			 			
			 				BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300.001f);
			 				//fileNametoEmailBody = fileNametoEmailBody.substring(0, fileNametoEmailBody.length() - 4);
			 				log.info("fileNametoEmailBody" + fileNametoEmailBody);
			 				File f =new File("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+fileNametoEmailBody+ ".png");
			 				 fileNameImage = "C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+fileNametoEmailBody + ".png";
			 				
			 				ImageIOUtil.writeImage(bim, fileNameImage, 300);
			 				document.close();
			 			} catch (IOException e) {
			 				log.info("Exception while trying to create pdf document - " + e);
			 			}
			 			if(!reportIDEB.equalsIgnoreCase("0")) {
			 			BodyPart msgBodyPart = new MimeBodyPart();
			 			String htmlText = "<p><b>Dear user please find your attached scheduled report.</p></b><img src=\"cid:image\">"
			 					+ "<br><b>Regards,<br>HyphenView Support</b>";
			 			msgBodyPart.setContent(htmltext1, "text/html");
			 			msgBodyPart.setContent(htmlText, "text/html");
			 			multipart.addBodyPart(msgBodyPart);
			 			}
			 			
			 			MimeBodyPart imgPart = new MimeBodyPart();
						DataSource img_data = new FileDataSource(fileNameImage);
						imgPart.setDataHandler(new DataHandler(img_data));
						imgPart.setDisposition(MimeBodyPart.INLINE);
						imgPart.setHeader("Content-ID", "<image>");
						multipart.addBodyPart(imgPart);
						message.setContent(multipart);
			        	 }
			        		 BodyPart msgBodyPart = new MimeBodyPart();
					 			String htmlText = "<p><b>Dear user please find your attached scheduled report.</p></b>"
					 					+ "<br><b>Regards,<br>HyphenView Support</b>";
					 			msgBodyPart.setContent(htmlText, "text/html");
					 			multipart.addBodyPart(msgBodyPart);
					 			message.setContent(multipart);
			        	 
			         ArrayList<String> pdffile = ttp.export(scheduleid,reportInterval,startDateReport,"N");
			        for(String pdffileContent :pdffile) {
			        	log.info("pdffileContent-->"+pdffileContent);
						File file = new File("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+pdffileContent+ ".pdf");
						   DataSource source = new FileDataSource(file);
				        	 MimeBodyPart messagePart = new MimeBodyPart();
				        	 MimeBodyPart attachment = new MimeBodyPart();
						      attachment.attachFile(file);
						      multipart.addBodyPart(attachment);
						      message.setContent(multipart);
						  
				    
			        }
			       
			        TableToXLS ttx = new TableToXLS();
			        ArrayList<String> xlsfile = ttx.export(scheduleid,reportInterval,startDateReport);
			        for(String xlsfileContent :xlsfile) {
			        	log.info("xlsfileContent-->"+xlsfileContent);
			        	File file = new File("C:\\apache-tomcat-9.0.56\\downloadedFiles\\"+xlsfileContent+ ".xlsx");
						  
			        	 DataSource source = new FileDataSource(file);
			        	 MimeBodyPart messagePart = new MimeBodyPart();
			        	 MimeBodyPart attachment = new MimeBodyPart();
					      attachment.attachFile(file);
					      multipart.addBodyPart(attachment);
					      message.setContent(multipart);
			        	
			     
			        }
			        
			        
			        	 
			         Transport.send(message);
			        log.info("Sent message successfully....");
			         
				}catch (Exception e) {
					 throw new RuntimeException(e);
				}

	}
	/*public static void main(final String[] args) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		String todaydate = sdf.format(calendar.getTime()).toUpperCase();
		
		System.out.println(todaydate);		// new
		// 
		// validate the mail configuration details
		Properties config = new Properties();
		config.put("mail.imap.auth", "true");
		//config.put("mail.smtp.starttls.enable", "true");
		config.put("mail.smtp.host", "outlook.office365.com");
		config.put("mail.smtp.port", "993");
		config.put("mail.smtp.ssl.trust", "outlook.office365.com");
		Session session = Session.getInstance(config, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("malarvizhi.seenivasan@erasmith.com", "");
			}

		});
		try {
			Transport transport = session.getTransport("imap");
			final Message message2 = new MimeMessage(session);
			message2.setFrom(new InternetAddress("malarvizhi.seenivasan@erasmith.com"));

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-YY");
			Calendar calendar1 = Calendar.getInstance();
			calendar.setTime(new Date());
			String output = sdf1.format(calendar1.getTime()).toUpperCase();
			System.out.println("Time" + output);

			transport.connect();

			message2.setRecipient(Message.RecipientType.TO, new InternetAddress("malarvizhi.seenivasan@erasmith.com"));
			message2.setText("Test");
			Multipart mp1 = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			
			

			messageBodyPart.setText("Dear User, <br><br>Please find the attached scheduled report.<br><br>"
					+ "With Regards,"

					+ "<br>" + "Wi-Fi Desk," + "<br>"

					+ "CNOC" + "<br>" + "Railtel Corporation Of India Limited" + "<br>" + "(Ministry of Railways)"
					+ "<br>" + "Email: access-railtel-noc@railtelindia.com" + "<br>" + "Mob: 0124-2714000, 011-22900400"
					+ "<br>" + "------------------------------------------------------------" + "<br>"
					+ "This is a system generated mail. Do-not reply."

					, "UTF-8", "html");

			mp1.addBodyPart(messageBodyPart);
			MimeBodyPart messageBodyPart2 = new MimeBodyPart();
		
			Transport.send(message2);
			transport.close();
			//String filename = "C:\\DownloadedFiles\\REGION WISE AVAILABILITY REPORT.pdf";
			//DataSource source = new FileDataSource(filename);
			//messageBodyPart2.setDataHandler(new DataHandler(source));
			// mp1.a
			// mp1.addBodyPart(messageBodyPart2);

			//InputStream inputStream = new FileInputStream(filename);

			//String inputStreamToSting = inputStream.toString();

			//inputStream.close();
		//	ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(inputStreamToString.getBytes());

			/*if (arrayInputStream != null && arrayInputStream instanceof ByteArrayInputStream) {
				// create the second message part with the attachment from a OutputStrean
				MimeBodyPart attachment = new MimeBodyPart();
				ByteArrayDataSource ds = new ByteArrayDataSource(arrayInputStream, "");
				attachment.setDataHandler(new DataHandler(ds));
				attachment.setDisposition(MimeBodyPart.INLINE);
				attachment.setFileName(filename);
				mp1.addBodyPart(attachment);
			}
			String fileNameImage = "";

			try (final PDDocument document = PDDocument
					.load(new File("C:\\DownloadedFiles\\REGION WISE AVAILABILITY REPORT.pdf"))) {
				PDFRenderer pdfRenderer = new PDFRenderer(document);
				for (int page = 0; page < document.getNumberOfPages(); ++page) {
					BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300.001f);
					fileNameImage = "C:\\DownloadedFiles\\" + "REGION WISE AVAILABILITY REPORT -" + page + ".png";
					ImageIOUtil.writeImage(bim, fileNameImage, 300);
				}
				document.close();
			} catch (IOException e) {
				System.err.println("Exception while trying to create pdf document - " + e);
			}

			// File template = new File ("C:\\DownloadedFiles\\Affected Services2.html");
			// FileUtils readContent = new FileUtils ();
			// String content = readContent.readFileToString (template);
			MimeMultipart multiPart = new MimeMultipart("related");
			// BodyPart bodyPart = new MimeBodyPart ();
			// bodyPart.setContent (content, "text/html");
			// multiPart.addBodyPart (bodyPart);
			
	         // first part (the html)
	         BodyPart messageBodyParttest = new MimeBodyPart();
	         String htmlText = "<H1>Hello</H1><img src=\"cid:image\">";
	         messageBodyParttest.setContent(htmlText, "text/html");
	         // add it
	         multiPart.addBodyPart(messageBodyParttest);


			BodyPart imgPart = new MimeBodyPart();
			DataSource img_data = new FileDataSource(fileNameImage);
			imgPart.setDataHandler(new DataHandler(img_data));
			imgPart.setDisposition(MimeBodyPart.INLINE);
			imgPart.setHeader("Content-ID", "<image>");
			multiPart.addBodyPart(imgPart);

			// add the multipart to the message
			message2.setContent(multiPart);
			//Transport.send(message2);
			transport.close();*/
		//} catch (Exception e) {
			//System.out.println(e.getMessage());
	//}
		//System.out.println("Valid credentials..");
		/*
		 * if(null != session){log.info("session established..."); }else{
		 *log.info("session establishment failed..."); }	
		 * 
		 */
//}
	 final Logger log = LoggerFactory.getLogger(SendEmailOffice365.class);
}