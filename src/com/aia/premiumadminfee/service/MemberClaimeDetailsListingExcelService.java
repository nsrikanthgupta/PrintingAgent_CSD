package com.aia.premiumadminfee.service;

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
import java.text.ParseException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.common.db.*;
import com.aia.premiumadminfee.model.MemberClaimeListingDetailsExcel;
import com.aia.premiumadminfee.model.MemberDetailsExcel;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;

@Service("memberClaimeDetailsListingExcelService")
public class MemberClaimeDetailsListingExcelService implements TemplateActions {
	private static final Logger LOGGER = LoggerFactory.getLogger(MemberClaimeDetailsListingExcelService.class);

	@Autowired
	private DBCSDCommon dbcmd;

	@Value("${print.agent.fileoutput.path}")
	private String outputPath;

	SimpleDateFormat sdf = new SimpleDateFormat("YYYY");

	SimpleDateFormat ymd = new SimpleDateFormat("YYYY-MM-dd");
	private static final String docType = "PAFMCDE";
	private static final String file_format = "xlsx";

	private String doc_creation_dt;
	private String year;
	private String tableName;
	private String tbl_doc_nm;
	private String process_year;

	private Integer dmStatus = 1;

	private String proposalNo; // policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type;
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;

	private Integer companyCode;

	private String g4CycleDate;

