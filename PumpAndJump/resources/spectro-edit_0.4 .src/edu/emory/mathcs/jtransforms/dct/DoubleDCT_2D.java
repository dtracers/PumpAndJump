package edu.emory.mathcs.jtransforms.dct;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DoubleDCT_2D
{
  private int n1;
  private int n2;
  private int[] ip;
  private double[] w;
  private double[] t;
  private DoubleDCT_1D dctn2;
  private DoubleDCT_1D dctn1;
  private int oldNthread;
  private int nt;

  public DoubleDCT_2D(int paramInt1, int paramInt2)
  {
    if ((!ConcurrencyUtils.isPowerOf2(paramInt1)) || (!ConcurrencyUtils.isPowerOf2(paramInt2)))
      throw new IllegalArgumentException("n1, n2 must be power of two numbers");
    if ((paramInt1 <= 1) || (paramInt2 <= 1))
      throw new IllegalArgumentException("n1, n2 must be greater than 1");
    this.n1 = paramInt1;
    this.n2 = paramInt2;
    this.ip = new int[2 + (int)Math.ceil(Math.sqrt(Math.max(paramInt1 / 2, paramInt2 / 2)))];
    this.w = new double[(int)Math.ceil(Math.max(paramInt1 * 1.5D, paramInt2 * 1.5D))];
    this.dctn2 = new DoubleDCT_1D(paramInt2, this.ip, this.w);
    this.dctn1 = new DoubleDCT_1D(paramInt1, this.ip, this.w);
    this.oldNthread = ConcurrencyUtils.getNumberOfProcessors();
    this.nt = (4 * this.oldNthread * paramInt1);
    if (paramInt2 == 2 * this.oldNthread)
      this.nt >>= 1;
    else if (paramInt2 < 2 * this.oldNthread)
      this.nt >>= 2;
    this.t = new double[this.nt];
  }

  public void forward(double[] paramArrayOfDouble, boolean paramBoolean)
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
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(-1, paramArrayOfDouble, paramBoolean);
      ddxt2d0_subth(-1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      ddxt2d_sub(-1, paramArrayOfDouble, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.forward(paramArrayOfDouble, n * this.n2, paramBoolean);
    }
  }

  public void forward(double[][] paramArrayOfDouble, boolean paramBoolean)
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
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(-1, paramArrayOfDouble, paramBoolean);
      ddxt2d0_subth(-1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      ddxt2d_sub(-1, paramArrayOfDouble, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.forward(paramArrayOfDouble[n], paramBoolean);
    }
  }

  public void inverse(double[] paramArrayOfDouble, boolean paramBoolean)
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
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(1, paramArrayOfDouble, paramBoolean);
      ddxt2d0_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      ddxt2d_sub(1, paramArrayOfDouble, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.inverse(paramArrayOfDouble, n * this.n2, paramBoolean);
    }
  }

  public void inverse(double[][] paramArrayOfDouble, boolean paramBoolean)
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
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      ddxt2d_subth(1, paramArrayOfDouble, paramBoolean);
      ddxt2d0_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      ddxt2d_sub(1, paramArrayOfDouble, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.dctn2.inverse(paramArrayOfDouble[n], paramBoolean);
    }
  }

  private void ddxt2d_subth(final int paramInt, final double[] paramArrayOfDouble, final boolean paramBoolean)
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
          if (DoubleDCT_2D.this.n2 > 2 * n)
          {
            if (paramInt == -1)
            {
              j = 4 * i1;
              while (j < DoubleDCT_2D.this.n2)
              {
                for (i = 0; i < DoubleDCT_2D.this.n1; i++)
                {
                  k = i * DoubleDCT_2D.this.n2 + j;
                  m = i2 + DoubleDCT_2D.this.n1 + i;
                  DoubleDCT_2D.this.t[(i2 + i)] = paramArrayOfDouble[k];
                  DoubleDCT_2D.this.t[m] = paramArrayOfDouble[(k + 1)];
                  DoubleDCT_2D.this.t[(m + DoubleDCT_2D.this.n1)] = paramArrayOfDouble[(k + 2)];
                  DoubleDCT_2D.this.t[(m + 2 * DoubleDCT_2D.this.n1)] = paramArrayOfDouble[(k + 3)];
                }
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2, paramBoolean);
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + 2 * DoubleDCT_2D.this.n1, paramBoolean);
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + 3 * DoubleDCT_2D.this.n1, paramBoolean);
                for (i = 0; i < DoubleDCT_2D.this.n1; i++)
                {
                  k = i * DoubleDCT_2D.this.n2 + j;
                  m = i2 + DoubleDCT_2D.this.n1 + i;
                  paramArrayOfDouble[k] = DoubleDCT_2D.this.t[(i2 + i)];
                  paramArrayOfDouble[(k + 1)] = DoubleDCT_2D.this.t[m];
                  paramArrayOfDouble[(k + 2)] = DoubleDCT_2D.this.t[(m + DoubleDCT_2D.this.n1)];
                  paramArrayOfDouble[(k + 3)] = DoubleDCT_2D.this.t[(m + 2 * DoubleDCT_2D.this.n1)];
                }
                j += 4 * n;
              }
            }
            int j = 4 * i1;
            while (j < DoubleDCT_2D.this.n2)
            {
              for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              {
                k = i * DoubleDCT_2D.this.n2 + j;
                m = i2 + DoubleDCT_2D.this.n1 + i;
                DoubleDCT_2D.this.t[(i2 + i)] = paramArrayOfDouble[k];
                DoubleDCT_2D.this.t[m] = paramArrayOfDouble[(k + 1)];
                DoubleDCT_2D.this.t[(m + DoubleDCT_2D.this.n1)] = paramArrayOfDouble[(k + 2)];
                DoubleDCT_2D.this.t[(m + 2 * DoubleDCT_2D.this.n1)] = paramArrayOfDouble[(k + 3)];
              }
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + 2 * DoubleDCT_2D.this.n1, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + 3 * DoubleDCT_2D.this.n1, paramBoolean);
              for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              {
                k = i * DoubleDCT_2D.this.n2 + j;
                m = i2 + DoubleDCT_2D.this.n1 + i;
                paramArrayOfDouble[k] = DoubleDCT_2D.this.t[(i2 + i)];
                paramArrayOfDouble[(k + 1)] = DoubleDCT_2D.this.t[m];
                paramArrayOfDouble[(k + 2)] = DoubleDCT_2D.this.t[(m + DoubleDCT_2D.this.n1)];
                paramArrayOfDouble[(k + 3)] = DoubleDCT_2D.this.t[(m + 2 * DoubleDCT_2D.this.n1)];
              }
              j += 4 * n;
            }
          }
          if (DoubleDCT_2D.this.n2 == 2 * n)
          {
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
            {
              k = i * DoubleDCT_2D.this.n2 + 2 * i1;
              m = i2 + i;
              DoubleDCT_2D.this.t[m] = paramArrayOfDouble[k];
              DoubleDCT_2D.this.t[(m + DoubleDCT_2D.this.n1)] = paramArrayOfDouble[(k + 1)];
            }
            if (paramInt == -1)
            {
              DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2, paramBoolean);
              DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
            }
            else
            {
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
            }
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
            {
              k = i * DoubleDCT_2D.this.n2 + 2 * i1;
              m = i2 + i;
              paramArrayOfDouble[k] = DoubleDCT_2D.this.t[m];
              paramArrayOfDouble[(k + 1)] = DoubleDCT_2D.this.t[(m + DoubleDCT_2D.this.n1)];
            }
          }
          if (DoubleDCT_2D.this.n2 == n)
          {
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              DoubleDCT_2D.this.t[(i2 + i)] = paramArrayOfDouble[(i * DoubleDCT_2D.this.n2 + i1)];
            if (paramInt == -1)
              DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2, paramBoolean);
            else
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2, paramBoolean);
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              paramArrayOfDouble[(i * DoubleDCT_2D.this.n2 + i1)] = DoubleDCT_2D.this.t[(i2 + i)];
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

  private void ddxt2d_subth(final int paramInt, final double[][] paramArrayOfDouble, final boolean paramBoolean)
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
          if (DoubleDCT_2D.this.n2 > 2 * n)
          {
            if (paramInt == -1)
            {
              j = 4 * i1;
              while (j < DoubleDCT_2D.this.n2)
              {
                for (i = 0; i < DoubleDCT_2D.this.n1; i++)
                {
                  k = i2 + DoubleDCT_2D.this.n1 + i;
                  DoubleDCT_2D.this.t[(i2 + i)] = paramArrayOfDouble[i][j];
                  DoubleDCT_2D.this.t[k] = paramArrayOfDouble[i][(j + 1)];
                  DoubleDCT_2D.this.t[(k + DoubleDCT_2D.this.n1)] = paramArrayOfDouble[i][(j + 2)];
                  DoubleDCT_2D.this.t[(k + 2 * DoubleDCT_2D.this.n1)] = paramArrayOfDouble[i][(j + 3)];
                }
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2, paramBoolean);
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + 2 * DoubleDCT_2D.this.n1, paramBoolean);
                DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + 3 * DoubleDCT_2D.this.n1, paramBoolean);
                for (i = 0; i < DoubleDCT_2D.this.n1; i++)
                {
                  k = i2 + DoubleDCT_2D.this.n1 + i;
                  paramArrayOfDouble[i][j] = DoubleDCT_2D.this.t[(i2 + i)];
                  paramArrayOfDouble[i][(j + 1)] = DoubleDCT_2D.this.t[k];
                  paramArrayOfDouble[i][(j + 2)] = DoubleDCT_2D.this.t[(k + DoubleDCT_2D.this.n1)];
                  paramArrayOfDouble[i][(j + 3)] = DoubleDCT_2D.this.t[(k + 2 * DoubleDCT_2D.this.n1)];
                }
                j += 4 * n;
              }
            }
            int j = 4 * i1;
            while (j < DoubleDCT_2D.this.n2)
            {
              for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              {
                k = i2 + DoubleDCT_2D.this.n1 + i;
                DoubleDCT_2D.this.t[(i2 + i)] = paramArrayOfDouble[i][j];
                DoubleDCT_2D.this.t[k] = paramArrayOfDouble[i][(j + 1)];
                DoubleDCT_2D.this.t[(k + DoubleDCT_2D.this.n1)] = paramArrayOfDouble[i][(j + 2)];
                DoubleDCT_2D.this.t[(k + 2 * DoubleDCT_2D.this.n1)] = paramArrayOfDouble[i][(j + 3)];
              }
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + 2 * DoubleDCT_2D.this.n1, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + 3 * DoubleDCT_2D.this.n1, paramBoolean);
              for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              {
                k = i2 + DoubleDCT_2D.this.n1 + i;
                paramArrayOfDouble[i][j] = DoubleDCT_2D.this.t[(i2 + i)];
                paramArrayOfDouble[i][(j + 1)] = DoubleDCT_2D.this.t[k];
                paramArrayOfDouble[i][(j + 2)] = DoubleDCT_2D.this.t[(k + DoubleDCT_2D.this.n1)];
                paramArrayOfDouble[i][(j + 3)] = DoubleDCT_2D.this.t[(k + 2 * DoubleDCT_2D.this.n1)];
              }
              j += 4 * n;
            }
          }
          if (DoubleDCT_2D.this.n2 == 2 * n)
          {
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
            {
              k = i2 + i;
              DoubleDCT_2D.this.t[k] = paramArrayOfDouble[i][(2 * i1)];
              DoubleDCT_2D.this.t[(k + DoubleDCT_2D.this.n1)] = paramArrayOfDouble[i][(2 * i1 + 1)];
            }
            if (paramInt == -1)
            {
              DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2, paramBoolean);
              DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
            }
            else
            {
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2, paramBoolean);
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2 + DoubleDCT_2D.this.n1, paramBoolean);
            }
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
            {
              k = i2 + i;
              paramArrayOfDouble[i][(2 * i1)] = DoubleDCT_2D.this.t[k];
              paramArrayOfDouble[i][(2 * i1 + 1)] = DoubleDCT_2D.this.t[(k + DoubleDCT_2D.this.n1)];
            }
          }
          if (DoubleDCT_2D.this.n2 == n)
          {
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              DoubleDCT_2D.this.t[(i2 + i)] = paramArrayOfDouble[i][i1];
            if (paramInt == -1)
              DoubleDCT_2D.this.dctn1.forward(DoubleDCT_2D.this.t, i2, paramBoolean);
            else
              DoubleDCT_2D.this.dctn1.inverse(DoubleDCT_2D.this.t, i2, paramBoolean);
            for (i = 0; i < DoubleDCT_2D.this.n1; i++)
              paramArrayOfDouble[i][i1] = DoubleDCT_2D.this.t[(i2 + i)];
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

  private void ddxt2d0_subth(final int paramInt, final double[] paramArrayOfDouble, final boolean paramBoolean)
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
            while (i < DoubleDCT_2D.this.n1)
            {
              DoubleDCT_2D.this.dctn2.forward(paramArrayOfDouble, i * DoubleDCT_2D.this.n2, paramBoolean);
              i += i;
            }
          }
          else
          {
            i = m;
            while (i < DoubleDCT_2D.this.n1)
            {
              DoubleDCT_2D.this.dctn2.inverse(paramArrayOfDouble, i * DoubleDCT_2D.this.n2, paramBoolean);
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

  private void ddxt2d0_subth(final int paramInt, final double[][] paramArrayOfDouble, final boolean paramBoolean)
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
            while (i < DoubleDCT_2D.this.n1)
            {
              DoubleDCT_2D.this.dctn2.forward(paramArrayOfDouble[i], paramBoolean);
              i += i;
            }
          }
          else
          {
            i = m;
            while (i < DoubleDCT_2D.this.n1)
            {
              DoubleDCT_2D.this.dctn2.inverse(paramArrayOfDouble[i], paramBoolean);
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

  private void ddxt2d_sub(int paramInt, double[] paramArrayOfDouble, boolean paramBoolean)
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
            this.t[i] = paramArrayOfDouble[k];
            this.t[m] = paramArrayOfDouble[(k + 1)];
            this.t[(m + this.n1)] = paramArrayOfDouble[(k + 2)];
            this.t[(m + 2 * this.n1)] = paramArrayOfDouble[(k + 3)];
          }
          this.dctn1.forward(this.t, 0, paramBoolean);
          this.dctn1.forward(this.t, this.n1, paramBoolean);
          this.dctn1.forward(this.t, 2 * this.n1, paramBoolean);
          this.dctn1.forward(this.t, 3 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            k = i * this.n2 + j;
            m = this.n1 + i;
            paramArrayOfDouble[k] = this.t[i];
            paramArrayOfDouble[(k + 1)] = this.t[m];
            paramArrayOfDouble[(k + 2)] = this.t[(m + this.n1)];
            paramArrayOfDouble[(k + 3)] = this.t[(m + 2 * this.n1)];
          }
        }
      for (int j = 0; j < this.n2; j += 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2 + j;
          m = this.n1 + i;
          this.t[i] = paramArrayOfDouble[k];
          this.t[m] = paramArrayOfDouble[(k + 1)];
          this.t[(m + this.n1)] = paramArrayOfDouble[(k + 2)];
          this.t[(m + 2 * this.n1)] = paramArrayOfDouble[(k + 3)];
        }
        this.dctn1.inverse(this.t, 0, paramBoolean);
        this.dctn1.inverse(this.t, this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 2 * this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 3 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2 + j;
          m = this.n1 + i;
          paramArrayOfDouble[k] = this.t[i];
          paramArrayOfDouble[(k + 1)] = this.t[m];
          paramArrayOfDouble[(k + 2)] = this.t[(m + this.n1)];
          paramArrayOfDouble[(k + 3)] = this.t[(m + 2 * this.n1)];
        }
      }
    }
    if (this.n2 == 2)
    {
      for (i = 0; i < this.n1; i++)
      {
        k = i * this.n2;
        this.t[i] = paramArrayOfDouble[k];
        this.t[(this.n1 + i)] = paramArrayOfDouble[(k + 1)];
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
        paramArrayOfDouble[k] = this.t[i];
        paramArrayOfDouble[(k + 1)] = this.t[(this.n1 + i)];
      }
    }
  }

  private void ddxt2d_sub(int paramInt, double[][] paramArrayOfDouble, boolean paramBoolean)
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
            this.t[i] = paramArrayOfDouble[i][j];
            this.t[k] = paramArrayOfDouble[i][(j + 1)];
            this.t[(k + this.n1)] = paramArrayOfDouble[i][(j + 2)];
            this.t[(k + 2 * this.n1)] = paramArrayOfDouble[i][(j + 3)];
          }
          this.dctn1.forward(this.t, 0, paramBoolean);
          this.dctn1.forward(this.t, this.n1, paramBoolean);
          this.dctn1.forward(this.t, 2 * this.n1, paramBoolean);
          this.dctn1.forward(this.t, 3 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            k = this.n1 + i;
            paramArrayOfDouble[i][j] = this.t[i];
            paramArrayOfDouble[i][(j + 1)] = this.t[k];
            paramArrayOfDouble[i][(j + 2)] = this.t[(k + this.n1)];
            paramArrayOfDouble[i][(j + 3)] = this.t[(k + 2 * this.n1)];
          }
        }
      for (int j = 0; j < this.n2; j += 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = this.n1 + i;
          this.t[i] = paramArrayOfDouble[i][j];
          this.t[k] = paramArrayOfDouble[i][(j + 1)];
          this.t[(k + this.n1)] = paramArrayOfDouble[i][(j + 2)];
          this.t[(k + 2 * this.n1)] = paramArrayOfDouble[i][(j + 3)];
        }
        this.dctn1.inverse(this.t, 0, paramBoolean);
        this.dctn1.inverse(this.t, this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 2 * this.n1, paramBoolean);
        this.dctn1.inverse(this.t, 3 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = this.n1 + i;
          paramArrayOfDouble[i][j] = this.t[i];
          paramArrayOfDouble[i][(j + 1)] = this.t[k];
          paramArrayOfDouble[i][(j + 2)] = this.t[(k + this.n1)];
          paramArrayOfDouble[i][(j + 3)] = this.t[(k + 2 * this.n1)];
        }
      }
    }
    if (this.n2 == 2)
    {
      for (i = 0; i < this.n1; i++)
      {
        this.t[i] = paramArrayOfDouble[i][0];
        this.t[(this.n1 + i)] = paramArrayOfDouble[i][1];
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
        paramArrayOfDouble[i][0] = this.t[i];
        paramArrayOfDouble[i][1] = this.t[(this.n1 + i)];
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
      double d1 = Math.atan(1.0D) / j;
      double d2 = Math.cos(d1 * j);
      this.w[0] = 1.0D;
      this.w[1] = d2;
      int i;
      if (j == 4)
      {
        this.w[2] = Math.cos(d1 * 2.0D);
        this.w[3] = Math.sin(d1 * 2.0D);
      }
      else if (j > 4)
      {
        makeipt(paramInt);
        this.w[2] = (0.5D / Math.cos(d1 * 2.0D));
        this.w[3] = (0.5D / Math.cos(d1 * 6.0D));
        for (i = 4; i < j; i += 4)
        {
          this.w[i] = Math.cos(d1 * i);
          this.w[(i + 1)] = Math.sin(d1 * i);
          this.w[(i + 2)] = Math.cos(3.0D * d1 * i);
          this.w[(i + 3)] = (-Math.sin(3.0D * d1 * i));
        }
      }
      int m;
      for (int k = 0; j > 2; k = m)
      {
        m = k + j;
        j >>= 1;
        this.w[m] = 1.0D;
        this.w[(m + 1)] = d2;
        double d3;
        double d4;
        if (j == 4)
        {
          d3 = this.w[(k + 4)];
          d4 = this.w[(k + 5)];
          this.w[(m + 2)] = d3;
          this.w[(m + 3)] = d4;
        }
        else if (j > 4)
        {
          d3 = this.w[(k + 4)];
          double d5 = this.w[(k + 6)];
          this.w[(m + 2)] = (0.5D / d3);
          this.w[(m + 3)] = (0.5D / d5);
          for (i = 4; i < j; i += 4)
          {
            int n = k + 2 * i;
            int i1 = m + i;
            d3 = this.w[n];
            d4 = this.w[(n + 1)];
            d5 = this.w[(n + 2)];
            double d6 = this.w[(n + 3)];
            this.w[i1] = d3;
            this.w[(i1 + 1)] = d4;
            this.w[(i1 + 2)] = d5;
            this.w[(i1 + 3)] = d6;
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

  private void makect(int paramInt1, double[] paramArrayOfDouble, int paramInt2)
  {
    this.ip[1] = paramInt1;
    if (paramInt1 > 1)
    {
      int j = paramInt1 >> 1;
      double d = Math.atan(1.0D) / j;
      paramArrayOfDouble[paramInt2] = Math.cos(d * j);
      paramArrayOfDouble[(paramInt2 + j)] = (0.5D * paramArrayOfDouble[paramInt2]);
      for (int i = 1; i < j; i++)
      {
        paramArrayOfDouble[(paramInt2 + i)] = (0.5D * Math.cos(d * i));
        paramArrayOfDouble[(paramInt2 + paramInt1 - i)] = (0.5D * Math.sin(d * i));
      }
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dct.DoubleDCT_2D
 * JD-Core Version:    0.6.1
 */