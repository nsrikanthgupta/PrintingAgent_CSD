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
	private Integer dmStatus;
	
	@Column(name = "tbl_doc_nm")
	private String tblDocNm;
	
	@Column(name = "is_supressed")
	private Integer isSupressed;
	
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

    /**
     * Returns the id.
     * 
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the dmDocId.
     * 
     * @return the dmDocId.
     */
    public String getDmDocId() {
        return dmDocId;
    }

    /**
     * Sets the dmDocId.
     * 
     * @param dmDocId the dmDocId
     */
    public void setDmDocId(String dmDocId) {
        this.dmDocId = dmDocId;
    }

    /**
     * Returns the mtAppCd.
     * 
     * @return the mtAppCd.
     */
    public String getMtAppCd() {
        return mtAppCd;
    }

    /**
     * Sets the mtAppCd.
     * 
     * @param mtAppCd the mtAppCd
     */
    public void setMtAppCd(String mtAppCd) {
        this.mtAppCd = mtAppCd;
    }

    /**
     * Returns the mtDocTypecd.
     * 
     * @return the mtDocTypecd.
     */
    public String getMtDocTypecd() {
        return mtDocTypecd;
    }

    /**
     * Sets the mtDocTypecd.
     * 
     * @param mtDocTypecd the mtDocTypecd
     */
    public void setMtDocTypecd(String mtDocTypecd) {
        this.mtDocTypecd = mtDocTypecd;
    }

    /**
     * Returns the proposalNo.
     * 
     * @return the proposalNo.
     */
    public String getProposalNo() {
        return proposalNo;
    }

    /**
     * Sets the proposalNo.
     * 
     * @param proposalNo the proposalNo
     */
    public void setProposalNo(String proposalNo) {
        this.proposalNo = proposalNo;
    }

    /**
     * Returns the icNo.
     * 
     * @return the icNo.
     */
    public String getIcNo() {
        return icNo;
    }

    /**
     * Sets the icNo.
     * 
     * @param icNo the icNo
     */
    public void setIcNo(String icNo) {
        this.icNo = icNo;
    }

    /**
     * Returns the recvDt.
     * 
     * @return the recvDt.
     */
    public Date getRecvDt() {
        return recvDt;
    }

    /**
     * Sets the recvDt.
     * 
     * @param recvDt the recvDt
     */
    public void setRecvDt(Date recvDt) {
        this.recvDt = recvDt;
    }

    /**
     * Returns the processyear.
     * 
     * @return the processyear.
     */
    public String getProcessyear() {
        return Processyear;
    }

    /**
     * Sets the processyear.
     * 
     * @param processyear the processyear
     */
    public void setProcessyear(String processyear) {
        Processyear = processyear;
    }

    /**
     * Returns the dmStatus.
     * 
     * @return the dmStatus.
     */
    public Integer getDmStatus() {
        return dmStatus;
    }

    /**
     * Sets the dmStatus.
     * 
     * @param dmStatus the dmStatus
     */
    public void setDmStatus(Integer dmStatus) {
        this.dmStatus = dmStatus;
    }

    /**
     * Returns the tblDocNm.
     * 
     * @return the tblDocNm.
     */
    public String getTblDocNm() {
        return tblDocNm;
    }

    /**
     * Sets the tblDocNm.
     * 
     * @param tblDocNm the tblDocNm
     */
    public void setTblDocNm(String tblDocNm) {
        this.tblDocNm = tblDocNm;
    }

    /**
     * Returns the isSupressed.
     * 
     * @return the isSupressed.
     */
    public Integer getIsSupressed() {
        return isSupressed;
    }

    /**
     * Sets the isSupressed.
     * 
     * @param isSupressed the isSupressed
     */
    public void setIsSupressed(Integer isSupressed) {
        this.isSupressed = isSupressed;
    }

    /**
     * Returns the createdBy.
     * 
     * @return the createdBy.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the createdBy.
     * 
     * @param createdBy the createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns the createdDt.
     * 
     * @return the createdDt.
     */
    public Date getCreatedDt() {
        return createdDt;
    }

    /**
     * Sets the createdDt.
     * 
     * @param createdDt the createdDt
     */
    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    /**
     * Returns the updatedBy.
     * 
     * @return the updatedBy.
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the updatedBy.
     * 
     * @param updatedBy the updatedBy
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Returns the updatedDt.
     * 
     * @return the updatedDt.
     */
    public Date getUpdatedDt() {
        return updatedDt;
    }

    /**
     * Sets the updatedDt.
     * 
     * @param updatedDt the updatedDt
     */
    public void setUpdatedDt(Date updatedDt) {
        this.updatedDt = updatedDt;
    }

    /**
     * Returns the docPwd.
     * 
     * @return the docPwd.
     */
    public String getDocPwd() {
        return docPwd;
    }

    /**
     * Sets the docPwd.
     * 
     * @param docPwd the docPwd
     */
    public void setDocPwd(String docPwd) {
        this.docPwd = docPwd;
    }

    /**
     * Returns the channel.
     * 
     * @return the channel.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Sets the channel.
     * 
     * @param channel the channel
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * Returns the docCreationDt.
     * 
     * @return the docCreationDt.
     */
    public String getDocCreationDt() {
        return docCreationDt;
    }

    /**
     * Sets the docCreationDt.
     * 
     * @param docCreationDt the docCreationDt
     */
    public void setDocCreationDt(String docCreationDt) {
        this.docCreationDt = docCreationDt;
    }

    /**
     * Returns the companyCode.
     * 
     * @return the companyCode.
     */
    public int getCompanyCode() {
        return companyCode;
    }

    /**
     * Sets the companyCode.
     * 
     * @param companyCode the companyCode
     */
    public void setCompanyCode(int companyCode) {
        this.companyCode = companyCode;
    }

    /**
     * Returns the debtorCode.
     * 
     * @return the debtorCode.
     */
    public String getDebtorCode() {
        return debtorCode;
    }

    /**
     * Sets the debtorCode.
     * 
     * @param debtorCode the debtorCode
     */
    public void setDebtorCode(String debtorCode) {
        this.debtorCode = debtorCode;
    }

    /**
     * Returns the agtNo.
     * 
     * @return the agtNo.
     */
    public String getAgtNo() {
        return agtNo;
    }

    /**
     * Sets the agtNo.
     * 
     * @param agtNo the agtNo
     */
    public void setAgtNo(String agtNo) {
        this.agtNo = agtNo;
    }

    /**
     * Returns the clientNo.
     * 
     * @return the clientNo.
     */
    public String getClientNo() {
        return clientNo;
    }

    /**
     * Sets the clientNo.
     * 
     * @param clientNo the clientNo
     */
    public void setClientNo(String clientNo) {
        this.clientNo = clientNo;
    }

    /**
     * Returns the clientName.
     * 
     * @return the clientName.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the clientName.
     * 
     * @param clientName the clientName
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Returns the inceptionStartDt.
     * 
     * @return the inceptionStartDt.
     */
    public String getInceptionStartDt() {
        return inceptionStartDt;
    }

    /**
     * Sets the inceptionStartDt.
     * 
     * @param inceptionStartDt the inceptionStartDt
     */
    public void setInceptionStartDt(String inceptionStartDt) {
        this.inceptionStartDt = inceptionStartDt;
    }

    /**
     * Returns the inceptionEndDt.
     * 
     * @return the inceptionEndDt.
     */
    public String getInceptionEndDt() {
        return inceptionEndDt;
    }

    /**
     * Sets the inceptionEndDt.
     * 
     * @param inceptionEndDt the inceptionEndDt
     */
    public void setInceptionEndDt(String inceptionEndDt) {
        this.inceptionEndDt = inceptionEndDt;
    }

    /**
     * Returns the sGUID.
     * 
     * @return the sGUID.
     */
    public String getsGUID() {
        return sGUID;
    }

    /**
     * Sets the sGUID.
     * 
     * @param sGUID the sGUID
     */
    public void setsGUID(String sGUID) {
        this.sGUID = sGUID;
    }

    /**
     * Returns the debtorCode2.
     * 
     * @return the debtorCode2.
     */
    public String getDebtorCode2() {
        return debtorCode2;
    }

    /**
     * Sets the debtorCode2.
     * 
     * @param debtorCode2 the debtorCode2
     */
    public void setDebtorCode2(String debtorCode2) {
        this.debtorCode2 = debtorCode2;
    }

    /**
     * Returns the billNo.
     * 
     * @return the billNo.
     */
    public String getBillNo() {
        return billNo;
    }

    /**
     * Sets the billNo.
     * 
     * @param billNo the billNo
     */
    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    /**
     * Returns the billType.
     * 
     * @return the billType.
     */
    public String getBillType() {
        return billType;
    }

    /**
     * Sets the billType.
     * 
     * @param billType the billType
     */
    public void setBillType(String billType) {
        this.billType = billType;
    }

    /**
     * Returns the amount.
     * 
     * @return the amount.
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     * 
     * @param amount the amount
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * Returns the subClientNo.
     * 
     * @return the subClientNo.
     */
    public String getSubClientNo() {
        return subClientNo;
    }

    /**
     * Sets the subClientNo.
     * 
     * @param subClientNo the subClientNo
     */
    public void setSubClientNo(String subClientNo) {
        this.subClientNo = subClientNo;
    }

    /**
     * Returns the subClientName.
     * 
     * @return the subClientName.
     */
    public String getSubClientName() {
        return subClientName;
    }

    /**
     * Sets the subClientName.
     * 
     * @param subClientName the subClientName
     */
    public void setSubClientName(String subClientName) {
        this.subClientName = subClientName;
    }

    /**
     * Returns the fileFormate.
     * 
     * @return the fileFormate.
     */
    public String getFileFormate() {
        return fileFormate;
    }

    /**
     * Sets the fileFormate.
     * 
     * @param fileFormate the fileFormate
     */
    public void setFileFormate(String fileFormate) {
        this.fileFormate = fileFormate;
    }

    /**
     * Returns the proposalType.
     * 
     * @return the proposalType.
     */
    public String getProposalType() {
        return proposalType;
    }

    /**
     * Sets the proposalType.
     * 
     * @param proposalType the proposalType
     */
    public void setProposalType(String proposalType) {
        this.proposalType = proposalType;
    }

    /**
     * Returns the indicator.
     * 
     * @return the indicator.
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * Sets the indicator.
     * 
     * @param indicator the indicator
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    /**
     * Returns the pageCount.
     * 
     * @return the pageCount.
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     * Sets the pageCount.
     * 
     * @param pageCount the pageCount
     */
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

}
