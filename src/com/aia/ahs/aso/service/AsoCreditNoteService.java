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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

@Service("asoCreditNoteService")
public class AsoCreditNoteService implements TemplateActions {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsoCreditNoteService.class);

	@Autowired
	private DBCSDCommon dbcmd;

	@Value("${print.agent.fileoutput.path}")
	private String outputPath;

	String jasperpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+ "../../jasper/";
	String jasper = FilenameUtils.normalize(jasperpath, true);

	
	String imgpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
			+ "../../img/";
	String logo = FilenameUtils.normalize(imgpath, true);
	
	/*String logo = FilenameUtils.normalize(
			this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../img/", true);*/

	SimpleDateFormat sdf = new SimpleDateFormat("YYYY");

	SimpleDateFormat ymd = new SimpleDateFormat("YYYY-MM-dd");

	private static final String docType = "ASOCN";

	private String file_format = "pdf";

	private String doc_creation_dt;

	private String year;

	private String tableName;

	private String tbl_doc_nm;

	private String process_year;

	private Integer companyCode;

	private Integer dmStatus = 1;

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
		LOGGER.info(jasper);
		int documentCount = 0;
		try {
			if (batchFileDetails.getStatus().equalsIgnoreCase("RECONCILIATION_SUCCESS")) {
				LOGGER.info("RECONCILIATION PROCESS COMPLETED FOR THE FILENAME {} AND FOR THE CYCLE DATE {}",
						batchFileDetails.getFileName(), batchCycle.getCycleDate());
				return -1;
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			this.g4CycleDate = batchCycle.getCycleDate();
			this.doc_creation_dt = ymd.format(dateFormat.parse(this.g4CycleDate));
			this.year = sdf.format(dateFormat.parse(this.g4CycleDate));
			this.tableName = "tbl_asocn_" + this.year;
			this.tbl_doc_nm = "[aiaIMGdb_CSD_" + this.year + "]..[" + this.tableName + "]";
			this.process_year = year;

			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> asoCreditNoteRSDetails = getAsoCreditNoteDetails(
					batchFileDetails.getFileLocation());

			int noFiles = asoCreditNoteRSDetails.size();
			for (int i = 0; i < noFiles; i++) {
				HashMap<Integer, HashMap<String, Object>> asoCreditNoteRS = asoCreditNoteRSDetails.get(i);

				HashMap<String, Object> dataSource = new HashMap<String, Object>();
				for (int a = 0; a < asoCreditNoteRS.size(); a++) {
					dataSource.putAll(asoCreditNoteRS.get(a));
				}
				this.proposalNo = (String) dataSource.get("policyNum");
				this.client_no = (String) dataSource.get("policyHolderNum");
				this.client_name = (String) dataSource.get("policyHolder");
				this.bill_no = (String) dataSource.get("billNum");

				this.proposal_type = (String) dataSource.get("policyType");
				this.sub_client_no = (String) dataSource.get("subsidiaryNum");
				this.sub_client_name = (String) dataSource.get("subsidiary");
				if(this.sub_client_name==null || this.sub_client_name.isEmpty() || this.sub_client_name.equalsIgnoreCase("-")){
					this.sub_client_name=this.client_name;
				}
				this.indicator = (String) dataSource.get("printHardCp");

				if(this.uploadReport(dataSource, companyCode.getCompanyCode())) {
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
			String pdfFullOutputPath = "";
			String jrFullReadpath = "";
			String pdfname = "";

			if (companyCode.equalsIgnoreCase("Co3")) {
				this.companyCode = 3;
				jrFullReadpath = this.jasper + "/PrintingAgent_CSD/WebContent/jasper/PrintingAgentReports/AHS/conventional/ASO/AsoCreditNote.jasper";
				String billmonth = "" + dataSource.get("billMonth");
				String billperiod = billmonth.replace("/", "");
				pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum")
						+ "_ASOCN.pdf";
				pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
			} else if (companyCode.equalsIgnoreCase("Co4")) {
				this.companyCode = 4;
				jrFullReadpath = this.jasper + "PrintingAgentReports\\AHS\\takaful\\ASO\\AsoCreditNote.jasper";
				String billmonth = "" + dataSource.get("billMonth");
				String billperiod = billmonth.replace("/", "");
				pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum")
						+ "_ASOCN.pdf";
				pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
			}
			dataSource.put("logo", this.logo);
			inputStream = new FileInputStream(jrFullReadpath);
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());// for
			File dir = new File(pdfFullOutputPath);
			if (!dir.exists()) {
				if (dir.mkdirs()) {
					System.out.println("directories are created! " + pdfFullOutputPath);
				} else {
					System.out.println("failed to create directories ! " + pdfFullOutputPath);

				}
			}
			File file = new File(dir.getAbsolutePath() + "/" + pdfname);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("directories are created @@@@@@@@.... " + file.getAbsoluteFile());
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

	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getAsoCreditNoteDetails(String filepath) {

		HashMap<Integer, HashMap<String, Object>> asoCreditNotetRS = new HashMap<Integer, HashMap<String, Object>>();
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> asoCreditNotetRSDetails = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
			if (br == null || br.equals("")) {
				System.out.println("No AsoCreditNote Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					HashMap<String, Object> asoCreditNote = new HashMap<String, Object>();

					if (cuurline == 0 || sCurrentLine.contains("****")) {
						asoCreditNote = new HashMap<String, Object>();
						asoCreditNotetRS = new HashMap<Integer, HashMap<String, Object>>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}
					String[] data = sCurrentLine.split("\\|");

					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
						asoCreditNote.put("companyName", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						asoCreditNote.put("addressLine1",
								data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						asoCreditNote.put("addressLine2",
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						asoCreditNote.put("addressLine3",
								data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						asoCreditNote.put("addressLine4",
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						asoCreditNote.put("addressLine5",
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						asoCreditNote.put("billNum", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						asoCreditNote.put("dateOfIssue", data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						asoCreditNote.put("billMonth",
								data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						asoCreditNote.put("paymentDueDate",
								data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						asoCreditNote.put("authorisedPerson",
								data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						asoCreditNote.put("phoneNum", data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
						asoCreditNote.put("portalUploadStatus",
								data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
						if (data.length >= 16) {
							asoCreditNote.put("printHardCp",
									data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
						}
						if (data.length >= 17) {
							asoCreditNote.put("policyType",
									data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
						}
						if (data.length >= 18) {
							asoCreditNote.put("policyTypeDscr",
									data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
						}
						if (data.length >= 19) {
							asoCreditNote.put("bankAcNo",
									data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
						}
						if (data.length >= 20) {
							asoCreditNote.put("bankName",
									data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
						}

					}
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("2H")) {
						asoCreditNote.put("policyHolder",
								data[2] != null && data[2].length() > 0 ? data[2].trim() : "");

						asoCreditNote.put("policyHolderNum",
								data[3] != null && data[3].length() > 0 ? data[3].trim() : "");

						asoCreditNote.put("subsidiary", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");

						if (data.length >= 6) {
							asoCreditNote.put("subsidiaryNum",
									data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						}
						if (data.length >= 7) {
							asoCreditNote.put("policyNum",
									data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}

						if (data.length >= 8) {
							asoCreditNote.put("poNum", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}

						if (data.length >= 9) {
							asoCreditNote.put("amnt", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						}
					}
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1T")) {
						asoCreditNote.put("totalAmnt", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
					}

					if (data[0].equalsIgnoreCase("0001")) {
						asoCreditNotetRS.put(cuurline, asoCreditNote);
						cuurline++;
						asoCreditNotetRSDetails.put(pdfgencount, asoCreditNotetRS);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("[AsoCreditNoteService.getAsoCreditNoteeDetails] Exception: " + e.toString());
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
		return asoCreditNotetRSDetails;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return cycledate;
	}

	public static void main(String[] args) {
		CompanyCode code = new CompanyCode();
		code.setCompanyCode("Co3");

		BatchFileDetails batchFileDetails = new BatchFileDetails();
		batchFileDetails.setFileLocation(
				"D:\\PrintAgenttext\\company3\\20190701\\Co3_ASOCN_ ASO Credit Note_20190701_0079.txt");

		BatchCycle batchCycle = new BatchCycle();
		batchCycle.setCycleDate("20190701");

		AsoCreditNoteService a = new AsoCreditNoteService();
		// String filepath = "D:\\PrintAgenttext\\company3\\20190701\\Co3_ASOCN_ ASO
		// Credit Note_20190701_0079.txt";
		// a.genReport(filepath, "Co3");

		a.genReport(code, batchCycle, batchFileDetails);

	}
}
