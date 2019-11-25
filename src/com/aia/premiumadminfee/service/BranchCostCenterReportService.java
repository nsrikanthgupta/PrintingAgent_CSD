package com.aia.premiumadminfee.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.ahs.aso.service.AsoCreditNoteService;
import com.aia.common.db.DBCSDCommon;
import com.aia.premiumadminfee.model.BranchCostCenter;
import com.aia.premiumadminfee.model.BranchCostCenterGrandTotalAmnt;
import com.aia.premiumadminfee.model.BranchCostCenterSubTotalAmnt;
import com.aia.premiumadminfee.model.BranchCstCenterTbleData;
import com.aia.premiumadminfee.model.InvoiceTabledata;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Service("branchCostCenterReportService")
public class BranchCostCenterReportService implements TemplateActions{
	private static final Logger LOGGER = LoggerFactory.getLogger(BranchCostCenterReportService.class);

	@Autowired
	private DBCSDCommon dbcmd;

	@Value("${print.agent.fileoutput.path}")
	private String outputPath;

	String jasper = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../jasper/", true);

	String logo = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../img/", true);
	
	SimpleDateFormat sdf=new SimpleDateFormat("YYYY");
	String year;
	SimpleDateFormat ymd=new SimpleDateFormat("YYYY-MM-dd"); 
	private int companyCode;
	private static final String docType="PAFBCSML";
	private String tableName;
	private String tbl_doc_nm;
	private String process_year; 
	
	private String file_format="pdf";
	private Integer dmStatus=1;
	private String doc_creation_dt;
	
	private String proposalNo; //policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type="";
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;
	private String indicator;
	
	private String g4CycleDate;

