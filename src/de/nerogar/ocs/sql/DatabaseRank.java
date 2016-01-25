package de.nerogar.ocs.sql;

import java.sql.*;
import java.util.ArrayList;

import de.nerogar.ocs.Rank;

public class DatabaseRank extends DatabaseAdapter {

	public DatabaseRank(Database database) {
		super(database);
	}

	public void loadRanks(ArrayList<Rank> ranks) {
		PreparedStatement ps = null;

		try {
			ps = database.getConnection().prepareStatement("SELECT * FROM " + prefix + "rank ORDER BY power ASC");

			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String name = result.getString("name");
				int power = result.getInt("power");
				boolean show = result.getInt("show") == 1;
				String shortName = result.getString("short");

				ranks.add(new Rank(name, power, show, shortName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeOrFail(ps);
		}
	}

}
