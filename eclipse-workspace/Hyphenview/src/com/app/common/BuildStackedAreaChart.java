package com.app.common;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;

import com.app.dao.ChartDataDAO;
import com.app.object.ChartData;
import com.app.object.ReportTemplate;


public class BuildStackedAreaChart {

	public String configure(ReportTemplate reportTemplate,String chartId) throws Exception {
			
		String chartOptionsJs ="";
		
		ChartDataDAO chartDataDAO = new ChartDataDAO();

		ChartData cData = chartDataDAO.getChartData(reportTemplate);	
			
		 List<String> xList = cData.baseLabels;
		 LinkedHashMap<String, List<String>> tempMap = cData.getData();
		 
		 String labels ="[\"x\",";
		int i = xList.size();
		
		 if( null != xList && xList.size() > 0){
        	 for(String element : xList){
        		 
        		 if (element==null)
        		 {
        			 element ="";	 
        		 }
        		 
        		 i--;
        		// System.out.println("i = " + i);
        		 labels = labels.concat("\"").concat(element).concat("\"");
        		 if(i!=0)
        		 labels = labels.concat(",");
        	 }
         }
		 
		 labels = labels.concat("],");
		// String jsonData = gson.toJson(tempMap);
		 JSONArray arr = new JSONArray();		
		 String columns="[\n" ;
		 columns = columns.concat(labels);
		 int k=0;
		 String groups ="[\n[";
		 
		 String types ="";
		 for(String key : tempMap.keySet()){
			 k++;
			 if(k==1) 
			 columns = columns.concat("[");
			 else
			 columns = columns.concat(",[");	 
				 
			 columns = columns.concat("\"").concat(key).concat("\",");
			
			 
			 if(k==1) 
			 {
			 types= types.concat("\"").concat(key).concat("\"").concat(":");
			 groups= groups.concat("\"").concat(key).concat("\"");
			 }
			 else
			 {
			 types= types.concat(",\n\"").concat(key).concat("\":");	 
			 groups= groups.concat(",\n\"").concat(key).concat("\"");
			 }
			 
    		 if(k%2==0)
    		 {    			 
    			 types= types.concat("\"area-spline\"").concat("\r\n"); 
    		 }
    		 else
    		 {
    			 types= types.concat("\"area\"").concat("\r\n");  
    		 }	        	
		
             int j = tempMap.get(key).stream().map(Integer::parseInt).collect(Collectors.toList()).size();
 			
			 if( null != tempMap.get(key).stream().map(Integer::parseInt).collect(Collectors.toList()) && tempMap.get(key).stream().map(Integer::parseInt).collect(Collectors.toList()).size() > 0){
	        	 for(Object element : tempMap.get(key).stream().map(Integer::parseInt).collect(Collectors.toList())){
	 
	        		 if (element==null)
	        		 {
	        			 element ="";	 
	        		 }
	        		 
	        		 j--;
	        		// System.out.println("j = " + j);
	        		 columns = columns.concat(element.toString());
	        		 if(j!=0)
	        		 {	 
	        		 columns = columns.concat(",");
	        		 
	        		 }	
	        	 }
	         }
         
			 columns = columns.concat("]");
		 }
		 columns = columns.concat("\n]");	
		 groups = groups.concat("]\r\n]\n");
		// System.out.println("types" + types);	

		 chartOptionsJs = "var "+ chartId +" = bb.generate({\r\n" + 
				 "  title: {\r\n" + 
				 "    text: \""+reportTemplate.getReportTemplateName() +"\"\r\n" + 
				 "  },"+
				 "size: {\r\n" + 
				 "    height: 420,\r\n" + 
				 
				 "  },"+
				 "  data: {\r\n empty: {\r\n" + 
			 		"    label: {\r\n" + 
			 		"      text: \"No Data\"\r\n" + 
			 		"    }\r\n" + 
			 		"  } ," +  
		 		 "x:\"x\" ," + 
		 		"    columns: "+ columns +",\r\n" + 
		 		"    types: {\r\n" + types + "\r\n" + 
		 		
		 		"    },\r\n" + 
		 		"groups:" + groups + ",\r\n" +
		 		"area: {\r\n" + 
		 		"    linearGradient: true\r\n" + 
		 		"  },"+
		 		"onclick: function (d, i) { drilldowndata" +chartId +"(d.name,this.categories()[d.index]); }"+
		 		"  },\r\n" + 
		 		" axis: {\r\n" + 
		 		"    x: {\r\n" + 
		 		"      type: \"category\",\r\n" + 
		 		"      tick: {\r\n" + 
		 		"        fit: false,\r\n" + 
		 		"        multiline: false,\r\n" + 
		 		"        autorotate: true,\r\n" + 
		 		"        rotate: 15,\r\n" + 
		 		"        culling: {max : 6}\r\n" + 
		 		"      }\r\n" + 
		 		"    },\r\n" + 
				"	y: {\r\n" + 
		 		"	   show: true,\r\n"+
		 		"    tick: {\r\n" + 
		 		"      culling: {max : 5}\r\n" + 
		 		"    }\r\n" + 
		 		"  }"
				+ "  }," + "  bindto: \"#" + chartId
				+ "\"\r\n" + "});\r\n" + "setTimeout(function() {\n" + "var mydiv = document.getElementById(\""
				+ chartId + "\");" + "" + chartId + ".export(\"image/png\", dataUrl => {\r\n"
				+ "var image = document.createElement(\"img\");\r\n" + "    const link = document.createElement(\"a\");\r\n"
				+ "\r\n" + "    link.download = '" + reportTemplate.getReportTemplateName() + ".png';\r\n"
				+ "    link.href = dataUrl;\r\n" + "    image.class = \"download-img\";\r\n" 
				+"    image.src = \"assets/images/downloadfile.png\";\r\n" 
				+"    image.title = \"Download Chart\";\r\n" 
				+"    image.align = \"right\";\r\n" 
				//+ "    link.innerHTML = \"Download chart as image\";\r\n" 
				+ "link.appendChild(image);\r\n" + "\r\n"
				+ "    mydiv.appendChild(link);\r\n" + "	});\r\n" + "}, 500);" +
		 		"setInterval(function() {\n" +
		 		chartId +".flush(true);\r\n"+

		 		"}, 60000);\r\n" 
		 		;

		
		return chartOptionsJs;
	}

}
	
