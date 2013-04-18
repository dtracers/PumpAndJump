package com.musicgame.PumpAndJump;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.musicgame.PumpAndJump.game.PumpAndJump;
//import com.musicgame.PumpAndJump.music.AndroidInputDecoder;
import com.musicgame.PumpAndJump.game.gameStates.FileChooserState;
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

        FileChooserState.fileDialog = new FileChooserAndroid(null,null);
     //   PumpAndJump.inputStream = new AndroidInputDecoder(0, null);
    }
}