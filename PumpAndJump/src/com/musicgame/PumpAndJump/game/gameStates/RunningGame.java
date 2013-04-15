package com.musicgame.PumpAndJump.game.gameStates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.musicgame.PumpAndJump.GameObject;
import com.musicgame.PumpAndJump.LevelInterpreter;
import com.musicgame.PumpAndJump.Player;
import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;
import com.musicgame.PumpAndJump.game.sound.MusicInputStreamer;
import com.musicgame.PumpAndJump.game.sound.MusicOutputStream;

public class RunningGame extends GameThread
{
	Stage stage;

	MusicInputStreamer streamer;
	MusicOutputStream outStreamer = new MusicOutputStream();

	/**
	this is a list of the on screen objects
	(by on screen it does include some that are partially off the screen too)
	the objects are basically a queue added at the end and removed from the front
	*/
	ArrayList<GameObject> levelObjects = new ArrayList<GameObject>();
	//contains the list of all objects that are in the level
	ArrayList<GameObject> actualObjects = new ArrayList<GameObject>();

	//Player object
	Player player;
	//the current frame that the sound player is at
	long soundFrame = 0;
	int bufferDistance = 200;
	long sampleRate = 44100;
	long start = 0;
	boolean toWait = false;
	private boolean started = false;

	long previousTime;
	long currentTime;
	float divide = 700;

	private boolean songFinished = false;

	public RunningGame()
	{
		reset();
	}

	/**
	 * Sets up the game for running
	 */
	public void reset()
	{
		stage = new Stage();

		/**
		A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
		*/
		FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
	    Skin uiSkin = new Skin( skinFile );
	    addButtonsInvisible(stage,uiSkin);
		// Create a table that fills the screen. Everything else will go inside this table.

		//table.debug(); // turn on all debug lines (table, cell, and widget)
		//table.debugTable(); // turn on only table lines

        player = new Player( new Point( 400.0f, 500.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ) );
		// Create a table that fills the screen. Everything else will go inside this table.

        soundFrame = 0;
	}

	/**
	 *
	 * @param stage
	 */
	public void addButtonsVisible(Stage stage,Skin uiSkin)
	{
		final TextButton pauseButton = new TextButton("||", uiSkin);
				pauseButton.setBounds(300, 300, 50, 50);
				pauseButton.addListener(
						new ChangeListener()
						{
							@Override
							public void changed(ChangeEvent event, Actor actor)
							{
								System.out.println("pause");
								pausingButton();
							}
						});
				stage.addActor(pauseButton);

				final TextButton jumpButton = new TextButton("Jump", uiSkin);
				stage.addActor(jumpButton);
				jumpButton.setBounds(10,10, 50, 50);
				jumpButton.addListener(
						new ChangeListener()
						{
							@Override
							public void changed(ChangeEvent event, Actor actor)
							{
								jump();
							}
						});

				final TextButton duckButton = new TextButton("Duck", uiSkin);
				stage.addActor(duckButton);
				duckButton.setBounds(400,10, 50, 50);
				duckButton.addListener(
						new ChangeListener()
						{
							@Override
							public void changed(ChangeEvent event, Actor actor)
							{
								duck();
							}
						});
			//	table.add(aboutButton).size(50,50).pad(5);


	}

	public void addButtonsInvisible(Stage stage,Skin uiSkin)
	{
		Table table = new Table();
		table.setFillParent(true);

		stage.addActor(table);
		 //seting up the buttons
		final TextButton pauseButton = new TextButton("Pause", uiSkin);
	//	pauseButton.setColor(0.0f,0.0f,0.0f, 0.0f); //make buttons invisible when on screen (pause should be visible though)
		pauseButton.addListener(
				new ChangeListener()
				{
					public void changed(ChangeEvent event, Actor actor)
					{
						pausingButton();
					}
				});
		final TextButton jumpButton = new TextButton("Jump", uiSkin);
		jumpButton.setColor(0.0f,0.0f,0.0f, 0.0f); //make buttons invisible when on screen
		jumpButton.addListener(
				new ChangeListener()
				{
					@Override
					public void changed(ChangeEvent event, Actor actor)
					{
						jump();
					}
				});

		final TextButton duckButton = new TextButton("Duck", uiSkin);
		duckButton.setColor(0.0f,0.0f,0.0f, 0.0f); //make buttons invisible when on screen
		duckButton.addListener(
				new ChangeListener()
				{
					@Override
					public void changed(ChangeEvent event, Actor actor)
					{
						duck();
					}
				});
		table.add(jumpButton).expand().fill();
		table.add(pauseButton).expand().size(250,100).pad(5);
		table.add(duckButton).expand().fill();

	}


