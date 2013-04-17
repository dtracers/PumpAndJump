package net.bluecow.spectro.detection.tempo;

import java.awt.Color;
import java.util.ArrayList;

import net.bluecow.spectro.detection.Beat;

class Distance implements Comparable,Averageable
{
	Color col;// = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
	double distance;
	double strength = 0.5;
	Beat starting;
	Beat other;
	public Distance(long l, double i, Beat startingBeat, Beat other)
	{
		distance = l;
		strength = i;
		starting = startingBeat;
		this.other = other;
	}
	@Override
	public int compareTo(Object arg0)
	{
		return (int) Math.signum(distance-((Distance)arg0).distance);
	}

	public String toString()
	{
		return "starting "+starting.sampleLocation+" ending "+other.sampleLocation+"d: "+distance+" s: "+strength;
	}
	@Override
	public double averageValue() {
		return distance;
	}
}

class DistanceSet implements Comparable
{
	public static boolean sortSize;
	public static boolean sortAvg;
	ArrayList<Distance> distancesInSet = new ArrayList<Distance>();
	int size = 0;
	double averageValue = 0;
	int createdBeatIndex;

	public DistanceSet(int createdIndex)
	{
		this.createdBeatIndex = createdIndex;
	}
	public boolean addDistance(Distance d)
	{
		if(distancesInSet.size()>=1)
		{
			Distance previous = distancesInSet.get(distancesInSet.size()-1);
			if(previous.other!=d.starting)
			{
				double totalDistance = d.starting.sampleLocation - previous.other.sampleLocation;
				if(totalDistance>0)
				{
					double dividedDistance = totalDistance/averageValue;
					double decimal = Math.abs(dividedDistance-Math.rint(dividedDistance));
					if(d.starting.sampleLocation>770&&d.starting.sampleLocation<790&&previous.other.sampleLocation<800&&previous.other.sampleLocation>760)
					{
						System.out.println("the starting location "+previous);
						System.out.println("the starting location "+d);
						System.out.println(totalDistance+" "+dividedDistance+" "+decimal);
						if(decimal<.1)
						{
							System.out.println("The Beats do not match! "+" "+averageValue);
							System.out.println(totalDistance+" "+dividedDistance+" "+decimal);
						}
					}
					//then I need to combine it into one thing? and then attach that result chain hopefully (which will result in a tree?)
					if(totalDistance<1)
					{

					}
				}
				return false;
			}
		//	System.out.println("The Beats do match! "+averageValue);
			distancesInSet.add(d);
			//averageValue = Statistics.average(distancesInSet);
		}else
		{
			distancesInSet.add(d);
			//averageValue = Statistics.average(distancesInSet);
		}

		return true;
	}

	@Override
	public int compareTo(Object arg0)
	{
		return (int) Math.signum(((DistanceSet)arg0).distancesInSet.size()-distancesInSet.size());
	}

}

interface Averageable
{
	public double averageValue();
}