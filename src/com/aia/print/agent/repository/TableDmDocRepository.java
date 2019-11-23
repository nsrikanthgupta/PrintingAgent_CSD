/**
 * 
 */
package com.aia.print.agent.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aia.print.agent.entiry.TableDmDoc;



/**
 * @author ITT0284
 *
 */
@Repository
public interface TableDmDocRepository extends CrudRepository<TableDmDoc, String> {

}
