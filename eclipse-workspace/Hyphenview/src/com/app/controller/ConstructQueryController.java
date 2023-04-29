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
 * Servlet implementation class ConstructQueryController
 */
@WebServlet("/ConstructQueryController")
public class ConstructQueryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConstructQueryController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In get method of ConstructQueryController...");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("In post method of ConstructQueryController...");
		String database = request.getParameter("database");
		String schema = request.getParameter("schema");
		
		String query = null;
		
		String selectedColumns[] = request.getParameterValues("selectedColumns"); 
		String selectClause = null;
		if (selectedColumns != null && selectedColumns.length != 0) {
			selectClause = "select ";
			for (int i = 0; i < selectedColumns.length; i++) {
				if(i>0){
					selectClause = selectClause.concat(",");
				}
				selectClause = selectClause.concat(selectedColumns[i]);
			}
		}
		LOGGER.info("selectClause --> " + selectClause);
		
		// from clause
		String select[] = request.getParameterValues("selectedList"); 
		String fromClause = null;
		if (select != null && select.length != 0) {
			fromClause = "from ";
			for (int i = 0; i < select.length; i++) {
				if(i>0){
					fromClause = fromClause.concat(",");
				}
				fromClause = fromClause.concat(select[i]);
			}
		}
		LOGGER.info("fromClause --> " + fromClause);
		
		// where clause
		int whereCount = Integer.parseInt(request.getParameter("whereCount"));
		LOGGER.info("whereCount -- > " + whereCount);
		String whereClause = null;
		int updatedWhereCount = 0;
		for(int i = 1; i <= whereCount; i++){
			String lhs = request.getParameter(("column1_").concat(String.valueOf(i)));
			LOGGER.info("lhs --> " + i + " --- " + lhs);
			String operator = request.getParameter(("symbols_").concat(String.valueOf(i)));
			LOGGER.info("operator --> " + i + " --- " + operator);
			String rhs = request.getParameter(("column2_").concat(String.valueOf(i)));
			LOGGER.info("rhs --> " + i + " --- " + rhs);
			if((null != lhs && !lhs.isEmpty()) && (rhs == null || rhs.isEmpty())){
				String tempVal = request.getParameter(("whereOptionValue_").concat(String.valueOf(i)));
				if(null != tempVal && !tempVal.isEmpty()){
					rhs = "'".concat(tempVal).concat("'");
				}
			}
			if((lhs != null && !lhs.isEmpty()) && (operator != null && !operator.isEmpty()) && (rhs != null && !rhs.isEmpty())){
				request.setAttribute("where", "true");
				if(whereClause == null){
					whereClause = "where ";
				}else{
					String condition = request.getParameter(("condition_").concat(String.valueOf(i)));
					whereClause = whereClause.concat(" " + condition + " ");
				}
				whereClause = whereClause.concat(lhs).concat(operator).concat(rhs);
				updatedWhereCount++;
				LOGGER.info("whereClause " + i + "   " + whereClause);
				LOGGER.info("updatedWhereCount --> " + updatedWhereCount);
			}
		}
		request.setAttribute("updatedWhereCount", updatedWhereCount);
		LOGGER.info("whereClause --> " + whereClause);
		
		//group by clause
		String groupbyClause = null; 
		String groupColumn = request.getParameter("groupbyColumn");
		if(groupColumn!= null && !groupColumn.equalsIgnoreCase("null") && !groupColumn.isEmpty()){
			groupbyClause = "group by " + groupColumn;
		}
		
		//order by clause
		String orderbyClause = null; 
		String orderColumn = request.getParameter("orderbyColumn");
		if(orderColumn!= null && !orderColumn.equalsIgnoreCase("null") && !orderColumn.isEmpty()){
			orderbyClause = "order by " + orderColumn;
		}
		
		//function clause
		String functionClause = null; 
		String function = request.getParameter("function");
		String functionColumn = request.getParameter("functionColumn");
		if(functionColumn!= null && !functionColumn.equalsIgnoreCase("null") && !functionColumn.isEmpty()){
			functionClause = function + "(" + functionColumn + ")";
			selectClause = selectClause.concat(", ").concat(functionClause);
		}
		
		query = selectClause.concat(" ").concat(fromClause);
		if (whereClause != null && !whereClause.isEmpty()){
			query = query.concat(" ").concat(whereClause);
		}
		if (groupbyClause != null && !groupbyClause.isEmpty()){
			query = query.concat(" ").concat(groupbyClause);
		}
		if (orderbyClause != null && !orderbyClause.isEmpty()){
			query = query.concat(" ").concat(orderbyClause);
		}
		LOGGER.info("complete query built --> " + query);
		request.setAttribute("customQuery", query);
		
		getServletContext().getRequestDispatcher("/ShowQuery.jsp").forward(request, response);
	}

}
