package de.nerogar.ocs.chat;

import org.json.simple.JSONObject;

import de.nerogar.ocs.Sendable;
import de.nerogar.ocs.user.User;

public class Alert extends Sendable {

	public static final String ALERT = "alert";
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String WARNING = "warning";
	public static final String INFORMATION = "information";

	private String type;
	private boolean sticky;
	private String msg;

	public Alert(String type, boolean sticky, String msg) {
		this.type = type;
		this.sticky = sticky;
		this.msg = msg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject alertJSON = new JSONObject();

		alertJSON.put("type", "alert");
		alertJSON.put("action", type);
		alertJSON.put("sticky", sticky);
		alertJSON.put("msg", msg);

		return alertJSON.toJSONString();
	}
}
