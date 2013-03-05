#include "point.h"
#include "model.h"
#include "util.h"

#ifndef WAMS_PLAYER
#define WAMS_PLAYER

#define WAMS_PLAYER_DOF 15

namespace WAMS
{ 

	enum Side{ LEFT = -1, RIGHT = 1 };

	void DrawCircle(float cx, float cy, float r, int num_segments) 
	{ 
		glBegin(GL_POLYGON); 
		for(int ii = 0; ii < num_segments; ii++) 
		{ 
			float theta = 2.0f * 3.1415926f * float(ii) / float(num_segments);//get the current angle 

			float x = r * cosf(theta);//calculate the x component 
			float y = r * sinf(theta);//calculate the y component 

			glVertex2f(x + cx, y + cy);//output vertex 

		} 
		glEnd(); 
	}

	class PlayerForearm: public Model
	{
		public:
			PlayerForearm( Side a ) : Model(  point( 20.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) )
			{
				nameHandler->add( this );

				cout<<"forearm"<<(nameHandler->modelToName[ this ])<<"\n";

				switch( a )
				{
					case LEFT:
						angle.z = 90.0f; break;
					case RIGHT:
						angle.z = 60.0f; break;
				}
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 20.0f, 2.0f,  0.0f);
					glVertex3f( 20.0f, -2.0f, 0.0f);
				glEnd();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;

