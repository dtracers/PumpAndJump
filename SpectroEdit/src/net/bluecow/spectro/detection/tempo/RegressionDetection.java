package net.bluecow.spectro.detection.tempo;

import java.util.ArrayList;

import net.bluecow.spectro.detection.Beat;

public class RegressionDetection extends TempoDetector
{

	ArrayList<DistanceSet> distanceSets = new ArrayList<DistanceSet>();
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
			Distance d = new Distance(b.sampleLocation-startingBeat.sampleLocation,1,startingBeat,b);
			distancePermutations.add(d);
		}
		double[] results = Statistics.leastSquares(distancePermutations);
		painter.line = results;

		Statistics.distances(distancePermutations, results);
	}

	@Override
	public void setTempoBeats() {
	}

}
