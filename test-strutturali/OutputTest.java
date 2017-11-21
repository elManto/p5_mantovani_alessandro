import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.Output;

public class OutputTest {
	private static Output output;
	private static String outputFolder;

	private static HashMap<String, ArrayList<String>> map;
	private static Scanner scanner;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		output = new Output();
		outputFolder = "data" + File.separator + "output";
		map = new HashMap<String, ArrayList<String>>();
		scanner = null;
	}

	@AfterClass
	public static void tearDownAfterClass() {
		scanner.close();
	}

	@Test
	public void testGetSetOutputFileFolder() {
		output.setOutputFileFolder(outputFolder);
		assertEquals(outputFolder, output.getOutputFileFolder());
	}

	@Test
	public void testGetSetMapOfValues() {
		output.setMapOfValues(map);
		assertTrue(output.getMapOfValues().equals(map));
	}

	/**
	 * 
	 * @throws IOException
	 */

	@Test
	public void testWriteToFile() throws IOException {
		output.setOutputFileFolder(outputFolder);

		/*
		 * Fills the <command, array of answers>  HashMap "map",
		 * used to contain the data which, in an actual execution 
		 * of the software, is supposedly produced as an output
		 * by a RunManager instance.
		 * 
		 * First, it initializes the array of the paths of the models
		 */

		String ecPath = "data" + File.separator + "ec" + File.separator + "EC1.jar";
		String[] trainPath = new String[] { "data" + File.separator + "train1.txt",
				"data" + File.separator + "train2.txt" };
		String[] testPath = new String[] { "data" + File.separator + "test1.txt",
				"data" + File.separator + "test2.txt" };
		
		/*
		 * Builds the two test commands, initializes an array of answers
		 * associated to each of them, and inserts the two new entries 
		 * <command, array of answers> into the output HashMap "map" 
		 */
		
		String command1 = "java -jar " + ecPath + " 2.0 7.0 7.0 8.0 " 
				+ trainPath[0] + " " + testPath[0];

		ArrayList<String> value = new ArrayList<String>();
		value.add("0 no");
		value.add("1 no");

		map.put(command1, value);

		String command2 = "java -jar " + ecPath + " 6.0 6.0 7.0 8.0 " 
				+ trainPath[1] + " " + testPath[1];

		value = new ArrayList<String>();
		value.add("0 yes");
		value.add("1 no");

		map.put(command2, value);
		
		/*
		 * Sets the initialized map as the output map of values
		 */
		
		output.setMapOfValues(map);

		System.out.println(map);
		
		/*
		 * Writes the output data to an output file
		 */

		output.writeToFile();

		/*
		 * Checks the existence of the output file
		 */

		File out = new File(outputFolder + File.separator + "output.txt");
		assertTrue(out.exists());
		scanner = new Scanner(out);

		
		/*
		 * Checks whether the expected output matches the actual output
		 * by reading a line at a time from the actual output file and 
		 * comparing each read line with each element of the expected output.
		 * 
		 * If all the element match, the expected output is consistent
		 * with the actual output and thus the test is passed.
		 */

		ArrayList<String> actualOutput = new ArrayList<String>();
		while (scanner.hasNextLine())
			actualOutput.add(scanner.nextLine());
		
		int count = -1, ansIndex = 0;
		while (++count < actualOutput.size()) {
			String command = actualOutput.get(count);
			ArrayList<String> answer = map.get(command);
			++count;
			while (!actualOutput.get(count).equals("")) {
				assertTrue(actualOutput.get(count).equals(answer.get(ansIndex)));
				++ansIndex;
				++count;
			}
			ansIndex = 0;
		}
	}
}
