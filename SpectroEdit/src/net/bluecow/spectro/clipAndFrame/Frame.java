/*     */ package net.bluecow.spectro.clipAndFrame;
/*     */
/*     */ import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.logging.Logger;

import net.bluecow.spectro.windowFunctions.NullWindowFunction;
import net.bluecow.spectro.windowFunctions.WindowFunction;
/*     */
/*     */ public class Frame
/*     */ {
/*  30 */   private static final Logger logger = Logger.getLogger(Frame.class.getName());
/*     */   private double[] data;
/*     */   private static DoubleDCT_1D dct;
/*     */   private final WindowFunction preWindowFunc;
			private final WindowFunction postWindowFunc;
/*     */
/*     */   public Frame(double[] timeData, WindowFunction windowFunc)
/*     */   {
				this(timeData,windowFunc,new NullWindowFunction());
			}

			/**
			 * Creates the frame after applying the preWindowFunction and then does the transform and then the postWindowFunction
			 * timeData is altered during the method
			 * @param timeData
			 * @param windowBefore
			 * @param windowAfter
			 */
			public Frame(double[] timeData, WindowFunction windowBefore,WindowFunction windowAfter)
			{
				this.preWindowFunc = windowBefore;
				this.postWindowFunc = windowAfter;
				if(dct == null)
				{
					dct = new DoubleDCT_1D(timeData.length);
				}

				//applies the function before the transform
				preWindowFunc.applyWindow(timeData);

				//transform is scaled
				dct.forward(timeData, true);

				//post transform window
				postWindowFunc.applyWindow(timeData);

				this.data = new double[timeData.length];
				double min = (1.0D / 0.0D);//infinity, (largest possible value)
				double max = (-1.0D / 0.0D);//-infinity (smallest possible value)
				for (int i = 0; i < this.data.length; i++)
				{
					this.data[i] = timeData[i];
					min = Math.min(this.data[i], min);
					max = Math.max(this.data[i], max);
				}

				logger.finer(String.format("Computed frame. min=%4.6f max=%4.6f",
						new Object[] { Double.valueOf(min), Double.valueOf(max) }));
			}
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
/* 112 */     this.preWindowFunc.applyWindow(timeData);
/* 113 */     return timeData;
/*     */   }
/*     */
/*     */  // public static void main(String[] args)
/*     */  // {
/* 120 */   //  double[] orig = { 1.0D, 2.0D, 3.0D, 4.0D, 5.0D, 0.0D, 9.0D, 8.0D, 7.0D, 6.0D, 5.0D, 4.0D, 3.0D, 2.0D, 1.0D, 7.0D };
/* 121 */   //  System.out.println(Arrays.toString(orig));
/* 122 */   //  Frame f = new Frame(orig, new NullWindowFunction());
/* 123 */  //   System.out.println(Arrays.toString(f.data));
/* 124 */  //   System.out.println(Arrays.toString(f.asTimeData()));
/*     */ //  }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.Frame
 * JD-Core Version:    0.6.1
 */