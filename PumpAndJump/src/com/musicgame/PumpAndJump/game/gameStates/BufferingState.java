package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;

public class BufferingState extends GameThread
{
	static Texture bufferingImage = new Texture(Gdx.files.internal("buffering.png"));
	float x,y;
	long position;
	Thread runMethod;
	long startTime;
	long delay = 5000;
	boolean threadStarted;
	long counter = 0;
	BitmapFont font;

	public BufferingState()
	{
	}

	@Override
	public void render(float delta)
	{
		x = 500.0f+20.0f*com.badlogic.gdx.math.MathUtils.cos(position/(MathUtils.PI*2.0f));
		y = 500.0f+20.0f*com.badlogic.gdx.math.MathUtils.sin(position/(MathUtils.PI*2.0f));

		batch.begin();
		font.draw(batch,"Counting "+counter,500, 500);
		batch.draw(bufferingImage, x,y);
		batch.end();
		position++;
	//	System.out.println("Buffering! " +x+" "+y);
	}

	@Override
	public void unpause()
	{
	}

	@Override
	public void switchFrom(GameThread currentThread)
	{
	}


	@Override
	public void addFrom(GameThread currentThread)
	{

		if(currentThread instanceof RunningGame)
		{
			final RunningGame game = (RunningGame)currentThread;
			//System.out.println("ADDING THE BUFFER THREAD!!!! YAYYYY");
			runMethod = new Thread()
			{
				public void run()
				{
					game.streamer.buffering = true;
					counter = 0;
					startTime = System.currentTimeMillis();
					while(game.streamer.bufferingNeeded())
					{
						System.out.println("Buffering");
						counter = game.streamer.minBufferingDistance();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					game.streamer.buffering = false;
					PumpAndJump.removeThread(ThreadName.BufferingState, BufferingState.this);
				}
			};
			runMethod.start();
		}
	}

	@Override
	public void removeFrom(GameThread currentThread)
	{
	}

	@Override
	public ThreadName getThreadName()
	{
		return ThreadName.BufferingState;
	}

	@Override
	public void repause() {
	}

}
