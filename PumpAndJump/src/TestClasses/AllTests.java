package TestClasses;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestIntersectionUtil.class , TestLevelInterpreter.class})
public class AllTests {

}
