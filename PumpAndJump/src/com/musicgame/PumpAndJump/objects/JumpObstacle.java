package com.musicgame.PumpAndJump.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.musicgame.PumpAndJump.Util.TextureMapping;

public class JumpObstacle extends Obstacle
{

	public JumpObstacle( float Start, float End) {
		super( Start, End, 0.0f, 50.0f );
		image.setColor( 0.0f, 1.0f, 0.0f, 1.0f );
	}
}
