package com.musicgame.PumpAndJump;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.music.DesktopInputDecoder;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "PumpAndJump";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 600;

		new LwjglApplication(new PumpAndJump(), cfg);

		PumpAndJump.inputStream = new DesktopInputDecoder(0, null);
	//	PumpAndJump.outputStream = new DesktopInputDecoder(0, null);
	}
}