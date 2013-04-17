package net.bluecow.spectro.detection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;

import ddf.minim.effects.IIRFilter;


public class BeatDetector
{
	//this is the instantaneous VEdata
	public ArrayList<float[]> VEdata = new ArrayList<float[]>();
	//this is the average VEdata (over 43 Energy histories
	public ArrayList<Double> AveragedEnergydata = new ArrayList<Double>();

	int historyLength = 20;//43;

	private double[] EnergyHistory = new double[historyLength];
	int currentHistoryIndex = 0;
//	private long sampleIndex;
	ArrayList<Beat> detectedBeats = new ArrayList<Beat>();

	double maxEnergy = 0;

	private IIRFilter filter;


	int shiftAvg = 10;//this is used so that some of the future values are computed in the average of the current value

	//values used for the actual beat detection
	boolean aboveAverage = false;
	long currentIndex = 0;
	long highestIndex = 0;//the index of the highest point when it is above the beat
	float highestPoint;
	//the senstitivity of the beat detector:  smaller numbers remove more beats and is more strict
	double senstitivity = 0.8;


	//valous used for the tempo detection
	double tempoStrength;
	double timeBetweenBeats;
	double locationOfNextBeat;
	double distancesenstitivity = 4;
	ArrayList<Distance> distances = null;

	int timeSince = 0;


	int type = 0;
	static int counter = 0;

	int numberOfBeats = 60;

	public BeatDetector(IIRFilter bandPass)
	{
		filter = bandPass;
		type = counter;
		counter++;
	}

	public void calculateVE(float[] timeData)
   	{
		if(filter!=null)
		{
			preFilter(timeData);
		}
   		//the size of bits that the array is taken over
   		int averageSize = Beat.FRAME_SIZE;
   		//number of values
   		int length = timeData.length/averageSize;
   		int index = 0;
   		for(int k = 0;k<length;k++)
   		{
   			float[] result = new float[2];
   			float volume = 0;
   			float energy = 0;
   			for(int q = 0; q<averageSize;q++)
   			{
   				float data = timeData[index];
   				volume+=data;
   				energy+=data*data;

   				index++;
   			}
   			maxEnergy = Math.max(maxEnergy, energy);
   			result[0] = volume;
   			result[1] = energy;
   			VEdata.add(result);

   			EnergyHistory[currentHistoryIndex] = energy;

   			double value = 0;
   			for(int q=0;q<historyLength;q++)
   			{
   				value+=EnergyHistory[q];
   			}
   			value/=historyLength;
   			AveragedEnergydata.add(value);

   			currentHistoryIndex++;
   			currentHistoryIndex%=historyLength;

   			beatDetectionAlgorithm();
   		}

   	}

	public void draw(Graphics2D g2,int startY,int scale)
	{
		double ratio = scale/maxEnergy;
		g2.setColor(Color.black);
		int length = VEdata.size();

		//VE
		float[] old = VEdata.get(0);
		float[] current = VEdata.get(0);
		//average
		double oldAvg = AveragedEnergydata.get(0);
		double currentAvg = AveragedEnergydata.get(0);

		g2.setColor(Color.black);
		for(int k = 0; k<length;k++)
		{
			old = current;
			current = VEdata.get(k);

			oldAvg = currentAvg;
			if(k>10)
				currentAvg = AveragedEnergydata.get(k-shiftAvg);

	//		System.out.println(old[0]+"\n"+old[1]);
	//		g2.setColor(Color.blue);
	//		g2.drawLine((k-1)*4, (int)(startY - old[0]*ratio), k*4, (int)(startY - current[0]*ratio));
			g2.setColor(Color.red);
			g2.drawLine((k-1)*4, (int)(startY - old[1]*ratio), k*4, (int)(startY- current[1]*ratio));
	//		g2.setColor(Color.green);
	//		g2.drawLine((k-1)*4, (int)(startY - oldAvg*ratio), k*4, (int)(startY- currentAvg*ratio));
		}
		int beatLength = detectedBeats.size();
		for(int k=0; k<beatLength;k++)
		{
			Beat b = detectedBeats.get(k);
			if(!b.predictedBeat)
			g2.setColor(Color.GRAY);
			else
				g2.setColor(b.col);
			g2.drawLine((int)b.sampleLocation*4, startY, (int) b.sampleLocation*4,(int)( startY-75));
		}
		g2.setColor(Color.black);
		g2.drawLine(0, startY, length*4, startY);

	}

	public void preFilter(float[] timeData)
	{
		filter.process(timeData);
	}

	/**
	 * Goes through each point once and sees if it is large enough away from the average to be considered a beat
	 * Need to make this static and go through all beats to detirmine Major beats
	 */
	public void beatDetectionAlgorithm()
	{
		if(currentIndex<shiftAvg)
		{
			currentIndex++;
			return;
		}

		float instantEnergy = VEdata.get((int) (currentIndex))[1];
		double averageEnergy = AveragedEnergydata.get((int) (currentIndex-shiftAvg));
		if(instantEnergy>=averageEnergy)
		{
			if(instantEnergy>highestPoint)
			{
				highestPoint = instantEnergy;
				highestIndex = currentIndex;
			}
			aboveAverage = true;
		}else if(aboveAverage)
		{
			double avgEnergy = AveragedEnergydata.get((int) (highestIndex-shiftAvg));
			double division = avgEnergy/highestPoint;
		//	System.out.println(division);
			aboveAverage = false;
			if(division<senstitivity)
			{
				detectedBeats.add(new Beat(highestIndex,highestPoint));
				detectTempo2();
		//		detectTempo();
			}
			highestPoint = 0;
			highestIndex = -1;
		}
		/**
		 * Will look for spikes that are above the average...
		 * Every spike above the average is a minor beat
		 * one the energy level crosses the average energy level we only take one spike until it falls back below
		 * This one spike is the maximum spike
		 *
		 * (maybe take two averages?)
		 */

		currentIndex++;
	}

	/**
	 * Attempts to detect the tempo of the piece
	 */
	private void detectTempo()
	{
		if(detectedBeats.size()<30)
		{
			return;
		}
		if(tempoStrength <.5)
		{
			int start = detectedBeats.size()-30;
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
	private void detectTempo2()
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
			System.out.println(distances.size());
			if(k==0)
				result = Statistics.leastSquares(temp.size(),temp,distances);

			else
				result = Statistics.weightedLeastSquares( distances.size(), distances,average);
			System.out.println(distances.size());
		//	double Rvalue = Statistics.getPearsonCorrelation(distances);

			finalAverage = average = Statistics.distances(0, distances.size(), distances, result);

			ArrayList<Beat> removedItems = Statistics.removeItemsAboveAverage(distances,temp, average*ratio);

			if(k==0&&false)
				for(int q = 0;q<removedItems.size();q++)
				{
					detectedBeats.remove(removedItems.get(q));
				}


			System.out.println("k "+ k+" size: "+temp.size()+"\t "+result[0]+ "\t "+ratio);
		}
	//	System.out.println(type+" "+finalAverage);

		for(int k=0;k<temp.size();k++)
		{
			temp.get(k).col = Color.CYAN;
			temp.get(k).predictedBeat = true;
		}

	//	System.out.println("data "+result[0]+" "+result[1]);
	}

}
