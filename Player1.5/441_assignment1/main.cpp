#include <GL/glut.h>
#include <stdlib.h>
#include <iostream>
#include "ui.h"
#include "model.h"
#include "util.h"
#include "camera.h"
#include "uimethods.cpp"
#include "Ray.h"
#include "Cylinder.h"
#include "Plane.h"
#include "Intersectable.h"
#include "Sphere.h"

using namespace WAMS;
using namespace std;

void changeSize(int w, int h) {
	if (h == 0)
		h = 1;
	float ratio =  w * 1.0f / h;
	height = h;
	width = w;

	glMatrixMode(GL_PROJECTION);

	glLoadIdentity();

	glViewport(0, 0, w, h);

	glOrtho( 0.0, (double)w , 0.0, (double)h, -10.0f, 500.0f );

	glMatrixMode(GL_MODELVIEW);
}

void init()
{

	utilInit();
	glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
	glClearDepth(1.0f);

	float light0Pos[] = { 100.0f, 100.0f, 0.0f, 0.0f };
	float light0Diffuse[] = {1.0f, 1.0f, 1.0f, 1.0f}; 

	glShadeModel( GL_SMOOTH );

	glLightfv(GL_LIGHT0, GL_DIFFUSE, light0Diffuse);
	glLightfv( GL_LIGHT0, GL_POSITION, light0Pos );

	glEnable(GL_COLOR_MATERIAL);

	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	glMatrixMode(GL_MODELVIEW);
	UIInit();
}

//------------------------------------------------------------------------------------------------
//		begin Display Callback
//------------------------------------------------------------------------------------------------
void display1(void)
{
	UIDisplay();
}
//------------------------------------------------------------------------------------------------
//		end Display Callback
//------------------------------------------------------------------------------------------------

//------------------------------------------------------------------------------------------------
//		begin Timer Callback
//------------------------------------------------------------------------------------------------
void timer( int value )
{
	glutTimerFunc( 33, timer, timerCount );
	UITimer( value );
}
//------------------------------------------------------------------------------------------------
//		end Timer Callback
//------------------------------------------------------------------------------------------------

//------------------------------------------------------------------------------------------------
//		begin Mouse Callbacks
//------------------------------------------------------------------------------------------------
void mouse(int button, int state, int x, int y)
{
	int modifiers = glutGetModifiers();
	switch( button )
	{
		case 0: mouse( LB, state, x, y, modifiers ); break;
		case 1: mouse( MB, state, x, y, modifiers ); break;
		case 2: mouse( RB, state, x, y, modifiers ); break;
	}
}
void mouseWheel( int wheel, int direction, int x, int y )
{
	int modifiers = glutGetModifiers();
	switch( direction )
	{
		case -1: mouse( SCROLLDOWN, 0, x, y, modifiers ); break;
		case 1: mouse( SCROLLUP, 0, x, y, modifiers ); break;
	}
}
void motion( int x, int y )
{
	mouse( MOTION, 0, x, y, 0 );
}
//------------------------------------------------------------------------------------------------
//		end Mouse Callbacks
//------------------------------------------------------------------------------------------------

//------------------------------------------------------------------------------------------------
//		begin Keyboard Callbacks
//------------------------------------------------------------------------------------------------
void keyboard( unsigned char key, int x, int y )
{
	int modifiers = glutGetModifiers();
	keyboard( key, x, y, modifiers );
}
void keyboardUp( unsigned char key, int x, int y )
{
	int modifiers = glutGetModifiers();
	keyboardUp( key, x, y, modifiers );
}
//------------------------------------------------------------------------------------------------
//		end Keyboard Callbacks
//------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------------------
//		Main Method
//------------------------------------------------------------------------------------------------

int main(int argc, char** argv)
{
	glutInit(&argc, argv);
	glutInitDisplayMode ( GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH );
	glutInitWindowSize  (800, 600 );
	glutInitWindowPosition (0, 0);
	glutCreateWindow( "Christopher A. Trask - Assignment 6" );
	init ();
	glutDisplayFunc(display1);
	glutMouseFunc(mouse);
	glutTimerFunc( 33, timer, timerCount );
	//glutMouseWheelFunc(mouseWheel);QQ at no scroll wheel guess i'll have to bind it to something else instead 
	glutMotionFunc(motion);
	glutKeyboardFunc(keyboard);
	glutKeyboardUpFunc(keyboardUp);
	glutReshapeFunc(changeSize);
	glutMainLoop();
	return 0;
}