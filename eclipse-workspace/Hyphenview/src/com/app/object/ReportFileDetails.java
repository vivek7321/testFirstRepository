package com.app.object;

public class ReportFileDetails {
	
	public String reportFileDetailsId;
	public String reportFilePath;
	public String reportFileName;
	public String customerId;
	public int displayOrder;
	public String emailId;
	public String reportFileType;
	
	
	/**
	 * @return the reportFileType
	 */
	public String getReportFileType() {
		return reportFileType;
	}
	/**
	 * @param reportFileType the reportFileType to set
	 */
	public void setReportFileType(String reportFileType) {
		this.reportFileType = reportFileType;
	}
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	/**
	 * @return the reportFileDetailsId
	 */
	public String getReportFileDetailsId() {
		return reportFileDetailsId;
	}
	/**
	 * @param reportFileDetailsId the reportFileDetailsId to set
	 */
	public void setReportFileDetailsId(String reportFileDetailsId) {
		this.reportFileDetailsId = reportFileDetailsId;
	}
	/**
	 * @return the reportFilePath
	 */
	public String getReportFilePath() {
		return reportFilePath;
	}
	/**
	 * @param reportFilePath the reportFilePath to set
	 */
	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}
	/**
	 * @return the reportFileName
	 */
	public String getReportFileName() {
		return reportFileName;
	}
	/**
	 * @param reportFileName the reportFileName to set
	 */
	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
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
		return "ReportFileDetails [reportFileDetailsId=" + reportFileDetailsId
				+ ", reportFilePath=" + reportFilePath + ", reportFileName="
				+ reportFileName + ", customerId=" + customerId
				+ ", displayOrder=" + displayOrder + ", emailId=" + emailId
				+ ", reportFileType=" + reportFileType + "]";
	}
	
}
