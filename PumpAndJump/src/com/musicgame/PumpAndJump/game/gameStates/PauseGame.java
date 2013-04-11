package com.musicgame.PumpAndJump.game.gameStates;

import com.musicgame.PumpAndJump.game.GameThread;

/**
 * Is running when the game is paused
 * @author gigemjt
 *
 */
public class PauseGame extends GameThread
{

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
	public void render(float delta) {
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
	public void switchFrom(GameThread currentThread) {
	}

	@Override
	public void addFrom(GameThread currentThread) {
	}

	@Override
	public void removeFrom(GameThread currentThread) {
	}

	@Override
	public void unpause() {
	}

}
