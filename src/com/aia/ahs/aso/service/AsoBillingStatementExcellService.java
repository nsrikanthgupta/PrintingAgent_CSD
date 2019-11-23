package com.aia.ahs.aso.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.ahs.aso.model.AsoSummaryStatementExcellData;
import com.aia.common.db.DBCSDCommon;


@Service
public class AsoBillingStatementExcellService{
	@Autowired
	private DBCSDCommon dbcmd;
	
	@Value("${print.agent.fileoutput.path}")
	private String outputPath;
	
	SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
	
	SimpleDateFormat ymd = new SimpleDateFormat("YYYY-MM-dd");

	private static final String docType = "ASOBSE";
	private String doc_creation_dt;
	private String year ;
	private String tableName ;
	private String tbl_doc_nm;
	private String process_year;
	private static final String file_format = "xlsx";
	private int companyCode ;

	private Integer dmStatus = 1;
	
	private String proposalNo; // policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type;
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;
	
	private String g4CycleDate;
	

	public void generateExcelReport(String filePath, String company) {
		this.g4CycleDate=getG4CycleDate(filePath).trim();
		this.doc_creation_dt=this.ymd.format(new Date(this.g4CycleDate)) ;
		this.year =this.sdf.format(new Date(this.g4CycleDate));
		this.tableName = "tbl_asobse_"+this.year;
		this.tbl_doc_nm="[aiaIMGdb_CSD_"+this.year+"]..["+this.tableName+"]";
		this.process_year=year; 
		
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listDetailsRS = getHeaderDetails(filePath);
		HashMap<Integer, List<AsoSummaryStatementExcellData>> listAsoSummaryStatementExcellDataRS = getAsoSummaryStatementExcellData(
				filePath);
		int noFiles = listDetailsRS.size();
		for (int i = 0; i < noFiles; i++) {
			HashMap<Integer, HashMap<String, Object>> detailsRS = listDetailsRS.get(i);
			List<AsoSummaryStatementExcellData> listAsoSummaryStatementExcellData = listAsoSummaryStatementExcellDataRS
					.get(i);
			HashMap<String, Object> datasource = new HashMap<String, Object>();
			for (int a = 0; a < detailsRS.size(); a++) {
				HashMap<String, Object> details = detailsRS.get(a);
				datasource.putAll(details);

			}
			this.proposalNo = (String) datasource.get("policyNum");
			this.client_no = (String) datasource.get("policyHolderNum");
			this.client_name = (String) datasource.get("policyHolder");
			this.bill_no = (String) datasource.get("billNum");
			this.proposal_type = (String) datasource.get("policyType");
			this.sub_client_no = (String) datasource.get("subsidiaryNum");
			this.sub_client_name = (String) datasource.get("subsidiary");
			
			if(this.sub_client_name.equalsIgnoreCase("-")  || this.sub_client_name.isEmpty() ||this.sub_client_name==null){
				this.sub_client_name=this.client_name;
			}
			
			String filename = datasource.get("policyNum") + "_" + datasource.get("billNum") + "_BillingStatement.xlsx";
			
			uploadExcelReport(listAsoSummaryStatementExcellData,filename,company);
		}
	}

