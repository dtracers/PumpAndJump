package com.musicgame.PumpAndJump.game.sound;

import java.util.ArrayList;

import ddf.minim.effects.BandPass;
import ddf.minim.effects.IIRFilter;

/**
 * Tries to do stuff with frequency
 * @author gigemjt
 *
 */
public class FrequencyHandler
{

	public static final int numFilters = 5;
	public static final int lowestFrequency = 10;
	public static final int highestFrequency = 17000;
	private ArrayList<IIRFilter> filters;

	public void createFilters()
	{
		int lowFreq = 10;
		int bandSize;
		for(int k =0;k<numFilters;k++)
		{
			filters.add(new BandPass(150.0f, 6.25f, MusicHandler.sampleRate));
		}
	}
}
