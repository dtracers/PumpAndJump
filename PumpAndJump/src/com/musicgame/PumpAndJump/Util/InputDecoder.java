package com.musicgame.PumpAndJump.Util;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.Files.FileType;


public abstract class InputDecoder
{
	protected int overlap = 2;
	protected double spectralScale;
	protected InputStream din;
	public int frameSize = 42314;
	protected float sampleRate;
	String file;
	FileType filetype;
	public InputDecoder(double spectralScale,String file)
	{
		this.spectralScale = spectralScale;
	}

	public static InputDecoder createInputDecoder( double spectralScale, String file )
	{
		/*
		if( file.getName().endsWith(".wav") || file.getName().endsWith(".aif") )
		{
			return new WavDecoder( spectralScale, file );
		}
		else if( file.getName().endsWith(".mp3") )
		{
			return new MP3Decoder( spectralScale, file );
		}
		*/
		return null;
	}

	public void setFrameSize( float sampleRate )
	{
		int t = (int)(sampleRate/133.4031928D);

		if( t % 2 == 1)
			t = t+1;

		frameSize = t*(int)(Math.pow(2.0, 7.0));
	}

	/**
	 * Creates the AuddioStream from the given file
	 * it will also set the sampleRate at this point
	 */
	protected abstract void createAudioStream(String file);

	/**
	 * uses the created audioStream to read and return the entire array
	 * @return
	 * @throws IOException
	 */
	public final float[] readEntireArray() throws IOException
	{
		return readEntireArray(din);
	}

	/**
	 * Creates the AuddioStream from the given file and then reads the entire array putting the music into a float array
	 */
	public final float[] readEntireArray(String file) throws IOException
	{
		createAudioStream(file);
		return readEntireArray();
	}

	/**
	 * It reads then separately and returns them in the order it is read
	 * @return
	 * @throws IOException
	 */
	public abstract float[] readSeparately() throws IOException;

	/**
	 * Reads the entire array from the given input stream
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected abstract float[] readEntireArray(InputStream in) throws IOException;

	public float getSampleRate()
	{
		return sampleRate;
	}

	public final void close()
	{

		try {
			din.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

