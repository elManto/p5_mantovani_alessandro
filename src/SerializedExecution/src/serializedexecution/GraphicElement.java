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
 * An abstract class that contains the essential information to
 * represent a graphical element. It doesn't belong to logic 
 * of the software so the following methods are not commented. 
 *
 */

public abstract class GraphicElement {
	private int x;
	private int y;
	private int width;
	private int height;

	
	public GraphicElement(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setValues(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void show(Object frame, Object component) {}
	public void hide(Object frame, Object component) {}
	public void setOnTop(Object frame, Object component) {}
	public void show(Object frame) {}
	public void hide(Object frame) {}
	public void setOnTop(Object frame) {}
	
	public void moveY(Object frame, int y) {
		setValues(this.x, y, this.width, this.height);
		hide(frame);
		show(frame);
	}
}
