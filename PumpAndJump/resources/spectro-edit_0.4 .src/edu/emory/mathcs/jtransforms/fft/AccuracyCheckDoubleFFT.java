package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class AccuracyCheckDoubleFFT
{
  public static void checkAccuracyComplexFFT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D complex FFT...");
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramInt1 + i;
      int k = (int)Math.pow(2.0D, j);
      DoubleFFT_1D localDoubleFFT_1D = new DoubleFFT_1D(k);
      double d2 = 0.0D;
      double[] arrayOfDouble1 = new double[2 * k];
      IOUtils.fillMatrix_1D(2 * k, arrayOfDouble1);
      double[] arrayOfDouble2 = new double[2 * k];
      IOUtils.fillMatrix_1D(2 * k, arrayOfDouble2);
      localDoubleFFT_1D.complexForward(arrayOfDouble1);
      localDoubleFFT_1D.complexInverse(arrayOfDouble1, true);
      for (int m = 0; m < 2 * k; m++)
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
      localDoubleFFT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyComplexFFT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D complex FFT (double[] input)...");
    int j;
    int k;
    DoubleFFT_2D localDoubleFFT_2D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[2 * k * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (double[])localObject1);
      localObject2 = new double[2 * k * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (double[])localObject2);
      localDoubleFFT_2D.complexForward((double[])localObject1);
      localDoubleFFT_2D.complexInverse((double[])localObject1, true);
      for (m = 0; m < 2 * k * k; m++)
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
      localDoubleFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D complex FFT (double[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (double[][])localObject1);
      localObject2 = new double[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (double[][])localObject2);
      localDoubleFFT_2D.complexForward((double[][])localObject1);
      localDoubleFFT_2D.complexInverse((double[][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < 2 * k; n++)
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
      localDoubleFFT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyComplexFFT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D complex FFT (double[] input)...");
    int j;
    int k;
    DoubleFFT_3D localDoubleFFT_3D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (double[])localObject1);
      localObject2 = new double[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (double[])localObject2);
      localDoubleFFT_3D.complexForward((double[])localObject1);
      localDoubleFFT_3D.complexInverse((double[])localObject1, true);
      for (m = 0; m < 2 * k * k * k; m++)
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
      localDoubleFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D complex FFT (double[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (double[][][])localObject1);
      localObject2 = new double[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (double[][][])localObject2);
      localDoubleFFT_3D.complexForward((double[][][])localObject1);
      localDoubleFFT_3D.complexInverse((double[][][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < k; n++)
          for (int i1 = 0; i1 < 2 * k; i1++)
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
      localDoubleFFT_3D = null;
      System.gc();
    }
  }

  public static void checkAccuracyRealFFT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D real FFT...");
    int j;
    int k;
    DoubleFFT_1D localDoubleFFT_1D;
    double d2;
    double[] arrayOfDouble1;
    double[] arrayOfDouble2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_1D = new DoubleFFT_1D(k);
      d2 = 0.0D;
      arrayOfDouble1 = new double[k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble1);
      arrayOfDouble2 = new double[k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble2);
      localDoubleFFT_1D.realForward(arrayOfDouble2);
      localDoubleFFT_1D.realInverse(arrayOfDouble2, true);
      for (m = 0; m < k; m++)
      {
        d1 = Math.abs(arrayOfDouble2[m] - arrayOfDouble1[m]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + d2);
      arrayOfDouble1 = null;
      arrayOfDouble2 = null;
      localDoubleFFT_1D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of on 1D real forward full FFT...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_1D = new DoubleFFT_1D(k);
      d2 = 0.0D;
      arrayOfDouble1 = new double[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble1);
      arrayOfDouble2 = new double[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble2);
      localDoubleFFT_1D.realForwardFull(arrayOfDouble2);
      localDoubleFFT_1D.complexInverse(arrayOfDouble2, true);
      for (m = 0; m < k; m++)
      {
        d1 = Math.abs(arrayOfDouble2[(2 * m)] - arrayOfDouble1[m]);
        d2 = Math.max(d2, d1);
        d1 = Math.abs(arrayOfDouble2[(2 * m + 1)]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + d2);
      arrayOfDouble1 = null;
      arrayOfDouble2 = null;
      localDoubleFFT_1D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 1D real inverse full FFT...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_1D = new DoubleFFT_1D(k);
      d2 = 0.0D;
      arrayOfDouble1 = new double[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble1);
      arrayOfDouble2 = new double[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfDouble2);
      localDoubleFFT_1D.realInverseFull(arrayOfDouble2, true);
      localDoubleFFT_1D.complexForward(arrayOfDouble2);
      for (m = 0; m < k; m++)
      {
        d1 = Math.abs(arrayOfDouble2[(2 * m)] - arrayOfDouble1[m]);
        d2 = Math.max(d2, d1);
        d1 = Math.abs(arrayOfDouble2[(2 * m + 1)]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + d2);
      arrayOfDouble1 = null;
      arrayOfDouble2 = null;
      localDoubleFFT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyRealFFT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D real FFT (double[] input)...");
    int j;
    int k;
    DoubleFFT_2D localDoubleFFT_2D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject1);
      localObject2 = new double[k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject2);
      localDoubleFFT_2D.realForward((double[])localObject2);
      localDoubleFFT_2D.realInverse((double[])localObject2, true);
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
      localDoubleFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real FFT (double[][] input)...");
    int n;
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject1);
      localObject2 = new double[k][k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject2);
      localDoubleFFT_2D.realForward((double[][])localObject2);
      localDoubleFFT_2D.realInverse((double[][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
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
      localDoubleFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real forward full FFT (double[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject1);
      localObject2 = new double[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject2);
      localDoubleFFT_2D.realForwardFull((double[])localObject2);
      localDoubleFFT_2D.complexInverse((double[])localObject2, true);
      for (m = 0; m < k * k; m++)
      {
        d1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        d2 = Math.max(d2, d1);
        d1 = Math.abs(localObject2[(2 * m + 1)]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = null;
      localObject2 = null;
      localDoubleFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real forward full FFT (double[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject1);
      localObject2 = new double[k][2 * k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject2);
      localDoubleFFT_2D.realForwardFull((double[][])localObject2);
      localDoubleFFT_2D.complexInverse((double[][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
        {
          d1 = Math.abs(localObject2[m][(2 * n)] - localObject1[m][n]);
          d2 = Math.max(d2, d1);
          d1 = Math.abs(localObject2[m][(2 * n + 1)]);
          d2 = Math.max(d2, d1);
        }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = (double[][])null;
      localObject2 = (double[][])null;
      localDoubleFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real inverse full FFT (double[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject1);
      localObject2 = new double[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (double[])localObject2);
      localDoubleFFT_2D.realInverseFull((double[])localObject2, true);
      localDoubleFFT_2D.complexForward((double[])localObject2);
      for (m = 0; m < k * k; m++)
      {
        d1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        d2 = Math.max(d2, d1);
        d1 = Math.abs(localObject2[(2 * m + 1)]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = null;
      localObject2 = null;
      localDoubleFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real inverse full FFT (double[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject1);
      localObject2 = new double[k][2 * k];
      IOUtils.fillMatrix_2D(k, k, (double[][])localObject2);
      localDoubleFFT_2D.realInverseFull((double[][])localObject2, true);
      localDoubleFFT_2D.complexForward((double[][])localObject2);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
        {
          d1 = Math.abs(localObject2[m][(2 * n)] - localObject1[m][n]);
          d2 = Math.max(d2, d1);
          d1 = Math.abs(localObject2[m][(2 * n + 1)]);
          d2 = Math.max(d2, d1);
        }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + d2);
      localObject1 = (double[][])null;
      localObject2 = (double[][])null;
      localDoubleFFT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyRealFFT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D real FFT (double[] input)...");
    int j;
    int k;
    DoubleFFT_3D localDoubleFFT_3D;
    double d2;
    Object localObject1;
    Object localObject2;
    int m;
    double d1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject1);
      localObject2 = new double[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject2);
      localDoubleFFT_3D.realForward((double[])localObject2);
      localDoubleFFT_3D.realInverse((double[])localObject2, true);
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
      localDoubleFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real FFT (double[][][] input)...");
    int n;
    int i1;
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject1);
      localObject2 = new double[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject2);
      localDoubleFFT_3D.realForward((double[][][])localObject2);
      localDoubleFFT_3D.realInverse((double[][][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
          for (i1 = 0; i1 < k; i1++)
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
      localDoubleFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real forward full FFT (double[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject1);
      localObject2 = new double[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject2);
      localDoubleFFT_3D.realForwardFull((double[])localObject2);
      localDoubleFFT_3D.complexInverse((double[])localObject2, true);
      for (m = 0; m < k * k * k; m++)
      {
        d1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        d2 = Math.max(d2, d1);
        d1 = Math.abs(localObject2[(2 * m + 1)]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      localObject1 = null;
      localObject2 = null;
      localDoubleFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real forward full FFT (double[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject1);
      localObject2 = new double[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject2);
      localDoubleFFT_3D.realForwardFull((double[][][])localObject2);
      localDoubleFFT_3D.complexInverse((double[][][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
          for (i1 = 0; i1 < k; i1++)
          {
            d1 = Math.abs(localObject2[m][n][(2 * i1)] - localObject1[m][n][i1]);
            d2 = Math.max(d2, d1);
            d1 = Math.abs(localObject2[m][n][(2 * i1 + 1)]);
            d2 = Math.max(d2, d1);
          }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      localObject1 = (double[][][])null;
      localObject2 = (double[][][])null;
      localDoubleFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real inverse full FFT (double[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject1);
      localObject2 = new double[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[])localObject2);
      localDoubleFFT_3D.realInverseFull((double[])localObject2, true);
      localDoubleFFT_3D.complexForward((double[])localObject2);
      for (m = 0; m < k * k * k; m++)
      {
        d1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        d2 = Math.max(d2, d1);
        d1 = Math.abs(localObject2[(2 * m + 1)]);
        d2 = Math.max(d2, d1);
      }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      localObject1 = null;
      localObject2 = null;
      localDoubleFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real inverse full FFT (double[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      d2 = 0.0D;
      localObject1 = new double[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject1);
      localObject2 = new double[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (double[][][])localObject2);
      localDoubleFFT_3D.realInverseFull((double[][][])localObject2, true);
      localDoubleFFT_3D.complexForward((double[][][])localObject2);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
          for (i1 = 0; i1 < k; i1++)
          {
            d1 = Math.abs(localObject2[m][n][(2 * i1)] - localObject1[m][n][i1]);
            d2 = Math.max(d2, d1);
            d1 = Math.abs(localObject2[m][n][(2 * i1 + 1)]);
            d2 = Math.max(d2, d1);
          }
      if (d2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + d2);
      localObject1 = (double[][][])null;
      localObject2 = (double[][][])null;
      localDoubleFFT_3D = null;
      System.gc();
    }
  }

  public static void main(String[] paramArrayOfString)
  {
    checkAccuracyComplexFFT_1D(0, 21);
    checkAccuracyRealFFT_1D(0, 21);
    checkAccuracyComplexFFT_2D(1, 11);
    checkAccuracyRealFFT_2D(1, 11);
    checkAccuracyComplexFFT_3D(1, 7);
    checkAccuracyRealFFT_3D(1, 7);
    System.exit(0);
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.AccuracyCheckDoubleFFT
 * JD-Core Version:    0.6.1
 */