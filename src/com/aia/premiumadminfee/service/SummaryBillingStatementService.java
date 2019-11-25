package com.aia.premiumadminfee.service;

import java.io.BufferedOutputStream;
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
import com.aia.premiumadminfee.model.AnnualPremiumRatesTb1;
import com.aia.premiumadminfee.model.AnnualPremiumRatesTb2;
import com.aia.premiumadminfee.model.AnnualPremiumRatesTb3;
import com.aia.premiumadminfee.model.BillingStatementGroupHealthTbData;
import com.aia.premiumadminfee.model.BillingStatementGroupTermLifeTbdata;
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

@Service("summaryBillingStatementService")
public class SummaryBillingStatementService implements TemplateActions {
	private static final Logger LOGGER = LoggerFactory.getLogger(SummaryBillingStatementService.class);

	@Autowired
	private DBCSDCommon dbcmd;

	@Value("${print.agent.fileoutput.path}")
	private String outputPath;

	String jasper = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../jasper/", true);

	String logo = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../img/", true);

	SimpleDateFormat sdf = new SimpleDateFormat("YYYY");

	SimpleDateFormat ymd = new SimpleDateFormat("YYYY-MM-dd");

	private int companyCode;

	private static final String docType = "PAFSBS";
	private String doc_creation_dt;
	String year;
	private String tableName;
	private String tbl_doc_nm;
	private String file_format = "pdf";
	private Integer dmStatus = 1;
	private String process_year;

