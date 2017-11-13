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

package serializedexecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The class Serializer retrieves an input configuration and produces 
 * all the combinations of parameters that will be used for the serialized
 * execution. For each combination of parameters a command line is built
 * and is launched.
 */

public class Serializer {
	private int numberOfParameters;
	private float[] start;
	private float[] end;
	private float[] step;
	private ArrayList<String> combinations;
	private ArrayList<String> commands;
	private String command;
	private ArrayList<String> testSet;
	private ArrayList<String> trainSet;
	
	/*
	 * "map" contains as key the command line that will be launched 
	 * and as value an ArrayList<String> that contains the answers
	 * for that command line 
	 */
	private HashMap<String, ArrayList<String>> map;
	
	public Serializer(ArrayList<NumericElement> val, String ecPath,
			ArrayList<String> testSet, ArrayList<String> trainSet) {
		numberOfParameters = val.size();
		start = new float[numberOfParameters];
		end = new float[numberOfParameters];
		step = new float[numberOfParameters];
		combinations = new ArrayList<>();
		commands = new ArrayList<>();
		command = new String("java -jar " + ecPath);
		this.testSet = testSet;
		this.trainSet = trainSet;
		
		map = new HashMap<String, ArrayList<String>>();
		
		for (int index = 0; index < val.size(); index++) {
			if (val.get(index) instanceof Parameter) {
				/*
				 * For constant values (only "start" , without "step")  
				 * you need to have "end" = "start", "step" = 0
				 */
				start[index] = ((Parameter) val.get(index)).getValue();
				end[index] = start[index];
				step[index] = 0;
			} else {
				start[index] = ((Variable) val.get(index)).getStart();
				end[index] = ((Variable) val.get(index)).getEnd();
				step[index] = ((Variable) val.get(index)).getStep();
			}
		}
	}
	
	/**
	 * This method retrieves all the command lines for a specified
	 * input configuration and requires the execution of each one
	 * through the private method "execution()".
	 * 
	 * @return HashMap<String, ArrayList<String>>
	 * @throws IOException 
	 */
	
	public HashMap<String, ArrayList<String>> run() throws IOException {
		
		float[] arr = new float[numberOfParameters];
		float[] tmp = new float[numberOfParameters];

		for (int i = 0; i < arr.length; i++) {
			arr[i] = start[i];
			tmp[i] = start[i];
		}

		String newCommand = command;

		for (int j = 0; j < numberOfParameters; j++) {
			newCommand += " " + arr[j];
		}
		combinations.add(Arrays.toString(arr));
		String[] trainAndTest = generateTrainAndTestCommand();
		String completeCommand = "";
		for(String s : trainAndTest){
			completeCommand = newCommand + s;
			commands.add(completeCommand);
			completeCommand = "";
		}
		
		for (int i = 0; i < arr.length; i++) {
			countAllCombinations(i, arr, tmp);
			arr = tmp.clone();
		}
		
		for (String s : commands)
			this.execution(s);
		
		return this.map;
	}
	
	/**
	 * This method generates the command that takes 
	 * in input the train set and test set files.
	 * 
	 * @return String[] 
	 */
	
	private String[] generateTrainAndTestCommand() {
		int dim;
		if (trainSet.size() == 0)
			trainSet.add("");
		dim = testSet.size() * trainSet.size();
		
		String[] combinationsOfTrainAndTest
			= new String[dim];
		int j = 0;
		int k = 0;
		for (int i = 0; i < dim ; i++){
			combinationsOfTrainAndTest[i] = " " + trainSet.get(j) 
					+ " " + testSet.get(k);
			if (++k == testSet.size()){
				k = 0;
				j++;
			}
		}
		return combinationsOfTrainAndTest;
	}

	/**
	 * This method invokes the O.S. in order to execute an external classifier.
	 * Finally it reads the answers and stores them into the this.map . 
	 * 
	 * @param comandoEsecuzione
	 * @throws IOException
	 */
	
	private void execution(String commandLine) throws IOException {
		Process proc = Runtime.getRuntime().exec(commandLine);
		InputStream in = proc.getInputStream();
		String executionOutput = getStringFromInputStream(in);

		ArrayList<String> lines = getAnswers(executionOutput);
		this.map.put(commandLine, lines);
	}

	/**
	 * Change an InputStream into a String value in order to get
	 * the answers.
	 * 
	 * @param is
	 * @return String
	 * @throws IOException 
	 */
	
	private String getStringFromInputStream(InputStream is) throws IOException {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;

		br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		
		if (br != null) 
			br.close();
		
		return sb.toString();
	}

	/**
	 * Recursive method that produces a list of all the possible combinations.
	 * 
	 * @param idx
	 * @param array
	 * @param tmp
	 */
	
	private void countAllCombinations(int idx, float[] array, float[] tmp) {
		if (array[idx] >= end[idx]) {
			return;
		}
		array[idx] += step[idx];
		tmp = array.clone();
		if (!combinations.contains(Arrays.toString(array))) {
			String newCommand = command;
			for (int j = 0; j < numberOfParameters; j++) {
				newCommand += " " + array[j];
			}
			combinations.add(Arrays.toString(array));
			String[] trainAndTest = generateTrainAndTestCommand();
			String completeCommand = "";
			for(String s : trainAndTest){
				completeCommand = newCommand + s;
				commands.add(completeCommand);
				completeCommand = "";
			}
		}
		else
			return;
		
		for (int i = 0; i < array.length; i++) {
			countAllCombinations(i, array, tmp);
			array = tmp.clone();

		}
	}

	/**
	 * This method reads the output of every execution of an EC and
	 * extracts an ArrayList containing all the answers of the EC for 
	 * that 
	 * @param executionOutput
	 * @return ArrayList<String>
	 */
	
	private static ArrayList<String> getAnswers(String executionOutput) {
		ArrayList<String> lines = new ArrayList<String>();
		int lastCharIndex = executionOutput.length();
		int currentCharIndex = 0;
		while (currentCharIndex < lastCharIndex) {
			int tmpIndex = currentCharIndex;
			currentCharIndex = executionOutput.indexOf('\n',
					(currentCharIndex + 1));
			if (currentCharIndex == -1) {
				break;
			}
			
			int subStringStartIndex = (tmpIndex == 0) ? 0 : (tmpIndex + 1);
			
			String singleRawAnswer = executionOutput
					.substring(subStringStartIndex, currentCharIndex);
			lines.add(singleRawAnswer);
		}
		return lines;
	}
}