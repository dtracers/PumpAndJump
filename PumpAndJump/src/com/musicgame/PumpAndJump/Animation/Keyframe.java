package com.musicgame.PumpAndJump.Animation;

public class Keyframe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public float[] pose;
	public int index;
	public float t;
	
	public Keyframe( float[] _pose, float _t, int _index ){ pose = _pose; t = _t; index = _index; }
	
	public Keyframe copy( )
	{
		float[] p = new float[ pose.length ];
		for( int i = 0; i < p.length; i++ )
		{
			p[ i ] = pose[ i ];
		}
		return new Keyframe( p, t, index );
	}
	
	public void normalize( Keyframe kf )
	{
		for( int i = 0; i < pose.length; i++ )
		{
			if( i != 15)
			{
				float posValue;
				float negValue;
				if( pose[i] >= 0.0f )
				{
					posValue = pose[i]%360.0f;
					negValue = pose[i]%360.0f - 360.0f;
				}
				else
				{
					posValue = pose[i]%360.0f + 360.0f;
					negValue = pose[i]%360.0f;
				}
				
				if( Math.abs( posValue - kf.pose[i] ) <=  Math.abs( negValue - kf.pose[i] ) )
				{
					pose[i] = posValue;
				}
				else
				{
					pose[i] = negValue;
				}
			}
		}
	}
	
	public void print()
	{
		System.out.print( "Index:"+index+"\t" );
		System.out.print( "Time:"+t+"\t" );
		for( int i = 0; i < pose.length; i++ )
		{
			System.out.print( pose[i]+"," );
		}
		System.out.println();
	}
	
	public float dist( Keyframe kf )
	{
		float sum = 0.0f;
		for( int i = 0; i < pose.length; i++ )
		{
			sum += Math.abs( pose[i] - kf.pose[i] )/( 360.0f );
		}
		return sum/((float)pose.length);
	}

}
