package com.musicgame.PumpAndJump.util;

import java.io.File;

import com.musicgame.PumpAndJump.Util.FileChooser;

public class FileChooserAndroid extends FileChooser{

	public FileChooserAndroid(String[] exT, String[] ex) {
		super(exT, ex);
	}

	@Override
	public void showChooser() {
	}

	@Override
	public File getFile() {
		return null;
	}

}
