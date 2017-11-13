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

import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class ParameterBackground extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public ParameterBackground(int x, int y, int width, int height) {
		this.setBounds(new Rectangle(x, y, width, height));
		this.setBackground(new Color(176,176,176));
	}
}
