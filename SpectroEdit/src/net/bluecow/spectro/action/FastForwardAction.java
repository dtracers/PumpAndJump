package net.bluecow.spectro.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bluecow.spectro.PlayerThread;

public class FastForwardAction extends AbstractAction
{
	private final PlayerThread playerThread;

	boolean forwarding = false;
	int speed;
	Thread running;

	private final ChangeListener playerStateHandler = new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
	   SwingUtilities.invokeLater(new Runnable() {
	   public void run() {
	     if (FastForwardAction.this.playerThread.isPlaying())
	    	 FastForwardAction.this.putValue("Name", "Stop");
        else
        	FastForwardAction.this.putValue("Name", "Rewind");
  }
   });
     }
	};

	public FastForwardAction(PlayerThread playerThread)
	{
		super("FastForward");
		this.playerThread = playerThread;
	//	playerThread.addChangeListener(this.playerStateHandler);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(!forwarding)
		{
			FastForwardAction.this.putValue("Name", "FastForwardx2");
		}
		if(forwarding)
		{
			switch(speed)
			{
				case 1:FastForwardAction.this.putValue("Name", "FastForwardx3");break;
				case 2:FastForwardAction.this.putValue("Name", "FastForwardx4");break;
				case 3:FastForwardAction.this.putValue("Name", "Stop");break;
			}
		}
		if(forwarding&&speed == 4)
		{
			speed = 0;
			forwarding = false;
			FastForwardAction.this.putValue("Name", "FastForward");
		}else
		{
			speed++;
			forwarding = true;
			//FastForwardAction.this.putValue("Name", "Stop");
			this.playerThread.stopPlaying();
			running = new Thread()
			{
				public void run()
				{
					long position = FastForwardAction.this.playerThread.getPlaybackPosition();
					while(forwarding&&position > 0)
					{
						position+= 10000*speed;
						if(position < 0 )
						{
							position = 0;
						}
						FastForwardAction.this.playerThread.setPlaybackPosition((int)position);
						FastForwardAction.this.playerThread.firePlaybackPositionUpdate(position);
						try {
							sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			running.start();
		}
	//	this.playerThread.setPlaybackPosition((int)this.playerThread.getPlaybackPosition()+1000);
	}
}