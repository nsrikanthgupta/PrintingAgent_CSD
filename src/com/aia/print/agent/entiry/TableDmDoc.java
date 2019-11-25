/**
 * 
 */
package com.aia.print.agent.entiry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author ITT0284
 *
 */
@Entity
@Table(name = "tbl_dm_doc")
public class TableDmDoc {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "dm_doc_id")
	private String dmDocId;
	
	@Column(name = "mt_app_cd")
	private String mtAppCd;
	
	@Column(name = "mt_doc_type_cd")
	private String mtDocTypecd;
	
	@Column(name = "proposal_no")
	private String proposalNo;
	
	@Column(name = "ic_no")
	private String icNo;
	
	@Column(name = "recv_dt")
	private Date recvDt;
	
	@Column(name = "process_year")
	private String Processyear;
	
	@Column(name = "dm_status")
	private int dmStatus;
	
	@Column(name = "tbl_doc_nm")
	private String tblDocNm;
	
	@Column(name = "is_supressed")
	private int isSupressed;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "created_dt")
	private Date createdDt;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "updated_dt")
	private Date updatedDt;
	
	@Column(name = "docpwd")
	private String docPwd;
	
	@Column(name = "channel")
	private String channel;
	
	@Column(name = "doc_creation_dt")
	private String docCreationDt;
	
	@Column(name = "company_code")
	private int companyCode;

	@Column(name = "debtor_code")
	private String debtorCode;
	
	@Column(name = "agt_no")
	private String agtNo;
	
	@Column(name = "client_no")
	private String clientNo;
	
	@Column(name = "client_name")
	private String clientName;
	
	@Column(name = "inception_start_dt")
	private String inceptionStartDt;
	
	@Column(name = "inception_end_dt")
	private String inceptionEndDt;
	
	@Column(name = "sGUID")
	private String sGUID;
	
	@Column(name = "debtor_code2")
	private String debtorCode2;
	
	@Column(name = "bill_no")
	private String billNo;
	
	@Column(name = "bill_type")
	private String billType;
	
	@Column(name = "amount")
	private Double amount;
	
	@Column(name = "sub_client_no")
	private String subClientNo;

	@Column(name = "sub_client_name")
	private String subClientName;
	
	
	@Column(name = "file_format")
	private String fileFormate;
	
	@Column(name = "proposal_type")
	private String proposalType;
	
	@Column(name = "indicator")
	private String indicator;
	
	@Column(name = "page_count")
	private Integer pageCount;
	
	/**
     * 
     */
    public TableDmDoc() {
        super();
        this.createdBy = "PrintingAgent_CSD";
    }

    public String getId() {
		return id;
	}

	public String getDmDocId() {
		return dmDocId;
	}

	public String getMtAppCd() {
		return mtAppCd;
	}

	public String getMtDocTypecd() {
		return mtDocTypecd;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String getIcNo() {
		return icNo;
	}

	public Date getRecvDt() {
		return recvDt;
	}

	public String getProcessyear() {
		return Processyear;
	}

	public int getDmStatus() {
		return dmStatus;
	}

	public String getTblDocNm() {
		return tblDocNm;
	}

	public int getIsSupressed() {
		return isSupressed;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public Date getUpdatedDt() {
		return updatedDt;
	}

	public String getDocPwd() {
		return docPwd;
	}

	public String getChannel() {
		return channel;
	}

	public String getDocCreationDt() {
		return docCreationDt;
	}

	public int getCompanyCode() {
		return companyCode;
	}

	public String getDebtorCode() {
		return debtorCode;
	}

	public String getAgtNo() {
		return agtNo;
	}

	public String getClientNo() {
		return clientNo;
	}

	public String getClientName() {
		return clientName;
	}

	public String getInceptionStartDt() {
		return inceptionStartDt;
	}

	public String getInceptionEndDt() {
		return inceptionEndDt;
	}

	public String getsGUID() {
		return sGUID;
	}

	public String getDebtorCode2() {
		return debtorCode2;
	}

	public String getBillNo() {
		return billNo;
	}

	public String getBillType() {
		return billType;
	}

	public Double getAmount() {
		return amount;
	}

	public String getSubClientNo() {
		return subClientNo;
	}

	public String getSubClientName() {
		return subClientName;
	}

	public String getFileFormate() {
		return fileFormate;
	}

	public String getProposalType() {
		return proposalType;
	}

	public String getIndicator() {
		return indicator;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDmDocId(String dmDocId) {
		this.dmDocId = dmDocId;
	}

	public void setMtAppCd(String mtAppCd) {
		this.mtAppCd = mtAppCd;
	}

	public void setMtDocTypecd(String mtDocTypecd) {
		this.mtDocTypecd = mtDocTypecd;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public void setIcNo(String icNo) {
		this.icNo = icNo;
	}

	public void setRecvDt(Date recvDt) {
		this.recvDt = recvDt;
	}

	public void setProcessyear(String processyear) {
		Processyear = processyear;
	}

	public void setDmStatus(int dmStatus) {
		this.dmStatus = dmStatus;
	}

	public void setTblDocNm(String tblDocNm) {
		this.tblDocNm = tblDocNm;
	}

	public void setIsSupressed(int isSupressed) {
		this.isSupressed = isSupressed;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setUpdatedDt(Date updatedDt) {
		this.updatedDt = updatedDt;
	}

	public void setDocPwd(String docPwd) {
		this.docPwd = docPwd;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setDocCreationDt(String docCreationDt) {
		this.docCreationDt = docCreationDt;
	}

	public void setCompanyCode(int companyCode) {
		this.companyCode = companyCode;
	}

	public void setDebtorCode(String debtorCode) {
		this.debtorCode = debtorCode;
	}

	public void setAgtNo(String agtNo) {
		this.agtNo = agtNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setInceptionStartDt(String inceptionStartDt) {
		this.inceptionStartDt = inceptionStartDt;
	}

	public void setInceptionEndDt(String inceptionEndDt) {
		this.inceptionEndDt = inceptionEndDt;
	}

	public void setsGUID(String sGUID) {
		this.sGUID = sGUID;
	}

	public void setDebtorCode2(String debtorCode2) {
		this.debtorCode2 = debtorCode2;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setSubClientNo(String subClientNo) {
		this.subClientNo = subClientNo;
	}

	public void setSubClientName(String subClientName) {
		this.subClientName = subClientName;
	}

	public void setFileFormate(String fileFormate) {
		this.fileFormate = fileFormate;
	}

	public void setProposalType(String proposalType) {
		this.proposalType = proposalType;
	}

	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	
	

}
