/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.ReconcilationDetail;

/**
 * 
 * @author Srikanth Neerumalla
 * @DateTime 25 Nov 2019 6:34:58 am
 */
@Repository
public interface ReconcilationDetailRepository extends CrudRepository< ReconcilationDetail , String > {

}
