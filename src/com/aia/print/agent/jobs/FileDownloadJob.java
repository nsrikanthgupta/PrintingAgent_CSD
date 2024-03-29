/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.jobs;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.BatchJobConfig;
import com.aia.print.agent.service.BatchReconciliationService;
import com.aia.print.agent.service.PrintAgentService;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 4 Oct 2019 2:19:33 pm
 */
public class FileDownloadJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadJob.class);

	/**
	 * printAgentService
	 */

	@Autowired
	@Qualifier("printAgentService")
	private PrintAgentService printAgentService;
	
	/**
	 * reconcilationCode
	 */
	@Value("${print.agent.reconcilation.code}")
    private String reconcilationCode;
	
	/**
     * batchReconciliationService
     */
    @Autowired
    private BatchReconciliationService batchReconciliationService;

	/** {@inheritDoc} */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("FILEDOWNLOADJOB TRIGGERD");
		BatchJobConfig batchJobConfig = printAgentService.getBatchJobConfigByKey("FileDownloadJob");
		if (batchJobConfig != null && batchJobConfig.getStatus().equalsIgnoreCase("ACTIVE")) {
			List<BatchCycle> batchCycles = printAgentService.getBatchCycles("NEW");
			if (CollectionUtils.isEmpty(batchCycles)) {
				LOGGER.info("THERE ARE NO ACTIVE CYCLES ");
			} else {
				LOGGER.info("FOUND BATCH CYCLES {} ", batchCycles.size());
				for (BatchCycle batchCycle : batchCycles) {
					batchCycle.setStatus("DOWNLOAD_INPROGRESS");
					batchCycle.setUpdatedDate(new Date());
					printAgentService.updateBatchCycle(batchCycle);
					boolean status = printAgentService.downloadBatchCycles(batchCycle);
					if (status) {
						batchCycle.setStatus("DOWNLOADED");
						
						List<BatchFileDetails> batchFileDetails = printAgentService.getBatchFileDetails(batchCycle.getBatchId());
						for(BatchFileDetails batchFile : batchFileDetails) {
						    if (batchFile.getFileName().contains(reconcilationCode)) {
						        batchReconciliationService.processReconcilationFile(batchFile);
			                }
						}
					} else {
						batchCycle.setStatus("DOWNLOAD_FAILED");
						/**
						 * Trigger Email for Failed Scenario
						 */
					}
					batchCycle.setUpdatedDate(new Date());
					printAgentService.updateBatchCycle(batchCycle);
				}
			}

		} else

		{
			LOGGER.info("FileDownloadJob is INACTIVE ");
		}
	}
}
