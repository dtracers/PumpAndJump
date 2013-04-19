package com.musicgame.PumpAndJump;

public class Beat extends Obstacle{
	public Beat(float startTime){
		super( startTime, startTime+.01f, 45.0f, 10.0f );
		image.setColor( 1.0f, 0.0f, 1.0f, 1.0f );
	}
	
}
