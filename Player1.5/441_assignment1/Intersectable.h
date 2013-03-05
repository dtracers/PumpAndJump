#include "point.h"
#include "Ray.h"
#ifndef _WAMS_INTERSECTABLE_H_
#define _WAMS_INTERSECTABLE_H_

namespace WAMS
{
	class Intersectable
	{
		public:
			virtual float intersects( const Ray& ) const = 0;
	};
}

#endif