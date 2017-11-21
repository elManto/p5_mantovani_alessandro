import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class FileManagerTest {
	private static FileManager fm;
	private static Database db;
	private static DatabaseCreator dbCreator;
	
	private static final FileType fileType = FileType.CONFIGURATION;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/*
		 * Instances the needed objects and connects to
		 * the database.
		 */
		String dbName = "FileManagerTest.sqlite";
		
		dbCreator = new DatabaseCreator(dbName);
		dbCreator.create();
		dbCreator.fillDatabase();
		
		String dbPath = "jdbc:sqlite:db" + File.separator + dbName;
		db = new Database(dbPath);

		fm = new FileManager(db);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dbCreator.delete(); //Deletes the testing database
		
		db.closeConnection(); //Closes connection with real db
	}
	
	@Test
	public final void testGetElement() {
		/*
		 * Test the getter of FileManager class.
		 * If an inexistent index is required we get a null value.
		 */
		int configurations = fm.getArraySize(fileType);
		
		assertNull(fm.getElement(fileType, -1));	//In both cases we get null
		assertNull(fm.getElement(fileType, configurations));
		
		/*
		 * If a valid index is required we expect to obtain
		 * a value which is different from null.
		 */
		for (int i = 0; i < configurations; i++)
			assertNotNull(fm.getElement(fileType, i));
	}
	
	
	@Test
	public final void testClicked() throws Exception {
		
		/*
		 * First, we deselect all the configurations
		 */

		int configurations = fm.getArraySize(fileType);
		
		int configIndex = 0;
		while(configIndex < configurations) {
			fm.setClicked(fileType, configIndex, false);
			configIndex++;
		}
		
		
		/*
		 * The invocation to getClicked() must return null because
		 * all the configurations are not clicked
		 */
		assertNull(fm.getClicked(fileType));
		
		
		// A configuration has been clicked(... 
		fm.setClicked(fileType, 0, true);
		
		/* ...so, we expect that an invocation to getClicked() 
		 * return a initialized ArrayList<Model>, with
		 * its first element that results as clicked.
		 */
		ArrayList<Model> checkedConfig = fm.getClicked(fileType);
		assertNotNull(checkedConfig);
		assertTrue(checkedConfig.get(0).getClicked());
		
		/*
		 * Reset to the previous state. Now we restore
		 * the configuration with index "0" as not clicked.
		 */
		
		fm.setClicked(fileType, 0, false);
		
		// Two configurations are clicked...
		fm.setClicked(fileType, configurations, true);
		int negativeConfigIndex = -1;
		fm.setClicked(fileType, negativeConfigIndex, true);
		
		/*
		 * ...but both the clicked configuration doesn't exist because
		 * the selected index ("configuration" and "negativeConfigIndex")
		 * are not valid since they are out of bounds. So this call fails
		 * and return null (no configurations clicked).
		 */
		
		assertNull(fm.getClicked(fileType));
	}
	
	
	@Test
	public final void testGetIterator() {
		/*
		 * Verify that the iterator returned by the "getIterator()"
		 * method is not null.
		 */
		assertNotNull(fm.getIterator(fileType));
	}

	
	@Test
	public final void testInsertRemove() throws Exception {
		/*
		 * This methods inserts a new configuration and checks
		 * that the insertion is correct.
		 * After that a configuration is removed and the test
		 * is performed.
		 */
		String configName = "InsertRemoveConfigNameTest";
		
		int configurations = fm.getArraySize(fileType);
		
		fm.insert(configName, "", fileType);
		
		/*
		 * After the insertion, the number of configurations is increased
		 */
		int expectedConfigurations = configurations + 1;
		assertEquals(fm.getArraySize(fileType), expectedConfigurations);
		
		Model mod = fm.getElement(fileType, configurations);
		
		assertNotNull(mod);
		assertEquals(mod.getPath(), "");
		assertEquals(mod.getName(), configName);
	
		configurations = fm.getArraySize(fileType);
		
		fm.remove(configName, fileType);
		
		expectedConfigurations = configurations - 1;
		assertEquals(fm.getArraySize(fileType), expectedConfigurations);
	}
	

	@Test
	public final void testGetModelArray() {
		/*
		 * Verify that the extraction of the ArrayList of configuration
		 * is successful. In particular, first we get the ArrayList and then
		 * we get an iterator to access sequentially to it. If when iterator
		 * ends, the counted elements are the same quantity of the 
		 * extracted array size the test can be considered successfull
		 */
		ArrayList<Model> modelArray = fm.getModelArray(fileType);
		Iterator<Model> configurationIterator = fm.getIterator(fileType);
		
		int counter = 0;
		for (int i = 0; i < modelArray.size(); i++) {
			counter++;	//counts the elements (introduced for clarity)
			assertTrue(configurationIterator.hasNext());
			assertEquals(modelArray.get(i), configurationIterator.next());
		}

		// If iterator doesn't have any following node it has finished
		assertFalse(configurationIterator.hasNext());
		assertEquals(modelArray.size(), counter);
	}
	

	@Test
	public final void testGetArraySize() {
		/*
		 * Checks that size of array given by filemanager is the same
		 * if we iterate over it with an iterator.
		 */
		int configurations = fm.getArraySize(fileType);
		
		Iterator<Model> iterator = fm.getIterator(fileType);
		int configurationCounter = 0;
		
		while (iterator.hasNext()) {
			configurationCounter++;
			iterator.next();
		}
		
		// The two sizes are the same
		assertEquals(configurationCounter, configurations);
	}
	
	
	@Test
	public final void testGetIdByName() throws Exception {
		/*
		 * First, we insert a configuration with name indicated by the
		 * string "configName".
		 * After that, it gets the id of the just inserted 
		 * configuration from the table "CONFIGURATION_TABLE". 
		 */
		String configName = "GetIdByNameConfigTest";
		
		fm.insert(configName, "", fileType);
		
		ArrayList<Model> modelArray = fm.getModelArray(fileType);
		
		int index = 0;
		for (Model m : modelArray) {
			if (m.getName().equals(configName))
				break;
			index++;
		}
		
		assertTrue(index < modelArray.size());
		
		/*
		 * Verify that the id of modelArray is the same of the id returned
		 * by getIdByName()
		 */
		assertEquals(fm.getIdByName(configName, fileType),
					modelArray.get(index).getId());
		/*
		 * If the parameter name is not contained in table the getIdByName()
		 * method returns -1
		 */
		assertEquals(fm.getIdByName("nonExistingConfiguration", fileType), -1);
	}
	
	
	@Test
	public final void testUpdateModelData() throws Exception {
		/*
		 * Extracts the values contained in the table 
		 * "CONFIGURATION_TABLE" and saves them into the ArrayList
		 * modelArray. Then set the "clicked" attribute of the 
		 * model indicated by the index 0 of FileType
		 * "fileType" to true. After that updates fileManager
		 * and perform another extraction of the ArrayList.
		 * If the first ArrayList and the updated ArrayList are the 
		 * same the test ends successfully.
		 */
		ArrayList<Model> modelArray = fm.getModelArray(fileType);
		
		// Clicks the array with index 0
		fm.setClicked(fileType, 0, true);
		// Updates the fileManager
		fm.updateModelData(fileType);
		
		ArrayList<Model> updatedModelArray = fm.getModelArray(fileType);

		/*
		 * Verify that the two ArrayList<Model> are the same
		 */
		int sizeBeforeUpdate = modelArray.size();
		int sizeAfterUpdate = updatedModelArray.size();
		assertEquals(sizeBeforeUpdate, sizeAfterUpdate);
		
		Iterator<Model> modelArrayIt = modelArray.iterator(); 
		Iterator<Model> updatedModelArrayIt = updatedModelArray.iterator();
		while(modelArrayIt.hasNext() && updatedModelArrayIt.hasNext())
			assertTrue(updatedModelArrayIt.next().getName()
					.equals(modelArrayIt.next().getName()));
		
		/*
		 * Test if the "checked" attribute is true as 
		 * previously set. 
		 */
		assertTrue(updatedModelArray.get(0).getClicked());
	}
	
	
}
