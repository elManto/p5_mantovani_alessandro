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

package serializedexecution;

/**
 * 
 * NumericElement is an abstract class used to represent all the numeric
 * elements which are contained in this graphic interface.
 *
 */

public abstract class NumericElement {
	protected String name;

	public NumericElement(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
