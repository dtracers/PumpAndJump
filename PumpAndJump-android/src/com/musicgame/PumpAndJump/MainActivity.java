package com.musicgame.PumpAndJump;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.musicgame.PumpAndJump.game.PumpAndJump;
//import com.musicgame.PumpAndJump.music.AndroidInputDecoder;
import com.musicgame.PumpAndJump.game.gameStates.FileChooserState;
import com.musicgame.PumpAndJump.game.sound.MP3Decoder;
import com.musicgame.PumpAndJump.music.AndroidMP3Decoder16;
import com.musicgame.PumpAndJump.util.Chooser;
import com.musicgame.PumpAndJump.util.FileChooserAndroid;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;
        cfg.useAccelerometer = false;
        cfg.useCompass = false;

        initialize(new PumpAndJump(), cfg);

        FileChooserState.fileDialog = new FileChooserAndroid(null);
        Intent intent = new Intent();
		intent.setClass(this, Chooser.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //required to launch from non-activity
		startActivity(intent);
        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        
        MP3Decoder decoder;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
        	decoder = new AndroidMP3Decoder16(null);
        } else{
        	decoder = null;
        }
        PumpAndJump.MP3decoder = decoder;*/
     //   PumpAndJump.inputStream = new AndroidInputDecoder(0, null);
    }
}