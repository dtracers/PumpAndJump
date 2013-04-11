package com.musicgame.PumpAndJump.Util;

public abstract class MusicOutputStream
{
	String file;
	FileType type;
	public void setFile(String fileName)
	{
		file = fileName;
	}

	/**
	 * Will generate a new instance of the MusicInputStream
	 * @return
	 */
	public abstract MusicOutputStream generateInstance();

	/**
	 * Will generate a new instance of the MusicInputStream with the given fileName
	 * @return
	 */
	public abstract MusicOutputStream generateInstance(String fileName);


	/**
	 * Reads in numSamples at offset into the samples array
	 * may return succes or not depending on implemenetaion
	 * @param samples
	 * @param offset
	 * @param numSamples
	 * @return
	 */
	public abstract int readData(short[] samples, int offset, int numSamples);

	private enum FileType
	{
		MP3,WAVE;
	}

	public String testWorking()
	{
		System.out.println("WORKING!");
		return "working";
	}
}
