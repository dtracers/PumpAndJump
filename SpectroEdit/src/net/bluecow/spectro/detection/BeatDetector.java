package net.bluecow.spectro.detection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import ddf.minim.effects.BandPass;
import ddf.minim.effects.IIRFilter;

import net.bluecow.spectro.clipAndFrame.Beat;

public class BeatDetector
{
	//this is the instantaneous VEdata
	public ArrayList<float[]> VEdata = new ArrayList<float[]>();
	//this is the average VEdata (over 43 Energy histories
	public ArrayList<Double> AveragedEnergydata = new ArrayList<Double>();

	int historyLength = 43;//43;

	private double[] EnergyHistory = new double[historyLength];
	int currentHistoryIndex = 0;
//	private long sampleIndex;
	static ArrayList<Beat> detectedBeats;

	double maxEnergy = 0;

	private IIRFilter filter;

	int shiftAvg = 10;//this is used so that some of the future values are computed in the average of the current value

	public BeatDetector(IIRFilter bandPass)
	{
		filter = bandPass;
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
			g2.setColor(Color.blue);
			g2.drawLine((k-1)*4, (int)(startY - old[0]*ratio), k*4, (int)(startY - current[0]*ratio));
			g2.setColor(Color.red);
			g2.drawLine((k-1)*4, (int)(startY - old[1]*ratio), k*4, (int)(startY- current[1]*ratio));
			g2.setColor(Color.green);
			g2.drawLine((k-1)*4, (int)(startY - oldAvg*ratio), k*4, (int)(startY- currentAvg*ratio));
		}
		g2.setColor(Color.black);
		g2.drawLine(0, startY, length*4, startY);

	}

	public void preFilter(float[] timeData)
	{
		filter.process(timeData);
	}

	public void beatDetectionAlgorithm()
	{
		/**
		 * Will look for spikes that are above the average...
		 * Every spike above the average is a minor beat
		 * one the energy level crosses the average energy level we only take one spike until it falls back below
		 * This one spike is the maximum spike
		 *
		 * (maybe take two averages?)
		 */
	}
}
