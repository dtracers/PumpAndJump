package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.musicgame.PumpAndJump.CameraHelp;
import com.musicgame.PumpAndJump.Util.TableUtil;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;

public class PostGame extends GameThread
{
	Stage stage;
	Table table;
	ParticleEffect effect;
	static TextureAtlas particle = new TextureAtlas( Gdx.files.internal( "square.txt") );
	BitmapFont font = new BitmapFont();
	int emitterIndex;
	Array<ParticleEmitter> emitters;

	String score;
	boolean alive = true;
	Label healthMessage;
	Label scoreMessage;
	public PostGame()
	{
		stage = new Stage();
		table = new Table();
		stage.addActor(table);

		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
		if(uiSkin == null)
		{
	        FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
	        uiSkin = new Skin( skinFile );
		}
		final TextButton mainmenuButton = new TextButton("Main Menu", uiSkin);
		mainmenuButton.addListener(
				new ChangeListener()
				{
					public void changed(ChangeEvent event, Actor actor)
					{
						PumpAndJump.switchThread(ThreadName.PreGame, PostGame.this);
					}
				});

		table.setFillParent(true);
		table.setSkin(uiSkin);

		healthMessage = new Label("I AM A SET OF EMPTY TEXT THAT DOES STUFF",uiSkin);
		healthMessage.setColor(Color.BLACK);
		scoreMessage = new Label("I AM A SET OF EMPTY TEXT THAT DOES STUFF",uiSkin);
		scoreMessage.setColor(Color.BLACK);

		TableUtil.addToCenter(table, healthMessage, 3).expand().fill().colspan(3).center();
		table.row();

		TableUtil.addToCenter(table, scoreMessage, 3).expand().fill().colspan(3).center();

		for(int k =0;k<2;k++)
		{
			table.row();
			TableUtil.addEmptyRow(table, 5);
		}

		table.row();
		TableUtil.addToCenter(table, mainmenuButton, 5).expand().fill().colspan(1).center();

	}
	@Override
	public void switchFrom(GameThread currentThread)
	{
		Gdx.input.setInputProcessor(stage);
		if(currentThread instanceof RunningGame)
		{
			double[] statistics = ((RunningGame)currentThread).getPostGameScore();
			alive = ((RunningGame)currentThread).isAlive();
			if(alive)
				healthMessage.setText("You Survived with "+statistics[0]+" left ");
			else
				healthMessage.setText("You Died!");
			scoreMessage.setText("Final Score: "+((int)statistics[1])+" Max Score During Run: "+((int)statistics[2]));
			table.pack();
		}
	}

	@Override
	public void addFrom(GameThread currentThread) {
	}

	@Override
	public void removeFrom(GameThread currentThread) {
	}

	@Override
	public void unpause() {
	}

	@Override
	public ThreadName getThreadName()
	{
		return ThreadName.PostGame;
	}

	@Override
	public void render(float delta)
	{
		stage.act(Math.min(delta, 1 / 30f));
		stage.draw();
	}

	@Override
	public void repause() {
	}

}
