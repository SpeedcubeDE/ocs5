package de.nerogar.ocs.user;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.nerogar.ocs.*;
import de.nerogar.ocs.party.Party;

public class Profile extends Sendable {

	private User user;

	private boolean dirty;

	public HashMap<String, TimeSet> times;
	public String wcaID;
	public int forumID;

	//public String banReason;
	//public long onlineTime;
	//public int loginCount;
	//public int chatMsgCount;

	public Profile(User user) {
		this.user = user;
		loadFromDatabase();
	}

	public void loadFromDatabase() {
		times = new HashMap<String, TimeSet>();
		OCSServer.databaseProfile.loadPartyResultsOfUser(this);
		dirty = !OCSServer.databaseProfile.loadUserStatistics(this);
	}

	public void setDirty() {
		dirty = true;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void addCubeTimes(String type, long time, long count, long best, long mode) {
		TimeSet timeSet = times.get(type);
		if (timeSet == null) timeSet = new TimeSet(type);

		if (mode == Party.MODE_NORMAL) {
			timeSet.timeN += time;
			timeSet.countN += count;
			timeSet.bestN = best;
		} else {
			timeSet.timeM += time;
			timeSet.countM += count;
			timeSet.bestM = best;
		}

		times.put(type, timeSet);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(User user) {
		JSONObject userObject = new JSONObject();

		userObject.put("userID", user.getID());
		
		userObject.put("wcaID", wcaID);
		userObject.put("forumID", forumID);

		userObject.put("registerDate", user.getRegisterDate());
		userObject.put("onlineTime", user.getOnlineTime());
		userObject.put("loginCount", user.getLoginCount());
		userObject.put("chatMsgCount", user.getChatMsgCount());
		userObject.put("status", user.getStatus());
		userObject.put("rank", Rank.getRankString(user.getPower(), user.hasPermission(User.SEE_ALL_RANKS)));
		if (user.hasPermission(User.MODERATE_USER)) userObject.put("banReason", user.getBanReason());

		JSONArray timesArrayJSON = new JSONArray();

		for (TimeSet timeSet : times.values()) {
			JSONObject timeJSON = new JSONObject();
			timeJSON.put("type", timeSet.type);

			timeJSON.put("timeN", timeSet.timeN);
			timeJSON.put("countN", timeSet.countN);
			timeJSON.put("bestN", timeSet.bestN);

			timeJSON.put("timeM", timeSet.timeM);
			timeJSON.put("countM", timeSet.countM);
			timeJSON.put("bestM", timeSet.bestM);

			timesArrayJSON.add(timeJSON);
		}

		userObject.put("times", timesArrayJSON);

		return userObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject userObject = new JSONObject();

		userObject.put("type", "profile");
		userObject.put("action", "data");
		userObject.put("userData", toJSON(this.user));
		userObject.put("edit", this.user.getID() == this.user.getID());
		userObject.put("editRank", this.user.getPower() > this.user.getPower());

		return userObject.toJSONString();
	}

	public User getUser() {
		return user;
	}

	private class TimeSet {
		public String type;
		public long timeN;
		public long countN;
		public long timeM;
		public long countM;

		public long bestN;
		public long bestM;

		public TimeSet(String type) {
			this.type = type;
			bestN = -1;
			bestM = -1;
		}
	}

}
