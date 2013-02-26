package edu.emory.mathcs.jtransforms.dht;

import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class AccuracyCheckDoubleDHT
{
  public static void checkAccuracyDHT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D DHT...");
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramInt1 + i;
      int k = (int)Math.pow(2.0D, j);
      DoubleDHT_1D localDoubleDHT_1D = new DoubleDHT_1D(k);
      double d2 = 0.0D;
      double[] arrayOfDouble1 = new double[k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble1);
      double[] arrayOfDouble2 = new double[k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble2);
      localDoubleDHT_1D.forward(arrayOfDouble1);
      localDoubleDHT_1D.inverse(arrayOfDouble1, true);
      for (int m = 0; m < k; m++)
      {
        double d1 = Math.abs(arrayOfDouble2[m] - arrayOfDouble1[m]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + d2);
      arrayOfDouble1 = null;
      arrayOfDouble2 = null;
      localDoubleDHT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDHT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D DHT (double[] input)...");
    int j;
    int k;
    DoubleDHT_2D localDoubleDHT_2D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDHT_2D = new DoubleDHT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject1);
      localObject2 = new double[k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject2);
      localDoubleDHT_2D.forward((double[])localObject1);
      localDoubleDHT_2D.inverse((double[])localObject1, true);
      for (m = 0; m < k * k; m++)
      {
        d1 = Math.abs(localObject2[m] - localObject1[m]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = null;
      localObject2 = null;
      localDoubleDHT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D DHT (double[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDHT_2D = new DoubleDHT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (double[][])localObject1);
      localObject2 = new double[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (double[][])localObject2);
      localDoubleDHT_2D.forward((double[][])localObject1);
      localDoubleDHT_2D.inverse((double[][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < 2 * k; n++)
        {
          d1 = Math.abs(localObject2[m][n] - localObject1[m][n]);
          d2 = Math.max(d2, d1);
        }
      if (d2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = (double[][])null;
      localObject2 = (double[][])null;
      localDoubleDHT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDHT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D DHT (double[] input)...");
    int j;
    int k;
    DoubleDHT_3D localDoubleDHT_3D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDHT_3D = new DoubleDHT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject1);
      localObject2 = new double[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject2);
      localDoubleDHT_3D.forward((double[])localObject1);
      localDoubleDHT_3D.inverse((double[])localObject1, true);
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
      localDoubleDHT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D DHT (double[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleDHT_3D = new DoubleDHT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject1);
      localObject2 = new double[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject2);
      localDoubleDHT_3D.forward((double[][][])localObject1);
      localDoubleDHT_3D.inverse((double[][][])localObject1, true);
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
      localDoubleDHT_3D = null;
      System.gc();
    }
  }

  public static void main(String[] paramArrayOfString)
  {
    checkAccuracyDHT_1D(0, 21);
    checkAccuracyDHT_2D(1, 11);
    checkAccuracyDHT_3D(1, 7);
    System.exit(0);
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dht.AccuracyCheckDoubleDHT
 * JD-Core Version:    0.6.1
 */