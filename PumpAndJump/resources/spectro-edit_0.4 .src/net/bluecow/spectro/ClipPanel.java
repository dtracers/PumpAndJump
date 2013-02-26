/*     */ package net.bluecow.spectro;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.DataBufferInt;
/*     */ import java.awt.image.WritableRaster;
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.Scrollable;
/*     */ import javax.swing.event.UndoableEditListener;
/*     */ import javax.swing.undo.AbstractUndoableEdit;
/*     */ import javax.swing.undo.CannotRedoException;
/*     */ import javax.swing.undo.CannotUndoException;
/*     */ import javax.swing.undo.UndoableEdit;
/*     */ import javax.swing.undo.UndoableEditSupport;
/*     */ 
/*     */ public class ClipPanel extends JPanel
/*     */   implements Scrollable
/*     */ {
/*  55 */   private static final Logger logger = Logger.getLogger(ClipPanel.class.getName());
/*     */   private final Clip clip;
/*     */   private final BufferedImage img;
/*     */   private final int[] imgPixels;
/*     */   private Rectangle region;
/*     */   private Rectangle oldRegion;
/*     */   private boolean regionMode;
/*  86 */   private final RegionMouseHandler mouseHandler = new RegionMouseHandler(null);
/*     */ 
/*  88 */   private final ClipPositionHeader clipPositionHeader = new ClipPositionHeader();
/*     */   private boolean undoing;
/*  96 */   private ValueColorizer colorizer = new LogarithmicColorizer(this);
/*     */ 
/*  98 */   private ClipDataChangeListener clipDataChangeHandler = new ClipDataChangeListener()
/*     */   {
/*     */     public void clipDataChanged(ClipDataChangeEvent e) {
/* 101 */       Rectangle r = ClipPanel.this.toScreenCoords(e.getRegion());
/* 102 */       ClipPanel.this.updateImage(r);
/* 103 */       ClipPanel.this.repaint(r);
/*     */     }
/*  98 */   };
/*     */ 
/* 108 */   private final UndoableEditSupport undoSupport = new UndoableEditSupport(this);
/*     */ 
/*     */   public static ClipPanel newInstance(Clip clip, PlayerThread playerThread) {
/* 111 */     ClipPanel cp = new ClipPanel(clip);
/* 112 */     clip.addClipDataChangeListener(cp.clipDataChangeHandler);
/* 113 */     playerThread.addPlaybackPositionListener(cp.clipPositionHeader);
/* 114 */     cp.clipPositionHeader.setPlayerThread(playerThread);
/* 115 */     return cp;
/*     */   }
/*     */ 
/*     */   private ClipPanel(Clip clip) {
/* 119 */     this.clip = clip;
/* 120 */     setPreferredSize(new Dimension(clip.getFrameCount(), clip.getFrameFreqSamples()));
/* 121 */     this.img = new BufferedImage(clip.getFrameCount(), clip.getFrameFreqSamples(), 1);
/* 122 */     this.imgPixels = ((DataBufferInt)this.img.getRaster().getDataBuffer()).getData();
/* 123 */     updateImage(null);
/* 124 */     setBackground(Color.BLACK);
/*     */   }
/*     */ 
/*     */   public Point toClipCoords(Point p)
/*     */   {
/* 140 */     p.y = (this.clip.getFrameFreqSamples() - p.y);
/* 141 */     return p;
/*     */   }
/*     */ 
/*     */   public Rectangle toClipCoords(Rectangle r)
/*     */   {
/* 167 */     r.y = (this.clip.getFrameFreqSamples() - (r.y + r.height));
/* 168 */     return r;
/*     */   }
/*     */ 
/*     */   public Point toScreenCoords(Point p)
/*     */   {
/* 184 */     p.y = (this.clip.getFrameFreqSamples() - p.y);
/* 185 */     return p;
/*     */   }
/*     */ 
/*     */   public Rectangle toScreenCoords(Rectangle r)
/*     */   {
/* 190 */     return toClipCoords(r);
/*     */   }
/*     */ 
/*     */   public ValueColorizer getColorizer() {
/* 194 */     return this.colorizer;
/*     */   }
/*     */ 
/*     */   void updateImage(Rectangle region)
/*     */   {
/* 211 */     if (this.clip == null) return;
/* 212 */     if (region == null)
/* 213 */       region = new Rectangle(0, 0, this.clip.getFrameCount(), this.clip.getFrameFreqSamples());
/*     */     else {
/* 215 */       region = new Rectangle(region);
/*     */     }
/*     */ 
/* 218 */     toClipCoords(region);
/*     */ 
/* 220 */     int endCol = region.x + region.width;
/* 221 */     int endRow = region.y + region.height;
/*     */ 
/* 223 */     for (int col = region.x; col < endCol; col++) {
/* 224 */       Frame f = this.clip.getFrame(col);
/* 225 */       for (int row = region.y; row < endRow; row++)
/*     */       {
/* 227 */         this.imgPixels[(col + row * this.img.getWidth())] = this.colorizer.colorFor(f.getReal(row));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/* 234 */     super.paintComponent(g);
/* 235 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 238 */     AffineTransform backupTransform = g2.getTransform();
/* 239 */     g2.translate(0, this.img.getHeight());
/* 240 */     g2.scale(1.0D, -1.0D);
/*     */ 
/* 242 */     Rectangle clipBounds = g2.getClipBounds();
/* 243 */     logger.finer(String.format("Clip bounds: (%d, %d) %dx%d", new Object[] { Integer.valueOf(clipBounds.x), Integer.valueOf(clipBounds.y), Integer.valueOf(clipBounds.width), Integer.valueOf(clipBounds.height) }));
/* 244 */     if (clipBounds.x + clipBounds.width > this.img.getWidth()) {
/* 245 */       clipBounds.width = (this.img.getWidth() - clipBounds.x);
/*     */     }
/* 247 */     if (clipBounds != null) {
/* 248 */       g2.drawImage(this.img, clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height, clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height, Color.BLACK, null);
/*     */     }
/*     */     else
/*     */     {
/* 253 */       g2.drawImage(this.img, 0, 0, null);
/*     */     }
/*     */ 
/* 257 */     g2.setTransform(backupTransform);
/* 258 */     if (this.region != null) {
/* 259 */       g2.setColor(Color.YELLOW);
/* 260 */       g2.drawRect(this.region.x, this.region.y, this.region.width, this.region.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Clip getClip()
/*     */   {
/* 267 */     return this.clip;
/*     */   }
/*     */ 
/*     */   private void repaintRegion()
/*     */   {
/* 276 */     Rectangle newRegion = this.region == null ? null : new Rectangle(this.region);
/* 277 */     if ((this.oldRegion != null) && (newRegion == null)) {
/* 278 */       repaint(this.oldRegion.x, this.oldRegion.y, this.oldRegion.width + 1, this.oldRegion.height + 1);
/* 279 */     } else if ((this.oldRegion == null) && (newRegion != null)) {
/* 280 */       repaint(newRegion.x, newRegion.y, newRegion.width + 1, newRegion.height + 1);
/* 281 */     } else if ((this.oldRegion != null) && (newRegion != null)) {
/* 282 */       this.oldRegion.add(newRegion);
/* 283 */       repaint(this.oldRegion.x, this.oldRegion.y, this.oldRegion.width + 1, this.oldRegion.height + 1);
/*     */     }
/* 285 */     this.oldRegion = newRegion;
/*     */   }
/*     */ 
/*     */   public void setRegionMode(boolean on)
/*     */   {
/* 296 */     if (on != this.regionMode)
/* 297 */       if (on) {
/* 298 */         this.regionMode = true;
/* 299 */         addMouseListener(this.mouseHandler);
/* 300 */         addMouseMotionListener(this.mouseHandler);
/*     */       } else {
/* 302 */         this.regionMode = false;
/* 303 */         setRegion(null);
/* 304 */         this.oldRegion = null;
/* 305 */         removeMouseListener(this.mouseHandler);
/* 306 */         removeMouseMotionListener(this.mouseHandler);
/*     */       }
/*     */   }
/*     */ 
/*     */   public Rectangle getRegion()
/*     */   {
/* 319 */     if (this.region == null) {
/* 320 */       return null;
/*     */     }
/* 322 */     return new Rectangle(this.region);
/*     */   }
/*     */ 
/*     */   private void setRegion(Rectangle r)
/*     */   {
/* 332 */     Rectangle oldRegion = this.region;
/* 333 */     this.region = normalized(r);
/* 334 */     repaintRegion();
/* 335 */     firePropertyChange("region", oldRegion, this.region);
/* 336 */     if (!this.undoing)
/* 337 */       this.undoSupport.postEdit(new RegionMoveEdit(oldRegion, null));
/*     */   }
/*     */ 
/*     */   private Rectangle normalized(Rectangle rect)
/*     */   {
/* 448 */     if (rect == null) return null;
/* 449 */     rect = new Rectangle(rect);
/* 450 */     if (rect.width < 0) {
/* 451 */       rect.x += rect.width;
/* 452 */       rect.width *= -1;
/*     */     }
/* 454 */     if (rect.height < 0) {
/* 455 */       rect.y += rect.height;
/* 456 */       rect.height *= -1;
/*     */     }
/* 458 */     return rect;
/*     */   }
/*     */ 
/*     */   public void addNotify()
/*     */   {
/* 514 */     super.addNotify();
/* 515 */     Component p = getParent();
/* 516 */     if (((p instanceof JViewport)) && ((p.getParent() instanceof JScrollPane))) {
/* 517 */       JScrollPane sp = (JScrollPane)p.getParent();
/* 518 */       sp.setColumnHeaderView(this.clipPositionHeader);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredScrollableViewportSize()
/*     */   {
/* 582 */     return getPreferredSize();
/*     */   }
/*     */ 
/*     */   public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
/* 586 */     return (int)(visibleRect.width * 0.9D);
/*     */   }
/*     */ 
/*     */   public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
/* 590 */     return 50;
/*     */   }
/*     */ 
/*     */   public boolean getScrollableTracksViewportHeight() {
/* 594 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean getScrollableTracksViewportWidth() {
/* 598 */     return false;
/*     */   }
/*     */ 
/*     */   public void addUndoableEditListener(UndoableEditListener l) {
/* 602 */     this.undoSupport.addUndoableEditListener(l);
/*     */   }
/*     */ 
/*     */   public void removeUndoableEditListener(UndoableEditListener l) {
/* 606 */     this.undoSupport.removeUndoableEditListener(l);
/*     */   }
/*     */ 
/*     */   private class ClipPositionHeader extends JPanel
/*     */     implements PlaybackPositionListener
/*     */   {
/*     */     private PlayerThread playerThread;
/*     */     volatile long playbackPosition;
/* 570 */     private final MouseListener repositionHandler = new MouseAdapter()
/*     */     {
/*     */       public void mousePressed(MouseEvent e) {
/* 573 */         ClipPanel.ClipPositionHeader.this.playerThread.setPlaybackPosition(e.getX() * ClipPanel.this.clip.getFrameTimeSamples());
/*     */       }
/* 570 */     };
/*     */ 
/*     */     public ClipPositionHeader()
/*     */     {
/* 532 */       setPreferredSize(new Dimension(1, 20));
/* 533 */       addMouseListener(this.repositionHandler);
/*     */     }
/*     */ 
/*     */     public void setPlayerThread(PlayerThread playerThread) {
/* 537 */       this.playerThread = playerThread;
/*     */     }
/*     */ 
/*     */     public void playbackPositionUpdate(PlaybackPositionEvent e) {
/* 541 */       int oldPixelPosition = playbackPixelPosition();
/* 542 */       this.playbackPosition = e.getSamplePos();
/* 543 */       int newPixelPosition = playbackPixelPosition();
/* 544 */       if (newPixelPosition >= oldPixelPosition)
/* 545 */         repaint(oldPixelPosition, 0, newPixelPosition - oldPixelPosition + 1, getHeight());
/*     */       else
/* 547 */         repaint(newPixelPosition, 0, oldPixelPosition - newPixelPosition + 1, getHeight());
/*     */     }
/*     */ 
/*     */     protected void paintComponent(Graphics g)
/*     */     {
/* 553 */       super.paintComponent(g);
/* 554 */       g.setColor(Color.BLACK);
/*     */ 
/* 556 */       g.drawRect(playbackPixelPosition(), 0, 1, getHeight());
/*     */     }
/*     */ 
/*     */     private int playbackPixelPosition()
/*     */     {
/* 564 */       return (int)(this.playbackPosition / ClipPanel.this.clip.getFrameTimeSamples());
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class RegionMoveEdit extends AbstractUndoableEdit
/*     */   {
/*     */     private Rectangle oldr;
/* 463 */     private final Rectangle newr = ClipPanel.this.region;
/*     */ 
/*     */     private RegionMoveEdit(Rectangle oldRegion) {
/* 466 */       this.oldr = oldRegion;
/*     */     }
/*     */ 
/*     */     public void undo() throws CannotUndoException
/*     */     {
/* 471 */       super.undo();
/* 472 */       ClipPanel.this.undoing = true;
/* 473 */       ClipPanel.this.setRegion(this.oldr);
/* 474 */       ClipPanel.this.undoing = false;
/*     */     }
/*     */ 
/*     */     public void redo() throws CannotRedoException
/*     */     {
/* 479 */       super.redo();
/* 480 */       ClipPanel.this.undoing = true;
/* 481 */       ClipPanel.this.setRegion(this.newr);
/* 482 */       ClipPanel.this.undoing = false;
/*     */     }
/*     */ 
/*     */     public boolean isSignificant()
/*     */     {
/* 487 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean replaceEdit(UndoableEdit anEdit)
/*     */     {
/* 492 */       if ((anEdit instanceof RegionMoveEdit)) {
/* 493 */         RegionMoveEdit replaceMe = (RegionMoveEdit)anEdit;
/* 494 */         this.oldr = replaceMe.oldr;
/* 495 */         replaceMe.die();
/* 496 */         return true;
/*     */       }
/* 498 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 504 */       return "Region move: " + this.oldr + " -> " + this.newr;
/*     */     }
/*     */   }
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
/* 368 */       switch (ClipPanel.2.$SwitchMap$net$bluecow$spectro$ClipPanel$MouseMode[this.mode.ordinal()]) {
/*     */       case 1:
/* 370 */         startRect(e.getPoint());
/* 371 */         break;
/*     */       case 2:
/* 373 */         resizeRect(e.getPoint());
/* 374 */         break;
/*     */       case 3:
/* 376 */         moveRect(e.getPoint());
/*     */       }
/*     */ 
/* 379 */       ClipPanel.this.setRegion(this.tempRegion);
/*     */     }
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
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.ClipPanel
 * JD-Core Version:    0.6.1
 */