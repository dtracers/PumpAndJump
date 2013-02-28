/*     */ package net.bluecow.spectro.tool;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import net.bluecow.spectro.SpectroEditSession;
import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.clipAndFrame.Frame;
import net.bluecow.spectro.painting.ClipPanel;
/*     */ 
/*     */ public class RegionFlipTool
/*     */   implements Tool
/*     */ {
/*     */   private ClipPanel clipPanel;
/*     */   private Clip clip;
/*     */   private final Box settingsPanel;
/*     */   private final JButton vflipButton;
/*     */   private final JButton hflipButton;
/*     */ 
/*     */   public RegionFlipTool()
/*     */   {
/*  46 */     this.settingsPanel = Box.createVerticalBox();
/*     */ 
/*  48 */     this.vflipButton = new JButton("Flip vertically");
/*  49 */     this.vflipButton.setOpaque(false);
/*  50 */     this.settingsPanel.add(this.vflipButton);
/*  51 */     this.vflipButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  53 */         RegionFlipTool.this.vflipRegion();
/*     */       }
/*     */     });
/*  57 */     this.hflipButton = new JButton("Flip horizontally");
/*  58 */     this.hflipButton.setOpaque(false);
/*  59 */     this.settingsPanel.add(this.hflipButton);
/*  60 */     this.hflipButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  62 */         RegionFlipTool.this.hflipRegion();
/*     */       }
/*     */     });
/*  66 */     this.settingsPanel.add(Box.createGlue());
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  70 */     return "Flip";
/*     */   }
/*     */ 
/*     */   public void activate(SpectroEditSession session) {
/*  74 */     this.clipPanel = session.getClipPanel();
/*  75 */     this.clip = this.clipPanel.getClip();
/*  76 */     this.clipPanel.setRegionMode(true);
/*     */   }
/*     */ 
/*     */   public void deactivate() {
/*  80 */     this.clip = null;
/*  81 */     this.clipPanel = null;
/*     */   }
/*     */ 
/*     */   public JComponent getSettingsPanel() {
/*  85 */     return this.settingsPanel;
/*     */   }
/*     */ 
/*     */   private void vflipRegion() {
/*  89 */     Rectangle region = this.clipPanel.getRegion();
/*  90 */     if ((region == null) || (region.width == 0) || (region.height == 0)) {
/*  91 */       return;
/*     */     }
/*  93 */     Rectangle frameRegion = this.clipPanel.toClipCoords(new Rectangle(region));
/*  94 */     this.clip.beginEdit(frameRegion, "Flip Region Vertically");
/*  95 */     for (int i = 0; i < frameRegion.width; i++) {
/*  96 */       Frame frame = this.clip.getFrame(frameRegion.x + i);
/*  97 */       for (int j = 0; j < frameRegion.height / 2; j++) {
/*  98 */         int bottom = frameRegion.y + frameRegion.height - 1 - j;
/*  99 */         int top = frameRegion.y + j;
/* 100 */         double tmp = frame.getReal(bottom);
/* 101 */         frame.setReal(bottom, frame.getReal(top));
/* 102 */         frame.setReal(top, tmp);
/*     */       }
/*     */     }
/* 105 */     this.clip.endEdit();
/*     */   }
/*     */ 
/*     */   private void hflipRegion() {
/* 109 */     Rectangle region = this.clipPanel.getRegion();
/* 110 */     if ((region == null) || (region.width == 0) || (region.height == 0)) {
/* 111 */       return;
/*     */     }
/* 113 */     Rectangle frameRegion = this.clipPanel.toClipCoords(new Rectangle(region));
/* 114 */     this.clip.beginEdit(frameRegion, "Flip Region Horizontally");
/* 115 */     for (int i = 0; i < frameRegion.width / 2; i++) {
/* 116 */       Frame lframe = this.clip.getFrame(frameRegion.x + i);
/* 117 */       Frame rframe = this.clip.getFrame(frameRegion.x + frameRegion.width - 1 - i);
/* 118 */       for (int j = frameRegion.y; j < frameRegion.y + frameRegion.height; j++) {
/* 119 */         double tmp = rframe.getReal(j);
/* 120 */         rframe.setReal(j, lframe.getReal(j));
/* 121 */         lframe.setReal(j, tmp);
/*     */       }
/*     */     }
/* 124 */     this.clip.endEdit();
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.RegionFlipTool
 * JD-Core Version:    0.6.1
 */