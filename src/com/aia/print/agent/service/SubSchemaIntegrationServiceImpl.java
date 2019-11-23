/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aia.print.agent.entiry.BatchCycle;
import com.aia.print.agent.entiry.TableDmDoc;
import com.aia.print.agent.repository.TableDmDocRepository;
import com.aia.print.agent.service.SubSchemaIntegrationService;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 6 Oct 2019 1:44:53 pm
 */
@Service("subSchemaIntegrationService")
public class SubSchemaIntegrationServiceImpl implements SubSchemaIntegrationService {
    
	/**
	 * table_dm_doc url
	 */
	@Value("${spring.datasource.url}")
	private String dataSourceUrl;
	
	/**
	 * table_dm_doc Username
	 */
	@Value("${spring.datasource.username}")
	private String dataSourceUsername;
	
	/**
	 * table_dm_doc Password 
	 */
	@Value("${spring.datasource.password}")
	private String dataSourcePassword;
	
	/**
	 * subSchemaUrl
	 */
	@Value("${print.agent.subschema.url}")
	private String subSchemaUrl;

	
	/**
	 * subSchemaName
	 */
	@Value("${print.agent.subschema.name}")
	private String subSchemaName;

	
	@Autowired
	private TableDmDocRepository tableDmDocRepository;
	
	/**
	 *
	 */
	@Override
	public void insertSummaryBilling(TableDmDoc tableDmDoc, byte[] fileContent, BatchCycle batchCycle) {
		String year = subSchemaName.concat(batchCycle.getCycleDate().substring(0, 4));
		String connectionUrl = subSchemaUrl.replace("SUB_SCHEMA", year);
		String tableName = "tbl_pafsbs_".concat(batchCycle.getCycleDate().substring(0, 4));
		String sql = "INSERT INTO " + tableName + " (dm_doc_id,dm_doc_data)VALUES(?,?)";
		try (Connection con = DriverManager.getConnection(connectionUrl);
				PreparedStatement stmt = con.prepareStatement(sql);) {
			int count = 1;
			stmt.setString(count++, tableDmDoc.getDmDocId());
			stmt.setBytes(count++, fileContent);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checktblDmDoc(Integer company_code,String proposalNo,String doc_creation_dt,String mt_doc_type_cd,String bill_no){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String dm_doc_id="";

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
			conn = DriverManager.getConnection( this.dataSourceUrl+"selectMethod=cursor", this.dataSourceUsername, this.dataSourcePassword);
			
			String strSql = "  select  dm_doc_id from tbl_dm_doc where company_code=? and doc_creation_dt=? and proposal_no=? and mt_doc_type_cd=?  and bill_no=?";
			st = conn.prepareStatement(strSql);
			st.setInt(1, company_code);
			st.setString(2, doc_creation_dt);
			st.setString(3, proposalNo);
			st.setString(4, mt_doc_type_cd);
			st.setString(5, bill_no);
			
			System.out.println(strSql);
			
			rs = st.executeQuery();	
			while (rs.next()) {
			    dm_doc_id=rs.getString("dm_doc_id");
				System.out.println(dm_doc_id);
				String updateSql = "update tbl_dm_doc set is_supressed = 1 where dm_doc_id= ? ";
				
				PreparedStatement ps = conn.prepareStatement(updateSql);
				ps.setString(1, dm_doc_id);
				ps.execute();
				conn.commit();
			}
		} catch(Exception io){
			io.printStackTrace();
			return false;
        } finally {
			try {
				if (st != null) {
					st.close();
				}
					
				if (rs != null) {
					rs.close();
				}
				
				conn.close();
			} catch(Exception ex){}
			
		}
		
		return true;
	}
	
	
	public void insertFileContent(String tableName,String dataId,byte[] fileContent,BatchCycle batchCycle) {
		String year = subSchemaName.concat(batchCycle.getCycleDate().substring(0, 4));
		String connectionUrl = subSchemaUrl.replace("SUB_SCHEMA", year);
		String tableNam = tableName.concat(batchCycle.getCycleDate().substring(0, 4));
		String sql = "INSERT INTO " + tableNam + " (dm_doc_id,dm_doc_data, created_dt)VALUES(?,?,?)";
		try (Connection con = DriverManager.getConnection(connectionUrl);
				PreparedStatement stmt = con.prepareStatement(sql);) {
			int count = 1;
			stmt.setString(count++, dataId);
			stmt.setBytes(count++, fileContent);
			stmt.setTimestamp(count++, getCurrentTimeStamp());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void insertInToTableDmDoc(TableDmDoc tabledmdc) {
		tableDmDocRepository.save(tabledmdc);
	}
	
	
	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}

	
	

}
