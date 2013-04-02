package com.musicgame.PumpAndJump.Util;

import java.util.ArrayList;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import org.junit.Assert.*;

public class IntersectionUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Testing algorithms
	}
	
	/*--------------------------------------------------------------------------------
	 * 	GrahamScan Helper Methods( They should also have other uses in the code )
	 *--------------------------------------------------------------------------------*/
	
	//Creates a new float array from a Vector2 array
	//Will runtime error if orig is null
	public static float[] Vector2ToFloat( Vector2[] orig )
	{
		float[] copy = new float[ orig.length*2 ];
		
		for( int i = 0; i < orig.length ; i++ )
		{
			copy[2*i] = orig[i].x;
			copy[2*i+1] = orig[i].y;
		}
		
		return copy;
	}
	
	//Creates a new Vector2 array from a float array
	//Will return null if orig is not a multiple of 2 ( as it could not be representing 2d points )
	//Will runtime error if orig is null
	public static Vector2[] FloatToVector2( float[] orig )
	{
		if( ( orig.length & 0x01 ) != 0 )
			return null;
		
		Vector2[] copy = new Vector2[ orig.length>>1 ];
		
		for( int i = 0; i < copy.length; i++ )
		{
			copy[i].x = orig[2*i];
			copy[i].y = orig[2*i+1];
		}
		
		return copy;
	}
	
	//Creates a convex hull from a polygon p
	//Will runtime error if p is null
	public static float[] createConvexHull( Polygon p )
	{
		Vector2[] points = FloatToVector2( p.getVertices() );
		
		Vector2[] convexHullPoints = grahamScan( points );
		
		float[] convexHullFloats = Vector2ToFloat( convexHullPoints );
		
		return convexHullFloats;
	}
	/*-------------------------------------------------------------------------------------
	 * 		Less Useful Helpers
	 *-----------------------------------------------------------------------------------*/
	
	private static Vector2 findVector2WithMinY( Vector2[] points )
	{
		Vector2 min = points[0];
		for( int i = 1; i < points.length; i++ )
		{
			if( min.y > points[i].y )
				min = points[i];
		}
		return min;
	}
	
	/*-------------------------------------------------------------------------------------
	 * 		Graham Scan
	 *-----------------------------------------------------------------------------------*/
	
	//Returns a Vector2 array consisting of the points in the input Vector2 array which complete
	//		the convex hull constructed by the points
	//Will return null if there aren't enough points to construct a polygon in the first place
	//Will runtime error if points is null
	public static Vector2[] grahamScan( Vector2[] points )
	{
		if( !(points.length > 2) )
			return null;
		
		Vector2 min = findVector2WithMinY( points );
		
		ArrayList< Vector2 > inConvexHull = new ArrayList< Vector2 >();
		inConvexHull.add( min );
		
		
		
		
		return ( ( Vector2[] ) inConvexHull.toArray() );
	}
	
	/*-------------------------------------------------------------------------------------
	 * 		End all Graham Scan methods
	 *-----------------------------------------------------------------------------------*/

}
