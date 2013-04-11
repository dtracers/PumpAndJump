#include "point.h"
#include "Intersectable.h"
#include "Ray.h"

#ifndef _WAMS_CYLINDER_H
#define _WAMS_CYLINDER_H

namespace WAMS 
{
	class Cylinder : Intersectable
	{
		public:
			point o;
			point d;
			float radius;
			Cylinder( point _o_, point _d_, float _radius_ ): o( _o_ ), d( _d_ ), radius( _radius_ ) 
			{
				d.normalize();
			}

			float intersects( const Ray& r ) const
			{
				point VxD = r.dir.cross( d );
				point P0minusOxD = ( r.p.subtract( o ) ).cross( d );
				
				float a = VxD.dot( VxD );
				float b = 2*( P0minusOxD.dot( VxD ) );
				float c = P0minusOxD.dot( P0minusOxD ) - radius*radius;

				float q = b*b - 4*a*c;

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
};
#endif