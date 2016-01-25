package de.nerogar.ocs.sql;

import java.sql.*;
import java.util.*;

import de.nerogar.ocs.*;
import de.nerogar.ocs.Time;
import de.nerogar.ocs.chat.*;
import de.nerogar.ocs.command.Command;
import de.nerogar.ocs.party.Party;
import de.nerogar.ocs.party.PartyUserResult;
import de.nerogar.ocs.user.Profile;
import de.nerogar.ocs.user.User;

public class OCSDatabase {

	//private ComboPooledDataSource dataSource;
	private Connection connection;
	private static final int TIMEOUT = 3000;
	private String host;
	private String user;
	private String password;
	private String database;
	private String prefix;

	public OCSDatabase(String host, String user, String password, String database, String prefix) throws SQLException {
		this.host = host;
		this.user = user;
		this.password = password;
		this.database = database;
		this.prefix = prefix;

		connect();
	}

	private void connect() throws SQLException {
		connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?rewriteBatchedStatements=true", user, password);
	}

	private Connection getConnection() throws SQLException {
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
	}

	public int getUserIDByToken(String authToken) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("SELECT id FROM " + prefix + "user WHERE loginToken = ? LIMIT 1");

			ps.setString(1, authToken);
			ResultSet result = ps.executeQuery();

			if (!result.next()) return -1; // No valid user authentification

			return result.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void resetUserToken(int userID) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("UPDATE " + prefix + "user SET loginToken=? WHERE id=?");

			ps.setString(1, "");
			ps.setInt(2, userID);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getUserIDByName(String username) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("SELECT id FROM " + prefix + "user WHERE name = ? LIMIT 1");

			ps.setString(1, username);
			ResultSet result = ps.executeQuery();

			if (!result.next()) return -1;

			return result.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public User getUserByID(int id) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("SELECT " + getUserSQLParameters() + " FROM " + prefix + "user WHERE id = ? LIMIT 1");

			ps.setInt(1, id);
			ResultSet result = ps.executeQuery();

			if (!result.next()) return null;

			return buildUser(result);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getUserSQLParameters() {
		return "id, registerDate, name, power, status, nameColor, muteTime, banReason, onlineTime, loginCount, chatMsgCount";
	}

	private User buildUser(ResultSet result) throws SQLException {
		return new User(result.getInt("id"), result.getLong("registerDate"), result.getString("name"), result.getInt("power"), result.getString("status"), result.getString("nameColor"), result.getLong("muteTime"), result.getString("banReason"), result.getLong("onlineTime"), result.getInt("loginCount"), result.getInt("chatMsgCount"));
	}

