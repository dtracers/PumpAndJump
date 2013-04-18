package com.musicgame.PumpAndJump;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.gameStates.FileChooserState;
import com.musicgame.PumpAndJump.util.FileChooserDesktop;

public class Main {
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "PumpAndJump";
		cfg.useGL20 = false;
		cfg.width = 960;
		cfg.height = 540;

		new LwjglApplication(new PumpAndJump(), cfg);

		FileChooserState.fileDialog = new FileChooserDesktop(null,null);
	//	PumpAndJump.inputStream = new DesktopInputDecoder(0, null);
	//	PumpAndJump.outputStream = new DesktopInputDecoder(0, null);
	}
}