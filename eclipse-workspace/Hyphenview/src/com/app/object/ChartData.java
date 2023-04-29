/**
 * 
 */
package com.app.object;

import java.util.LinkedHashMap;
import java.util.List;


public class ChartData {

	public List<String> baseLabels;
	public LinkedHashMap<String, List<String>> data;
	/**
	 * @return the baseLabels
	 */
	public List<String> getBaseLabels() {
		return baseLabels;
	}
	/**
	 * @param baseLabels the baseLabels to set
	 */
	public void setBaseLabels(List<String> baseLabels) {
		this.baseLabels = baseLabels;
	}
	/**
	 * @return the data
	 */
	public LinkedHashMap<String, List<String>> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(LinkedHashMap<String, List<String>> data) {
		this.data = data;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChartData [baseLabels=" + baseLabels + ", data=" + data + "]";
	}
	
	
}
