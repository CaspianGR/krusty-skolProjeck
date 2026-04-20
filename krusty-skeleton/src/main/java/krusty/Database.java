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
			return "{}";
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
			return "{}";
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
			return "{}";
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
			return "{}";
		}
	}

	public String getPallets(Request req, Response res) {
		Stirng förfrågan = 
		String sql = "SELECT id, cookie, production_date, customer, blocked FROM Pallets";
		int t = 0;
		
		String from = "";
		String to = "";
		String cookie = "";
		String blocked = "";
		
		if (req.queryParams("from") != null) { 
    	from = req.queryParams("from"); 
		t += 1;
		}
		if (req.queryParams("to") != null) { 
    	to = req.queryParams("to"); 
		t += 1;
		}
		if (req.queryParams("cookie") != null) { 
    	 cookie = req.queryParams("cookie"); 
		t += 1;
		}
		if (req.queryParams("blocked") != null) { 
    	 blocked = req.queryParams("blocked"); 
		t += 1;
		}
		if(t != 1){
			sql += " where ";
			if(from != ""){
				sql += "production_date > " from;
				t--;
				if(t != 0){
					sql +=" and "
				}

			}
			if(to != "" && t != 0){
				sql += "production_date < " from;
				t--;
				if(t != 0){
					sql +=" and "
				}

			}
			if(cookie != "" && t != 0){
				sql += "cookie = " cookie;
				t--;
				if(t != 0){
					sql +=" and "
				}

			}
			if(blocked != "" && t != 0){
				if(blocked == "yes"){
					sql += "blocked = true";
				}
				else if(blocked == "no"){
					sql += "blocked = false";
				}
				
				

			}

		}
		




		return "{\"pallets\":[]}";
	}

	public String reset(Request req, Response res) {
		return "{}";
	}

	public String createPallet(Request req, Response res) {
		return "{}";
	}
}
