package com.musicgame.PumpAndJump.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.musicgame.PumpAndJump.Player;
import com.musicgame.PumpAndJump.game.ScoreHandler;

public class ObjectHandler
{
	ScoreHandler score = new ScoreHandler();
	public ArrayList<Obstacle> actualObjects = new ArrayList<Obstacle>();
	public int lastStartIndex = 0;

	double numberOfPositives;
	double numberOfNegatives;

	double ratio = 1;

	/**
	 * Updates the last index
	 * @param timeReference
	 */
	public void updateIndex(float timeReference)
	{
		// move last index
		for(int k = lastStartIndex; k<actualObjects.size(); k++)
		{
			Obstacle currentObj = actualObjects.get(k);
			if(currentObj.rightOfLeftSideOfScreen(timeReference - .33333f ) )
			{
				break;
			}
			currentObj.done();
			lastStartIndex++;
		}
	}


	/**
	 * Updates the Obstacles
	 * @param timeReference
	 * @param mv
	 * @param delta
	 * @param player
	 * @param tempo
	 */
	public void updateObstacles(float timeReference, Matrix4 mv,float delta, Player player,float tempo)
	{
		ratio = (double)numberOfNegatives/((double)numberOfPositives*.7);
		// update the obstacles that are onscreen
		for(int k = lastStartIndex;k<actualObjects.size();k++)
		{
			Obstacle currentObj = actualObjects.get(k);
			if( currentObj.leftOfRightSideOfScreen(  timeReference + 3.0f ) )
			{
				currentObj.update( mv, delta );
				if( currentObj.inScreenRange( timeReference - .33333f, timeReference + .33333f ) )
				{
					if( player.intersects( currentObj.hull ) )
					{
						currentObj.Impacted( tempo );
						score.Impacted(currentObj,ratio);
					}
				}
			}
			else
			{
				break;
			}
		}
	}


	/**
	 * Renders the obstacles
	 * @param timeReference
	 * @param batch
	 */
	public void renderObstacles(float timeReference,SpriteBatch batch)
	{
		for(int k = lastStartIndex;k<actualObjects.size();k++)
		{
			Obstacle currentObj = actualObjects.get(k);
			if( currentObj.leftOfRightSideOfScreen( (float) timeReference + 3.0f ) )
			{
				currentObj.draw( batch );
			}
			else
			{
				break;
			}
		}
	}


	public void add(Beat b)
	{
		actualObjects.add(b);
		numberOfPositives++;
	}
	public void add(Obstacle create)
	{
		numberOfNegatives++;
		actualObjects.add(create);
	}


	public void add(int i, Obstacle create)
	{
		numberOfNegatives++;
		actualObjects.add(i,create);
	}

	public ScoreHandler getScoreKeeper()
	{
		return score;
	}
}
