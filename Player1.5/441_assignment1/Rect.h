#include "point.h"
#include "model.h"
#include "NameHandler.h"

#ifndef _WAMS_RECT_H
#define _WAMS_RECT_H

namespace WAMS
{
	class Rect : public Model
	{
		public:
			Rect( point p, point scale ): Model( p, point(0,0,0), scale )
			{
				nameHandler->add(this);
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, 0.0f, 0.0f);
					glVertex3f(	0.0f, 1.0f, 0.0f);
					glVertex3f( 1.0f, 1.0f,  0.0f);
					glVertex3f( 1.0f, 0.0f, 0.0f);
				glEnd();

				glPopMatrix();
			}

			void update()
			{

			}
	};

	Rect* selectedRect = NULL;
	double origRect[3];
}

#endif