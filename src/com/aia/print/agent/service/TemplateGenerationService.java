/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.BatchFileDetails;
import com.aia.print.agent.entiry.CompanyCode;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla 
 * @DateTime 23 Nov 2019 10:05:51 am
 */
public interface TemplateGenerationService {

    /**
     * @param companyCode
     * @param batchCycle
     * @param batchFileDetails
     */
    void processFiles(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails);

    /**
     * @param companyCode
     * @param batchCycle
     * @param batchFileDetails
     */
    void generateTemplate(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails);

}