	@Override
	public int genReport(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {

		int documentCount = 0;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			this.g4CycleDate = batchCycle.getCycleDate();
			this.doc_creation_dt = ymd.format(dateFormat.parse(this.g4CycleDate));
			this.year = sdf.format(dateFormat.parse(this.g4CycleDate));
			this.tableName = "tbl_pafbcsml_"+this.year;
			this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
			this.process_year=this.year; 
			
			HashMap<Integer,List<BranchCostCenter>> branchCstCenterListDetails=	getBranchCstCenterTbleData(batchFileDetails.getFileLocation());
			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> branchCstCenterdataRS=getBranchCstCenterReportDetails(batchFileDetails.getFileLocation());
			int noFiles=branchCstCenterdataRS.size();
			for(int i=0;i<noFiles;i++) {
				HashMap<String, Object> dataSource=new HashMap<String, Object>();
				
				List<BranchCostCenter> branchCstCenterTbleDataList=branchCstCenterListDetails.get(i);
			    HashMap<Integer, HashMap<String, Object>> branchCstCenterMaindata=branchCstCenterdataRS.get(i);
				for (int a = 0; a <branchCstCenterMaindata.size(); a++) {
					dataSource.putAll(branchCstCenterMaindata.get(a));
				}
				
				this.proposalNo=(String) dataSource.get("policyNum");
				this.client_no=(String) dataSource.get("policyHolderNum");
				this.client_name=(String) dataSource.get("policyHolder");
				this.bill_no=(String) dataSource.get("billNum");
				
				this.proposal_type=(String) dataSource.get("policyType");
				this.sub_client_no=(String) dataSource.get("subsidiaryNum");
				this.sub_client_name=(String) dataSource.get("subsidiary");
				if(this.sub_client_name.equalsIgnoreCase("-")  || this.sub_client_name.isEmpty() ||this.sub_client_name==null){
					this.sub_client_name=this.client_name;
				}
				this.indicator=(String) dataSource.get("printHardCp");
				
				
				dataSource.put("listBranchCostCenter", branchCstCenterTbleDataList);
				if(this.uploadReport(dataSource,companyCode.getCompanyCode())) {
					++documentCount;
				}
				
				}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return documentCount;
	}
	
	
	public  boolean uploadReport(HashMap<String, Object> dataSource,String companyCode) {
		FileInputStream mainreportinputStream=null;
		FileOutputStream outputStream =null;
		try {
			  String pdfFullOutputPath="";
				String pdfname="";
				String jrMainReportFullReadpath=null;
				String jrSubReportFullReadpath =null;
				if(companyCode.trim().equalsIgnoreCase("Co3")) {
					this.companyCode=3;
					if(((String) dataSource.get("templetType")).equalsIgnoreCase("GI")){
						 jrMainReportFullReadpath = jasper+"PrintingAgentReports\\premiumandbilling\\generalinsurence\\branchcostCenter\\BranchCostCenter.jasper";
					     jrSubReportFullReadpath = jasper+"PrintingAgentReports\\premiumandbilling\\generalinsurence\\branchcostCenter\\BranchCostCenterSubReport.jasper";
					     pdfname=dataSource.get("policyNum")+"_"+dataSource.get("billNum")+"_BrhCostCentre.pdf";
					     pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
					}else if(((String) dataSource.get("templetType")).equalsIgnoreCase("CO")){
						jrMainReportFullReadpath = jasper+"PrintingAgentReports\\premiumandbilling\\conventional\\branchcostCenter\\BranchCostCenter.jasper";
						jrSubReportFullReadpath = jasper+"PrintingAgentReports\\premiumandbilling\\conventional\\branchcostCenter\\BranchCostCenterSubReport.jasper";
						pdfname=dataSource.get("policyNum")+"_"+dataSource.get("billNum")+"_BrhCostCentre.pdf";
						pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
					}
					
				}
				if(companyCode.trim().equalsIgnoreCase("Co4")){
					this.companyCode=4;
						jrMainReportFullReadpath = jasper+"PrintingAgentReports\\premiumandbilling\\takaful\\branchcostCenter\\BranchCostCenter.jasper";
					    jrSubReportFullReadpath = jasper+"PrintingAgentReports\\premiumandbilling\\takaful\\branchcostCenter\\BranchCostCenterSubReport.jasper";
					    pdfname=dataSource.get("policyNum")+"_"+dataSource.get("billNum")+"_BrhCostCentre.pdf";
					    pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
				}
				
				 mainreportinputStream = new FileInputStream(jrMainReportFullReadpath);
	            JasperReport subreport = (JasperReport)JRLoader.loadObjectFromFile(jrSubReportFullReadpath);
	            dataSource.put("branchCostCenterSubReport", subreport);
	            dataSource.put("logo",logo);
			JasperPrint jasperPrint = JasperFillManager.fillReport(mainreportinputStream,dataSource, new JREmptyDataSource());// for compiled Report .jrxml file
			File dir=new File(pdfFullOutputPath);
			 if (!dir.exists()) {
		            if (dir.mkdirs()) {
		                System.out.println("directories are created! "+dir.getAbsolutePath());
		            } else {
		            	System.out.println("failed to create directories ! "+pdfFullOutputPath);
		            	
		            }
			 }
			
				File file=new File(dir.getAbsolutePath()+"/"+pdfname);
				if(!file.exists()) {
					file.createNewFile();
					//System.out.println("directories are created @@@@@@@@.... "+file.getAbsoluteFile());
				}
			 outputStream = new FileOutputStream(file.getAbsolutePath());
			JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
			PDDocument doc = PDDocument.load(new File(file.getAbsolutePath()));
			int page_count = doc.getNumberOfPages();
		
			
			byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	        String dataId = UUID.randomUUID().toString();
			boolean add=dbcmd.checktblDmDoc(this.companyCode, proposalNo, this.doc_creation_dt, docType, bill_no);
			if(add){
				dbcmd.insertIntoDocTypeTable(dataId,fileContent, this.tableName,this.year );
				
				dbcmd.insertIntoTblDmDoc(dataId,docType,proposalNo,process_year,dmStatus,tbl_doc_nm,this.doc_creation_dt,this.companyCode,
						 client_no,client_name,bill_no,bill_type,sub_client_no,sub_client_name,file_format,proposal_type,this.indicator,page_count);

			}
			else{
				dbcmd.insertIntoDocTypeTable(dataId,fileContent, this.tableName,this.year);
			
			    dbcmd.insertIntoTblDmDoc(dataId,docType,proposalNo,process_year,dmStatus,tbl_doc_nm,this.doc_creation_dt,this.companyCode,
					 client_no,client_name,bill_no,bill_type,sub_client_no,sub_client_name,file_format,proposal_type,this.indicator,page_count);
             }
			
			
		} catch (Exception e) {
			System.out.println("Exception occurred : " + e.toString());
			return false;
		}finally {
			 try {
				 if(mainreportinputStream!=null) {
					 mainreportinputStream.close();
				 }
				 if(outputStream!=null) {
					 outputStream.flush();
					 outputStream.close(); 
				 }
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return true;
	}
	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getBranchCstCenterReportDetails(String filename) {
		
		
	
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> branchCstCenterdataRS = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		HashMap<Integer, HashMap<String, Object>> branchCstCenterdata = new HashMap<Integer, HashMap<String, Object>>();
		BufferedReader br = null;
		try {
	
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			if (br == null || br.equals("")) {
				System.out.println("No Branch cost Center Report Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					HashMap<String, Object> branchCstCenter = new HashMap<String, Object>();

					if (cuurline == 0 || sCurrentLine.contains("****")) {
						branchCstCenter = new HashMap<String, Object>();
						branchCstCenterdata = new HashMap<Integer, HashMap<String, Object>>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}
					String[] data = sCurrentLine.split("\\|");
						if(data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
								branchCstCenter.put("policyHolder",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
								branchCstCenter.put("subsidiary",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
								branchCstCenter.put("policyNum",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								branchCstCenter.put("policyPeriod",	data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
								branchCstCenter.put("billNum",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
								branchCstCenter.put("dateOfIssue",data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
								branchCstCenter.put("billingPeriod",data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
								
								String billingFrequecy=data[9] != null && data[9].length() > 0 ? data[9].trim() : "";
								if (billingFrequecy.equalsIgnoreCase("Halfyearly")){
									billingFrequecy="Half Yearly";
								}
								branchCstCenter.put("billingFrequecy",billingFrequecy);
								String adjustmentFrequency=data[10] != null && data[10].length() > 0 ? data[10].trim() : "";
								if (adjustmentFrequency.equalsIgnoreCase("Halfyearly")){
									adjustmentFrequency="Half Yearly";
								}
								branchCstCenter.put("adjustmentFrequency",adjustmentFrequency);
								
								branchCstCenter.put("policyHolderNum", data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
								branchCstCenter.put("subsidiaryNum",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
								branchCstCenter.put("authorisedPerson",data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
								branchCstCenter.put("phoneNum", data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
								branchCstCenter.put("portalUploadStatus",data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
								if(data.length>=17){
								branchCstCenter.put("printHardCp",	data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
								}
								if(data.length>=18){
								branchCstCenter.put("templetType",data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
								}
								if(data.length>=19){
								branchCstCenter.put("policyType", data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
								}
								if(data.length>=20){
								branchCstCenter.put("policyTypeDscr",data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
								}
						}
					if(data[0].equalsIgnoreCase("0001")){
						branchCstCenterdata.put(cuurline, branchCstCenter);
						cuurline++;
						branchCstCenterdataRS.put(pdfgencount, branchCstCenterdata);
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("[BranchCstCenterService.getBranchCstCenterReportDetails] Exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return branchCstCenterdataRS;
	}
	
	
	HashMap<Integer,List<BranchCostCenter>> getBranchCstCenterTbleData(String filename){
		HashMap<Integer,List<BranchCostCenter>> branchCstCenterListDetails = new HashMap<Integer, List<BranchCostCenter>>();
		
		List<BranchCostCenter> branchCostCenterList = new  ArrayList<BranchCostCenter>();
		List<BranchCstCenterTbleData> branchCstCenterTbleDataList = new  ArrayList<BranchCstCenterTbleData>();
		List<BranchCostCenterSubTotalAmnt> branchCostCenterSubTotalAmntList=	new	ArrayList<BranchCostCenterSubTotalAmnt>();
		List<BranchCostCenterGrandTotalAmnt> branchCostCenterGrandTotalAmntList=	new	ArrayList<BranchCostCenterGrandTotalAmnt>();
		
		
		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			if (br == null || br.equals("")) {
				System.out.println("No BranchCostCenter Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					BranchCostCenter branchCostCenter=new BranchCostCenter();
					BranchCstCenterTbleData branchCstCenterTbleData=new BranchCstCenterTbleData();
					BranchCostCenterSubTotalAmnt branchCostCenterSubTotalAmnt=new	BranchCostCenterSubTotalAmnt();
					BranchCostCenterGrandTotalAmnt branchCostCenterGrandTotalAmnt=new BranchCostCenterGrandTotalAmnt();
					//boolean add = false;
					if (sCurrentLine.contains("****")) {
						branchCostCenter=new BranchCostCenter();
						branchCostCenterList = new  ArrayList<BranchCostCenter>();
						pdfgencount++;
					}
					String data[] = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("2H")) {
							 branchCostCenter.setBranchName(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							 branchCstCenterTbleDataList = new  ArrayList<BranchCstCenterTbleData>();
							 branchCostCenterSubTotalAmntList=	new	ArrayList<BranchCostCenterSubTotalAmnt>();
							 branchCostCenterGrandTotalAmntList=	new	ArrayList<BranchCostCenterGrandTotalAmnt>();
					}if(data[0].equalsIgnoreCase("0003") && data[1].equalsIgnoreCase("3D")) {
								branchCstCenterTbleData.setCostCenter(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
								branchCstCenterTbleData.setEmpCount(Long.parseLong(data[3] != null && data[3].length() > 0 ? data[3].trim() : ""));
								branchCstCenterTbleData.setEmpPremium(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								branchCstCenterTbleData.setEmpAdminVitalityFee(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
								branchCstCenterTbleData.setSpouseCount(Long.parseLong(data[6] != null && data[6].length() > 0 ? data[6].trim() : ""));
	                            branchCstCenterTbleData.setSpousePremium(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
	                            branchCstCenterTbleData.setSpouseAdminVitalityFee(data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
	                            branchCstCenterTbleData.setChildCount(Long.parseLong(data[9] != null && data[9].length() > 0 ? data[9].trim() : ""));
	                            branchCstCenterTbleData.setChildPremium(data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
	                            branchCstCenterTbleData.setChildAdminVitalityFee(data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
	                            branchCstCenterTbleData.setTotalCount(Long.parseLong(data[12] != null && data[12].length() > 0 ? data[12].trim() : ""));
	                            branchCstCenterTbleData.setTotalPremium(data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
	                            branchCstCenterTbleData.setTotalAdminVitalityFee(data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
	                            branchCstCenterTbleData.setTotalSt(data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
	                            branchCstCenterTbleData.setTotalPremiumAdminVitalitySt(data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
	                            branchCstCenterTbleDataList.add(branchCstCenterTbleData);
	                         }
					    	 branchCostCenter.setBranchCstCenterTbleData(branchCstCenterTbleDataList);
						     if(data[0].equalsIgnoreCase("0004") && data[1].equalsIgnoreCase("1S")) {
					    		 branchCostCenterSubTotalAmnt.setSubTtlEmpCount(Long.parseLong(data[2] != null&& data[2].length() > 0 ? data[2].trim(): ""));
					    		 branchCostCenterSubTotalAmnt.setSubTtlEmpPremium(data[3] != null&& data[3].length() > 0 ? data[3].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlEmpAdminVitalityFee(data[4] != null&& data[4].length() > 0 ? data[4].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlSpouseCount(Long.parseLong(data[5] != null&& data[5].length() > 0 ? data[5].trim(): ""));
							     branchCostCenterSubTotalAmnt.setSubTtlSpousePremium(data[6] != null&& data[6].length() > 0 ? data[6].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlSpouseAdminVitalityFee(data[7] != null&& data[7].length() > 0 ? data[7].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlChildCount(Long.parseLong(data[8] != null&& data[8].length() > 0 ? data[8].trim(): ""));
					    		 branchCostCenterSubTotalAmnt.setSubTtlChildPremium(data[9] != null&& data[9].length() > 0 ? data[9].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlChildAdminVitalityFee(data[10] != null&& data[10].length() > 0 ? data[10].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlTotalCount(Long.parseLong(data[11] != null&& data[11].length() > 0 ? data[11].trim(): ""));
					    		 branchCostCenterSubTotalAmnt.setSubTtlTotalPremium(data[12] != null&& data[12].length() > 0 ? data[12].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlTotalAdminVitalityFee(data[13] != null&& data[13].length() > 0 ? data[13].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlTotalSt(data[14] != null&& data[14].length() > 0 ? data[14].trim(): "");
					    		 branchCostCenterSubTotalAmnt.setSubTtlTotalPremiumAdminVitalityFee(data[15] != null&& data[15].length() > 0 ? data[15].trim(): "");
					    		 branchCostCenterSubTotalAmntList.add(branchCostCenterSubTotalAmnt);
					    	 }
					    	 branchCostCenter.setBranchCostCenterSubTotalAmnt(branchCostCenterSubTotalAmntList);
					    	 if(data[0].equalsIgnoreCase("0005") && data[1].equalsIgnoreCase("1G")) {
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlEmpCount(Long.parseLong(data[2] != null&& data[2].length() > 0 ? data[2].trim(): ""));
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlEmpPremium(data[3] != null&& data[3].length() > 0 ? data[3].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlEmpAdminVitalityFee(data[4] != null&& data[4].length() > 0 ? data[4].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlSpouseCount(Long.parseLong(data[5] != null&& data[5].length() > 0 ? data[5].trim(): ""));
							     branchCostCenterGrandTotalAmnt.setGrandTtlSpousePremium(data[6] != null&& data[6].length() > 0 ? data[6].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlSpouseAdminVitalityFee(data[7] != null&& data[7].length() > 0 ? data[7].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlChildCount(Long.parseLong(data[8] != null&& data[8].length() > 0 ? data[8].trim(): ""));
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlChildPremium(data[9] != null&& data[9].length() > 0 ? data[9].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlChildAdminVitalityFee(data[10] != null&& data[10].length() > 0 ? data[10].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlTotalCount(Long.parseLong(data[11] != null&& data[11].length() > 0 ? data[11].trim(): ""));
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlTotalPremium(data[12] != null&& data[12].length() > 0 ? data[12].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlTotalAdminVitalityFee(data[13] != null&& data[13].length() > 0 ? data[13].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlTotalSt(data[14] != null&& data[14].length() > 0 ? data[14].trim(): "");
					    		 branchCostCenterGrandTotalAmnt.setGrandTtlTotalPremiumAdminVitalityFee(data[15] != null&& data[15].length() > 0 ? data[15].trim(): "");
					    		 branchCostCenterGrandTotalAmntList.add(branchCostCenterGrandTotalAmnt);
					    	 }
					    	 branchCostCenter.setBranchCostCenterGrandTotalAmnt(branchCostCenterGrandTotalAmntList);
					   if (data[0].equalsIgnoreCase("0002")) {
							branchCostCenterList.add(branchCostCenter);
							branchCstCenterListDetails.put(pdfgencount, branchCostCenterList);
						}
				}
			}
                     
		} catch (Exception e) {
			System.out.println(
					"[BranchCostCenterReportService.getBranchCstCenterTbleData()] Exception: " + e.toString());
			e.printStackTrace();
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return branchCstCenterListDetails;
	}
	
	public String getG4CycleDate(String filePath){
		String cycledate="";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			
			if (br == null || br.equals(null)) {
				System.out.println("No MemberClaimList Flat File....");
			 }else{
				 String sCurrentline;
					while ((sCurrentline = br.readLine()) != null) {
						
						String data[] = sCurrentline.split("\\|");
						if (data[0].equalsIgnoreCase("0000")) {
							if (data.length >= 3) {
								cycledate=data[2] != null && data[2].length() > 0 ? data[2].trim() : "";
							}
						}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(br!=null){
					br.close();
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return cycledate;
	}
	
	
}
