/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.ReconcilationData;
import com.aia.print.agent.entiry.ReconcilationDetail;
import com.aia.print.agent.entiry.TableDmDoc;
import com.aia.print.agent.repository.BatchFileDetailsRepository;
import com.aia.print.agent.repository.ReconcilationDataRepository;
import com.aia.print.agent.repository.ReconcilationDetailRepository;
import com.aia.print.agent.repository.TableDmDocRepository;

/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 6 Oct 2019 11:11:37 am
 */
@Service("batchReconciliationService")
public class BatchReconciliationServiceImpl implements BatchReconciliationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchReconciliationServiceImpl.class);

    /**
     * batchFileDetailsRepository
     */
    @Autowired
    private BatchFileDetailsRepository batchFileDetailsRepository;

    @Autowired
    private ReconcilationDataRepository dataRepository;

    @Autowired
    private ReconcilationDetailRepository detailRepository;

    @Value("${print.agent.reconcilation.code}")
    private String reconcilationCode;

    @Autowired
    private TableDmDocRepository tableDmDocRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public int perFormReconciliation(BatchCycle batchCycle) {
        /**
         * Implement Logic For Reconciliation
         */
        List< ReconcilationData > reconcilationData = dataRepository.getReconcilationData(batchCycle.getBatchId());
        if (!CollectionUtils.isEmpty(reconcilationData)) {
            for (ReconcilationData conciliationData : reconcilationData) {

                LOGGER.debug("Reconcilation process starts for the policy {} and bill number {} ", conciliationData.getPolicyNo(),
                    conciliationData.getBillNo());

                LOGGER.debug("Expected total documents are {} for the policy {} and bill number {} ",
                    conciliationData.getTotalNo(), conciliationData.getPolicyNo(), conciliationData.getBillNo());

                List< TableDmDoc > list =
                    tableDmDocRepository.getTableDmDoc(conciliationData.getPolicyNo(), conciliationData.getBillNo(),
                        conciliationData.getCreatedBy(), this.getCycleDate(conciliationData.getCycleDate()));
                String totalDocumentCount = this.getTotalDocuments(list);

                LOGGER.debug("Actual total documents are {} for the policy {} and bill number {} ", totalDocumentCount,
                    conciliationData.getPolicyNo(), conciliationData.getBillNo());

                if (!totalDocumentCount.equals(conciliationData.getTotalNo())) {
                    LOGGER.debug("Reconcilation process ends for the policy {} and bill number {} and it is fail",
                        conciliationData.getPolicyNo(), conciliationData.getBillNo());
                    return 0;
                }

                LOGGER.debug("Reconcilation process ends for the policy {} and bill number {} and it is pass",
                    conciliationData.getPolicyNo(), conciliationData.getBillNo());
            }
        }
        return 1;
    }

    private String getTotalDocuments(List< TableDmDoc > list) {
        Set< String > set = new HashSet<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {
            for (TableDmDoc dmDoc : list) {
                set.add(dmDoc.getMtDocTypecd());
            }
            return String.valueOf(set.size());
        }
        return "0";
    }

    private String getCycleDate(String cycleDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return outputFormat.format(inputFormat.parseObject(cycleDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int verifyTemplateGeneration(BatchCycle batchCycle) {
        List< BatchFileDetails > fileList = batchFileDetailsRepository.getBatchFileDetails(batchCycle.getBatchId());

        LOGGER.info("batch cycle file count {} ", fileList.size());
        for (BatchFileDetails batchFileDetails : fileList) {

            if (batchFileDetails.getDocumentCode().equalsIgnoreCase("GMEXSR")) {
                continue;
            }
            if (batchFileDetails.getFileName().contains(reconcilationCode)) {
                continue;
            }
            if (batchFileDetails.getStatus().equalsIgnoreCase("TMPL_GENERATION_INPROGRESS")) {
                return 2;
            }
            if (batchFileDetails.getStatus().equalsIgnoreCase("DOWNLOADED")) {
                continue;
            }
            if (!batchFileDetails.getStatus().equalsIgnoreCase("TMPl_GENERATION_COMPLETED")) {
                return 0;
            }

            if (StringUtils.isNoneBlank(batchFileDetails.getParseError())) {
                return 0;
            }
        }
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    @Async
    public void processReconcilationFile(BatchFileDetails batchFileDetails) {
        String lineContent = null;
        int lineCount = 0;

        try (LineIterator iterator =
            IOUtils.lineIterator(new FileInputStream(new File(batchFileDetails.getFileLocation())), Charset.forName("UTF-8"))) {
            while (iterator.hasNext()) {
                lineContent = iterator.nextLine();
                if (StringUtils.isBlank(lineContent)) {
                    continue;
                }
                if (lineCount == 0) {
                    ++lineCount;
                    continue;
                }
                ++lineCount;
                LOGGER.debug("Reconciliation Content --> {}", lineContent);
                String[] list = lineContent.split("\\|");
                if (list.length >= 26) {
                    ReconcilationData data = new ReconcilationData(batchFileDetails.getBatchId());
                    data.setCompanyCode(this.getPropertyValue(list[0]));
                    data.setPolicyNo(this.getPropertyValue(list[1]));
                    data.setBillNo(this.getPropertyValue(list[2]));
                    data.setCycleDate(this.getPropertyValue(list[3]));
                    data.setSubsidaryNo(this.getPropertyValue(list[4]));
                    data.setCreatedDate(new Date());
                    data.setTotalNo(this.getPropertyValue(list[25]));
                    data = dataRepository.save(data);
                    LOGGER.debug("Reconciliation Content Saved for the Policy {} and for the cycle date {}", data.getPolicyNo(),
                        data.getCycleDate());
                    for (int index = 5; index <= 24; index++) {
                        if (StringUtils.isNoneBlank(this.getPropertyValue(list[index]))) {
                            ReconcilationDetail detail = new ReconcilationDetail(data.getReconcilationId());
                            detail.setDocType(this.getPropertyValue(list[index]));
                            detail.setCreatedDate(new Date());
                            detailRepository.save(detail);
                        }
                    }
                } else {
                    /**
                     * Invalid Reconciliation File --> Trigger Email to IT Support
                     */
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error While Processing Reconciliation file ", e);
            e.printStackTrace();
        }
    }

    private String getPropertyValue(String property) {
        if (StringUtils.isNotBlank(property)) {
            return property.trim();
        }
        return StringUtils.EMPTY;
    }

}
