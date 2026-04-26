package krusty;

import spark.Request;
import spark.Response;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.checkerframework.checker.units.qual.s;

import static krusty.Jsonizer.toJson;

public class Database {
	/**
	 * Modify it to fit your environment and then use this string when connecting to your database!
	 */
	private static final String jdbcString = "jdbc:mysql://localhost/krusty";

	// For use with MySQL or PostgreSQL
	private static final String jdbcUsername = "<CHANGE ME>";
	private static final String jdbcPassword = "<CHANGE ME>";
	private Connection conn;




	public void connect() {
		try {
			// Connection strings for included DBMS clients:
			// [MySQL] jdbc:mysql://[host]/[database]
			// [PostgreSQL] jdbc:postgresql://[host]/[database]
			// [SQLite] jdbc:sqlite://[filepath]

			// Use "jdbc:mysql://puccini.cs.lth.se/" + userName if you using our shared
			// server
			// If outside, this statement will hang until timeout.
			conn = DriverManager.getConnection("jdbc:sqlite:L2_lokal_DB.db", jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			System.err.println(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// TODO: Implement and change output in all methods below!

	public String getCustomers(Request req, Response res) {
		String sql = "SELECT name,address FROM customers";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				String json = JSONizer.toJSON(ResultSet, "customers"); 
    			return json; 
				
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}




		
	}

	public String getRawMaterials(Request req, Response res) {
		String sql = "SELECT name,stock_quantity,unit FROM ingredients";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				String json = JSONizer.toJSON(ResultSet, "customers"); 
    			return json; 
				
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getCookies(Request req, Response res) {
		String sql = "SELECT name FROM products";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				String json = JSONizer.toJSON(ResultSet, "customers"); 
    			return json; 
				
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getRecipes(Request req, Response res) {
		String sql = "SELECT cookie,raw_material,amount,unit FROM Recipes";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				String json = JSONizer.toJSON(ResultSet, "customers"); 
    			return json; 
				
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPallets(Request req, Response res) {
		String sql = """
			SELECT id, cookie, production_date, Orders.customer_name AS customer, RTRIM(CASEWHEN(blocked, 'yes', 'no')) AS blocked FROM Pallets
			LEFT OUTER JOIN Orders ON Orders.id=Pallets.order_id""";

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
			values.add(switch (req.queryParams("blocked")) {
				case "yes" -> true;
				case "no" -> false;
				default -> throw new IllegalArgumentException("blocked=" + req.queryParams("blocked"));
			});
		}

		try (var ps = c.prepareStatement(sql)) {
			for (int i = 0; i < values.size(); i++) {
				Object v = values.get(i);
				if (v instanceof String s) {
					ps.setString(i + 1, s);
				} else if (v instanceof Boolean b) {
					ps.setBoolean(i + 1, b);
				}
			}
			return Jsonizer.toJson(ps.executeQuery(), "pallets");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
		




		return "{\"pallets\":[]}";
	}

	public String reset(Request req, Response res) {
		try {
			c.setAutoCommit(false);
			SqlFile sqlFile = new SqlFile(getClass().getResource("/reset-schema.sql"), "UTF-8");
			sqlFile.setConnection(this.c);
			sqlFile.execute();
			c.commit();
			return """
					{"status": "ok"}""";
		} catch (IOException | SQLException | SqlToolError e) {
			try {
				c.rollback();
			} catch (SQLException rollbackEx) {
				throw new RuntimeException("Rollback failed: " + rollbackEx.getMessage());
			}
			throw new RuntimeException("Reset failed: " + e.getMessage());
		} finally {
			try {
				c.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error("Could not set auto commit status: {}", e.getMessage());
			}
		}
	}

	public String createPallet(Request req, Response res) {
		return "{}";
	}
}
