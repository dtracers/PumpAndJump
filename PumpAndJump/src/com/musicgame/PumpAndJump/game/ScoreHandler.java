package com.musicgame.PumpAndJump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.musicgame.PumpAndJump.objects.Beat;
import com.musicgame.PumpAndJump.objects.DuckObstacle;
import com.musicgame.PumpAndJump.objects.JumpObstacle;
import com.musicgame.PumpAndJump.objects.Obstacle;

/**
 * Holds the score and computes the score (so we can change out scoring options easier)
 *
 * @author gigemjt
 *
 */
public class ScoreHandler
{
	public ShapeRenderer shapeRenderer;
	double maxHealth = 200;
	double maxScore;
	double score;
	double health = 200;
	int objectRatio;
	private double superSaiyanScore = 50.0;

	public void hitNegative(double ratio)
	{
		score--;
		health-=.5;
	}
	public void hitPositive(double ratio)
	{
		health+=.5*ratio;
		score+=1*ratio;
		if(score>maxScore)
			maxScore = score;
		if(health>maxHealth)
			health = maxHealth;
	}

	public boolean isSuperSaiyan()
	{
		if(score>superSaiyanScore)
			return true;
		else
			return false;
	}
	public String getScore() {
		return ""+score;
	}
	public void Impacted(Obstacle currentObj, double ratio)
	{
		if(currentObj instanceof Beat)
			hitPositive(ratio);
		if(currentObj instanceof JumpObstacle||currentObj instanceof DuckObstacle)
			hitNegative(ratio);
	}
	public void drawHealth(float x,float y, Matrix4 proj, Matrix4 trans)
	{
		shapeRenderer.setProjectionMatrix(proj);
		shapeRenderer.setTransformMatrix(trans);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.begin(ShapeType.FilledRectangle);
		if(health>0)
			shapeRenderer.filledRect(x, y-(float)maxHealth, 20f,(float)health , new Color(1f, 0f, 0f, 0.8f), new Color(1f, 0f, 0f, 0.5f), new Color(1f, 0f, 0f, 0.5f), new Color(1f, 0f, 0f, 0.8f));
		shapeRenderer.end();

		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	public boolean isAlive()
	{
		return health>0;
	}
}
