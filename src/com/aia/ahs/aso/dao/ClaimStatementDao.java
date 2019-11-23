package com.aia.ahs.aso.dao;

import java.util.List;

import com.aia.ahs.aso.model.ClaimStatementBean;
import com.aia.ahs.aso.model.SympNotification;

public interface ClaimStatementDao {
	/**
	 * get download Request From Sumyfap00021DB
	 */
	List<SympNotification> getDownloadRequestFromSumyfap00021DB();

	/**
	 * start report Gen
	 */
	void startReportGen(List<ClaimStatementBean> listClaimStatementBean, SympNotification sympNotification);

	/**
	 * process Download Requests
	 */
	boolean processDownloadRequest(ClaimStatementBean claimStatementBean, SympNotification sympNotification,
			long listsize);

	/**
	 * update multiple PDF uploaded
	 */
	void updateSympanyNotificationAsSuccesForMultiplePdfById(Integer documentId, Integer pdfCount);

	/**
	 * update single PDF uploaded
	 */
	void updateSympanyNotificationAsSuccesForSinglePdfById(Integer documentId, Integer pdfCount);

	/**
	 * update as failure
	 */
	void updateSympanyNotificationAsFailureById(Integer documentId, String REMARK_FAILURE);

}
