#include "point.h"
#include "model.h"
#include <map>
#include <gl\glut.h>

#ifndef _WAMS_NAME_HANDLER_H
#define _WAMS_NAME_HANDLER_H

namespace WAMS
{
	class NameHandler
	{
		public:
			GLuint count;
			map< Model*, GLuint > modelToName;
			map< GLuint, Model* > nameToModel;

			NameHandler()
			{
				count = 1;
			}

			void add( Model* m )
			{
				modelToName[ m ] = count;
				nameToModel[ count ] = m;
				count++;
			}

			void LoadName( Model* m )
			{
				glLoadName( modelToName[m] );
			}

			void PushName( Model* m )
			{
				glPushName( modelToName[m] );
			}

			void PopName( )
			{
				glPopName();
			}

			Model* getModel( GLuint name )
			{
				return nameToModel[ name ];
			}
	};

	NameHandler* nameHandler;
};

#endif _WAMS_NAME_HANDLER_H