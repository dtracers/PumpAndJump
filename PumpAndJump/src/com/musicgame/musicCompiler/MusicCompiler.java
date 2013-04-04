package com.musicgame.musicCompiler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.io.Decoder;
import com.badlogic.gdx.audio.io.WavDecoder;

/**
 * This is the thread that runs and decompiles the music
 * @author gigemjt
 *
 */
public class MusicCompiler extends Thread
{
	String fileName;
	Decoder mysounddecoder;

	public void loadSound()
	{
		mysounddecoder = new WavDecoder(Gdx.files.internal("drop.wav"));
	}

	/**
	 * The running thread that decompiles the music
	 */
	public void run()
	{

	}
}
