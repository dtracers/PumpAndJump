package com.musicgame.PumpAndJump;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;

public abstract class Model
{
	public Point p;
	public Point angle;
	public Point scalar;
	public Point gp;
	public Point ga;
	Matrix4 before;

	public Model( Point _p, Point _angle, Point _scale ) { p = _p; angle =  _angle; scalar = _scale;  }

	public void pushTransforms( SpriteBatch sb )
	{
		Matrix4 mv = sb.getTransformMatrix();
		before = new Matrix4( mv );
		
		mv.translate( p.x, p.y, p.z );

		mv.rotate( 1.0f, 0.0f, 0.0f, angle.x );
		mv.rotate( 0.0f, 1.0f, 0.0f, angle.y );
		mv.rotate( 0.0f, 0.0f, 1.0f, angle.z );

		mv.scale( scalar.x, scalar.y, scalar.z );
		
		sb.setTransformMatrix( mv );
	}

	public void translate( float x, float y, float z )
	{
		p.x += x;
		p.y += y;
		p.z += z;
	}
	
	public void rotate( float x_degrees, float y_degrees, float z_degrees )
	{
		angle.x += x_degrees;
		angle.y += y_degrees;
		angle.z += z_degrees;
	}
	
	public void scale( float x_scalar, float y_scalar, float z_scalar )
	{
		scalar.x *= x_scalar;
		scalar.y *= y_scalar;
		scalar.z *= z_scalar;
	}
	
	public void popTransforms( SpriteBatch sb )
	{
		sb.setTransformMatrix( before ); 
	}

	public abstract void display( SpriteBatch sb );
}