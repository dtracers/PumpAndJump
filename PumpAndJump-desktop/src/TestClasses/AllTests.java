package TestClasses;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.musicgame.PumpAndJump.game.PumpAndJump;

@RunWith(Suite.class)
@SuiteClasses({ TestIntersectionUtil.class , TestLevelInterpreter.class})
public class AllTests {
	private static LwjglApplication window;
	@BeforeClass
	public static void setUp(){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "PumpAndJump";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 600;

		window=new LwjglApplication(new PumpAndJump(), cfg);

		//PumpAndJump.inputStream = new DesktopInputDecoder(0, null);
	}
	@AfterClass
	public static void tearDown(){
		window.exit();
	}
}
