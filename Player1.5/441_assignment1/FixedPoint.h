#include "point.h"
#include "NameHandler.h"
#include "model.h"
#include <gl/glut.h>
#include <vector>

#ifndef _WAMS_FIXEDPOINT_H
#define _WAMS_FIXEDPOINT_H

using namespace std;

namespace WAMS
{
	class FixedPoint: public Model
	{

		public:		
			point* org;
			point distance;
			point* angles;
			FixedPoint( point loc, point* globalModel, point* globalModelAngles) : Model( loc, point( 0.0f, 0.0f, 0.0f ), point( 5.0f, 5.0f, 5.0f ) ) 
			{
				org = globalModel;
				distance = loc.subtract( (*globalModel) );
				angles = globalModelAngles;
				nameHandler->add( this );
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				glColor4f( 0.8f, 0.2f, 0.2f, 1.0f );
				glutSolidSphere( 1.0, 10, 10 );

				glPopMatrix();
			}

			point fpNow( )
			{
				point fpNow = *(org);
				point local = point( distance.distance(), 0.0f, 0.0f );
				local = RotateAroundXAxis( local, angles->x );
				local = RotateAroundYAxis( local, angles->y );
				local = RotateAroundZAxis( local, angles->z );
				fpNow = fpNow.add( local );
				return fpNow.subtract( p );
			}

			void findfpnowprint()
			{
				point fpNow = *(org);
				cout<<"\n";
				angles->print();
				cout<<"\n";
				point local = point( distance.distance(), 0.0f, 0.0f );
				local.print();
				local = RotateAroundXAxis( local, angles->x );
				local.print();
				local = RotateAroundYAxis( local, angles->y );
				local.print();
				local = RotateAroundZAxis( local, angles->z );
				local.print();
				cout<<"\n";
				local.print();
				fpNow = fpNow.add( local );
				fpNow.print();
				fpNow.subtract( p ).print();
			}
	};

	vector< FixedPoint* > * fp;
	
};

#endif