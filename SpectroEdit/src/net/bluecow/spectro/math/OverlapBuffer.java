/*    */ package net.bluecow.spectro.math;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ public class OverlapBuffer
/*    */ {
/*    */   private final LinkedList<double[]> buffers;
/*    */   private final double[] emptyFrame;
/*    */   private final int offset;
/*    */   private int current;
/*    */ 
/*    */   public OverlapBuffer(int frameSize, int overlap)
/*    */   {
/* 60 */     this.offset = (frameSize / overlap);
/* 61 */     this.emptyFrame = new double[frameSize];
/*    */ 
/* 63 */     this.buffers = new LinkedList();
/* 64 */     for (int i = 0; i < overlap; i++)
/* 65 */       this.buffers.add(this.emptyFrame);
/*    */   }
/*    */ 
/*    */   public double next()
/*    */   {
/* 70 */     int myOffset = this.current;
/* 71 */     double val = 0.0D;
/* 72 */     for (double[] buf : this.buffers)
/*    */     {
/* 75 */       val += buf[myOffset];
/* 76 */       myOffset += this.offset;
/*    */     }
/* 78 */     this.current += 1;
/* 79 */     return val;
/*    */   }
/*    */ 
/*    */   public void addFrame(double[] frame) {
/* 83 */     this.buffers.addFirst(frame);
/* 84 */     this.buffers.removeLast();
/* 85 */     this.current = 0;
/*    */   }
/*    */ 
/*    */   public void addEmptyFrame()
/*    */   {
/* 92 */     addFrame(this.emptyFrame);
/*    */   }
/*    */ 
/*    */   public boolean needsNewFrame() {
/* 96 */     return this.current == this.offset;
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.OverlapBuffer
 * JD-Core Version:    0.6.1
 */