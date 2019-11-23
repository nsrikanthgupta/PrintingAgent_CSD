/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import java.util.List;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.CompanyCode;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 5 Oct 2019 11:36:57 am
 */
public interface PrintAgentService {

	/**
	 * @return
	 */
	List<CompanyCode> getActiveCompanyCodeList();

	/**
	 * @param code
	 * @return
	 */
	boolean checkConnectivity(CompanyCode code);

	/**
	 * @param code
	 * @return
	 */
	String checkForNewCyle(CompanyCode code);

	/**
	 * @param code
	 */
	void triggerNewBatchCycle(CompanyCode code);

	/**
	 * @param string
	 * @return
	 */
	List<BatchCycle> getBatchCycles(String status);

	/**
	 * @param batchCycle
	 * @return
	 */
	boolean downloadBatchCycles(BatchCycle batchCycle);

	/**
	 * @param batchCycle
	 */
	void updateBatchCycle(BatchCycle batchCycle);

	/**
	 * @param companyCode
	 * @return
	 */
	CompanyCode getCompanyCode(String companyCode);

	boolean verifyBatchCycleExist(String newCycleDate, CompanyCode code);

}
