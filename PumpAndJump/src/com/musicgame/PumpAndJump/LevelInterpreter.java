package com.musicgame.PumpAndJump;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class LevelInterpreter
{
	/**
	 * Returns the GameObject specified in inputLine
	 * Input line is supposed to begin with either the characters j or s
	 * to indicate a jump or a slide obstacle respectively.
	 * This is followed by a double to indicate
	 * the time to place the obstacle down.
	 * If  slide obstacle then additional
	 * double added to indicate when slide
	 * should finish.
	 * If inputLine is an invalid string then returns null
	 * If inputLine is null throws runtimeException
	 * @param inputLine
	 * @return
	 */
	public GameObject getNextObject(String inputLine){
		String jumpPattern="j \\d+(\\.\\d+)?\\s*";
		String slidePattern="s \\d+(\\.\\d+)? \\d+(\\.\\d+)?\\s*";
		if(inputLine.matches(jumpPattern)){
			String[] input=inputLine.split(" ");
			double startTime=Double.parseDouble(input[1]);
			return new JumpObstacle(startTime);
		}
		if(inputLine.matches(slidePattern)){
			String[] input=inputLine.split(" ");
			double startTime=Double.parseDouble(input[1]);
			double endTime=Double.parseDouble(input[2]);
			return new DuckObstacle(startTime,endTime);
		}
		return null;
	}
	/**
	 * Returns the list of the GameObjects that were loaded from the level
	 * Will return an empty array list if no file is found.
	 * Precondition: The first txt file in the resources folder is the level data
	 * @return
	 * @throws FileNotFoundException 
	 */
	public ArrayList<GameObject> loadLevel() throws FileNotFoundException
	{
		ArrayList<GameObject> Level=new ArrayList <GameObject>();
		File f= new File("resources");
		for(File inside : f.listFiles())
		{
			if(inside.getName().endsWith("txt"))
			{
				Scanner LevelIn = new Scanner(inside);
				while(LevelIn.hasNextLine())
					Level.add(getNextObject(LevelIn.nextLine()));
				break;
			}
		}
		return Level;
	}
}
