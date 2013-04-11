#include <cmath>
#include <map>
#include "point.h"
#include "Ray.h"

#ifndef _WAMS_UTIL_H
#define _WAMS_UTIL_H

#define PI 3.14159265

namespace WAMS
{
	using namespace std;

	map< float, float > sines;
	map< float, float > cosines;

	void utilInit()
	{
		for( float i = 0.0f; i <= 360.0f; i++ )
		{
			float iInRadians = i*PI/180.0f;
			sines[i] = sin( iInRadians );
			cosines[i] = cos( iInRadians );
		}
	}

	float round( float f)
	{
		return floor(f+0.5f);
	}

	int makePositive( int degrees )
	{
		if( degrees < 0 )
			return 360 + degrees;
		return degrees;
	}

	float rcos( float d )
	{
		float dInRadians = d*PI/180.0f;
		return cos( dInRadians);
	}

	float fcos( float degrees )
	{
		int d = (int)round( degrees );
		d = d%360;
		d = makePositive( d );
		
		return cosines[ d ];
	}

	float rsin( float d )
	{
		float dInRadians = d*PI/180.0f;
		return sin( dInRadians);
	}

	float fsin( float degrees )
	{
		int d = (int)round( degrees );
		d = d%360;
		d = makePositive( d );

		return sines[ d ];
	}

	float rtan( float d )
	{
		float dInRadians = d*PI/180.0f;
		return tan( dInRadians );
	}

	point RotateAroundXAxis( point p, float angle )
	{
		angle = angle*PI/180.0f;
		float x1 = p.x;
		float y1 = p.y*cos( angle ) - p.z*sin( angle );
		float z1 = p.y*sin( angle ) + p.z*cos( angle );
		return point( x1, y1, z1 );
	}

	point RotateAroundYAxis( point p, float angle )
	{
		angle = angle*PI/180.0f;
		float x1 = p.x*cos( angle ) + p.z*sin( angle );
		float y1 = p.y;
		float z1 = -p.x*sin( angle ) + p.z*cos( angle );
		return point( x1, y1, z1 );
	}

	point RotateAroundZAxis( point p, float angle )
	{
		angle = angle*PI/180.0f;
		float x1 = p.x*cos( angle ) - p.y*sin( angle );
		float y1 = p.x*sin( angle ) + p.y*cos( angle );
		float z1 = p.z;
		return point( x1, y1, z1 );
	}
}

#endif