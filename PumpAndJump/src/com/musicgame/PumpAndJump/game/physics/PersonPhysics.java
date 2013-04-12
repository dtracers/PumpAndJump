package com.musicgame.PumpAndJump.game.physics;

import com.musicgame.PumpAndJump.Player;
import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;

/**
 * This class will hold the person jumping and and ducking
 * it will basically control the Y position of the player
 * @author gigemjt
 *
 */
public class PersonPhysics
{
	public static final float JUMP_VELOCITY = 2;
	public static final float GRAVITY = 1.2f;
	float velocity = 0;
	float y = 0;
	float floorY = 0;
	//sets the lower Y position it
	boolean jumping = false;
	boolean overAir = false;
	boolean ducking = false;

	Point personLoc;
	Player player;

	public void update(float delta)
	{
		velocity -=GRAVITY*delta;
		y = y+velocity*delta;//simple equation for computer location over time
		if(y<=floorY&&jumping&&!overAir)
		{
			velocity = 0;
			jumping = false;
		}
	}

	/**
	 * When this is called it will only jump once
	 */
	public void jump()
	{
		if(!jumping)
		{
			velocity += JUMP_VELOCITY;
			jumping = true;
		}

	}
	/**
	 * When this is called it will only duck once
	 */
	public void duck()
	{
		ducking = true;
	}

	public void hitGround()
	{

	}

	/**
	 *change the Y based off of a difference given (which is either positive or negative
	 */
	public void changeFloorY(float dY)
	{
		floorY += dY;
	}
}
