/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.entiry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author Srikanth Neerumalla 
 * @DateTime 25 Nov 2019 6:09:24 am
 */
@Entity
@Table(name = "tbl_ReconcilationData")
public class ReconcilationData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ReconcilationDataIdSequence")
    @SequenceGenerator(name = "ReconcilationDataIdSequence", initialValue = 1000)
    private Long reconcilationId;
    
    @Column
    private String companyCode;
    
    @Column
    private String PolicyNo;
    
    @Column
    private String BillNo;
    
    @Column
    private String CycleDate;
    
    @Column
    private String SubsidaryNo;
    
    @Column
    private String TotalNo;
    
    @Column
    private Long batchId;
    
    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    /**
     * 
     */
    public ReconcilationData() {
        super();
        this.createdBy = "PrintingAgent_CSD";
    }

    public ReconcilationData(Long batchId) {
        this.batchId = batchId;
        this.createdBy = "PrintingAgent_CSD";
    }

    /**
     * Returns the reconcilationId.
     * 
     * @return the reconcilationId.
     */
    public Long getReconcilationId() {
        return reconcilationId;
    }

    /**
     * Sets the reconcilationId.
     * 
     * @param reconcilationId the reconcilationId
     */
    public void setReconcilationId(Long reconcilationId) {
        this.reconcilationId = reconcilationId;
    }

    /**
     * Returns the companyCode.
     * 
     * @return the companyCode.
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     * Sets the companyCode.
     * 
     * @param companyCode the companyCode
     */
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    /**
     * Returns the policyNo.
     * 
     * @return the policyNo.
     */
    public String getPolicyNo() {
        return PolicyNo;
    }

    /**
     * Sets the policyNo.
     * 
     * @param policyNo the policyNo
     */
    public void setPolicyNo(String policyNo) {
        PolicyNo = policyNo;
    }

    /**
     * Returns the billNo.
     * 
     * @return the billNo.
     */
    public String getBillNo() {
        return BillNo;
    }

    /**
     * Sets the billNo.
     * 
     * @param billNo the billNo
     */
    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    /**
     * Returns the cycleDate.
     * 
     * @return the cycleDate.
     */
    public String getCycleDate() {
        return CycleDate;
    }

    /**
     * Sets the cycleDate.
     * 
     * @param cycleDate the cycleDate
     */
    public void setCycleDate(String cycleDate) {
        CycleDate = cycleDate;
    }

    /**
     * Returns the subsidaryNo.
     * 
     * @return the subsidaryNo.
     */
    public String getSubsidaryNo() {
        return SubsidaryNo;
    }

    /**
     * Sets the subsidaryNo.
     * 
     * @param subsidaryNo the subsidaryNo
     */
    public void setSubsidaryNo(String subsidaryNo) {
        SubsidaryNo = subsidaryNo;
    }

    /**
     * Returns the totalNo.
     * 
     * @return the totalNo.
     */
    public String getTotalNo() {
        return TotalNo;
    }

    /**
     * Sets the totalNo.
     * 
     * @param totalNo the totalNo
     */
    public void setTotalNo(String totalNo) {
        TotalNo = totalNo;
    }

    /**
     * Returns the batchId.
     * 
     * @return the batchId.
     */
    public Long getBatchId() {
        return batchId;
    }

    /**
     * Sets the batchId.
     * 
     * @param batchId the batchId
     */
    public void setBatchId(Long batchId) {
        this.batchId = batchId;
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
     * Returns the updatedDate.
     * 
     * @return the updatedDate.
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Sets the updatedDate.
     * 
     * @param updatedDate the updatedDate
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * Returns the createdDate.
     * 
     * @return the createdDate.
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the createdDate.
     * 
     * @param createdDate the createdDate
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    
    
}
