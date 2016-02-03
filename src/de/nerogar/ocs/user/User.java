package de.nerogar.ocs.user;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import de.nerogar.ocs.*;
import de.nerogar.ocs.chat.*;
import de.nerogar.ocs.parse.ParseMainUser;
import de.nerogar.ocs.party.Party;
import de.nerogar.ocs.sql.DatabasePermission;
import de.nerogar.ocs.tasks.Task;

public class User extends Sendable {

	public static final String MAX_POWER = "maxPower";

	public static final String CREATE_CHAT_ROOM = "createChatRoom";
	public static final String EDIT_ALL_CHAT_ROOMS = "editAllChatRooms";
	public static final String EDIT_ALL_PARTIES = "editAllParties";
	public static final String CREATE_UNLIMITED_PARTIES = "createUnlimitedParties";
	public static final String CREATE_PARTY_WITH_MODE = "createPartyWithMode";
	public static final String CREATE_PARTY = "createParty";
	public static final String SEE_ALL_RANKS = "seeAllRanks";
	public static final String MODERATE_USER = "moderateUser";
	public static final String SPAM_ALLOWED = "spamAllowed";

	public static HashMap<String, Integer> permissions;

	private static DatabasePermission databasePermission;

	public static final String DEF_NAME_COLOR = "000000";

	private int id;
	private Profile profile;
	private String username;
	private long registerDate;
	private int power;
	private String status;
	private String nameColor;
	private long muteTime;
	private String banReason;
	private long onlineTime;
	private int loginCount;
	private int chatMsgCount;

	public OCSFrameHandler connection;
	private boolean disconnected;
	private boolean loggedIn;
	private Task logoutTask;
	private long lastHeartbeat;
	private long loginTime;

	private ParseMainUser dataParser;

	public User(int id, long registerDate, String username, int power, String status, String nameColor, long muteTime, String banReason, long onlineTime, int loginCount, int chatMsgCount) {
		this.id = id;
		this.registerDate = registerDate;
		this.username = username;
		this.power = power;
		this.status = status;
		this.nameColor = nameColor;
		this.muteTime = muteTime;
		this.banReason = banReason;
		this.onlineTime = onlineTime;
		this.loginCount = loginCount;
		this.chatMsgCount = chatMsgCount;

		dataParser = new ParseMainUser(this);
	}