	private String proposalNo; // policynum
	private String client_no;
	private String client_name;
	private String bill_no;
	private String bill_type;
	private String proposal_type;// polocyType
	private String sub_client_no;
	private String sub_client_name;
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
			this.tableName = "tbl_pafsbs_" + this.year;
			this.tbl_doc_nm = "[aiaIMGdb_CSD_" + this.year + "]..[" + this.tableName + "]";
			this.process_year = this.year;

			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> billingStatementRSDetails = getSummaryBillingSattementDetails(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<BillingStatementGroupHealthTbData>> billStatementGHListDetails = getBillingStatementGroupHealthTbData(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<BillingStatementGroupTermLifeTbdata>> billStatementGTLListDetails = getBillingStatementGroupTermLifeTbdata(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<AnnualPremiumRatesTb1>> annualPremiumRatesTb1ListDetails = getAnnualPremiumRatesTb1Data(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<AnnualPremiumRatesTb2>> annualPremiumRatesTb2ListDetails = getAnnualPremiumRatesTb2Data(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<AnnualPremiumRatesTb3>> annualPremiumRatesTb3ListDetails = getAnnualPremiumRatesTb3Data(
					batchFileDetails.getFileLocation());

			int noFiles = billingStatementRSDetails.size();
			for (int i = 0; i < noFiles; i++) {

				HashMap<Integer, HashMap<String, Object>> billingStatementRS = billingStatementRSDetails.get(i);
				List<BillingStatementGroupHealthTbData> billStatementGHList = billStatementGHListDetails.get(i);
				List<BillingStatementGroupTermLifeTbdata> billStatementGTLList = billStatementGTLListDetails.get(i);
				List<AnnualPremiumRatesTb1> annualPremiumRatesTb1List = annualPremiumRatesTb1ListDetails.get(i);
				List<AnnualPremiumRatesTb2> annualPremiumRatesTb2List = annualPremiumRatesTb2ListDetails.get(i);
				List<AnnualPremiumRatesTb3> annualPremiumRatesTb3List = annualPremiumRatesTb3ListDetails.get(i);
				HashMap<String, Object> dataSource = new HashMap<String, Object>();
				for (int a = 0; a < billingStatementRS.size(); a++) {
					dataSource.putAll(billingStatementRS.get(a));
				}

				this.proposalNo = (String) dataSource.get("policyNum");
				this.client_no = (String) dataSource.get("policyHolderNum");
				this.client_name = (String) dataSource.get("policyHolder");
				this.bill_no = (String) dataSource.get("billNum");
				this.proposal_type = (String) dataSource.get("policyType");
				this.sub_client_no = (String) dataSource.get("subsidiaryNum");
				this.sub_client_name = (String) dataSource.get("subsidiary");
				if (this.sub_client_name.equalsIgnoreCase("-") || this.sub_client_name.isEmpty()
						|| this.sub_client_name == null) {
					this.sub_client_name = this.client_name;
				}
				this.indicator = (String) dataSource.get("printHardCp");
				System.out.println("indicater  : " + this.indicator);

				dataSource.put("groupHealthTbDataList", billStatementGHList);
				dataSource.put("groupTermLifeTbDataList", billStatementGTLList);
				dataSource.put("annualPremiumRatesTb1List", annualPremiumRatesTb1List);
				dataSource.put("annualPremiumRatesTb2List", annualPremiumRatesTb2List);
				dataSource.put("annualPremiumRatesTb3List", annualPremiumRatesTb3List);

				if (this.uploadReport(dataSource, companyCode.getCompanyCode())) {
					++documentCount;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return documentCount;
	}

	public boolean uploadReport(HashMap<String, Object> dataSource, String companyCode) {
		FileInputStream inputStream = null;
		BufferedOutputStream outputStream = null;

		try {

			String pdfname = null;

			String pdfFullOutputPath = null;

			String jrMainReportpath = null;
			String summaryBillingGhsSubReport = null;
			String summaryBillingGTLSubReport = null;
			if (companyCode.trim().equalsIgnoreCase("Co3")) {
				this.companyCode = 3;
				if (((String) dataSource.get("templetType")).equalsIgnoreCase("GI")) {

					jrMainReportpath = jasper
							+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\SummarBillingStatement\\summaryBilling.jasper";
					summaryBillingGhsSubReport = jasper
							+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\SummarBillingStatement\\summaryBillingGhsSubReport.jasper";
					summaryBillingGTLSubReport = jasper
							+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\SummarBillingStatement\\summarybillingGTLSubreport.jasper";
					pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_SummaryBillingStmt.pdf";
					pdfFullOutputPath = this.outputPath + "/" + companyCode + "/"
							+ this.doc_creation_dt.replace("-", "");
				} else {
					jrMainReportpath = jasper
							+ "PrintingAgentReports\\premiumandbilling\\conventional\\SummarBillingStatement\\summaryBilling.jasper";
					summaryBillingGhsSubReport = jasper
							+ "PrintingAgentReports\\premiumandbilling\\conventional\\SummarBillingStatement\\summaryBillingGhsSubReport.jasper";
					summaryBillingGTLSubReport = jasper
							+ "PrintingAgentReports\\premiumandbilling\\conventional\\SummarBillingStatement\\summarybillingGTLSubreport.jasper";
					pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_SummaryBillingStmt.pdf";

					pdfFullOutputPath = this.outputPath + "/" + companyCode + "/"
							+ this.doc_creation_dt.replace("-", "");
				}
			}
			if (companyCode.trim().equalsIgnoreCase("Co4")) {
				this.companyCode = 4;
				jrMainReportpath = jasper
						+ "PrintingAgentReports\\premiumandbilling\\takaful\\SummarBillingStatement\\summaryBilling.jasper";
				summaryBillingGhsSubReport = jasper
						+ "PrintingAgentReports\\premiumandbilling\\takaful\\SummarBillingStatement\\summaryBillingGhsSubReport.jasper";
				summaryBillingGTLSubReport = jasper
						+ "PrintingAgentReports\\premiumandbilling\\takaful\\SummarBillingStatement\\summarybillingGTLSubreport.jasper";
				pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_SummaryBillingStmt.pdf";
				pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
			}
			inputStream = new FileInputStream(jrMainReportpath);

			JasperReport subReport1 = (JasperReport) JRLoader.loadObjectFromFile(summaryBillingGhsSubReport);
			JasperReport subReport2 = (JasperReport) JRLoader.loadObjectFromFile(summaryBillingGTLSubReport);
			dataSource.put("summaryBillingGhsSubReport", subReport1);
			dataSource.put("summaryBillingGTLSubReport", subReport2);
			dataSource.put("logo", logo);

			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());// for

			File dir = new File(pdfFullOutputPath);
			if (!dir.exists()) {
				if (dir.mkdirs()) {
					System.out.println("directories are created! " + dir.getAbsolutePath());
				} else {
					System.out.println("failed to create directories ! " + pdfFullOutputPath);

				}
			}

			File file = new File(dir.getAbsolutePath() + "/" + pdfname);
			if (!file.exists()) {
				file.createNewFile();
				// System.out.println("directories are created @@@@@@@@....
				// "+file.getAbsoluteFile());

			}

			outputStream = new BufferedOutputStream(new FileOutputStream(file));
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

			PDDocument doc = PDDocument.load(new File(file.getAbsolutePath()));
			int page_count = doc.getNumberOfPages();

			byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			String dataId = UUID.randomUUID().toString();
			boolean add = dbcmd.checktblDmDoc(this.companyCode, proposalNo, this.doc_creation_dt, docType, bill_no);
			if (add) {
				dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

				dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm,
						this.doc_creation_dt, this.companyCode, client_no, client_name, bill_no, bill_type,
						sub_client_no, sub_client_name, file_format, proposal_type, this.indicator, page_count);

			} else {
				dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

				dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm,
						this.doc_creation_dt, this.companyCode, client_no, client_name, bill_no, bill_type,
						sub_client_no, sub_client_name, file_format, proposal_type, this.indicator, page_count);
			}

		} catch (Exception e) {
			System.out.println("Exception occurred : " + e);
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
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

	public static HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getSummaryBillingSattementDetails(
			String filePath) {

		BufferedReader br = null;
		HashMap<Integer, HashMap<String, Object>> billingStatementRS = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> billingStatementRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No BillingSattement Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {

					Boolean add = false;

					HashMap<String, Object> billingStatement = new HashMap<String, Object>();

					if (cuurline == 0 || sCurrentLine.contains("****")) {
						billingStatement = new HashMap<String, Object>();
						billingStatementRS = new HashMap<Integer, HashMap<String, Object>>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}
					String[] data = sCurrentLine.split("\\|");

					if (data[0].equalsIgnoreCase("0001") || data[0].equalsIgnoreCase("0002")
							|| data[0].equalsIgnoreCase("0004")) {
						add = true;

					}
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
						billingStatement.put("policyHolder",
								data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						billingStatement.put("subsidiary",
								data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						billingStatement.put("policyNum",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						billingStatement.put("policyPeriod",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						billingStatement.put("billNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billingStatement.put("dateOfIssue",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						billingStatement.put("billingPeriod",
								data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						billingStatement.put("billingFrequecy",
								data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						billingStatement.put("adjustmentFrequency",
								data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						billingStatement.put("policyHolderNum",
								data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						billingStatement.put("subsidiaryNum",
								data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						billingStatement.put("authorisedPerson",
								data[13] != null && data[13].length() > 0 ? data[13].trim() : "");

						if (data.length >= 15) {
							billingStatement.put("phoneNum",
									data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
						}
						if (data.length >= 16) {
							billingStatement.put("portalUploadStatus",
									data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
						}
						if (data.length >= 17) {
							billingStatement.put("printHardCp",
									data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
						}
						if (data.length >= 18) {
							billingStatement.put("templetType",
									data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
						}
						if (data.length >= 19) {
							billingStatement.put("policyType",
									data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
						}
						if (data.length >= 20) {
							billingStatement.put("policyTypeDscr",
									data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
						}

					}
					if (data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("2H")
							&& data[2].equalsIgnoreCase("GHS")) {
						billingStatement.put("ghsGroup", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0004") && data[1].equalsIgnoreCase("1T")
							&& data[2].equalsIgnoreCase("GHS")) {
						billingStatement.put("ghsTtlAmntExclStPremium",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						billingStatement.put("ghsTtlAmntExclStLoadingPremium",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						billingStatement.put("ghsTtlAmntExclStAdminVitalityFee",
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billingStatement.put("ghsTtlAmntExclStTtlPremiumAdminFee",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
					}

					if (data[0].equalsIgnoreCase("0004") && data[1].equalsIgnoreCase("2T")
							&& data[2].equalsIgnoreCase("GHS")) {
						billingStatement.put("ghsStPremium",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						billingStatement.put("ghsStLoadingPremium",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						billingStatement.put("ghsStAdminVitalityFee",
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billingStatement.put("ghsStTtlPremiumAdminFee",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0004") && data[1].equalsIgnoreCase("3T")
							&& data[2].equalsIgnoreCase("GHS")) {
						billingStatement.put("ghsTtlAmntInclStPremium",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						billingStatement.put("ghsTotalAmntInclStLoadingPremium",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						billingStatement.put("ghsTtlAmntInclStAdminVitalityFee",
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billingStatement.put("ghsTtlAmntInclStTtlPremiumAdminFee",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("2H")
							&& data[2].equalsIgnoreCase("GTL")) {
						billingStatement.put("gtlGroup", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0004") && data[1].equalsIgnoreCase("1T")
							&& data[2].equalsIgnoreCase("GTL")) {
						billingStatement.put("gtlTtlAmntExclStPremium",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						billingStatement.put("gtlTtlAmntExclStLoadingPremium",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						billingStatement.put("gtlTtlAmntExclStAdminVitalityFee",
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billingStatement.put("gtlTtlAmntExclStTtlPremiumAdminFee",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0004") && data[1].equalsIgnoreCase("2T")
							&& data[2].equalsIgnoreCase("GTL")) {
						billingStatement.put("gtlStPremium",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						billingStatement.put("gtlStLoadingPremium",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						billingStatement.put("gtlStAdminVitalityFee",
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billingStatement.put("gtlStTtlPremiumAdminFee",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
					}

					if (data[0].equalsIgnoreCase("0004") && data[1].equalsIgnoreCase("3T")
							&& data[2].equalsIgnoreCase("GTL")) {
						billingStatement.put("gtlTtlAmntInclStPremium",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						billingStatement.put("gtlTtlAmntInclStLoadingPremium",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						billingStatement.put("gtlTtlAmntInclStAdminVitalityFee",
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billingStatement.put("gtlTtlAmntInclStTtlPremiumAdminFee",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
					}

					if (add) {
						billingStatementRS.put(cuurline, billingStatement);
						cuurline++;
						billingStatementRSDetails.put(pdfgencount, billingStatementRS);
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println(
					"[SummaryBillingStatementService.getSummaryBillingSattementDetails] Exception: " + e.toString());
			e.printStackTrace();
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
		return billingStatementRSDetails;
	}

	public static HashMap<Integer, List<BillingStatementGroupHealthTbData>> getBillingStatementGroupHealthTbData(
			String filePath) {
		BufferedReader br = null;
		List<BillingStatementGroupHealthTbData> billStatementGHList = new ArrayList<BillingStatementGroupHealthTbData>();
		HashMap<Integer, List<BillingStatementGroupHealthTbData>> billStatementGHListDetails = new HashMap<Integer, List<BillingStatementGroupHealthTbData>>();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No BillingStatement Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					BillingStatementGroupHealthTbData billStatementGH = new BillingStatementGroupHealthTbData();

					if (sCurrentLine.contains("****")) {
						billStatementGH = new BillingStatementGroupHealthTbData();
						billStatementGHList = new ArrayList<BillingStatementGroupHealthTbData>();
						pdfgencount++;
					}
					String data[] = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0003") && data[1].equalsIgnoreCase("3D")
							&& data[2].equalsIgnoreCase("GHS")) {
						billStatementGH.setGhsPlan(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						billStatementGH
								.setGhsPlanDscrp((data[4] != null && data[4].length() > 0 ? data[4].trim() : ""));
						String relationship = data[5] != null && data[5].length() > 0 ? data[5].trim() : "";
						String relation = "";
						if (relationship.equalsIgnoreCase("01")) {
							relation = "Employee";
						}
						if (relationship.equalsIgnoreCase("02")) {
							relation = "Spouse";
						}
						if (relationship.equalsIgnoreCase("03")) {
							relation = "Child";
						}
						billStatementGH.setGhsRelationShip(relation);
						billStatementGH.setGhsProduct(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						billStatementGH
								.setGhsNumOfMemb((data[7] != null && data[7].length() > 0 ? data[7].trim() : ""));
						billStatementGH.setGhsProposedSumAssured(
								data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						billStatementGH.setGhsAcceptedSumAssured(
								data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						billStatementGH.setGhsPremium(data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						billStatementGH
								.setGhsLoadingPremium(data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						billStatementGH.setGhsAdminVitalityFee(
								data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						billStatementGH.setGhsTotalPremiumAdminFee(
								data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
					}
					if (data[0].equalsIgnoreCase("0003") && data[1].equalsIgnoreCase("3D")
							&& data[2].equalsIgnoreCase("GHS")) {
						billStatementGHList.add(billStatementGH);
						billStatementGHListDetails.put(pdfgencount, billStatementGHList);
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println(
					"[SummaryBillingStatementService.getBillingStatementGroupHealthTbData] Exception: " + e.toString());
			e.printStackTrace();
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
		return billStatementGHListDetails;
	}

	public static HashMap<Integer, List<BillingStatementGroupTermLifeTbdata>> getBillingStatementGroupTermLifeTbdata(
			String filePath) {
		List<BillingStatementGroupTermLifeTbdata> billStatementGTLList = new ArrayList<BillingStatementGroupTermLifeTbdata>();
		HashMap<Integer, List<BillingStatementGroupTermLifeTbdata>> billStatementGTLListDetails = new HashMap<Integer, List<BillingStatementGroupTermLifeTbdata>>();
		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No SummaryBillingStatement Flat file ");
			} else {
				String sCurrentLine;
				int currentLint = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					BillingStatementGroupTermLifeTbdata billStatementGTL = new BillingStatementGroupTermLifeTbdata();
					boolean add = false;
					if (currentLint == 0 || sCurrentLine.contains("****")) {
						billStatementGTL = new BillingStatementGroupTermLifeTbdata();
						billStatementGTLList = new ArrayList<BillingStatementGroupTermLifeTbdata>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						currentLint = 0;
					}

					String data[] = sCurrentLine.split("\\|");
					for (int i = 0; i < data.length; i++) {
						if (data[0].equalsIgnoreCase("0003")) {
							add = true;

						}
						if (data[0].equalsIgnoreCase("0003") && data[1].equalsIgnoreCase("3D")
								&& data[2].equalsIgnoreCase("GTL")) {
							billStatementGTL.setGtlPlan(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
							billStatementGTL
									.setGtlPlanDscrp(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");

							String relationship = data[5] != null && data[5].length() > 0 ? data[5].trim() : "";
							String relation = "";
							if (relationship.equalsIgnoreCase("01")) {
								relation = "Employee";
							}
							if (relationship.equalsIgnoreCase("02")) {
								relation = "Spouse";
							}
							if (relationship.equalsIgnoreCase("03")) {
								relation = "Child";
							}
							billStatementGTL.setGtlRelationShip(relation);
							billStatementGTL
									.setGtlProduct(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
							billStatementGTL
									.setGtlNumOfMem(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
							billStatementGTL.setGtlproposedSumAssured(
									data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
							billStatementGTL.setGtlAcceptedSumAssured(
									data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
							billStatementGTL
									.setGtlPremium(data[10] != null && data[10].length() > 0 ? data[10].trim() : "");

							billStatementGTL.setGtlLoadingPremium(
									data[11] != null && data[11].length() > 0 ? data[11].trim() : "");

							if (data.length >= 13) {
								billStatementGTL.setGtlAdminVetalityFee(
										data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
							}
							if (data.length >= 14) {
								billStatementGTL.setGtlTtlPremiumAdminFee(
										data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
							}
						}

					}
					if (add) {
						if (data[0].equalsIgnoreCase("0003") && data[1].equalsIgnoreCase("3D")
								&& data[2].equalsIgnoreCase("GTL")) {
							billStatementGTLList.add(billStatementGTL);
						}
					}
					currentLint++;
					billStatementGTLListDetails.put(pdfgencount, billStatementGTLList);
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("[SummaryBillingStatementService.getBillingstatementGroupTermLifeTbdata] Exception: "
					+ e.toString());
			e.printStackTrace();
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
		return billStatementGTLListDetails;
	}

	public static HashMap<Integer, List<AnnualPremiumRatesTb1>> getAnnualPremiumRatesTb1Data(String filePath) {

		BufferedReader br = null;
		List<AnnualPremiumRatesTb1> annualPremiumRatesTb1List = new ArrayList<AnnualPremiumRatesTb1>();
		HashMap<Integer, List<AnnualPremiumRatesTb1>> annualPremiumRatesTb1ListDetails = new HashMap<Integer, List<AnnualPremiumRatesTb1>>();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No SummaryBillingStatement Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					AnnualPremiumRatesTb1 annualPremiumRatesTb1 = new AnnualPremiumRatesTb1();

					if (sCurrentLine.contains("****")) {
						annualPremiumRatesTb1 = new AnnualPremiumRatesTb1();
						annualPremiumRatesTb1List = new ArrayList<AnnualPremiumRatesTb1>();
						pdfgencount++;

					}

					String data[] = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0005") && data[1].equalsIgnoreCase("5T")) {

						if (data.length >= 3) {
							annualPremiumRatesTb1
									.setAprTb1Plan(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						}
						if (data.length >= 4) {
							annualPremiumRatesTb1
									.setAprTb1Prod(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						}
						if (data.length >= 5) {
							annualPremiumRatesTb1
									.setAprTb1Emp(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						}
						if (data.length >= 6) {
							annualPremiumRatesTb1
									.setAprTb1EmpSpouse(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						}
						if (data.length >= 7) {
							annualPremiumRatesTb1.setAprTb1EmpChildren(
									data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if (data.length >= 8) {
							annualPremiumRatesTb1
									.setAprTb1EmpFamily(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}

					}

					if (data[0].equalsIgnoreCase("0005") && data[1].equalsIgnoreCase("5T")) {
						annualPremiumRatesTb1List.add(annualPremiumRatesTb1);

					}

					annualPremiumRatesTb1ListDetails.put(pdfgencount, annualPremiumRatesTb1List);
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
		return annualPremiumRatesTb1ListDetails;
	}

	public HashMap<Integer, List<AnnualPremiumRatesTb2>> getAnnualPremiumRatesTb2Data(String filePath) {

		List<AnnualPremiumRatesTb2> annualPremiumRatesTb2List = new ArrayList<AnnualPremiumRatesTb2>();
		HashMap<Integer, List<AnnualPremiumRatesTb2>> annualPremiumRatesTb2ListDetails = new HashMap<Integer, List<AnnualPremiumRatesTb2>>();
		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No SummaryBillingStatement Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					AnnualPremiumRatesTb2 annualPremiumRatesTb2 = new AnnualPremiumRatesTb2();

					if (sCurrentLine.contains("****")) {
						annualPremiumRatesTb2 = new AnnualPremiumRatesTb2();
						annualPremiumRatesTb2List = new ArrayList<AnnualPremiumRatesTb2>();
						pdfgencount++;
					}

					String data[] = sCurrentLine.split("\\|");

					if (data[0].equalsIgnoreCase("0006") && data[1].equalsIgnoreCase("6T")) {

						if (data.length >= 3) {
							annualPremiumRatesTb2
									.setAprTb2Plan(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						}
						if (data.length >= 4) {
							annualPremiumRatesTb2
									.setAprTb2Prod(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						}
						if (data.length >= 5) {
							annualPremiumRatesTb2
									.setAprTb2Emp(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						}
						if (data.length >= 6) {
							annualPremiumRatesTb2
									.setAprTb2EmpSpouse(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						}
						if (data.length >= 7) {
							annualPremiumRatesTb2.setAprTb2EmpChildren(
									data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if (data.length >= 8) {
							annualPremiumRatesTb2
									.setAprTb2EmpFamily(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}

					}
					if (data[0].equalsIgnoreCase("0006") && data[1].equalsIgnoreCase("6T")) {
						annualPremiumRatesTb2List.add(annualPremiumRatesTb2);

					}
					annualPremiumRatesTb2ListDetails.put(pdfgencount, annualPremiumRatesTb2List);
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
		return annualPremiumRatesTb2ListDetails;
	}

	public static HashMap<Integer, List<AnnualPremiumRatesTb3>> getAnnualPremiumRatesTb3Data(String filePath) {

		BufferedReader br = null;
		List<AnnualPremiumRatesTb3> annualPremiumRatesTb3List = new ArrayList<AnnualPremiumRatesTb3>();
		HashMap<Integer, List<AnnualPremiumRatesTb3>> annualPremiumRatesTb3ListDetails = new HashMap<Integer, List<AnnualPremiumRatesTb3>>();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			if (br == null || br.equals("")) {
				System.out.println("No SummaryBillingStatement Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					AnnualPremiumRatesTb3 annualPremiumRatesTb3 = new AnnualPremiumRatesTb3();

					if (sCurrentLine.contains("****")) {
						annualPremiumRatesTb3 = new AnnualPremiumRatesTb3();
						annualPremiumRatesTb3List = new ArrayList<AnnualPremiumRatesTb3>();
						pdfgencount++;

					}

					String data[] = sCurrentLine.split("\\|");

					if (data[0].equalsIgnoreCase("0007") && data[1].equalsIgnoreCase("7T")) {
						annualPremiumRatesTb3
								.setAprTb3Plan(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						annualPremiumRatesTb3
								.setAprTb3Prod(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						annualPremiumRatesTb3
								.setAprTb3Rate(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
					}

					if (data[0].equalsIgnoreCase("0007") && data[1].equalsIgnoreCase("7T")) {
						annualPremiumRatesTb3List.add(annualPremiumRatesTb3);
					}
					annualPremiumRatesTb3ListDetails.put(pdfgencount, annualPremiumRatesTb3List);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			{
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return annualPremiumRatesTb3ListDetails;
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
