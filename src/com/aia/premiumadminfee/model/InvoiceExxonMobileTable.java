package com.aia.premiumadminfee.model;

public class InvoiceExxonMobileTable {
	public InvoiceExxonMobileTable(){}
	private String dsc;
	private String claimsPaid;
	private String adminFeeExclSt;
	private String st;
	private String amntInclSt;

	
	
	public InvoiceExxonMobileTable(String dsc, String claimsPaid, String adminFeeExclSt, String st,
			String amntInclSt) {
		super();
		this.dsc = dsc;
		this.claimsPaid = claimsPaid;
		this.adminFeeExclSt = adminFeeExclSt;
		this.st = st;
		this.amntInclSt = amntInclSt;
	}
	public String getDsc() {
		return dsc;
	}
	public void setDsc(String dsc) {
		this.dsc = dsc;
	}
	public String getClaimsPaid() {
		return claimsPaid;
	}
	public void setClaimsPaid(String claimsPaid) {
		this.claimsPaid = claimsPaid;
	}
	public String getAdminFeeExclSt() {
		return adminFeeExclSt;
	}
	public void setAdminFeeExclSt(String adminFeeExclSt) {
		this.adminFeeExclSt = adminFeeExclSt;
	}
	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
	public String getAmntInclSt() {
		return amntInclSt;
	}
	public void setAmntInclSt(String amntInclSt) {
		this.amntInclSt = amntInclSt;
	}
	
	
}
