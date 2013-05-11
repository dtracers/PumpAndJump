package com.musicgame.PumpAndJump.objects;


public class JumpObstacle extends Obstacle
{
	boolean triggered = false;
	public JumpObstacle( float Start, float End) {
		super( Start, End, 0.0f, 50.0f );
		image.setColor( 0.0f, 1.0f, 0.0f, 1.0f );
	}

	public void Impacted( float tempo )
	{
		if( !triggered )
		{
		//	triggered = true;
		//	RunningGame.score--;
		}
	}
}
