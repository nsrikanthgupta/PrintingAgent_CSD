/**
 * 
 *//*
package com.aia.print.agent.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.TableDmDoc;
import com.aia.print.agent.service.SubSchemaInvoiceIntegarationService;


*//**
 * @author ITT0284
 *
 *//*
@Service("subSchemaInvoiceIntegarationService")
public class SubSchemaInvoiceIntegarationServiceImpl implements SubSchemaInvoiceIntegarationService{

	*//**
	 * subSchemaUrl
	 *//*
	@Value("${print.agent.subschema.url}")
	private String subSchemaUrl;

	*//**
	 * subSchemaName
	 *//*
	@Value("${print.agent.subschema.name}")
	private String subSchemaName;

		
	
	
	public void insertInvoice(TableDmDoc tableDmDoc, byte[] fileContent, BatchCycle batchCycle) {
		// TODO Auto-generated method stub
		
		String year = subSchemaName.concat(batchCycle.getCycleDate().substring(0, 4));
		String connectionUrl = subSchemaUrl.replace("SUB_SCHEMA", year);
		String tableName = "tbl_pafinv_".concat(batchCycle.getCycleDate().substring(0, 4));
		String sql = "INSERT INTO " + tableName + " (dm_doc_id,dm_doc_data)VALUES(?,?)";
		try (Connection con = DriverManager.getConnection(connectionUrl);
				PreparedStatement stmt = con.prepareStatement(sql);) {
			int count = 1;
			stmt.setString(count++, tableDmDoc.getDmDocId());
			stmt.setBytes(count++, fileContent);
			stmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}








	
	
	

}
*/