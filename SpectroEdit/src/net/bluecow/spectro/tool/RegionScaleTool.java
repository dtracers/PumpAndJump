/*     */ package net.bluecow.spectro.tool;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import net.bluecow.spectro.SpectroEditSession;
import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.clipAndFrame.Frame;
import net.bluecow.spectro.painting.ClipDataEdit;
import net.bluecow.spectro.painting.ClipPanel;
/*     */ 
/*     */ public class RegionScaleTool
/*     */   implements Tool
/*     */ {
/*     */   private ClipPanel clipPanel;
/*     */   private Clip clip;
/*  41 */   private final PropertyChangeListener clipEventHandler = new PropertyChangeListener()
/*     */   {
/*     */     public void propertyChange(PropertyChangeEvent evt) {
/*  44 */       if ("region".equals(evt.getPropertyName()))
/*     */       {
/*  49 */         RegionScaleTool.this.scaleSlider.setValue(100);
/*     */       }
/*     */     }
/*  41 */   };
/*     */   private ClipDataEdit origData;
/*     */   private final Box settingsPanel;
/*     */   private final JSlider scaleSlider;
/*  66 */   private final int initialScale = 100;
/*     */ 
/*     */   public RegionScaleTool() {
/*  69 */     this.settingsPanel = Box.createVerticalBox();
/*  70 */     this.settingsPanel.add(new JLabel("Scale amount"));
/*     */ 
/*  72 */     this.scaleSlider = new JSlider(0, 500, 100);
/*  73 */     this.scaleSlider.setOpaque(false);
/*  74 */     this.scaleSlider.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/*  77 */         if (RegionScaleTool.this.scaleSlider.getValueIsAdjusting())
/*  78 */           RegionScaleTool.this.scaleRegion(RegionScaleTool.this.scaleSlider.getValue() / 100.0D);
/*     */       }
/*     */     });
/*  84 */     this.settingsPanel.add(this.scaleSlider);
/*     */ 
/*  86 */     this.settingsPanel.add(Box.createGlue());
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  90 */     return "Scale Region";
/*     */   }
/*     */ 
/*     */   public void activate(SpectroEditSession session) {
/*  94 */     this.clipPanel = session.getClipPanel();
/*  95 */     this.clip = this.clipPanel.getClip();
/*  96 */     this.clipPanel.setRegionMode(true);
/*  97 */     this.clipPanel.addPropertyChangeListener("region", this.clipEventHandler);
/*     */   }
/*     */ 
/*     */   public void deactivate() {
/* 101 */     if (this.origData != null)
/*     */     {
/* 103 */       this.origData = null;
/*     */     }
/* 105 */     this.clipPanel.removePropertyChangeListener("region", this.clipEventHandler);
/* 106 */     this.clip = null;
/* 107 */     this.clipPanel = null;
/*     */   }
/*     */ 
/*     */   public JComponent getSettingsPanel() {
/* 111 */     return this.settingsPanel;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 116 */     return "Region";
/*     */   }
/*     */ 
/*     */   public void scaleRegion(double amount)
/*     */   {
/* 125 */     Rectangle region = this.clipPanel.getRegion();
/* 126 */     if ((region == null) || (region.width == 0) || (region.height == 0)) {
/* 127 */       this.origData = null;
/* 128 */       return;
/*     */     }
/* 130 */     Rectangle frameRegion = this.clipPanel.toClipCoords(new Rectangle(region));
/* 131 */     if ((this.origData == null) || (!this.origData.isSameRegion(frameRegion))) {
/* 132 */       this.origData = new ClipDataEdit(this.clip, frameRegion);
/*     */     }
/*     */ 
/* 135 */     this.clip.beginEdit(frameRegion, "Scale Region");
/* 136 */     double[][] orig = this.origData.getOldData();
/* 137 */     for (int i = frameRegion.x; i < frameRegion.x + frameRegion.width; i++) {
/* 138 */      // Frame frame = this.clip.getFrame(i);
/* 139 */       for (int j = frameRegion.y; j < frameRegion.y + frameRegion.height; j++) {
/* 140 */        // frame.setReal(j, orig[(i - frameRegion.x)][(j - frameRegion.y)] * amount);
/*     */       }
/*     */     }
/*     */ 
/* 144 */     this.clip.endEdit();
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.RegionScaleTool
 * JD-Core Version:    0.6.1
 */