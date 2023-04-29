package com.app.common;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Element;

import com.app.dao.QueryDao;
import com.app.object.ChartData;
import com.app.object.ReportTemplate;
import com.google.gson.Gson;
//import com.hyjavacharts.model.highcharts.annotations.Labels;

public class BuildStackedBarChart2 {

	public String configure(ReportTemplate reportTemplate) {
		
		String chartOptionsJs ="";
		
		
		 ChartData cData = getChartData(reportTemplate);	
			
		 List<String> xList = cData.baseLabels;
		 LinkedHashMap<String, List<String>> tempMap = cData.getData();
		 
		String labels ="[";
		int i = xList.size();
		
		 if( null != xList && xList.size() > 0){
        	 for(String element : xList){
        		 
        		 if (element==null)
        		 {
        			 element ="";	 
        		 }
        		 
        		 i--;
        		 System.out.println("i = " + i);
        		 labels = labels.concat("'").concat(element).concat("'");
        		 if(i!=0)
        		 labels = labels.concat(",");
        	 }
         }
		 
		 labels = labels.concat("]");
		// String jsonData = gson.toJson(tempMap);
		 JSONArray arr = new JSONArray();		
		 
		 for(String key : tempMap.keySet()){
			 JSONObject json=new JSONObject();	
             json.put("name",key);
             json.put("value",tempMap.get(key).stream().map(Integer::parseInt).collect(Collectors.toList()));
             
             arr.put(json);

		 }
		
		
		
	 chartOptionsJs ="{\r\n" + 
	 		"    chart: {\r\n" + 
	 		"        type: 'bar'\r\n" + 
	 		"    },\r\n" + 
	 		"    title: {\r\n" + 
	 		"        text:' "+reportTemplate.getReportTemplateName() +   " '\r\n" + 
	 		"    },\r\n" + 
	 		"    xAxis: {\r\n" + 
	 		"        categories: "+labels +",\r\n" + 
	 		"    },\r\n" + 
	 		"    yAxis: {\r\n" + 
	 		"        min: 0,\r\n" + 
	 		"        title: {\r\n" + 
	 		"            text: 'Total Incidents'\r\n" + 
	 		"        }\r\n" + 
	 		"    },\r\n" + 
	 		"    legend: {\r\n" + 
	 		"        reversed: true\r\n" + 
	 		"    },\r\n" + 
	 		"    plotOptions: {\r\n" + 
	 		"        series: {\r\n" + 
	 		"            stacking: 'normal'\r\n" + 
	 		"        }\r\n" + 
	 		"    },\r\n" + 
	 		"    series:"+arr+
			" }";
		 

	 
	 
	 
        		 
        		 
        		
        		

		
		
		return chartOptionsJs;
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


