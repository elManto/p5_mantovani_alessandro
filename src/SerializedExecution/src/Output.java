/*
 * Author: Roberto Ronco, Alessandro Mantovani
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



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class handles the total output data which are produced
 * after the serialised execution of an EC (External Classifier)
 */

public class Output {
	private String outputFileFolder;
	private HashMap<String, ArrayList<String>> mapOfValues;
	
	public Output() {
		this.mapOfValues = new HashMap<String, ArrayList<String>>();
		this.outputFileFolder = null;
	}
	
	public void setMapOfValues(HashMap<String, ArrayList<String>> mapOfValues) {
		this.mapOfValues = mapOfValues;
	}

	public HashMap<String, ArrayList<String>> getMapOfValues() {
		return mapOfValues;
	}

	public String getOutputFileFolder() {
		return this.outputFileFolder;
	}
	
	public void setOutputFileFolder(String outputFileFolder) {
		this.outputFileFolder = outputFileFolder;
	}

	
	/**
	 * This method manages the process of writing results of the serialised
	 * execution of an EC into a structured document located at path
	 * "outputFileFolder" and named "output.txt" 
	 * 
	 * @param val
	 * @param sortedEntries
	 * @throws IOException 
	 */
	
	public void writeToFile() throws IOException{
		File f = new File(outputFileFolder + File.separator + "output.txt");
		f.createNewFile();
		
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		for (Entry<String,ArrayList<String>> entry : this.mapOfValues.entrySet()){
			writer.println(entry.getKey());
			for (String line : entry.getValue()) {
				writer.println(line);
			}
			writer.println();
		}
		writer.close();
	}
}
