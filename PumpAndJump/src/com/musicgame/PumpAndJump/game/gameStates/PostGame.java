package com.musicgame.PumpAndJump.game.gameStates;

import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.ThreadName;

public class PostGame extends GameThread
{

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

	@Override
	public ThreadName getThreadName()
	{
		return ThreadName.PostGame;
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void repause() {
	}

}
