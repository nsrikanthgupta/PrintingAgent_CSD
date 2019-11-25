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
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.common.db.DBCSDCommon;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service("claimExcessDebitNoteService")
public class ClaimExcessDebitNoteService implements TemplateActions{

	@Autowired
	private DBCSDCommon dbcmd;
	
	@Value("${print.agent.fileoutput.path}")
	private String outputPath;
	
	String jasper = FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../jasper/", true);
	String logo= FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"../../img/", true); 
	
	
	SimpleDateFormat sdf=new SimpleDateFormat("YYYY");
	
	SimpleDateFormat ymd=new SimpleDateFormat("YYYY-MM-dd");
	
    private int companyCode;
	private static final String docType="CEDN";
	private String doc_creation_dt;
	private String year;
	private String tableName ;
	private String tbl_doc_nm;
	
	private String file_format="pdf";
	private Integer dmStatus=1;
	private String process_year="2019"; 
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

		this.g4CycleDate = batchCycle.getCycleDate();
		this.doc_creation_dt=ymd.format(new Date(this.g4CycleDate)) ;
		this.year =sdf.format(new Date(this.g4CycleDate));
		this.tableName = "tbl_cedn_"+this.year;
		this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
		this.process_year=this.year; 
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> ceDebitNoteRSDetails=getCEDebitNoteDetails(batchFileDetails.getFileLocation());
		int documentCount = 0;	
		int noFiles=ceDebitNoteRSDetails.size();
		for (int i = 0; i < noFiles; i++) {

			HashMap<Integer, HashMap<String, Object>> ceDebitNoteRS = ceDebitNoteRSDetails.get(i);
			HashMap<String, Object> dataSource = new HashMap<String, Object>();
			for (int a = 0; a < ceDebitNoteRS.size(); a++) {
				dataSource.putAll(ceDebitNoteRS.get(a));
			}
			
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
			
			
			if(this.uploadReport(dataSource,companyCode.getCompanyCode())) {
				++documentCount;
			}
		}
		return documentCount;
	}
		public  boolean uploadReport(HashMap<String, Object> dataSource,String companyCode) {
			FileInputStream inputStream=null;
			BufferedOutputStream outputStream=null;
			try {
		

			 String jrFullReadpath="";
			 String pdfname="";
			 String pdfFullOutputPath ="";
				
			if(companyCode.trim().equalsIgnoreCase("Co3")) {
				 this.companyCode=3;
				 jrFullReadpath = jasper+ "PrintingAgentReports\\AHS\\conventional\\CE\\ClaimExcessDebitNote.jasper";
				 String billmonth = "" + dataSource.get("billMonth");
				 String billperiod = billmonth.replace("/", "");
				 pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum") + "_CEDN.pdf";
				 pdfFullOutputPath =  this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");			 
			}
			if(companyCode.trim().equalsIgnoreCase("Co4")) {
				 this.companyCode=4;
				 jrFullReadpath = jasper+  "PrintingAgentReports\\AHS\\takaful\\CE\\ClaimExcessDebitNote.jasper";
				 String billmonth = "" + dataSource.get("billMonth");
				 String billperiod = billmonth.replace("/", "");
				 pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum") + "_DEDN.pdf";
				 pdfFullOutputPath =  this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");			 
			}
			
			 
			inputStream = new FileInputStream(jrFullReadpath);
			dataSource.put("logo", logo);

			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());
			File dir=new File(pdfFullOutputPath);
			 if (!dir.exists()) {
		            if (dir.mkdirs()) {
		                System.out.println("directories are created! "+dir.getAbsolutePath());
		            } else {
		            	System.out.println("failed to create directories ! "+pdfFullOutputPath);
		            	
		            }
			 }
			
			 //System.out.println("PDF name ================>:"+pdfname);
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
		
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getCEDebitNoteDetails(String filePath){
		
			
		
			HashMap<Integer, HashMap<String, Object>> ceDebitNotetRS = new HashMap<Integer, HashMap<String, Object>>();
			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> ceDebitNotetRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
			BufferedReader br = null;
			try {
				
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
				if (br == null || br.equals("")) {
					System.out.println("No Claim Excess DebitNote Flat file ");
				} else {
					String sCurrentLine;
					int cuurline = 0, pdfgencount = 0;

					while ((sCurrentLine = br.readLine()) != null) {
						HashMap<String, Object> ceDebitNote = new HashMap<String, Object>();
						if (cuurline == 0 || sCurrentLine.contains("****")) {
							ceDebitNote = new HashMap<String, Object>();
							ceDebitNotetRS = new HashMap<Integer, HashMap<String, Object>>();

							if (sCurrentLine.contains("****")) {
								pdfgencount++;
							}
							cuurline = 0;
						}
						String[] data = sCurrentLine.split("\\|");
							if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
								ceDebitNote.put("companyName", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
								//ceCreditNote.put("companySurname", data[i] != null && data[i].length() > 0 ? data[i].trim() : "");
								ceDebitNote.put("addressLine1", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
								ceDebitNote.put("addressLine2", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								ceDebitNote.put("addressLine3", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
								ceDebitNote.put("addressLine4", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							 	ceDebitNote.put("addressLine5", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							 	ceDebitNote.put("billNum", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							 	ceDebitNote.put("dateOfIssue",data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
							 if(data.length>=11){
								 ceDebitNote.put("billMonth", data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
							}
							if(data.length>=12){
								ceDebitNote.put("paymentDueDate",data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
							}
							if(data.length>=13){
								ceDebitNote.put("authorisedPerson",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
							}
							if(data.length>=14){
								ceDebitNote.put("phoneNum", data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
							}
							if(data.length>=15){
								ceDebitNote.put("portalUploadStatus",data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
							}
							if(data.length>=16){
								ceDebitNote.put("printHardCp",data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
							}
							if(data.length>=17){
								ceDebitNote.put("policyType",data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
							}
							if(data.length>=18){
								ceDebitNote.put("policyTypeDscr",data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
								/*System.out.print("policyTypeDscr  :" );
								System.out.println(data[i] != null && data[i].length() > 0 ? data[i].trim() : "");
								*/
							}
							if(data.length>=19){
								ceDebitNote.put("bankAcNo", data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
								/*System.out.print("bankAcNo  :" );
								System.out.println(data[i] != null && data[i].length() > 0 ? data[i].trim() : "");
								*/	}
							if(data.length>=20){
								ceDebitNote.put("bankName", data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
							}
							
						}
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("2H")) {
								if(data.length>=3){
									ceDebitNote.put("policyHolder",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
								}
								if(data.length>=4){
									ceDebitNote.put("policyHolderNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
								}
								if(data.length>=5){
									ceDebitNote.put("subsidiary", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								}
								if(data.length>=6){
									ceDebitNote.put("subsidiaryNum", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
								}
								if(data.length>=7){
									ceDebitNote.put("policyNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
								}
								if(data.length>=8){
									ceDebitNote.put("poNum", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
								}
								if(data.length>=9){
									ceDebitNote.put("amnt", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
								}
							}
							if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1T")) {
								ceDebitNote.put("totalAmnt", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							}
						
						
						if (data[0].equalsIgnoreCase("0001")) {
							ceDebitNotetRS.put(cuurline, ceDebitNote);
							cuurline++;
							ceDebitNotetRSDetails.put(pdfgencount, ceDebitNotetRS);
							
						}
					}
					
				}

			} catch (Exception e) {
				System.out.println("[ClaimExcessDebitNoteService.getCEDebitNoteDetails] Exception: "
						+ e.toString());
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
			return ceDebitNotetRSDetails;
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
		
		

		public static void main(String args[]) {
			ClaimExcessDebitNoteService sbs = new ClaimExcessDebitNoteService();
		
			System.out.println("startedd.....");
		}

		
		
}
