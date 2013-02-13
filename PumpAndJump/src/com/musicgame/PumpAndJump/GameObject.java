package com.musicgame.PumpAndJump;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector;

public abstract class GameObject
{
	Polygon hull;
	
	Polygon getHull()
	{
		return hull;
	}
	
	boolean intersects( Polygon poly )
	{
		return Intersector.overlapConvexPolygons( hull, poly );
	}
	
	void draw( ShapeRenderer sr )
	{
		 sr.begin( ShapeRenderer.ShapeType.Line );
		 float v[] = hull.getVertices();
		 if( v.length > 1 )
		 {
			 float lastX = -1;
			 float lastY = -1;
			 boolean firstRound = true;
			 for( int i = 0; i < v.length/2; i++ )
			 {
				 float newX = v[i*2];
				 float newY = v[i*2+1];
				 
				 if( !firstRound )
				 {
					 sr.line( lastX, lastY, newX, newY );
				 }
				 
				 lastX = newX;
				 lastY = newY;
			 }
			 sr.line( lastX, lastY, v[0], v[1] );
		 }
		 sr.end();
	}
}
