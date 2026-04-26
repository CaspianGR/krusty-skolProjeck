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
		String sql = "INSERT INTO Pallets (cookie, production_date, blocked) VALUES (?, LOCALTIMESTAMP, ?);";
		String otherError = "{\"status\": \"error\"}";

		try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			c.setAutoCommit(false);
			Map<String, Integer> map = findRawMaterials(req.queryParams("cookie"));
			for (Map.Entry<String, Integer> e : map.entrySet()) {
				if (!checkRawMaterials(e.getKey(), e.getValue())) {
					// No need to rollback because we have not mutated anything yet
					res.status(400);
					return otherError;
				}
			}

			ps.setString(1, req.queryParams("cookie"));
			ps.setBoolean(2, false);

			if (ps.executeUpdate() != 1) {
				c.rollback();
				logger.atError()
					.addKeyValue("Request: ", req)
					.addKeyValue("Response: ", res)
					.log("Wrong number of rows affected by insertion, rolling back.");
				res.status(400);
				return """
					{"status": "unknown cookie"}""";
			}
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if (!generatedKeys.next()) {
				throw new IllegalStateException("Generated keys were not returned");
			}

			for (Map.Entry<String, Integer> e : map.entrySet()) {
				boolean updateRawMaterials = updateRawMaterials(e.getKey(), e.getValue());
				if (!updateRawMaterials) {
					// Need to rollback because the same connection object is used so the
					// statements executed so far must be discarded even if they were not commited.
					c.rollback();
					res.status(400);
					return otherError;
				}
			}
			c.commit();
			return """
				{
					"status": "ok",
					"id": %d
				}""".formatted(generatedKeys.getInt(1));
		} catch (Exception e) {
			try {
				c.rollback();
			} catch (SQLException e2) {
				// Let it pass since we want to return the same error message.
			}
			logger.error("Could not create pallet", e);
			res.status(500);
			return otherError;
		} finally {
			try {
				c.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error("Could not set auto commit status: {}", e.getMessage());
			}
		}
	}

	/**
	 * Tries to find all materials needed in creating a certain cookie.
	 *
	 * @param cookie the cookie for which constituent materials are wanted.
	 * @return a <code>java.util.Map</code> containing key/value pairs of <code>String</code> raw material names, and
	 * <code>Integer</code> amounts.
	 * @throws SQLException if SQL query could not be run, <br> i.e. either an error in the query itself, or if the
	 *					  <code>cookie</code> could not be found in the DB.
	 */
	private Map<String, Integer> findRawMaterials(String cookie) throws SQLException {
		Map<String, Integer> rawMaterialsAndAmounts = new HashMap<>();

		String sql = "SELECT raw_material, amount FROM Recipes WHERE cookie = ?";

		try (var ps = c.prepareStatement(sql)) {
			ps.setString(1, cookie);

			var rs = ps.executeQuery();

			while (rs.next()) {
				rawMaterialsAndAmounts.put(rs.getString("raw_material"), rs.getInt("amount"));
			}

		} catch (SQLException e) {
			logger.atError()
				.addKeyValue("cookie", cookie)
				.setCause(e)
				.log("SQL query: {} could not be run", sql);
			throw e;
		}

		return rawMaterialsAndAmounts;
	}

	/**
	 * Tries to update the amount of raw material in stock.
	 *
	 * @param rawMaterial the raw material to update.
	 * @param amount	  the amount of <code>raw material</code>
	 * @return <code>true</code> if update was successful.
	 * @throws SQLException if too many rows were affected or SQL query could not run.
	 * @implNote like the {@link #checkRawMaterials(String, int) Check} method, multiplies <code>amount</code>.
	 */
	private boolean updateRawMaterials(String rawMaterial, int amount) throws SQLException {

		if (!checkRawMaterials(rawMaterial, amount)) return false;

		String sql = "UPDATE RawMaterials SET amount = amount - (54 * ?) WHERE name = ?";
		try (PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, amount);
			ps.setString(2, rawMaterial);
			return ps.executeUpdate() == 1;
		}
	}


	/**
	 * Checks whether there are enough raw materials in stock to produce a certain cookie.
	 *
	 * @param rawMaterial the raw material to check.
	 * @param amount	  the amount of <code>rawMaterial</code> needed for a batch of 100 cookies.
	 * @return <code>true</code> if there are enough materials.
	 * @throws SQLException			 if rawMaterial or amount are not
	 * @throws NullPointerException	 if <code>rawMaterial</code> is null.
	 * @throws IllegalArgumentException if <code>amount</code> is negative.
	 * @implNote <code>amount</code> is multiplied by 54 due to a pallet containing 5400 cookies.
	 */
	private boolean checkRawMaterials(String rawMaterial, int amount) throws SQLException {
		if (rawMaterial == null) {
			throw new NullPointerException("rawMaterial is null.");
		}
		if (amount < 0) {
			throw new IllegalArgumentException("amount is negative.");
		}

		boolean rawMaterialEnough = false;
		String sql = "SELECT CASEWHEN(amount - (54 * ?) > 0, true, false) AS is_enough FROM RawMaterials WHERE name = ?";

		try (PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, amount);
			ps.setString(2, rawMaterial);

			ResultSet rs = ps.executeQuery();

			try {
				rs.next();
				rawMaterialEnough = rs.getBoolean("is_enough");
			} catch (SQLException e) {
				logger.error("Cursor moved beyond last row of column.", e);
			}

		} catch (SQLException e) {
			logger.atError()
				.addKeyValue("Raw Material: ", rawMaterial)
				.addKeyValue("Amount: ", amount)
				.log("SQL query: {} could not be run", sql);
		}
		return rawMaterialEnough;
	}
}
