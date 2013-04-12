package com.musicgame.PumpAndJump;

import com.musicgame.PumpAndJump.Animation.Animated;
import com.musicgame.PumpAndJump.Util.AnimationUtil;
import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;

public class Player extends GameObject implements Animated{
	
	public PlayerHip hip;
	public boolean changed;
	public Float[] pose;
	public float origY;
	public final int WAMS_PLAYER_DOF = 16;

	Player( Point pos, Point angle )
	{
		hip = new PlayerHip( pos, angle );
		origY = pos.y;

		hip.scale( 5.0f, 5.0f, 5.0f );

		changed = true;

		pose = new Float[ WAMS_PLAYER_DOF ];

		pose[ 0 ] = Float.valueOf( hip.angle.z );

		pose[ 1 ] = Float.valueOf( hip.leftThigh.angle.z );
		pose[ 2 ] = Float.valueOf( hip.leftThigh.shin.angle.z );
		pose[ 3 ] = Float.valueOf( hip.leftThigh.shin.foot.angle.z );
		pose[ 4 ] = Float.valueOf( hip.leftThigh.shin.foot.tuckles.angle.z );

		pose[ 5 ] = Float.valueOf(hip.rightThigh.angle.z );
		pose[ 6 ] = Float.valueOf(hip.rightThigh.shin.angle.z );
		pose[ 7 ] = Float.valueOf(hip.rightThigh.shin.foot.angle.z );
		pose[ 8 ] = Float.valueOf(hip.rightThigh.shin.foot.tuckles.angle.z );

		pose[ 9 ] = Float.valueOf(hip.torso.angle.z );
			
		pose[ 10 ] = Float.valueOf(hip.torso.leftArm.angle.z );
		pose[ 11 ] = Float.valueOf(hip.torso.leftArm.forearm.angle.z );

		pose[ 12 ] = Float.valueOf(hip.torso.rightArm.angle.z );
		pose[ 13 ] = Float.valueOf(hip.torso.rightArm.forearm.angle.z );

		pose[ 14 ] = Float.valueOf(hip.torso.head.angle.z );

		pose[ 15 ] = Float.valueOf(hip.p.y );
	}

	void setPose( float[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			pose[ i ] = a[i];//temp;
		}
		changed = true;
	}

	void setPose( double[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			pose[ i ] = (float)a[i];
		}
		changed = true;
	}

	void getPose( float[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			a[ i ] = pose[ i ];
		}
	}

	void getPose( double[] a )
	{
		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			a[ i ] = ( double ) ( pose[ i ] );
		}
	}

	void update( )
	{
		if( changed )
		{
			if( hip != null )
				hip.update( hip.p, new Point( 0.0f, 0.0f, 1.0f ), new Point( 0.0f, 0.0f, 0.0f ) );
			changed = false;
		}
	}

	void display( SpriteBatch sb )
	{
		hip.display( sb );
	}
	
	@Override
	public void UpdatePose(float[] pose) {
		// TODO Auto-generated method stub
		setPose( pose );
	}
	
	
}

enum Side{ LEFT, RIGHT }; 

class PlayerForearm extends Model
{
	public PlayerForearm( Side a ) 
	{
		super(  new Point( 20.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );

		switch( a )
		{
			case LEFT:
				angle.z = 90.0f; break;
			case RIGHT:
				angle.z = 60.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/

		popTransforms( sb );
	}

	public void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;

		Point local = new Point( 20.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );
	}

}

class PlayerShoulder extends Model
{
	public PlayerForearm forearm;
	public Side side;

