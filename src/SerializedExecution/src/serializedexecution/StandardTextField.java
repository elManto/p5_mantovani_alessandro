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

import javax.swing.JTextField;

/**
 * The class StandardTextField wraps a JTextField and extends StandardElement
 * in order to provide basic graphic functionality such as element show,
 * hide, value retrieval or text modification.
 */

public class StandardTextField extends StandardElement {
	private JTextField textField;
	
	public StandardTextField(String defaultText, int xOffset, int yOffset) {
		super(xOffset, yOffset, 114, 19);
		textField = new JTextField();
		textField.setColumns(10);
		textField.setText(defaultText);
	}
	 
	@Override
	public void show(Object frame) {
		textField.setBounds(super.getX(), super.getY(), super.getWidth(), super.getHeight());
		super.show(frame, textField);
	}
	
	@Override
	public void hide(Object frame) {
		super.hide(frame, textField);
	}
	
	@Override
	public void setOnTop(Object frame) {
		super.setOnTop(frame, (Object) textField);
	}

	@Override
	public String retrieveValue() {
		return textField.getText();
	}

	@Override
	public void setValue(Object value) {
		textField.setText(value.toString());
	}
}
