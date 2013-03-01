package net.bluecow.spectro.inputReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.bluecow.spectro.math.AudioFileUtils;

public class WavDecoder extends InputDecoder{

	/**
	 * The only audio format we can use to make wave files
	 */
	private static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100.0F, 16, 1, true, true);

	public WavDecoder(double spectralScale, File file) {
		super(spectralScale, file);
		createAudioStream(file);
	}


	@Override
	protected void createAudioStream(File file)
	{
		AudioFormat desiredFormat = AUDIO_FORMAT;
		this.sampleRate = desiredFormat.getSampleRate();
		setFrameSize(sampleRate);
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(AudioFileUtils.readAsMono(desiredFormat, file));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		din = in;
	}

	@Override
	/**
	 * Puts the entire song into a single buffer (memory expensive yes but then the filter should work better)
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected float[] readEntireArray(InputStream in) throws IOException
	{
		ArrayList<double[]> buffers = new ArrayList<double[]>();
		byte[] buf = new byte[this.frameSize * 2];

		in.mark(buf.length * 2);
		int n;
		while ((n = readFully(in, buf)) != -1)
		{
			if (n != buf.length)
			{
				for (int i = n; i < buf.length; i++)
				{
					buf[i] = 0;
				}
			}
			double[] samples = new double[this.frameSize];
			for (int i = 0; i < this.frameSize; i++)
			{
				int hi = buf[(2 * i)];
				int low = buf[(2 * i + 1)] & 0xFF;
				int sampVal = hi << 8 | low;
				samples[i] = (sampVal / this.spectralScale);
			}
			buffers.add(samples);
			//this.frames.add(new Frame(samples, windowFunc));
			in.reset();
			long bytesToSkip = this.frameSize * 2 / this.overlap;
			long bytesSkipped;
			if ((bytesSkipped = in.skip(bytesToSkip)) != bytesToSkip)
			{
			}
			in.mark(buf.length * 2);
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

	private int readFully(InputStream in, byte[] buf)
		    throws IOException
		{
			int offset = 0;
			int length = buf.length;
			int bytesRead = 0;
			while ((offset < buf.length) && ((bytesRead = in.read(buf, offset, length)) != -1))
			{
				length -= bytesRead;
				offset += bytesRead;
			}
			if (offset > 0) {
				return offset;
			}
			return -1;
		}


	@Override
	public float[] readSeparately() throws IOException {
		byte[] buf = new byte[this.frameSize * 2];

		din.mark(buf.length * 2);
		int n;
		float[] samples = null;
		if((n = readFully(din, buf)) != -1)
		{
			if (n != buf.length)
			{
				for (int i = n; i < buf.length; i++)
				{
					buf[i] = 0;
				}
			}
			samples = new float[this.frameSize];
			for (int i = 0; i < this.frameSize; i++)
			{
				int hi = buf[(2 * i)];
				int low = buf[(2 * i + 1)] & 0xFF;
				int sampVal = hi << 8 | low;
				samples[i] = (float)(sampVal / this.spectralScale);
			}
			//this.frames.add(new Frame(samples, windowFunc));
			din.reset();
			long bytesToSkip = this.frameSize * 2 / this.overlap;
			long bytesSkipped;
			if ((bytesSkipped = din.skip(bytesToSkip)) != bytesToSkip)
			{
			}
			din.mark(buf.length * 2);
		}

		return samples;
	}

}
