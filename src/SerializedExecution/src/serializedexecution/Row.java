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
import java.util.ArrayList;
import javax.swing.JFrame;

import serializedexecution.StandardElement;

/**
 * This class is used to represent a generic row.
 */

public abstract class Row extends StandardElement {
	protected ArrayList<StandardElement> element;
	
	public Row(int x, int y, int width, int height) {
		super(x, y, width, height);
		element = new ArrayList<StandardElement>();
	}
	
	/**
	 * Used to visually show the instance 
	 * of the class which extends Row.
	 * @param frame 
	 */
	
	public void show(Object frame){
		for (StandardElement e : element)
			e.show(frame);
		
		((JFrame) frame).repaint();
	}
	
	/**
	 * Used to visually hide the instance 
	 * of the class which extends Row.
	 * @param frame
	 */
	
	public void hide(Object frame){
		for (StandardElement e : element)
			e.hide(frame);
		
		((JFrame) frame).repaint();	
	}
	
	/**
	 * Used to visually move the instance 
	 * of the class which extends Row on
	 * the vertical axis of "frame".
	 */
	
	@Override
	public void moveY(Object frame, int y) {
		setValues(getX(), y, getWidth(), getHeight());
		for (StandardElement e : element)
			e.setValues(e.getX(), y, e.getWidth(), e.getHeight());
		hide(frame);
		show(frame);
	}
	
	public void addDeleteRowListener(ActionListener actionListener) {
	}
	
	/**
	 * Used to visually set the instance 
	 * of the class which extends Row on
	 * top of the graphic elements in the
	 * frame object passed as parameter.
	 * @param frame
	 */
	
	public void setOnTop(Object frame) {
		for (StandardElement e : element) {
			e.setOnTop(frame);
		}
	}
	
	public ArrayList<StandardElement> getElement(){
		return this.element;
	}
}
