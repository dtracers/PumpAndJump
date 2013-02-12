package com.musicgame.Demo;

/*import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;*/
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.Game;
import com.musicgame.Demo.GameScreen;

public class PumpAndJump extends Game {
	Screen gs;
   
   @Override
   public void create() {
	   gs= new GameScreen();
	   setScreen(gs);
	   
   }
  }