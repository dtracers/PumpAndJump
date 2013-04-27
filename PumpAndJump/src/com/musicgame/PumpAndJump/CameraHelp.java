package com.musicgame.PumpAndJump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraHelp {

	/**
	 * @param args
	 */
	
	public static final float virtualWidth = 800.0f;
	public static final float virtualHeight = 520.0f;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static OrthographicCamera GetCamera()
	{
		  float viewportWidth = virtualWidth;
		  float viewportHeight = virtualHeight;
		  float physicalWidth = Gdx.graphics.getWidth();
		  float physicalHeight = Gdx.graphics.getHeight();
		  float aspect = virtualWidth / virtualHeight;
		  //This is to maintain the aspect ratio.
		  //If the virtual aspect ration does not match with the aspect ratio
		  //of the hardware screen then the viewport would scaled to
		  //meet the size of one dimension and other would not cover full dimension
		  //If we stretch it to meet the screen aspect ratio then textures will
		  //get distorted either become fatter or elongated
		  if (physicalWidth / physicalHeight >= aspect) {
		   // Letterbox left and right.
		   viewportHeight = virtualHeight;
		   viewportWidth = viewportHeight * physicalWidth / physicalHeight;
		  } else {
		  // Letterbox above and below.
		   viewportWidth = virtualWidth;
		   viewportHeight = viewportWidth * physicalHeight / physicalWidth;
		  }
	
		  OrthographicCamera camera = new OrthographicCamera(viewportWidth, viewportHeight);
		  camera.position.set(virtualWidth/2, 0.0f, 0.0f);
		  camera.update();
		  return camera;
	}

}
