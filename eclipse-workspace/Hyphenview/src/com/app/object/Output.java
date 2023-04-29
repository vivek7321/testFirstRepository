package com.app.object;

import java.util.List;

public class Output {
	
	public String message;
	public String messageCode;
	public List<String> headerList;
	public List<List<String>> data;
	public List<String> chartTypeData;
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	public List<String> getChartTypeData() {
		return chartTypeData;
	}
	public void setChartTypeData(List<String> chartTypeData) {
		this.chartTypeData = chartTypeData;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the headerList
	 */
	public List<String> getHeaderList() {
		return headerList;
	}
	/**
	 * @param headerList the headerList to set
	 */
	public void setHeaderList(List<String> headerList) {
		this.headerList = headerList;
	}
	/**
	 * @return the data
	 */
	public List<List<String>> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(List<List<String>> data) {
		this.data = data;
	}
	/**
	 * @return the messageCode
	 */
	public String getMessageCode() {
		return messageCode;
	}
	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	
}
