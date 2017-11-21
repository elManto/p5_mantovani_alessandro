import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.Parameter;
import alessandromantovani.ParameterRow;
import alessandromantovani.Variable;

public class ParameterRowTest {
	private static ParameterRow parameterRow;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parameterRow = new ParameterRow(0, 0);
	}

	@Test
	public final void testGetSetRetrieveField() throws Exception {
		/*
		 * Creates a Variable and tries to insert it in the ParameterRow
		 * as the value field. The insertion through the setValue method
		 * must throw an Exception
		 */
		
		Variable variable = new Variable("testVariable", 0, 1, 1);
		try {
			parameterRow.setValue(variable);
			fail();
		} catch (Exception e) {
		}
		
		/* 
		 * Creates a Parameter and inserts it in the ParameterRow
		 * as the value field. The operation must succeed
		 */
		
		Parameter parameter = new Parameter("testParameter", 1);
		parameterRow.setValue(parameter);
		
		/*
		 * Retrieves the Parameter contained in the ParameterRow and checks its
		 * equality with the retrieved Parameter
		 */
		
		Parameter retrievedParameter = (Parameter) parameterRow.retrieveValue();
		
		assertTrue(retrievedParameter.toString().equals(parameter.toString()));
	}
	
	@Test
	public final void testGetSetCoordinate() {
		int x = 10;
		parameterRow.setX(x);
		assertEquals(x, parameterRow.getX());
		
		int y = 5;
		parameterRow.setY(y);
		assertEquals(y, parameterRow.getY());
	}
}
