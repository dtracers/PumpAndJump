package edu.emory.mathcs.jtransforms.dst;

import edu.emory.mathcs.jtransforms.dct.FloatDCT_1D;
import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatDST_1D
{
  private int n;
  private FloatDCT_1D dct;

  public FloatDST_1D(int paramInt)
  {
    this.n = paramInt;
    this.dct = new FloatDCT_1D(paramInt);
  }

  public FloatDST_1D(int paramInt, int[] paramArrayOfInt, float[] paramArrayOfFloat)
  {
    this.n = paramInt;
    this.dct = new FloatDCT_1D(paramInt, paramArrayOfInt, paramArrayOfFloat);
  }

  public void forward(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    forward(paramArrayOfFloat, 0, paramBoolean);
  }

  public void forward(final float[] paramArrayOfFloat, final int paramInt, boolean paramBoolean)
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
            if (FloatDST_1D.this.n >= 4)
              for (i = i3; i < i4; i += 4)
              {
                paramArrayOfFloat[i] = (-paramArrayOfFloat[i]);
                paramArrayOfFloat[(i + 2)] = (-paramArrayOfFloat[(i + 2)]);
              }
            else
              for (i = i3; i < i4; i += 2)
                paramArrayOfFloat[i] = (-paramArrayOfFloat[i]);
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
          paramArrayOfFloat[i2] = (-paramArrayOfFloat[i2]);
          paramArrayOfFloat[(i2 + 2)] = (-paramArrayOfFloat[(i2 + 2)]);
        }
      else
        for (i2 = j; i2 < k; i2 += 2)
          paramArrayOfFloat[i2] = (-paramArrayOfFloat[i2]);
    }
    this.dct.forward(paramArrayOfFloat, paramInt, paramBoolean);
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
            int i = paramInt + FloatDST_1D.this.n - 1;
            int k;
            float f;
            int j;
            if (FloatDST_1D.this.n / 2 >= 4)
              for (k = i3; k < i4; k += 4)
              {
                f = paramArrayOfFloat[k];
                j = i - k;
                paramArrayOfFloat[k] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
                f = paramArrayOfFloat[(k + 1)];
                j = i - k - 1;
                paramArrayOfFloat[(k + 1)] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
                f = paramArrayOfFloat[(k + 2)];
                j = i - k - 2;
                paramArrayOfFloat[(k + 2)] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
                f = paramArrayOfFloat[(k + 3)];
                j = i - k - 3;
                paramArrayOfFloat[(k + 3)] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
              }
            else
              for (k = i3; k < i4; k++)
              {
                f = paramArrayOfFloat[k];
                j = i - k;
                paramArrayOfFloat[k] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
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
      float f;
      if (this.n / 2 >= 4)
        for (m = 0; m < this.n / 2; m += 4)
        {
          f = paramArrayOfFloat[(paramInt + m)];
          paramArrayOfFloat[(paramInt + m)] = paramArrayOfFloat[(j - m)];
          paramArrayOfFloat[(j - m)] = f;
          f = paramArrayOfFloat[(paramInt + m + 1)];
          paramArrayOfFloat[(paramInt + m + 1)] = paramArrayOfFloat[(j - m - 1)];
          paramArrayOfFloat[(j - m - 1)] = f;
          f = paramArrayOfFloat[(paramInt + m + 2)];
          paramArrayOfFloat[(paramInt + m + 2)] = paramArrayOfFloat[(j - m - 2)];
          paramArrayOfFloat[(j - m - 2)] = f;
          f = paramArrayOfFloat[(paramInt + m + 3)];
          paramArrayOfFloat[(paramInt + m + 3)] = paramArrayOfFloat[(j - m - 3)];
          paramArrayOfFloat[(j - m - 3)] = f;
        }
      else
        for (m = 0; m < this.n / 2; m++)
        {
          f = paramArrayOfFloat[(paramInt + m)];
          paramArrayOfFloat[(paramInt + m)] = paramArrayOfFloat[(j - m)];
          paramArrayOfFloat[(j - m)] = f;
        }
    }
  }

  public void inverse(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    inverse(paramArrayOfFloat, 0, paramBoolean);
  }

  public void inverse(final float[] paramArrayOfFloat, final int paramInt, boolean paramBoolean)
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
            int i = paramInt + FloatDST_1D.this.n - 1;
            int k;
            float f;
            int j;
            if (FloatDST_1D.this.n / 2 >= 4)
              for (k = i4; k < i5; k += 4)
              {
                f = paramArrayOfFloat[k];
                j = i - k;
                paramArrayOfFloat[k] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
                f = paramArrayOfFloat[(k + 1)];
                j = i - k - 1;
                paramArrayOfFloat[(k + 1)] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
                f = paramArrayOfFloat[(k + 2)];
                j = i - k - 2;
                paramArrayOfFloat[(k + 2)] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
                f = paramArrayOfFloat[(k + 3)];
                j = i - k - 3;
                paramArrayOfFloat[(k + 3)] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
              }
            else
              for (k = i4; k < i5; k++)
              {
                f = paramArrayOfFloat[k];
                j = i - k;
                paramArrayOfFloat[k] = paramArrayOfFloat[j];
                paramArrayOfFloat[j] = f;
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
      float f;
      if (this.n / 2 >= 4)
        for (k = 0; k < this.n / 2; k += 4)
        {
          f = paramArrayOfFloat[(paramInt + k)];
          paramArrayOfFloat[(paramInt + k)] = paramArrayOfFloat[(j - k)];
          paramArrayOfFloat[(j - k)] = f;
          f = paramArrayOfFloat[(paramInt + k + 1)];
          paramArrayOfFloat[(paramInt + k + 1)] = paramArrayOfFloat[(j - k - 1)];
          paramArrayOfFloat[(j - k - 1)] = f;
          f = paramArrayOfFloat[(paramInt + k + 2)];
          paramArrayOfFloat[(paramInt + k + 2)] = paramArrayOfFloat[(j - k - 2)];
          paramArrayOfFloat[(j - k - 2)] = f;
          f = paramArrayOfFloat[(paramInt + k + 3)];
          paramArrayOfFloat[(paramInt + k + 3)] = paramArrayOfFloat[(j - k - 3)];
          paramArrayOfFloat[(j - k - 3)] = f;
        }
      else
        for (k = 0; k < this.n / 2; k++)
        {
          f = paramArrayOfFloat[(paramInt + k)];
          paramArrayOfFloat[(paramInt + k)] = paramArrayOfFloat[(j - k)];
          paramArrayOfFloat[(j - k)] = f;
        }
    }
    this.dct.inverse(paramArrayOfFloat, paramInt, paramBoolean);
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
            if (FloatDST_1D.this.n >= 4)
              for (i = i4; i < i5; i += 4)
              {
                paramArrayOfFloat[i] = (-paramArrayOfFloat[i]);
                paramArrayOfFloat[(i + 2)] = (-paramArrayOfFloat[(i + 2)]);
              }
            else
              for (i = i4; i < i5; i += 2)
                paramArrayOfFloat[i] = (-paramArrayOfFloat[i]);
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
          paramArrayOfFloat[i3] = (-paramArrayOfFloat[i3]);
          paramArrayOfFloat[(i3 + 2)] = (-paramArrayOfFloat[(i3 + 2)]);
        }
      else
        for (i3 = j; i3 < m; i3 += 2)
          paramArrayOfFloat[i3] = (-paramArrayOfFloat[i3]);
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dst.FloatDST_1D
 * JD-Core Version:    0.6.1
 */