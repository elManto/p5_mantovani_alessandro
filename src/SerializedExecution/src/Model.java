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




import java.io.File;

/**
 * The class Model represents each file type used in the software.
 * The file type represented by each instance of the Model class
 * is identified by the fileType member variable.
 * Note that the file types used in the software are listed in the 
 * source file "FileType.java".
 */

public class Model {
	
	private int id;
	private String name, path;
	private boolean clicked;
	private FileType fileType;

	public Model(int id, String name, String path, boolean clicked, FileType fileType) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.clicked = clicked;
		this.fileType = fileType;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public boolean getClicked() {
		return clicked;
	}
	
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public FileType getFileType() {
		return fileType;
	}
	
	public boolean exist(){
		return new File(path).isFile();
	}

	@Override
	public String toString() {
		return fileType.name() + " [id = " + id + ", name = " + name + ", path = "
							   + path + ", clicked = " + clicked + "]";
	}
}
