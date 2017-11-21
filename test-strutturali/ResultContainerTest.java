import static org.junit.Assert.assertNull;

import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.ResultContainer;

public class ResultContainerTest {
	private static ResultContainer rc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rc = new ResultContainer(null, null);
	}

	@Test
	public final void testGetRs() throws SQLException {
		assertNull(rc.getRs());
	}
}

