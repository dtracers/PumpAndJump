/*     */ package net.bluecow.spectro;
/*     */
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.prefs.BackingStoreException;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.SwingUtilities;
/*     */ import net.bluecow.spectro.action.PlayPauseAction;
/*     */ import net.bluecow.spectro.action.RewindAction;
/*     */ import net.bluecow.spectro.action.SaveAction;
/*     */ import net.bluecow.spectro.action.UndoRedoAction;
import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.painting.ClipPanel;
import net.bluecow.spectro.painting.ClipPlayBackLocation;
import net.bluecow.spectro.painting.UndoManager;
/*     */ import net.bluecow.spectro.tool.ToolboxPanel;
/*     */
public class SpectroEditSession
{

	private static final Preferences prefs = Preferences.userNodeForPackage(SpectroEditSession.class);

	private final UndoManager undoManager = new UndoManager();
	private final PlayerThread playerThread;
	private final ClipPanel clipPanel;

	protected SpectroEditSession(Clip c) throws LineUnavailableException
	{
		this.playerThread = new PlayerThread(c);
		this.playerThread.start();

		this.clipPanel = ClipPanel.newInstance(c, this.playerThread);
		this.clipPanel.addUndoableEditListener(this.undoManager);

		final JFrame f = new JFrame("Spectro-Edit ME");
		f.setDefaultCloseOperation(3);
		f.setLayout(new BorderLayout());

		JSplitPane splitPane = new JSplitPane(1);

		splitPane.setTopComponent(new ToolboxPanel(this).getPanel());

		//creates a scroll pane
		JScrollPane pane = new JScrollPane(this.clipPanel);
		//adds the playback listener that moves the scroll pane
		ClipPlayBackLocation playLoc = new ClipPlayBackLocation(clipPanel.getPlaybackPoint(),clipPanel.getOffsetPoint(),pane,clipPanel.getTotalSamples());
		this.playerThread.addPlaybackPositionListener(playLoc);

		this.clipPanel.setClipPlayLoc(playLoc);

		splitPane.setBottomComponent(pane);
		f.add(splitPane, "Center");

		JToolBar toolbar = new JToolBar();
		toolbar.add(new SaveAction(c, f));
		toolbar.add(UndoRedoAction.createUndoInstance(this.undoManager));
		toolbar.add(UndoRedoAction.createRedoInstance(this.undoManager));
		toolbar.addSeparator();

		toolbar.add(new PlayPauseAction(this.playerThread));
		toolbar.add(new RewindAction(this.playerThread));
		f.add(toolbar, "North");

		if (prefs.get("frameBounds", null) != null)
		{
			String[] frameBounds = prefs.get("frameBounds", null).split(",");
			if (frameBounds.length == 4)
			{
				f.setBounds(Integer.parseInt(frameBounds[0]), Integer.parseInt(frameBounds[1]), Integer.parseInt(frameBounds[2]), Integer.parseInt(frameBounds[3]));
			}

		}
		else
		{
			f.pack();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			f.setSize(Math.min(screenSize.width - 50, f.getWidth()), Math.min(screenSize.height - 50, f.getHeight()));

			f.setLocationRelativeTo(null);

		}
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				try
				{
					SpectroEditSession.prefs.put("frameBounds", String.format("%d,%d,%d,%d", new Object[] { Integer.valueOf(f.getX()), Integer.valueOf(f.getY()), Integer.valueOf(f.getWidth()), Integer.valueOf(f.getHeight()) }));
					SpectroEditSession.prefs.flush();
				}
				catch (BackingStoreException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		f.setVisible(true);
	}

	public static SpectroEditSession createSession(File wavFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
			Clip c = new Clip(wavFile);
			SpectroEditSession session = new SpectroEditSession(c);
			c.addUndoableEditListener(session.undoManager);
			return session;
	}
	public static void main(String[] args) throws Exception
	{
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Spectro-Edit");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		final JFrame f = new JFrame("Dummy frame for owning dialogs");
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					FileDialog fd = new FileDialog(f, "Choose a 16-bit WAV or AIFF file");
					fd.setVisible(true);
					String dir = fd.getDirectory();
					String file = fd.getFile();
					if ((dir == null) || (file == null))
					{
						JOptionPane.showMessageDialog(null, "Ok, maybe next time");
						System.exit(0);
					}
					File wavFile = new File(dir, file);
					SpectroEditSession.createSession(wavFile);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Sorry, couldn't read your sample:\n" + e.getMessage() + "\nBe sure your file is 16-bit WAV or AIFF!");
					System.exit(0);

				}

			}

		});

	}
	public void undo()
	{
		this.undoManager.undo();
	}

	public void redo() {
		this.undoManager.redo();
	}

	public ClipPanel getClipPanel() {
		return this.clipPanel;
	}
	public UndoManager getUndoManager() {
		return this.undoManager;
	}
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.SpectroEditSession
 * JD-Core Version:    0.6.1
 */