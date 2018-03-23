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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class kingsraid {
	private Connection connection = null;

	public static void main(String args[]) throws SQLException{
		kingsraid obj = new kingsraid();
		obj.ui();
		
	}

	public void ui(){
		Scanner kb = new Scanner(System.in);
		getConnection();
		int input = 0;
		try {
			while (input != 5){
				System.out.println("ようこそみんあさん"
						+ "\nWhat would you like to do?"
						+ "\n\t1. create table"
						+ "\n\t2. delete table"
						+ "\n\t3. query"
						+ "\n\t4. insert"
						+ "\n\t5. exit");
				input = kb.nextInt();
				kb.nextLine();
				switch (input) {
				case 1: createItemsAndHeroes(kb);
						break;
				case 2: deleteTables(kb);
						break;
				case 3: createQuery(createQueryString(kb));
						break;
				case 4: insert(kb);
						break;
				case 5: closeConnection();
						break;
				default: closeConnection();
						break;
				}
				System.out.println();
			}
		} catch (SQLException e){
			System.out.println("msg:" + e.getMessage());
		}
		kb.close();
		System.exit(0);
	}
	public String createQueryString(Scanner kb){
		//kb.nextLine();
		System.out.println("enter query");
		String input = kb.nextLine();
		return input;
	}
	/**
	 * This method creates a predetermined table in the database corresponding to either heroes or items.
	 * @param kb Scanner object for user I/O.
	 */
	public void createItemsAndHeroes(Scanner kb){
		//kb.nextLine();
		String input;
		try {
			Statement stmt = connection.createStatement();
			System.out.println("Enter table name to create: (heroes or items)");
			input = kb.nextLine().toLowerCase().trim();
			//check doesn't work, 
			if (input.equals("items")){
				String statsList = "('atk', 'ats', 'crit', 'critd', 'pen', 'ls', 'pdodge', 'pblock', 'pdef', 'mdodge', 'mblock', 'mdef', 'hp', 'acc', 'cc')";
				String stats = "ENUM " + statsList + " NOT NULL, ";
				stmt.executeUpdate("create table kings_raid_items.ITEMS ("
						+ "id SMALLINT UNSIGNED UNIQUE AUTO_INCREMENT, "
						+ "Stat_1 " + stats + "CHECK (Stat_1 in " + statsList + "), "
						+ "Stat_2 " + stats + "CHECK (Stat_2 in " + statsList + "), "
						+ "Stat_3 " + stats + "CHECK (Stat_3 in " + statsList + "), "
						+ "Stat_4 " + stats + "CHECK (Stat_4 in " + statsList + "), "
						+ "Tier TINYINT UNSIGNED DEFAULT 7, "
						+ "CHECK (Tier>0 AND Tier<8),"
						+ "item_class ENUM(\"\")NOT NULL, "
						+ "item_location ENUM(\"\") NOT NULL,"
						+ "Mod_stat ENUM(\"Stat_1\", \"STAT_2\", \"STAT_3\", \"STAT_4\", \"NULL\"),"
						+ "PRIMARY KEY (id))");
			}
			else if (input.equals("heroes")){
				String charpref = "TINYINT UNSIGNED UNIQUE, ";
				String ref = "REFERENCES kings_raid_items.ITEMS(id) ON DELETE SET NULL ON UPDATE CASCADE, ";
				stmt.executeUpdate("create table kings_raid_items.HEROES ("
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
			System.out.println("Table " + input + " successfully created!");
		} catch (SQLException e){
			System.out.println("error! " + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
	}


	public void insert(Scanner kb){
		try {
			//kb.nextLine();
			PreparedStatement ps = connection.prepareStatement("insert into kings_raid_items.items"
					+ "(stat_1, stat_2, stat_3, stat_4, tier, mod_stat) values (?, ?, ?, ?, ?, ?)");
			//Scanner kb = new Scanner(System.in);
			String input;
			String[] inputBlock;

			System.out.println("Enter Values to enter into: (stat_1, stat_2, stat_3, stat_4, tier, mod_stat)"
					+ "\nv1, v2,..,vn");
			input = kb.nextLine();
			inputBlock = input.split(", ");

			System.out.println("entered: " + Arrays.toString(inputBlock));
			int i = 1;
			for (String s : inputBlock){
				ps.setString(i++, s);
			}

			int result = ps.executeUpdate();
			if (result != 0){
				System.out.println("insert success!");
			}
			else {
				System.out.println("insert failed!");
			}
		} catch (SQLException e) {
			System.out.println("error!" + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
	}

	public void createQuery(String query){
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.createStatement();
			if (stmt.execute(query)) {	//true if rs obj or false if no update/no result, rs stored in stmt; executeQuery(string) returns ResultSet
				rs = stmt.getResultSet();
				//assume not using *
					//start indexes of certain keywords
				int start = query.indexOf("select ") + 7;	//should be 0
				int end = query.indexOf(" from ");	//index of first space before from
				
				String[] selectedTerms = query.substring(start, end).split(", ");	//contains terms, assuming not *
				if (selectedTerms.length > 0 && selectedTerms[0].equals("*")){	//if asterisk
					int from = end + 6;
					String fromTable = query.substring(from, query.length());
					StringBuilder newQuery = new StringBuilder("select column_name from information_schema.columns where table_name = '");
					newQuery.append(fromTable + "' and table_schema = 'kings_raid_items'");	//table name and db
					
					ResultSet columns = stmt.executeQuery(newQuery.toString());	// list of all column names
					ArrayList<String> columnNames = new ArrayList<String>();	//hold the string representation of column names of variable amount
					
					int j = 0;
					while (columns.next()){
						//System.out.printf("%-8s ", columns.getString(1));
						columnNames.add(j++, columns.getString(1));
					}
					StringBuilder asterisk = new StringBuilder("select ");
					for (int i = 0; i < columnNames.size(); ++i){
						asterisk.append(columnNames.get(i));
						if (i < columnNames.size()-1)
							asterisk.append(", ");
						else
							asterisk.append(" ");
					}
					asterisk.append("from " + fromTable);
					//System.out.println("\ntesting : " + asterisk.toString());
					
					createQuery(asterisk.toString());
				} else {
					for (String s:selectedTerms){
						System.out.printf("%-15s", s);
					}
					System.out.println();
					while (rs.next()){
						for (int i = 0; i < selectedTerms.length; ++i){
							System.out.printf("%-15s", rs.getString(selectedTerms[i]));
							//System.out.print(selectedTerms[i] + ": " + rs.getString(selectedTerms[i]) + ", ");
						}
						System.out.println();
					}
				}
			}
			else {
				stmt.executeUpdate(query);
				System.out.println("no rs");
			}
			//do something with rs
		} catch (SQLException e) {
			System.out.println("error! " + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		} finally {
			closeRSSTMT(rs, stmt);
		}
	}
	public void displayTables(){
		try {
			Statement stmt = connection.createStatement();
			stmt.executeQuery("SHOW TABLES FROM kings_raid_items");
			//rs.
		} catch (SQLException e) {
			System.out.println("error! " + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
		
	}

	public void createTable(String sql){
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Table created");
		} catch (SQLException e) {
			System.out.println("error! " + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
	}
	public void deleteTables(Scanner kb){
		try {
			//kb.nextLine();
			String tableToDrop;
			StringBuilder input = new StringBuilder("drop table kings_raid_items.");
			Statement stmt = connection.createStatement();
			System.out.println("Enter table to drop: ex. heroes");
			tableToDrop = kb.nextLine();
			input.append(tableToDrop);
			stmt.executeUpdate(input.toString());
			System.out.println("Table " + tableToDrop + " successfully dropped!");
		} catch (SQLException e) {
			System.out.println("error!" + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
	}
	public void getConnection(){
		try {	//tests for driver and returns an active connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			//String unicode="useSSL=false&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
			connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/kings_raid_items?autoReconnect=true&useSSL=false", "java", "test");
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to connect.", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public void closeRSSTMT(ResultSet rs, Statement stmt){
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
	
	public void closeConnection() throws SQLException{
		connection.close();
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
	public boolean loadDriver(){	//unused
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
