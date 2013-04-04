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
		
		assertNotNull(ErrorMessage+"Returning Null Array",Result);
		assertEquals(ErrorMessage+"Invalid Array Return Length",ExpResult.length,Result.length);
		for(int i=0;i<ExpResult.length;i++){
			assertEquals(ErrorMessage+"Values Improperly Set",ExpResult[i],Result[i],.00001);
		}
		
		exception.expect(RuntimeException.class);
		IntersectionUtil.Vector2ToFloat(null);
	}

	@Test
	public void testFloatToVector2() {
		float [] OddTest={0};
		float [] TestCase={1,2,3,4};
		Vector2[] ExpResult={new Vector2(1,2),new Vector2(3,4)};
		String ErrorMessage="IntersectionUtil::testFloatToVector2:";
		
		Vector2[] Result=IntersectionUtil.FloatToVector2(TestCase);
		assertEquals(ErrorMessage+"Invalid return length",ExpResult.length,Result.length);
		assertNotNull(ErrorMessage+"Returning Null Array",Result);
		for(int i=0;i<Result.length;i++){
			assertEquals(ErrorMessage+"Invalid x-Value in Array",ExpResult[i].x,Result[i].x,.00001);
			assertEquals(ErrorMessage+"Invalid y-Value in Array",ExpResult[i].y,Result[i].y,.00001);
		}
		
		assertNull(ErrorMessage+"Returning non-null values with odd length input arrays",IntersectionUtil.FloatToVector2(OddTest));
		
		exception.expect(RuntimeException.class);
		IntersectionUtil.FloatToVector2(null);
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
