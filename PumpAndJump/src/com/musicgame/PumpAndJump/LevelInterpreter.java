package com.musicgame.PumpAndJump;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class LevelInterpreter
{
	/**
	 * Returns the gameobject specfied in the string inputline
	 * Input line is supposed to begin with either the characters j or s
	 * to indicate a jump or a slide obstacle respectively.
	 * This is followed by a double to indicate
	 * the time to place the obstacle down.
	 * If  slide obstacle then additional
	 * double added to indicate when slide
	 * should finish.
	 * If inputline is an invalid string then returns null
	 * @param inputLine
	 * @return
	 */
	public GameObject getNextObject(String inputLine){
		String jumpPattern="j \\d+(\\.\\d+)? *";
		String slidePattern="s \\d+(\\.\\d+)? \\d+(\\.\\d+)? *";
		if(!inputLine.matches(jumpPattern)||!inputLine.matches(slidePattern))
			return null;
		if(inputLine.charAt(0)=='j'){
			return null;
		}
		else if(inputLine.charAt(0)=='s'){
			return null;
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
