package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.musicgame.PumpAndJump.CameraHelp;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;

public class PostGame extends GameThread
{
	static Stage stage;
	ParticleEffect effect;
	static TextureAtlas particle = new TextureAtlas( Gdx.files.internal( "square.txt") );

	int emitterIndex;
	Array<ParticleEmitter> emitters;
	public PostGame()
	{
		stage = new Stage();
		Table table = new Table();
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

	//	Image youwinImage = new Image(new TextureRegion(new Texture(Gdx.files.internal("music_note.png")),0,0,331,78 ) );
		Image youwinImage = new Image(new TextureRegion(new Texture(Gdx.files.internal("youwin.png")),0,0,331,78 ) );
		table.add().expand().fill();
		table.add(youwinImage).expand().fill().colspan(3);
		table.add().expand().fill();
		table.row();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.row();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.row();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add().expand().fill();
		table.row();
		table.add().expand().fill();
		table.add().expand().fill();
		table.add(mainmenuButton).expand().fill().pad(5);
		table.add().expand().fill();
		table.add().expand().fill();

	}
	@Override
	public void switchFrom(GameThread currentThread)
	{
		Gdx.input.setInputProcessor(stage);
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
