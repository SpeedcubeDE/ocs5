package de.nerogar.ocs.sql;

import java.sql.*;
import java.util.ArrayList;

import de.nerogar.ocs.Time;
import de.nerogar.ocs.user.User;

public class DatabaseLog extends DatabaseAdapter {

	public DatabaseLog(Database database) {
		super(database);
	}

	public void logLogin(User user, long timestamp) {
		PreparedStatement ps = null;
		try {
			ps = database.getConnection().prepareStatement("INSERT INTO " + prefix + "log_login(userID, time, ip) VALUES(?, ?, ?)");

			ps.setInt(1, user.getID());
			ps.setLong(2, timestamp);
			ps.setString(3, user.connection.getIP());

			ps.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

	public ArrayList<String> getLogLogin(int userID, int limit) {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "log_login WHERE userID = ? ORDER BY id DESC LIMIT ?");

			ps.setInt(1, userID);
			ps.setInt(2, limit);

			ResultSet result = ps.executeQuery();

			ArrayList<String> retMsg = new ArrayList<String>();

			while (result.next()) {
				String tempString = "[" + Time.asString(result.getLong("time")) + "] " + result.getString("ip");
				retMsg.add(tempString);
			}

			return retMsg;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		return null;
	}

	public ArrayList<String> getLogDuplicate(int userID, int limit) {
		PreparedStatement ps = null;

		try {
			/*
			SELECT u.name, l.time, ips.ip
			FROM
			(
			SELECT l.ip
			FROM " + prefix + "log_login l
			WHERE l.userID = ?
			GROUP BY l.ip
			) ips, " + prefix + "log_login l, " + prefix + "user u
			WHERE l.userID = u.id AND l.userID != ? AND ips.ip = l.ip
			GROUP BY u.id, ips.ip
			*/

			ps = database.getConnection().prepareStatement("SELECT u.name, l.time, ips.ip FROM (SELECT l.ip FROM " + prefix + "log_login l WHERE l.userID = ? GROUP BY l.ip) ips, " + prefix + "log_login l, " + prefix + "user u WHERE l.userID = u.id AND l.userID != ? AND ips.ip = l.ip GROUP BY u.id, ips.ip");

			ps.setInt(1, userID);
			ps.setInt(2, userID);

			ResultSet result = ps.executeQuery();

			ArrayList<String> retMsg = new ArrayList<String>();

			while (result.next()) {
				String tempString = Time.asString(result.getLong("time")) + " " + result.getString("name") + " " + result.getString("ip");
				retMsg.add(tempString);
			}

			return retMsg;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		return null;
	}

	public void logCommand(User user, long timestamp, String commandName, String argumentString) {
		PreparedStatement ps = null;
		try {
			ps = database.getConnection().prepareStatement("INSERT INTO " + prefix + "log_command(userID, time, command, arguments) VALUES(?, ?, ?, ?)");

			ps.setInt(1, user.getID());
			ps.setLong(2, timestamp);
			ps.setString(3, commandName);
			ps.setString(4, argumentString);

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

	public ArrayList<String> getLogCommand(int userID, int limit) {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "log_command WHERE userID = ? ORDER BY id DESC LIMIT ?");

			ps.setInt(1, userID);
			ps.setInt(2, limit);

			ResultSet result = ps.executeQuery();

			ArrayList<String> retMsg = new ArrayList<String>();

			while (result.next()) {
				String tempString = "[" + Time.asString(result.getLong("time")) + "] " + result.getString("command") + " " + result.getString("arguments");
				retMsg.add(tempString);

			}

			return retMsg;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		return null;
	}

}
