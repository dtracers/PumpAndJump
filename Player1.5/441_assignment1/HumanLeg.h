#include "util.h"
#include "model.h"
#include "point.h"
#include "Ray.h"
#include "Intersectable.h"
#include "Cylinder.h"
#include "Sphere.h"
#include "NameHandler.h"

#ifndef _WAMS_HUMANLEG_H
#define _WAMS_HUMANLEG_H
namespace WAMS
{

	class Joint: public Model
	{
		public:
			Sphere s;
			Joint( point loc, point orien, point scale, float radius ) : Model( loc, orien, scale ), s( loc, radius)
			{

			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();

				glutSolidSphere( s.radius, 10, 10 );

				glPopMatrix();
			}
	};

	class Toes: public Model
	{
		public:
			Joint* tuckles; //( tuckles == toe knuckles == Metatarsal phalangeal joints )
			Joint* toeEnd;
			GLUquadric* cylinder;
			Cylinder c;

			Toes( point loc, point orien, point scale ): Model( loc, orien, scale ), c( loc, point( 0.0f, 0.0f, 1.0f ), 1.5f )
			{
				tuckles = new Joint( point( 0.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ), 1.5f );
				toeEnd = new Joint( point( 0.0f, 0.0f, 1.5f), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ), 1.5f );
				cylinder = gluNewQuadric();
				nameHandler->add( this );
				cout<<"Toes"<<(nameHandler->modelToName[ this ])<<"\n";
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				tuckles->display();
				gluCylinder( cylinder, 1.5f, 1.5f, 1.5f, 20, 20 );
				toeEnd->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;

				c.o = globalPos;
				point localDir = globalCylinderDir;
				localDir = RotateAroundXAxis( localDir, angle.x );
				localDir = RotateAroundYAxis( localDir, angle.y );
				localDir = RotateAroundZAxis( localDir, angle.z );
				c.d = localDir;

				tuckles->s.p = globalPos;

