package com.musicgame.PumpAndJump.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

import io.OutputDevice;

public class LibGDXOutputDevice implements OutputDevice
{
	AudioDevice device;
	public LibGDXOutputDevice(int sampleRate,boolean is_mono)
	{
		device = Gdx.audio.newAudioDevice(44100, is_mono);
	}
	@Override
	public boolean isMono() {
		return device.isMono();
	}

	@Override
	public void writeSamples(short[] samples, int offset, int numSamples) {
		device.writeSamples(samples, offset, numSamples);
	}

	@Override
	public void writeSamples(float[] samples, int offset, int numSamples) {
		device.writeSamples(samples, offset, numSamples);
	}

	@Override
	public int getLatency() {
		return device.getLatency();
	}

	@Override
	public void dispose() {
		device.dispose();
	}

	@Override
	public void setVolume(float volume) {
		device.setVolume(volume);
	}

}
