import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import util.DatabaseCreator;
import javax.swing.JTextField;

public class UC12 {

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
		/*
		 *  Starting UC12 test - main scenario
		 */
		
		System.out.print("Running UC12 test - main scenario... ");
		ml = new MainLauncher();
		ml.start();
		
		ArrayList<Component> components = ml.getAllComponents(
				ml.getConfigurationFrame());
		
		for(Component component : components ){
			if(component instanceof JTextField){
				Rectangle r = component.getBounds();
				System.out.println(r);
				if(r.x == 124){
					((JTextField) component).setText("118.0");
					Thread.sleep(4000);
				}
			}
		}
		
		System.out.println(" Done.");
		
		
		/*
		 * Starting UC12 test - alternative scenario 3a
		 */
			
		System.out.print("Running UC12 test - alternative scenario 3a... ");
		
		/*
		 * Obtains the number of parameters and variables
		 * of the clicked configuration
		 */
		
		int configurationValues = ml.retrieveConfigurationValues().size();
		
		/*
		 * Runs the deletion test
		 */
		
		ml.testConfigurationValueDelete();
		
		/*
		 * Verifies that the configuration values after deletion
		 * are less than the configuration values before the deletion
		 */
		
		assertTrue(ml.retrieveConfigurationValues().size() < configurationValues);
		System.out.println("Done.");
		
		/*
		 * Updates the current number of configuration values
		 */
		
		configurationValues = ml.retrieveConfigurationValues().size();
		
		/* The following lines test UC12 alternative scenarios 3b and 4a. 
		 * The latter tests how ACO handles invalid 
		 * configuration input (parameters and variables).
		 */
		
		System.out.print("Running UC12 test - alternative scenario 3b and 4a... ");

		for (BuildInvalidValue b : buildInvalidValue) {
			/*
			 * Tests the configuration value add routine with 
			 * an invalid value provided by the interface.
			 */
			
			ml.testConfigurationValueAdd(b.buildValue());
			assertTrue(configurationValues == ml.retrieveConfigurationValues().size());
			
			/*
			 *  Refreshes the current configuration values
			 */
			configurationValues = ml.retrieveConfigurationValues().size();
		}
		
		for (BuildValidValue b : buildValidValue) {
			/*
			 * Tests the configuration value add routine with 
			 * a valid value provided by the interface.
			 */
			
			ml.testConfigurationValueAdd(b.buildValue());
			assertTrue(configurationValues + 1 == ml.retrieveConfigurationValues().size());
			
			/*
			 *  Refreshes the current configuration values
			 */
			configurationValues = ml.retrieveConfigurationValues().size();
		}
		
		System.out.println("Done.\nUC12 test successfully done.");
	}
	
	interface BuildInvalidValue {
		ArrayList<String> buildValue();
	}
	
	private static BuildInvalidValue[] buildInvalidValue = new BuildInvalidValue[] {
		new BuildInvalidValue() { 
			public ArrayList<String> buildValue() {
				return buildInvalidStringParameter();
			}
		},
		new BuildInvalidValue() { 
			public ArrayList<String> buildValue() {
				return buildInvalidStringVariable();
			}
		},
		new BuildInvalidValue() { 
			public ArrayList<String> buildValue() {
				return buildInvalidEndGreaterThanStartVariable();
			}
		},
		new BuildInvalidValue() { 
			public ArrayList<String> buildValue() {
				return buildInvalidNegativeStepVariable();
			}
		},
		new BuildInvalidValue() { 
			public ArrayList<String> buildValue() {
				return buildInvalidOverflowVariable();
			}
		},
		new BuildInvalidValue() { 
			public ArrayList<String> buildValue() {
				return buildInvalidUnderflowVariable();
			}
		},
		new BuildInvalidValue() { 
			public ArrayList<String> buildValue() {
				return buildInvalidStringParameter();
			}
		},
	};
	
	interface BuildValidValue{
		ArrayList<String> buildValue(); 
	}
	
	private static BuildValidValue[] buildValidValue = new BuildValidValue[]{
			new BuildValidValue() { 
				public ArrayList<String> buildValue() {
					return buildValidVariable();
				}
			},
			new BuildValidValue() { 
				public ArrayList<String> buildValue() {
					return buildValidParameter();
				}
			}
	};
	
	private static ArrayList<String> buildInvalidStringParameter(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("invalidParameter1");
		toBeReturn.add("a String");
		return toBeReturn;
	}
	
	private static ArrayList<String> buildInvalidStringVariable(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("invalidVariable1");
		toBeReturn.add("a String");
		toBeReturn.add("a String");
		toBeReturn.add("a String");
		return toBeReturn;
	}
	
	private static ArrayList<String> buildInvalidEndGreaterThanStartVariable(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("invalidVariable2");
		toBeReturn.add(String.valueOf(10));
		toBeReturn.add(String.valueOf(1));
		toBeReturn.add(String.valueOf(10));
		return toBeReturn;
	}
	
	private static ArrayList<String> buildInvalidNegativeStepVariable(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("invalidVariable3");
		toBeReturn.add(String.valueOf(1));
		toBeReturn.add(String.valueOf(10));
		toBeReturn.add(String.valueOf(-1));
		return toBeReturn;
	}
	
	private static ArrayList<String> buildInvalidOverflowVariable(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("invalidVariable4");
		toBeReturn.add(String.valueOf(1));
		toBeReturn.add(String.valueOf(Float.POSITIVE_INFINITY));
		toBeReturn.add(String.valueOf(1));
		return toBeReturn;
	}
	
	private static ArrayList<String> buildInvalidUnderflowVariable(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("invalidVariable5");
		toBeReturn.add(String.valueOf(Float.NEGATIVE_INFINITY));
		toBeReturn.add(String.valueOf(1));
		toBeReturn.add(String.valueOf(1));
		return toBeReturn;
	}
	
	private static ArrayList<String> buildValidVariable(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("Variable1");
		toBeReturn.add(String.valueOf(1));
		toBeReturn.add(String.valueOf(10));
		toBeReturn.add(String.valueOf(1));
		return toBeReturn;
	}
	
	private static ArrayList<String> buildValidParameter(){
		ArrayList<String> toBeReturn = new ArrayList<String>();
		toBeReturn.add("Parameter1");
		toBeReturn.add(String.valueOf(1));
		return toBeReturn;
	}

}
