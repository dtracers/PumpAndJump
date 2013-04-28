package com.musicgame.PumpAndJump.game.sound;

import java.util.ArrayList;

import com.musicgame.PumpAndJump.DuckObstacle;
import com.musicgame.PumpAndJump.JumpObstacle;
import com.musicgame.PumpAndJump.Obstacle;

public class ObjectCreator
{
	ArrayList<SignificantItem> importantItems;
	ArrayList<Obstacle> objects;
	double timeSinceLastObjectEnded = 0;
	public static final double maxObjectLength = .75;//in seconds
	public static final double minObjectLength = 0.3;//in seconds
	public static final double maxDistanceBetweenObjects = 5;//in seconds
	public static final double minDistanceBetweenObjects = 1;//in seconds


	//temp values
	double timeSinceLastDuckEnded = 0;
	double timeSinceLastJumpEnded = 0;

	int currentIndex;
	int realIndex;
	int numberOfBeatsToCheck = 20;

	public void createObjects()
	{
		int endingIndex = importantItems.size();
		SignificantItem i = importantItems.get(currentIndex);
		double timeSince = i.timeIndex-timeSinceLastObjectEnded;

		if(timeSince>minDistanceBetweenObjects||timeSinceLastObjectEnded ==0)
		{
			System.out.println("I am able to create an object");
			double startTime = i.timeIndex;
			double endingTime = i.timeIndex;

			while(endingTime-startTime<minObjectLength&&endingTime-startTime<maxObjectLength&&currentIndex<endingIndex)
			{
				endingTime = importantItems.get(currentIndex).timeIndex;
				currentIndex++;
			}

			System.out.println("Creating object with\n"+startTime+"\n"+endingTime);

			timeSinceLastObjectEnded = endingTime;

			Obstacle create;
			//we need to create an object!
			if((i.soundIntensity)%10>5)
			{
				System.out.println("I am a jump object");
				create = new JumpObstacle((float)startTime,(float)endingTime);
				//create jump
			}else
			{
				System.out.println("I am a duck object");
				create = new DuckObstacle((float)startTime,(float)endingTime);
			}

			int index = objects.indexOf(i.associatedBeat);
			System.out.println("Ading at "+index);
			if(index>0)
			{
				objects.add(index-1,create);
			}else
			{
				objects.add(0,create);
			}

		}
		currentIndex++;
		if(currentIndex>=endingIndex)
		{
			currentIndex = endingIndex;
		}
	}

	/*
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
	*/
}
