package com.musicgame.PumpAndJump.Util;

import java.io.PrintStream;

public class AnimationUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	//abs of a float
	public static float fabs( float f )
	{
		if( f < 0 )
			return -f;
		return f;
	}
	
	//Linear interpolation, t must be between 0 and 1( inclusive ).
	public static float lerp( float t, float p1, float p2 )
	{
		return p1+(p2-p1)*t;
	}
	
	//A Cubic Hermite spline, t is between p1's time and p2's time , t is  between 0 and 1
	public static float catmullrom( float t, float p0, float p1, float p2, float p3 )
	{
		return ( 0.5f * (
            (2 * p1 )  +
            ( (-p0 + p2) * t ) +
            ( (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t ) +
            ( (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t ) ) );
	}

	public static class Point
	{
		public float x, y, z;
		public Point()
		{
			x = 0.0f;
			y = 0.0f;
			z = 0.0f;
		}
		public Point( float _x, float  _y,  float _z )
		{
			x = _x;
			y = _y;
			z = _z;
		}
		public Point( Point p )
		{
			x = p.x;
			y = p.y;
			z = p.z;
		}

		public Point add( Point p ) 
		{
			return new Point( x+p.x, y+p.y, z+p.z );
		}

		public Point add( float a )
		{
			return new Point( x+a, y+a, z+a );
		}

		public Point subtract( Point p )
		{
			return new Point( x-p.x, y-p.y, z-p.z );
		}

		public Point subtract( float s ) 
		{
			return new Point( x-s, y-s, z-s );
		}

		public Point scale( float s )
		{
			return new Point( x*s, y*s, z*s );
		}

		public Point scale( Point p )
		{
			return new Point( x*p.x, y*p.y, z*p.z );
		}

		public float dot( Point p ) 
		{
			return x*p.x+y*p.y+z*p.z;
		}

		public Point cross( Point p )
		{
			return new Point( y*p.z - p.y*z, x*p.z - p.x*z, x*p.y - p.x*y );
		}

		public float distance()
		{
			float d = x*x;
			d += y*y;
			d += z*z;

			return (float) Math.sqrt( d );
		}

		public float distance( Point p )  
		{
			Point vec = subtract( p );

			return vec.distance();
		}

		public float squareDistance()
		{
			float d = x*x;
			d += y*y;
			d += z*z;

			return d;
		}

		public float squareDistance( Point p )
		{
			Point vec = subtract( p );

			return vec.squareDistance();
		}

		public void normalize()
		{
			float d = distance();
			x =  x / d;
			y = y / d;
			z = z / d;
		}

		public void abs()
		{
			x = fabs( x );
			y = fabs( y );
			z = fabs( z );
		}

		public void print( PrintStream os )
		{
			os.print("("+x+","+y+","+z+")\n");
		}
	}
	
	public static Point RotateAroundXAxis( Point p, float angle )
	{
		angle = (float) (angle*Math.PI/180.0f);
		float x1 = p.x;
		float y1 = (float) (p.y*Math.cos( angle ) - p.z*Math.sin( angle ));
		float z1 = (float) (p.y*Math.sin( angle ) + p.z*Math.cos( angle ));
		return new Point( x1, y1, z1 );
	}

	public static Point RotateAroundYAxis( Point p, float angle )
	{
		angle = (float) (angle*Math.PI/180.0f);
		float x1 = (float) (p.x*Math.cos( angle ) + p.z*Math.sin( angle ));
		float y1 = p.y;
		float z1 = (float) (-p.x*Math.sin( angle ) + p.z*Math.cos( angle ));
		return new Point( x1, y1, z1 );
	}

	public static Point RotateAroundZAxis( Point p, float angle )
	{
		angle = (float) (angle*Math.PI/180.0f);
		float x1 = (float) (p.x*Math.cos( angle ) - p.y*Math.sin( angle ));
		float y1 = (float) (p.x*Math.sin( angle ) + p.y*Math.cos( angle ));
		float z1 = p.z;
		return new Point( x1, y1, z1 );
	}

}
