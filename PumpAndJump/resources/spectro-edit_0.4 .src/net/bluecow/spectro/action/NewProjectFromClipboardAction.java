/*    */ package net.bluecow.spectro.action;
/*    */ 
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.datatransfer.Clipboard;
/*    */ import java.awt.datatransfer.DataFlavor;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.io.InputStream;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JList;
/*    */ import javax.swing.JScrollPane;
/*    */ import javax.swing.SwingUtilities;
/*    */ import sun.misc.HexDumpEncoder;
/*    */ 
/*    */ public class NewProjectFromClipboardAction extends AbstractAction
/*    */ {
/*    */   public NewProjectFromClipboardAction()
/*    */   {
/* 44 */     super("New Project From Clipboard...");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 52 */     Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 53 */     JList flavourList = new JList(cb.getAvailableDataFlavors());
/* 54 */     JDialog d = new JDialog((JFrame)null, "Flavours on the clipboard");
/* 55 */     d.setContentPane(new JScrollPane(flavourList));
/* 56 */     d.setModal(true);
/* 57 */     d.pack();
/* 58 */     d.setVisible(true);
/* 59 */     d.dispose();
/*    */ 
/* 61 */     if (flavourList.getSelectedValue() != null) {
/* 62 */       DataFlavor f = (DataFlavor)flavourList.getSelectedValue();
/* 63 */       if (f.isRepresentationClassInputStream())
/*    */         try {
/* 65 */           InputStream in = (InputStream)cb.getData(f);
/* 66 */           HexDumpEncoder hde = new HexDumpEncoder();
/* 67 */           hde.encode(in, System.out);
/*    */         } catch (Exception ex) {
/* 69 */           throw new RuntimeException(ex);
/*    */         }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 76 */     SwingUtilities.invokeLater(new Runnable() {
/*    */       public void run() {
/* 78 */         NewProjectFromClipboardAction a = new NewProjectFromClipboardAction();
/* 79 */         a.actionPerformed(null);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.action.NewProjectFromClipboardAction
 * JD-Core Version:    0.6.1
 */