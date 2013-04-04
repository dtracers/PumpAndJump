package TestClasses;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.badlogic.gdx.math.Vector2;

import com.musicgame.PumpAndJump.Util.IntersectionUtil;

public class TestIntersectionUtil {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Test
	public void testVector2ToFloat() {
		Vector2[] TestCase={new Vector2(1,2),new Vector2(3,4)};
		float [] ExpResult={1,2,3,4};
		String ErrorMessage="IntersectionUtil::testVector2ToFloat-";
		
		float [] Result=IntersectionUtil.Vector2ToFloat(TestCase);
		
		assertEquals(ErrorMessage+"Invalid Array Return Length",ExpResult.length,Result.length);
		assertNotNull(ErrorMessage+"Returning Null Array");
		for(int i=0;i<ExpResult.length;i++){
			assertEquals(ErrorMessage+"Values Improperly Set",ExpResult[i],Result[i],.00001);
		}
		
		exception.expect(RuntimeException.class);
		IntersectionUtil.Vector2ToFloat(null);
	}

	@Test
	public void testFloatToVector2() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateConvexHull() {
		fail("Not yet implemented");
	}

	@Test
	public void testGrahamScan() {
		fail("Not yet implemented");
	}

}
