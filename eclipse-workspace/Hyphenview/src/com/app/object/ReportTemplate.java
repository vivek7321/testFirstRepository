package com.app.object;

public class ReportTemplate {
	
	public String reportTemplateId;
	public String reportTemplateName;
	public String reportType;
	public String templateType;
	public String chartType;
	public String definedQuery;
	public String customerId;
	public String databaseId;
	public String email;
	public String database;
	public String schema;
	public String drilldown;
	public int displayOrder;
	public int autoUpdateInterval;
	public String bgcolor;
	public String rectcolor;
	public String startDate;
	public String endDate;
	public String uploadLogoStatus;
	public String timeperiod;
	//attributes added for drilldown feature
	public String series;
	public String category;
	
	public String theme;
	
	public String subTitle;
	
	public String dashboardId;
	
	public String dashboardType;
	
	public String serverName;
	
	public String getDashboardType() {
		return dashboardType;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public void setDashboardType(String dashboardType) {
		this.dashboardType = dashboardType;
	}
	public String getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	/**
	 * @return the series
	 */
	public String getSeries() {
		return series;
	}
	/**
	 * @param series the series to set
	 */
	public void setSeries(String series) {
		this.series = series;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the rectcolor
	 */
	public String getRectcolor() {
		return rectcolor;
	}
	/**
	 * @param rectcolor the rectcolor to set
	 */
	public void setRectcolor(String rectcolor) {
		this.rectcolor = rectcolor;
	}
	/**
	 * @return the bgcolor
	 */
	public String getBgcolor() {
		return bgcolor;
	}
	/**
	 * @param bgcolor the bgcolor to set
	 */
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
	/**
	 * @return the autoUpdateInterval
	 */
	public int getAutoUpdateInterval() {
		return autoUpdateInterval;
	}
	/**
	 * @param autoUpdateInterval the autoUpdateInterval to set
	 */
	public void setAutoUpdateInterval(int autoUpdateInterval) {
		this.autoUpdateInterval = autoUpdateInterval;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the reportTemplateId
	 */
	public String getReportTemplateId() {
		return reportTemplateId;
	}
	/**
	 * @param reportTemplateId the reportTemplateId to set
	 */
	public void setReportTemplateId(String reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}
	/**
	 * @return the reportTemplateName
	 */
	public String getReportTemplateName() {
		return reportTemplateName;
	}
	/**
	 * @param reportTemplateName the reportTemplateName to set
	 */
	public void setReportTemplateName(String reportTemplateName) {
		this.reportTemplateName = reportTemplateName;
	}
	/**
	 * @return the reportType
	 */
	public String getReportType() {
		return reportType;
	}
	/**
	 * @param reportType the reportType to set
	 */
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType ;
	}
	
	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	
	/**
	 * @return the chartType
	 */
	public String getChartType() {
		return chartType;
	}
	/**
	 * @param chartType the chartType to set
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	/**
	 * @return the definedQuery
	 */
	public String getDefinedQuery() {
		return definedQuery;
	}
	/**
	 * @param definedQuery the definedQuery to set
	 */
	public void setDefinedQuery(String definedQuery) {
		this.definedQuery = definedQuery;
	}
	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}
	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	/**
	 * @return the EndDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param database the EndDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
		System.out.println("endDate inside ReportTemplate-->"+this.endDate);
	}
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @param database the StartDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
		System.out.println("startDate inside ReportTemplate-->"+this.startDate);
	}
	
	/**
	 * @return the uploadLogoStatus
	 */
	public String getuploadLogoStatus() {
		return uploadLogoStatus;
	}
	/**
	 * @param database the StartDate to set
	 */
	public void setuploadLogoStatus(String uploadLogoStatus) {
		this.uploadLogoStatus = uploadLogoStatus;
			}
	
	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}
	/**
	 * @param schema the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	/**
	 * @return the databaseId
	 */
	public String getDatabaseId() {
		return databaseId;
	}
	/**
	 * @param databaseId the databaseId to set
	 */
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	/**
	 * @return the drilldown
	 */
	public String getDrilldown() {
		return drilldown;
	}
	/**
	 * @param drilldown the drilldown to set
	 */
	public void setDrilldown(String drilldown) {
		this.drilldown = drilldown;
	}
	/**
	 * @return the displayOrder
	 */
	public int getDisplayOrder() {
		return displayOrder;
	}
	/**
	 * @param displayOrder the displayOrder to set
	 */
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getTimePeriod() {
		return timeperiod;
	}
	/**
	 * @param timePeriod the displayOrder to set
	 * @param timeperiod 
	 */
	public void setTimePeriod(String timeperiod) {
		this.timeperiod = timeperiod;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReportTemplate [reportTemplateId=" + reportTemplateId
				+ ", reportTemplateName=" + reportTemplateName
				+ ", reportType=" + reportType + ", chartType=" + chartType
				+ ", definedQuery=" + definedQuery + ", customerId="
				+ customerId + ", databaseId=" + databaseId + ", email="
				+ email + ", database=" + database + ", schema=" + schema
				+ ", drilldown=" + drilldown + ", displayOrder=" + displayOrder
				+ ", autoUpdateInterval=" + autoUpdateInterval + ", bgcolor="
				+ bgcolor + ", rectcolor=" + rectcolor + ", timePeriod=" + timeperiod+"]"; /*timePeriod as the Month name for charts title*/
	}
	
	
}
