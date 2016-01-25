package de.nerogar.ocs.sql;

import java.sql.*;

import de.nerogar.ocs.Logger;

public class Database {

	private Connection connection;
	private static final int TIMEOUT = 10;
	private String host;
	private String user;
	private String password;
	private String database;
	private String prefix;

	private boolean closed;

	public Database(String host, String user, String password, String database, String prefix) throws SQLException {
		this.host = host;
		this.user = user;
		this.password = password;
		this.database = database;
		this.prefix = prefix;

		connect();
	}

	private void connect() throws SQLException {
		closed = false;

		connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?rewriteBatchedStatements=true", user, password);
	}

	public Connection getConnection() throws SQLException {
		if (closed) return null;

		if (!connection.isValid(TIMEOUT)) {
			connection.close();
			connect();
		}
		return connection;
	}

	public void closeConnection() {
		try {
			getConnection().close();
		} catch (SQLException e) {
			Logger.log(Logger.ERROR, "Could not close database.");
			e.printStackTrace();
		}

		closed = true;
	}

	public String getPrefix() {
		return prefix;
	}
}
