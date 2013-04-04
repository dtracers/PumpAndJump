package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.io.WavDecoder;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.musicCompiler.MusicCompiler;

public class RunningGame extends GameThread
{
	MusicCompiler compiler;
	long time;
	boolean toWait = false;
	boolean jumping = false,ducking = false;
	boolean paused = false;
	public RunningGame()
	{
		time = 0;
	}

	public synchronized void resetTime()
	{
		time = 0;
	}

	public synchronized long getTimeLocation()
	{
		return time;
	}

	/**
	 * Run method happens while the game is running
	 */
	 @Override
	 public void run()
	 {
		 time = System.currentTimeMillis();

		 if(toWait)
			 myWait();
	 }

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

	@Override
	public void pause()
	{
		toWait = true;
	}

	@Override
	public void render(float delta)
	{
	}

	@Override
	public void show()
	{
		toWait = false;
	}

	@Override
	public void hide()
	{
		toWait = true;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void switchFrom(GameThread currentThread)
	{
		if(currentThread instanceof PauseGame && paused)
		{
			paused = false;
		}
		if(currentThread instanceof PreGame)
		{
			compiler = new MusicCompiler();
		}
			//mysounddecoder = new WavDecoder(Gdx.files.internal("drop.wav"));
	}


	@Override
	public void addFrom(GameThread currentThread) {
	}

	@Override
	public void removeFrom(GameThread currentThread) {
	}

}
