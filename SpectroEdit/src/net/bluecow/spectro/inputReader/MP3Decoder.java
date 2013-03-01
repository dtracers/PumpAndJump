package net.bluecow.spectro.inputReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;

import net.bluecow.spectro.math.AudioFileUtils;

public class MP3Decoder extends InputDecoder
{
	public MP3Decoder(double spectralScale, File file) {
		super(spectralScale, file);
		createAudioStream(file);
	}


	@Override
	protected void createAudioStream(File file)
	{
		try {
			din = AudioFileUtils.readMP3AsMono( file );
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.sampleRate = ((AudioInputStream) din).getFormat().getSampleRate();
	}


	/**
	 * Puts the entire song into a single buffer (memory expensive yes but then the filter should work better)
	 * @param in
	 * @return
	 * @throws IOException
	 */
	@Override
	protected float[] readEntireArray(InputStream in) throws IOException
	{
		ArrayList<double[]> buffers = new ArrayList<double[]>();

		byte[] buf = new byte[this.frameSize];
		byte[] oldbuf = new byte[this.frameSize];

		int nBytesRead = 0;

	    while (nBytesRead != -1)
	    {
	        nBytesRead = in.read(buf, 0, buf.length);

	        double[] samples = new double[this.frameSize];

	        for (int i = 0; i < this.frameSize/2; i++)
			{
				int hi = oldbuf[(2 * i)];
				int low = oldbuf[(2 * i + 1)] & 0xFF;
				int sampVal = hi << 8 | low;
				samples[i] = (sampVal / this.spectralScale);
			}

	        for (int i = 0; i < this.frameSize/2; i++)
			{
				int hi = buf[(2 * i)];
				int low = buf[(2 * i + 1)] & 0xFF;
				int sampVal = hi << 8 | low;
				samples[ this.frameSize/2 + i] = (sampVal / this.spectralScale);
			}


	        for( int i = 0; i < buf.length; i++ )
	        {
	        	oldbuf[i] = buf[i];
	        }

	        buffers.add( samples );

	    }

		float[] totalBuffer = new float[buffers.size()*this.frameSize];
		int index = 0 ;

		//copy the entire thing over
		for(double[] tempBuffer:buffers)
		{
			for(int k=0;k<tempBuffer.length;k++)
			{
				totalBuffer[index] = (float)tempBuffer[k];
				index++;
			}
		}
		buffers.clear();
		buffers = null;
		System.gc();
		return totalBuffer;
	}

}
