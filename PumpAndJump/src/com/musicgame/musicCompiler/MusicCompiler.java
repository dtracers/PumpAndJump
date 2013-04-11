package com.musicgame.musicCompiler;

import java.util.ArrayList;

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
	String fileName = "Skrillex_Cinema.wav";
	Decoder decoder;
	public ArrayList<short[]> frames = new ArrayList<short[]>();
	public int currentFrame;
	int frameSize = 1024;
	public boolean buffering = true;
	//do frame stuff here

	public void loadSound()
	{
		decoder = new WavDecoder(Gdx.files.internal(fileName));
	}

	/**
	 * The running thread that decompiles the music
	 */
	public void run()
	{
		int readSong = 0;
		while(readSong ==0)
		{
			short[] frame = new short[frameSize];
			//readSamples(short[] samples, int offset, int numSamples)
			readSong = decoder.readSamples(frame,0, frameSize);
			frames.add(frame);
			currentFrame++;
			decoder.skipSamples(frameSize);
			if(buffering)
			{
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else
			{
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
