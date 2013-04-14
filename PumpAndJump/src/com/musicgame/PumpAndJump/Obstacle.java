package com.musicgame.PumpAndJump;

public class Obstacle extends GameObject
{
	private double start;
	private double end;
	public Obstacle(double Start,double End){
		super();
		start=Start;
		end=End;
	}

	public double getStartTime(){
		return start;
	}
	public double getEndTime(){
		return end;
	}
}
