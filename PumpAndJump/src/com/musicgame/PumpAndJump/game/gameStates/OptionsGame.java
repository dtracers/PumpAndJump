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
import com.badlogic.gdx.scenes.scene2d.utils.Align;
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
        
        Table container = new Table();
		stage.addActor(container);
		container.setFillParent(true);
		//container.debug(); // turn on all debug lines (table, cell, and widget)
		//container.debugTable(); // turn on only table lines
		
		Table scrolltable = new Table();
		//scrolltable.debug(); // turn on all debug lines (table, cell, and widget)
		//scrolltable.debugTable(); // turn on only table lines
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
		saveButton.addListener(
					new ChangeListener()
					{
						public void changed(ChangeEvent event, Actor actor)
						{
							controls.saveControls();
							goBack();
						}
					});
		Slider controlsLayoutSlider = new Slider(0, 3, 1, false, uiSkin);
		controlsLayoutSlider.addListener(stopTouchDown);
		controlsLayoutSlider.setValue( controls.getControllerLayout() );
		controlsLayoutSlider.addListener( new ChangeListener()
		{
			public void changed(ChangeEvent event, Actor actor)
			{
				controls.setControlsLayout( (int)((Slider) actor).getValue());
				controls.defineControlsTable();
			}
		});
		Slider visibilitySlider = new Slider(0, 100, 1, false, uiSkin);
		visibilitySlider.setValue(controls.getVisibility()*100);
		visibilitySlider.addListener(stopTouchDown);
		visibilitySlider.addListener(
				new ChangeListener()
				{
					public void changed(ChangeEvent event, Actor actor)
					{
						controls.setVisibility(((Slider) actor).getValue()/100.0f);
					}
				});
		int buttonWidth = Gdx.graphics.getWidth()/2-15;
		int buttonHeight = Gdx.graphics.getHeight()/6-10;
		
		container.add(scroll).expand().fill();
		
		scrolltable.add().expand().fill();
		Label l = new Label("Controls Layout ", uiSkin);
		l.setAlignment(Align.center);
		scrolltable.add(l).expand().fill();
		scrolltable.add(controlsLayoutSlider).colspan(3).expand().fill();
		scrolltable.add().expand().fill();

		scrolltable.row();
		scrolltable.add(this.controls.controlsTable).colspan(6).size((int)(0.6f*Gdx.graphics.getWidth()),(int)(0.6f*Gdx.graphics.getHeight()) );

		scrolltable.row();
		scrolltable.add().expand().fill();
		Label vl = new Label("Visibility  ", uiSkin);
		vl.setAlignment(Align.center);
		scrolltable.add(vl).expand().fill();
		scrolltable.add(visibilitySlider).colspan(3).expand().fill();
		scrolltable.add().expand().fill();
		
		scrolltable.row();
		scrolltable.add().expand().fill();
		scrolltable.add().expand().fill();
		scrolltable.add().expand().fill();
		scrolltable.add().expand().fill();
		scrolltable.add().expand().fill();
		scrolltable.add().expand().fill();
		
		scrolltable.row();
		scrolltable.add(backButton).colspan(3).expand().fill().pad(5);//.colspan(2).pad(5).left();
		scrolltable.add(saveButton).colspan(3).expand().fill().pad(5);//.size(buttonWidth,buttonHeight);//.colspan(2).pad(5).right();
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
