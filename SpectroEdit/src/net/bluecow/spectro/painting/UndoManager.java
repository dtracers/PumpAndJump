/*    */ package net.bluecow.spectro.painting;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Vector;
/*    */ import java.util.logging.Logger;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ import javax.swing.event.UndoableEditEvent;
/*    */ import javax.swing.undo.CannotRedoException;
/*    */ import javax.swing.undo.CannotUndoException;
/*    */ import javax.swing.undo.UndoableEdit;
/*    */ 
/*    */ public class UndoManager extends javax.swing.undo.UndoManager
/*    */ {
/* 32 */   private static final Logger logger = Logger.getLogger(UndoManager.class.getName());
/*    */ 
/* 65 */   private final List<ChangeListener> changeListeners = new ArrayList();
/*    */ 
/*    */   public UndoManager()
/*    */   {
/* 36 */     setLimit(1000);
/*    */   }
/*    */ 
/*    */   public void undoableEditHappened(UndoableEditEvent e)
/*    */   {
/* 41 */     logger.finest("Got undoable edit: " + e.getEdit());
/* 42 */     super.undoableEditHappened(e);
/* 43 */     logger.fine("Added edit " + this.edits.size() + "/" + getLimit());
/*    */   }
/*    */ 
/*    */   protected void undoTo(UndoableEdit edit) throws CannotUndoException
/*    */   {
/* 48 */     super.undoTo(edit);
/* 49 */     fireStateChanged();
/*    */   }
/*    */ 
/*    */   protected void redoTo(UndoableEdit edit) throws CannotRedoException
/*    */   {
/* 54 */     super.redoTo(edit);
/* 55 */     fireStateChanged();
/*    */   }
/*    */ 
/*    */   public synchronized boolean addEdit(UndoableEdit anEdit)
/*    */   {
/* 60 */     boolean added = super.addEdit(anEdit);
/* 61 */     fireStateChanged();
/* 62 */     return added;
/*    */   }
/*    */ 
/*    */   public void addChangeListener(ChangeListener l)
/*    */   {
/* 68 */     this.changeListeners.add(l);
/*    */   }
/*    */ 
/*    */   public void removeChangeListener(ChangeListener l) {
/* 72 */     this.changeListeners.remove(l);
/*    */   }
/*    */ 
/*    */   public void fireStateChanged() {
/* 76 */     ChangeEvent e = new ChangeEvent(this);
/* 77 */     for (int i = this.changeListeners.size() - 1; i >= 0; i--)
/* 78 */       ((ChangeListener)this.changeListeners.get(i)).stateChanged(e);
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.UndoManager
 * JD-Core Version:    0.6.1
 */