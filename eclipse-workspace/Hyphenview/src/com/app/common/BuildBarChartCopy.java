package com.app.common;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.app.dao.QueryDao;
import com.app.object.ChartData;
import com.app.object.ReportTemplate;

//import com.hyjavacharts.model.highcharts.annotations.Labels;

public class BuildBarChartCopy {

	public String configure(ReportTemplate reportTemplate , String chartScriptId) {
		
		String chartOptionsJs ="";
		
		String Chart ="";
		
		
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
		
		try
		{
		
	 chartOptionsJs ="var margin = {top: 10, right: 30, bottom: 90, left: 40},\r\n" + 
	 		"    width = 370 - margin.left - margin.right,\r\n" + 
	 		"    height = 400 - margin.top - margin.bottom;\r\n" + 
	 		"\r\n" +
	 		
	 		"// append the svg object to the body of the page\r\n" + 
	 		"var svg = d3.select(\"#"+chartScriptId+"\")\r\n" + 
	 		"  .append(\"svg\")\r\n" + 
	 		"    .attr(\"width\", width + margin.left + margin.right)\r\n" + 
	 		"    .attr(\"height\", height + margin.top + margin.bottom)\r\n" + 
	 		"  .append(\"g\")\r\n" + 
	 		"    .attr(\"transform\",\r\n" + 
	 		"          \"translate(\" + margin.left + \",\" + margin.top + \")\");\r\n" + 
	 		"\r\n" + 
	 		"// Parse the Data\r\n" + 
	 		"    var data = " + getChartDataForDS(reportTemplate) + ";" +
	 		"var maxData = d3.max(data, function(d){ return d.value; });" +
	 		
	 		"var tip = d3.tip()\r\n" + 
	 		"  .attr('class', 'd3-tip')\r\n" + 
	 		"  .offset([-10, 0])\r\n" + 
	 		"  .html(function(data) {\r\n" + 
	 		"    return \"<strong>Incident:</strong> <span style='color:red'>\" + data.value + \"</span>\";\r\n" + 
	 		"  }); \r\n"
	 		+ "svg.call(tip);\r\n"+
	 		"// X axis\r\n" + 
	 		"var x = d3.scaleBand()\r\n" + 
	 		"  .rangeRound([ 0, width ])\r\n" + 
	 		"  .domain(data.map(function(d) { return d.name; }))\r\n" + 
	 		"  .padding(0.2);\r\n" + 
	 		"svg.append(\"g\")\r\n" + 
	 		"  .attr(\"transform\", \"translate(0,\" + height + \")\")\r\n" + 
	 		"  .call(d3.axisBottom(x))\r\n" + 
	 		"  .selectAll(\"text\")\r\n" + 
	 		"    .attr(\"transform\", \"translate(-10,0)rotate(-45)\")\r\n" + 
	 		"    .style(\"text-anchor\", \"end\");\r\n" + 
	 		"\r\n" + 
	 		"// Add Y axis\r\n" + 
	 		"var y = d3.scaleLinear()\r\n" + 
	 		"  .domain([0, maxData])\r\n" + 
	 		"  .rangeRound([ height, 0]);\r\n" + 
	 		"svg.append(\"g\")\r\n" + 
	 		"  .call(d3.axisLeft(y));\r\n" + 
	 		"\r\n" + 
	 		"// Bars\r\n" + 
	 		"svg.selectAll(\"mybar\")\r\n" + 
	 		"  .data(data)\r\n" + 
	 		"  .enter()\r\n" + 
	 		"  .append(\"rect\")\r\n" + 
	 		"    .attr(\"x\", function(d) { return x(d.name); })\r\n" + 
	 		"    .attr(\"width\", x.bandwidth())\r\n" + 
	 		"    .attr(\"fill\", \"#48b5E0\")\r\n" + 
	 		"    // no bar at the beginning thus:\r\n" + 
	 		"    .attr(\"height\", function(d) { return height - y(0); }) // always equal to 0\r\n" + 
	 		"    .attr(\"y\", function(d) { return y(0); })\r\n" + 
	 		"\r\n" + 
	 		"// Animation\r\n" + 
	 		"svg.selectAll(\"rect\")\r\n" + 
	 		"  .transition()\r\n" + 
	 		"  .duration(10)\r\n" + 
	 		"  .attr(\"y\", function(data) { return y(data.value); })\r\n" + 
	 		"  .attr(\"height\", function(data) { return height - y(data.value); })\r\n" + 
	 		"  .delay(function(data,i){console.log(i) ; return(i*10)}).on('mouseover', tip.show)\r\n" + 
	 		"      .on('mouseout', tip.hide);\r\n" ;
	 		
	 		

	 
	 
	 
        		 
        		 
        		
        		

		
		
		
		}
		catch(Exception e)
		{
			
		}
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
		
	

public JSONArray getChartDataForDS(ReportTemplate reportTemplate){
	ChartData chartData = null;
	QueryDao queryDao = new QueryDao();
	ResultSet res = null;
	List<String> baseLabels = null;
	List<String> dataList = null;
	LinkedHashMap<String, List<String>> data = null;
	
	//temporary maps for rearranging data
	LinkedHashMap<String, LinkedHashMap<String, String>> resultMap = null;
	LinkedHashMap<String, String> internalMap = null;
	JSONArray arr = new JSONArray();		
	 
	


	 
	
	try{
		if(null != reportTemplate.getDatabase()){
			res = queryDao.executeQueryRes(reportTemplate.getEmail(), reportTemplate.getDefinedQuery(), reportTemplate.getDatabase(), reportTemplate.getSchema());
		}else{
			res = queryDao.executeReportQuery(reportTemplate);
		}
		if(res.getMetaData().getColumnCount() == 2){
			JSONObject json=new JSONObject();	
	       
	       String value ="";
	        
			
			while(res.next())
			{
				json.put("name",res.getString(1));	
				
				value = res.getString(2);
				if(res.getString(2).equalsIgnoreCase(null))
				{
				value ="0";
				}
				
				json.put("value",value);
				arr.put(json);
			}
			
		}else if(res.getMetaData().getColumnCount() == 3){
			JSONObject json=new JSONObject();	
	       
	       
	        
			
			while(res.next())
			{
				
				json.put("name",res.getString(1));			
				json.put("value",res.getString(2));
				arr.put(json);
			}
			
		}
		
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{
			res.close();
		}catch(Exception ex){}
	}
	System.out.println("Array" + arr);
	return arr;
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


