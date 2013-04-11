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
	 * may return succes or not depending on implemenetaion
	 * @param samples
	 * @param offset
	 * @param numSamples
	 * @return
	 */
	public void writeData(short[] samples, int offset, int numSamples)
	{

	}
}
