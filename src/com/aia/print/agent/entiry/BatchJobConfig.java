/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.entiry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 24 Nov 2019 6:06:33 am
 */
@Entity
@Table(name = "tbl_BatchJobConfig")
public class BatchJobConfig {

    @Id
    private String jobKey;

    @Column
    private String jobDescription;

    @Column
    private String status;

    @Column
    private String jobDetail;

    @Column
    private String expression;

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
    public BatchJobConfig() {
        super();
        this.createdBy = "PrintingAgent_CSD";
    }

    /**
     * Returns the jobKey.
     * 
     * @return the jobKey.
     */
    public String getJobKey() {
        return jobKey;
    }

    /**
     * Sets the jobKey.
     * 
     * @param jobKey the jobKey
     */
    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    /**
     * Returns the jobDescription.
     * 
     * @return the jobDescription.
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Sets the jobDescription.
     * 
     * @param jobDescription the jobDescription
     */
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
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
     * Returns the jobDetail.
     * 
     * @return the jobDetail.
     */
    public String getJobDetail() {
        return jobDetail;
    }

    /**
     * Sets the jobDetail.
     * 
     * @param jobDetail the jobDetail
     */
    public void setJobDetail(String jobDetail) {
        this.jobDetail = jobDetail;
    }

    /**
     * Returns the expression.
     * 
     * @return the expression.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Sets the expression.
     * 
     * @param expression the expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
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
