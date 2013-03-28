package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.musicgame.PumpAndJump.game.GameThread;

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
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void pause() {
	}

	@Override
	public void startUp() {
	}


	@Override
	public void stopThread() {
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void transferFrom(GameThread currentThread) {
	}

}
