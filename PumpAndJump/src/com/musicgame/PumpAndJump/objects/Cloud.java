package com.musicgame.PumpAndJump.objects;

import com.badlogic.gdx.math.Matrix4;

public class Cloud extends Obstacle
{

	public Cloud(float Start, float End, float Y, float Height)
	{
		super(Start, End, Y, Height,"cloud.png");
	}

	//updates location
	public void update( Matrix4 m, float delta )
	{
		super.update( m, delta);
		//image.setColor(tint);
	}

}
