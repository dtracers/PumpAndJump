package net.bluecow.spectro.detection;

import java.awt.Color;
import java.util.ArrayList;

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
		return "d: "+distance+" s: "+strength;
	}
	@Override
	public double averageValue() {
		return distance;
	}
}

class DistanceSet implements Averageable,Comparable
{
	public static boolean sortSize;
	public static boolean sortAvg;
	ArrayList<Distance> distancesInSet = new ArrayList<Distance>();
	double a,b;
	double R2 = 1;//R^2 value
	double averageValue = 0;

	public void addDistance(Distance d)
	{
		distancesInSet.add(d);
		if(distancesInSet.size()>1)
		{
		//	System.out.println("AVERAGE DISTANCE IS "+averageValue);
		//	System.out.println("Number in set is  "+distancesInSet.size());
			double[] result = Statistics.leastSquares(distancesInSet);
			a = result[0];
			b = result[1];
			R2 = result[2];
		}
		averageValue = Statistics.average(distancesInSet);

	}

	@Override
	public double averageValue() {
		return R2;
	}

	@Override
	public int compareTo(Object arg0)
	{
		if(sortAvg)
			return (int) Math.signum(averageValue-((DistanceSet)arg0).averageValue);
		if(sortSize)
			return (int) Math.signum(((DistanceSet)arg0).distancesInSet.size()-distancesInSet.size());
		return 0;
	}
}

interface Averageable
{
	public double averageValue();
}