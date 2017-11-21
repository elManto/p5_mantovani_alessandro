import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import alessandromantovani.FileType;
import alessandromantovani.Model;

public class ModelTest {

	private static String modelFolder;
	
	private static int id;
	private static String modelName;
	private static String modelPath;
	private static boolean clicked;
	private static FileType fileType;
	
	private static Model model;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		id = 0;
		modelName = "EC1";
		modelFolder = "data" + File.separator;
		modelPath = modelFolder + modelName + ".jar";
		clicked = true;
		fileType = FileType.EC;
		
		model = new Model(id, modelName, modelPath, clicked, fileType);
	}

	@Test
	public final void testGetId() {
		assertEquals(model.getId(), id);
	}

	@Test
	public final void testGetName() {
		assertTrue(model.getName().equals(modelName));
	}

	@Test
	public final void testGetPath() {
		assertTrue(model.getPath().equals(modelPath));
	}
	
	@Test
	public final void testGetSetClicked() {
		boolean unclicked = !clicked;
		model.setClicked(unclicked);
		assertEquals(model.getClicked(), unclicked);
	}
	
	@Test
	public final void testGetFileType() {
		assertEquals(model.getFileType().ordinal(), fileType.ordinal());
	}
	
	@Test
	public final void testExist() throws IOException {	
		String testExistModelName = "TestExistEC";
		String testExistModelPath = modelFolder + testExistModelName + ".jar";
		
		File file = new File(testExistModelPath);
		file.createNewFile();
		
		Model testExistModel = new Model(id, testExistModelName,
				testExistModelPath, clicked, fileType);
		
		assertTrue(testExistModel.exist());
		assertTrue(file.delete());
	}	
	
	@Test
	public final void testToString() {
		assertNotNull(model.toString());
	}
}
