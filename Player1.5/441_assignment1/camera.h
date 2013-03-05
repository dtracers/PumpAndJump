#include <gl/glut.h>
#include "util.h"
#include "model.h"

#ifndef _WAMS_CAMERA_H
#define _WAMS_CAMERA_H

namespace WAMS
{
	class camera
	{
		public:
			point cameraLoc;
			point lookAt;
			point norm;
			point angles;

			camera()
			{
				cameraLoc = point( 0.0f, 0.0f, 0.0f );
				lookAt = point( 100.0f, 0.0f, 0.0f );
				norm = point( 0.0f, 1.0f, 0.0f );
				angles = point( 0.0f, 0.0f, 0.0f );
			}

			void move( float multi )
			{
				float tx = (lookAt.x - cameraLoc.x);
				float ty = (lookAt.y - cameraLoc.y);
				float tz = (lookAt.z - cameraLoc.z);

				float h = sqrt( tx*tx + ty*ty + tz*tz );

				float rx = (tx/h);
				float ry = (ty/h);
				float rz = (tz/h);

				moveLoc( multi*rx, multi*ry, multi*rz );
			}

			void strafe( float multi )
			{
				
				float tx = (lookAt.x - cameraLoc.x);
				float tz = (lookAt.z - cameraLoc.z);

				float h = sqrt( tx*tx + tz*tz );

				float x = h*fsin(angles.x+90.0f);
				float z = h*fcos(angles.x+90.0f);

				float rx = x/h;
				float rz = z/h;

				moveLoc( multi*rx, 0, multi*rz );// note that y doesn't matter and ratios x and z switched
			}

			void moveLoc( float x, float y, float z )
			{
				cameraLoc.x += x;
				cameraLoc.y += y;
				cameraLoc.z += z;

				lookAt.x += x;
				lookAt.y += y;
				lookAt.z += z;
			}

			void modXAngle( float mod )
			{
				angles.x += mod;

				float tx = (lookAt.x - cameraLoc.x);
				float tz = (lookAt.z - cameraLoc.z);

				float h = sqrt( tx*tx + tz*tz );

				float x = h*fsin( angles.x );
				float z = h*fcos( angles.x );

				lookAt.x = cameraLoc.x + x;
				lookAt.z = cameraLoc.z + z;
			}


			void modYAngle( float mod )
			{
				angles.y += mod;
				if( angles.y > 179.0f )
					angles.y = 179.0f;
				else if ( angles.y < -179.0f )
					angles.y = -179.0f;

				float tx = (lookAt.x - cameraLoc.x);
				float ty = (lookAt.y - cameraLoc.y);
				float tz = (lookAt.z - cameraLoc.z);

				float h = sqrt( tx*tx + ty*ty + tz*tz );

				float y = h*fsin( angles.y );

				lookAt.y = cameraLoc.y + y;
			}

			void placeCamera()
			{
				gluLookAt(	cameraLoc.x, cameraLoc.y, cameraLoc.z, 
					lookAt.x, lookAt.y, lookAt.z,
					norm.x, norm.y, norm.z);
			}

			void setLoc( float x, float y, float z )
			{
				cameraLoc = point( x, y, z );
			}

			void setLookAt( float x, float y, float z )
			{
				lookAt = point( x, y, z );
			}

			void setNorm( float x, float y, float z )
			{
				norm = point ( x, y, z );
			}

	};
};

#endif