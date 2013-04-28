package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
	public Table controlsTable= new Table();
	Preferences prefs = Gdx.app.getPreferences("ControlsPreferences");
	Skin uiSkin;
	TextButton jumpButton;
	TextButton pauseButton;
	TextButton duckButton;

	float visibility;
	int controllerLayout;
	//define my listeners
	public ChangeListener jumpListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			//System.out.println("jump!");
		}
	};
	public ChangeListener duckListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			//System.out.println("duck!");
		}
	};
	public ChangeListener pauseListener = new ChangeListener() {
		public void changed(ChangeEvent event, Actor actor)
		{
			//System.out.println("pause!");
		}
	};

	public GameControls()
	{
		this.uiSkin = GameThread.uiSkin;
		if(uiSkin == null)
		{
	        FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
	        GameThread.uiSkin = new Skin( skinFile );
	        this.uiSkin = GameThread.uiSkin;
		}
		this.uiSkin = GameThread.uiSkin;
		this.visibility=this.prefs.getFloat("visibility", 1.0f);
		this.controllerLayout=this.prefs.getInteger("controllerLayout", 0);

		this.defineControlsTable();

	}
	public GameControls(ChangeListener jumpL, ChangeListener duckL, ChangeListener pauseL)
	{
		this.uiSkin = GameThread.uiSkin;
		if(uiSkin == null)
		{
	        FileHandle skinFile = Gdx.files.internal( "uiskin/uiskin.json" );
	        GameThread.uiSkin = new Skin( skinFile );
	        this.uiSkin = GameThread.uiSkin;
		}
		this.visibility=this.prefs.getFloat("visibility", 1.0f);
		this.controllerLayout=this.prefs.getInteger("controllerLayout", 0);
		this.jumpListener = jumpL;
		this.duckListener = duckL;
		this.pauseListener = pauseL;

		this.defineControlsTable();
	}
	public void loadPrefs()
	{
		this.visibility=this.prefs.getFloat("visibility", 1.0f);
		this.controllerLayout=this.prefs.getInteger("controllerLayout", 0);
	}
	public void defineControlsTable()
	{

	    controlsTable.reset();

		pauseButton = new TextButton("Pause", uiSkin);
		jumpButton = new TextButton("Jump", uiSkin);
		duckButton = new TextButton("Duck", uiSkin);

		jumpButton.addListener(jumpListener);
		duckButton.addListener(duckListener);
		pauseButton.addListener(pauseListener);

		if(this.controllerLayout == 1)//entire left jump, pause center top, entire right duck
		{
			//this.controllerLayout=1;
			//controlsTable.add(new Label("Controls "+Integer.toString(controllertype), uiSkin)).colspan(3).center().pad(5);controlsTable.row();
			Table ltable=new Table();
			ltable.add(jumpButton).fill().expand();
			Table rtable=new Table();
			rtable.add(duckButton).fill().expand();
			Table ctable=new Table();
			ctable.add(pauseButton).fill().expand();
			ctable.row();
			ctable.add().fill().expand();
			ctable.row();
			ctable.add().fill().expand().pad(5);

			controlsTable.add(ltable).fill().expand().pad(5).left();
			controlsTable.add(ctable).fill().expand().pad(5).center();
			controlsTable.add(rtable).fill().expand().pad(5).right();

		}
		else if(this.controllerLayout == 2)
		{
			//this.controllerLayout=2;
			//controlsTable.add(new Label("Controls "+Integer.toString(controllertype), uiSkin)).colspan(3).center().pad(5);controlsTable.row();
			Table ltable=new Table();
			ltable.add(jumpButton).fill().expand();
			ltable.row();
			ltable.add(duckButton).fill().expand();
			Table rtable=new Table();
			rtable.add().fill().expand();
			Table ctable=new Table();
			ctable.add().fill().expand();
			ctable.row();
			ctable.add(pauseButton).fill().expand();
			ctable.row();
			ctable.add().fill().expand().pad(5);
			controlsTable.add(ltable).fill().expand().pad(5).left();
			controlsTable.add(ctable).fill().expand().pad(5).center();
			controlsTable.add(rtable).fill().expand().pad(5).right();
		}
		else if(this.controllerLayout == 3)
		{
			//this.controllerLayout=2;
			//controlsTable.add(new Label("Controls "+Integer.toString(controllertype), uiSkin)).colspan(3).center().pad(5);controlsTable.row();
			Table ltable=new Table();
			ltable.add(jumpButton).fill().expand();
			ltable.row();
			ltable.add(duckButton).fill().expand();
			Table rtable=new Table();
			rtable.add().fill().expand();
			Table ctable=new Table();
			ctable.add(pauseButton).fill().expand();
			ctable.row();
			ctable.add().fill().expand();
			ctable.row();
			ctable.add().fill().expand().pad(5);
			controlsTable.add(ltable).fill().expand().pad(5).left();
			controlsTable.add(ctable).fill().expand().pad(5).center();
			controlsTable.add(rtable).fill().expand().pad(5).right();
		}
		else //default controller type =0 //entire left jump, pause center, entire right duck
		{
			this.controllerLayout=0; //reset just incase it was set to something wierd
			//controlsTable.add(new Label("Controls "+Integer.toString(controllertype), uiSkin)).colspan(3).center().pad(5);controlsTable.row();
			Table ltable=new Table();
			ltable.add(jumpButton).fill().expand();
			Table rtable=new Table();
			rtable.add(duckButton).fill().expand();
			Table ctable=new Table();
			ctable.add().fill().expand();
			ctable.row();
			ctable.add(pauseButton).fill().expand();
			ctable.row();
			ctable.add().fill().expand().pad(5);
			controlsTable.add(ltable).fill().expand().pad(5).left();
			controlsTable.add(ctable).fill().expand().pad(5).center();
			controlsTable.add(rtable).fill().expand().pad(5).right();
		}
		this.setVisibility(this.visibility);
	}
	public float getVisibility(){return this.visibility;}
	public int getControllerLayout(){return this.controllerLayout;}
	public void saveControls()
	{
		this.prefs.putFloat("visibility", this.visibility);
		this.prefs.putInteger("controllerLayout", this.controllerLayout);
		this.prefs.flush();
		//System.out.print("controllerLayout:");System.out.println(this.controllerLayout);
		//System.out.print("visibility:");System.out.println(this.visibility);
	}
	public void setDisabled(boolean disabled)
	{
			jumpButton.setDisabled(disabled);
			pauseButton.setDisabled(disabled);
			duckButton.setDisabled(disabled);

	}
	public void setVisibility(float visibl)
	{
		this.visibility = visibl;
			jumpButton.setColor(visibility, visibility, visibility, visibility);
			pauseButton.setColor(visibility, visibility, visibility, visibility);
			duckButton.setColor(visibility, visibility, visibility, visibility);
	}
	public void setControlsLayout(int clay)
	{
		this.controllerLayout = clay;
	}
}
