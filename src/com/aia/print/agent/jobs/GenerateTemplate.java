/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.PrintAgentService;
import com.aia.print.agent.service.TemplateGenerationService;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 4 Oct 2019 2:22:03 pm
 */
public class GenerateTemplate implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateTemplate.class);

    /**
     * printAgentService
     */
    @Autowired
    @Qualifier("printAgentService")
    private PrintAgentService printAgentService;

    @Autowired
    private TemplateGenerationService templateGenerationService;

    /** {@inheritDoc} */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("GenerateTemplate Triggerd");
        List< String > largeDocuments = new ArrayList<>();
        largeDocuments.add("ASOBSE");
        largeDocuments.add("ASOPEMSE");
        List< String > smallDocuments = new ArrayList<>();
        smallDocuments.add("ASOCN");
        smallDocuments.add("ASODN");
        smallDocuments.add("GMEXSR");
        smallDocuments.add("ASOSBS");
        try {
            List< BatchCycle > batchCycles = printAgentService.getBatchCycles("DOWNLOADED");
            if (CollectionUtils.isEmpty(batchCycles)) {
                LOGGER.info("THERE ARE NO BATCH CYCLES WITH RECONCILIATION_SUCCESS STATUS");
            } else {
                for (BatchCycle batchCycle : batchCycles) {
                    
                    CompanyCode companyCode = printAgentService.getCompanyCode(batchCycle.getCompanyCode());
                    List< BatchFileDetails > fileDetails = printAgentService.getBatchFileDetails(batchCycle.getBatchId());
                    /**
                     * Concurrent Processing Large Files --> Dedicated Thread for each Service
                     */
                    for (BatchFileDetails batchFileDetails : fileDetails) {
                        if (StringUtils.isNoneBlank(batchFileDetails.getDocumentCode())) {
                            if (largeDocuments.contains(batchFileDetails.getDocumentCode())) {
                                templateGenerationService.processFiles(companyCode, batchCycle, batchFileDetails);
                            }
                        }
                    }
                    /**
                     * Sequential Process
                     */
                    for (BatchFileDetails batchFileDetails : fileDetails) {
                        if (StringUtils.isNoneBlank(batchFileDetails.getDocumentCode())) {
                            if (smallDocuments.contains(batchFileDetails.getDocumentCode())) {
                                templateGenerationService.generateTemplate(companyCode, batchCycle, batchFileDetails);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
