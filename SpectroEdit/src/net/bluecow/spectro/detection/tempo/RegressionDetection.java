package net.bluecow.spectro.detection.tempo;

import java.util.ArrayList;

import net.bluecow.spectro.detection.Beat;

public class RegressionDetection extends TempoDetector
{

	public static double distanceSensitivity = 1.15;
	ArrayList<DistanceSet> distanceSets = new ArrayList<DistanceSet>();

	double averageDistance;
	DistanceSet correctDistances;
	DistancePaint painter;

	double runningDistanceAverage = 0;
	double runThroughs = 0;
	public RegressionDetection(ArrayList<Beat> beats) {
		super(beats);
	}

	@Override
	public void detectTempo(int startIndex)
	{
		if(detectedBeats.size()<numberOfBeats)
		{
			return;
		}

		//int startIndex = detectedBeats.size()-numberOfBeats;
		int endIndex = startIndex+numberOfBeats;

		Beat startingBeat = detectedBeats.get(startIndex);

		ArrayList<Distance> distancePermutations = new ArrayList<Distance>();
		painter.distances = distancePermutations;

		/**
		 * Creates a bunch distances that will then be compared
		 */
		if(anyPrintout)
			System.out.println("Creating new distances");
		for(int k = startIndex+1;k<endIndex;k++)
		{
			Beat b = detectedBeats.get(k);
			Distance d = new Distance(b.sampleLocation-startingBeat.sampleLocation,1,startingBeat,b,k-(startIndex+1));
			distancePermutations.add(d);
		}
		double[] results = Statistics.leastSquares(distancePermutations);
		painter.line = results;

		runningDistanceAverage+= results[0];
		runThroughs++;

		//R^2 value
		ArrayList<Distance> secondRound = distancePermutations;
		double[] results2 = results;
		boolean loop = false;

		int counter = 0;
		if(results2[2]<.998&&secondRound.size()>this.numberOfBeats/3)
		{
			System.out.println("time through "+counter+" "+results[2]+" "+secondRound.size());
			counter++;
			loop = true;
			averageDistance = Statistics.distances(secondRound, results);
			ArrayList<Distance>secondRound2 = new ArrayList<Distance>();
			for(int k = 0;k<secondRound.size();k++)
			{
				if(secondRound.get(k).strength>averageDistance*distanceSensitivity)
				{
				//	System.out.println("Removing");
				}else
				{
					secondRound2.add(secondRound.get(k));
				}
			}
			secondRound = secondRound2;
			results2 = Statistics.leastSquares(secondRound);

			runningDistanceAverage+=results2[0];
			runThroughs++;

		}
		if(!loop)
		{
			averageDistance = 0;
			results2 = null;
			secondRound = null;
		}
		painter.line2 = results2;
		painter.secondRound = secondRound;
		painter.averageDistance = runningDistanceAverage/runThroughs;


	}

	@Override
	public void setTempoBeats() {
	}

}
