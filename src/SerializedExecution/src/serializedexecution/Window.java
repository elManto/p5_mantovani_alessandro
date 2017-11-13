/*
 * Author: Roberto Ronco
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import serializedexecution.ConfigurationBuilder;
import serializedexecution.StandardElement;
import serializedexecution.StandardTextField;

/**
 * The class Window initializes and manages the GUI of the application, 
 * which implements configuration selection and modification operations.
 */

public class Window {

	private JFrame frame;
	private Listener[] listener;
	private JTable[] jTable;
	private DefaultTableModel[] defaultTableModel;
	
	private FileManager fileManager;
	private RunManager runManager;
	private ConfigurationManager configuration;

	private JButton inputConfigurationChange;
	
	private JButton executionButton;
	private Thread runManagerThread;
	private ConfigurationBuilder configurationBuilder;
	
	/**
	 * The Window constructor instantiates the table used to display
	 * the configuration data, and its listener for changes.
	 * 
	 * @param fileManager
	 * @param runManager
	 * @param configuration
	 * @throws Exception
	 */
	
	public Window(FileManager fileManager, RunManager runManager, 
			ConfigurationManager configuration) throws Exception {
		this.fileManager = fileManager;
		this.runManager = runManager;
		this.configuration = configuration;

		final int modelsNumber = FileType.values().length;

		listener = new Listener[modelsNumber];
		jTable = new JTable[modelsNumber];
		defaultTableModel = new DefaultTableModel[modelsNumber];
	}
	
	/**
	 * The initialize() function creates the window, the configuration
	 * table and all the buttons needed to control it. This function 
	 * also fills tables with the configuration data available and 
	 * launches the application interface.
	 * 
	 * @throws Exception
	 */

