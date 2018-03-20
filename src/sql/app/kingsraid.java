/**
 * Author: Colin Koo
 * Date: 3/19/2018
 * Description: This project is to practice creating a SQL database managed using java, MySQL, and JDBC for a mobile game.
 * notes: <p>, {@link x}, @see
 * Database name : kings_raid_items, port 3306
 * CREATE USER 'java'@'localhost' IDENTIFIED BY 'test';
 * GRANT ALL ON kings_raid_items.* TO 'java'@'localhost' IDENTIFIED BY 'test' 
 */
package sql.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class kingsraid {

	
	public static void main(String args[]){
		kingsraid obj = new kingsraid();
		Connection connection = null;
		
		connection = obj.login("java", "test", "jdbc:mysql://localhost:3306/kings_raid_items");
		//obj.ui(connection);
	}
	
	public void ui(Connection connection){
		String stats = "ENUM('atk', 'ats', 'crit', 'critd', 'pen', 'ls', 'pdodge', 'pblock', 'pdef', 'mdodge', 'mblock', 'mdef', 'hp', 'acc', 'cc') NOT NULL, ";
		String charpref = "TINYINT UNSIGNED UNIQUE, ";
		String ref = "REFERENCES kings_raid_items.ITEMS(id) ON DELETE SET NULL ON UPDATE CASCADE, ";
		
		createTable(connection, "create table kings_raid_items.ITEMS ("
				+ "id SMALLINT UNSIGNED UNIQUE AUTO_INCREMENT NOT NULL, "
				+ "Stat_1 " + stats
				+ "Stat_2 " + stats
				+ "Stat_3 " + stats
				+ "Stat_4 " + stats
				+ "Tier TINYINT UNSIGNED NOT NULL DEFAULT 7 CHECK (Tier > 0 AND Tier < 8),"
				+ "Mod_stat ENUM(\"Stat_1\", \"STAT_2\", \"STAT_3\", \"STAT_4\"),"
				+ "PRIMARY KEY (id))");
		
		createTable(connection, "create table kings_raid_items.HEROES ("
				+ "name VARCHAR(15) NOT NULL UNIQUE,"
				+ "armor_pref " + charpref
				+ "sec_gear_pref " + charpref
				+ "accessory_pref " + charpref
				+ "orb_pref " + charpref
				+ "FOREIGN KEY (armor_pref) " + ref
				+ "FOREIGN KEY (sec_gear_pref) " + ref
				+ "FOREIGN KEY (accessory_pref) " + ref
				+ "FOREIGN KEY (orb_pref) " + ref
				+ "PRIMARY KEY (name))");
	}
	
	/**
	 * Login creates a connection with a database specified in parameters, with the user information also being 
	 * specified inthe fields.
	 * @param user	the username to login with.
	 * @param pass	the password to login with.
	 * @param targeturl	the link to the database to connect to.
	 * @return	the connection object representing an object of valid connection to the database or null.
	 * @see java.sql.Connection.getConnection
	 */
	public Connection login(String user, String pass, String targeturl){	//modify this to extend privileges to other functions
		Connection connect = null;
		if (loadDriver()){
			String url = targeturl;
			String username = user;
			String password = pass;

			System.out.println("Attempting to connect...");
			try (Connection testConnection = DriverManager.getConnection(url, username, password)) {
				System.out.println("Connected!");
				connect = testConnection;
				ui(connect);
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				throw new IllegalStateException("Unable to connect.", e);
			}
		}
		return connect;
	}
	
	// to test
	public Connection getConnection(String user, String pass, String targeturl){
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String unicode="useSSL=false&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
			return DriverManager.getConnection(targeturl, user, pass);
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to connect.", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Test if the proper files are in place for java to perform SQL management.
	 * @return boolean value depending if the project has the proper files.
	 */
	public boolean loadDriver(){
		boolean result = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded!");
			result = true;
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find the driver in the classpath!", e);
		}
		return result;
	}
	
	public void testQuery(Connection connection, String query){
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.createStatement();
			if (stmt.execute(query)) {	//true or false
					//executeQuery(string) returns ResultSet
				rs = stmt.getResultSet();
			}
			//do something with rs
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to connect.", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) { }
				stmt = null;
			}
		}
	}
	public void createTable(Connection connection, String sql){
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
