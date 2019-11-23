package com.aia.print.agent.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.aia.ahs.aso.model.ClaimStatementBean;
import com.aia.ahs.aso.model.SympNotification;
import com.aia.ahs.aso.service.ClaimStatementService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;


public class ClaimStatementJob implements Job {
	@Autowired
	@Qualifier("claimStatementService")
	private ClaimStatementService claimStatementService;
	
	

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimStatementJob.class);
	boolean start=true;
	long count=0;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("ClaimStatementJob is Triggered..........................................................!");
			List<SympNotification> list=this.claimStatementService.getRequestforDownloadFromSumyfap00021DB();
			
			/*if (list != null && list.size()!=0 && !list.isEmpty()) {
				LOGGER.info("Request Size............!  "+list.size());
				this.claimStatementService.startReportGen(list);
			}else {
				LOGGER.info("No Download Request.............!");
			}*/
			
			
			//this.claimStatementGmmService.testReportGeneration();
	
			
	 }

}
