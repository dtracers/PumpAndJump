package com.musicgame.PumpAndJump.game.sound;

import com.badlogic.gdx.audio.io.Decoder;

import io.InputDevice;

public class LibGDXInputDevice implements InputDevice
{
	Decoder decoder;
	public LibGDXInputDevice(Decoder d)
	{
		decoder = d;
	}
	@Override
	public int readSamples(short[] samples, int offset, int numSamples) {
		return decoder.readSamples(samples, offset, numSamples);
	}

	@Override
	public int getChannels() {
		return decoder.getChannels();
	}

	@Override
	public float getLength() {
		return decoder.getLength();
	}

	@Override
	public int getRate() {
		return decoder.getRate();
	}

	@Override
	public int skipSamples(int numSamples) {
		return decoder.skipSamples(numSamples);
	}

	@Override
	public short[] readAllSamples() {
		return decoder.readAllSamples();
	}

}
