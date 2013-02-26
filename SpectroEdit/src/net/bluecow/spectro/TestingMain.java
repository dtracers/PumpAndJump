/*    */ package net.bluecow.spectro;
/*    */
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.FileDialog;
/*    */ import java.io.File;

import javax.swing.JDialog;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JOptionPane;
/*    */ import javax.swing.JScrollPane;
/*    */ import javax.swing.SwingUtilities;

import net.bluecow.spectro.clipAndFrame.Clip;
/*    */
/*    */ public class TestingMain
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 21 */     final JFrame f = new JFrame("Spectro-Edit ME" );
/* 22 */     SwingUtilities.invokeLater(new Runnable() {
/*    */       public void run() {
/*    */         try {
/* 25 */           FileDialog fd = new FileDialog(f, "Choose a 16-bit mono WAV file");
/* 26 */           fd.setVisible(true);
/* 27 */           String dir = fd.getDirectory();
/* 28 */           String file = fd.getFile();
/* 29 */           if ((dir == null) || (file == null)) {
/* 30 */             JOptionPane.showMessageDialog(f, "Ok, maybe next time");
/* 31 */             System.exit(0);
/*    */           }
/* 33 */           File wavFile = new File(dir, file);
/* 34 */           Clip c = new Clip(wavFile);
/* 35 */           ClipPanel cp = new ClipPanel(c);
/* 36 */           f.setDefaultCloseOperation(3);
/* 37 */           f.setLayout(new BorderLayout());
/* 38 */           f.add(new JScrollPane(cp), "Center");
/* 39 */           f.add(new ToolboxPanel(cp).getPanel(), "South");
/*    */
/* 42 */           f.pack();
/* 43 */           f.setLocationRelativeTo(null);
/* 44 */           f.setVisible(true);
/*    */         }
/*    */         catch (Exception e) {
/* 47 */           e.printStackTrace();
/* 48 */           JOptionPane.showMessageDialog(f, "Sorry, couldn't read your sample:\n" + e.getMessage() + "\nBe sure your file is 16-bit mono!");
/*    */
/* 52 */           System.exit(0);
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.TestingMain
 * JD-Core Version:    0.6.1
 */