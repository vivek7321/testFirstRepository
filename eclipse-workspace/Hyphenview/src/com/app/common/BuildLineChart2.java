package com.app.common;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.app.dao.QueryDao;
import com.app.object.ChartData;
import com.app.object.ReportTemplate;
import com.hyjavacharts.chart.Highchart;
import com.hyjavacharts.model.common.themes.HighchartTheme;
import com.hyjavacharts.model.highcharts.ChartOptions;
import com.hyjavacharts.model.highcharts.YAxis;
import com.hyjavacharts.model.highcharts.constants.HorizontalAlign;
import com.hyjavacharts.model.highcharts.constants.Layout;
import com.hyjavacharts.model.highcharts.constants.VerticalAlign;
import com.hyjavacharts.model.highcharts.series.SeriesLine;
//import com.hyjavacharts.model.highcharts.annotations.Labels;

public class BuildLineChart2 {

	public Highchart configure(ReportTemplate reportTemplate) {
		Highchart highChart = new Highchart();
		ChartOptions chartOptions = highChart.getChartOptions();
	    
		chartOptions.getTitle().setText("Line Chart - Sample");
		//chartOptions.getSubtitle().setText("Source: thesolarfoundation.com");
	
		YAxis yAxi = new YAxis();
		yAxi.getTitle().setText("Number of Incidents");
		chartOptions.getYAxis().add(yAxi);
		//label = new Labels();
		
		//chartOptions.getXAxisSingle().setCategories(
               // Arrays.asList();
			
		chartOptions.getLegend().setLayout(Layout.VERTICAL).setAlign(HorizontalAlign.RIGHT).setVerticalAlign(VerticalAlign.MIDDLE);
		chartOptions.getPlotOptions()
			.getSeries()
				.setPointStart(2010)
				.getLabel().setConnectorAllowed(false);
		
		chartOptions.getResponsive().getRulesSingle()
			.getCondition().setMaxWidth(500);
		chartOptions.getResponsive().getRulesSingle()
			.getChartOptions().getLegend()
				.setLayout(Layout.HORIZONTAL).setAlign(HorizontalAlign.CENTER).setVerticalAlign(VerticalAlign.BOTTOM);
		
		//Getting Values From Database
		
		/*java.sql.Connection conn = null;
		try
		{
		Statement stmnt = conn.createStatement();
		ResultSet rs = null;
		rs = stmnt.executeQuery("select year(open_time) as yr, case when SEVERITY = 1 then 'CRITICAL' when SEVERITY = 2 then 'HIGH' when SEVERITY = 3 then 'MEDIUM' when SEVERITY = 4 then 'LOW' end, count(incident_id) from hpsm.dbo.INCIDENTSM1 where year(open_time) in (2013, 2014,2015,2016) AND SEVERITY!='NULL' group by year(open_time),SEVERITY ORDER BY year(open_time),SEVERITY");		
		SeriesLine seriesLine = new SeriesLine();
		
	  /*  HashMap<String, HashMap<String,String>> outerMap = new HashMap<String,HashMap<String,String>>();

		 
			        ResultSetMetaData meta = rs.getMetaData();
	        while (rs.next()) {
	   	     HashMap<String,String> innerMap =  new HashMap<String,String>();
	            for (int i = 2; i <= meta.getColumnCount(); i++) {
	                String key = meta.getColumnName(i);
	                String value = rs.getString(key);
	                innerMap.put(key, value);
	            }
	            outerMap.put(rs.getString(1),innerMap);
	        }	
		 
	        Iterator entries = outerMap.entrySet().iterator();
	        while (entries.hasNext()) {
	            Map.Entry entry = (Map.Entry) entries.next();
	            String key = entry.getKey().toString();
	            String value = entry.getValue().toString();
	            System.out.println("Key = " + key + ", Value = " + value);
	        }*/
		 
		/* ResultSetMetaData meta = rs.getMetaData();
	        while (rs.next()) {
	        	for (int i = 1; i <= meta.getColumnCount(); i++)
	        	{
	        		seriesLine.setName(rs.getString(2));
	        	}
	        }
		 
		
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}	
		*/
		 ChartData cData = getChartData(reportTemplate);	
		
		 List<String> xList = cData.baseLabels;
		 LinkedHashMap<String, List<String>> tempMap = cData.getData();
		 

        		 
        		 
        		 for(String key : tempMap.keySet()){
        			 SeriesLine seriesLine = new SeriesLine();
        			 seriesLine.setName(key);
        		 seriesLine.setDataAsArrayNumber(tempMap.get(key).stream().map(Integer::parseInt).collect(Collectors.toList())); 
        		 chartOptions.getSeries().add(seriesLine);

        		 }
        		
        		
      
   
         
		/*SeriesLine seriesLine = new SeriesLine();
		seriesLine.setName("Installation");
		seriesLine.setDataAsArrayNumber();
		seriesLine.setDataAsArrayNumber(Arrays.asList(43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175));
		chartOptions.getSeries().add(seriesLine);
		
		seriesLine = new SeriesLine();
		seriesLine.setName("Manufacturing");
		seriesLine.setDataAsArrayNumber(Arrays.asList(24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434));
		chartOptions.getSeries().add(seriesLine);
		
		seriesLine = new 
		SeriesLine();
		seriesLine.setName("Sales & Distribution");
		seriesLine.setDataAsArrayNumber(Arrays.asList(11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387));
		chartOptions.getSeries().add(seriesLine);
		
		seriesLine = new SeriesLine();
		seriesLine.setName("Project Development");
		seriesLine.setDataAsArrayNumber(Arrays.asList(null, null, 7988, 12169, 15112, 22452, 34400, 34227));
		chartOptions.getSeries().add(seriesLine);
		
		seriesLine = new SeriesLine();
		seriesLine.setName("Other");
		seriesLine.setDataAsArrayNumber(Arrays.asList(12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111));
		chartOptions.getSeries().add(seriesLine); */
		highChart.setTheme(HighchartTheme.SKIES);
		
		String theme =  reportTemplate.getTheme();
		System.out.println("Theme" + theme );
		
		if (theme.equalsIgnoreCase("DARK_UNICA"))
		highChart.setTheme(HighchartTheme.DARK_UNICA);
		if (theme.equalsIgnoreCase("GREY"))
		highChart.setTheme(HighchartTheme.GREY);
		if (theme.equalsIgnoreCase("GRID_LIGHT"))
		highChart.setTheme(HighchartTheme.GRID_LIGHT);
		if (theme.equalsIgnoreCase("SKIES"))
		highChart.setTheme(HighchartTheme.SKIES);
		
		
		return highChart;
	}

