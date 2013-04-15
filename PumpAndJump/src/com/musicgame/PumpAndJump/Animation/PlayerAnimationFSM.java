package com.musicgame.PumpAndJump.Animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class PlayerAnimationFSM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	Map< String, Animation > Animations;
	Map< String, ArrayList< Animation > > States;
	Map< String, ArrayList< String > > Relations;
	String currentAni;
	
	public PlayerAnimationFSM( String StatesFile, String FSMFile, String ani )
	{
		Animations = new HashMap< String, Animation >();
		States = new HashMap< String, ArrayList< Animation > >();
		Relations = new HashMap< String, ArrayList< String > >();
		LoadStates( StatesFile );
		LoadRelations( FSMFile );
		currentAni = ani;
		/*for( String s: States.keySet() )
		{
			System.out.println( s );
		}
		
		for( String s: Relations.keySet() )
		{
			System.out.print( s );
			for( String cs: Relations.get( s ) )
			{
				System.out.print( cs+" " );
			}
			System.out.println();
		}*/
	}
	
	public Animation getAni()
	{
		ArrayList< Animation > a = States.get( currentAni );
		int r = (int)( Math.random()*a.size() );
		Animation ani = a.get( r );
		
		ArrayList< String > c = Relations.get( currentAni );
		r = (int)( Math.random()*c.size() );
		currentAni = c.get( r );
		
		return ani;
	}
	
	public Animation startJumped()
	{
		ArrayList< String > c = Relations.get( currentAni );
		ArrayList< String > temp = new ArrayList< String >();
		
		for( String s: c )
		{
			if( s.startsWith( "sj" ) )
				temp.add( s );
		}
		
		int r = (int)( Math.random()*temp.size() );
		
		ArrayList< Animation > anis= States.get( r );
		
		r = (int)( Math.random()*anis.size() );
		
		currentAni = "ej";
		
		return anis.get( r );
	}
	
	public Animation startDucked()
	{
		ArrayList< String > c = Relations.get( currentAni );
		ArrayList< String > temp = new ArrayList< String >();
		
		for( String s: c )
		{
			if( s.contains( "sd" ) )
				temp.add( s );
		}
		
		int r = (int)( Math.random()*temp.size() );
		
		ArrayList< Animation > anis= States.get( r );
		
		r = (int)( Math.random()*anis.size() );
		
		currentAni = "ed";
		
		return anis.get( r );
	}
	
	void LoadStates( String StatesFile )
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
		
			//Animation ani = new Animation( animationFileName );
			Animation ani = null;
		
			Animations.put( state, ani );
			
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
	
	void LoadRelations( String FSMFile )
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
