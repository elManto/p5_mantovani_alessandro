package alessandromantovanitest_strutturali;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import alessandromantovani.Database;
import alessandromantovani.FileType;
import alessandromantovani.Model;
import alessandromantovani.NumericElement;
import alessandromantovani.Parameter;
import alessandromantovani.Variable;
import util.DatabaseCreator;

public class DatabaseTest {
	private static Database db;
	private static DatabaseCreator dbCreator;
	private static final FileType fileType = FileType.CONFIGURATION;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String dbName = "DatabaseTest.sqlite";
	
		dbCreator = new DatabaseCreator(dbName);
		dbCreator.create();
		dbCreator.fillDatabase();
		
		String dbPath = "jdbc:sqlite:db" + File.separator + dbName;
		db = new Database(dbPath);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.closeConnection();
		dbCreator.delete();
	}
	
	@Test
	public final void testGetTableName() throws Exception {
		String[] table = new String[]{"EC_TABLE", "TEST_SET_TABLE", 
				"TRAIN_SET_TABLE", "CONFIGURATION_TABLE"};
		
		int numberOfTables = table.length;
		for (int i = 0; i < numberOfTables; i++)
			assertEquals(Database.getTableName(FileType.values()[i]), table[i]);
	}
	
	@Test
	public final void testUpdateClicked() throws Exception {
		/*
		 * Tests whether updating the clicked state of 
		 * a non existing configuration has a perceivable,
		 * disruptive effect on the execution or not
		 */
		
		ArrayList<Model> retrievedModel = db.retrieveFromTable(fileType);
		
		db.updateClicked("inexistentConfiguration", 1, fileType);
		
		ArrayList<Model> newRetrievedModel = db.retrieveFromTable(fileType);
		
		int retrievedModelSize = retrievedModel.size();
		int newRetrievedModelSize = newRetrievedModel.size();
		assertEquals(retrievedModelSize, newRetrievedModelSize);
		
		for (int i = 0; i < retrievedModelSize; i++) {
			assertTrue(newRetrievedModel.get(i).getName() 
					== retrievedModel.get(i).getName());
			assertTrue(newRetrievedModel.get(i).getClicked() 
					== retrievedModel.get(i).getClicked());
		}
		
		/*
		 * Inserts a new configuration, clicks and unclicks 
		 * and checks the consistency of the operations
		 */
		
		String configName = "UpdateClickedConfigTest";
		db.insertIntoTable(configName, "", fileType);
		
		db.updateClicked(configName, 1, fileType);
		
		ArrayList<Model> checkedModel = db.getClickedModels(fileType);
		Iterator<Model> checkedModelIterator = checkedModel.iterator();
		
		while(checkedModelIterator.hasNext()) {
			Model mod = checkedModelIterator.next();
			if (mod.getName().equals(configName))
				assertTrue(mod.getClicked());
		}
		
		db.updateClicked(configName, 0, fileType);
		checkedModel = db.getClickedModels(fileType);
		
		for (int i = 0; i < checkedModel.size(); i++)
			assertFalse(checkedModel.get(i).getName().equals(configName));
		
		
		/*
		 * Unclicks all the configurations and verifies the
		 * consistency of the operation
		 */
		
		String secondConfigName = "UpdateClickedConfig2test";
		db.insertIntoTable(secondConfigName, "", fileType);
		
		db.updateClicked("*", 0, fileType);
		checkedModel = db.getClickedModels(fileType);
		assertEquals(checkedModel.size(), 0);
		
		db.removeFromTable(secondConfigName, fileType);
		db.removeFromTable(configName, fileType);
		
	}

	@Test
	public final void testInsertRemoveRetrieveTable() throws Exception {
		/*
		 * Retrieves the current models from the database
		 */
		
		ArrayList<Model> retrievedModel = db.retrieveFromTable(fileType);
		
		/*
		 * Inserts a new model into the database
		 */
		
		String s = "testRetrieveInsert";
		
		db.insertIntoTable(s, "", fileType);
		
		/*
		 * Retrieves the current models from the database, again.
		 * Now the returned ArrayList will contain the new model,
		 * as verified later
		 */
		
		ArrayList<Model> newRetrievedModel = db.retrieveFromTable(fileType);
		
		/*
		 * Asserts that the models number has increased
		 * by one after insertion
		 */
		
		int expextedModelNumber = retrievedModel.size() + 1;
		int realModelNumber = newRetrievedModel.size();
		assertTrue(realModelNumber == expextedModelNumber);
		
		/*
		 * Compares all the models before the insertion with
		 * the new models, except for the last one, which must
		 * be the result of the last insertion
		 */
		
		Iterator<Model> retrievedModelIt = retrievedModel.iterator();
		Iterator<Model> newRetrievedModelIt = newRetrievedModel.iterator();
		
		while(retrievedModelIt.hasNext() && newRetrievedModelIt.hasNext())
			assertEquals(retrievedModelIt.next().getId(),
					newRetrievedModelIt.next().getId());
		
		/*
		 * Checks the equality of the last element 
		 * of newModel -supposedly the last model 
		 * inserted - with the expected name
		 */
		
		Model model = newRetrievedModel.get(newRetrievedModel.size() - 1);
		int modelId = model.getId(); // saves the id for further computation
		
		assertEquals(model.getName(), s);
		assertEquals(model.getPath(), "");	
		
		/*
		 * Removes the inserted model from the table
		 */
		
		assertTrue(db.removeFromTable(s, fileType));
		
		/*
		 * After the removal, the models' array must be reset,
		 * i.e. decreased by one and totally equal to the 
		 * first array of model, "model"
		 */
		
		newRetrievedModel = db.retrieveFromTable(fileType);
		assertEquals(retrievedModel.size(), newRetrievedModel.size());
		
		Iterator<Model> retrievedModelIter = retrievedModel.iterator();
		Iterator<Model> newRetrievedModelIter = newRetrievedModel.iterator();
		
		while(retrievedModelIter.hasNext() && newRetrievedModelIter.hasNext())
			assertEquals(retrievedModelIter.next().getId(),
					newRetrievedModelIter.next().getId());
	
		
		/*
		 * Tries to remove a non existing configuration
		 */
		
		assertFalse(db.removeFromTable("nonExistingConfig", fileType));
		
		/*
		 * Checks that all the associated configuration 
		 * values (if any) have been deleted, too
		 */
		
		assertTrue(db.retrieveConfigurationValues(String.valueOf(modelId)).size() == 0);
		
		/*
		 * Unclicks all the external classifiers, and then checks
		 * that no configuration can be inserted or retrieved
		 */
		
		ArrayList<Model> retrievedExternalClassifier = db
				.retrieveFromTable(FileType.EC);
		
		assertTrue(retrievedExternalClassifier.size() > 0);
		
		db.updateClicked("*", 0, FileType.EC);
		
		db.insertIntoTable("aTestConfiguration", "", fileType);
		
		assertEquals(db.retrieveFromTable(fileType).size(), 0);
			
		db.updateClicked(retrievedExternalClassifier.get(0).getName(), 1, FileType.EC);
		
	}
	
	
	@Test
	public final void testConnection() throws Exception {
		/*
		 * Closes the current connection
		 */
		
		db.closeConnection();
		
		/*
		 * If invalid, restores a proper connection to the database
		 */
		
		db.connectionValidator();
		
		/*
		 * If the following statement fails, the 
		 * connection has not been properly reset
		 */
		
		db.retrieveFromTable(fileType);
	}

	
	
	@Test
	public final void testGetClickedModels() throws Exception {
		
		String configName = "GetClickedModelsConfigtest";
		
		/*
		 * Retrieves the current array of models
		 */
		
		ArrayList<Model> checkedModel = db.getClickedModels(fileType);
		
		/*
		 * Inserts a new model into the database and clicks it
		 */
		
		db.insertIntoTable(configName, "", fileType);
		db.updateClicked(configName, 1, fileType);
		
		/*
		 * Retrieves the new array of models, which 
		 * must contain the newly inserted element,
		 * which must be characterized by a consistent
		 * clicked state
		 */
		
		ArrayList<Model> newCheckedModel = db.getClickedModels(fileType);
		
		/*
		 * Checks that the size of the fileType array 
		 * of models has been increased by one
		 */
		
		int expectedNumberOfModel = checkedModel.size() + 1;
		int effectiveNumberOfModel = newCheckedModel.size();
		assertEquals(effectiveNumberOfModel, expectedNumberOfModel);
		
		/*
		 * Checks the consistency of the clicked state 
		 * of the current models with the old ones
		 */
		
		for (int i = 0; i < effectiveNumberOfModel; i++) {
			assertEquals(newCheckedModel.get(i).getName(), checkedModel.get(i).getName());
			assertEquals(newCheckedModel.get(i).getClicked(), checkedModel.get(i).getClicked());
		}
		
		assertTrue(newCheckedModel.get(newCheckedModel.size() - 1).getClicked());	
	}
	
	@Test
	public final void testInsertRetrieveConfigurationValues() throws Exception {
		/*
		 * Creates a new configuration and inserts it into the database
		 */
		
		String configName = "InsertRetrieveConfigtest";
		db.insertIntoTable(configName, "", fileType);
		
		/*
		 * Retrieves the id of the configuration
		 */
		
		String idConfig = "";
		ArrayList<Model> retrievedModel = db.retrieveFromTable(fileType);
		Iterator<Model> modelIterator = retrievedModel.iterator();
		while(modelIterator.hasNext()) {
			Model mod = modelIterator.next();
			if (mod.getName().equals(configName))
				idConfig = String.valueOf(mod.getId());
		}
		
		assertTrue(!idConfig.equals(""));
		
		/*
		 * Creates and fill a new ArrayList<NumericElement>
		 * with some Variables and Parameters
		 */
		
		ArrayList<NumericElement> element = new ArrayList<NumericElement>();
		element.add(new Variable("variable1", 1, 7, 1));
		element.add(new Parameter("parameter1", 10));
		element.add(new Variable("variable2", 0, 2, 0.25f));
		
		/*
		 * Inserts the values of the configuration
		 * into the database
		 */
		
		db.insertConfigurationValues(String.valueOf(idConfig), 
				element);
		
		/*
		 * Retrieves the configuration values inserted
		 */
		
		ArrayList<NumericElement> retrievedElement =
				db.retrieveConfigurationValues(idConfig);
		
		/*
		 * Checks the consistency of the inserted numeric element
		 * with the retrieved ones
		 */
		int elementSize = element.size();
		int retrievedElementSize = retrievedElement.size();
		assertEquals(elementSize, retrievedElementSize);
		for (int i = 0; i < element.size(); i++) 
			assertTrue(element.get(i).toString()
					.equals(retrievedElement.get(i).toString()));		
	}
	
	
	
}
