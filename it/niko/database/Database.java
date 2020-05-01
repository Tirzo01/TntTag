package it.niko.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	
	private static Database instance = null;
	
	private String url;
	private String username;
	private String password;
	private String tableName;
	private Connection conn;
	
	private Database() {
		this.url = "jdbc:mysql://localhost:3306/HideWars";
		this.username = "admin";
		this.password = "HwHideWarsTiAlek99";
		this.tableName = "HidePlayers";
	}
	
	public static Database getInstance() {
		if(instance == null)
			instance = new Database();
		return instance;
	}
	
	public void createNewHidePlayer(HidePlayer p) {
		try {
			conn = DriverManager.getConnection(url, username , password);
			PreparedStatement prepared = conn.prepareStatement("INSERT INTO "+this.tableName+" (Name, Exp, Coins, Crates, Language) values (?,?,?,?,?)");
			prepared.setString(1, p.getName());
			prepared.setInt(2, p.getExp());
			prepared.setInt(3, p.getCoins());
			prepared.setInt(4, p.getCrates());
			prepared.setString(5, p.getLanguage().name());
			prepared.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateHidePlayer(HidePlayer p) {
		try {
			conn = DriverManager.getConnection(url, username , password);
			PreparedStatement prepared = conn.prepareStatement("UPDATE "+this.tableName+" SET Exp = ?, Coins = ?, Crates = ?, Language = ? WHERE Name = ?");
			prepared.setInt(1, p.getExp());
			prepared.setInt(2, p.getCoins());
			prepared.setInt(3, p.getCrates());
			prepared.setString(4, p.getLanguage().name());
			prepared.setString(5, p.getName());
			prepared.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteHidePlayer(HidePlayer p) {
		try {
			conn = DriverManager.getConnection(url, username , password);
			PreparedStatement prepared = conn.prepareStatement("DELETE FROM "+this.tableName+" WHERE Name = ?");
			prepared.setString(1, p.getName());
			prepared.executeUpdate();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public HidePlayer getHidePlayer(String match) {
		try {
			conn = DriverManager.getConnection(url, username , password);
			PreparedStatement prepared = conn.prepareStatement("SELECT Name,Exp,Coins,Crates,Language FROM "+this.tableName+" WHERE Name = ?");
			prepared.setString(1, match);
			ResultSet rs = prepared.executeQuery();
			while(rs.next()) {
				String name = rs.getString("Name");
				int exp = rs.getInt("Exp");
				int coins = rs.getInt("Coins");
				int crates = rs.getInt("Crates");
				Language language = Language.valueOf(rs.getString("Language"));
				return new HidePlayer(name,exp,coins,crates,language); //Il risultato sarà uno e uno solo.
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	} //SELECT (SQRT((Exp*100)+625)-25)/50,Coins,Crates FROM HidePlayers WHERE NAME = 'Wanda_Ox';
}
