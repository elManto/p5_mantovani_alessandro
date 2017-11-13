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
 * Variable extends NumericElement to hold information about the 
 * name, the start, the end, and the step of a numeric value.
 */

public class Variable extends NumericElement {
	private float start, end, step;
	private float currentValue;
	
	public Variable(String name, float start, float end, float step) {
		super(name);
		this.start = start;
		this.end = end;
		this.step = step;
	}
	
	public float startIteration() {
		currentValue = start;
		return currentValue;
	}
	
	public float nextValue() {
		if (currentValue <= end - step)
			currentValue += step;
		else
			currentValue = end;
		
		return currentValue;
	}
	
	public float getCurrentValue() {
		return currentValue;
	}
	
	public boolean hasEnded() {
		return currentValue == end;
	}
	
	public float getStart() {
		return start;
	}
	
	public float getEnd() {
		return end;
	}
	
	public float getStep() {
		return step;
	}
	
	@Override
	public String toString() {
		return name + " " + start + " " + end + " " + step;
	}
}
