package com.musicgame.PumpAndJump.game.sound;

import java.util.ArrayList;

import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;
import com.musicgame.PumpAndJump.objects.Beat;
import com.musicgame.PumpAndJump.objects.DuckObstacle;
import com.musicgame.PumpAndJump.objects.JumpObstacle;
import com.musicgame.PumpAndJump.objects.ObjectHandler;
import com.musicgame.PumpAndJump.objects.Obstacle;

public class ObjectCreator
{
	ArrayList<SignificantItem> importantItems;
	ObjectHandler objects;
	double timeSinceLastObjectEnded = 0;
	public static final double maxObjectLength = .6;//in seconds
	public static final double minObjectLength = 0.2;//in seconds
	public static final double maxDistanceBetweenObjects = 5;//in seconds
	public static final double minDistanceBetweenObjects = .5;//in seconds


	Beat start = null;
	Beat end = null;
	double startObjectTime = 0;
	double endObjectTime = 0;
	boolean readyForObjectCreation = false;

	int currentIndex;
	int realIndex;
	int numberOfBeatsToCheck = 20;

	public void createObjects()
	{
		int endingIndex = importantItems.size();
		SignificantItem i = importantItems.get(currentIndex);
		double timeSince = i.timeIndex-timeSinceLastObjectEnded;

		if(!readyForObjectCreation&&(timeSince>minDistanceBetweenObjects||timeSinceLastObjectEnded ==0))
		{
			readyForObjectCreation = true;

			startObjectTime = i.timeIndex;
			endObjectTime = i.timeIndex;

			start = i.associatedBeat;

		}

		if(readyForObjectCreation&&i.timeIndex-startObjectTime>=minObjectLength)
		{
			readyForObjectCreation = false;
			endObjectTime = i.timeIndex;

			end = i.associatedBeat;

			if(endObjectTime -startObjectTime >= maxObjectLength)
			{
				endObjectTime = startObjectTime+maxObjectLength;
			}

//			System.out.println("Creating object with\n"+startObjectTime+"\n"+endObjectTime);

			timeSinceLastObjectEnded = endObjectTime;

			Obstacle create;
			boolean jump = (i.soundIntensity)%10>5;
			//we need to create an object!
			if(jump)
			{
//				System.out.println("I am a jump object");
				create = new JumpObstacle((float)startObjectTime,(float)endObjectTime);
				//create jump
			}else
			{
//				System.out.println("I am a duck object");
				create = new DuckObstacle((float)startObjectTime,(float)endObjectTime);
			}

			int startIndex = objects.actualObjects.indexOf(start);
			int endIndex = objects.actualObjects.indexOf(end);
			adjustBeatHeight(startIndex,endIndex,jump);

//			System.out.println("Ading at "+index);
			if(startIndex>0)
			{
				objects.add(startIndex-1,create);
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

	private void adjustBeatHeight(int startIndex, int endIndex, boolean jump)
	{
		Point jumpPoint = new Point(0,20,0);
		Point duckPoint= new Point(0,-20,0);
		for(int k = startIndex;k<=endIndex;k++)
		{
			objects.actualObjects.get(k).translate(0,jump?30:-30,0);
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
