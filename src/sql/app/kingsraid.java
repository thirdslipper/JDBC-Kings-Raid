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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;



public class kingsraid {
	private Connection connection = null;

	public static void main(String args[]) throws SQLException{
		kingsraid obj = new kingsraid();
		obj.ui();
		//obj.getConnection();

		//obj.createItemsAndHeroes();

		//obj.deleteTable();
		//obj.closeConnection();
	}

	public void ui() throws SQLException{
		Scanner kb = new Scanner(System.in);
		getConnection();
		int input = 0;
		while (input != 5){
			System.out.println("ようこそみんあさん"
					+ "\nWhat would you like to do?"
					+ "\n\t1. create tables"
					+ "\n\t2. delete tables"
					+ "\n\t3. query"
					+ "\n\t4. insert"
					+ "\n\t5. exit"
					+ "\n\t6. alter");
			input = kb.nextInt();
			
			switch (input) {
			case 1: createItemsAndHeroes();
					break;
			case 2: //deleteTables();
					break;
			case 3: testQuery(createQueryString());
					break;
			case 4: prepareInsert();
					break;
			case 5: closeConnection();
					break;
			case 6:
					break;
			default: closeConnection();
			}
		}
		kb.close();
		System.exit(0);
	}
	public String createQueryString(){
		Scanner kb = new Scanner(System.in);
		System.out.println("enter query");
		String input = kb.nextLine();
		kb.close();
		return input;
	}

	public void createItemsAndHeroes(){
		//Connection connection = getConnection();
		String stats = "ENUM('atk', 'ats', 'crit', 'critd', 'pen', 'ls', 'pdodge', 'pblock', 'pdef', 'mdodge', 'mblock', 'mdef', 'hp', 'acc', 'cc') NOT NULL, ";
		String charpref = "TINYINT UNSIGNED UNIQUE, ";
		String ref = "REFERENCES kings_raid_items.ITEMS(id) ON DELETE SET NULL ON UPDATE CASCADE, ";
		createTable("create table kings_raid_items.ITEMS ("
				+ "id SMALLINT UNSIGNED UNIQUE AUTO_INCREMENT, "
				+ "Stat_1 " + stats
				+ "Stat_2 " + stats
				+ "Stat_3 " + stats
				+ "Stat_4 " + stats
				+ "Tier TINYINT UNSIGNED NOT NULL DEFAULT 7 CHECK (Tier > 0 AND Tier < 8),"
				+ "Mod_stat ENUM(\"Stat_1\", \"STAT_2\", \"STAT_3\", \"STAT_4\"),"
				+ "PRIMARY KEY (id))");

		createTable("create table kings_raid_items.HEROES ("
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

	public void getConnection(){
		try {	//tests for driver and returns an active connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			//String unicode="useSSL=false&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
			connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/kings_raid_items?autoReconnect=true&amp;useSSL=true", "java", "test");
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to connect.", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public void closeConnection() throws SQLException{
		connection.close();
	}

	public void prepareInsert(){
		try {
			PreparedStatement ps = connection.prepareStatement("insert into kings_raid_items.items(stat_1, stat_2, stat_3, stat_4) values (?,?,?,?)");
			Scanner kb = new Scanner(System.in);
			String []input = new String[4];//= "", input2 = "";
			
			//System.out.println("ex: kings_raid_items.items(stat_1, stat_2, stat_3, stat_4, tier, mod_stat)"
			//		+ "\nEnter db.table to enter into: ");
			//input1 = kb.nextLine();
			//System.out.println("entered: " + input1);
			//ps.setString(1, input1);
			for (int i = 0; i < 4; ++i){
			System.out.println("Enter Values to enter into: "
					+ "\n('v1', 'v2',..,)");
			input[i] = kb.nextLine();
			ps.setString(i+1, input[i]);
			}
			//System.out.println("entered: " + input2);
			
			int result = ps.executeUpdate();
			if (result != 0)
				System.out.println("statment success");
			else {
				System.out.println("fail");
			}
			kb.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void testQuery(String query){
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.createStatement();
			if (stmt.execute(query)) {	//true or false
				//executeQuery(string) returns ResultSet
				rs = stmt.getResultSet();
			}
			else {
				stmt.executeUpdate(query);
				System.out.println("success");
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

	public void createTable(String sql){
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Table created");
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	public void deleteTables(){	//hardcoded delete tables
		try {
			//Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kings_raid_items", "java", "test");
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("drop table kings_raid_items.heroes, kings_raid_items.items;");
			System.out.println("dropped!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Login creates a connection with a database specified in parameters, with the user information also being 
	 * specified in the fields.
	 * @param user	the username to login with.
	 * @param pass	the password to login with.
	 * @param targeturl	the link to the database to connect to.
	 * @return	the connection object representing an object of valid connection to the database or null.
	 * @see java.sql.Connection.getConnection
	 */
	public Connection login(String user, String pass, String targeturl){	//modify this to extend privileges to other functions, unused
		Connection connect = null;
		if (loadDriver()){
			String url = targeturl;
			String username = user;
			String password = pass;

			System.out.println("Attempting to connect...");
			try (Connection testConnection = DriverManager.getConnection(url, username, password)) {
				System.out.println("Connected!");
				//ui(testConnection);		//only works with proper validation and in sequence with testconnection
				return testConnection;
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
				throw new IllegalStateException("Unable to connect.", e);
			}
		}
		return connect;
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
}
