/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.jobs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.repository.TableDmDocRepository;
import com.aia.print.agent.service.PrintAgentService;
import com.aia.print.agent.service.SubSchemaIntegrationService;
import com.aia.print.agent.service.SubSchemaInvoiceIntegarationService;

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

	@Qualifier("subSchemaIntegrationService")
	private SubSchemaIntegrationService subSchemaIntegrationService;

	@Autowired

	@Qualifier("subSchemaInvoiceIntegarationService")
	private SubSchemaInvoiceIntegarationService subSchemaInvoiceIntegarationService;

	@Autowired
	private TableDmDocRepository tableDmDocRepository;
	
/*	@Autowired
	private InvoiceService invoiceService;*/

	/** {@inheritDoc} */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("GenerateTemplate Triggerd");

		try {

			/*List<BatchCycle> batchCycles = printAgentService.getBatchCycles("RECONCILIATION_SUCCESS");
			if (CollectionUtils.isEmpty(batchCycles)) {
				LOGGER.info("THERE ARE NO BATCH CYCLES WITH RECONCILIATION_SUCCESS STATUS");
			} else {
				for (BatchCycle batchCycle : batchCycles) {

					CompanyCode companyCode = printAgentService.getCompanyCode(batchCycle.getCompanyCode());
					Path folderPath = new File(companyCode.getLocalFolderPath().concat(batchCycle.getCycleDate())).toPath();
					List<Path> atrackFileNames = Files.list(folderPath).collect(Collectors.toList());
					for (Path filePath : atrackFileNames) {
						if (filePath.toFile().getName().contains("PAFINV")) {
						this.invoiceService.generatePdf(filePath.toFile().getAbsolutePath(), batchCycle);
						
						}else if(filePath.toFile().getName().contains("PAFCN")) {
							
						}
						
					}
				}
			}
*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	
	
}
