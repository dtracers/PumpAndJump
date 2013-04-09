package net.bluecow.spectro.inputReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestDecoder extends MP3Decoder
{

	public TestDecoder(double spectralScale, File file) {
		super(spectralScale, file);
	}

	@Override
	public void createAudioStream(File file)
	{
		sampleRate = 44100;
		setFrameSize(sampleRate);
		final double[] midiValues = new double[128];
		for( int i = 0; i < 128; i++ )
		{
			midiValues[i] = 8.1757989916D*Math.pow( 2.0D, (1.0D/12.0D)*((double)i+8.0D) );// midi formula
			System.out.println( "Octave"+i/12+" Note"+i%12+" MidiFreq"+midiValues[i] );
		}

		din = new InputStream()
		{
			final double adjustedFrequency = Math.PI*2.0/44100.0;
			long point = 1;
			int i = 0;
			int counter = 0;
			@Override
			public int read() throws IOException
			{
				double value = (point%44100)*adjustedFrequency*midiValues[i];
				if(point%44100==0&&point!=0)
				{
					counter++;
				}
				if(counter%3==0&&counter!=0)
				{
					counter = 0;
					i++;
	//				System.out.println(" point "+point+" counter "+counter+" i "+i);
				}
				if(i>=127)
				{
					return -1;
				}
				point++;
				double result = (Math.sin(value))+1;
				//System.out.println(" point "+point+" counter "+counter+" i "+i);
				return (int)result*10000;
			}
		};
	}

	@Override
	public float[] readSeparately() throws IOException
	{

		byte[] buf = new byte[this.frameSize];

		int nBytesRead = 0;
        nBytesRead = din.read(buf, 0, buf.length);
        float[] samples = null;
        if(nBytesRead!=-1)
        {
	        samples= new float[this.frameSize];

	        for (int i = 0; i < this.frameSize; i++)
			{
			//	int hi = buf[(2 * i)];
			//	int low = buf[(2 * i + 1)] & 0xFF;
			//	int sampVal = hi << 8 | low;
				samples[i] = buf[i]-1;//(float)(sampVal / this.spectralScale);
			}

        }
	    return samples;

    }

}
