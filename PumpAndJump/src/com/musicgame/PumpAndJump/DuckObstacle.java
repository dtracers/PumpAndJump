package com.musicgame.PumpAndJump;

public class DuckObstacle extends Obstacle {

	public DuckObstacle( float Start, float End)
	{
		super( Start, End, 60.0f, 50.0f );
		image.setColor( 0.0f, 1.0f, 1.0f, 1.0f );
	}
}
