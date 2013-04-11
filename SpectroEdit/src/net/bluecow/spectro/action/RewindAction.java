package net.bluecow.spectro.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bluecow.spectro.PlayerThread;

public class RewindAction extends AbstractAction
{
	private final PlayerThread playerThread;

	boolean rewinding = false;
	Thread running;

	private final ChangeListener playerStateHandler = new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
	   SwingUtilities.invokeLater(new Runnable() {
	   public void run() {
	     if (RewindAction.this.playerThread.isPlaying())
	    	 RewindAction.this.putValue("Name", "Stop");
        else
        	RewindAction.this.putValue("Name", "Rewind");
  }
   });
     }
	};

	public RewindAction(PlayerThread playerThread)
	{
		super("Rewind");
		this.playerThread = playerThread;
	//	playerThread.addChangeListener(this.playerStateHandler);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(rewinding)
		{
			rewinding = false;
			RewindAction.this.putValue("Name", "Rewind");

		}else
		{
			rewinding = true;
			RewindAction.this.putValue("Name", "Stop");
			this.playerThread.stopPlaying();
			running = new Thread()
			{
				public void run()
				{
					long position = RewindAction.this.playerThread.getPlaybackPosition();
					while(rewinding&&position > 0)
					{
						position-= 10000;
						if(position < 0 )
						{
							position = 0;
						}
						RewindAction.this.playerThread.setPlaybackPosition((int)position);
						RewindAction.this.playerThread.firePlaybackPositionUpdate(position);
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