				globalRotation = globalRotation.add( angle );
				point local = toeEnd->p;
				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );
				toeEnd->s.p = globalPos.add( local );
			}
	};

	class Foot: public Model
	{
		public:
			Joint* ankle;
			Toes* toes;
			Joint* heel; // lol not really a joint just can easily use the class for this
			GLUquadric* cylinder;
			Cylinder c;

			Foot( point loc, point orien, point scale ): Model( loc, orien, scale ), c( loc, point( 0.0f, 0.0f, 1.0f ), 2.0f )
			{
				ankle = new Joint( point( 0.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ), 2.0f );
				toes = new Toes( point( 0.0f, 0.0f, 7.0f ), point( 15.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) );
				heel = new Joint( point( 0.0f, 1.3f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ), 2.0f );
				cylinder = gluNewQuadric();
				nameHandler->add( this );
				cout<<"Foot"<<(nameHandler->modelToName[ this ])<<"\n";
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				ankle->display();
				gluCylinder( cylinder, 2.0f, 1.5f, 7.0f, 20, 20 );
				heel->display();
				toes->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;

				c.o = globalPos;
				point localDir = globalCylinderDir;
				localDir = RotateAroundXAxis( localDir, angle.x );
				localDir = RotateAroundYAxis( localDir, angle.y );
				localDir = RotateAroundZAxis( localDir, angle.z );
				c.d = localDir;

				ankle->s.p = globalPos;

				globalRotation = globalRotation.add( angle );
				point local = heel->p;
				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );
				heel->s.p = globalPos.add( local );

				local = toes->p;
				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );
				point newGlobal = globalPos.add( local );
				toes->update( newGlobal, c.d, globalRotation );
			}
	};

	class Crus: public Model
	{

		public:
			Joint* knee;
			GLUquadric* cylinder;
			Cylinder c;
			Foot* foot;

			Crus( point loc, point orien, point scale ): Model( loc, orien, scale ), c( loc, point( 0.0f, 0.0f, 1.0f ), 2.0f )
			{
				knee = new Joint( point( 0.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ), 2.5f );
				foot = new Foot( point( 0.0f, 0.0f, 17.0f ), point( 75.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) );
				cylinder = gluNewQuadric();
				nameHandler->add( this );
				cout<<"Crus"<<(nameHandler->modelToName[ this ])<<"\n";
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				knee->display();
				gluCylinder( cylinder, 2.5f, 2.0f, 17.0f, 20, 20 );
				foot->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;

				c.o = globalPos;
				point localDir = globalCylinderDir;
				localDir = RotateAroundXAxis( localDir, angle.x );
				localDir = RotateAroundYAxis( localDir, angle.y );
				localDir = RotateAroundZAxis( localDir, angle.z );
				c.d = localDir;

				knee->s.p = globalPos;

				globalRotation = globalRotation.add( angle );
				point local = foot->p;
				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );
				point newGlobal = globalPos.add( local );
				foot->update( newGlobal, c.d, globalRotation );
			}
	};

	class Thigh: public Model
	{

		public:
			Joint* hip;
			GLUquadric* cylinder;
			Cylinder c;
			Crus* crus;

			Thigh( point loc, point orien, point scale ): Model( loc, orien, scale ), c( loc, point( 0.0f, 0.0f, 1.0f ), 3.0f )
			{
				hip = new Joint( point( 0.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ), 3.5f );
				crus = new Crus( point( 0.0f, 0.0f, 19.0f ), point( -5.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) );
				cylinder = gluNewQuadric();
				nameHandler->add( this );
				cout<<"Thigh"<<(nameHandler->modelToName[ this ])<<"\n";
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				hip->display();
				gluCylinder( cylinder, 3.5f, 2.5f, 19.0f, 20, 20 );
				crus->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;

				c.o = globalPos;
				point localDir = globalCylinderDir;
				localDir = RotateAroundXAxis( localDir, angle.x );
				localDir = RotateAroundYAxis( localDir, angle.y );
				localDir = RotateAroundZAxis( localDir, angle.z );
				c.d = localDir;

				hip->s.p = globalPos;

				globalRotation = globalRotation.add( angle );
				point local = crus->p;
				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );
				point newGlobal = globalPos.add( local );
				crus->update( newGlobal, c.d, globalRotation );
			}

	};

	class HumanLeg: public Model
	{
		public:
			Thigh* thigh;

			HumanLeg( point loc, point orien, point scale ): Model( loc, orien, scale )
			{
				thigh = new Thigh( point( 0.0f, 0.0f, 0.0f ), point( 95.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) );
			}
			
			void display()
			{
				glPushMatrix();
				addAllTransforms();

				glColor4f( 0.2f, 0.2f, 0.2f, 1.0f );

				thigh->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				globalRotation = globalRotation.add( angle );
				point local = thigh->p;
				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );
				point newGlobal = globalPos.add( local );
				thigh->update( newGlobal, globalCylinderDir, globalRotation );
			}
	};

	class HumanLegController
	{
		public:
			HumanLeg* hl;
			Thigh* thigh;
			Crus* crus;
			Foot* foot;
			Toes* toes;
			point* p;
			point* hipAngles;
			float* kneeXRotation;
			float* ankleXRotation;
			float* ankleZRotation;
			float* tucklesXRotation;
			bool changed;

			HumanLegController( HumanLeg* _hl_ )
			{
				hl = _hl_;

				thigh = hl->thigh;
				crus = thigh->crus;
				foot = crus->foot;
				toes = foot->toes;

				p = &(hl->p);
				hipAngles = &( thigh->angle );
				kneeXRotation = &( crus->angle.x );
				ankleXRotation = &( foot->angle.x );
				ankleZRotation = &( foot->angle.z );
				tucklesXRotation = &( toes->angle.x );

				changed = true;
			}

			void setPosition( float x, float y, float z )
			{
				p->x = x;
				p->y = y;
				p->z = z;
			}

			void setHipAngles( float rx, float ry, float rz )
			{
				hipAngles->x = rx;
				hipAngles->y = ry;
				hipAngles->z = rz;
			}
			
			void setKneeXRotation( float rx )
			{
				( *kneeXRotation ) = rx;
			}

			void setAnkleXAndZAngles( float rx, float rz )
			{
				( *ankleXRotation ) = rx;
				( *ankleZRotation ) = rz;
			}

			void setTucklesXRotation( float rx )
			{
				( *tucklesXRotation ) = rx;
			}

			void setPose( float x, float y, float z, float hx, float hy, float hz, float kx, float ax, float az, float tx )
			{
				setPosition( x, y, z );
				setHipAngles( hx, hy, hz );
				setKneeXRotation( kx );
				setAnkleXAndZAngles( ax, az );
				setTucklesXRotation( tx );
				changed = true;
			}

			void getPose( float* a )
			{
				a[0] = p->x;
				a[1] = p->y;
				a[2] = p->z;
				a[3] = hipAngles->x;
				a[4] = hipAngles->y;
				a[5] = hipAngles->z;
				a[6] = *(kneeXRotation);
				a[7] = *(ankleXRotation);
				a[8] = *(ankleZRotation);
				a[9] = *(tucklesXRotation);
			}

			void getPose( double* a )
			{
				a[0] = (double)p->x;
				a[1] = (double)p->y;
				a[2] = (double)p->z;
				a[3] = (double)hipAngles->x;
				a[4] = (double)hipAngles->y;
				a[5] = (double)hipAngles->z;
				a[6] = (double)*(kneeXRotation);
				a[7] = (double)*(ankleXRotation);
				a[8] = (double)*(ankleZRotation);
				a[9] = (double)*(tucklesXRotation);
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation )
			{
				if( changed )
				{
					hl->update( globalPos, globalCylinderDir, globalRotation );
					changed = false;
				}
			}

			void print()
			{
				foot->c.o.print();
				foot->c.d.print();
			}
	};
};
#endif