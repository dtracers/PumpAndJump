/*    */ package net.bluecow.spectro;
/*    */ 
/*    */ import javax.swing.Box;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JSlider;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ 
/*    */ public class GreyscaleLogColorizer
/*    */   implements ValueColorizer
/*    */ {
/* 28 */   double multiplier = 6000.0D;
/*    */   private final ClipPanel clipPanel;
/*    */   private final JComponent settingsPanel;
/*    */ 
/*    */   GreyscaleLogColorizer(ClipPanel clipPanel)
/*    */   {
/* 34 */     this.clipPanel = clipPanel;
/* 35 */     final JSlider brightness = new JSlider(0, 5000000, (int)(this.multiplier * 100.0D));
/* 36 */     brightness.addChangeListener(new ChangeListener() {
/*    */       public void stateChanged(ChangeEvent e) {
/* 38 */         GreyscaleLogColorizer.this.setMultiplier(brightness.getValue() / 100.0D);
/*    */       }
/*    */     });
/* 41 */     this.settingsPanel = Box.createVerticalBox();
/* 42 */     this.settingsPanel.add(new JLabel("Brightness"));
/* 43 */     this.settingsPanel.add(brightness);
/*    */   }
/*    */ 
/*    */   public int colorFor(double val) {
/* 47 */     int greyVal = (int)(this.multiplier * Math.abs(val));
/* 48 */     if (greyVal < 0)
/* 49 */       greyVal = 0;
/* 50 */     else if (greyVal > 255) {
/* 51 */       greyVal = 255;
/*    */     }
/* 53 */     return greyVal << 16 | greyVal << 8 | greyVal;
/*    */   }
/*    */ 
/*    */   public void setMultiplier(double multiplier) {
/* 57 */     this.multiplier = multiplier;
/* 58 */     this.clipPanel.updateImage(null);
/* 59 */     this.clipPanel.repaint();
/*    */   }
/*    */ 
/*    */   public JComponent getSettingsPanel() {
/* 63 */     return this.settingsPanel;
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.GreyscaleLogColorizer
 * JD-Core Version:    0.6.1
 */