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
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.common.db.DBCSDCommon;
import com.aia.premiumadminfee.model.CommissionStatementTableData;
import com.aia.premiumadminfee.model.CreditTabledata;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Service("commisionStatementService")
public class CommisionStatementService implements TemplateActions{
	private static final Logger LOGGER = LoggerFactory.getLogger(CommisionStatementService.class);

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
	private static final String docType="PAFCS";
	String tableName ;
	private   String tbl_doc_nm;
	private String file_format="pdf";
	private Integer dmStatus=1;
	private String process_year; 
	
	private String proposalNo; //policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type;
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;
	private String doc_creation_dt;
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
			this.tableName = "tbl_pafcs_"+year;
			this.tbl_doc_nm="[aiaIMGdb_CSD_"+year+"]..["+tableName+"]";
			this.process_year=year; 
			
			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> commissionStatementdataRS=getCommissionStatementDetails(batchFileDetails.getFileLocation());
			HashMap<Integer,List<CommissionStatementTableData>> commissionStatementTbleDataListDetails=	getCommissionStatementTableData(batchFileDetails.getFileLocation());
			int noFiles=commissionStatementdataRS.size();

			for(int i=0;i<noFiles;i++) {
				HashMap<Integer, HashMap<String, Object>> commissionStatementdata=commissionStatementdataRS.get(i);
				List<CommissionStatementTableData> commissionStatementTbleDataList=commissionStatementTbleDataListDetails.get(i);
				
				HashMap<String, Object> datasource=new HashMap<String, Object>();
				

				for (int a = 0; a <commissionStatementdata.size(); a++) {
					HashMap<String, Object> details = commissionStatementdata.get(a);
					datasource.putAll(details);
				}
				
				this.proposalNo=(String) datasource.get("policyNum");
				this.client_no=(String) datasource.get("policyHolderNum");
				this.client_name=(String) datasource.get("policyHolder");
				this.bill_no=(String) datasource.get("billNum");
				
				this.proposal_type=(String) datasource.get("policyType");
				this.sub_client_no=(String) datasource.get("subsidiaryNum");
				this.sub_client_name=(String) datasource.get("subsidiary");
				if(this.sub_client_name==null || this.sub_client_name.isEmpty() || this.sub_client_name.equalsIgnoreCase("-")){
					this.sub_client_name=this.client_name;
				}
				this.indicator=(String) datasource.get("printHardCp");
				System.out.println("indicater  : "+this.indicator);
				
				datasource.put("commissionStatementTbleDataList", commissionStatementTbleDataList);
				if(this.uploadReport(datasource,companyCode.getCompanyCode())){
					++documentCount;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return documentCount;
	}
	
	public  boolean uploadReport(HashMap<String, Object> dataSource,String companyCode) {
		
		 FileInputStream inputStream=null;
		 FileOutputStream outputStream=null;
		 PDDocument doc =null;
		  try {
			 
			 String pdfFullOutputPath=null;
			 String  jrSubReportFullReadpath=null;
			 String jrFullReadpath=null;
			 String pdfname="";
			 if(companyCode.trim().equalsIgnoreCase("Co3")) {
				 this.companyCode=3;
				 if(dataSource.get("templetType") != null && ((String) dataSource.get("templetType")).equalsIgnoreCase("GI")){ 
						jrFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\commissionStatement\\CommStmt.jasper";
						jrSubReportFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\commissionStatement\\commissionStmtSubReport.jasper";
						pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_CommStmt.pdf";
						pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
	 
				 }else{
					 	jrFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\conventional\\commissionStatement\\CommStmt.jasper";
					 	jrSubReportFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\conventional\\commissionStatement\\commissionStmtSubReport.jasper";
					 	pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_CommStmt.pdf";
					 	pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
	
				 }
			 }
			 if(companyCode.trim().equalsIgnoreCase("Co4")) {
				 this.companyCode=4;
					jrFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\takaful\\commissionStatement\\CommStmt.jasper";
					jrSubReportFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\takaful\\commissionStatement\\commissionStmtSubReport.jasper";
					  pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_CommStmt.pdf";
					  pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
				 
			 }
			
			dataSource.put("logo",logo);
	        inputStream = new FileInputStream(jrFullReadpath);
		    JasperReport subreport = (JasperReport)JRLoader.loadObjectFromFile(jrSubReportFullReadpath);
		    dataSource.put("commissionStmtSubReport",subreport);
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
					//System.out.println("directories are created @@@@@@@@.... "+file.getAbsoluteFile());
			  	}
			
			outputStream = new FileOutputStream(file);
			JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
			System.out.println("====>PDF Generated..."+file.getAbsolutePath());
			
			 doc = PDDocument.load(new File(file.getAbsolutePath()));
			int page_count = doc.getNumberOfPages();
			//System.out.println("Pages in PDF====> : "+page_count);
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
			e.printStackTrace();
			return false;
		} finally{
			 try {
				 if(doc!=null ) {
					doc.close(); 
				 }
				 if(inputStream!=null){
						inputStream.close();
					 }
				 if(outputStream!=null){
					 outputStream.flush();
					 outputStream.close();
				 }
				 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	return true;	
	}
	
	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getCommissionStatementDetails(String filePath) {
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> commissionStatementdataRS = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		HashMap<Integer, HashMap<String, Object>> commissionStatementdata = new HashMap<Integer, HashMap<String, Object>>();
		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No CommissionStatement Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					HashMap<String, Object> commissionStatement = new HashMap<String, Object>();
					if (cuurline == 0 || sCurrentLine.contains("****")) {
						commissionStatement = new HashMap<String, Object>();
						commissionStatementdata = new HashMap<Integer, HashMap<String, Object>>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}
					String[] data = sCurrentLine.split("\\|");
					
						if(data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")){
								commissionStatement.put("companyName",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							
								commissionStatement.put("address1",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							
								commissionStatement.put("address2",	data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
							
								commissionStatement.put("address3",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							
								commissionStatement.put("address4",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							
						    	commissionStatement.put("address5",data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						        commissionStatement.put("authorisedPerson", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						 
						    	commissionStatement.put("phoneNum",data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						    
						    	commissionStatement.put("portalUploadStatus",data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						    
						    	commissionStatement.put("printHardCp",data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						    
						    	commissionStatement.put("templetType",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						    	if(data.length>=14){
						    	commissionStatement.put("policyType",data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
						    	}
						    	if(data.length>=15){
						    	commissionStatement.put("policyTypeDscr",data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
						    	}
						    	/*if(data.length>=17){
						    	commissionStatement.put("bankAcNo", data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
						    	}
						    	if(data.length>=18){
						    	commissionStatement.put("bankName", data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
						    	}*/
						}
						if(data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("2H")) {
							
								commissionStatement.put("policyHolder",data[2] != null&& data[2].length() > 0 ? data[2].trim(): "");
							
								commissionStatement.put("subsidiary",data[3] != null&& data[3].length() > 0 ? data[3].trim(): "");
							
								commissionStatement.put("policyNum",data[4] != null&& data[4].length() > 0 ? data[4].trim(): "");
							
								commissionStatement.put("policyPeriod",data[5] != null&& data[5].length() > 0 ? data[5].trim(): "");
							
								commissionStatement.put("billNum",data[6] != null&& data[6].length() > 0 ? data[6].trim(): "");
							
								commissionStatement.put("dateOfIssue",data[7] != null&& data[7].length() > 0 ? data[7].trim(): "");
						
								if(data.length>=9){
								commissionStatement.put("billingPeriod",data[8] != null&& data[8].length() > 0 ? data[8].trim(): "");
								}
								if(data.length>=10){
								commissionStatement.put("policyHolderNum", data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
								}
								if(data.length>=11){
								commissionStatement.put("subsidiaryNum", data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
								}
						}
						if(data[0].equalsIgnoreCase("0003") && data[1].equalsIgnoreCase("1T")) {
								commissionStatement.put("totalAmnt",data[3] != null&& data[3].length() > 0 ? data[3].trim(): "");
						}
						if(data[0].equalsIgnoreCase("0001")||data[0].equalsIgnoreCase("0003")){
							commissionStatementdata.put(cuurline, commissionStatement);
							cuurline++;
							commissionStatementdataRS.put(pdfgencount, commissionStatementdata);
						}
				 }
				}
			}catch (Exception e) {
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
		return commissionStatementdataRS;
	}
	private HashMap<Integer, List<CommissionStatementTableData>> getCommissionStatementTableData(String filePath) {

		List<CommissionStatementTableData> commissionStatementTableDataList = new  ArrayList<CommissionStatementTableData>();
		HashMap<Integer,List<CommissionStatementTableData>> commissionStatementTableDataListListDetails = new HashMap<Integer, List<CommissionStatementTableData>>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No CommissionStatement Flat file ");
			} else {
				String sCurrentLine;
				int  pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					CommissionStatementTableData commissionStatementTbleData=new CommissionStatementTableData();
					if ( sCurrentLine.contains("****")) {
						commissionStatementTbleData=new CommissionStatementTableData();
						commissionStatementTableDataList = new  ArrayList<CommissionStatementTableData>();
							pdfgencount++;
					}
					
					String data[]=sCurrentLine.split("\\|");
						 if(data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("1D")) {
								    commissionStatementTbleData.setProduct(data[2] != null&& data[2].length() > 0 ? data[2].trim(): "");
									commissionStatementTbleData.setDescription(data[3] != null&& data[3].length() > 0 ? data[3].trim(): "");
									commissionStatementTbleData.setCommission(data[4] != null&& data[4].length() > 0 ? data[4].trim(): "");
									commissionStatementTbleData.setAmount(data[5] != null&& data[5].length() > 0 ? data[5].trim(): "");
								
							}
					 if(data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("1D")) {
						 commissionStatementTableDataList.add(commissionStatementTbleData);
							
					}
					 commissionStatementTableDataListListDetails.put(pdfgencount, commissionStatementTableDataList);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return commissionStatementTableDataListListDetails;
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