				point local = point( 20.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );
			}

	};

	class PlayerShoulder: public Model
	{
		public:
			PlayerForearm* forearm;
			Side side;

			PlayerShoulder( Side a ) : Model( point( 35.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) )
			{
				side = a;
				nameHandler->add( this );

				cout<<"Shoulder"<<(nameHandler->modelToName[ this ])<<"\n";
				
				forearm = new PlayerForearm( a );
				
				switch( a )
				{
					case LEFT:
						angle.z = 135.0f; break;
					case RIGHT:
						angle.z = -135.0f; break;
				}
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();

				switch( side )
				{
					case LEFT:
						glColor4f( 0.6f, 0.3f, 0.6f, 1.0f ); break;
					case RIGHT:
						glColor4f( 0.3f, 0.3f, 0.7f, 1.0f );  break;
				}

				forearm->display();

				nameHandler->LoadName( this );

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 20.0f, 2.0f,  0.0f);
					glVertex3f( 20.0f, -2.0f, 0.0f);
				glEnd();

				glColor4f( 0.2f, 0.2f, 0.2f, 1.0f );

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;

				point local = point( 20.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				//gp = globalPos.add( p );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );

				forearm->update( newGlobal, globalCylinderDir, globalRotation );
			}
	};

	class PlayerHead: public Model
	{
		public:
			PlayerHead() : Model( point( 35.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) )
			{
				nameHandler->add( this );
				cout<<"Head"<<(nameHandler->modelToName[ this ])<<"\n";
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 15.0f, 2.0f,  0.0f);
					glVertex3f( 15.0f, -2.0f, 0.0f);
				glEnd();

				DrawCircle( 15.0f, 0.0f, 10.0f, 100 );

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				ga = globalRotation;
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;


				point local = point( 15.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );

			}

	};

	class PlayerTorso: public Model
	{
		public:
			PlayerShoulder* leftArm;
			PlayerShoulder* rightArm;
			PlayerHead* head;

			PlayerTorso() : Model( point( 0.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 90.0f ), point( 1.0f, 1.0f, 1.0f ) )
			{
				nameHandler->add( this );
				cout<<"torso"<<(nameHandler->modelToName[ this ])<<"\n";
				leftArm = new PlayerShoulder( LEFT );
				rightArm = new PlayerShoulder( RIGHT );
				head = new PlayerHead();
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();

				leftArm->display();

				head->display();

				nameHandler->LoadName( this );
				
				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 35.0f, 2.0f,  0.0f);
					glVertex3f( 35.0f, -2.0f, 0.0f);
				glEnd();
				
				rightArm->display();
				
				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				ga = globalRotation;
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;
				point local = point( 35.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				//gp = globalPos.add( p );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );

				head->update( newGlobal, globalCylinderDir, globalRotation );
				leftArm->update( newGlobal, globalCylinderDir, globalRotation );
				rightArm->update( newGlobal, globalCylinderDir, globalRotation );
			}
	};

	class PlayerTuckles: public Model
	{
		public:
			PlayerTuckles( Side a ): Model( point( 9.0f, 0.0f, 0.0f ), point( 0.0f, 0.0, 0.0f ), point( 1.0f, 1.0f, 1.0f ) )
			{
				nameHandler->add( this );
				cout<<"Tuckles"<<(nameHandler->modelToName[ this ])<<"\n";
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 3.0f, -2.0f, 0.0f);
				glEnd();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				ga = globalRotation;
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;


				point local = point( 3.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				//gp = globalPos.add( p );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );

			}
	};

	class PlayerFoot: public Model
	{
		public:
			PlayerTuckles* tuckles;

			PlayerFoot( Side a ): Model( point( 20.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f ,1.0f ) )
			{
				nameHandler->add( this );
				cout<<"Foot"<<(nameHandler->modelToName[ this ])<<"\n";
				tuckles = new PlayerTuckles( a );
				switch( a )
				{
					case LEFT:
						angle.z = 90.0f; break;
					case RIGHT:
						angle.z = 90.0f; break;
				}
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 9.0f, 2.0f,  0.0f);
					glVertex3f( 9.0f, -2.0f, 0.0f);
				glEnd();

				tuckles->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{

				gp = globalPos;
				ga = globalRotation;
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;


				point local = point( 9.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				//gp = globalPos.add( p );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );

				tuckles->update( newGlobal, globalCylinderDir, globalRotation );
			}
	};

	class PlayerShin: public Model
	{
		public:
			PlayerFoot* foot;

			PlayerShin( Side a ): Model( point( 20.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) )
			{
				nameHandler->add( this );
				cout<<"Shin"<<(nameHandler->modelToName[ this ])<<"\n";
				foot = new PlayerFoot( a );

				switch( a )
				{
					case LEFT:
						angle.z = -60.0f; break;
					case RIGHT:
						angle.z = -45.0f; break;
				}
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 20.0f, 2.0f,  0.0f);
					glVertex3f( 20.0f, -2.0f, 0.0f);
				glEnd();

				foot->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				ga = globalRotation;
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;
				

				point local = point( 20.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				//gp = globalPos.add( p );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );

				foot->update( newGlobal, globalCylinderDir, globalRotation );
			}

	};

	class PlayerThigh: public Model
	{
		public:
			PlayerShin* shin;
			Side side;

			PlayerThigh( Side a ): Model( point( 0.0f, 0.0f, 0.0f ), point( 0.0f, 0.0f, 0.0f ), point( 1.0f, 1.0f, 1.0f ) )
			{
				nameHandler->add( this );
				cout<<"Thigh"<<(nameHandler->modelToName[ this ])<<"\n";
				shin = new PlayerShin( a ); 

				side = a;

				switch( a )
				{
					case LEFT:
						angle.z = -45.0f; break;
					case RIGHT:
						angle.z = -90.0f; break;
				}
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				nameHandler->LoadName( this );

				switch( side )
				{
					case LEFT:
						glColor4f( 0.6f, 0.3f, 0.6f, 1.0f ); break;
					case RIGHT:
						glColor4f( 0.3f, 0.3f, 0.7f, 1.0f );  break;
				}

				glBegin(GL_POLYGON);
					glVertex3f( 0.0f, -2.0f, 0.0f);
					glVertex3f(	0.0f, 2.0f, 0.0f);
					glVertex3f( 20.0f, 2.0f,  0.0f);
					glVertex3f( 20.0f, -2.0f, 0.0f);
				glEnd();

				shin->display();

				glColor4f( 0.2f, 0.2f, 0.2f, 1.0f );

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				ga = globalRotation;
				globalRotation = globalRotation.add( angle );
				ga = globalRotation;
				

				point local = point( 20.0f, 0.0f, 0.0f );

				local = local.scale( 5.0f );

				//gp = globalPos.add( p );
				
				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos.add( local );

				shin->update( newGlobal, globalCylinderDir, globalRotation );
			}

	};

	class PlayerHip: public Model
	{
		public:
			PlayerTorso* torso;
			PlayerThigh* leftThigh;
			PlayerThigh* rightThigh;

			PlayerHip( point pos, point angle ) : Model( pos, angle, point( 1.0f, 1.0f, 1.0f ) )
			{
				nameHandler->add( this );
				cout<<"Hip"<<(nameHandler->modelToName[ this ])<<"\n";
				torso = new PlayerTorso();
				leftThigh = new PlayerThigh( LEFT );
				rightThigh = new PlayerThigh( RIGHT );
			}

			void display()
			{
				glPushMatrix();
				addAllTransforms();
				//nameHandler->LoadName( this );

				glColor4f( 0.2f, 0.2f, 0.2f, 1.0f );

				leftThigh->display();
				torso->display();
				rightThigh->display();

				glPopMatrix();
			}

			void update( point globalPos, point globalCylinderDir, point globalRotation  )
			{
				gp = globalPos;
				
				ga = globalRotation;
				
				globalRotation = globalRotation.add( angle );

				ga = globalRotation;
				
				point local = point( 0.0f, 0.0f, 0.0f );

				local = RotateAroundXAxis( local, globalRotation.x );
				local = RotateAroundYAxis( local, globalRotation.y );
				local = RotateAroundZAxis( local, globalRotation.z );

				point newGlobal = globalPos;

				leftThigh->update( newGlobal, globalCylinderDir, globalRotation );
				rightThigh->update( newGlobal, globalCylinderDir, globalRotation );
				torso->update( newGlobal, globalCylinderDir, globalRotation );
			}
	};

	class Player
	{
		public:
			PlayerHip* hip;
			bool changed;
			float** pose;

			Player( point pos, point angle )
			{

				hip = new PlayerHip( pos, angle );

				hip->scale( 5.0, 5.0, 5.0 );

				changed = true;

				pose = new float*[ WAMS_PLAYER_DOF ];

				pose[ 0 ] = &hip->angle.z;

				pose[ 1 ] = &hip->leftThigh->angle.z;
				pose[ 2 ] = &hip->leftThigh->shin->angle.z;
				pose[ 3 ] = &hip->leftThigh->shin->foot->angle.z;
				pose[ 4 ] = &hip->leftThigh->shin->foot->tuckles->angle.z;

				pose[ 5 ] = &hip->rightThigh->angle.z;
				pose[ 6 ] = &hip->rightThigh->shin->angle.z;
				pose[ 7 ] = &hip->rightThigh->shin->foot->angle.z;
				pose[ 8 ] = &hip->rightThigh->shin->foot->tuckles->angle.z;

				pose[ 9 ] = &hip->torso->angle.z;
					
				pose[ 10 ] = &hip->torso->leftArm->angle.z;
				pose[ 11 ] = &hip->torso->leftArm->forearm->angle.z;

				pose[ 12 ] = &hip->torso->rightArm->angle.z;
				pose[ 13 ] = &hip->torso->rightArm->forearm->angle.z;

				pose[ 14 ] = &hip->torso->head->angle.z;
			}

			void setPose( float* a )
			{
				for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
				{
					/*float temp = a[i];
					if( temp <= 0 || temp > 360.0 )
					{
						temp = temp/360.0f - (float)(((int)temp)/360);
						temp = abs( temp );
					}*/
					*( pose[ i ] ) = a[i];//temp;
				}
				changed = true;
			}

			void setPose( double* a )
			{
				for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
				{
					*( pose[ i ] ) = (float)a[i];
				}
				changed = true;
			}

			void getPose( float* a )
			{
				for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
				{
					a[ i ] = *( pose[ i ] );
				}
			}

			void getPose( double* a )
			{
				for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
				{
					a[ i ] = (double ) ( *( pose[ i ] ) );
				}
			}

			void update( )
			{
				if( changed )
				{
					if( hip != NULL )
						hip->update( hip->p, point( 0.0f, 0.0f, 1.0f ), point( 0.0f, 0.0f, 0.0f ) );
					changed = false;
				}
			}

			void display()
			{
				hip->display();

				glLoadName( 0 );

				glColor4f( 0.7f, 0.7f, 0.7f, 1.0f );

				DrawCircle( hip->gp.x, hip->gp.y, 3.0f, 10.0f );

				glColor4f( 0.7f, 0.2f, 0.7f, 1.0f );

				DrawCircle( hip->leftThigh->gp.x, hip->leftThigh->gp.y, 3.0f, 10.0f ); 
				DrawCircle( hip->leftThigh->shin->gp.x, hip->leftThigh->shin->gp.y, 3.0f, 10.0f ); 

				DrawCircle( hip->rightThigh->gp.x, hip->rightThigh->gp.y, 3.0f, 10.0f ); 
				DrawCircle( hip->rightThigh->shin->gp.x, hip->rightThigh->shin->gp.y, 3.0f, 10.0f ); 

				glColor4f( 0.5f, 0.2f, 0.7f, 1.0f );

				DrawCircle( hip->leftThigh->shin->foot->gp.x, hip->leftThigh->shin->foot->gp.y, 3.0f, 10.0f ); 
				DrawCircle( hip->leftThigh->shin->foot->tuckles->gp.x, hip->leftThigh->shin->foot->tuckles->gp.y, 3.0f, 10.0f ); 

				DrawCircle( hip->rightThigh->shin->foot->gp.x, hip->rightThigh->shin->foot->gp.y, 3.0f, 10.0f ); 
				DrawCircle( hip->rightThigh->shin->foot->tuckles->gp.x, hip->rightThigh->shin->foot->tuckles->gp.y, 3.0f, 10.0f ); 

				glColor4f( 0.5f, 0.5f, 0.7f, 1.0f );

				DrawCircle( hip->torso->leftArm->gp.x, hip->torso->leftArm->gp.y, 3.0f, 10.0f );
				DrawCircle( hip->torso->rightArm->gp.x, hip->torso->rightArm->gp.y, 3.0f, 10.0f );

				glColor4f( 0.9f, 0.9f, 0.1f, 1.0f );

				DrawCircle( hip->torso->leftArm->forearm->gp.x, hip->torso->leftArm->forearm->gp.y, 3.0f, 10.0f );
				DrawCircle( hip->torso->rightArm->forearm->gp.x, hip->torso->rightArm->forearm->gp.y, 3.0f, 10.0f );

				glColor4f( 0.7f, 0.5f, 0.3f, 1.0f );

				DrawCircle( hip->torso->head->gp.x, hip->torso->head->gp.y, 3.0f, 10.0f );

				//hip->torso->head->gp.print();
			}
	};
};

#endif