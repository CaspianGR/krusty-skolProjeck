package krusty;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import org.checkerframework.checker.units.qual.N;

import spark.Request;
import spark.Response;

public class Database {
	private Connection kont;

	private static final String jdbcString = "jdbc:sqlite:kacker.db";


	public void connect() {
		try{
			kont = DriverManager.getConnection(jdbcString);
			
			StringBuilder stringBuilder = new StringBuilder();
			
			BufferedReader reader = new BufferedReader(new FileReader ("create.schema.sql"));
			String line = null;
			

			
			while((line = reader.readLine()) != null) {
				if(line.length()>0 && line.charAt(1)!= '-')
					stringBuilder.append(line);
				stringBuilder.append("\n");
			}
				String s =  stringBuilder.toString();
				String[] sL = s.split(";");
				for(int i =0; i< sL.length; i++){
					if(sL[i] != null && sL[i].length() >2){
					try (Statement pstmt = kont.createStatement()) {
								pstmt.execute(sL[i]+";");
							}
						if(i == 16){
							int ewf=1;						}
				}
				}


				
			reset(null, null);
			reader.close();
		} catch(Exception t ){
			t.printStackTrace();

		}
	}

	// TODO: Implement and change output in all methods below!

	public String getCustomers(Request req, Response res) {
		
		return GetDatta("SELECT name,address FROM Customers",kont,"customers");
	}

	public String getRawMaterials(Request req, Response res) {
		return GetDatta("SELECT name,amount,unit FROM RawMaterials",kont,"raw-materials");
	}

	public String getCookies(Request req, Response res) {
		return GetDatta("SELECT name FROM Cookies",kont,"cookies");
	}

	public String getRecipes(Request req, Response res) {
		return GetDatta("SELECT cookie,raw_material,amount,unit FROM Recipes",kont,"recipes");
	}

	public String getPallets(Request req, Response res) {
		String fråga ="SELECT id, cookie, production_date, Orders.customer_name AS customer, RTRIM(CASEWHEN(blocked, 'yes', 'no')) AS blocked FROM Pallets LEFT OUTER JOIN Orders ON Orders.id=Pallets.order_id";
		ArrayList<String> values = new ArrayList<String>();
		boolean f = true;

		if (req.queryParams("from") != null) {
			fråga += f ? " WHERE " : " AND ";
			fråga += "WHERE production_date >= ?";
			values.add(req.queryParams("to"));
			f = false;
		}
		if (req.queryParams("to") != null) {
			fråga += f ? " WHERE " : " AND ";
			fråga += "WHERE production_date <= ?";
			values.add(req.queryParams("to"));
			f = false;
		}
		if (req.queryParams("cookie") != null) {
			fråga += f ? " WHERE " : " AND ";
			fråga += "cookie = ?";
			values.add(req.queryParams("to"));
			f = false;
		}
		if (req.queryParams("to") != null) {
			fråga += f ? " WHERE " : " AND ";
			if (req.queryParams("blocked").equals("yes")){
				fråga += "blocked = yes";
			}
			else{
				fråga += "blocked = no";
			}
		}
		try (PreparedStatement stmt = kont.prepareStatement(fråga)) { 
    		for (int i = 0; i < values.size(); i++) { 
      			stmt.setString(i+1, values.get(i)); 
    		} 
    		ResultSet R = stmt.executeQuery(); 
			return Jsonizer.toJson(R, "pallets");
  		} catch (SQLException e) { 
			e.printStackTrace();
			return "{\"pallets\": []}";
		} 




	}

	public String reset(Request req, Response res) {
		System.out.println("safjeashuif");
		StringBuilder stringBuilder = new StringBuilder();
		try{
			    BufferedReader reader = new BufferedReader(new FileReader ("Reset.schema.sql"));
    			String line = null;
    			

    			
     				while((line = reader.readLine()) != null) {
        		    if(line.length()>0 && line.charAt(1)!= '-'){
						stringBuilder.append(line);
						stringBuilder.append("\n");}
		}
		String s =  stringBuilder.toString();
		if(s != ""){
			String[]sT = s.split("\n");
			for (String sTs: sT) {
				if(sTs.length() >2){
					String sadf= sTs + "";
					try (PreparedStatement pstmt = kont.prepareStatement(sadf)) {
							pstmt.executeUpdate();
						}
				}
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
		
//den här koden är skräåj jag orkar inte att fixa den

		String lägTillPall = "INSERT INTO Pallets (id,cookie, production_date, order_id,delivered_at,blocked) VALUES (?,?, LOCALTIMESTAMP,?,LOCALTIMESTAMP, ?)";
		String ValdKacka = req.queryParams("cookie");
		int paletId;
		if(ValdKacka == null){
			return "{\"status\": \"error\"}";
		}

		try{
			kont.setAutoCommit(false);
			String SeOmKakaFins = "SELECT name FROM Cookies WHERE name = ?";
			try(PreparedStatement pstmt = kont.prepareStatement(SeOmKakaFins)){
				pstmt.setString(1, ValdKacka);
				ResultSet r = pstmt.executeQuery();
				if (!r.next()) {
					kont.setAutoCommit(true);
					return "{\"status\": \"unknown cookie\"}";
				}
			}
			try(PreparedStatement sKapaPaler = kont.prepareStatement(lägTillPall,java.sql.Statement.RETURN_GENERATED_KEYS)){
				Random r= new Random();
				
				String[]Alternativ = {Integer.toString(0),ValdKacka,Integer.toString(r.nextInt(1000)),"false"};
				for(int i =0; i< Alternativ.length; i++){
					sKapaPaler.setString(i++, Alternativ[i]);
				}
				sKapaPaler.executeUpdate();
				ResultSet Nyklar = sKapaPaler.getGeneratedKeys();
				Nyklar.next();
				paletId = Nyklar.getInt(1);
			}




			kont.commit();
			kont.setAutoCommit(true);
			return "{\"status\": \"ok\", \"id\": " + paletId + "}";

		}
		catch (SQLException e) {
			e.printStackTrace();
			try {
				kont.rollback();
				kont.setAutoCommit(true); 
			} 
			catch (SQLException FellFell) {
				FellFell.printStackTrace();
			}
			return "{\"status\": \"error\"}";
		}




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
