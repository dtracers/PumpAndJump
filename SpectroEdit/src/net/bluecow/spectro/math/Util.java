/*    */ package net.bluecow.spectro.math;
/*    */ 
/*    */ public class Util
/*    */ {
/*    */   public static double[][] realToComplex(double[] real)
/*    */   {
/* 28 */     double[][] complex = new double[real.length][2];
/* 29 */     for (int i = 0; i < real.length; i++) {
/* 30 */       complex[i][0] = real[i];
/*    */     }
/* 32 */     return complex;
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.Util
 * JD-Core Version:    0.6.1
 */