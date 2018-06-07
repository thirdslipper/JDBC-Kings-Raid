/**
 * Author: Colin Koo
 * Date: 3/19/2018
 * Description: This project is to practice creating a SQL database managed using java, MySQL, and JDBC for a mobile game.
 * notes: <p>, {@link x}, @see
 * Database name : kings_raid_items, port 3306
 * CREATE USER 'java'@'localhost' IDENTIFIED BY 'test';
 * GRANT ALL ON kings_raid_items.* TO 'java'@'localhost' IDENTIFIED BY 'test'
 * Pre-reqs to using the project: an existing database, an existing account for the database (or root), and port is set to 3306.
 * This project uses the package Java.sql, or the Java Database Connectivity to implement MySQL database management using an
 * embedded SQl.
 * The account information and port may be changed.  
 */
package sql.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;



public class kingsraid {
	private Connection connection = null;

	/**
	 * Creates an object of this class to execute the UI for the user.
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String args[]) throws SQLException{
		kingsraid obj = new kingsraid();
		obj.ui();
	}

	/**
	 * This is the main user interface that the user interacts with to utilize the functions to manipulate the database
	 */
	public void ui(){
		Scanner kb = new Scanner(System.in);
		System.out.println("Would you like to do a custom(1) or default(2) connection?");
		String connect = "";
		do {
			if (!connect.equals("")){
				System.out.println("Invalid entry.");
			}
			connect = kb.nextLine();
		} while (!connect.equals("1") && !connect.equals("2") && !connect.equalsIgnoreCase("custom") && !connect.equalsIgnoreCase("default"));
		
		if (connect.equalsIgnoreCase("custom") || connect.equals("1")){
			System.out.println("this is insecure and only for personal project intentions.");
			System.out.println("Enter url, user, pass."
					+ "\n ex: localhost:3306/kings_raid_items?autoReconnect=true&useSSL=false, java, test");
			String[] credentials = kb.nextLine().trim().split(", ");
			if (credentials.length == 3)
				customConnection(credentials[0], credentials[1], credentials[2]);
			else {
				System.out.println("invalid credential entry, entering default connection!");
				getConnection();
			}
		}
		else{
			getConnection();
		}
		
		
		int input = 1;
		try {
			while (input != 6){
				System.out.println("ようこそみんなさん | Welcome"
						+ "\nWhat would you like to do?"
						+ "\n\t1. create table (heroes or items)"
						+ "\n\t2. delete table (heroes or items)"
						+ "\n\t3. custom statement"
						+ "\n\t4. insert"
						+ "\n\t5. display tables"
						+ "\n\t6. exit");
				input = kb.nextInt();
				kb.nextLine();
				switch (input) {
				case 1: createItemsAndHeroes(kb);
						break;
				case 2: deleteTables(kb);
						break;
				case 3: createStatement(createStatementString(kb));
						break;
				case 4: insert(kb);
						break;
				case 5: displayTables();
						break;
				case 6: closeConnection();
						break;
				default: System.out.println("invalid option!");;
						break;
				}
				System.out.println("\n");
			}
			System.out.println("Seeやなら！");
		} catch (SQLException e){
			System.out.println("Error! " + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
		kb.close();
		System.exit(0);
	}
	
	/**
	 * This method serves as a Error-checking function to allow some leeway when allowing the user to enter a MySQL query.
	 * @param kb Scanner object for user I/O.
	 * @return a properly formatted string representing a MySQL query.
	 */
	// to do
	public String createStatementString(Scanner kb){
		System.out.println("enter query");
		String input = kb.nextLine();
		return input;
	}
	
	/**
	 * This method creates a predetermined table in the database corresponding to either heroes or items.
	 * enums for item_class, item_type, item_location to do
	 * @param kb Scanner object for user I/O.
	 */
	public void createItemsAndHeroes(Scanner kb){
		String input;
		try {
			Statement stmt = connection.createStatement();
			System.out.println("Enter table name to create: (heroes or items)");
			do {
				input = kb.nextLine().toLowerCase().trim();
			} while (!(input.equals("items") || input.equals("heroes")));
			//check somewhat unreliable
			if (input.equals("items")){
				String statsList = "('atk', 'ats', 'crit', 'critd', 'pen', 'ls', 'pdodge', 'pblock', 'pdef', 'mdodge', 'mblock', 'mdef', 'hp', 'acc', 'cc')";
				String stats = "ENUM " + statsList + " NOT NULL, ";
				stmt.executeUpdate("create table kings_raid_items.ITEMS ("
						+ "id SMALLINT UNSIGNED UNIQUE AUTO_INCREMENT, "
							//maybe remove check, since ENUM
						+ "Stat_1 " + stats + "CHECK (Stat_1 in " + statsList + "), "
						+ "Stat_2 " + stats + "CHECK (Stat_2 in " + statsList + "), "
						+ "Stat_3 " + stats + "CHECK (Stat_3 in " + statsList + "), "
						+ "Stat_4 " + stats + "CHECK (Stat_4 in " + statsList + "), "
						+ "Tier TINYINT UNSIGNED DEFAULT 7, "
							+ "CHECK (Tier>0 AND Tier<8),"
						+ "Quantity TINYINT UNSIGNED DEFAULT 0,"
			//			+ "item_class NOT NULL, " //ENUM(\"\")
			//			+ "item_type NOT NULL,"
			//			+ "item_location NOT NULL," // ENUM(\"\")
						+ "Mod_stat ENUM(\"Stat_1\", \"STAT_2\", \"STAT_3\", \"STAT_4\", \"NULL\"),"
						+ "Enchant varchar(50),"
						+ "PRIMARY KEY (id))");
			}
			else if (input.equals("heroes")){
				String charpref = "TINYINT UNSIGNED UNIQUE DEFAULT NULL, ";
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
			closeRSSTMT(null, stmt);
		} catch (SQLException e){
			System.out.println("Error! " + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
	}

	/**
	 * Inserts data into the items table using a {@link PreparedStatement}.  The user-entered data is split into an array to be set into
	 * the prepared statement.  Not possible to add multiple items in a single query (use createStatement()).
	 * todo insert data into heroes table 
	 * @param kb Scanner to read user I/O.
	 */
	public void insert(Scanner kb){
		try {
			String inputTable = "";
			do{
				System.out.println("Would you like to insert into items or heroes?");
				inputTable = kb.nextLine();
			} while (inputTable.equalsIgnoreCase("items") || inputTable.equalsIgnoreCase("heroes"));
			
			if (inputTable.equalsIgnoreCase("heroes") || inputTable.equalsIgnoreCase("items")){
				PreparedStatement ps;
				if (inputTable.equalsIgnoreCase("items")){
					ps = connection.prepareStatement("insert into kings_raid_items.items" 
							+ "(stat_1, stat_2, stat_3, stat_4, tier, mod_stat, item_class, item_type, item_location) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");	
					System.out.println("Enter values to enter into: (stat_1, stat_2, stat_3, stat_4, tier, mod_stat, item_class, item_type, item_location)"
							+ "\nv1, v2,..,vn");
				} else {
					ps = connection.prepareStatement("insert into kings_raid_items.heroes"
							+ "(name, armor_pref, sec_gear_pref, accessory_pref, orb_pref) values (?, ?, ?, ?, ?)");
					System.out.println("Enter values to enter into: (name, armor_pref, sec_gear_pref, accessory_pref, orb_pref)"
							+ "\nv1, v2,..,vn");
				}
				String input;
				String[] inputBlock;

				input = kb.nextLine();
				inputBlock = input.split(", ");
				
				//System.out.println("entered: " + Arrays.toString(inputBlock));
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
				closeRSSTMT(null, ps);
			} else {
				System.out.println("Unspecified table!");
				insert(kb);
			}

		} catch (SQLException e) {
			System.out.println("Error!" + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
	}

	/**
	 * 
	 * @param query A user entered MySQL query that has some Error-checking attempted on the statement.  The function initially 
	 * checks if the query has a result to be returned and executes accordingly.  If there is a result to be returned, then the query is
	 * analyzed, where the control flow divides into whether or not the MySQL code uses an asterisk (*) for the table fields.  The result
	 * is then formatted and displayed for the user.
	 * Works for insert into, select, select *, show(columns), show 
	 * todo rewrite using ResultSetMetaData?
	 */
	public void createStatement(String query){
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.createStatement();
				//intended for select statements
			if (stmt.execute(query)) {	//true if rs obj or false if no update/no result, rs stored in stmt; executeQuery(string) returns ResultSet
				System.out.println("has rs");
				rs = stmt.getResultSet();
				if (query.length() > 6 && query.trim().substring(0, 6).contains("select")){
						//start indexes of certain keywords
					int start = query.indexOf("select ") + 7;	//should be 0
					int end = query.indexOf(" from ");	//index of first space before from
					
					
					String[] selectedTerms = query.substring(start, end).split(", ");	//contains terms, assuming not *
					
					if (selectedTerms.length > 0 && selectedTerms[0].equals("*")){	//if asterisk
						System.out.println("is *");
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
						
						closeRSSTMT(columns, null);
						createStatement(asterisk.toString());
					} else {	//If select function but no asterisk
						System.out.println("no *");
						String format;
						String[] textSizes = new String[selectedTerms.length];
						//for (String s : selectedTerms){
						for (int k = 0; k < selectedTerms.length; ++k){
							format = "%-" + (selectedTerms[k].length()+1) + "s";
							textSizes[k] = format;
							System.out.printf(format, selectedTerms[k]);	//item_location is long
						}
						System.out.println();
						while (rs.next()){
							for (int i = 0; i < selectedTerms.length; ++i){
								System.out.printf(textSizes[i], rs.getString(selectedTerms[i]));
								//System.out.print(selectedTerms[i] + ": " + rs.getString(selectedTerms[i]) + ", ");
							}
							System.out.println();
						}
					}
				}
				else if (query.length() > 4 && query.substring(0, 4).toLowerCase().contains("show")){	// has resultset
					System.out.println("show");
					ResultSetMetaData rsmd = rs.getMetaData();
					int cols = rsmd.getColumnCount();
					while (rs.next()){
						for (int i = 1; i <= cols; ++i)
							System.out.printf("%-10s", rs.getString(i));
					}
				}
			}
			else {
				System.out.println("statement executed!");
				//stmt.executeUpdate(query);
			}
		} catch (SQLException e) {
			System.out.println("Error! " + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		} finally {
			closeRSSTMT(rs, stmt);
		}
	}
	public void displayTables(){
		createStatement("SHOW TABLES FROM kings_raid_items");
	}


	
	/**
	 * Deletes a table specified by the user.
	 * @param kb Scanner for user I/O.
	 */
	public void deleteTables(Scanner kb){
		try {
			String tableToDrop;
			StringBuilder input = new StringBuilder("drop table kings_raid_items.");
			
			Statement stmt = connection.createStatement();
			System.out.println("Enter table to drop: items or heroes");
			tableToDrop = kb.nextLine();
			
			input.append(tableToDrop);
			stmt.executeUpdate(input.toString());
			
			System.out.println("Table " + tableToDrop + " successfully dropped!");
			closeRSSTMT(null, stmt);
		} catch (SQLException e) {
			System.out.println("Error!" + e.getMessage() + ", Error code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
		}
	}
	
	/**
	 * Grants a connection to the MySQL server using the login credentials that are hard-coded in.
	 */
	public void getConnection(){
		try {	//tests for driver and returns an active connection to the database
			Class.forName("com.mysql.jdbc.Driver");
			//String unicode="useSSL=false&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
			//can replace "root" with "java and "" with "test"
			connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/kings_raid_items?autoReconnect=true&useSSL=false", "root", "");
			System.out.println("Connected!");
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to connect.", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Grants a connection to a specified MySQL server using a custom login.
	 * @param url 
	 * @param user
	 * @param password
	 */
	public void customConnection(String url, String user, String password){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection =  DriverManager.getConnection("jdbc:mysql://" + url, user, password);
			System.out.println("Connected!");
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to connect.", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Helper function to close ResultSet and Statement objects.
	 * @param rs
	 * @param stmt
	 */
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
	/**
	 * Disconnects the user from the MySQL server.
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException{
		connection.close();
	}

	
	/**
	 * Login creates a connection with a database specified in parameters, with the user information also being 
	 * specified in the fields.
	 * This function is unused, as it is incorporated into getConnection().
	 * @param user	the username to login with.
	 * @param pass	the password to login with.
	 * @param targeturl	the link to the database to connect to.
	 * @return	the connection object representing an object of valid connection to the database or null.
	 * @see java.sql.Connection.getConnection
	 */
	public Connection login(String user, String pass, String targeturl){
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
	 * This function is unused, as it is incorporated into getConnection().
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
