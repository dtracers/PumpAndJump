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
public class MusicInputStreamer extends Thread
{
	String fileName = "Skrillex_Cinema.wav";
	Decoder decoder;
	public ArrayList<short[]> frames = new ArrayList<short[]>();
	public int currentFrame;
	public int frameSize = 1024/4;
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
		int readSong = 1;
		while(readSong != 0)
		{
			short[] frame = new short[frameSize*2];
			short[] frame2 = new short[frameSize];
			//readSamples(short[] samples, int offset, int numSamples)
			readSong = decoder.readSamples(frame,0, frameSize*2);
			for(int k=0;k<frame2.length;k++)
			{
				frame2[k] = (short) ((frame[k*2]+frame[k*2+1])/2.0);//gets half of the song (maybe because it is stereo?)
			}
		//	System.out.println("Reading the song" +readSong+" "+currentFrame);
			frames.add(frame2);
			currentFrame++;
		//	decoder.skipSamples(frameSize);
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
