/*    */ package net.bluecow.spectro.painting;
/*    */
/*    */ import java.awt.Point;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.awt.event.MouseListener;
/*    */ import java.awt.event.MouseMotionListener;

import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.clipAndFrame.Frame;
/*    */
/*    */ public class PaintbrushTool
/*    */ {
/* 16 */   int radius = 5;
/*    */   private final ClipPanel clipPanel;
/*    */   private final Clip clip;
/* 19 */   private final PaintbrushMouseHandler mouseHandler = new PaintbrushMouseHandler();
/*    */
/*    */   public PaintbrushTool(ClipPanel clipPanel) {
/* 22 */     this.clipPanel = clipPanel;
/* 23 */     this.clip = clipPanel.getClip();
/* 24 */     clipPanel.addMouseListener(this.mouseHandler);
/* 25 */     clipPanel.addMouseMotionListener(this.mouseHandler);
/*    */   }
/*    */
/*    */   public void discard() {
/* 29 */     this.clipPanel.removeMouseListener(this.mouseHandler);
/* 30 */     this.clipPanel.removeMouseMotionListener(this.mouseHandler);
/*    */   }
/*    */
/*    */   public int getRadius()
/*    */   {
/* 77 */     return this.radius;
/*    */   }
/*    */
/*    */   private class PaintbrushMouseHandler
/*    */     implements MouseMotionListener, MouseListener
/*    */   {
/*    */     private PaintbrushMouseHandler()
/*    */     {
/*    */     }
/*    */
/*    */     public void mouseDragged(MouseEvent e)
/*    */     {
/* 36 */       Point p = PaintbrushTool.this.clipPanel.toClipCoords(e.getPoint());
/* 37 */       for (int x = p.x - PaintbrushTool.this.radius; x < p.x + PaintbrushTool.this.radius; x++) {
/* 38 */         Frame f = PaintbrushTool.this.clip.getFrame(x);
/* 39 */         for (int y = p.y - PaintbrushTool.this.radius; y < p.y + PaintbrushTool.this.radius; y++) {
/* 40 */           f.setReal(y, 0.0D);
/*    */         }
/*    */       }
/* 43 */       Rectangle updateRegion = new Rectangle(e.getX() - PaintbrushTool.this.radius, e.getY() - PaintbrushTool.this.radius, PaintbrushTool.this.radius * 2, PaintbrushTool.this.radius * 2);
/* 44 */       PaintbrushTool.this.clipPanel.updateImage(updateRegion);
/* 45 */       PaintbrushTool.this.clipPanel.repaint(e.getX() - PaintbrushTool.this.radius, e.getY() - PaintbrushTool.this.radius, PaintbrushTool.this.radius * 2, PaintbrushTool.this.radius * 2);
/*    */     }
/*    */
/*    */     public void mouseMoved(MouseEvent e)
/*    */     {
/*    */     }
/*    */
/*    */     public void mouseClicked(MouseEvent e)
/*    */     {
/*    */     }
/*    */
/*    */     public void mouseEntered(MouseEvent e)
/*    */     {
/*    */     }
/*    */
/*    */     public void mouseExited(MouseEvent e)
/*    */     {
/*    */     }
/*    */
/*    */     public void mousePressed(MouseEvent e)
/*    */     {
/* 67 */       mouseDragged(e);
/*    */     }
/*    */
/*    */     public void mouseReleased(MouseEvent e)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.PaintbrushTool
 * JD-Core Version:    0.6.1
 */