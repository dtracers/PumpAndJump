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
import net.bluecow.spectro.painting.ClipPanel;
import net.bluecow.spectro.painting.ToolboxPanel;
/*    */
/*    */ public class TestingMain
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
				SpectroEditSession.main(args);
				/*
				if(false)
				{
     final JFrame f = new JFrame("Spectro-Edit ME" );
     SwingUtilities.invokeLater(new Runnable() {
       public void run() {
         try {
           FileDialog fd = new FileDialog(f, "Choose a 16-bit mono WAV file");
           fd.setVisible(true);
           String dir = fd.getDirectory();
           String file = fd.getFile();
           if ((dir == null) || (file == null)) {
            JOptionPane.showMessageDialog(f, "Ok, maybe next time");
            System.exit(0);
           }
           File wavFile = new File(dir, file);
          Clip c = new Clip(wavFile);
           ClipPanel cp = new ClipPanel(c);
           f.setDefaultCloseOperation(3);
           f.setLayout(new BorderLayout());
           f.add(new JScrollPane(cp), "Center");
           f.add(new ToolboxPanel(cp).getPanel(), "South");
           f.pack();
         f.setLocationRelativeTo(null);
          f.setVisible(true);
         }
         catch (Exception e) {
           e.printStackTrace();
          JOptionPane.showMessageDialog(f, "Sorry, couldn't read your sample:\n" + e.getMessage() + "\nBe sure your file is 16-bit mono!");

           System.exit(0);
        }
       }
     });
				}
				*/
/*    */   }

/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.TestingMain
 * JD-Core Version:    0.6.1
 */