package com.musicgame.PumpAndJump.game;

/*import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;*/
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.Game;
import com.musicgame.PumpAndJump.game.gameStates.PauseGame;
import com.musicgame.PumpAndJump.game.gameStates.PostGame;
import com.musicgame.PumpAndJump.game.gameStates.PreGame;
import com.musicgame.PumpAndJump.game.gameStates.RunningGame;

public class PumpAndJump extends Game
{
	private static PumpAndJump instance;
	Screen gs;
	InputProcessor input;
	private static PreGame preGameThread;
	private static PostGame postGameThread;
	private static RunningGame runningGameThread;
	private static PauseGame pauseGameThread;

	@Override
	public void create()
	{
		gs = new GameScreen(this);
		input = new GameInput();
		setScreen(gs);
		Gdx.input.setInputProcessor(input);
		preGameThread = new PreGame();
		preGameThread.start();

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
	static void switchThread(String switchTo,GameThread currentThread)
	{
		GameThread temp = getThread(switchTo);
		temp.transferFrom(currentThread);
		PumpAndJump.instance.setScreen(temp);
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
			return postGameThread;
		}else if(switchTo.equalsIgnoreCase("PauseGame"))
		{
			return postGameThread;
		}
		return null;
	}


}