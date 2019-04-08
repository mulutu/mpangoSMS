/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mpango.sms;

//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;

public final class DBConnector {
	public Connection conn;
	private Statement statement;
	public static DBConnector db;

	private DBConnector() {
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "mpango_farm";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "farmgrow";
		String password = "Mbwangaskater2018!$";
		try {
			Class.forName(driver).newInstance();
			this.conn = (Connection) DriverManager.getConnection(url + dbName, userName, password);
		} catch (Exception sqle) {
			sqle.printStackTrace();
		}
	}

	/**
	 *
	 * @return MysqlConnect Database connection object
	 */
	public static synchronized DBConnector getDbCon() {
		if (db == null) {
			db = new DBConnector();
		}
		return db;

	}

	/**
	 *
	 * @param query
	 *            String The query to be executed
	 * @return a ResultSet object containing the results or null if not
	 *         available
	 * @throws SQLException
	 */
	public ResultSet query(String query) throws SQLException {
		statement = db.conn.createStatement();
		ResultSet res = statement.executeQuery(query);
		return res;
	}

	/**
	 * @desc Method to insert data to a table
	 * @param insertQuery
	 *            String The Insert query
	 * @return boolean
	 * @throws SQLException
	 */
	public int insert(String insertQuery) throws SQLException {
		statement = db.conn.createStatement();
		int result = statement.executeUpdate(insertQuery);
		return result;

	}
}
