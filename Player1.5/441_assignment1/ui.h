#ifdef _WIN32
	#include <Windows.h>
#endif

#include <GL/glut.h>
#include <map>
#include <list>

#ifndef _WAMS_UI_H
#define _WAMS_UI_H

	using namespace std;

	enum MouseInputType{ LB, RB, MB, SCROLLUP, SCROLLDOWN, MOTION };
	map< unsigned char, void(*)(int,int,int) > keysMethods;
	map< unsigned char, bool > keysPressed;
	map< unsigned char, void(*)(int,int,int) > keysUpMethods;
	map< MouseInputType, void(*)(int,int,int) > mouseMethods;
	map< MouseInputType, bool > mousePressed;
	map< MouseInputType, void(*)(int,int,int) > mouseUpMethods;
	list< void(*)( unsigned int ) > updateMethods;
	int timerCount;
	unsigned int oldTime;

	void registerUpdateMethod( void(*func)( unsigned int ) )
	{
		updateMethods.push_back( func );
	}
	void registerUpdateMethodFront( void(*func)( unsigned int ) )
	{
		updateMethods.push_front( func );
	}

	void registerKeyMethod( unsigned char c, void(*func)(int,int,int) )
	{
		c = tolower(c);
		keysMethods[c] = func;
	}
	void registerKeyUpMethod( unsigned char c, void(*func)(int,int,int) )
	{
		c = tolower(c);
		keysUpMethods[c] = func;
	}

	void registerMouseMethod( MouseInputType mouseInput, void(*func)(int,int,int) )
	{
		mouseMethods[ mouseInput ] = func; 
	}
	void registerMouseUpMethod( MouseInputType mouseInput, void(*func)(int,int,int) )
	{
		mouseUpMethods[ mouseInput ] = func;
	}

	bool isPressed( unsigned char c )
	{
		return keysPressed[c];
	}
	bool isPressed( MouseInputType mouseInput )
	{
		return mousePressed[ mouseInput ];
	}

	void keyboardInit();
	void mouseInit();
	void updateInit();
	void UIMethodsInit();

	void UIInit()
	{
		#ifdef _WIN32
			oldTime = GetTickCount(); // note this is not portable
		#endif

		UIMethodsInit();
		updateInit();
		keyboardInit();
		mouseInit();
	}

	void display();

	void UIDisplay()
	{
		display();
		glutSwapBuffers();
	}

	void UIUpdate( unsigned int changeInTime )
	{
		for( list< void(*)( unsigned int ) >::iterator i = updateMethods.begin(); i != updateMethods.end(); i++ )
		{
			void (*function)( unsigned int );
			function = (*i);
			if( function != NULL )
				function( changeInTime );
		}
	}

#ifdef _WIN32
	void UITimer( int value )
	{
		unsigned int newTime = GetTickCount();// note this is not portable
		unsigned int changeInTime = newTime - oldTime;
		oldTime = newTime;
		// note value not actually used this expression will always remain true 
		// i even forgot what this was for
		if( value%INT_MAX == timerCount%INT_MAX )// TRUE
		{
			UIUpdate( changeInTime );
			glutPostRedisplay();
		}
	}
#else
	void UITimer( int value )
	{
		// note value not actually used this expression will always remain true 
		// i even forgot what this was for
		if( value%INT_MAX == timerCount%INT_MAX )// TRUE
		{
			UIUpdate( changeInTime );
			glutPostRedisplay();
		}
	}
#endif

	void keyboard( char c, int x, int y, int modifier )
	{
		c = tolower( c );

		keysPressed[c] = true;

		void (*function)( int,int,int );
		function = keysMethods[ c ];

		if( function != NULL )
			function( x, y, modifier );
	}

	void keyboardUp( char c, int x, int y, int modifier )
	{
		c = tolower( c );

		if( keysPressed[c] )
		{
			keysPressed[c] = false;

			void (*function)( int,int,int );
			function = keysUpMethods[c];

			if( function != NULL )
				function( x, y, modifier );
		}
	}

	void mouse( MouseInputType mouseInput, int state, int x, int y, int modifier )
	{
		if( mouseInput == MOTION || 
			mouseInput == SCROLLDOWN ||
			mouseInput == SCROLLUP )
		{
			void (*function)( int, int, int );
			function = mouseMethods[ mouseInput ];

			if( function != NULL )
				function( x, y, modifier );
		}
		else if( state == GLUT_DOWN )
		{
			mousePressed[ mouseInput] = true; 

			void (*function)( int, int, int );
			function = mouseMethods[ mouseInput ];

			if( function != NULL )
				function( x, y, modifier );
		}
		else if( state == GLUT_UP && mousePressed[ mouseInput ] )
		{
			mousePressed[ mouseInput ] = false;

			void (*function)( int, int , int );
			function = mouseUpMethods[ mouseInput ];

			if( function != NULL )
				function( x, y, modifier );
		}
	}


#endif