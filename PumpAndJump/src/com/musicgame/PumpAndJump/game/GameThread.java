package com.musicgame.PumpAndJump.game;

public abstract class GameThread extends Thread
{
	public void myWait()
	{
		synchronized(this)
		{
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
