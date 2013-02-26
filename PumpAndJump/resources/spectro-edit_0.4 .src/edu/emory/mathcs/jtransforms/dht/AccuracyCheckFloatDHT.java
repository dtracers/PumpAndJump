package edu.emory.mathcs.jtransforms.dht;

import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class AccuracyCheckFloatDHT
{
  public static void checkAccuracyDHT_1D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 1D DHT...");
    for (int i = 0; i < paramInt2; i++)
    {
      int j = paramInt1 + i;
      int k = (int)Math.pow(2.0D, j);
      FloatDHT_1D localFloatDHT_1D = new FloatDHT_1D(k);
      float f2 = 0.0F;
      float[] arrayOfFloat1 = new float[k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat1);
      float[] arrayOfFloat2 = new float[k];
      IOUtils.fillMatrix_1D(k, arrayOfFloat2);
      localFloatDHT_1D.forward(arrayOfFloat1);
      localFloatDHT_1D.inverse(arrayOfFloat1, true);
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
      localFloatDHT_1D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDHT_2D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 2D DHT (float[] input)...");
    int j;
    int k;
    FloatDHT_2D localFloatDHT_2D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDHT_2D = new FloatDHT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject1);
      localObject2 = new float[k * k];
      IOUtils.fillMatrix_2D(k, k, (float[])localObject2);
      localFloatDHT_2D.forward((float[])localObject1);
      localFloatDHT_2D.inverse((float[])localObject1, true);
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
      localFloatDHT_2D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 2D DHT (float[][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDHT_2D = new FloatDHT_2D(k, k);
      f2 = 0.0F;
      localObject1 = new float[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (float[][])localObject1);
      localObject2 = new float[k][2 * k];
      IOUtils.fillMatrix_2D(k, 2 * k, (float[][])localObject2);
      localFloatDHT_2D.forward((float[][])localObject1);
      localFloatDHT_2D.inverse((float[][])localObject1, true);
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
      localFloatDHT_2D = null;
      System.gc();
    }
  }

  public static void checkAccuracyDHT_3D(int paramInt1, int paramInt2)
  {
    System.out.println("Checking accuracy of 3D DHT (float[] input)...");
    int j;
    int k;
    FloatDHT_3D localFloatDHT_3D;
    float f2;
    Object localObject1;
    Object localObject2;
    int m;
    float f1;
    for (int i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDHT_3D = new FloatDHT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject1);
      localObject2 = new float[k * k * k];
      IOUtils.fillMatrix_3D(k, k, k, (float[])localObject2);
      localFloatDHT_3D.forward((float[])localObject1);
      localFloatDHT_3D.inverse((float[])localObject1, true);
      for (m = 0; m < k * k * k; m++)
      {
        f1 = Math.abs(localObject2[m] - localObject1[m]);
        f2 = Math.max(f2, f1);
      }
      if (f2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = null;
      localObject2 = null;
      localFloatDHT_3D = null;
      System.gc();
    }
    System.out.println("Checking accuracy of 3D DHT (float[][][] input)...");
    for (i = 0; i < paramInt2; i++)
    {
      j = paramInt1 + i;
      k = (int)Math.pow(2.0D, j);
      localFloatDHT_3D = new FloatDHT_3D(k, k, k);
      f2 = 0.0F;
      localObject1 = new float[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject1);
      localObject2 = new float[k][k][k];
      IOUtils.fillMatrix_3D(k, k, k, (float[][][])localObject2);
      localFloatDHT_3D.forward((float[][][])localObject1);
      localFloatDHT_3D.inverse((float[][][])localObject1, true);
      for (m = 0; m < k; m++)
        for (int n = 0; n < k; n++)
          for (int i1 = 0; i1 < k; i1++)
          {
            f1 = Math.abs(localObject2[m][n][i1] - localObject1[m][n][i1]);
            f2 = Math.max(f2, f1);
          }
      if (f2 > 1.0E-10D)
        System.err.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      else
        System.out.println("\tsize = 2^" + j + " x 2^" + j + " x 2^" + j + ";\t\terror = " + f2);
      localObject1 = (float[][][])null;
      localObject2 = (float[][][])null;
      localFloatDHT_3D = null;
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.dht.AccuracyCheckFloatDHT
 * JD-Core Version:    0.6.1
 */