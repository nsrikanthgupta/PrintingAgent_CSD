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
import org.springframework.util.CollectionUtils;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.service.BatchReconciliationService;
import com.aia.print.agent.service.PrintAgentService;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 4 Oct 2019 2:21:51 pm
 */
public class VerifyReconcileData implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyReconcileData.class);

	/**
	 * printAgentService
	 */

	@Autowired

	@Qualifier("printAgentService")
	private PrintAgentService printAgentService;

	/**
	 * batchReconciliationService
	 */
	@Autowired

	@Qualifier("batchReconciliationService")
	private BatchReconciliationService batchReconciliationService;

	/** {@inheritDoc} */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("VerifyReconcileData Triggerd");
		List<BatchCycle> batchCycles = printAgentService.getBatchCycles("DOWNLOADED");
		if (CollectionUtils.isEmpty(batchCycles)) {
			LOGGER.info("THERE ARE NO BATCH CYCLES WITH DOWNLOADED STATUS");
		} else {
            /*
             * for (BatchCycle batchCycle : batchCycles) { int status =
             * batchReconciliationService.perFormReconciliation(batchCycle); if (status == 1) {
             * batchCycle.setStatus("RECONCILIATION_SUCCESS"); } else if (status == 2) {
             * batchCycle.setStatus("RECONCILIATION_PARTIAL_SUCCESS"); } else { batchCycle.setStatus("RECONCILIATION_FAILED"); }
             * batchCycle.setUpdatedBy("SD"); batchCycle.setUpdatedDate(new Date());
             * printAgentService.updateBatchCycle(batchCycle); }
             */
		}

	}

}
