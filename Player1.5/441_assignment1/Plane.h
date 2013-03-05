#include "point.h"
#include "Intersectable.h"
#include "Ray.h"

#ifndef _WAMS_PLANE_H
#define _WAMS_PLANE_H

namespace WAMS 
{
	class Plane : Intersectable
	{
		public:
			point p;
			point v1;
			point v2;
			float ambient, diffuse, specular;
			int specPow;
			float reflect;
			point color1;
			point color2;

			Plane( point _p_, point _v1_, point _v2_ ) : p( _p_ ), v1( _v1_ ), v2( _v2_ ), color1( 0.8f, 0.2f, 0.2f ), color2( 0.2f, 0.2f, 0.8f )
			{
				ambient = 0.15f, diffuse = 0.9f, specular = 0.15f;
				specPow = 50;
				reflect = 0.1f;
				v1.normalize();
				v2.normalize();
			}

			Plane( Plane& _p_ ) : p( _p_.p ), v1( _p_.v1 ), v2( _p_.v2 ), color1( _p_.color1 ), color2( _p_.color2 )
			{
				ambient = _p_.ambient, diffuse = _p_.diffuse, specular = _p_.specular;
				specPow = _p_.specPow;
				reflect = _p_.reflect;
			}

			float intersects( const Ray& r ) const
			{
				point n = v1.cross( v2 );

				n.normalize();

				float d = p.dot( n );

				float p0DotN = r.p.dot( n );

				float vDotN = r.dir.dot( n );

				float t = - ( p0DotN - d ) / vDotN;

				return t;
			}

			point getColor( point& p )
			{
				int cx = max( abs( v1.x ), abs( v2.x ) );
				int cy = max( abs( v1.y ), abs( v2.y ) );
				int cz = max( abs( v1.z ), abs( v2.z ) );

				int colorID = 0;

				colorID += ( ( ( p.x < 0 ) ^ ( p.y < 0 ) ^ ( p.z < 0 ) ) & 1 );

				if( cx >= cz && cy >= cz )
				{
					colorID += ( (int)( p.x / cx ) ) % 2;
					colorID += ( (int)( p.y / cy ) ) % 2;
				}
				else if( cx >= cy && cz >= cy )
				{
					colorID += ( (int)( p.x / cx ) ) % 2;
					colorID += ( (int)( p.z / cz ) ) % 2;
				}
				else if( cy >= cx && cz >= cx )
				{
					colorID += ( (int)( p.y / cy ) ) % 2;
					colorID += ( (int)( p.z / cz ) ) % 2;
				}

				if( colorID % 2 == 0 )
					return color1;
				else
					return color2;

			}
	};
}

#endif