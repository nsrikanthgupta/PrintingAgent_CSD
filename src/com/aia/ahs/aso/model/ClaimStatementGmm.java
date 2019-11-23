package com.aia.ahs.aso.model;

import java.util.List;

public class ClaimStatementGmm {
	
private String claimNum;
private String admissionOrDischarge;
private String paymentAuth;
private String claimantName;
private String empName;
private String nricPasprtNo;
private String policyHolder;
private String subsidiary;
private String policyNum;
private String medicalProvider;
private String medicalProviderInvoiceNo;
private String medicalProviderReceiptNo;
private String relationship;
private String empId;
private String product;
private String plan;

private List<ClaimStatementGmmMainTable> claimStatementGmmMainTable;
private String subMajorMedicalAmnt;
private String subIncurredAmnt;
private String subEligibleAmnt;
private String subInEligibleAmnt;

//summary table details
private String summary_IneligibleAmnt;
private String summary_DeductibleAmnt;
private String percentage_co_InsurenceAmnt;
private String summary_co_InsurenceAmnt;
private String summary_TtlInEligibleAmnt;
private String summary_LMGHDCashAllowancePayableClaimant;
private String summary_LessMemberDeposit;
private String summary_AmntDue;

//private List<ClaimStatementGmmClaimExcessTable> claimStatementGmmClaimExcessTableDetails;
private String EXRSN1;
private String EXRSN2;
private String EXRSN3;
private String EXRSN4;

private String EXAMT1;
private String EXAMT2;
private String EXAMT3;
private String EXAMT4;

private List<ClaimStatementGmmPayeeTable>  claimStatementGmmPayeeTable;

public String getClaimNum() {
	return claimNum;
}

public void setClaimNum(String claimNum) {
	this.claimNum = claimNum;
}

public String getAdmissionOrDischarge() {
	return admissionOrDischarge;
}

public void setAdmissionOrDischarge(String admissionOrDischarge) {
	this.admissionOrDischarge = admissionOrDischarge;
}

public String getPaymentAuth() {
	return paymentAuth;
}

public void setPaymentAuth(String paymentAuth) {
	this.paymentAuth = paymentAuth;
}

public String getClaimantName() {
	return claimantName;
}

public void setClaimantName(String claimantName) {
	this.claimantName = claimantName;
}

public String getEmpName() {
	return empName;
}

public void setEmpName(String empName) {
	this.empName = empName;
}



public String getNricPasprtNo() {
	return nricPasprtNo;
}

public void setNricPasprtNo(String nricPasprtNo) {
	this.nricPasprtNo = nricPasprtNo;
}

public String getPolicyHolder() {
	return policyHolder;
}

public void setPolicyHolder(String policyHolder) {
	this.policyHolder = policyHolder;
}

public String getSubsidiary() {
	return subsidiary;
}

public void setSubsidiary(String subsidiary) {
	this.subsidiary = subsidiary;
}

public String getPolicyNum() {
	return policyNum;
}

public void setPolicyNum(String policyNum) {
	this.policyNum = policyNum;
}

public String getMedicalProvider() {
	return medicalProvider;
}

public void setMedicalProvider(String medicalProvider) {
	this.medicalProvider = medicalProvider;
}

public String getMedicalProviderInvoiceNo() {
	return medicalProviderInvoiceNo;
}

public void setMedicalProviderInvoiceNo(String medicalProviderInvoiceNo) {
	this.medicalProviderInvoiceNo = medicalProviderInvoiceNo;
}

public String getMedicalProviderReceiptNo() {
	return medicalProviderReceiptNo;
}

public void setMedicalProviderReceiptNo(String medicalProviderReceiptNo) {
	this.medicalProviderReceiptNo = medicalProviderReceiptNo;
}

public String getRelationship() {
	return relationship;
}

public void setRelationship(String relationship) {
	this.relationship = relationship;
}

public String getEmpId() {
	return empId;
}

public void setEmpId(String empId) {
	this.empId = empId;
}

public String getProduct() {
	return product;
}

public void setProduct(String product) {
	this.product = product;
}

public String getPlan() {
	return plan;
}

public void setPlan(String plan) {
	this.plan = plan;
}

public List<ClaimStatementGmmMainTable> getClaimStatementGmmMainTable() {
	return claimStatementGmmMainTable;
}

public void setClaimStatementGmmMainTable(List<ClaimStatementGmmMainTable> claimStatementGmmMainTable) {
	this.claimStatementGmmMainTable = claimStatementGmmMainTable;
}

public String getSubMajorMedicalAmnt() {
	return subMajorMedicalAmnt;
}

public void setSubMajorMedicalAmnt(String subMajorMedicalAmnt) {
	this.subMajorMedicalAmnt = subMajorMedicalAmnt;
}

public String getSubIncurredAmnt() {
	return subIncurredAmnt;
}

public void setSubIncurredAmnt(String subIncurredAmnt) {
	this.subIncurredAmnt = subIncurredAmnt;
}

public String getSubEligibleAmnt() {
	return subEligibleAmnt;
}

public void setSubEligibleAmnt(String subEligibleAmnt) {
	this.subEligibleAmnt = subEligibleAmnt;
}

public String getSubInEligibleAmnt() {
	return subInEligibleAmnt;
}

public void setSubInEligibleAmnt(String subInEligibleAmnt) {
	this.subInEligibleAmnt = subInEligibleAmnt;
}

public String getSummary_IneligibleAmnt() {
	return summary_IneligibleAmnt;
}

public void setSummary_IneligibleAmnt(String summary_IneligibleAmnt) {
	this.summary_IneligibleAmnt = summary_IneligibleAmnt;
}

public String getSummary_DeductibleAmnt() {
	return summary_DeductibleAmnt;
}

public void setSummary_DeductibleAmnt(String summary_DeductibleAmnt) {
	this.summary_DeductibleAmnt = summary_DeductibleAmnt;
}

public String getPercentage_co_InsurenceAmnt() {
	return percentage_co_InsurenceAmnt;
}

public void setPercentage_co_InsurenceAmnt(String percentage_co_InsurenceAmnt) {
	this.percentage_co_InsurenceAmnt = percentage_co_InsurenceAmnt;
}

public String getSummary_co_InsurenceAmnt() {
	return summary_co_InsurenceAmnt;
}

public void setSummary_co_InsurenceAmnt(String summary_co_InsurenceAmnt) {
	this.summary_co_InsurenceAmnt = summary_co_InsurenceAmnt;
}

public String getSummary_TtlInEligibleAmnt() {
	return summary_TtlInEligibleAmnt;
}

public void setSummary_TtlInEligibleAmnt(String summary_TtlInEligibleAmnt) {
	this.summary_TtlInEligibleAmnt = summary_TtlInEligibleAmnt;
}

public String getSummary_LMGHDCashAllowancePayableClaimant() {
	return summary_LMGHDCashAllowancePayableClaimant;
}

public void setSummary_LMGHDCashAllowancePayableClaimant(String summary_LMGHDCashAllowancePayableClaimant) {
	this.summary_LMGHDCashAllowancePayableClaimant = summary_LMGHDCashAllowancePayableClaimant;
}

public String getSummary_LessMemberDeposit() {
	return summary_LessMemberDeposit;
}

public void setSummary_LessMemberDeposit(String summary_LessMemberDeposit) {
	this.summary_LessMemberDeposit = summary_LessMemberDeposit;
}

public String getSummary_AmntDue() {
	return summary_AmntDue;
}

public void setSummary_AmntDue(String summary_AmntDue) {
	this.summary_AmntDue = summary_AmntDue;
}

public String getEXRSN1() {
	return EXRSN1;
}

public void setEXRSN1(String eXRSN1) {
	EXRSN1 = eXRSN1;
}

public String getEXRSN2() {
	return EXRSN2;
}

public void setEXRSN2(String eXRSN2) {
	EXRSN2 = eXRSN2;
}

public String getEXRSN3() {
	return EXRSN3;
}

public void setEXRSN3(String eXRSN3) {
	EXRSN3 = eXRSN3;
}

public String getEXRSN4() {
	return EXRSN4;
}

public void setEXRSN4(String eXRSN4) {
	EXRSN4 = eXRSN4;
}

public String getEXAMT1() {
	return EXAMT1;
}

public void setEXAMT1(String eXAMT1) {
	EXAMT1 = eXAMT1;
}

public String getEXAMT2() {
	return EXAMT2;
}

public void setEXAMT2(String eXAMT2) {
	EXAMT2 = eXAMT2;
}

public String getEXAMT3() {
	return EXAMT3;
}

public void setEXAMT3(String eXAMT3) {
	EXAMT3 = eXAMT3;
}

public String getEXAMT4() {
	return EXAMT4;
}

public void setEXAMT4(String eXAMT4) {
	EXAMT4 = eXAMT4;
}

public List<ClaimStatementGmmPayeeTable> getClaimStatementGmmPayeeTable() {
	return claimStatementGmmPayeeTable;
}

public void setClaimStatementGmmPayeeTable(List<ClaimStatementGmmPayeeTable> claimStatementGmmPayeeTable) {
	this.claimStatementGmmPayeeTable = claimStatementGmmPayeeTable;
}



}
