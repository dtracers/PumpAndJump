package edu.emory.mathcs.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Random;

public class IOUtils
{
  private static final String FF = "%.4f";

  public static void fillMatrix_1D(int paramInt, double[] paramArrayOfDouble)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt; i++)
      paramArrayOfDouble[i] = localRandom.nextDouble();
  }

  public static void fillMatrix_1D(int paramInt, float[] paramArrayOfFloat)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt; i++)
      paramArrayOfFloat[i] = localRandom.nextFloat();
  }

  public static void fillMatrix_2D(int paramInt1, int paramInt2, double[] paramArrayOfDouble)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt1; i++)
      for (int j = 0; j < paramInt2; j++)
        paramArrayOfDouble[(i * paramInt2 + j)] = localRandom.nextDouble();
  }

  public static void fillMatrix_2D(int paramInt1, int paramInt2, float[] paramArrayOfFloat)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt1; i++)
      for (int j = 0; j < paramInt2; j++)
        paramArrayOfFloat[(i * paramInt2 + j)] = localRandom.nextFloat();
  }

  public static void fillMatrix_2D(int paramInt1, int paramInt2, double[][] paramArrayOfDouble)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt1; i++)
      for (int j = 0; j < paramInt2; j++)
        paramArrayOfDouble[i][j] = localRandom.nextDouble();
  }

  public static void fillMatrix_2D(int paramInt1, int paramInt2, float[][] paramArrayOfFloat)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt1; i++)
      for (int j = 0; j < paramInt2; j++)
        paramArrayOfFloat[i][j] = localRandom.nextFloat();
  }

  public static void fillMatrix_3D(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble)
  {
    Random localRandom = new Random(2L);
    int i = paramInt2 * paramInt3;
    int j = paramInt3;
    for (int k = 0; k < paramInt1; k++)
      for (int m = 0; m < paramInt2; m++)
        for (int n = 0; n < paramInt3; n++)
          paramArrayOfDouble[(k * i + m * j + n)] = localRandom.nextDouble();
  }

  public static void fillMatrix_3D(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat)
  {
    Random localRandom = new Random(2L);
    int i = paramInt2 * paramInt3;
    int j = paramInt3;
    for (int k = 0; k < paramInt1; k++)
      for (int m = 0; m < paramInt2; m++)
        for (int n = 0; n < paramInt3; n++)
          paramArrayOfFloat[(k * i + m * j + n)] = localRandom.nextFloat();
  }

  public static void fillMatrix_3D(int paramInt1, int paramInt2, int paramInt3, double[][][] paramArrayOfDouble)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt1; i++)
      for (int j = 0; j < paramInt2; j++)
        for (int k = 0; k < paramInt3; k++)
          paramArrayOfDouble[i][j][k] = localRandom.nextDouble();
  }

  public static void fillMatrix_3D(int paramInt1, int paramInt2, int paramInt3, float[][][] paramArrayOfFloat)
  {
    Random localRandom = new Random(2L);
    for (int i = 0; i < paramInt1; i++)
      for (int j = 0; j < paramInt2; j++)
        for (int k = 0; k < paramInt3; k++)
          paramArrayOfFloat[i][j][k] = localRandom.nextFloat();
  }

  public static void showComplex_1D(double[] paramArrayOfDouble, String paramString)
  {
    System.out.println(paramString);
    System.out.println("-------------------");
    int i = 0;
    while (i < paramArrayOfDouble.length)
    {
      if (paramArrayOfDouble[(i + 1)] == 0.0D)
        System.out.println(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }));
      else if (paramArrayOfDouble[i] == 0.0D)
        System.out.println(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i + 1)]) }) + "i");
      else if (paramArrayOfDouble[(i + 1)] < 0.0D)
        System.out.println(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }) + " - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[(i + 1)]) }) + "i");
      else
        System.out.println(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }) + " + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i + 1)]) }) + "i");
      i += 2;
    }
    System.out.println();
  }

  public static void showComplex_2D(int paramInt1, int paramInt2, double[] paramArrayOfDouble, String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(String.format(paramString + ": complex array 2D: %d rows, %d columns\n\n", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
    for (int i = 0; i < paramInt1; i++)
    {
      int j = 0;
      while (j < 2 * paramInt2)
      {
        if (paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)] == 0.0D)
          localStringBuffer.append(String.format("%.4f\t", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j)]) }));
        else if (paramArrayOfDouble[(i * 2 * paramInt2 + j)] == 0.0D)
          localStringBuffer.append(String.format("%.4fi\t", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) }));
        else if (paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)] < 0.0D)
          localStringBuffer.append(String.format("%.4f - %.4fi\t", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j)]), Double.valueOf(-paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) }));
        else
          localStringBuffer.append(String.format("%.4f + %.4fi\t", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j)]), Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) }));
        j += 2;
      }
      localStringBuffer.append("\n");
    }
    System.out.println(localStringBuffer.toString());
  }

  public static void showComplex_3D(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble, String paramString)
  {
    int i = paramInt2 * 2 * paramInt3;
    int j = 2 * paramInt3;
    System.out.println(paramString);
    System.out.println("-------------------");
    int k = 0;
    while (k < 2 * paramInt3)
    {
      System.out.println("(:,:," + k / 2 + ")=\n");
      for (int m = 0; m < paramInt1; m++)
      {
        for (int n = 0; n < paramInt2; n++)
          if (paramArrayOfDouble[(m * i + n * j + k + 1)] == 0.0D)
            System.out.print(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + "\t");
          else if (paramArrayOfDouble[(m * i + n * j + k)] == 0.0D)
            System.out.print(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k + 1)]) }) + "i\t");
          else if (paramArrayOfDouble[(m * i + n * j + k + 1)] < 0.0D)
            System.out.print(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + " - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[(m * i + n * j + k + 1)]) }) + "i\t");
          else
            System.out.print(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + " + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k + 1)]) }) + "i\t");
        System.out.println("");
      }
      k += 2;
    }
    System.out.println("");
  }

  public static void showReal_1D(double[] paramArrayOfDouble, String paramString)
  {
    System.out.println(paramString);
    System.out.println("-------------------");
    for (int i = 0; i < paramArrayOfDouble.length; i++)
      System.out.println(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }));
    System.out.println();
  }

  public static void showReal_2D(int paramInt1, int paramInt2, double[] paramArrayOfDouble, String paramString)
  {
    System.out.println(paramString);
    System.out.println("-------------------");
    for (int i = 0; i < paramInt1; i++)
    {
      for (int j = 0; j < paramInt2; j++)
        if (Math.abs(paramArrayOfDouble[(i * paramInt2 + j)]) < 5.E-05D)
          System.out.print("0\t");
        else
          System.out.print(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * paramInt2 + j)]) }) + "\t");
      System.out.println();
    }
    System.out.println();
  }

  public static void showReal_3D(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble, String paramString)
  {
    int i = paramInt2 * paramInt3;
    int j = paramInt3;
    System.out.println(paramString);
    System.out.println("-------------------");
    for (int k = 0; k < paramInt3; k++)
    {
      System.out.println();
      System.out.println("(:,:," + k + ")=\n");
      for (int m = 0; m < paramInt1; m++)
      {
        for (int n = 0; n < paramInt2; n++)
          if (Math.abs(paramArrayOfDouble[(m * i + n * j + k)]) <= 5.E-05D)
            System.out.print("0\t");
          else
            System.out.print(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + "\t");
        System.out.println();
      }
    }
    System.out.println();
  }

  public static void writeToFileComplex_1D(double[] paramArrayOfDouble, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      int i = 0;
      while (i < paramArrayOfDouble.length)
      {
        if (paramArrayOfDouble[(i + 1)] == 0.0D)
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }));
          localBufferedWriter.newLine();
        }
        else if (paramArrayOfDouble[i] == 0.0D)
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i + 1)]) }) + "i");
          localBufferedWriter.newLine();
        }
        else if (paramArrayOfDouble[(i + 1)] < 0.0D)
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }) + " - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[(i + 1)]) }) + "i");
          localBufferedWriter.newLine();
        }
        else
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }) + " + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i + 1)]) }) + "i");
          localBufferedWriter.newLine();
        }
        i += 2;
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileComplex_1D(float[] paramArrayOfFloat, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      int i = 0;
      while (i < paramArrayOfFloat.length)
      {
        if (paramArrayOfFloat[(i + 1)] == 0.0F)
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[i]) }));
          localBufferedWriter.newLine();
        }
        else if (paramArrayOfFloat[i] == 0.0F)
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i + 1)]) }) + "i");
          localBufferedWriter.newLine();
        }
        else if (paramArrayOfFloat[(i + 1)] < 0.0F)
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[i]) }) + " - " + String.format("%.4f", new Object[] { Float.valueOf(-paramArrayOfFloat[(i + 1)]) }) + "i");
          localBufferedWriter.newLine();
        }
        else
        {
          localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[i]) }) + " + " + String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i + 1)]) }) + "i");
          localBufferedWriter.newLine();
        }
        i += 2;
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileComplex_2D(int paramInt1, int paramInt2, double[] paramArrayOfDouble, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int i = 0; i < paramInt1; i++)
      {
        int j = 0;
        while (j < 2 * paramInt2)
        {
          if ((Math.abs(paramArrayOfDouble[(i * 2 * paramInt2 + j)]) < 5.E-05D) && (Math.abs(paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) < 5.E-05D))
          {
            if (paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)] >= 0.0D)
              localBufferedWriter.write("0 + 0i\t");
            else
              localBufferedWriter.write("0 - 0i\t");
          }
          else if (Math.abs(paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) < 5.E-05D)
          {
            if (paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)] >= 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j)]) }) + " + 0i\t");
            else
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j)]) }) + " - 0i\t");
          }
          else if (Math.abs(paramArrayOfDouble[(i * 2 * paramInt2 + j)]) < 5.E-05D)
          {
            if (paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)] >= 0.0D)
              localBufferedWriter.write("0 + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
            else
              localBufferedWriter.write("0 - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
          }
          else if (paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)] < 0.0D)
            localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j)]) }) + " - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
          else
            localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j)]) }) + " + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
          j += 2;
        }
        localBufferedWriter.newLine();
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileComplex_2D(int paramInt1, int paramInt2, float[] paramArrayOfFloat, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int i = 0; i < paramInt1; i++)
      {
        int j = 0;
        while (j < 2 * paramInt2)
        {
          if ((Math.abs(paramArrayOfFloat[(i * 2 * paramInt2 + j)]) < 5.E-05D) && (Math.abs(paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)]) < 5.E-05D))
          {
            if (paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)] >= 0.0D)
              localBufferedWriter.write("0 + 0i\t");
            else
              localBufferedWriter.write("0 - 0i\t");
          }
          else if (Math.abs(paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)]) < 5.E-05D)
          {
            if (paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)] >= 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i * 2 * paramInt2 + j)]) }) + " + 0i\t");
            else
              localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i * 2 * paramInt2 + j)]) }) + " - 0i\t");
          }
          else if (Math.abs(paramArrayOfFloat[(i * 2 * paramInt2 + j)]) < 5.E-05D)
          {
            if (paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)] >= 0.0D)
              localBufferedWriter.write("0 + " + String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
            else
              localBufferedWriter.write("0 - " + String.format("%.4f", new Object[] { Float.valueOf(-paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
          }
          else if (paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)] < 0.0F)
            localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i * 2 * paramInt2 + j)]) }) + " - " + String.format("%.4f", new Object[] { Float.valueOf(-paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
          else
            localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i * 2 * paramInt2 + j)]) }) + " + " + String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i * 2 * paramInt2 + j + 1)]) }) + "i\t");
          j += 2;
        }
        localBufferedWriter.newLine();
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileComplex_2D(int paramInt1, int paramInt2, double[][] paramArrayOfDouble, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int i = 0; i < paramInt1; i++)
      {
        int j = 0;
        while (j < 2 * paramInt2)
        {
          if ((Math.abs(paramArrayOfDouble[i][j]) < 5.E-05D) && (Math.abs(paramArrayOfDouble[i][(j + 1)]) < 5.E-05D))
          {
            if (paramArrayOfDouble[i][(j + 1)] >= 0.0D)
              localBufferedWriter.write("0 + 0i\t");
            else
              localBufferedWriter.write("0 - 0i\t");
          }
          else if (Math.abs(paramArrayOfDouble[i][(j + 1)]) < 5.E-05D)
          {
            if (paramArrayOfDouble[i][(j + 1)] >= 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i][j]) }) + " + 0i\t");
            else
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i][j]) }) + " - 0i\t");
          }
          else if (Math.abs(paramArrayOfDouble[i][j]) < 5.E-05D)
          {
            if (paramArrayOfDouble[i][(j + 1)] >= 0.0D)
              localBufferedWriter.write("0 + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i][(j + 1)]) }) + "i\t");
            else
              localBufferedWriter.write("0 - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[i][(j + 1)]) }) + "i\t");
          }
          else if (paramArrayOfDouble[i][(j + 1)] < 0.0D)
            localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i][j]) }) + " - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[i][(j + 1)]) }) + "i\t");
          else
            localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i][j]) }) + " + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i][(j + 1)]) }) + "i\t");
          j += 2;
        }
        localBufferedWriter.newLine();
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileComplex_3D(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble, String paramString)
  {
    int i = paramInt2 * paramInt3 * 2;
    int j = paramInt3 * 2;
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      int k = 0;
      while (k < 2 * paramInt3)
      {
        localBufferedWriter.newLine();
        localBufferedWriter.write("(:,:," + k / 2 + ")=");
        localBufferedWriter.newLine();
        localBufferedWriter.newLine();
        for (int m = 0; m < paramInt1; m++)
        {
          for (int n = 0; n < paramInt2; n++)
            if (paramArrayOfDouble[(m * i + n * j + k + 1)] == 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + "\t");
            else if (paramArrayOfDouble[(m * i + n * j + k)] == 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k + 1)]) }) + "i\t");
            else if (paramArrayOfDouble[(m * i + n * j + k + 1)] < 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + " - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[(m * i + n * j + k + 1)]) }) + "i\t");
            else
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + " + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k + 1)]) }) + "i\t");
          localBufferedWriter.newLine();
        }
        k += 2;
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileComplex_3D(int paramInt1, int paramInt2, int paramInt3, double[][][] paramArrayOfDouble, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      int i = 0;
      while (i < 2 * paramInt3)
      {
        localBufferedWriter.newLine();
        localBufferedWriter.write("(:,:," + i / 2 + ")=");
        localBufferedWriter.newLine();
        localBufferedWriter.newLine();
        for (int j = 0; j < paramInt1; j++)
        {
          for (int k = 0; k < paramInt2; k++)
            if (paramArrayOfDouble[j][k][(i + 1)] == 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[j][k][i]) }) + "\t");
            else if (paramArrayOfDouble[j][k][i] == 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[j][k][(i + 1)]) }) + "i\t");
            else if (paramArrayOfDouble[j][k][(i + 1)] < 0.0D)
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[j][k][i]) }) + " - " + String.format("%.4f", new Object[] { Double.valueOf(-paramArrayOfDouble[j][k][(i + 1)]) }) + "i\t");
            else
              localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[j][k][i]) }) + " + " + String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[j][k][(i + 1)]) }) + "i\t");
          localBufferedWriter.newLine();
        }
        i += 2;
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileReal_1D(double[] paramArrayOfDouble, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int i = 0; i < paramArrayOfDouble.length; i++)
      {
        localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }));
        localBufferedWriter.newLine();
      }
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileReal_1D(float[] paramArrayOfFloat, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int i = 0; i < paramArrayOfFloat.length; i++)
      {
        localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[i]) }));
        localBufferedWriter.newLine();
      }
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileReal_2D(int paramInt1, int paramInt2, double[] paramArrayOfDouble, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int i = 0; i < paramInt1; i++)
      {
        for (int j = 0; j < paramInt2; j++)
          if (Math.abs(paramArrayOfDouble[(i * paramInt2 + j)]) < 5.E-05D)
            localBufferedWriter.write("0\t");
          else
            localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(i * paramInt2 + j)]) }) + "\t");
        localBufferedWriter.newLine();
      }
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileReal_2D(int paramInt1, int paramInt2, float[] paramArrayOfFloat, String paramString)
  {
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int i = 0; i < paramInt1; i++)
      {
        for (int j = 0; j < paramInt2; j++)
          if (Math.abs(paramArrayOfFloat[(i * paramInt2 + j)]) < 5.E-05D)
            localBufferedWriter.write("0\t");
          else
            localBufferedWriter.write(String.format("%.4f", new Object[] { Float.valueOf(paramArrayOfFloat[(i * paramInt2 + j)]) }) + "\t");
        localBufferedWriter.newLine();
      }
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeToFileReal_3D(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble, String paramString)
  {
    int i = paramInt2 * paramInt3;
    int j = paramInt3;
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString));
      for (int k = 0; k < paramInt3; k++)
      {
        localBufferedWriter.newLine();
        localBufferedWriter.write("(:,:," + k + ")=");
        localBufferedWriter.newLine();
        localBufferedWriter.newLine();
        for (int m = 0; m < paramInt1; m++)
        {
          for (int n = 0; n < paramInt2; n++)
            localBufferedWriter.write(String.format("%.4f", new Object[] { Double.valueOf(paramArrayOfDouble[(m * i + n * j + k)]) }) + "\t");
          localBufferedWriter.newLine();
        }
        localBufferedWriter.newLine();
      }
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public static void writeFFTBenchmarkResultsToFile(String paramString, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt, double[] paramArrayOfDouble)
  {
    String[] arrayOfString = { "os.name", "os.version", "os.arch", "java.vendor", "java.version" };
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramString, false));
      localBufferedWriter.write(new Date().toString());
      localBufferedWriter.newLine();
      localBufferedWriter.write("System properties:");
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tos.name = " + System.getProperty(arrayOfString[0]));
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tos.version = " + System.getProperty(arrayOfString[1]));
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tos.arch = " + System.getProperty(arrayOfString[2]));
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tjava.vendor = " + System.getProperty(arrayOfString[3]));
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tjava.version = " + System.getProperty(arrayOfString[4]));
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tavailable processors = " + Runtime.getRuntime().availableProcessors());
      localBufferedWriter.newLine();
      localBufferedWriter.write("Settings:");
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tused processors = " + paramInt1);
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tTHREADS_BEGIN_N_2D = " + ConcurrencyUtils.getThreadsBeginN_2D());
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tTHREADS_BEGIN_N_3D = " + ConcurrencyUtils.getThreadsBeginN_3D());
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tnumber of iterations = " + paramInt2);
      localBufferedWriter.newLine();
      localBufferedWriter.write("\twarm-up performed = " + paramBoolean1);
      localBufferedWriter.newLine();
      localBufferedWriter.write("\tscaling performed = " + paramBoolean2);
      localBufferedWriter.newLine();
      localBufferedWriter.write("--------------------------------------------------------------------------------------------------");
      localBufferedWriter.newLine();
      localBufferedWriter.write("sizes=[");
      for (int i = 0; i < paramArrayOfInt.length; i++)
      {
        localBufferedWriter.write(Integer.toString(paramArrayOfInt[i]));
        if (i < paramArrayOfInt.length - 1)
          localBufferedWriter.write(", ");
        else
          localBufferedWriter.write("]");
      }
      localBufferedWriter.newLine();
      localBufferedWriter.write("times(in msec)=[");
      for (i = 0; i < paramArrayOfDouble.length; i++)
      {
        localBufferedWriter.write(String.format("%.2f", new Object[] { Double.valueOf(paramArrayOfDouble[i]) }));
        if (i < paramArrayOfDouble.length - 1)
          localBufferedWriter.write(", ");
        else
          localBufferedWriter.write("]");
      }
      localBufferedWriter.newLine();
      localBufferedWriter.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.utils.IOUtils
 * JD-Core Version:    0.6.1
 */