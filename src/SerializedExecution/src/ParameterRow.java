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



import java.awt.event.ActionListener;

/**
 * The class ParameterRow extends Row to display the necessary fields
 * to perform the definition of a Parameter class (name, value).
 */

public class ParameterRow extends Row {
	
	/**
	 * The constructor displays the two text fields and 
	 * the deletion button in a visually appealing manner.
	 * @param xOffset
	 * @param yOffset
	 */
	
	public ParameterRow(int xOffset, int yOffset) {
		super(0, 0, 0, 0);
		
		element.add(new StandardTextField("Name", xOffset, yOffset));
		element.add(new StandardTextField("Value", 
				element.get(0).getX() + element.get(0).getWidth() + 10 + xOffset, 
				yOffset));
		element.add(new StandardButton("Delete", 
				element.get(1).getX() + element.get(1).getWidth() + 10 + xOffset, 
				yOffset));
		
		
		int totalWidth = 0;
		for (StandardElement e : element)
			totalWidth += e.getWidth();
		
		super.setValues(element.get(0).getX(), element.get(0).getY(), 
				totalWidth, element.get(0).getHeight());
	}
	
	public void addDeleteRowListener(ActionListener actionListener) {
		((StandardButton) element.get(2))
			.addStandardActionListener(actionListener);
	}
	

	/**
	 * Retrieves the Parameter object associated to the class.
	 */
	
	@Override
	public Object retrieveValue() throws NumberFormatException {
		return new Parameter(element.get(0).retrieveValue().toString(), 
					Float.parseFloat(element.get(1).retrieveValue().toString()));
	}

	/**
	 * Set the fields of the ParameterRow basing on the 
	 * value fields if it is an instance of Parameter.
	 */
	
	@Override
	public void setValue(Object value) throws Exception {
		if (value instanceof Parameter) {
			Parameter el = (Parameter) value;
			element.get(0).setValue(el.getName());
			element.get(1).setValue(el.getValue());
		}
		else {
			throw new Exception("Value type not recognized");
		}
	}
}
