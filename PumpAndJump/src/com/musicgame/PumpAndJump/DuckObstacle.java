package com.musicgame.PumpAndJump;

public class DuckObstacle extends GameObject {
	private double StartTime;
	private double EndTime;
	public DuckObstacle(double Start,double End){
		super();
		StartTime=Start;
		EndTime=End;
	}
	public double getStartTime(){
		return StartTime;
	}
	public double getEndTime(){
		return EndTime;
	}
}
