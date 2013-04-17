package com.musicgame.PumpAndJump.Animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FSM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	// static map from States file name to an FSM
	static Map< String, FSM > StatesFileToFSM = new TreeMap<String, FSM>();
	// static map from Relations file name to an FSM
	static Map< String, FSM > RelationsFileToFSM = new TreeMap<String, FSM>();
	
	//**Shared** with any other FSM with the same StatesFile
	Map< String, Animation > NameToAnimation;
	Map< Animation, String > AnimationToName;
	
	//States map
	//**Shared** with any other FSM with the same StatesFile
	Map< String, ArrayList< Animation > > States;
	
	//Relation between States expressed as Strings
	//**Shared** with any other FSM with the same RelationsFile
	Map< String, ArrayList< String > > Relations;
	
	//**Not Shared** Animation currently being used 
	String currentAni;
	//**Not Shared** Next animation to be used
	String nextAni;
	
	public FSM( String StatesFile, String FSMFile, String ani )
	{

		NameToAnimation = new HashMap< String, Animation >();
		AnimationToName = new HashMap< Animation, String >();
		States = new HashMap< String, ArrayList< Animation > >();
		Relations = new HashMap< String, ArrayList< String > >();
		LoadStates( StatesFile );
		LoadRelations( FSMFile );
		currentAni = ani;
		nextAni = ani;
	}
	
	//copies references to another FSMs maps
	//def not thread safe to write to any of the maps in this FSM
	public FSM( FSM otherFSM, String ani )
	{
		NameToAnimation = otherFSM.NameToAnimation;
		AnimationToName = otherFSM.AnimationToName;
		States = otherFSM.States;
		Relations = otherFSM.Relations;
		currentAni = ani;
		nextAni = ani;
	}
	
	//Returns an Animation from the corresponding state expressed by the String nextAni
	public synchronized Animation getAni( )
	{
		ArrayList< Animation > possibleAnimations = States.get( nextAni );
	
		Animation ani = getRandom( possibleAnimations );

		ArrayList< String > stateChoices = Relations.get( nextAni );
		
		currentAni = nextAni;
		nextAni = getRandom( stateChoices );

		return ani;
	}
	
	
	//Returns a Random element from an ArrayList
	<T> T getRandom( ArrayList< T > list )
	{
		if( list.size() == 0 )
			return null;
		int r = (int)( Math.random()*list.size() );
		return list.get( r );
	}
	
	//Decides whether to read from the states file or to use another FSM's state based maps
	void LoadStates( String StatesFile )
	{
		StatesFile = StatesFile.toLowerCase();
		FSM otherFSM = StatesFileToFSM.get( StatesFile );
		if( otherFSM != null )
		{
			NameToAnimation = otherFSM.NameToAnimation;
			AnimationToName = otherFSM.AnimationToName;
			States = otherFSM.States;
		}
		else
		{
			StatesFileToFSM.put( StatesFile, this );
			ReadStates( StatesFile );
		}
	}
	
	//Reads the State file 
	void ReadStates( String StatesFile )
	{
		FileHandle dir =  Gdx.files.internal( StatesFile );
		Scanner s = new Scanner( dir.reader() );

		int size;
		size = s.nextInt();

		s.nextLine();

		for( int i = 0; i < size; i++ )
		{
			String state;
			state = s.next();

			String animationFileName;
			animationFileName = s.next();

			Animation ani = new Animation( animationFileName );

			NameToAnimation.put( state, ani );
			AnimationToName.put( ani, state );

			ArrayList< Animation > aniArray = new ArrayList< Animation >();
			aniArray.add( ani );

			States.put( state, aniArray );
		}

		size = s.nextInt();
		s.nextLine();
		for( int i = 0; i < size; i++ )
		{
			String state;
			state = s.next();

			int stateCount = s.nextInt();

			ArrayList< Animation > aniArray = new ArrayList< Animation >();

			for( int j = 0; j < stateCount; j++ )
			{
				String childState;
				childState = s.next();

				ArrayList< Animation > childAniArray = States.get( childState );

				for( Animation ani: childAniArray )
				{
					aniArray.add( ani );
				}
			}

			States.put( state, aniArray );
		}

		s.close();
	}

	//Decides whether to read from the Relations file or to use another FSM's Relation based maps
	void LoadRelations( String FSMFile )
	{
		FSMFile = FSMFile.toLowerCase();
		FSM otherFSM = RelationsFileToFSM.get( FSMFile );
		if( otherFSM != null )
		{
			Relations = otherFSM.Relations;
		}
		else
		{
			RelationsFileToFSM.put( FSMFile, this );
			ReadRelations( FSMFile );
		}
	}
	
	//Reads the Relations file 
	void ReadRelations( String FSMFile )
	{
		FileHandle dir =  Gdx.files.internal( FSMFile );
		Scanner s = new Scanner( dir.reader() );

		int size;
		size = s.nextInt();

		s.nextLine();
		for( int i = 0; i < size; i++ )
		{
			String state;
			state = s.next();

			int stateCount = s.nextInt();

			ArrayList< String > childStates = new ArrayList< String >();

			for( int j = 0; j < stateCount; j++ )
			{
				String childState;
				childState = s.next();

				childStates.add( childState );
			}

			Relations.put( state, childStates );
		}

		s.close();
	}

}
