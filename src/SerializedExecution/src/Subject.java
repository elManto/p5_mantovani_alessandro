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



import java.util.ArrayList;
import java.util.List;

/**
 * The class Subject is featured in the "Observer" Design Pattern.
 * It notifies observers about changes in its state.
 */

public abstract class Subject {
	private List<Observer> observer;
	
	public Subject() {
		observer = new ArrayList<Observer>();
	}
	
	/**
	 * Attaches an observer to "observer"
	 * @param o
	 */
	
	public void attach(Observer o) {
		observer.add(o);
	}
	
	/**
	 * Detaches an observer from "observer"
	 * @param o
	 */
	
	public void detach(Observer o) {
		observer.remove(o);
	}
	
	/**
	 * Notify all the observers attached to the subject
	 * of a status update.
	 * @throws Exception
	 */
	
	public void notifyObservers() throws Exception {
		for (Observer o : observer) {
			o.update(this);
		}
	}
	
	public abstract void write() throws Exception;
}
