package com.musicgame.musicCompiler;

public abstract class MusicInputStream
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
	public abstract MusicInputStream generateInstance();

	/**
	 * Will generate a new instance of the MusicInputStream with the given fileName
	 * @return
	 */
	public abstract MusicInputStream generateInstance(String fileName);


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
