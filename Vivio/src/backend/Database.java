package backend;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Database {
	
	private final static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private final static String database = "command_data";
	
	private static Connection conn;
	private static int queryCount = 0;
	
	private static void queryCommit() throws SQLException {
		if(queryCount++ > 0)  {
			conn.commit();
			queryCount = 0;
		}
	}
	
	//build connection
	private static Connection connect() throws SQLException {
		try {
			Class.forName(driver).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection rv = DriverManager.getConnection("jdbc:derby:" + database+";create=true");
		rv.setAutoCommit(true);
		return rv;
	}
	
	//update, delete
	public static void execRaw(String query) throws SQLException {
		if(conn == null) conn = connect();
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		stmt.close();
		queryCommit();
	}
	
	//insert
	public static void insert(String table, String columns, String values) throws SQLException {
		if(conn == null) conn = connect();
		Statement stmt = conn.createStatement();
		stmt.execute("insert into "+table+"("+columns+") values ("+values+")");
		stmt.close();
		queryCommit();
	}
	
	//select
	public static List<HashMap<String,Object>> select(String query, int max) throws SQLException{
		if(conn == null) conn = connect();
		Statement stmt = conn.createStatement();
		stmt.setMaxRows(max);
		List<HashMap<String,Object>> rv =  convertResultSetToList(stmt.executeQuery(query));
		stmt.close();
		return rv;
	}
	
	public static List<HashMap<String,Object>> select(String query) throws SQLException {
		return select(query, 0);
	}
	
	//create tables
	public static void createTable(String tableName, String columns) throws SQLException{
		if(conn == null) conn = connect();
		Statement stmt = conn.createStatement();
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getTables(null, "APP", tableName.toUpperCase(), null);
		if(!rs.next()) {
			stmt.execute("create table "+tableName+"(id integer not null generated always as identity (start with 1, increment by 1), "+columns+", constraint primary_key_"+tableName.toLowerCase()+" primary key(id))");
		}
		rs.close();
		stmt.close();
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

	public static String getEnclosedString(String s) {
		return "'" + s.replaceAll("'", "''") + "'";
				
	}
	
	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String formatTimestamp(Date date) {
		return getEnclosedString(timestampFormat.format(date));
	}
	
	public static String formatTimestamp(long milliseconds) {
		return formatTimestamp(new Date(milliseconds));
	}
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String formatDate(Date date) {
		return getEnclosedString(dateFormat.format(date));
	}
	
	public static String formatDate(long milliseconds) {
		return formatDate(new Date(milliseconds));
	}
	
private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public static String formatTime(Date date) {
		return getEnclosedString(timeFormat.format(date));
	}
	
	public static String formatTime(long milliseconds) {
		return formatTime(new Date(milliseconds));
	}
	
	public static Date parseTimestamp(String timestamp) throws ParseException
	{
		return timestampFormat.parse(timestamp);
	}
	
	public static Date parseDate(String date) throws ParseException
	{
		return dateFormat.parse(date);
	}
	
	public static Date parseTime(String time) throws ParseException
	{
		return timeFormat.parse(time);
	}
}
