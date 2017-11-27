import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;



@RunWith(value = Parameterized.class)
public class SerializerTest {
	private static ArrayList<NumericElement> numericElement;
	private static Database database;
	private static String externalClassifierPath;
	private static DatabaseCreator databaseCreator;
	private static Serializer serializer;
	
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
        	{1,   0,  2, 1, -2,   0,  2},
        	{2,  -1,  1, 1,  3,   5,  4},    
        	{3,   1,  2, 1, -5, 6.5f, 4}
        });
    }
    
    @Parameter(value = 0)
    public int index;
    @Parameter(value = 1)
    public float start1;
    @Parameter(value = 2)
    public float end1;
    @Parameter(value = 3)
    public float step1;
    @Parameter(value = 4)
    public float start2;
    @Parameter(value = 5)
    public float end2;
    @Parameter(value = 6)
    public float step2;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		externalClassifierPath = "data" + File.separator 
				+ "ec" + File.separator + "EC1.jar";
		
		String databaseName = "db_ser.sqlite";
		
		databaseCreator = new DatabaseCreator(databaseName);
		databaseCreator.create();
		database = new Database("jdbc:sqlite:db" + File.separator + databaseName);
		
		System.out.println("Starting the parametrized tests for the Serializer"
				+ " (note that it might take a long time)");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		database.closeConnection();
		databaseCreator.delete();
	}

	@Before
	public void setUp() throws Exception {
		numericElement = new ArrayList<NumericElement>();
	}

	/**
	 * Testing the retrieval of all the command lines 
	 * for a specified input configuration and the 
	 * requirement of the execution of each one.
	 * 
	 * @return HashMap<String, ArrayList<String>>
	 */
	
	@Test
	public final void testRun() throws IOException {
		
		System.out.println("Executing test " + index + "/3 for the Serializer...");
		
		/*
		 * Creates two variables and two parameters and fills 
		 * an ArrayList<NumericElement> for insertion
		 */
		
		numericElement = new ArrayList<NumericElement>();
		Variable testVariable1 = new Variable("testVariable1", start1, end1, step1);
		Variable testVariable2 = new Variable("testVariable2", start2, end2, step2);
		Param testParameter1 = 
				new Param("testParameter1", 5);
		Param testParameter2 = 
				new Param("testParameter2", 8);
		
		/*
		 * Computes the possible values of the two 
		 * variables within their range of variation
		 */
		
		int testVariable1PossibleValues = 
				(int) Math.ceil(((testVariable1.getEnd() - testVariable1.getStart()) 
						/ testVariable1.getStep()) + 1);
		
		int testVariable2PossibleValues = 
        		(int) Math.ceil(((testVariable2.getEnd() - testVariable2.getStart()) 
        				/ testVariable2.getStep()) + 1);
		
		/*
		 * Computes the total combinations of all the 
		 * possible values of the two variables combined
		 */
		
        int totalCombinations = testVariable1PossibleValues * 
        		testVariable2PossibleValues;	
        
        /*
         * Adds the variables and the parameters to 
         * ArrayList<NumericElement> numericElement
         */
        
        numericElement.add(testVariable1);
        numericElement.add(testVariable2);
        numericElement.add(testParameter1);
        numericElement.add(testParameter2);
        
        /* 
         * Creates and fills an ArrayList for the test sets,
         * and one for the train sets
         */
       
        ArrayList<String> testSet = new ArrayList<String>();      
        ArrayList<String> trainSet = new ArrayList<String>();
        
        testSet.add("data" + File.separator + "test1.txt");
        testSet.add("data" + File.separator + "test2.txt");
        testSet.add("data" + File.separator + "test3.txt");     
        
        trainSet.add("data" + File.separator + "train1.txt");
		trainSet.add("data" + File.separator + "train2.txt");
        
        /*
         *  Tests the Serializer with no train set  
         */
		
        serializer = new Serializer(numericElement, externalClassifierPath, 
        		testSet, new ArrayList<String>());
		HashMap<String, ArrayList<String>> map = serializer.run();	
		
		/*
		 * The resulting HashMap must be not null, it must contain
		 * all the train sets used and its size must be the number 
		 * of the test sets times the number of the total combinations
		 * of variables
		 */
		
		assertNotNull(map);
		for (String ts : testSet)
			assertTrue(map.toString().contains(ts));
		assertEquals(map.size(), testSet.size() * totalCombinations);		
		
		/*
		 * Tests the Serializer with at least a train set		
		 */	
		
		serializer = new Serializer(numericElement, externalClassifierPath, 
				testSet, trainSet);
		map = serializer.run();	
		
		/*
		 * The resulting HashMap must be not null, it must contain
		 * all the test sets and the train sets used and its size 
		 * must be the number of the test sets times the number of
		 * the train sets times the number of the total combinations
		 * of variables
		 */
		
		assertNotNull(map);
		for (String ts : testSet)
			assertTrue(map.toString().contains(ts));
		for (String ts : trainSet)
			assertTrue(map.toString().contains(ts));
		
		assertEquals(totalCombinations * testSet.size() * trainSet.size(), map.size());
	}
}
