package com.musicgame.PumpAndJump.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.musicgame.PumpAndJump.CameraHelp;
import com.musicgame.PumpAndJump.game.gameStates.RunningGame;

public class Beat extends Obstacle{

	static ParticleEffect effect = null;
	float tempo;
	boolean triggered = false;
	static TextureAtlas particle = new TextureAtlas( Gdx.files.internal( "square.txt" ) );

	public Beat(float startTime){
		super( startTime, startTime+.01f, 55.0f, 10.0f );
		image.setColor( 1.0f, 0.0f, 1.0f, 1.0f );
		if(effect == null)
		{
			effect = new ParticleEffect();
			effect.load(Gdx.files.internal("fireworks"),
		            particle );
		}

	}

	public void display( SpriteBatch sb )
	{
		pushTransforms( sb );

		drawSprite( sb );

		sb.setTransformMatrix( getModelView( new Matrix4() ) );
		if( effect != null && !effect.isComplete() )
		{
			effect.draw( sb );
		}

		popTransforms( sb );

	}

	public void done()
	{

	}

	public void update( Matrix4 m, float delta )
	{
		super.update( m, delta );
		if( effect != null )
			effect.update( delta*tempo );
	}

	public void Impacted( float tempo )
	{
		if( !triggered )
		{
			effect.reset();
			effect.setPosition( CameraHelp.virtualWidth/2.0f, 0.0f );
			this.tempo = tempo/60.0f/2.0f;
			RunningGame.score++;
		}
		//effect.start();
	}
}
