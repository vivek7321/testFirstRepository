package com.app.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.object.ChartData;
import com.app.object.ReportTemplate;

public class ChartDataDAO {
	
	static final Logger LOGGER = LoggerFactory.getLogger(EraviewDBconnection.class);
	
	
	public ChartData getChartData(ReportTemplate reportTemplate){
		ChartData chartData = null;
		QueryDao queryDao = new QueryDao();
		ResultSet res = null;
		List<String> baseLabels = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		LinkedHashMap<String, List<String>> data = null;
		
		//temporary maps for rearranging data
		LinkedHashMap<String, LinkedHashMap<String, String>> resultMap = new LinkedHashMap<String, LinkedHashMap<String,String>>();
		LinkedHashMap<String, String> internalMap = new LinkedHashMap<String, String>();
		
		Map<String, Object> outputMap = null;
		
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
		
		try{

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
				conn.close();
			}catch(Exception ex){}
		}
		return chartData;
	}
		


}
