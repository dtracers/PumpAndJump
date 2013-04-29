package com.musicgame.PumpAndJump.music;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import com.badlogic.gdx.files.FileHandle;
import com.musicgame.PumpAndJump.game.sound.MP3Decoder;

public class DesktopMP3Decoder extends MP3Decoder
{
	int sampleRate = 0;
	public InputStream din;
	public DesktopMP3Decoder(FileHandle file)
	{
		super(file);
		if(file == null)
			return;
		File f = file.file();
		AudioInputStream ain = null;
		try{
			AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(f);
			//AudioFormat baseFormat = baseFileFormat.getFormat();
			if (baseFileFormat instanceof TAudioFileFormat)
			{
				Map<String, Object> properties = ((TAudioFileFormat)baseFileFormat).properties();
				sampleRate = (Integer)properties.get("mp3.frequency.hz");
			}
			else
			{
				sampleRate = 44100;
			}
			ain= AudioSystem.getAudioInputStream( f );
		}catch( Exception e )
		{

		}
		System.out.println( "SampleRate: " + sampleRate );
		try {
			din = AudioFileUtils.readMP3AsMono( f , ain );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MP3Decoder getInstance(FileHandle file) {
		return new DesktopMP3Decoder(file);
	}

	@Override
	public int skipSamples(int paramInt) {
		return 0;
	}

	@Override
	public int getChannels() {
		return 0;
	}

	@Override
	public int getRate() {
		return 0;
	}

	@Override
	public float getLength() {
		return 0;
	}

	@Override
	public void dispose() {
	}

	@Override
	public int readSamples(short[] samples, int offset, int numSamples)
	{

		byte[] buf = new byte[numSamples];

		int nBytesRead = 0;
        try {
			nBytesRead = din.read(buf, 0, buf.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
        if(nBytesRead!=-1)
        {
	        for(int k = 0;k<numSamples;k++)
	        {
	        	samples[offset+k] = buf[k];
	        }
        }
        /*
        float[] samples = null;

	        samples= new float[this.frameSize];

	        for (int i = 0; i < this.frameSize; i++)
			{
				int hi = buf[(2 * i)];
				int low = buf[(2 * i + 1)] & 0xFF;
				int sampVal = hi << 8 | low;
				samples[i] = (float)(sampVal / this.spectralScale);
			}
	        */

	    return nBytesRead;
	}


}
