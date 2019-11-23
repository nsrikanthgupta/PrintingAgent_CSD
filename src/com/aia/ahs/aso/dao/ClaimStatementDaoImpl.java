package com.aia.ahs.aso.dao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.ahs.aso.model.ClaimStatementBean;
import com.aia.ahs.aso.model.ClaimStatementGmm;
import com.aia.ahs.aso.model.ClaimStatementGmmMainTable;
import com.aia.ahs.aso.model.ClaimStatementGmmPayeeTable;
import com.aia.ahs.aso.model.SympNotification;
import com.aia.common.db.DBCPDataSourceUtil;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * ITT0284
 * 
 */
@Service("claimStatementDao")
public class ClaimStatementDaoImpl implements ClaimStatementDao {
	/**
	 * Mount inbound path
	 * 
	 */
	@Value("${mnt.inbound.path}")
	private String mountInboundPath;

	/**
	 * Mount outbound path
	 * 
	 */
	@Value("${mnt.outbound.path}")
	private String mountOutboundPath;

	@Value("${as400.db.url}")
	private String as400URI;

	@Value("${as400.db.userid}")
	private String as400UserId;

	@Value("${as400.db.password}")
	private String as400password;

	@Autowired
	private DBCPDataSourceUtil dBCPDataSourceUtil;

	private final String PRINTAGENTFLAG = "P";
	private final String PRINTAGENTFLAG_SUCCES = "C";
	private final String REMARK_SUCCES = "Printagent Processing completed";
	private final String PRINTAGENTFLAG_FAILURE = "F";
	private final String REMARK_FAILURE = "Printagent unable to generate pdf";
	private final String UPDATEDBY = "PrintingAgent_CSD";

	private final String DOCUMENTSTATUS_FOR_SINGLPDF = "READY";
	private final String DOCUMENTSTATUSDESC_FOR_SINGLEPDF = "Ready";

	private final String DOCUMENTSTATUS_FAILURE = "FAILED";
	private final String DOCUMENTSTATUSDESC_FAILURE = "Failed";

	private final String SQL_UPDATE_WHENSERVERSTARTS = "UPDATE  SYMP_NOTIFICATION SET PRINTAGENTFLAG='P' WHERE PRINTAGENTFLAG='I' ";
	private final String SQL_SELECT_QUERY = "SELECT TOP 10 DOCUMENTID,INBOUNDFILENAME,INBOUNDFILEPATH,OUTBOUNDFILENAME,OUTBOUNDFILEPATH, SELECTQUERY from SYMP_NOTIFICATION  WHERE PROCESSINGSTATUS='C' and  upper(DOCUMENTSTATUS)='IN PROGRESS' and isnull(ISDELETED,'N')='N' and SELECTQUERY is not NULL AND PRINTAGENTFLAG='P' ORDER BY DOCUMENTID";
	private final String SQL_UPDATE_FLAG_INPRO = "UPDATE  SYMP_NOTIFICATION SET PRINTAGENTFLAG='P' WHERE PRINTAGENTFLAG='I' ";

