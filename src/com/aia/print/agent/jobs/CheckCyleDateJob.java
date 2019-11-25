/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.jobs;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.aia.print.agent.entiry.BatchJobConfig;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.PrintAgentService;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 4 Oct 2019 2:14:39 pm
 */
public class CheckCyleDateJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckCyleDateJob.class);

	/**
	 * printAgentService
	 */
	@Autowired
	@Qualifier("printAgentService")
	private PrintAgentService printAgentService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("CheckCyleDateJob Triggerd");

		BatchJobConfig batchJobConfig = printAgentService.getBatchJobConfigByKey("CheckCyleDateJob");
		if (batchJobConfig != null && batchJobConfig.getStatus().equalsIgnoreCase("ACTIVE")) {
			List<CompanyCode> companyCodes = printAgentService.getActiveCompanyCodeList();
			if (CollectionUtils.isEmpty(companyCodes)) {
				LOGGER.info("NO ACTIVE CONFGIURATIONS TO PROCESS");
			} else {
				for (CompanyCode code : companyCodes) {
					boolean verifyConnectivity = printAgentService.checkConnectivity(code);
					if (verifyConnectivity) {
						String newCycleDate = printAgentService.checkForNewCyle(code);
						if (StringUtils.isEmpty(newCycleDate)) {
							LOGGER.info("LATEST CYCLE IS NOT AVAILABLE FOR {} ", code.getCompanyCode());
						} else {
							boolean readyToDownload = printAgentService.verifyBatchCycleExist(newCycleDate, code);
							if (readyToDownload) {
								LOGGER.info("LATEST CYCLE IS AVAILABLE FOR {} AND IT IS {} ", code.getCompanyCode(),
										newCycleDate);
								code.setLatestCycleDate(newCycleDate);
								printAgentService.triggerNewBatchCycle(code);
							} else {
								LOGGER.info("LATEST CYCLE IS NOT AVAILABLE FOR {} ", code.getCompanyCode());
							}
						}
					} else {
						/**
						 * Trigger Email
						 */
					}
				}
			}
		} else {
			LOGGER.info("JOB IS INACTIVE");
		}
	}

}
