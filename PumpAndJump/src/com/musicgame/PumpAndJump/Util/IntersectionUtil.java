package com.musicgame.PumpAndJump.Util;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class IntersectionUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Testing algorithms
		float[] fpoints = { 0.0f, 0.0f, 2.0f, 0.0f, 1.0f, 1.0f, 2.0f, 2.0f, 0.0f, 2.0f };
		Vector2[] points = FloatToVector2( fpoints );
		
		Vector2[] ch= grahamScan( points );
		
		for( int i = 0; i < ch.length; i++ )
		{
			System.out.println( ch[i].x + "," + ch[ i ].y );
		}
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
		
		Vector2[] copy = new Vector2[ (orig.length>>1) ];
		
		for( int i = 0; i < copy.length; i++ )
		{
			copy[i] = new Vector2( orig[2*i], orig[2*i+1] );
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
	//Returns the Vector2 with the lowest value of y
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
	
	//Helper class to sort the points rotationally
	//Class assumes min is found with findVector2WithMinY and is correct
	//Allows me to use java's sort and is faster because the angles are not recomputed
	private static class rotationalPointValue implements Comparable< rotationalPointValue >
	{
		public Vector2 point;
		public float angle;
		
		public rotationalPointValue( Vector2 point, Vector2 min )
		{
			this.point = point;
			
			float xDist = point.x - min.x;
			float yDist = point.y - min.y;
			
			if( xDist == 0 && yDist == 0 )
				angle = -1;
			else
			{
				//Compute the hypotenuse of the triangle that was created
				float h = (float)Math.sqrt( xDist*xDist + yDist*yDist );
				//Find the adjacent edge to the angle which is to be found
				float a = xDist;
				//Compute angle using inverse cosine 
				angle = (float)Math.acos( a/h );
			}
		}
		
		public int compareTo( rotationalPointValue point ){
	       return Float.compare( angle, point.angle );
	    }
		
		public String toString()
		{
			return angle+" ";
		}
	}
	
	//sorts in the Vector2 array points
	//assumes min is found with findVector2WithMinY and is correct
	private static void sortPointsRotationallyAroundMin( Vector2[] points, Vector2 min )
	{
		rotationalPointValue[] rotPoints = new rotationalPointValue[ points.length ];
		for( int i = 0; i < rotPoints.length; i++ )
		{
			rotPoints[i] = new rotationalPointValue( points[i], min );
		}
		
		
		
		Arrays.sort( rotPoints );
		
		for( int i = 0; i < points.length; i++ )
		{
			points[i] = rotPoints[i].point;
		}
	}
	
	//Checks to see if the angle between 3 points is counter clockwise
	private static boolean isClockwise( Vector2 p1, Vector2 p2, Vector2 p3)
	{
	    return ( ( ( p2.x - p1.x )*( p3.y - p1.y ) - ( p2.y - p1.y )*( p3.x - p1.x ) ) <= 0 );
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

		
		sortPointsRotationallyAroundMin( points, min );
		inConvexHull.add( min );
		inConvexHull.add( points[1] );
		
		//actual algorithm
		for( int i = 2; i < points.length; i++ )
		{
			while ( isClockwise( inConvexHull.get( inConvexHull.size() - 2 ), inConvexHull.get( inConvexHull.size() - 1 ), points[i] ) )
			{
				if( inConvexHull.size() > 1 )
				{
					inConvexHull.remove( inConvexHull.size() - 1 );
				}
			}   
			
			inConvexHull.add( points[i] );  
		}
		
		/*while ( isClockwise( inConvexHull.get( inConvexHull.size() - 3 ), inConvexHull.get( inConvexHull.size() - 2 ), inConvexHull.get( inConvexHull.size() - 1 ) ) )
		{
			if( inConvexHull.size() > 1 )
			{
				inConvexHull.remove( inConvexHull.size() - 1 );
			}
		}  */
		
		Vector2[] ret = new Vector2[ inConvexHull.size() ];
		for( int i = 0; i < ret.length; i++ )
		{
			ret[i] = inConvexHull.get( i );
		}
		
		return ret;
	}
	
	/*-------------------------------------------------------------------------------------
	 * 		End all Graham Scan methods
	 *-----------------------------------------------------------------------------------*/

}
