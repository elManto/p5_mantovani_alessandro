import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.JFrame;



public class MainLauncher extends Thread {

	public void run(){
		try {
			String[] args = new String[1];
			args[0] = "debug";
			Main.main(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Model> getClicked(FileType fileType) throws InterruptedException{
		
		return Main.getClicked(fileType);
	}
	
	
	public int getModelNumber(FileType fileType) throws Exception{
		return Main.getModelNumber(fileType);
	}
	
	public void testClickedConfiguration() throws Exception{
		Main.testClickedConfiguration();
	}
	
	public void testConfigurationValueDelete() throws Exception{
		Main.testConfigurationValueDelete();
	}
	
	public void testConfigurationValueAdd(ArrayList<String> toBeAdd) throws Exception{
		Main.testConfigurationValueAdd(toBeAdd);
	}
	
	public void setOutputFileFolder(String path) throws InterruptedException{
		Main.setOutputFileFolder(path);
	}
	
	public void testSerializedExecution() throws Exception{
		Main.testSerializedExecution();
	}
	
	public void testFaultySerializedExecution() throws Exception{
		Main.testFaultySerializedExecution();
	}
	
	public ArrayList<NumericElement> retrieveConfigurationValues() throws Exception{
		return Main.retrieveConfigurationValues();
	}
	
	public void updateFileManagerModels() throws Exception{
		Main.updateFileManagerModels();
	}
	
	public void closeDBConnection() throws Exception{
		Main.closeDBConnection();
	}
	
	public ArrayList<Component> getAllComponents(Container c) throws InterruptedException{
		return Main.getAllComponents(c);
	}
	
	public JFrame getConfigurationFrame() throws InterruptedException{
		return Main.getConfigurationFrame();
	}
}
