package com.musicgame.PumpAndJump;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
        FileChooserState.type="android";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("No GPS");

		// set dialog message
		alertDialogBuilder
			.setMessage("Please Enable GPS!")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, Chooser.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //required to launch from non-activity
					startActivity(intent);

				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing

				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
        
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