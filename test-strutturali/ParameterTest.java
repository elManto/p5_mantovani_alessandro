package alessandromantovanitest_strutturali;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.Parameter;

public class ParameterTest {
	static Parameter parameter;
	static String name;
	static float value;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		name = "parameter";
		value = 16;
		parameter = new Parameter(name, value);
	}

	@Test
	public final void testToString() {
		assertTrue(parameter.toString().equals(name + " " + value));
	}

	@Test
	public final void testGetValue() {
		assertEquals(parameter.getValue(), value, 0.0);
	}
	
	@Test
	public final void testGetName() {
		assertTrue(parameter.getName().equals(name));
	}
}
