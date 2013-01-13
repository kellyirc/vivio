/*
 * @author Kyle Kemp
 */
package backend;

import java.math.BigDecimal;
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

// TODO: Auto-generated Javadoc
/**
 * The Class Database.
 */
public class Database {

	/** The Constant driver. */
	private final static String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	/** The Constant database. */
	private final static String database = "command_data";

	/** The conn. */
	private static Connection conn;

	// build connection
	/**
	 * Connect.
	 * 
	 * @return the connection
	 * @throws SQLException
	 *             the sQL exception
	 */
	private static Connection connect() throws SQLException {
		try {
			Class.forName(driver).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection rv = DriverManager.getConnection("jdbc:derby:" + database
				+ ";create=true");
		rv.setAutoCommit(true);
		return rv;
	}

	// update, delete
	/**
	 * Exec raw.
	 * 
	 * @param query
	 *            the query
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static void execRaw(String query) throws SQLException {
		if (conn == null)
			conn = connect();
		Statement stmt = conn.createStatement();
		stmt.execute(query);
		stmt.close();
	}

	// insert
	/**
	 * Insert.
	 * 
	 * @param table
	 *            the table
	 * @param columns
	 *            the columns
	 * @param values
	 *            the values
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static void insert(String table, String columns, String values)
			throws SQLException {
		if (conn == null)
			conn = connect();
		Statement stmt = conn.createStatement();
		stmt.execute("insert into " + table + "(" + columns + ") values ("
				+ values + ")");
		stmt.close();
	}

	/**
	 * Insert.
	 * 
	 * @param table
	 *            the table
	 * @param columns
	 *            the columns
	 * @param array
	 *            the array
	 * @param isString
	 *            the is string
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static void insert(String table, String columns, Object[] array,
			boolean[] isString) throws SQLException {
		String values = "";
		for (int i = 0; i < array.length; i++) {
			if (isString[i])
				values += Database.getEnclosedString((String) array[i]);
			else
				values += String.valueOf(array[i]);
			if (i != array.length - 1)
				values += ",";
		}
		insert(table, columns, values);
	}

	// select
	/**
	 * Select.
	 * 
	 * @param query
	 *            the query
	 * @param max
	 *            the max
	 * @return the list
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static List<HashMap<String, Object>> select(String query, int max)
			throws SQLException {
		if (conn == null)
			conn = connect();
		Statement stmt = conn.createStatement();
		stmt.setMaxRows(max);
		List<HashMap<String, Object>> rv = convertResultSetToList(stmt
				.executeQuery(query));
		stmt.close();
		return rv;
	}

	/**
	 * Select.
	 * 
	 * @param query
	 *            the query
	 * @return the list
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static List<HashMap<String, Object>> select(String query)
			throws SQLException {
		return select(query, 0);
	}

	// create tables
	/**
	 * Creates the table.
	 * 
	 * @param tableName
	 *            the table name
	 * @param columns
	 *            the columns
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static void createTable(String tableName, String columns)
			throws SQLException {
		if (conn == null)
			conn = connect();
		Statement stmt = conn.createStatement();
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getTables(null, "APP", tableName.toUpperCase(),
				null);
		if (!rs.next()) {
			stmt.execute("create table "
					+ tableName
					+ "(id integer not null generated always as identity (start with 1, increment by 1), "
					+ columns + ", constraint primary_key_"
					+ tableName.toLowerCase() + " primary key(id))");
		}
		rs.close();
		stmt.close();
	}

	/**
	 * Checks for row.
	 * 
	 * @param query
	 *            the query
	 * @return true, if successful
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static boolean hasRow(String query) throws SQLException {
		return select(query).size() > 0;
	}

	/**
	 * Gets the last generated id.
	 * 
	 * @param tableName
	 *            the table name
	 * @return the last generated id
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static int getLastGeneratedId(String tableName) throws SQLException {
		return ((BigDecimal) Database
				.select("select IDENTITY_VAL_LOCAL() as lastid from "
						+ tableName).get(0).get("LASTID")).intValue();
	}

	// turn a result set into an iterable, mutable list
	/**
	 * Convert result set to list.
	 * 
	 * @param rs
	 *            the rs
	 * @return the list
	 * @throws SQLException
	 *             the sQL exception
	 */
	private static List<HashMap<String, Object>> convertResultSetToList(
			ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		while (rs.next()) {
			HashMap<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(row);
		}

		return list;
	}

	/**
	 * Gets the enclosed string.
	 * 
	 * @param s
	 *            the s
	 * @return the enclosed string
	 */
	public static String getEnclosedString(String s) {
		return "'" + s.replaceAll("'", "''") + "'";

	}

	/** The Constant timestampFormat. */
	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * Format timestamp.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formatTimestamp(Date date) {
		return getEnclosedString(timestampFormat.format(date));
	}

	/**
	 * Format timestamp.
	 * 
	 * @param milliseconds
	 *            the milliseconds
	 * @return the string
	 */
	public static String formatTimestamp(long milliseconds) {
		return formatTimestamp(new Date(milliseconds));
	}

	/** The Constant dateFormat. */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/**
	 * Format date.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formatDate(Date date) {
		return getEnclosedString(dateFormat.format(date));
	}

	/**
	 * Format date.
	 * 
	 * @param milliseconds
	 *            the milliseconds
	 * @return the string
	 */
	public static String formatDate(long milliseconds) {
		return formatDate(new Date(milliseconds));
	}

	/** The Constant timeFormat. */
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss");

	/**
	 * Format time.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String formatTime(Date date) {
		return getEnclosedString(timeFormat.format(date));
	}

	/**
	 * Format time.
	 * 
	 * @param milliseconds
	 *            the milliseconds
	 * @return the string
	 */
	public static String formatTime(long milliseconds) {
		return formatTime(new Date(milliseconds));
	}

	/**
	 * Parses the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date parseTimestamp(String timestamp) throws ParseException {
		return timestampFormat.parse(timestamp);
	}

	/**
	 * Parses the date.
	 * 
	 * @param date
	 *            the date
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date parseDate(String date) throws ParseException {
		return dateFormat.parse(date);
	}

	/**
	 * Parses the time.
	 * 
	 * @param time
	 *            the time
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date parseTime(String time) throws ParseException {
		return timeFormat.parse(time);
	}

	/**
	 * Gets the random row.
	 * 
	 * @param data
	 *            the data
	 * @return the random row
	 */
	public static HashMap<String, Object> getRandomRow(
			List<HashMap<String, Object>> data) {
		return data.get((int) (Math.random() * data.size()));
	}
}
