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
 * @DateTime 23 Nov 2019 11:20:57 am
 */
public interface TemplateActions {

    int genReport(CompanyCode companyCode, BatchCycle batchCycle, BatchFileDetails batchFileDetails);
    

}
