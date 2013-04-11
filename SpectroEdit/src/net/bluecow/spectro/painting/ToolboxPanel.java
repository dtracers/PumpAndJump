/*     */ package net.bluecow.spectro.painting;
/*     */
/*     */ import java.awt.Component;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;

import javax.sound.sampled.AudioFileFormat;
/*     */ import javax.sound.sampled.AudioFileFormat.Type;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;

import net.bluecow.spectro.PlayerThread;
/*     */
/*     */ public class ToolboxPanel
/*     */ {
/*     */   private final ClipPanel clipPanel;
/*     */   private final JPanel panel;
/*     */
/*     */   public ToolboxPanel(ClipPanel clipPanel)
/*     */   {
/*  43 */     this.clipPanel = clipPanel;
/*  44 */     this.panel = new JPanel();
/*  45 */     this.panel.add(makeBrightnessSlider());
/*  46 */     this.panel.add(makeShuttleControls());
/*  47 */     this.panel.add(makeSaveButton());
/*  48 */     this.panel.add(makePaintControls());
/*     */   }
/*     */
/*     */   private JComponent makePaintControls() {
/*  52 */     PaintbrushTool paintbrush = new PaintbrushTool(this.clipPanel);
/*  53 */     JLabel paintLabel = new JLabel("Paintbrush size: " + paintbrush.getRadius());
/*  54 */     return paintLabel;
/*     */   }
/*     */
/*     */   private Component makeShuttleControls() {
/*     */     try {
/*  59 */       final PlayerThread playerThread = new PlayerThread(this.clipPanel.getClip());
/*  60 */       playerThread.start();
/*  61 */       final JButton playPause = new JButton("Play");
/*  62 */       JButton rewind = new JButton("Rewind");
/*  63 */       playPause.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/*  65 */           if ("Play".equals(playPause.getText())) {
/*  66 */             playerThread.startPlaying();
/*  67 */             playPause.setText("Pause");
/*  68 */           } else if ("Pause".equals(playPause.getText())) {
/*  69 */             playerThread.stopPlaying();
/*  70 */             playPause.setText("Play");
/*     */           }
/*     */         }
/*     */       });
/*  75 */       rewind.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/*  77 */           playerThread.setPlaybackPosition(0);
/*     */         }
/*     */       });
/*  82 */       JPanel p = new JPanel(new FlowLayout());
/*  83 */       p.add(playPause);
/*  84 */       p.add(rewind);
/*  85 */       return p;
/*     */     } catch (LineUnavailableException ex) {
/*  87 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */
/*     */   public JComponent makeBrightnessSlider() {
/*  92 */     final JSlider brightness = new JSlider(0, 5000000, (int)(10 * 100.0D));
/*  93 */     brightness.addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/*  95 */         ToolboxPanel.this.clipPanel.setSpectralToScreenMultiplier(brightness.getValue() / 100.0D);
/*     */       }
/*     */     });
/*  98 */     Box box = Box.createVerticalBox();
/*  99 */     box.add(new JLabel("Brightness"));
/* 100 */     box.add(brightness);
/* 101 */     return box;
/*     */   }
/*     */
/*     */   private JButton makeSaveButton() {
/* 105 */     JButton saveButton = new JButton("Save...");
/* 106 */     saveButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*     */         try {
/* 110 */           Window owner = SwingUtilities.getWindowAncestor(ToolboxPanel.this.panel);
/*     */           FileDialog fd;
/* 111 */           if ((owner instanceof Frame))
/* 112 */             fd = new FileDialog((Frame)owner, "Save sample as", 1);
/*     */           else {
/* 114 */             fd = new FileDialog((Dialog)owner, "Save sample as", 1);
/*     */           }
/* 116 */           fd.setVisible(true);
/* 117 */           String dir = fd.getDirectory();
/* 118 */           String file = fd.getFile();
/* 119 */           if (file == null) return;
/* 120 */           if (!file.toLowerCase().endsWith(".wav")) {
/* 121 */             file = file + ".wav";
/*     */           }
/* 123 */           AudioSystem.write(ToolboxPanel.this.clipPanel.getClip().getAudio(), AudioFileFormat.Type.WAVE, new File(dir, file));
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/* 128 */           throw new RuntimeException(ex);
/*     */         }
/*     */       }
/*     */     });
/* 132 */     return saveButton;
/*     */   }
/*     */   public JPanel getPanel() {
/* 135 */     return this.panel;
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.ToolboxPanel
 * JD-Core Version:    0.6.1
 */