package com.app.common;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.Styler.ChartTheme;

import com.app.dao.QueryDao;
import com.app.object.ChartData;
import com.app.object.ReportTemplate;

/**
 * GGPlot2 Theme Bar chart
 *
 * <p>Demonstrates the following:
 *
 * <ul>
 *   <li>String categories
 *   <li>Positive and negative values
 *   <li>Multiple series
 */
public class BuildXChartBarChart implements ExampleChart<CategoryChart> {

  public static void main(String[] args) {

    ExampleChart<CategoryChart> exampleChart = new BuildXChartBarChart();
    CategoryChart chart = exampleChart.getChart();
    new SwingWrapper<CategoryChart>(chart).displayChart();
  }

  @Override
  public CategoryChart getChart() {

    // Create Chart
    CategoryChart chart =
        new CategoryChartBuilder()
            .width(800)
            .height(600)
            .title("Temperature vs. Color")
            .xAxisTitle("Color")
            .yAxisTitle("Temperature")
            .theme(ChartTheme.GGPlot2)
            .build();

    // Customize Chart
    chart.getStyler().setPlotGridVerticalLinesVisible(false);

    // Series
    chart.addSeries(
        "fish",
        new ArrayList<String>(
            Arrays.asList(new String[] {"Blue", "Red", "Green", "Yellow", "Orange"})),
        new ArrayList<Number>(Arrays.asList(new Number[] {-40, 30, 20, 60, 60})));
    chart.addSeries(
        "worms",
        new ArrayList<String>(
            Arrays.asList(new String[] {"Blue", "Red", "Green", "Yellow", "Orange"})),
        new ArrayList<Number>(Arrays.asList(new Number[] {50, 10, -20, 40, 60})));
    chart.addSeries(
        "birds",
        new ArrayList<String>(
            Arrays.asList(new String[] {"Blue", "Red", "Green", "Yellow", "Orange"})),
        new ArrayList<Number>(Arrays.asList(new Number[] {13, 22, -23, -34, 37})));
    chart.addSeries(
        "ants",
        new ArrayList<String>(
            Arrays.asList(new String[] {"Blue", "Red", "Green", "Yellow", "Orange"})),
        new ArrayList<Number>(Arrays.asList(new Number[] {50, 57, -14, -20, 31})));
    chart.addSeries(
        "slugs",
        new ArrayList<String>(
            Arrays.asList(new String[] {"Blue", "Red", "Green", "Yellow", "Orange"})),
        new ArrayList<Number>(Arrays.asList(new Number[] {-2, 29, 49, -16, -43})));

    return chart;
  }
  
  
  

  @Override
  public String getExampleChartName() {

    return getClass().getSimpleName() + " - GGPlot2 Theme";
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
