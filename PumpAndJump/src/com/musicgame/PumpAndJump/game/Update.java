package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.Gdx;

public class Update extends Thread
{
	boolean guyRunning = false;
	 @Override
	   public void run() {
	      // do something important here, asynchronously to the rendering thread
	    //  final Result result = createResult();
	      // post a Runnable to the rendering thread that processes the result
	      Gdx.app.postRunnable(new Runnable() {
	         @Override
	         public void run() {
	            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
	     //       results.add(result);
	         }
	      });
	   }
}
