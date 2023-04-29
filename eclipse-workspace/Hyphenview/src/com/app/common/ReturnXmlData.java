package com.app.common;

import java.io.InputStream;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.app.dao.QueryDao;
import com.app.object.ChartData;
import com.app.object.ReportTemplate;

public class ReturnXmlData {
	

	public String returnUpdatedData(List<String> xList1, List<String> yList, List<List<String>> dataList, ReportTemplate reportTemplate){
		final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
		String output = null;
		String type = reportTemplate.getChartType();
		String chartData = reportTemplate.getReportTemplateName();
		String reportId = reportTemplate.getReportTemplateId();
		String rectColor = reportTemplate.getRectcolor();
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("properties/config.properties");
			Properties p = new Properties();
			p.load(is);
			String updateUrl = p.getProperty("updateUrl");
			String xmlLicense = p.getProperty("xmlLicense");
			
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.newDocument();
	         
	         // root element
	         Element rootElement = doc.createElement("chart");
	         doc.appendChild(rootElement);
	         
	      // Add license tag
	         Element license = doc.createElement("license");
	         license.appendChild(doc.createTextNode(xmlLicense));
	         rootElement.appendChild(license);
	         
	         // Add the context-menu tag
	         Element contextMenu = doc.createElement("context_menu");
	         Attr fullScreen = doc.createAttribute("full_screen");
	         fullScreen.setValue("true");
	         contextMenu.setAttributeNode(fullScreen);
	         Attr jpeg = doc.createAttribute("save_as_jpeg");
	         jpeg.setValue("true");
	         contextMenu.setAttributeNode(jpeg);
	         Attr png = doc.createAttribute("save_as_png");
	         png.setValue("true");
	         contextMenu.setAttributeNode(png);
	         Attr print = doc.createAttribute("print");
	         print.setValue("true");
	         contextMenu.setAttributeNode(print);
	         rootElement.appendChild(contextMenu);
	         
	         Element axisCategory = doc.createElement("axis_category");
	         axisCategory.setAttribute("orientation", "diagonal_up");
	         rootElement.appendChild(axisCategory);
	         
	         // chart type element
	         Element charttype = doc.createElement("chart_type");

	         LOGGER.debug("chart type passed --> " + type);
	         switch (type){
		         case "column" :
		        	 LOGGER.debug("in switch case column......");
		        	 charttype.appendChild(doc.createTextNode("column"));
		             break;
		         case "bar" :
		        	 charttype.appendChild(doc.createTextNode("bar"));
		             break;
		         case "pie" :
		        	 charttype.appendChild(doc.createTextNode("pie"));
		             break;
		         case "line" :
		        	 charttype.appendChild(doc.createTextNode("line"));
		             break;
		         case "donut" :
		        	 charttype.appendChild(doc.createTextNode("donut"));
		             break;
		         case "area" :
		        	 charttype.appendChild(doc.createTextNode("area"));
		             break;
		         case "stacked column" :
		        	 charttype.appendChild(doc.createTextNode("stacked column"));
		             break;
		         case "stacked bar" :
		        	 charttype.appendChild(doc.createTextNode("stacked bar"));
		             break;
		         default:
		        	 charttype.appendChild(doc.createTextNode("column"));
		             break;
	         }
	         rootElement.appendChild(charttype);

	         // chart data element
	         Element chartdata = doc.createElement("chart_data");
	         rootElement.appendChild(chartdata);
	         
	         // row1 element
	         Element row1 = doc.createElement("row");
	         chartdata.appendChild(row1);
	         
	         // null element
	         Element nullElement = doc.createElement("null");
	         row1.appendChild(nullElement);
	         
	         ChartData cData = getChartData(reportTemplate);
	         
	         //create x axis elements
	         List<String> xList = cData.baseLabels;
	         if( null != xList && xList.size() > 0){
	        	 for(String element : xList){
	        		 Element stringElement = doc.createElement("string");
	        		 stringElement.appendChild(doc.createTextNode(element));
	        		 row1.appendChild(stringElement);
	        	 }
	         }
	         
	         LinkedHashMap<String, List<String>> tempMap = cData.getData();
	         for(String key : tempMap.keySet()){
			  	        	 Element row2 = doc.createElement("row");
    	         chartdata.appendChild(row2);
				         Element yStringElement = doc.createElement("string");
		         yStringElement.appendChild(doc.createTextNode(key));
		        	    row2.appendChild(yStringElement);
		         for(int i = 0; i < tempMap.get(key).size(); i++){
		        	 String value = tempMap.get(key).get(i);
				        			 Element stringElement = doc.createElement("number");
	        		 stringElement.appendChild(doc.createTextNode(value));

	        		 Attr shadow1 = doc.createAttribute("shadow");
	        		 shadow1.setValue("high");
	        		 stringElement.setAttributeNode(shadow1);
					        		 stringElement.setAttribute("bevel", "data");

					        		 Attr tooltip = doc.createAttribute("tooltip");
	        		 tooltip.setValue(xList.get(i) + ":" +value + "\n" + key);
					        		 stringElement.setAttributeNode(tooltip);
					        		 row2.appendChild(stringElement);
					        		 
				        		 }
				        	 }
		        	 
		        	 // color for the bars or columns in comparison charts
		        	 Element seriesColor = doc.createElement("series_color");
		        	 Element color1 = doc.createElement("color");
		        	 color1.appendChild(doc.createTextNode("3366ff"));//b0c9af
		        	 seriesColor.appendChild(color1);
		        	 Element color2 = doc.createElement("color");
		        	 color2.appendChild(doc.createTextNode("ff6600"));
		        	 seriesColor.appendChild(color2);
		        	 Element color3 = doc.createElement("color");
		        	 color3.appendChild(doc.createTextNode("408000"));
		        	 seriesColor.appendChild(color3);
		        	 Element color4 = doc.createElement("color");
		        	 color4.appendChild(doc.createTextNode("800060"));
		        	 seriesColor.appendChild(color4);
		        	 rootElement.appendChild(seriesColor);
		         
		         //hide the legend for single bar or column charts
		        	 if(type.equalsIgnoreCase("bar") || type.equalsIgnoreCase("column") || type.equalsIgnoreCase("Line") || type.equalsIgnoreCase("stacked bar") || type.equalsIgnoreCase("stacked column") ){
		        	 Element legend = doc.createElement("legend");
		        	 legend.setAttribute("layout", "hide");
		        	 rootElement.appendChild(legend);
		         }
		         if(type.equalsIgnoreCase("pie")){
		        	 Element seriesExplode = doc.createElement("series_explode");
		        	 Element number1 = doc.createElement("number");
		        	 number1.appendChild(doc.createTextNode("0"));
		        	 seriesExplode.appendChild(number1);
		        	 Element number2 = doc.createElement("number");
		        	 number2.appendChild(doc.createTextNode("15"));
		        	 seriesExplode.appendChild(number2);
		        	 Element number3 = doc.createElement("number");
		        	 number3.appendChild(doc.createTextNode("0"));
		        	 seriesExplode.appendChild(number3);
		        	 Element number4 = doc.createElement("number");
		        	 number4.appendChild(doc.createTextNode("30"));
		        	 seriesExplode.appendChild(number4);
		        	 Element number5 = doc.createElement("number");
		        	 number5.appendChild(doc.createTextNode("0"));
		        	 seriesExplode.appendChild(number5);
		        	 rootElement.appendChild(seriesExplode);
		         }
	         Element chartLabel = doc.createElement("chart_label");
	         chartLabel.setAttribute("size", "10");
	         if(type.equalsIgnoreCase("column") || type.equalsIgnoreCase("stacked column")){
	        	 chartLabel.setAttribute("position", "middle");
	         }else if(type.equalsIgnoreCase("bar") || type.equalsIgnoreCase("stacked bar") || type.equalsIgnoreCase("area") || type.equalsIgnoreCase("line")){
	        	 chartLabel.setAttribute("position", "center");
	         }else if(type.equalsIgnoreCase("pie")){
	        	 chartLabel.setAttribute("position", "inside");
	         }
	         rootElement.appendChild(chartLabel);
	         
	         //<--Shadow in Bar Chart corner_tl='0' corner_tr='30' corner_br='30' corner_bl='0' positive_color='dba34c' negative_color='ff0000'
	         Element chart_rect=doc.createElement("chart_rect");
	         Attr shadow=doc.createAttribute("shadow");
	         if(type.equalsIgnoreCase("pie")){
	        	 chart_rect.setAttribute("x", "165");
	         }else{
	        	 chart_rect.setAttribute("x", "100");
	         }  
	         chart_rect.setAttribute("y", "40");
	         chart_rect.setAttribute("width", "320");
	         chart_rect.setAttribute("height", "200"); 
	         chart_rect.setAttribute("corner_tl", "0");
	         chart_rect.setAttribute("corner_tr", "30");
	         chart_rect.setAttribute("corner_br", "30");
	         chart_rect.setAttribute("corner_bl", "0");
	         if(null != rectColor && !rectColor.isEmpty()){
	        	 LOGGER.debug("rectColor in create xml file --> " + rectColor);
	        	 chart_rect.setAttribute("positive_color", rectColor);
	         }else{
	        	 chart_rect.setAttribute("positive_color", "dba34c");
	         }
	         chart_rect.setAttribute("negative_color", "ff0000");
	         
	         shadow.setValue("high");
	         chart_rect.setAttributeNode(shadow);
	         
	         /*Attr width=doc.createAttribute("width");
	         width.setValue("140");
	         chart_rect.setAttributeNode(width);
	         Attr height=doc.createAttribute("height");
	         height.setValue("60");
	         chart_rect.setAttributeNode(height);*/

	         rootElement.appendChild(chart_rect);
	         
	         Element filter=doc.createElement("filter");
	         Element FilterShadow=doc.createElement("shadow");
	         Element bevel=doc.createElement("bevel");
	         bevel.setAttribute("id", "data");
	         bevel.setAttribute("angle", "90");
	         bevel.setAttribute("blurX", "10");
	         bevel.setAttribute("blurY", "10");
	         bevel.setAttribute("distance", "5");
	         bevel.setAttribute("type", "full");
	         bevel.setAttribute("highlightAlpha", "10");
	         bevel.setAttribute("shadowAlpha", "20");
	         Attr id=doc.createAttribute("id");
	         id.setValue("high");
	         FilterShadow.setAttribute("inner", "true");
	         FilterShadow.setAttribute("color", "65f442");
	         FilterShadow.setAttribute("strength", "200");
	         FilterShadow.setAttributeNode(id);
	         filter.appendChild(FilterShadow);
	         filter.appendChild(bevel);
	         rootElement.appendChild(filter);
	         
	         //-->shadow in bar chart
	         
	         // To create folder structure with the source xml file
	        /* String directory = System.getenv("SERVER_HOME");
	         LOGGER.debug("directory for xml creation --> " + directory);
	         
	         File xmlDir = new File(directory + folderPath + "/" + folderName);
//	         File xmlDir = new File(folderPath + "/" + folderName);
	         if(!xmlDir.exists()){
	        	 boolean dirCreated = xmlDir.mkdirs();
	        	 LOGGER.debug("Directory eith server id " + folderName + " got created?? --> " + dirCreated);
	         }
	         File newFile = new File(xmlDir.getAbsolutePath() + "/" + fileName + ".xml");
	         
	         boolean isFileCreated = newFile.createNewFile();
	         LOGGER.debug("Is file created successfully?? --> " + isFileCreated);
	         LOGGER.debug("file created in --> " + newFile.getAbsolutePath());*/
	         
	      // update data
	         UUID uuid = UUID.randomUUID();
		     String randomUUIDString = uuid.toString();
	         Element update = doc.createElement("update");
	         update.setAttribute("url", updateUrl + "?reportId="+reportId+"&uniqueID="+ randomUUIDString);
	         String autoUpdtInt = Integer.toString(reportTemplate.getAutoUpdateInterval()*60);
	         update.setAttribute("delay",autoUpdtInt);
	       //  update.setAttribute("mode", "data");
	         rootElement.appendChild(update);
	         
	         // write the content into xml file
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	        /* StreamResult result = new StreamResult(newFile);
	         transformer.transform(source, result);*/
	         
	         // Output to console for testing
	         StreamResult consoleResult = new StreamResult(System.out);
	         transformer.transform(source, consoleResult);
	         LOGGER.debug("<<<<<<xml data created>>>>");
	         
	         StringWriter writer = new StringWriter();
	         transformer.transform(new DOMSource(doc), new StreamResult(writer));
	         output = writer.getBuffer().toString();
	         LOGGER.debug("xml as string --> " + output);   
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		return output;
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
					baseLabels.add(res.getString(1));
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
