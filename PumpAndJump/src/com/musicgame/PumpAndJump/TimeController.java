package com.musicgame.PumpAndJump;

public class TimeController {
	
	double[] furlongSpeeds;// holds the speed in furlongs / tock
	double[] furlongDistanceStarts;// holds the starting point in furlongs of the tock sections
	
	
	int indexOfCurrentTockSection;// holds the current tock section index
	double currentTocks;// holds the current tocks( time )
	double currentFurlongDistance;// holds the current furlong distance based on the time
	
	public TimeController( double[] furlongLengthOfSections )
	{
		furlongSpeeds = new double[ furlongLengthOfSections.length ];
		furlongDistanceStarts = new double[ furlongLengthOfSections.length ];
		
		double tempFurlongs = 0.0;
		
		for( int i = 0; i < furlongLengthOfSections.length; i++ )
		{
			furlongSpeeds[i] = furlongLengthOfSections[i] / 0.250;;// calculating speed over the section
			
			furlongDistanceStarts[i] = tempFurlongs; // cacluating the furlong starting point of the section
			tempFurlongs += furlongLengthOfSections[i];
		}
	}
	
	public int getTockSectionIndex( double time )
	{
		return (int)(4*time);
	}

}
