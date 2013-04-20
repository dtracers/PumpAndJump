package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.musicgame.PumpAndJump.game.gameStates.PauseGame;

public class GameControls
{
	public Table controlsTable= new Table();;
	TextButton jumpButton;
	TextButton pauseButton;
	TextButton duckButton;
	float visiblity;
	//define my listeners
	public ChangeListener jumpListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			System.out.println("jump!");
		}
	};
	public ChangeListener duckListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			System.out.println("duck!");
		}
	};
	public ChangeListener pauseListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			System.out.println("pause!");
		}
	};
	
	public GameControls()
	{	
		/*controlsTable 
		//controlsTable.setFillParent(true);
		controlsTable.debug(); // turn on all debug lines (table, cell, and widget)
		controlsTable.debugTable(); // turn on only table lines*/
		this.visiblity=1.0f;
		this.defineControlsTable(0);
		jumpButton.addListener(jumpListener);
		duckButton.addListener(duckListener);
		pauseButton.addListener(pauseListener);
	}
	public GameControls(ChangeListener jumpL, ChangeListener duckL, ChangeListener pauseL)
	{	
		this.defineControlsTable(0);
		jumpButton.addListener(jumpL);
		duckButton.addListener(duckL);
		pauseButton.addListener(pauseL);
	}
	
	public void defineControlsTable(int controllertype)
	{
		FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
	    Skin uiSkin = new Skin( skinFile );
	    controlsTable.reset();

		pauseButton = new TextButton("Pause", uiSkin);
		jumpButton = new TextButton("Jump", uiSkin);
		duckButton = new TextButton("Duck", uiSkin);
		if(controllertype == 1)
		{
			controlsTable.add(new Label("Controls "+Integer.toString(controllertype), uiSkin)).colspan(2).center().pad(5);controlsTable.row();
			controlsTable.add(jumpButton).fill().expand().pad(5).left();
			controlsTable.add(pauseButton).size(100,100).pad(5).center();
			controlsTable.add(duckButton).fill().expand().pad(5).right();
		}
		else if(controllertype == 2)
		{
			controlsTable.add(new Label("Controls "+Integer.toString(controllertype), uiSkin)).colspan(2).center().pad(5);controlsTable.row();
			controlsTable.add(jumpButton).fill().expand().pad(5).left();
			controlsTable.add(pauseButton).size(250,100).pad(5).center();
			controlsTable.add(duckButton).fill().expand().pad(5).right();		
		}
		else //default controller type =0
		{
			controlsTable.add(new Label("Controls "+Integer.toString(controllertype), uiSkin)).colspan(2).center().pad(5);controlsTable.row();
			controlsTable.add(jumpButton).fill().expand().pad(5).left();
			controlsTable.add(pauseButton).size(100,100).pad(5).center();
			controlsTable.add(duckButton).fill().expand().pad(5).right();
		}
		this.setVisiblity(this.visiblity);
		System.out.print("width: ");
		System.out.println(Gdx.graphics.getWidth());
	}
	
	public void setDisabled(boolean disabled)
	{
			jumpButton.setDisabled(disabled);
			pauseButton.setDisabled(disabled);
			duckButton.setDisabled(disabled);
	
	}
	public void setVisiblity(float visibl)
	{		
		this.visiblity = visibl;
			jumpButton.setColor(visiblity, visiblity, visiblity, visiblity);
			pauseButton.setColor(visiblity, visiblity, visiblity, visiblity);
			duckButton.setColor(visiblity, visiblity, visiblity, visiblity);
	}
}