	public void saveUser(User user) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("UPDATE " + prefix + "user SET power=?, status=?, nameColor=?, muteTime=?, banReason=?, onlineTime=?, loginCount=?, chatMsgCount=? WHERE id=?");

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
		}
	}

	public void loadRanks(ArrayList<Rank> ranks) {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT * FROM " + prefix + "rank ORDER BY power ASC");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String name = result.getString("name");
				int power = result.getInt("power");
				boolean show = result.getInt("show") == 1;
				String shortName = result.getString("short");

				ranks.add(new Rank(name, power, show, shortName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadCommands(HashMap<String, Command> commands) {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT name, minPower FROM " + prefix + "command");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				Command command = commands.get(result.getString("name").toLowerCase());
				if (command != null) command.setMinPower(result.getInt("minPower"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public void saveCommand(Command command) {

		PreparedStatement ps;
		boolean exists = false;

		try {
			ps = getConnection().prepareStatement("SELECT COUNT(*) FROM " + prefix + "command WHERE name=?");

			ps.setString(1, command.getName());

			ResultSet result = ps.executeQuery();
			result.next();
			exists = result.getInt(1) == 1;

			if (exists) {
				ps = getConnection().prepareStatement("UPDATE " + prefix + "command SET minPower=? WHERE name=?");

				ps.setInt(1, command.getMinPowerReal());
				ps.setString(2, command.getName());

				ps.execute();

			} else if (!exists) {
				ps = getConnection().prepareStatement("INSERT INTO " + prefix + "command(name, minPower) VALUES(?, ?)");

				ps.setString(1, command.getName());
				ps.setInt(2, command.getMinPowerReal());

				ps.execute();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void loadChatRooms(ChatRoomManager chatRoomManager) {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT * FROM " + prefix + "chatroom WHERE closed = 0");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				int ownerID = result.getInt("ownerID");
				int minPower = result.getInt("minPower");
				String password = result.getString("password");
				String salt = result.getString("salt");

				chatRoomManager.addChatRoom(new ChatRoom(id, name, ownerID, minPower, password, salt));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public void saveChatRoom(ChatRoom chatRoom) {

		PreparedStatement ps;
		boolean exists = false;

		try {
			ps = getConnection().prepareStatement("SELECT COUNT(*) FROM " + prefix + "chatroom WHERE id=?");

			ps.setInt(1, chatRoom.id);

			ResultSet result = ps.executeQuery();
			result.next();
			exists = result.getInt(1) == 1;

			if (exists) {
				ps = getConnection().prepareStatement("UPDATE " + prefix + "chatroom SET name=?, minPower=?, password=?, salt=?, closed=? WHERE id=?");

				ps.setString(1, chatRoom.name);
				ps.setInt(2, chatRoom.minPower);
				ps.setString(3, chatRoom.password);
				ps.setString(4, chatRoom.salt);
				ps.setInt(5, chatRoom.closed ? 1 : 0);

				ps.setInt(6, chatRoom.id);

				ps.execute();

			} else if (!exists) {
				ps = getConnection().prepareStatement("INSERT INTO " + prefix + "chatroom(id, name, ownerID, minPower, password, salt, closed) VALUES(?, ?, ?, ?, ?, ?, ?)");

				ps.setInt(1, chatRoom.id);
				ps.setString(2, chatRoom.name);
				ps.setInt(3, chatRoom.ownerID);
				ps.setInt(4, chatRoom.minPower);
				ps.setString(5, chatRoom.password);
				ps.setString(6, chatRoom.salt);
				ps.setInt(7, chatRoom.closed ? 1 : 0);

				ps.execute();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public int getNextChatRoomID() {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT Auto_increment FROM information_schema.tables WHERE table_name='" + prefix + "chatroom'");
			ResultSet result = ps.executeQuery();

			result.next();
			return result.getInt("Auto_increment");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void loadPermissions(HashMap<String, Integer> permissions) {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT * FROM " + prefix + "permission");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String name = result.getString("name");
				int minPower = result.getInt("minPower");

				permissions.put(name, minPower);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void savePermission(HashMap<String, Integer> permissions) {
		Collection<String> names = permissions.keySet();

		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE " + prefix + "permission SET minPower = ? WHERE name = ?");
			for (String name : names) {
				ps.setInt(1, permissions.get(name));
				ps.setString(2, name);

				ps.addBatch();

			}

			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadConfig(HashMap<String, Integer> configs) {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT * FROM " + prefix + "config");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String name = result.getString("name");
				int value = result.getInt("value");

				configs.put(name, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void saveConfig(HashMap<String, Integer> configs) {
		Collection<String> names = configs.keySet();

		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE " + prefix + "config SET value = ? WHERE name = ?");
			for (String name : names) {
				ps.setInt(1, configs.get(name));
				ps.setString(2, name);

				ps.addBatch();

			}

			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getNextChatMessageID() {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT Auto_increment FROM information_schema.tables WHERE table_name='" + prefix + "chat'");
			ResultSet result = ps.executeQuery();

			result.next();
			return result.getInt("Auto_increment");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void saveChatMessages(List<ChatMessage> chatMessages) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("INSERT INTO " + prefix + "chat(id, userID, chatroomID, msg, time) VALUES(?, ?, ?, ?, ?)");

			int count = 0;
			int batchSize = 1000;

			for (ChatMessage chatMessage : chatMessages) {
				if (!chatMessage.saved) {
					ps.setInt(1, chatMessage.id);
					ps.setInt(2, chatMessage.userID);
					ps.setInt(3, chatMessage.chatRoom.persistant ? chatMessage.chatRoom.id : -1);
					ps.setString(4, chatMessage.asString());
					ps.setLong(5, chatMessage.timestamp);
					ps.addBatch();

					count++;

					if (count % batchSize == 0) {
						ps.executeBatch();
					}

					chatMessage.saved = true;
				}
			}

			ps.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void saveParty(Party party) {

		PreparedStatement ps;
		try {
			//party
			ps = getConnection().prepareStatement("INSERT INTO " + prefix + "party(id, ownerID, cubeType, rounds, startTime, mode) VALUES(?, ?, ?, ?, ?, ?)");

			ps.setInt(1, party.id);
			ps.setInt(2, party.ownerID);
			ps.setString(3, party.scrambleType);
			ps.setInt(4, party.rounds);
			ps.setLong(5, party.startTime);
			ps.setInt(6, party.mode);

			ps.execute();

			//scrambles
			ps = getConnection().prepareStatement("INSERT INTO " + prefix + "partyRound(partyID, round, scramble) VALUES(?, ?, ?)");

			int count = 0;
			int batchSize = 1000;

			for (int i = 0; i < party.scrambles.length; i++) {

				ps.setInt(1, party.id);
				ps.setInt(2, i);
				ps.setString(3, party.scrambles[i]);
				ps.addBatch();

				count++;

				if (count % batchSize == 0) {
					ps.executeBatch();
				}
			}

			ps.executeBatch();

			//times
			ps = getConnection().prepareStatement("INSERT INTO " + prefix + "partytime(partyID, userID, round, time) VALUES(?, ?, ?, ?)");

			count = 0;
			batchSize = 1000;

			for (PartyUserResult pur : party.results) {
				for (int i = 0; i < pur.times.length; i++) {

					ps.setInt(1, party.id);
					ps.setInt(2, pur.userID);
					ps.setInt(3, i);
					ps.setInt(4, pur.times[i]);
					ps.addBatch();

					count++;

					if (count % batchSize == 0) {
						ps.executeBatch();
					}
				}
			}

			ps.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getNextPartyID() {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT Auto_increment FROM information_schema.tables WHERE table_name='" + prefix + "party'");
			ResultSet result = ps.executeQuery();

			result.next();
			return result.getInt("Auto_increment");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void loadPartyResultsOfUser(Profile profile) {
		PreparedStatement ps;

		try {

			/*
			 * SELECT MIN(pt.time) as best, SUM(pt.time) as time, COUNT(pt.time) as count, p.cubeType as type, p.mode as mode
			 * FROM " + prefix + "party p, " + prefix + "partytime pt, " + prefix + "user u
			 * WHERE pt.userID = u.id AND u.name = ? AND p.id = pt.partyID AND pt.time > 0
			 * GROUP BY p.cubeType, p.mode;
			 */

			ps = getConnection().prepareStatement("SELECT MIN(pt.time) as best, SUM(pt.time) as time, COUNT(pt.time) as count, p.cubeType as type, p.mode as mode FROM " + prefix + "party p, " + prefix + "partytime pt, " + prefix + "user u WHERE pt.userID = u.id AND u.name = ? AND p.id = pt.partyID AND pt.time > 0 GROUP BY p.cubeType, p.mode;");

			ps.setString(1, profile.getUser().getUsername());
			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String type = result.getString("type");
				long time = result.getLong("time");
				long count = result.getLong("count");
				long mode = result.getLong("mode");
				long best = result.getLong("best");

				profile.addCubeTimes(type, time, count, best, mode);
			}

			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return;
	}

	public boolean loadUserStatistics(Profile profile) {
		PreparedStatement ps;

		try {

			ps = getConnection().prepareStatement("SELECT wcaID, forumID  FROM " + prefix + "user WHERE id=? LIMIT 1;");

			ps.setInt(1, profile.getUser().getID());
			ResultSet result = ps.executeQuery();

			if (!result.next()) return false;

			profile.wcaID = result.getString("wcaID");
			profile.forumID = result.getInt("forumID");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	public void logCommand(User user, long timestamp, String commandName, String argumentString) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("INSERT INTO " + prefix + "log_command(userID, time, command, arguments) VALUES(?, ?, ?, ?)");

			ps.setInt(1, user.getID());
			ps.setLong(2, timestamp);
			ps.setString(3, commandName);
			ps.setString(4, argumentString);

			ps.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getLogCommand(int userID, int limit) {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT * FROM " + prefix + "log_command WHERE userID = ? ORDER BY id DESC LIMIT ?");

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
		}

		return null;
	}

	public void logLogin(User user, long timestamp) {
		PreparedStatement ps;
		try {
			ps = getConnection().prepareStatement("INSERT INTO " + prefix + "log_login(userID, time, ip) VALUES(?, ?, ?)");

			ps.setInt(1, user.getID());
			ps.setLong(2, timestamp);
			ps.setString(3, user.connection.getIP());

			ps.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getLogLogin(int userID, int limit) {
		PreparedStatement ps;

		try {
			ps = getConnection().prepareStatement("SELECT * FROM " + prefix + "log_login WHERE userID = ? ORDER BY id DESC LIMIT ?");

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
		}

		return null;
	}

	public ArrayList<String> getLogDuplicate(int userID, int limit) {
		PreparedStatement ps;

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

			ps = getConnection().prepareStatement("SELECT u.name, l.time, ips.ip FROM (SELECT l.ip FROM " + prefix + "log_login l WHERE l.userID = ? GROUP BY l.ip) ips, " + prefix + "log_login l, " + prefix + "user u WHERE l.userID = u.id AND l.userID != ? AND ips.ip = l.ip GROUP BY u.id, ips.ip");

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
		}

		return null;
	}

}
