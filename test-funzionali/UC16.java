import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class UC16 {

	private static DatabaseCreator testDatabase;
	private static MainLauncher ml;
	private static final int sleepTime = 5000;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testDatabase = new DatabaseCreator("testDB");
		testDatabase.create();
		testDatabase.fillDatabase();
		
		ml = new MainLauncher();
		ml.start();
		Thread.sleep(sleepTime);
		ml.setOutputFileFolder(new File("").getAbsolutePath());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ml.closeDBConnection();
		testDatabase.delete();
		File output = new File("output.txt");
		if (output.exists()){
			System.out.print("\nTearing down... ");
			if (output.delete())
				System.out.println("Output file successfully deleted.");
			else
				System.out.println("Problem deleting output file.");
		}
	}
	
	@Test
	public void test() throws Exception {
		
		// Starts UC16 - main scenario
		
		String folderPath = new File("").getAbsolutePath();
		Thread.sleep(sleepTime);
		
		// Starts the serialized execution with a correct input set
		
		ml.setOutputFileFolder(folderPath);
		ml.testSerializedExecution();
		assertTrue(new File(folderPath + File.separator + "output.txt").exists());
		
		File output = new File("output.txt");
		if (output.exists()){
			if (!output.delete())
				System.out.println("Problem deleting output file. Further testing"
						+ " might be faulty.");
		}
		
		System.out.println("UC16 test - main scenario completed.");
		
		// Starts UC16 - alternative scenario 2a
		
		testDatabase.query("INSERT INTO TEST_SET_TABLE VALUES"
				+ " (NULL,'invalidTestSet','/nonExistingPath', 1)");
		ml.updateFileManagerModels();
		
		Thread.sleep(sleepTime);
		
		ml.testFaultySerializedExecution();
		assertTrue(!new File(folderPath + "output.txt").exists());
		
		// Restores a consistent state in the ACO system
		
		testDatabase.query("DELETE FROM TEST_SET_TABLE WHERE NAME = 'invalidTestSet'");
		ml.updateFileManagerModels();
		
		System.out.println("UC16 test - alternative scenario 2a completed.");
		
		// Starts UC16 - alternative scenario 3a
		
		// Gets and sets an illegal output file folder path
		
		String illegalPath = retrieveIllegalPath();
		ml.setOutputFileFolder(illegalPath);
		
		Thread.sleep(sleepTime);
		
		// Starts the serialized execution with the faulty output path
		
		ml.testSerializedExecution();
		assertTrue(!new File(illegalPath + File.separator + "output.txt").exists());
		
		System.out.println("UC16 test - alternative scenario 3a completed.");
		
		// Starts UC16 - alternative scenario 5a
		
		testDatabase.query("UPDATE TRAIN_SET_TABLE SET CHECKED = 0 "
				+ "WHERE ID = "
				+ "(SELECT ID FROM TRAIN_SET_TABLE WHERE CHECKED = 1)");
		ml.updateFileManagerModels();
		
		assertTrue(ml.getClicked(FileType.TRAIN) == null);
		ml.setOutputFileFolder(folderPath);
		
		Thread.sleep(sleepTime);
		ml.testSerializedExecution();
		
		assertTrue(new File(folderPath + File.separator + "output.txt").exists());
		
		System.out.println("UC16 test - alternative scenario 5a completed.");
	}
		
	private String retrieveIllegalPath(){
		if (System.getProperty("os.name").toLowerCase().contains("windows"))
			return "C:"+ File.separator +"Windows" + File.separator + "System32";
		else
			return File.separator;
	}
}
