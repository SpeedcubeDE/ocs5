package de.nerogar.ocs.chat;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.Sendable;
import de.nerogar.ocs.security.Password;
import de.nerogar.ocs.sql.DatabaseChat;
import de.nerogar.ocs.user.User;

public class ChatRoomManager extends Sendable {
	private HashMap<Integer, ChatRoom> chatRooms;

	private DatabaseChat databaseChat;

	public ChatRoomManager() {
		databaseChat = new DatabaseChat(OCSServer.databaseNew);

		chatRooms = new HashMap<Integer, ChatRoom>();
		databaseChat.loadChatRooms(this);
		ChatRoom.MAX_ID = databaseChat.getNextChatRoomID();
		ChatMessage.MAX_ID = databaseChat.getNextChatMessageID();
	}

	public boolean addChatRoom(ChatRoom chatRoom) {
		if (!hasRoom(chatRoom)) {
			chatRooms.put(chatRoom.id, chatRoom);
			broadcast(OCSServer.userlist);
			return true;
		}
		return false;
	}

	public boolean removeChatRoom(int id, User user) {
		ChatRoom chatRoom = getChatRoom(id);
		if (chatRoom != null) {
			if (chatRoom.canEdit(user)) {
				chatRoom.closed = true;
				chatRoom.removeAllUsers();

				broadcast(OCSServer.userlist);

				return true;
			}
		}
		return false;
	}

	public ChatRoom getChatRoom(int id) {
		return chatRooms.get(id);
	}

	public boolean hasRoom(ChatRoom chatRoom) {
		for (ChatRoom cr : getAvailableRooms(null)) {
			if (cr.equals(chatRoom)) return true;
		}
		return false;
	}

	public ChatRoom getChatRoom(ChatRoom chatRoom) {
		for (ChatRoom cr : getAvailableRooms(null)) {
			if (cr.equals(chatRoom)) return cr;
		}
		return null;
	}

	public Collection<ChatRoom> getAvailableRooms(User user) {
		ArrayList<ChatRoom> availableChatRooms = new ArrayList<ChatRoom>();

		for (ChatRoom chatRoom : chatRooms.values()) {
			if (!chatRoom.closed && (user == null || chatRoom.canEnter(user))) availableChatRooms.add(chatRoom);
		}

		return availableChatRooms;
	}

	public Collection<ChatRoom> getChatRooms() {
		ArrayList<ChatRoom> availableChatRooms = new ArrayList<ChatRoom>();

		for (ChatRoom chatRoom : chatRooms.values()) {
			availableChatRooms.add(chatRoom);
		}

		return availableChatRooms;
	}

	public boolean enterChatRoom(User user, int chatRoomID, String password) {
		ChatRoom enterRoom = getChatRoom(chatRoomID);
		if (enterRoom != null && enterRoom.canEnter(user) && !enterRoom.closed && !enterRoom.hasUser(user)) {
			if (enterRoom.hasPassword() && !Password.isPasswordValid(password, enterRoom.password, enterRoom.salt)) return false;

			enterRoom.addUser(user);

			return true;
		}
		return false;
	}

	public boolean leaveChatRoom(User user, int chatRoomID) {
		ChatRoom leaveRoom = getChatRoom(chatRoomID);
		if (leaveRoom != null && leaveRoom.hasUser(user)) {
			leaveRoom.removeUser(user);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		Collection<ChatRoom> availableRooms = OCSServer.chatRoomManager.getAvailableRooms(user);

		JSONObject roomsJSON = new JSONObject();
		JSONArray roomsArray = new JSONArray();
		for (ChatRoom chatRoom : availableRooms) {
			JSONObject roomObject = new JSONObject();

			roomObject.put("name", chatRoom.name);
			roomObject.put("id", chatRoom.id);
			roomObject.put("hasPW", chatRoom.hasPassword());
			roomObject.put("inRoom", chatRoom.hasUser(user));
			roomObject.put("userNum", chatRoom.getUserCount());
			roomObject.put("canClose", chatRoom.canEdit(user));
			roomObject.put("type", chatRoom.getType());

			roomsArray.add(roomObject);
		}

		roomsJSON.put("rooms", roomsArray);

		roomsJSON.put("type", "roomlist");

		return roomsJSON.toJSONString();
	}

	public void saveAll() {
		for (ChatRoom chatRoom : chatRooms.values()) {
			if (chatRoom.persistant) databaseChat.saveChatRoom(chatRoom);
			databaseChat.saveChatMessages(chatRoom.messages);

			/*for (ChatMessage chatMessage : chatRoom.messages) {
				if (!chatMessage.saved) {
					OCSServer.database.saveChatMessage(chatMessage);
					chatMessage.saved = true;
				}
			}*/

			chatRoom.removeOldMessages();
		}

		ArrayList<ChatRoom> roomList = new ArrayList<ChatRoom>();
		roomList.addAll(chatRooms.values());
		for (ChatRoom chatRoom : roomList) {
			if (chatRoom.closed) chatRooms.remove(chatRoom.id);
		}
	}

	public void removeUser(User user) {
		for (ChatRoom chatRoom : chatRooms.values()) {
			chatRoom.removeUser(user);
		}
	}

	public void flushUserlists() {
		for (ChatRoom chatRoom : chatRooms.values()) {
			chatRoom.flushUserlist();
		}
	}

}
