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

import java.awt.event.ActionListener;

/**
 * The class VariableRow extends Row to display the necessary fields
 * to perform the definition of a Variable class (name, start, end, step).
 */

public class VariableRow extends Row {
	
	
	/**
	 * The constructor displays the four text fields and the deletion
	 * button in a visually appealing manner.
	 * @param xOffset
	 * @param yOffset
	 */
	
	public VariableRow(int xOffset, int yOffset){
		super(0, 0, 0, 0);
		
		element.add(new StandardTextField("Name", xOffset, yOffset));
		element.add(new StandardTextField("Start",
				element.get(0).getX() + element.get(0).getWidth() + 10 + xOffset,
				yOffset));
		element.add(new StandardTextField("End",
				element.get(1).getX() + element.get(1).getWidth() + 10 + xOffset,
				yOffset));
		element.add(new StandardTextField("Step",
				element.get(2).getX() + element.get(2).getWidth() + 10 + xOffset,
				yOffset));
		element.add(new StandardButton("Delete", 
				element.get(3).getX() + element.get(3).getWidth() + 10 + xOffset,
				yOffset));
		
		int totalWidth = 0;
		for (StandardElement e : element)
			totalWidth += e.getWidth();
		
		super.setValues(element.get(0).getX(), element.get(0).getY(), totalWidth,
				element.get(0).getHeight());
	}
	
	@Override
	public void addDeleteRowListener(ActionListener actionListener) {
		((StandardButton) element.get(4)).addStandardActionListener(actionListener);
	}

	/**
	 * Retrieves the Variable object associated to the class.
	 */
	
	@Override
	public Object retrieveValue() throws NumberFormatException {
		return new Variable(element.get(0).retrieveValue().toString(),
					Float.valueOf(element.get(1).retrieveValue().toString()),
					Float.valueOf(element.get(2).retrieveValue().toString()),
					Float.valueOf(element.get(3).retrieveValue().toString()));
	}
	
	/**
	 * Set the fields of the VariableRow basing on the 
	 * value fields if it is an instance of Variable.
	 */
	
	@Override
	public void setValue(Object value) throws Exception {
		if (value instanceof Variable) {
			Variable el = (Variable) value;
			element.get(0).setValue(el.getName());
			element.get(1).setValue(el.getStart());
			element.get(2).setValue(el.getEnd());
			element.get(3).setValue(el.getStep());
		}
		else {
			throw new Exception("Value type not recognized");
		}
	}
}
