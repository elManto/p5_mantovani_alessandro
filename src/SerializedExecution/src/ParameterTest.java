import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;


public class ParameterTest {
	static Param param;
	static String name;
	static float value;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		name = "parameter";
		value = 16;
		param = new Param(name, value);
	}

	@Test
	public final void testToString() {
		assertTrue(param.toString().equals(name + " " + value));
	}

	@Test
	public final void testGetValue() {
		assertEquals(param.getValue(), value, 0.0);
	}
	
	@Test
	public final void testGetName() {
		assertTrue(param.getName().equals(name));
	}
}
