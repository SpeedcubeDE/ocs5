package de.nerogar.ocs.sql;

import de.nerogar.ocs.Config;
import de.nerogar.ocs.chat.ChatMessage;
import de.nerogar.ocs.chat.ChatRoom;
import de.nerogar.ocs.chat.ChatRoomManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseChat extends DatabaseAdapter {

	public DatabaseChat(Database database) {
		super(database);
	}

	public void loadChatRooms(ChatRoomManager chatRoomManager) {
		PreparedStatement ps = null;

		List<ChatRoom> chatRooms = new ArrayList<>();

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "chatroom WHERE closed = 0");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				int ownerID = result.getInt("ownerID");
				int minPower = result.getInt("minPower");
				String password = result.getString("password");
				String salt = result.getString("salt");

				ChatRoom chatRoom = new ChatRoom(id, name, ownerID, minPower, password, salt);
				chatRooms.add(chatRoom);
				chatRoomManager.addChatRoom(chatRoom);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM (SELECT * FROM " + prefix + "chat WHERE chatroomID = ? ORDER BY id DESC LIMIT ?) sub ORDER BY id ASC");

			for (ChatRoom chatRoom : chatRooms) {

				ps.setInt(1, chatRoom.id);
				ps.setInt(2, Config.getValue(Config.LOGIN_MSG_LIMIT));

				ResultSet result = ps.executeQuery();

				while (result.next()) {
					int id = result.getInt("id");
					int userID = result.getInt("userID");
					long timestamp = result.getLong("time");
					String message = result.getString("msg");

					chatRoom.addMessage(new ChatMessage(id, userID, chatRoom, false, new String[] { message }, timestamp, true));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}

	}

	public void saveChatRoom(ChatRoom chatRoom) {

		PreparedStatement ps = null;
		boolean exists = false;

		try {
			ps = database.getConnection().prepareStatement("SELECT COUNT(*) FROM " + prefix + "chatroom WHERE id=?");

			ps.setInt(1, chatRoom.id);

			ResultSet result = ps.executeQuery();
			result.next();
			exists = result.getInt(1) == 1;

			ps.close();
			if (exists) {
				ps = database.getConnection().prepareStatement("UPDATE " + prefix + "chatroom SET name=?, minPower=?, password=?, salt=?, closed=? WHERE id=?");

				ps.setString(1, chatRoom.name);
				ps.setInt(2, chatRoom.minPower);
				ps.setString(3, chatRoom.password);
				ps.setString(4, chatRoom.salt);
				ps.setInt(5, chatRoom.closed ? 1 : 0);

				ps.setInt(6, chatRoom.id);

				ps.execute();

			} else if (!exists) {
				ps = database.getConnection().prepareStatement("INSERT INTO " + prefix + "chatroom(id, name, ownerID, minPower, password, salt, closed) VALUES(?, ?, ?, ?, ?, ?, ?)");

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
		} finally {
			closeOrFail(ps);
		}

	}

	public int getNextChatRoomID() {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT Auto_increment FROM information_schema.tables WHERE table_name='" + prefix + "chatroom'");
			ResultSet result = ps.executeQuery();

			result.next();
			return result.getInt("Auto_increment");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeOrFail(ps);
		}
	}

	public int getNextChatMessageID() {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT Auto_increment FROM information_schema.tables WHERE table_name='" + prefix + "chat'");
			ResultSet result = ps.executeQuery();

			result.next();
			return result.getInt("Auto_increment");
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeOrFail(ps);
		}
	}

	public void saveChatMessages(List<ChatMessage> chatMessages) {
		PreparedStatement ps = null;
		try {
			ps = database.getConnection().prepareStatement("INSERT INTO " + prefix + "chat(id, userID, chatroomID, msg, time) VALUES(?, ?, ?, ?, ?)");

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
		} finally {
			closeOrFail(ps);
		}

	}

}
