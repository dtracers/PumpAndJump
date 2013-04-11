package com.musicgame.PumpAndJump.game.gameStates;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.musicgame.PumpAndJump.GameObject;
import com.musicgame.PumpAndJump.LevelInterpreter;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;
import com.musicgame.musicCompiler.MusicCompiler;

public class RunningGame extends GameThread
{
	MusicCompiler streamer;
	//this is a list of the on screen objects
	//(by on screen it does include some that are partially off the screen too)
	//the objects are basically a queue added at the end and removed from the front
	ArrayList<GameObject> levelObjects = new ArrayList<GameObject>();
	//contains the list of all objects that are in the level
	ArrayList<GameObject> actualObjects = new ArrayList<GameObject>();
	long time;
	long frame;
	//the current frame that the sound player is at
	long soundFrame = 0;
	//the distance between the frame
	int bufferDistance = 1000;
	long sampleRate = 44100;
	long start = 0;
	boolean toWait = false;
	boolean jumping = false,ducking = false;
	boolean paused = false;
	public RunningGame()
	{
		time = 0;
	}

	public synchronized void resetTime()
	{
		time = 0;
	}

	public synchronized long getTimeLocation()
	{
		return time;
	}

	/**
	 * Run method happens while the game is running
	 */
	 @Override
	 public void run()
	 {
		 if(bufferingNeeded())
		 {
			 goBuffer();
		 }
		 time = System.currentTimeMillis();
		 start = System.currentTimeMillis();
		 while(true)
		 {
			 time = System.currentTimeMillis() - start;
			 frame = time/sampleRate;
			 if(toWait)
				 myWait();
			 try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
	 }

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

	@Override
	public void pause()
	{
		toWait = true;
	}

	@Override
	public void render(float delta)
	{
		for(int k = 0;k<levelObjects.size();k++)
		{
			levelObjects.get(k).draw((SpriteBatch)null);
		}
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		System.out.println(time);
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
		if(currentThread instanceof PauseGame && paused)
		{
			this.myNotify();
		}else
		if(currentThread instanceof Buffering)
		{
			this.myNotify();
		}else
		if(currentThread instanceof PreGame)
		{
			try {
				actualObjects = LevelInterpreter.loadLevel();
			} catch (Exception e) {
				actualObjects = new ArrayList<GameObject>();
				e.printStackTrace();
			}
			streamer = new MusicCompiler();
			streamer.loadSound();
			streamer.start();
			this.start();
		}
			//mysounddecoder = new WavDecoder(Gdx.files.internal("drop.wav"));
	}


	@Override
	public void addFrom(GameThread currentThread)
	{
	}

	@Override
	public void removeFrom(GameThread currentThread) {
	}

	/**
	 * Called after notify
	 */
	@Override
	public void unpause() {
		toWait = false;
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
		return streamer.currentFrame-soundFrame<bufferDistance;
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
}
