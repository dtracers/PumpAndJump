package TestClasses;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import com.musicgame.PumpAndJump.Util.IntersectionUtil;

public class TestIntersectionUtil {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Test
	public void testVector2ToFloat() {
		Vector2[] TestCase={new Vector2(1.0f,2.0f),new Vector2(3.0f,4.0f)};
		float [] ExpResult={1.0f,2.0f,3.0f,4.0f};
		String ErrorMessage="IntersectionUtil::Vector2ToFloat-";
		
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
		float [] TestCase={1.0f,2.0f,3.0f,4.0f};
		Vector2[] ExpResult={new Vector2(1.0f,2.0f),new Vector2(3.0f,4.0f)};
		String ErrorMessage="IntersectionUtil::FloatToVector2:";
		
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
		//need more info to create Valid test case
		float [] TestArray={1.0f,2.0f,3.0f,4.0f};
		Polygon TestCase=new Polygon(TestArray);
		String ErrorMessage="IntersectionUtil::CreateConvexHull-";
		float [] Result=IntersectionUtil.createConvexHull(TestCase);
		
		assertTrue(ErrorMessage+" Invalid return array length",Result.length<=TestArray.length);
		exception.expect(RuntimeException.class);
		IntersectionUtil.createConvexHull(null);
	}

	@Test
	public void testGrahamScan() {
		Vector2 [] tooFew={new Vector2(0.0f,0.0f)};
		String ErrorMessage="IntersectionUtil::GrahamScan-";
		
		assertNull(ErrorMessage+" Non null returned for too few points.",IntersectionUtil.grahamScan(tooFew));
		exception.expect(RuntimeException.class);
		IntersectionUtil.grahamScan(null);
	}

}
