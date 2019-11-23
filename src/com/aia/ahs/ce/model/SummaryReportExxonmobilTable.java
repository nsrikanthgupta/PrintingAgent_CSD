package com.aia.ahs.ce.model;

public class SummaryReportExxonmobilTable {
	public SummaryReportExxonmobilTable(){}
	
	private String policyNum;
	private String clientName;
	private String billNum;
	private String asoPaid;
	private String asoExcess;
	private String grandTtl;
	public SummaryReportExxonmobilTable(String policyNum, String clientName, String billNum, String asoPaid,
			String asoExcess, String grandTtl) {
		super();
		this.policyNum = policyNum;
		this.clientName = clientName;
		this.billNum = billNum;
		this.asoPaid = asoPaid;
		this.asoExcess = asoExcess;
		this.grandTtl = grandTtl;
	}
	public String getPolicyNum() {
		return policyNum;
	}
	public void setPolicyNum(String policyNum) {
		this.policyNum = policyNum;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getBillNum() {
		return billNum;
	}
	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}
	public String getAsoPaid() {
		return asoPaid;
	}
		public void setAsoPaid(String asoPaid) {
		this.asoPaid = asoPaid;
	}
	public String getAsoExcess() {
		return asoExcess;
	}
	public void setAsoExcess(String asoExcess) {
		this.asoExcess = asoExcess;
	}
		public String getGrandTtl() {
		return grandTtl;
	}
	public void setGrandTtl(String grandTtl) {
		this.grandTtl = grandTtl;
	}

	
	
}
