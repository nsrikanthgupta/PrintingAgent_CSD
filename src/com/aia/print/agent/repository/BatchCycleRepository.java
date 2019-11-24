/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.BatchCycle;

/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 5 Oct 2019 7:08:53 pm
 */
@Repository
public interface BatchCycleRepository extends CrudRepository<BatchCycle, String> {

	/**
	 * @param status
	 * @return
	 */
	@Query("select bc from BatchCycle bc where bc.status = ?1")
	List<BatchCycle> getLatestBatchCycles(String status);

	/**
	 * @return
	 */
	@Query("select bc from BatchCycle bc order by bc.batchId desc")
    List< BatchCycle > getAllBatchCycleList();

	/**
	 * @param cycleDate
	 * @return
	 */
	@Query("select bc from BatchCycle bc where bc.cycleDate = ?1")
    List< BatchCycle > getAllBatchCycleByDate(String cycleDate);
}
