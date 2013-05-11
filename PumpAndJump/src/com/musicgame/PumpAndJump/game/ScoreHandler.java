package com.musicgame.PumpAndJump.game;

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
	private double superSaiyanScore;

	public void hitNegative()
	{

	}
	public void hitPositive()
	{

	}

	public boolean isSuperSaiyan()
	{
		if(score>superSaiyanScore)
			return true;
		else
			return false;
	}
	public String getScore() {
		return null;
	}
}
