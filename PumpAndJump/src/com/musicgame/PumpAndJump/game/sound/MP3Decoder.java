package com.musicgame.PumpAndJump.game.sound;


import java.io.File;

import com.badlogic.gdx.audio.io.Decoder;
import com.badlogic.gdx.files.FileHandle;

public abstract class MP3Decoder extends Decoder
{
	protected String absolutePath = null;
	/**
	 * It is empty and does absolutely nothing for right now
	 * @param file
	 */
	public MP3Decoder(FileHandle file)
	{
		if(file!=null)
		{
			File f = file.file();
			if(f!=null)
			{
				absolutePath = f.getAbsolutePath();
			}
		}
	}

	/**
	 * Will create a specific instance of the MP3 decoder with the given file
	 * @param file
	 * @return
	 */
	public abstract MP3Decoder getInstance(FileHandle file);

	/**
	 * @param samples		this is the short array that the values are copied into
	 * @param offset		this is the offest
	 * @param numsamples	the number samples that will be read
	 */
	public abstract int readSamples(short[] samples, int offset, int numSamples);

}
