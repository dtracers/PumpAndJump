package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class AccuracyCheckFloatFFT
{
  public static void checkAccuracyComplexFFT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D complex FFT...");
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramInt1 + i;
      int k = (int)Math.pow(2.0D, j);
      FloatFFT_1D localFloatFFT_1D = new FloatFFT_1D(k);
      float f2 = 0.0F;
      float[] arrayOfFloat1 = new float[2 * k];
      IOUtils.fillMatrix_1D(2 * k, arrayOfFloat1);
      float[] arrayOfFloat2 = new float[2 * k];
      IOUtils.fillMatrix_1D(2 * k, arrayOfFloat2);
      localFloatFFT_1D.complexForward(arrayOfFloat1);
      localFloatFFT_1D.complexInverse(arrayOfFloat1, true);
      for (int m = 0; m < 2 * k; m++)
      {
        float f1 = Math.abs(arrayOfFloat2[m] - arrayOfFloat1[m]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + f2);
      arrayOfFloat1 = null;
      arrayOfFloat2 = null;
      localFloatFFT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyComplexFFT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D complex FFT (float[] input)...");
    int j;
    int k;
    FloatFFT_2D localFloatFFT_2D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[2 * k * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (float[])localObject1);
      localObject2 = new float[2 * k * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (float[])localObject2);
      localFloatFFT_2D.complexForward((float[])localObject1);
      localFloatFFT_2D.complexInverse((float[])localObject1, true);
      for (m = 0; m < 2 * k * k; m++)
      {
        f1 = Math.abs(localObject2[m] - localObject1[m]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D complex FFT (float[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (float[][])localObject1);
      localObject2 = new float[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (float[][])localObject2);
      localFloatFFT_2D.complexForward((float[][])localObject1);
      localFloatFFT_2D.complexInverse((float[][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < 2 * k; n++)
        {
          f1 = Math.abs(localObject2[m][n] - localObject1[m][n]);
          f2 = Math.max(f2, f1);
        }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = (float[][])null;
      localObject2 = (float[][])null;
      localFloatFFT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyComplexFFT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D complex FFT (float[] input)...");
    int j;
    int k;
    FloatFFT_3D localFloatFFT_3D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (float[])localObject1);
      localObject2 = new float[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (float[])localObject2);
      localFloatFFT_3D.complexForward((float[])localObject1);
      localFloatFFT_3D.complexInverse((float[])localObject1, true);
      for (m = 0; m < 2 * k * k * k; m++)
      {
        f1 = Math.abs(localObject2[m] - localObject1[m]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D complex FFT (float[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (float[][][])localObject1);
      localObject2 = new float[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, 2 * k, (float[][][])localObject2);
      localFloatFFT_3D.complexForward((float[][][])localObject1);
      localFloatFFT_3D.complexInverse((float[][][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < k; n++)
          for (int i1 = 0; i1 < 2 * k; i1++)
          {
            f1 = Math.abs(localObject2[m][n][i1] - localObject1[m][n][i1]);
            f2 = Math.max(f2, f1);
          }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = (float[][][])null;
      localObject2 = (float[][][])null;
      localFloatFFT_3D = null;
      System.gc();
    }
  }

  public static void checkAccuracyRealFFT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D real FFT...");
    int j;
    int k;
    FloatFFT_1D localFloatFFT_1D;
    float f2;
    float[] arrayOfFloat1;
    float[] arrayOfFloat2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_1D = new FloatFFT_1D(k);
      f2 = 0.0F;
      arrayOfFloat1 = new float[k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat1);
      arrayOfFloat2 = new float[k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat2);
      localFloatFFT_1D.realForward(arrayOfFloat2);
      localFloatFFT_1D.realInverse(arrayOfFloat2, true);
      for (m = 0; m < k; m++)
      {
        f1 = Math.abs(arrayOfFloat2[m] - arrayOfFloat1[m]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + f2);
      arrayOfFloat1 = null;
      arrayOfFloat2 = null;
      localFloatFFT_1D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of on 1D real forward full FFT...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_1D = new FloatFFT_1D(k);
      f2 = 0.0F;
      arrayOfFloat1 = new float[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat1);
      arrayOfFloat2 = new float[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat2);
      localFloatFFT_1D.realForwardFull(arrayOfFloat2);
      localFloatFFT_1D.complexInverse(arrayOfFloat2, true);
      for (m = 0; m < k; m++)
      {
        f1 = Math.abs(arrayOfFloat2[(2 * m)] - arrayOfFloat1[m]);
        f2 = Math.max(f2, f1);
        f1 = Math.abs(arrayOfFloat2[(2 * m + 1)]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + f2);
      arrayOfFloat1 = null;
      arrayOfFloat2 = null;
      localFloatFFT_1D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 1D real inverse full FFT...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_1D = new FloatFFT_1D(k);
      f2 = 0.0F;
      arrayOfFloat1 = new float[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat1);
      arrayOfFloat2 = new float[2 * k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat2);
      localFloatFFT_1D.realInverseFull(arrayOfFloat2, true);
      localFloatFFT_1D.complexForward(arrayOfFloat2);
      for (m = 0; m < k; m++)
      {
        f1 = Math.abs(arrayOfFloat2[(2 * m)] - arrayOfFloat1[m]);
        f2 = Math.max(f2, f1);
        f1 = Math.abs(arrayOfFloat2[(2 * m + 1)]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + ";\terror = " + f2);
      arrayOfFloat1 = null;
      arrayOfFloat2 = null;
      localFloatFFT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyRealFFT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D real FFT (float[] input)...");
    int j;
    int k;
    FloatFFT_2D localFloatFFT_2D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject1);
      localObject2 = new float[k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject2);
      localFloatFFT_2D.realForward((float[])localObject2);
      localFloatFFT_2D.realInverse((float[])localObject2, true);
      for (m = 0; m < k * k; m++)
      {
        f1 = Math.abs(localObject2[m] - localObject1[m]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real FFT (float[][] input)...");
    int n;
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject1);
      localObject2 = new float[k][k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject2);
      localFloatFFT_2D.realForward((float[][])localObject2);
      localFloatFFT_2D.realInverse((float[][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
        {
          f1 = Math.abs(localObject2[m][n] - localObject1[m][n]);
          f2 = Math.max(f2, f1);
        }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = (float[][])null;
      localObject2 = (float[][])null;
      localFloatFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real forward full FFT (float[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject1);
      localObject2 = new float[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject2);
      localFloatFFT_2D.realForwardFull((float[])localObject2);
      localFloatFFT_2D.complexInverse((float[])localObject2, true);
      for (m = 0; m < k * k; m++)
      {
        f1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        f2 = Math.max(f2, f1);
        f1 = Math.abs(localObject2[(2 * m + 1)]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real forward full FFT (float[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject1);
      localObject2 = new float[k][2 * k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject2);
      localFloatFFT_2D.realForwardFull((float[][])localObject2);
      localFloatFFT_2D.complexInverse((float[][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
        {
          f1 = Math.abs(localObject2[m][(2 * n)] - localObject1[m][n]);
          f2 = Math.max(f2, f1);
          f1 = Math.abs(localObject2[m][(2 * n + 1)]);
          f2 = Math.max(f2, f1);
        }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = (float[][])null;
      localObject2 = (float[][])null;
      localFloatFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real inverse full FFT (float[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject1);
      localObject2 = new float[2 * k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject2);
      localFloatFFT_2D.realInverseFull((float[])localObject2, true);
      localFloatFFT_2D.complexForward((float[])localObject2);
      for (m = 0; m < k * k; m++)
      {
        f1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        f2 = Math.max(f2, f1);
        f1 = Math.abs(localObject2[(2 * m + 1)]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D real inverse full FFT (float[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_2D = new FloatFFT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject1);
      localObject2 = new float[k][2 * k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject2);
      localFloatFFT_2D.realInverseFull((float[][])localObject2, true);
      localFloatFFT_2D.complexForward((float[][])localObject2);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
        {
          f1 = Math.abs(localObject2[m][(2 * n)] - localObject1[m][n]);
          f2 = Math.max(f2, f1);
          f1 = Math.abs(localObject2[m][(2 * n + 1)]);
          f2 = Math.max(f2, f1);
        }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + ";\terror = " + f2);
      localObject1 = (float[][])null;
      localObject2 = (float[][])null;
      localFloatFFT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyRealFFT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D real FFT (float[] input)...");
    int j;
    int k;
    FloatFFT_3D localFloatFFT_3D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject1);
      localObject2 = new float[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject2);
      localFloatFFT_3D.realForward((float[])localObject2);
      localFloatFFT_3D.realInverse((float[])localObject2, true);
      for (m = 0; m < k * k * k; m++)
      {
        f1 = Math.abs(localObject2[m] - localObject1[m]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real FFT (float[][][] input)...");
    int n;
    int i1;
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject1);
      localObject2 = new float[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject2);
      localFloatFFT_3D.realForward((float[][][])localObject2);
      localFloatFFT_3D.realInverse((float[][][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
          for (i1 = 0; i1 < k; i1++)
          {
            f1 = Math.abs(localObject2[m][n][i1] - localObject1[m][n][i1]);
            f2 = Math.max(f2, f1);
          }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = (float[][][])null;
      localObject2 = (float[][][])null;
      localFloatFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real forward full FFT (float[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject1);
      localObject2 = new float[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject2);
      localFloatFFT_3D.realForwardFull((float[])localObject2);
      localFloatFFT_3D.complexInverse((float[])localObject2, true);
      for (m = 0; m < k * k * k; m++)
      {
        f1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        f2 = Math.max(f2, f1);
        f1 = Math.abs(localObject2[(2 * m + 1)]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real forward full FFT (float[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject1);
      localObject2 = new float[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject2);
      localFloatFFT_3D.realForwardFull((float[][][])localObject2);
      localFloatFFT_3D.complexInverse((float[][][])localObject2, true);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
          for (i1 = 0; i1 < k; i1++)
          {
            f1 = Math.abs(localObject2[m][n][(2 * i1)] - localObject1[m][n][i1]);
            f2 = Math.max(f2, f1);
            f1 = Math.abs(localObject2[m][n][(2 * i1 + 1)]);
            f2 = Math.max(f2, f1);
          }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = (float[][][])null;
      localObject2 = (float[][][])null;
      localFloatFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real inverse full FFT (float[] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject1);
      localObject2 = new float[2 * k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject2);
      localFloatFFT_3D.realInverseFull((float[])localObject2, true);
      localFloatFFT_3D.complexForward((float[])localObject2);
      for (m = 0; m < k * k * k; m++)
      {
        f1 = Math.abs(localObject2[(2 * m)] - localObject1[m]);
        f2 = Math.max(f2, f1);
        f1 = Math.abs(localObject2[(2 * m + 1)]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatFFT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D real inverse full FFT (float[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatFFT_3D = new FloatFFT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject1);
      localObject2 = new float[k][k][2 * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject2);
      localFloatFFT_3D.realInverseFull((float[][][])localObject2, true);
      localFloatFFT_3D.complexForward((float[][][])localObject2);
      for (m = 0; m < k; m++)
        for (n = 0; n < k; n++)
          for (i1 = 0; i1 < k; i1++)
          {
            f1 = Math.abs(localObject2[m][n][(2 * i1)] - localObject1[m][n][i1]);
            f2 = Math.max(f2, f1);
            f1 = Math.abs(localObject2[m][n][(2 * i1 + 1)]);
            f2 = Math.max(f2, f1);
          }
      if (f2 > 1.E-05D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = (float[][][])null;
      localObject2 = (float[][][])null;
      localFloatFFT_3D = null;
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.AccuracyCheckFloatFFT
 * JD-Core Version:    0.6.1
 */