package com.app.common;


import java.sql.ResultSet;

import java.util.ArrayList;

import com.app.dao.QueryDao;
import com.app.object.ReportTemplate;
import com.hyjavacharts.chart.Highchart;
import com.hyjavacharts.model.common.Color;
import com.hyjavacharts.model.common.themes.HighchartTheme;
import com.hyjavacharts.model.highcharts.ChartOptions;
import com.hyjavacharts.model.highcharts.constants.ChartType;
import com.hyjavacharts.model.highcharts.constants.Cursor;
import com.hyjavacharts.model.highcharts.series.SeriesPie;
import com.hyjavacharts.model.highcharts.series.seriespie.Data;

public class BuildPieChart2 {

	public Highchart configure(ReportTemplate reportTemplate) {
		Highchart highChart = new Highchart();
		ChartOptions chartOptions = highChart.getChartOptions();

		chartOptions.getChart().setType(ChartType.PIE).setPlotShadow(false).setPlotBackgroundColor(null)
				.setPlotBorderWidth(null);
		chartOptions.getTitle().setText(reportTemplate.getReportTemplateName());
		
		chartOptions.getTooltip().setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");

		chartOptions.getPlotOptions().getPie().setAllowPointSelect(true).setCursor(Cursor.POINTER).getDataLabels()
				.setEnabled(true).setFormat("<b>{point.name}</b>: {point.percentage:.1f} %").getStyle()
				.setColor(new Color()
						.setColorValue("(Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'"));
	

		SeriesPie seriesPie = new SeriesPie();
		// seriesPie.setName("Incidents").setColorByPoint(true);
		seriesPie.setDataAsArrayObject(getSeriesData(reportTemplate));
		chartOptions.getSeries().add(seriesPie);
		String theme =  reportTemplate.getTheme();
		System.out.println("Theme" + theme );
		
		highChart.setTheme(HighchartTheme.SKIES);
		
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

	/**
	 * 
	 * @return
	 */
	private ArrayList<Data> getSeriesData(ReportTemplate reportTemplate) {
		ArrayList<Data> array = new ArrayList<Data>();

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

				if (count == 1) {
					array.add(new Data().setName(res.getString(1)).setY(new Integer(res.getString(2))).setSliced(true)
							.setSelected(true));
					count++;
				} else {
					array.add(new Data().setName(res.getString(1)).setY(new Integer(res.getString(2))));
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}

		/*
		 * array.add(new
		 * Data().setName("Chrome").setY(61.41).setSliced(true).setSelected(true));
		 * array.add(new Data().setName("Internet Explorer").setY(11.84)); array.add(new
		 * Data().setName("Firefox").setY(10.85)); array.add(new
		 * Data().setName("Edge").setY(4.67)); array.add(new
		 * Data().setName("Safari").setY(4.18)); array.add(new
		 * Data().setName("Sogou Explorer").setY(1.64)); array.add(new
		 * Data().setName("Opera").setY(1.6)); array.add(new
		 * Data().setName("QQ").setY(1.2)); array.add(new
		 * Data().setName("Other").setY(2.61));
		 */

		return array;
	}
}

//{
//    chart: {
//        plotBackgroundColor: null,
//        plotBorderWidth: null,
//        plotShadow: false,
//        type: 'pie'
//    },
//    title: {
//        text: 'Browser market shares in January, 2018'
//    },
//    tooltip: {
//        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
//    },
//    plotOptions: {
//        pie: {
//            allowPointSelect: true,
//            cursor: 'pointer',
//            dataLabels: {
//                enabled: true,
//                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
//                style: {
//                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
//                }
//            }
//        }
//    },
//    series: [{
//        name: 'Brands',
//        colorByPoint: true,
//        data: [{
//            name: 'Chrome',
//            y: 61.41,
//            sliced: true,
//            selected: true
//        }, {
//            name: 'Internet Explorer',
//            y: 11.84
//        }, {
//            name: 'Firefox',
//            y: 10.85
//        }, {
//            name: 'Edge',
//            y: 4.67
//        }, {
//            name: 'Safari',
//            y: 4.18
//        }, {
//            name: 'Sogou Explorer',
//            y: 1.64
//        }, {
//            name: 'Opera',
//            y: 1.6
//        }, {
//            name: 'QQ',
//            y: 1.2
//        }, {
//            name: 'Other',
//            y: 2.61
//        }]
//    }]
//}
