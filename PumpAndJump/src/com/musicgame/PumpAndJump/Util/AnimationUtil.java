package com.musicgame.PumpAndJump.Util;

import java.io.PrintStream;

public class AnimationUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static float fabs( float f )
	{
		if( f < 0 )
			return -f;
		return f;
	}

	public class Point
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

}
