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
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 24 Nov 2019 1:12:14 pm
 */
public class VerifyTemplateGeneration implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyTemplateGeneration.class);

    @Autowired
    @Qualifier("printAgentService")
    private PrintAgentService printAgentService;

    /**
     * batchReconciliationService
     */
    @Autowired
    @Qualifier("batchReconciliationService")
    private BatchReconciliationService batchReconciliationService;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        LOGGER.debug("VerifyTemplateGeneration Triggered");
        BatchJobConfig batchJobConfig = printAgentService.getBatchJobConfigByKey("VerifyTemplateGeneration");
        if (batchJobConfig != null && batchJobConfig.getStatus().equalsIgnoreCase("ACTIVE")) {
            List< BatchCycle > batchCycles = printAgentService.getBatchCycles("TMPL_GENERATION_INPROGRESS");
            if (CollectionUtils.isEmpty(batchCycles)) {
                LOGGER.info("THERE ARE NO BATCH CYCLES WITH DOWNLOADED STATUS");
            } else {
                for (BatchCycle batchCycle : batchCycles) {
                	LOGGER.info("found batch cycle {} ", batchCycle.getCycleDate());
                    int status = batchReconciliationService.verifyTemplateGeneration(batchCycle);
                    if (status == 1) {
                        batchCycle.setUpdatedDate(new Date());
                        batchCycle.setUpdatedBy("PrintingAgent_CSD");
                        batchCycle.setStatus("TMPL_GENERATION_COMPLETED");
                        printAgentService.updateBatchCycle(batchCycle);
                    } else  if (status == 0) {
                    	batchCycle.setUpdatedDate(new Date());
                        batchCycle.setUpdatedBy("PrintingAgent_CSD");
                        batchCycle.setStatus("TMPL_GENERATION_FAILED");
                        printAgentService.updateBatchCycle(batchCycle);
                    }
                }
            }
        }
    }

}
