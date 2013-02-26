/*    */ package net.bluecow.spectro.windowFunctions;
/*    */
/*    */ import java.util.Arrays;
/*    */ import java.util.logging.Logger;

/*    */
/*    */ public class VorbisWindowFunction
/*    */   implements WindowFunction
/*    */ {
/* 24 */   private static final Logger logger = Logger.getLogger(VorbisWindowFunction.class.getName());
/*    */   private final double[] scalars;
/*    */   private static final double PI = 3.141592653589793D;
/*    */
/*    */   public VorbisWindowFunction(int size)
/*    */   {
/* 31 */     this.scalars = new double[size];
/* 32 */     for (int i = 0; i < size; i++)
/*    */     {
/* 38 */       double xx = Math.sin(3.141592653589793D / (2.0D * size) * (2.0D * i));
/* 39 */       this.scalars[i] = Math.sin(1.570796326794897D * (xx * xx));
/*    */     }
/* 41 */     logger.finest(String.format("VorbisWindowFunction scalars (size=%d): %s\n", new Object[] { Integer.valueOf(this.scalars.length), Arrays.toString(this.scalars) }));
/*    */   }
/*    */
/*    */   public void applyWindow(double[] data) {
/* 45 */     if (data.length != this.scalars.length) {
/* 46 */       throw new IllegalArgumentException("Invalid array size (required: " + this.scalars.length + "; given: " + data.length + ")");
/*    */     }
/*    */
/* 50 */     for (int i = 0; i < data.length; i++)
/* 51 */       data[i] *= this.scalars[i];
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.VorbisWindowFunction
 * JD-Core Version:    0.6.1
 */