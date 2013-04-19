package com.musicgame.PumpAndJump.Animation;

import com.badlogic.gdx.math.Matrix4;

//All things animated with interpolation need to implement this interface
public interface Animated {
	
	public void UpdatePose( Matrix4 mv, float[] pose );
	
}