	public ChartData getChartData(ReportTemplate reportTemplate){
		ChartData chartData = null;
		QueryDao queryDao = new QueryDao();
		ResultSet res = null;
		List<String> baseLabels = null;
		List<String> dataList = null;
		LinkedHashMap<String, List<String>> data = null;
		
		//temporary maps for rearranging data
		LinkedHashMap<String, LinkedHashMap<String, String>> resultMap = null;
		LinkedHashMap<String, String> internalMap = null;
		
		try{
			if(null != reportTemplate.getDatabase()){
				res = queryDao.executeQueryRes(reportTemplate.getEmail(), reportTemplate.getDefinedQuery(), reportTemplate.getDatabase(), reportTemplate.getSchema());
			}else{
				res = queryDao.executeReportQuery(reportTemplate);
			}
			if(res.getMetaData().getColumnCount() == 2){
				baseLabels = new ArrayList<String>();
				dataList = new ArrayList<String>();
				data = new LinkedHashMap<String, List<String>>();
				while(res.next())
				{
					if(!baseLabels.contains(res.getString(1)))
					{
					baseLabels.add(res.getString(1));
					}
					dataList.add(res.getString(2));
				}
				data.put(reportTemplate.getReportTemplateName(), dataList);
			}else if(res.getMetaData().getColumnCount() == 3){
				resultMap = new LinkedHashMap<String, LinkedHashMap<String,String>>();
				List<String> masterList = new ArrayList<String>();
				while(res.next())
				{
					String tmpLabel = res.getString(1);
					if(!resultMap.keySet().contains(tmpLabel)){
						internalMap = new LinkedHashMap<String, String>();
					}else{
						internalMap = resultMap.get(tmpLabel);
					}
					String tmpString = res.getString(2);
					internalMap.put(tmpString, res.getString(3));
					resultMap.put(tmpLabel, internalMap);
					if(!masterList.contains(tmpString)){
						masterList.add(tmpString);
					}
				}
				
				//populate the baselabel list
				for (String label : resultMap.keySet()){
					if(null == baseLabels){
						baseLabels = new ArrayList<String>();
					}
					if(!baseLabels.contains(label))
					baseLabels.add(label);
				}
				
				// find the key with full set of data
				/*int maxCount = 0;
				int tempCount = 0;
				List<String> masterList = null;
				LinkedHashMap<String, String> tempMap = new LinkedHashMap<String, String>();
				for (String i : resultMap.keySet()) {
					tempMap = resultMap.get(i);
					tempCount = tempMap.size();
					if(tempCount > maxCount){
						maxCount = tempCount;
						for (String str : tempMap.keySet()){
							if(null == masterList){
								masterList = new ArrayList<String>();
							}
							masterList.add(str);
						}
					}
				}*/
				
				// construct chart data
				data = new LinkedHashMap<String, List<String>>();
				LinkedHashMap<String, String> tempMap = new LinkedHashMap<String, String>();
				for(String key : masterList){
					dataList = new ArrayList<String>();
					for (String baseLabel : resultMap.keySet()) {
						tempMap = resultMap.get(baseLabel);
						String temp = tempMap.get(key);
						if(null != temp && !temp.isEmpty()){
							dataList.add(temp);
						}else{
							dataList.add("0");
						}
					}
					data.put(key, dataList);
				}
			}
			chartData = new ChartData();
			chartData.setBaseLabels(baseLabels);
			chartData.setData(data);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				res.close();
			}catch(Exception ex){}
		}
		return chartData;
	}
		
	
}
	
