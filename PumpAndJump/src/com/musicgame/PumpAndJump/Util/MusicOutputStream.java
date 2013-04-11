package com.musicgame.PumpAndJump.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

public class MusicOutputStream
{
	int latency;
	AudioDevice device;
	public MusicOutputStream()
	{
		device = Gdx.audio.newAudioDevice(44100, true);
		latency = device.getLatency();
	}

	/**
	 * Reads in numSamples at offset into the samples array
	 * it also blocks so send it very short samples
	 * @param samples
	 * @param offset
	 * @param numSamples
	 * @see com.badlogic.gdx.audio.AudioDevice#writeSamples(float[], int, int)
	 * @return
	 */
	public void writeData(float[] samples, int offset, int numSamples)
	{
		device.writeSamples(samples, offset, numSamples);
	}
}
