package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class BenchmarkDoubleFFT
{
  private static int nthread = 2;
  private static int nsize = 6;
  private static int niter = 200;
  private static boolean doWarmup = true;
  private static int initialExponent1D = 17;
  private static int initialExponent2D = 7;
  private static int initialExponent3D = 2;
  private static boolean doScaling = false;

  public static void parseArguments(String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
      System.out.println("args[" + i + "]:" + paramArrayOfString[i]);
    if ((paramArrayOfString == null) || (paramArrayOfString.length != 10))
    {
      System.out.println("Parameters: <number of threads> <THREADS_BEGIN_N_2D> <THREADS_BEGIN_N_3D> <number of iterations> <perform warm-up> <perform scaling> <number of sizes> <initial exponent for 1D transforms> <initial exponent for 2D transforms> <initial exponent for 3D transforms>");
      System.exit(-1);
    }
    nthread = Integer.parseInt(paramArrayOfString[0]);
    ConcurrencyUtils.setThreadsBeginN_2D(Integer.parseInt(paramArrayOfString[1]));
    ConcurrencyUtils.setThreadsBeginN_3D(Integer.parseInt(paramArrayOfString[2]));
    niter = Integer.parseInt(paramArrayOfString[3]);
    doWarmup = Boolean.parseBoolean(paramArrayOfString[4]);
    doScaling = Boolean.parseBoolean(paramArrayOfString[5]);
    nsize = Integer.parseInt(paramArrayOfString[6]);
    initialExponent1D = Integer.parseInt(paramArrayOfString[7]);
    initialExponent2D = Integer.parseInt(paramArrayOfString[8]);
    initialExponent3D = Integer.parseInt(paramArrayOfString[9]);
    ConcurrencyUtils.setNumberOfProcessors(nthread);
  }

  public static void benchmarkComplexForward_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble1 = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Complex forward FFT 1D of size 2^" + j);
      DoubleFFT_1D localDoubleFFT_1D = new DoubleFFT_1D(k);
      double[] arrayOfDouble2 = new double[2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_1D(2 * k, arrayOfDouble2);
        localDoubleFFT_1D.complexForward(arrayOfDouble2);
        IOUtils.fillMatrix_1D(2 * k, arrayOfDouble2);
        localDoubleFFT_1D.complexForward(arrayOfDouble2);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_1D(2 * k, arrayOfDouble2);
        l = System.nanoTime();
        localDoubleFFT_1D.complexForward(arrayOfDouble2);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble1[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble2 = null;
      localDoubleFFT_1D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleComplexForwardFFT_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble1);
  }

  public static void benchmarkRealForward_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble1 = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Real forward FFT 1D of size 2^" + j);
      DoubleFFT_1D localDoubleFFT_1D = new DoubleFFT_1D(k);
      double[] arrayOfDouble2 = new double[2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_1D(k, arrayOfDouble2);
        localDoubleFFT_1D.realForwardFull(arrayOfDouble2);
        IOUtils.fillMatrix_1D(k, arrayOfDouble2);
        localDoubleFFT_1D.realForwardFull(arrayOfDouble2);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_1D(k, arrayOfDouble2);
        l = System.nanoTime();
        localDoubleFFT_1D.realForwardFull(arrayOfDouble2);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble1[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble2 = null;
      localDoubleFFT_1D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleRealForwardFFT_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble1);
  }

  public static void benchmarkComplexForward_2D_input_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble1 = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Complex forward FFT 2D (input 1D) of size 2^" + j + " x 2^" + j);
      DoubleFFT_2D localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      double[] arrayOfDouble2 = new double[k * 2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_2D(k, 2 * k, arrayOfDouble2);
        localDoubleFFT_2D.complexForward(arrayOfDouble2);
        IOUtils.fillMatrix_2D(k, 2 * k, arrayOfDouble2);
        localDoubleFFT_2D.complexForward(arrayOfDouble2);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_2D(k, 2 * k, arrayOfDouble2);
        l = System.nanoTime();
        localDoubleFFT_2D.complexForward(arrayOfDouble2);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble1[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble2 = null;
      localDoubleFFT_2D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleComplexForwardFFT_2D_input_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble1);
  }

  public static void benchmarkComplexForward_2D_input_2D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Complex forward FFT 2D (input 2D) of size 2^" + j + " x 2^" + j);
      DoubleFFT_2D localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      double[][] arrayOfDouble1 = new double[k][2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_2D(k, 2 * k, arrayOfDouble1);
        localDoubleFFT_2D.complexForward(arrayOfDouble1);
        IOUtils.fillMatrix_2D(k, 2 * k, arrayOfDouble1);
        localDoubleFFT_2D.complexForward(arrayOfDouble1);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_2D(k, 2 * k, arrayOfDouble1);
        l = System.nanoTime();
        localDoubleFFT_2D.complexForward(arrayOfDouble1);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble1 = (double[][])null;
      localDoubleFFT_2D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleComplexForwardFFT_2D_input_2D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void benchmarkRealForward_2D_input_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble1 = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Real forward FFT 2D (input 1D) of size 2^" + j + " x 2^" + j);
      DoubleFFT_2D localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      double[] arrayOfDouble2 = new double[k * 2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfDouble2);
        localDoubleFFT_2D.realForwardFull(arrayOfDouble2);
        IOUtils.fillMatrix_2D(k, k, arrayOfDouble2);
        localDoubleFFT_2D.realForwardFull(arrayOfDouble2);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfDouble2);
        l = System.nanoTime();
        localDoubleFFT_2D.realForwardFull(arrayOfDouble2);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble1[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble2 = null;
      localDoubleFFT_2D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleRealForwardFFT_2D_input_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble1);
  }

  public static void benchmarkRealForward_2D_input_2D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Real forward FFT 2D (input 2D) of size 2^" + j + " x 2^" + j);
      DoubleFFT_2D localDoubleFFT_2D = new DoubleFFT_2D(k, k);
      double[][] arrayOfDouble1 = new double[k][2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfDouble1);
        localDoubleFFT_2D.realForwardFull(arrayOfDouble1);
        IOUtils.fillMatrix_2D(k, k, arrayOfDouble1);
        localDoubleFFT_2D.realForwardFull(arrayOfDouble1);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfDouble1);
        l = System.nanoTime();
        localDoubleFFT_2D.realForwardFull(arrayOfDouble1);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble1 = (double[][])null;
      localDoubleFFT_2D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleRealForwardFFT_2D_input_2D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void benchmarkComplexForward_3D_input_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble1 = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Complex forward FFT 3D (input 1D) of size 2^" + j + " x 2^" + j + " x 2^" + j);
      DoubleFFT_3D localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      double[] arrayOfDouble2 = new double[k * k * 2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_3D(k, k, 2 * k, arrayOfDouble2);
        localDoubleFFT_3D.complexForward(arrayOfDouble2);
        IOUtils.fillMatrix_3D(k, k, 2 * k, arrayOfDouble2);
        localDoubleFFT_3D.complexForward(arrayOfDouble2);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_3D(k, k, 2 * k, arrayOfDouble2);
        l = System.nanoTime();
        localDoubleFFT_3D.complexForward(arrayOfDouble2);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble1[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble2 = null;
      localDoubleFFT_3D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleComplexForwardFFT_3D_input_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble1);
  }

  public static void benchmarkComplexForward_3D_input_3D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Complex forward FFT 3D (input 3D) of size 2^" + j + " x 2^" + j + " x 2^" + j);
      DoubleFFT_3D localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      double[][][] arrayOfDouble1 = new double[k][k][2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_3D(k, k, 2 * k, arrayOfDouble1);
        localDoubleFFT_3D.complexForward(arrayOfDouble1);
        IOUtils.fillMatrix_3D(k, k, 2 * k, arrayOfDouble1);
        localDoubleFFT_3D.complexForward(arrayOfDouble1);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_3D(k, k, 2 * k, arrayOfDouble1);
        l = System.nanoTime();
        localDoubleFFT_3D.complexForward(arrayOfDouble1);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble1 = (double[][][])null;
      localDoubleFFT_3D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleComplexForwardFFT_3D_input_3D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void benchmarkRealForward_3D_input_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble1 = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Real forward FFT 3D (input 1D) of size 2^" + j + " x 2^" + j + " x 2^" + j);
      DoubleFFT_3D localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      double[] arrayOfDouble2 = new double[k * k * 2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfDouble2);
        localDoubleFFT_3D.realForwardFull(arrayOfDouble2);
        IOUtils.fillMatrix_3D(k, k, k, arrayOfDouble2);
        localDoubleFFT_3D.realForwardFull(arrayOfDouble2);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfDouble2);
        l = System.nanoTime();
        localDoubleFFT_3D.realForwardFull(arrayOfDouble2);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble1[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble2 = null;
      localDoubleFFT_3D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleRealForwardFFT_3D_input_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble1);
  }

  public static void benchmarkRealForward_3D_input_3D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Real forward FFT 3D (input 3D) of size 2^" + j + " x 2^" + j + " x 2^" + j);
      DoubleFFT_3D localDoubleFFT_3D = new DoubleFFT_3D(k, k, k);
      double[][][] arrayOfDouble1 = new double[k][k][2 * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfDouble1);
        localDoubleFFT_3D.realForwardFull(arrayOfDouble1);
        IOUtils.fillMatrix_3D(k, k, k, arrayOfDouble1);
        localDoubleFFT_3D.realForwardFull(arrayOfDouble1);
      }
      double d = 0.0D;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfDouble1);
        l = System.nanoTime();
        localDoubleFFT_3D.realForwardFull(arrayOfDouble1);
        l = System.nanoTime() - l;
        d += l;
      }
      arrayOfDouble[i] = (d / 1000000.0D / niter);
      System.out.println("\tAverage execution time: " + String.format("%.2f", new Object[] { Double.valueOf(d / 1000000.0D / niter) }) + " msec");
      arrayOfDouble1 = (double[][][])null;
      localDoubleFFT_3D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkDoubleRealForwardFFT_3D_input_3D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void main(String[] paramArrayOfString)
  {
    parseArguments(paramArrayOfString);
    benchmarkComplexForward_1D(initialExponent1D);
    benchmarkRealForward_1D(initialExponent1D);
    benchmarkComplexForward_2D_input_1D(initialExponent2D);
    benchmarkComplexForward_2D_input_2D(initialExponent2D);
    benchmarkRealForward_2D_input_1D(initialExponent2D);
    benchmarkRealForward_2D_input_2D(initialExponent2D);
    benchmarkComplexForward_3D_input_1D(initialExponent3D);
    benchmarkComplexForward_3D_input_3D(initialExponent3D);
    benchmarkRealForward_3D_input_1D(initialExponent3D);
    benchmarkRealForward_3D_input_3D(initialExponent3D);
    System.exit(0);
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.BenchmarkDoubleFFT
 * JD-Core Version:    0.6.1
 */