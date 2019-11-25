/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.jobs;

import java.util.ArrayList;
import java.util.Date;
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
import com.aia.print.agent.entiry.BatchJobConfig;
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
		BatchJobConfig batchJobConfig = printAgentService.getBatchJobConfigByKey("GenerateTemplate");
		if (batchJobConfig != null && batchJobConfig.getStatus().equalsIgnoreCase("ACTIVE")) {
			List<String> largeDocuments = new ArrayList<>();
		    largeDocuments.add("ASOBSE"); 
			largeDocuments.add("ASOPEMSE");
			largeDocuments.add("PAFMDE"); 
			largeDocuments.add("CEBSE");  
			largeDocuments.add("PAFMCDE");
			
			
			largeDocuments.add("PAFMD");
			largeDocuments.add("PAFBCSML"); 
			largeDocuments.add("PAFSBS"); 
			largeDocuments.add("PAFINV");
			largeDocuments.add("PAFINVX");
			largeDocuments.add("PAFDN");
			largeDocuments.add("PAFCN");
			largeDocuments.add("PAFCS");
			largeDocuments.add("COROR");
			List<String> smallDocuments = new ArrayList<>();
			smallDocuments.add("ASOCN"); 
			smallDocuments.add("ASODN"); 
			smallDocuments.add("GMEXSR");
			smallDocuments.add("ASOSBS");
			smallDocuments.add("CECN");
			smallDocuments.add("CEDN");
			smallDocuments.add("CESBS");
			
			
			
			try {
				List<BatchCycle> batchCycles = printAgentService.getBatchCycles("DOWNLOADED");
				if (CollectionUtils.isEmpty(batchCycles)) {
					LOGGER.info("THERE ARE NO BATCH CYCLES WITH DOWNLOADED STATUS");
				} else {
					for (BatchCycle batchCycle : batchCycles) {
						batchCycle.setUpdatedDate(new Date());
						batchCycle.setStatus("TMPL_GENERATION_INPROGRESS");
						printAgentService.updateBatchCycle(batchCycle);

						LOGGER.info("BATCH CYCLE {} UPDATED TO TMPL_GENERATION_INPROGRESS ", batchCycle.getCycleDate());

						CompanyCode companyCode = printAgentService.getCompanyCode(batchCycle.getCompanyCodeId());

						LOGGER.info("CompanyCode {} FOR BATCH CYCLE {} ", companyCode.getCompanyCode(),
								batchCycle.getCycleDate());

						List<BatchFileDetails> fileDetails = printAgentService
								.getBatchFileDetails(batchCycle.getBatchId());

						LOGGER.info("BatchFileDetails COUNT {} FOR BATCH CYCLE {}  ", fileDetails.size(),
								batchCycle.getCycleDate());

						/**
						*
						*/
						for (BatchFileDetails batchFileDetails : fileDetails) {
							if (StringUtils.isNoneBlank(batchFileDetails.getDocumentCode())) {
								if (smallDocuments.contains(batchFileDetails.getDocumentCode())) {
									LOGGER.info("Processing {} for Cycle Date {} ", batchFileDetails.getDocumentCode(),
											batchCycle.getCycleDate());
									templateGenerationService.processFiles(companyCode, batchCycle, batchFileDetails);
								}
							}
						}

						/**
						 * 
						 */
						for (BatchFileDetails batchFileDetails : fileDetails) {
							if (StringUtils.isNoneBlank(batchFileDetails.getDocumentCode())) {
								if (largeDocuments.contains(batchFileDetails.getDocumentCode())) {
									templateGenerationService.generateTemplate(companyCode, batchCycle,
											batchFileDetails);
								}
							}
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.info("JOB IS INACTIVE");
		}

	}

}
