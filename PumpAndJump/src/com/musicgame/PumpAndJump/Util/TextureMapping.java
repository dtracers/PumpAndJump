package com.musicgame.PumpAndJump.Util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureMapping 
{
	public static TextureMapping textures;
	public Map< String, Texture > nameToTexture;
	
	public TextureMapping()
	{
		nameToTexture = new HashMap< String, Texture >();
	}
	
	public static void constructStaticMapping()
	{
		textures = new TextureMapping();
	}
	
	public boolean addTexture( String textureStr )
	{
		Texture tex = new Texture( Gdx.files.internal( textureStr ) );
		
		if( nameToTexture.get( textureStr ) == null )
		{
			nameToTexture.put( textureStr, tex  );
			return true;
		}
		
		return false;
	}
	
	public Texture get( String textureStr )
	{
		return nameToTexture.get( textureStr );
	}
}
