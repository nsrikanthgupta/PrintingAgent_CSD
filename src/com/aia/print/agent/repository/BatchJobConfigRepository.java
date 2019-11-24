/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.BatchJobConfig;

/**
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 24 Nov 2019 6:09:49 am
 */
@Repository
public interface BatchJobConfigRepository extends CrudRepository< BatchJobConfig , String > {

    /**
     * @param status
     * @return
     */
    @Query("select bjc from BatchJobConfig bjc where bjc.status = ?1")
    List< BatchJobConfig > getBatchJobConfig(String status);
    
    
    /**
     * @param jobKey
     * @return
     */
    @Query("select bjc from BatchJobConfig bjc where bjc.jobKey = ?1")
    List< BatchJobConfig > getBatchJobConfigByKey(String jobKey);
    
    
    /**
     * @param status
     * @return
     */
    @Query("select bjc from BatchJobConfig bjc")
    List< BatchJobConfig > getBatchJobConfig();
}
