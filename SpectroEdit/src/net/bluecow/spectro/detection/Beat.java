package net.bluecow.spectro.detection;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class Beat
{
	public Beat(long highestIndex, float highestPoint)
	{
		soundIntensity = highestPoint;
		sampleLocation = highestIndex;
	}
	public static final int FRAME_SIZE = 1320;
	double soundIntensity;
	long sampleLocation;
	boolean predictedBeat;
	Color col;
	public String toString()
	{
		return ""+sampleLocation;
	}


	public static void writeBeatsToFile(ArrayList<Beat> beats)
	{
		System.out.println("Writing beats to file");
		try {
			File f = new File("test.txt");
			int counter = 0;
			while(f.exists())
			{
				f = new File("test"+counter+".txt");
				counter++;
			}
			f.createNewFile();
			FileOutputStream output = new FileOutputStream(f);
			PrintStream print = new PrintStream(output);
			for(Beat b:beats)
			{
				print.println(b.toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
