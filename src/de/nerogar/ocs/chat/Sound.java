package de.nerogar.ocs.chat;

import org.json.simple.JSONObject;

import de.nerogar.ocs.Sendable;
import de.nerogar.ocs.user.User;

public class Sound extends Sendable {

	public static final Sound PARTY_NEW_ROUND = new Sound("newRound.ogg", 1f);

	private String filename;
	private float volume;

	public Sound(String filename, float volume) {
		this.filename = filename;
		this.volume = volume;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject alertJSON = new JSONObject();

		alertJSON.put("type", "sound");
		alertJSON.put("filename", filename);
		alertJSON.put("volume", volume);

		return alertJSON.toJSONString();
	}
}
