	package alessandromantovanitest_funzionali;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.FileType;
import alessandromantovani.Model;
import util.DatabaseCreator;
import alessandromantovanitest_funzionali.MainLauncher;

public class UC10 {

	private static DatabaseCreator testDB;
	private static MainLauncher ml;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testDB = new DatabaseCreator("testDB");
		testDB.create();
		testDB.fillDatabase();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ml.closeDBConnection();
		testDB.delete();
	}

	@Test
	public void test() throws Exception {
		// starting UC10 test - main scenario
		System.out.println("Running UC10 test...");
		ml = new MainLauncher();
		ml.start();
		/* 
		 * Going to obtain current clicked configuration. Clicked means that it will be 
		 * used to run the serialized execution. Can be null if no configuration is 
		 * clicked.  
		 */
		ArrayList<Model> currentConfiguration = ml.getClicked(FileType.CONFIGURATION);
		
		/* 
		 * Going to execute a function to mark as clicked a different configuration
		 * from the one contained in currentConfiguration (if any). 
		 */		
		
		ml.testClickedConfiguration();
		ArrayList<Model> clickedConfiguration = ml.getClicked(FileType.CONFIGURATION);

		
		/* 
		 * At this point, if no configuration is clicked, the test MUST fail, because
		 * surely testClickedConfiguration() did not work.  
		 */
		
		assertTrue(clickedConfiguration.size() == 1);
		
		/* 
		 * The configuration clicked MUST be different from the one contained in 
		 * currentConfiguration.
		 */
		assertFalse(currentConfiguration.get(0) == clickedConfiguration.get(0));
		System.out.println("UC10 test successfully done.");
	}

}
