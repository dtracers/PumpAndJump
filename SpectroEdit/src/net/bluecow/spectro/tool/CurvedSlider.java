/*    */ package net.bluecow.spectro.tool;
/*    */ 
/*    */ import javax.swing.JSlider;
/*    */ 
/*    */ public class CurvedSlider extends JSlider
/*    */ {
/*    */   private static final int RESOLUTION = 100;
/*    */   private final double exponent;
/*    */   private final double scalar;
/*    */ 
/*    */   public CurvedSlider(double min, double max, double curviness)
/*    */   {
/* 53 */     super(0, 100, 0);
/* 54 */     this.exponent = curviness;
/*    */ 
/* 62 */     this.scalar = (Math.pow(max, 1.0D / this.exponent) / 100.0D);
/*    */   }
/*    */ 
/*    */   public double getCurvedValue()
/*    */   {
/* 70 */     return Math.pow(this.scalar * getValue(), this.exponent);
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.CurvedSlider
 * JD-Core Version:    0.6.1
 */