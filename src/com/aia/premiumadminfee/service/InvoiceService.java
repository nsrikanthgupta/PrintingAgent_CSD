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

import com.aia.common.db.DBCSDCommon;

import com.aia.premiumadminfee.model.DebitTabledata;
import com.aia.premiumadminfee.model.InvoiceTabledata;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;

import net.sf.jasperreports.engine.JREmptyDataSource;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service("invoiceService")
public class InvoiceService implements TemplateActions{
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

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
	private static final String docType="PAFINV";
    private String tableName;
	private	String tbl_doc_nm;
	private String process_year=year; 
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
			this.tableName = "[tbl_pafinv_"+this.year+"]";
			this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
			this.process_year=year; 
			
			
			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> ListinvoiceRSdetails = getInvoicDetails(batchFileDetails.getFileLocation());
			HashMap<Integer, List<InvoiceTabledata>> invoiceTabledataListDetails = getInvoicTableData(batchFileDetails.getFileLocation());
			int noFiles = ListinvoiceRSdetails.size();
			for (int i = 0; i < noFiles; i++) {
				
				HashMap<Integer, HashMap<String, Object>> invoiceRS = ListinvoiceRSdetails.get(i);
				
				HashMap<String, Object> dataSource = new HashMap<String, Object>();
				for (int a = 0; a < invoiceRS.size(); a++) {
					HashMap<String, Object> details = invoiceRS.get(a);
					dataSource.putAll(details);
				}
				
				
				
				this.proposalNo=(String) dataSource.get("policyNum");
				this.client_no=(String) dataSource.get("policyHolderNum");
				this.client_name=(String) dataSource.get("policyHolder");
				this.bill_no=(String) dataSource.get("billNum");
				if(invoiceTabledataListDetails.get(i)!=null){
					for(InvoiceTabledata t:invoiceTabledataListDetails.get(i)){
						this.bill_type=t.getBillType();
						}	
				}
				
				this.proposal_type=(String) dataSource.get("policyType");
				this.sub_client_no=(String) dataSource.get("subsidiaryNum");
				this.sub_client_name=(String) dataSource.get("subsidiary");
				if(this.sub_client_name==null || this.sub_client_name.isEmpty() || this.sub_client_name.equalsIgnoreCase("-")){
					this.sub_client_name=this.client_name;
				}
				this.indicator=(String) dataSource.get("printHardCp");
				System.out.println("indicater  : "+this.indicator);
				
				dataSource.put("InvoiceTabledatadatasource", invoiceTabledataListDetails.get(i));
				
				if(this.upLoadReport(dataSource,companyCode.getCompanyCode())) {
					++documentCount;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return documentCount;
	}

	public boolean upLoadReport(HashMap<String, Object> dataSource,String companyCode) {
		
		FileInputStream inputStream=null;
		FileOutputStream outputStream=null;
		try {
			String jrFullReadpath = "";
			String pdfname ="";
			
			String pdfFullOutputPath = "";
			
			if(companyCode.trim().equalsIgnoreCase("Co3")) {
				 this.companyCode=3;
				 if (((String) dataSource.get("templetType")).equalsIgnoreCase("GI")) {
						jrFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\Invoice\\invoice.jasper";
						pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_Invoice.pdf";
						pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
				 
				 } else if(((String) dataSource.get("templetType")).equalsIgnoreCase("CO")) {
						jrFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\conventional\\Invoice\\invoice.jasper";
						pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_Invoice.pdf";
						pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");

						
					}
			}
			if(companyCode.trim().equalsIgnoreCase("Co4")) {
				 this.companyCode=4;
					jrFullReadpath = jasper+ "PrintingAgentReports\\premiumandbilling\\takaful\\Invoice\\invoice.jasper";
					pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_Invoice.pdf";
					pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
			}
			 inputStream = new FileInputStream(jrFullReadpath);
			String imgpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../img/";
			String logo = FilenameUtils.normalize(imgpath, true);
			dataSource.put("logo", logo);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());
			
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
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			System.out.println("==> PDF Generated..." + file.getAbsolutePath());
			
			PDDocument doc = PDDocument.load(new File(file.getAbsolutePath()));
			int page_count = doc.getNumberOfPages();
			
			byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()+"\\"+pdfname));
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
				if(inputStream !=null) {
					inputStream.close();
				}
				if(outputStream !=null) {
					outputStream.flush();
					outputStream.close();
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		return true;
	}
	

	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getInvoicDetails(String fileName) {
		
		HashMap<Integer, HashMap<String, Object>> invoicresult = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listInvoiceDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			if (br == null || br.equals("")) {
				System.out.println("No Invoice Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					HashMap<String, Object> invoice = new HashMap<String, Object>();
					if (cuurline == 0 || sCurrentLine.contains("****")) {
						invoice = new HashMap<String, Object>();
						invoicresult = new HashMap<Integer, HashMap<String, Object>>();
						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}
					String[] data = sCurrentLine.split("\\|");
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")&& data[2].equalsIgnoreCase("01")) {
								invoice.put("companyName",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
								invoice.put("addressLine1",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								invoice.put("addressLine2",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
								invoice.put("addressLine3",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
								invoice.put("addressLine4",data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
								invoice.put("addressLine5",data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
								invoice.put("authorisedPerson",data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
								invoice.put("phoneNum", data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
								invoice.put("portalUploadStatus",data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
								invoice.put("printHardCp",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
								if (data.length >=14) {
								invoice.put("templetType",data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
								}
								if (data.length >=15) {
								invoice.put("policyType",data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
								}
								if (data.length >=16) {
								invoice.put("policyTypeDscr",data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
								}
								if (data.length >=17) {
								invoice.put("bankAcNo", data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
								}
								if (data.length >=18) {
								invoice.put("bankName", data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
								}
						}
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H") && data[2].equalsIgnoreCase("02")) {

							if (data.length >=4) {
								invoice.put("billNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							}
							if (data.length >=5) {
								invoice.put("dateOfIssue",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
							}
							if (data.length >=6) {
								invoice.put("billingPeriod",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							}
							if (data.length >=7) {
								invoice.put("paymentDueDate",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							}
						}
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")&& data[2].equalsIgnoreCase("03")) {
							if (data.length >=4) {
								invoice.put("policyHolder",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							}
							if(data.length >=5){
								invoice.put("policyHolderNum", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
							}
							if (data.length >=6) {
								invoice.put("subsidiary",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							}
							if (data.length >=7) {
								invoice.put("subsidiaryNum",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							}
							if(data.length >=8){
								invoice.put("policyNum", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							}
							if (data.length >=9) {
								invoice.put("policyPeriod",data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							
							}
						}
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")&& data[2].equalsIgnoreCase("04")) {
							if (data.length>=4) {
								invoice.put("poNum",data[3] != null &&data[3].length() > 0 ? data[3].trim() : "");
							}else{
								invoice.put("poNum","");
							}
						}
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1T") && data[2].equalsIgnoreCase("01")) {
							
								invoice.put("totalAmtExSt",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								invoice.put("totalAmtSt",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
								if (data.length>=7) {
								invoice.put("totalAmountInclSt",data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
								}
						}
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1S")&& data[2].equalsIgnoreCase("01")) {
								if (data.length>=4) {
								invoice.put("reasonOfbilling",data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
								}
						}
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1R")&& data[2].equalsIgnoreCase("01")) {
								if (data.length>=4) {
								invoice.put("email", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
								}
						}
					

					if (data[0].equalsIgnoreCase("0001")) {
						invoicresult.put(cuurline, invoice);
						cuurline++;
						listInvoiceDetails.put(pdfgencount, invoicresult);
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(br!=null) {
					br.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return listInvoiceDetails;
	}

	public static HashMap<Integer, List<InvoiceTabledata>> getInvoicTableData(String fileName) {
	
		BufferedReader br = null;
		
		List<InvoiceTabledata> invoiceTabledataList = new ArrayList<InvoiceTabledata>();
		HashMap<Integer, List<InvoiceTabledata>> invoiceTabledataListDetails = new HashMap<Integer, List<InvoiceTabledata>>();
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			if (br == null || br.equals("")) {
				System.out.println("No Invoice Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					InvoiceTabledata invoiceTabledata = new InvoiceTabledata();

					if (sCurrentLine.contains("****")) {
						invoiceTabledata = new InvoiceTabledata();
						invoiceTabledataList = new ArrayList<InvoiceTabledata>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
					}

					String data[] = sCurrentLine.split("\\|");
					for (int i = 0; i < data.length; i++) {
						if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1D")) {
							invoiceTabledata.setDescription(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
							invoiceTabledata.setBillType(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							invoiceTabledata.setAmountExSt(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							invoiceTabledata.setAmountSt(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							invoiceTabledata.setAmountInclSt(data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						}
					}
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1D")) {
						invoiceTabledataList.add(invoiceTabledata);
						invoiceTabledataListDetails.put(pdfgencount, invoiceTabledataList);
					}

				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("[Invoice.getInvoicedetails] Exception: " + e.toString());
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
		return invoiceTabledataListDetails;
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