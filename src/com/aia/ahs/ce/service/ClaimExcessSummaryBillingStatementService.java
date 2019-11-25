package com.aia.ahs.ce.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

import com.aia.ahs.ce.model.ClaimExcessAsoSummaryStatementTableData;
import com.aia.common.db.DBCSDCommon;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service("claimExcessSummaryBillingStatementService")
public class ClaimExcessSummaryBillingStatementService implements TemplateActions{
	@Autowired
	private DBCSDCommon dbcmd;
	
	@Value("${print.agent.fileoutput.path}")
	private String outputPath;
	
	String jasper = FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../jasper/", true);
	String logo= FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"../../img/", true); 
	
	
	SimpleDateFormat sdf=new SimpleDateFormat("YYYY");
	
	SimpleDateFormat ymd=new SimpleDateFormat("YYYY-MM-dd");
	
    private int companyCode;
	private static final String docType="CESBS";
	private String year;
	private String tableName;
	private String tbl_doc_nm;
	private String doc_creation_dt;
	
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
	private String indicator;
	
	private String g4CycleDate;

	public int genReport(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {

		int documentCount = 0;
		this.g4CycleDate = batchCycle.getCycleDate();
		this.doc_creation_dt=ymd.format(new Date(this.g4CycleDate)) ;
		this.year =sdf.format(new Date(this.g4CycleDate));
		this.tableName = "tbl_cesbs_"+this.year;
		this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
		this.process_year=year; 
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> ceSummaryStatementRSDetails = getCESummaryStatementDetails(batchFileDetails.getFileLocation());
		HashMap<Integer, List<ClaimExcessAsoSummaryStatementTableData>> ceSummaryStatementListDetails = getCESummaryStatementTableData(batchFileDetails.getFileLocation());
		int noFiles = ceSummaryStatementRSDetails.size();
		for (int i = 0; i < noFiles; i++) {
			HashMap<Integer, HashMap<String, Object>> ceSummaryStatementRS = ceSummaryStatementRSDetails.get(i);
			List<ClaimExcessAsoSummaryStatementTableData> ceSummaryStatementList = ceSummaryStatementListDetails.get(i);
			HashMap<String, Object> dataSource = new HashMap<String, Object>();
			for (int a = 0; a < ceSummaryStatementRS.size(); a++) {
				dataSource.putAll(ceSummaryStatementRS.get(a));
			}
			dataSource.put("ceSummaryStatementList", ceSummaryStatementList);

			this.proposalNo=(String) dataSource.get("policyNum");
			this.client_no=(String) dataSource.get("policyHolderNum");
			this.client_name=(String) dataSource.get("policyHolder");
			this.bill_no=(String) dataSource.get("billNum");
			
			this.proposal_type=(String) dataSource.get("policyType");
			this.sub_client_no=(String) dataSource.get("subsidiaryNum");
			this.sub_client_name=(String) dataSource.get("subsidiary");
			if(this.sub_client_name==null || this.sub_client_name.isEmpty() || this.sub_client_name.equalsIgnoreCase("-")){
				this.sub_client_name=this.client_name;
			}
			this.indicator=(String) dataSource.get("printHardCp");
			
			if(this.uploadReport(dataSource, companyCode.getCompanyCode())) {
				++documentCount;
			}

		}
return documentCount;
	}

	public boolean uploadReport(HashMap<String, Object> dataSource, String companyCode) {
		
		FileInputStream inputStream=null;
		BufferedOutputStream outputStream=null;
		
		try {
	
			String jrFullReadpath="";
			 String pdfname="";
			 String pdfFullOutputPath ="";
				
			if(companyCode.trim().equalsIgnoreCase("Co3")) {
				 this.companyCode=3;
				 jrFullReadpath = this.jasper+"PrintingAgentReports\\AHS\\conventional\\CE\\ClaimExcessSummaryStatement.jasper";
				 	String billmonth = "" + dataSource.get("billMonth");
					String billperiod = billmonth.replace("/", "").replace(" ", "");
					pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum")+ "_CESummaryStmt.pdf";
					pdfFullOutputPath =  this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");			 
			}
			else if(companyCode.trim().equalsIgnoreCase("Co4")) {
				 this.companyCode=4;
				 jrFullReadpath = this.jasper+ "PrintingAgentReports\\AHS\\takaful\\CE\\ClaimExcessSummaryStatement.jasper";
				 String billmonth = "" + dataSource.get("billMonth");
				 String billperiod = billmonth.replace("/", "");
				 pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum")+ "_CESummaryStmt.pdf";
				 pdfFullOutputPath =  this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");			 
			}
			
			
			 inputStream = new FileInputStream(jrFullReadpath);
		
			String imgpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../img/";
			String image = FilenameUtils.normalize(imgpath, true);
			dataSource.put("logo", image);

			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());// 
			 File dir=new File(pdfFullOutputPath);
			 if (!dir.exists()) {
		            if (dir.mkdirs()) {
		                System.out.println("directories are created! "+pdfFullOutputPath);
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
			
			System.out.println("==> PDF Generated..."+file.getAbsolutePath());
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
			e.printStackTrace();
			return false;
		} finally {
			try {
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

	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}

	HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getCESummaryStatementDetails(String filePath) {
		
		HashMap<Integer, HashMap<String, Object>> ceSummaryStatementtRS = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> ceSummaryStatementtRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No AHS conventional  Claim Excess SummaryStatement Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {

					HashMap<String, Object> ceSummaryStatement = new HashMap<String, Object>();

					if (cuurline == 0 || sCurrentLine.contains("****")) {
						ceSummaryStatement = new HashMap<String, Object>();
						ceSummaryStatementtRS = new HashMap<Integer, HashMap<String, Object>>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}
					String[] data = sCurrentLine.split("\\|");
				
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
								ceSummaryStatement.put("policyHolder",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
								ceSummaryStatement.put("policyHolderNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
								
								ceSummaryStatement.put("subsidiary",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								ceSummaryStatement.put("subsidiaryNum", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							
								ceSummaryStatement.put("policyNum",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							
								ceSummaryStatement.put("billNum",data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							
								ceSummaryStatement.put("dateOfIssue",data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							
								ceSummaryStatement.put("billMonth",data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
							
								ceSummaryStatement.put("authorisedPerson",data[10] != null && data[10].length() > 0 ? data[10].trim() : "");

								ceSummaryStatement.put("phoneNum", data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
							
								ceSummaryStatement.put("portalUploadStatus",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
							
								ceSummaryStatement.put("printHardCp",data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
							
							   if (data.length>=15) {
								ceSummaryStatement.put("policyType",data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
							   }
							   if (data.length>=16) {
								ceSummaryStatement.put("policyTypeDscr",data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
							   }
							  if (data.length>=17) {
								  ceSummaryStatement.put("deptercode",data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
							   }
						}

						if (data[0].equalsIgnoreCase("0001")&&data[1].equalsIgnoreCase("1T")) {
							 if (data.length>=3) {
							ceSummaryStatement.put("totalAmnt",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							 }
						 }
					
					if (data[0].equalsIgnoreCase("0001")) {
						ceSummaryStatementtRS.put(cuurline, ceSummaryStatement);
						cuurline++;
						ceSummaryStatementtRSDetails.put(pdfgencount, ceSummaryStatementtRS);
					}
				}

			}

		} catch (Exception e) {
			System.out.println(
					"[ClaimExcessSummaryBillingStatementService.getCESummaryStatementeDetails] Exception: " + e.toString());
			e.printStackTrace();
		}
		return ceSummaryStatementtRSDetails;
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
				
				e.printStackTrace();
			}
		}

		return cycledate;
	}
	
	public HashMap<Integer, List<ClaimExcessAsoSummaryStatementTableData>> getCESummaryStatementTableData(String filePath) {
	
		
		List<ClaimExcessAsoSummaryStatementTableData> ceSummaryStatementList = new ArrayList<ClaimExcessAsoSummaryStatementTableData>();
		HashMap<Integer, List<ClaimExcessAsoSummaryStatementTableData>> ceSummaryStatementListDetails = new HashMap<Integer, List<ClaimExcessAsoSummaryStatementTableData>>();
		 BufferedReader br = null;
		try {
			
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			
			if (br == null || br.equals("")) {
				System.out.println("No CESummaryBillingStatement Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;
				while ((sCurrentLine = br.readLine()) != null) {
					ClaimExcessAsoSummaryStatementTableData ceSummaryStatement = new ClaimExcessAsoSummaryStatementTableData();

					if (sCurrentLine.contains("****")) {
						ceSummaryStatement = new ClaimExcessAsoSummaryStatementTableData();
						ceSummaryStatementList = new ArrayList<ClaimExcessAsoSummaryStatementTableData>();
						pdfgencount++;
					}
					String data[]=sCurrentLine.split("\\|");
					
					 if (data[0].equalsIgnoreCase("0001")&& data[1].equalsIgnoreCase("1D")){

						 if (data.length>=3) {
								ceSummaryStatement.setClaimNum(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							}
							if (data.length>=4) {
								ceSummaryStatement.setEmpName(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							}
							if (data.length>=5) {
								ceSummaryStatement.setEmpNricPassportNum(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
									}
							if (data.length>=6) {
								ceSummaryStatement.setClaimantName(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							}
							if (data.length>=7) {
								ceSummaryStatement.setRelationship(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							}
							if (data.length>=8) {
								ceSummaryStatement.setVisitDate(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							}
							if (data.length>=9) {
								ceSummaryStatement.setClaimExcess(data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							} 
				      }
					 if (data[0].equalsIgnoreCase("0001")&& data[1].equalsIgnoreCase("1D")){
						 ceSummaryStatementList.add(ceSummaryStatement);
					 }
					 ceSummaryStatementListDetails.put(pdfgencount, ceSummaryStatementList);
				}
				
			} 
			
		} catch (Exception e) {
			System.out.println("[ClaimExcessSummaryBillingStatementService.getCESummaryStatementTableData] Exception: " + e.toString());
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
		
		
		return ceSummaryStatementListDetails;
	}


	public static void main(String args[]) {
		ClaimExcessSummaryBillingStatementService sbs = new ClaimExcessSummaryBillingStatementService();

		System.out.println("startedd.....");
	}

}
