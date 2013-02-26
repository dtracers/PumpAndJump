package edu.emory.mathcs.jtransforms.dht;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatDHT_1D
{
  private int n;
  private FloatFFT_1D fft;

  public FloatDHT_1D(int paramInt)
  {
    this.n = paramInt;
    this.fft = new FloatFFT_1D(paramInt);
  }

  public FloatDHT_1D(int paramInt, int[] paramArrayOfInt, float[] paramArrayOfFloat)
  {
    this.n = paramInt;
    this.fft = new FloatFFT_1D(paramInt, paramArrayOfInt, paramArrayOfFloat);
  }

  public void forward(float[] paramArrayOfFloat)
  {
    forward(paramArrayOfFloat, 0);
  }

  public void forward(final float[] paramArrayOfFloat, final int paramInt)
  {
    if (this.n == 1)
      return;
    this.fft.realForward(paramArrayOfFloat, paramInt);
    final float[] arrayOfFloat = new float[this.n];
    System.arraycopy(paramArrayOfFloat, paramInt, arrayOfFloat, 0, this.n);
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
              paramArrayOfFloat[(paramInt + k)] = (arrayOfFloat[i] - arrayOfFloat[j]);
              paramArrayOfFloat[(paramInt + FloatDHT_1D.this.n - k)] = (arrayOfFloat[i] + arrayOfFloat[j]);
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
        paramArrayOfFloat[(paramInt + i2)] = (arrayOfFloat[k] - arrayOfFloat[m]);
        paramArrayOfFloat[(paramInt + this.n - i2)] = (arrayOfFloat[k] + arrayOfFloat[m]);
      }
    }
    paramArrayOfFloat[(paramInt + i)] = arrayOfFloat[1];
  }

  public void inverse(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    inverse(paramArrayOfFloat, 0, paramBoolean);
  }

  public void inverse(float[] paramArrayOfFloat, int paramInt, boolean paramBoolean)
  {
    if (this.n == 1)
      return;
    forward(paramArrayOfFloat, paramInt);
    if (paramBoolean)
      scale(this.n, paramArrayOfFloat, paramInt, false);
  }

  private void scale(final float paramFloat, final float[] paramArrayOfFloat, int paramInt, boolean paramBoolean)
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
              paramArrayOfFloat[i] /= paramFloat;
              paramArrayOfFloat[(i + 1)] /= paramFloat;
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
        paramArrayOfFloat[k] /= paramFloat;
        paramArrayOfFloat[(k + 1)] /= paramFloat;
      }
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dht.FloatDHT_1D
 * JD-Core Version:    0.6.1
 */