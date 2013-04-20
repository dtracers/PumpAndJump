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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.musicgame.PumpAndJump.game.GameControls;
import com.musicgame.PumpAndJump.game.GameThread;
import com.musicgame.PumpAndJump.game.PumpAndJump;
import com.musicgame.PumpAndJump.game.ThreadName;

/**
 * It happens before the game starts
 * @author gigemjt
 *
 */
public class OptionsGame extends GameThread
{

	Skin uiSkin;
	Stage stage;
	ThreadName fromThread;
	GameControls controls;
	private Table container;
	
	public OptionsGame()
	{
		stage = new Stage();

        FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
        uiSkin = new Skin( skinFile );
        this.controls = new GameControls();//jumpListener,duckListener,pauseListener);
        //this.controls.setDisabled();
        
        Table container = new Table();
		stage.addActor(container);
		container.setFillParent(true);

		Table scrolltable = new Table();

		final ScrollPane scroll = new ScrollPane(scrolltable, uiSkin);

		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
				return false;
			}
		};

		final TextButton backButton = new TextButton("Back", uiSkin);
		backButton.addListener(
					new ChangeListener()
					{
						public void changed(ChangeEvent event, Actor actor)
						{
							goBack();
						}
					});
		final TextButton saveButton = new TextButton("Save", uiSkin);
		backButton.addListener(
					new ChangeListener()
					{
						public void changed(ChangeEvent event, Actor actor)
						{
							//goBack();
						}
					});
		Slider controlsLayoutSlider = new Slider(0, 2, 1, false, uiSkin);
		controlsLayoutSlider.addListener(stopTouchDown);
		controlsLayoutSlider.setValue( 0 );
		controlsLayoutSlider.addListener( new ChangeListener()
		{
			public void changed(ChangeEvent event, Actor actor)
			{
				controls.defineControlsTable( (int)((Slider) actor).getValue());
			}
		});
		Slider visibilitySlider = new Slider(0, 100, 1, false, uiSkin);
		visibilitySlider.addListener(stopTouchDown);
		visibilitySlider.addListener(
				new ChangeListener()
				{
					public void changed(ChangeEvent event, Actor actor)
					{
						controls.setVisiblity(((Slider) actor).getValue()/100.0f);
					}
				});
		int buttonWidth = Gdx.graphics.getWidth()/2-15;
		int buttonHeight = Gdx.graphics.getHeight()/6-10;
		
		container.add(scroll).expand().fill();//.colspan(2);//.pad(10).colspan(2);
		
		scrolltable.add(new Label("Controls Layout ", uiSkin)).right().pad(5);
		scrolltable.add(controlsLayoutSlider).left();
		scrolltable.row();//.size(500,500);
		scrolltable.add(this.controls.controlsTable).colspan(2).size((int)(0.6f*Gdx.graphics.getWidth()),(int)(0.6f*Gdx.graphics.getHeight()) );

		scrolltable.row();//.space(10).padBottom(10);
		scrolltable.add(new Label("Controls Visibility ", uiSkin)).pad(5);
		scrolltable.add(visibilitySlider).expand().fill().pad(15);
		scrolltable.row();//.space(10).padBottom(10);
		scrolltable.add(backButton).size(buttonWidth,buttonHeight).pad(5).left();
		scrolltable.add(saveButton).size(buttonWidth,buttonHeight).pad(5).right();
	}

	@Override
    public void render(float delta)
	{
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(delta, 1 / 30f));
		stage.draw();
		Table.drawDebug(stage);
    }

	@Override
    public void resize(int width, int height)
	{
		super.resize(width, height);
        stage.setViewport(width, height, false);
    }

	public void goBack()
	{
		switch(fromThread)
		{
			case PreGame:
				PumpAndJump.switchThread(ThreadName.PreGame, this);break;
			case PauseGame:
				PumpAndJump.removeThread(ThreadName.OptionsGame, this);break;
		}
	}

	@Override
	public void switchFrom(GameThread currentThread)
	{

		Gdx.input.setInputProcessor(stage);
		fromThread = currentThread.getThreadName();

	}

	@Override
	public void addFrom(GameThread currentThread)
	{
		Gdx.input.setInputProcessor(stage);
		fromThread = currentThread.getThreadName();
	}

	@Override
	public void removeFrom(GameThread currentThread)
	{
	}

	@Override
	public void unpause() {
	}

	@Override
	public ThreadName getThreadName() {
		return ThreadName.OptionsGame;
	}

	@Override
	public void repause() {
	}
}
