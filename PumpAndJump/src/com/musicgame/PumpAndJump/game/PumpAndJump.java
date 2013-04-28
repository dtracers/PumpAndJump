package com.musicgame.PumpAndJump.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.musicgame.PumpAndJump.Util.TextureMapping;
import com.musicgame.PumpAndJump.game.gameStates.AboutGame;
import com.musicgame.PumpAndJump.game.gameStates.Buffering;
import com.musicgame.PumpAndJump.game.gameStates.DemoGame;
import com.musicgame.PumpAndJump.game.gameStates.FileChooserState;
import com.musicgame.PumpAndJump.game.gameStates.InstructionGame;
import com.musicgame.PumpAndJump.game.gameStates.OptionsGame;
import com.musicgame.PumpAndJump.game.gameStates.PauseGame;
import com.musicgame.PumpAndJump.game.gameStates.PostGame;
import com.musicgame.PumpAndJump.game.gameStates.PreGame;
import com.musicgame.PumpAndJump.game.gameStates.RunningGame;
import com.musicgame.PumpAndJump.game.sound.MP3Decoder;

public class PumpAndJump extends Game
{
	public static MP3Decoder MP3decoder;
	GameScreen gameScreen;
	InputProcessor input;

	static Array<GameThread> runningThreads;

	@Override
	public void create()
	{
	//	instance = this;
		runningThreads = new Array<GameThread>();

		TextureMapping.constructStaticMapping();

		gameScreen = new GameScreen();
		this.setScreen(gameScreen);

		input = new GameInput();
		Gdx.input.setInputProcessor(input);

		initialize();

		switchThread(ThreadName.PreGame,null);

	}

	private static void initialize()
	{
	//	preGameThread = new PreGame();
	//	postGameThread = new PostGame();
	//	runningGameThread = new RunningGame();
	//	pauseGameThread = new PauseGame();
	//	demoGameThread = new DemoGame();
	//	aboutGameThread = new AboutGame();
	//	inctructionsGameThread = new InstructionGame();
	//	optionsGameThread = new OptionsGame();
		bufferingThread = new Buffering();
	//	fileChooserThread=new FileChooserState();
	}

	/**
	 * Ends the previous thread and switches to the given thread
	 *
	 * PreGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * Pause
	 * @param switchTo
	 */
	public static void switchThread(ThreadName switchTo,GameThread currentThread)
	{
		GameThread temp = getThread(switchTo);

		temp.updateSelf();
		temp.switchFrom(currentThread);
		clearThreads(currentThread);
		runningThreads.add(temp);

	}

	private static void clearThreads(GameThread currentThread)
	{
		for(GameThread running:runningThreads)
		{
			running.removeFrom(currentThread);
		}
		runningThreads.clear();
		runningThreads = new Array<GameThread>();
	}

	/**
	 * add the thread to the existing set of threads
	 *
	 * PreGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * Pause
	 * @param switchTo
	 */
	public static void addThread(ThreadName switchTo,GameThread currentThread)
	{
		GameThread temp = getThread(switchTo);
		temp.updateSelf();
		temp.addFrom(currentThread);
		runningThreads.add(temp);
	}

	/**
	 * remove the thread from the existing set of threads
	 *
	 * PreGame
	 * PostGame
	 * RunningGame
	 * PauseGame
	 *
	 * Pause
	 * @param switchTo
	 */
	public static void removeThread(ThreadName switchTo, GameThread currentThread)
	{
		System.out.println("num threads before: ");
		System.out.println(runningThreads.size);
		GameThread temp = getThread(switchTo);
		temp.removeFrom(currentThread);

		runningThreads.removeValue(temp, true);
		if(runningThreads.size>=1)
		{
			GameThread newTopThread = runningThreads.get(runningThreads.size-1);
			Gdx.input.setInputProcessor(newTopThread);
			newTopThread.switchFrom(temp);
			System.out.println("num threads now: ");
			System.out.println(runningThreads.size);
		}
	}

	/**
	 * sets the current thread to null for garbage collecting
	 * @param switchTo
	 * @return
	 */

	public static void setThreadToNull(ThreadName switchTo)
	{
		switch(switchTo)
		{
			case  PreGame:		preGameThread = null;break;
			case  PostGame:		postGameThread = null;break;
			case  RunningGame:	runningGameThread = null;break;
			case  PauseGame:	pauseGameThread = null;break;
			case  DemoGame:		demoGameThread = null;break;
			case  AboutGame:		aboutGameThread = null;break;
			case  OptionsGame:		optionsGameThread = null;break;
			case  InstructionGame:		inctructionsGameThread = null;break;
			case  Buffering:		bufferingThread = null;break;
			case FileChooser:		fileChooserThread = null;break;
		}
	}

	/**
	 * gets the thread and creates it if the thread happens to be null
	 * @param switchTo
	 * @return
	 */
	private static GameThread getThread(ThreadName switchTo)
	{
		switch(switchTo)
		{
			case  PreGame:
			{
				if(preGameThread == null)
				{
					preGameThread = new PreGame();
				}
				return preGameThread;
			}
			case  PostGame:
			{
				if(postGameThread == null)
				{
					postGameThread = new PostGame();
				}
				return postGameThread;
			}
			case  RunningGame:
			{
				if(runningGameThread == null)
				{
					runningGameThread = new RunningGame();
				}
				return runningGameThread;
			}
			case  PauseGame:
			{
				if(pauseGameThread == null)
				{
					pauseGameThread = new PauseGame();
				}
				return pauseGameThread;
			}
			case  DemoGame:
			{
				if(demoGameThread == null)
				{
					demoGameThread = new DemoGame();
				}
				return demoGameThread;
			}
			case  AboutGame:
			{
				if(aboutGameThread == null)
				{
					aboutGameThread = new AboutGame();
				}
				return aboutGameThread;
			}
			case  OptionsGame:
			{
				if(optionsGameThread == null)
				{
					optionsGameThread = new OptionsGame();
				}
				return optionsGameThread;
			}
			case  InstructionGame:
			{
				if(inctructionsGameThread == null)
				{
					inctructionsGameThread = new InstructionGame();
				}
				return inctructionsGameThread;
			}
			case  Buffering:
			{
				if(bufferingThread == null)
				{
					bufferingThread = new Buffering();
				}
				return bufferingThread;
			}
			case FileChooser:
			{
				if(fileChooserThread == null)
				{
					fileChooserThread = new FileChooserState();
				}
				return fileChooserThread;
			}
		}
		return null;
	}

	public void dispose()
	{
		for(ThreadName name: ThreadName.values())
		{
			if(getThread(name)!=null)
				getThread(name).dispose();
		}
	}

	//the game threads are at the bottom
	private static PreGame preGameThread;
	private static PostGame postGameThread;
	private static RunningGame runningGameThread;
	private static PauseGame pauseGameThread;
	private static DemoGame demoGameThread;
	private static AboutGame aboutGameThread;
	private static OptionsGame optionsGameThread;
	private static InstructionGame inctructionsGameThread;
	private static Buffering bufferingThread;
	private static FileChooserState fileChooserThread;

}