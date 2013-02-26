package edu.emory.mathcs.jtransforms.dct;

import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class AccuracyCheckFloatDCT
{
  public static void checkAccuracyDCT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D DCT...");
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramInt1 + i;
      int k = (int)Math.pow(2.0D, j);
      FloatDCT_1D localFloatDCT_1D = new FloatDCT_1D(k);
      float f2 = 0.0F;
      float[] arrayOfFloat1 = new float[k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat1);
      float[] arrayOfFloat2 = new float[k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat2);
      localFloatDCT_1D.forward(arrayOfFloat1, true);
      localFloatDCT_1D.inverse(arrayOfFloat1, true);
      for (int m = 0; m < k; m++)
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
      localFloatDCT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDCT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D DCT (float[] input)...");
    int j;
    int k;
    FloatDCT_2D localFloatDCT_2D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDCT_2D = new FloatDCT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject1);
      localObject2 = new float[k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject2);
      localFloatDCT_2D.forward((float[])localObject1, true);
      localFloatDCT_2D.inverse((float[])localObject1, true);
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
      localFloatDCT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D DCT (float[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDCT_2D = new FloatDCT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject1);
      localObject2 = new float[k][k];
      IOUtils.fillMatrix_2D(k, k, (float[][])localObject2);
      localFloatDCT_2D.forward((float[][])localObject1, true);
      localFloatDCT_2D.inverse((float[][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < k; n++)
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
      localFloatDCT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDCT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D DCT (float[] input)...");
    int j;
    int k;
    FloatDCT_3D localFloatDCT_3D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDCT_3D = new FloatDCT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject1);
      localObject2 = new float[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject2);
      localFloatDCT_3D.forward((float[])localObject1, true);
      localFloatDCT_3D.inverse((float[])localObject1, true);
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
      localFloatDCT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D DCT (float[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDCT_3D = new FloatDCT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject1);
      localObject2 = new float[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject2);
      localFloatDCT_3D.forward((float[][][])localObject1, true);
      localFloatDCT_3D.inverse((float[][][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < k; n++)
          for (int i1 = 0; i1 < k; i1++)
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
      localFloatDCT_3D = null;
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.dct.AccuracyCheckFloatDCT
 * JD-Core Version:    0.6.1
 */