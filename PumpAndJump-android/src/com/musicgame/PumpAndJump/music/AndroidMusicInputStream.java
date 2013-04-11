package com.musicgame.PumpAndJump.music;

import com.musicgame.PumpAndJump.Util.MusicInputStream;

public class AndroidMusicInputStream extends MusicInputStream
{

	@Override
	public MusicInputStream generateInstance() {
		return null;
	}

	@Override
	public MusicInputStream generateInstance(String fileName) {
		return null;
	}

	@Override
	public int readData(short[] samples, int offset, int numSamples) {
		return 0;
	}

}
