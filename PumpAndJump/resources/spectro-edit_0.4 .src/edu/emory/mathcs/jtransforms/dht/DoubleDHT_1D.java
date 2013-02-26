package edu.emory.mathcs.jtransforms.dht;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DoubleDHT_1D
{
  private int n;
  private DoubleFFT_1D fft;

  public DoubleDHT_1D(int paramInt)
  {
    this.n = paramInt;
    this.fft = new DoubleFFT_1D(paramInt);
  }

  public DoubleDHT_1D(int paramInt, int[] paramArrayOfInt, double[] paramArrayOfDouble)
  {
    this.n = paramInt;
    this.fft = new DoubleFFT_1D(paramInt, paramArrayOfInt, paramArrayOfDouble);
  }

  public void forward(double[] paramArrayOfDouble)
  {
    forward(paramArrayOfDouble, 0);
  }

  public void forward(final double[] paramArrayOfDouble, final int paramInt)
  {
    if (this.n == 1)
      return;
    this.fft.realForward(paramArrayOfDouble, paramInt);
    final double[] arrayOfDouble = new double[this.n];
    System.arraycopy(paramArrayOfDouble, paramInt, arrayOfDouble, 0, this.n);
    int i = this.n / 2;
    int j = ConcurrencyUtils.getNumberOfProcessors();
    int k;
    if ((j > 1) && (i > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      k = i / j;
      Future[] arrayOfFuture = new Future[j];
      for (int i1 = 0; i1 < j; i1++)
      {
        final int i3 = 1 + i1 * k;
        final int i4;
        if (i1 == j - 1)
          i4 = i;
        else
          i4 = i3 + k;
        arrayOfFuture[i1] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            for (int k = i3; k < i4; k++)
            {
              int i = 2 * k;
              int j = i + 1;
              paramArrayOfDouble[(paramInt + k)] = (arrayOfDouble[i] - arrayOfDouble[j]);
              paramArrayOfDouble[(paramInt + DoubleDHT_1D.this.n - k)] = (arrayOfDouble[i] + arrayOfDouble[j]);
            }
          }
        });
      }
      try
      {
        for (i1 = 0; i1 < j; i1++)
          arrayOfFuture[i1].get();
      }
      catch (ExecutionException localExecutionException)
      {
        localExecutionException.printStackTrace();
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
    }
    else
    {
      for (int i2 = 1; i2 < i; i2++)
      {
        k = 2 * i2;
        int m = k + 1;
        paramArrayOfDouble[(paramInt + i2)] = (arrayOfDouble[k] - arrayOfDouble[m]);
        paramArrayOfDouble[(paramInt + this.n - i2)] = (arrayOfDouble[k] + arrayOfDouble[m]);
      }
    }
    paramArrayOfDouble[(paramInt + i)] = arrayOfDouble[1];
  }

  public void inverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    inverse(paramArrayOfDouble, 0, paramBoolean);
  }

  public void inverse(double[] paramArrayOfDouble, int paramInt, boolean paramBoolean)
  {
    if (this.n == 1)
      return;
    forward(paramArrayOfDouble, paramInt);
    if (paramBoolean)
      scale(this.n, paramArrayOfDouble, paramInt, false);
  }

  private void scale(final double paramDouble, final double[] paramArrayOfDouble, int paramInt, boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
      i = 2 * this.n;
    else
      i = this.n;
    int j = ConcurrencyUtils.getNumberOfProcessors();
    final int k;
    if ((j > 1) && (i >= ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      k = i / j;
      Future[] arrayOfFuture = new Future[j];
      for (int m = 0; m < j; m++)
      {
        final int i1 = paramInt + m * k;
        arrayOfFuture[m] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            for (int i = i1; i < i1 + k; i += 2)
            {
              paramArrayOfDouble[i] /= paramDouble;
              paramArrayOfDouble[(i + 1)] /= paramDouble;
            }
          }
        });
      }
      try
      {
        for (m = 0; m < j; m++)
          arrayOfFuture[m].get();
      }
      catch (ExecutionException localExecutionException)
      {
        localExecutionException.printStackTrace();
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
    }
    else
    {
      for (k = paramInt; k < paramInt + i; k += 2)
      {
        paramArrayOfDouble[k] /= paramDouble;
        paramArrayOfDouble[(k + 1)] /= paramDouble;
      }
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dht.DoubleDHT_1D
 * JD-Core Version:    0.6.1
 */