	/**
	 * Run method happens while the game is running
	 */
	@Override
	public void run()
	{
		previousTime = currentTime = System.currentTimeMillis();
		float delta = 0;
		while(true)
		{
			if(bufferingNeeded())
			{
				goBuffer();
			}else
			{
				writeSound();
			}
		//	System.out.println(actualObjects.size());
			previousTime = currentTime;
			currentTime = System.currentTimeMillis();
			delta = (currentTime-previousTime)/divide;
			player.update(delta);
			for(int k = 0;k<levelObjects.size();k++)
			{
				levelObjects.get(k).update(delta);
			}

			if(toWait)
				myWait();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void pause()
	{
		toWait = true;
	}

	@Override
	public void render(float delta)
	{
		batch.begin();
		for(int k = 0;k<levelObjects.size();k++)
		{
			levelObjects.get(k).draw(batch);
		}
	//	if(!toWait)
	//		player.update( delta );
		player.draw( batch );
		batch.end();
	//	Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
	//	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if(!toWait)
		{
			stage.act(Math.min(delta, 1 / 30f));
			stage.draw();
		}


	//	Table.drawDebug(stage);
	//	System.out.println(frame);
	}

	@Override
	public void show()
	{
		toWait = false;
	}

	@Override
	public void hide()
	{
		toWait = true;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void switchFrom(GameThread currentThread)
	{
		System.out.println("Switching!");
		//Pause button won't work without this commented out
		if(currentThread instanceof PauseGame)
		{
			Gdx.input.setInputProcessor(stage);
			this.myNotify();
			System.out.println("unpause");
		}else
		if(currentThread instanceof Buffering)
		{
			Gdx.input.setInputProcessor(stage);
			System.out.println("NOTIFYING");
			this.myNotify();
		}else
		if(currentThread instanceof PreGame)
		{
			Gdx.input.setInputProcessor(stage);
			try {
				actualObjects = LevelInterpreter.loadLevel();
			} catch (Exception e) {
				actualObjects = new ArrayList<GameObject>();
				e.printStackTrace();
			}
			streamer = new MusicInputStreamer();
			streamer.loadSound();
			streamer.start();
			if(!started)
			{
				started = true;
				this.start();
			}else
			{
				this.myNotify();
			}
		}
			//mysounddecoder = new WavDecoder(Gdx.files.internal("drop.wav"));
	}


	@Override
	public void addFrom(GameThread currentThread)
	{
	}

	@Override
	public void removeFrom(GameThread currentThread)
	{
		reset();
		System.out.println("BEING REMOBED");
	}

	/**
	 * Called after notify
	 */
	@Override
	public void unpause() {
		toWait = false;
		previousTime = System.currentTimeMillis();
		currentTime = System.currentTimeMillis();
	}

	@Override
	public void repause() {
	}

	/**
	 * This method will pause the game and go buffer for a little big
	 */
	public void goBuffer()
	{
		System.out.println("GO BUFFER!");
		streamer.buffering = true;
		toWait = true;
		PumpAndJump.addThread(ThreadName.Buffering, this);
	}

	/**
	 * The method that is called to pause the game for the pause button
	 */
	public void pausingButton()
	{
		pause();
		toWait = true;
		PumpAndJump.addThread(ThreadName.PauseGame, this);
	}

	/**
	 * Returns true if the bufferingDistance is less than the bufferDistance value
	 * it is calculated by: MusicInputStream.currentFrame - OuputStream.currentFrame
	 * @return
	 */
	public boolean bufferingNeeded()
	{
		return streamer.currentFrame-soundFrame<bufferDistance&&!streamer.doneReading;
	}

	/**
	 * Returns true if the bufferingDistance is less than the bufferDistance value
	 * it is calculated by: MusicInputStream.currentFrame - OuputStream.currentFrame
	 * @return
	 */
	public long bufferingDistance()
	{
		return bufferDistance - (streamer.currentFrame-soundFrame);
	}

	public void writeSound()
	{
	//	System.out.println("Output Sound "+soundFrame);
		if(!songFinished)
		{
			outStreamer.write(streamer.frames.get((int)soundFrame));
			soundFrame++;
			if(streamer.frames.size()<=soundFrame)
			{
				songFinished = true;
			}
		}
	}

	@Override
	public ThreadName getThreadName()
	{
		return ThreadName.RunningGame;
	}

	/**
	 * Called when the player presses the jump button
	 */
	public void jump()
	{
		System.out.println("Jumping");
		player.jump();
	}

	/**
	 * Called when the player presses the duck button
	 */
	public void duck()
	{
		System.out.println("Ducking");
		player.duck();
	}

}
