/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.repository.BatchFileDetailsRepository;



/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 6 Oct 2019 11:11:37 am
 */
@Service("batchReconciliationService")
public class BatchReconciliationServiceImpl implements BatchReconciliationService {

    /**
     * batchFileDetailsRepository
     */
    @Autowired
    private BatchFileDetailsRepository batchFileDetailsRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public int perFormReconciliation(BatchCycle batchCycle) {
        /**
         * Implement Logic For Reconciliation
         */
        List< BatchFileDetails > fileList = batchFileDetailsRepository.getBatchFileDetails(batchCycle.getBatchId());
        if (!CollectionUtils.isEmpty(fileList)) {
            for (BatchFileDetails batchFileDetails : fileList) {
                batchFileDetails.setStatus("RECONCILIATION_SUCCESS");
                batchFileDetails.setUpdatedBy("SD");
                batchFileDetails.setUpdatedDate(new Date());
                batchFileDetailsRepository.save(batchFileDetails);
            }
        }
        return 1;
    }

	
}
