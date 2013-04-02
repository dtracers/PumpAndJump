package com.musicgame.PumpAndJump.game;


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
		temp.transferFrom(currentThread);
		Gdx.input.setInputProcessor(temp);
	//	PumpAndJump.instance.setScreen(temp);
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