package edu.emory.mathcs.jtransforms.dct;

import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class AccuracyCheckDoubleDCT
{
  public static void checkAccuracyDCT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D DCT...");
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramInt1 + i;
      int k = (int)Math.pow(2.0D, j);
      DoubleDCT_1D localDoubleDCT_1D = new DoubleDCT_1D(k);
      double d2 = 0.0D;
      double[] arrayOfDouble1 = new double[k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble1);
      double[] arrayOfDouble2 = new double[k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble2);
      localDoubleDCT_1D.forward(arrayOfDouble1, true);
      localDoubleDCT_1D.inverse(arrayOfDouble1, true);
      for (int m = 0; m < k; m++)
      {
        double d1 = Math.abs(arrayOfDouble2[m] - arrayOfDouble1[m]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + d2);
      arrayOfDouble1 = null;
      arrayOfDouble2 = null;
      localDoubleDCT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDCT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D DCT (double[] input)...");
    int j;
    int k;
    DoubleDCT_2D localDoubleDCT_2D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDCT_2D = new DoubleDCT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject1);
      localObject2 = new double[k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject2);
      localDoubleDCT_2D.forward((double[])localObject1, true);
      localDoubleDCT_2D.inverse((double[])localObject1, true);
      for (m = 0; m < k * k; m++)
      {
        d1 = Math.abs(localObject2[m] - localObject1[m]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = null;
      localObject2 = null;
      localDoubleDCT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D DCT (double[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDCT_2D = new DoubleDCT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject1);
      localObject2 = new double[k][k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject2);
      localDoubleDCT_2D.forward((double[][])localObject1, true);
      localDoubleDCT_2D.inverse((double[][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < k; n++)
        {
          d1 = Math.abs(localObject2[m][n] - localObject1[m][n]);
          d2 = Math.max(d2, d1);
        }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = (double[][])null;
      localObject2 = (double[][])null;
      localDoubleDCT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDCT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D DCT (double[] input)...");
    int j;
    int k;
    DoubleDCT_3D localDoubleDCT_3D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDCT_3D = new DoubleDCT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject1);
      localObject2 = new double[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject2);
      localDoubleDCT_3D.forward((double[])localObject1, true);
      localDoubleDCT_3D.inverse((double[])localObject1, true);
      for (m = 0; m < k * k * k; m++)
      {
        d1 = Math.abs(localObject2[m] - localObject1[m]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      localObject1 = null;
      localObject2 = null;
      localDoubleDCT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D DCT (double[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDCT_3D = new DoubleDCT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject1);
      localObject2 = new double[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject2);
      localDoubleDCT_3D.forward((double[][][])localObject1, true);
      localDoubleDCT_3D.inverse((double[][][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < k; n++)
          for (int i1 = 0; i1 < k; i1++)
          {
            d1 = Math.abs(localObject2[m][n][i1] - localObject1[m][n][i1]);
            d2 = Math.max(d2, d1);
          }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      localObject1 = (double[][][])null;
      localObject2 = (double[][][])null;
      localDoubleDCT_3D = null;
      System.gc();
    }
  }

  public static void main(String[] paramArrayOfString)
  {
    checkAccuracyDCT_1D(0, 21);
    checkAccuracyDCT_2D(1, 11);
    checkAccuracyDCT_3D(1, 7);
    System.exit(0);
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dct.AccuracyCheckDoubleDCT
 * JD-Core Version:    0.6.1
 */