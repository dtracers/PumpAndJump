package net.bluecow.spectro.painting;

import java.awt.Point;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

public class ClipPlayBackLocation implements PlaybackPositionListener{

	double clipPosition;
	Point drawPosition,offset;
	JScrollPane panel;
	long samplesOnScreen;
	double numSamples = 0;
	JScrollBar bar;
	public ClipPlayBackLocation(Point drawPosition,Point offset,JScrollPane scrollPane,double numSamples)
	{
		this.drawPosition = drawPosition;
		this.offset = offset;
		panel = scrollPane;
		this.numSamples = numSamples;
		bar = panel.getHorizontalScrollBar();
	}
	@Override
	public void playbackPositionUpdate(PlaybackPositionEvent e)
	{
		drawPosition.setLocation(e.getSamplePos(),drawPosition.getY());
		clipPosition = ((double)drawPosition.getX())/numSamples*((double)bar.getMaximum());
		clipPosition +=offset.getX();
	//	System.out.println(offset.getX()+" "+clipPosition);
		if(clipPosition<0)
		{

			clipPosition = 0;
		}
		bar.setValue((int)clipPosition);
	}

}
