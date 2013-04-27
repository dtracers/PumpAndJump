package com.musicgame.PumpAndJump.game.sound;

public class Beat implements Comparable
{

	static boolean SORT_BY_LOCATION = false;
	static boolean SORT_BY_INTENSITY = false;
	public Beat(long highestIndex, float highestPoint,int indexInList)
	{
		System.out.println("Creating a beat at "+highestIndex);
		soundIntensity = highestPoint;
		sampleLocation = highestIndex;
		this.indexInList = indexInList;
	}
	double soundIntensity;
	public long sampleLocation;
	public boolean predictedBeat;
	public int indexInList;
	public String toString()
	{
		return ""+sampleLocation;//"b "+(double)(sampleLocation*1320.0/44100.0);
	}

	@Override
	public int compareTo(Object o)
	{
		if(SORT_BY_LOCATION)
		{
			return (int) Math.signum(this.sampleLocation-((Beat)o).sampleLocation);
		}else if(SORT_BY_INTENSITY)
		{
			return (int) Math.signum(this.soundIntensity-((Beat)o).soundIntensity);
		}else
			return 0;
	}
}
