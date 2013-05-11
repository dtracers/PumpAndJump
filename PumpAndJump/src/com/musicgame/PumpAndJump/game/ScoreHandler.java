package com.musicgame.PumpAndJump.game;

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
	double score;
	double health;
	int objectRatio;
	private double superSaiyanScore = 50.0;

	public void hitNegative(double ratio)
	{
		score--;
		health--;
	}
	public void hitPositive(double ratio)
	{
		health+=1*ratio;
		score+=1*ratio;
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
}
