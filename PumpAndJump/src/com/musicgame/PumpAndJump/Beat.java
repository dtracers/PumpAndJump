package com.musicgame.PumpAndJump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;

public class Beat extends Obstacle{
	
	ParticleEffect effect;
	static TextureAtlas particle = new TextureAtlas( Gdx.files.internal( "square.txt") );
    
	public Beat(float startTime){
		super( startTime, startTime+.01f, 45.0f, 10.0f );
		image.setColor( 1.0f, 0.0f, 1.0f, 1.0f );
		
	}
	
	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		drawSprite( sb );
		if( effect != null )
		{
			effect.draw( sb );
		}

		popTransforms( sb );
	}
	
	public void done()
	{
		effect = null;
	}
	
	public void update( Matrix4 m, float delta )
	{
		super.update( m, delta );
		if( effect != null )
			effect.update( delta );
	}
	
	public void Impacted()
	{
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("fireworks"), 
	            particle );
		effect.setPosition( p.x, p.y );
		effect.start();
	}
}
