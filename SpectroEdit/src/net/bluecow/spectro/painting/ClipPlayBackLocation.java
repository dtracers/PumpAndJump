package net.bluecow.spectro.painting;

import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.Scrollable;

public class ClipPlayBackLocation implements PlaybackPositionListener{

	Point drawPosition,offset;
	JScrollPane panel;
	long samplesOnScreen;
	public ClipPlayBackLocation(Point drawPosition,Point offset,JScrollPane scrollPane)
	{
		this.drawPosition = drawPosition;
		this.offset = offset;
		panel = scrollPane;
	}
	@Override
	public void playbackPositionUpdate(PlaybackPositionEvent e)
	{
		drawPosition.setLocation(offset.getX()+e.getSamplePos(),drawPosition.getY());
		panel.getHorizontalScrollBar().setValue((int)drawPosition.getX());
	}

}
