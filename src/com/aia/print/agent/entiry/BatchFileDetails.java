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
 * 
 * @author Srikanth Neerumalla
 * @DateTime 5 Oct 2019 1:07:48 pm
 */
@Entity
@Table(name = "tbl_BatchFileDetails")
public class BatchFileDetails {

    @Column
    private Long batchId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BatchFileIdSequence")
    @SequenceGenerator(name = "BatchFileIdSequence", initialValue = 5)
    private Long batchFileId;

    @Column
    private String fileName;

    @Column
    private String documentCode;

    @Column
    private String fileLocation;

    @Column
    private String status;

    @Column
    private String archivePath;

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

    @Column
    private Integer expectedDocumentCount;

    @Column
    private Integer actualDocumentCount;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date documentProcessStartDt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date documentProcessEndDt;
    
    @Column
    private String parseError;

    /**
     * 
     */
    public BatchFileDetails() {
        super();
        this.createdBy = "PrintingAgent_CSD";
    }

    /**
     * @param batchId
     * @param batchFileId
     * @param fileName
     * @param fileLocation
     * @param status
     * @param archivePath
     * @param remarks
     */
    public BatchFileDetails(Long batchId, Long batchFileId, String fileName, String fileLocation, String status,
        String archivePath, String remarks) {
        super();
        this.batchId = batchId;
        this.batchFileId = batchFileId;
        this.fileName = fileName;
        this.fileLocation = fileLocation;
        this.status = status;
        this.archivePath = archivePath;
        this.remarks = remarks;
        this.createdBy = "PrintingAgent_CSD";
        this.createdDate = new Date();
    }

    /**
     * @param batchId
     * @param status
     * @param fileName
     */
    public BatchFileDetails(Long batchId, String status, String fileName) {
        super();
        this.batchId = batchId;
        this.fileName = fileName;
        this.status = status;
        this.createdBy = "PrintingAgent_CSD";
        this.createdDate = new Date();
        this.updatedBy = "";
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
     * Returns the batchFileId.
     * 
     * @return the batchFileId.
     */
    public Long getBatchFileId() {
        return batchFileId;
    }

    /**
     * Sets the batchFileId.
     * 
     * @param batchFileId the batchFileId
     */
    public void setBatchFileId(Long batchFileId) {
        this.batchFileId = batchFileId;
    }

    /**
     * Returns the fileName.
     * 
     * @return the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the fileName.
     * 
     * @param fileName the fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the fileLocation.
     * 
     * @return the fileLocation.
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Sets the fileLocation.
     * 
     * @param fileLocation the fileLocation
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
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
     * Returns the archivePath.
     * 
     * @return the archivePath.
     */
    public String getArchivePath() {
        return archivePath;
    }

    /**
     * Sets the archivePath.
     * 
     * @param archivePath the archivePath
     */
    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
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

    /**
     * Returns the documentCode.
     * 
     * @return the documentCode.
     */
    public String getDocumentCode() {
        return documentCode;
    }

    /**
     * Sets the documentCode.
     * 
     * @param documentCode the documentCode
     */
    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    /**
     * Returns the expectedDocumentCount.
     * 
     * @return the expectedDocumentCount.
     */
    public Integer getExpectedDocumentCount() {
        return expectedDocumentCount;
    }

    /**
     * Sets the expectedDocumentCount.
     * 
     * @param expectedDocumentCount the expectedDocumentCount
     */
    public void setExpectedDocumentCount(Integer expectedDocumentCount) {
        this.expectedDocumentCount = expectedDocumentCount;
    }

    /**
     * Returns the actualDocumentCount.
     * 
     * @return the actualDocumentCount.
     */
    public Integer getActualDocumentCount() {
        return actualDocumentCount;
    }

    /**
     * Sets the actualDocumentCount.
     * 
     * @param actualDocumentCount the actualDocumentCount
     */
    public void setActualDocumentCount(Integer actualDocumentCount) {
        this.actualDocumentCount = actualDocumentCount;
    }

    /**
     * Returns the documentProcessStartDt.
     * 
     * @return the documentProcessStartDt.
     */
    public Date getDocumentProcessStartDt() {
        return documentProcessStartDt;
    }

    /**
     * Sets the documentProcessStartDt.
     * 
     * @param documentProcessStartDt the documentProcessStartDt
     */
    public void setDocumentProcessStartDt(Date documentProcessStartDt) {
        this.documentProcessStartDt = documentProcessStartDt;
    }

    /**
     * Returns the documentProcessEndDt.
     * 
     * @return the documentProcessEndDt.
     */
    public Date getDocumentProcessEndDt() {
        return documentProcessEndDt;
    }

    /**
     * Sets the documentProcessEndDt.
     * 
     * @param documentProcessEndDt the documentProcessEndDt
     */
    public void setDocumentProcessEndDt(Date documentProcessEndDt) {
        this.documentProcessEndDt = documentProcessEndDt;
    }

	/**
	 * @return the parseError
	 */
	public String getParseError() {
		return parseError;
	}

	/**
	 * @param parseError the parseError to set
	 */
	public void setParseError(String parseError) {
		this.parseError = parseError;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchFileDetails [batchId=" + batchId + ", batchFileId=" + batchFileId + ", fileName=" + fileName
				+ ", documentCode=" + documentCode + ", fileLocation=" + fileLocation + ", status=" + status
				+ ", archivePath=" + archivePath + ", remarks=" + remarks + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + ", updatedDate=" + updatedDate + ", createdDate=" + createdDate
				+ ", expectedDocumentCount=" + expectedDocumentCount + ", actualDocumentCount=" + actualDocumentCount
				+ ", documentProcessStartDt=" + documentProcessStartDt + ", documentProcessEndDt="
				+ documentProcessEndDt + ", parseError=" + parseError + "]";
	}
	
	
    
}
