/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.BatchJobConfig;
import com.aia.print.agent.entiry.CompanyCode;
import com.aia.print.agent.repository.BatchCycleRepository;
import com.aia.print.agent.repository.BatchFileDetailsRepository;
import com.aia.print.agent.repository.BatchJobConfigRepository;
import com.aia.print.agent.repository.CompanyCodeRepository;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileInputStream;

/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 5 Oct 2019 11:37:08 am
 */
@Service("printAgentService")
public class PrintAgentServiceImpl implements PrintAgentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintAgentServiceImpl.class);

    /**
     * companyCodeRepository
     */
    @Autowired
    private CompanyCodeRepository companyCodeRepository;

    /**
     * batchCycleRepository
     */
    @Autowired
    private BatchCycleRepository batchCycleRepository;

    /**
     * batchJobConfigRepository
     */
    @Autowired
    private BatchJobConfigRepository batchJobConfigRepository;

    /**
     * batchFileDetailsRepository
     */
    @Autowired
    private BatchFileDetailsRepository batchFileDetailsRepository;

    @Value("${print.agent.reconcilation.code}")
    private String reconcilationCode;

    /** {@inheritDoc} */
    @Override
    public List< CompanyCode > getActiveCompanyCodeList() {
        return makeCollection(companyCodeRepository.findAll());
    }

    /**
     * @param <E>
     * @param iter
     * @return
     */
    public static < E > List< E > makeCollection(Iterable< E > iter) {
        List< E > list = new ArrayList< E >();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public boolean checkConnectivity(CompanyCode code) {
        Boolean status = Boolean.TRUE;
        try {
            AS400 as400 = new AS400(code.getIpAddress(), code.getUsername(), code.getPassword());
            as400.disconnectAllServices();
        } catch (Exception e) {
            LOGGER.error("ERROR WHILE CHECKING THE CONNECTIVITY TO G400 SERVER ", e);
            /**
             * Trigger Email to IT Support
             */
            e.printStackTrace();
            status = Boolean.FALSE;
        }
        return status;
    }

    /** {@inheritDoc} */
    @Override
    public String checkForNewCyle(CompanyCode code) {
        String latestCycleDate = code.getLatestCycleDate();
        List< Date > cycleDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            AS400 as400 = new AS400(code.getIpAddress(), code.getUsername(), code.getPassword());
            IFSFile directory = new IFSFile(as400, code.getFolderPath());
            if (directory.isDirectory()) {
                IFSFile[] directorySubFolders = directory.listFiles();
                for (int i = 0; i < directorySubFolders.length; i++) {
                    if (directorySubFolders[i].isDirectory()) {
                        LOGGER.info("SUB DIRECTORY NAME :: {} ", directorySubFolders[i].getName());
                        cycleDates.add(dateFormat.parse(directorySubFolders[i].getName()));
                    }
                }
            }
            as400.disconnectAllServices();
            if (!cycleDates.isEmpty()) {
                Collections.sort(cycleDates);
                if (StringUtils.isEmpty(latestCycleDate)) {
                    return dateFormat.format(cycleDates.get(0));
                }
            }
            Date parsedLatestCycleDate = dateFormat.parse(latestCycleDate);
            for (Date date : cycleDates) {
                if (date.compareTo(parsedLatestCycleDate) > 0) {
                    return dateFormat.format(date);
                }
            }
        } catch (IOException | ParseException e) {
            LOGGER.error("ERROR WHILE CHECKING FOR LATEST CYCLE DATE IN  G400 SERVER ", e);
            /**
             * Trigger Email to IT Support
             */
            e.printStackTrace();
            latestCycleDate = "";
        }
        return "";
    }

    @Override
    public void triggerNewBatchCycle(CompanyCode code) {
        BatchCycle batchCycle = new BatchCycle();
        batchCycle.setCompanyCode(code.getCompanyCode());
        batchCycle.setCreatedBy("PrintingAgent_CSD");
        batchCycle.setCreatedDate(new Date());
        batchCycle.setCycleDate(code.getLatestCycleDate());
        batchCycle.setStatus("NEW");
        batchCycle.setCompanyCodeId(code.getCompanyCodeId());
        batchCycleRepository.save(batchCycle);

        companyCodeRepository.save(code);
        /**
         * Trigger Email for IT support regarding new Batch Cycle
         */
    }

    /** {@inheritDoc} */
    @Override
    public List< BatchCycle > getBatchCycles(String status) {
        return batchCycleRepository.getLatestBatchCycles(status);
    }

    /** {@inheritDoc} */
    @Override
    public boolean downloadBatchCycles(BatchCycle batchCycle) {
        LOGGER.info("DOWNLOAD PROCESS STARTS FOR THE BATCH  {} ", batchCycle.getBatchId());
        CompanyCode code = this.getCompanyCode(batchCycle.getCompanyCodeId());
        try {
            AS400 as400 = new AS400(code.getIpAddress(), code.getUsername(), code.getPassword());
            LOGGER.info("CONNECTION ESTABLISHED WITH G400 FOR THE BATCH  {} ", batchCycle.getBatchId());
            IFSFile directory = new IFSFile(as400, code.getFolderPath().concat(batchCycle.getCycleDate()));
            String localRootDirectory = code.getLocalFolderPath().concat(batchCycle.getCycleDate());
            IFSFile[] directoryFiles = directory.listFiles();
            verifyLocalDirectory(code, batchCycle.getCycleDate());
            for (int j = 0; j < directoryFiles.length; j++) {
                BatchFileDetails batchFileDetails =
                    new BatchFileDetails(batchCycle.getBatchId(), "DOWNLOADED", directoryFiles[j].getName());
                LOGGER.info("DOWNLOAED FILENAME {} FOR THE BATCH :: {}", directoryFiles[j].getName(), batchCycle.getBatchId());
                IFSFileInputStream inputStream = new IFSFileInputStream(directoryFiles[j]);
                File destFile = new File(localRootDirectory.concat("/").concat(directoryFiles[j].getName()));
                batchFileDetails.setFileLocation(localRootDirectory.concat("/").concat(directoryFiles[j].getName()));
                OutputStream out = new FileOutputStream(destFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
                Object object = this.readDocumentCount(batchFileDetails.getFileLocation());
                if (object instanceof java.lang.String) {
                    batchFileDetails.setExpectedDocumentCount(0);
                    batchFileDetails.setParseError(object.toString());
                } else {
                    batchFileDetails.setExpectedDocumentCount(Integer.parseInt(object.toString()));
                }
                if (batchFileDetails.getFileLocation().contains(reconcilationCode)) {
                    batchFileDetails.setActualDocumentCount(0);
                }
                batchFileDetails.setDocumentCode(getDocumentCode(batchFileDetails.getFileName()));
                batchFileDetailsRepository.save(batchFileDetails);
            }
        } catch (IOException | AS400SecurityException e) {
            LOGGER.info("DOWNLOAD PROCESS COMPLETED WITH ERROS FOR THE BATCH  {} ", batchCycle.getBatchId());
            LOGGER.error("ERROR WHILE DOWNLOADING FILES FROM G400", e);
            e.printStackTrace();
            return false;
        }
        LOGGER.info("DOWNLOAD PROCESS COMPLETED WITH NO ERROS FOR THE BATCH  {} ", batchCycle.getBatchId());
        return true;
    }

    private String getDocumentCode(String fileName) {
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(fileName)) {
            String[] subsets = fileName.split("_");
            if (subsets.length >= 2) {
                return subsets[1];
            }
        }
        return null;
    }

    private void verifyLocalDirectory(CompanyCode code, String cycleDate) {
        try {
            File directory = new File(code.getLocalFolderPath().concat(cycleDate));
            if (directory.exists()) {
                LOGGER.info("FOUND LOCAL DIRECTORY FOR THE CYCLE DATE {}", cycleDate);
                FileUtils.deleteDirectory(directory);
                LOGGER.info("DELETED EXISTING FILES FOR THE CYCLE DATE {}", cycleDate);
            }
            File newDirectory = new File(code.getLocalFolderPath().concat(cycleDate));
            newDirectory.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param companyCode
     * @return
     */
    @Override
    public CompanyCode getCompanyCode(Long companyCodeId) {
        List<CompanyCode> list = companyCodeRepository.findByCompanyId(companyCodeId);
        if(CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void updateBatchCycle(BatchCycle batchCycle) {
        batchCycleRepository.save(batchCycle);
    }

    @Override
    public boolean verifyBatchCycleExist(String newCycleDate, CompanyCode code) {
        try {
            String targetFileName = code.getCompanyCode().concat(reconcilationCode).concat(newCycleDate);
            // String targetFileName = "Co3".concat(reconcilationCode).concat(newCycleDate);
            LOGGER.info("targetFileName file name {} ", targetFileName);
            AS400 as400 = new AS400(code.getIpAddress(), code.getUsername(), code.getPassword());
            IFSFile directory = new IFSFile(as400, code.getFolderPath().concat(newCycleDate));
            IFSFile[] directoryFiles = directory.listFiles();
            verifyLocalDirectory(code, newCycleDate);
            for (int j = 0; j < directoryFiles.length; j++) {
                if ((directoryFiles[j].getName().contains(targetFileName))) {
                    LOGGER.info("file name {} ", directoryFiles[j].getName());
                    return true;
                }
            }
            as400.disconnectAllServices();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public Object readDocumentCount(String filePath) {
        if (filePath.contains(reconcilationCode)) {
            return 0;
        }
        ReversedLinesFileReader object = null;
        try {
            object = new ReversedLinesFileReader(new File(filePath), Charset.forName("UTF-8"));
            String lastLine = object.readLine();
            if (lastLine != null && lastLine.isEmpty()) {
                lastLine = object.readLine();
            }
            if (lastLine != null && !lastLine.isEmpty()) {
                String[] objects = lastLine.split("\\|");
                if (objects.length >= 3) {
                    LOGGER.info(lastLine);
                    LOGGER.info("filePath {} & Object {} ", filePath, objects[2]);
                    LOGGER.info("Document Count {} ", getIntegerValue(objects[2]));
                    return getIntegerValue(objects[2]);
                } else {
                    return "No Record Count";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                object.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private Object getIntegerValue(String object) {
        try {
            if (object.equalsIgnoreCase("Bill no")) {
                return 0;
            }
            return Integer.parseInt(object);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /** {@inheritDoc} */
    @Override
    public List< BatchFileDetails > getBatchFileDetails(Long batchId) {
        return batchFileDetailsRepository.getBatchFileDetails(batchId);
    }

    /** {@inheritDoc} */
    @Override
    public BatchJobConfig getBatchJobConfigByKey(String jobKey) {
        List< BatchJobConfig > list = batchJobConfigRepository.getBatchJobConfigByKey(jobKey);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

}
