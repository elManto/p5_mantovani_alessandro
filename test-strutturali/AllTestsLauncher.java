import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ConfigurationManagerTest.class, DatabaseTest.class,
		FileManagerTest.class,
		ModelTest.class, OutputTest.class, ParameterRowTest.class,
		ParameterTest.class, ResultContainerTest.class, RunManagerTest.class, 
		SerializerTest.class, VariableRowTest.class, VariableTest.class })
public class AllTestsLauncher {

}
