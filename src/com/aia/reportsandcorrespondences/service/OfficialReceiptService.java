package com.aia.reportsandcorrespondences.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service("officialReceiptService")
public class OfficialReceiptService implements TemplateActions {
	private static final Logger LOGGER = LoggerFactory.getLogger(OfficialReceiptService.class);
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

	private static final String docType = "COROR";

	private String tableName;
	private String year;
	private String tbl_doc_nm;;
	private String process_year;
	private String doc_creation_dt;
	private String file_format = "pdf";
	private Integer dmStatus = 1;

	private String proposalNo;
	private String client_name;
	private String bill_no;
	private int companyCode;

	private String g4CycleDate;

	@Override
	public int genReport(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {

		int documentCount = 0;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			this.g4CycleDate = batchCycle.getCycleDate();
			this.doc_creation_dt = ymd.format(dateFormat.parse(this.g4CycleDate));
			this.year = sdf.format(dateFormat.parse(this.g4CycleDate));
			this.tableName = "tbl_coror_" + this.year;
			this.tbl_doc_nm = "[aiaIMGdb_CSD_" + this.year + "]..[" + tableName + "]";
			this.process_year = this.year;

			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listRSDetails = getOfficialReceiptDetails(
					batchFileDetails.getFileLocation());
			int noFiles = listRSDetails.size();
			for (int i = 0; i < noFiles; i++) {
				HashMap<Integer, HashMap<String, Object>> detailsRS = listRSDetails.get(i);
				HashMap<String, Object> dataSource = new HashMap<String, Object>();
				for (int a = 0; a < detailsRS.size(); a++) {
					HashMap<String, Object> details = detailsRS.get(a);
					dataSource.putAll(details);
				}

				System.out.println("Indicater   : " + dataSource.get("templetType"));

				this.proposalNo = (String) dataSource.get("policyNum");
				this.client_name = (String) dataSource.get("companyName");
				this.bill_no = (String) dataSource.get("receiptNum");
				if (this.uploadReport(dataSource, companyCode.getCompanyCode())) {
					++documentCount;
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return documentCount;
	}

	public boolean uploadReport(HashMap<String, Object> dataSource, String companyCode) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		PDDocument doc = null;
		try {
			String date = (String) dataSource.get("date");
			String pdfname = "";
			String pdfFullOutputPath = "";
			String jrFullReadpath = "";
			if (companyCode.trim().equalsIgnoreCase("Co3")) {
				this.companyCode = 3;
				if (((String) dataSource.get("templetType")).equalsIgnoreCase("GI")) {
					if (dataSource.get("receiptType").equals("Y")) {
						jrFullReadpath = this.jasper
								+ "PrintingAgentReports\\ReportsAndCorrespondences\\officialReceipt\\generalinsurence\\OfficialReceipt.jasper";
					} else {
						jrFullReadpath = this.jasper
								+ "PrintingAgentReports\\ReportsAndCorrespondences\\officialReceipt\\generalinsurence\\CancellationOfficialReceipt.jasper";
					}
					pdfFullOutputPath = this.outputPath + "/oficialReceipt/" + companyCode + "/"
							+ this.doc_creation_dt.replace("-", "");
					pdfname = dataSource.get("receiptNum") + "_" + dataSource.get("policyNum") + "_"
							+ date.replace("/", "") + "_OfficialReceipt.pdf";
				} else {

					if (dataSource.get("receiptType").equals("Y")) {
						jrFullReadpath = this.jasper
								+ "PrintingAgentReports\\ReportsAndCorrespondences\\officialReceipt\\conventional\\OfficialReceipt.jasper";
					} else {
						jrFullReadpath = this.jasper
								+ "PrintingAgentReports\\ReportsAndCorrespondences\\officialReceipt\\conventional\\CancellationOfficialReceipt.jasper";
					}
					pdfFullOutputPath = this.outputPath + "/oficialReceipt/" + companyCode + "/"
							+ this.doc_creation_dt.replace("-", "");
					pdfname = dataSource.get("receiptNum") + "_" + dataSource.get("policyNum") + "_"
							+ date.replace("/", "") + "_OfficialReceipt.pdf";

				}

			}

			if (companyCode.trim().equalsIgnoreCase("Co4")) {
				this.companyCode = 4;

				if (dataSource.get("receiptType").equals("Y")) {
					jrFullReadpath = this.jasper
							+ "PrintingAgentReports\\ReportsAndCorrespondences\\officialReceipt\\takaful\\TkfOfficialReceipt.jasper";
				} else {
					jrFullReadpath = this.jasper
							+ "\\PrintingAgentReports\\ReportsAndCorrespondences\\officialReceipt\\takaful\\TkfCancellationOfficialReceipt.jasper";
				}
				pdfFullOutputPath = this.outputPath + "/oficialReceipt/" + companyCode + "/"
						+ this.doc_creation_dt.replace("-", "");
				pdfname = dataSource.get("receiptNum") + "_" + dataSource.get("policyNum") + "_" + date.replace("/", "")
						+ "_OfficialReceipt.pdf";

			}

			inputStream = new FileInputStream(jrFullReadpath);

				dataSource.put("logo", logo);
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());// for
																														// compiled
																														// Report
																														// .jrxml
																														// file

			File dir = new File(pdfFullOutputPath);
			if (!dir.exists()) {
				dir.mkdirs();
				System.out.println("directories are created!  @@@@@@@@@@@@@@@@@ " + pdfFullOutputPath);
			}

			System.out.println("PDF name ================>:" + pdfname);
			File file = new File(dir.getAbsolutePath() + "/" + pdfname);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("directories are created!  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ "
						+ file.getAbsoluteFile());

			}

			outputStream = new FileOutputStream(file);
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			System.out.println("PDF Generated..." + file.getAbsolutePath());

			doc = PDDocument.load(new File(file.getAbsolutePath()));
			int page_count = doc.getNumberOfPages();
			System.out.println("Pages in PDF====> : " + page_count);

			byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

			String dataId = UUID.randomUUID().toString();

			boolean add = dbcmd.checktblDmDoc(this.companyCode, proposalNo, this.doc_creation_dt, docType, bill_no);
			if (add) {
				dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

				dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm,
						this.doc_creation_dt, this.companyCode, "", client_name, bill_no, "", "", "", file_format, "",
						"", page_count);

			} else {
				dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

				dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm,
						this.doc_creation_dt, this.companyCode, "", client_name, bill_no, "", "", "", file_format, "",
						"", page_count);
			}

		} catch (Exception e) {
			System.out.println("Exception occurred : " + e);
			e.printStackTrace();
		} finally {
			try {
				if (doc != null) {
					doc.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getOfficialReceiptDetails(String filePath) {

		HashMap<Integer, HashMap<String, Object>> detailsRS = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> listdetailsRS = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();

		BufferedReader br = null;
		FileReader fr = null;

		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);

			if (br == null || br.equals(null)) {
				System.out.println("No official Receipt  Flat File....");
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

						details.put("companyName", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");

						details.put("addressLine1", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");

						details.put("addressLine2", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");

						details.put("addressLine3", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");

						details.put("addressLine4", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");

						details.put("receiptNum", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");

						details.put("issuedBy", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");

						if (data.length >= 10) {
							details.put("date", data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						}
						if (data.length >= 11) {
							details.put("policyNum", data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						}
						if (data.length >= 12) {
							details.put("templetType",
									data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						}
					}
					if (data[0].equalsIgnoreCase("0002") && data[1].equalsIgnoreCase("2H")) {

						details.put("companyHolder", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						details.put("sumOf", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						String amnt = data[4] != null && data[4].length() > 0 ? data[4].trim() : "";
						details.put("amount", amnt.replace("-", ""));
						details.put("reversalOf", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						details.put("chequeOrTT", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						// if ---> Y officieal OR if N cancelation receipt
						details.put("receiptType", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");

					}

					if (data[0].equalsIgnoreCase("0001") || data[0].equalsIgnoreCase("0002")) {
						detailsRS.put(currentLint, details);
						currentLint++;
						listdetailsRS.put(pdfGencount, detailsRS);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("[OfficialReceiptService.getOfficialReceiptDetails()]  Exception : " + e.toString());
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
