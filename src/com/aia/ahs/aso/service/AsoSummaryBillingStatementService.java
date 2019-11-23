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

import com.aia.ahs.aso.model.AsoSummaryBillingStatementTableData;
import com.aia.common.db.DBCSDCommon;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
@Service
public class AsoSummaryBillingStatementService{
	@Autowired
	private DBCSDCommon dbcmd;
	
	@Value("${print.agent.fileoutput.path}")
	private String outputPath;
	
	String jasper = FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../jasper/", true);
	String logo= FilenameUtils.normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"../../img/", true); 
	
	SimpleDateFormat sdf=new SimpleDateFormat("YYYY");
	
	SimpleDateFormat ymd=new SimpleDateFormat("YYYY-MM-dd"); 
	private Integer companyCode;
	private static final String docType="ASOSBS";
	private static String file_format="pdf";
	private String doc_creation_dt;
	private String year;
    private String tableName;
	private	String tbl_doc_nm;
	
	private String process_year; 
	
	private Integer dmStatus=1;
	private String proposalNo; //policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type;
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;
	private String indicator;
	private String  g4CycleDate;
	
	public  void genReport(String filepath,String companyCode){
		this.g4CycleDate=getG4CycleDate(filepath).trim();
		this.doc_creation_dt=ymd.format(new Date(this.g4CycleDate)) ;
		this.year =sdf.format(new Date(this.g4CycleDate));
		this.tableName = "tbl_asosbs_"+this.year;
		this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
		this.process_year=this.year; 
		
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> asoSummaryStatementRSDetails=getAsoSummaryStatementDetails(filepath);
		HashMap<Integer, List<AsoSummaryBillingStatementTableData>> asoSummaryStatementListDetails=getAsoSummaryStatementTableData(filepath);
		int noFiles=asoSummaryStatementRSDetails.size();
		    for(int i=0; i<noFiles;i++){
		    	HashMap<Integer, HashMap<String, Object>> asoSummaryStatementRS=asoSummaryStatementRSDetails.get(i);
		    	List<AsoSummaryBillingStatementTableData> asoSummaryStatementList=asoSummaryStatementListDetails.get(i);
		       HashMap<String, Object> dataSource=new HashMap<String, Object>();
		    	for(int a=0;a<asoSummaryStatementRS.size();a++){
		    	        dataSource.putAll(asoSummaryStatementRS.get(a));
		    	}
		    	
		    	dataSource.put("asoSummaryStatementList", asoSummaryStatementList);
		    
		      	this.proposalNo=(String) dataSource.get("policyNum");
				this.client_no=(String) dataSource.get("policyHolderNum");
				this.client_name=(String) dataSource.get("policyHolder");
				this.bill_no=(String) dataSource.get("billNum");
				
				this.proposal_type=(String) dataSource.get("policyType");
				this.sub_client_no=(String) dataSource.get("subsidiaryNum");
				this.sub_client_name=(String) dataSource.get("subsidiary");
				this.indicator=(String) dataSource.get("printHardCp");
				if(this.sub_client_name.equalsIgnoreCase("-")  || this.sub_client_name.isEmpty() ||this.sub_client_name==null){
					this.sub_client_name=this.client_name;
				}
		    	uploadReport(dataSource,companyCode);
		    	
		    }
		
		}
		
		
		public synchronized  void uploadReport(HashMap<String, Object> dataSource, String companyCode) {
			
			FileInputStream inputStream=null;
			BufferedOutputStream outputStream=null;
			try {

				String pdfFullOutputPath="";
				String jrFullReadpath="";
				String pdfname="";
				if(companyCode.equalsIgnoreCase("Co3")){
					this.companyCode=3;
					jrFullReadpath =jasper+"PrintingAgentReports\\AHS\\conventional\\ASO\\AsoSummaryStatement.jasper";
				    String billmonth=""+dataSource.get("billMonth");
			    	String billperiod=billmonth.replace("/", "");
			    	pdfname=dataSource.get("policyNum")+"_"+billperiod+"_"+dataSource.get("billNum")+"_ASOSummaryStmt.pdf";
			    	pdfFullOutputPath = this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");
			    }
				else if(companyCode.equalsIgnoreCase("Co4")){
					this.companyCode=4;
					jrFullReadpath = jasper+"PrintingAgentReports\\AHS\\takaful\\ASO\\AsoSummaryStatement.jasper";
					
				    String billmonth=""+dataSource.get("billMonth");
			    	String billperiod=billmonth.replace("/", "");
			    	pdfname=dataSource.get("policyNum")+"_"+billperiod+"_"+dataSource.get("billNum")+"_ASOSummaryStmt.pdf";
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
				
				System.out.println("==> PDF Generated..."+file.getAbsolutePath());
				PDDocument doc = PDDocument.load(new File(file.getAbsolutePath()));
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
		}
		
		public static java.sql.Timestamp getCurrentTimeStamp() {
			java.util.Date today = new java.util.Date();
			return new java.sql.Timestamp(today.getTime());

		}
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getAsoSummaryStatementDetails(String filepath){
			
			BufferedReader br = null;
			HashMap<Integer, HashMap<String, Object>> asoSummaryStatementtRS = new HashMap<Integer, HashMap<String, Object>>();
			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> asoSummaryStatementtRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();

			try {
			
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
				if (br == null || br.equals("")) {
					System.out.println("No AsoSummaryStatement Flat file ");
				} else {
					String sCurrentLine;
					int cuurline = 0, pdfgencount = 0;

					while ((sCurrentLine = br.readLine()) != null) {
						
						Boolean add = false;

						HashMap<String, Object> asoSummaryStatement = new HashMap<String, Object>();

						if (cuurline == 0 || sCurrentLine.contains("****")) {
							asoSummaryStatement = new HashMap<String, Object>();
							asoSummaryStatementtRS = new HashMap<Integer, HashMap<String, Object>>();

							if (sCurrentLine.contains("****")) {
								pdfgencount++;
							}
							cuurline = 0;
						}
						String[] data = sCurrentLine.split("\\|");
						

							if (data[0].equalsIgnoreCase("0001")||data[0].equalsIgnoreCase("0004")) {
								add = true;
							} 
							if (data[0].equalsIgnoreCase("0001")&&data[1].equalsIgnoreCase("1H")) {
								
									asoSummaryStatement.put("policyHolder", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
									
							
									asoSummaryStatement.put("policyHolderNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
									
									asoSummaryStatement.put("subsidiary",data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
								
									asoSummaryStatement.put("subsidiaryNum",data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
								
									asoSummaryStatement.put("policyNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
									
								
									asoSummaryStatement.put("billNum", data[7] != null && data[7].length() > 0 ? data[7].trim(): "");
								
									asoSummaryStatement.put("dateOfIssue", data[8] != null && data[8].length() > 0 ? data[8].trim(): "");
								
									asoSummaryStatement.put("billMonth", data[9] != null && data[9].length() > 0 ? data[9].trim(): "");
								
									asoSummaryStatement.put("authorisedPerson",data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
								
								if(data.length>=12){
									asoSummaryStatement.put("phoneNum", data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
								}
								if(data.length>=13){
									asoSummaryStatement.put("portalUploadStatus",data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
								}
								if(data.length>=14){
									asoSummaryStatement.put("printHardCp",data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
								}
								if(data.length>=15){
									asoSummaryStatement.put("policyType",data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
								}
								if(data.length>=16){
									asoSummaryStatement.put("policyTypeDscr",data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
								}
								
								
								
							} 		
							if (data[0].equalsIgnoreCase("0004")) {
								if(data.length>=3){
									asoSummaryStatement.put("grandTttlAmntAsoPaid", data[2] != null && data[2].length() > 0 ? data[2].trim(): "");
								}
								if(data.length>=4){
									asoSummaryStatement.put("grandTttlAmntAsoExcess", data[3] != null && data[3].length() > 0 ? data[3].trim(): "");
								}
								if(data.length>=5){
									asoSummaryStatement.put("grandTttlAmntGst", data[4] != null && data[4].length() > 0 ? data[4].trim(): "");
								}
								if(data.length>=6){
									asoSummaryStatement.put("grandTttlAndTtlAmnt", data[5] != null && data[5].length() > 0 ? data[5].trim(): "");
								}
								
							} 
							
						
						if (add) {
							asoSummaryStatementtRS.put(cuurline, asoSummaryStatement);
							cuurline++;
							asoSummaryStatementtRSDetails.put(pdfgencount, asoSummaryStatementtRS);
							
						}
					}
					
				}

			} catch (Exception e) {
				System.out.println("[AsoSummaryBillingStatementService.getAsoSummaryStatementeDetails] Exception: "+ e.toString());
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
			return asoSummaryStatementtRSDetails;
		}
		
	public static HashMap<Integer, List<AsoSummaryBillingStatementTableData>> getAsoSummaryStatementTableData(String filepath) {
	
		List<AsoSummaryBillingStatementTableData> asoSummaryStatementList = new ArrayList<AsoSummaryBillingStatementTableData>();
		HashMap<Integer, List<AsoSummaryBillingStatementTableData>> asoSummaryStatementListDetails = new HashMap<Integer, List<AsoSummaryBillingStatementTableData>>();
		BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
			
			if (br == null || br.equals("")) {
				System.out.println("No AsoSummaryBillingStatement Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;
				while ((sCurrentLine = br.readLine()) != null) {
					AsoSummaryBillingStatementTableData asoSummaryBillingStatementTableData=new AsoSummaryBillingStatementTableData();
					
					if (sCurrentLine.contains("****")) {
						asoSummaryBillingStatementTableData=new AsoSummaryBillingStatementTableData();
						asoSummaryStatementList = new  ArrayList<AsoSummaryBillingStatementTableData>();
						pdfgencount++;
					}
					String data[]=sCurrentLine.split("\\|");
					
					 if (data[0].equalsIgnoreCase("0001")&& data[1].equalsIgnoreCase("1D")){

							   asoSummaryBillingStatementTableData.setClaimNum(data[2]!= null&& data[2].length() > 0 ?data[2].trim(): "");
								/* System.out.print("ClaimNum ==> ");
								System.out.println(data[2]!= null&& data[2].length() > 0 ?data[2].trim(): "");*/
							
								asoSummaryBillingStatementTableData.setEmpName(data[3]!= null&& data[3].length() > 0 ?data[3].trim(): "");
								asoSummaryBillingStatementTableData.setEmpNricPassportNum(data[4]!= null&& data[4].length() > 0 ?data[4].trim(): "");
								asoSummaryBillingStatementTableData.setClaimantName(data[5]!= null&& data[5].length() > 0 ?data[5].trim(): "");
								asoSummaryBillingStatementTableData.setRelationship(data[6]!= null&& data[6].length() > 0 ?data[6].trim(): "");
								asoSummaryBillingStatementTableData.setVisitDate(data[7]!= null&& data[7].length() > 0 ?data[7].trim(): "");
								asoSummaryBillingStatementTableData.setAsoPaid(data[8]!= null&& data[8].length() > 0 ?data[8].trim(): "");
								asoSummaryBillingStatementTableData.setAsoExcess(data[9]!= null&& data[9].length() > 0 ?data[9].trim(): "");
								if (data.length >=11) {
								asoSummaryBillingStatementTableData.setGst(data[10]!= null&& data[10].length() > 0 ?data[10].trim(): "");
								}
								if (data.length >=12) {
								asoSummaryBillingStatementTableData.setTtlAmnt(data[11]!= null&& data[11].length() > 0 ?data[11].trim(): "");
								}
				      }
					 if (data[0].equalsIgnoreCase("0001")&& data[1].equalsIgnoreCase("1D")){
						 asoSummaryStatementList.add(asoSummaryBillingStatementTableData);
					 }
					asoSummaryStatementListDetails.put(pdfgencount, asoSummaryStatementList);
				}
				
				
				
			}
			
			
		}catch(Exception e){
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
		
		
		return asoSummaryStatementListDetails;
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
			AsoSummaryBillingStatementService sbs = new AsoSummaryBillingStatementService();
			
		}
	
}
