import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;



public class DatabaseTest {
	private static final FileType fileType = FileType.CONFIGURATION;

	private static Database db;
	private static DatabaseCreator dbCreator;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String dbName = "DatabaseTest.sqlite";
		dbCreator = new DatabaseCreator(dbName);
		dbCreator.create();
		dbCreator.fillDatabase();
		
		db = new Database("jdbc:sqlite:db" + File.separator + dbName);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.closeConnection();
		dbCreator.delete();
	}

	@Test
	public final void testConnection() throws Exception {
		/*
		 * Ends the current connection
		 */
		
		db.closeConnection();
		
		/*
		 * If it is invalid, it restores a valid connection to db
		 */
		
		db.connectionValidator();
		
		/*
		 * If the retrieval fails, the 
		 * connection has not been properly reset
		 */
		
		db.retrieveFromTable(fileType);
	}
	
	@Test
	public final void testGetTableName() throws Exception {
		String[] table = new String[]{"EC_TABLE", "TEST_SET_TABLE", 
				"TRAIN_SET_TABLE", "CONFIGURATION_TABLE"};
		
		/*
		 * Simply we check the correctness of the method for each available
		 * FileType by comparing the expected table name (contained in array
		 * "table") and the returned table name
		 */
		
		int i = 0;
		
		while (i < FileType.values().length) {
			assertEquals(Database.getTableName(
									FileType.values()[i]), 
									table[i]);
			
			i++;
		}
		
	}

	@Test
	public final void testUpdateClicked() throws Exception {
		/*
		 * Checks if updating the attribute "clicked" of 
		 * a not existing configuration has an observable,
		 * (eventually negative) result on the flow of the
		 * software. 
		 */
		
		ArrayList<Model> retrievedModel = db.retrieveFromTable(fileType);
		
		db.updateClicked("nonExistingConfiguration", 1, fileType);
		
		ArrayList<Model> newRetrievedModelPostUpdate = db.retrieveFromTable(fileType);
		
		/*
		 * We expect that no difference is detected 
		 */
		
		int expectedSize = retrievedModel.size();
		int effectiveSize = newRetrievedModelPostUpdate.size(); 
		assertEquals(effectiveSize, expectedSize);
		
		int i = 0;
		while (i < retrievedModel.size()) {
			assertEquals(
					newRetrievedModelPostUpdate.get(i).getName(), 
					retrievedModel.get(i).getName()
					);
			
			assertEquals(
					newRetrievedModelPostUpdate.get(i).getClicked(), 
					retrievedModel.get(i).getClicked()
					);
			
			i++;
		}
		
		/*
		 * Firstly, we insert a new configuration. 
		 * 
		 * Secondly, click and unclick are performed
		 *  
		 * Finally we check the consistency of the operations.
		 */
		
		String configName = "testAnotherConfiguration";
		db.insertIntoTable(configName, "", fileType);
		
		db.updateClicked(configName, 1, fileType);
		
		ArrayList<Model> models = db.getClickedModels(fileType);
		
		for (Model mod : models)
			if (mod.getName().equals(configName))
				assertEquals(mod.getClicked(), true);
		
		db.updateClicked(configName, 0, fileType);
		models = db.getClickedModels(fileType);
		for (Model m : models)
			assertTrue(!m.getName().equals(configName));
		
		/*
		 * Unclicks every available configuration and checks the
		 * coherency and the consistency of the executed operation
		 */
		
		String secondConfigName = "testUpdateClickedConfiguration2";
		db.insertIntoTable(secondConfigName, "", fileType);
		
		db.updateClicked("*", 0, fileType);
		models = db.getClickedModels(fileType);
		assertTrue(models.size() == 0);
		
		db.removeFromTable(configName, fileType);
		db.removeFromTable(secondConfigName, fileType);
	}
	
	@Test
	public final void testGetClickedModels() throws Exception {
		
		String configName = "testGetClickedModelsConfiguration";
		
		/*
		 * Gets the current array of models
		 */
		
		ArrayList<Model> extracted = db.getClickedModels(fileType);
		
		/*
		 * Inserts a new configuration into the appropriate 
		 * table database and clicks it
		 */
		
		db.insertIntoTable(configName, "", fileType);
		db.updateClicked(configName, 1, fileType);
		
		/*
		 * Gets the new array of models. If everything is 
		 * ok, it must contain the just inserted configuration,
		 * which should have consistent clicked state
		 */
		
		ArrayList<Model> newModel = db.getClickedModels(fileType);
		
		/*
		 * Checks that the size of the fileType array 
		 * of models has been increased by one
		 */
		
		int effective = newModel.size();
		int expected = extracted.size() + 1;
		assertEquals(effective, expected);
		
		/*
		 * Checks the consistency of the clicked state 
		 * of the current models with the old ones
		 */
		
		int i = 0;
		while (i < extracted.size()) {
			assertEquals(
					newModel.get(i).getName(),
					extracted.get(i).getName());
			
			assertEquals(
					newModel.get(i).getClicked(), 
					extracted.get(i).getClicked());
			i++;
		}
		
		assertEquals(newModel.get(newModel.size() - 1).getClicked(), true);	
	}
	
	
	@Test
	public final void testInsertRemoveRetrieveTable() throws Exception {
		
		/*
		 * The current models are retrieved from the database
		 */
		ArrayList<Model> model = db.retrieveFromTable(fileType);
		
		/*
		 * Now, we insert a new model into the appropriate database
		 * table
		 */
		
		String configName = "correctTestConfigName";
		db.insertIntoTable(configName, "", fileType);
		
		/*
		 * We perform another extraction of the current models from 
		 * the database. The ArrayList which is returned will contain 
		 * the new model, as verified later
		 */
		
		ArrayList<Model> newRetrievedModel = db.retrieveFromTable(fileType);
		
		/*
		 * Consequence of these passages is that the models number
		 * (i.e. the size of ArrayList<> named "newRetrievedModel")
		 * has increased by one after insertion
		 */
		
		int expectedModelNumber = model.size() + 1;
		int realModelNumber = newRetrievedModel.size();
		assertEquals(realModelNumber, expectedModelNumber);
		
		/*
		 * Here, a comparison between all the models before the insertion
		 * and after the insertion is performed. The last one is not considered
		 * because it has been inserted in the last insertion
		 */
		
		int i = 0;
		while (i < model.size()) {
			
			assertEquals(newRetrievedModel.get(i).getId(), model.get(i).getId());
			i++;
		}
		
		/*
		 * Test that the last inserted element has name equal
		 * to the choosen name stored in "configName"
		 */
		
		Model lastInsertedModel = newRetrievedModel.get(newRetrievedModel.size() - 1);
		assertTrue(lastInsertedModel.getPath().equals(""));	
		assertTrue(lastInsertedModel.getName().equals(configName));
		
		
		/*
		 * Deletes the just inserted model from the table
		 * of the configurations
		 */
		
		assertTrue(db.removeFromTable(configName, fileType));
		
		/*
		 * After that, the models' array must be 
		 * decreased by one and totally equal to the 
		 * first array of model.
		 */
		
		newRetrievedModel = db.retrieveFromTable(fileType);
		assertEquals(model.size(), newRetrievedModel.size());
		
		i = 0;
		while (i < model.size()) {
			assertEquals(newRetrievedModel.get(i).getId(), model.get(i).getId());
			i++;
		}
		
		/*
		 * Now we check that all the associated configuration 
		 * values (i.e. parameters or variables) have been deleted
		 * as the configuration is not more available. 
		 * To implement this check we need the id of the deleted
		 * model.
		 */
		
		int modelId = lastInsertedModel.getId(); 

		
		assertEquals(db.retrieveConfigurationValues(String.valueOf(modelId)).size(), 0);
		
		/*
		 * Attempts to delete a not existing configuration
		 */
		
		assertFalse(db.removeFromTable("ThisConfigDoesntExist", fileType));
		
		/*
		 * Deselects every external classifier (EC); finally checks
		 * that no configuration can be inserted or retrieved. This
		 * is due to the fact that EC and Configurations are bound.
		 */
		
		ArrayList<Model> ec = 
				db.retrieveFromTable(FileType.EC);
		assertTrue(ec.size() > 0);
		
		db.updateClicked("*", 0, FileType.EC);
		
		db.insertIntoTable("testConfig", "", fileType);
		assertEquals(db.retrieveFromTable(fileType).size(), 0);
			
		db.updateClicked(ec.get(0).getName(), 1, FileType.EC);
		
	}
	
	
	@Test
	public final void testInsertRetrieveConfigurationValues() throws Exception {
		/*
		 * Firstly we create a new configuration named as follows:
		 */
		
		String name = "testInsertRetrieveConfiguration";
		
		//...and inserts it into the database
		db.insertIntoTable(name, "", fileType);
		
		/*
		 * Gets the identifier of the configuration
		 */
		
		String configurationId = "";
		ArrayList<Model> model = db.retrieveFromTable(fileType);
		for (Model m : model)
			if (m.getName().equals(name))
				configurationId = String.valueOf(m.getId());
		assertFalse(configurationId.equals(""));
		
		/*
		 * Creates and fill a new ArrayList<NumericElement>
		 * with some Variables and Parameters
		 */
		
		ArrayList<NumericElement> numericElement = new ArrayList<NumericElement>();
		numericElement.add(new Variable("var1", 1, 11, 2));
		numericElement.add(new Variable("var1", 3, 4, 0.5f));
		numericElement.add(new Param("par1", 21));
		
		/*
		 * Inserts the values of the configuration
		 * into the database
		 */
		
		db.insertConfigurationValues(String.valueOf(configurationId), 
				numericElement);
		
		/*
		 * Retrieves the configuration values inserted
		 */
		
		ArrayList<NumericElement> retrievedNumericElement =
				db.retrieveConfigurationValues(configurationId);
		
		/*
		 * Checks the consistency of the inserted numeric element
		 * with the retrieved ones
		 */
		
		assertEquals(numericElement.size(), retrievedNumericElement.size());
		
		int i = 0;
		while (i < numericElement.size()) {
			assertTrue(numericElement.get(i).toString()
					.equals(retrievedNumericElement.get(i).toString()));
			i++;
		}
					
	}
}
