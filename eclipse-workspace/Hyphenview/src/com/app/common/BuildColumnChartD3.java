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

public class BuildColumnChartD3 {

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
		
	 chartOptionsJs = " var options = {"	+
	 		"         data: "+ getChartDataForDS(reportTemplate) +", // data for chart rendering\r\n" + 
	 		"         params: { // columns from data array for rendering graph\r\n" + 
	 		//"            group_name: "+  +", // title for group name to be shown in legend\r\n" + 
	 		"            name: 'name', // name for xaxis\r\n" + 
	 		"            value: 'value' // value for yaxis\r\n" + 
	 		"         },\r\n" + 
	 		"         horizontal_bars: false, // default chart orientation\r\n" + 
	 		"         chart_height: 400, // default chart height in px\r\n" + 
	 		"         colors: null, // colors for chart\r\n" + 
	 		"         show_legend: true, // show chart legend\r\n" + 
	 		"         legend: { // default legend settings\r\n" + 
	 		"            position: LegendPosition.bottom, // legend position (bottom/top/right/left)\r\n" + 
	 		"            width: 200 // legend width in pixels for left/right\r\n" + 
	 		"         },\r\n" + 
	 		"         x_grid_lines: false, // show x grid lines\r\n" + 
	 		"         y_grid_lines: false, // show y grid lines\r\n" + 
	 		"         tweenDuration: 300, // speed for tranistions\r\n" + 
	 		"         bars: { // default bar settings\r\n" + 
	 		"            padding: 0.075, // padding between bars\r\n" + 
	 		"            opacity: 0.7, // default bar opacity\r\n" + 
	 		"            opacity_hover: 0.45, // default bar opacity on mouse hover\r\n" + 
	 		"            disable_hover: false, // disable animation and legend on hover\r\n" + 
	 		"            hover_name_text: 'name', // text for name column for label displayed on bar hover\r\n" + 
	 		"            hover_value_text: 'value', // text for value column for label displayed on bar hover\r\n" + 
	 		"         },\r\n" + 
	 		"         number_format: { // default locale for number format\r\n" + 
	 		"            format: '', // default number format\r\n" + 
	 		"            decimal: '.', // decimal symbol\r\n" + 
	 		"            thousands : ',', // thousand separator symbol\r\n" + 
	 		"            grouping: [3], // thousand separator grouping\r\n" + 
	 		"            currency: ['$'] // currency symbol\r\n" + 
	 		"         },\r\n" + 
	 		"         margin: { // margins for chart rendering\r\n" + 
	 		"            top: 0, // top margin\r\n" + 
	 		"            right: 35, // right margin\r\n" + 
	 		"            bottom: 20, // bottom margin\r\n" + 
	 		"            left: 70 // left margin\r\n" + 
	 		"         },\r\n" + 
	 		"         rotate_x_axis_labels: { // rotate xaxis label params\r\n" + 
	 		"            process: true, // process xaxis label rotation\r\n" + 
	 		"            minimun_resolution: 720, // minimun_resolution for label rotating\r\n" + 
	 		"            bottom_margin: 15, // bottom margin for label rotation\r\n" + 
	 		"            rotating_angle: 90, // angle for rotation,\r\n" + 
	 		"            x_position: 9, // label x position after rotation\r\n" + 
	 		"            y_position: -3 // label y position after rotation\r\n" + 
	 		"         }\r\n" + 
	 		"      };\r\n $('#" + chartScriptId +"').animatedBarChart(options);\r\n\r\n" ;
	 		
	 		
	 		

	 
	 
	 
        		 
        		 
        		
        		

		
		
		
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
				json.put("group_name","Incidents");
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
				json.put("group_name",res.getString(1));	
				json.put("name",res.getString(2));			
				json.put("value",res.getString(3));
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


