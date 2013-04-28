package com.musicgame.PumpAndJump.game.gameStates;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.musicgame.PumpAndJump.CameraHelp;
import com.musicgame.PumpAndJump.GameObject;
import com.musicgame.PumpAndJump.Obstacle;
import com.musicgame.PumpAndJump.Player;
import com.musicgame.PumpAndJump.Animation.Animation;
import com.musicgame.PumpAndJump.Animation.AnimationQueue;
import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;
import com.musicgame.PumpAndJump.Util.FileFormatException;
import com.musicgame.PumpAndJump.Util.LevelInterpreter;
import com.musicgame.PumpAndJump.Util.TextureMapping;
import com.musicgame.PumpAndJump.game.GameControls;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;
import com.musicgame.PumpAndJump.game.sound.MusicHandler;

public class RunningGame extends GameThread
{
	static Stage stage;
	static MusicHandler streamer;
	static File filename=null;
	static boolean pick=false;
	static String test=null;

	//contains the list of all objects that are in the level
	static ArrayList<Obstacle> actualObjects = new ArrayList<Obstacle>();
	static int lastStartIndex = 0;

	//Player object
	static Player player;
	//the current frame that the sound player is at
	long soundFrame = 0;
	//the timeRefernce of each object
	static double timeReference = 0;
	static double lastTimeReference = 0;

	public static float tempo = 240.0f;
	static Point pos;
	static Point rotation;
	static Point scale;
	static Animation levelAni;
	static AnimationQueue levelAniQ;
	static boolean toWait = false;
	private boolean started = false;
	static Sprite background;
	static Sprite leftBar;
	static Sprite rightBar;
	static OrthographicCamera cam;
	static Matrix4 oldProjection;

