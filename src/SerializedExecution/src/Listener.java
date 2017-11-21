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



import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Superclass of listeners implementing table model listeners, providing 
 * methods to handle the events of the graphic elements of the table model
 * which the listener is attached to.
 */

public class Listener implements TableModelListener {
	protected boolean externalModify;
	protected FileManager fileManager;
	protected String selectedElement, table;
	protected TableModel model;
	protected FileType fileType;
	protected int row, column;
	
	public Listener(FileManager fileManager, FileType fileType) throws Exception {
		this.fileManager = fileManager;
		this.fileType = fileType;
		
		externalModify = true;
		selectedElement = null;
		this.table = Database.getTableName(fileType);
	}
	
	public String getSelectedElement() {
		return selectedElement;
	}
	
	/**
	 * Table change callback function.
	 */
	
	@Override
	public void tableChanged(TableModelEvent e) {
		try {
			tableChangedHandler(e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method is invoked when the tableChanged callback is fired.
	 * It retrieves the element just checked and updates the fileManager
	 * and the database accordingly.
	 * 
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public boolean tableChangedHandler(TableModelEvent e) throws Exception {
		if (!externalModify)
			return false;
		
		row = e.getFirstRow();
		column = e.getColumn();

		model = (TableModel) e.getSource();
		selectedElement = (String) model.getValueAt(row, 1);
		
		boolean checked = (boolean) model.getValueAt(row, column);
		
		// Sets the checked fileType element in the fileManager
		fileManager.setClicked(fileType, row, checked);
		
		return true;
	}
	
	/**
	 * Prevents from triggering new tableChanged callbacks
	 * when modifying the table.
	 */
	
	public void setLock() {
		externalModify = false;
	}

	public void unsetLock() {
		externalModify = true;
	}

	public boolean isLocked() {
		return externalModify;
	}
}
