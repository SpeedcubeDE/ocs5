package de.nerogar.ocs.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.nerogar.ocs.Logger;

public abstract class DatabaseAdapter {

	protected Database database;
	protected String prefix;

	public DatabaseAdapter(Database database) {
		this.database = database;
		prefix = database.getPrefix();
	}

	protected void closeOrFail(PreparedStatement ps) {
		try {
			if (ps != null) ps.close();
		} catch (SQLException e) {
			e.printStackTrace(Logger.getErrorStream());
		}
	}

}
