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
 * 
 * @author Srikanth Neerumalla
 * @DateTime 5 Oct 2019 11:26:42 am
 */
@Entity
@Table(name = "tbl_CompanyCode")
public class CompanyCode {
    
    @Id
    private Long companyCodeId;

    @Column
    private String companyCode;

    @Column
    private String username;

    @Column
    private String folderPath;

    @Column
    private String password;

    @Column
    private String ipAddress;

    @Column
    private String status;

    @Column
    private String latestCycleDate;

    @Column
    private String localFolderPath;

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
    public CompanyCode() {
        super();
        this.createdBy = "PrintingAgent_CSD";
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
     * Returns the username.
     * 
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the folderPath.
     * 
     * @return the folderPath.
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * Sets the folderPath.
     * 
     * @param folderPath the folderPath
     */
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    /**
     * Returns the password.
     * 
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the ipAddress.
     * 
     * @return the ipAddress.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the ipAddress.
     * 
     * @param ipAddress the ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
     * Returns the latestCycleDate.
     * 
     * @return the latestCycleDate.
     */
    public String getLatestCycleDate() {
        return latestCycleDate;
    }

    /**
     * Sets the latestCycleDate.
     * 
     * @param latestCycleDate the latestCycleDate
     */
    public void setLatestCycleDate(String latestCycleDate) {
        this.latestCycleDate = latestCycleDate;
    }

    /**
     * Returns the localFolderPath.
     * 
     * @return the localFolderPath.
     */
    public String getLocalFolderPath() {
        return localFolderPath;
    }

    /**
     * Sets the localFolderPath.
     * 
     * @param localFolderPath the localFolderPath
     */
    public void setLocalFolderPath(String localFolderPath) {
        this.localFolderPath = localFolderPath;
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

    /**
     * Returns the companyCodeId.
     * 
     * @return the companyCodeId.
     */
    public Long getCompanyCodeId() {
        return companyCodeId;
    }

    /**
     * Sets the companyCodeId.
     * 
     * @param companyCodeId the companyCodeId
     */
    public void setCompanyCodeId(Long companyCodeId) {
        this.companyCodeId = companyCodeId;
    }

    
    
    

}
