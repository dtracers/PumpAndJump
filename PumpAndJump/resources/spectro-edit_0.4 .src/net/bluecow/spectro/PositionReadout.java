/*    */ package net.bluecow.spectro;
/*    */ 
/*    */ import java.awt.Point;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.awt.event.MouseListener;
/*    */ import java.awt.event.MouseMotionListener;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class PositionReadout
/*    */ {
/*    */   private final ClipPanel cp;
/* 33 */   private final JLabel label = new JLabel();
/*    */ 
/* 35 */   private final MouseHandler mouseHandler = new MouseHandler(null);
/*    */ 
/*    */   public PositionReadout(ClipPanel cp) {
/* 38 */     this.cp = cp;
/* 39 */     cp.addMouseMotionListener(this.mouseHandler);
/* 40 */     cp.addMouseListener(this.mouseHandler);
/*    */ 
/* 43 */     this.mouseHandler.mouseExited(null);
/*    */   }
/*    */ 
/*    */   private static String toNoteName(double freq)
/*    */   {
/* 79 */     int semitones = (int)(48.0D + 12.0D * log2(freq / 440.0D));
/* 80 */     String[] notes = { "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" };
/* 81 */     String note = notes[(semitones % 12)];
/* 82 */     String octave = String.valueOf(semitones / 12);
/* 83 */     return note + octave;
/*    */   }
/*    */ 
/*    */   private static double log2(double a)
/*    */   {
/* 91 */     return Math.log(a) / Math.log(2.0D);
/*    */   }
/*    */ 
/*    */   public JLabel getLabel() {
/* 95 */     return this.label;
/*    */   }
/*    */ 
/*    */   private class MouseHandler
/*    */     implements MouseListener, MouseMotionListener
/*    */   {
/*    */     private MouseHandler()
/*    */     {
/*    */     }
/*    */ 
/*    */     public void mouseDragged(MouseEvent e)
/*    */     {
/* 49 */       mouseMoved(e);
/*    */     }
/*    */ 
/*    */     public void mouseMoved(MouseEvent e) {
/* 53 */       double rate = PositionReadout.this.cp.getClip().getSamplingRate();
/* 54 */       double fSamples = PositionReadout.this.cp.getClip().getFrameFreqSamples();
/* 55 */       double tSamples = PositionReadout.this.cp.getClip().getFrameTimeSamples();
/* 56 */       Point p = PositionReadout.this.cp.toClipCoords(e.getPoint());
/* 57 */       double freq = rate / 2.0D / fSamples * p.getY();
/* 58 */       PositionReadout.this.label.setText(String.format("0:%06.03fs %6.0fHz (%s)", new Object[] { Double.valueOf(p.getX() * tSamples / rate), Double.valueOf(freq), PositionReadout.toNoteName(freq) }));
/* 59 */       PositionReadout.this.label.setEnabled(true);
/*    */     }
/*    */ 
/*    */     public void mouseExited(MouseEvent e) {
/* 63 */       PositionReadout.this.label.setText("(No current position)");
/* 64 */       PositionReadout.this.label.setEnabled(false);
/*    */     }
/*    */ 
/*    */     public void mouseEntered(MouseEvent e)
/*    */     {
/*    */     }
/*    */ 
/*    */     public void mouseClicked(MouseEvent e)
/*    */     {
/*    */     }
/*    */ 
/*    */     public void mousePressed(MouseEvent e)
/*    */     {
/*    */     }
/*    */ 
/*    */     public void mouseReleased(MouseEvent e)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.PositionReadout
 * JD-Core Version:    0.6.1
 */