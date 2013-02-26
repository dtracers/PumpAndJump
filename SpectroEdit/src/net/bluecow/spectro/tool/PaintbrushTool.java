/*     */ package net.bluecow.spectro.tool;
/*     */
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import net.bluecow.spectro.ClipPanel;
/*     */ import net.bluecow.spectro.SpectroEditSession;
import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.clipAndFrame.Frame;
/*     */
/*     */ public class PaintbrushTool
/*     */   implements Tool
/*     */ {
/*     */   private ClipPanel clipPanel;
/*     */   private Clip clip;
/*  45 */   private final PaintbrushMouseHandler mouseHandler = new PaintbrushMouseHandler();
/*     */   private final Box settingsPanel;
/*     */   private final JSlider brushSlider;
/*     */   private final JLabel brushSizeLabel;
/*     */
/*     */   public PaintbrushTool()
/*     */   {
/*  57 */     this.settingsPanel = Box.createVerticalBox();
/*  58 */     this.settingsPanel.add(this.brushSizeLabel = new JLabel());
/*  59 */     this.settingsPanel.add(this.brushSlider = new JSlider(1, 20, 1));
/*  60 */     this.settingsPanel.add(Box.createGlue());
/*     */
/*  62 */     this.brushSlider.setOpaque(false);
/*  63 */     this.brushSlider.addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/*  65 */         PaintbrushTool.this.brushSizeLabel.setText("Paintbrush size: " + PaintbrushTool.this.brushSlider.getValue());
/*     */       }
/*     */     });
/*  68 */     this.brushSlider.setValue(5);
/*     */   }
/*     */
/*     */   public String getName() {
/*  72 */     return "Paintbrush";
/*     */   }
/*     */
/*     */   public void activate(SpectroEditSession session) {
/*  76 */     this.clipPanel = session.getClipPanel();
/*  77 */     this.clip = this.clipPanel.getClip();
/*  78 */     this.clipPanel.setRegionMode(false);
/*  79 */     this.clipPanel.addMouseListener(this.mouseHandler);
/*  80 */     this.clipPanel.addMouseMotionListener(this.mouseHandler);
/*     */   }
/*     */
/*     */   public void deactivate() {
/*  84 */     this.clipPanel.removeMouseListener(this.mouseHandler);
/*  85 */     this.clipPanel.removeMouseMotionListener(this.mouseHandler);
/*  86 */     this.clip = null;
/*  87 */     this.clipPanel = null;
/*     */   }
/*     */
/*     */   public JComponent getSettingsPanel()
/*     */   {
/* 137 */     return this.settingsPanel;
/*     */   }
/*     */
/*     */   public String toString()
/*     */   {
/* 142 */     return "Paintbrush";
/*     */   }
/*     */
/*     */   private class PaintbrushMouseHandler
/*     */     implements MouseMotionListener, MouseListener
/*     */   {
/*     */     private PaintbrushMouseHandler()
/*     */     {
/*     */     }
/*     */
/*     */     public void mouseDragged(MouseEvent e)
/*     */     {
/*  93 */       Point p = PaintbrushTool.this.clipPanel.toClipCoords(e.getPoint());
/*  94 */       int radius = PaintbrushTool.this.brushSlider.getValue();
/*  95 */       Rectangle updateRegion = new Rectangle(p.x - radius, p.y - radius, radius * 2, radius * 2);
/*     */
/*  99 */       PaintbrushTool.this.clip.beginEdit(updateRegion, "Brush stroke");
/* 100 */       for (int x = p.x - radius; x < p.x + radius; x++) {
/* 101 */         Frame f = PaintbrushTool.this.clip.getFrame(x);
/* 102 */         for (int y = p.y - radius; y < p.y + radius; y++) {
/* 103 */           f.setReal(y, 0.0D);
/*     */         }
/*     */       }
/* 106 */       PaintbrushTool.this.clip.endEdit();
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
/*     */     public void mousePressed(MouseEvent e) {
/* 126 */       PaintbrushTool.this.clip.beginCompoundEdit("Painting");
/* 127 */       mouseDragged(e);
/*     */     }
/*     */
/*     */     public void mouseReleased(MouseEvent e) {
/* 131 */       PaintbrushTool.this.clip.endCompoundEdit();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.PaintbrushTool
 * JD-Core Version:    0.6.1
 */