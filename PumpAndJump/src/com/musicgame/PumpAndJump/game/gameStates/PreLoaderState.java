package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.musicgame.PumpAndJump.Animation.Animation;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;

public class PreLoaderState extends GameThread
{
	RunningGame loadingThread;
	BitmapFont font;
	private ShapeRenderer shapeRenderer;
	@Override
	public void render(float delta)
	{
		batch.begin();
		font.draw(batch,"Number of Animations Loaded "+Animation.loadingAnimation,width/2, height/2);
		batch.end();

		float percent = loadingThread.loadingPercent/((float)loadingThread.maxLoading);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.begin(ShapeType.FilledRectangle);
		shapeRenderer.filledRect(0f, height/2.0f, Animation.loadingAnimation*20,50f , new Color(0.5f, 0.5f, 0.5f, 0.5f), new Color(1f, 0f, 0f, 0.5f), new Color(0.5f, 0.5f, 0.5f, 0.5f), new Color(0f, 0f, 1f, 0.75f));
		shapeRenderer.end();

		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public ThreadName getThreadName()
	{
		return ThreadName.PreLoaderState;
	}

	@Override
	public void unpause() {
	}

	@Override
	public void repause() {
	}

	@Override
	public void switchFrom(GameThread currentThread)
	{
	}

	/**
	 * Calls the reset method in a separate thread
	 */
	@Override
	public void run()
	{
		Animation.loadingAnimation = 0;
		loadingThread.longReset();
		PumpAndJump.removeThread(PreLoaderState.this.getThreadName(), PreLoaderState.this);
	}

	@Override
	public void addFrom(GameThread currentThread)
	{
		//which it will always be
		if(currentThread instanceof RunningGame)
		{
			width = Gdx.graphics.getWidth();height = Gdx.graphics.getHeight();
			if(font == null)
				font = new BitmapFont();
			font.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			shapeRenderer = new ShapeRenderer();
			loadingThread = ((RunningGame)currentThread);
			Thread d = new Thread(this);
			d.start();
		}
	}

	@Override
	public void removeFrom(GameThread currentThread)
	{
		PumpAndJump.setThreadToNull(getThreadName());
	}

}