	@Override
	public int genReport(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {

		int documentCount = 0;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			this.g4CycleDate = batchCycle.getCycleDate();
			this.doc_creation_dt = ymd.format(dateFormat.parse(this.g4CycleDate));
			this.year = sdf.format(dateFormat.parse(this.g4CycleDate));

			this.tableName = "tbl_pafmcde_" + this.year;
			this.tbl_doc_nm = "[aiaIMGdb_CSD_" + this.year + "]..[" + tableName + "]";
			this.process_year = this.year;

			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listDetailsRS = getHeaderDetails(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<MemberClaimeListingDetailsExcel>> listmemberClimeDetails = getMemberClimeDetails(
					batchFileDetails.getFileLocation());
			int noFiles = listDetailsRS.size();
			for (int i = 0; i < noFiles; i++) {
				HashMap<Integer, HashMap<String, Object>> detailsRS = listDetailsRS.get(i);
				List<MemberClaimeListingDetailsExcel> listmemberDetails = listmemberClimeDetails.get(i);
			
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
				/*
				 * String billperiod=""+datasource.get("billMonth"); String
				 * billmonth=billperiod.replace("/", "").replace(" ", "").trim();
				 */
				String filename = datasource.get("policyNum") + "_" + datasource.get("billNum")+ "_MemberClaimListing.xlsx";
				if (this.uploadExcelReport(listmemberDetails, filename, companyCode.getCompanyCode())) {
					++documentCount;
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return documentCount;
	}

	private boolean uploadExcelReport(List<MemberClaimeListingDetailsExcel> listmemberDetails, String filename,
			String companyCode) {

		String[] column = null;
		if (companyCode.equalsIgnoreCase("Co3")) {
			this.companyCode = 3;
			column = new String[] { "Policy Number", "Company Name", "Billing Month \n [MM/YYYY]", "Bill Number",
					"Claim No.", "Employee Name", "Employee \n NRIC/Passport No.", "Employee ID", "Claimant Name",
					"Membership No.", "Relationship", "Plan No.", "Plan Description", "Product Code",
					"Product Description", "Branch", "Cost Centre", "Visit Date", "Provider", "Claim Type",
					"ASO Billed Amount (RM)", " Claims Paid Amount (RM)", "Claim Paid Date" };
		} else if (companyCode.equalsIgnoreCase("Co4")) {
			column = new String[] { "Certificate Number", "Company Name", "Billing Month \n [MM/YYYY]", "Bill Number",
					"Claim No.", "Employee Name", "Employee \n NRIC/Passport No.", "Employee ID", "Claimant Name",
					"Membership No.", "Relationship", "Plan No.", "Plan Description", "Product Code",
					"Product Description", "Branch", "Cost Centre", "Visit Date", "Provider", "Claim Type",
					"ASO Billed Amount (RM)", " Claims Paid Amount (RM)", "Claim Paid Date" };

		}
		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet("ClaimDetailsListing");
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
			// System.out.println(i+"."+column[i]);
		}
		DataFormat format = workbook.createDataFormat();
		Font font = sheet.getWorkbook().createFont();
		font.setFontHeightInPoints((short) 9);
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyle.setFont(font);

		CellStyle currencyCellStyle = sheet.getWorkbook().createCellStyle();
		currencyCellStyle.setWrapText(true);
		currencyCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		currencyCellStyle.setFont(font);
		// currencyCellStyle.setDataFormat(format.getFormat("$#,##0.00;[Red]($#,##0.00)"));
		// currencyCellStyle.setDataFormat(format.getFormat("0.00;[Red]0.00"));
		currencyCellStyle.setDataFormat(format.getFormat("#,##0.00"));
		int rowCount = 1;
		// data row
		for (MemberClaimeListingDetailsExcel memberdata : listmemberDetails) {
			Row row = sheet.createRow(rowCount++);
			row.setHeight((short) (1 * sheet.getDefaultRowHeight()));
			createDataRow(memberdata, row, cellStyle, currencyCellStyle);
		}
		for (int i = 0; i < column.length; i++) {
			sheet.autoSizeColumn(i);
		}
		BufferedOutputStream outputStream = null;
		try {

			String excellOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");

			File dir = new File(excellOutputPath);
			if (!dir.exists()) {
				if (dir.mkdirs()) {
					System.out.println("directories are created! " + dir.getAbsolutePath());
				} else {
					System.out.println("failed to create directories ! " + excellOutputPath);

				}
			}
			File file = new File(dir.getAbsolutePath() + "/" + filename);
			if (!file.exists()) {
				file.createNewFile();
				// System.out.println("directories are created @@@@@@@@....
				// "+file.getAbsoluteFile());

			}
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
			workbook.write(outputStream);

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
						this.doc_creation_dt, this.companyCode, client_no, client_name, bill_no, this.bill_type,
						sub_client_no, sub_client_name, file_format, proposal_type, null, 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
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

	private static void createDataRow(MemberClaimeListingDetailsExcel memberclimedata, Row row, CellStyle cellStyle,
			CellStyle currencyCellStyle) {

		Cell cell_0 = row.createCell(0);
		cell_0.setCellStyle(cellStyle);
		cell_0.setCellValue(memberclimedata.getPolicyNum());

		Cell cell_1 = row.createCell(1);
		cell_1.setCellStyle(cellStyle);
		cell_1.setCellValue(memberclimedata.getCompanyName());

		Cell cell2 = row.createCell(2);
		cell2.setCellStyle(cellStyle);
		cell2.setCellValue(memberclimedata.getBillingMonth());

		Cell cell3 = row.createCell(3);
		cell3.setCellStyle(cellStyle);
		cell3.setCellValue(memberclimedata.getBillNum());

		Cell cell4 = row.createCell(4);
		cell4.setCellStyle(cellStyle);
		cell4.setCellValue(memberclimedata.getClaimNum());

		Cell cell5 = row.createCell(5);
		cell5.setCellStyle(cellStyle);
		cell5.setCellValue(memberclimedata.getEmpName());

		Cell cell6 = row.createCell(6);
		cell6.setCellStyle(cellStyle);
		cell6.setCellValue(memberclimedata.getEmpIcOrPsprtNUm());

		Cell cell7 = row.createCell(7);
		cell7.setCellStyle(cellStyle);
		cell7.setCellValue(memberclimedata.getEmpId());

		Cell cell8 = row.createCell(8);
		cell8.setCellStyle(cellStyle);
		cell8.setCellValue(memberclimedata.getClaimantName());

		Cell cell9 = row.createCell(9);
		cell9.setCellStyle(cellStyle);
		cell9.setCellValue(memberclimedata.getMembersh());

		Cell cell_10 = row.createCell(10);
		cell_10.setCellStyle(cellStyle);
		cell_10.setCellValue(memberclimedata.getRelationShip());

		Cell cell_11 = row.createCell(11);
		cell_11.setCellStyle(cellStyle);
		cell_11.setCellValue(memberclimedata.getPlanNum());

		Cell cell_12 = row.createCell(12);
		cell_12.setCellStyle(cellStyle);
		cell_12.setCellValue(memberclimedata.getPlandescr());

		Cell cell_13 = row.createCell(13);
		cell_13.setCellStyle(cellStyle);
		cell_13.setCellValue(memberclimedata.getProdCode());

		Cell cell_14 = row.createCell(14);
		cell_14.setCellStyle(cellStyle);
		cell_14.setCellValue(memberclimedata.getProdDescr());

		Cell cell_15 = row.createCell(15);
		cell_15.setCellStyle(cellStyle);
		cell_15.setCellValue(memberclimedata.getBranch());

		Cell cell_16 = row.createCell(16);
		cell_16.setCellStyle(cellStyle);
		cell_16.setCellValue(memberclimedata.getCostCentre());

		Cell cell_17 = row.createCell(17);
		cell_17.setCellStyle(cellStyle);
		cell_17.setCellValue(memberclimedata.getVisitDate());

		Cell cell_18 = row.createCell(18);
		cell_18.setCellStyle(cellStyle);
		cell_18.setCellValue(memberclimedata.getProvider());

		Cell cell_19 = row.createCell(19);
		cell_19.setCellStyle(cellStyle);
		cell_19.setCellValue(memberclimedata.getClaimeType());

		Cell cell_20 = row.createCell(20);
		cell_20.setCellStyle(currencyCellStyle);
		String AsoBillAmt = memberclimedata.getAsoBillAmt();
		if (AsoBillAmt.isEmpty() || AsoBillAmt.equalsIgnoreCase(".00")) {
			AsoBillAmt = "0";
		}
		AsoBillAmt=AsoBillAmt.replace(",", "");
		cell_20.setCellValue(Double.parseDouble(AsoBillAmt));

		Cell cell_21 = row.createCell(21);
		cell_21.setCellStyle(currencyCellStyle);
		String AsoclaimsPaid = memberclimedata.getAsoclaimsPaid();
		if (AsoclaimsPaid.isEmpty() || AsoclaimsPaid.equalsIgnoreCase(".00")) {
			AsoclaimsPaid = "0";
		}
		AsoclaimsPaid=AsoclaimsPaid.replace(",", "");
		cell_21.setCellValue(Double.parseDouble(AsoclaimsPaid));

		Cell cell_22 = row.createCell(22);
		cell_22.setCellStyle(cellStyle);

		cell_22.setCellValue(memberclimedata.getClaimPaid());
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
				if (br != null) {
					br.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return listdetailsRS;
	}

	private HashMap<Integer, List<MemberClaimeListingDetailsExcel>> getMemberClimeDetails(String filePath) {
		List<MemberClaimeListingDetailsExcel> listmemberClimeDetails = new ArrayList<MemberClaimeListingDetailsExcel>();
		HashMap<Integer, List<MemberClaimeListingDetailsExcel>> listmemberClimeDetailsRS = new HashMap<Integer, List<MemberClaimeListingDetailsExcel>>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			if (br == null || br.equals(null)) {
				System.out.println("No MemberClaimList Flat File....");
			} else {

				String sCurrentline;
				int pdfgencount = 0;
				while ((sCurrentline = br.readLine()) != null) {

					MemberClaimeListingDetailsExcel memberClimeDetails = new MemberClaimeListingDetailsExcel();
					if (sCurrentline.contains("****")) {
						memberClimeDetails = new MemberClaimeListingDetailsExcel();
						listmemberClimeDetails = new ArrayList<MemberClaimeListingDetailsExcel>();
						pdfgencount++;
					}
					String data[] = sCurrentline.split("\\|");
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1D")) {
						if (data.length >= 3) {
							memberClimeDetails
									.setPolicyNum(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						}
						if (data.length >= 4) {

							memberClimeDetails
									.setCompanyName(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						}
						if (data.length >= 5) {

							memberClimeDetails
									.setBillingMonth(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						}
						if (data.length >= 6) {
							memberClimeDetails
									.setBillNum(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");

						}
						if (data.length >= 7) {
							memberClimeDetails
									.setClaimNum(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if (data.length >= 8) {
							memberClimeDetails
									.setEmpName(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}
						if (data.length >= 9) {
							memberClimeDetails
									.setEmpIcOrPsprtNUm(data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						}
						if (data.length >= 10) {
							memberClimeDetails.setEmpId(data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						}
						if (data.length >= 11) {
							memberClimeDetails
									.setClaimantName(data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						}
						if (data.length >= 12) {
							memberClimeDetails
									.setMembersh(data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						}
						if (data.length >= 13) {
							memberClimeDetails
									.setRelationShip(data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						}
						if (data.length >= 14) {
							memberClimeDetails
									.setPlanNum(data[13] != null && data[13].length() > 0 ? data[13].trim() : "");

						}
						if (data.length >= 15) {
							memberClimeDetails
									.setPlandescr(data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
						}
						if (data.length >= 16) {
							memberClimeDetails
									.setProdCode(data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
						}
						if (data.length >= 17) {

							memberClimeDetails
									.setProdDescr(data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
						}
						if (data.length >= 18) {
							memberClimeDetails
									.setBranch(data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
						}
						if (data.length >= 19) {
							memberClimeDetails
									.setCostCentre(data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
						}
						if (data.length >= 20) {
							memberClimeDetails
									.setVisitDate(data[19] != null && data[19].length() > 0 ? data[19].trim() : "");

						}

						if (data.length >= 22) {

							memberClimeDetails
									.setClaimeType(data[21] != null && data[21].length() > 0 ? data[21].trim() : "");
						}
						if (data.length >= 23) {
							memberClimeDetails
									.setAsoBillAmt(data[22] != null && data[22].length() > 0 ? data[22].trim() : "");
						}
						if (data.length >= 24) {
							memberClimeDetails
									.setAsoclaimsPaid(data[23] != null && data[23].length() > 0 ? data[23].trim() : "");
						}
						if (data.length >= 25) {
							memberClimeDetails
									.setClaimPaid(data[24] != null && data[24].length() > 0 ? data[24].trim() : "");
						}
						listmemberClimeDetails.add(memberClimeDetails);
					}
					listmemberClimeDetailsRS.put(pdfgencount, listmemberClimeDetails);

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
		return listmemberClimeDetailsRS;
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

}