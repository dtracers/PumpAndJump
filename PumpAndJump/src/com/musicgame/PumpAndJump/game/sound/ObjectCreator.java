package com.musicgame.PumpAndJump.game.sound;

import java.util.ArrayList;
import java.util.Random;

import com.musicgame.PumpAndJump.DuckObstacle;
import com.musicgame.PumpAndJump.JumpObstacle;
import com.musicgame.PumpAndJump.Obstacle;

public class ObjectCreator
{
	double timeSinceLastObjectEnded = 0;
	public static final double maxObjectLength = .75;//in seconds
	public static final double minObjectLength = 0.3;//in seconds
	public static final double maxDistanceBetweenObjects = 5;//in seconds
	public static final double minDistanceBetweenObjects = 2;//in seconds


	//temp values
	double timeSinceLastDuckEnded = 0;
	double timeSinceLastJumpEnded = 0;

	public void createObjects(ArrayList<Obstacle> objects,double currentTime)
	{
		createDuckObject(objects,currentTime,currentTime+maxObjectLength);
		createJumpObject(objects,currentTime,currentTime+minObjectLength);
	}

	public void createDuckObject(ArrayList<Obstacle> objects,double start,double end)
	{
		if(start-timeSinceLastDuckEnded>=minDistanceBetweenObjects)
		{

			timeSinceLastDuckEnded = end;
			DuckObstacle h = new DuckObstacle((float)start, (float)(end));
			objects.add(h);
		}
	}

	public void createJumpObject(ArrayList<Obstacle> objects,double start,double end)
	{
		if(start-timeSinceLastJumpEnded>=maxDistanceBetweenObjects)
		{
			System.out.println("Creating object at "+start);
			timeSinceLastJumpEnded = end;
			JumpObstacle h = new JumpObstacle((float)start, (float)(end));
			objects.add(h);
		}
	}
}
