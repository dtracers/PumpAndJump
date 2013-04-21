package net.bluecow.spectro.detection.tempo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import net.bluecow.spectro.detection.Beat;

public abstract class TempoDetector
{
	public static boolean compareTestPrintout = false;
	public static boolean cullingPrintout = true;
	public static boolean anyPrintout = false;
	public static boolean cullingPrintoutDetailed = false;

	public static double samplingRate = 44100;
	public static double frameSize = 1320;
	public static double BPMtoFrameRatio = (60.0*samplingRate/frameSize);//the ratio from an BPM to actual frame distances
	public static double SlowestBPM = 20;
	public static double maxIntervalAllowed = BPMtoFrameRatio/SlowestBPM;

	static int numberOfBeats = 60;

	ArrayList<Beat> detectedBeats;
	public static double realTempo;

	public TempoDetector(ArrayList<Beat> beats)
	{
		detectedBeats = beats;
	}

	public final void detectTempo()
	{
		detectTempo(detectedBeats.size()-numberOfBeats);
	}

	/**
	 * Attempts to detect the tempo of the music
	 */
	public abstract void detectTempo(int startIndex);


	public void createBeatsFromAFile(String fileName)
	{
			detectedBeats = new ArrayList<Beat>();
			Scanner s = null;
			try
			{
				s = new Scanner(new File(fileName));
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			System.out.println("Reading");
			if(s.hasNextDouble())
			{
				realTempo = s.nextDouble();
			}
			while(s.hasNext())
			{
				long s2 =s.nextLong();
				detectedBeats.add(new Beat(s2,0,detectedBeats.size()));
			}
			/*
			System.out.println("Done Reading "+detectedBeats.size());
			TempoDetector detector = new TempoDetector(detectedBeats);

			for(int k = 0; k<detectedBeats.size()-detector.numberOfBeats;k++)
			{
				System.out.println("TEMPO RUN THROUGH TIME: "+k);
				detector.detectTempoPermutations(k);
			//	detector.printDistanceSets();
			}
			detector.printDistanceSets();
			System.out.println("\t"+maxDistanceAllowed);
			*/
	}

	public void detectTempoFromBeatList()
	{
		for(int k = 0; k<detectedBeats.size()-numberOfBeats;k++)
		{
			System.out.println("TEMPO RUN THROUGH TIME: "+k);
			detectTempo(k);
		//	detector.printDistanceSets();
		}
	}

	public abstract void setTempoBeats();

	/**
	 * Calculates the tempo from the average distance
	 * @param distance
	 * @return
	 */
	public static double calculateTempoFromDistance(double distance)
	{
		double BPS = samplingRate/(distance*frameSize);
		return BPS*60.0;
	}

	/**
	 * Calculates the distance from a tempo in BPM
	 * @param intervalSize
	 * @return
	 */
	public static double calculateDistanceFromTempo(double tempo)
	{
		double BPS = tempo/60.0;
		double distance = samplingRate/(BPS*frameSize);
		return distance;
	}

	public static double calculateTimeFromLocation(double location)
	{
		return location*frameSize/samplingRate;
	}
}
