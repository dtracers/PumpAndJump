package net.bluecow.spectro.detection;

import java.awt.Color;
import java.util.ArrayList;

class Distance implements Comparable
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
		return "d: "+distance+" s: "+strength;
	}
}

class DistanceSet
{
	ArrayList<Distance> distancesInSet = new ArrayList<Distance>();
	double a,b;
	double R2;//R^2 value
	double averageValue = 0;

	public void addDistance(Distance d)
	{
		distancesInSet.add(d);
		if(distancesInSet.size()>1)
		{
			System.out.println("AVERAGE DISTANCE IS "+averageValue);
			double[] result = Statistics.leastSquares(distancesInSet);
			a = result[0];
			b = result[1];
			R2 = result[3];
		}
		averageValue = Statistics.average(distancesInSet);

	}
}