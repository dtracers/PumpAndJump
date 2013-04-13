package TestClasses;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.musicgame.PumpAndJump.DuckObstacle;
import com.musicgame.PumpAndJump.GameObject;
import com.musicgame.PumpAndJump.JumpObstacle;
import com.musicgame.PumpAndJump.LevelInterpreter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestLevelInterpreter {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Test
	public void testGetNextObject() {
		String jumpTestCase="j 1.2";
		String slideTestCase="s 1.2 3.4";
		JumpObstacle expectedJumpOutput=new JumpObstacle(1.2);
		DuckObstacle expectedSlideOutput=new DuckObstacle(1.2,3.4);
		
		GameObject jumpResult=LevelInterpreter.getNextObject(jumpTestCase);
		assertTrue("Didn't return a jump obstacle for jump input",jumpResult instanceof JumpObstacle);
		assertEquals("Didn't read in the right jump start time",((JumpObstacle)jumpResult).getStartTime(),expectedJumpOutput.getStartTime(),.00001);

		GameObject slideResult=LevelInterpreter.getNextObject(slideTestCase);
		assertTrue(slideResult instanceof DuckObstacle);
		assertEquals("Didn't read in the right slide start time",((DuckObstacle)slideResult).getStartTime(),expectedSlideOutput.getStartTime(),.00001);
		assertEquals("Didn't read in the right slide end time",((DuckObstacle)slideResult).getEndTime(),expectedSlideOutput.getEndTime(),.00001);
		
		assertNull("Returned non null value for invalid input",LevelInterpreter.getNextObject(""));
		exception.expect(RuntimeException.class);
		LevelInterpreter.getNextObject(null);
	}
	/*
	@Test
	public void testLoadLevel(){
		String jumpTest="j 1.2",slideTest="s 1.2 3.4",garbageTest="garbage";
		try{
			FileWriter writer=new FileWriter("resources/11111.txt");
			
			writer.write(jumpTest+"\n");
			writer.append(slideTest+"\n");
			writer.append(garbageTest);
			writer.flush();
	
			ArrayList <GameObject> result =	LevelInterpreter.loadLevel();
			System.out.println(result.size());
			assertEquals("Invalid Return Size",result.size(),2);
			assertTrue("Incorrect gameObject type",result.get(0) instanceof JumpObstacle);
			assertTrue("Incorrect gameObject type",result.get(1) instanceof DuckObstacle);
			assertEquals("Invalid Time(Jump)",((JumpObstacle)result.get(0)).getStartTime(),1.2,0);
			assertEquals("Invalid Time(Slide Start)",((DuckObstacle)result.get(1)).getStartTime(),1.2,0);
			assertEquals("Invalid Time(Slide Start)",((DuckObstacle)result.get(1)).getEndTime(),3.4,0);
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	*/
}
