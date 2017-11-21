import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



public class ConfigurationManagerTest {
	
	private static final String testConfigurationName = "21";
	
	private static ConfigurationManager configManager;
	private static DatabaseCreator dbCreator;
	private static Database db;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// creation of database
		String dbName = "configurationManager.sqlite";
		
		dbCreator = new DatabaseCreator(dbName);
		dbCreator.create();
		dbCreator.fillDatabase();
		
		String dbPath = "jdbc:sqlite:db" + File.separator + dbName;
		

		/*
		 * First, we connect to database. After that, we
		 * insert some parameters and some values in the
		 * ArrayList<NumericElement> named "numericElement"
		 */
		
		db = new Database(dbPath);
		
		ArrayList<NumericElement> numericElement = new ArrayList<NumericElement>();
		// insert a variable "v1"
		numericElement.add(new Variable("v1", 2, 8, 2));
		
		// insert a second variable "v2"
		numericElement.add(new Variable("v2", -1, 2, 0.25f));
		
		// insert a parameter "p1"
		numericElement.add(new Param("p1", 21));
		
		/*
		 * The ArrayList<NumericElement> numericElement is 
		 * added to the configuration values with id
		 * given by "testConfigurationName."
		 *
		 */
		
		db.insertConfigurationValues(testConfigurationName, numericElement);
		
		/*
		 * Such a configuration exists since the databaseCreator
		 * inserts at 4 configurations with an AUTO INCREMENT
		 * SQLite attribute called "id". 
		 */
		
		configManager = new ConfigurationManager(db);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {	
		
		db.closeConnection();	//close connection to db
		dbCreator.delete();
	}
	
	@Before
	public void setUp() throws Exception {
		/*
		 * Implements the loading of
		 * the configuration
		 */
		
		configManager.load(testConfigurationName);	
	}
	
	@Test
	public final void testGetSetConfiguration() throws Exception {
		
		/*
		 * A new row of NumericElement objects
		 * is created
		 */
		
		ArrayList<NumericElement> configurationValues = new ArrayList<NumericElement>();
		
		// add a variable named "configurationVariable"
		configurationValues.add(new Variable("configurationVariable", 2, 3, 1));
		// add a parameter called "configurationParameter"
		configurationValues.add(new Param("configurationParameter", 21));
		
		/*
		 * The just created row becomes the current configuration and 
		 * configuration values are retrieved with the method 
		 * "getConfiguration()" (which belongs to the class 
		 * ConfigurationManager.java). 
		 */
		
		configManager.setConfigurationFromNumericElement(configurationValues);
		ArrayList<NumericElement> newConfigurationValues = configManager.getConfiguration();
		
		/*
		 * Verify the size's equality of the old and of the new
		 * configuration with an assertTrue
		 */
		
		int configurationValueSize = configurationValues.size(); // old config
		int newConfigurationValueSize = newConfigurationValues.size(); // new config
		
		assertTrue(configurationValueSize == newConfigurationValueSize);
		
		
		/*
		 * Iterates on the new configuration and the old configuration
		 * in order to check the equality of each value.
		 */
		
		// First, we get iterators
		Iterator<NumericElement> configurationValuesIt = configurationValues.iterator();
		Iterator<NumericElement> newConfigurationValuesIt = configurationValues.iterator();
		
		// iteration and check
		while (configurationValuesIt.hasNext() && newConfigurationValuesIt.hasNext()) 
			assertTrue(configurationValuesIt.next().toString()
					.equals(newConfigurationValuesIt.next().toString()));
		
		
		/*
		 *  Instance of a new ArrayList<Row>. It contains
		 *  rows of parameters and of variables. 
		 */
		ArrayList<Row> configRow = new ArrayList<Row>();
		
		/*
		 * Now creates a new row of variables and parameters 
		 * and fills it with some correct values.
		 */
		
		ParameterRow parameter = new ParameterRow(0, 0);
		// tests the setter of class ParameterRow
		parameter.setValue(new Param("configurationParameter", 21));
		configRow.add(parameter);
		
		VariableRow variable = new VariableRow(0, 0);
		// tests the setter of class VariableRow
		variable.setValue(new Variable("configurationVariable", 3, 7, 1));
		configRow.add(variable);
		
		/*
		 * The just created array of variables and parameters
		 * is set as current configuration
		 */
		
		configManager.setConfigurationFromRow(configRow);
		
		/*
		 * The new config is retrieved with the getter provided by
		 * the class ConfigurationManager.
		 */
		
		ArrayList<NumericElement> actualRow = configManager.getConfiguration();
		
		/*
		 * Verify the equality of the new configuration with the old one.
		 * First of all it realizes a check on the size. Secondly, it implements 
		 * a control for each element
		 * 
		 */
		
		int actualRowSize = actualRow.size(); 	// the two sizes.
		int configRowSize = configRow.size();
		
		assertTrue(actualRowSize == configRowSize);
		
		Iterator<NumericElement> actualRowIt = actualRow.iterator();
		Iterator<Row> configRowIt = configRow.iterator();
		
		while (actualRowIt.hasNext() && configRowIt.hasNext()) 
			assertTrue(configRowIt.next().retrieveValue().toString()
					.equals(actualRowIt.next().toString()));
		
		/*
		 * If the two configuration are equals, it is retrieved the
		 * current configuration to allow the configuration restore.
		 */
		
		actualRow = configManager.getConfiguration();
		
		/*
		 * Now test for the insertion of an invalid array of 
		 * Variable and Parameter
		 */
		
		configRow.add(new VariableRow(1, 1));
		//The configuration is INVALID!!!
		
		/*
		 * Because of invalidity of configuration we expect that the 
		 * setConfigurationFromRow method of ConfigurationManager.java
		 * fail returns false.
		 */
		
		assertTrue(!configManager.setConfigurationFromRow(configRow));
		
		/*
		 * Finally we reset a valid configuration.
		 */
		
		configManager.setConfigurationFromNumericElement(actualRow);
		
	}
	
