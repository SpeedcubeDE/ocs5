package de.nerogar.ocs.sql;

import java.sql.*;

import de.nerogar.ocs.user.User;

public class DatabaseUser extends DatabaseAdapter {

	public DatabaseUser(Database database) {
		super(database);
	}

	public int getUserIDByToken(String authToken) {
		PreparedStatement ps = null;
		try {
			ps = database.getConnection().prepareStatement("SELECT id FROM " + prefix + "user WHERE loginToken = ? LIMIT 1");

			ps.setString(1, authToken);
			ResultSet result = ps.executeQuery();

			if (!result.next()) return -1; // No valid user authentification

			return result.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeOrFail(ps);
		}
	}

	public void resetUserToken(int userID) {
		PreparedStatement ps = null;
		try {
			ps = database.getConnection().prepareStatement("UPDATE " + prefix + "user SET loginToken=? WHERE id=?");

			ps.setString(1, "");
			ps.setInt(2, userID);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

	public int getUserIDByName(String username) {
		PreparedStatement ps = null;
		try {
			ps = database.getConnection().prepareStatement("SELECT id FROM " + prefix + "user WHERE name = ? LIMIT 1");

			ps.setString(1, username);
			ResultSet result = ps.executeQuery();

			if (!result.next()) return -1;

			return result.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeOrFail(ps);
		}
	}

	public User getUserByID(int id) {
		PreparedStatement ps = null;
		try {
			String userSQLParameters = "id, registerDate, name, power, status, nameColor, muteTime, banReason, onlineTime, loginCount, chatMsgCount";

			ps = database.getConnection().prepareStatement("SELECT " + userSQLParameters + " FROM " + prefix + "user WHERE id = ? LIMIT 1");

			ps.setInt(1, id);
			ResultSet result = ps.executeQuery();

			if (!result.next()) return null;

			return buildUser(result);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			closeOrFail(ps);
		}
	}

	private User buildUser(ResultSet result) throws SQLException {
		return new User(result.getInt("id"), result.getLong("registerDate"), result.getString("name"), result.getInt("power"), result.getString("status"), result.getString("nameColor"), result.getLong("muteTime"), result.getString("banReason"), result.getLong("onlineTime"), result.getInt("loginCount"), result.getInt("chatMsgCount"));
	}

	public void saveUser(User user) {
		PreparedStatement ps = null;
		try {
			ps = database.getConnection().prepareStatement("UPDATE " + prefix + "user SET power=?, status=?, nameColor=?, muteTime=?, banReason=?, onlineTime=?, loginCount=?, chatMsgCount=? WHERE id=?");

			ps.setInt(1, user.getPower());
			ps.setString(2, user.getStatus());
			ps.setString(3, user.getNameColor());
			ps.setLong(4, user.getMuteTime());
			ps.setString(5, user.getBanReason());
			ps.setLong(6, user.getOnlineTime());
			ps.setLong(7, user.getLoginCount());
			ps.setInt(8, user.getChatMsgCount());

			ps.setInt(9, user.getID());

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}


}
