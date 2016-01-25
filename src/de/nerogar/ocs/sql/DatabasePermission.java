package de.nerogar.ocs.sql;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;

public class DatabasePermission extends DatabaseAdapter {

	public DatabasePermission(Database database) {
		super(database);
	}

	public void loadPermissions(HashMap<String, Integer> permissions) {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "permission");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String name = result.getString("name");
				int minPower = result.getInt("minPower");

				permissions.put(name, minPower);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

	public void savePermission(HashMap<String, Integer> permissions) {
		Collection<String> names = permissions.keySet();
		PreparedStatement ps = null;
		
		try {
			ps = database.getConnection().prepareStatement("UPDATE " + prefix + "permission SET minPower = ? WHERE name = ?");
			for (String name : names) {
				ps.setInt(1, permissions.get(name));
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
