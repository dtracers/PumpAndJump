#include "point.h"
#include "util.h"
#include <cmath>

#ifndef _WAMS_PROJECTION_MATRIX_H_
#define _WAMS_PROJECTION_MATRIX_H_

namespace WAMS
{
	class ProjectionMatrix
	{
		public:
			float h0, v0;
			float h, v;
			float sh, sv;
			float horiAngle;
			float vertAngle;
			float x, y;
			float depth;

			ProjectionMatrix( float _h0_, float _v0_, float _vertAngle_, float _depth_ ) : h0( _h0_ ), v0( _v0_ ),
				vertAngle( _vertAngle_ ), depth( _depth_ )
			{
				y = depth * ( rsin( vertAngle/2.0f ) / rcos( vertAngle/2.0f ) );
				v = v0 + 2*y;
				h = v*( h0 / v0 );
				x = ( h - h0 ) / 2.0;

				sv = sqrt( depth*depth + y * y );
				sh = sqrt( depth*depth + x * x );

				horiAngle = 2*atan( x / sv )*180.0f/PI;
			}
	};
}

#endif