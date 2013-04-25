package com.musicgame.PumpAndJump.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.musicgame.PumpAndJump.Util.FileChooser;
import com.musicgame.PumpAndJump.game.gameStates.FileChooserState;

public class FileChooserDesktop extends FileChooser
{

	public FileChooserDesktop(String[] exT) {
		super(exT);
	}

	@Override
	public void showChooser() {
	}

	@Override
	public File getFile() {
		JFileChooser jfc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV files", "wav");
		jfc.setFileFilter(filter);
	    jfc.showDialog(null,"Open");
	    jfc.setVisible(true);
	    File filename = jfc.getSelectedFile();
		return filename;
	}

}
