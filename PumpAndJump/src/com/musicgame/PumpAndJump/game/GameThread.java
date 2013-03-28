package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

public abstract class GameThread extends Thread implements InputProcessor, Screen
{
	public int height,width;

	@Override
	public void resize(int width, int height)
	{
		this.height = height;
		this.width = width;
	}

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

	/**
	 * The calling object will get whatever ever needed information from the given object
	 * @param currentThread
	 */
	public abstract void transferFrom(GameThread currentThread);
}
