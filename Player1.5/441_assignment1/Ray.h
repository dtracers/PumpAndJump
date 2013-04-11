#include <iostream>
#include <math.h>
#include "point.h"

#ifndef _WAMS_RAY_H
#define _WAMS_RAY_H

namespace WAMS
{

	class Ray
	{
		public:
			point p;
			point dir;

			Ray( point _p_, point _dir_ ) : p( _p_ ), dir( _dir_ ) {  dir.normalize(); }
			Ray( float _x_, float _y_, float _z_, float _xDir_, float _yDir_, float _zDir_ ) : p( _x_, _y_, _z_ ), dir( _xDir_, _yDir_, _zDir_ ) { dir.normalize(); }
			Ray( Ray& r ) : p( r.p ), dir( r.dir ) { }

	};
}

#endif