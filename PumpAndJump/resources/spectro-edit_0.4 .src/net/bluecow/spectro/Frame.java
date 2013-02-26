/*     */ package net.bluecow.spectro;
/*     */ 
/*     */ import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class Frame
/*     */ {
/*  30 */   private static final Logger logger = Logger.getLogger(Frame.class.getName());
/*     */   private double[] data;
/*     */   private static DoubleDCT_1D dct;
/*     */   private final WindowFunction windowFunc;
/*     */ 
/*     */   public Frame(double[] timeData, WindowFunction windowFunc)
/*     */   {
/*  42 */     this.windowFunc = windowFunc;
/*  43 */     if (dct == null)
/*     */     {
/*  45 */       dct = new DoubleDCT_1D(timeData.length);
/*     */     }
/*     */ 
/*  49 */     windowFunc.applyWindow(timeData);
/*     */ 
/*  52 */     dct.forward(timeData, true);
/*     */ 
/*  54 */     double min = (1.0D / 0.0D);
/*  55 */     double max = (-1.0D / 0.0D);
/*     */ 
/*  57 */     this.data = new double[timeData.length];
/*  58 */     for (int i = 0; i < this.data.length; i++) {
/*  59 */       this.data[i] = timeData[i];
/*  60 */       min = Math.min(this.data[i], min);
/*  61 */       max = Math.max(this.data[i], max);
/*     */     }
/*     */ 
/*  64 */     logger.finer(String.format("Computed frame. min=%4.6f max=%4.6f", new Object[] { Double.valueOf(min), Double.valueOf(max) }));
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  72 */     return this.data.length;
/*     */   }
/*     */ 
/*     */   public double getReal(int idx)
/*     */   {
/*  79 */     return this.data[idx];
/*     */   }
/*     */ 
/*     */   public double getImag(int idx)
/*     */   {
/*  86 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public void setReal(int idx, double d)
/*     */   {
/*  98 */     this.data[idx] = d;
/*     */   }
/*     */ 
/*     */   public double[] asTimeData()
/*     */   {
/* 109 */     double[] timeData = new double[this.data.length];
/* 110 */     System.arraycopy(this.data, 0, timeData, 0, this.data.length);
/* 111 */     dct.inverse(timeData, true);
/* 112 */     this.windowFunc.applyWindow(timeData);
/* 113 */     return timeData;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 120 */     double[] orig = { 1.0D, 2.0D, 3.0D, 4.0D, 5.0D, 0.0D, 9.0D, 8.0D, 7.0D, 6.0D, 5.0D, 4.0D, 3.0D, 2.0D, 1.0D, 7.0D };
/* 121 */     System.out.println(Arrays.toString(orig));
/* 122 */     Frame f = new Frame(orig, new NullWindowFunction());
/* 123 */     System.out.println(Arrays.toString(f.data));
/* 124 */     System.out.println(Arrays.toString(f.asTimeData()));
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.Frame
 * JD-Core Version:    0.6.1
 */