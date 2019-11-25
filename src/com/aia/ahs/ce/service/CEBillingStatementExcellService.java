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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.ahs.ce.model.CESummaryStatementExcellData;
import com.aia.common.db.DBCSDCommon;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;

@Service("cEBillingStatementExcellService")
public class CEBillingStatementExcellService implements TemplateActions {
	@Autowired
	private DBCSDCommon dbcmd;
	
	@Value("${print.agent.fileoutput.path}")
	private String outputPath;

	SimpleDateFormat sdf = new SimpleDateFormat("YYYY");

	SimpleDateFormat ymd = new SimpleDateFormat("YYYY-MM-dd");
	Integer companyCode;
	private static final String docType = "CEBSE";
	private String tbl_doc_nm;
	private String tableName;
	private String process_year;
	private static final String file_format = "xlsx";

	private Integer dmStatus = 1;

	private String proposalNo; // policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type;
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;
	private String doc_creation_dt;
	private String year;
	
	private String g4CycleDate;
	
	public int genReport(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {

		this.g4CycleDate = batchCycle.getCycleDate();
		this.doc_creation_dt=this.ymd.format(new Date(this.g4CycleDate)) ;
		this.year =this.sdf.format(new Date(this.g4CycleDate));
		
		this.tableName = "tbl_cebse_" + this.year;
		this.tbl_doc_nm = "[aiaIMGdb_CSD_" + this.year + "]..[" + this.tableName + "]";
		this.process_year = this.year;

		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listDetailsRS = getHeaderDetails(batchFileDetails.getFileLocation());
		HashMap<Integer, List<CESummaryStatementExcellData>> cESummaryStatementExcellDataRS = getCESummaryStatementExcellData(
				batchFileDetails.getFileLocation());
		int documentCount = 0;
		int noFiles = listDetailsRS.size();
		for (int i = 0; i < noFiles; i++) {
			HashMap<Integer, HashMap<String, Object>> detailsRS = listDetailsRS.get(i);
			List<CESummaryStatementExcellData> listCESummaryStatementExcellData = cESummaryStatementExcellDataRS.get(i);
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
			if(this.sub_client_name==null || this.sub_client_name.isEmpty() || this.sub_client_name.equalsIgnoreCase("-")){
				this.sub_client_name=this.client_name;
			}

//			String billperiod = "" + (String) datasource.get("billMonth");
//			System.out.println("billperiod:" + billperiod);
//			String billmonth = billperiod.replace("/", "").replace(" ", "").trim();
//			System.out.println("billmonth:" + billmonth);
			String filename = datasource.get("policyNum") + "_" + datasource.get("billNum") + "_CEBillingStmt.xlsx";
		
			if(this.uploadExcelReport(listCESummaryStatementExcellData, filename,companyCode.getCompanyCode())) {
				++documentCount;
			}
		}
     return documentCount;
}

	private boolean uploadExcelReport(List<CESummaryStatementExcellData> listCESummaryStatementExcellData, String filename,
			String company) {
		String[] column = null;
		if (company.equalsIgnoreCase("Co3")) {
			this.companyCode =3; 
		column =new String[]  { "Policy Number", "Company Name", "Billing Month\n[MM/YYYY]", "Bill Number", "Claim No.",
				"Employee Name", "Employee \n NRIC/Passport No.", "Employee ID", "Claimant Name", "Membership No. ",
				"Relationship", "Plan No", "Plan Description", "Product Code", "Product Description", "Branch",
				"Cost Centre", "Visit Date", "Hospital/Clinic/Specialist", "Claim Type", "Total Claim Excess (RM)",
				"Reason For Excess (1)", "Excess Amount (1) (RM)", "Reason For Excess (2)", "Excess Amount (2) (RM)",
				"Reason For Excess (3)", "Excess Amount (3) (RM)", "Reason For Excess (4)", "Excess Amount (4) (RM)" };
		}
		if (company.equalsIgnoreCase("Co4")) {
			this.companyCode =4; 
		  column =new String[] { "Certificate Number", "Company Name", "Billing Month\n[MM/YYYY]", "Bill Number",
				"Claim No.", "Employee Name", "Employee \n NRIC/Passport No.", "Employee ID", "Claimant Name",
				"Membership No. ", "Relationship", "Plan No", "Plan Description", "Product Code", "Product Description",
				"Branch", "Cost Centre", "Visit Date", "Hospital/Clinic/Specialist", "Claim Type",
				"Total Claim Excess (RM)", "Reason For Excess (1)", "Excess Amount (1) (RM)", "Reason For Excess (2)",
				"Excess Amount (2) (RM)", "Reason For Excess (3)", "Excess Amount (3) (RM)", "Reason For Excess (4)",
				"Excess Amount (4) (RM)" };
		}
		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet("ClaimExcessBillingStatement");
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
			
		}
		DataFormat format = workbook.createDataFormat();
		Font font = sheet.getWorkbook().createFont();
		font.setFontHeightInPoints((short) 9);
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyle.setFont(font);

		CellStyle curencyCellStyle = sheet.getWorkbook().createCellStyle();
		curencyCellStyle.setWrapText(true);
		curencyCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		curencyCellStyle.setFont(font);
		curencyCellStyle.setDataFormat(format.getFormat("#,##0.00"));

		int rowCount = 1;
		for (CESummaryStatementExcellData data : listCESummaryStatementExcellData) {

			Row row = sheet.createRow(rowCount++);
			row.setHeight((short) (1 * sheet.getDefaultRowHeight()));
			createDataRow(data, row, cellStyle, curencyCellStyle);
		}

		for (int i = 0; i < column.length; i++) {
			sheet.autoSizeColumn(i);
		}

		BufferedOutputStream outputStream = null;
		try {
			String excelOutputPath =this.outputPath+"/"+companyCode+"/"+this.doc_creation_dt.replace("-","");

			File dir = new File(excelOutputPath);
			if (!dir.exists()) {
				if (dir.mkdirs()) {
					System.out.println("directories are created! " + dir.getAbsolutePath());
				} else {
					System.out.println("failed to create directories ! " + excelOutputPath);
				}
			}
			File file = new File(dir.getAbsolutePath() + "/" + filename);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("directories are created @@@@@@@@.... " + file.getAbsoluteFile());

			}

			outputStream = new BufferedOutputStream(new FileOutputStream(file));
			workbook.write(outputStream);
			//System.out.println("excel created .... :" + file.getAbsolutePath());

			byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

			String dataId = UUID.randomUUID().toString();
			boolean add = dbcmd.checktblDmDoc(this.companyCode, proposalNo, this.doc_creation_dt, docType, bill_no);
			if (add) {
				dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

				dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm,
						this.doc_creation_dt, this.companyCode, client_no, client_name, bill_no, bill_type,
						sub_client_no, sub_client_name, file_format, proposal_type, null, 0);

			} else {
				dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

				dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm,
						this.doc_creation_dt, this.companyCode, client_no, client_name, bill_no, bill_type,
						sub_client_no, sub_client_name, file_format, proposal_type, null, 0);
			}

		} catch (Exception e) {
			System.out.println("Exception in CEBillingStatementExcellService.generateExcelReport() :" + e.toString());
		return false;
		} finally {
			try {
				workbook.dispose();
				if (workbook != null) {
					workbook.close();
				}
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
				

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void createDataRow(CESummaryStatementExcellData data, Row row, CellStyle cellStyle,
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
		String TtlclaimExcess = data.getTtlclaimExcess();
		if (TtlclaimExcess.isEmpty() || TtlclaimExcess.equalsIgnoreCase(".00")) {
			TtlclaimExcess = "0";
		}
		cell_20.setCellValue(Double.parseDouble(TtlclaimExcess.replace(",", "").replace("(", "-").replace(")", "")));

		Cell cell_21 = row.createCell(21);
		cell_21.setCellStyle(cellStyle);
		String reasonExcess1 = data.getReasonExcess1();
		if (!reasonExcess1.isEmpty()) {
			cell_21.setCellValue(reasonExcess1);
		} else {
			cell_21.setCellValue("NIL");
		}

		Cell cell_22 = row.createCell(22);
		cell_22.setCellStyle(curencyCellStyle);
		String ExcessAmount1 = data.getExcessAmount1();
		if (ExcessAmount1.isEmpty() || ExcessAmount1.equalsIgnoreCase(".00")) {
			ExcessAmount1 = "0";
		}
		cell_22.setCellValue(Double.parseDouble(ExcessAmount1.replace(",", "").replace("(", "-").replace(")", "")));

		Cell cell_23 = row.createCell(23);
		cell_23.setCellStyle(cellStyle);
		String reasonExcess2 = data.getReasonExcess2();
		if (!reasonExcess2.isEmpty()) {
			cell_23.setCellValue(reasonExcess2);
		} else {
			cell_23.setCellValue("NIL");
		}

		Cell cell_24 = row.createCell(24);
		cell_24.setCellStyle(curencyCellStyle);
		String ExcessAmount2 = data.getExcessAmount2();
		if (ExcessAmount2.isEmpty() || ExcessAmount2.equalsIgnoreCase(".00")) {
			ExcessAmount2 = "0";
		}
		cell_24.setCellValue(Double.parseDouble(ExcessAmount2.replace(",", "").replace("(", "-").replace(")", "")));

		Cell cell_25 = row.createCell(25);
		cell_25.setCellStyle(cellStyle);
		String reasonExcess3 = data.getReasonExcess3();
		if (!reasonExcess3.isEmpty()) {
			cell_25.setCellValue(reasonExcess3);
		} else {
			cell_25.setCellValue("NIL");
		}

		Cell cell_26 = row.createCell(26);
		cell_26.setCellStyle(curencyCellStyle);
		String ExcessAmount3 = data.getExcessAmount3();
		if (ExcessAmount3.isEmpty() || ExcessAmount3.equalsIgnoreCase(".00")) {
			ExcessAmount3 = "0";
		}
		cell_26.setCellValue(Double.parseDouble(ExcessAmount3.replace(",", "").replace("(", "-").replace(")", "")));

		Cell cell_27 = row.createCell(27);
		cell_27.setCellStyle(cellStyle);
		String reasonExcess4 = data.getReasonExcess4();
		if (!reasonExcess4.isEmpty()) {
			cell_27.setCellValue(reasonExcess4);
		} else {
			cell_27.setCellValue("NIL");
		}

		Cell cell_28 = row.createCell(28);
		cell_28.setCellStyle(curencyCellStyle);
		String ExcessAmount4 = data.getExcessAmount4();
		if (ExcessAmount4.isEmpty() || ExcessAmount4.equalsIgnoreCase(".00")) {
			ExcessAmount4 = "0";
		}
		cell_28.setCellValue(Double.parseDouble(ExcessAmount4.replace(",", "").replace("(", "-").replace(")", "")));
		
	}

	private HashMap<Integer, List<CESummaryStatementExcellData>> getCESummaryStatementExcellData(String filePath) {
		List<CESummaryStatementExcellData> listceSummaryStatementExcellData = new ArrayList<CESummaryStatementExcellData>();
		HashMap<Integer, List<CESummaryStatementExcellData>> listceSummaryStatementExcellDataRS = new HashMap<Integer, List<CESummaryStatementExcellData>>();
		
		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			if (br == null || br.equals(null)) {
				System.out.println("No Aso_summaryStatement xl Flat File....");
			} else {
				String sCurrentline;
				int pdfgencount = 0;
				while ((sCurrentline = br.readLine()) != null) {
					// System.out.println(sCurrentline);

					CESummaryStatementExcellData ceSummaryStatement = new CESummaryStatementExcellData();
					if (sCurrentline.contains("****")) {
						ceSummaryStatement = new CESummaryStatementExcellData();
						listceSummaryStatementExcellData = new ArrayList<CESummaryStatementExcellData>();
						pdfgencount++;
					}
					String data[] = sCurrentline.split("\\|");

					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1D")) {

						ceSummaryStatement.setPolicyNum(data[2].length() > 0 ? data[2].trim() : "");

						ceSummaryStatement.setCompanyName(data[3].length() > 0 ? data[3].trim() : "");

						ceSummaryStatement.setBillMonth(data[4].length() > 0 ? data[4].trim() : "");

						ceSummaryStatement.setBillNum(data[5].length() > 0 ? data[5].trim() : "");

						ceSummaryStatement.setClaimNum(data[6].length() > 0 ? data[6].trim() : "");

						ceSummaryStatement.setEmpName(data[7].length() > 0 ? data[7].trim() : "");

						ceSummaryStatement.setEmpNricPassportNum(data[8].length() > 0 ? data[8].trim() : "");

						ceSummaryStatement.setEmpId(data[9].length() > 0 ? data[9].trim() : "");

						ceSummaryStatement.setClaimantName(data[10].length() > 0 ? data[10].trim() : "");

						ceSummaryStatement.setMembershipNum(data[11].length() > 0 ? data[11].trim() : "");

						ceSummaryStatement.setRelationship(data[12].length() > 0 ? data[12].trim() : "");

						ceSummaryStatement.setPlanNum(data[13].length() > 0 ? data[13].trim() : "");

						ceSummaryStatement.setPlanDsrc(data[14].length() > 0 ? data[14].trim() : "");

						ceSummaryStatement.setProdCode(data[15].length() > 0 ? data[15].trim() : "");

						ceSummaryStatement.setProdDsrc(data[16].length() > 0 ? data[16].trim() : "");

						ceSummaryStatement.setBranch(data[17].length() > 0 ? data[17].trim() : "");

						ceSummaryStatement.setCostCenter(data[18].length() > 0 ? data[18].trim() : "");

						ceSummaryStatement.setVisitDate(data[19].length() > 0 ? data[19].trim() : "");

						ceSummaryStatement.setHospitalSpecialist(data[20].length() > 0 ? data[20].trim() : "");

						ceSummaryStatement.setClaimType(data[21].length() > 0 ? data[21].trim() : "");

						if (data.length >= 23) {
							ceSummaryStatement.setTtlclaimExcess(data[22].length() > 0 ? data[22].trim() : "");
						}
						if (data.length >= 24) {
							ceSummaryStatement.setReasonExcess1(data[23].length() > 0 ? data[23].trim() : "");
						}
						if (data.length >= 25) {
							ceSummaryStatement.setExcessAmount1(data[24].length() > 0 ? data[24].trim() : "");
						}
						if (data.length >= 26) {
							ceSummaryStatement.setReasonExcess2(data[25].length() > 0 ? data[25].trim() : "");
						}
						if (data.length >= 27) {
							ceSummaryStatement.setExcessAmount2(data[26].length() > 0 ? data[26].trim() : "");
						}
						if (data.length >= 28) {
							ceSummaryStatement.setReasonExcess3(data[27].length() > 0 ? data[27].trim() : "");
						}
						if (data.length >= 29) {
							ceSummaryStatement.setExcessAmount3(data[28].length() > 0 ? data[28].trim() : "");
						}
						if (data.length >= 30) {
							ceSummaryStatement.setReasonExcess4(data[29].length() > 0 ? data[29].trim() : "");
						}
						if (data.length >= 31) {
							ceSummaryStatement.setExcessAmount4(data[30].length() > 0 ? data[30].trim() : "");
						}
					}

					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1D")) {
						listceSummaryStatementExcellData.add(ceSummaryStatement);
						listceSummaryStatementExcellDataRS.put(pdfgencount, listceSummaryStatementExcellData);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("[CEBillingStatementExcellService.getCESummaryStatementExcellData()]  Exception : " + e.toString());
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return listceSummaryStatementExcellDataRS;
	}

	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getHeaderDetails(String filePath) {

		HashMap<Integer, HashMap<String, Object>> detailsRS = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listdetailsRS = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();

		BufferedReader br = null;

		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			if (br == null || br.equals(null)) {
				System.out.println("No MemberDetailsExcel statement XL Flat File....");
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
						if (data.length >= 3) {
							details.put("policyNum", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						}
						if (data.length >= 4) {
							details.put("billNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						}
						if (data.length >= 5) {
							details.put("policyHolderNum",
									data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						}
						if (data.length >= 6) {
							details.put("policyHolder", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");

						}
						if (data.length >= 7) {
							details.put("subsidiaryNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if (data.length >= 8) {
							details.put("subsidiary", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}
						if (data.length >= 9) {
							details.put("proposalType", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						}
						if (data.length >= 10) {
							details.put("depterCode", data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						}
						/*
						 * if (i == 10) { details.put("billMonth", data[i] != null && data[i].length() >
						 * 0 ? data[i].trim() : ""); }
						 */
					}

					if (data[0].equalsIgnoreCase("0001")) {
						detailsRS.put(currentLint, details);
						currentLint++;
						listdetailsRS.put(pdfGencount, detailsRS);
					}
				}
				
			}

		} catch (Exception e) {
			System.out.println("[MemberDetailsExcelService.getHeaderDetails()]  Exception : " + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if(br!=null) {
					br.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return listdetailsRS;
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
