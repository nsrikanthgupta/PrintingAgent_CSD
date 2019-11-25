/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.CompanyCode;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 5 Oct 2019 11:34:50 am
 */
@Repository
public interface CompanyCodeRepository extends CrudRepository<CompanyCode, String> {

    /**
     * @param companyCodeId
     * @return
     */
    @Query("select cc from CompanyCode cc where cc.companyCodeId = ?1")
    List< CompanyCode > findByCompanyId(Long companyCodeId);

}