	public boolean isConnected() {
		return (connection != null && !disconnected);
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLogoutTask(Task logoutTask) {
		this.logoutTask = logoutTask;
	}

	public void cancelLogoutTask() {
		OCSServer.scheduler.cancelTask(logoutTask);
		logoutTask = null;
	}

	public void setDisconnectedFrom(OCSFrameHandler connection) {
		if (this.connection == connection) {
			disconnected = true;

			broadcast(OCSServer.userlist);
		}
	}

	public void disconnect() {
		if (!isConnected()) return;

		setDisconnectedFrom(connection);

		connection.ctx.disconnect();
	}

	public void logout(boolean resetLoginToken) {
		if (!isLoggedIn()) return;
		if (resetLoginToken) resetLoginToken();

		disconnect();

		loggedIn = false;

		addOnlineTime(OCSServer.getTimestamp() - loginTime);
		OCSServer.userlist.removeUser(this);
		OCSServer.chatRoomManager.removeUser(this);
		OCSServer.saveOnlineFile(true);

		OCSServer.userlist.broadcast(OCSServer.userlist);
		OCSServer.databaseUser.saveUser(this);
	}

	public void sendRawMessage(String text) {
		if (isConnected()) {
			connection.send(text);
		}
	}

	public void sendMessage(ChatRoom chatRoom, String... text) {
		ChatMessage errorMessage = new ChatMessage(-1, chatRoom, false, text, OCSServer.getTimestamp());
		errorMessage.sendTo(this);
	}

	public void sendMessage(ChatRoom chatRoom, List<String> text) {
		sendMessage(chatRoom, text.toArray(new String[text.size()]));
	}

	public void refreshTimeout() {
		lastHeartbeat = OCSServer.getTimestamp();
	}

	public boolean isTimedOut() {
		if (lastHeartbeat + Config.getValue(Config.TIMEOUT_LIMIT) * OCSServer.get1SecondTimestamp() < OCSServer.getTimestamp()) return true;
		return false;
	}

	public void handleLogin(OCSFrameHandler ocsFrameHandler) {
		//handle connection
		if (isConnected()) {
			new Alert(Alert.WARNING, true, OCSStrings.getString("system.logout.differentLocation")).sendTo(this);
			disconnect();
		}

		boolean wasLoggedIn = isLoggedIn();

		if (wasLoggedIn) {
			cancelLogoutTask();
		}

		connection = ocsFrameHandler;
		disconnected = false;
		loggedIn = true;

		OCSServer.userlist.addUser(this);
		OCSServer.saveOnlineFile(true);

		OCSServer.partyContainer.reactivateUser(this);

		initClient();

		//enter main rooms
		for (ChatRoom chatRoom : OCSServer.chatRoomManager.getAvailableRooms(this)) {
			if (chatRoom.hasUser(this)) {
				chatRoom.addUser(this);
			}
		}

		OCSServer.chatRoomManager.enterChatRoom(this, 1, ""); //do this after entering all rooms to prevent double messages

		//log login
		if (!wasLoggedIn) {
			OCSServer.databaseLog.logLogin(this, OCSServer.getTimestamp());
			addLoginCount();
			loginTime = OCSServer.getTimestamp();
		}
	}

	public void initClient() {
		if (!isConnected()) return;

		//userlist
		OCSServer.userlist.sendTo(this);
		broadcast(OCSServer.userlist);

		//parties
		OCSServer.partyContainer.sendTo(this);

		for (Party party : OCSServer.partyContainer.getParties()) {
			party.sendTo(this);
		}

		//chatrooms
		OCSServer.chatRoomManager.sendTo(this);
	}

	public static void initPermissions() {
		databasePermission = new DatabasePermission(OCSServer.databaseNew);
		permissions = new HashMap<String, Integer>();
		databasePermission.loadPermissions(permissions);
	}

	public boolean hasPermission(String permissionName) {
		if (!permissions.containsKey(permissionName)) return false;
		return (power >= permissions.get(permissionName) || power >= permissions.get(MAX_POWER));
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(User user) {
		JSONObject userObject = new JSONObject();

		userObject.put("id", id);
		userObject.put("username", username);
		userObject.put("nameColor", nameColor);
		userObject.put("rank", Rank.getRankString(power, user.hasPermission(User.SEE_ALL_RANKS)));
		userObject.put("status", status);
		userObject.put("connected", isConnected());

		return userObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject userObject = new JSONObject();

		userObject.put("type", "user");
		userObject.put("action", "data");
		userObject.put("user", toJSON(user));

		return userObject.toJSONString();
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public int getID() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public long getRegisterDate() {
		return registerDate;
	}

	public void setPower(int power) {
		this.power = power;
		OCSServer.databaseUser.saveUser(this);
		initClient();
	}

	public int getPower() {
		return power;
	}

	public void setStatus(String status) {
		this.status = status;
		OCSServer.databaseUser.saveUser(this);
		broadcast(OCSServer.userlist);
	}

	public String getStatus() {
		return status;
	}

	public void setNameColor(String nameColor) {
		this.nameColor = nameColor;
		OCSServer.databaseUser.saveUser(this);
		broadcast(OCSServer.userlist);
	}

	public String getNameColor() {
		return nameColor;
	}

	public void setMuteTime(long muteTime) {
		this.muteTime = muteTime;
		OCSServer.databaseUser.saveUser(this);
		broadcast(OCSServer.userlist);
	}

	/**
	 * @return mute end time
	 */
	public long getMuteTime() {
		if (muteTime != -1 && muteTime < OCSServer.getTimestamp()) muteTime = -1;
		return muteTime;
	}

	public boolean isMutedAndPrevent() {
		long muteTime = getMuteTime() - OCSServer.getTimestamp();

		if (muteTime > 0) {
			new Alert(Alert.WARNING, false, OCSStrings.getString("chat.mute.prevent", Time.asStringDelta(muteTime))).sendTo(this);
			return true;
		}

		return false;
	}

	public void setBanReason(String banReason) {
		this.banReason = banReason;
		OCSServer.databaseUser.saveUser(this);
	}

	public String getBanReason() {
		return banReason;
	}

	public long getOnlineTime() {
		return onlineTime;
	}

	public void addOnlineTime(long time) {
		onlineTime += time;
	}

	public long getLoginCount() {
		return loginCount;
	}

	public void addLoginCount() {
		loginCount++;
	}

	public int getChatMsgCount() {
		return chatMsgCount;
	}

	public void incChatMsgCount() {
		chatMsgCount++;
	}

	public void resetLoginToken() {
		OCSServer.databaseUser.resetUserToken(id);
	}

	public ParseMainUser getDataParser() {
		return dataParser;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User user = (User) o;
			return user.id == id;
		}
		return false;
	}

	public static void savePermissions() {
		databasePermission.savePermission(permissions);
	}

}
