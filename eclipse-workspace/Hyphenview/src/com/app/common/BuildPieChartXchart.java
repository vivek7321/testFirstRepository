package com.app.common;

import java.awt.Color;
import java.sql.ResultSet;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import com.app.dao.QueryDao;
import com.app.object.ReportTemplate;


/**
 * Pie Chart Custom Color Palette
 * 
 * Demonstrates the following:
 * <ul>
 * <li>Pie Chart
 * <li>ChartBuilderPie
 * <li>Custom series palette
 */
public class BuildPieChartXchart  {
 
  public static void main(String[] args) {
 
	
  }
 	

  public PieChart getChart(ReportTemplate reportTemplate) {
 
    // Create Chart
    PieChart chart = new PieChartBuilder().width(800).height(600).title(getClass().getSimpleName()).build();
 
    // Customize Chart
    Color[] sliceColors = new Color[] { new Color(224, 68, 14), new Color(230, 105, 62), new Color(236, 143, 110), new Color(243, 180, 159), new Color(246, 199, 182) };
    chart.getStyler().setSeriesColors(sliceColors);
 
    // Series
       
	try {

		QueryDao queryDao = new QueryDao();

		ResultSet res = null;

		if (null != reportTemplate.getDatabase()) {
			res = queryDao.executeQueryRes(reportTemplate.getEmail(), reportTemplate.getDefinedQuery(),
					reportTemplate.getDatabase(), reportTemplate.getSchema());
		} else {
			res = queryDao.executeReportQuery(reportTemplate);
		}

		int count = 1;
		while (res.next()) {
			chart.addSeries(res.getString(1),new Integer(res.getString(2)));
			
		}

	} catch (Exception ex) {
		ex.printStackTrace();
		System.out.println(ex.getMessage());
	}

    
    
 
    return chart;
  }
  
 
}