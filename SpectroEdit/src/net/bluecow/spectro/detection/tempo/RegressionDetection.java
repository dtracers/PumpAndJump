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

		//R^2 value
		ArrayList<Distance> secondRound = null;
		double[] results2 = null;
		if(results[2]<.998)
		{
			averageDistance = Statistics.distances(distancePermutations, results);
			secondRound = new ArrayList<Distance>();
			for(int k = 0;k<distancePermutations.size();k++)
			{
				if(distancePermutations.get(k).strength>averageDistance*distanceSensitivity)
				{
				//	System.out.println("Removing");
				}else
				{
					secondRound.add(distancePermutations.get(k));
				}
			}
			results2 = Statistics.leastSquares(secondRound);
		}else
		{
			//only for debugging
			averageDistance = 0;
		}
		painter.line2 = results2;
		painter.secondRound = secondRound;
		painter.averageDistance = averageDistance;

	}

	@Override
	public void setTempoBeats() {
	}

}
