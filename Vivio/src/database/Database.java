package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
	
	private final static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private final static String database = "command_data";
	
	//build connection
	private static Connection connect() throws SQLException {
		try {
			Class.forName(driver).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection("jdbc:derby:" + database+";create=true");
	}
	
	//update, delete
	public static void execRaw(String query) throws SQLException {
		Connection conn = connect();
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		stmt.close();
		conn.close();
	}
	
	//insert
	public static void insert(String table, String columns, String values) throws SQLException {
		Connection conn = connect();
		Statement stmt = conn.createStatement();
		stmt.execute("insert into "+table+"("+columns+") values ("+values+");");
		stmt.close();
		conn.close();
	}
	
	//select
	public static List<HashMap<String,Object>> select(String query) throws SQLException{
		Connection conn = connect();
		Statement stmt = conn.createStatement();
		List<HashMap<String,Object>> rv =  convertResultSetToList(stmt.executeQuery(query));
		stmt.close();
		conn.close();
		return rv;
	}
	
	//create tables
	public static void createTable(String tableName, String columns) throws SQLException{
		Connection conn = connect();
		Statement stmt = conn.createStatement();
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getTables(null, "APP", tableName.toUpperCase(), null);
		if(!rs.next()) {
			stmt.execute("create table "+tableName+"(id integer not null generated always as identity (start with 1, increment by 1), "+columns+", constraint primary_key primary key(id))");
		}
		rs.close();
		stmt.close();
		conn.close();
	}
	
	public static boolean hasRow(String query) throws SQLException {
		return select(query).size() > 0;
	}
	
	//turn a result set into an iterable, mutable list
	private static List<HashMap<String,Object>> convertResultSetToList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

	    while (rs.next()) {
	        HashMap<String,Object> row = new HashMap<String, Object>(columns);
	        for(int i=1; i<=columns; ++i) {
	            row.put(md.getColumnName(i),rs.getObject(i));
	        }
	        list.add(row);
	    }

	    return list;
	}
}
