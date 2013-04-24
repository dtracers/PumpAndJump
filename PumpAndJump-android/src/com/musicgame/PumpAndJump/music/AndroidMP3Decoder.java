package com.musicgame.PumpAndJump.music;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.media.MediaExtractor;
import android.os.Build;

import com.badlogic.gdx.files.FileHandle;
import com.musicgame.PumpAndJump.game.sound.InputDecoder;
import com.musicgame.PumpAndJump.game.sound.MP3Decoder;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AndroidMP3Decoder extends MP3Decoder
{

	MediaExtractor extractor = new MediaExtractor();
	public AndroidMP3Decoder(FileHandle file)
	{
		super(file);
		extractor.setDataSource(absolutePath);
	}

	/*

	@Override
	protected void createAudioStream(String file)
	{
		extractor.setDataSource(file);
		buf = ByteBuffer.allocate(frameSize);
	}

	*/
	@Override
	public MP3Decoder getInstance(FileHandle file)
	{
		return null;
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
	public int readSamples(short[] samples, int offset, int numSamples) {
		return 0;
	}

}
