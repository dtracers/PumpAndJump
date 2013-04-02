package com.musicgame.PumpAndJump.game;


import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.musicgame.PumpAndJump.game.gameStates.PauseGame;
import com.musicgame.PumpAndJump.game.gameStates.PostGame;
import com.musicgame.PumpAndJump.game.gameStates.PreGame;
import com.musicgame.PumpAndJump.game.gameStates.RunningGame;

public class PumpAndJump extends Game
{
	//Screen gameScreen;
	//MainMenuScreen menuScreen;

	static ArrayList<GameThread> runningThreads;

	private static PumpAndJump instance;
	private static PreGame preGameThread = new PreGame();
	private static PostGame postGameThread = new PostGame();
	private static RunningGame runningGameThread = new RunningGame();
	private static PauseGame pauseGameThread = new PauseGame();

	/*
	@Override
	public void create()
	{
		gameScreen= new GameScreen();
		menuScreen = new MainMenuScreen(this);
		setScreen(menuScreen);

	}
	*/
	@Override
	public void create()
	{
		switchThread("PreGame",null);
	}

	/**
	 * Ends the previous thread and switches to the given thread
	 *
	 * PreGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * Pause
	 * @param switchTo
	 */
	public static void switchThread(String switchTo,GameThread currentThread)
	{
		GameThread temp = getThread(switchTo);
		temp.switchFrom(currentThread);
		Gdx.input.setInputProcessor(temp);
		runningThreads.clear();
	}

	/**
	 * add the thread to the existing set of threads
	 *
	 * PreGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * Pause
	 * @param switchTo
	 */
	public static void addThread(String switchTo,GameThread currentThread)
	{
		GameThread temp = getThread(switchTo);
		temp.addFrom(currentThread);
		Gdx.input.setInputProcessor(temp);
		runningThreads.add(temp);
	}

	/**
	 * remove the thread from the existing set of threads
	 *
	 * PreGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * Pause
	 * @param switchTo
	 */
	public static void removeThread(String switchTo,GameThread currentThread)
	{
		GameThread temp = getThread(switchTo);
		temp.removeFrom(currentThread);
		Gdx.input.setInputProcessor(temp);
		runningThreads.remove(temp);
	}

	/**
	 *
	 * PreGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * @param switchTo
	 * @return
	 */
	static GameThread getThread(String switchTo)
	{
		if(switchTo.equalsIgnoreCase("PreGame"))
		{
			return preGameThread;
		}else if(switchTo.equalsIgnoreCase("PostGame"))
		{
			return postGameThread;
		}else if(switchTo.equalsIgnoreCase("RunningGame"))
		{
			return runningGameThread;
		}else if(switchTo.equalsIgnoreCase("PauseGame"))
		{
			return pauseGameThread;
		}
		return null;
	}


}