	@Test
	public final void testGetId() {
		/*
		 * Simply, this checks that id of loaded config is the same
		 * of "testConfigurationName".
		 */
		assertTrue(configManager.getId().equals(testConfigurationName));
	}
	
	@Test
	public final void testLoad() throws Exception {
		
		/*
		 * The idea of this test function is the following:
		 * 
		 * 1) the setUpBeforeClass method has the aim of 
		 * reloading a configurations with id given by
		 * "testConfigurationName"-
		 * 
		 * 2) the configuration which is reloaded has to correspond
		 * to the configuration which has been retrieved in the 
		 * setUpBeforClass()
		 */
		
		ArrayList<NumericElement> managerConfiguration =
				configManager.getConfiguration();
		
		// Extracts config with id "testConfigurationName" from db
		ArrayList<NumericElement> databaseConfiguration =
				db.retrieveConfigurationValues(testConfigurationName);
		
		/*
		 * Checks for the equality of size of the two configurations
		 */
		assertEquals(managerConfiguration.size(), databaseConfiguration.size());
		
		/*
		 * Finally, it checks the elements of the two configurations
		 */
		Iterator<NumericElement> managerConfigIt = 
				managerConfiguration.iterator(); 
		
		Iterator<NumericElement> databaseConfigIt = 
				databaseConfiguration.iterator();
		
		// Parallel iteration on the two configuration
		while (managerConfigIt.hasNext() && databaseConfigIt.hasNext()) 
			assertTrue(managerConfigIt.next().toString()
					.equals(databaseConfigIt.next().toString()));
	}
	
