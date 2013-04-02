package com.musicgame.PumpAndJump.game;


import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.musicgame.PumpAndJump.game.gameStates.DemoGame;
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
	private static DemoGame demoGameThread = new DemoGame();

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
		switchThread(ThreadName.PreGame,null);
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
	public static void switchThread(ThreadName switchTo,GameThread currentThread)
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
	public static void addThread(ThreadName switchTo,GameThread currentThread)
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
	public static void removeThread(ThreadName switchTo,GameThread currentThread)
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
	static GameThread getThread(ThreadName switchTo)
	{
		switch(switchTo)
		{
			case  PreGame:		return preGameThread;
			case  PostGame:		return postGameThread;
			case  RunningGame:	return runningGameThread;
			case  PauseGame:	return pauseGameThread;
			case  DemoGame:		return demoGameThread;
		}
		return null;
	}
}