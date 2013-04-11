#include <iostream>
#include <cmath>

#ifndef _WAMS_POINT_H
#define _WAMS_POINT_H

namespace WAMS
{
	using namespace std;
	
	float fabs( float f )
	{
		if( f < 0 )
			return -f;
		return f;
	}

	class point
	{
		public:
			float x, y, z;
			point()
			{
				x = 0.0f;
				y = 0.0f;
				z = 0.0f;
			}
			point( float _x, float  _y,  float _z )
			{
				x = _x;
				y = _y;
				z = _z;
			}
			point( const point& p )
			{
				x = p.x;
				y = p.y;
				z = p.z;
			}

			point add( const point& p ) const 
			{
				return point( x+p.x, y+p.y, z+p.z );
			}

			point add( float a ) const 
			{
				return point( x+a, y+a, z+a );
			}

			point subtract( const point& p ) const 
			{
				return point( x-p.x, y-p.y, z-p.z );
			}

			point subtract( float s ) const 
			{
				return point( x-s, y-s, z-s );
			}

			point scale( float s ) const 
			{
				return point( x*s, y*s, z*s );
			}

			point scale( point p )
			{
				return point( x*p.x, y*p.y, z*p.z );
			}

			float dot( const point& p ) const 
			{
				return x*p.x+y*p.y+z*p.z;
			}

			point cross( const point& p ) const 
			{
				return point( y*p.z - p.y*z, x*p.z - p.x*z, x*p.y - p.x*y );
			}

			float distance() const 
			{
				float d = x*x;
				d += y*y;
				d += z*z;

				return sqrt( d );
			}

			float distance( const point& p ) const 
			{
				point vec = subtract( p );

				return vec.distance();
			}

			float squareDistance() const 
			{
				float d = x*x;
				d += y*y;
				d += z*z;

				return d;
			}

			float squareDistance( const point& p ) const 
			{
				point vec = subtract( p );

				return vec.squareDistance();
			}

			void normalize()
			{
				float d = distance();
				x =  x / d;
				y = y / d;
				z = z / d;
			}

			void abs()
			{
				x = fabs( x );
				y = fabs( y );
				z = fabs( z );
			}

			void print() const
			{
				cout<<"("<<x<<","<<y<<","<<z<<")\n";
			}
	};

}

#endif