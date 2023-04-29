package com.app.constant;

public class QueryConstants {
	
	public QueryConstants(){
		
	};
	
	public final static String ORACLE_MYSQL = "mysql";
	
	public final static String MICROSOFT_MSSQL = "mssql";
	
	public final static String POSTGRESQL = "postgres";
	
	public final static String DB2SERVER = "db2";
	
	public final static String VERTICA = "vertica";
	
	public final static String db2_fetch_all_tables = "select trim(table_schema) || '.' || trim(table_name) from SYSIBM.tables where table_schema = ? and table_type = 'BASE TABLE' order by table_name";
	
	public final static String mysql_fetch_all_tables = "SHOW TABLES";
	 
	public final static String vertica_fetch_all_tables = "select table_name from all_tables where table_type='TABLE'";
	
	public final static String postgres_fetch_all_tables = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
	
	public final static String mssql_fetch_all_tables = "SELECT concat(TABLE_CATALOG, '.', TABLE_SCHEMA, '.', TABLE_NAME) FROM ##.INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME";
	
	public final static String mysql_fetch_all_schemas = "SELECT `schema_name` from INFORMATION_SCHEMA.SCHEMATA WHERE `SCHEMA_NAME` not IN ('information_schema', 'performance_schema','mysql', 'sys')";
	
	public final static String db2_fetch_all_schemas = "SELECT SCHEMANAME from syscat.SCHEMATA WHERE SCHEMANAME not like ('SYS%')";
	
	public final static String vertica_fetch_all_schemas = "select schema_name from v_catalog.schemata s join v_catalog.users u on s.schema_owner_id = u.user_id where is_system_schema ='f' order by schema_name";
	
	public final static String mssql_fetch_all_schemas = "SELECT name FROM SYS.DATABASES WHERE name NOT IN ('master', 'model', 'msdb', 'tempdb', 'Resource','distribution' , 'reportserver', 'reportservertempdb')";
	
	public final static String mysql_fetch_all_column_names = "SELECT concat(TABLE_NAME,'.', COLUMN_NAME) FROM information_schema.COLUMNS WHERE TABLE_NAME = ? and TABLE_SCHEMA = ?  order by COLUMN_NAME";
	
	public final static String mssql_fetch_all_column_names = "SELECT concat(TABLE_NAME,'.', COLUMN_NAME) FROM ##.information_schema.COLUMNS WHERE TABLE_NAME = ?  order by COLUMN_NAME";
	
	public final static String postgres_fetch_all_column_names = "SELECT concat(TABLE_NAME,'.', COLUMN_NAME) FROM information_schema.COLUMNS WHERE TABLE_NAME = ? and TABLE_SCHEMA = 'public'  order by COLUMN_NAME";
	
	public final static String db2_fetch_all_column_names = "SELECT trim(TBCREATOR) || '.' || trim(TBNAME) || '.' || trim(NAME) FROM SYSIBM.SYSCOLUMNS WHERE TBNAME = ? order by name";
	
	public final static String vertica_fetch_all_column_names = "select column_name from columns where table_name = ?";
	
	public final static String fetch_all_features = "SELECT * from features";

}
