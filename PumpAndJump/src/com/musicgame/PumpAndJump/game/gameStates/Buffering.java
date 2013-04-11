package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;

public class Buffering extends GameThread
{
	Texture dropImage = new Texture(Gdx.files.internal("droplet.png"));
	SpriteBatch batch = new SpriteBatch();
	OrthographicCamera camera = new OrthographicCamera();
	float x,y;
	long position;
	Thread runMethod;
	long startTime;
	long delay = 5000;
	boolean threadStarted;

	public Buffering()
	{
		camera.setToOrtho(false, 800, 480);
	}

	@Override
	public void render(float delta)
	{
		x = 20*com.badlogic.gdx.math.MathUtils.cos((float) (position*MathUtils.PI*2.0));
		y = 20*com.badlogic.gdx.math.MathUtils.sin((float) (position*MathUtils.PI*2.0));
		 camera.update();

	    // tell the SpriteBatch to render in the
	    // coordinate system specified by the camera.
	    batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(dropImage, x,y);
		batch.end();
		position++;
		System.out.println("Buffering!");
	}

	@Override
	public void show()
	{
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
	public void dispose()
	{
	}

	@Override
	public void unpause()
	{
	}

	@Override
	public boolean keyDown(int keycode) {return false;}

	@Override
	public boolean keyUp(int keycode) {return false;}

	@Override
	public boolean keyTyped(char character) {return false;}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {return false;}

	@Override
	public boolean scrolled(int amount) {return false;}


	@Override
	public void switchFrom(GameThread currentThread)
	{
	}


	@Override
	public void addFrom(GameThread currentThread)
	{
		if(currentThread instanceof RunningGame)
		{

			runMethod = new Thread()
			{
				public void run()
				{
					startTime = System.currentTimeMillis();
					while(System.currentTimeMillis()-delay>startTime)
					{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					PumpAndJump.removeThread(ThreadName.Buffering, Buffering.this);
				}
			};
			runMethod.start();
		}
	}

	@Override
	public void removeFrom(GameThread currentThread)
	{
	}

}
