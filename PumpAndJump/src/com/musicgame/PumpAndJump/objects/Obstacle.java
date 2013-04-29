package com.musicgame.PumpAndJump.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.musicgame.PumpAndJump.Util.TextureMapping;

public class Obstacle extends GameObject
{
	private float start;
	private float end;
	private float width;
	private float height;

	public Obstacle(float Start, float End, float Y, float Height ){
		super( );
		start = Start;
		end = End;
		p.x = Start;
		p.y = Y;
		width = End - Start;
		height = Height;
		makeHull();
		image = new Sprite( TextureMapping.staticGet( "WhiteTemp.png" ) );
		image.setSize( End - Start, Height );
	}

	public Obstacle(float Start, float End, float Y, float Height ,String imageFile){
		super( );
		start = Start;
		end = End;
		p.x = Start;
		p.y = Y;
		width = End - Start;
		height = Height;
		makeHull();
		image = new Sprite( TextureMapping.staticGet( imageFile ) );
		image.setSize( End - Start, Height );
	}

	void makeHull()
	{
		float[] points = new float[ 8 ];
		points[ 0 ] = 0.0f; points[ 1 ] = 0.0f;
		points[ 2 ] = width; points[ 3 ] = 0.0f;
		points[ 4 ] = width; points[ 5 ] = height;
		points[ 6 ] = 0.0f; points[ 7 ] = height;

		poly = new Polygon( points );
	}

	public float getStartTime()
	{
		return start;
	}

	public float getEndTime()
	{
		return end;
	}

	/**
	 * If the start time of the object is off the screen to the right  (has not been on screen yet)
	 * or the end time is off the time to the left (has been on screen and just left)
	 * @param screenLeftTime
	 * @param screenRightTime
	 * @return
	 */
	public void done()
	{

	}

	public boolean leftOfRightSideOfScreen( float screenRightTime )
	{
		return start <= screenRightTime;
	}

	public boolean rightOfLeftSideOfScreen( float screenLeftTime )
	{
		return end >= screenLeftTime;
	}

	public boolean inScreenRange(float screenLeftTime,float screenRightTime)
	{
		return start<=screenRightTime&&end>=screenLeftTime;
	}

	public void Impacted( float tempo )
	{

	}
}
