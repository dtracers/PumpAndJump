package net.bluecow.spectro.detection;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class TempoDetector
{

	public static boolean compareTestPrintout = false;
	public static boolean cullingPrintout = true;
	public static boolean anyPrintout = false;
	public static boolean cullingPrintoutDetailed = false;


	public static void main(String args[]) throws FileNotFoundException
	{
		ArrayList<Beat> beats = new ArrayList<Beat>();
	//	Scanner s = new Scanner(new File("myTest.txt"));
		Scanner s = new Scanner(new File("The Hand That Feeds - Nine Inch Nails.txt"));
		System.out.println("Reading");
		while(s.hasNext())
		{
			long s2 =s.nextLong();
			beats.add(new Beat(s2,0,beats.size()));
		}
		System.out.println("Done Reading "+beats.size());
		TempoDetector detector = new TempoDetector(beats);

		for(int k = 0; k<beats.size()-detector.numberOfBeats;k++)
		{
			System.out.println("TEMPO RUN THROUGH TIME: "+k);
			detector.detectTempo3(k);
		//	detector.printDistanceSets();
		}
		detector.printDistanceSets();
		System.out.println("\t"+maxDistanceAllowed);

	}


	//valous used for the tempo detection
	double tempoStrength;
	double timeBetweenBeats;
	double locationOfNextBeat;
	double distancesenstitivity = 4;
	ArrayList<Distance> distances = null;

	ArrayList<DistanceSet> distanceSets = new ArrayList<DistanceSet>();

	int timeSince = 0;
	int numberOfBeats = 30;

	ArrayList<Beat> detectedBeats;

	int counter = 0;
	public static double samplingRate = 44100;
	public static double frameSize = 1320;
	public static double BPMtoFrameRatio = (60.0*samplingRate/frameSize);//the ratio from an BPM to actual frame distances
	public static double SlowestBPM = 20;
	public static double maxDistanceAllowed = BPMtoFrameRatio/SlowestBPM;


	public TempoDetector(ArrayList<Beat> beats)
	{
		this.detectedBeats = beats;
	}

	/**
	 * Attempts to detect the tempo of the piece
	 */
	public void detectTempo()
	{
		if(detectedBeats.size()<numberOfBeats)
		{
			return;
		}
		if(tempoStrength <.5)
		{
			int start = detectedBeats.size()-numberOfBeats;
			int end = detectedBeats.size();
			int beatDistance = 6;

			//we go through 30 beats in the list
			//we attempt to find beats differences that strengthen and remove those that weaken
			//it is an attempt at a genetic algorithm
			for(int k=start;k<end-beatDistance;k++)
			{
				Beat startingBeat = detectedBeats.get(k);
				ArrayList<Distance> tempDistances = new ArrayList<Distance>();
				for(int q = 1; q<beatDistance;q++)
				{
					Beat other = detectedBeats.get(k+q);
					tempDistances.add(new Distance(other.sampleLocation-startingBeat.sampleLocation,.5,startingBeat,other));
				}

				//moves the first set over because they all have zero strength
				if(k==start)
				{
					distances = tempDistances;
				}else
				{
					//goes through and tries to match distances
					//both should be sorted by distance
					//it will remove some if the strength is below a certain value
					//also it will use a sorted merge list style
					int distanceIndex = 0;//the index for the distance
					int length = distances.size();
					int tempLength = tempDistances.size();
					for(int q = 0; q<tempLength; q++)
					{
						Distance dist = tempDistances.get(q);
						if(distanceIndex< distances.size())
						{
							Distance dist2 = distances.get(distanceIndex);
							//it is close to what we want so we need to make it stronger or weaker
							if(Math.abs(dist2.distance-dist.distance)<distancesenstitivity)
							{
								dist2.strength+=.05;
								if(dist2.strength>1)
								{
									dist.other.predictedBeat = true;
									dist.other.col = dist2.col;
								}
								distanceIndex++;
							}else if(dist2.distance>dist.distance)//it means we havent reached one yet
							{
								continue;
							}else// it means we passed it and it did not have a matching beat
							{
								dist2.strength-=.05;
								if(dist2.strength<.1)
								{
									distances.remove(dist2);
									distanceIndex-=1;
								}
								distanceIndex++;
							}

						}else
						{
							//maybe add them on?
							//find a way to remove distances that are too long?
							distances.add(dist);
						}

					}
					Collections.sort(distances);

					//go through distances and combine beats that are doubles of other beats

				//	for(Distance distance:distances)
				//		System.out.println(distance);
				}

			}
		}
	}

	/**
	 * Attempts to detect the tempo of the piece
	 * it uses a regression over 30 *items of significance*
	 */
	public void detectTempo2()
	{
		if(detectedBeats.size()<numberOfBeats)
		{
			timeSince++;
			return;
		}

		if(timeSince<numberOfBeats/2)
		{
			timeSince++;
			return;
		}
		timeSince = 0;


		int start = detectedBeats.size()-numberOfBeats;

		ArrayList<Beat> temp = new ArrayList<Beat> ();
		ArrayList<Distance> distances = new ArrayList<Distance>();

		Statistics.copy(start, numberOfBeats, detectedBeats, temp);

		double finalAverage = 0;

		double average = 0;
		for(int k = 0;k<4;k++)
		{

			double ratio = numberOfBeats;
			ratio = ratio/temp.size();
			if(ratio <1.3)
			{
				ratio = 1.3;
			}
			double[] result=null;
		//	System.out.println(distances.size());
			if(k==0||true)
				result = Statistics.leastSquares(temp.size(),temp,distances);
			else
				result = Statistics.weightedLeastSquares( distances.size(), distances,average);
		//	System.out.println(distances.size());
		//	double Rvalue = Statistics.getPearsonCorrelation(distances);

			finalAverage = average = Statistics.distances(0, distances.size(), distances, result);

			ArrayList<Beat> removedItems = Statistics.removeItemsAboveAverage(distances,temp, average*ratio);

			if(k==0&&false)
				for(int q = 0;q<removedItems.size();q++)
				{
					detectedBeats.remove(removedItems.get(q));
				}


		//	System.out.println("k "+ k+" size: "+temp.size()+"\t "+result[0]+ "\t "+ratio);
		}
	//	System.out.println(type+" "+finalAverage);

		for(int k=0;k<temp.size();k++)
		{
			temp.get(k).col = Color.CYAN;
			temp.get(k).predictedBeat = true;
		}

	//	System.out.println("data "+result[0]+" "+result[1]);
	}

	public void detectTempo3()
	{
		detectTempo3(detectedBeats.size()-numberOfBeats);
	}

	/**
	 * Attempts to detect the tempo of the music
	 */
	public void detectTempo3(int startIndex)
	{
		if(detectedBeats.size()<numberOfBeats)
		{
			return;
		}

		//int startIndex = detectedBeats.size()-numberOfBeats;
		int endIndex = startIndex+numberOfBeats;

		Beat startingBeat = detectedBeats.get(startIndex);

		ArrayList<Distance> firstRoundDistances = new ArrayList<Distance>();

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
				firstRoundDistances.add(d);
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
			if(tempDistanceIndex>= firstRoundDistances.size()||tempDistanceIndex<0)
				break;
			DistanceSet dSet = distanceSets.get(k);
			double avgDistance = dSet.averageValue;
			Distance distances = firstRoundDistances.get(tempDistanceIndex);
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
						firstRoundDistances.remove(tempDistanceIndex);
						tempDistanceIndex-=1;
					}
				}
				tempDistanceIndex++;
				if(tempDistanceIndex>= firstRoundDistances.size())
					break;
				distances = firstRoundDistances.get(tempDistanceIndex);
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
					if(tempDistanceIndex>= firstRoundDistances.size())
						tempDistanceIndex-=1;
					firstRoundDistances.remove(tempDistanceIndex);
					tempDistanceIndex-=1;
				}
			}
		}

		/**
		 * Removing the weak sets here
		 */

		boolean R2Average = false;
		if(R2Average)
		{
			double avg = Statistics.averageR2(distanceSets);
			if(anyPrintout)
				System.out.println("READY TO CULL THE HERD "+avg);
			if(anyPrintout)
				System.out.println("Size before "+distanceSets.size());
			for(int k = 0; k<distanceSets.size();k++)
			{
				if(distanceSets.get(k).R2<avg)
				{
					if(cullingPrintoutDetailed&&anyPrintout)
						System.out.println("TOO WEAK " + distanceSets.get(k).R2);
					distanceSets.remove(k);
					k-=1;
				}
			}
			System.out.println("Size after "+distanceSets.size());
		}else //if(false)
		{
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
		}
		//adding the leftOver distances here
		for(int k = 0; k<firstRoundDistances.size(); k++)
		{
			Distance d = firstRoundDistances.get(k);
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
			System.out.println("average Distance "+set.averageValue+" size "+set.distancesInSet.size()+" R2 "+set.R2);
		}
	}

	public void setSignificanceBeats()
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
