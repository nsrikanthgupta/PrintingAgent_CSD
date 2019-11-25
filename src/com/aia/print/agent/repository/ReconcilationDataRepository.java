/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.ReconcilationData;

/**
 * 
 * @author Srikanth Neerumalla
 * @DateTime 25 Nov 2019 6:34:22 am
 */
@Repository
public interface ReconcilationDataRepository extends CrudRepository< ReconcilationData , String > {
    
    /**
     * @param batchId
     * @return
     */
    @Query("select rcd from ReconcilationData rcd where rcd.batchId = ?1")
    List<ReconcilationData> getReconcilationData(Long batchId);

}
