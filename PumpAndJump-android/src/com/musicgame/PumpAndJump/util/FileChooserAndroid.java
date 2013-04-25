package com.musicgame.PumpAndJump.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.musicgame.PumpAndJump.MainActivity;
import com.musicgame.PumpAndJump.Util.FileChooser;

public class FileChooserAndroid extends FileChooser{
	
	public static File file;
	public FileChooserAndroid(String[] exT) {
		super(exT);
	}

	@Override
	public void showChooser() {
		
	}
	//Context context = new Activity(MainActivity.getApplicationContext());

	@Override
	public File getFile() {
		System.out.println("HERE1");
		
		
		
		
		
		return file;
	}

}
