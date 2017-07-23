package de.nerogar.ocs.chat;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.nerogar.ocs.OCSServer;
import de.nerogar.ocs.Sendable;
import de.nerogar.ocs.user.User;

public class ChatMessage extends Sendable {
	public static int MAX_ID;

	public int id;
	public int userID;
	public ChatRoom chatRoom;
	public boolean me;
	public String[] message;
	public long timestamp;

	public boolean saved; //saved in the database

	public ChatMessage(int id, int userID, ChatRoom chatRoom, boolean me, String[] message, long timestamp, boolean saved) {
		this.id = id;
		this.userID = userID;
		this.chatRoom = chatRoom;
		this.me = me;
		this.message = message;
		this.timestamp = timestamp;

		this.saved = saved;
	}

	public ChatMessage(int userID, ChatRoom chatRoom, boolean me, String[] message, long timestamp) {
		this(getNewID(), userID, chatRoom, me, message, timestamp, false);
	}

	public ChatMessage(int userID, ChatRoom chatRoom, boolean me, List<String> message, long timestamp) {
		this(userID, chatRoom, me, message.toArray(new String[message.size()]), timestamp);
	}

	public ChatMessage(int userID, ChatRoom chatRoom, boolean me, String message, long timestamp) {
		this(userID, chatRoom, me, new String[] { message }, timestamp);
	}

	public String asString() {
		if (message.length == 1) {
			return message[0];
		} else {
			StringBuilder retMessage = new StringBuilder();
			for (int i = 0; i < message.length; i++) {
				retMessage.append(message[i]);
			}
			return retMessage.toString();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject messageJSON = new JSONObject();

		JSONArray messageArray = new JSONArray();

		for (String msg : message) {
			messageArray.add(msg);
		}

		messageJSON.put("userID", userID);
		messageJSON.put("roomID", chatRoom.id);
		messageJSON.put("me", me);
		messageJSON.put("msg", messageArray);
		messageJSON.put("time", timestamp / OCSServer.get1SecondTimestamp());
		messageJSON.put("type", "chat");

		return messageJSON.toJSONString();
	}

	public static int getNewID() {
		MAX_ID++;
		return MAX_ID - 1;
	}
}
