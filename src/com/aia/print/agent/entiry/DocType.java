/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.entiry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 6 Oct 2019 2:00:58 pm
 */
@Entity
@Table(name = "tbl_mt_doc_type543")
public class DocType {

    @Column
    @Id
    private String cd;

    @Column
    private String dscp;

    @Column
    private int status;

    @Column
    private String tbl_nm;

    @Column
    private char tbl_freq;

    @Column
    private String mt_cm_info_cd;

    @Column
    private String CATEGORY;

    @Column
    private char ALPPUSED;

    @Column
    private String DISPLAYNAME;

    @Column
    private char SHOWPROCESS_YEAR;

    @Column
    private String main_doc;

    @Column
    private int doc_seq;

    @Column
    private String doc_type;

    
    
    /**
     * 
     */
    public DocType() {
        super();
    }

    /**
     * Returns the cd.
     * 
     * @return the cd.
     */
    public String getCd() {
        return cd;
    }

    /**
     * Sets the cd.
     * 
     * @param cd the cd
     */
    public void setCd(String cd) {
        this.cd = cd;
    }

    /**
     * Returns the dscp.
     * 
     * @return the dscp.
     */
    public String getDscp() {
        return dscp;
    }

    /**
     * Sets the dscp.
     * 
     * @param dscp the dscp
     */
    public void setDscp(String dscp) {
        this.dscp = dscp;
    }

    /**
     * Returns the status.
     * 
     * @return the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Returns the tbl_nm.
     * 
     * @return the tbl_nm.
     */
    public String getTbl_nm() {
        return tbl_nm;
    }

    /**
     * Sets the tbl_nm.
     * 
     * @param tbl_nm the tbl_nm
     */
    public void setTbl_nm(String tbl_nm) {
        this.tbl_nm = tbl_nm;
    }

    /**
     * Returns the tbl_freq.
     * 
     * @return the tbl_freq.
     */
    public char getTbl_freq() {
        return tbl_freq;
    }

    /**
     * Sets the tbl_freq.
     * 
     * @param tbl_freq the tbl_freq
     */
    public void setTbl_freq(char tbl_freq) {
        this.tbl_freq = tbl_freq;
    }

    /**
     * Returns the mt_cm_info_cd.
     * 
     * @return the mt_cm_info_cd.
     */
    public String getMt_cm_info_cd() {
        return mt_cm_info_cd;
    }

    /**
     * Sets the mt_cm_info_cd.
     * 
     * @param mt_cm_info_cd the mt_cm_info_cd
     */
    public void setMt_cm_info_cd(String mt_cm_info_cd) {
        this.mt_cm_info_cd = mt_cm_info_cd;
    }

    /**
     * Returns the cATEGORY.
     * 
     * @return the cATEGORY.
     */
    public String getCATEGORY() {
        return CATEGORY;
    }

    /**
     * Sets the cATEGORY.
     * 
     * @param cATEGORY the cATEGORY
     */
    public void setCATEGORY(String cATEGORY) {
        CATEGORY = cATEGORY;
    }

    /**
     * Returns the aLPPUSED.
     * 
     * @return the aLPPUSED.
     */
    public char getALPPUSED() {
        return ALPPUSED;
    }

    /**
     * Sets the aLPPUSED.
     * 
     * @param aLPPUSED the aLPPUSED
     */
    public void setALPPUSED(char aLPPUSED) {
        ALPPUSED = aLPPUSED;
    }

    /**
     * Returns the dISPLAYNAME.
     * 
     * @return the dISPLAYNAME.
     */
    public String getDISPLAYNAME() {
        return DISPLAYNAME;
    }

    /**
     * Sets the dISPLAYNAME.
     * 
     * @param dISPLAYNAME the dISPLAYNAME
     */
    public void setDISPLAYNAME(String dISPLAYNAME) {
        DISPLAYNAME = dISPLAYNAME;
    }

    /**
     * Returns the sHOWPROCESS_YEAR.
     * 
     * @return the sHOWPROCESS_YEAR.
     */
    public char getSHOWPROCESS_YEAR() {
        return SHOWPROCESS_YEAR;
    }

    /**
     * Sets the sHOWPROCESS_YEAR.
     * 
     * @param sHOWPROCESS_YEAR the sHOWPROCESS_YEAR
     */
    public void setSHOWPROCESS_YEAR(char sHOWPROCESS_YEAR) {
        SHOWPROCESS_YEAR = sHOWPROCESS_YEAR;
    }

    /**
     * Returns the main_doc.
     * 
     * @return the main_doc.
     */
    public String getMain_doc() {
        return main_doc;
    }

    /**
     * Sets the main_doc.
     * 
     * @param main_doc the main_doc
     */
    public void setMain_doc(String main_doc) {
        this.main_doc = main_doc;
    }

    /**
     * Returns the doc_seq.
     * 
     * @return the doc_seq.
     */
    public int getDoc_seq() {
        return doc_seq;
    }

    /**
     * Sets the doc_seq.
     * 
     * @param doc_seq the doc_seq
     */
    public void setDoc_seq(int doc_seq) {
        this.doc_seq = doc_seq;
    }

    /**
     * Returns the doc_type.
     * 
     * @return the doc_type.
     */
    public String getDoc_type() {
        return doc_type;
    }

    /**
     * Sets the doc_type.
     * 
     * @param doc_type the doc_type
     */
    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

}
