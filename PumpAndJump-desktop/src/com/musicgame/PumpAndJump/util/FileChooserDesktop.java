package com.musicgame.PumpAndJump.util;

import java.io.File;

import com.musicgame.PumpAndJump.Util.FileChooser;

public class FileChooserDesktop extends FileChooser
{

	public FileChooserDesktop(String[] exT, String[] ex) {
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
