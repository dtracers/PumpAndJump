package com.musicgame.PumpAndJump.Util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TextureMapping 
{
	public static TextureMapping textures;
	public Map< String, Texture > nameToTexture;
	public TextureAtlas textureAtlas;
	
	public TextureMapping()
	{
		nameToTexture = new HashMap< String, Texture >();
		textureAtlas = new TextureAtlas( "playerSprite.txt" );
	}
	
	public static void constructStaticMapping()
	{
		textures = new TextureMapping();
		
	}
	
	public int addTexture( String textureStr )
	{
		Texture tex = new Texture( Gdx.files.internal( textureStr ) );
		
		if( nameToTexture.get( textureStr ) == null )
		{
			nameToTexture.put( textureStr, tex  );
			return 1;
		}
		
		return 0;
	}
	
	public static int staticAddTexture( String textureStr )
	{
		if( textures != null )
			return textures.addTexture( textureStr );
		return -1;
	}
	
	public static Texture staticGet( String textureStr )
	{
		if( textures != null )
		{
			Texture t = textures.get( textureStr );
			if( t != null )
			{
				return t;
			}
			
			staticAddTexture( textureStr );
			
			return textures.get( textureStr );
		}
		return null;
	}
	
	public static Sprite staticGetSprite( String spriteName )
	{

		if( textures != null )
		{
			Sprite s = textures.getSprite( spriteName );
			if( s != null )
			{
				return s;
			}
		}
		return null;
	}
	
	public Texture get( String textureStr )
	{
		if( nameToTexture.get( textureStr ) != null )
			return nameToTexture.get( textureStr );
		return null;
	}
	
	public Sprite getSprite( String spriteName )
	{
		return textureAtlas.createSprite( spriteName );
	}
}
