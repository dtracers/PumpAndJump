package com.musicgame.PumpAndJump.Util;

import java.io.File;

public abstract class FileChooser
{
	String[] extensionText;//the text that comes with each extension
	//String[] extension;//the actual exstension
	public FileChooser(String[] exT)
	{
		this.extensionText = exT;
		//this.extension = ex;
	}

	public abstract void showChooser();

	public abstract File getFile();
	


}