	PlayerShoulder( Side a )
	{
		super( new Point( 35.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		side = a;
		
		forearm = new PlayerForearm( a );
		
		switch( a )
		{
			case LEFT:
				angle.z = 135.0f; break;
			case RIGHT:
				angle.z = -135.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );
		
		forearm.display( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/

		popTransforms( sb );
	}

	public void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;

		Point local = new Point( 20.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		//gp = globalPos.add( p );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

		Point newGlobal = globalPos.add( local );

		forearm.update( newGlobal, globalCylinderDir, globalRotation );
	}
}

class PlayerHead extends Model
{
	public PlayerHead() 
	{
		super( new Point( 35.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 15.0f, 2.0f,  0.0f);
			glVertex3f( 15.0f, -2.0f, 0.0f);
		glEnd();*/

		//DrawCircle( 15.0f, 0.0f, 10.0f, 100 );

		popTransforms( sb );
	}

	void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		ga = globalRotation;
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;


		Point local = new Point( 15.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

	}

};

class PlayerTorso extends Model
{
	public PlayerShoulder leftArm;
	public PlayerShoulder rightArm;
	public PlayerHead head;

	public PlayerTorso() 
	{ 
		super( new Point( 0.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 90.0f ),new Point( 1.0f, 1.0f, 1.0f ) );
		
		leftArm = new PlayerShoulder( Side.LEFT );
		rightArm = new PlayerShoulder( Side.RIGHT );
		head = new PlayerHead();
		
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		leftArm.display( sb );

		head.display( sb );
		
		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 35.0f, 2.0f,  0.0f);
			glVertex3f( 35.0f, -2.0f, 0.0f);
		glEnd();*/
		
		rightArm.display( sb );
		
		popTransforms( sb );
	}

	public void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		ga = globalRotation;
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;
		Point local = new Point( 35.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		//gp = globalPos.add( p );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

		Point newGlobal = globalPos.add( local );

		head.update( newGlobal, globalCylinderDir, globalRotation );
		leftArm.update( newGlobal, globalCylinderDir, globalRotation );
		rightArm.update( newGlobal, globalCylinderDir, globalRotation );
	}
}

class PlayerTuckles extends Model
{
	public PlayerTuckles( Side a )
	{
		super( new Point( 9.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 3.0f, -2.0f, 0.0f);
		glEnd();*/

		popTransforms( sb );
	}

	public void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		ga = globalRotation;
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;


		Point local = new Point( 3.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		//gp = globalPos.add( p );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

	}
};

class PlayerFoot extends Model
{
	public PlayerTuckles tuckles;

	public PlayerFoot( Side a )
	{
		super( new Point( 20.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f ,1.0f ) );
		
		tuckles = new PlayerTuckles( a );
		
		switch( a )
		{
			case LEFT:
				angle.z = 90.0f; break;
			case RIGHT:
				angle.z = 90.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 9.0f, 2.0f,  0.0f);
			glVertex3f( 9.0f, -2.0f, 0.0f);
		glEnd();*/

		tuckles.display( sb );

		popTransforms( sb );
	}

	public void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		ga = globalRotation;
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;


		Point local = new Point( 9.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		//gp = globalPos.add( p );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

		Point newGlobal = globalPos.add( local );

		tuckles.update( newGlobal, globalCylinderDir, globalRotation );
	}
}

class PlayerShin extends Model
{
	public PlayerFoot foot;

	PlayerShin( Side a ) 
	{
		super( new Point( 20.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );

		foot = new PlayerFoot( a );

		switch( a )
		{
			case LEFT:
				angle.z = -60.0f; break;
			case RIGHT:
				angle.z = -45.0f; break;
		}
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/

		foot.display( sb );

		popTransforms( sb );
	}

	void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		ga = globalRotation;
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;
		

		Point local = new Point( 20.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		//gp = globalPos.add( p );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

		Point newGlobal = globalPos.add( local );

		foot.update( newGlobal, globalCylinderDir, globalRotation );
	}

}

class PlayerThigh extends Model
{
	public PlayerShin shin;
	public Side side;

	public PlayerThigh( Side a )
	{ 
		super( new Point( 0.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
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

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		/*glBegin(GL_POLYGON);
			glVertex3f( 0.0f, -2.0f, 0.0f);
			glVertex3f(	0.0f, 2.0f, 0.0f);
			glVertex3f( 20.0f, 2.0f,  0.0f);
			glVertex3f( 20.0f, -2.0f, 0.0f);
		glEnd();*/

		shin.display( sb );

		popTransforms( sb );
	}

	void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		ga = globalRotation;
		globalRotation = globalRotation.add( angle );
		ga = globalRotation;
		

		Point local = new Point( 20.0f, 0.0f, 0.0f );

		local = local.scale( 5.0f );

		//gp = globalPos.add( p );
		
		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

		Point newGlobal = globalPos.add( local );

		shin.update( newGlobal, globalCylinderDir, globalRotation );
	}

};

class PlayerHip extends Model
{
	public PlayerTorso torso;
	public PlayerThigh leftThigh;
	public PlayerThigh rightThigh;

	public PlayerHip( Point pos, Point angle ) 
	{
		super( pos, angle, new Point( 1.0f, 1.0f, 1.0f ) );
		torso = new PlayerTorso();
		leftThigh = new PlayerThigh( Side.LEFT );
		rightThigh = new PlayerThigh( Side.RIGHT );
	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		leftThigh.display( sb );
		torso.display( sb );
		rightThigh.display( sb );

		popTransforms( sb );
	}

	public void update( Point globalPos, Point globalCylinderDir, Point globalRotation  )
	{
		gp = globalPos;
		
		ga = globalRotation;
		
		globalRotation = globalRotation.add( angle );

		ga = globalRotation;
		
		Point local = new Point( 0.0f, 0.0f, 0.0f );

		local = AnimationUtil.RotateAroundXAxis( local, globalRotation.x );
		local = AnimationUtil.RotateAroundYAxis( local, globalRotation.y );
		local = AnimationUtil.RotateAroundZAxis( local, globalRotation.z );

		Point newGlobal = globalPos;

		leftThigh.update( newGlobal, globalCylinderDir, globalRotation );
		rightThigh.update( newGlobal, globalCylinderDir, globalRotation );
		torso.update( newGlobal, globalCylinderDir, globalRotation );
	}
};
