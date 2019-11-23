/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.aia.print.agent.entiry.BatchCycle;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 5 Oct 2019 7:08:53 pm
 */
public interface BatchCycleRepository extends CrudRepository<BatchCycle, String> {

	/**
	 * @param status
	 * @return
	 */
	@Query("select bc from BatchCycle bc where bc.status = ?1")
	List<BatchCycle> getLatestBatchCycles(String status);
}
