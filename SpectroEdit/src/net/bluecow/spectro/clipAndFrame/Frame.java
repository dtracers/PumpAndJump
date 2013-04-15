package net.bluecow.spectro.clipAndFrame;

import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.bluecow.spectro.windowFunctions.NullWindowFunction;
import net.bluecow.spectro.windowFunctions.VorbisWindowFunction;
import net.bluecow.spectro.windowFunctions.WindowFunction;

		public class Frame
		{
			//private static final Logger logger = Logger.getLogger(Frame.class.getName());
			public int octave;
			private static DoubleDCT_1D dct[];
			private static WindowFunction preFunc[];
			private static double midiValues[];
			private static double coef[];
			private static int binNumber[];
			private double noteData[][];
			private int timeLength;
			private double mult[];
			private static double preMult[] = { .5, .5, .5, .5, .25, .05, .25, .5, .5, .5, .5 };

			private double[][] VEdata;
			//private final WindowFunction preWindowFunc;
			//private final WindowFunction postWindowFunc;

			public static void SetUp( double sampleRate, int frameSize )
			{
				if( midiValues == null )// actual values of each midi value
				{
					midiValues = new double[128];
					for( int i = 0; i < 128; i++ )
					{
						midiValues[i] = 8.1757989916D*Math.pow( 2.0D, (1.0D/12.0D)*((double)i+8.0D) );// midi formula
						System.out.println( "Octave"+i/12+" Note"+i%12+" MidiFreq"+midiValues[i] );
					}
				}

				if( coef == null )// the coefficients for each linear regression approximated and creates the proper Vorbis Window Function
				{
					coef = new double[11];
					preFunc = new WindowFunction[11];
					for( int i = 0; i < 11; i++ )
					{
						int fs;
						if( i < 7 )
							fs = (frameSize)/( 1<<i );// find the frame size for octave
						else
							fs = (frameSize)/( 1<<7 );
						coef[i] = sampleRate/((double)2*fs);
						System.out.println( "Octave"+i+" frameSize"+fs+" Coeff"+coef[i] );
						preFunc[i] = new VorbisWindowFunction( fs );
					}
				}

				if( binNumber == null )// calculating which bin the midi note will go in
				{
					binNumber = new int[ 128 ];
					for( int i = 0; i < 11; i++ )
					{
						for( int j = 0; j < 12 && i*12+j < 128; j++ )
						{
							binNumber[i*12+j] = (int)(( midiValues[i*12+j ] / coef[i]+1.));
							System.out.println( "Octave"+i+" Note"+j+" bin"+binNumber[i*12+j] );
						}
					}
				}

				if( dct == null )// constructing the needed dcts
				{
					dct = new DoubleDCT_1D[11];
					for(int i = 0; i < 11; i++ )
					{
						int fs;
						if( i < 7 )
							fs = (frameSize)/( 1<<i );// find the frame size for octave
						else
							fs = (frameSize)/( 1<<7 );
						dct[i] = new DoubleDCT_1D( fs );
					}
				}
			}

			/**
			 * Creates the frame after applying the preWindowFunction and then does the transform and then the postWindowFunction
			 * timeData is altered during the method
			 * @param timeData
			 * @param windowBefore
			 * @param windowAfter
			 */
			public Frame(double[] timeData, int octave )
			{
				int numOfSections;

				this.octave = octave;

				if( octave < 7 )// after about octave 7 it becomes too expensive to do more sections for the higher frequencies
					numOfSections = 1<<octave;
				else
					numOfSections = 1<<7;

				int frameSize = timeData.length/numOfSections;

				timeLength = timeData.length;

			//	System.out.println(timeLength);
				//put volume here
				VEdata = calculateVE(timeData);
				//WindowFunction preWindowFunc = preFunc[ octave ];
				//WindowFunction postWindowFunc = new NullWindowFunction();

				double sectionData[][] = new double[numOfSections][ frameSize ];


				for( int i = 0; i < numOfSections; i++ )//separating the sections of the time data
				{
					for( int j = 0; j < frameSize; j++ )
					{
						sectionData[i][j] = timeData[ frameSize*i + j ];
					}
				}

				double SectionAverages[] = new double[ numOfSections];
				double SectionAveragesWONotes[] = new double[ numOfSections ];
				//double SectionSD[] = new double[ numOfSections ];
				double NoiseSD[] = new double[ numOfSections ];
				mult = new double[ numOfSections ];

				for( int i = 0; i < numOfSections; i++ )// changing each section from time domain to frequency domain
				{
					//applies the function before the transform
					preFunc[octave].applyWindow( sectionData[i] );

					//transform is scaled
					dct[octave].forward( sectionData[i], true);

					//post transform window
					//postWindowFunc.applyWindow( sectionData[i] );

					double SectionSum = 0.0D;
					for( int j = 0; j < frameSize; j++ )
					{
						SectionSum += Math.abs(sectionData[i][j] );
					}

					SectionAverages[ i ] = SectionSum / frameSize;

					int noteCount = 0;
					for( int j = 0; j < 12 && octave*12+j < 128; j++ )//finding notes
					{
						int bn = binNumber[ octave*12 + j ];
						SectionSum -= Math.abs(sectionData[i][bn] );
						noteCount++;
					}

					SectionAveragesWONotes[ i ] = SectionSum / (frameSize - noteCount);

					//double SectionVariance = 0.0D;
					//for( int j = 0; j < frameSize; j++ )
					//{
						//SectionVariance += ( Math.abs( sectionData[i][j] ) - SectionAverages[i] ) * ( Math.abs( sectionData[i][j] ) - SectionAverages[i] ) ;
					//}
					//SectionSD[i] = Math.sqrt( SectionVariance );

					double SectionVarianceWONotes = 0.0D;
					for( int j = 0; j < frameSize; j++ )
					{
						double temp = ( Math.abs( sectionData[i][j] ) - SectionAveragesWONotes[i] );
						SectionVarianceWONotes += temp * temp;
					}

					for( int j = 0; j < 12 && octave*12+j < 128; j++ )//finding notes
					{
						int bn = binNumber[ octave*12 + j ];
						double temp = ( Math.abs( sectionData[i][bn] ) - SectionAveragesWONotes[i] );
						SectionVarianceWONotes -= temp*temp;
					}

					NoiseSD[i] = Math.sqrt( SectionVarianceWONotes );

					mult[i] = SectionAverages[i]/NoiseSD[i];
				}

				noteData = new double[ 12 ][ numOfSections ];
				double NoteAverages[] = new double[ numOfSections ];
				double NoteSD[] = new double[ numOfSections ];

				for( int i = 0; i < numOfSections; i++ )
				{
					double NoteSum = 0.0D;
					int noteCount = 0;
					double NoteVariance = 0.0D;

					for( int j = 0; j < 12 && octave*12+j < 128; j++ )//finding notes
					{
						int bn = binNumber[ octave*12 + j ];
						noteData[ j ][ i ] = sectionData[ i ][ bn ];
						NoteSum += Math.abs( noteData[j][i] );
						noteCount++;
					}

					NoteAverages[ i ] = NoteSum / noteCount;

					for( int j = 0; j < 12 && octave*12+j < 128; j++ )
					{
						double temp = ( Math.abs( noteData[j][i] ) - NoteAverages[i] );
						NoteVariance += temp*temp;
					}

					NoteSD[i] = Math.sqrt( NoteVariance );
				}

				for( int i = 0; i < noteData.length; i++ )
				{
					for( int j = 0; j < noteData[i].length; j++ )
					{
							noteData[i][j] *= mult[j];
					}
				}
				timeData = null;
				sectionData = null;
			}


		public double[] getNoteDataFor( int note )
		{
			return noteData[ note ];
		}

		public static int getOctaveLB( int octave )
		{
			if( binNumber == null )
				return -1;
			return binNumber[ octave*12 ];
		}

		public static int getOctaveUB( int octave )
		{
			if( binNumber == null )
				return -1;
			int note = octave*12 + 11;
			if( note >= 128 ) note = 127;
			return binNumber[ note ];
		}

	   	public double[] asTimeData()
	   	{
		   	double[] timeData = new double[timeLength];

		   	int numOfSections;

			if( octave < 7 )
				numOfSections = 1<<octave;
			else
				numOfSections = 1<<7;

			int frameSize = timeLength/numOfSections;

			for( int i = 0; i < numOfSections; i++ )
			{
				double timeDataTemp[] = new double[frameSize];

				for( int note = 0; note < 12 && (octave*12 + note) < 128; note++ )// place notes in appropriate bins
				{
					int bn = binNumber[ octave*12+note ];
					timeDataTemp[ bn ] += noteData[ note ][ i ];
				}

				dct[ octave ].inverse( timeDataTemp, true);//do inverse

				preFunc[ octave ].applyWindow( timeDataTemp );// reapply the window function

				for( int j = 0; j < timeDataTemp.length; j++ )// place into time data
				{
					timeData[ frameSize*i + j ] = timeDataTemp[ j ];
				}
			}
	    	return timeData;
	  	}


	   	/**
	   	 * David methods below
	   	 * in array [0] it is the volume
	   	 * in array [1] it is the energy
	   	 * the length of the array is the size of the given array/1024
	   	 */
	   	public double[][] calculateVE(double[] timeData)
	   	{

	   		//the size of bits that the array is taken over
	   		int averageSize = 1024;
	   		//number of values
	   		int length = timeData.length/averageSize+1;
	   		int leftOver = timeData.length%1024;
	   		double[][] result = new double[2][length];
	   		int index = 0;
	   		for(int k = 0;k<length;k++)
	   		{
	   			if(leftOver!=0&&k==length-1)
	   			{
	   				averageSize = leftOver;
	   			}
	   			double volume = 0;
	   			double energy = 0;
	   			for(int q = 0; q<averageSize;q++)
	   			{
	   				double data = timeData[index];
	   				volume+=data;
	   				energy+=data*data;
	   				index++;
	   			}
	   			result[0][k]=volume;
	   			result[1][k]=energy;
	   		}
	   		return result;
	   	}

}