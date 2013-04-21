package net.bluecow.spectro.detection.tempo;

import java.util.ArrayList;
import java.util.Collections;

import net.bluecow.spectro.detection.Beat;

public class PermutationDetection extends TempoDetector {

	public PermutationDetection(ArrayList<Beat> beats) {
		super(beats);
	}

	double distanceAcuteness = 4;


	ArrayList<IntervalSet> distanceSets = new ArrayList<IntervalSet>();

	@Override
	public void detectTempo(int startIndex)
	{
		//we need a certain number of beats before we can run the program
		if(detectedBeats.size()<numberOfBeats)
		{
			return;
		}

		int endIndex = startIndex+numberOfBeats;

		ArrayList<Interval> intervalPermutations;

		/**
		 * Creates a bunch distances that will then be compared
		 */
		if(anyPrintout)
			System.out.println("Creating new distances");
		intervalPermutations = createIntervalArray(startIndex,endIndex);

		addIntervalsToSet(intervalPermutations);

		/**
		 * Removing the weak sets here
		 */
		double avg = Statistics.averageSize(distanceSets);
	//	avg = 14;
		if(anyPrintout)
			System.out.println("READY TO CULL THE HERD "+avg);
		if(cullingPrintout&&anyPrintout)
			System.out.println("Size before "+distanceSets.size());
		for(int k = 0; k<distanceSets.size();k++)
		{
			IntervalSet set = distanceSets.get(k);
			if(set.distancesInSet.size()<avg&&startIndex-set.createdBeatIndex>numberOfBeats/4)
			{
				if(cullingPrintoutDetailed&&anyPrintout)
					System.out.println("TOO WEAK " + set.distancesInSet.size()+" "+startIndex+" "+set.createdBeatIndex);
				distanceSets.remove(k);
				k-=1;
			}
		}
		if(cullingPrintout&&anyPrintout)
			System.out.println("Size after "+distanceSets.size());
		//adding the leftOver distances here
		for(int k = 0; k<intervalPermutations.size(); k++)
		{
			Interval d = intervalPermutations.get(k);
			IntervalSet set = new IntervalSet(d.starting.indexInList);
			set.addDistance(d);
			distanceSets.add(set);
		}
		IntervalSet.sortSize = true;
		IntervalSet.sortAvg = false;
		Collections.sort(distanceSets);

//		System.out.println("Max Size"+distanceSets.get(0).distancesInSet.size());

		IntervalSet.sortSize = false;
		IntervalSet.sortAvg = true;
		Collections.sort(distanceSets);
	}


	/**
	 * Creates the Interval Array
	 * limitations on the arry:
	 *
	 * The max interval is limited by maxIntervalAllowed
	 *
	 * A regression is also done on the current interval set
	 * and will not allow items that are very far away from the regression line
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public ArrayList<Interval> createIntervalArray(int startIndex,int endIndex)
	{
		ArrayList<Interval> regression = new ArrayList<Interval>();
		ArrayList<Interval> intervalPermutations = new ArrayList<Interval>();

		Beat startingBeat = detectedBeats.get(startIndex);

		//adds all of the distances to the list
		for(int k = startIndex+1;k<endIndex;k++)
		{
			Beat b = detectedBeats.get(k);
			Interval d = new Interval(b.sampleLocation-startingBeat.sampleLocation,1,startingBeat,b);

			regression.add(d);
		}

		double[] results = Statistics.leastSquares(regression);
		for(int k = 0 ;k<regression.size();k++)
		{
			Interval d = regression.get(k);
			double interval = d.intervalSize;
			double lineDistance = interval-results[0];
			if(interval<maxIntervalAllowed)//&&Math.abs(lineDistance)<(distanceAcuteness+1))
			{
				intervalPermutations.add(d);
			}
		}

		return intervalPermutations;
	}

	/**
	 * This tries to add distances that are similar to the distanceSets (the list is in distance sorted order)
	 * it will go up the list of while the distances are less than the average in the distance set
	 * it will remove distances from the firstRoundDistances
	 * left overs will be creating there own distanceList
	 * before the left overs are added to their own distnaceList the list is culled removing sets that have a lower than average
	 */
	public void addIntervalsToSet(ArrayList<Interval> intervalPermutations)
	{
	//	ArrayList<Interval> leftOvers = new ArrayList<Interval>();
		//removes it from the list - the left overs
		int tempDistanceIndex = 0;//it starts off trying to compare distances

		for(int k = 0;k<distanceSets.size();k++)
		{
			if(tempDistanceIndex >= intervalPermutations.size() || tempDistanceIndex<0)
				break;

			IntervalSet dSet = distanceSets.get(k);
			double avgIntervalLength = dSet.averageValue;
			Interval currentInterval = intervalPermutations.get(tempDistanceIndex);

			//while the distances in the new list is less than the current distance count up (kinda like insertion sort)
			while(currentInterval.intervalSize <= avgIntervalLength)
			{
				//if it is close enough add it to the list of distance sets
				if(Math.abs(avgIntervalLength - currentInterval.intervalSize) < distanceAcuteness)
				{
					if(dSet.addDistance(currentInterval))
					{

						intervalPermutations.remove(tempDistanceIndex);
						tempDistanceIndex-=1;

					}else
					{
						//leftOvers.add(currentInterval);
					}
				}else
				{
					//leftOvers.add(currentInterval);
				}

				tempDistanceIndex++;

				if(tempDistanceIndex >= intervalPermutations.size())
					break;

				currentInterval = intervalPermutations.get(tempDistanceIndex);
			}

			//do it for the one that is one above the distance too
			if(Math.abs(currentInterval.intervalSize-avgIntervalLength)<distanceAcuteness)
			{
				if(dSet.addDistance(currentInterval))
				{
					if(tempDistanceIndex>= intervalPermutations.size())
						tempDistanceIndex-=1;
					intervalPermutations.remove(tempDistanceIndex);
					tempDistanceIndex-=1;
				}else
				{
					//leftOvers.add(currentInterval);
				}
			}
		}

	//	return intervalPermutations;
	}

	public void printDistanceSets()
	{
		for(int q = 0;q<distanceSets.size();q++)
		{
			IntervalSet set = distanceSets.get(q);
			System.out.println("average Distance "+set.averageValue+" size "+set.distancesInSet.size());
		}
	}

	public void setTempoBeats()
	{
		IntervalSet.sortSize = true;
		IntervalSet.sortAvg = false;
		Collections.sort(distanceSets);

		for(int k = 0;k<Math.min(distanceSets.size(),5);k++)
			for(Interval d:distanceSets.get(k).distancesInSet)
			{
				d.starting.predictedBeat = true;
			}
	}

}
