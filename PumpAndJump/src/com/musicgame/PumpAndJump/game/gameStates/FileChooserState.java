package com.musicgame.PumpAndJump.game.gameStates;

import com.musicgame.PumpAndJump.Util.FileChooser;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.ThreadName;

public class FileChooserState extends GameThread
{
	public static FileChooser fileDialog;
	//need to make a file chooser
	@Override
	public void render(float delta)
	{
		//will draw all of the files that are available
	}

	@Override
	public ThreadName getThreadName() {
		return null;
	}

	@Override
	public void unpause() {
	}

	@Override
	public void repause() {
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

}
