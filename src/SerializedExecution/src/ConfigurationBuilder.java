/*
 * Author: Alessandro Mantovani, Roberto Ronco, Giulio Tavella
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




import javax.swing.JFrame;
import javax.swing.JOptionPane;



import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The class ConfigurationBuilder makes available a GUI in order to 
 * insert, modify, or delete the parameters of a configuration. 
 */

public class ConfigurationBuilder {

	private JFrame frame;
	private JFrame parent; 
	private JButton btnAddStaticParameter;
	private JButton btnAddVariable;
	private JButton btnConfirm;
	private JButton btnCancel;
	private int level, elements;
	private ArrayList<Row> row;
	private ParameterBackground parameterBackground;
	private int maxLevels = 10;
	private ConfigurationManager configuration;
	private JFrame jOptionFrame;
		
	/**
	 * Create window that shows and contains the graphical elements
	 * which can be used to handle the values of a configuration.
	 * @throws Exception 
	 */
	public ConfigurationBuilder(JFrame parent, 
			ConfigurationManager configuration) throws Exception {
		this.parent = parent;
		this.configuration = configuration;
		
		/*
		 * These two integer values are used to regulate the deepth 
		 * inside the view.
		 */
		
		level = 0;
		elements = 5;
		
		/*
		 *  The ArrayList<Row> named "row" is initialized and is used
		 *  to contain all the variable and/or parameter rows.
		 */
		
		row = new ArrayList<Row>();
	}

	/**
	 * Initializes the frame and all the elements contained
	 * such as buttons.
	 * @throws Exception 
	 */
	public void initialize() throws Exception {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 420);
	    frame.getContentPane().setLayout(null);
	    frame.setResizable(false);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    frame.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
	        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	            parent.setEnabled(true);
	        }
	    });
	    
	    parameterBackground = new ParameterBackground(0, 45, 700, 300);
		frame.getContentPane().add(parameterBackground);
		
		
		/*
		 * This code is used to get an istance of the "Add parameter"
		 * button to the layout
		 */
		
		
		btnAddStaticParameter = new JButton("Add parameter");
		btnAddStaticParameter.setBounds(0, 0, 150, 25);
		
		/*
		 * A listener is connected to this button so that, when is
		 * clicked (i.e. a new parameter is added), a new parameter
		 * row is added.
		 */
		
		btnAddStaticParameter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (level < maxLevels) {
					elements += 3;
					addRow(new ParameterRow(0, 50 + level * 30));
				}
			}
		});
		frame.getContentPane().add(btnAddStaticParameter);
		
	
		/*
		 * "Add variable" button is instanced and added
		 * to the layout. After this a listener is connected to
		 * in order to add a variable row when the button is 
		 * pressed
		 */
		

		btnAddVariable = new JButton("Add variable");
		btnAddVariable.setBounds(10 + 150, 0, 150, 25);
		btnAddVariable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (level < maxLevels) {
					elements += 5;
					addRow(new VariableRow(0, 50 + level * 30));
				}
			}
		});
		frame.getContentPane().add(btnAddVariable);
		
		/*
		 * "Confirm" button is created. It is used to confirm the changes
		 * made to the current configuration.
		 */
		
		btnConfirm = new JButton("     Confirm     ");
		btnConfirm.setBounds(0, 350, 150, 25);
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// setConfiguration checks for the validity of the inserted values
				if (configuration.setConfigurationFromRow(row)) {
					try {
						/*
						 * invoke the notify_observers() to notify to
						 * the observer the change of the state
						 */
						configuration.notifyObservers();
						parent.setEnabled(true);
						frame.dispatchEvent(new WindowEvent(frame, 
								WindowEvent.WINDOW_CLOSING));
					}  catch (Exception ex) {
						//ex.printStackTrace();
						jOptionFrame = new JFrame();
						JOptionPane.showMessageDialog(jOptionFrame, 
								"Problem saving the configuration.\n"
								+ ex.getMessage());
					}
				}
				else {
					/*
					 *  If setConfiguration() returns false this means
					 *  that some inserted values are not correct.
					 */
					
					jOptionFrame = new JFrame();
					JOptionPane.showMessageDialog(jOptionFrame, 
							"Cannot save the configuration: " +
							"inappropriate numeric values.");
				}
			}
		});
		frame.getContentPane().add(btnConfirm);
		
		/*
		 *  Deletion button is created. When it is pressed the window
		 *  that manages the values of a configuration is closed.
		 */
		
		btnCancel = new JButton("     Cancel     ");
		btnCancel.setBounds(10 + 150, 350, 150, 25);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.setEnabled(true);
				frame.dispatchEvent(new WindowEvent(frame, 
						WindowEvent.WINDOW_CLOSING));
				
			}
		});
		frame.getContentPane().add(btnCancel);
		
		//Shows the rows of the current configuration.
		displayConfiguration();
	}
	
	/**
	 * The following method provides the way to add a row which can be of two
	 * types:
	 * 1) parameterRow composed by a "name" and "start" attributes
	 * 2) variableRow composed by "name", "start", "step" and "end" attributes
	 * 
	 * @param newRow
	 */
	private void addRow(Row newRow) {
		row.add(newRow);
		int index = row.size() - 1;
		row.get(index).show(frame);
		row.get(index).addDeleteRowListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int index = -1;
						for (int i = 0; i < row.size(); i++) {
							if (newRow == row.get(i)) {
								index = i;
								row.remove(i);
								newRow.hide(frame);
								if (newRow instanceof ParameterRow)
									elements -= 3;
								else
									elements -= 5;
								break;
							}
						}
						for (int i = index; i < row.size(); i++) {
							Row tmp = row.get(i);
							tmp.moveY(frame, newRow.getY() + (i - index) * 30);
						}
						level--;
						frame.getContentPane()
							.setComponentZOrder(parameterBackground, elements - 1);
					}
				});
		row.get(index).setOnTop(frame);
		frame.getContentPane().setComponentZOrder(parameterBackground, elements - 1);
		level++;
	}
	
	
	/**
	 * The method displayConfiguration() is used to show the values
	 * that belong to the current configuration.
	 * 
	 * @throws Exception
	 */
	private void displayConfiguration() throws Exception {
		Iterator<NumericElement> i = configuration.getNumericElementIterator();
		
		while (i.hasNext()) {
			NumericElement el = i.next();
			if (el instanceof Variable) {
				elements += 5;
				VariableRow variableRow = new VariableRow(0, 50 + level * 30);
				variableRow.setValue(el);
				addRow(variableRow);
			}
			else {
				elements += 3;
				ParameterRow parameterRow = new ParameterRow(0, 50 + level * 30);
				parameterRow.setValue(el);
				addRow(parameterRow);
			}
		}
	}
	
	/*
	 * 
	 * The following methods are used in the test phase.
	 * 
	 */
	
	public JFrame getFrame(){
		return this.frame;
	}
	
	public ArrayList<Row> getRow(){
		return this.row;
	}
	
	public JButton getConfirmButton(){
		return this.btnConfirm;
	}
	
	public JButton getAddParameterButton(){
		return this.btnAddStaticParameter;
	}
	
	public JButton getAddVariableButton(){
		return this.btnAddVariable;
	}
	
	public void closeJOptionPane(){
		if(jOptionFrame != null){
			jOptionFrame.dispatchEvent(new WindowEvent(jOptionFrame, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	public void close() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
