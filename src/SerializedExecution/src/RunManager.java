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



import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


/**
 * The class RunManager handles the thread associated to the serialised execution
 * of the specified EC, along with the set of selected train and test set
 * on an appropriately chosen configuration defined by the user itself.
 */


public class RunManager implements Runnable {

	private ArrayList<ArrayList<Model>> model;
	private Database database;
	private Output outputSet;
	private JFrame parent;

	public RunManager(Database database) {
		this.database = database;
		outputSet = new Output();
		resetModels();
	}

	public Output getOutputSet() {
		return this.outputSet;
	}
	
	public void setParent(JFrame parent){
		this.parent = parent;
	}
	/**
	 * According to the value of "fileType", this method checks all the clicked
	 * models of the specified type. For a serialized execution you must have:
	 * 1) Exactly one External Classifier (EC)
	 * 2) Exactly one Configuration for the selected EC
	 * 3) At least one test set and one train set 
	 * 
	 * @param fileType
	 * @return
	 * @throws Exception
	 */
	
	private boolean checkClickedModels(FileType fileType) throws SQLException {
		ArrayList<Model> model = database.getClickedModels(fileType);
		
		if (fileType == FileType.TRAIN)
			return true;

		String message = null;

		if (model.size() == 0)
			message = "You must set at least one ";
		else if (model.size() > 1
				&& (fileType == FileType.EC || fileType == FileType.CONFIGURATION))
			message = "You must set only one ";

		if (message != null) {
			JOptionPane.showMessageDialog(null, message + fileType.name());
			return false;
		}

		return true;
	}

	/**
	 * This method shows to the user the selected data 
	 * for the current execution before it starts.
	 * @param frame
	 */

	private void showRunningWindow(JFrame frame) {
		setParent(frame);
		this.parent.setEnabled(false);
		String message = "Serialized execution running...\nPlease wait\n\n"
				+ "Current running environment:\n";
		for (FileType fileType : FileType.values()) {
			message = message + fileType.name() + " : ";
			
			for (Model m : model.get(fileType.ordinal())) {
				message = message + m.getName() + ", ";
			}
			
			message = message.substring(0, message.length() - 2);
			message = message + "\n";
		}
		message += "\nAfter serialized execution this window will be "
				+ "automatically closed.\nProduced output will "
				+ "be available at " + outputSet.getOutputFileFolder();
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JTextArea textArea = new JTextArea(message);
		
		
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setText(message);
		textArea.setWrapStyleWord(true);
		textArea.setSize(new Dimension(280,280));
		textArea.setBackground(new Color(0,0,0,0));
		
		
		panel.add(textArea);
		
		frame.add(panel);
		frame.setTitle("Work in progress. Please wait");
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		
	}
	
	private void hideRunningWindow(JFrame frame) {
		this.parent.setEnabled(true);
		frame.setVisible(false);
	}
	
	/**
	 * This method resets the ArrayList of ArrayList of models
	 */
	
	private void resetModels() {
		model = new ArrayList<ArrayList<Model>>();

		for (FileType fileType : FileType.values()) {
			model.add(fileType.ordinal(), new ArrayList<Model>());
		}
	}
	
	/**
	 * This method loads and validates the models selected for execution
	 * @return false if an invalid set is specified, true otherwise
	 */
	private boolean loadModels() {
		for (FileType fileType : FileType.values()) {
			try {
				if (!checkClickedModels(fileType))
					return false;
				setModel(fileType);
			} catch (SQLException e) {
				return false;
			}
		}
		
		ArrayList<Model> invalidModel = validatePaths();
		if (!invalidModel.isEmpty()) {
			String message = "Invalid paths of the following files: ";
			for (Model m : invalidModel) {
				message += "\n" + m.getName();
			}
			JOptionPane.showMessageDialog(null, message);
			return false;
		}
		return true;
	}
	
	/**
	 * This method sets the model of type "fileType" for the execution
	 * 
	 * @param fileType
	 * @throws Exception
	 */
	
	private void setModel(FileType fileType) throws SQLException {
		this.model.add(fileType.ordinal(), 
				database.getClickedModels(fileType));
	}
	
	
	/**
	 * The method run() is overridden from the Runnable interface. 
	 * It checks if all the inputs are set and then it executes 
	 * the External Classifier (EC) with all the combinations of the input. 
	 * Finally, it writes the result of all the computations to a file.
  	 */
	
	@Override
	public void run() {
		resetModels();
		if (!loadModels())
			return;
		
		JFrame frame = new JFrame();
		this.showRunningWindow(frame);
		
		int configurationId = model.get(FileType.CONFIGURATION.ordinal())
				.get(0).getId();

		try {
			ArrayList<NumericElement> val = database
					.retrieveConfigurationValues(Integer
							.toString(configurationId));
			
			ArrayList<String> testSetPath = new ArrayList<String>();
			for (Model m : model.get(FileType.TEST.ordinal()))
				testSetPath.add(m.getPath());
			
			ArrayList<String> trainSetPath = new ArrayList<String>();
			for (Model m : model.get(FileType.TRAIN.ordinal()))
				trainSetPath.add(m.getPath());
			
			Serializer s = new Serializer(val, 
					model.get(FileType.EC.ordinal()).get(0).getPath(),
					testSetPath, trainSetPath);
			
			outputSet.setMapOfValues(s.run());
			outputSet.writeToFile();
			Thread.sleep(5000);
			this.hideRunningWindow(frame);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Illegal folder path : " + outputSet.getOutputFileFolder());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String getOutputFileFolder() {
		return outputSet.getOutputFileFolder();
	}

	public void setOutputFileFolder(String outputFileFolder) {
		outputSet.setOutputFileFolder(outputFileFolder);
	}
	
	@Override
	public String toString() {
		return "RunManager [model=" + model + ", database=" + database
				+ ", outputSet=" + outputSet + "]";
	}
	
	/*
	 * For each clicked models, we must be sure that its path exists.
	 * We perform this control and we add the inexistent model (i.e.
	 * models whose path is not correct) to the ArrayList<Model>
	 * named "invalidModel"
	 */
	
	private ArrayList<Model> validatePaths() {
		ArrayList<Model> invalidModel = new ArrayList<Model>();
		for (int i = 0; i < model.size(); i++) {
			for (Model m : model.get(i)) {
				if (!m.exist() && m.getFileType() != FileType.CONFIGURATION) {
					invalidModel.add(m);
				}
			}
		}
		return invalidModel;
	}
	
	public void closeParent() {
		if (parent != null) 
			parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));

		
	}
}