	private void uploadExcelReport(List<AsoSummaryStatementExcellData> listAsoSummaryStatementExcellData,String filename, String company) {
		
		String[] column = null;
		if (company.equalsIgnoreCase("Co3")) {
			this.companyCode =3; 
			column =new String[] { "Policy Number", "Company Name", "Billing Month[MM/YYYY]", "Bill Number", "Claim No.",
					"Employee Name", "Employee \n NRIC/Passport No.", "Employee ID", "Claimant Name", "Membership No. ",
					"Relationship", "Plan No", "Plan Description", "Product Code", "Product Description", "Branch",
					"Cost Centre", "Visit Date", "Hospital/Clinic/Specialist", "Claim Type", "ASO Paid (RM)",
					"ASO Excess (RM)", "GST (RM)", "Total Amount (RM)", "Reason For Excess (1)", "Excess Amount (1) (RM)",
					"Reason For Excess (2)", "Excess Amount (2) (RM)", "Reason For Excess (3)", "Excess Amount (3) (RM)",
					"Reason For Excess (4)", "Excess Amount (4) (RM)" };
		} else if (company.equalsIgnoreCase("Co4")) {
			this.companyCode =4;
			column = new String[]{ "Certificate Number", "Company Name", "Billing Month[MM/YYYY]", "Bill Number", "Claim No.",
					"Employee Name", "Employee \n NRIC/Passport No.", "Employee ID", "Claimant Name", "Membership No. ",
					"Relationship", "Plan No", "Plan Description", "Product Code", "Product Description", "Branch",
					"Cost Centre", "Visit Date", "Hospital/Clinic/Specialist", "Claim Type", "ASO Paid (RM)",
					"ASO Excess (RM)", "GST (RM)", "Total Amount (RM)", "Reason For Excess (1)", "Excess Amount (1) (RM)",
					"Reason For Excess (2)", "Excess Amount (2) (RM)", "Reason For Excess (3)", "Excess Amount (3) (RM)",
					"Reason For Excess (4)", "Excess Amount (4) (RM)" };
		}
		
		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet("AsoBillingStatement");
		((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		Font headerfont = sheet.getWorkbook().createFont();
		headerfont.setBold(true);
		headerfont.setFontHeightInPoints((short) 9);
		CellStyle headercellStyle = sheet.getWorkbook().createCellStyle();
		headercellStyle.setWrapText(true);
		headercellStyle.setFont(headerfont);
		// cellStyle.setAlignment(HorizontalAlignment.CENTER);
		headercellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		Row hedderRow = sheet.createRow(0);
		hedderRow.setHeight((short) (1.5 * sheet.getDefaultRowHeight()));
		for (int i = 0; i < column.length; i++) {
			Cell cell = hedderRow.createCell(i);
			cell.setCellValue(column[i]);
			cell.setCellStyle(headercellStyle);
			// System.out.println(i+". "+column[i]);
		}
		DataFormat format = workbook.createDataFormat();
		Font font = sheet.getWorkbook().createFont();
		font.setFontHeightInPoints((short) 9);
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyle.setFont(font);

		CellStyle curencyCellStyle = sheet.getWorkbook().createCellStyle();
		curencyCellStyle.setWrapText(true);
		curencyCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		curencyCellStyle.setFont(font);
		curencyCellStyle.setDataFormat(format.getFormat("#,##0.00"));

		int rowCount = 1;
		for (AsoSummaryStatementExcellData data : listAsoSummaryStatementExcellData) {
			Row row = sheet.createRow(rowCount++);
			row.setHeight((short) (1 * sheet.getDefaultRowHeight()));
			createDataRow(data, row, cellStyle, curencyCellStyle);
		}

		for (int i = 0; i < column.length; i++) {

			sheet.autoSizeColumn(i);
		}

		BufferedOutputStream outputStream = null;
		try {
			String excelOutputPath=this.outputPath+"/"+company+"/"+this.doc_creation_dt.replace("-","");
		
			File dir=new File(excelOutputPath);
			 if (!dir.exists()) {
		            if (dir.mkdirs()) {
		                System.out.println("directories are created! "+dir.getAbsolutePath());
		            } else {
		            	System.out.println("failed to create directories ! "+excelOutputPath);
		            }
			 }
			
				File file=new File(dir.getAbsolutePath()+"/"+filename);
				if(!file.exists()) {
					file.createNewFile();
					System.out.println("directories are created @@@@@@@@.... "+file.getAbsoluteFile());
						
				}
			 
			outputStream = new BufferedOutputStream(new FileOutputStream(file));;
			workbook.write(outputStream);
			System.out.println("excel created .... :" + file.getAbsolutePath());

			byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

			String dataId = UUID.randomUUID().toString();
			boolean add=dbcmd.checktblDmDoc(this.companyCode, proposalNo, this.doc_creation_dt, docType, bill_no);
			if(add){
				dbcmd.insertIntoDocTypeTable(dataId,fileContent, this.tableName,this.year );
				
				dbcmd.insertIntoTblDmDoc(dataId,docType,proposalNo,process_year,dmStatus,tbl_doc_nm,this.doc_creation_dt,this.companyCode,
						 client_no,client_name,bill_no,bill_type,sub_client_no,sub_client_name,file_format,proposal_type,null,0);

			}
			else{
				dbcmd.insertIntoDocTypeTable(dataId,fileContent, this.tableName,this.year);
			
			    dbcmd.insertIntoTblDmDoc(dataId,docType,proposalNo,process_year,dmStatus,tbl_doc_nm,this.doc_creation_dt,this.companyCode,
					 client_no,client_name,bill_no,bill_type,sub_client_no,sub_client_name,file_format,proposal_type,null,0);
             }

		} catch (Exception e) {
			System.out.println("Exception in AsoBillingStatementExcellService.uploadExcelReport() :" + e.toString());
		} finally {
			try {
				if(outputStream!=null){
					outputStream.flush();
					outputStream.close();
					
				}
				if(workbook!=null){
					workbook.dispose();
					workbook.close();
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createDataRow(AsoSummaryStatementExcellData data, Row row, CellStyle cellStyle,
			CellStyle curencyCellStyle) {

		Cell cell_0 = row.createCell(0);
		cell_0.setCellStyle(cellStyle);
		cell_0.setCellValue(data.getPolicyNum());

		Cell cell_1 = row.createCell(1);
		cell_1.setCellStyle(cellStyle);
		cell_1.setCellValue(data.getCompanyName());

		Cell cell2 = row.createCell(2);
		cell2.setCellStyle(cellStyle);
		cell2.setCellValue(data.getBillMonth());

		Cell cell3 = row.createCell(3);
		cell3.setCellStyle(cellStyle);
		cell3.setCellValue(data.getBillNum());

		Cell cell4 = row.createCell(4);
		cell4.setCellStyle(cellStyle);
		cell4.setCellValue(data.getClaimNum());

		Cell cell5 = row.createCell(5);
		cell5.setCellStyle(cellStyle);
		cell5.setCellValue(data.getEmpName());

		Cell cell6 = row.createCell(6);
		cell6.setCellStyle(cellStyle);
		cell6.setCellValue(data.getEmpNricPassportNum());

		Cell cell7 = row.createCell(7);
		cell7.setCellStyle(cellStyle);
		cell7.setCellValue(data.getEmpId());

		Cell cell8 = row.createCell(8);
		cell8.setCellStyle(cellStyle);
		cell8.setCellValue(data.getClaimantName());

		Cell cell9 = row.createCell(9);
		cell9.setCellStyle(cellStyle);
		cell9.setCellValue(data.getMembershipNum());

		Cell cell_10 = row.createCell(10);
		cell_10.setCellStyle(cellStyle);
		cell_10.setCellValue(data.getRelationship());

		Cell cell_11 = row.createCell(11);
		cell_11.setCellStyle(cellStyle);
		cell_11.setCellValue(data.getPlanNum());

		Cell cell_12 = row.createCell(12);
		cell_12.setCellStyle(cellStyle);
		cell_12.setCellValue(data.getPlanDsrc());

		Cell cell_13 = row.createCell(13);
		cell_13.setCellStyle(cellStyle);
		cell_13.setCellValue(data.getProdCode());

		Cell cell_14 = row.createCell(14);
		cell_14.setCellStyle(cellStyle);
		cell_14.setCellValue(data.getProdDsrc());

		Cell cell_15 = row.createCell(15);
		cell_15.setCellStyle(cellStyle);
		cell_15.setCellValue(data.getBranch());

		Cell cell_16 = row.createCell(16);
		cell_16.setCellStyle(cellStyle);
		cell_16.setCellValue(data.getCostCenter());

		Cell cell_17 = row.createCell(17);
		cell_17.setCellStyle(cellStyle);
		cell_17.setCellValue(data.getVisitDate());

		Cell cell_18 = row.createCell(18);
		cell_18.setCellStyle(cellStyle);
		cell_18.setCellValue(data.getHospitalSpecialist());

		Cell cell_19 = row.createCell(19);
		cell_19.setCellStyle(cellStyle);
		cell_19.setCellValue(data.getClaimType());

		Cell cell_20 = row.createCell(20);
		cell_20.setCellStyle(curencyCellStyle);

		cell_20.setCellValue(data.getAsoPaid());

		Cell cell_21 = row.createCell(21);
		cell_21.setCellStyle(curencyCellStyle);
		cell_21.setCellValue(data.getAsoExcess());

		Cell cell_22 = row.createCell(22);
		cell_22.setCellStyle(curencyCellStyle);
		cell_22.setCellValue(data.getGst());

		Cell cell_23 = row.createCell(23);
		cell_23.setCellStyle(curencyCellStyle);
		cell_23.setCellValue(data.getTtlAmnt());

		Cell cell_24 = row.createCell(24);
		cell_24.setCellStyle(curencyCellStyle);
		String reason1 = data.getReson1();
		if (!reason1.isEmpty()) {
			cell_24.setCellValue(reason1);
		} else {
			cell_24.setCellValue("NIL");
		}

		Cell cell_25 = row.createCell(25);
		cell_25.setCellStyle(curencyCellStyle);
		cell_25.setCellValue(data.getExcessAmnt1());

		Cell cell_26 = row.createCell(26);
		cell_26.setCellStyle(cellStyle);
		String reason2 = data.getReson2();
		if (!reason2.isEmpty()) {
			cell_26.setCellValue(reason2);
		} else {
			cell_26.setCellValue("NIL");
		}

		Cell cell_27 = row.createCell(27);
		cell_27.setCellStyle(curencyCellStyle);
		cell_27.setCellValue(data.getExcessAmnt2());

		Cell cell_28 = row.createCell(28);
		cell_28.setCellStyle(cellStyle);
		String reason3 = data.getReson3();
		if (!reason3.isEmpty()) {
			cell_28.setCellValue(reason3);
		} else {
			cell_28.setCellValue("NIL");
		}

		Cell cell_29 = row.createCell(29);
		cell_29.setCellStyle(curencyCellStyle);
		cell_29.setCellValue(data.getExcessAmnt3());

		Cell cell_30 = row.createCell(30);
		cell_30.setCellStyle(cellStyle);
		String reason4 = data.getReson4();
		if (!reason4.isEmpty()) {
			cell_30.setCellValue(reason4);
		} else {
			cell_30.setCellValue("NIL");
		}

		Cell cell_31 = row.createCell(31);
		cell_31.setCellStyle(curencyCellStyle);
		cell_31.setCellValue(data.getExcessAmnt4());

	}

	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getHeaderDetails(String filePath) {

		HashMap<Integer, HashMap<String, Object>> detailsRS = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listdetailsRS = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();

		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			if (br == null || br.equals(null)) {
				System.out.println("No  ASO Billing statement XL Flat File....");
			} else {
				String sCurrentline;
				int currentLint = 0, pdfGencount = 0;
				while ((sCurrentline = br.readLine()) != null) {
					HashMap<String, Object> details = new HashMap<String, Object>();
					if (currentLint == 0 || sCurrentline.contains("****")) {
						details = new HashMap<String, Object>();
						detailsRS = new HashMap<Integer, HashMap<String, Object>>();
						if (sCurrentline.contains("****")) {
							pdfGencount++;
						}
						currentLint = 0;
					}

					String data[] = sCurrentline.split("\\|");

					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
						if (data.length>=3) {
							details.put("policyNum", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						}
						if (data.length>=4) {
							details.put("billNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						}
						if (data.length>=5) {
							details.put("policyHolderNum",
									data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						}
						if (data.length>=6) {
							details.put("policyHolder", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");

						}
						if (data.length>=7) {
							details.put("subsidiaryNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if (data.length>=8) {
							details.put("subsidiary", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}
						if (data.length>=9) {
							details.put("proposalType", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						}
						if (data.length>=10) {
							details.put("depterCode", data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						}
						/*
						 * if (i == 10) { details.put("billMonth", data[i] !=
						 * null && data[i].length() > 0 ? data[i].trim() : "");
						 * }
						 */
					}

					if (data[0].equalsIgnoreCase("0001")) {
						detailsRS.put(currentLint, details);
						currentLint++;
						listdetailsRS.put(pdfGencount, detailsRS);
					}
				}
				// System.out.println("count"+count);
			}

		} catch (Exception e) {
			System.out.println("[AsoBillingStatementExcellService.getHeaderDetails()]  Exception : " + e.toString());
		} finally {
			try {
			if(br!=null) {
				br.close();
			}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return listdetailsRS;
	}

	private HashMap<Integer, List<AsoSummaryStatementExcellData>> getAsoSummaryStatementExcellData(String filePath) {
		List<AsoSummaryStatementExcellData> listAsoSummaryStatementExcellData = new ArrayList<AsoSummaryStatementExcellData>();
		HashMap<Integer, List<AsoSummaryStatementExcellData>> listAsoSummaryStatementExcellDataRS = new HashMap<Integer, List<AsoSummaryStatementExcellData>>();
		BufferedReader br = null;
		try {
		
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			if (br == null || br.equals(null)) {
				System.out.println("No  ASO Billing statement XL Flat File.... Flat File....");
			} else {
				String sCurrentline;
				int pdfgencount = 0;
				
				while ((sCurrentline = br.readLine()) != null) {
					// System.out.println(sCurrentline);
					
					AsoSummaryStatementExcellData asoSummaryStatement = new AsoSummaryStatementExcellData();
					if (sCurrentline.contains("****")) {
						asoSummaryStatement = new AsoSummaryStatementExcellData();
						listAsoSummaryStatementExcellData = new ArrayList<AsoSummaryStatementExcellData>();
						pdfgencount++;
					}
					String data[] = sCurrentline.split("\\|");

					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1D")) {
						asoSummaryStatement.setPolicyNum(data[2].length() > 0 ? data[2].trim() : "");

						asoSummaryStatement.setCompanyName(data[3].length() > 0 ? data[3].trim() : "");

						asoSummaryStatement.setBillMonth(data[4].length() > 0 ? data[4].trim() : "");

						asoSummaryStatement.setBillNum(data[5].length() > 0 ? data[5].trim() : "");

						asoSummaryStatement.setClaimNum(data[6].length() > 0 ? data[6].trim() : "");

						asoSummaryStatement.setEmpName(data[7].length() > 0 ? data[7].trim() : "");

						asoSummaryStatement.setEmpNricPassportNum(data[8].length() > 0 ? data[8].trim() : "");

						asoSummaryStatement.setEmpId(data[9].length() > 0 ? data[9].trim() : "");

						asoSummaryStatement.setClaimantName(data[10].length() > 0 ? data[10].trim() : "");

						asoSummaryStatement.setMembershipNum(data[11].length() > 0 ? data[11].trim() : "");

						asoSummaryStatement.setRelationship(data[12].length() > 0 ? data[12].trim() : "");

						asoSummaryStatement.setPlanNum(data[13].length() > 0 ? data[13].trim() : "");

						asoSummaryStatement.setPlanDsrc(data[14].length() > 0 ? data[14].trim() : "");

						asoSummaryStatement.setProdCode(data[15].length() > 0 ? data[15].trim() : "");

						asoSummaryStatement.setProdDsrc(data[16].length() > 0 ? data[16].trim() : "");

						asoSummaryStatement.setBranch(data[17].length() > 0 ? data[17].trim() : "");

						asoSummaryStatement.setCostCenter(data[18].length() > 0 ? data[18].trim() : "");

						asoSummaryStatement.setVisitDate(data[19].length() > 0 ? data[19].trim() : "");

						asoSummaryStatement.setHospitalSpecialist(data[20].length() > 0 ? data[20].trim() : "");

						asoSummaryStatement.setClaimType(data[21].length() > 0 ? data[21].trim() : "");

						String AsoPaid = data[22] != null && data[22].length() > 0 ? data[22].trim() : "";
						if (AsoPaid.isEmpty() || AsoPaid.equalsIgnoreCase(".00")) {
							AsoPaid = "0";
						}
						asoSummaryStatement.setAsoPaid(
								Double.parseDouble(AsoPaid.replace(",", "").replace("(", "-").replace(")", "")));

						String AsoExcess = data[23] != null && data[23].length() > 0 ? data[23].trim() : "";
						if (AsoExcess.isEmpty() || AsoExcess.equalsIgnoreCase(".00")) {
							AsoExcess = "0";
						}
						asoSummaryStatement.setAsoExcess(
								Double.parseDouble(AsoExcess.replace(",", "").replace("(", "-").replace(")", "")));

						String Gst = data[24] != null && data[24].length() > 0 ? data[24].trim() : "";
						if (Gst.isEmpty() || Gst.equalsIgnoreCase(".00")) {
							Gst = "0";
						}
						asoSummaryStatement
								.setGst(Double.parseDouble(Gst.replace(",", "").replace("(", "-").replace(")", "")));

						String TtlAmnt = data[25] != null && data[25].length() > 0 ? data[25].trim() : "";
						if (TtlAmnt.isEmpty() || TtlAmnt.equalsIgnoreCase(".00")) {
							TtlAmnt = "0";
						}
						asoSummaryStatement.setTtlAmnt(
								Double.parseDouble(TtlAmnt.replace(",", "").replace("(", "-").replace(")", "")));

						asoSummaryStatement.setReson1(data[26].length() > 0 ? data[26].trim() : "");

						String ExcessAmnt1 = data[27] != null && data[27].length() > 0 ? data[27].trim() : "";
						if (ExcessAmnt1.isEmpty() || ExcessAmnt1.equalsIgnoreCase(".00")) {
							ExcessAmnt1 = "0";
						}
						asoSummaryStatement.setExcessAmnt1(
								Double.parseDouble(ExcessAmnt1.replace(",", "").replace("(", "-").replace(")", "")));

						asoSummaryStatement.setReson2(data[28].length() > 0 ? data[28].trim() : "");

						String ExcessAmnt2 = data[29] != null && data[29].length() > 0 ? data[29].trim() : "";
						if (ExcessAmnt2.isEmpty() || ExcessAmnt2.equalsIgnoreCase(".00")) {
							ExcessAmnt2 = "0";
						}
						asoSummaryStatement.setExcessAmnt2(
								Double.parseDouble(ExcessAmnt2.replace(",", "").replace("(", "-").replace(")", "")));

						asoSummaryStatement.setReson3(data[30].length() > 0 ? data[30].trim() : "");

						String ExcessAmnt3 = data[31] != null && data[31].length() > 0 ? data[31].trim() : "";
						if (ExcessAmnt3.isEmpty() || ExcessAmnt3.equalsIgnoreCase(".00")) {
							ExcessAmnt3 = "0";
						}
						asoSummaryStatement.setExcessAmnt3(
								Double.parseDouble(ExcessAmnt3.replace(",", "").replace("(", "-").replace(")", "")));

						asoSummaryStatement.setReson4(data[32].length() > 0 ? data[32].trim() : "");

						String ExcessAmnt4 = data[33] != null && data[33].length() > 0 ? data[33].trim() : "";
						if (ExcessAmnt4.isEmpty() || ExcessAmnt4.equalsIgnoreCase(".00")) {
							ExcessAmnt4 = "0";
						}
						asoSummaryStatement.setExcessAmnt4(
								Double.parseDouble(ExcessAmnt4.replace(",", "").replace("(", "-").replace(")", "")));
						listAsoSummaryStatementExcellData.add(asoSummaryStatement);
					}

					listAsoSummaryStatementExcellDataRS.put(pdfgencount, listAsoSummaryStatementExcellData);
				}
			}
		} catch (Exception e) {
			System.out.println("[AsoBillingStatementExcellService.getAsoSummaryStatementExcellData()]  Exception : "+ e.toString());
		}
		return listAsoSummaryStatementExcellDataRS;
	}

	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
	
	public String getG4CycleDate(String filePath) {
		String cycledate = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			if (br == null || br.equals(null)) {
				System.out.println("No MemberClaimList Flat File....");
			} else {
				String sCurrentline;
				while ((sCurrentline = br.readLine()) != null) {

					String data[] = sCurrentline.split("\\|");
					if (data[0].equalsIgnoreCase("0000")) {
						if (data.length >= 3) {
							cycledate = data[2] != null && data[2].length() > 0 ? data[2].trim() : "";
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

		return cycledate;
	}

}
