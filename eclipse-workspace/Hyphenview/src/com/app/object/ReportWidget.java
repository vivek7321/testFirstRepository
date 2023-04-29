package com.app.object;

public class ReportWidget {
	
	public String widgetId;
	public String widgetName;
	public String widgetType;
	public String definedQuery;
	public String customerId;
	public String databaseId;
	public String email;
	public String database;
	public String schema;
	public int displayOrder;
	/**
	 * @return the widgetId
	 */
	public String getWidgetId() {
		return widgetId;
	}
	/**
	 * @param widgetId the widgetId to set
	 */
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}
	/**
	 * @return the widgetName
	 */
	public String getWidgetName() {
		return widgetName;
	}
	/**
	 * @param widgetName the widgetName to set
	 */
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	/**
	 * @return the widgetType
	 */
	public String getWidgetType() {
		return widgetType;
	}
	/**
	 * @param widgetType the widgetType to set
	 */
	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReportWidget [widgetId=" + widgetId + ", widgetName="
				+ widgetName + ", widgetType=" + widgetType + ", definedQuery="
				+ definedQuery + ", customerId=" + customerId + ", databaseId="
				+ databaseId + ", email=" + email + ", database=" + database
				+ ", schema=" + schema + ", displayOrder=" + displayOrder + "]";
	}
	
	
}
