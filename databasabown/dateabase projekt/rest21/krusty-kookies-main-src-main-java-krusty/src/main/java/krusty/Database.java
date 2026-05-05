
package krusty;

import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

	private static final String JDBC_STRING = "jdbc:hsqldb:file:krustydb;shutdown=true";

	private Connection c;
	private static final Logger logger = LoggerFactory.getLogger(Database.class);

	public void connect() {
		try {
			this.c = DriverManager.getConnection(JDBC_STRING);
			SqlFile sqlFile = new SqlFile(getClass().getResource("/create-schema.sql"), "UTF-8");
			sqlFile.setConnection(this.c);
			sqlFile.execute();
		} catch (SQLException | IOException | SqlToolError e) {
			throw new RuntimeException(e);
		}
	}

	public String getCustomers(Request req, Response res) {
		try (var ps = c.prepareStatement("SELECT * FROM Customers")) {
			return Jsonizer.toJson(ps.executeQuery(), "customers");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getRawMaterials(Request req, Response res) {
		try (var ps = c.prepareStatement("SELECT * FROM RawMaterials")) {
			return Jsonizer.toJson(ps.executeQuery(), "raw-materials");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getCookies(Request req, Response res) {
		try (var ps = c.prepareStatement("SELECT * FROM Cookies")) {
			return Jsonizer.toJson(ps.executeQuery(), "cookies");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getRecipes(Request req, Response res) {
		try (var ps = c.prepareStatement("SELECT * FROM Recipes")) {
			return Jsonizer.toJson(ps.executeQuery(), "recipes");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPallets(Request req, Response res) {
		String sql = """
			SELECT id, cookie, production_date,
			       Orders.customer_name AS customer,
			       RTRIM(CASEWHEN(blocked, 'yes', 'no')) AS blocked
			FROM Pallets
			LEFT OUTER JOIN Orders ON Orders.id = Pallets.order_id
		""";

		ArrayList<Object> values = new ArrayList<>();

		if (req.queryParams("from") != null) {
			sql += " WHERE production_date >= ?";
			values.add(req.queryParams("from"));
		}
		if (req.queryParams("to") != null) {
			sql += (!values.isEmpty() ? " AND " : " WHERE ") + "production_date <= ?";
			values.add(req.queryParams("to"));
		}
		if (req.queryParams("cookie") != null) {
			sql += (!values.isEmpty() ? " AND " : " WHERE ") + "cookie = ?";
			values.add(req.queryParams("cookie"));
		}
		if (req.queryParams("blocked") != null) {
			sql += (!values.isEmpty() ? " AND " : " WHERE ") + "blocked = ?";
			values.add(req.queryParams("blocked").equals("yes"));
		}

		try (var ps = c.prepareStatement(sql)) {
			for (int i = 0; i < values.size(); i++) {
				Object v = values.get(i);
				if (v instanceof String s) ps.setString(i + 1, s);
				else if (v instanceof Boolean b) ps.setBoolean(i + 1, b);
			}
			return Jsonizer.toJson(ps.executeQuery(), "pallets");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String reset(Request req, Response res) {
		try {
			c.setAutoCommit(false);
			SqlFile sqlFile = new SqlFile(getClass().getResource("/reset-schema.sql"), "UTF-8");
			sqlFile.setConnection(this.c);
			sqlFile.execute();
			c.commit();
			return "{\"status\": \"ok\"}";
		} catch (Exception e) {
			try { c.rollback(); } catch (SQLException ignored) {}
			throw new RuntimeException(e);
		} finally {
			try { c.setAutoCommit(true); } catch (SQLException ignored) {}
		}
	}

	public String createPallet(Request req, Response res) {
		String cookie = req.queryParams("cookie");
		String error = "{\"status\": \"error\"}";

		try {
			c.setAutoCommit(false);

			Map<String, Integer> materials = findRawMaterials(cookie);

			// FIX 1: unknown cookie
			if (materials.isEmpty()) {
				res.status(400);
				return "{\"status\": \"unknown cookie\"}";
			}

			// check stock
			for (var e : materials.entrySet()) {
				if (!checkRawMaterials(e.getKey(), e.getValue())) {
					c.rollback();
					res.status(400);
					return error;
				}
			}

			// insert pallet
			String sql = "INSERT INTO Pallets (cookie, production_date, blocked) VALUES (?, LOCALTIMESTAMP, false)";
			var ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, cookie);

			ps.executeUpdate();

			var keys = ps.getGeneratedKeys();
			keys.next();
			int id = keys.getInt(1);

			// update materials
			for (var e : materials.entrySet()) {
				updateRawMaterials(e.getKey(), e.getValue());
			}

			c.commit();

			return String.format("{\"status\": \"ok\", \"id\": %d}", id);

		} catch (Exception e) {
			try { c.rollback(); } catch (SQLException ignored) {}
			res.status(500);
			return error;
		} finally {
			try { c.setAutoCommit(true); } catch (SQLException ignored) {}
		}
	}

	private Map<String, Integer> findRawMaterials(String cookie) throws SQLException {
		Map<String, Integer> map = new HashMap<>();

		String sql = "SELECT raw_material, amount FROM Recipes WHERE cookie = ?";

		try (var ps = c.prepareStatement(sql)) {
			ps.setString(1, cookie);
			var rs = ps.executeQuery();

			while (rs.next()) {
				map.put(rs.getString("raw_material"), rs.getInt("amount"));
			}
		}
		return map;
	}

	private boolean checkRawMaterials(String rawMaterial, int amount) throws SQLException {
		String sql = "SELECT amount FROM RawMaterials WHERE name = ?";

		try (var ps = c.prepareStatement(sql)) {
			ps.setString(1, rawMaterial);
			var rs = ps.executeQuery();

			if (!rs.next()) return false;

			int stock = rs.getInt("amount");

			// FIX 2: allow exact match
			return stock - (54 * amount) >= 0;
		}
	}

	private boolean updateRawMaterials(String rawMaterial, int amount) throws SQLException {
		String sql = "UPDATE RawMaterials SET amount = amount - (54 * ?) WHERE name = ?";

		try (var ps = c.prepareStatement(sql)) {
			ps.setInt(1, amount);
			ps.setString(2, rawMaterial);
			return ps.executeUpdate() == 1;
		}
	}
}

