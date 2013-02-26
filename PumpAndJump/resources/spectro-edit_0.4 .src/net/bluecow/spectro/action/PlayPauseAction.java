/*    */ package net.bluecow.spectro.action;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.SwingUtilities;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ import net.bluecow.spectro.PlayerThread;
/*    */ 
/*    */ public class PlayPauseAction extends AbstractAction
/*    */ {
/*    */   private final PlayerThread playerThread;
/* 32 */   private final ChangeListener playerStateHandler = new ChangeListener() {
/*    */     public void stateChanged(ChangeEvent e) {
/* 34 */       SwingUtilities.invokeLater(new Runnable() {
/*    */         public void run() {
/* 36 */           if (PlayPauseAction.this.playerThread.isPlaying())
/* 37 */             PlayPauseAction.this.putValue("Name", "Pause");
/*    */           else
/* 39 */             PlayPauseAction.this.putValue("Name", "Play");
/*    */         }
/*    */       });
/*    */     }
/* 32 */   };
/*    */ 
/*    */   public PlayPauseAction(PlayerThread playerThread)
/*    */   {
/* 47 */     super("Play");
/* 48 */     this.playerThread = playerThread;
/* 49 */     playerThread.addChangeListener(this.playerStateHandler);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e) {
/* 53 */     if (this.playerThread.isPlaying())
/* 54 */       this.playerThread.stopPlaying();
/*    */     else
/* 56 */       this.playerThread.startPlaying();
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.action.PlayPauseAction
 * JD-Core Version:    0.6.1
 */