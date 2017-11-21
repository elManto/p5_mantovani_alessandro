/*
 * Author: Roberto Ronco
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



import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The Main Class initializes the graphic interface. Moreover, it creates the 
 * objects to carry out the operations needed for the application to work.
 */

public class Main {
	private static FileManager manager;
	private static ConfigurationManager configManager;
	private static RunManager rm;
	private static Database db;
	
	private static Window mainView;
	
	private static AtomicBoolean ready = new AtomicBoolean(false);

	public static void main(String[] args) {
		
		String name = "database.sqlite";
		
		if(args.length > 0 && args[0].equals("debug"))
			name = "testDB";
		/* 
		 * Instantiates the connection to the database,
		 * and undertakes proper actions in case of failure
		 */
		String databasePath = "jdbc:sqlite:db" + File.separator + name ;
		
		try {
			db = new Database(databasePath);
			manager = new FileManager(db);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return;
		}
		/*
		 * Creates the objects needed for 
		 * the application to work properly
		 */
		rm = new RunManager(db);
		configManager = new ConfigurationManager(db);
		configManager.attach(new Writer());
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main.mainView = new Window(manager, rm, configManager);
					Main.mainView.initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		ready.set(true);
	}
	
	/**
	 * The following methods have been introduced for testing
	 * purpose
	 */
	
	public static int getModelQuantity(FileType fileType) throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		return manager.getArraySize(fileType);
	}
	
	
	public static void setOutputFileFolder(String path) throws InterruptedException{
		while (!ready.get() && Main.rm.getOutputFileFolder() == null){
			Thread.sleep(1000);
		}
		Main.rm.setOutputFileFolder(path);
	}
	
	public static void testSerializedExecution() throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		Main.mainView.testSerializedExecution();
	}
	
	public static void testFaultySerializedExecution() throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		Main.mainView.testFaultySerializedExecution();
	}
	
	public static ArrayList<NumericElement> getConfigurationParameters(String name) throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		ArrayList<Model> configuration = manager
				.getModelArray(FileType.CONFIGURATION);
		for(int i = 0; i < configuration.size(); i++){
			if(configuration.get(i).getName().equals(name))
				return db.retrieveConfigurationValues(
						String.valueOf(configuration.get(i).getId()));		
		}
		return null;
		
	}
	
	public static ArrayList<Model> getClicked(FileType fileType) throws InterruptedException {
		while (!ready.get()){
			Thread.sleep(1000);
		}
		return manager.getClicked(fileType);
	}
	
	public static int getModelNumber(FileType fileType) throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		return manager.getArraySize(fileType);
	}
	
	public static void testClickedConfiguration() throws InterruptedException{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		Thread.sleep(5000);
		mainView.testClickedConfiguration();
	}
	
	public static void testConfigurationValueDelete() throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		Thread.sleep(1000);
		mainView.testConfigurationValueDelete();
	}
	
	public static ArrayList<NumericElement> retrieveConfigurationValues() throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		return db.retrieveConfigurationValues(String.valueOf(1));
	}
	
	public static void testConfigurationValueAdd(ArrayList<String> toBeAdd) throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		Thread.sleep(1000);
		mainView.testConfigurationValueAdd(toBeAdd);
	}
	
	
	public static void closeDBConnection() throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		db.closeConnection();
	}
	
	public static void updateFileManagerModels() throws Exception{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		manager.updateAllModelData();
	}
	
	public static ArrayList<Component> getAllComponents(Container c)
			throws InterruptedException{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		return mainView.getAllComponents(c);
	}
	
	public static JFrame getConfigurationFrame() throws InterruptedException{
		while (!ready.get()){
			Thread.sleep(1000);
		}
		return mainView.getConfigurationFrame();
	}
}
