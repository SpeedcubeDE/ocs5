package de.nerogar.ocs.chat;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.nerogar.ocs.*;
import de.nerogar.ocs.user.User;

public class ChatRoom extends Sendable {
	public static int MAX_ID;

	public int id;
	public String name;
	public int ownerID;
	public int minPower; //staff rooms
	public String password;
	public String salt;
	public boolean closed;
	public boolean persistant;

	public ArrayList<ChatMessage> messages;
	public Userlist userlist;

	public ChatRoom(String name, int ownerID, int minPower, String password, String salt) {
		this(getNewID(), name, ownerID, minPower, password, salt);
	}

	public ChatRoom(int id, String name, int ownerID, int minPower, String password, String salt) {
		this.id = id;
		this.name = name;
		this.ownerID = ownerID;
		this.minPower = minPower;
		this.password = password;
		this.salt = salt;
		persistant = true;

		messages = new ArrayList<ChatMessage>();
		userlist = new Userlist();
	}

	public void addMessage(boolean me, String chatString, User user) {
		if (user == null) { //System message
			ChatMessage newMessage = new ChatMessage(-1, this, me, chatString, OCSServer.getTimestamp());
			messages.add(newMessage);
			newMessage.broadcast(userlist);
		}else if(!user.isMutedAndPrevent()){
			ChatMessage newMessage = new ChatMessage(user.getID(), this, me, chatString, OCSServer.getTimestamp());
			messages.add(newMessage);
			newMessage.broadcast(userlist);
			user.incChatMsgCount();	
		}
	}

	public boolean hasUser(User user) {
		return userlist.hasUser(user);
	}

	public boolean hasPassword() {
		return !password.equals("");
	}

	public void addUser(User user) {
		boolean userWasInRoom = userlist.hasUser(user);

		userlist.addUser(user);

		int loginMsgLimit = Config.getValue(Config.LOGIN_MSG_LIMIT);

		OCSServer.chatRoomManager.broadcast(OCSServer.userlist);

		for (int i = (Math.max(messages.size() - 1 - loginMsgLimit, 0)); i < messages.size(); i++) {
			messages.get(i).sendTo(user);
		}

		broadcast(userlist);

		if (!userWasInRoom && !user.isMutedAndPrevent()) {
			new ChatMessage(-1, this, false, OCSStrings.getString("chat.room.enter", user.getUsername()), OCSServer.getTimestamp()).broadcast(userlist);
		}
	}

	public void removeUser(User user) {
		if (userlist.hasUser(user)) {
			userlist.removeUser(user);

			if (userlist.userCount() > 0 || persistant) {
				broadcast(userlist);
				OCSServer.chatRoomManager.broadcast(OCSServer.userlist);
				if (!user.isMutedAndPrevent()) {
					new ChatMessage(-1, this, false, OCSStrings.getString("chat.room.leave", user.getUsername()), OCSServer.getTimestamp()).broadcast(userlist);
				}
			} else {
				OCSServer.chatRoomManager.removeChatRoom(id, null);
			}
		}
	}

	public void removeAllUsers() {
		for (User user : userlist.getUsers()) {
			removeUser(user);
		}
	}

	public int getUserCount() {
		return userlist.userCount();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject userlistJSON = new JSONObject();

		JSONArray usersArray = new JSONArray();
		for (User listUser : userlist.getUsers()) {
			JSONObject userObject = new JSONObject();

			userObject.put("id", listUser.getID());

			usersArray.add(userObject);
		}
		userlistJSON.put("users", usersArray);

		userlistJSON.put("roomID", id);
		userlistJSON.put("type", "roomUserlist");

		return userlistJSON.toJSONString();
	}

	public void removeOldMessages() {
		//könnte fehlerhaft sein
		int loginMsgLimit = Config.getValue(Config.LOGIN_MSG_LIMIT);
		for (int i = messages.size() - 1 - loginMsgLimit; i >= 0; i--) {
			ChatMessage message = messages.get(i);
			if (message.saved) messages.remove(i);
		}
	}

	public boolean canEnter(User user) {
		return user.getPower() >= minPower;
	}

	public boolean canEdit(User user) {
		return user == null || ((ownerID == user.getID() || user.hasPermission(User.EDIT_ALL_CHAT_ROOMS)) && ownerID != -1);
	}

	public void flushUserlist() {
		userlist.flushUsers();
	}

	public static int getNewID() {
		MAX_ID++;
		return MAX_ID - 1;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ChatRoom) {
			ChatRoom chatRoom = (ChatRoom) o;
			return chatRoom.id == id;
		}
		return false;
	}

	public String getType() {
		return "default";
	}
}
