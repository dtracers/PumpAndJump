package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.io.WavDecoder;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.musicCompiler.MusicCompiler;

public class RunningGame extends GameThread
{
	MusicCompiler compiler;
	long time;
	long start = 0;
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
		 start = System.currentTimeMillis();
		 while(true)
		 {
			 time = System.currentTimeMillis() - start;
			 if(toWait)
				 myWait();
			 try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
	 }

	@Override
	public boolean keyDown(int keycode) {
		myNotify();
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
		toWait = true;
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
		System.out.println(time);
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
			this.notify();
		}
		if(currentThread instanceof PreGame)
		{
			compiler = new MusicCompiler();
			this.start();
		}
			//mysounddecoder = new WavDecoder(Gdx.files.internal("drop.wav"));
	}


	@Override
	public void addFrom(GameThread currentThread)
	{
	}

	@Override
	public void removeFrom(GameThread currentThread) {
	}

	@Override
	public void unpause() {
		toWait = false;
	}

}