	private boolean songFinished = false;
	private boolean stopRunning = false;//if this is set to true the Thread will cease to exist
	GameControls controls;
	//define my listeners
	public ChangeListener jumpListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			jump();
		}
	};
	public ChangeListener duckListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			duck();
		}
	};
	public ChangeListener pauseListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			pausingButton();
		}
	};


	public RunningGame()
	{

	}

	/**
	 * Sets up the game for running
	 */
	public void reset()
	{
		System.out.println("WHY IS THIS NOT WORKING?");
		lastTimeReference = 0;
		lastStartIndex = 0;
		stage = new Stage();

		this.controls = new GameControls(jumpListener,duckListener,pauseListener);
		this.controls.controlsTable.setFillParent(true);
		stage.addActor(this.controls.controlsTable);
		//this.controls.setVisible( false );

        player = new Player( new Point( 80.0f, 40.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ) );
        float[] f = { 0.0f };
        levelAni = new Animation( "level1_ani.txt" );
        levelAniQ = new AnimationQueue( levelAni, f );
        cam = CameraHelp.GetCamera();

        background = new Sprite( TextureMapping.staticGet( "WhiteTemp.png" ) );
        background.setSize( CameraHelp.virtualWidth, Gdx.graphics.getHeight()  );
        background.setPosition( 0.0f, -Gdx.graphics.getHeight()/2.0f+60.0f );
        background.setColor( 0.0f, 0.0f, 0.0f, 1.0f );

        leftBar = new Sprite( TextureMapping.staticGet( "WhiteTemp.png" ) );
        rightBar = new Sprite( TextureMapping.staticGet( "WhiteTemp.png" ) );
        float barWidth = ( Gdx.graphics.getWidth() - CameraHelp.virtualWidth ) / 2.0f;

        leftBar.setSize( barWidth, Gdx.graphics.getHeight() );
        leftBar.setPosition( -barWidth, -Gdx.graphics.getHeight()/2.0f + 60.0f );
        leftBar.setColor( 0.8f, 0.8f, 1.0f, 1.0f );

        rightBar.setSize( barWidth, Gdx.graphics.getHeight() );
        rightBar.setPosition( CameraHelp.virtualWidth, -Gdx.graphics.getHeight()/2.0f + 60.0f );
        rightBar.setColor( 0.8f, 0.8f, 1.0f, 1.0f );

        pos = new Point( 0.0f, 0.0f, 0.0f );
        rotation = new Point( 0.0f, 0.0f, 0.0f );
        scale = new Point( tempo, 1.0f, 1.0f );

        oldProjection = batch.getProjectionMatrix();
        batch.setProjectionMatrix( cam.combined );
		// Create a table that fills the screen. Everything else will go inside this table.

        soundFrame = 0;
	}

	/**
	 * Run method happens while the game is running
	 */
	@Override
	public void run()
	{
		stopRunning = false;
		float delta = 0;
		timeReference = 0;
		lastTimeReference = 0;
		while(!stopRunning)
		{
			if(!toWait)
			{
				//where music output is
				timeReference = MusicHandler.outputTimeReference;
				delta = (float)(timeReference-lastTimeReference);
				pos.x = (float)timeReference-( player.p.x/scale.x );

				player.update( new Matrix4(), delta);

				//setRotation( levelAniQ.getPose( delta ) );
				//update based on object's modelview
				Matrix4 mv = new Matrix4();
				makeWorldView( mv );

				// move last index
				for(int k = lastStartIndex;k<actualObjects.size();k++)
				{
					Obstacle currentObj = actualObjects.get(k);
					if(currentObj.rightOfLeftSideOfScreen( (float) timeReference - .33333f ) )
					{
						break;
					}
					else
					{
						currentObj.done();
						lastStartIndex++;
					}
				}

				// update the obstacles that are onscreen
				for(int k = lastStartIndex;k<actualObjects.size();k++)
				{
					Obstacle currentObj = actualObjects.get(k);
					if( currentObj.leftOfRightSideOfScreen( (float) timeReference + 3.0f ) )
					{
						currentObj.update( mv, delta );
						if( currentObj.inScreenRange( (float)timeReference - .33333f, (float) timeReference + .33333f ) )
						{
							if( player.intersects( currentObj.hull ) )
							{
								currentObj.Impacted( tempo );
							}
						}
					}
					else
					{
						break;
					}
				}

				lastTimeReference += delta;
				/*
				if(toWait)
					myWait();
				*/
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("MY THREAD HAS STOPPED RUNNNNNNING!!!!");

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
		//save orginal matrix
		background.draw( batch );

		Matrix4 mv = batch.getTransformMatrix();
		Matrix4 before = new Matrix4( mv.cpy() );

		rotateWorldView( mv );

		Matrix4 beforeWV = new Matrix4( mv.cpy() );
		//make world view
		makeWorldView( mv );

		//set world view
		batch.setTransformMatrix( mv );
		//draw gameObjects

		for(int k = lastStartIndex;k<actualObjects.size();k++)
		{
			Obstacle currentObj = actualObjects.get(k);
			if( currentObj.leftOfRightSideOfScreen( (float) timeReference + 3.0f ) )
			{
				currentObj.draw( batch );
			}
			else
			{
				break;
			}
		}
		//reset to the original transform matrix
		batch.setTransformMatrix( beforeWV );

		player.draw( batch );

		batch.setTransformMatrix( before );


		leftBar.draw( batch );
		rightBar.draw( batch );

		batch.end();
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
			this.controls.loadPrefs();
			this.controls.defineControlsTable();
			System.out.println("unpause");
		}else
		if(currentThread instanceof Buffering)
		{
			Gdx.input.setInputProcessor(stage);
			System.out.println("NOTIFYING");
			this.myNotify();
		}else
		if(currentThread instanceof PreGame || currentThread instanceof FileChooserState)
		{
			System.out.println("SWITCHING AND TRING TO DO ");
			reset();
			Gdx.input.setInputProcessor(stage);
			try {
				actualObjects = LevelInterpreter.loadLevel();

				actualObjects = new ArrayList<Obstacle>();
			} catch (Exception e) {
				actualObjects = new ArrayList<Obstacle>();
				e.printStackTrace();
			}

			if(pick)
			{
				filename=FileChooserState.fileDialog.getFile();
			}
			//System.out.println(filename);

		    streamer = new MusicHandler(actualObjects);

			if(filename != null)
			{
				streamer.fileName=filename.getAbsolutePath();
			}
			if(!pick && test!=null)
			{
				streamer.fileName=test;
			}

			try {
				streamer.loadSound();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (FileFormatException e) {
				e.printStackTrace();
			}
			streamer.start();

			startThread();

		}
			//mysounddecoder = new WavDecoder(Gdx.files.internal("drop.wav"));
	}


	private void startThread()
	{
		Thread running = new Thread(this);
		Thread musicOutput = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while(!stopRunning)
				{
					if(streamer.bufferingNeeded())
					{
						goBuffer();
					}else
					{
						writeSound();
					}
						if(toWait)
						{
							myWait();
						}
					}
			}

		});
		running.start();
		musicOutput.start();
	}

	@Override
	public void addFrom(GameThread currentThread)
	{
	}

	@Override
	public void removeFrom(GameThread currentThread)
	{
		streamer.dispose();
		this.myNotify();//notifies to exit the thread
		stopRunning = true;
		System.out.println("BEING REMOVED");
		PumpAndJump.setThreadToNull(getThreadName());
	}

	/**
	 * Called after notify
	 */
	@Override
	public void unpause() {
		toWait = false;
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

	public void writeSound()
	{
		streamer.writeSound();
		songFinished = streamer.songFinished;
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
		//System.out.println("Jumping");
		player.jump();
	}

	/**
	 * Called when the player presses the duck button
	 */
	public void duck()
	{
		//System.out.println("Ducking");
		player.duck();
	}

	/**
	 * multiplies and sets the input matrix by the world pos, rotation, and scale
	 */
	private void makeWorldView( Matrix4 mv )
	{
		mv.translate( -pos.x*tempo, pos.y, pos.z );

		mv.scale( scale.x, scale.y, scale.z );
	}

	private void rotateWorldView(Matrix4 mv)
	{
		mv.rotate( 1.0f, 0.0f, 0.0f, rotation.x );
		mv.rotate( 0.0f, 1.0f, 0.0f, rotation.y );
		mv.rotate( 0.0f, 0.0f, 1.0f, rotation.z );
	}


	void setRotation( float[] f )
	{
		rotation.z = f[ 0 ];
	}

}
