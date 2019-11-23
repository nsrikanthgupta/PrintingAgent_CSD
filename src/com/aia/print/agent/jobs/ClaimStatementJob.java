package com.aia.print.agent.jobs;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.aia.ahs.aso.model.SympNotification;
import com.aia.ahs.aso.service.ClaimStatementService;

public class ClaimStatementJob implements Job {
    @Autowired
    @Qualifier("claimStatementService")
    private ClaimStatementService claimStatementService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimStatementJob.class);

    boolean start = true;

    long count = 0;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("ClaimStatementJob is Triggered..........................................................!");
        List< SympNotification > list = this.claimStatementService.getRequestforDownloadFromSumyfap00021DB();

        /*
         * if (list != null && list.size()!=0 && !list.isEmpty()) { LOGGER.info("Request Size............!  "+list.size());
         * this.claimStatementService.startReportGen(list); }else { LOGGER.info("No Download Request.............!"); }
         */

        // this.claimStatementGmmService.testReportGeneration();

    }

}
