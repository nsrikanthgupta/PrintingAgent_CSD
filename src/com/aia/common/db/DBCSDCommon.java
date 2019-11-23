package com.aia.common.db;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class DBCSDCommon {
	/**
	 * check and update 
	 * */
	public boolean checktblDmDoc(int company_code,String proposalNo,String doc_creation_dt,String mt_doc_type_cd,String bill_no){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String dm_doc_id="";

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
			conn = DriverManager.getConnection( "jdbc:sqlserver://10.136.101.124:1433;databaseName=aiaIMGdb_CSD;selectMethod=cursor", "uatuser", "INGuat02");
			
			String strSql = "  select  dm_doc_id from tbl_dm_doc where company_code=? and doc_creation_dt=? and proposal_no=? and mt_doc_type_cd=?  and bill_no=?";
			
			st = conn.prepareStatement(strSql);
			
			st.setInt(1, company_code);
			st.setString(2, doc_creation_dt);
			st.setString(3, proposalNo);
			st.setString(4, mt_doc_type_cd);
			st.setString(5, bill_no);
			
			//System.out.println(strSql);
			
			rs = st.executeQuery();	
			while (rs.next()) {
			    dm_doc_id=rs.getString("dm_doc_id");
				//System.out.println(dm_doc_id);
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
	
	
	public String getTableNameForDocType(String mt_doc_type){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String tableName="";

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
			conn = DriverManager.getConnection( "jdbc:sqlserver://10.136.101.124:1433;databaseName=aiaIMGdb_CSD;selectMethod=cursor", "uatuser", "INGuat02");
			
			String strSql = "  select tbl_nm from tbl_mt_doc_type where cd=?";
			
			st = conn.prepareStatement(strSql);
			
			st.setString(1, mt_doc_type);
			
			System.out.println(strSql);
			
			rs = st.executeQuery();	
			while (rs.next()) {
				tableName=rs.getString("tbl_nm");
			}
		} catch(Exception io){
			io.printStackTrace();
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
		
		return tableName;
	}
	
	
	
	
	
	
	public boolean insertIntoTblDmDoc(String dataId,String docType,String proposalNo,String process_year,int dmStatus,String tbl_doc_nm,String doc_creation_dt,int companyCode,
			                          String client_no,String client_name,String bill_no,String bill_type,String sub_client_no,String sub_client_name,String file_format,String proposal_type,String indicator,int page_count){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String dm_doc_id="";

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
			conn = DriverManager.getConnection( "jdbc:sqlserver://10.136.101.124:1433;databaseName=aiaIMGdb_CSD;selectMethod=cursor", "uatuser", "INGuat02");
			
			String strSql = " INSERT INTO tbl_dm_doc (id,dm_doc_id,mt_doc_type_cd,proposal_no,process_year,dm_status,tbl_doc_nm,created_by,created_dt,doc_creation_dt,company_code,client_no,client_name,bill_no,bill_type,sub_client_no,sub_client_name,file_format,proposal_type,indicator,page_count) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			
			    PreparedStatement stmt = conn.prepareStatement(strSql);
				String docId = UUID.randomUUID().toString();
				stmt.setString(1, docId);
				System.out.println("Doc Id " + docId);
				stmt.setString(2, dataId);
				stmt.setString(3, docType);
				stmt.setString(4, proposalNo);
				stmt.setString(5,process_year );
				stmt.setInt(6, dmStatus);
				stmt.setString(7, tbl_doc_nm);
				stmt.setString(8, "PrintingAgent_CSD");
				Date currentTime=new Date();
				SimpleDateFormat ymd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 

						
				String created_dt=ymd.format(currentTime);
				System.out.print("------------"+created_dt+"------------");
				stmt.setString(9, created_dt);
				stmt.setString(10,doc_creation_dt);
				stmt.setInt(11, companyCode);
				stmt.setString(12, client_no);
				
				System.out.println(client_no);
				stmt.setString(13, client_name);
				stmt.setString(14, bill_no);
				stmt.setString(15, bill_type);
				stmt.setString(16, sub_client_no);
				stmt.setString(17, sub_client_name);
				stmt.setString(18, file_format);
				stmt.setString(19, proposal_type);
				stmt.setString(20, indicator);
				stmt.setInt(21, page_count);
				
				stmt.execute();
				
				conn.commit();
				
				
				
			
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
	
	public String getG4CycleDate(String filePath){
		String cycledate="";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			
			if (br == null || br.equals(null)) {
				System.out.println("No MemberClaimList Flat File....");
			 }else{
				 String sCurrentline;
					while ((sCurrentline = br.readLine()) != null) {
						
						String data[] = sCurrentline.split("\\|");
						if (data[0].equalsIgnoreCase("0000")) {
							if (data.length >= 3) {
								cycledate=data[2] != null && data[2].length() > 0 ? data[2].trim() : "";
							}
						}
					}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(br!=null){
					br.close();
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

		return cycledate;
	}
	

	
	public boolean insertIntoDocTypeTable(String dm_doc_id,byte[] fileContent,String tableName,String subSchemaYear) {
		
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
String url="jdbc:sqlserver://10.136.101.124:1433;databaseName=aiaIMGdb_CSD_SUBSCHEMA";
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
			conn = DriverManager.getConnection( url.replace("SUBSCHEMA", subSchemaYear)+";selectMethod=cursor", "uatuser", "INGuat02");
			
			String strSql = "INSERT INTO " + tableName + " (dm_doc_id,dm_doc_data, created_dt)VALUES(?,?,?)";
			
			    PreparedStatement stmt = conn.prepareStatement(strSql);
				stmt.setString(1, dm_doc_id);
				stmt.setBytes(2, fileContent);
				Date currentTime=new Date();
				SimpleDateFormat ymd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
				String created_dt=ymd.format(currentTime);
				stmt.setString(3, created_dt);
					
				stmt.execute();
				
				conn.commit();
			
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

}
