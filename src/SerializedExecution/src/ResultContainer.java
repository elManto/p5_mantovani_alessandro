/*
 * Author: Dario Capozzi, Alessandro Mantovani, Roberto Ronco, Giulio Tavella
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



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class wraps PreparedStatement and ResultSet objects 
 * to easily access retrieved data and properly handle the 
 * release of the resources.
 */

public class ResultContainer {
	private PreparedStatement pst;
	private ResultSet rs;
	
	public ResultContainer(PreparedStatement pst, ResultSet rs){
		this.pst = pst;
		this.rs = rs;
	}
	
	public ResultSet getRs() {
		return rs;
	}

	public void close() throws SQLException {
		pst.close();
		rs.close();
	}
}
