package com.aia.ahs.aso.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aia.ahs.aso.dao.ClaimStatementDaoImpl;
import com.aia.ahs.aso.model.ClaimStatementBean;
import com.aia.ahs.aso.model.SympNotification;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ClaimStatementService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimStatementService.class);

	@Autowired
	private ClaimStatementDaoImpl claimStatementDao;

	public List<SympNotification> getRequestforDownloadFromSumyfap00021DB() {
		return this.claimStatementDao.getDownloadRequestFromSumyfap00021DB();
	}

	public void startReportGen(List<SympNotification> listSympNotification) {
		ObjectMapper mapper = new ObjectMapper();
		for (SympNotification s : listSympNotification) {
			try {
				List<ClaimStatementBean> listClaimStatementBean = Arrays
						.asList(mapper.readValue(s.getJsonData(), ClaimStatementBean[].class));
				this.claimStatementDao.startReportGen(listClaimStatementBean, s);

			} catch (Exception e) {
				LOGGER.info("Exception Requsted Id" + s.getDocumentId() + " " + e.getMessage());
				e.printStackTrace();
			}

		}
	}

}
