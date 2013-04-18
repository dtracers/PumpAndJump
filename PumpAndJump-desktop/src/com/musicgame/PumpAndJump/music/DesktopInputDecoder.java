package com.musicgame.PumpAndJump.music;

import java.io.IOException;

import com.musicgame.PumpAndJump.game.sound.InputDecoder;

public class DesktopInputDecoder extends InputDecoder {

	public DesktopInputDecoder(double spectralScale, String file) {
		super(spectralScale, file);
	}

	@Override
	protected void createAudioStream(String file) {
	}

	@Override
	public float[] readEntireArray() {
		return null;
	}

	@Override
	public float[] readSeparately() throws IOException {
		return null;
	}


}