// v5.
//	{
//
//	    title: {
//	        text: 'Solar Employment Growth by Sector, 2010-2016'
//	    },
//
//	    subtitle: {
//	        text: 'Source: thesolarfoundation.com'
//	    },
//
//	    yAxis: {
//	        title: {
//	            text: 'Number of Employees'
//	        }
//	    },
//	    legend: {
//	        layout: 'vertical',
//	        align: 'right',
//	        verticalAlign: 'middle'
//	    },
//
//	    plotOptions: {
//	        series: {
//	            pointStart: 2010
//	        }
//	    },
//
//	    series: [{
//	        name: 'Installation',
//	        data: [43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175]
//	    }, {
//	        name: 'Manufacturing',
//	        data: [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]
//	    }, {
//	        name: 'Sales & Distribution',
//	        data: [11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387]
//	    }, {
//	        name: 'Project Development',
//	        data: [null, null, 7988, 12169, 15112, 22452, 34400, 34227]
//	    }, {
//	        name: 'Other',
//	        data: [12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111]
//	    }]
//
//	}


// v6.
//{
//
//    title: {
//        text: 'Solar Employment Growth by Sector, 2010-2016'
//    },
//
//    subtitle: {
//        text: 'Source: thesolarfoundation.com'
//    },
//
//    yAxis: {
//        title: {
//            text: 'Number of Employees'
//        }
//    },
//    legend: {
//        layout: 'vertical',
//        align: 'right',
//        verticalAlign: 'middle'
//    },
//
//    plotOptions: {
//        series: {
//            label: {
//                connectorAllowed: false
//            },
//            pointStart: 2010
//        }
//    },
//
//    series: [{
//        name: 'Installation',
//        data: [43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175]
//    }, {
//        name: 'Manufacturing',
//        data: [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]
//    }, {
//        name: 'Sales & Distribution',
//        data: [11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387]
//    }, {
//        name: 'Project Development',
//        data: [null, null, 7988, 12169, 15112, 22452, 34400, 34227]
//    }, {
//        name: 'Other',
//        data: [12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111]
//    }],
//
//    responsive: {
//        rules: [{
//            condition: {
//                maxWidth: 500
//            },
//            chartOptions: {
//                legend: {
//                    layout: 'horizontal',
//                    align: 'center',
//                    verticalAlign: 'bottom'
//                }
//            }
//        }]
//    }
//
//}


