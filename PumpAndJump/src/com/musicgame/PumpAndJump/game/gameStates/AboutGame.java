package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;

/**
 * It happens before the game starts
 * @author gigemjt
 *
 */
public class AboutGame extends GameThread
{

	Skin uiSkin;
	Stage stage;
	SpriteBatch batch;

	public AboutGame()
	{
		batch = new SpriteBatch();
		stage = new Stage();
        FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
        uiSkin = new Skin( skinFile );
        Table table = new Table();
		stage.addActor(table);
		table.setFillParent(true);

		Table scrolltable = new Table();
		scrolltable.setFillParent(true);
		final ScrollPane scroll = new ScrollPane(scrolltable, uiSkin);
		final Label infoText = new Label("A side scroller game that generates levels from music. New line tester. Made in the labs at Texas A&M.", uiSkin);
		infoText.setWrap(true);
		scrolltable.add(infoText).size(400,150);


		final TextButton backButton = new TextButton("Back", uiSkin);
		backButton.addListener(
					new ChangeListener()
					{
						public void changed(ChangeEvent event, Actor actor)
						{
							PumpAndJump.switchThread(ThreadName.PreGame, AboutGame.this);
						}
					});

		table.add(scroll).size(450,200);
		table.row().space(10).padBottom(10);
		table.add(backButton).size(250,50).pad(5);
	}

	@Override
    public void render(float delta)
	{
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(delta, 1 / 30f));
		stage.draw();
		//Table.drawDebug(stage);
    }

	@Override
    public void resize(int width, int height)
	{
		super.resize(width, height);
        stage.setViewport(width, height, false);
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
	public ThreadName getThreadName() {
		return ThreadName.AboutGame;
	}

}
