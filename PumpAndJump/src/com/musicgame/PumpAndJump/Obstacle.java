package com.musicgame.PumpAndJump;

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
}
