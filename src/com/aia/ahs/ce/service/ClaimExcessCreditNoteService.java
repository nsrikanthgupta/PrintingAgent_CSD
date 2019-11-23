package com.aia.ahs.ce.service;

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
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.aia.common.db.DBCSDCommon;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class ClaimExcessCreditNoteService{
	@Autowired
	private DBCSDCommon dbcmd;
	
	@Value("${print.agent.fileoutput.path}")
	private String outputPath;
	
	String jasper = FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../jasper/", true);
	String logo= FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"../../img/", true); 
	
	
	SimpleDateFormat sdf=new SimpleDateFormat("YYYY");
	
	SimpleDateFormat ymd=new SimpleDateFormat("YYYY-MM-dd");
	
    private int companyCode;
	private static final String docType="CECN";
	private String doc_creation_dt;
	private String year;
	private String tableName;
	private String tbl_doc_nm;
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
	private String  indicator;

	private String g4CycleDate;

	

	public synchronized void genReport(String filePath,String companyCode) {
		this.g4CycleDate=getG4CycleDate(filePath).trim();
		this.doc_creation_dt=ymd.format(new Date(this.g4CycleDate)) ;
		this.year =sdf.format(new Date(this.g4CycleDate));
		this.tableName = "tbl_cecn_"+this.year;
		this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
		this.process_year=this.year; 
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> ceCreditNoteRSDetails = getCECreditNoteDetails(filePath);
		int noFiles = ceCreditNoteRSDetails.size();
		for (int i = 0; i < noFiles; i++) {
			
			HashMap<Integer, HashMap<String, Object>> ceCreditNoteRS = ceCreditNoteRSDetails.get(i);
			HashMap<String, Object> dataSource = new HashMap<String, Object>();
			for (int a = 0; a < ceCreditNoteRS.size(); a++) {
				dataSource.putAll(ceCreditNoteRS.get(a));
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
	    	uploadReport(dataSource,companyCode);
			uploadReport(dataSource,companyCode);
		}
	}

	public synchronized void uploadReport(HashMap<String, Object> dataSource,String companyCode) {
		FileInputStream inputStream=null;
		BufferedOutputStream outputStream =null;
		try {
			 String jrFullReadpath="";
			 String pdfname="";
			 String pdfFullOutputPath ="";
				
			if(companyCode.trim().equalsIgnoreCase("Co3")) {
				 this.companyCode=3;
				 jrFullReadpath = jasper+ "PrintingAgentReports\\AHS\\conventional\\CE\\ClaimExcessCreditNote.jasper";
				 String billmonth = "" + dataSource.get("billMonth");
				 String billperiod = billmonth.replace("/", "");
				 pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum") + "_CECN.pdf";
				 pdfFullOutputPath = this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");		 
			}
			if(companyCode.trim().equalsIgnoreCase("Co4")) {
				 this.companyCode=4;
				 jrFullReadpath = jasper+ "PrintingAgentReports\\AHS\\takaful\\CE\\ClaimExcessCreditNote.jasper";
				 String billmonth = ""+dataSource.get("billMonth");
				 String billperiod = billmonth.replace("/", "");
				 pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum") + "_CECN.pdf";
				 pdfFullOutputPath =  this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");		
			}

			inputStream = new FileInputStream(jrFullReadpath);
			String imgpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../img/";
			String image = FilenameUtils.normalize(imgpath, true);
			dataSource.put("logo", image);
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());

			File dir=new File(pdfFullOutputPath);
			 if (!dir.exists()) {
		            if (dir.mkdirs()) {
		                System.out.println("directories are created! "+pdfFullOutputPath);
		            } else {
		            	System.out.println("failed to create directories ! "+pdfFullOutputPath);
		            	
		            }
			 }
			
			 //System.out.println("PDF name ================>:"+pdfname);
				File file=new File(dir.getAbsolutePath()+"/"+pdfname);
				if(!file.exists()) {
					file.createNewFile();
					//System.out.println("directories are created @@@@@@@@.... "+file.getAbsoluteFile());
						
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
				if(inputStream!=null){
					inputStream.close();
				}
				if(outputStream!=null){
					outputStream.flush();
				   outputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getCECreditNoteDetails(String filePath) {
		
	
	
		HashMap<Integer, HashMap<String, Object>> ceCreditNotetRS = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> ceCreditNotetRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		BufferedReader br = null;
		try {
		
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No Claim Excess CreditNote Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					HashMap<String, Object> ceCreditNote = new HashMap<String, Object>();
					if (cuurline == 0 || sCurrentLine.contains("****")) {
						ceCreditNote = new HashMap<String, Object>();
						ceCreditNotetRS = new HashMap<Integer, HashMap<String, Object>>();
						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}
					String[] data = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
							ceCreditNote.put("companyName", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							//ceCreditNote.put("companySurname", data[i] != null && data[i].length() > 0 ? data[i].trim() : "");
							ceCreditNote.put("addressLine1", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							ceCreditNote.put("addressLine2", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
							ceCreditNote.put("addressLine3", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						 	ceCreditNote.put("addressLine4", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						 	ceCreditNote.put("addressLine5", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						 	ceCreditNote.put("billNum", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						 	ceCreditNote.put("dateOfIssue",data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						 if(data.length>=11){
							ceCreditNote.put("billMonth", data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						}
						if(data.length>=12){
							ceCreditNote.put("paymentDueDate",data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						}
						if(data.length>=13){
							ceCreditNote.put("authorisedPerson",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						}
						if(data.length>=14){
							ceCreditNote.put("phoneNum", data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
						}
						if(data.length>=15){
							ceCreditNote.put("portalUploadStatus",data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
						}
						if(data.length>=16){
							ceCreditNote.put("printHardCp",data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
						}
						if(data.length>=17){
							ceCreditNote.put("policyType",data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
						}
						if(data.length>=18){
							ceCreditNote.put("policyTypeDscr",data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
							/*System.out.print("policyTypeDscr  :" );
							System.out.println(data[i] != null && data[i].length() > 0 ? data[i].trim() : "");
							*/
						}
						if(data.length>=19){
							ceCreditNote.put("bankAcNo", data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
							/*System.out.print("bankAcNo  :" );
							System.out.println(data[i] != null && data[i].length() > 0 ? data[i].trim() : "");
							*/	}
						if(data.length>=20){
							ceCreditNote.put("bankName", data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
						}
					}
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("2H")) {
						if(data.length>=3){
							ceCreditNote.put("policyHolder",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						}
						if(data.length>=4){
							ceCreditNote.put("policyHolderNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						}
						if(data.length>=5){
							ceCreditNote.put("subsidiary", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						}
						if(data.length>=6){
							ceCreditNote.put("subsidiaryNum", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						}
						if(data.length>=7){
							ceCreditNote.put("policyNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if(data.length>=8){
							ceCreditNote.put("poNum", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}
						if(data.length>=9){
							ceCreditNote.put("amnt", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						}
					}
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1T")) {
						ceCreditNote.put("totalAmnt", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
					}
				
					if (data[0].equalsIgnoreCase("0001")) {
						ceCreditNotetRS.put(cuurline, ceCreditNote);
						cuurline++;
						ceCreditNotetRSDetails.put(pdfgencount, ceCreditNotetRS);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("[ceCreditNoteService.getCECreditNoteeDetails] Exception: " + e.toString());
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
		return ceCreditNotetRSDetails;
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
		ClaimExcessCreditNoteService sbs = new ClaimExcessCreditNoteService();
	    System.out.println("startedd.....");
	}

}
