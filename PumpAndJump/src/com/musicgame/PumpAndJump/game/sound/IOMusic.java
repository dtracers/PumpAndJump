package com.musicgame.PumpAndJump.game.sound;

import io.MusicHandler;

import java.io.FileNotFoundException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.io.Decoder;
import com.badlogic.gdx.audio.io.VorbisDecoder;
import com.badlogic.gdx.audio.io.WavDecoder;
import com.badlogic.gdx.files.FileHandle;
import com.musicgame.PumpAndJump.Util.FileFormatException;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;
import com.musicgame.PumpAndJump.game.gameStates.RunningGame;
import com.musicgame.PumpAndJump.objects.ObjectHandler;

public class IOMusic extends MusicHandler
{
	RunningGame parentGame = null;
	public static String fileName= "the_hand_that_feeds.wav";

	BeatDetector detect;

	public IOMusic()
	{
		super();
	}

	public void loadSound() throws FileNotFoundException, FileFormatException
	{
		FileHandle file = null;
		try
		{
			file = Gdx.files.internal(fileName);
			if(file == null)
			{
				int i = 1/0;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				file = Gdx.files.absolute(fileName);
				if(file == null)
				{
					int i = 1/0;
				}
			}catch(Exception e2)
			{
				file = Gdx.files.internal("the_hand_that_feeds.wav");
				e2.printStackTrace();
			}
		}
		if(file == null||!file.exists())
		{
			throw new FileNotFoundException(fileName);

		}
		//it is not null and it exists

		Decoder decoder;

		String extension = file.extension();
		System.out.println("extesnsion is "+extension);
		if(extension.equalsIgnoreCase("wav"))
		{
			decoder = new WavDecoder(file);
		}else if(extension.equalsIgnoreCase("mp3"))
		{
			System.out.println("Creating MP3 FILE VERSIOn");
			decoder = PumpAndJump.MP3decoder.getInstance(file);
		//	decoder = new Mpg123Decoder(file);
		}else if(extension.equalsIgnoreCase("ogg"))
		{
			decoder = new VorbisDecoder(file);
		}else
		{
			throw new FileFormatException("File format not supported "+extension);
		}

		this.loadInput(new LibGDXInputDevice(decoder));
	}

	public void setUpOutputStream()
	{
		this.loadOutput(new LibGDXOutputDevice(44100,true));
	}

	public IOMusic(ObjectHandler actualObjects,RunningGame parent)
	{
		detect = new BeatDetector(actualObjects);
		setUpOutputStream();
		this.parentGame = parent;
	}

	@Override
	public void postWriteMethod(short[] longerArray)
	{
		detect.calculateVE(longerArray,inputTimeReference);
	}

	@Override
	public void buffering()
	{
	}

	@Override
	public void slowingDownInput()
	{
	}

	@Override
	public void dispose() {
		super.dispose();
		musicFile.reset();
		musicFile.empty();
		fileName = "the_hand_that_feeds.wav";
	}

	@Override
	protected void finishedSongOutput()
	{
		dispose();
		RunningGame.switchToPostGame(parentGame);
	}

	@Override
	protected void finishedSongInput() {
	}

}
