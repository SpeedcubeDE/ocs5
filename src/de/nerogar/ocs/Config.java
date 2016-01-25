package de.nerogar.ocs;

import java.util.HashMap;

import de.nerogar.ocs.sql.DatabaseConfig;

public class Config {

	public static final String LOGIN_MSG_LIMIT = "loginMsgLimit";
	public static final String MAX_PARTY_ROUNDS = "maxPartyRounds";
	public static final String MAX_CREATED_PARTIES = "maxCreatedParties";
	public static final String TIMEOUT_LIMIT = "timeoutLimit";
	public static final String MAX_USER_POOL_SIZE = "maxUserPoolSize";
	public static final String MAX_STATUS_LENGTH = "maxStatusLength";
	public static final String MIN_CHAT_MSG_DELAY = "minChatMsgDelay";
	public static final String LOGOUT_DELAY = "logoutDelay";

	public static HashMap<String, Integer> configs;

	private static DatabaseConfig databaseConfig;

	public static int getValue(String name) {
		Integer value = configs.get(name);
		return value == null ? 0 : value;
	}

	public static boolean setValue(String name, int value) {
		if (configs.containsKey(name)) {
			configs.put(name, value);
			return true;
		}
		return false;
	}

	public static void saveAll() {
		databaseConfig.saveConfig(configs);
	}

	public static void init() {
		databaseConfig = new DatabaseConfig(OCSServer.databaseNew);

		configs = new HashMap<String, Integer>();
		databaseConfig.loadConfig(configs);
	}

}
