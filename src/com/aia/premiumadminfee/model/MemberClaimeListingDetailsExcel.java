package com.aia.premiumadminfee.model;

import java.io.Serializable;

public class MemberClaimeListingDetailsExcel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String policyNum;
	private String companyName;
	private String billingMonth;
	private String billNum;
	private String claimNum;
	private String empName;
	private String empIcOrPsprtNUm;
	private String empId;
	private String claimantName;
	private String membersh;
	private String relationShip;
	private String planNum;
	private String plandescr;
	private String prodCode;
	private String prodDescr;
	private String branch;
	private String costCentre;
	private String visitDate;
	private String provider;
	private String claimeType;
	private String asoBillAmt;
	private String asoclaimsPaid;
	private String claimPaid;
	public String getPolicyNum() {
		return policyNum;
	}
	public void setPolicyNum(String policyNum) {
		this.policyNum = policyNum;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getBillingMonth() {
		return billingMonth;
	}
	public void setBillingMonth(String billingMonth) {
		this.billingMonth = billingMonth;
	}
	public String getBillNum() {
		return billNum;
	}
	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}
	public String getClaimNum() {
		return claimNum;
	}
	public void setClaimNum(String claimNum) {
		this.claimNum = claimNum;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getEmpIcOrPsprtNUm() {
		return empIcOrPsprtNUm;
	}
	public void setEmpIcOrPsprtNUm(String empIcOrPsprtNUm) {
		this.empIcOrPsprtNUm = empIcOrPsprtNUm;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getClaimantName() {
		return claimantName;
	}
	public void setClaimantName(String claimantName) {
		this.claimantName = claimantName;
	}
	public String getMembersh() {
		return membersh;
	}
	public void setMembersh(String membersh) {
		this.membersh = membersh;
	}
	public String getRelationShip() {
		return relationShip;
	}
	public void setRelationShip(String relationShip) {
		this.relationShip = relationShip;
	}
	public String getPlanNum() {
		return planNum;
	}
	public void setPlanNum(String planNum) {
		this.planNum = planNum;
	}
	public String getPlandescr() {
		return plandescr;
	}
	public void setPlandescr(String plandescr) {
		this.plandescr = plandescr;
	}
	public String getProdCode() {
		return prodCode;
	}
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getProdDescr() {
		return prodDescr;
	}
	public void setProdDescr(String prodDescr) {
		this.prodDescr = prodDescr;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getCostCentre() {
		return costCentre;
	}
	public void setCostCentre(String costCentre) {
		this.costCentre = costCentre;
	}
	public String getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getClaimeType() {
		return claimeType;
	}
	public void setClaimeType(String claimeType) {
		this.claimeType = claimeType;
	}
	public String getAsoBillAmt() {
		return asoBillAmt;
	}
	public void setAsoBillAmt(String asoBillAmt) {
		this.asoBillAmt = asoBillAmt;
	}
	public String getAsoclaimsPaid() {
		return asoclaimsPaid;
	}
	public void setAsoclaimsPaid(String asoclaimsPaid) {
		this.asoclaimsPaid = asoclaimsPaid;
	}
	public String getClaimPaid() {
		return claimPaid;
	}
	public void setClaimPaid(String claimPaid) {
		this.claimPaid = claimPaid;
	}
	@Override
	public String toString() {
		return "MemberClimeDetails [policyNum=" + policyNum + ", companyName=" + companyName + ", billingMonth="
				+ billingMonth + ", billNum=" + billNum + ", claimNum=" + claimNum + ", empName=" + empName
				+ ", empIcOrPsprtNUm=" + empIcOrPsprtNUm + ", empId=" + empId + ", claimantName=" + claimantName
				+ ", membersh=" + membersh + ", relationShip=" + relationShip + ", planNum=" + planNum + ", plandescr="
				+ plandescr + ", prodCode=" + prodCode + ", prodDescr=" + prodDescr + ", branch=" + branch
				+ ", costCentre=" + costCentre + ", visitDate=" + visitDate + ", provider=" + provider + ", climeType="
				+ claimeType + ", asoBillAmt=" + asoBillAmt + ", asoclaimsPaid=" + asoclaimsPaid + ", claimPaid="
				+ claimPaid + "]";
	}
}