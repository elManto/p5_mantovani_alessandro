package alessandromantovanitest_strutturali;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.Parameter;
import alessandromantovani.Variable;
import alessandromantovani.VariableRow;

public class VariableRowTest {
	private static VariableRow variableRow;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		variableRow = new VariableRow(0, 0);
	}

	@Test
	public final void testGetSetRetrieveField() throws Exception {
		/*
		 * Creates a Parameter and tries to insert it in the VariableRow
		 * as the value field. The insertion through the setValue method
		 * must throw an Exception
		 */
		
		Parameter parameter = new Parameter("testParameter", 1);
		try {
			variableRow.setValue(parameter);
			fail();
		} catch (Exception e) {
		}
		
		/* 
		 * Creates a Variable and inserts it in the VariableRow
		 * as the value field. The operation must succeed
		 */
		
		Variable variable = new Variable("testVariable", 0, 1, 1);
		variableRow.setValue(variable);
		
		/*
		 * Retrieves the Variable contained in the VariableRow and checks its
		 * equality with the retrieved Variable
		 */
		
		Variable retrievedVariable = (Variable) variableRow.retrieveValue();
		
		assertTrue(retrievedVariable.toString().equals(variable.toString()));
	}
	
	@Test
	public final void testGetSetCoordinate() {
		int x = 10;
		variableRow.setX(x);
		assertEquals(x, variableRow.getX());
		
		int y = 5;
		variableRow.setY(y);
		assertEquals(y, variableRow.getY());
	}
}
