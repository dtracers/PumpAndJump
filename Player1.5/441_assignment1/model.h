#include <iostream>
#include <list>
#include <GL/glut.h>
#include "point.h"

#ifndef _WAMS_MODEL_H
#define _WAMS_MODEL_H

using namespace std;

namespace WAMS
{
	class color
	{
		public:
			float red, green, blue;
			color( float r, float g, float b)
			{
				red = r;
				green = g;
				blue = b;
			}
	};

	class Transform
	{
		public:
			enum Type{ TRANSLATION, ROTATION, SCALE };
		
			Type t;
			point info;

			Transform(){}
			Transform( Type _t, point _info ) : info( _info ), t(_t) { }

			virtual void add() {}
	};

	class Translation: public Transform
	{
		public:
			Translation( float x, float y, float z ) : Transform( TRANSLATION, point( x,y,z) ){}
			Translation( point p ) : Transform( TRANSLATION, p ){}

			void add()
			{
				glTranslatef( info.x, info.y, info.z );
			}
	};

	class Rotation: public Transform
	{
		public:
			Rotation( float x, float y, float z ) : Transform( ROTATION, point( x, y, z ) ){}
			Rotation( point p ) : Transform( ROTATION, p ){}

			void add()
			{
				if( info.x != 0.0f )
					glRotatef( info.x, 1.0f, 0.0f, 0.0f );
				if( info.y != 0.0f )
					glRotatef( info.y, 0.0f, 1.0f, 0.0f );
				if( info.z != 0.0f )
					glRotatef( info.z, 0.0f, 0.0f, 1.0f );
			}
	};

	class Scale: public Transform
	{
		public:
			Scale( float x, float y, float z ) : Transform( SCALE, point( x, y, z ) ){}
			Scale( point p ) : Transform( SCALE, p ){}

			void add()
			{
				glScalef( info.x, info.y, info.z );
			}
	};

	class Model
	{
		public:
			point p;
			point angle;
			point scalar;
			point gp;
			point ga;

			list<Transform> transforms;

			Model( point _p, point _angle, point _scale ) : p(_p), angle( _angle ), scalar( _scale ) { }

			void addTransform( Transform t )
			{
				transforms.push_front( t );
			}

			void addAllTransforms()
			{
				for(list<Transform>::iterator i = transforms.begin(); i != transforms.end(); i++ )
				{
					Transform t = (*i);
					t.add();
				}

				Translation trans( p );
				trans.add();
				Rotation rot( angle );
				rot.add();
				Scale s( scalar );
				s.add();
			}

			void clearAllTransforms()
			{
				transforms = list<Transform>();
			}

			void translate( float x, float y, float z )
			{
				p.x += x;
				p.y += y;
				p.z += z;
			}
			void rotate( float x_degrees, float y_degrees, float z_degrees )
			{
				angle.x += x_degrees;
				angle.y += y_degrees;
				angle.z += z_degrees;
			}
			void scale( float x_scalar, float y_scalar, float z_scalar )
			{
				scalar.x *= x_scalar;
				scalar.y *= y_scalar;
				scalar.z *= z_scalar;
			}

			virtual void display() {}

			void update( ) 
			{
				glPushMatrix();
				addAllTransforms();

				GLdouble modelview[16]; // where modelview matrix will be stored
				glGetDoublev( GL_MODELVIEW_MATRIX, modelview );// loads modelview matrix
				double** mv= (double**)modelview;

				float temp[4];
				temp[0] = p.x;
				temp[1] = p.y;
				temp[2] = p.z;
				temp[3] = 1.0f;

				float temp2[4]; 

				for( int i = 0; i < 4; i++ )
				{
					float t = 0.0f;
					for( int j = 0; j < 4; j++ )
					{
						t += ((float)mv[i][j])*temp[j];
					}
					temp2[i] = t;
				}

				gp.x = temp2[0];
				gp.y = temp2[1];
				gp.z = temp2[2];

				glPopMatrix();
			}
	};
};

#endif