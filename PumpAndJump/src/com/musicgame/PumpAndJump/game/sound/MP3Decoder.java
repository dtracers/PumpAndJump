package com.musicgame.PumpAndJump.game.sound;


import com.badlogic.gdx.audio.io.Decoder;
import com.badlogic.gdx.files.FileHandle;

public class MP3Decoder extends Decoder
{

	public MP3Decoder(FileHandle file)
	{

	}
	@Override
	public int readSamples(short[] paramArrayOfShort, int paramInt1,
			int paramInt2) {
		return 0;
	}

	@Override
	public int skipSamples(int paramInt) {
		return 0;
	}

	@Override
	public int getChannels() {
		return 0;
	}

	@Override
	public int getRate() {
		return 0;
	}

	@Override
	public float getLength() {
		return 0;
	}

	@Override
	public void dispose() {
	}

}
