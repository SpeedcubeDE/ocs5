package de.nerogar.ocs;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.nerogar.ocs.user.User;

public class Userlist extends Sendable {
	private HashMap<Integer, User> users;

	public Userlist() {
		users = new HashMap<Integer, User>();
	}

	public void addUser(User user) {
		users.put(user.getID(), user);
	}

	public void removeUser(User user) {
		users.remove(user.getID());
	}

	public User getUser(String username) {
		for (User user : getUsers()) {
			if (user.getUsername().equalsIgnoreCase(username)) return user;
		}
		return null;
	}

	public User getUser(int userID) {
		return users.get(userID);
	}

	public List<User> getUsers() {
		List<User> userList = new ArrayList<User>();
		for (User user : users.values()) {
			userList.add(user);
		}

		return userList;
	}

	public User getRandomUser() {
		if (users.isEmpty()) return null;
		int index = (int) (Math.random() * users.size());
		return getUsers().get(index);
	}

	public boolean hasUser(User user) {
		return users.containsValue(user);
	}

	public boolean hasUser(int userID) {
		return users.containsKey(userID);
	}

	public boolean flushUsers() {
		boolean updated = false;
		List<User> userlist = getUsers();
		for (User user : userlist) {
			if (user.isTimedOut()) {
				updated = true;
				users.remove(user.getID());
				user.disconnect();
				user.logout(true);
			}
		}
		return updated;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String send(User user) {
		JSONObject userlistJSON = new JSONObject();

		List<User> users = getUsers();

		JSONArray usersArray = new JSONArray();
		for (User listUser : users) {
			if (listUser.isLoggedIn()) {
				usersArray.add(listUser.toJSON(user));
			}
		}
		userlistJSON.put("users", usersArray);

		userlistJSON.put("type", "userlist");

		return userlistJSON.toJSONString();
	}

	public int userCount() {
		return users.size();
	}

	public void clear() {
		users.clear();
	}

	public void saveAll() {
		List<User> users = getUsers();
		for (User user : users) {
			OCSServer.databaseUser.saveUser(user);
		}
	}

}
