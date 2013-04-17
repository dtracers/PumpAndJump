package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.musicgame.PumpAndJump.game.gameStates.PauseGame;

public class GameControls
{
	public Table controlsTable;
	TextButton jumpButton;
	TextButton pauseButton;
	TextButton duckButton;
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
		this.initializeTable();
		jumpButton.addListener(jumpListener);
		duckButton.addListener(duckListener);
		pauseButton.addListener(pauseListener);
	}
	public GameControls(ChangeListener jumpL, ChangeListener duckL, ChangeListener pauseL)
	{	
		this.initializeTable();
		jumpButton.addListener(jumpL);
		duckButton.addListener(duckL);
		pauseButton.addListener(pauseL);
	}
	
	void initializeTable()
	{
		FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
	    Skin uiSkin = new Skin( skinFile );
	    
		controlsTable = new Table();
		controlsTable.setFillParent(true);
		
		pauseButton = new TextButton("Pause", uiSkin);
		jumpButton = new TextButton("Jump", uiSkin);
		duckButton = new TextButton("Duck", uiSkin);
		
		controlsTable.add(jumpButton).expand().fill();
		controlsTable.add(pauseButton).expand().size(250,100).pad(5);
		controlsTable.add(duckButton).expand().fill();
	}
	
	public void setDisabled(boolean disabled)
	{
			jumpButton.setDisabled(disabled);
			pauseButton.setDisabled(disabled);
			duckButton.setDisabled(disabled);
	
	}
	public void setVisible(boolean visible)
	{
		if(!visible){
			jumpButton.setColor(0.0f,0.0f,0.0f, 0.0f); //make buttons invisible when on screen
			pauseButton.setColor(0.0f,0.0f,0.0f, 0.0f); //make buttons invisible when on screen			
			duckButton.setColor(0.0f,0.0f,0.0f, 0.0f); //make buttons invisible when on screen
		}
		else
		{
			jumpButton.setColor(1.0f,1.0f,1.0f, 1.0f); //make buttons invisible when on screen
			pauseButton.setColor(1.0f,1.0f,1.0f, 1.0f); //make buttons invisible when on screen			
			duckButton.setColor(1.0f,1.0f,1.0f, 1.0f); //make buttons invisible when on screen
		}
	}
}
