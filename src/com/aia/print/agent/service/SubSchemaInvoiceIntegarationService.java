/**
 * 
 */
package com.aia.print.agent.service;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.TableDmDoc;


/**
 * @author ITT0284
 *
 */
public interface SubSchemaInvoiceIntegarationService {

	void insertInvoice(TableDmDoc tableDmDoc, byte[] fileContent, BatchCycle batchCycle);
	
	

}
