package com.app.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.app.dao.QueryDao;
import com.app.object.ChartData;
import com.app.object.ReportTemplate;
//import com.hyjavacharts.model.highcharts.annotations.Labels;

public class BuildPieChart {

	public String configure(ReportTemplate reportTemplate,String chartId ) throws Exception {
			
		String chartOptionsJs ="";
		
		String columns = getChartData(reportTemplate);
		System.out.print("Piecolumn" +columns );
		
		if (columns.equalsIgnoreCase("[]"))
		{
			 chartOptionsJs = "var "+ chartId +" = bb.generate({\r\n" + 
					 "  title: {\r\n" + 
					 "    text: \""+reportTemplate.getReportTemplateName() +"\"\r\n" + 
					 "  },"+
					 "size: {\r\n" + 
					 "    height: 420,\r\n" + 
					 
					 "  },"+
					 "  data: {\r\n empty: {\r\n" + 
				 		"    label: {\r\n " + 
				 		
				 		"      text: \"No Data\"\r\n" + 
				 		"    }\r\n" + 
				 		"  } ," + 
					
			 		"    columns: "+ "[]" +",\r\n" + 
			 		

			
			 		"onclick: function (d, i) { drilldowndata" +chartId +"(d.name,d.name); }"+
			 		"  },\r\n" + 		 		
			 		 		
			 		"  bindto: \"#"+chartId+"\"\r\n" + 
			 		"});\r\n" + "setTimeout(function() {\n" + "var mydiv = document.getElementById(\""
					+ chartId + "\");" + "" + chartId + ".export(\"image/png\", dataUrl => {\r\n"
					+ "var image = document.createElement(\"img\");\r\n" + "    const link = document.createElement(\"a\");\r\n"
					+ "\r\n" + "    link.download = '" + reportTemplate.getReportTemplateName() + ".png';\r\n"
					+ "    link.href = dataUrl;\r\n" + "    image.class = \"download-img\";\r\n" 
					+"    image.src = \"assets/images/downloadfile.png\";\r\n" 
					+"    image.title = \"Download Chart\";\r\n" 
					+"    image.align = \"right\";\r\n" 
					//+ "    link.innerHTML = \"Download chart as image\";\r\n" 
					+ "link.appendChild(image);\r\n" + "\r\n"
					+ "    mydiv.appendChild(link);\r\n" + "	});\r\n" + "}, 100);" +
			 		"setInterval(function() {\n" +
			 		chartId +".flush(true);\r\n"+

			 		"}, 60000);\r\n" 
			 		;			
		}
		
		else
		{
	
		 chartOptionsJs = "var "+ chartId +" = bb.generate({\r\n" + 
				 "  title: {\r\n" + 
				 "    text: \""+reportTemplate.getReportTemplateName() +"\"\r\n" + 
				 "  },"+
				 "size: {\r\n" + 
				 "    height: 420,\r\n" + 
				 
				 "  },"+
				 "  data: {\r\n empty: {\r\n" + 
			 		"    label: {\r\n show: true," + 
			 		
			 		"      text: \"No Data\"\r\n" + 
			 		"    }\r\n" + 
			 		"  } ," + 
				
		 		"    columns: "+ columns +",\r\n" + 
		 		"    type: \"pie\" ,\r\n" + 

		
		 		"onclick: function (d, i) { drilldowndata" +chartId +"(d.name,d.name); }"+
		 		"  },\r\n" + 		 		
		 		 		
		 		"  bindto: \"#"+chartId+"\"\r\n" + 
		 		"});\r\n" + "setTimeout(function() {\n" + "var mydiv = document.getElementById(\""
				+ chartId + "\");" + "" + chartId + ".export(\"image/png\", dataUrl => {\r\n"
				+ "var image = document.createElement(\"img\");\r\n" + "    const link = document.createElement(\"a\");\r\n"
				+ "\r\n" + "    link.download = '" + reportTemplate.getReportTemplateName() + ".png';\r\n"
				+ "    link.href = dataUrl;\r\n" + "    image.class = \"download-img\";\r\n" 
				+"    image.src = \"assets/images/downloadfile.png\";\r\n" 
				+"    image.title = \"Download Chart\";\r\n" 
				+"    image.align = \"right\";\r\n" 
				//+ "    link.innerHTML = \"Download chart as image\";\r\n" 
				+ "link.appendChild(image);\r\n" + "\r\n"
				+ "    mydiv.appendChild(link);\r\n" + "	});\r\n" + "}, 100);" +
		 		"setInterval(function() {\n" +
		 		chartId +".flush(true);\r\n"+

		 		"}, 60000);\r\n" 
		 		;
		}

		return chartOptionsJs;
	}


	
	public String getChartData(ReportTemplate reportTemplate){
		ChartData chartData = null;
		QueryDao queryDao = new QueryDao();
		ResultSet res = null;
		List<String> baseLabels = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		String columnForPie = "";
		LinkedHashMap<String, List<String>> data = null;
		
		//temporary maps for rearranging data
		Map<String, Object> outputMap = new HashMap<String, Object>();;
		
		Connection conn = null;
		
		if(null != reportTemplate.getDatabase()){

		outputMap = queryDao.executeQuery(reportTemplate.getEmail(), reportTemplate.getDefinedQuery(), reportTemplate.getDatabase(), reportTemplate.getSchema());
		}
		
		else
		{
			outputMap = queryDao.executeReportQuery(reportTemplate ,reportTemplate.getEmail() );
		}
			Iterator<Map.Entry<String, Object>> entries = outputMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Object> entry = entries.next();
			if(entry.getKey().equalsIgnoreCase("connection")){
				conn = (Connection)entry.getValue();
			}else if(entry.getKey().equalsIgnoreCase("resultset")){
				res = (ResultSet)entry.getValue();
			}
		}
		try
		{
			columnForPie="[" ;
			if(res.getMetaData().getColumnCount() == 2){
				
				baseLabels = new ArrayList<String>();
				dataList = new ArrayList<String>();
				data = new LinkedHashMap<String, List<String>>();
				int index=0;
				while(res.next())
				{
					index++;
					if(!baseLabels.contains(res.getString(1)))
					{
					baseLabels.add(res.getString(1));
					if(index==1)
						columnForPie = columnForPie.concat("[\"").concat(res.getString(1)).concat("\",");
					else
						columnForPie=columnForPie.concat(" ,[\"").concat(res.getString(1)).concat("\",");	
					}
					dataList.add(res.getString(2));
					columnForPie=columnForPie.concat(res.getString(2)).concat("]\r\n");
				}
				columnForPie = columnForPie.concat("]");
				data.put(reportTemplate.getReportTemplateName(), dataList);
			}else if(res.getMetaData().getColumnCount() == 3){
				
				baseLabels = new ArrayList<String>();
				dataList = new ArrayList<String>();
				data = new LinkedHashMap<String, List<String>>();
				int index=0;
				while(res.next())
				{
					index++;
					if(!baseLabels.contains(res.getString(1)))
					{
					baseLabels.add(res.getString(1));
					if(index==1)
						columnForPie=columnForPie.concat("[\"").concat(res.getString(1)).concat("\",");
					else
						columnForPie=columnForPie.concat(" ,[\"").concat(res.getString(1)).concat("\",");	
					}
					dataList.add(res.getString(3));
					columnForPie=columnForPie.concat(res.getString(3)).concat("]\r\n");
				}
				data.put(reportTemplate.getReportTemplateName(), dataList);
				columnForPie = columnForPie.concat("]");
			}
			else
			{
				columnForPie ="";
			}

			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				res.close();
				conn.close();
			}catch(Exception ex){
				
				ex.printStackTrace();	
			}
		}
		//System.out.println("columnForPie"+ columnForPie);
		return columnForPie;
	}
		
	
}
	


