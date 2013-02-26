/*     */ package net.bluecow.spectro.tool;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import net.bluecow.spectro.ClipDataEdit;
/*     */ import net.bluecow.spectro.ClipPanel;
/*     */ import net.bluecow.spectro.SpectroEditSession;
import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.clipAndFrame.Frame;
/*     */ 
/*     */ public class RegionThresholdTool
/*     */   implements Tool
/*     */ {
/*     */   private ClipPanel clipPanel;
/*     */   private Clip clip;
/*  49 */   private final PropertyChangeListener clipEventHandler = new PropertyChangeListener()
/*     */   {
/*     */     public void propertyChange(PropertyChangeEvent evt) {
/*  52 */       if ("region".equals(evt.getPropertyName()))
/*  53 */         RegionThresholdTool.this.thresholdSlider.setValue(100);
/*     */     }
/*  49 */   };
/*     */   private ClipDataEdit origData;
/*     */   private final Box settingsPanel;
/*     */   private final CurvedSlider thresholdSlider;
/*  70 */   private final int initialThreshold = 100;
/*     */   private final JCheckBox upper;
/*     */ 
/*     */   public RegionThresholdTool()
/*     */   {
/*  81 */     this.settingsPanel = Box.createVerticalBox();
/*  82 */     this.settingsPanel.add(new JLabel("Cutoff Threshold"));
/*     */ 
/*  84 */     this.thresholdSlider = new CurvedSlider(0.0D, 10.0D, 3.0D);
/*  85 */     this.thresholdSlider.setOpaque(false);
/*  86 */     this.thresholdSlider.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/*  89 */         if (RegionThresholdTool.this.thresholdSlider.getValueIsAdjusting())
/*  90 */           RegionThresholdTool.this.applyRegionThreshold(RegionThresholdTool.this.thresholdSlider.getCurvedValue());
/*     */       }
/*     */     });
/*  96 */     this.settingsPanel.add(this.thresholdSlider);
/*     */ 
/*  98 */     this.upper = new JCheckBox("Upper Threshold");
/*  99 */     this.upper.setOpaque(false);
/* 100 */     this.upper.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 103 */         RegionThresholdTool.this.applyRegionThreshold(RegionThresholdTool.this.thresholdSlider.getCurvedValue());
/*     */       }
/*     */     });
/* 106 */     this.settingsPanel.add(this.upper);
/*     */ 
/* 108 */     this.settingsPanel.add(Box.createGlue());
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 112 */     return "Region Threshold";
/*     */   }
/*     */ 
/*     */   public void activate(SpectroEditSession session) {
/* 116 */     this.clipPanel = session.getClipPanel();
/* 117 */     this.clip = this.clipPanel.getClip();
/* 118 */     this.clipPanel.setRegionMode(true);
/* 119 */     this.clipPanel.addPropertyChangeListener("region", this.clipEventHandler);
/*     */   }
/*     */ 
/*     */   public void deactivate() {
/* 123 */     this.origData = null;
/* 124 */     this.clipPanel.removePropertyChangeListener("region", this.clipEventHandler);
/* 125 */     this.clip = null;
/* 126 */     this.clipPanel = null;
/*     */   }
/*     */ 
/*     */   public JComponent getSettingsPanel() {
/* 130 */     return this.settingsPanel;
/*     */   }
/*     */ 
/*     */   private void applyRegionThreshold(double threshold)
/*     */   {
/* 135 */     Rectangle region = this.clipPanel.getRegion();
/* 136 */     if ((region == null) || (region.width == 0) || (region.height == 0)) {
/* 137 */       this.origData = null;
/* 138 */       return;
/*     */     }
/* 140 */     Rectangle frameRegion = this.clipPanel.toClipCoords(new Rectangle(region));
/* 141 */     if ((this.origData == null) || (!this.origData.isSameRegion(frameRegion))) {
/* 142 */       this.origData = new ClipDataEdit(this.clip, frameRegion);
/*     */     }
/* 144 */     this.clip.beginEdit(frameRegion, "Region Threshold");
/* 145 */     double[][] orig = this.origData.getOldData();
/* 146 */     for (int i = frameRegion.x; i < frameRegion.x + frameRegion.width; i++) {
/* 147 */       Frame frame = this.clip.getFrame(i);
/* 148 */       for (int j = frameRegion.y; j < frameRegion.y + frameRegion.height; j++) {
/* 149 */         double origVal = orig[(i - frameRegion.x)][(j - frameRegion.y)];
/* 150 */         if ((this.upper.isSelected()) && (Math.abs(origVal) > threshold))
/* 151 */           frame.setReal(j, 0.0D);
/* 152 */         else if ((!this.upper.isSelected()) && (Math.abs(origVal) < threshold))
/* 153 */           frame.setReal(j, 0.0D);
/*     */         else {
/* 155 */           frame.setReal(j, origVal);
/*     */         }
/*     */       }
/*     */     }
/* 159 */     this.clip.endEdit();
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.RegionThresholdTool
 * JD-Core Version:    0.6.1
 */