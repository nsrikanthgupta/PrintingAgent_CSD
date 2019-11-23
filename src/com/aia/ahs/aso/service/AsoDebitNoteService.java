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

@Service("asoDebitNoteService")
public class AsoDebitNoteService implements TemplateActions {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsoDebitNoteService.class);

    @Autowired
    private DBCSDCommon dbcmd;

    @Value("${print.agent.fileoutput.path}")
    private String outputPath;

    String jasper = FilenameUtils
        .normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../jasper/", true);

    String logo = FilenameUtils
        .normalize(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../../img/", true);

    SimpleDateFormat sdf = new SimpleDateFormat("YYYY");

    String year = sdf.format(new Date());

    SimpleDateFormat ymd = new SimpleDateFormat("YYYY-MM-dd");

    private int companyCode;

    private static final String docType = "ASODN";

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

    private String doc_creation_dt;

    private String indicator;

    private String g4CycleDate;

    @Override
    public int genReport(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails) {

        if (batchFileDetails.getStatus().equalsIgnoreCase("RECONCILIATION_SUCCESS")) {
            LOGGER.info("RECONCILIATION PROCESS COMPLETED FOR THE FILENAME {} AND FOR THE CYCLE DATE {}",
                batchFileDetails.getFileName(), batchCycle.getCycleDate());
            return -1;
        }

        this.g4CycleDate = batchCycle.getCycleDate();
        this.doc_creation_dt = ymd.format(new Date(this.g4CycleDate));
        this.year = sdf.format(new Date(this.g4CycleDate));
        this.tableName = "tbl_asodn_" + this.year;
        this.tbl_doc_nm = "[aiaIMGdb_CSD_" + this.year + "]..[" + this.tableName + "]";
        this.process_year = this.year;

        HashMap< Integer , HashMap< Integer , HashMap< String , Object > > > asoDebitNoteRSDetails =
            getAsoDebitNoteDetails(batchFileDetails.getFileLocation());

        int noFiles = asoDebitNoteRSDetails.size();
        for (int i = 0; i < noFiles; i++) {
            HashMap< Integer , HashMap< String , Object > > asoDebitNoteRS = asoDebitNoteRSDetails.get(i);

            HashMap< String , Object > dataSource = new HashMap< String , Object >();
            for (int a = 0; a < asoDebitNoteRS.size(); a++) {
                dataSource.putAll(asoDebitNoteRS.get(a));
            }

            this.proposalNo = (String) dataSource.get("policyNum");
            this.client_no = (String) dataSource.get("policyHolderNum");
            this.client_name = (String) dataSource.get("policyHolder");
            this.bill_no = (String) dataSource.get("billNum");

            this.proposal_type = (String) dataSource.get("policyType");
            this.sub_client_no = (String) dataSource.get("subsidiaryNum");
            this.sub_client_name = (String) dataSource.get("subsidiary");
            if (this.sub_client_name.equalsIgnoreCase("-") || this.sub_client_name.isEmpty() || this.sub_client_name == null) {
                this.sub_client_name = this.client_name;
            }
            this.indicator = (String) dataSource.get("printHardCp");

            uploadReport(dataSource, companyCode.getCompanyCode());
        }

        return noFiles;

    }

    public synchronized void uploadReport(HashMap< String , Object > dataSource, String companyCode) {

        FileInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {

            String pdfFullOutputPath = "";
            String jrFullReadpath = "";
            String pdfname = "";
            if (companyCode.equalsIgnoreCase("Co3")) {
                this.companyCode = 3;
                jrFullReadpath = jasper + "PrintingAgentReports\\AHS\\conventional\\ASO\\AsoDebitNote.jasper";

                String billmonth = "" + dataSource.get("billMonth");
                String billperiod = billmonth.replace("/", "");
                pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum") + "_ASODN.pdf";
                pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");
            } else if (companyCode.equalsIgnoreCase("Co4")) {
                this.companyCode = 4;
                jrFullReadpath = jasper + "PrintingAgentReports\\AHS\\takaful\\ASO\\AsoDebitNote.jasper";

                String billmonth = "" + dataSource.get("billMonth");
                String billperiod = billmonth.replace("/", "");
                pdfname = dataSource.get("policyNum") + "_" + billperiod + "_" + dataSource.get("billNum") + "_ASODN.pdf";
                pdfFullOutputPath = this.outputPath + "/" + companyCode + "/" + this.doc_creation_dt.replace("-", "");

            }

            dataSource.put("logo", this.logo);
            inputStream = new FileInputStream(jrFullReadpath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, dataSource, new JREmptyDataSource());

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
                System.out.println("directories are created @@@@@@@@.... " + file.getAbsoluteFile());

            }

            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            System.out.println("==> PDF Generated..." + file.getAbsolutePath());
            PDDocument doc = PDDocument.load(new File(file.getAbsolutePath()));
            int page_count = doc.getNumberOfPages();

            byte[] fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            String dataId = UUID.randomUUID().toString();
            boolean add = dbcmd.checktblDmDoc(this.companyCode, proposalNo, this.doc_creation_dt, docType, bill_no);
            if (add) {
                dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

                dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm, this.doc_creation_dt,
                    this.companyCode, client_no, client_name, bill_no, bill_type, sub_client_no, sub_client_name, file_format,
                    proposal_type, this.indicator, page_count);

            } else {
                dbcmd.insertIntoDocTypeTable(dataId, fileContent, this.tableName, this.year);

                dbcmd.insertIntoTblDmDoc(dataId, docType, proposalNo, process_year, dmStatus, tbl_doc_nm, this.doc_creation_dt,
                    this.companyCode, client_no, client_name, bill_no, bill_type, sub_client_no, sub_client_name, file_format,
                    proposal_type, this.indicator, page_count);
            }

        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public HashMap< Integer , HashMap< Integer , HashMap< String , Object > > > getAsoDebitNoteDetails(String filePath) {

        HashMap< Integer , HashMap< String , Object > > asoDebitNotetRS = new HashMap< Integer , HashMap< String , Object > >();
        HashMap< Integer , HashMap< Integer , HashMap< String , Object > > > asoDebitNotetRSDetails =
            new HashMap< Integer , HashMap< Integer , HashMap< String , Object > > >();
        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            if (br == null || br.equals("")) {
                System.out.println("No AsoDebitNote Flat file ");
            } else {
                String sCurrentLine;
                int cuurline = 0, pdfgencount = 0;

                while ((sCurrentLine = br.readLine()) != null) {
                    HashMap< String , Object > asoDebitNote = new HashMap< String , Object >();
                    if (cuurline == 0 || sCurrentLine.contains("****")) {
                        asoDebitNote = new HashMap< String , Object >();
                        asoDebitNotetRS = new HashMap< Integer , HashMap< String , Object > >();

                        if (sCurrentLine.contains("****")) {
                            pdfgencount++;
                        }
                        cuurline = 0;
                    }
                    String[] data = sCurrentLine.split("\\|");
                    if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1H")) {
                        asoDebitNote.put("companyName", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
                        asoDebitNote.put("addressLine1", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");
                        asoDebitNote.put("addressLine2", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");
                        asoDebitNote.put("addressLine3", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
                        asoDebitNote.put("addressLine4", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
                        asoDebitNote.put("addressLine5", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
                        asoDebitNote.put("billNum", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
                        asoDebitNote.put("dateOfIssue", data[9] != null && data[9].length() > 0 ? data[9].trim() : "");
                        asoDebitNote.put("billMonth", data[10] != null && data[10].length() > 0 ? data[10].trim() : "");
                        asoDebitNote.put("paymentDueDate", data[11] != null && data[11].length() > 0 ? data[11].trim() : "");
                        asoDebitNote.put("authorisedPerson", data[12] != null && data[12].length() > 0 ? data[12].trim() : "");
                        asoDebitNote.put("phoneNum", data[13] != null && data[13].length() > 0 ? data[13].trim() : "");
                        asoDebitNote.put("portalUploadStatus", data[14] != null && data[14].length() > 0 ? data[14].trim() : "");
                        if (data.length >= 16) {
                            asoDebitNote.put("printHardCp", data[15] != null && data[15].length() > 0 ? data[15].trim() : "");
                        }
                        if (data.length >= 17) {
                            asoDebitNote.put("policyType", data[16] != null && data[16].length() > 0 ? data[16].trim() : "");
                        }
                        if (data.length >= 18) {
                            asoDebitNote.put("policyTypeDscr", data[17] != null && data[17].length() > 0 ? data[17].trim() : "");
                        }
                        if (data.length >= 19) {
                            asoDebitNote.put("bankAcNo", data[18] != null && data[18].length() > 0 ? data[18].trim() : "");
                        }
                        if (data.length >= 20) {
                            asoDebitNote.put("bankName", data[19] != null && data[19].length() > 0 ? data[19].trim() : "");
                        }
                    }
                    if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("2H")) {
                        asoDebitNote.put("policyHolder", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");

                        asoDebitNote.put("policyHolderNum", data[3] != null && data[3].length() > 0 ? data[3].trim() : "");

                        asoDebitNote.put("subsidiary", data[4] != null && data[4].length() > 0 ? data[4].trim() : "");

                        if (data.length >= 6) {
                            asoDebitNote.put("subsidiaryNum", data[5] != null && data[5].length() > 0 ? data[5].trim() : "");
                        }
                        if (data.length >= 7) {
                            asoDebitNote.put("policyNum", data[6] != null && data[6].length() > 0 ? data[6].trim() : "");
                        }

                        if (data.length >= 8) {
                            asoDebitNote.put("poNum", data[7] != null && data[7].length() > 0 ? data[7].trim() : "");
                        }

                        if (data.length >= 9) {
                            asoDebitNote.put("amnt", data[8] != null && data[8].length() > 0 ? data[8].trim() : "");
                        }
                    }
                    if (data[0].equalsIgnoreCase("0001") && data[1].equalsIgnoreCase("1T")) {
                        asoDebitNote.put("totalAmnt", data[2] != null && data[2].length() > 0 ? data[2].trim() : "");
                    }

                    if (data[0].equalsIgnoreCase("0001")) {
                        asoDebitNotetRS.put(cuurline, asoDebitNote);
                        cuurline++;
                        asoDebitNotetRSDetails.put(pdfgencount, asoDebitNotetRS);
                    }
                }

            }

        } catch (Exception e) {
            System.out.println("[AsoDebitNoteService.getAsoDebitNoteeDetails] Exception: " + e.toString());
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
        return asoDebitNotetRSDetails;
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

}
