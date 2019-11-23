/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.BatchFileDetails;

/**
 * 
 * @author Srikanth Neerumalla
 * @DateTime 6 Oct 2019 7:31:23 am
 */
@Repository
public interface BatchFileDetailsRepository extends CrudRepository<BatchFileDetails, String> {

	/**
	 * @param batchId
	 * @return
	 */
	@Query("select bfd from BatchFileDetails bfd where bfd.batchId = ?1")
	List<BatchFileDetails> getBatchFileDetails(Long batchId);

}
