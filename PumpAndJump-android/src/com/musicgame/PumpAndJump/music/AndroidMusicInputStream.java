package com.musicgame.PumpAndJump.music;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.media.MediaExtractor;
import android.os.Build;

import com.musicgame.PumpAndJump.Util.InputDecoder;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AndroidMusicInputStream extends InputDecoder
{
	MediaExtractor extractor = new MediaExtractor();
	public AndroidMusicInputStream(double spectralScale, String file)
	{
		super(spectralScale, file);
	}

	@Override
	protected void createAudioStream(String file)
	{
		extractor.setDataSource(file);
		buf = ByteBuffer.allocate(frameSize);
	}

	@Override
	public float[] readSeparately() throws IOException
	{
		extractor.readSampleData(buf, 0);//not sure how bytebuffers work... need to figure this out
		return null;
	}

	/**
	 * this will read the entire array
	 */
	@Override
	public float[] readEntireArray()
	{
		return null;
	}

}