	private final String SQL_UPDATEFOR_MULTIPLE_PDFGEN = "UPDATE  SYMP_NOTIFICATION SET PRINTAGENTFLAG=? , REMARKS=?, UPDATEDT=?, UPDATEDBY=?, NOOFRECORDS=? WHERE DOCUMENTID=? ";
	private final String SQL_UPDATEFOR_SINGLE_PDFGEN = "UPDATE  SYMP_NOTIFICATION SET PRINTAGENTFLAG=? , REMARKS=?, UPDATEDT=?, UPDATEDBY=?, NOOFRECORDS=?, DOCUMENTSTATUS=?, DOCUMENTSTATUSDESC=? WHERE DOCUMENTID=? ";
	private final String SQL_UPDATEFOR_FFAILUIRE = "UPDATE  SYMP_NOTIFICATION SET PRINTAGENTFLAG=?, REMARKS=?, UPDATEDT=? , UPDATEDBY=?,DOCUMENTSTATUS=? WHERE DOCUMENTID=? ";

	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimStatementDaoImpl.class);

	public void startReportGen(List<ClaimStatementBean> listClaimStatementBean, SympNotification sympNotification) {
		LOGGER.info("ClaimStatementDaoImpl startReportGen() called-----------------------");
		long listSize = listClaimStatementBean.stream().count();
		LOGGER.info("Number of Pdf Files for Generation  ! " + listSize);
		Integer pdfCount = 0;
		for (ClaimStatementBean claimStatementBean : listClaimStatementBean) {
			boolean add = this.processDownloadRequest(claimStatementBean, sympNotification, listSize);
			if (add) {
				pdfCount++;
			}
		}
		if (listSize == pdfCount) {
			if (listSize == 1 && pdfCount == 1) {
				this.updateSympanyNotificationAsSuccesForSinglePdfById(sympNotification.getDocumentId(), pdfCount);
				LOGGER.info("Update Suscces Single File ==============> List Size: " + listSize + " PDF Count :"
						+ pdfCount);

			} else {
				this.updateSympanyNotificationAsSuccesForMultiplePdfById(sympNotification.getDocumentId(), pdfCount);
				LOGGER.info("Update Succes Multiple Files======> List Size: " + listSize + " PDF Count :" + pdfCount);

			}
		} else {
			// update failure
			LOGGER.info("failure pdf Gen ======> List Size: " + listSize + " PDF Count :" + pdfCount);
		}
	}

	public boolean processDownloadRequest(ClaimStatementBean claimStatementBean, SympNotification sympNotification,
			long listsize) {

		System.out.println("processDownloadRequest called...!");

		List<ClaimStatementGmm> listClaimStatementGmm = new ArrayList<ClaimStatementGmm>();
		List<ClaimStatementGmmMainTable> listClaimStatementGmmMainTable = new ArrayList<ClaimStatementGmmMainTable>();
		List<ClaimStatementGmmPayeeTable> listClaimStatementGmmPayeeTable = new ArrayList<ClaimStatementGmmPayeeTable>();
		boolean add = false;
		try {
			DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
			// Connection conn = DriverManager.getConnection(this.as400URI,this.as400UserId,
			// this.as400password);
			Connection conn = DriverManager.getConnection("jdbc:as400://e109f71b/E109F71B", "ITT0251", "Sep@2019");

			if (conn != null) {
				// LOGGER.info("Connected to the jdbc:as400
				// database-------------------------------!");
				// System.out.println("connected DB...!");
			} else {
				LOGGER.info("Failed to make jdbc:as400 connection-----------------------------!");
			}
			CallableStatement cstmt = conn.prepareCall("{CALL SYMDTA.PRC_DOWNLOAD_CLAIM_STATEMENT (?,?,?,?)}");

			cstmt.setString(1, claimStatementBean.getClaimNo());
			cstmt.setString(2, claimStatementBean.getClaimOccurance());
			String claimcompany = "";
			if (claimStatementBean.getClaimCompany().equalsIgnoreCase("Conventional")) {
				claimcompany = "3";
			} else {
				claimcompany = "4";
			}
			cstmt.setString(3, claimcompany);
			cstmt.registerOutParameter(4, Types.CHAR);
			cstmt.execute();
			String gmmIndicator = cstmt.getString(4).trim();
			ResultSet rs = cstmt.getResultSet();

			double subMajorMedicalAmnt = 0;
			double subIncurredAmnt = 0;
			double subEligibleAmnt = 0;
			double subInEligibleAmnt = 0;

			double DEDUCT_AMOUNT = 0;
			double CO_INS_AMOUNT = 0;
			double LMGHDCashAllowancePayable = 0;
			double Summary_LessMemberDep = 0;

			String ClaimNum = "";
			String Admission = "";
			String Discharg = "";
			String MedicalProviderInvoiceNo = "";
			String ProviderReceiptNo = "";

			String Summary_DeductibleAmnt = "";
			String Summary_co_InsurenceAmnt = "";
			double Summary_co_InsurnceAmnt = 0;
			String Summary_LMGHDCashAllowancePayableClaimant = "";
			String Summary_LessMemberDeposit = "";

			ClaimStatementGmm claimStatementGmm = new ClaimStatementGmm();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");

			while (rs.next()) {
				ClaimStatementGmmMainTable claimStatementGmmMainTable = new ClaimStatementGmmMainTable();
				/// ====1 main headers
				if (!ClaimNum.equalsIgnoreCase(rs.getString("CLAIMNO"))) {
					ClaimNum = rs.getString("CLAIMNO");
					claimStatementGmm.setClaimNum(ClaimNum);
				}
				claimStatementGmm.setMedicalProvider(rs.getString("MEDICALPROVIDER"));
				if (!Admission.equalsIgnoreCase(rs.getString("ADMISSIONDATE"))
						&& !Discharg.equalsIgnoreCase(rs.getString("DISCHARGEDATE"))) {
					Admission = rs.getString("ADMISSIONDATE");
					Discharg = rs.getString("DISCHARGEDATE");
					StringBuilder sb = new StringBuilder(Admission);
					sb.insert(4, '/');
					sb.insert(7, "/");
					String admsn = "" + sb;
					String admsion = sdf.format(new Date(admsn.trim()));

					StringBuilder sb1 = new StringBuilder(Discharg);
					sb1.insert(4, '/');
					sb1.insert(7, "/");
					String Dscharg = "" + sb1;
					String Discharg1 = sdf.format(new Date(Dscharg.trim()));

					claimStatementGmm.setAdmissionOrDischarge(admsion + " - " + Discharg1);
				}
				MedicalProviderInvoiceNo = rs.getString("MEDICALPROVIDERINVOICE");
				claimStatementGmm.setMedicalProviderInvoiceNo(MedicalProviderInvoiceNo);

				StringBuilder sb3 = new StringBuilder(rs.getString("PAYMENTAUTHDATE"));
				sb3.insert(4, '/');
				sb3.insert(7, "/");
				String PAYMENTAUTHDATE = "" + sb3;
				String paymentAuth = sdf.format(new Date(PAYMENTAUTHDATE.trim()));
				claimStatementGmm.setPaymentAuth(paymentAuth);

				ProviderReceiptNo = rs.getString("MEDICALPROVIDERRECEIPT");
				claimStatementGmm.setMedicalProviderReceiptNo(ProviderReceiptNo);
				claimStatementGmm.setClaimantName(rs.getString("CLAIMANTNAME"));
				claimStatementGmm.setRelationship(rs.getString("RELATIONSHIP"));
				claimStatementGmm.setEmpName(rs.getString("EMLOYEENAME"));
				if (rs.getString("EMPLOYEEIC") != null) {
					claimStatementGmm.setNricPasprtNo(rs.getString("EMPLOYEEIC").trim());
				}
				claimStatementGmm.setEmpId(rs.getString("EMPLOYEEID"));
				claimStatementGmm.setPolicyHolder(rs.getString("POLICYHOLDER"));
				claimStatementGmm.setProduct(rs.getString("PRODUCT"));

				claimStatementGmm.setSubsidiary(rs.getString("SUBSIDIARY"));
				claimStatementGmm.setPlan(rs.getString("PLAN"));
				claimStatementGmm.setPolicyNum(rs.getString("POLICYNUMBER"));
				// ======2 Main Table

				claimStatementGmmMainTable.setDsrc(rs.getString("DESCRIPTION"));
				claimStatementGmmMainTable.setNoOfDays(rs.getString("NO_OF_DAYS"));
				claimStatementGmmMainTable.setEblAmnt(rs.getString("ELIGIBLEBENFITLIMITAMOUNT") + "");
				claimStatementGmmMainTable.setEblMaximumLimit(rs.getString("ELIGIBLEBENFITLIMITMAX") + "");
				claimStatementGmmMainTable.setMajorMedicalAmnt(rs.getString("MEDICALAMOUNT"));

				claimStatementGmmMainTable.setIncurredAmnt(rs.getString("INCURRED"));
				claimStatementGmmMainTable.setEligibleAmnt(rs.getString("ELIGIBLEAMOUNT"));
				claimStatementGmmMainTable.setInEligibleAmnt(rs.getString("INELIGIBLEAMOUNT"));
				// ------- SubTotal Of main Table
				subMajorMedicalAmnt = subMajorMedicalAmnt
						+ Double.parseDouble(rs.getString("MEDICALAMOUNT").trim().replace(",", ""));
				subIncurredAmnt = subIncurredAmnt
						+ Double.parseDouble(rs.getString("INCURRED").trim().replace(",", ""));
				subEligibleAmnt = subEligibleAmnt
						+ Double.parseDouble(rs.getString("ELIGIBLEAMOUNT").trim().replace(",", ""));
				subInEligibleAmnt = subInEligibleAmnt
						+ Double.parseDouble(rs.getString("INELIGIBLEAMOUNT").trim().replace(",", ""));

				// =====3 summary

				if (gmmIndicator.equalsIgnoreCase("Y")) {// GMM
					claimStatementGmm.setSummary_IneligibleAmnt(claimStatementGmm.getSubInEligibleAmnt());
					if (!Summary_DeductibleAmnt.equalsIgnoreCase(rs.getString("DEDUCT_AMOUNT"))) {
						Summary_DeductibleAmnt = rs.getString("DEDUCT_AMOUNT");
						DEDUCT_AMOUNT = Double.parseDouble(Summary_DeductibleAmnt.trim().replace(",", ""));
						claimStatementGmm
								.setSummary_DeductibleAmnt(new DecimalFormat("#,###.00").format(DEDUCT_AMOUNT));
					}
					if (!Summary_co_InsurenceAmnt.equalsIgnoreCase(rs.getString("CO_INS_AMOUNT"))) {
						Summary_co_InsurenceAmnt = rs.getString("CO_INS_AMOUNT");
						CO_INS_AMOUNT = Double.parseDouble(Summary_co_InsurenceAmnt.trim().replace(",", ""));
						claimStatementGmm.setPercentage_co_InsurenceAmnt(Double.toString(CO_INS_AMOUNT));
					}

					if (!Summary_LMGHDCashAllowancePayableClaimant
							.equalsIgnoreCase(rs.getString("DAILYCASHALLOWANCE"))) {
						Summary_LMGHDCashAllowancePayableClaimant = rs.getString("DAILYCASHALLOWANCE");
						if (!Summary_LMGHDCashAllowancePayableClaimant.equalsIgnoreCase("0")) {
							LMGHDCashAllowancePayable = Double
									.parseDouble(Summary_LMGHDCashAllowancePayableClaimant.trim().replace(",", ""));
							claimStatementGmm.setSummary_LMGHDCashAllowancePayableClaimant(
									new DecimalFormat("#,###.00").format(LMGHDCashAllowancePayable));
						}

					}
					if (!Summary_LessMemberDeposit.equalsIgnoreCase(rs.getString("LESSMEMBERDEPOSIT"))) {
						Summary_LessMemberDeposit = rs.getString("LESSMEMBERDEPOSIT");
						Summary_LessMemberDep = Double.parseDouble(Summary_LessMemberDeposit.trim().replace(",", ""));
						claimStatementGmm.setSummary_LessMemberDeposit(
								new DecimalFormat("#,###.00").format(Summary_LessMemberDep));
					}

				} else {// No_GMM
					claimStatementGmm.setSummary_IneligibleAmnt(claimStatementGmm.getSubInEligibleAmnt());
					if (!Summary_DeductibleAmnt.equalsIgnoreCase(rs.getString("DEDUCT_AMOUNT"))) {
						Summary_DeductibleAmnt = rs.getString("DEDUCT_AMOUNT");
						DEDUCT_AMOUNT = Double.parseDouble(Summary_DeductibleAmnt.trim().replace(",", ""));
						claimStatementGmm
								.setSummary_DeductibleAmnt(new DecimalFormat("#,###.00").format(DEDUCT_AMOUNT));
					}
					if (!Summary_co_InsurenceAmnt.equalsIgnoreCase(rs.getString("CO_INS_AMOUNT"))) {
						Summary_co_InsurenceAmnt = rs.getString("CO_INS_AMOUNT");
						CO_INS_AMOUNT = Double.parseDouble(Summary_co_InsurenceAmnt.trim().replace(",", ""));
						claimStatementGmm.setPercentage_co_InsurenceAmnt(Double.toString(CO_INS_AMOUNT));
					}

					if (!Summary_LMGHDCashAllowancePayableClaimant
							.equalsIgnoreCase(rs.getString("DAILYCASHALLOWANCE"))) {
						Summary_LMGHDCashAllowancePayableClaimant = rs.getString("DAILYCASHALLOWANCE");
						if (!Summary_LMGHDCashAllowancePayableClaimant.equalsIgnoreCase("0")) {
							LMGHDCashAllowancePayable = Double
									.parseDouble(Summary_LMGHDCashAllowancePayableClaimant.trim().replace(",", ""));
							claimStatementGmm.setSummary_LMGHDCashAllowancePayableClaimant(
									new DecimalFormat("#,###.00").format(LMGHDCashAllowancePayable));
						}

					}
					if (!Summary_LessMemberDeposit.equalsIgnoreCase(rs.getString("LESSMEMBERDEPOSIT"))) {
						Summary_LessMemberDeposit = rs.getString("LESSMEMBERDEPOSIT");
						Summary_LessMemberDep = Double.parseDouble(Summary_LessMemberDeposit.trim().replace(",", ""));
						claimStatementGmm.setSummary_LessMemberDeposit(
								new DecimalFormat("#,###.00").format(Summary_LessMemberDep));
					}
				}
				claimStatementGmm.setSubMajorMedicalAmnt(new DecimalFormat("#,###.00").format(subMajorMedicalAmnt));
				claimStatementGmm.setSubIncurredAmnt(new DecimalFormat("#,###.00").format(subIncurredAmnt));
				claimStatementGmm.setSubEligibleAmnt(new DecimalFormat("#,###.00").format(subEligibleAmnt));
				claimStatementGmm.setSubInEligibleAmnt(new DecimalFormat("#,###.00").format(subInEligibleAmnt));

				claimStatementGmm.setPercentage_co_InsurenceAmnt(Double.toString(CO_INS_AMOUNT));
				if (gmmIndicator.equalsIgnoreCase("Y")) {
					Summary_co_InsurnceAmnt = (CO_INS_AMOUNT / 100) * subMajorMedicalAmnt;
					claimStatementGmm
							.setSummary_co_InsurenceAmnt(new DecimalFormat("#,###.00").format(Summary_co_InsurnceAmnt));
					double Summary_TtlInEligibleAmnt = subInEligibleAmnt + DEDUCT_AMOUNT + Summary_co_InsurnceAmnt;
					claimStatementGmm.setSummary_TtlInEligibleAmnt(
							new DecimalFormat("#,###.00").format(Summary_TtlInEligibleAmnt));
					double summary_AmntDue = Summary_TtlInEligibleAmnt
							- (LMGHDCashAllowancePayable + Summary_LessMemberDep);
					claimStatementGmm.setSummary_AmntDue(new DecimalFormat("#,###.00").format(summary_AmntDue));
				} else {
					Summary_co_InsurnceAmnt = (CO_INS_AMOUNT / 100) * subIncurredAmnt;
					claimStatementGmm
							.setSummary_co_InsurenceAmnt(new DecimalFormat("#,###.00").format(Summary_co_InsurnceAmnt));
					double Summary_TtlInEligibleAmnt = subInEligibleAmnt + DEDUCT_AMOUNT + Summary_co_InsurnceAmnt;
					claimStatementGmm.setSummary_TtlInEligibleAmnt(
							new DecimalFormat("#,###.00").format(Summary_TtlInEligibleAmnt));
					double summary_AmntDue = Summary_TtlInEligibleAmnt
							- (LMGHDCashAllowancePayable + Summary_LessMemberDep);
					claimStatementGmm.setSummary_AmntDue(new DecimalFormat("#,###.00").format(summary_AmntDue));
				}
				listClaimStatementGmmMainTable.add(claimStatementGmmMainTable);
				claimStatementGmm.setClaimStatementGmmMainTable(listClaimStatementGmmMainTable);
			}
			rs.close();
			cstmt.getMoreResults();
			ResultSet rs2 = cstmt.getResultSet();
			while (rs2.next()) {
				claimStatementGmm.setEXRSN1(rs2.getString("EXRSN1"));
				claimStatementGmm.setEXRSN2(rs2.getString("EXRSN2"));
				claimStatementGmm.setEXRSN3(rs2.getString("EXRSN3"));
				claimStatementGmm.setEXRSN4(rs2.getString("EXRSN4"));

				claimStatementGmm.setEXAMT1(rs2.getString("EXAMT1"));
				claimStatementGmm.setEXAMT2(rs2.getString("EXAMT2"));
				claimStatementGmm.setEXAMT3(rs2.getString("EXAMT3"));
				claimStatementGmm.setEXAMT4(rs2.getString("EXAMT4"));

			}
			rs2.close();
			cstmt.getMoreResults();
			ResultSet rs3 = cstmt.getResultSet();
			while (rs3.next()) {
				ClaimStatementGmmPayeeTable ClaimStatementGmmPayeeTable = new ClaimStatementGmmPayeeTable();
				ClaimStatementGmmPayeeTable.setPayee(rs3.getString("PAYEE"));
				ClaimStatementGmmPayeeTable.setPaymentreferenceNo(rs3.getString("TRANDATE"));
				ClaimStatementGmmPayeeTable.setAmnt(rs3.getString("PAYAMT"));
				listClaimStatementGmmPayeeTable.add(ClaimStatementGmmPayeeTable);
				claimStatementGmm.setClaimStatementGmmPayeeTable(listClaimStatementGmmPayeeTable);

			}
			rs3.close();

			listClaimStatementGmm.add(claimStatementGmm);
			add = this.uploadPDF(listClaimStatementGmm, sympNotification, ClaimNum, gmmIndicator, claimStatementBean,
					listsize);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("@@@@....!:" + e.getMessage());
		} finally {

		}

		return add;
	}

	public synchronized boolean uploadPDF(List<ClaimStatementGmm> listClaimStatementGmm,
			SympNotification sympNotification, String ClaimNum, String gmmIndicator,
			ClaimStatementBean claimStatementBean, long listsize) {

		FileInputStream inputStream = null;
		BufferedOutputStream bos = null;
		boolean add = false;
		try {
			// System.out.println("uploadReport called.....!");
			String pdfname = "";
			String pdfFullOutputPath = "";
			if (listsize == 1) {
				pdfname = "/" + sympNotification.getOutBoundFileName();
				pdfFullOutputPath = this.mountOutboundPath.trim();
			} else {
				pdfname = "/" + ClaimNum + "_ClaimStatement.pdf";
				pdfFullOutputPath = this.mountInboundPath.trim() + sympNotification.getInBoundFileName();
			}
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listClaimStatementGmm);
			String jrFullReadpath = "";
			String jasperpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
					+ "../../jasper/";
			String jasper = FilenameUtils.normalize(jasperpath, true);

			if (claimStatementBean.getClaimCompany().equalsIgnoreCase("Conventional")) {
				if (gmmIndicator.equalsIgnoreCase("Y")) {
					jrFullReadpath = jasper + "PrintingAgentReports/ClaimStatement/AsoClaimStatementGmm.jasper";
					LOGGER.info("ClaimStatementGMM indicater.....! " + gmmIndicator);
				} else {
					jrFullReadpath = jasper + "PrintingAgentReports/ClaimStatement/AsoClaimStatementNo_Gmm.jasper";
					LOGGER.info("ClaimStatementNO_GMM indicater.....! " + gmmIndicator);
				}
			} else {
				if (gmmIndicator.equalsIgnoreCase("Y")) {
					jrFullReadpath = jasper + "PrintingAgentReports/ClaimStatement/TkfClaimStatementGmm.jasper";
					LOGGER.info("Takaful ClaimStatementGMM indicater.....! " + gmmIndicator);
				} else {
					jrFullReadpath = jasper + "PrintingAgentReports/ClaimStatement/TkfClaimStatementNo_Gmm.jasper";
					LOGGER.info("Takaful ClaimStatementNo_GMM indicater.....! " + gmmIndicator);
				}
			}

			inputStream = new FileInputStream(jrFullReadpath);

			String imgpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
					+ "../../img/";
			String image = FilenameUtils.normalize(imgpath, true);
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("logo", image);
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, param, dataSource);

			File dir = new File(pdfFullOutputPath);
			if (!dir.exists()) {
				dir.mkdirs();
				LOGGER.info("Directory Created=====!   " + dir.getAbsolutePath());
			}

			File file = new File(dir.getAbsolutePath() + pdfname);
			if (!file.exists()) {
				file.createNewFile();
				LOGGER.info("File created...! " + file.getAbsoluteFile());
			}

			bos = new BufferedOutputStream(new FileOutputStream(file));
			JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
			LOGGER.info("PDF Generated===!   " + file.getAbsoluteFile());

			// send email success ......
			/**
			 * COMPANYNO
			 * 
			 */

			add = true;
		} catch (Exception e) {
			/**
			 * update Failure...
			 *
			 */
			// this.updateSympanyNotificationAsFailureById(sympNotification.getDocumentId(),e.getMessage());
			LOGGER.info("Failed PDF Generation.@@@@@@!   " + e.getMessage());

			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return add;

	}

	public List<SympNotification> getDownloadRequestFromSumyfap00021DB() {
		List<SympNotification> list = new ArrayList<SympNotification>();
		LOGGER.info("Checking DownLoad Request..!");
		Connection con = null;
		try {
			con = dBCPDataSourceUtil.getConnection();
			PreparedStatement psmt = con.prepareStatement(this.SQL_SELECT_QUERY);
			ResultSet rs = psmt.executeQuery();
			while (rs.next()) {
				SympNotification sympNotification = new SympNotification();
				sympNotification.setDocumentId(rs.getInt("DOCUMENTID"));
				sympNotification.setInBoundFileName(rs.getString("INBOUNDFILENAME").trim());
				sympNotification.setInBoundFilePath(rs.getString("INBOUNDFILEPATH").trim());
				sympNotification.setJsonData(rs.getString("SELECTQUERY"));
				sympNotification.setOutBoundFileName(rs.getString("OUTBOUNDFILENAME").trim());
				sympNotification.setOutBoundFilePath(rs.getString("OUTBOUNDFILEPATH").trim());
				list.add(sympNotification);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return list;
	}

	@Override
	public void updateSympanyNotificationAsSuccesForMultiplePdfById(Integer documentId, Integer pdfCount) {

		String remark = getRemarkbyId(documentId) + this.REMARK_SUCCES + " : " + getCurrentTimeStamp();
		Connection con = null;
		try {
			con = dBCPDataSourceUtil.getConnection();
			PreparedStatement psmt = con.prepareStatement(this.SQL_UPDATEFOR_MULTIPLE_PDFGEN);
			psmt.setString(1, this.PRINTAGENTFLAG_SUCCES);
			psmt.setString(2, remark);
			psmt.setTimestamp(3, getCurrentTimeStamp());
			psmt.setString(4, this.UPDATEDBY);
			psmt.setInt(5, pdfCount);
			psmt.setInt(6, documentId);
			psmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void updateSympanyNotificationAsSuccesForSinglePdfById(Integer documentId, Integer pdfCount) {
		String remark = getRemarkbyId(documentId) + "; " + this.REMARK_SUCCES + " : " + getCurrentTimeStamp();
		Connection con = null;
		try {
			con = dBCPDataSourceUtil.getConnection();
			PreparedStatement psmt = con.prepareStatement(this.SQL_UPDATEFOR_SINGLE_PDFGEN);
			psmt.setString(1, this.PRINTAGENTFLAG_SUCCES);
			psmt.setString(2, remark);
			psmt.setTimestamp(3, getCurrentTimeStamp());
			psmt.setString(4, this.UPDATEDBY);
			psmt.setInt(5, pdfCount);
			psmt.setString(6, this.DOCUMENTSTATUS_FOR_SINGLPDF);
			psmt.setString(7, this.DOCUMENTSTATUSDESC_FOR_SINGLEPDF);
			psmt.setInt(8, documentId);
			psmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void updateSympanyNotificationAsFailureById(Integer documentId, String REMARK_FAILURE) {
		String remark = getRemarkbyId(documentId) + "; " + this.REMARK_SUCCES + " : " + getCurrentTimeStamp();
		Connection con = null;
		try {
			con = dBCPDataSourceUtil.getConnection();
			PreparedStatement psmt = con.prepareStatement(this.SQL_UPDATEFOR_FFAILUIRE);
			psmt.setString(1, this.PRINTAGENTFLAG_FAILURE);
			psmt.setString(2, remark);
			psmt.setTimestamp(3, getCurrentTimeStamp());
			psmt.setString(4, this.UPDATEDBY);
			psmt.setString(5, this.DOCUMENTSTATUS_FAILURE);
			psmt.setString(6, DOCUMENTSTATUSDESC_FAILURE);
			psmt.setInt(7, documentId);
			psmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private String getRemarkbyId(Integer documentId) {
		Connection con = null;
		String remark = "";
		try {
			con = dBCPDataSourceUtil.getConnection();
			String sql = "SELECT REMARKS FROM SYMP_NOTIFICATION WHERE DOCUMENTID =?";
			PreparedStatement psmt = con.prepareStatement(sql);
			psmt.setInt(1, documentId);
			ResultSet rs = psmt.executeQuery();
			if (rs.next()) {
				remark = rs.getString("REMARKS");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return remark;
	}

	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}

}
