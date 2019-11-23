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
 * @DateTime 5 Oct 2019 12:19:33 pm
 */
@Entity
@Table(name = "tbl_BatchCycle")
public class BatchCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BatchIdSequence")
    @SequenceGenerator(name = "BatchIdSequence", initialValue = 1000)
    private Long batchId;

    @Column
    private String companyCode;

    @Column
    private String status;

    @Column
    private String cycleDate;

    @Column
    private String remarks;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    @Temporal(TemporalType.DATE)
    private Date updatedDate;

    @Column
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    /**
     * 
     */
    public BatchCycle() {
        super();
    }

    /**
     * @param batchId
     * @param companyCode
     * @param status
     * @param cycleDate
     */
    public BatchCycle(Long batchId, String companyCode, String status, String cycleDate) {
        super();
        this.batchId = batchId;
        this.companyCode = companyCode;
        this.status = status;
        this.cycleDate = cycleDate;
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
     * Returns the status.
     * 
     * @return the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the cycleDate.
     * 
     * @return the cycleDate.
     */
    public String getCycleDate() {
        return cycleDate;
    }

    /**
     * Sets the cycleDate.
     * 
     * @param cycleDate the cycleDate
     */
    public void setCycleDate(String cycleDate) {
        this.cycleDate = cycleDate;
    }

    /**
     * Returns the remarks.
     * 
     * @return the remarks.
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks.
     * 
     * @param remarks the remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
