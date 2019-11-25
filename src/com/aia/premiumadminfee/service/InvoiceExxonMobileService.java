package com.aia.premiumadminfee.service;

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

import com.aia.common.db.DBCSDCommon;
import com.aia.premiumadminfee.model.DebitTabledata;
import com.aia.premiumadminfee.model.InvoiceExxonMobileTable;
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

/**
 * 
 * 
 * @author ITT0284
 * @DateTime 23 Nov 2019 05:06:07 am
 */

@Service("invoiceExxonMobileService")
public class InvoiceExxonMobileService implements TemplateActions{
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceExxonMobileService.class);

	@Autowired
	private DBCSDCommon dbcmd;

	@Value("${print.agent.fileoutput.path}")
	private String outputPath;

	String jasper = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../jasper/", true);

	String logo = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../img/", true);

	
	SimpleDateFormat sdf=new SimpleDateFormat("YYYY");
	SimpleDateFormat ymd=new SimpleDateFormat("YYYY-MM-dd");

	
	//this templt only for conventional
	private int companyCode=3;
	private static final String docType="PAFINVX";
	private String year;
	private String tableName ;
	private   String tbl_doc_nm;
	private String process_year; 
	
	private String file_format="pdf";
	private Integer dmStatus=1;
	
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
			this.tableName = "tbl_pafinvx_"+this.year;
			this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
			this.process_year=this.year; 
			
  HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> invoiceExxonMobileRSDetails=getAsoInvoiceExxonMobileDetails(batchFileDetails.getFileLocation());
			HashMap<Integer,List<InvoiceExxonMobileTable>> invoiceExxonMobileTableDetails=  getASoInvoiceExxonMobileTableDetails(batchFileDetails.getFileLocation());
			
			int noFiles=invoiceExxonMobileRSDetails.size();
			for(int i=0; i<noFiles;i++){
				HashMap<Integer, HashMap<String, Object>> invoiceExxonMobileRS=invoiceExxonMobileRSDetails.get(i);
				
				HashMap<String, Object> dataSource=new HashMap<String, Object>();
				for(int a=0;a<invoiceExxonMobileRS.size();a++){
				        dataSource.putAll(invoiceExxonMobileRS.get(a));
				}
				dataSource.put("invoicExxonMobilTableList",invoiceExxonMobileTableDetails.get(i));
				
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
	  return documentCount;
	 }
		
		
	public  boolean uploadReport(HashMap<String, Object> dataSource,String companyCode) {
			FileInputStream inputStream=null;
			FileOutputStream outputStream=null;
			try {
			String jrFullReadpath = jasper+"PrintingAgentReports\\premiumandbilling\\conventional\\invoiceExxonMobile\\InvoiceExxonMobile.jasper";
			String pdfname=dataSource.get("policyNum")+"_"+dataSource.get("billNum")+"_ASOIncoiceExxonMobile.pdf";
			String  pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
				    	
				
				
		    	inputStream = new FileInputStream(jrFullReadpath);
				
			   String imgpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"../../img/logo.jpg"; 
			   String logo= FilenameUtils.normalize(imgpath, true); 
			   dataSource.put("logo", logo);
			   JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream,dataSource, new JREmptyDataSource());// for compiled Report .jrxml file
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
					//	System.out.println("directories are created @@@@@@@@.... "+file.getAbsoluteFile());
							
					}
				 
			   
				outputStream = new FileOutputStream(file.getAbsolutePath());
				JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
				System.out.println("PDF Generated..."+file.getAbsolutePath());
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
		
		
		
		public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>  getAsoInvoiceExxonMobileDetails(String filePath){
			
			
			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> invoiceExxonMobileRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
			HashMap<Integer, HashMap<String, Object>> invoiceExxonMobileRS=new HashMap<Integer, HashMap<String, Object>>();
			BufferedReader br = null;
			try {
				 
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
				if (br == null || br.equals("")) {
					System.out.println("No AsoInvoiceExxonMobile file ");
				} else {
					String sCurrentLine;
					int cuurline=0,pdfgencount = 0;

					while ((sCurrentLine = br.readLine()) != null) {
						HashMap<String, Object> invoiceExxonMobile=new HashMap<String, Object>();
						if (cuurline == 0 || sCurrentLine.contains("****")) {
							invoiceExxonMobile = new HashMap<String, Object>();
							invoiceExxonMobileRS = new HashMap<Integer, HashMap<String, Object>>();
							if (sCurrentLine.contains("****")) {
								pdfgencount++;
							}
							cuurline = 0;
						}
						
						String[] data = sCurrentLine.split("\\|");
					
						if (data[0].equalsIgnoreCase("0001")&&data[1].equalsIgnoreCase("1H")) {
							 	invoiceExxonMobile.put("companyName",data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							 	invoiceExxonMobile.put("companyNum",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							 	invoiceExxonMobile.put("addressLine1",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
							 	invoiceExxonMobile.put("addressLine2",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							 	invoiceExxonMobile.put("addressLine3",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							 	invoiceExxonMobile.put("addressLine4",data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							 	invoiceExxonMobile.put("addressLine5",data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							 	invoiceExxonMobile.put("authorisedPerson",data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
							 	invoiceExxonMobile.put("phoneNum",data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
							 
								invoiceExxonMobile.put("portalUploadStatus",data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
							 
								invoiceExxonMobile.put("printHardCp",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");

								invoiceExxonMobile.put("templetType",data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
							
								invoiceExxonMobile.put("policyType",data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
							
							if (data.length >=16) {
								invoiceExxonMobile.put("policyTypeDscr",data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
							}
							if (data.length >=17) {
								invoiceExxonMobile.put("billNum",data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
							}
							if (data.length>=18) {
								invoiceExxonMobile.put("dateOfIssue",data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
							}
							if (data.length >=19) {
								invoiceExxonMobile.put("billingPeriod",data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
							}
							if (data.length >=20 ){
								invoiceExxonMobile.put("bankAcNo",data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
							}
							if (data.length >=21) {
								invoiceExxonMobile.put("bankName", data[20] != null && data[20].length() > 0 ? data[20].trim() : "");
							}
						}
				
						if (data[0].equalsIgnoreCase("0002")&&data[1].equalsIgnoreCase("2H")) {
							if(data.length>=3){
								invoiceExxonMobile.put("policyHolder", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
							}
							if(data.length>=4){
								invoiceExxonMobile.put("policyHolderNum",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							}
							if(data.length>=5){
								invoiceExxonMobile.put("subsidiary",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								
							}
							if(data.length >=6){
								invoiceExxonMobile.put("subsidiaryNum", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							}
							if(data.length >=7 ){
								invoiceExxonMobile.put("policyNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							}
							if(data.length >=8){
								invoiceExxonMobile.put("policyPeriod",data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							}
							if(data.length>=9){
								invoiceExxonMobile.put("poNum",data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							}
						}
						if (data[0].equalsIgnoreCase("0003")&&data[1].equalsIgnoreCase("1T")) { 	 	    
							
								invoiceExxonMobile.put("totalAmtExSt", data[3] != null && data[3].length() > 0 ? data[3].trim(): "");
						
								invoiceExxonMobile.put("totalAmtSt", data[4] != null && data[4].length() > 0 ? data[4].trim(): "");
							
								invoiceExxonMobile.put("totalAmountInclSt", data[5] != null && data[5].length() > 0 ? data[5].trim(): "");
							
						}
					
						 if (data[0].equalsIgnoreCase("0001")||data[0].equalsIgnoreCase("0002")||data[0].equalsIgnoreCase("0003")) {
							 invoiceExxonMobileRS.put(cuurline, invoiceExxonMobile);
						     cuurline++;
						     invoiceExxonMobileRSDetails.put(pdfgencount, invoiceExxonMobileRS);
					}
					}
				}
				
			}catch(Exception e) {
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
			return invoiceExxonMobileRSDetails;
		}
		public  HashMap<Integer, List<InvoiceExxonMobileTable>> getASoInvoiceExxonMobileTableDetails(String filePath) {
	
			HashMap<Integer, List<InvoiceExxonMobileTable>> invoiceExxonMobileTableListDetails = new HashMap<Integer, List<InvoiceExxonMobileTable>>();
		    List<InvoiceExxonMobileTable> invoiceExxonMobileTablList = new ArrayList<InvoiceExxonMobileTable>();

			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
				if (br == null || br.equals("")) {
					System.out.println("No AsoInvoiceExxonMobile file ");
				} else {
					String sCurrentLine;
					int pdfgencount = 0;

					while ((sCurrentLine = br.readLine()) != null) {
						InvoiceExxonMobileTable invoiceExxonMobile=new InvoiceExxonMobileTable();
						if (sCurrentLine.contains("****")) {
							invoiceExxonMobile=new InvoiceExxonMobileTable();
							invoiceExxonMobileTablList = new ArrayList<InvoiceExxonMobileTable>();
								pdfgencount++;
							}
						String[] data = sCurrentLine.split("\\|");
						if (data[0].equalsIgnoreCase("0002")&&data[1].equalsIgnoreCase("1D")) {
							invoiceExxonMobile.setDsc(data[2] != null && data[2].length() > 0 ? data[2].trim(): "");
							invoiceExxonMobile.setClaimsPaid(data[3] != null && data[3].length() > 0 ? data[3].trim(): "");
							invoiceExxonMobile.setAdminFeeExclSt(data[4] != null && data[4].length() > 0 ? data[4].trim(): "");
							invoiceExxonMobile.setSt(data[5] != null && data[5].length() > 0 ? data[5].trim(): "");
							invoiceExxonMobile.setAmntInclSt(data[6] != null && data[6].length() > 0 ? data[6].trim(): "");
						  }
						 if (data[0].equalsIgnoreCase("0002")&& data[1].equalsIgnoreCase("1D")){
							 invoiceExxonMobileTablList.add(invoiceExxonMobile);
							 invoiceExxonMobileTableListDetails.put(pdfgencount, invoiceExxonMobileTablList);
					 }
						}
					}
				}catch(Exception e) {
					System.out.println("[InvoiceExxonMobileService.getASoInvoiceExxonMobileTableDetails] Exception: "+ e);
					e.printStackTrace();
				}finally {
					try {
						if(br!=null) {
							br.close();
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
		
			return invoiceExxonMobileTableListDetails;	
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