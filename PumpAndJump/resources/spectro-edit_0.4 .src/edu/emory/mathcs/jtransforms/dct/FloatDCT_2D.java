package edu.emory.mathcs.jtransforms.dct;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatDCT_2D
{
  private int n1;
  private int n2;
  private int[] ip;
  private float[] w;
  private float[] t;
  private FloatDCT_1D dctn2;
  private FloatDCT_1D dctn1;
  private int oldNthread;
  private int nt;

  public FloatDCT_2D(int paramInt1, int paramInt2)
  {
    if ((!ConcurrencyUtils.isPowerOf2(paramInt1)) || (!ConcurrencyUtils.isPowerOf2(paramInt2)))
      throw new IllegalArgumentException("n1, n2 must be power of two numbers");
    if ((paramInt1 <= 1) || (paramInt2 <= 1))
      throw new IllegalArgumentException("n1, n2 must be greater than 1");
    this.n1 = paramInt1;
    this.n2 = paramInt2;
    this.ip = new int[2 + (int)Math.ceil(Math.sqrt(Math.max(paramInt1 / 2, paramInt2 / 2)))];
    this.w = new float[(int)Math.ceil(Math.max(paramInt1 * 1.5D, paramInt2 * 1.5D))];
    this.dctn2 = new FloatDCT_1D(paramInt2, this.ip, this.w);
    this.dctn1 = new FloatDCT_1D(paramInt1, this.ip, this.w);
    this.oldNthread = ConcurrencyUtils.getNumberOfProcessors();
    this.nt = (4 * this.oldNthread * paramInt1);
    if (paramInt2 == 2 * this.oldNthread)
      this.nt >>= 1;
    else if (paramInt2 < 2 * this.oldNthread)
      this.nt >>= 2;
    this.t = new float[this.nt];
  }

  public void forward(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    int j = this.n1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (j > m)
    {
      m = j;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (4 * i * this.n1);
      if (this.n2 == 2 * i)
        this.nt >>= 1;
      else if (this.n2 < 2 * i)
        this.nt >>= 2;
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(-1, paramArrayOfFloat, paramBoolean);
      ddxt2d0_subth(-1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt2d_sub(-1, paramArrayOfFloat, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.forward(paramArrayOfFloat, n * this.n2, paramBoolean);
    }
  }

  public void forward(float[][] paramArrayOfFloat, boolean paramBoolean)
  {
    int j = this.n1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (j > m)
    {
      m = j;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (4 * i * this.n1);
      if (this.n2 == 2 * i)
        this.nt >>= 1;
      else if (this.n2 < 2 * i)
        this.nt >>= 2;
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(-1, paramArrayOfFloat, paramBoolean);
      ddxt2d0_subth(-1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt2d_sub(-1, paramArrayOfFloat, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.forward(paramArrayOfFloat[n], paramBoolean);
    }
  }

  public void inverse(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    int j = this.n1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (j > m)
    {
      m = j;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (4 * i * this.n1);
      if (this.n2 == 2 * i)
        this.nt >>= 1;
      else if (this.n2 < 2 * i)
        this.nt >>= 2;
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(1, paramArrayOfFloat, paramBoolean);
      ddxt2d0_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt2d_sub(1, paramArrayOfFloat, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.inverse(paramArrayOfFloat, n * this.n2, paramBoolean);
    }
  }

  public void inverse(float[][] paramArrayOfFloat, boolean paramBoolean)
  {
    int j = this.n1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (j > m)
    {
      m = j;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (4 * i * this.n1);
      if (this.n2 == 2 * i)
        this.nt >>= 1;
      else if (this.n2 < 2 * i)
        this.nt >>= 2;
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(1, paramArrayOfFloat, paramBoolean);
      ddxt2d0_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt2d_sub(1, paramArrayOfFloat, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.inverse(paramArrayOfFloat[n], paramBoolean);
    }
  }

  private void ddxt2d_subth(final int paramInt, final float[] paramArrayOfFloat, final boolean paramBoolean)
  {
    int m = ConcurrencyUtils.getNumberOfProcessors();
    int i = m;
    int j = 4 * this.n1;
    if (this.n2 == 2 * m)
    {
      j >>= 1;
    }
    else if (this.n2 < 2 * m)
    {
      i = this.n2;
      j >>= 2;
    }
    final int n = i;
    Future[] arrayOfFuture = new Future[i];
    final int i1;
    for (int k = 0; k < i; k++)
    {
      i1 = k;
      final int i2 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int i;
          int k;
          int m;
          if (FloatDCT_2D.this.n2 > 2 * n)
          {
            if (paramInt == -1)
            {
              j = 4 * i1;
              while (j < FloatDCT_2D.this.n2)
              {
                for (i = 0; i < FloatDCT_2D.this.n1; i++)
                {
                  k = i * FloatDCT_2D.this.n2 + j;
                  m = i2 + FloatDCT_2D.this.n1 + i;
                  FloatDCT_2D.this.t[(i2 + i)] = paramArrayOfFloat[k];
                  FloatDCT_2D.this.t[m] = paramArrayOfFloat[(k + 1)];
                  FloatDCT_2D.this.t[(m + FloatDCT_2D.this.n1)] = paramArrayOfFloat[(k + 2)];
                  FloatDCT_2D.this.t[(m + 2 * FloatDCT_2D.this.n1)] = paramArrayOfFloat[(k + 3)];
                }
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2, paramBoolean);
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + 2 * FloatDCT_2D.this.n1, paramBoolean);
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + 3 * FloatDCT_2D.this.n1, paramBoolean);
                for (i = 0; i < FloatDCT_2D.this.n1; i++)
                {
                  k = i * FloatDCT_2D.this.n2 + j;
                  m = i2 + FloatDCT_2D.this.n1 + i;
                  paramArrayOfFloat[k] = FloatDCT_2D.this.t[(i2 + i)];
                  paramArrayOfFloat[(k + 1)] = FloatDCT_2D.this.t[m];
                  paramArrayOfFloat[(k + 2)] = FloatDCT_2D.this.t[(m + FloatDCT_2D.this.n1)];
                  paramArrayOfFloat[(k + 3)] = FloatDCT_2D.this.t[(m + 2 * FloatDCT_2D.this.n1)];
                }
                j += 4 * n;
              }
            }
            int j = 4 * i1;
            while (j < FloatDCT_2D.this.n2)
            {
              for (i = 0; i < FloatDCT_2D.this.n1; i++)
              {
                k = i * FloatDCT_2D.this.n2 + j;
                m = i2 + FloatDCT_2D.this.n1 + i;
                FloatDCT_2D.this.t[(i2 + i)] = paramArrayOfFloat[k];
                FloatDCT_2D.this.t[m] = paramArrayOfFloat[(k + 1)];
                FloatDCT_2D.this.t[(m + FloatDCT_2D.this.n1)] = paramArrayOfFloat[(k + 2)];
                FloatDCT_2D.this.t[(m + 2 * FloatDCT_2D.this.n1)] = paramArrayOfFloat[(k + 3)];
              }
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + 2 * FloatDCT_2D.this.n1, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + 3 * FloatDCT_2D.this.n1, paramBoolean);
              for (i = 0; i < FloatDCT_2D.this.n1; i++)
              {
                k = i * FloatDCT_2D.this.n2 + j;
                m = i2 + FloatDCT_2D.this.n1 + i;
                paramArrayOfFloat[k] = FloatDCT_2D.this.t[(i2 + i)];
                paramArrayOfFloat[(k + 1)] = FloatDCT_2D.this.t[m];
                paramArrayOfFloat[(k + 2)] = FloatDCT_2D.this.t[(m + FloatDCT_2D.this.n1)];
                paramArrayOfFloat[(k + 3)] = FloatDCT_2D.this.t[(m + 2 * FloatDCT_2D.this.n1)];
              }
              j += 4 * n;
            }
          }
          if (FloatDCT_2D.this.n2 == 2 * n)
          {
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
            {
              k = i * FloatDCT_2D.this.n2 + 2 * i1;
              m = i2 + i;
              FloatDCT_2D.this.t[m] = paramArrayOfFloat[k];
              FloatDCT_2D.this.t[(m + FloatDCT_2D.this.n1)] = paramArrayOfFloat[(k + 1)];
            }
            if (paramInt == -1)
            {
              FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2, paramBoolean);
              FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
            }
            else
            {
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
            }
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
            {
              k = i * FloatDCT_2D.this.n2 + 2 * i1;
              m = i2 + i;
              paramArrayOfFloat[k] = FloatDCT_2D.this.t[m];
              paramArrayOfFloat[(k + 1)] = FloatDCT_2D.this.t[(m + FloatDCT_2D.this.n1)];
            }
          }
          if (FloatDCT_2D.this.n2 == n)
          {
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
              FloatDCT_2D.this.t[(i2 + i)] = paramArrayOfFloat[(i * FloatDCT_2D.this.n2 + i1)];
            if (paramInt == -1)
              FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2, paramBoolean);
            else
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2, paramBoolean);
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
              paramArrayOfFloat[(i * FloatDCT_2D.this.n2 + i1)] = FloatDCT_2D.this.t[(i2 + i)];
          }
        }
      });
    }
    try
    {
      for (i1 = 0; i1 < i; i1++)
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

  private void ddxt2d_subth(final int paramInt, final float[][] paramArrayOfFloat, final boolean paramBoolean)
  {
    int m = ConcurrencyUtils.getNumberOfProcessors();
    int i = m;
    int j = 4 * this.n1;
    if (this.n2 == 2 * m)
    {
      j >>= 1;
    }
    else if (this.n2 < 2 * m)
    {
      i = this.n2;
      j >>= 2;
    }
    final int n = i;
    Future[] arrayOfFuture = new Future[i];
    final int i1;
    for (int k = 0; k < i; k++)
    {
      i1 = k;
      final int i2 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int i;
          int k;
          if (FloatDCT_2D.this.n2 > 2 * n)
          {
            if (paramInt == -1)
            {
              j = 4 * i1;
              while (j < FloatDCT_2D.this.n2)
              {
                for (i = 0; i < FloatDCT_2D.this.n1; i++)
                {
                  k = i2 + FloatDCT_2D.this.n1 + i;
                  FloatDCT_2D.this.t[(i2 + i)] = paramArrayOfFloat[i][j];
                  FloatDCT_2D.this.t[k] = paramArrayOfFloat[i][(j + 1)];
                  FloatDCT_2D.this.t[(k + FloatDCT_2D.this.n1)] = paramArrayOfFloat[i][(j + 2)];
                  FloatDCT_2D.this.t[(k + 2 * FloatDCT_2D.this.n1)] = paramArrayOfFloat[i][(j + 3)];
                }
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2, paramBoolean);
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + 2 * FloatDCT_2D.this.n1, paramBoolean);
                FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + 3 * FloatDCT_2D.this.n1, paramBoolean);
                for (i = 0; i < FloatDCT_2D.this.n1; i++)
                {
                  k = i2 + FloatDCT_2D.this.n1 + i;
                  paramArrayOfFloat[i][j] = FloatDCT_2D.this.t[(i2 + i)];
                  paramArrayOfFloat[i][(j + 1)] = FloatDCT_2D.this.t[k];
                  paramArrayOfFloat[i][(j + 2)] = FloatDCT_2D.this.t[(k + FloatDCT_2D.this.n1)];
                  paramArrayOfFloat[i][(j + 3)] = FloatDCT_2D.this.t[(k + 2 * FloatDCT_2D.this.n1)];
                }
                j += 4 * n;
              }
            }
            int j = 4 * i1;
            while (j < FloatDCT_2D.this.n2)
            {
              for (i = 0; i < FloatDCT_2D.this.n1; i++)
              {
                k = i2 + FloatDCT_2D.this.n1 + i;
                FloatDCT_2D.this.t[(i2 + i)] = paramArrayOfFloat[i][j];
                FloatDCT_2D.this.t[k] = paramArrayOfFloat[i][(j + 1)];
                FloatDCT_2D.this.t[(k + FloatDCT_2D.this.n1)] = paramArrayOfFloat[i][(j + 2)];
                FloatDCT_2D.this.t[(k + 2 * FloatDCT_2D.this.n1)] = paramArrayOfFloat[i][(j + 3)];
              }
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + 2 * FloatDCT_2D.this.n1, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + 3 * FloatDCT_2D.this.n1, paramBoolean);
              for (i = 0; i < FloatDCT_2D.this.n1; i++)
              {
                k = i2 + FloatDCT_2D.this.n1 + i;
                paramArrayOfFloat[i][j] = FloatDCT_2D.this.t[(i2 + i)];
                paramArrayOfFloat[i][(j + 1)] = FloatDCT_2D.this.t[k];
                paramArrayOfFloat[i][(j + 2)] = FloatDCT_2D.this.t[(k + FloatDCT_2D.this.n1)];
                paramArrayOfFloat[i][(j + 3)] = FloatDCT_2D.this.t[(k + 2 * FloatDCT_2D.this.n1)];
              }
              j += 4 * n;
            }
          }
          if (FloatDCT_2D.this.n2 == 2 * n)
          {
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
            {
              k = i2 + i;
              FloatDCT_2D.this.t[k] = paramArrayOfFloat[i][(2 * i1)];
              FloatDCT_2D.this.t[(k + FloatDCT_2D.this.n1)] = paramArrayOfFloat[i][(2 * i1 + 1)];
            }
            if (paramInt == -1)
            {
              FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2, paramBoolean);
              FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
            }
            else
            {
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2, paramBoolean);
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2 + FloatDCT_2D.this.n1, paramBoolean);
            }
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
            {
              k = i2 + i;
              paramArrayOfFloat[i][(2 * i1)] = FloatDCT_2D.this.t[k];
              paramArrayOfFloat[i][(2 * i1 + 1)] = FloatDCT_2D.this.t[(k + FloatDCT_2D.this.n1)];
            }
          }
          if (FloatDCT_2D.this.n2 == n)
          {
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
              FloatDCT_2D.this.t[(i2 + i)] = paramArrayOfFloat[i][i1];
            if (paramInt == -1)
              FloatDCT_2D.this.dctn1.forward(FloatDCT_2D.this.t, i2, paramBoolean);
            else
              FloatDCT_2D.this.dctn1.inverse(FloatDCT_2D.this.t, i2, paramBoolean);
            for (i = 0; i < FloatDCT_2D.this.n1; i++)
              paramArrayOfFloat[i][i1] = FloatDCT_2D.this.t[(i2 + i)];
          }
        }
      });
    }
    try
    {
      for (i1 = 0; i1 < i; i1++)
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

  private void ddxt2d0_subth(final int paramInt, final float[] paramArrayOfFloat, final boolean paramBoolean)
  {
    int k = ConcurrencyUtils.getNumberOfProcessors();
    final int i;
    if (k > this.n1)
      i = this.n1;
    else
      i = k;
    Future[] arrayOfFuture = new Future[i];
    final int m;
    for (int j = 0; j < i; j++)
    {
      m = j;
      arrayOfFuture[j] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int i;
          if (paramInt == -1)
          {
            i = m;
            while (i < FloatDCT_2D.this.n1)
            {
              FloatDCT_2D.this.dctn2.forward(paramArrayOfFloat, i * FloatDCT_2D.this.n2, paramBoolean);
              i += i;
            }
          }
          else
          {
            i = m;
            while (i < FloatDCT_2D.this.n1)
            {
              FloatDCT_2D.this.dctn2.inverse(paramArrayOfFloat, i * FloatDCT_2D.this.n2, paramBoolean);
              i += i;
            }
          }
        }
      });
    }
    try
    {
      for (m = 0; m < i; m++)
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

  private void ddxt2d0_subth(final int paramInt, final float[][] paramArrayOfFloat, final boolean paramBoolean)
  {
    int k = ConcurrencyUtils.getNumberOfProcessors();
    final int i;
    if (k > this.n1)
      i = this.n1;
    else
      i = k;
    Future[] arrayOfFuture = new Future[i];
    final int m;
    for (int j = 0; j < i; j++)
    {
      m = j;
      arrayOfFuture[j] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int i;
          if (paramInt == -1)
          {
            i = m;
            while (i < FloatDCT_2D.this.n1)
            {
              FloatDCT_2D.this.dctn2.forward(paramArrayOfFloat[i], paramBoolean);
              i += i;
            }
          }
          else
          {
            i = m;
            while (i < FloatDCT_2D.this.n1)
            {
              FloatDCT_2D.this.dctn2.inverse(paramArrayOfFloat[i], paramBoolean);
              i += i;
            }
          }
        }
      });
    }
    try
    {
      for (m = 0; m < i; m++)
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

  private void ddxt2d_sub(int paramInt, float[] paramArrayOfFloat, boolean paramBoolean)
  {
    int i;
    int k;
    if (this.n2 > 2)
    {
      int m;
      if (paramInt == -1)
        for (j = 0; j < this.n2; j += 4)
        {
          for (i = 0; i < this.n1; i++)
          {
            k = i * this.n2 + j;
            m = this.n1 + i;
            this.t[i] = paramArrayOfFloat[k];
            this.t[m] = paramArrayOfFloat[(k + 1)];
            this.t[(m + this.n1)] = paramArrayOfFloat[(k + 2)];
            this.t[(m + 2 * this.n1)] = paramArrayOfFloat[(k + 3)];
          }
          this.dctn1.forward(this.t, 0, paramBoolean);
          this.dctn1.forward(this.t, this.n1, paramBoolean);
          this.dctn1.forward(this.t, 2 * this.n1, paramBoolean);
          this.dctn1.forward(this.t, 3 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            k = i * this.n2 + j;
            m = this.n1 + i;
            paramArrayOfFloat[k] = this.t[i];
            paramArrayOfFloat[(k + 1)] = this.t[m];
            paramArrayOfFloat[(k + 2)] = this.t[(m + this.n1)];
            paramArrayOfFloat[(k + 3)] = this.t[(m + 2 * this.n1)];
          }
        }
      for (int j = 0; j < this.n2; j += 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2 + j;
          m = this.n1 + i;
          this.t[i] = paramArrayOfFloat[k];
          this.t[m] = paramArrayOfFloat[(k + 1)];
          this.t[(m + this.n1)] = paramArrayOfFloat[(k + 2)];
          this.t[(m + 2 * this.n1)] = paramArrayOfFloat[(k + 3)];
        }
        this.dctn1.inverse(this.t, 0, paramBoolean);
        this.dctn1.inverse(this.t, this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 2 * this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 3 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2 + j;
          m = this.n1 + i;
          paramArrayOfFloat[k] = this.t[i];
          paramArrayOfFloat[(k + 1)] = this.t[m];
          paramArrayOfFloat[(k + 2)] = this.t[(m + this.n1)];
          paramArrayOfFloat[(k + 3)] = this.t[(m + 2 * this.n1)];
        }
      }
    }
    if (this.n2 == 2)
    {
      for (i = 0; i < this.n1; i++)
      {
        k = i * this.n2;
        this.t[i] = paramArrayOfFloat[k];
        this.t[(this.n1 + i)] = paramArrayOfFloat[(k + 1)];
      }
      if (paramInt == -1)
      {
        this.dctn1.forward(this.t, 0, paramBoolean);
        this.dctn1.forward(this.t, this.n1, paramBoolean);
      }
      else
      {
        this.dctn1.inverse(this.t, 0, paramBoolean);
        this.dctn1.inverse(this.t, this.n1, paramBoolean);
      }
      for (i = 0; i < this.n1; i++)
      {
        k = i * this.n2;
        paramArrayOfFloat[k] = this.t[i];
        paramArrayOfFloat[(k + 1)] = this.t[(this.n1 + i)];
      }
    }
  }

  private void ddxt2d_sub(int paramInt, float[][] paramArrayOfFloat, boolean paramBoolean)
  {
    int i;
    if (this.n2 > 2)
    {
      int k;
      if (paramInt == -1)
        for (j = 0; j < this.n2; j += 4)
        {
          for (i = 0; i < this.n1; i++)
          {
            k = this.n1 + i;
            this.t[i] = paramArrayOfFloat[i][j];
            this.t[k] = paramArrayOfFloat[i][(j + 1)];
            this.t[(k + this.n1)] = paramArrayOfFloat[i][(j + 2)];
            this.t[(k + 2 * this.n1)] = paramArrayOfFloat[i][(j + 3)];
          }
          this.dctn1.forward(this.t, 0, paramBoolean);
          this.dctn1.forward(this.t, this.n1, paramBoolean);
          this.dctn1.forward(this.t, 2 * this.n1, paramBoolean);
          this.dctn1.forward(this.t, 3 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            k = this.n1 + i;
            paramArrayOfFloat[i][j] = this.t[i];
            paramArrayOfFloat[i][(j + 1)] = this.t[k];
            paramArrayOfFloat[i][(j + 2)] = this.t[(k + this.n1)];
            paramArrayOfFloat[i][(j + 3)] = this.t[(k + 2 * this.n1)];
          }
        }
      for (int j = 0; j < this.n2; j += 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = this.n1 + i;
          this.t[i] = paramArrayOfFloat[i][j];
          this.t[k] = paramArrayOfFloat[i][(j + 1)];
          this.t[(k + this.n1)] = paramArrayOfFloat[i][(j + 2)];
          this.t[(k + 2 * this.n1)] = paramArrayOfFloat[i][(j + 3)];
        }
        this.dctn1.inverse(this.t, 0, paramBoolean);
        this.dctn1.inverse(this.t, this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 2 * this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 3 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = this.n1 + i;
          paramArrayOfFloat[i][j] = this.t[i];
          paramArrayOfFloat[i][(j + 1)] = this.t[k];
          paramArrayOfFloat[i][(j + 2)] = this.t[(k + this.n1)];
          paramArrayOfFloat[i][(j + 3)] = this.t[(k + 2 * this.n1)];
        }
      }
    }
    if (this.n2 == 2)
    {
      for (i = 0; i < this.n1; i++)
      {
        this.t[i] = paramArrayOfFloat[i][0];
        this.t[(this.n1 + i)] = paramArrayOfFloat[i][1];
      }
      if (paramInt == -1)
      {
        this.dctn1.forward(this.t, 0, paramBoolean);
        this.dctn1.forward(this.t, this.n1, paramBoolean);
      }
      else
      {
        this.dctn1.inverse(this.t, 0, paramBoolean);
        this.dctn1.inverse(this.t, this.n1, paramBoolean);
      }
      for (i = 0; i < this.n1; i++)
      {
        paramArrayOfFloat[i][0] = this.t[i];
        paramArrayOfFloat[i][1] = this.t[(this.n1 + i)];
      }
    }
  }

  private void makewt(int paramInt)
  {
    this.ip[0] = paramInt;
    this.ip[1] = 1;
    if (paramInt > 2)
    {
      int j = paramInt >> 1;
      float f1 = (float)(Math.atan(1.0D) / j);
      float f2 = (float)Math.cos(f1 * j);
      this.w[0] = 1.0F;
      this.w[1] = f2;
      int i;
      if (j == 4)
      {
        this.w[2] = (float)Math.cos(f1 * 2.0F);
        this.w[3] = (float)Math.sin(f1 * 2.0F);
      }
      else if (j > 4)
      {
        makeipt(paramInt);
        this.w[2] = (float)(0.5D / Math.cos(f1 * 2.0F));
        this.w[3] = (float)(0.5D / Math.cos(f1 * 6.0F));
        for (i = 4; i < j; i += 4)
        {
          this.w[i] = (float)Math.cos(f1 * i);
          this.w[(i + 1)] = (float)Math.sin(f1 * i);
          this.w[(i + 2)] = (float)Math.cos(3.0F * f1 * i);
          this.w[(i + 3)] = (float)(-Math.sin(3.0F * f1 * i));
        }
      }
      int m;
      for (int k = 0; j > 2; k = m)
      {
        m = k + j;
        j >>= 1;
        this.w[m] = 1.0F;
        this.w[(m + 1)] = f2;
        float f3;
        float f4;
        if (j == 4)
        {
          f3 = this.w[(k + 4)];
          f4 = this.w[(k + 5)];
          this.w[(m + 2)] = f3;
          this.w[(m + 3)] = f4;
        }
        else if (j > 4)
        {
          f3 = this.w[(k + 4)];
          float f5 = this.w[(k + 6)];
          this.w[(m + 2)] = (float)(0.5D / f3);
          this.w[(m + 3)] = (float)(0.5D / f5);
          for (i = 4; i < j; i += 4)
          {
            int n = k + 2 * i;
            int i1 = m + i;
            f3 = this.w[n];
            f4 = this.w[(n + 1)];
            f5 = this.w[(n + 2)];
            float f6 = this.w[(n + 3)];
            this.w[i1] = f3;
            this.w[(i1 + 1)] = f4;
            this.w[(i1 + 2)] = f5;
            this.w[(i1 + 3)] = f6;
          }
        }
      }
    }
  }

  private void makeipt(int paramInt)
  {
    this.ip[2] = 0;
    this.ip[3] = 16;
    int k = 2;
    int j = paramInt;
    while (j > 32)
    {
      int m = k << 1;
      int i1 = m << 3;
      for (int i = k; i < m; i++)
      {
        int n = this.ip[i] << 2;
        this.ip[(k + i)] = n;
        this.ip[(m + i)] = (n + i1);
      }
      k = m;
      j >>= 2;
    }
  }

  private void makect(int paramInt1, float[] paramArrayOfFloat, int paramInt2)
  {
    this.ip[1] = paramInt1;
    if (paramInt1 > 1)
    {
      int j = paramInt1 >> 1;
      float f = (float)Math.atan(1.0D) / j;
      paramArrayOfFloat[paramInt2] = (float)Math.cos(f * j);
      paramArrayOfFloat[(paramInt2 + j)] = (0.5F * paramArrayOfFloat[paramInt2]);
      for (int i = 1; i < j; i++)
      {
        paramArrayOfFloat[(paramInt2 + i)] = (float)(0.5D * Math.cos(f * i));
        paramArrayOfFloat[(paramInt2 + paramInt1 - i)] = (float)(0.5D * Math.sin(f * i));
      }
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dct.FloatDCT_2D
 * JD-Core Version:    0.6.1
 */