package com.musicgame.PumpAndJump;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.musicgame.PumpAndJump.Util.AnimationUtil.Point;
import com.musicgame.PumpAndJump.Util.IntersectionUtil;




public class GameObject extends Model
{
	
	public GameObject()
	{
		super( new Point( 0.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
	}

	public GameObject(Polygon hull)
	{
		super( new Point( 0.0f, 0.0f, 0.0f ), new Point( 0.0f, 0.0f, 0.0f ), new Point( 1.0f, 1.0f, 1.0f ) );
		this.hull = hull;
	}

	public Polygon getHull()
	{
		return hull;
	}

	/*public boolean intersects( Polygon poly )
	{
		if( hull == null )
			return false;
		return Intersector.overlapConvexPolygons( hull, poly );
	}*/

	public boolean intersects( GameObject gmObj )
	{
		//if( hull == null )
			//return false;
		return intersects( gmObj.getHull() );
	}

	public void draw( SpriteBatch sb )
	{
		display( sb );
	} 

	public void draw( ShapeRenderer sr )
	{
		 sr.begin( ShapeRenderer.ShapeType.Line );
		 float v[] = hull.getVertices();
		 if( v.length > 1 )
		 {
			 float lastX = -1;
			 float lastY = -1;
			 boolean firstRound = true;
			 for( int i = 0; i < v.length/2; i++ )
			 {
				 float newX = v[i*2];
				 float newY = v[i*2+1];

				 if( !firstRound )
				 {
					 sr.line( lastX, lastY, newX, newY );
				 }

				 lastX = newX;
				 lastY = newY;
			 }
			 sr.line( lastX, lastY, v[0], v[1] );
		 }
		 sr.end();
	}

	public void update( Matrix4 before, float delta )
	{
		update( before );
	}

	@Override
	public void display(SpriteBatch sb) {
		pushTransforms( sb );

		drawSprite( sb );

		popTransforms( sb );
	}
}
