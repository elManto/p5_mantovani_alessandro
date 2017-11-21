/*
 * Author: Dario Capozzi, Alessandro Mantovani, Roberto Ronco, Giulio Tavella
 * 
 * Date: 20/06/2017 
 * 
 * The aim of the project is the optimization of an automatic classifier. In 
 * particular, the software will execute the classifier selected by the user 
 * with different combinations of input parameters. The result is a file
 * containing all the outputs for each execution that can be used by the 
 * analyst to choose the best input configuration.
 * 
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * The class Database manages the SQLite database. 
 * 
 * In particular it makes available all the necessary methods
 * to perform the different kinds of query to the database.
 */

public class Database {
	private Connection connection;
	private String path;

	public Database(String path) throws Exception {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection(path);
		this.path = path;
	}

	/**
	 * Perform a "select" query to the database in order to extract the
	 * models contained in the table relative to the FileType "fileType".
	 * 
	 * @param fileType
	 * @return ArrayList<Model>
	 * @throws Exception
	 */
	
	public ArrayList<Model> retrieveFromTable(FileType fileType)
			throws Exception {
		ArrayList<Model> data = new ArrayList<Model>();

		//Builds the "select" query
		String query = "SELECT * FROM " + Database.getTableName(fileType);

		if (fileType == FileType.CONFIGURATION) {
			/*
			 * If we are retrieving a "configuration", it is necessary to 
			 * consider only the configuration that are related to the
			 * clicked EC, so we query the db in order to get the clicked
			 * EC.
			 */
			ArrayList<Model> clickedEC = this.getClickedModels(FileType.EC);
			if (clickedEC.size() == 0)
				return data;	//if no EC selected, no configuration retrieved
			
			/*
			 * if there is an EC selected , we modify the query so that 
			 * we add a "where" clause
			 */
			
			query = query + " WHERE EC_ID = " + clickedEC.get(0).getId() + "";
		}
		//Execute the query
		ResultContainer rw = this.executeQuery(query);

		ResultSet rs = rw.getRs();
		
		while (rs.next()) {
			/*
			 *  Saves attribute values obtained by the
			 *  query to the DB
			 */
			
			Integer id = rs.getInt("ID");
			String name = rs.getString("NAME");
			Integer checked = rs.getInt("CHECKED");
			String path = "";
			if (fileType != FileType.CONFIGURATION)
				path = rs.getString("PATH");
			
			/*
			 *  instance a new Model with the values retrieved by 
			 *  the database 
			 */
			
			Model model = new Model(id, name, path, checked == 1, fileType);
			data.add(model);	//adds the new model to the arrayList to return
		}

		rw.close();
		return data;
	}

	
	/**
	 * Performs an "insert" query into the table relative to the FileType
	 * "fileType", in order to insert the element with name "name" and 
	 * located at path "path". Value of "path" has a double meaning:
	 * it is the file path in case of fileType != FileType.CONFIGURATION
	 * else "path" is replaced by the id of the selected EC id.
	 * 
	 * @param name
	 * @param path
	 * @param fileType
	 * @throws Exception
	 */
	
	public void insertIntoTable(String name, String path, FileType fileType)
			throws Exception {
		
		// builds the "insert" query
		String query = "INSERT INTO " + Database.getTableName(fileType);
		
		/*
		 * As said before, there is a distinction if a FileType is a 
		 * CONFIGURATION or not.
		 */
		if (fileType == FileType.CONFIGURATION) {
			// we get the clicked EC
			ArrayList<Model> clickedEC = getClickedModels(FileType.EC);
			if (clickedEC.size() == 0)
				return;		// no selected EC, so we return
			query = query + " VALUES(null, " + clickedEC.get(0).getId() + ",'"
					+ name + "',0)";
		} // The following branches are added for testing purposes
		else if (fileType == FileType.TEST) {
			query = query + " VALUES(null, '" + name + "', '" + path + "',0)";
		}
		else 
			return;
		
		/*
		 *  Finally, execute the update query
		 */
		this.executeUpdate(query);
	}

	
	/**
	 * Perform a "delete" query in order to remove the element 
	 * named "name" from the table of the corresponding "fileType".
	 * 
	 * If we delete a configuration, we want to remove all the 
	 * values connected to that specific configuration (both 
	 * parameters and variables).
	 * 
	 * If we delete an External Classifier we want to remove
	 * all the configuration which have as EC_ID the id of
	 * the deleted EC.
	 * 
	 * @param name
     * @param fileType
	 * @throws Exception
	 */
	
