package de.nerogar.ocs.sql;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;

public class DatabaseConfig extends DatabaseAdapter {

	public DatabaseConfig(Database database) {
		super(database);
	}

	public void loadConfig(HashMap<String, Integer> configs) {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "config");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String name = result.getString("name");
				int value = result.getInt("value");

				configs.put(name, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

	public void saveConfig(HashMap<String, Integer> configs) {
		Collection<String> names = configs.keySet();
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("UPDATE " + prefix + "config SET value = ? WHERE name = ?");
			for (String name : names) {
				ps.setInt(1, configs.get(name));
				ps.setString(2, name);

				ps.addBatch();

			}

			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

}
