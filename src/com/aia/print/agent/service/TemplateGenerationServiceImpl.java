/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.repository.BatchFileDetailsRepository;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 23 Nov 2019 10:06:07 am
 */
@Service
public class TemplateGenerationServiceImpl implements TemplateGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateGenerationServiceImpl.class);

    @Autowired
    @Qualifier("asoCreditNoteService")
    private TemplateActions asoCreditNoteService;

    @Autowired
    @Qualifier("asoDebitNoteService")
    private TemplateActions asoDebitNoteService;

    @Autowired
    private BatchFileDetailsRepository batchFileDetailsRepository;

    @Override
    @Async
    public void processFiles(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {
        LOGGER.info("TRIGGERED --- PROCESS STARTS");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("TRIGGERED --- PROCESS ENDS");
    }

    @Override
    public void generateTemplate(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {
        int documentCount = 0;
        batchFileDetails.setDocumentProcessStartDt(new Date());
        if (batchFileDetails.getDocumentCode().equals("ASOCN")) {
            LOGGER.info("Processing the batch file {} ", batchFileDetails.getFileName());
            documentCount = asoCreditNoteService.genReport(companyCode, batchCycle, batchFileDetails);
        } else if (batchFileDetails.getDocumentCode().equals("ASODN")) {
            documentCount = asoDebitNoteService.genReport(companyCode, batchCycle, batchFileDetails);
        }
        if (documentCount != -1) {
            batchFileDetails.setActualDocumentCount(documentCount);
        }
        batchFileDetails.setDocumentProcessEndDt(new Date());
        batchFileDetails.setUpdatedDate(new Date());
        batchFileDetailsRepository.save(batchFileDetails);
    }

}