	public boolean removeFromTable(String name, FileType fileType)
			throws Exception {
		/*
		 * Test if a model with name "name" exists or not
		 */
		
		ResultContainer rw = this.executeQuery("SELECT ID FROM " 
    			+ Database.getTableName(fileType)
    			+ " WHERE NAME = '" + name + "'");
    
		ResultSet rs = rw.getRs();
		Integer id = null;
	    while (rs.next()) 
	    	id = rs.getInt("ID");
	    
	    rw.close();
		
	    /*
	     * If id is still null, model does not exist
	     */
	    
	    if (id == null)
	    	return false;
	    
	    //Creation of the string of the query.
	    
		String q = "DELETE FROM " + Database.getTableName(fileType)
				+ " WHERE NAME = '" + name + "'";
		
		/* 
		 * When a configuration model is removed,
		 * delete all the numeric values associated
		 * to it
		 */
		
		this.executeUpdate("DELETE FROM CONFIGURATION_VALUES_TABLE"
				+ " WHERE CONFIGURATION_ID = '" + id + "'");

		this.executeUpdate(q);
		return true;
	}

	/**
	 * Performs the SQLite query indicated by the string named "query"
	 * using the prepared statement.
	 * 
	 * @param query
	 * @return ResultSet
	 * @throws SQLException
	 */
	
	private ResultContainer executeQuery(String query) throws SQLException {
		connectionValidator();
		PreparedStatement pst = this.connection.prepareStatement(query);
		ResultSet rs = pst.executeQuery();
		return new ResultContainer(pst, rs);
	}
	
	/**
	 * Performs the SQLite "update" related to the string named
	 * "query" using the prepared statement.
	 * 
	 * @param query
	 * @throws SQLException
	 */

	private void executeUpdate(String query) throws SQLException {
		connectionValidator();
		PreparedStatement pst = this.connection.prepareStatement(query);
		pst.executeUpdate();
		pst.close();
	}

	/**
	 * The method getTableName() is static and offers a way to
	 * obtain the table name in the database given a specific
	 * FileType "fileType".
	 * 
	 * @param fileType
	 * @return String
	 * @throws Exception
	 */
	
	public static String getTableName(FileType fileType) {
		switch (fileType) {
			case EC:
				return "EC_TABLE";
			case TEST:
				return "TEST_SET_TABLE";
			case TRAIN:
				return "TRAIN_SET_TABLE";
			case CONFIGURATION:
				return "CONFIGURATION_TABLE";
			default:
				return "";
		}
	}
	
	/**
	 * Performs an update query in order to Set the value of the 
	 * database field "CHECKED" to "clicked". The table is 
	 * indicated by the "fileType".
	 * 
	 * @param selectedName
	 * @param clicked
	 * @param fileType
	 * @throws Exception
	 */
	
	public void updateClicked(String selectedName, int clicked,
			FileType fileType) throws Exception {
		String whereClause = "";
		// "*" (asterisk) character is used for indicates all the
		// elements in a table.
		if (!selectedName.equals("*"))
			whereClause = " WHERE NAME = '" + selectedName + "'";
		
		// Builds the string
		String q = "UPDATE " + getTableName(fileType) + " SET CHECKED = "
				+ clicked + whereClause;
		
		// Executes the query
		this.executeUpdate(q);
	}

	
	/**
	 * Retrieves the models contained in the database in the table
	 * indicated by "fileType" if the database field "CHECKED"
	 * evaluates "1".
	 * 
	 * @param fileType
	 * @return ArrayList<Model>
	 * @throws Exception
	 */
	
