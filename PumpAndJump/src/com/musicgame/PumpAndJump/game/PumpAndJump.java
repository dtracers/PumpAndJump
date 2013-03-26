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
	PreGame preGameThread;
	PostGame PostGameThread;
	RunningGame RunningGameThread;
	PauseGame PauseGameThread;
	RunningGame runningGameThread;

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
}