package com.musicgame.PumpAndJump.Animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class PlayerAnimationFSM extends FSM{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public PlayerAnimationFSM( String StatesFile, String FSMFile, String ani )
	{
		super( StatesFile, FSMFile, ani );
	}
	
	public synchronized Animation getAni( )
	{		
		ArrayList< String > stateChoices = Relations.get( nextAni );
		stateChoices = removeJumpsAndDucks( stateChoices );
		
		currentAni = nextAni;
		nextAni = getRandom( stateChoices );
		
		ArrayList< Animation > possibleAnimations = States.get( currentAni );
		Animation ani = getRandom( possibleAnimations );

		return ani;
	}
	
	public synchronized Animation startJump()
	{
		return startSpecial( 'j' );
	}

	public synchronized Animation startDuck()
	{
		return startSpecial( 'd' );
	}
	
	ArrayList< String > removeJumpsAndDucks( ArrayList< String > stateChoices )
	{
		ArrayList< String > stateChoicesWithoutJD = new ArrayList< String >();
		for( String s: stateChoices )
		{
			if( s.length() >= 2)
			{
				if( s.charAt(1) != 'j' && s.charAt(1) != 'd' )
				{
					stateChoicesWithoutJD.add( s );
				}
			}
			else
			{
				stateChoicesWithoutJD.add( s );
			}
					
		}
		
		return stateChoicesWithoutJD;
	}
	
	ArrayList< String > getSpecialStates( ArrayList< String > stateChoices, char c )
	{
		ArrayList< String > onlySpecialStates = new ArrayList< String >();
		
		for( String s:  stateChoices)
		{
			if( s.length() >= 2)
				if( s.charAt(1) == c )
					onlySpecialStates.add( s );
		}
		
		return onlySpecialStates;
	}
	
	Animation startSpecial( char c )
	{
		//get the possible next states from the current animation
		ArrayList< String > stateChoices = Relations.get( currentAni );
		//get only the possible special states from the current animation
		stateChoices = getSpecialStates( stateChoices, c );

		//Choose a random Animation from the possible animations
		ArrayList< Animation > possibleAnimations = States.get( getRandom( stateChoices ) );
	
		Animation ani = getRandom( possibleAnimations );

		currentAni = AnimationToName.get( ani );
		nextAni = AnimationToName.get( ani );
		getAni();

		return ani;
	}

}
