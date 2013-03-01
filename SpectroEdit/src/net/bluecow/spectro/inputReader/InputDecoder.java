package net.bluecow.spectro.inputReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public abstract class InputDecoder
{
	protected int overlap = 2;
	protected InputStream din;
	protected double spectralScale;
	public static int frameSize = 42314;
	protected float sampleRate;
	public InputDecoder(double spectralScale,File file)
	{
		this.spectralScale = spectralScale;
	}

	/**
	 * Creates the AuddioStream from the given file
	 * it will also set the sampleRate at this point
	 */
	protected abstract void createAudioStream(File file);

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
	public final float[] readEntireArray(File file) throws IOException
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
