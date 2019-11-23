/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import com.aia.print.agent.entiry.BatchCycle;

/**
 * 
 * 
 * @author Srikanth Neerumalla 
 * @DateTime 6 Oct 2019 11:11:23 am
 */
public interface BatchReconciliationService {

    int perFormReconciliation(BatchCycle batchCycle);

}
