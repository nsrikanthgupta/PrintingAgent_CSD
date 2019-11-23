package com.aia.common.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

@Service
public class DBCPDataSourceUtil {
	private DBCPDataSourceUtil(){}
	
	@Value("${sumyfap00021.db.username}")
	private  String sumyfap00021DbUserName;
	
	@Value("${sumyfap00021.db.password}")
	private  String sumyfap00021Dbpasword;
	
	@Value("${sumyfap00021.db.servername}")
	private  String sumyfap00021ServerName;
	
	@Value("${sumyfap00021.db.schemaname}")
	private  String sumyfap00021SchemaName;

	
	/*
	private static String sumyfap00021DbUserName="dotnetuser";
	private static String sumyfap00021Dbpasword="ABC@2020";
	private static String sumyfap00021ServerName="10.136.101.123";
	private static String sumyfap00021SchemaName="AetnaI3sdb";*/
	
public  Connection con =null;
public  Connection  getConnection() throws SQLException {
	SQLServerDataSource ds = new SQLServerDataSource();
	ds.setUser(sumyfap00021DbUserName);
	ds.setPassword(sumyfap00021Dbpasword);
	ds.setServerName(sumyfap00021ServerName);
//	ds.setPortNumber(symapanyDbPort);
	ds.setDatabaseName(sumyfap00021SchemaName);
	if(con==null || con.isClosed()) {
		 con =ds.getConnection();
	}
	return con;
 }
}
