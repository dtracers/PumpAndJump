package com.musicgame.PumpAndJump;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.music.DesktopMusicInputStream;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "PumpAndJump";
		cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 320;

		new LwjglApplication(new PumpAndJump(), cfg);

		PumpAndJump.inputStream = new DesktopMusicInputStream();
	}
}
