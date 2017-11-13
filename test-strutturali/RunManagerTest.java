package alessandromantovanitest_strutturali;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.Database;
import alessandromantovani.FileType;
import alessandromantovani.RunManager;
import util.DatabaseCreator;
import util.DatabaseTestUtil;

/**
 * DatabaseCreator.fillDatabase() fills the database with multiple 
 * externalClassifiers, train sets, test sets, and configurations, 
 * as confirmed by DatabaseTestUtil.printDatabaseData(database).
 * Moreover, one model of each class of models is clicked for the
 * serialized execution.
 * 
 * Hence, the aim of this module is to test the serialized execution 
 * with all the possible input combinations by marking multiple
 * subsets of the models available in the database as clicked or
 * unclicked, and adding and clicking models characterized by an
 * invalid path.
 */

public class RunManagerTest {

	private static Database database;
	private static DatabaseCreator databaseCreator;	
	private static RunManager runManager;
	
	private static boolean closeFlag;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String databaseName = "runManagerTest.sqlite";
		databaseCreator = new DatabaseCreator(databaseName);
		databaseCreator.create();
		databaseCreator.fillDatabase();
		
		String databasePath = "jdbc:sqlite:db" + File.separator + databaseName;
		database = new Database(databasePath);
		runManager = new RunManager(database);		
		runManager.setOutputFileFolder(new File("").getAbsolutePath() 
				+ File.separator + "data" + File.separator + "output");
		
		closeFlag = false;
		
		System.out.println("\nRunManagerTest\n\n");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		database.closeConnection();
		databaseCreator.delete();
	}
	
	@After
	public void tearDown() throws Exception{
		//scheduleAutoAnswerJOptionPane();
	}

	@Test
	public final void testGetSetOutputSet() {
		assertNotNull(runManager.getOutputSet());
	}	
	
	/**
	 * @throws Exception
	 */
	
	@Test
	public final void testRun() throws Exception {
		/*
		 * Unsets all the EC's and launch execution.
		 * This must result in an error.
		 */
		
		database.updateClicked("*", 0, FileType.EC);
		scheduleAutoAnswerJOptionPane();
		runManager.run();	
		assertTrue(closeFlag);
		closeFlag = false;
		
		/*
		 * Sets all the EC's and launch execution.
		 * This must result in an error.
		 */
		
		database.updateClicked("*", 1, FileType.EC);
		scheduleAutoAnswerJOptionPane();
		runManager.run();	
		assertTrue(closeFlag);
		closeFlag = false;
		
		/*
		 * Restore initial EC settings
		 */
		
		database.updateClicked("*", 0, FileType.EC);
		database.updateClicked("EC1", 1, FileType.EC);
		
		/*
		 * Unset all the configurations and launch execution.
		 * This must result in an error.
		 */
		
		database.updateClicked("*", 0, FileType.CONFIGURATION);
		scheduleAutoAnswerJOptionPane();
		runManager.run();	
		assertTrue(closeFlag);
		closeFlag = false;
		
		/*
		 * Set all the configurations and launch execution.
		 * This must result in an error.
		 */
		
		database.updateClicked("*", 1, FileType.CONFIGURATION);
		scheduleAutoAnswerJOptionPane();
		runManager.run();
		assertTrue(closeFlag);
		closeFlag = false;
		
		/*
		 * Restore initial configuration settings
		 */
		
		database.updateClicked("*", 0, FileType.CONFIGURATION);
		database.updateClicked("Configuration EC 1 0", 1, FileType.CONFIGURATION);
		
		/* 
		 * Insert and set an invalid model, then launch execution
		 */
		
		database.insertIntoTable("InexistentTrain", "InexistentTrainPath", FileType.TRAIN);
		database.insertIntoTable("InexistentTest", "InexistentTestPath", FileType.TEST);
		database.updateClicked("InexistentTest", 1, FileType.TEST);
		scheduleAutoAnswerJOptionPane();
		runManager.run();
		assertTrue(closeFlag);
		closeFlag = false;
		
		/*
		 * Restore a consistent input set by unclicking the inexistent test set
		 */
		
		database.updateClicked("InexistentTest", 0, FileType.TEST);
		
		/*
		 * Launch the execution with a correct configuration,
		 */
		
		
		runManager.run();
	}

	@Test
	public final void testGetSetOutputFilePath() {
		String path = "testOutputPath";
		runManager.setOutputFileFolder(path);
		assertTrue(runManager.getOutputFileFolder().equals(path));
		
		/*
		 * Restores the valid output path
		 */
		
		runManager.setOutputFileFolder(new File("").getAbsolutePath() 
				+ File.separator + "data" + File.separator + "output");
	}

	@Test
	public final void testToString() {
		assertNotNull(runManager.toString());
	}

	private void scheduleAutoAnswerJOptionPane(){
		TimerTask timerTask = new TimerTask() {
	        @Override
	        public void run() {
	        	java.awt.Window[] windows = java.awt.Window.getWindows();
	            for (java.awt.Window window : windows) {
	                if (window instanceof JDialog) {
	                    JDialog dialog = (JDialog) window;
	                    if (dialog.getContentPane().getComponentCount() == 1
	                        && dialog.getContentPane().getComponent(0) instanceof JOptionPane) {
	                    	JOptionPane op = (JOptionPane) dialog.getContentPane().getComponent(0);
	                    	op.setValue(JOptionPane.OK_OPTION);
	                    	closeFlag = true;
	                    }
	                }
	            }
	        }
	    };
	
	    Timer timer = new Timer("MyTimer");
	    timer.schedule(timerTask, 2000);
	}
}
