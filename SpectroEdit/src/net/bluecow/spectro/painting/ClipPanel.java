package net.bluecow.spectro.painting;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import net.bluecow.spectro.PlayerThread;
import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.clipAndFrame.Frame;

public class ClipPanel extends JPanel
implements Scrollable
{
private static final Logger logger = Logger.getLogger(ClipPanel.class.getName());
private final Clip clip;
private final BufferedImage img;
private final int[] imgPixels;
private Rectangle region;
private Rectangle oldRegion;
private boolean regionMode;
private final RegionMouseHandler mouseHandler = new RegionMouseHandler();

private final ClipPositionHeader clipPositionHeader = new ClipPositionHeader();
private boolean undoing;
private ValueColorizer colorizer = new LogarithmicColorizer(this);

	private ClipDataChangeListener clipDataChangeHandler = new ClipDataChangeListener()
	{
		public void clipDataChanged(ClipDataChangeEvent e)
		{
			Rectangle r = ClipPanel.this.toScreenCoords(e.getRegion());
			ClipPanel.this.updateImage(r);
			ClipPanel.this.repaint(r);
		}
	};

	private final UndoableEditSupport undoSupport = new UndoableEditSupport(this);

	public static ClipPanel newInstance(Clip clip, PlayerThread playerThread)
	{
		ClipPanel cp = new ClipPanel(clip);
		clip.addClipDataChangeListener(cp.clipDataChangeHandler);
		playerThread.addPlaybackPositionListener(cp.clipPositionHeader);
		cp.clipPositionHeader.setPlayerThread(playerThread);
		return cp;
	}

	public ClipPanel(Clip clip) {
		this.clip = clip;
		System.out.println("frame count "+clip.getFrameCount());
		setPreferredSize(new Dimension(clip.getFrameCount(), clip.getFrameFreqSamples()));
		this.img = new BufferedImage(clip.getFrameCount(), clip.getFrameFreqSamples(), 1);
		this.imgPixels = ((DataBufferInt)this.img.getRaster().getDataBuffer()).getData();
		updateImage(null);
		setBackground(Color.BLACK);
	}
	public Point toClipCoords(Point p)
	{
		p.y = (this.clip.getFrameFreqSamples() - p.y);
		return p;
	}

	public Rectangle toClipCoords(Rectangle r)
	{
		r.y = (this.clip.getFrameFreqSamples() - (r.y + r.height));
		return r;
	}
	public Point toScreenCoords(Point p)
	{
		p.y = (this.clip.getFrameFreqSamples() - p.y);
		return p;
	}
	public Rectangle toScreenCoords(Rectangle r)
	{
		return toClipCoords(r);
	}

	public ValueColorizer getColorizer()
	{
		return this.colorizer;
	}

	void updateImage(Rectangle region)
	{
		if (this.clip == null) return;

		if (region == null)
			region = new Rectangle(0, 0, this.clip.getFrameCount(), this.clip.getFrameFreqSamples());
		else
		{
			region = new Rectangle(region);
		}
		toClipCoords(region);
		int endCol = region.x + region.width;
		int endRow = region.y + region.height;
		for (int col = region.x; col < endCol; col++)
		{
			Frame f = this.clip.getFrame(col);
			for (int row = region.y+26; row < endRow; row++)
			{
				this.imgPixels[(col + row * this.img.getWidth())] = this.colorizer.colorFor(f.getReal(row));
				}
			}
		}
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform backupTransform = g2.getTransform();
		g2.translate(0, this.img.getHeight());
		g2.scale(1.0D, -1.0D);

		Rectangle clipBounds = g2.getClipBounds();

		logger.finer(String.format("Clip bounds: (%d, %d) %dx%d", new Object[] { Integer.valueOf(clipBounds.x), Integer.valueOf(clipBounds.y), Integer.valueOf(clipBounds.width), Integer.valueOf(clipBounds.height) }));
		if (clipBounds.x + clipBounds.width > this.img.getWidth())
		{
			clipBounds.width = (this.img.getWidth() - clipBounds.x);
		}
		if (clipBounds != null)
		{
			g2.drawImage(this.img, clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height, clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height, Color.BLACK, null);
		}
		else
		{
			g2.drawImage(this.img, 0, 0, null);
		}
		g2.setTransform(backupTransform);
		if (this.region != null)
		{
			g2.setColor(Color.YELLOW);
			g2.drawRect(this.region.x, this.region.y, this.region.width, this.region.height);
		}
	}

	public Clip getClip()
	{
		return this.clip;
	}

	private void repaintRegion()
	{
		Rectangle newRegion = this.region == null ? null : new Rectangle(this.region);
		if ((this.oldRegion != null) && (newRegion == null))
		{
			repaint(this.oldRegion.x, this.oldRegion.y, this.oldRegion.width + 1, this.oldRegion.height + 1);
		}
		else if ((this.oldRegion == null) && (newRegion != null))
		{
			repaint(newRegion.x, newRegion.y, newRegion.width + 1, newRegion.height + 1);
		} else if ((this.oldRegion != null) && (newRegion != null))
		{
			this.oldRegion.add(newRegion);
			repaint(this.oldRegion.x, this.oldRegion.y, this.oldRegion.width + 1, this.oldRegion.height + 1);
		}
		this.oldRegion = newRegion;
	}
	public void setRegionMode(boolean on)
	{
		if (on != this.regionMode)
		{
			if (on)
			{
				this.regionMode = true;
				addMouseListener(this.mouseHandler);
				addMouseMotionListener(this.mouseHandler);
			} else
			{
				this.regionMode = false;
				setRegion(null);
				this.oldRegion = null;
				removeMouseListener(this.mouseHandler);
				removeMouseMotionListener(this.mouseHandler);
			}
		}
	}

	public Rectangle getRegion()
	{
		if (this.region == null)
		{
			return null;
		}
		return new Rectangle(this.region);
	}

	private void setRegion(Rectangle r)
	{
		Rectangle oldRegion = this.region;
		this.region = normalized(r);
		repaintRegion();
		firePropertyChange("region", oldRegion, this.region);
		if (!this.undoing)
			this.undoSupport.postEdit(new RegionMoveEdit(oldRegion));
		}

	private Rectangle normalized(Rectangle rect)
	{
		if (rect == null) return null;

		rect = new Rectangle(rect);

		if (rect.width < 0)
		{
			rect.x += rect.width;
			rect.width *= -1;
		}

		if (rect.height < 0)
		{
			rect.y += rect.height;
			rect.height *= -1;
		}
		return rect;
	}

	public void addNotify()
	{

		super.addNotify();
		Component p = getParent();
		if (((p instanceof JViewport)) && ((p.getParent() instanceof JScrollPane))) {
			JScrollPane sp = (JScrollPane)p.getParent();
			sp.setColumnHeaderView(this.clipPositionHeader);
			}
		}

	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return (int)(visibleRect.width * 0.9D);
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 50;
	}

	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}

	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}

	public void addUndoableEditListener(UndoableEditListener l)
	{
		this.undoSupport.addUndoableEditListener(l);
	}

	public void removeUndoableEditListener(UndoableEditListener l)
	{
		this.undoSupport.removeUndoableEditListener(l);
	}

	private class ClipPositionHeader extends JPanel implements PlaybackPositionListener
	{
		private PlayerThread playerThread;
		volatile long playbackPosition;
		private final MouseListener repositionHandler = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				ClipPanel.ClipPositionHeader.this.playerThread.setPlaybackPosition(e.getX() * ClipPanel.this.clip.getFrameTimeSamples());
				}
		};

		public ClipPositionHeader()
		{
			setPreferredSize(new Dimension(1, 20));
			addMouseListener(this.repositionHandler);
		}

		public void setPlayerThread(PlayerThread playerThread)
		{
			this.playerThread = playerThread;
		}

		public void playbackPositionUpdate(PlaybackPositionEvent e)
		{
			int oldPixelPosition = playbackPixelPosition();
			this.playbackPosition = e.getSamplePos();
			int newPixelPosition = playbackPixelPosition();
			if (newPixelPosition >= oldPixelPosition)
				repaint(oldPixelPosition, 0, newPixelPosition - oldPixelPosition + 1, getHeight());
			else
				repaint(newPixelPosition, 0, oldPixelPosition - newPixelPosition + 1, getHeight());
		}
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawRect(playbackPixelPosition(), 0, 1, getHeight());
		}
		private int playbackPixelPosition()
		{
			return (int)(this.playbackPosition / ClipPanel.this.clip.getFrameTimeSamples());
		}
	}

	private final class RegionMoveEdit extends AbstractUndoableEdit
	{
		private Rectangle oldr;
		private final Rectangle newr = ClipPanel.this.region;
		private RegionMoveEdit(Rectangle oldRegion)
		{
			this.oldr = oldRegion;
		}
		public void undo() throws CannotUndoException
		{
			super.undo();
			ClipPanel.this.undoing = true;
			ClipPanel.this.setRegion(this.oldr);
			ClipPanel.this.undoing = false;
		}

		public void redo() throws CannotRedoException
		{
			super.redo();
			ClipPanel.this.undoing = true;
			ClipPanel.this.setRegion(this.newr);
			ClipPanel.this.undoing = false;
		}
		public boolean isSignificant()
		{
			return false;
		}

		public boolean replaceEdit(UndoableEdit anEdit)
		{
			if ((anEdit instanceof RegionMoveEdit))
			{
				RegionMoveEdit replaceMe = (RegionMoveEdit)anEdit;
				this.oldr = replaceMe.oldr;
				replaceMe.die();
				return true;
			}
			return false;
		}

		public String toString()
		{
			return "Region move: " + this.oldr + " -> " + this.newr;
			}
		}
