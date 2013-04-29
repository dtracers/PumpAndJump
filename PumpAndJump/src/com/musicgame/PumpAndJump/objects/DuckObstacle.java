package com.musicgame.PumpAndJump.objects;

public class DuckObstacle extends Obstacle {

	public DuckObstacle( float Start, float End)
	{
		super( Start, End, 70.0f, 60.0f );
		image.setColor( 0.0f, 1.0f, 1.0f, 1.0f );
	}
}
