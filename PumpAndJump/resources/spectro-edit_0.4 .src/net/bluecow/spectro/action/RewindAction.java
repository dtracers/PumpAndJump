/*    */ package net.bluecow.spectro.action;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import net.bluecow.spectro.PlayerThread;
/*    */ 
/*    */ public class RewindAction extends AbstractAction
/*    */ {
/*    */   private final PlayerThread playerThread;
/*    */ 
/*    */   public RewindAction(PlayerThread playerThread)
/*    */   {
/* 30 */     super("Rewind");
/* 31 */     this.playerThread = playerThread;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e) {
/* 35 */     this.playerThread.setPlaybackPosition(0);
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.action.RewindAction
 * JD-Core Version:    0.6.1
 */