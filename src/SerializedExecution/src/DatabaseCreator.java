import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class supports testing and allow to create a database 
 * on the go. In such a way testing is not dependent from the
 * the state of the database because a new database is created
 * for each test.
 */

public class DatabaseCreator {
	private Connection connection;
	private String dbPath;

	/**
	 * Instances the database
	 * 
	 * @param fileName
	 */
	public DatabaseCreator(String fileName) {
		String url = "jdbc:sqlite:db/" + fileName;
		dbPath = fileName;
		try {
			if(new File("db/" + fileName).exists()){
				System.out.println("old " + "db/" + fileName + " found! deleting...");
				Files.delete(Paths.get("db/" + fileName));
			}
			connection = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the schema of the database with the different tables
	 * which are used by the software.
	 */
	public void create() {

		// SQL statement for creating a new table
		String configurationTable = "CREATE TABLE IF NOT EXISTS"
				+ "'CONFIGURATION_TABLE' ("
				+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,"
				+ "EC_ID INTEGER, "
				+ "NAME VARCHAR UNIQUE NOT NULL, "
				+ "CHECKED INTEGER NOT NULL DEFAULT 0, "
				+ "FOREIGN KEY(EC_ID) REFERENCES EC_TABLE(ID));";

		String configurationValuesTable = "CREATE TABLE IF NOT EXISTS" 
				+ " 'CONFIGURATION_VALUES_TABLE'  ("
				+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,"
				+ "CONFIGURATION_ID INTEGER, "
				+ "NAME VARCHAR NOT NULL, "
				+ "START FLOAT NOT NULL, "
				+ "STEP FLOAT NOT NULL, "
				+ "END FLOAT NOT NULL, "
				+ "FOREIGN KEY(CONFIGURATION_ID) REFERENCES CONFIGURATION_TABLE(ID)"
				+ ");";

		String trainSetTable = "CREATE TABLE IF NOT EXISTS"
				+ " 'TRAIN_SET_TABLE'"
				+ " ('ID' INTEGER PRIMARY KEY  NOT NULL ,"
				+ "'NAME' VARCHAR DEFAULT (null) ," + "'PATH' VARCHAR UNIQUE, "
				+ "'CHECKED' INTEGER DEFAULT (0) );";

		String testSetTable = "CREATE TABLE IF NOT EXISTS"
				+ " 'TEST_SET_TABLE'"
				+ " ('ID' INTEGER PRIMARY KEY  NOT NULL ,"
				+ "'NAME' VARCHAR DEFAULT (null) ," + "'PATH' VARCHAR UNIQUE, "
				+ "'CHECKED' INTEGER DEFAULT (0) );";

		String ecTable = "CREATE TABLE IF NOT EXISTS " 
				+ "'EC_TABLE'" + " ('ID' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL  ,"
				+ "'NAME' VARCHAR DEFAULT (null) ," 
				+ "'PATH' VARCHAR UNIQUE, " 
				+ "'CHECKED' INTEGER NOT NULL  DEFAULT (0) );";
		try {
			/*
			 * Query are executed here 
			 */
			enableForeignKeysConstraint();
			query(configurationTable);
			query(configurationValuesTable);
			query(trainSetTable);
			query(testSetTable);
			query(ecTable);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Simple method to execute a query to the database
	 * 
	 * @param q
	 * @throws SQLException
	 */
	public void query(String q) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(q);
		stmt.close();
	}
	
	/**
	 * By default SQLite doesn't enable the foreign keys so it is
	 * necessary to enable it when database is created.
	 * 
	 * @throws SQLException
	 */
	private void enableForeignKeysConstraint() throws SQLException{
		Statement stmt = connection.createStatement();
		stmt.execute("PRAGMA foreign_keys = ON;");
		stmt.close();
	}
	
	/**
	 * This method fills the "CONFIGURATION_TABLE" inserting
	 * a new configuration.
	 * 
	 * @param EC
	 * @throws SQLException
	 */
	private void insertConfiguration(ArrayList<String> EC) throws SQLException{
		String insertQuery = "INSERT INTO CONFIGURATION_TABLE VALUES(NULL, ?, ?, ?)";
		boolean wasSet = false;
		for(int i = 0; i < EC.size() ; i++){
			PreparedStatement stmt = connection.prepareStatement(insertQuery);
			stmt.setString(1, String.valueOf(i+1));
			stmt.setString(2, "Configuration EC " + (i+1) + " 0");
			if(!wasSet){
				stmt.setInt(3, 1);
				wasSet = true;
			} else {
				stmt.setInt(3, 0);
			}
			stmt.executeUpdate();
			stmt = connection.prepareStatement(insertQuery);
			stmt.setString(1, String.valueOf(i+1));
			stmt.setString(2, "Configuration EC " + (i+1) + " 1");
			stmt.setInt(3, 0);
			stmt.executeUpdate();
			stmt.close();
		}
	}
	
	/**
	 * This method inserts numeric values associated to a configuration.
	 * @throws SQLException
	 */
	private void fillConfiguration() throws SQLException{
		String insertQuery = "INSERT INTO CONFIGURATION_VALUES_TABLE VALUES(NULL, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(insertQuery);
		stmt.setString(1, String.valueOf(1));
		stmt.setString(2, "Prefilled Variable ");
		stmt.setInt(3, 1);
		stmt.setInt(4, 2);
		stmt.setInt(5, 10);
		stmt.executeUpdate();
		stmt.close();
	}
	
	
	/**
	 * This method fills the "EC_TABLE", the "TEST_SET_TABLE" and
	 * the "TRAIN_SET_TABLE".
	 * @throws SQLException
	 */
	public void fillDatabase() throws SQLException {
		ArrayList<ArrayList<String>> name = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> path = new ArrayList<ArrayList<String>>();
		String[] table = new String[]{"EC_TABLE", "TEST_SET_TABLE", "TRAIN_SET_TABLE","CONFIGURATION_TABLE"};
		
		path.add(new ArrayList<String>());
		for (int i = 0; i < 2; i++)
			path.get(path.size() - 1).add("data" + File.separator + "ec" + File.separator 
					+ "EC" + (i + 1) + ".jar");
		
		path.add(new ArrayList<String>());
		for (int i = 0; i < 3; i++)
			path.get(path.size() - 1).add("data" + File.separator + "test" + File.separator 
					+ "test" + (i + 1) + ".txt");
		
		path.add(new ArrayList<String>());
		for (int i = 0; i < 3; i++)
			path.get(path.size() - 1).add("data" + File.separator + "train" 
					+ File.separator + "train" + (i + 1) + ".txt");
		
		String doubleSeparator = File.separator + File.separator;
		
		for (int i = 0; i < path.size(); i++) {
			name.add(new ArrayList<String>());
			for (int j = 0; j < path.get(i).size(); j++) {
				String[] parts = path.get(i).get(j).split(doubleSeparator);
				String singleName = parts[parts.length - 1]
						.substring(
								parts[parts.length - 1].lastIndexOf(File.separator) + 1,
								parts[parts.length - 1].lastIndexOf('.')
								);
				name.get(i).add(singleName);
			}
		}
		
		for (int i = 0; i < path.size(); i++) {
			String insertQuery = "INSERT INTO " + table[i]
					+ " VALUES(NULL, ?, ?, ?)";
			boolean wasSet = false;
			for (int j = 0; j < path.get(i).size(); j++) {
				PreparedStatement stmt = connection.prepareStatement(insertQuery);
				
				stmt.setString(1, name.get(i).get(j));
				stmt.setString(2, path.get(i).get(j));
				if(!wasSet){
					stmt.setInt(3, 1);
					wasSet = true;
				} else {
					stmt.setInt(3, 0);
				}
				
				stmt.executeUpdate();
				stmt.close();
			}
		}
		
		insertConfiguration(path.get(0));
		fillConfiguration();
	}
	
	/**
	 * This method deletes the database after closing
	 * the connection.
	 * 
	 * @throws IOException
	 */
	public void delete() throws IOException {
		try {
			connection.close();
			while(!connection.isClosed()){
				Thread.sleep(1000);
			}
			
			String path = "db" + File.separator + dbPath;
			File f = new File(path);
			f.delete();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
