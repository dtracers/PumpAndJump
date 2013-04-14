package com.musicgame.PumpAndJump.Animation;

public class Keyframe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public float[] pose;
	public float t;
	
	public Keyframe( float[] _pose, float _t ){ pose = _pose; t = _t; }
	
	public Keyframe copy( )
	{
		float[] p = new float[ pose.length ];
		for( int i = 0; i < p.length; i++ )
		{
			p[ i ] = pose[ i ];
		}
		return new Keyframe( p, t );
	}
	
	public void normalize()
	{
		for( int i = 0; i < pose.length; i++ )
		{
			pose[i] %= 360.0f;
		}
	}

}
