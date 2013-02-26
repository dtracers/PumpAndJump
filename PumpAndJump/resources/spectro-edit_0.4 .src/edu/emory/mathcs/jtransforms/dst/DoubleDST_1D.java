package edu.emory.mathcs.jtransforms.dst;

import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DoubleDST_1D
{
  private int n;
  private DoubleDCT_1D dct;

  public DoubleDST_1D(int paramInt)
  {
    this.n = paramInt;
    this.dct = new DoubleDCT_1D(paramInt);
  }

  public DoubleDST_1D(int paramInt, int[] paramArrayOfInt, double[] paramArrayOfDouble)
  {
    this.n = paramInt;
    this.dct = new DoubleDCT_1D(paramInt, paramArrayOfInt, paramArrayOfDouble);
  }

  public void forward(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    forward(paramArrayOfDouble, 0, paramBoolean);
  }

  public void forward(final double[] paramArrayOfDouble, final int paramInt, boolean paramBoolean)
  {
    if (this.n == 1)
      return;
    int i = ConcurrencyUtils.getNumberOfProcessors();
    int j;
    final int i3;
    final int i4;
    int i2;
    if ((i > 1) && (this.n > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      j = this.n / i;
      Future[] arrayOfFuture1 = new Future[i];
      for (int i1 = 0; i1 < i; i1++)
      {
        i3 = paramInt + i1 * j + 1;
        if (i1 == i - 1)
          i4 = this.n;
        else
          i4 = i3 + j;
        arrayOfFuture1[i1] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            int i;
            if (DoubleDST_1D.this.n >= 4)
              for (i = i3; i < i4; i += 4)
              {
                paramArrayOfDouble[i] = (-paramArrayOfDouble[i]);
                paramArrayOfDouble[(i + 2)] = (-paramArrayOfDouble[(i + 2)]);
              }
            else
              for (i = i3; i < i4; i += 2)
                paramArrayOfDouble[i] = (-paramArrayOfDouble[i]);
          }
        });
      }
      try
      {
        for (i1 = 0; i1 < i; i1++)
          arrayOfFuture1[i1].get();
      }
      catch (ExecutionException localExecutionException1)
      {
        localExecutionException1.printStackTrace();
      }
      catch (InterruptedException localInterruptedException1)
      {
        localInterruptedException1.printStackTrace();
      }
    }
    else
    {
      j = 1 + paramInt;
      int k = paramInt + this.n;
      if (this.n >= 4)
        for (i2 = j; i2 < k; i2 += 4)
        {
          paramArrayOfDouble[i2] = (-paramArrayOfDouble[i2]);
          paramArrayOfDouble[(i2 + 2)] = (-paramArrayOfDouble[(i2 + 2)]);
        }
      else
        for (i2 = j; i2 < k; i2 += 2)
          paramArrayOfDouble[i2] = (-paramArrayOfDouble[i2]);
    }
    this.dct.forward(paramArrayOfDouble, paramInt, paramBoolean);
    if ((i > 1) && (this.n > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      j = this.n / 2 / i;
      Future[] arrayOfFuture2 = new Future[i];
      for (i2 = 0; i2 < i; i2++)
      {
        i3 = paramInt + i2 * j;
        if (i2 == i - 1)
          i4 = this.n / 2;
        else
          i4 = i3 + j;
        arrayOfFuture2[i2] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            int i = paramInt + DoubleDST_1D.this.n - 1;
            int k;
            double d;
            int j;
            if (DoubleDST_1D.this.n / 2 >= 4)
              for (k = i3; k < i4; k += 4)
              {
                d = paramArrayOfDouble[k];
                j = i - k;
                paramArrayOfDouble[k] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
                d = paramArrayOfDouble[(k + 1)];
                j = i - k - 1;
                paramArrayOfDouble[(k + 1)] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
                d = paramArrayOfDouble[(k + 2)];
                j = i - k - 2;
                paramArrayOfDouble[(k + 2)] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
                d = paramArrayOfDouble[(k + 3)];
                j = i - k - 3;
                paramArrayOfDouble[(k + 3)] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
              }
            else
              for (k = i3; k < i4; k++)
              {
                d = paramArrayOfDouble[k];
                j = i - k;
                paramArrayOfDouble[k] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
              }
          }
        });
      }
      try
      {
        for (i2 = 0; i2 < i; i2++)
          arrayOfFuture2[i2].get();
      }
      catch (ExecutionException localExecutionException2)
      {
        localExecutionException2.printStackTrace();
      }
      catch (InterruptedException localInterruptedException2)
      {
        localInterruptedException2.printStackTrace();
      }
    }
    else
    {
      j = paramInt + this.n - 1;
      int m;
      double d;
      if (this.n / 2 >= 4)
        for (m = 0; m < this.n / 2; m += 4)
        {
          d = paramArrayOfDouble[(paramInt + m)];
          paramArrayOfDouble[(paramInt + m)] = paramArrayOfDouble[(j - m)];
          paramArrayOfDouble[(j - m)] = d;
          d = paramArrayOfDouble[(paramInt + m + 1)];
          paramArrayOfDouble[(paramInt + m + 1)] = paramArrayOfDouble[(j - m - 1)];
          paramArrayOfDouble[(j - m - 1)] = d;
          d = paramArrayOfDouble[(paramInt + m + 2)];
          paramArrayOfDouble[(paramInt + m + 2)] = paramArrayOfDouble[(j - m - 2)];
          paramArrayOfDouble[(j - m - 2)] = d;
          d = paramArrayOfDouble[(paramInt + m + 3)];
          paramArrayOfDouble[(paramInt + m + 3)] = paramArrayOfDouble[(j - m - 3)];
          paramArrayOfDouble[(j - m - 3)] = d;
        }
      else
        for (m = 0; m < this.n / 2; m++)
        {
          d = paramArrayOfDouble[(paramInt + m)];
          paramArrayOfDouble[(paramInt + m)] = paramArrayOfDouble[(j - m)];
          paramArrayOfDouble[(j - m)] = d;
        }
    }
  }

  public void inverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    inverse(paramArrayOfDouble, 0, paramBoolean);
  }

  public void inverse(final double[] paramArrayOfDouble, final int paramInt, boolean paramBoolean)
  {
    if (this.n == 1)
      return;
    int i = ConcurrencyUtils.getNumberOfProcessors();
    int j;
    final int i4;
    final int i5;
    if ((i > 1) && (this.n > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      j = this.n / 2 / i;
      Future[] arrayOfFuture1 = new Future[i];
      for (int i1 = 0; i1 < i; i1++)
      {
        i4 = paramInt + i1 * j;
        if (i1 == i - 1)
          i5 = this.n / 2;
        else
          i5 = i4 + j;
        arrayOfFuture1[i1] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            int i = paramInt + DoubleDST_1D.this.n - 1;
            int k;
            double d;
            int j;
            if (DoubleDST_1D.this.n / 2 >= 4)
              for (k = i4; k < i5; k += 4)
              {
                d = paramArrayOfDouble[k];
                j = i - k;
                paramArrayOfDouble[k] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
                d = paramArrayOfDouble[(k + 1)];
                j = i - k - 1;
                paramArrayOfDouble[(k + 1)] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
                d = paramArrayOfDouble[(k + 2)];
                j = i - k - 2;
                paramArrayOfDouble[(k + 2)] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
                d = paramArrayOfDouble[(k + 3)];
                j = i - k - 3;
                paramArrayOfDouble[(k + 3)] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
              }
            else
              for (k = i4; k < i5; k++)
              {
                d = paramArrayOfDouble[k];
                j = i - k;
                paramArrayOfDouble[k] = paramArrayOfDouble[j];
                paramArrayOfDouble[j] = d;
              }
          }
        });
      }
      try
      {
        for (i1 = 0; i1 < i; i1++)
          arrayOfFuture1[i1].get();
      }
      catch (ExecutionException localExecutionException1)
      {
        localExecutionException1.printStackTrace();
      }
      catch (InterruptedException localInterruptedException1)
      {
        localInterruptedException1.printStackTrace();
      }
    }
    else
    {
      j = paramInt + this.n - 1;
      int k;
      double d;
      if (this.n / 2 >= 4)
        for (k = 0; k < this.n / 2; k += 4)
        {
          d = paramArrayOfDouble[(paramInt + k)];
          paramArrayOfDouble[(paramInt + k)] = paramArrayOfDouble[(j - k)];
          paramArrayOfDouble[(j - k)] = d;
          d = paramArrayOfDouble[(paramInt + k + 1)];
          paramArrayOfDouble[(paramInt + k + 1)] = paramArrayOfDouble[(j - k - 1)];
          paramArrayOfDouble[(j - k - 1)] = d;
          d = paramArrayOfDouble[(paramInt + k + 2)];
          paramArrayOfDouble[(paramInt + k + 2)] = paramArrayOfDouble[(j - k - 2)];
          paramArrayOfDouble[(j - k - 2)] = d;
          d = paramArrayOfDouble[(paramInt + k + 3)];
          paramArrayOfDouble[(paramInt + k + 3)] = paramArrayOfDouble[(j - k - 3)];
          paramArrayOfDouble[(j - k - 3)] = d;
        }
      else
        for (k = 0; k < this.n / 2; k++)
        {
          d = paramArrayOfDouble[(paramInt + k)];
          paramArrayOfDouble[(paramInt + k)] = paramArrayOfDouble[(j - k)];
          paramArrayOfDouble[(j - k)] = d;
        }
    }
    this.dct.inverse(paramArrayOfDouble, paramInt, paramBoolean);
    if ((i > 1) && (this.n > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      j = this.n / i;
      Future[] arrayOfFuture2 = new Future[i];
      for (int i2 = 0; i2 < i; i2++)
      {
        i4 = paramInt + i2 * j + 1;
        if (i2 == i - 1)
          i5 = this.n;
        else
          i5 = i4 + j;
        arrayOfFuture2[i2] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            int i;
            if (DoubleDST_1D.this.n >= 4)
              for (i = i4; i < i5; i += 4)
              {
                paramArrayOfDouble[i] = (-paramArrayOfDouble[i]);
                paramArrayOfDouble[(i + 2)] = (-paramArrayOfDouble[(i + 2)]);
              }
            else
              for (i = i4; i < i5; i += 2)
                paramArrayOfDouble[i] = (-paramArrayOfDouble[i]);
          }
        });
      }
      try
      {
        for (i2 = 0; i2 < i; i2++)
          arrayOfFuture2[i2].get();
      }
      catch (ExecutionException localExecutionException2)
      {
        localExecutionException2.printStackTrace();
      }
      catch (InterruptedException localInterruptedException2)
      {
        localInterruptedException2.printStackTrace();
      }
    }
    else
    {
      j = 1 + paramInt;
      int m = paramInt + this.n;
      int i3;
      if (this.n >= 4)
        for (i3 = j; i3 < m; i3 += 4)
        {
          paramArrayOfDouble[i3] = (-paramArrayOfDouble[i3]);
          paramArrayOfDouble[(i3 + 2)] = (-paramArrayOfDouble[(i3 + 2)]);
        }
      else
        for (i3 = j; i3 < m; i3 += 2)
          paramArrayOfDouble[i3] = (-paramArrayOfDouble[i3]);
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dst.DoubleDST_1D
 * JD-Core Version:    0.6.1
 */