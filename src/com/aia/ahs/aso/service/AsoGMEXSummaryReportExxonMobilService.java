package com.aia.ahs.aso.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.ahs.aso.model.SummaryReportExxonmobilTable;
import com.aia.common.db.DBCSDCommon;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service 
public class AsoGMEXSummaryReportExxonMobilService {

	@Autowired
	private DBCSDCommon dbcmd;

	@Value("${print.agent.fileoutput.path}")
	private String outputPath;

	private String jasper = FilenameUtils.normalize( 
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../jasper/", true);
	private String logo = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../img/", true);

	SimpleDateFormat sdf = new SimpleDateFormat("YYYY");

	SimpleDateFormat ymd = new SimpleDateFormat("YYYY-MM-dd");

	private static String docType = "GMEXSR";
	private String doc_creation_dt;
	private String year;
	private String tableName;
	private String tbl_doc_nm;
	private String file_format = "pdf";
	private Integer companyCode;
	private Integer dmStatus = 1;
	private String process_year;

	private String proposalNo; // policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type;
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;
	private String medicalcostMonth;
	private String indicator;
	private String g4CycleDate;
	    
	public void genReport(String fileName, String companyCode) {
		this.g4CycleDate=getG4CycleDate(fileName).trim();
		this.doc_creation_dt=ymd.format(new Date(this.g4CycleDate)) ;
		this.year =sdf.format(new Date(this.g4CycleDate));
		this.tableName = "tbl_gmexsr_"+this.year;
		this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
		this.process_year=this.year; 
		
		this.getmedicalcostMonth(fileName);
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> asoCreditNoteRSDetails = getSummaryReportExxonMobilePolicyDetails(fileName);
		HashMap<Integer, List<SummaryReportExxonmobilTable>> summaryReportExxonMobilTableListDetails = getSummaryReportExxonMobileTableDetails(fileName);
		int noFiles = asoCreditNoteRSDetails.size();
		for (int i = 0; i < noFiles; i++) {
			HashMap<Integer, HashMap<String, Object>> asoCreditNoteRS = asoCreditNoteRSDetails.get(i);

			HashMap<String, Object> dataSource = new HashMap<String, Object>();
			for (int a = 0; a < asoCreditNoteRS.size(); a++) {
				dataSource.putAll(asoCreditNoteRS.get(a));
			}
			this.proposalNo=(String) dataSource.get("policyNum");
			this.bill_no=(String) dataSource.get("billNum");
			
			this.proposal_type=(String) dataSource.get("policyType");
			this.sub_client_no=(String) dataSource.get("subsidiaryNum");
			this.sub_client_name=(String) dataSource.get("subsidiary");
			
			if(this.sub_client_name.equalsIgnoreCase("-")  || this.sub_client_name.isEmpty() ||this.sub_client_name==null){
				this.sub_client_name=this.client_name;
			}
			this.indicator=(String) dataSource.get("printHardCp");
			
			dataSource.put("medicalcostMonth", medicalcostMonth);
			dataSource.put("summaryReportExxonmobilTableList", summaryReportExxonMobilTableListDetails.get(i));
			this.uploadReport(dataSource,companyCode);

		}
	}
	public synchronized void uploadReport(HashMap<String, Object> dataSource,String companyCode) {
		 
		
		FileInputStream inputStream=null;
		BufferedOutputStream outputStream=null;
		
		try {
		
			String pdfFullOutputPath="";
			String jrFullReadpath="";
			String pdfname="";
			if(companyCode.equalsIgnoreCase("Co3")){
				this.companyCode=3;
				jrFullReadpath =jasper+"PrintingAgentReports\\AHS\\conventional\\ASO\\AsoSummaryReportExxonMobile.jasper";
			   
		    	pdfname=dataSource.get("policyNum")+"_"+dataSource.get("billNum")+"_GMEXMedicalCost.pdf";
		    	pdfFullOutputPath = this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");
			}
			else if(companyCode.equalsIgnoreCase("Co4")){
				this.companyCode=4;
				jrFullReadpath = jasper+"PrintingAgentReports\\AHS\\takaful\\ASO\\AsoSummaryReportExxonMobile.jasper";
			    pdfname=dataSource.get("policyNum")+"_"+dataSource.get("billNum")+"_GMEXMedicalCost.pdf";
			    pdfFullOutputPath = this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");
			}
			 dataSource.put("logo",this.logo);	
		    inputStream = new FileInputStream(jrFullReadpath);
		    JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream,dataSource, new JREmptyDataSource());// for compiled Report .jrxml file

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
					System.out.println("directories are created @@@@@@@@.... "+file.getAbsoluteFile());
						
			}
			 
			outputStream =new BufferedOutputStream(new FileOutputStream(file));
			JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
			
			//System.out.println("==> PDF Generated..."+file.getAbsolutePath());
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
			System.out.println("Exception occurred : " + e);
		} finally {
			try {
				if(outputStream!=null){
					outputStream.flush();
					outputStream.close();
				}
				if(outputStream!=null){
					outputStream.close();
				}
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	public void getmedicalcostMonth(String filename){
		
		BufferedReader br=null;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			if (br == null || br.equals("")) {
				System.out.println("No AsoSummaryReportExxonMobile file ");
			} else {
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					String[] data = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0000")) {
						if(data.length>=3){
							 String month=data[2] != null && data[2].length() > 0 ? data[2].trim() : "";
							 this.medicalcostMonth=month.substring(3).replace("-", " ");
						}
					 }
					
				}		
			}
		} catch (Exception e) {
			System.out.println("[AsoGMEXSummaryReportExxonMobilService.getCycleDate] Exception: " + e);
		}finally{
			try {
				if(br!=null) {
					br.close();
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getSummaryReportExxonMobilePolicyDetails(String filename) {
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> summaryReportExxonMobileRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		HashMap<Integer, HashMap<String, Object>> summaryReportExxonMobileRS = new HashMap<Integer, HashMap<String, Object>>();
		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			if (br == null || br.equals("")) {
				System.out.println("No AsoSummaryReportExxonMobile file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					HashMap<String, Object> summaryReportExxonMobile = new HashMap<String, Object>();
					if (cuurline == 0 || sCurrentLine.contains("****")) {
						summaryReportExxonMobile = new HashMap<String, Object>();
						summaryReportExxonMobileRS = new HashMap<Integer, HashMap<String, Object>>();
						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}

					String[] data = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
						
							summaryReportExxonMobile.put("policyHolder",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							summaryReportExxonMobile.put("billingPeriod",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							summaryReportExxonMobile.put("billType",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
							summaryReportExxonMobile.put("authorisedPerson",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						if(data.length>=7){
							summaryReportExxonMobile.put("phoneNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if(data.length>=8){
							summaryReportExxonMobile.put("printHardCp",data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}
						if(data.length>=9){
							summaryReportExxonMobile.put("policyType",data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							
						}
						if(data.length>=10){
							summaryReportExxonMobile.put("subsidiaryNum",data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						}
						if(data.length>=11){
							summaryReportExxonMobile.put("policyNum", data[10] != null && data[10].length() > 0 ? data[10].trim(): "");
						}
						if(data.length>=12){
							summaryReportExxonMobile.put("billNum", data[11] != null && data[11].length() > 0 ? data[11].trim(): "");
						}
					}

					if (data[0].equalsIgnoreCase("0003") && data[1].equalsIgnoreCase("1T")) {
						summaryReportExxonMobile.put("totalAmtAsoPaid",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						summaryReportExxonMobile.put("totalAmtAsoExcess",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						summaryReportExxonMobile.put("totalGrandTtl",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0001") || data[0].equalsIgnoreCase("0003")) {
						summaryReportExxonMobileRS.put(cuurline, summaryReportExxonMobile);
						cuurline++;
						summaryReportExxonMobileRSDetails.put(pdfgencount, summaryReportExxonMobileRS);
					}
				
				}
			}

		} catch (Exception e) {
			System.out.println("[AsoGMEXSummaryReportExxonMobilService.getSummaryReportExxonMobilePolicyDetails] Exception: " + e);
		e.printStackTrace();
		}finally{
			try {
				if(br!=null){
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return summaryReportExxonMobileRSDetails;
	}

	public HashMap<Integer, List<SummaryReportExxonmobilTable>> getSummaryReportExxonMobileTableDetails(String fileName) {
	HashMap<Integer, List<SummaryReportExxonmobilTable>> summaryReportExxonmobilTableListDetails = new HashMap<Integer, List<SummaryReportExxonmobilTable>>();
		List<SummaryReportExxonmobilTable> summaryReportExxonmobilTableList = new ArrayList<SummaryReportExxonmobilTable>();

		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			if (br == null || br.equals("")) {
				System.out.println("No AsoSummaryReportExxonMobile file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					SummaryReportExxonmobilTable summaryReportExxonmobilTable = new SummaryReportExxonmobilTable();
					if (sCurrentLine.contains("****")) {
						summaryReportExxonmobilTable = new SummaryReportExxonmobilTable();
						summaryReportExxonmobilTableList = new ArrayList<SummaryReportExxonmobilTable>();
						pdfgencount++;
					}
					String[] data = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("1D")) {
						summaryReportExxonmobilTable.setPolicyNum(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						summaryReportExxonmobilTable.setClientName(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						summaryReportExxonmobilTable.setBillNum(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						summaryReportExxonmobilTable.setAsoPaid(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						summaryReportExxonmobilTable.setAsoExcess(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						summaryReportExxonmobilTable.setGrandTtl(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("1D")) {
						summaryReportExxonmobilTableList.add(summaryReportExxonmobilTable);
						summaryReportExxonmobilTableListDetails.put(pdfgencount, summaryReportExxonmobilTableList);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("[AsoGMEXSummaryReportExxonMobilService.getSummaryReportExxonMobileTableDetails] Exception: "+e);
			e.printStackTrace();
		}finally{
			try {
				if(br!=null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return summaryReportExxonmobilTableListDetails;
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
	
	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}
	
	
}
