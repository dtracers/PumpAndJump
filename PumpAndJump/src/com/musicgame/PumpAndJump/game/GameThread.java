package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.InputProcessor;

public abstract class GameThread extends Thread implements InputProcessor
{
	public abstract void pause();

	public abstract void startUp();

	public abstract void draw();

	public void myWait()
	{
		synchronized(this)
		{
			pause();
			try
			{
				this.wait();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