/*     */
/*     */   private class RegionMouseHandler
/*     */     implements MouseMotionListener, MouseListener
/*     */   {
/* 352 */     ClipPanel.MouseMode mode = ClipPanel.MouseMode.IDLE;
/*     */     Point moveHandle;
/*     */     Rectangle tempRegion;
/*     */
/*     */     private RegionMouseHandler()
/*     */     {
/*     */     }
/*     */
/*     */     public void mouseDragged(MouseEvent e)
/*     */     {
/*
       switch (ClipPanel.MouseMode[this.mode.ordinal()]) {
       case 1:
         startRect(e.getPoint());
         break;
       case 2:
         resizeRect(e.getPoint());
         break;
       case 3:
         moveRect(e.getPoint());
       }

       ClipPanel.this.setRegion(this.tempRegion);
       */
    }

/*     */
/*     */     public void mousePressed(MouseEvent e) {
/* 383 */       this.tempRegion = ClipPanel.this.normalized(ClipPanel.this.region);
/* 384 */       Point p = e.getPoint();
/* 385 */       if ((this.tempRegion != null) && (this.tempRegion.contains(p))) {
/* 386 */         this.mode = ClipPanel.MouseMode.MOVING;
/* 387 */         this.moveHandle = new Point(p.x - this.tempRegion.x, p.y - this.tempRegion.y);
/*     */       } else {
/* 389 */         startRect(p);
/* 390 */         this.mode = ClipPanel.MouseMode.SIZING;
/*     */       }
/* 392 */       ClipPanel.this.setRegion(this.tempRegion);
/*     */     }
/*     */
/*     */     public void mouseReleased(MouseEvent e) {
/* 396 */       this.mode = ClipPanel.MouseMode.IDLE;
/*     */
/* 398 */       ClipPanel.this.setRegion(this.tempRegion);
/* 399 */       this.tempRegion = null;
/*     */     }
/*     */
/*     */     public void mouseMoved(MouseEvent e)
/*     */     {
/*     */     }
/*     */
/*     */     public void mouseClicked(MouseEvent e)
/*     */     {
/*     */     }
/*     */
/*     */     public void mouseEntered(MouseEvent e)
/*     */     {
/*     */     }
/*     */
/*     */     public void mouseExited(MouseEvent e)
/*     */     {
/*     */     }
/*     */
/*     */     private void startRect(Point p) {
/* 419 */       this.tempRegion = new Rectangle(p.x, p.y, 0, 0);
/*     */     }
/*     */
/*     */     private void resizeRect(Point p) {
/* 423 */       this.tempRegion.width = (p.x - this.tempRegion.x);
/* 424 */       this.tempRegion.height = (p.y - this.tempRegion.y);
/* 425 */       ClipPanel.logger.finer("Resizing region to: " + this.tempRegion);
/*     */     }
/*     */
/*     */     private void moveRect(Point p) {
/* 429 */       this.tempRegion.x = (p.x - this.moveHandle.x);
/* 430 */       this.tempRegion.y = (p.y - this.moveHandle.y);
/*     */     }
/*     */   }
/*     */
/*     */   static enum MouseMode
/*     */   {
/* 344 */     IDLE, SIZING, MOVING;
/*     */   }
/*     */
public void setSpectralToScreenMultiplier(double d) {
} }