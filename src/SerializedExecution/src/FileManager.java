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
import java.util.Iterator;

/**
 * The class FileManager is essential to put in communication
 * database, graphic interface and logic of the software.
 */

public class FileManager {
	private Database db;
	private ArrayList<ArrayList<Model>> models;
	
	/*
	 * "array" is an ArrayList<ArrayList<Model>> . In practice, it 
	 * will contain four ArrayList<Model> (one for each fileType).
	 */

	public FileManager(Database db) throws Exception {
		this.db = db;
		this.models = new ArrayList<ArrayList<Model>>();
		
		for (FileType fileType : FileType.values()) 
			this.models.add(fileType.ordinal(), 
					new ArrayList<Model>());
		
		this.updateAllModelData();
	}
	
	/**
	 * Returns the model of file type "fileType" and
	 * with index "index".
	 * 
	 * @param kindOfFile
	 * @param j
	 * @return null if no model at index "index" is available,
	 * the found model otherwise
	 */

	public Model getElement(FileType kindOfFile, int j) {
		int maxIndex = models.get(kindOfFile.ordinal()).size();
		if (j < 0 || j >= maxIndex)	// check on the bounds of the array
			return null;
		return models.get(kindOfFile.ordinal()).get(j);
	}
	
	/**
	 * Wraps the Database method in order to remove a model named 
	 * "name" of FileType "fileType".
	 * 
	 * @param modelName
	 * @param path
	 * @param kindOfFile
	 * @throws Exception
	 */
	
	public void remove(String modelName, FileType kindOfFile) throws Exception {
		this.db.removeFromTable(modelName, kindOfFile);
		this.updateModelData(kindOfFile);
	}
	
	/**
	 * Returns a copy of the array of 
	 * models specified by fileType
	 * 
	 * @param kindOfFile
	 * @return
	 */
	
	public ArrayList<Model> getModelArray(FileType kindOfFile) {
		
		ArrayList<Model> modelArray = new ArrayList<Model>();
		Iterator<Model> iterator = this.getIterator(kindOfFile);
		
		while (iterator.hasNext())
			modelArray.add(iterator.next());
		
		return modelArray;
	}
	
	/**
	 * Returns an iterator to the ArrayList<Model> 
	 * containing the models of fileType "fileType"
	 * 
	 * @param kindOfFile
	 * @return Iterator<Model>
	 */

	public Iterator<Model> getIterator(FileType kindOfFile) {
		return models.get(kindOfFile.ordinal()).iterator();
	}
	
	/**
	 * Returns the size of the ArrayList<Model>
	 * containing the models of fileType "fileType"
	 * 
	 * @param kindOfFile
	 * @return Iterator<Model>
	 */

	public int getArraySize(FileType kindOfFile) {
		ArrayList<Model> array = models.get(kindOfFile.ordinal());
		return array.size();
	}
	
	/**
	 * Wraps the Database method in order to insert a model named 
	 * "name", located at path "path", of FileType "fileType".
	 * 
	 * @param modelName
	 * @param modelPath
	 * @param kindOfFile
	 * @throws Exception
	 */
	
	public void insert(String modelName, String modelPath, FileType kindOfFile) throws Exception {
		this.db.insertIntoTable(modelName, modelPath, kindOfFile);
		this.updateModelData(kindOfFile);
	}
	
	/**
	 * First, this method retrieves all the models of a specified "fileType"
	 * from the database. 
	 * Finally it saves the result in the private field array.
	 * 
	 * @param kindOfFile
	 * @return ArrayList<Model>
	 * @throws Exception
	 */
	
	public ArrayList<Model> updateModelData(FileType kindOfFile) throws Exception {
		ArrayList<Model> result = db.retrieveFromTable(kindOfFile);
			
		models.set(kindOfFile.ordinal(), result);
		
		return result;
	}
	
	/**
	 * Wraps the Database method that makes available the 
	 * possibility of downloading all the models from the
	 * database.
	 * 
	 * @throws Exception
	 */
	
	public void updateAllModelData() throws Exception {
		for (FileType fileType : FileType.values()) 
			this.updateModelData(fileType);
		
	}
	
	/**
	 * Set the "clicked" attribute of the model indicated by
	 * the index "index" of FileType "fileType" to "val".
	 * 
	 * @param kindOfFile
	 * @param j
	 * @param checked
	 */
	
	public void setClicked(FileType kindOfFile, int j, boolean checked) throws Exception {
		int maxIndex = models.get(kindOfFile.ordinal()).size();
		if (j < 0 || j >= maxIndex)
			return;
		if (checked && (kindOfFile == FileType.EC || kindOfFile == FileType.CONFIGURATION)) {
			for (Model mod : models.get(kindOfFile.ordinal())) {
				/*
				 * EC and CONFIGURATION models must be exclusive so we must
				 * set all the other models to false.
				 */
				mod.setClicked(false);
			}
			db.updateClicked("*", 0, kindOfFile);
		}
		// Get the model at index "j"
		Model mod = models.get(kindOfFile.ordinal()).get(j);
		// Current model set at "val"
		mod.setClicked(checked);
		// Database is kept coherent with this
		db.updateClicked(mod.getName(), 1, kindOfFile);
	}
	
	
	/**
	 * It returns an ArrayList containing all the models of the type 
	 * indicated by "fileType" that have been clicked by the user.
	 * 
	 * @param kinfOfFile
	 * @return ArrayList<Model>
	 */
	
	public ArrayList<Model> getClicked(FileType kinfOfFile) {
		ArrayList<Model> modelArray = this.models.get(kinfOfFile.ordinal());
		ArrayList<Model> checkedModel = new ArrayList<Model>();
		
		Iterator<Model> iterator = modelArray.iterator();
		while(iterator.hasNext()) {
			Model mod = iterator.next();
			if(mod.getClicked()) {
				checkedModel.add(mod);
			}
		}
		
		if (checkedModel.size() == 0)
			return null;
		
		return checkedModel;
	}
	

	/**
	 * Get the id of the model with name "name" of fileType "fileType"
	 * 
	 * @param name, fileType
	 * @return int
	 * @throws Exception
	 */
	
	public int getIdByName(String modelName, FileType kindOfFile) throws Exception {
		Iterator<Model> iterator = models.get(kindOfFile.ordinal()).iterator();
		while(iterator.hasNext()) {
			Model mod = iterator.next();
			if (mod.getName().equals(modelName))
				return mod.getId();
		}
		
		return -1;
	}
	
}
