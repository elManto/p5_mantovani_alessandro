package alessandromantovanitest_strutturali;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import alessandromantovani.Variable;

@RunWith(Parameterized.class)
public class VariableTest {
	
	private static Variable variable;

	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][]{
			{"v1", 1, 3, 1},
			{"v2", -1, 4, 1},
			{"v3", 10, 30.5f, 7},
			{"v4", 1, 1, 4},
			{"v5", 10, 30, 30},
		});
	}
	
	@Parameter(value = 0)
    public String name;
	
	@Parameter(value = 1)
    public float start;
	
	@Parameter(value = 2)
    public float end;
	
	@Parameter(value = 3)
    public float step;
	
	@Before
	public void setUp() throws Exception {
		variable = new Variable(name, start, end, step);
	}
		
	@Test
	public final void testStartIteration() {
		assertEquals(start, variable.startIteration(), 0.0);
	}
	
	/**
	 * Tests that the nextValue method of Variable
	 * provides the correct value for each step of
	 * the iteration.
	 */
	
	@Test
	public final void testNextValue() {
		variable.startIteration();
		
		float count = (int) Math.ceil(((end - start) / step));
		float currentValue = variable.getStart();
		float expectedValue = start;
		
		for (int i = 0; i < count; i++) {
			assertEquals(expectedValue, currentValue, 0.0);
			assertTrue(currentValue <= end);
			assertTrue(currentValue >= start);
			assertEquals(0, ((currentValue - start) % step), 0.0);
			
			currentValue = variable.nextValue();
			expectedValue += step;		
		}
	}
	
	/**
	 * Tests that the getCurrentValue method of Variable
	 * always returns a proper value, i.e. it is rounded
	 * near the bounds of the variation intervals and
	 * is consistent with the the state of the iteration.
	 */
	
	@Test
	public final void testGetCurrentValue() {
		variable.startIteration();
		
		assertEquals(start, variable.getCurrentValue(), 0.0);
		variable.nextValue();
		
		if (start + step <= end)
			assertEquals(start + step, variable.getCurrentValue(), 0.0);
		else
			assertEquals(end, variable.getCurrentValue(), 0.0);
	}

	@Test
	public final void testHasEnded() {
		variable.startIteration();
		if (start != end){
			assertFalse(variable.hasEnded());
			testNextValue();
		}
		assertTrue(variable.hasEnded());
	}

	@Test
	public final void testGetStart() {
		assertEquals(variable.getStart(), start, 0.0);
	}

	@Test
	public final void testGetEnd() {
		assertEquals(variable.getEnd(), end, 0.0);
	}

	@Test
	public final void testGetStep() {
		assertEquals(variable.getStep(), step, 0.0);
	}
	
	@Test
	public final void testToString() {
		assertNotNull(variable.toString());
	}
	
	@Test
	public final void testGetName() {
		assertTrue(variable.getName().equals(name));
	}
}