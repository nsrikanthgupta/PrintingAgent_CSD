package com.aia.premiumadminfee.model;

import java.util.List;

public class MemberDetails {
	private List<MemberDetilsTableData> memberDetilsTableData;
	private List<MembrDetailsSubTotal> membrDetailsSubTotal;
	private List<MembrDetailsGrandTotal> membrDetailsGrandTotal;
	
	
	public List<MemberDetilsTableData> getMemberDetilsTableData() {
		return memberDetilsTableData;
	}
	public void setMemberDetilsTableData(List<MemberDetilsTableData> memberDetilsTableData) {
		this.memberDetilsTableData = memberDetilsTableData;
	}
	public List<MembrDetailsSubTotal> getMembrDetailsSubTotal() {
		return membrDetailsSubTotal;
	}
	public void setMembrDetailsSubTotal(List<MembrDetailsSubTotal> membrDetailsSubTotal) {
		this.membrDetailsSubTotal = membrDetailsSubTotal;
	}
	public List<MembrDetailsGrandTotal> getMembrDetailsGrandTotal() {
		return membrDetailsGrandTotal;
	}
	public void setMembrDetailsGrandTotal(List<MembrDetailsGrandTotal> membrDetailsGrandTotal) {
		this.membrDetailsGrandTotal = membrDetailsGrandTotal;
	}
	

	
}
