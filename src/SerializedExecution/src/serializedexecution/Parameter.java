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
 * Parameter extends NumericElement to hold information about
 * the name and the value of a numeric constant.
 */

public class Parameter extends NumericElement {
	private float value;
	
	public Parameter(String name, float value) {
		super(name);
		this.value = value;
	}
	
	public float getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return name + " " + value;
	}
}
