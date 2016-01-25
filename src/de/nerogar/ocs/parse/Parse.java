package de.nerogar.ocs.parse;

import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import de.nerogar.ocs.user.User;

public abstract class Parse {

	protected enum DataType {
		STRING, INTEGER;
	}

	private User user;

	public Parse(User user) {
		this.user = user;
	}

	public abstract void parse(JSONObject jsonData);

	protected boolean validate(JSONObject jsonData, Map<String, DataType> expectedStructure) {
		for (Entry<String, DataType> entry : expectedStructure.entrySet()) {
			if (!jsonData.containsKey(entry.getKey())) return false;

			switch (entry.getValue()) {
				case STRING:
					if (!(jsonData.get(entry.getKey()) instanceof String)) return false;
					break;
				case INTEGER:
					if (!(jsonData.get(entry.getKey()) instanceof Long)) return false;
					break;

			}

		}

		return true;
	}

	public User getUser() {
		return user;
	}
}
