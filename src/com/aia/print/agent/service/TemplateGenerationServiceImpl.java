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
	@Qualifier("asoGMEXSummaryReportExxonMobilService")
	private TemplateActions asoGMEXSummaryReportExxonMobilService;
	
	@Autowired
	@Qualifier("asoSummaryBillingStatementService")
	private  TemplateActions asoSummaryBillingStatementService;
	
	@Autowired
	@Qualifier("asoBillingStatementExcellService")
	private  TemplateActions asoBillingStatementExcellService;
	
	@Autowired
	@Qualifier("asoPreEmploymentMedicalScreeningExcellService")
	private TemplateActions asoPreEmploymentMedicalScreeningExcellService;
	
	@Autowired
	@Qualifier("cEBillingStatementExcellService")
	private TemplateActions cEBillingStatementExcellService;
	
	@Autowired
	@Qualifier("claimExcessCreditNoteService")
	private TemplateActions  claimExcessCreditNoteService;
	
	@Autowired
	@Qualifier("claimExcessDebitNoteService")
	private TemplateActions claimExcessDebitNoteService;
	
	@Autowired
	@Qualifier("claimExcessSummaryBillingStatementService")
	private TemplateActions claimExcessSummaryBillingStatementService;
	
	@Autowired
	@Qualifier("branchCostCenterReportService")
	private TemplateActions  branchCostCenterReportService;
	
	@Autowired
	@Qualifier("commisionStatementService")
	private TemplateActions commisionStatementService;
	
	@Autowired
	@Qualifier("creditNoteService")
	private TemplateActions  creditNoteService;
	
	@Autowired
	@Qualifier("debitNoteService")
	private TemplateActions debitNoteService;
	
	@Autowired
	@Qualifier("invoiceExxonMobileService")
	private TemplateActions invoiceExxonMobileService;
	
	@Autowired
	@Qualifier("invoiceService")
	private TemplateActions invoiceService;
	
	@Autowired
	@Qualifier("memberClaimeDetailsListingExcelService")
	private TemplateActions memberClaimeDetailsListingExcelService;
	
	@Autowired
	@Qualifier("memberDetailsExcelService")
	private TemplateActions memberDetailsExcelService;
	
	@Autowired
	@Qualifier("membrDetailsService")
	private TemplateActions membrDetailsService;
	

	@Autowired
	@Qualifier("summaryBillingStatementService")
	private TemplateActions summaryBillingStatementService;
	
	@Autowired
	@Qualifier("officialReceiptService")
	private TemplateActions officialReceiptService; 
	
	@Autowired
	private BatchFileDetailsRepository batchFileDetailsRepository;

	@Override
	@Async
	public void processFiles(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {
		try {
			batchFileDetails.setUpdatedDate(new Date());
			batchFileDetails.setStatus("TMPL_GENERATION_INPROGRESS");
			batchFileDetailsRepository.save(batchFileDetails);
			int documentCount = 0;
			batchFileDetails.setDocumentProcessStartDt(new Date());
			LOGGER.info("Processing the batch file {} ", batchFileDetails.getFileName());
			if (batchFileDetails.getDocumentCode().equals("ASOCN")) {
				documentCount = asoCreditNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			} else if (batchFileDetails.getDocumentCode().equals("ASODN")) {
				documentCount = asoDebitNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			} else if(batchFileDetails.getDocumentCode().equals("GMEXSR")) {
				documentCount=asoGMEXSummaryReportExxonMobilService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("ASOSBS")) {
				documentCount=asoSummaryBillingStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("CECN")) {
				documentCount=claimExcessCreditNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("CEDN")) {
				documentCount=claimExcessDebitNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("CESBS")) {
				documentCount=claimExcessSummaryBillingStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFBCSML")) {
				documentCount=branchCostCenterReportService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFCS")) {
				documentCount=commisionStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFCN")) {
				documentCount=creditNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFDN")) {
				documentCount=debitNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFINVX")) {
				documentCount=invoiceExxonMobileService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFINV")) {
				documentCount=invoiceService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFMD")) {
				documentCount=membrDetailsService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFSBS")) {
				documentCount=summaryBillingStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if (batchFileDetails.getDocumentCode().equals("ASOBSE")) {
				documentCount=asoBillingStatementExcellService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if (batchFileDetails.getDocumentCode().equals("ASOPEMSE")) {
				documentCount=asoPreEmploymentMedicalScreeningExcellService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("CEBSE")) {
				documentCount=cEBillingStatementExcellService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFMCDE")) {
				documentCount=memberClaimeDetailsListingExcelService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFMDE")) {
				documentCount=memberDetailsExcelService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("COROR")) {
				documentCount=officialReceiptService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			
			// call service  summaryBillingStatementService
			LOGGER.info("Document Count {} for  the batch file {} ",documentCount, batchFileDetails.getFileName());
			if (documentCount != -1) {
				batchFileDetails.setActualDocumentCount(documentCount);
			}
			batchFileDetails.setDocumentProcessEndDt(new Date());
			batchFileDetails.setUpdatedDate(new Date());
			batchFileDetails.setStatus("TMPL_GENERATION_COMPLETED");
			batchFileDetailsRepository.save(batchFileDetails);
		} catch (Exception e) {
			e.printStackTrace();
			batchFileDetails.setParseError(e.getMessage());
			batchFileDetails.setUpdatedDate(new Date());
			batchFileDetails.setStatus("TMPL_GENERATION_FAILED");
			batchFileDetailsRepository.save(batchFileDetails);
		}
	}

	
	/**
	 * Larg files 
	 * 
	 * */
	@Override
	public void generateTemplate(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {
		int documentCount = 0;
		batchFileDetails.setUpdatedDate(new Date());
		batchFileDetails.setStatus("TMPL_GENERATION_INPROGRESS");
		batchFileDetailsRepository.save(batchFileDetails);
		LOGGER.info("Processing the batch file {} ", batchFileDetails.getFileName());
		try {
			if (batchFileDetails.getDocumentCode().equals("ASOBSE")) {
				documentCount=asoBillingStatementExcellService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if (batchFileDetails.getDocumentCode().equals("ASOPEMSE")) {
				documentCount=asoPreEmploymentMedicalScreeningExcellService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("CEBSE")) {
				documentCount=cEBillingStatementExcellService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFMCDE")) {
				documentCount=memberClaimeDetailsListingExcelService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFMDE")) {
				documentCount=memberDetailsExcelService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if (batchFileDetails.getDocumentCode().equals("ASOCN")) {
				documentCount = asoCreditNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			} else if (batchFileDetails.getDocumentCode().equals("ASODN")) {
				documentCount = asoDebitNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			} else if(batchFileDetails.getDocumentCode().equals("GMEXSR")) {
				documentCount=asoGMEXSummaryReportExxonMobilService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("ASOSBS")) {
				documentCount=asoSummaryBillingStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("CECN")) {
				documentCount=claimExcessCreditNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("CEDN")) {
				documentCount=claimExcessDebitNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("CESBS")) {
				documentCount=claimExcessSummaryBillingStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFBCSML")) {
				documentCount=branchCostCenterReportService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFCS")) {
				documentCount=commisionStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFCN")) {
				documentCount=creditNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFDN")) {
				documentCount=debitNoteService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFINVX")) {
				documentCount=invoiceExxonMobileService.genReport(companyCode, batchCycle, batchFileDetails);
			}else if(batchFileDetails.getDocumentCode().equals("PAFINV")) {
				documentCount=invoiceService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFMD")) {
				documentCount=membrDetailsService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			else if(batchFileDetails.getDocumentCode().equals("PAFSBS")) {
				documentCount=summaryBillingStatementService.genReport(companyCode, batchCycle, batchFileDetails);
			}
			LOGGER.info("Document Count {} for  the batch file {} ",documentCount, batchFileDetails.getFileName());
			
			//  
			if (documentCount != -1) {
				batchFileDetails.setActualDocumentCount(documentCount);
			}
			batchFileDetails.setDocumentProcessStartDt(new Date());
			batchFileDetails.setDocumentProcessEndDt(new Date());
			batchFileDetails.setUpdatedDate(new Date());
			batchFileDetails.setStatus("TMPL_GENERATION_COMPLETED");
			batchFileDetailsRepository.save(batchFileDetails);
		} catch (Exception e) {
			e.printStackTrace();
			batchFileDetails.setUpdatedDate(new Date());
			batchFileDetails.setStatus("TMPL_GENERATION_FAILED");
			batchFileDetails.setParseError(e.getMessage());
			batchFileDetailsRepository.save(batchFileDetails);
		}
	}

}
