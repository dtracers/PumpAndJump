package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

public class GameScreen implements Screen
{
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		for(GameThread thread:PumpAndJump.runningThreads)
		{
			thread.render(delta);
		}
	}

	@Override
	public void resize(int width, int height)
	{
		for(GameThread thread:PumpAndJump.runningThreads)
		{
			thread.resize(width,height);
		}
	}

	@Override
	public void show()
	{
		resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	}

	@Override
	public void hide()
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void dispose()
	{

	}

}
