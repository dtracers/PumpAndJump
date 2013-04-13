package com.musicgame.PumpAndJump.game.gameStates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	public PauseGame()
	{
		batch = new SpriteBatch();
		stage = new Stage();
		
		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
        uiSkin = new Skin( skinFile );

		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table();
		table.setFillParent(true);
		
		//table.debug(); // turn on all debug lines (table, cell, and widget)
		//table.debugTable(); // turn on only table lines
		
		
		stage.addActor(table);
		
		final TextButton unpauseGameButton = new TextButton("Pause", uiSkin);
		unpauseGameButton.addListener(
				new ChangeListener()
				{
					public void changed(ChangeEvent event, Actor actor)
					{
						PumpAndJump.switchThread(ThreadName.RunningGame, PauseGame.this);
						//PumpAndJump.removeThread(ThreadName.RunningGame, PauseGame.this);
						System.out.println("pressed!");
						unpause();
					}
				});
		final TextButton jumpGameButton = new TextButton("Jump", uiSkin);
		jumpGameButton.setColor(.4f,.4f,.4f,.6f);
		jumpGameButton.setDisabled(true);
		final TextButton duckGameButton = new TextButton("Duck", uiSkin);
		duckGameButton.setDisabled(true);
		duckGameButton.setColor(.4f,.4f,.4f,.6f);

		table.add(jumpGameButton).expand().fill();
		table.add(unpauseGameButton).expand().size(250,100).pad(5);
		table.add(duckGameButton).expand().fill();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		System.out.println("mouseMoved");
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void pause() {
	}

	@Override
	public void render(float delta)
	{
		//Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 0.1f);
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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

}
