package de.nerogar.ocs.sql;

import java.sql.*;
import java.util.HashMap;

import de.nerogar.ocs.command.Command;

public class DatabaseCommand extends DatabaseAdapter {

	public DatabaseCommand(Database database) {
		super(database);
	}

	public void loadCommands(HashMap<String, Command> commands) {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT name, minPower FROM " + prefix + "command");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				Command command = commands.get(result.getString("name").toLowerCase());
				if (command != null) command.setMinPower(result.getInt("minPower"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

	public void saveCommand(Command command) {
		PreparedStatement ps = null;
		PreparedStatement psUpdate = null;
		PreparedStatement psInsert = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT COUNT(*) FROM " + prefix + "command WHERE name=?");

			ps.setString(1, command.getName());

			ResultSet result = ps.executeQuery();
			result.next();
			boolean exists = result.getInt(1) == 1;

			if (exists) {
				psUpdate = database.getConnection().prepareStatement("UPDATE " + prefix + "command SET minPower=? WHERE name=?");

				psUpdate.setInt(1, command.getMinPowerReal());
				psUpdate.setString(2, command.getName());

				psUpdate.execute();
			} else {
				psInsert = database.getConnection().prepareStatement("INSERT INTO " + prefix + "command(name, minPower) VALUES(?, ?)");

				psInsert.setString(1, command.getName());
				psInsert.setInt(2, command.getMinPowerReal());

				psInsert.execute();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
			closeOrFail(psUpdate);
			closeOrFail(psInsert);
		}

	}

}
