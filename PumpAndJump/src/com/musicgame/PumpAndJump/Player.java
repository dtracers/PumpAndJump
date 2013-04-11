package com.musicgame.PumpAndJump;

import com.musicgame.PumpAndJump.Animation.Animated;
import com.badlogic.gdx.math.Polygon;

public class Player extends GameObject implements Animated{
	public Player(Polygon hull)
	{
		super(hull);
	}

	@Override
	public void UpdatePose(float pose) {
		// TODO Auto-generated method stub
		
	}
}
