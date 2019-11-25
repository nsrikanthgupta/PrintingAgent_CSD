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
 * @DateTime 25 Nov 2019 6:13:59 am
 */
@Entity
@Table(name = "tbl_ReconcilationDetail")
public class ReconcilationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ReconcilationDetailIdSequence")
    @SequenceGenerator(name = "ReconcilationDetailIdSequence", initialValue = 1000)
    private Long reconcilationDetailId;

    @Column
    private Long reconcilationId;
    
    @Column
    private String docType;

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
    public ReconcilationDetail() {
        super();
        this.createdBy = "PrintingAgent_CSD";
    }

    public ReconcilationDetail(Long reconcilationId) {
        this.reconcilationId = reconcilationId;
        this.createdBy = "PrintingAgent_CSD";
    }

    /**
     * Returns the reconcilationDetailId.
     * 
     * @return the reconcilationDetailId.
     */
    public Long getReconcilationDetailId() {
        return reconcilationDetailId;
    }

    /**
     * Sets the reconcilationDetailId.
     * 
     * @param reconcilationDetailId the reconcilationDetailId
     */
    public void setReconcilationDetailId(Long reconcilationDetailId) {
        this.reconcilationDetailId = reconcilationDetailId;
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
     * Returns the docType.
     * 
     * @return the docType.
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets the docType.
     * 
     * @param docType the docType
     */
    public void setDocType(String docType) {
        this.docType = docType;
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
