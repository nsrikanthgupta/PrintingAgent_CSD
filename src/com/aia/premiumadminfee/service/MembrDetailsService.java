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
import com.aia.premiumadminfee.model.MemberDetails;
import com.aia.premiumadminfee.model.MemberDetilsTableData;
import com.aia.premiumadminfee.model.MembrDetailsGrandTotal;
import com.aia.premiumadminfee.model.MembrDetailsSubTotal;
import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.service.TemplateActions;
import com.aia.premiumadminfee.model.MemberDetailsNoteForPageFooter;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Service("membrDetailsService")
public class MembrDetailsService implements TemplateActions {
	private static final Logger LOGGER = LoggerFactory.getLogger(MembrDetailsService.class);

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
	private static final String docType = "PAFMD";
	private String doc_creation_dt;
	private String year;
	private String tableName;
	private String tbl_doc_nm;
	private String process_year;
	private String file_format = "pdf";
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

		int documentCount = 0;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			this.g4CycleDate = batchCycle.getCycleDate();
			this.doc_creation_dt = ymd.format(dateFormat.parse(this.g4CycleDate));
			this.year = sdf.format(dateFormat.parse(this.g4CycleDate));
			this.tableName = "tbl_pafmd_" + this.year;
			this.tbl_doc_nm = "[aiaIMGdb_CSD_" + this.year + "]..[" + this.tableName + "]";
			this.process_year = this.year;

			HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> membrDetailsdataRS = getMembrDetailsReportDetails(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<MemberDetails>> memberDetailsListRS = getMemberDetailsTableDetails(
					batchFileDetails.getFileLocation());
			HashMap<Integer, List<MemberDetailsNoteForPageFooter>> listNoteForPageFooterRS = getNoteForFooter(
					batchFileDetails.getFileLocation());

			int noFile = membrDetailsdataRS.size();
			for (int i = 0; i < noFile; i++) {
				HashMap<String, Object> dataSource = new HashMap<String, Object>();
				HashMap<Integer, HashMap<String, Object>> membrDetailsdata = membrDetailsdataRS.get(i);
				List<MemberDetailsNoteForPageFooter> listNoteForPageFooter = listNoteForPageFooterRS.get(i);

				for (int j = 0; j < membrDetailsdata.size(); j++) {
					dataSource.putAll(membrDetailsdata.get(j));
				}
				StringBuilder relationship = new StringBuilder();

				StringBuilder medical = new StringBuilder();
				StringBuilder termLife = new StringBuilder();
				StringBuilder underWritingStatus = new StringBuilder();
				StringBuilder movementType = new StringBuilder();

				for (MemberDetailsNoteForPageFooter n : listNoteForPageFooter) {
					if (n.getRelationship() != null) {
						relationship.append(n.getRelationship());
					}
					if (n.getMedical() != null) {
						medical.append(n.getMedical());
					}
					if (n.getTermLife() != null) {
						termLife.append(n.getTermLife());
					}
					if (n.getUnderWritingStatus() != null) {
						underWritingStatus.append(n.getUnderWritingStatus());
					}
					if (n.getMovementType() != null) {
						movementType.append(n.getMovementType());
					}

				}
				String rlsp = " " + relationship;
				dataSource.put("relationship", rlsp);
				String mdcl = " " + medical;
				dataSource.put("medical", mdcl);
				String te = " " + termLife;
				dataSource.put("termLife", te);
				String unws = " " + termLife;
				dataSource.put("underWritingStatus", unws);
				String mt = " " + movementType;
				dataSource.put("movementType", mt);

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

				dataSource.put("memberDetilsList", memberDetailsListRS.get(i));
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
		FileInputStream mainreportinputStream = null;
		BufferedOutputStream outputStream = null;
		PDDocument doc = null;
		try {

			String pdfname = "";

			String pdfFullOutputPath = "";

			String jrMainReportFullReadpath = null;
			String jrSubReportFullReadpath = null;
			if (companyCode.trim().equalsIgnoreCase("Co3")) {
				this.companyCode = 3;
				if (dataSource.get("templetType").equals("GI")) {
					if (dataSource.get("VitalityFlag").equals("Y")) {
						jrMainReportFullReadpath = jasper
								+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\memberdtails\\memberdeatls_vitality.jasper";
					} else {
						jrMainReportFullReadpath = jasper
								+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\memberdtails\\memberdeatls.jasper";
					}
					jrSubReportFullReadpath = jasper
							+ "PrintingAgentReports\\premiumandbilling\\generalinsurence\\memberdtails\\memberDetailsSubReport.jasper";
					pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_memberDetails.pdf";
					pdfFullOutputPath = this.outputPath + "/" + companyCode + "/"
							+ this.doc_creation_dt.replace("-", "");
				} else {
					if (dataSource.get("VitalityFlag").equals("Y")) {
						jrMainReportFullReadpath = jasper
								+ "PrintingAgentReports\\premiumandbilling\\conventional\\memberdtails\\memberdeatls_vitality.jasper";
					} else {
						jrMainReportFullReadpath = jasper
								+ "PrintingAgentReports\\premiumandbilling\\conventional\\memberdtails\\memberdeatls.jasper";
					}
					jrSubReportFullReadpath = jasper
							+ "PrintingAgentReports\\premiumandbilling\\conventional\\memberdtails\\memberDetailsSubReport.jasper";
					pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_memberDetails.pdf";
					pdfFullOutputPath = this.outputPath + "/" + companyCode + "/"
							+ this.doc_creation_dt.replace("-", "");
				}
			}

			if (companyCode.trim().equalsIgnoreCase("Co4")) {
				this.companyCode = 4;
				if (dataSource.get("VitalityFlag").equals("Y")) {
					jrMainReportFullReadpath = jasper
							+ "PrintingAgentReports\\premiumandbilling\\takaful\\memberdtails\\memberdeatls_vitality.jasper";
				} else {
					jrMainReportFullReadpath = jasper
							+ "PrintingAgentReports\\premiumandbilling\\takaful\\memberdtails\\memberdeatls.jasper";
				}
				jrSubReportFullReadpath = jasper
						+ "PrintingAgentReports\\premiumandbilling\\takaful\\memberdtails\\memberDetailsSubReport.jasper";
				pdfname = dataSource.get("policyNum") + "_" + dataSource.get("billNum") + "_memberDetails.pdf";
				pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
			}

			mainreportinputStream = new FileInputStream(jrMainReportFullReadpath);
			JasperReport subreport = (JasperReport) JRLoader.loadObjectFromFile(jrSubReportFullReadpath);
			dataSource.put("memberDetilsSubReport", subreport);
			dataSource.put("logo", logo);
			JasperPrint jasperPrint = JasperFillManager.fillReport(mainreportinputStream, dataSource,
					new JREmptyDataSource());

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

			doc = PDDocument.load(new File(file.getAbsolutePath()));
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

			e.printStackTrace();
			return false;
		} finally {
			try {
				if(doc != null) {
					doc.close();
				}
				if (mainreportinputStream != null) {
					mainreportinputStream.close();
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

	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> getMembrDetailsReportDetails(String filename) {
		BufferedReader br = null;
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> membrDetailsdataRS = new HashMap<Integer, HashMap<Integer, HashMap<String, Object>>>();
		HashMap<Integer, HashMap<String, Object>> membrDetailsdata = new HashMap<Integer, HashMap<String, Object>>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			if (br == null || br.equals("")) {
				System.out.println("No MemberDetils Report Flat file ");
			} else {
				String sCurrentLine;
				int cuurline = 0, pdfgencount = 0;

				while ((sCurrentLine = br.readLine()) != null) {
					HashMap<String, Object> membrDetails = new HashMap<String, Object>();

					if (cuurline == 0 || sCurrentLine.contains("****")) {
						membrDetails = new HashMap<String, Object>();
						membrDetailsdata = new HashMap<Integer, HashMap<String, Object>>();
						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
						cuurline = 0;
					}

					String[] data = sCurrentLine.split("\\|");

					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
						membrDetails.put("policyHolder", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");

						membrDetails.put("policyHolderNum",
								data[3] != null && data[3].length() > 0 ? data[3].trim() : "");

						membrDetails.put("subsidiary", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");

						if (data.length >= 6) {
							membrDetails.put("subsidiaryNum",
									data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
							// System.out.println(data[i] != null && data[i].length() > 0 ? data[i].trim() :
							// "");
						}
						if (data.length >= 7) {
							membrDetails.put("policyNum",
									data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						}
						if (data.length >= 8) {
							membrDetails.put("policyPeriod",
									data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						}
						if (data.length >= 9) {
							membrDetails.put("billNum", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						}
						if (data.length >= 10) {
							membrDetails.put("dateOfIssue",
									data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						}
						if (data.length >= 11) {
							membrDetails.put("billingPeriod",
									data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						}
						if (data.length >= 12) {
							membrDetails.put("billingFrequecy",
									data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						}
						if (data.length >= 13) {
							membrDetails.put("adjustmentFrequency",
									data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						}
						if (data.length >= 14) {
							membrDetails.put("authorisedPerson",
									data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
						}
						if (data.length >= 15) {
							membrDetails.put("phoneNum",
									data[14] != null && data[16].length() > 0 ? data[16].trim() : "");
						}
						if (data.length >= 16) {
							membrDetails.put("portalUploadStatus",
									data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
						}
						if (data.length >= 17) {
							membrDetails.put("printHardCp",
									data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
						}
						if (data.length >= 18) {
							membrDetails.put("templetType",
									data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
						}
						// For Vitality||Admin fee if Y
						if (data.length >= 19) {
							membrDetails.put("VitalityFlag",
									data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
						}
						if (data.length >= 20) {
							membrDetails.put("policyType",
									data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
						}
						if (data.length >= 21) {
							membrDetails.put("policyTypeDscr",
									data[20] != null && data[20].length() > 0 ? data[20].trim() : "");
						}
					}

					if (data[0].equalsIgnoreCase("0001")) {
						membrDetailsdata.put(cuurline, membrDetails);
						cuurline++;
						membrDetailsdataRS.put(pdfgencount, membrDetailsdata);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("[MembrDetailsSrvice.getMembrDetailsReportDetails] Exception: " + e.toString());
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
		return membrDetailsdataRS;
	}

	public HashMap<Integer, List<MemberDetails>> getMemberDetailsTableDetails(String filename) {
		HashMap<Integer, List<MemberDetails>> memberDetailsListRS = new HashMap<Integer, List<MemberDetails>>();
		List<MemberDetails> memberDetailsList = new ArrayList<MemberDetails>();
		List<MemberDetilsTableData> memberDetilsTableDataList = new ArrayList<MemberDetilsTableData>();
		List<MembrDetailsSubTotal> membrDetailsSubTotalList = new ArrayList<MembrDetailsSubTotal>();
		List<MembrDetailsGrandTotal> membrDetailsGrandTotalList = new ArrayList<MembrDetailsGrandTotal>();
		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			if (br == null || br.equals("")) {
				System.out.println("No Member Details Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;
				while ((sCurrentLine = br.readLine()) != null) {
					MemberDetails membrDetails = new MemberDetails();
					MemberDetilsTableData memberDetilsTableData = new MemberDetilsTableData();
					MembrDetailsSubTotal membrDetailsSubTotal = new MembrDetailsSubTotal();
					MembrDetailsGrandTotal membrDetailsGrandTotal = new MembrDetailsGrandTotal();
					// boolean add = false;
					if (sCurrentLine.contains("****")) {
						membrDetails = new MemberDetails();
						memberDetailsList = new ArrayList<MemberDetails>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}

					}

					String data[] = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1D")) {

						String MemberNameOrNum = data[2] != null && data[2].length() > 0 ? data[2].trim() : "";
						if (MemberNameOrNum.startsWith("/")) {
							MemberNameOrNum = MemberNameOrNum.replace("/", "");
						}
						memberDetilsTableData.setMemberNameOrNum(MemberNameOrNum);

						memberDetilsTableData.setAge(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						memberDetilsTableData.setRel(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						memberDetilsTableData.setPlan(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						memberDetilsTableData.setProduct(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						memberDetilsTableData
								.setProposedSumassure(data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						memberDetilsTableData
								.setAcceptedSumassure(data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
						memberDetilsTableData.setIncreasedOrDecrSumassure(
								data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
						memberDetilsTableData
								.setUwStatus(data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
						memberDetilsTableData
								.setPremium(data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
						memberDetilsTableData
								.setLoadingPremium(data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
						memberDetilsTableData
								.setAdminVitlityFee(data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
						memberDetilsTableData.setSt(data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
						memberDetilsTableData.setTotalPrmiumOrAdminFee(
								data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
						memberDetilsTableData
								.setEffectiveDate(data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
						memberDetilsTableData
								.setMovementType(data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
						memberDetilsTableDataList.add(memberDetilsTableData);

					}
					membrDetails.setMemberDetilsTableData(memberDetilsTableDataList);

					if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1T")) {
						membrDetailsSubTotal
								.setSubTtlPremium(data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						membrDetailsSubTotal
								.setSubTtlLoadingPremium(data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						membrDetailsSubTotal
								.setSubTtlAminVitlityFee(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						membrDetailsSubTotal.setSubTtlSt(data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
						membrDetailsSubTotal.setSubTtlTotalPrmiumOrAdminFee(
								data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
						membrDetailsSubTotalList.add(membrDetailsSubTotal);
					}

					membrDetails.setMembrDetailsSubTotal(membrDetailsSubTotalList);

					if (data[0].equalsIgnoreCase("0002")) {
						memberDetilsTableDataList = new ArrayList<MemberDetilsTableData>();
						membrDetailsSubTotalList = new ArrayList<MembrDetailsSubTotal>();
						membrDetailsGrandTotalList = new ArrayList<MembrDetailsGrandTotal>();

					}

					if (data[0].equalsIgnoreCase("0004")) {
						membrDetailsGrandTotal
								.setGrandTtlPremium(data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
						membrDetailsGrandTotal.setGrandTtlLoadingPremium(
								data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
						membrDetailsGrandTotal.setGrandTtlAminVitlityFee(
								data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
						membrDetailsGrandTotal
								.setGrandTtlSt(data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
						membrDetailsGrandTotal.setGrandTtlTotalPrmiumOrAdminFee(
								data[6] != null && data[6].length() > 0 ? data[6].trim() : "");

						membrDetailsGrandTotalList.add(membrDetailsGrandTotal);
					}
					membrDetails.setMembrDetailsGrandTotal(membrDetailsGrandTotalList);

					if (data[0].equalsIgnoreCase("0002")) {
						memberDetailsList.add(membrDetails);

					}
					memberDetailsListRS.put(pdfgencount, memberDetailsList);

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
		return memberDetailsListRS;

	}

	public HashMap<Integer, List<MemberDetailsNoteForPageFooter>> getNoteForFooter(String filename) {
		List<MemberDetailsNoteForPageFooter> listNoteForPageFooter = new ArrayList<MemberDetailsNoteForPageFooter>();
		HashMap<Integer, List<MemberDetailsNoteForPageFooter>> listNoteForPageFooterRS = new HashMap<Integer, List<MemberDetailsNoteForPageFooter>>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			if (br == null || br.equals("")) {
				System.out.println("No Member Details Flat file ");
			} else {
				String sCurrentLine;
				int pdfgencount = 0;
				while ((sCurrentLine = br.readLine()) != null) {

					MemberDetailsNoteForPageFooter memberDetailsNoteForPageFooter = new MemberDetailsNoteForPageFooter();
					if (sCurrentLine.contains("****")) {
						memberDetailsNoteForPageFooter = new MemberDetailsNoteForPageFooter();
						listNoteForPageFooter = new ArrayList<MemberDetailsNoteForPageFooter>();

						if (sCurrentLine.contains("****")) {
							pdfgencount++;
						}
					}
					String data[] = sCurrentLine.split("\\|");
					if (data[0].equalsIgnoreCase("0005")) {
						memberDetailsNoteForPageFooter
								.setRelationship(data[1] != null && data[1].length() > 0 ? data[1].trim() : "");
						listNoteForPageFooter.add(memberDetailsNoteForPageFooter);
					}
					if (data[0].equalsIgnoreCase("0006")) {
						memberDetailsNoteForPageFooter
								.setMedical(data[1] != null && data[1].length() > 0 ? data[1].trim() : "");
						listNoteForPageFooter.add(memberDetailsNoteForPageFooter);
					}
					if (data[0].equalsIgnoreCase("0007")) {
						memberDetailsNoteForPageFooter
								.setTermLife(data[1] != null && data[1].length() > 0 ? data[1].trim() : "");
						listNoteForPageFooter.add(memberDetailsNoteForPageFooter);
					}
					if (data[0].equalsIgnoreCase("0008")) {
						memberDetailsNoteForPageFooter
								.setUnderWritingStatus(data[1] != null && data[1].length() > 0 ? data[1].trim() : "");
						listNoteForPageFooter.add(memberDetailsNoteForPageFooter);
					}
					if (data[0].equalsIgnoreCase("0009")) {
						memberDetailsNoteForPageFooter
								.setMovementType(data[1] != null && data[1].length() > 0 ? data[1].trim() : "");
						listNoteForPageFooter.add(memberDetailsNoteForPageFooter);
					}

					listNoteForPageFooterRS.put(pdfgencount, listNoteForPageFooter);

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
		return listNoteForPageFooterRS;
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
