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



/**
 * This class is used in the "Observer" Design Pattern.
 * It declares an abstract method update(), which is 
 * invoked by a subject in order to notify its observer(s).
 */

public abstract class Observer {
	public abstract void update(Subject changedSubject) throws Exception;
	protected Observer() {};
}
