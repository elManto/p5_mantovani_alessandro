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



import java.awt.Component;
import javax.swing.JFrame;

/**
 * The class StandardElement extends GraphicElement in 
 * order to provide basic graphic functionality such as 
 * element show, hide, value retrieval or text modification.
 */

public abstract class StandardElement extends GraphicElement {

	public StandardElement(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	@Override
	public void show(Object frame, Object component) {
		((JFrame) frame).add((Component) component);
	}
	
	@Override
	public void hide(Object frame, Object component) {
		((JFrame) frame).remove((Component) component);
	}
	
	@Override
	public void setOnTop(Object frame, Object component) {
		((JFrame) frame).getContentPane().setComponentZOrder((Component) component, 0);
	}
	
	public abstract Object retrieveValue();
	public abstract void setValue(Object value) throws Exception;
}
