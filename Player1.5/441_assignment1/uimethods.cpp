#include "ui.h"
#include "model.h"
#include "util.h"
#include "camera.h"
#include "HumanLeg.h"
#include "ProjectionMatrix.h"
#include "Ray.h"
#include "Plane.h"
#include "Player.h"
#include "NameHandler.h"
#include "FixedPoint.h"
#include "levmar.h"
#include "Pose.h"
#include "Keyframe.h"
#include "fstream"
#include "Rect.h"
#include <iostream>

#ifndef _WAMS_KEYFRAME_METHODS_CPP
#define _WAMS_KEYFRAME_METHODS_CPP

#define LAMBDA 0.1
#define CON 5.0

using namespace WAMS;

#ifndef LM_DBL_PREC
#error Example program assumes that levmar has been compiled with double precision, see LM_DBL_PREC!
#endif


	enum Mode{ CAMERA = -1, MOVE = 0, ANIMATE = 1 };
	int count;
	int sum;
	camera cam;
	camera cam2;
	int oldX = 0;
	int oldY = 0;
	Mode mode = MOVE; // -1 camera
	//HumanLeg* hl;
	//HumanLegController* hlc;
	Player* pl;
	ProjectionMatrix pm( .8f, .6f, 45.0f, 300.0f );
	float time;
	int height = 600;
	int width = 800;
	float origPos[WAMS_PLAYER_DOF];
	FixedPoint* pointer;
	bool isDragging = false;
	bool hasChanged = false;
	Plane* dragPlane = NULL;
	Plane* dragRectPlane = NULL;
	//selection buffer
	#define SELECT_BUFFER_SIZE 40// space for up to 10 hits

	void costEvaluation(double* p, double* xm, int m, int n, void* data)
	{
		pl->setPose( p );
		pl->update( ); 
		//hlc->setPose( (float)p[0], (float)p[1], (float)p[2], (float)p[3], (float)p[4], (float)p[5], (float)p[6], (float)p[7], (float)p[8], (float)p[9] );
		//hlc->update( (*hlc->p), point( 0.0f, 0.0f, 1.0f ), point( 0.0f, 0.0f, 0.0f ) );
		
		for(unsigned int i = 0; i < fp->size(); i++ )
		{
			point cDistance = (*fp)[i]->fpNow(); 
			cDistance.abs();
		//	cDistance.scale(10);
			xm[i] = ((double)cDistance.distance())*CON;//*(double)cDistance.x;
		}

		if( pointer != NULL )
		{
			point cDistance = pointer->fpNow();
			cDistance.abs();
		//	cDistance.scale(10);
			xm[fp->size()] = ((double)cDistance.distance());//*(double)cDistance.x;
		}

		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			xm[ (fp->size()+1) + i ] = abs( (double)(LAMBDA*(origPos[i] - p[i]) ) + .0001 );
		}

	//	for( int i = 0; i < 14; i++ )
	//	{
	//		xm[ (fp->size()+2)*3 + i ] = 0.0;
	//	}

		//naturalGod( p, &xm[ (fp->size()+2)*3 ]);
	}

	point getIntersection( int* viewport, int x, int y, float* winZ )
	{
		/* code for this is from http://nehe.gamedev.net/article/using_gluunproject/16013/ 
			as i was having trouble with the pick ray */
		
		GLdouble modelview[16]; // where modelview matrix will be stored
		GLdouble projection[16]; // where projection matrix will be stored
		GLfloat winX, winY; // window x, y, and z buffer positions ( note z is always between 0.0 and 1.0 
			//and shows how close the point is to the end or begining of the frustrum )
		GLdouble posX, posY, posZ; // stores the global point gained from unproject
 
		glGetDoublev( GL_MODELVIEW_MATRIX, modelview );// loads modelview matrix
		glGetDoublev( GL_PROJECTION_MATRIX, projection ); // loads projection matrix
 
		winX = (float)x;
		winY = (float)viewport[3] - (float)y; // invert y component on windows systems

		glReadPixels( x, int(winY), 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZ ); // finds the z buffer for the pixel at (x, y)
 
		gluUnProject( winX, winY, *winZ, modelview, projection, viewport, &posX, &posY, &posZ);// finds the global coordinates
			// dear god this is even better then a pick ray :D

		return point( (float)posX, (float)posY, (float)posZ );
	}

	point getIntersection( int* viewport, int x, int y, float winZ )
	{
		/* code for this is from http://nehe.gamedev.net/article/using_gluunproject/16013/ 
			as i was having trouble with the pick ray */
		
		GLdouble modelview[16]; // where modelview matrix will be stored
		GLdouble projection[16]; // where projection matrix will be stored
		GLfloat winX, winY; // window x, y, and z buffer positions ( note z is always between 0.0 and 1.0 
			//and shows how close the point is to the end or begining of the frustrum )
		GLdouble posX, posY, posZ; // stores the global point gained from unproject
 
		glGetDoublev( GL_MODELVIEW_MATRIX, modelview );// loads modelview matrix
		glGetDoublev( GL_PROJECTION_MATRIX, projection ); // loads projection matrix
 
		winX = (float)x;
		winY = (float)viewport[3] - (float)y; // invert y component on windows systems
 
		gluUnProject( winX, winY, winZ, modelview, projection, viewport, &posX, &posY, &posZ);// finds the global coordinates
			// dear god this is even better then a pick ray :D

		return  point( (float)posX, (float)posY, (float)posZ );
	}

	void UIMethodsInit()
	{
		PosesInit();
		nameHandler = new NameHandler();
		fp = new vector< FixedPoint* >();
		//hl = new HumanLeg( point( 0.0f, 50.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) );
		//hlc = new HumanLegController( hl );
		pl = new Player( point( ((float)width)/2.0f, ((float)height)/2.0f, 0 ), point( 0.0f, 0.0f, 0.0f ) );
		selectedRect = new Rect( point( 0,0,0 ), point( 2000, 100, 0 ) );
		float pos[WAMS_PLAYER_DOF];
		pl->getPose( pos );
		//hlc->getPose( pos );
		keyframes.push_back( new Keyframe( pos, -1.0f ) );
		count = 0;
		cam.setLoc( 0, 0, -50 );
		cam.setLookAt( 0, 0, 50 );
		//cam2.setLoc( 0.0f, 300.0f, 0.0f );
		//cam.setLookAt( 0, 100, 0 );
	}

	void update( unsigned int changeInTime )
	{
		sum += (int)( changeInTime );
		count++;

		pl->update(  );
	}

	void wasHits(int hits, int* viewport, GLuint* SelectionBuffer, int xx, int yy, bool rightClick )
	{
		if( hits > 0 && mode == MOVE )
		{
			GLuint id = SelectionBuffer[3];
			GLuint depth = SelectionBuffer[1];
			for (int i = 1; i < hits; i++)              // Loop Through All The Detected Hits
			{
				// If This Object Is Closer To Us Than The One We Have Selected
				if (SelectionBuffer[i*4+1] < depth)
				{
					id = SelectionBuffer[i*4+3];          // Select The Closer Object
					depth = SelectionBuffer[i*4+1];  // Store How Far Away It Is
				}      
			}

			float winZ;
			point p = getIntersection( viewport, xx, yy, &winZ );
			Model* m = nameHandler->getModel( id );
			if( m != NULL )
			{
				if( rightClick )
				{
					bool selectedFixedPoint = false;
					for (vector<FixedPoint*>::iterator it = (*fp).begin() ; it != (*fp).end() && !selectedFixedPoint; ++it)
					{
						if( m == (Model*)(*it) )
						{
							cout<<"erased\n";
							selectedFixedPoint = true;
							delete *it;
							(*fp).erase( it );
						}
						cout<<" not erased "<< nameHandler->modelToName[(*it)]<<" compared to " << id<<"\n";
					}
					if( !selectedFixedPoint )
						fp->push_back( new FixedPoint( p, &(m->gp), &(m->ga) ) );
				}
				else
				{
					if( !isDragging )
					{	
						point FromCamera = point( 0, 0, -1.0f );
						FromCamera.normalize();
						point Vert = point( 0, 1, 0 );
						point Sideways = Vert.cross( FromCamera );
						Sideways.normalize();
						
						if( selectedRect != m )
						{
							pointer = new FixedPoint( p, &(m->gp), &(m->ga) );
							dragPlane = new Plane( p, Vert, Sideways );
							pl->getPose( origPos );
						}
						else
						{
							pointer = new FixedPoint( p, &(m->p), &(m->angle) );
							dragRectPlane = new Plane( p, Vert, Sideways );
							origRect[0] = (double)( xx );
							origRect[1] = (double)( yy );
						}			
					}
				}
			}
				
		}
	}

	void changePose( )
	{
		if( dragPlane != NULL )
		{
			GLint vp[4];// get viewport
			glGetIntegerv(GL_VIEWPORT, vp);

			Ray r( point( (float)oldX, (float)(height-oldY), -50.0f ), point( 0.0f, 0.0f, -1.0f ) );

			float t = dragPlane->intersects( r );

			point newInter =  r.dir.scale(t);
			newInter = r.p.add( newInter );

			pointer->p = newInter;

			double p[WAMS_PLAYER_DOF];
			pl->getPose( p );
			double* x;
			x = new double[ (1+fp->size()+WAMS_PLAYER_DOF) ];

			for(unsigned int i=0;i<(int)(1+fp->size()+WAMS_PLAYER_DOF);i++)
			{
				x[i] = 0.0;
			}

			//cerr<<"IKStart\n";
			double opts[LM_OPTS_SZ];
			opts[0] = LM_INIT_MU; opts[1] = 1E-15; opts[2] = 1E-15; opts[3] = 1E-20;
			opts[4] = LM_DIFF_DELTA;
			dlevmar_dif( costEvaluation, p, &(*x), WAMS_PLAYER_DOF, (int)(1+fp->size()+WAMS_PLAYER_DOF), 100, opts, NULL, NULL, NULL, NULL);

			//cerr<<"IKDone\n";
			pl->setPose( p );

			delete x;
		}

		if( dragRectPlane != NULL )
		{
			GLint vp[4];// get viewport
			glGetIntegerv(GL_VIEWPORT, vp);

			float x = (float)oldX;
			float y = (float)(oldY);

			selectedRect->p = selectedRect->p.add(point( x - origRect[0], -(y - origRect[1]),  0.0f ));
			pointer->p = pointer->p.add(point( x - origRect[0], -(y - origRect[1]),  0.0f ));
			origRect[0] = (double)( x );
			origRect[1] = (double)( y );
		}
	}

	void display()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		//cam.placeCamera();

		glColor4f( 0.1, 0.7, 0.1f, 1.0f );
		glBegin(GL_QUADS);
			glVertex3f( 0.0f, (float)height, -1.0f);
			glVertex3f(	0.0f, 0.0f, -1.0f);
			glVertex3f( (float)width, 0.0f,  -1.0f);
			glVertex3f( (float)width, (float)height, -1.0f);
		glEnd();

		//draw ground
		/*glColor4f( 0.1f, 0.7f, 0.1f, 1.0f );
		glBegin(GL_QUADS);
			glVertex3f(-300.0f, 0.0f, -300.0f);
			glVertex3f(-300.0f, 0.0f,  300.0f);
			glVertex3f( 300.0f, 0.0f,  300.0f);
			glVertex3f( 300.0f, 0.0f, -300.0f);

			glColor4f( 0.2f, 0.2f, 0.9f, 1.0f );
			glVertex3f(-300.0f, 300.0f, -300.0f);
			glVertex3f(-300.0f, 300.0f,  300.0f);
			glVertex3f( 300.0f, 300.0f,  300.0f);
			glVertex3f( 300.0f, 300.0f, -300.0f);

			glColor4f( 0.6f, 0.6f, 0.9f, 1.0f );
			glVertex3f(-300.0f, 300.0f, -300.0f);
			glVertex3f(-300.0f, 300.0f,  300.0f);
			glVertex3f( -300.0f, 0.0f, 300.0f);
			glVertex3f( -300.0f, 0.0f,  -300.0f);

			glColor4f( 0.4f, 0.4f, 0.9f, 1.0f );
			glVertex3f(-300.0f, 300.0f, -300.0f);
			glVertex3f( 300.0f, 300.0f,  -300.0f);
			glVertex3f( 300.0f, 0.0f, -300.0f);
			glVertex3f( -300.0f, 0.0f,  -300.0f);

			glColor4f( 0.4f, 0.4f, 0.9f, 1.0f );
			glVertex3f(300.0f, 300.0f, 300.0f);
			glVertex3f(-300.0f, 300.0f, 300.0f);
			glVertex3f( -300.0f, 0.0f, 300.0f);
			glVertex3f( 300.0f, 0.0f, 300.0f);

			glColor4f( 0.5f, 0.5f, 0.9f, 1.0f );
			glVertex3f( 300.0f, 300.0f, 300.0f);
			glVertex3f( 300.0f, 300.0f, -300.0f);
			glVertex3f( 300.0f, 0.0f, -300.0f);
			glVertex3f( 300.0f, 0.0f, 300.0f);
		glEnd();*/

		//hl->display();
		pl->display();

		for( unsigned int i = 0; i < fp->size(); i++ )
		{
			(*fp)[i]->display();
		}

		if( selectedRect != NULL )
		{
			selectedRect->display();
		}

		if( pointer != NULL )
		{
			pointer->display();
		}
	}

	void Selection( int x, int y, bool rightClick )
	{
		GLint hits;// number of objects in pick matrix
		GLuint SelectionBuffer[SELECT_BUFFER_SIZE]; // selection buffer
		
		GLint vp[4];// get viewport
		glGetIntegerv(GL_VIEWPORT, vp);

		glSelectBuffer(SELECT_BUFFER_SIZE, SelectionBuffer);// set selection buffer

		glRenderMode( GL_SELECT );//set render mode to select

		glInitNames();// intialize names
		glPushName( 0 );
		
		glMatrixMode(GL_PROJECTION); // switch to projection matrix and add another matrix
		glPushMatrix();
		glLoadIdentity();

		gluPickMatrix((GLdouble) x, (GLdouble) (vp[3]-y), 1.0f, 1.0f, vp); // add the pick matrix

		float ratio =  width * 1.0f / height;
		//gluPerspective(45.0f, ratio, 0.1f, 900.0f); // add the perspective matrix
		glOrtho( 0.0, (double)width , 0.0, (double)height, -10.0f, 500.0f );

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// intialize the draw mode
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		//cam.placeCamera();

		pl->display();

		for( unsigned int i = 0; i < fp->size(); i++ )
		{
			(*fp)[i]->display();
		}

		if( selectedRect != NULL )
		{
			selectedRect->display();
		}

		glMatrixMode( GL_PROJECTION ); // switch to projection and pop
		glPopMatrix();

		glMatrixMode(GL_MODELVIEW);// switch back to model view 

		hits = glRenderMode( GL_RENDER ); // get hits

		wasHits( hits, vp, SelectionBuffer, x, y, rightClick ); // process hits
	}

	void changePoseUpdate( unsigned int changeInTime )
	{
		if( mode == MOVE && isDragging && hasChanged && pointer != NULL)
		{
			changePose();
			hasChanged = false;
		}
	}

	void camForward( unsigned int changeInTime )
	{
		if( mode == CAMERA )
			if( isPressed('w') )
				cam.move( 4 );
	}

	void camStrafeLeft( unsigned int changeInTime )
	{
		if( mode == CAMERA )
			if( isPressed('a') )
				cam.strafe( 4 );
	}

	void camBackward( unsigned int changeInTime )
	{
		if( mode == CAMERA )
			if( isPressed('s') )
				cam.move( -4 );
	}

	void camStrafeRight( unsigned int changeInTime )
	{
		if( mode == CAMERA )
			if( isPressed('d') )
				cam.strafe( -4 );
	}

	void animateUpdate( unsigned int changeInTime )
	{
		if( mode == ANIMATE )
		{
			time += ((float)changeInTime)/1000.0f;
			float* pos = getAnimation( time );
			if( pos != NULL )
			{
				pl->setPose( pos );

				delete pos;
			}
		}
	}

	void moveHip( unsigned int changeInTime )
	{
		if( mode == MOVE )
		{
			if( isPressed('w') )
				pl->hip->p.y++;
			if( isPressed('s') )
				pl->hip->p.y--;
		}
	}

	void updateInit()
	{
		registerUpdateMethod( update );
		registerUpdateMethod( animateUpdate );
		registerUpdateMethod( moveHip );
		//registerUpdateMethod( camForward );
		//registerUpdateMethod( camBackward );
		//registerUpdateMethod( camStrafeRight );
		//registerUpdateMethod( camStrafeLeft );
		registerUpdateMethod( changePoseUpdate );
	}

	void clearFP( int x, int y, int modifiers )
	{
		if( mode == MOVE )
		{
			for( unsigned int i = 0; i < fp->size(); i++ )
			{
				delete (*fp)[i];
			}
		
			delete fp;
			fp = new vector < FixedPoint* >();
		}
		else if( mode == ANIMATE )
		{
			Keyframe* temp = keyframes[0];
			for( unsigned int i = 1; i < keyframes.size(); i++ )
			{
				delete keyframes[i];
			}

			keyframes = vector<Keyframe*>();
			keyframes.push_back( temp );

			lastT = 0.0f;
			lastIndex = 1;
			lastTime = 0.0f;
		}
	}

	void setCameraMode( int x, int y, int modifiers )
	{
		mode = CAMERA;
	}

	void setMoveMode( int x, int y, int modifiers )
	{
		mode = MOVE;
	}

	void reset( int x, int y, int modifiers )
	{
		clearFP( x, y, modifiers );
		float a[ WAMS_PLAYER_DOF ];
		a[0] = 0.0f;//hip
		a[1] = -45.0f;//left leg
			a[2] = -60.0f;
			a[3] = 90.0f;
			a[4] = 0.0f;
		a[5] = -90.0f;
			a[6] = -45.0f;
			a[7] = 90.0f;
			a[8] = 0.0f;
		a[9] = 90.0f;// torso
			a[10] = 135.0f;//left arm
				a[11] = 90.0f;
			a[12] = -135.0f;//right arm
				a[13] = 60.0f;
			a[14] = 0.0f;//head
			a[15] = pl->origY;
		pl->setPose( a );
	}

	void setKeyframe( int x, int y, int modifiers )
	{
		float t = 0.0f;
		for( int i = 0; i < keyframes.size(); i++ )
		{
			cout<<"("<<i<<","<<keyframes[i]->t<<")";
			if( i % 5 == 4 )
				cout<<"\n";
		}
		cout<<"\n";
		cout<<"Time:\t";
		cin>>t;
		float pos[WAMS_PLAYER_DOF];
		pl->getPose( pos );
		//hlc->getPose( pos );
		keyframes.push_back( new Keyframe( pos, t ) );
	}

	void animate( int x, int y, int modifiers )
	{
		mode = ANIMATE;
		time = keyframes[1]->t;
		isDragging = false;
	}

	void loadAni( int x, int y, int modifiers )
	{
		Keyframe* temp = keyframes[0];
		for( unsigned int i = 1; i < keyframes.size(); i++ )
		{
			delete keyframes[i];
		}

		keyframes = vector<Keyframe*>();
		keyframes.push_back( temp );

		ifstream ifs( "ani.dat" );
		int type;
		ifs>>type;

		int size;
		float origY = pl->origY;

		if( type >= 0 )
		{
			size = type;
			type = 0;
			cout<<"Type: "<<type<<endl;
			cout<<size<<"\n";
			cout<<"OrigY: "<<origY<<"\n";
		}
		else if( type == -1 )
		{
			ifs>>size;
			cout<<"Type: "<<type<<endl;
			cout<<size<<"\n";

			ifs>>origY;
			pl->origY = origY;
			cout<<"OrigY: "<<origY<<"\n";
		}
		else if( type == -2 )
		{
			ifs>>size;
			ifs>>origY;
			pl->origY = origY;

			char s[256];
			ifs.getline( s, 256 );
		}

		float pos[ WAMS_PLAYER_DOF ];

		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
			pos[i] = 0.0f;

		float t = 0.0f;

		for( int i = 0; i < size; i++ )
		{
			for( int j = 0; j < WAMS_PLAYER_DOF; j++ )
			{
				if( type == 0 && j == 15)
				{
					pos[j] = origY;
				}
				else
				{
					ifs>>pos[j];
					cout<<pos[j]<<" ";
				}
			}
			ifs>>t;
			cout<<t<<"\n";
			keyframes.push_back( new Keyframe( pos, t ) );
		}

		cout<<"Size:"<<keyframes.size()<<"\n";

		ifs.close();
	}

	void printAni( int x, int y, int modifiers )
	{
		ofstream ofs( "ani.dat");

		ofs<<(-1)<<" "<<keyframes.size()-1<<" "<<pl->origY<<"\n";

		for( int i = 1; i < keyframes.size(); i++ )
		{
			for( int j = 0; j < WAMS_PLAYER_DOF; j++ )
			{
				ofs<<keyframes[i]->p[j]<<" ";
			}
			ofs<<keyframes[i]->t;
			ofs<<"\n";
		}

		ofs.close();
	}

	void keyboardInit()
	{
		registerKeyUpMethod( 'm', setMoveMode );
		registerKeyUpMethod( 'c', clearFP );
		registerKeyUpMethod( 'r', reset );
		registerKeyUpMethod( 'a', animate );
		registerKeyUpMethod( 'k', setKeyframe );
		registerKeyUpMethod( 'p', printAni );
		registerKeyUpMethod( 'l', loadAni );
	}

	void leftMouseButton( int x, int y, int modifiers )
	{
		oldX = x;
		oldY = y;
		if( mode == MOVE )
		{
			Selection( x, y, false );
		}
		isDragging = true;
	}

	void leftMouseButtonUp( int x, int y, int modifiers )
	{
		isDragging = false;
		if( dragPlane != NULL )
			delete dragPlane;
		if( dragRectPlane != NULL )
			delete dragRectPlane;
		delete pointer;
		pointer = NULL;
		dragPlane = NULL;
		dragRectPlane = NULL;
	}

	void rightMouseButton( int x, int y, int modifiers )
	{
		if( mode == MOVE )
		{
			Selection( x, y, true );
		}
	}

	void Motion( int x, int y, int modifiers )
	{
		if( mode == CAMERA )
		{
			cam.modXAngle( ((float)( oldX - x ))*0.3f );
			cam.modYAngle( ((float)( oldY - y ))*0.3f );
		}
		if( mode == MOVE )
			hasChanged = true;
		oldX = x;
		oldY = y;
	}

	void mouseInit()
	{
		registerMouseMethod( MOTION, Motion );
		registerMouseMethod( LB, leftMouseButton );
		registerMouseMethod( RB, rightMouseButton );
		registerMouseUpMethod( LB, leftMouseButtonUp );
	}


#endif