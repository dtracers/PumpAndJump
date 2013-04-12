package com.musicgame.PumpAndJump.Animation;

import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;

/**
 * A blank animation template that has animation methods for the players
 * Different animations will extends this class
 * @author gigemjt
 */
public class Animation {
	
	public ArrayList< Keyframe > keyframes;
	public int dof = 16;
	
	public Animation( String fileName )
	{
		Scanner s = new Scanner( Gdx.files.internal( fileName ).reader() );

		keyframes = new ArrayList< Keyframe >();
		
		int type;
		type = s.nextInt();
		
		if( type == -1 )
		{

			int size = s.nextInt();
	
			float origY = s.nextFloat();
	
			float[] pose = new float[ dof ];
	
			float t = 0.0f;
	
			for( int i = 0; i < size; i++ )
			{
				for( int j = 0; j < dof; j++ )
				{
					pose[j] = s.nextFloat();
				}
				t = s.nextFloat();
				keyframes.add( new Keyframe( pose, t ) );
			}
	
			s.close();
		}
	}
	
}
