/*
 * Author: Dario Capozzi, Alessandro Mantovani, Roberto Ronco, Giulio Tavella
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

import java.util.ArrayList;

/**
 * The class DatabaseTestUtil was created for debugging purpose.
 */

public class DatabaseTestUtil {
	public static void printDatabaseData(Database database) throws Exception {
		ArrayList<Model> externalClassifier = database.retrieveFromTable(FileType.EC);
		ArrayList<Model> testSet = database.retrieveFromTable(FileType.TEST);
		ArrayList<Model> trainSet = database.retrieveFromTable(FileType.TRAIN);
		ArrayList<Model> configuration = database.retrieveFromTable(FileType.CONFIGURATION);
		
		System.out.println("External classifiers : ");
		for (Model m : externalClassifier) {
			System.out.println(m);
		}
		System.out.println("Test set : ");
		for (Model m : testSet) {
			System.out.println(m);
		}
		System.out.println("Train set : ");
		for (Model m : trainSet) {
			System.out.println(m);
		}
		System.out.println("Configuration : ");
		for (Model m : configuration) {
			System.out.println(m);
		}
	}
}
