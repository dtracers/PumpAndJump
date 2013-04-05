package com.musicgame.PumpAndJump.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.musicgame.PumpAndJump.game.gameStates.DemoGame;
import com.musicgame.PumpAndJump.game.gameStates.PauseGame;
import com.musicgame.PumpAndJump.game.gameStates.PostGame;
import com.musicgame.PumpAndJump.game.gameStates.PreGame;
import com.musicgame.PumpAndJump.game.gameStates.RunningGame;
import com.musicgame.musicCompiler.MusicInputStream;

public class PumpAndJump extends Game
{
	public static MusicInputStream inputStream;
	Screen gameScreen;
	InputProcessor input;

	//MainMenuScreen menuScreen;

	static PumpAndJump instance;
	static Array<GameThread> runningThreads;

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
		instance = this;
		runningThreads = new Array<GameThread>();
		gameScreen = new GameScreen();
		this.setScreen(gameScreen);

		input = new GameInput();
		Gdx.input.setInputProcessor(input);

		initialize();

		switchThread(ThreadName.PreGame,null);

	}

	private void initialize()
	{
		preGameThread = new PreGame();
		postGameThread = new PostGame();
		runningGameThread = new RunningGame();
		pauseGameThread = new PauseGame();
		demoGameThread = new DemoGame();
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
		Gdx.input.setInputProcessor(temp);
	//	instance.setScreen(temp);
		temp.switchFrom(currentThread);
		clearThreads(currentThread);
		runningThreads.add(temp);

	}

	private static void clearThreads(GameThread currentThread)
	{
		for(GameThread running:runningThreads)
		{
			running.removeFrom(currentThread);
		}
		runningThreads.clear();
		runningThreads = new Array<GameThread>();
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
		runningThreads.add(temp);
		Gdx.input.setInputProcessor(temp);
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
		runningThreads.removeValue(temp, true);
		if(runningThreads.size>=1)
		{
			GameThread newTopThread = runningThreads.get(runningThreads.size-1);
			temp.switchFrom(newTopThread);
			Gdx.input.setInputProcessor(newTopThread);
		}
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
	private static GameThread getThread(ThreadName switchTo)
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

	public void dispose()
	{
		for(ThreadName name: ThreadName.values())
		{
			if(getThread(name)!=null)
				getThread(name).dispose();
		}
	}

	//the game threads are at the bottom
	private static PreGame preGameThread;
	private static PostGame postGameThread;
	private static RunningGame runningGameThread;
	private static PauseGame pauseGameThread;
	private static DemoGame demoGameThread;
}