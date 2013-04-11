package com.musicgame.PumpAndJump;

public class GameError extends Throwable
{
	private static final long serialVersionUID = 1L;
	
	String err;
	public GameError( String err )
	{
		this.err = err;
	}
	
	public String toString()
	{
		return err;
	}
}

class IntializationError extends GameError
{
	private static final long serialVersionUID = 1L;

	public IntializationError( String err )
	{
		super( err );
	}
	
	public String toString()
	{
		return "IntializationError: "+err;
	}
}
