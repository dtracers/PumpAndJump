package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.InputProcessor;

public class GameInput implements InputProcessor
{

	@Override
	public boolean keyDown(int keycode)
	{
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.keyDown(keycode))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.keyUp(keycode))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.keyTyped(character))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.touchDown(screenX, screenY, pointer, button))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.touchUp(screenX, screenY, pointer, button))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.touchDragged(screenX, screenY, pointer))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.mouseMoved(screenX, screenY))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		for(GameThread thread: PumpAndJump.runningThreads)
		{
			if(thread.scrolled(amount))
			{
				return true;
			}
		}
		return false;
	}

}
