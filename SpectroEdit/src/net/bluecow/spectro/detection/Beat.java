package net.bluecow.spectro.detection;

import java.awt.Color;

public class Beat
{
	public Beat(long highestIndex, float highestPoint)
	{
		soundIntensity = highestPoint;
		sampleLocation = highestIndex;
	}
	public static final int FRAME_SIZE = 1320;
	double soundIntensity;
	long sampleLocation;
	boolean predictedBeat;
	Color col;
}
