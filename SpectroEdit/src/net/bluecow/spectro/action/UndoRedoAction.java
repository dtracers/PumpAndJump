/*    */ package net.bluecow.spectro.action;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;

import net.bluecow.spectro.painting.UndoManager;
/*    */ 
/*    */ public class UndoRedoAction extends AbstractAction
/*    */ {
/*    */   private final boolean undo;
/*    */   private final UndoManager undoManager;
/* 40 */   private ChangeListener undoManagerChangeHandler = new ChangeListener() {
/*    */     public void stateChanged(ChangeEvent e) {
/* 42 */       UndoRedoAction.this.updateEnabledness();
/*    */     }
/* 40 */   };
/*    */ 
/*    */   public static UndoRedoAction createUndoInstance(UndoManager undoManager)
/*    */   {
/* 30 */     return new UndoRedoAction(undoManager, true);
/*    */   }
/*    */ 
/*    */   public static UndoRedoAction createRedoInstance(UndoManager undoManager) {
/* 34 */     return new UndoRedoAction(undoManager, false);
/*    */   }
/*    */ 
/*    */   public UndoRedoAction(UndoManager undoManager, boolean undo)
/*    */   {
/* 47 */     super(undo ? "Undo" : "Redo");
/* 48 */     this.undoManager = undoManager;
/* 49 */     this.undo = undo;
/* 50 */     undoManager.addChangeListener(this.undoManagerChangeHandler);
/* 51 */     updateEnabledness();
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e) {
/* 55 */     if (this.undo) {
/* 56 */       if (this.undoManager.canUndo()) {
/* 57 */         this.undoManager.undo();
/*    */       }
/*    */     }
/* 60 */     else if (this.undoManager.canRedo())
/* 61 */       this.undoManager.redo();
/*    */   }
/*    */ 
/*    */   private void updateEnabledness()
/*    */   {
/* 71 */     if (this.undo)
/* 72 */       setEnabled(this.undoManager.canUndo());
/*    */     else
/* 74 */       setEnabled(this.undoManager.canRedo());
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.action.UndoRedoAction
 * JD-Core Version:    0.6.1
 */