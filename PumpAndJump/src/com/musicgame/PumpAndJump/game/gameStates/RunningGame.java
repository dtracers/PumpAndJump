package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.musicgame.PumpAndJump.game.GameThread;

public class RunningGame extends GameThread
{
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
	public void transferFrom(GameThread currentThread)
	{
		if(currentThread instanceof PauseGame && paused)
		{
			paused = false;
		}
	}

}
