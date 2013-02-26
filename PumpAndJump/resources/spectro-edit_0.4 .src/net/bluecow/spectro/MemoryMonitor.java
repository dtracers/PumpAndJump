/*    */ package net.bluecow.spectro;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.Timer;
/*    */ 
/*    */ public class MemoryMonitor
/*    */ {
/*    */   private Timer timer;
/* 28 */   private JLabel label = new JLabel();
/*    */ 
/* 30 */   private ActionListener timerAction = new ActionListener() {
/*    */     public void actionPerformed(ActionEvent e) {
/* 32 */       long megabyte = 1048576L;
/* 33 */       long totalMemory = Runtime.getRuntime().totalMemory() / megabyte;
/* 34 */       long freeMemory = Runtime.getRuntime().freeMemory() / megabyte;
/* 35 */       long usedMemory = totalMemory - freeMemory;
/* 36 */       MemoryMonitor.this.label.setText(usedMemory + "M/" + totalMemory + "M");
/*    */     }
/* 30 */   };
/*    */ 
/*    */   public MemoryMonitor()
/*    */   {
/* 41 */     this.timer = new Timer(1000, this.timerAction);
/*    */   }
/*    */ 
/*    */   public void start() {
/* 45 */     this.timer.start();
/*    */   }
/*    */ 
/*    */   public void stop() {
/* 49 */     this.timer.stop();
/*    */   }
/*    */ 
/*    */   public JLabel getLabel() {
/* 53 */     return this.label;
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.MemoryMonitor
 * JD-Core Version:    0.6.1
 */