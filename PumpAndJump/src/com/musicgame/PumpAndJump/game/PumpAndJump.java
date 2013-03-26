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
	Screen gs;
	InputProcessor input;
	private static PreGame preGameThread;
	private static PostGame PostGameThread;
	private static RunningGame RunningGameThread;
	private static PauseGame PauseGameThread;

	@Override
	public void create()
	{
		gs = new GameScreen();
		input = new GameInput();
		setScreen(gs);
		Gdx.input.setInputProcessor(input);
		preGameThread = new PreGame();
		preGameThread.start();
	}

	/**
	 * Ends the previous thread and switches to the given thread
	 *
	 * preGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * Pause
	 * @param switchTo
	 */
	static void switchThread(String switchTo,GameThread currentThread)
	{
		currentThread.myWait();
	}

	/**
	 * Ends the given Thread
	 * preGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 * @param switchTo
	 */
	static void endThread(String switchTo)
	{

	}

	/**
	 * adds the given thread to the running thread list
	 *
	 * preGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * @param switchTo
	 */
	static void addThread(String switchTo)
	{

	}
}