	public void initialize() throws Exception {
		

		/*
		 * Download all the configuration data from database
		 */
		

		fileManager.updateAllModelData();

		/*
		 * Initializes the panel for the input configurations
		 */
		
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(700, 400));
		frame.setBounds(100, 100, 1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		frame.setVisible(true);
		
		this.runManager.setParent(frame);
		/*
		 * Initializes the left panel and attaches it to the main frame
		 */
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		frame.getContentPane().add(leftPanel);

		/*
		 * Initializes the panel for the input configurations
		 */

		
		JPanel executionPanel = new JPanel();
		leftPanel.add(executionPanel);
		
		executionButton = new JButton("Run");
		executionPanel.add(executionButton);
		executionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (runManager.getOutputSet().getOutputFileFolder() == null) {
					JOptionPane.showMessageDialog(null, "Set the output file path" + "\n" );
					return;
				}
				runManagerThread = new Thread(runManager);
				runManagerThread.start();
			}
		});

		JButton pathToSaveButton = new JButton("Path to save");
		executionPanel.add(pathToSaveButton);
		
		pathToSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser f = new JFileChooser();
					f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					f.showSaveDialog(null);
					File directoryToSave = f.getSelectedFile();
					if (directoryToSave == null)
						return;
					runManager.setOutputFileFolder(directoryToSave.getAbsolutePath());
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});
		
		/*
		 * Initializes the panel for the input configurations
		 */
		
		JPanel inputParametersPanel = new JPanel();
		inputParametersPanel.setLayout(new GridLayout(1, 0, 0, 0));
		inputParametersPanel.setMinimumSize(new Dimension(100, 100));
		inputParametersPanel.add(addScrollablePane(FileType.CONFIGURATION));
		
		/*
		 * Creates the right panel and attaches it to the main frame
		 */
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		frame.getContentPane().add(rightPanel);
		
		// Adds the input parameters panel to the right panel
		rightPanel.add(inputParametersPanel);  		
		
		/*
		 * Initializes the configuration table and buttons
		 */
		
		JPanel inputParametersPanelButtons = new JPanel();
		inputParametersPanel.add(inputParametersPanelButtons);

		inputConfigurationChange = new JButton("Change Input Conf");
		inputParametersPanelButtons.add(inputConfigurationChange);
		inputConfigurationChange.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				modifyConfiguration(-1);
			}
		});
	}

	/**
	 * @param fileType
	 * @return the appropriate jTable column name associated to fileType.
	 * @throws Exception
	 */
	
	private String getColumnName(FileType fileType) throws Exception {
		switch (fileType) {
			case CONFIGURATION:
				return "Configuration";
			default:
				throw new Exception("Unrecognized file type");
		}
	}

	/**
	 * @param fileType
	 * @return the appropriate jTable listener associated to fileType.
	 * @throws Exception
	 */
	
	private Listener createListener(FileType fileType) throws Exception {
		switch (fileType) {
			case CONFIGURATION:
				return new ConfigurationListener(fileManager);
			default:
				throw new Exception("Unrecognized file type");
		}
	}

	/**
	 * @param fileType
	 * @return the jScrollPane associated to the jTable specified by fileType,
	 * filled with appropriated data which is appropriately queried to the File
	 * Manager. 
	 * @throws Exception
	 */
	
	private JScrollPane addScrollablePane(FileType fileType) throws Exception {
		Object[] columnNames = { getColumnName(fileType), "Nome" };

		/*
		 * Fills the data matrix with the models data of file type "fileType".
		 * Each matrix row is formatted as (clicked, model name)
		 */
		
		int index = fileType.ordinal();
		int size = fileManager.getArraySize(fileType);
		Object[][] data = new Object[size][2];
		for (int i = 0; i < size; i++) {
			Model tmpModel = fileManager.getElement(fileType, i);
			data[i][0] = tmpModel.getClicked();
			data[i][1] = tmpModel.getName();
		}

		/*
		 * Prepares the table grid
		 */
		
		defaultTableModel[index] = new DefaultTableModel(data, columnNames) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}
		};

		/*
		 * Prepares the jTable and its column types
		 */
		
		jTable[index] = new JTable(defaultTableModel[index]) {

			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:
					return Boolean.class;
				case 1:
					return String.class;
				default:
					return String.class;
				}
			}
		};
		
		/*
		 * Creates the listener and attaches it to the table
		 */
		
		listener[index] = createListener(fileType);
		defaultTableModel[index].addTableModelListener(listener[index]);
		jTable[index].setPreferredScrollableViewportSize(jTable[index].getPreferredSize());
		JScrollPane scrollPane = new JScrollPane(jTable[index], JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		return scrollPane;
	}
	
	/**
	 * Allows to modify an already created configuration.
	 * 
	 * @param configurationRow
	 */
	
	private void modifyConfiguration(int configurationRow) {
		int index = FileType.CONFIGURATION.ordinal();
	
		// If the configuration has not just been created
		if (configurationRow == -1) {
			configurationRow = jTable[index].getSelectedRow();
			if (configurationRow == -1)
				return;
		}
		
		String configName = jTable[index].getModel().getValueAt(configurationRow, 1).toString();
		
		try {
			int id = fileManager.getIdByName(configName, FileType.CONFIGURATION);
			if (id == -1)
				return;
			
			configuration.load(Integer.toString(id));
			
			frame.setEnabled(false);
			
			configurationBuilder = 
					new ConfigurationBuilder(frame, configuration);
			configurationBuilder.initialize();					
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void testClickedConfiguration() throws InterruptedException{
		FileType fileType = FileType.CONFIGURATION;
		ArrayList<Model> EC = fileManager.getClicked(fileType);
		if(!EC.isEmpty()){
			int clickedIndex = -1;
			for(int i = 0; i < jTable[fileType.ordinal()].getRowCount(); i++){
				if((boolean) defaultTableModel[fileType.ordinal()].getValueAt(i, 0)){
					clickedIndex = i;
					break;
				}
			}
			int toBeClicked = ((clickedIndex + 1) > 
					defaultTableModel[fileType.ordinal()].getRowCount() - 1) ? 
							0 : (clickedIndex + 1);
			
			defaultTableModel[fileType.ordinal()].setValueAt(true, toBeClicked, 0);
			defaultTableModel[fileType.ordinal()].fireTableChanged(
					new TableModelEvent(defaultTableModel[fileType.ordinal()],toBeClicked,toBeClicked,0));
		}
	}
	
	public void testConfigurationValueDelete() throws Exception{
		jTable[FileType.CONFIGURATION.ordinal()].setRowSelectionInterval(0, 0);
		Thread.sleep(3000);
		inputConfigurationChange.doClick();
		Thread.sleep(3000);
		ArrayList<StandardElement> element =  configurationBuilder.getRow().get(0).getElement();
		StandardButton sb = (StandardButton) element.get(element.size() - 1);
		sb.getButton().doClick();
		Thread.sleep(3000);
		configurationBuilder.getConfirmButton().doClick();
	}
	
	public void testConfigurationValueAdd(ArrayList<String> toBeAdd) throws Exception{
		jTable[FileType.CONFIGURATION.ordinal()].setRowSelectionInterval(0, 0);
		Thread.sleep(3000);
		inputConfigurationChange.doClick();
		Thread.sleep(3000);
		if(toBeAdd.size() == 2)
			configurationBuilder.getAddParameterButton().doClick();
		if(toBeAdd.size() == 4)
			configurationBuilder.getAddVariableButton().doClick();
		Thread.sleep(1000);
		ArrayList<StandardElement> standardElement =  configurationBuilder
				.getRow().get(configurationBuilder.getRow().size() - 1)
				.getElement();
		
		if(toBeAdd.size() == 2){
			StandardTextField parameterName = (StandardTextField) standardElement.get(0);
			parameterName.setValue(toBeAdd.get(0));
			Thread.sleep(1000);
			StandardTextField parameterValue = (StandardTextField) standardElement.get(1);
			parameterValue.setValue(toBeAdd.get(1));
			Thread.sleep(1000);
		} else if (toBeAdd.size() == 4) {
			StandardTextField variableName = (StandardTextField) standardElement.get(0);
			variableName.setValue(toBeAdd.get(0));
			Thread.sleep(1000);
			StandardTextField variableStart = (StandardTextField) standardElement.get(1);
			variableStart.setValue(toBeAdd.get(1));
			Thread.sleep(1000);
			StandardTextField variableEnd = (StandardTextField) standardElement.get(2);
			variableEnd.setValue(toBeAdd.get(2));
			Thread.sleep(1000);
			StandardTextField variableStep = (StandardTextField) standardElement.get(3);
			variableStep.setValue(toBeAdd.get(3));
			Thread.sleep(1000);
		}
		
	    TimerTask timerTask = new TimerTask() {
	        @Override
	        public void run() {
	        	configurationBuilder.closeJOptionPane();
	        	configurationBuilder.close();
	        }
	    };
	
	    Timer timer = new Timer("MyTimer");//create a new Timer
	    timer.schedule(timerTask, 5000);
		configurationBuilder.getConfirmButton().doClick();
		
	}
	
	public void testFaultySerializedExecution() throws InterruptedException{
		Thread.sleep(2000);
		scheduleAutoAnswerJOptionPane(JOptionPane.OK_OPTION, 5000);
		this.executionButton.doClick();
	}
	
	public void testSerializedExecution() throws Exception{
		Thread.sleep(3000);
		TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
            	runManager.closeParent();
            
            }
            
        };

        Timer timer = new Timer("MyTimer");//create a new Timer
        timer.schedule(timerTask, 5000);
		this.executionButton.doClick();
		while(this.runManagerThread == null){
			Thread.sleep(3000);
		}
		this.runManagerThread.join();
	}
	
	private void scheduleAutoAnswerJOptionPane(int answer,int timeToWait){
		TimerTask timerTask = new TimerTask() {
	        @Override
	        public void run() {
	        	//System.out.println("Going to fire scheduleAutoAnswerJOptionPane with " + answer);
	        	java.awt.Window[] windows = java.awt.Window.getWindows();
	            for (java.awt.Window window : windows) {
	                if (window instanceof JDialog) {
	                    JDialog dialog = (JDialog) window;
	                    if (dialog.getContentPane().getComponentCount() == 1
	                        && dialog.getContentPane().getComponent(0) instanceof JOptionPane){
	                    	JOptionPane op = (JOptionPane) dialog.getContentPane().getComponent(0);
	                    	op.setValue(answer);
	                    }
	                }
	            }
	        }
	    };
	
	    Timer timer = new Timer("MyTimer");//create a new Timer
	    timer.schedule(timerTask, timeToWait);
	}
	
	public ArrayList<Component> getAllComponents(Container c) {
	    Component[] comps = c.getComponents();
	    ArrayList<Component> compList = new ArrayList<Component>();
	    for (Component comp : comps) {
	        compList.add(comp);
	        if (comp instanceof Container)
	            compList.addAll(getAllComponents((Container) comp));
	    }
	    return compList;
	}
	
	public JFrame getConfigurationFrame() throws InterruptedException{
		Thread.sleep(3000);
		jTable[FileType.CONFIGURATION.ordinal()].setRowSelectionInterval(0, 0);
		Thread.sleep(3000);
		inputConfigurationChange.doClick();
		while(configurationBuilder == null){
			Thread.sleep(1000);
		}
		return configurationBuilder.getFrame();
	}
	
}
