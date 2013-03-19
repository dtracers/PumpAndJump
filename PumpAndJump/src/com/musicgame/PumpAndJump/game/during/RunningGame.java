package com.musicgame.PumpAndJump.game.during;

import com.badlogic.gdx.Gdx;
import com.musicgame.PumpAndJump.game.GameThread;

import android.graphics.Point;
public class RunningGame extends GameThread
{
	long time;
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


		 //when it wants to wait
		 myWait();

		 // do something important here, asynchronously to the rendering thread
		 // final Result result = createResult();
		 // post a Runnable to the rendering thread that processes the result
		 /*
		 Gdx.app.postRunnable(new Runnable()
		 {
			 @Override
			 public void run()
			 {
				 // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
				 // results.add(result);
			 }
		 });
		 */
	 }

}
