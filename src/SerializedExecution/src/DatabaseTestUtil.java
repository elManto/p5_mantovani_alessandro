import java.util.ArrayList;



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
