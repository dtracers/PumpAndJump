/*     */ package net.bluecow.spectro;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Arrays;
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.undo.AbstractUndoableEdit;
/*     */ import javax.swing.undo.CannotRedoException;
/*     */ import javax.swing.undo.CannotUndoException;
/*     */ import javax.swing.undo.UndoableEdit;
/*     */ 
/*     */ public class ClipDataEdit extends AbstractUndoableEdit
/*     */ {
/*  34 */   private static final Logger logger = Logger.getLogger(ClipDataEdit.class.getName());
/*     */   private final Clip clip;
/*     */   private final int firstFrame;
/*     */   private final int firstFreqIndex;
/*     */   private double[][] oldData;
/*     */   private double[][] newData;
/*     */ 
/*     */   public ClipDataEdit(Clip clip, int firstFrame, int firstFreqIndex, int nFrames, int nFreqs)
/*     */   {
/*  55 */     if (nFrames == 0) {
/*  56 */       throw new IllegalArgumentException("Data area to capture is empty (nFrames == 0)");
/*     */     }
/*  58 */     if (nFreqs == 0) {
/*  59 */       throw new IllegalArgumentException("Data area to capture is empty (nFreqs == 0)");
/*     */     }
/*  61 */     this.clip = clip;
/*  62 */     this.firstFrame = firstFrame;
/*  63 */     this.firstFreqIndex = firstFreqIndex;
/*  64 */     this.oldData = new double[nFrames][nFreqs];
/*  65 */     capture(this.oldData);
/*     */   }
/*     */ 
/*     */   public ClipDataEdit(Clip clip, Rectangle r)
/*     */   {
/*  76 */     this(clip, r.x, r.y, r.width, r.height);
/*     */   }
/*     */ 
/*     */   public boolean replaceEdit(UndoableEdit anEdit)
/*     */   {
/*  81 */     boolean replace = false;
/*  82 */     if ((anEdit instanceof ClipDataEdit)) {
/*  83 */       ClipDataEdit other = (ClipDataEdit)anEdit;
/*  84 */       if ((other.firstFrame == this.firstFrame) && (other.firstFreqIndex == this.firstFreqIndex) && (other.oldData.length == this.oldData.length) && (other.oldData[0].length == this.oldData[0].length) && (other.clip == this.clip))
/*     */       {
/*  90 */         replace = true;
/*  91 */         this.oldData = other.oldData;
/*  92 */         other.die();
/*     */       }
/*     */     }
/*  95 */     logger.fine("Replace edit? " + replace);
/*  96 */     return replace;
/*     */   }
/*     */ 
/*     */   public void captureNewData()
/*     */   {
/* 104 */     if (this.newData != null) {
/* 105 */       throw new IllegalStateException("Already captured new data");
/*     */     }
/* 107 */     this.newData = new double[this.oldData.length][this.oldData[0].length];
/* 108 */     capture(this.newData);
/* 109 */     if (Arrays.deepEquals(this.oldData, this.newData))
/* 110 */       logger.fine("Captured new data == old data!");
/*     */   }
/*     */ 
/*     */   public void undo()
/*     */     throws CannotUndoException
/*     */   {
/* 116 */     super.undo();
/* 117 */     logger.fine("Undoing edit at " + getRegion());
/* 118 */     apply(this.oldData);
/* 119 */     this.clip.regionChanged(getRegion());
/*     */   }
/*     */ 
/*     */   public void redo() throws CannotRedoException
/*     */   {
/* 124 */     super.redo();
/* 125 */     logger.fine("Redoing edit at " + getRegion());
/* 126 */     apply(this.newData);
/* 127 */     this.clip.regionChanged(getRegion());
/*     */   }
/*     */ 
/*     */   private void apply(double[][] data)
/*     */   {
/* 136 */     for (int i = 0; i < data.length; i++) {
/* 137 */       Frame f = this.clip.getFrame(i + this.firstFrame);
/* 138 */       for (int j = 0; j < data[0].length; j++)
/* 139 */         f.setReal(j + this.firstFreqIndex, data[i][j]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void capture(double[][] data)
/*     */   {
/* 150 */     for (int i = 0; i < data.length; i++) {
/* 151 */       Frame f = this.clip.getFrame(i + this.firstFrame);
/* 152 */       for (int j = 0; j < data[0].length; j++)
/* 153 */         data[i][j] = f.getReal(j + this.firstFreqIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Rectangle getRegion()
/*     */   {
/* 164 */     return new Rectangle(this.firstFrame, this.firstFreqIndex, this.oldData.length, this.oldData[0].length);
/*     */   }
/*     */ 
/*     */   public boolean isSameRegion(Rectangle r)
/*     */   {
/* 174 */     if (r == null) {
/* 175 */       return false;
/*     */     }
/* 177 */     return (r.x == this.firstFrame) && (r.y == this.firstFreqIndex) && (r.width == this.oldData.length) && (r.height == this.oldData[0].length);
/*     */   }
/*     */ 
/*     */   public double[][] getOldData()
/*     */   {
/* 190 */     return this.oldData;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 195 */     return String.format("Clip Data Edit @ [%d, %d %d x %d]", new Object[] { Integer.valueOf(this.firstFrame), Integer.valueOf(this.firstFreqIndex), Integer.valueOf(this.oldData.length), Integer.valueOf(this.oldData[0].length) });
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.ClipDataEdit
 * JD-Core Version:    0.6.1
 */