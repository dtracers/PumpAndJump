package com.musicgame.PumpAndJump;

import java.io.File;
import java.util.ArrayList;

public class LevelInterpreter
{
	/**
	 * Returns the list of the GameObjects that were loaded from the level
	 * @return
	 */
	public ArrayList<GameObject> loadLevel()
	{
		File f= new File("resources");
		for(File inside : f.listFiles())
		{
			if(inside.getName().endsWith("txt"))
			{
				System.out.println("I am a text file");
			}
		}
		return null;
	}
}
