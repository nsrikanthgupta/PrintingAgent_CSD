/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import java.util.Set;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;

/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 6 Oct 2019 11:11:23 am
 */
public interface BatchReconciliationService {

    int perFormReconciliation(BatchCycle batchCycle);

    int verifyTemplateGeneration(BatchCycle batchCycle);

    void processReconcilationFile(BatchFileDetails batchFileDetails);

    /**
     * @param cycleDate
     * @param debtorCode
     * @return
     */
    Set< String > getPolicyInfo(String cycleDate, String debtorCode);

}
