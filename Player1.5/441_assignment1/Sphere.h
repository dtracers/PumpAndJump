#include "point.h"
#include "Intersectable.h"
#include "Ray.h"

#ifndef _WAMS_SPHERE_H
#define _WAMS_SPHERE_H

namespace WAMS 
{
	class Sphere : Intersectable
	{
		public:
			point p;
			float radius;
			float ambient, diffuse, specular;
			int specPow;
			float reflect;
			point color;

			Sphere( point _p_, float _radius_ ) : p( _p_ ), radius( _radius_ ), color( 1.0f, 1.0f, 1.0f ) 
			{
				ambient = 0.33f, diffuse = 0.33f, specular = 0.33f;
				specPow = 5;
				reflect = 0.3f;
			}

			Sphere( point _p_, float _radius_, point _color_ ) : p( _p_ ), radius( _radius_ ), color( _color_ ) 
			{
				ambient = 0.33f, diffuse = 0.33f, specular = 0.33f;
				specPow = 5;
				reflect = 0.3f;
			}

			Sphere( Sphere& s ): p( s.p ), radius( s.radius ), color( s.color )
			{
				ambient = s.ambient, diffuse = s.diffuse, specular = s.specular;
				specPow = s.specPow;
				reflect = s.reflect;
			}

			float intersects( const Ray& r ) const
			{
				point v = r.dir;
				point pd = r.p.subtract( p );
				
				float a = v.dot( v );
				float b = ( v.dot( pd ) )*2;
				float c = pd.dot( pd ) - radius*radius;

				float q = b*b - 4.0f*a*c;

				if( q < 0 )
					return -1.0f;

				float sqrtQ = sqrt( q );

				float t;
				
				if( b >= 0 )
					t = ( - b + sqrtQ)/(2*a);
				else
					t = ( - b - sqrtQ)/(2*a);

				if( t <= 0 )
				{
					return -1.0f;
				}

				return t;
			}
	};
}

#endif