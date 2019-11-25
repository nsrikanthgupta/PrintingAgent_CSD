package com.aia.premiumadminfee.model;

public class DebitTabledata {
	

	private String description;
	private String billType;
	private String amountExSt;
	private String amountSt;
	private String amountInclSt;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getAmountExSt() {
		return amountExSt;
	}
	public void setAmountExSt(String amountExSt) {
		this.amountExSt = amountExSt;
	}
	public String getAmountSt() {
		return amountSt;
	}
	public void setAmountSt(String amountSt) {
		this.amountSt = amountSt;
	}
	public String getAmountInclSt() {
		return amountInclSt;
	}
	public void setAmountInclSt(String amountInclSt) {
		this.amountInclSt = amountInclSt;
	}
	@Override
	public String toString() {
		return "InvoiceTabledata [description=" + description + ", billType="
				+ billType + ", amountExSt=" + amountExSt + ", amountSt="
				+ amountSt + ", amountInclSt=" + amountInclSt + "]";
	}
	
}
