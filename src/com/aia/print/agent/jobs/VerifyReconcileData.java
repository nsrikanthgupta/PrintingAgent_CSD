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
import com.aia.print.agent.entiry.BatchJobConfig;
import com.aia.print.agent.service.BatchReconciliationService;
import com.aia.print.agent.service.PrintAgentService;

/**
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
        BatchJobConfig batchJobConfig = printAgentService.getBatchJobConfigByKey("VerifyReconcileData");
        if (batchJobConfig != null && batchJobConfig.getStatus().equalsIgnoreCase("ACTIVE")) {
            List< BatchCycle > batchCycles = printAgentService.getBatchCycles("TMPL_GENERATION_COMPLETED");
            if (CollectionUtils.isEmpty(batchCycles)) {
                LOGGER.info("THERE ARE NO BATCH CYCLES WITH DOWNLOADED STATUS");
            } else {
                for (BatchCycle batchCycle : batchCycles) {
                    int status = batchReconciliationService.perFormReconciliation(batchCycle);
                    if (status <= 1) {
                        if (status == 1) {
                            batchCycle.setStatus("RECONCILIATION_SUCCESS");
                            /**
                             * Update tbl doc data to success
                             */
                        } else if (status == 0) {
                            batchCycle.setStatus("RECONCILIATION_FAILED");
                            /**
                             * Update tbl doc data to fail
                             */
                        }
                        batchCycle.setUpdatedBy("PrintingAgent_CSD");
                        batchCycle.setUpdatedDate(new Date());
                        printAgentService.updateBatchCycle(batchCycle);
                    }
                }

            }
        } else {
            LOGGER.info("VerifyReconcileData JOB IS NOT ACTIVE");
        }

    }

}