	@Test
	public final void testGetNumericElementIterator() throws Exception {
		/*
		 * Test the fact that iterator returned by the method
		 * getNumericElementIterator is not null.
		 */
		assertNotNull(configManager.getNumericElementIterator());
	}
	
	
	@Test
	public final void testWrite() throws Exception {
		/*
		 * Function write runs in the following way:
		 * 1) It checks the validity of the current 
		 * configuration
		 * 2) If it is satisfied, the write method triggers
		 * the db to insert the new configuration values. 
		 */
		
		// Obtain the numeric elements that compose the current configuration 
		ArrayList<NumericElement> retrievedConfiguration = 
				configManager.getConfiguration();
		
		/*
		 * Insert an uncorrect variable. In particular start value is
		 * major than its end value.
		 * Its write by configurationManager must throw an exception
		 */
		
		retrievedConfiguration.add(new Variable("fouloWriteVariable1", 5, 2, 2));
		
		// Sets the modified array of values as the new current configuration
		configManager.setConfigurationFromNumericElement(retrievedConfiguration);
		
		try {	
			// try to write but it will throw an exception
			configManager.write();
			
			fail();
		} catch (Exception e) {
			
		}
		
		
		/*
		 * Inserts another uncorrect value that consists of a variable
		 * with a negative step.
		 * We expect that when the write() is called
		 * it throws an Exception
		 */
		
		retrievedConfiguration.set(retrievedConfiguration.size() - 1,
				new Variable("fouloWriteVariable2", 0, 5, -2));	//negative step!
		
		configManager.setConfigurationFromNumericElement(retrievedConfiguration);
		
		try {	
			// Try to write but we capture the exception thrown by write()
			configManager.write();
			
			fail();
		} catch (Exception e) {
		}
		
		
		/*
		 * Now we test the case of correct variable. First, we remove 
		 * the uncorrect variable deleting the last added value.
		 */
		int configurationValueToRemove = retrievedConfiguration.size() - 1;
		retrievedConfiguration.remove(configurationValueToRemove);
		
		// A legal configuration is reset as current
		configManager.setConfigurationFromNumericElement(retrievedConfiguration);
		
		/*
		 * Creates a valid NumericElement and inserts it in 
		 * the configuration.
		 */
		retrievedConfiguration.add(new Variable("fouloWriteVariable1", 2, 4, 1));
		
		// Then, it inserts the modified configuration in the configurationManager
		configManager.setConfigurationFromNumericElement(retrievedConfiguration);
		
		/*
		 * Now configManager contains a valid configuration so we require
		 * the write() method in order to store it into the database.
		 * 
		 * Unlike past calls to write method the following call to write()
		 * does NOT throw any exception, since the configuration
		 * is legal and the database is consistent and coherent.
		 */
		
		configManager.write();
		
		/*
		 * Extracts the just inserted configuration values 
		 * from the database.
		 */
		
		ArrayList<NumericElement> extracted = 
				db.retrieveConfigurationValues(testConfigurationName);	
		
		/*
		 * Now we realize the final check: in particular we expect that
		 * retrieved values are the same of the current configuration
		 * values.
		 */
		
		int extractedSize = extracted.size();
		int retrievedConfigurationSize = retrievedConfiguration.size();
		
		// First check on the size
		assertTrue(extractedSize == retrievedConfigurationSize);
		
		Iterator<NumericElement> extractedIt = extracted.iterator();
		Iterator<NumericElement> retrievedIt = 
				retrievedConfiguration.iterator();
		
		// Check on the different values
		while (extractedIt.hasNext() && retrievedIt.hasNext()) 
			assertTrue(extractedIt.next().toString()
					.equals(retrievedIt.next().toString()));
	}
	
	
	
	@Test
	public final void testSubject() {
		Writer w = new Writer();
		
		configManager.attach(w);
		
		/*
		 * According to the Observer design pattern we have a sequence of
		 * function calls which can be represented as:
		 * 
		 * configManager.notifyObserver -> writer.update -> configManager.write
		 * 
		 * Supposed that the write() method correctly runs (we have tested it 
		 * in the previous testing method named "testWrite()"), we can consider
		 * that the notifyObserver() method is succesful if it doesn't throw 
		 * any exception and at the same time a valid configuration manager
		 * is loaded in the configManager (the observed subject).
		 * 
		 * This corresponds to say that the test is succesful whether the
		 * method .write() doesn't throw exception.
		 */
		
		try {
			configManager.notifyObservers();
		}
		catch (Exception ex) {
			fail();
		}
		
		/*
		 * Writer is detached and we load an invalid
		 * configuration into configurationManager. After that we invoke the
		 * method configurationManager.notifyObservers()
		 * 
		 * If no exception is thrown the test is succesfully terminated. This
		 * because no other observers attached to configManager -> no subject
		 * can start the configuration validity check (and after thi the write
		 * into db).
		 */
		
		configManager.detach(w);

		ArrayList<Row> rowToAdd = new ArrayList<Row>();
		rowToAdd.add(new VariableRow(21, 21));
		
		ArrayList<NumericElement> configRow = 
				configManager.getConfiguration();
		
		configManager.setConfigurationFromRow(rowToAdd);
		
		try {
			configManager.notifyObservers();
		} catch (Exception e) {
			fail();
		}
			
		
		/*
		 * Finally we reset a valid config
		 */
		
		configManager.setConfigurationFromNumericElement(configRow);
	}
}
