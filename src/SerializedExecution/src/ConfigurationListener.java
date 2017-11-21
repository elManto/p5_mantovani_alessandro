/*
 * Author: Alessandro Mantovani, Roberto Ronco, Giulio Tavella
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



import javax.swing.event.TableModelEvent;

/**
 * The class ConfigurationListener is used for the events of the graphic
 * elements connected to the table model relative to the configuration.
 * 
 */

public class ConfigurationListener extends Listener {
	
	/**
	 * The attribute "internalModify" is a flag that is used to avoid
	 * that new changes are required while the table model is being 
	 * changed. In other words, if the user tries to modify the table
	 * of the configurations but some changes are currently under 
	 * construction the new event will not be fired. 
	 */
	private boolean internalModify; 

	public ConfigurationListener(FileManager fileManager) throws Exception {
		super(fileManager, FileType.CONFIGURATION);
		internalModify = true;
	}
	
	/**
	 * The following method manages the arrival of an event. 
	 * According to the value of the lock "internalModify" the 
	 * method performs or not the event. 
	 *  
	 * @param tableModelEvent
	 */
	
	@Override
	public void tableChanged(TableModelEvent tableModelEvent) {
		try {
			if (!internalModify)
				return;
			
			if (!super.tableChangedHandler(tableModelEvent))
				return;
			
			updateList(row, column);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method updates the list of the configuration.
	 * 
	 * @param row
	 * @param column
	 */
	
	private void updateList(int row, int column) {
		internalModify = false;
		
		for (int i = 0; i < fileManager.getArraySize(fileType); i++) {
			model.setValueAt(false, i, column);
		}
		
		model.setValueAt(true, row, column);
		internalModify = true;

	}
}
