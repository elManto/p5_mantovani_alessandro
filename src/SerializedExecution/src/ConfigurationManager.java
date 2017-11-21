/*
 * Author: Alessandro Mantovani, Roberto Ronco
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
 * The class ConfigurationManager extends Subject (Subject is used 
 * to implement the design pattern Observer). It provides an interface 
 * to link database and GUI. 
 */

public class ConfigurationManager extends Subject {
	private ArrayList<NumericElement> numericElements;
	private Database db;
	private String stringId;

	public ConfigurationManager(Database db) {
		this.db = db;
		this.numericElements = new ArrayList<NumericElement>();
		this.stringId = new String("");
	}
	
	/**
	 * Insert a configuration passed as ArrayList<Row> in the
	 * ArrayList<NumericElement> element.
	 * @param rows
	 * @return true if the configuration contains correct values,
	 * false otherwise.
	 */
	
	public boolean setConfigurationFromRow(ArrayList<Row> rows) {
		this.numericElements = new ArrayList<NumericElement>();
		try {
			for (Row row : rows) {
				NumericElement numericElement = (NumericElement) row.retrieveValue(); 
				this.numericElements.add(numericElement);
			}
		} catch (IllegalArgumentException e) {
			this.numericElements = new ArrayList<NumericElement>();
			return false;
		}
		return true;
	}
	
	
	/**
	 * @return Iterator<NumericElement>
	 */
	
	public Iterator<NumericElement> getNumericElementIterator() {
		return numericElements.iterator();
	}
	
	
	/**
	 * @return ArrayList "element"
	 */
	
	public ArrayList<NumericElement> getConfiguration() {
		return this.numericElements;
	}
	
	/**
	 * After removing the blank spaces and after checking 
	 * for the validity of a configuration the new configuration
	 * contained in the private attribute "element" is written 
	 * into database. 
	 * 
	 * @param db 
	 * @see observer.Subject#write(database.Database)
	 */
	
	@Override
	public void write() throws Exception {
		checkValidity();
		db.insertConfigurationValues(stringId, numericElements);
	}
	
	
	/**
	 * Extract a configuration from the database with id
	 * "id" and save it in the ArrayList this.element.
	 * @throws Exception
	 */
	
	public void load(String stringId) throws Exception {
		this.stringId = stringId;
		numericElements = db.retrieveConfigurationValues(stringId);
	}
	
	public String getId() {
		return this.stringId;
	}
	
	
	/**
	 * Insert values from a configuration into ArrayList<> 
	 * "element"
	 * @param row
	 * @return
	 */
	
	public void setConfigurationFromNumericElement(
			ArrayList<NumericElement> numericElementArray) {
		
		Iterator<NumericElement> numericElementIterator = numericElementArray.iterator();
		numericElements = new ArrayList<NumericElement>();
		
		while(numericElementIterator.hasNext()) {
			NumericElement numeric = numericElementIterator.next();
			numericElements.add(numeric);
		}
	}
	
	
	/**
	 * The method checkValidity() looks at the correctness of the input
	 * values written by the user. The function performs the following
	 * checks: 
	 * 1) the value of "end" must be greater than the field "start"
	 * 2) the value of "step" must be positive
	 * 
	 * @throws Exception if any constraint is not satisfied
	 */
	
	private void checkValidity() throws Exception {
		for (NumericElement numericEl : numericElements) {
			if (numericEl instanceof Variable) {
				Variable variableToCheck = (Variable) numericEl;
				if (variableToCheck.getStep() < 0) {
					throw new Exception("The increase step of variable '" +
							numericEl.getName() + "' is a negative value");
							
				}
				if (variableToCheck.getStart() > variableToCheck.getEnd()) {
					throw new Exception("The start value of variable '" + 
							numericEl.getName() + "' is higher"
									+ " than the end value");
				}
			}
		}
	}
	
}
