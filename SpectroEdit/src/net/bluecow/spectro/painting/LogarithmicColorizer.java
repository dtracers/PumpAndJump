/*     */ package net.bluecow.spectro.painting;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.PrintStream;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import net.bluecow.spectro.tool.CurvedSlider;
/*     */ 
/*     */ public class LogarithmicColorizer
/*     */   implements ValueColorizer
/*     */ {
/*  34 */   private double preMult = 0.0D;
/*  35 */   private double brightness = 0.0D;
/*  36 */   private double contrast = 0.0D;
/*  37 */   private boolean useRed = false;
/*     */   private final ClipPanel clipPanel;
/*     */   private final JComponent settingsPanel;
/*     */ 
/*     */   LogarithmicColorizer(ClipPanel clipPanel)
/*     */   {
/*  44 */     this.clipPanel = clipPanel;
/*     */ 
/*  46 */     final CurvedSlider preMultSlider = new CurvedSlider(0.0D, 7000.0D, 4.0D);
/*  47 */     preMultSlider.addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/*  49 */         LogarithmicColorizer.this.setPreMult(preMultSlider.getCurvedValue());
/*     */       }
/*     */     });
/*  53 */     final JSlider brightnessSlider = new JSlider(-300, 300, 0);
/*  54 */     brightnessSlider.addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/*  56 */         LogarithmicColorizer.this.setBrightness(brightnessSlider.getValue());
/*     */       }
/*     */     });
/*  60 */     final CurvedSlider contrastSlider = new CurvedSlider(0.0D, 10000.0D, 4.0D);
/*  61 */     contrastSlider.addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/*  63 */         LogarithmicColorizer.this.setContrast(contrastSlider.getCurvedValue());
/*     */       }
/*     */     });
/*  67 */     final JCheckBox useRedCheckbox = new JCheckBox("Use red", this.useRed);
/*  68 */     useRedCheckbox.setOpaque(false);
/*  69 */     useRedCheckbox.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  71 */         LogarithmicColorizer.this.setUseRed(useRedCheckbox.isSelected());
/*     */       }
/*     */     });
/*  75 */     this.settingsPanel = Box.createVerticalBox();
/*  76 */     this.settingsPanel.add(new JLabel("Pre Multiplier"));
/*  77 */     this.settingsPanel.add(preMultSlider);
/*  78 */     this.settingsPanel.add(new JLabel("Brightness"));
/*  79 */     this.settingsPanel.add(brightnessSlider);
/*  80 */     this.settingsPanel.add(new JLabel("Contrast"));
/*  81 */     this.settingsPanel.add(contrastSlider);
/*  82 */     this.settingsPanel.add(useRedCheckbox);
/*     */ 
/*  84 */     preMultSlider.setValue(20);
/*  85 */     brightnessSlider.setValue(0);
/*  86 */     contrastSlider.setValue(50);
				preMult = 0.1;
/*     */   }
/*     */ 
/*     */   public int colorFor(double val) {
/*  90 */     int greyVal = (int)(this.brightness + this.contrast * Math.log1p(Math.abs(this.preMult * val)));
/*     */ 
/*  92 */     if (this.useRed) {
/*  93 */       if (greyVal < 0)
/*  94 */         return 0;
/*  95 */       if (greyVal <= 255)
/*  96 */         return greyVal << 16 | greyVal << 8 | greyVal;
/*  97 */       if (greyVal <= 512) {
/*  98 */         greyVal -= 256;
/*  99 */         greyVal = 256 - greyVal;
/* 100 */         return 0xFF0000 | greyVal << 8 | greyVal;
/*     */       }
/* 102 */       return 16711680;
/*     */     }
/*     */ 
/* 105 */     greyVal = Math.min(255, Math.max(0, greyVal));
/* 106 */     return greyVal << 16 | greyVal << 8 | greyVal;
/*     */   }
/*     */ 
/*     */   public void setPreMult(double multiplier)
/*     */   {
/* 112 */     System.out.println("multiplier: " + multiplier);
/* 113 */     this.preMult = multiplier;
/* 114 */     this.clipPanel.updateImage(null);
/* 115 */     this.clipPanel.repaint();
/*     */   }
/*     */ 
/*     */   public void setBrightness(double brightness) {
/* 119 */     System.out.println("brightness: " + brightness);
/* 120 */     this.brightness = brightness;
/* 121 */     this.clipPanel.updateImage(null);
/* 122 */     this.clipPanel.repaint();
/*     */   }
/*     */ 
/*     */   public void setContrast(double contrast) {
/* 126 */     System.out.println("contrast: " + contrast);
/* 127 */     this.contrast = contrast;
/* 128 */     this.clipPanel.updateImage(null);
/* 129 */     this.clipPanel.repaint();
/*     */   }
/*     */ 
/*     */   public void setUseRed(boolean useRed) {
/* 133 */     this.useRed = useRed;
/* 134 */     this.clipPanel.updateImage(null);
/* 135 */     this.clipPanel.repaint();
/*     */   }
/*     */ 
/*     */   public JComponent getSettingsPanel() {
/* 139 */     return this.settingsPanel;
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.LogarithmicColorizer
 * JD-Core Version:    0.6.1
 */