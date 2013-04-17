package net.bluecow.spectro.detection.tempo;

import java.util.ArrayList;
import java.util.Collections;

import net.bluecow.spectro.detection.Beat;

public class PermutationDetection extends TempoDetector {

	public PermutationDetection(ArrayList<Beat> beats) {
		super(beats);
	}

	double distancesenstitivity = 4;


	ArrayList<DistanceSet> distanceSets = new ArrayList<DistanceSet>();

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

		/**
		 * Creates a bunch distances that will then be compared
		 */
		if(anyPrintout)
			System.out.println("Creating new distances");
		for(int k = startIndex+1;k<endIndex;k++)
		{
			Beat b = detectedBeats.get(k);
			Distance d = new Distance(b.sampleLocation-startingBeat.sampleLocation,1,startingBeat,b);
			if(d.distance<maxDistanceAllowed)
			{
			//	System.out.println(d.distance);
				distancePermutations.add(d);
			}
		}

		/**
		 * This tries to add distances that are similar to the distanceSets (the list is in distance sorted order)
		 * it will go up the list of while the distances are less than the average in the distance set
		 * it will remove distances from the firstRoundDistances
		 * left overs will be creating there own distanceList
		 * before the left overs are added to their own distnaceList the list is culled removing sets that have a lower than average
		 * R2-Value that is some how combined with the number in the set
		 */
		//removes it from the list - the left overs
		int tempDistanceIndex = 0;//it starts off trying to compare distances

		if(compareTestPrintout&&anyPrintout)
			System.out.println("comparing distances");
		for(int k = 0;k<distanceSets.size();k++)
		{
			if(tempDistanceIndex>= distancePermutations.size()||tempDistanceIndex<0)
				break;
			DistanceSet dSet = distanceSets.get(k);
			double avgDistance = dSet.averageValue;
			Distance distances = distancePermutations.get(tempDistanceIndex);
			//while the distances in the new list is less than the current distance count up (kinda like insertion sort)
			while(distances.distance<=avgDistance)
			{
				if(compareTestPrintout&&anyPrintout)
					System.out.println(tempDistanceIndex+" during: "+(avgDistance-distances.distance)+" "+distances.distance+" "+avgDistance);
				//if it is close enough add it to the list of distance sets
				if(Math.abs(avgDistance-distances.distance)<distancesenstitivity)
				{
					if(compareTestPrintout&&anyPrintout)
						System.out.println("We have a winner in this set!");
					if(dSet.addDistance(distances))
					{
						distancePermutations.remove(tempDistanceIndex);
						tempDistanceIndex-=1;
					}
				}
				tempDistanceIndex++;
				if(tempDistanceIndex>= distancePermutations.size())
					break;
				distances = distancePermutations.get(tempDistanceIndex);
			}
			if(compareTestPrintout&&anyPrintout)
				System.out.println(tempDistanceIndex+" after: "+(avgDistance-distances.distance)+" "+distances.distance+" "+avgDistance);
			//do it for the one that is one above the distance too
			if(Math.abs(distances.distance-avgDistance)<distancesenstitivity)
			{
				if(compareTestPrintout&&anyPrintout)
					System.out.println("We have a winner in this set!");
				if(dSet.addDistance(distances))
				{
					if(tempDistanceIndex>= distancePermutations.size())
						tempDistanceIndex-=1;
					distancePermutations.remove(tempDistanceIndex);
					tempDistanceIndex-=1;
				}
			}
		}

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
			DistanceSet set = distanceSets.get(k);
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
		for(int k = 0; k<distancePermutations.size(); k++)
		{
			Distance d = distancePermutations.get(k);
			DistanceSet set = new DistanceSet(d.starting.indexInList);
			set.addDistance(d);
			distanceSets.add(set);
		}
		DistanceSet.sortSize = true;
		DistanceSet.sortAvg = false;
		Collections.sort(distanceSets);

//		System.out.println("Max Size"+distanceSets.get(0).distancesInSet.size());

		DistanceSet.sortSize = false;
		DistanceSet.sortAvg = true;
		Collections.sort(distanceSets);
	}


	public void printDistanceSets()
	{
		for(int q = 0;q<distanceSets.size();q++)
		{
			DistanceSet set = distanceSets.get(q);
			System.out.println("average Distance "+set.averageValue+" size "+set.distancesInSet.size());
		}
	}

	public void setTempoBeats()
	{
		DistanceSet.sortSize = true;
		DistanceSet.sortAvg = false;
		Collections.sort(distanceSets);

		for(int k = 0;k<Math.min(distanceSets.size(),5);k++)
			for(Distance d:distanceSets.get(k).distancesInSet)
			{
				d.starting.predictedBeat = true;
			}
	}

}
