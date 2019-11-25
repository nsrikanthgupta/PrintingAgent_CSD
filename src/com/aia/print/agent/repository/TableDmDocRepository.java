/**
 * 
 */
package com.aia.print.agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.TableDmDoc;

/**
 * @author ITT0284
 *
 */
@Repository
public interface TableDmDocRepository extends CrudRepository< TableDmDoc , String > {

    /**
     * @param status
     * @return
     */
    @Query("select tdc from TableDmDoc tdc where tdc.proposalNo = ?1 and tdc.billNo = ?2 and tdc.createdBy = ?3 and tdc.docCreationDt =?4")
    List< TableDmDoc > getTableDmDoc(String proposalNo, String billNumber, String createdBy, String cycleDate);

    @Query("select tdc from TableDmDoc tdc where tdc.createdBy = ?2 and tdc.isSupressed is null and tdc.debtorCode is null and tdc.docCreationDt =?1")
    List< TableDmDoc > getPolicyInfoWithoutDebtorCode(String cycleDate, String createdBy);
    
    
    @Query("select tdc from TableDmDoc tdc where tdc.createdBy = ?2 and tdc.isSupressed is null and tdc.debtorCode is not null and tdc.docCreationDt =?1")
    List< TableDmDoc > getPolicyInfoWithDebtorCode(String cycleDate, String createdBy);
}
