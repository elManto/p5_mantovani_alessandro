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
import javax.swing.JButton;

/**
 * The class StandardButton wraps a JButton and extends StandardElement
 * in order to provide basic graphic functionality such as element show,
 * hide, value retrieval or text modification.
 */

public class StandardButton extends StandardElement {
	private JButton button;
	
	public StandardButton(String defaultText, int xOffset, int yOffset) {
		super(xOffset, yOffset, 80, 19);
		button = new JButton();
		button.setText(defaultText);
	}
	
	public void addStandardActionListener(ActionListener actionListener){
		this.button.addActionListener(actionListener);
	}
	
	@Override
	public void show(Object frame) {
		button.setBounds(super.getX(), super.getY(), super.getWidth(), super.getHeight());
		super.show(frame, button);
	}
	
	@Override
	public void hide(Object frame) {
		super.hide(frame, button);
	}
	
	@Override
	public void setOnTop(Object frame) {
		super.setOnTop(frame, (Object) button);
	}

	@Override
	public Object retrieveValue() {
		return button.getText().toString();
	}

	@Override
	public void setValue(Object value) {
		button.setText(value.toString());
	}
	
	public void doClick(){
		button.doClick();
	}
	
	public JButton getButton(){
		return this.button;
	}
}
