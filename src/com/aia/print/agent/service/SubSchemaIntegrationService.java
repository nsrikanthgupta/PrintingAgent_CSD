/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.TableDmDoc;


/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla 
 * @DateTime 6 Oct 2019 1:44:41 pm
 */
public interface SubSchemaIntegrationService {


	/**
	 * @param tableDmDoc
	 * @param fileContent
	 * @param batchCycle
	 */
	void insertSummaryBilling(TableDmDoc tableDmDoc, byte[] fileContent, BatchCycle batchCycle);
    

}
