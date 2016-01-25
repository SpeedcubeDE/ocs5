package de.nerogar.ocs.parse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.nerogar.ocs.user.User;

public class ParseMainUser {

	private ParseChat chatParser;
	private ParseChatRoom chatRoomParser;
	private ParseParty partyParser;
	private ParseUser userParser;
	private ParseProfile profileParser;
	private ParseWhisper whisperParser;

	//private User user;

	public ParseMainUser(User user) {
		//this.user = user;

		chatParser = new ParseChat(user);
		chatRoomParser = new ParseChatRoom(user);
		partyParser = new ParseParty(user);
		userParser = new ParseUser(user);
		profileParser = new ParseProfile(user);
		whisperParser = new ParseWhisper(user);
	}

	public void parse(String jsonText) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonData = (JSONObject) jsonParser.parse(jsonText);
		if (!jsonData.containsKey("type")) return;

		String typeString = (String) jsonData.get("type");
		if (typeString == null) return;

		switch (typeString) {
			case "chat":
				chatParser.parse(jsonData);
				break;
			case "chatRoom":
				chatRoomParser.parse(jsonData);
				break;
			case "party":
				partyParser.parse(jsonData);
				break;
			case "user":
				userParser.parse(jsonData);
				break;
			case "profile":
				profileParser.parse(jsonData);
			case "whisper":
				whisperParser.parse(jsonData);
		}

	}

}
