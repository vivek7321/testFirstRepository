package com.app.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.app.common.AESCrypt;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.app.constant.QueryConstants;

import era.database.connection;

/**
 * Servlet implementation class DBConfigController
 */
@WebServlet("/RPTDbConfigController")
public class RPTDbConfigController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RPTDbConfigController() {
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
		LOGGER.info("In the Post method of  RPTDbConfigController..");
		String rdbms = request.getParameter("rptRdbms");
		String serverName = request.getParameter("rptServerName");
		String port = request.getParameter("rptPort");
		String schema = request.getParameter("rptSchema");
		String dbUserName = request.getParameter("rptUserName");
		String dbPassword = request.getParameter("rptPassword");
		String encryptedPwd = null;
		try {
			// encrypt the database password before storing in database
			AESCrypt aesCrypt = new AESCrypt();
			encryptedPwd = aesCrypt.encrypt(dbPassword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//check whether the db connection details are valid
		Connection conn = null;
		try{
			if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
				LOGGER.debug("trying to connect with MS SQL...");
				conn = connection.getSqlServerConnection(serverName, port, null, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.POSTGRESQL)){
				LOGGER.debug("trying to connect with POSTGRESQL...");
				conn = connection.getPostgreSqlConnection(serverName, port, schema, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
				LOGGER.debug("trying to connect with DB2 server...");
				conn = connection.getDB2Connection(serverName, port, schema, dbUserName, dbPassword);
			}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
				LOGGER.debug("trying to connect with Oracle MYSQL server...");
				conn = connection.getMySqlConnection(serverName, port, schema, dbUserName, dbPassword);
			}
		}catch(Exception e){
			LOGGER.debug("Exception trying to connect with DB...");
			e.printStackTrace();
		}
		
		if(null == conn){
			LOGGER.debug("DB connection is null in DBConfigController...");
			request.setAttribute("Message", "Invalid Database Details. Kindly provide valid values.");
			getServletContext().getRequestDispatcher("/RptDbConfiguration.jsp").forward(request, response);
		}else{
			LOGGER.debug("DB connection is not null in AddDatabaseController...");
			try {
				String directory = System.getenv("SERVER_HOME");
				String dbUrl = null;
				String driverClass = null;
				
				if(rdbms.equalsIgnoreCase(QueryConstants.MICROSOFT_MSSQL)){
					dbUrl = "jdbc:sqlserver://"+serverName+":"+port;
					driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				}else if(rdbms.equalsIgnoreCase(QueryConstants.POSTGRESQL)){
					dbUrl = "jdbc:postgresql://"+serverName+":"+port + "/" + schema;
					driverClass = "org.postgresql.Driver";
				}else if(rdbms.equalsIgnoreCase(QueryConstants.DB2SERVER)){
					driverClass = "com.ibm.db2.jcc.DB2Driver";
					dbUrl = "jdbc:db2://"+serverName+":"+port + "/" + schema;
				}else if(rdbms.equalsIgnoreCase(QueryConstants.ORACLE_MYSQL)){
					dbUrl = "jdbc:mysql://"+serverName+":"+port + "/" + schema;
					driverClass = "com.mysql.jdbc.Driver";
				}
				PropertiesConfiguration pc = new PropertiesConfiguration(directory+"/webapps/Hyphenview/WEB-INF/classes/properties/config.properties");
				pc.setProperty("rpt.dbusername", dbUserName);
				pc.setProperty("rpt.dbpassword", encryptedPwd);
				pc.setProperty("rpt.dburl", dbUrl);
				pc.setProperty("rpt.dbport", port);
				pc.setProperty("rpt.databaseName", schema);
				pc.setProperty("rpt.dbservername", serverName);
				pc.setProperty("rpt.rdbms", rdbms);
				pc.save();
				
				// Add resource-ref to web.xml and resource tag to context.xml
				updateResourceXmls(directory, schema, dbUrl, driverClass, dbUserName, dbPassword);
				
			} catch (ConfigurationException e) {
				e.printStackTrace();
				getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			} catch (Exception e){
				e.printStackTrace();
				getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			}finally{
				try{
					if(null != conn){conn.close();}
				}catch(Exception e){ }
			}
			getServletContext().getRequestDispatcher("/MailXmlConfiguration.jsp").forward(request, response);
		}
	}
	
	
private void updateResourceXmls(String severHome, String schema, String dbUrl, String driverClass, String userName, String password) 
		throws TransformerException, ParserConfigurationException, SAXException, IOException{
		
		// update web.xml
		String filepath = severHome+"/webapps/Hyphenview/WEB-INF/web.xml";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(filepath);

		// Get the root element
		Node webApp = doc.getFirstChild();
		
		// create new element resource-ref
		Element resourceRef = doc.createElement("resource-ref");
		Element description = doc.createElement("description");
		description.appendChild(doc.createTextNode("Connection Pool"));
		resourceRef.appendChild(description);
		Element resRefName = doc.createElement("res-ref-name");
		resRefName.appendChild(doc.createTextNode("jdbc/"+schema));
		resourceRef.appendChild(resRefName);
		Element resType = doc.createElement("res-type");
		resType.appendChild(doc.createTextNode("javax.sql.DataSource"));
		resourceRef.appendChild(resType);
		Element resAuth = doc.createElement("res-auth");
		resAuth.appendChild(doc.createTextNode("Container"));
		resourceRef.appendChild(resAuth);
		webApp.appendChild(resourceRef);
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filepath));
		transformer.transform(source, result);
		
		// update context.xml
		String contextFilepath = severHome+"/conf/context.xml";
		/*DocumentBuilderFactory docFactory1 = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder1 = docFactory1.newDocumentBuilder();
		Document contextDoc = docBuilder1.parse(contextFilepath);

		// Get the root element
		Node context = contextDoc.getFirstChild();*/
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document contextDoc = dBuilder.newDocument();
        // root element
        Element rootElement = contextDoc.createElement("Context");
        contextDoc.appendChild(rootElement);
        //WatchedResource element
        Element watchedResource= contextDoc.createElement("WatchedResource");
        watchedResource.appendChild(contextDoc.createTextNode("WEB-INF/web.xml"));
        rootElement.appendChild(watchedResource);
		
		// create element Resource
		Element resource = contextDoc.createElement("Resource");
		resource.setAttribute("auth", "Container");
		resource.setAttribute("driverClassName", driverClass);
		resource.setAttribute("maxIdle", "10");
		resource.setAttribute("maxTotal", "25");
		resource.setAttribute("name", "jdbc/"+schema);
		resource.setAttribute("password", password);
		resource.setAttribute("type", "javax.sql.DataSource");
		resource.setAttribute("url", dbUrl);
		resource.setAttribute("username", userName);
		resource.setAttribute("validationQuery", "select 1");
	//	context.appendChild(resource);
		
		rootElement.appendChild(resource);
		
		// write the content into xml file
		TransformerFactory transformerFactory1 = TransformerFactory.newInstance();
		Transformer transformer1 = transformerFactory1.newTransformer();
		DOMSource source1 = new DOMSource(contextDoc);
		StreamResult result1 = new StreamResult(new File(contextFilepath));
		transformer1.transform(source1, result1);
		
	}

}
