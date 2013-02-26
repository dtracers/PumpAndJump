/*    */ package net.bluecow.spectro;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.FileDialog;
/*    */ import java.io.File;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JOptionPane;
/*    */ import javax.swing.JScrollPane;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class TestingMain
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 21 */     JFrame f = new JFrame("Spectro-Edit " + Version.VERSION);
/* 22 */     SwingUtilities.invokeLater(new Runnable() {
/*    */       public void run() {
/*    */         try {
/* 25 */           FileDialog fd = new FileDialog(this.val$f, "Choose a 16-bit mono WAV file");
/* 26 */           fd.setVisible(true);
/* 27 */           String dir = fd.getDirectory();
/* 28 */           String file = fd.getFile();
/* 29 */           if ((dir == null) || (file == null)) {
/* 30 */             JOptionPane.showMessageDialog(this.val$f, "Ok, maybe next time");
/* 31 */             System.exit(0);
/*    */           }
/* 33 */           File wavFile = new File(dir, file);
/* 34 */           Clip c = new Clip(wavFile);
/* 35 */           ClipPanel cp = new ClipPanel(c);
/* 36 */           this.val$f.setDefaultCloseOperation(3);
/* 37 */           this.val$f.setLayout(new BorderLayout());
/* 38 */           this.val$f.add(new JScrollPane(cp), "Center");
/* 39 */           this.val$f.add(new ToolboxPanel(cp).getPanel(), "South");
/*    */ 
/* 42 */           this.val$f.pack();
/* 43 */           this.val$f.setLocationRelativeTo(null);
/* 44 */           this.val$f.setVisible(true);
/*    */         }
/*    */         catch (Exception e) {
/* 47 */           e.printStackTrace();
/* 48 */           JOptionPane.showMessageDialog(this.val$f, "Sorry, couldn't read your sample:\n" + e.getMessage() + "\nBe sure your file is 16-bit mono!");
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