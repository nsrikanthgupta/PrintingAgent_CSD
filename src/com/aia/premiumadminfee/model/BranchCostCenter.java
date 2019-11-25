package com.aia.premiumadminfee.model;

import java.util.List;

public class BranchCostCenter {
    private String branchName;
    
	private List<BranchCstCenterTbleData> branchCstCenterTbleData;
	private List<BranchCostCenterSubTotalAmnt> branchCostCenterSubTotalAmnt;
	private List<BranchCostCenterGrandTotalAmnt> branchCostCenterGrandTotalAmnt;
	
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public List<BranchCstCenterTbleData> getBranchCstCenterTbleData() {
		return branchCstCenterTbleData;
	}
	public void setBranchCstCenterTbleData(List<BranchCstCenterTbleData> branchCstCenterTbleData) {
		this.branchCstCenterTbleData = branchCstCenterTbleData;
	}
	public List<BranchCostCenterSubTotalAmnt> getBranchCostCenterSubTotalAmnt() {
		return branchCostCenterSubTotalAmnt;
	}
	public void setBranchCostCenterSubTotalAmnt(List<BranchCostCenterSubTotalAmnt> branchCostCenterSubTotalAmnt) {
		this.branchCostCenterSubTotalAmnt = branchCostCenterSubTotalAmnt;
	}
	public List<BranchCostCenterGrandTotalAmnt> getBranchCostCenterGrandTotalAmnt() {
		return branchCostCenterGrandTotalAmnt;
	}
	public void setBranchCostCenterGrandTotalAmnt(List<BranchCostCenterGrandTotalAmnt> branchCostCenterGrandTotalAmnt) {
		this.branchCostCenterGrandTotalAmnt = branchCostCenterGrandTotalAmnt;
	}
	
	
	
}
