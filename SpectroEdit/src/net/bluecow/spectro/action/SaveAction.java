/*    */ package net.bluecow.spectro.action;
/*    */
/*    */ import java.awt.Component;
/*    */ import java.awt.Dialog;
/*    */ import java.awt.FileDialog;
/*    */ import java.awt.Frame;
/*    */ import java.awt.Window;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.io.File;

import javax.sound.sampled.AudioFileFormat;
/*    */ import javax.sound.sampled.AudioFileFormat.Type;
/*    */ import javax.sound.sampled.AudioSystem;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JOptionPane;
/*    */ import javax.swing.SwingUtilities;

import net.bluecow.spectro.clipAndFrame.Clip;
/*    */
/*    */ public class SaveAction extends AbstractAction
/*    */ {
/* 42 */   private static final boolean PROMPT_ON_OVERWRITE = System.getProperty("mrj.version") != null;
/*    */   private final Clip clip;
/*    */   private final Component dialogOwner;
/*    */
/*    */   public SaveAction(Clip clip, Component dialogOwner)
/*    */   {
/* 48 */     super("Save...");
/* 49 */     this.clip = clip;
/* 50 */     this.dialogOwner = dialogOwner;
/* 51 */     if (dialogOwner == null)
/* 52 */       throw new NullPointerException("You have to specify an owning component for the save dialog");
/*    */   }
/*    */
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/*    */     try
/*    */     {
/*    */       Window owner;
/* 61 */       if ((this.dialogOwner instanceof Window))
/* 62 */         owner = (Window)this.dialogOwner;
/*    */       else
/* 64 */         owner = SwingUtilities.getWindowAncestor(this.dialogOwner);
/*    */       FileDialog fd;
/* 66 */       if ((owner instanceof Frame))
/* 67 */         fd = new FileDialog((Frame)owner, "Save sample as", 1);
/*    */       else {
/* 69 */         fd = new FileDialog((Dialog)owner, "Save sample as", 1);
/* 71 */       }File targetFile = null;
/*    */       boolean promptAgain;
/*    */       do {
/* 74 */         promptAgain = false;
/* 75 */         fd.setVisible(true);
/* 76 */         String dir = fd.getDirectory();
/* 77 */         String fileName = fd.getFile();
/* 78 */         if (fileName == null) return;
/* 79 */         if (!fileName.toLowerCase().endsWith(".wav")) {
/* 80 */           fileName = fileName + ".wav";
/*    */         }
/* 82 */         targetFile = new File(dir, fileName);
/* 83 */         if ((PROMPT_ON_OVERWRITE) && (targetFile.exists())) {
/* 84 */           int choice = JOptionPane.showOptionDialog(owner, "The file " + targetFile + " exists.\nDo you want to replace it?", "File exists", -1, 2, null, new String[] { "Replace", "Cancel" }, "Replace");
/*    */
/* 88 */           if (choice == 0) {
/* 89 */             promptAgain = false;
/* 90 */           } else if (choice == 1) {
/* 91 */             promptAgain = true; } else {
/* 92 */             if (choice == -1) {
/* 93 */               return;
/*    */             }
/* 95 */             throw new RuntimeException("Unrecognized choice: " + choice);
/*    */           }
/*    */         }
/*    */       }
/* 98 */       while (promptAgain);
/* 99 */       AudioSystem.write(this.clip.getAudio(), AudioFileFormat.Type.WAVE, targetFile);
/*    */     }
/*    */     catch (Exception ex)
/*    */     {
/* 104 */       throw new RuntimeException(ex);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.action.SaveAction
 * JD-Core Version:    0.6.1
 */