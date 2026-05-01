package krusty;

import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import spark.Request;
import spark.Response;


import static krusty.Jsonizer.toJson;

public class Database {
	private Connection kont;

	private static final String jdbcString = "jdbc:kacker:.db";


	public void connect() {
		try{
			kont = DriverManager.getConnection(jdbcString);

			
		}
		catch(SQLException t){
			t.printStackTrace();
		}
	}

	// TODO: Implement and change output in all methods below!

	public String getCustomers(Request req, Response res) {
		return GetDatta("SELECT name,address FROM customers",kont,"customers");
	}

	public String getRawMaterials(Request req, Response res) {
		return GetDatta("SELECT name,stock_quantity,unit FROM ingredients",kont,"raw-materials");
	}

	public String getCookies(Request req, Response res) {
		return GetDatta("SELECT name FROM products",kont,"cookies");
	}

	public String getRecipes(Request req, Response res) {
		return GetDatta("SELECT cookie,raw_material,amount,unit FROM Recipes",kont,"recipes");
	}

	public String getPallets(Request req, Response res) {
		return "{\"pallets\":[]}";
	}

	public String reset(Request req, Response res) {
		StringBuilder stringBuilder = new StringBuilder();
		try{
			    BufferedReader reader = new BufferedReader(new FileReader ("Reset.schema.sql"));
    			String line = null;
    			

    			
     				while((line = reader.readLine()) != null) {
        		    if(line.charAt(1)!= '-')
						stringBuilder.append(line);
						stringBuilder.append("\n");
		}
		String s =  stringBuilder.toString();
		if(s != ""){
			try (PreparedStatement pstmt = kont.prepareStatement(s)) {
						pstmt.executeUpdate();
					}
		}
		reader.close();
		return "{\"statos\": \"= )\"}";
		

	}catch(Exception a){
		a.printStackTrace();
		return "{\"statos\": \"= (\"}";
	}
	


	}

	public String createPallet(Request req, Response res) {
		return "{}";
	}
	private String GetDatta(String fråga, Connection kont, String Destinaton){
		try (
			PreparedStatement pstmt = kont.prepareStatement(fråga); 
			ResultSet svar = pstmt.executeQuery()
		) {

			String j = Jsonizer.toJson(svar, Destinaton);
			return j;

		} catch (SQLException e) {
			e.printStackTrace();
			return "{\""+Destinaton+"\": []}";
		}
	}
	
}
