package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;

/**
 * Is running when the game is paused
 * @author gigemjt
 *
 */
public class PauseGame extends GameThread
{
	Skin uiSkin;
	Stage stage;
	SpriteBatch batch;
	private ShapeRenderer shapeRenderer;

	public PauseGame()
	{
		batch = new SpriteBatch();
		stage = new Stage();

		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
        uiSkin = new Skin( skinFile );

        shapeRenderer = new ShapeRenderer();

		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table();
		table.setFillParent(true);

		//table.debug(); // turn on all debug lines (table, cell, and widget)
		//table.debugTable(); // turn on only table lines


		stage.addActor(table);

		final TextButton unpauseGameButton = new TextButton("Resume", uiSkin);
		unpauseGameButton.addListener(
				new ChangeListener()
				{
					public void changed(ChangeEvent event, Actor actor)
					{
					//	PumpAndJump.switchThread(ThreadName.RunningGame, PauseGame.this);
						PumpAndJump.removeThread(ThreadName.PauseGame, PauseGame.this);
						System.out.println("pressed!");
						unpause();
					}
				});

		final TextButton optionGameButton = new TextButton("Options", uiSkin);
		optionGameButton.addListener(
				new ChangeListener()
				{
					public void changed(ChangeEvent event, Actor actor)
					{
						PumpAndJump.addThread(ThreadName.OptionsGame, PauseGame.this);
						//PumpAndJump.removeThread(ThreadName.RunningGame, PauseGame.this);
						System.out.println("pressed!");
					}
				});

		final TextButton jumpGameButton = new TextButton("Jump Button", uiSkin);
		jumpGameButton.setColor(.4f,.4f,.4f,.6f);
		jumpGameButton.setDisabled(true);
		final TextButton duckGameButton = new TextButton("Duck Button", uiSkin);
		duckGameButton.setDisabled(true);
		duckGameButton.setColor(.4f,.4f,.4f,.6f);
		table.add(jumpGameButton).expand().fill();

		table.add(optionGameButton).expand().size(250,100).pad(5);
		table.add(unpauseGameButton).expand().size(250,100).pad(5);

		table.add(duckGameButton).expand().fill();

	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
	//	System.out.println("mouseMoved");
		return false;
	}

	@Override
	public void render(float delta)
	{
		//Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 0.1f);
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.begin(ShapeType.FilledRectangle);
		shapeRenderer.filledRect(0f, 0f, width,height, Color.RED, Color.CLEAR, Color.CLEAR, Color.RED);
		shapeRenderer.end();

		Gdx.gl.glDisable(GL10.GL_BLEND);

		stage.act(Math.min(delta, 1 / 30f));
		stage.draw();
		//Table.drawDebug(stage);
		//System.out.println("going");
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}


	@Override
	public void switchFrom(GameThread currentThread) {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void addFrom(GameThread currentThread) {
		Gdx.input.setInputProcessor(stage);
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
		return ThreadName.PauseGame;
	}

}