	public ArrayList<Model> getClickedModels(FileType fileType)
			throws SQLException {
		ArrayList<Model> data = new ArrayList<Model>();

		// Builds a "select" query
		String query = "SELECT * FROM " + Database.getTableName(fileType)
				+ " WHERE CHECKED = 1 ";
		ResultContainer rw = this.executeQuery(query);

		ResultSet rs = rw.getRs();
		
		while (rs.next()) {
			
			/*
			 *  Get attribute values from DB
			 */
			
			Integer id = rs.getInt("ID");
			String name = rs.getString("NAME");
			String path = "";
			if (fileType != FileType.CONFIGURATION)
				path = rs.getString("PATH");
			
			/*
			 *  Create a Model "model" to add to the ArrayList<Model>
			 *  named "data". This ArrayList<Model> will be returned.
			 */
			
			Model model = new Model(id, name, path, true, fileType);
			data.add(model);
		}

		rw.close();
		
		return data;
	}

	
	/**
	 * Get all the numeric values that have belong to a configuration
	 * with id "configuration_id". For each extracted value the method
	 * understands if it is a parameter or a variable and decides what 
	 * class to instantiate (i.e. "Parameter" or "Variable").
	 * To decide what kind of value has been retrieved we use a simple way:
	 * If the "step" is a negative number, then a parameter has been
	 * retrieved, else, the retrieved element is a variable.
	 * 
	 * @param configurationId
	 * @return ArrayList<NumericElement>
	 * @throws Exception
	 */
	
	public ArrayList<NumericElement> retrieveConfigurationValues(
			String configurationId) throws SQLException {
		ArrayList<NumericElement> val = new ArrayList<NumericElement>();

		/*
		 *  Builds the query to select the values from the
		 *  "CONFIGURATION_VALUES_TABLE" table.
		 */
		String q = "SELECT * FROM CONFIGURATION_VALUES_TABLE WHERE CONFIGURATION_ID = "
				+ configurationId;
		ResultContainer rw = this.executeQuery(q);

		ResultSet rs = rw.getRs();
		
		while (rs.next()) {
			// Get attribute values from DB
			String name = rs.getString("NAME");
			Float start = rs.getFloat("START");
			Float step = rs.getFloat("STEP");
			Float end = rs.getFloat("END");

			/*
			 * Takes a decision on what kind of element has been
			 * retrieved and instance it.
			 */
			NumericElement el;
			if (step <= 0)
				el = new Parameter(name, start);
			else
				el = new Variable(name, start, end, step);

			val.add(el);
		}

		rw.close();
		return val;
	}
	
	/**
	 * Executes an "insert" query to store the numeric values related 
	 * to a configuration with id "id". The numeric values are contained
	 * in the ArrayList<NumericElement>.
	 * 
	 * @param id
	 * @param value
	 * @throws Exception
	 */
	
	public void insertConfigurationValues(String id,
			ArrayList<NumericElement> value) throws Exception {
		this.executeUpdate("DELETE FROM CONFIGURATION_VALUES_TABLE"
				+ " WHERE CONFIGURATION_ID = " + id);

		for (NumericElement el : value) {
			String[] parts = el.toString().split(" ");

			if (parts.length == 2)
				parts = new String[] { parts[0], parts[1], "0", "0" };

			this.executeUpdate("INSERT INTO CONFIGURATION_VALUES_TABLE"
					+ " VALUES(null, " + id + ",'" + parts[0] + "'," + parts[1]
					+ "," + parts[3] + "," + parts[2] + ")");
		}
	}
	
	
	/**
	 * Close the connection to the SQLite database.
	 * 
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		this.connection.abort(Executors.newSingleThreadExecutor());
		this.connection.close();
	}
	
	public void connectionValidator() throws SQLException {
		if(!this.connection.isValid(1000))
			this.connection = DriverManager.getConnection(path);	
	}
}
