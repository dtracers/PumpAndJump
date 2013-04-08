package com.musicgame.PumpAndJump;

public class JumpObstacle extends GameObject {
	private double StartTime;
	JumpObstacle(double time){
		super();
		StartTime=time;
	}
	public double getStartTime(){
		return StartTime;
	}

}
