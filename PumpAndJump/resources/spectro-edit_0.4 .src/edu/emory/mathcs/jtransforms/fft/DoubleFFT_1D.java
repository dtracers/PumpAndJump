package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DoubleFFT_1D
{
  private int n;
  private int[] ip;
  private double[] w;

  public DoubleFFT_1D(int paramInt)
  {
    if (!ConcurrencyUtils.isPowerOf2(paramInt))
      throw new IllegalArgumentException("n must be power of two number");
    if (paramInt < 1)
      throw new IllegalArgumentException("n must be greater than 0");
    this.n = paramInt;
    this.ip = new int[2 + (int)Math.ceil(2 + (1 << (int)(Math.log(paramInt + 0.5D) / Math.log(2.0D)) / 2))];
    this.w = new double[paramInt / 2];
  }

  public DoubleFFT_1D(int paramInt, int[] paramArrayOfInt, double[] paramArrayOfDouble)
  {
    if (!ConcurrencyUtils.isPowerOf2(paramInt))
      throw new IllegalArgumentException("n must be power of two number");
    if (paramInt < 1)
      throw new IllegalArgumentException("n must be greater than 0");
    this.n = paramInt;
    this.ip = paramArrayOfInt;
    this.w = paramArrayOfDouble;
  }

  public void complexForward(double[] paramArrayOfDouble)
  {
    complexForward(paramArrayOfDouble, 0);
  }

  public void complexForward(double[] paramArrayOfDouble, int paramInt)
  {
    int j = 2 * this.n;
    int i = this.ip[0];
    if (j > i << 2)
    {
      i = j >> 2;
      makewt(i);
    }
    cftbsub(j, paramArrayOfDouble, paramInt, this.ip, i, this.w);
  }

  public void complexInverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    complexInverse(paramArrayOfDouble, 0, paramBoolean);
  }

  public void complexInverse(double[] paramArrayOfDouble, int paramInt, boolean paramBoolean)
  {
    int j = 2 * this.n;
    int i = this.ip[0];
    if (j > i << 2)
    {
      i = j >> 2;
      makewt(i);
    }
    cftfsub(j, paramArrayOfDouble, paramInt, this.ip, i, this.w);
    if (paramBoolean)
      scale(this.n, paramArrayOfDouble, paramInt, true);
  }

  public void realForward(double[] paramArrayOfDouble)
  {
    realForward(paramArrayOfDouble, 0);
  }

  public void realForward(double[] paramArrayOfDouble, int paramInt)
  {
    if (this.n == 1)
      return;
    int i = this.ip[0];
    if (this.n > i << 2)
    {
      i = this.n >> 2;
      makewt(i);
    }
    int j = this.ip[1];
    if (this.n > j << 2)
    {
      j = this.n >> 2;
      makect(j, this.w, i);
    }
    if (this.n > 4)
    {
      cftfsub(this.n, paramArrayOfDouble, paramInt, this.ip, i, this.w);
      rftfsub(this.n, paramArrayOfDouble, paramInt, j, this.w, i);
    }
    else if (this.n == 4)
    {
      cftx020(paramArrayOfDouble, paramInt);
    }
    double d = paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 1)];
    paramArrayOfDouble[paramInt] += paramArrayOfDouble[(paramInt + 1)];
    paramArrayOfDouble[(paramInt + 1)] = d;
  }

  public void realForwardFull(double[] paramArrayOfDouble)
  {
    realForwardFull(paramArrayOfDouble, 0);
  }

  public void realForwardFull(final double[] paramArrayOfDouble, final int paramInt)
  {
    realForward(paramArrayOfDouble, paramInt);
    final int i = 2 * this.n;
    int j = ConcurrencyUtils.getNumberOfProcessors();
    int m;
    if ((j > 1) && (this.n / 2 > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      Future[] arrayOfFuture = new Future[j];
      m = this.n / 2 / j;
      for (int i1 = 0; i1 < j; i1++)
      {
        final int i2 = i1 * m;
        final int i3;
        if (i1 == j - 1)
          i3 = this.n / 2;
        else
          i3 = i2 + m;
        arrayOfFuture[i1] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            for (int j = i2; j < i3; j++)
            {
              int i = 2 * j;
              paramArrayOfDouble[(paramInt + (i - i) % i)] = paramArrayOfDouble[(paramInt + i)];
              paramArrayOfDouble[(paramInt + ((i - i) % i + 1))] = (-paramArrayOfDouble[(paramInt + i + 1)]);
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
      for (m = 0; m < this.n / 2; m += 2)
      {
        int k = 2 * m;
        paramArrayOfDouble[(paramInt + (i - k) % i)] = paramArrayOfDouble[(paramInt + k)];
        paramArrayOfDouble[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfDouble[(paramInt + k + 1)]);
        k = 2 * (m + 1);
        paramArrayOfDouble[(paramInt + (i - k) % i)] = paramArrayOfDouble[(paramInt + k)];
        paramArrayOfDouble[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfDouble[(paramInt + k + 1)]);
      }
    }
    paramArrayOfDouble[(paramInt + this.n)] = (-paramArrayOfDouble[(paramInt + 1)]);
    paramArrayOfDouble[(paramInt + 1)] = 0.0D;
  }

  public void realInverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    realInverse(paramArrayOfDouble, 0, paramBoolean);
  }

  public void realInverse(double[] paramArrayOfDouble, int paramInt, boolean paramBoolean)
  {
    if (this.n == 1)
      return;
    int i = this.ip[0];
    if (this.n > i << 2)
    {
      i = this.n >> 2;
      makewt(i);
    }
    int j = this.ip[1];
    if (this.n > j << 2)
    {
      j = this.n >> 2;
      makect(j, this.w, i);
    }
    paramArrayOfDouble[(paramInt + 1)] = (0.5D * (paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 1)]));
    paramArrayOfDouble[paramInt] -= paramArrayOfDouble[(paramInt + 1)];
    if (this.n > 4)
    {
      rftfsub(this.n, paramArrayOfDouble, paramInt, j, this.w, i);
      cftbsub(this.n, paramArrayOfDouble, paramInt, this.ip, i, this.w);
    }
    else if (this.n == 4)
    {
      cftxc020(paramArrayOfDouble, paramInt);
    }
    if (paramBoolean)
      scale(this.n / 2, paramArrayOfDouble, paramInt, false);
  }

  public void realInverseFull(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    realInverseFull(paramArrayOfDouble, 0, paramBoolean);
  }

  public void realInverseFull(final double[] paramArrayOfDouble, final int paramInt, boolean paramBoolean)
  {
    realInverse2(paramArrayOfDouble, paramInt, paramBoolean);
    final int i = 2 * this.n;
    int j = ConcurrencyUtils.getNumberOfProcessors();
    int m;
    if ((j > 1) && (this.n / 2 > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
    {
      Future[] arrayOfFuture = new Future[j];
      m = this.n / 2 / j;
      for (int i1 = 0; i1 < j; i1++)
      {
        final int i2 = i1 * m;
        final int i3;
        if (i1 == j - 1)
          i3 = this.n / 2;
        else
          i3 = i2 + m;
        arrayOfFuture[i1] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            for (int j = i2; j < i3; j++)
            {
              int i = 2 * j;
              paramArrayOfDouble[(paramInt + (i - i) % i)] = paramArrayOfDouble[(paramInt + i)];
              paramArrayOfDouble[(paramInt + ((i - i) % i + 1))] = (-paramArrayOfDouble[(paramInt + i + 1)]);
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
      for (m = 0; m < this.n / 2; m += 2)
      {
        int k = 2 * m;
        paramArrayOfDouble[(paramInt + (i - k) % i)] = paramArrayOfDouble[(paramInt + k)];
        paramArrayOfDouble[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfDouble[(paramInt + k + 1)]);
        k = 2 * (m + 1);
        paramArrayOfDouble[(paramInt + (i - k) % i)] = paramArrayOfDouble[(paramInt + k)];
        paramArrayOfDouble[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfDouble[(paramInt + k + 1)]);
      }
    }
    paramArrayOfDouble[(paramInt + this.n)] = (-paramArrayOfDouble[(paramInt + 1)]);
    paramArrayOfDouble[(paramInt + 1)] = 0.0D;
  }

  protected void realInverse2(double[] paramArrayOfDouble, int paramInt, boolean paramBoolean)
  {
    int i = this.ip[0];
    if (this.n > i << 2)
    {
      i = this.n >> 2;
      makewt(i);
    }
    int j = this.ip[1];
    if (this.n > j << 2)
    {
      j = this.n >> 2;
      makect(j, this.w, i);
    }
    if (this.n > 4)
    {
      cftfsub(this.n, paramArrayOfDouble, paramInt, this.ip, i, this.w);
      rftbsub(this.n, paramArrayOfDouble, paramInt, j, this.w, i);
    }
    else if (this.n == 4)
    {
      cftbsub(this.n, paramArrayOfDouble, paramInt, this.ip, i, this.w);
    }
    double d = paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 1)];
    paramArrayOfDouble[paramInt] += paramArrayOfDouble[(paramInt + 1)];
    paramArrayOfDouble[(paramInt + 1)] = d;
    if (paramBoolean)
      scale(this.n, paramArrayOfDouble, paramInt, false);
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
            int i1 = k + 2 * i;
            int i2 = m + i;
            d3 = this.w[i1];
            d4 = this.w[(i1 + 1)];
            d5 = this.w[(i1 + 2)];
            double d6 = this.w[(i1 + 3)];
            this.w[i2] = d3;
            this.w[(i2 + 1)] = d4;
            this.w[(i2 + 2)] = d5;
            this.w[(i2 + 3)] = d6;
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
      int i2 = m << 3;
      for (int i = k; i < m; i++)
      {
        int i1 = this.ip[i] << 2;
        this.ip[(k + i)] = i1;
        this.ip[(m + i)] = (i1 + i2);
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

  private void cftfsub(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int[] paramArrayOfInt, int paramInt3, double[] paramArrayOfDouble2)
  {
    if (paramInt1 > 8)
    {
      if (paramInt1 > 32)
      {
        cftf1st(paramInt1, paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, paramInt3 - (paramInt1 >> 2));
        if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (paramInt1 > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
          cftrec4_th(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        else if (paramInt1 > 512)
          cftrec4(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        else if (paramInt1 > 128)
          cftleaf(paramInt1, 1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        else
          cftfx41(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        bitrv2(paramInt1, paramArrayOfInt, paramArrayOfDouble1, paramInt2);
      }
      else if (paramInt1 == 32)
      {
        cftf161(paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, paramInt3 - 8);
        bitrv216(paramArrayOfDouble1, paramInt2);
      }
      else
      {
        cftf081(paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, 0);
        bitrv208(paramArrayOfDouble1, paramInt2);
      }
    }
    else if (paramInt1 == 8)
      cftf040(paramArrayOfDouble1, paramInt2);
    else if (paramInt1 == 4)
      cftxb020(paramArrayOfDouble1, paramInt2);
  }

  private void cftbsub(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int[] paramArrayOfInt, int paramInt3, double[] paramArrayOfDouble2)
  {
    if (paramInt1 > 8)
    {
      if (paramInt1 > 32)
      {
        cftb1st(paramInt1, paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, paramInt3 - (paramInt1 >> 2));
        if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (paramInt1 > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
          cftrec4_th(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        else if (paramInt1 > 512)
          cftrec4(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        else if (paramInt1 > 128)
          cftleaf(paramInt1, 1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        else
          cftfx41(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        bitrv2conj(paramInt1, paramArrayOfInt, paramArrayOfDouble1, paramInt2);
      }
      else if (paramInt1 == 32)
      {
        cftf161(paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, paramInt3 - 8);
        bitrv216neg(paramArrayOfDouble1, paramInt2);
      }
      else
      {
        cftf081(paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, 0);
        bitrv208neg(paramArrayOfDouble1, paramInt2);
      }
    }
    else if (paramInt1 == 8)
      cftb040(paramArrayOfDouble1, paramInt2);
    else if (paramInt1 == 4)
      cftxb020(paramArrayOfDouble1, paramInt2);
  }

  private void bitrv2(int paramInt1, int[] paramArrayOfInt, double[] paramArrayOfDouble, int paramInt2)
  {
    int i2 = 1;
    int i1 = paramInt1 >> 2;
    while (i1 > 8)
    {
      i2 <<= 1;
      i1 >>= 2;
    }
    int i3 = paramInt1 >> 1;
    int i4 = 4 * i2;
    int i;
    int j;
    int m;
    int i5;
    int i6;
    double d1;
    double d2;
    double d3;
    double d4;
    if (i1 == 8)
      for (k = 0; k < i2; k++)
      {
        for (i = 0; i < k; i++)
        {
          j = 4 * i + 2 * paramArrayOfInt[(i2 + k)];
          m = 4 * k + 2 * paramArrayOfInt[(i2 + i)];
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i3;
          m += 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += 2;
          m += i3;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i3;
          m -= 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
        }
        m = 4 * k + 2 * paramArrayOfInt[(i2 + k)];
        j = m + 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i4;
        m += 2 * i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= 2;
        m -= i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i3 + 2;
        m += i3 + 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i3 - i4;
        m += 2 * i4 - 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
      }
    for (int k = 0; k < i2; k++)
    {
      for (i = 0; i < k; i++)
      {
        j = 4 * i + paramArrayOfInt[(i2 + k)];
        m = 4 * k + paramArrayOfInt[(i2 + i)];
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i3;
        m += 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i3;
        m -= 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
      }
      m = 4 * k + paramArrayOfInt[(i2 + k)];
      j = m + 2;
      m += i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      d1 = paramArrayOfDouble[i5];
      d2 = paramArrayOfDouble[(i5 + 1)];
      d3 = paramArrayOfDouble[i6];
      d4 = paramArrayOfDouble[(i6 + 1)];
      paramArrayOfDouble[i5] = d3;
      paramArrayOfDouble[(i5 + 1)] = d4;
      paramArrayOfDouble[i6] = d1;
      paramArrayOfDouble[(i6 + 1)] = d2;
      j += i4;
      m += i4;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      d1 = paramArrayOfDouble[i5];
      d2 = paramArrayOfDouble[(i5 + 1)];
      d3 = paramArrayOfDouble[i6];
      d4 = paramArrayOfDouble[(i6 + 1)];
      paramArrayOfDouble[i5] = d3;
      paramArrayOfDouble[(i5 + 1)] = d4;
      paramArrayOfDouble[i6] = d1;
      paramArrayOfDouble[(i6 + 1)] = d2;
    }
  }

  private void bitrv2conj(int paramInt1, int[] paramArrayOfInt, double[] paramArrayOfDouble, int paramInt2)
  {
    int i2 = 1;
    int i1 = paramInt1 >> 2;
    while (i1 > 8)
    {
      i2 <<= 1;
      i1 >>= 2;
    }
    int i3 = paramInt1 >> 1;
    int i4 = 4 * i2;
    int i;
    int j;
    int m;
    int i5;
    int i6;
    double d1;
    double d2;
    double d3;
    double d4;
    if (i1 == 8)
      for (k = 0; k < i2; k++)
      {
        for (i = 0; i < k; i++)
        {
          j = 4 * i + 2 * paramArrayOfInt[(i2 + k)];
          m = 4 * k + 2 * paramArrayOfInt[(i2 + i)];
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i3;
          m += 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += 2;
          m += i3;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i3;
          m -= 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          d1 = paramArrayOfDouble[i5];
          d2 = -paramArrayOfDouble[(i5 + 1)];
          d3 = paramArrayOfDouble[i6];
          d4 = -paramArrayOfDouble[(i6 + 1)];
          paramArrayOfDouble[i5] = d3;
          paramArrayOfDouble[(i5 + 1)] = d4;
          paramArrayOfDouble[i6] = d1;
          paramArrayOfDouble[(i6 + 1)] = d2;
        }
        m = 4 * k + 2 * paramArrayOfInt[(i2 + k)];
        j = m + 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        paramArrayOfDouble[(i5 - 1)] = (-paramArrayOfDouble[(i5 - 1)]);
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        paramArrayOfDouble[(i6 + 3)] = (-paramArrayOfDouble[(i6 + 3)]);
        j += i4;
        m += 2 * i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= 2;
        m -= i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i3 + 2;
        m += i3 + 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i3 - i4;
        m += 2 * i4 - 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        paramArrayOfDouble[(i5 - 1)] = (-paramArrayOfDouble[(i5 - 1)]);
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        paramArrayOfDouble[(i6 + 3)] = (-paramArrayOfDouble[(i6 + 3)]);
      }
    for (int k = 0; k < i2; k++)
    {
      for (i = 0; i < k; i++)
      {
        j = 4 * i + paramArrayOfInt[(i2 + k)];
        m = 4 * k + paramArrayOfInt[(i2 + i)];
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i3;
        m += 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i3;
        m -= 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        d1 = paramArrayOfDouble[i5];
        d2 = -paramArrayOfDouble[(i5 + 1)];
        d3 = paramArrayOfDouble[i6];
        d4 = -paramArrayOfDouble[(i6 + 1)];
        paramArrayOfDouble[i5] = d3;
        paramArrayOfDouble[(i5 + 1)] = d4;
        paramArrayOfDouble[i6] = d1;
        paramArrayOfDouble[(i6 + 1)] = d2;
      }
      m = 4 * k + paramArrayOfInt[(i2 + k)];
      j = m + 2;
      m += i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      paramArrayOfDouble[(i5 - 1)] = (-paramArrayOfDouble[(i5 - 1)]);
      d1 = paramArrayOfDouble[i5];
      d2 = -paramArrayOfDouble[(i5 + 1)];
      d3 = paramArrayOfDouble[i6];
      d4 = -paramArrayOfDouble[(i6 + 1)];
      paramArrayOfDouble[i5] = d3;
      paramArrayOfDouble[(i5 + 1)] = d4;
      paramArrayOfDouble[i6] = d1;
      paramArrayOfDouble[(i6 + 1)] = d2;
      paramArrayOfDouble[(i6 + 3)] = (-paramArrayOfDouble[(i6 + 3)]);
      j += i4;
      m += i4;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      paramArrayOfDouble[(i5 - 1)] = (-paramArrayOfDouble[(i5 - 1)]);
      d1 = paramArrayOfDouble[i5];
      d2 = -paramArrayOfDouble[(i5 + 1)];
      d3 = paramArrayOfDouble[i6];
      d4 = -paramArrayOfDouble[(i6 + 1)];
      paramArrayOfDouble[i5] = d3;
      paramArrayOfDouble[(i5 + 1)] = d4;
      paramArrayOfDouble[i6] = d1;
      paramArrayOfDouble[(i6 + 1)] = d2;
      paramArrayOfDouble[(i6 + 3)] = (-paramArrayOfDouble[(i6 + 3)]);
    }
  }

  private void bitrv216(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[(paramInt + 2)];
    double d2 = paramArrayOfDouble[(paramInt + 3)];
    double d3 = paramArrayOfDouble[(paramInt + 4)];
    double d4 = paramArrayOfDouble[(paramInt + 5)];
    double d5 = paramArrayOfDouble[(paramInt + 6)];
    double d6 = paramArrayOfDouble[(paramInt + 7)];
    double d7 = paramArrayOfDouble[(paramInt + 8)];
    double d8 = paramArrayOfDouble[(paramInt + 9)];
    double d9 = paramArrayOfDouble[(paramInt + 10)];
    double d10 = paramArrayOfDouble[(paramInt + 11)];
    double d11 = paramArrayOfDouble[(paramInt + 14)];
    double d12 = paramArrayOfDouble[(paramInt + 15)];
    double d13 = paramArrayOfDouble[(paramInt + 16)];
    double d14 = paramArrayOfDouble[(paramInt + 17)];
    double d15 = paramArrayOfDouble[(paramInt + 20)];
    double d16 = paramArrayOfDouble[(paramInt + 21)];
    double d17 = paramArrayOfDouble[(paramInt + 22)];
    double d18 = paramArrayOfDouble[(paramInt + 23)];
    double d19 = paramArrayOfDouble[(paramInt + 24)];
    double d20 = paramArrayOfDouble[(paramInt + 25)];
    double d21 = paramArrayOfDouble[(paramInt + 26)];
    double d22 = paramArrayOfDouble[(paramInt + 27)];
    double d23 = paramArrayOfDouble[(paramInt + 28)];
    double d24 = paramArrayOfDouble[(paramInt + 29)];
    paramArrayOfDouble[(paramInt + 2)] = d13;
    paramArrayOfDouble[(paramInt + 3)] = d14;
    paramArrayOfDouble[(paramInt + 4)] = d7;
    paramArrayOfDouble[(paramInt + 5)] = d8;
    paramArrayOfDouble[(paramInt + 6)] = d19;
    paramArrayOfDouble[(paramInt + 7)] = d20;
    paramArrayOfDouble[(paramInt + 8)] = d3;
    paramArrayOfDouble[(paramInt + 9)] = d4;
    paramArrayOfDouble[(paramInt + 10)] = d15;
    paramArrayOfDouble[(paramInt + 11)] = d16;
    paramArrayOfDouble[(paramInt + 14)] = d23;
    paramArrayOfDouble[(paramInt + 15)] = d24;
    paramArrayOfDouble[(paramInt + 16)] = d1;
    paramArrayOfDouble[(paramInt + 17)] = d2;
    paramArrayOfDouble[(paramInt + 20)] = d9;
    paramArrayOfDouble[(paramInt + 21)] = d10;
    paramArrayOfDouble[(paramInt + 22)] = d21;
    paramArrayOfDouble[(paramInt + 23)] = d22;
    paramArrayOfDouble[(paramInt + 24)] = d5;
    paramArrayOfDouble[(paramInt + 25)] = d6;
    paramArrayOfDouble[(paramInt + 26)] = d17;
    paramArrayOfDouble[(paramInt + 27)] = d18;
    paramArrayOfDouble[(paramInt + 28)] = d11;
    paramArrayOfDouble[(paramInt + 29)] = d12;
  }

  private void bitrv216neg(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[(paramInt + 2)];
    double d2 = paramArrayOfDouble[(paramInt + 3)];
    double d3 = paramArrayOfDouble[(paramInt + 4)];
    double d4 = paramArrayOfDouble[(paramInt + 5)];
    double d5 = paramArrayOfDouble[(paramInt + 6)];
    double d6 = paramArrayOfDouble[(paramInt + 7)];
    double d7 = paramArrayOfDouble[(paramInt + 8)];
    double d8 = paramArrayOfDouble[(paramInt + 9)];
    double d9 = paramArrayOfDouble[(paramInt + 10)];
    double d10 = paramArrayOfDouble[(paramInt + 11)];
    double d11 = paramArrayOfDouble[(paramInt + 12)];
    double d12 = paramArrayOfDouble[(paramInt + 13)];
    double d13 = paramArrayOfDouble[(paramInt + 14)];
    double d14 = paramArrayOfDouble[(paramInt + 15)];
    double d15 = paramArrayOfDouble[(paramInt + 16)];
    double d16 = paramArrayOfDouble[(paramInt + 17)];
    double d17 = paramArrayOfDouble[(paramInt + 18)];
    double d18 = paramArrayOfDouble[(paramInt + 19)];
    double d19 = paramArrayOfDouble[(paramInt + 20)];
    double d20 = paramArrayOfDouble[(paramInt + 21)];
    double d21 = paramArrayOfDouble[(paramInt + 22)];
    double d22 = paramArrayOfDouble[(paramInt + 23)];
    double d23 = paramArrayOfDouble[(paramInt + 24)];
    double d24 = paramArrayOfDouble[(paramInt + 25)];
    double d25 = paramArrayOfDouble[(paramInt + 26)];
    double d26 = paramArrayOfDouble[(paramInt + 27)];
    double d27 = paramArrayOfDouble[(paramInt + 28)];
    double d28 = paramArrayOfDouble[(paramInt + 29)];
    double d29 = paramArrayOfDouble[(paramInt + 30)];
    double d30 = paramArrayOfDouble[(paramInt + 31)];
    paramArrayOfDouble[(paramInt + 2)] = d29;
    paramArrayOfDouble[(paramInt + 3)] = d30;
    paramArrayOfDouble[(paramInt + 4)] = d13;
    paramArrayOfDouble[(paramInt + 5)] = d14;
    paramArrayOfDouble[(paramInt + 6)] = d21;
    paramArrayOfDouble[(paramInt + 7)] = d22;
    paramArrayOfDouble[(paramInt + 8)] = d5;
    paramArrayOfDouble[(paramInt + 9)] = d6;
    paramArrayOfDouble[(paramInt + 10)] = d25;
    paramArrayOfDouble[(paramInt + 11)] = d26;
    paramArrayOfDouble[(paramInt + 12)] = d9;
    paramArrayOfDouble[(paramInt + 13)] = d10;
    paramArrayOfDouble[(paramInt + 14)] = d17;
    paramArrayOfDouble[(paramInt + 15)] = d18;
    paramArrayOfDouble[(paramInt + 16)] = d1;
    paramArrayOfDouble[(paramInt + 17)] = d2;
    paramArrayOfDouble[(paramInt + 18)] = d27;
    paramArrayOfDouble[(paramInt + 19)] = d28;
    paramArrayOfDouble[(paramInt + 20)] = d11;
    paramArrayOfDouble[(paramInt + 21)] = d12;
    paramArrayOfDouble[(paramInt + 22)] = d19;
    paramArrayOfDouble[(paramInt + 23)] = d20;
    paramArrayOfDouble[(paramInt + 24)] = d3;
    paramArrayOfDouble[(paramInt + 25)] = d4;
    paramArrayOfDouble[(paramInt + 26)] = d23;
    paramArrayOfDouble[(paramInt + 27)] = d24;
    paramArrayOfDouble[(paramInt + 28)] = d7;
    paramArrayOfDouble[(paramInt + 29)] = d8;
    paramArrayOfDouble[(paramInt + 30)] = d15;
    paramArrayOfDouble[(paramInt + 31)] = d16;
  }

  private void bitrv208(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[(paramInt + 2)];
    double d2 = paramArrayOfDouble[(paramInt + 3)];
    double d3 = paramArrayOfDouble[(paramInt + 6)];
    double d4 = paramArrayOfDouble[(paramInt + 7)];
    double d5 = paramArrayOfDouble[(paramInt + 8)];
    double d6 = paramArrayOfDouble[(paramInt + 9)];
    double d7 = paramArrayOfDouble[(paramInt + 12)];
    double d8 = paramArrayOfDouble[(paramInt + 13)];
    paramArrayOfDouble[(paramInt + 2)] = d5;
    paramArrayOfDouble[(paramInt + 3)] = d6;
    paramArrayOfDouble[(paramInt + 6)] = d7;
    paramArrayOfDouble[(paramInt + 7)] = d8;
    paramArrayOfDouble[(paramInt + 8)] = d1;
    paramArrayOfDouble[(paramInt + 9)] = d2;
    paramArrayOfDouble[(paramInt + 12)] = d3;
    paramArrayOfDouble[(paramInt + 13)] = d4;
  }

  private void bitrv208neg(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[(paramInt + 2)];
    double d2 = paramArrayOfDouble[(paramInt + 3)];
    double d3 = paramArrayOfDouble[(paramInt + 4)];
    double d4 = paramArrayOfDouble[(paramInt + 5)];
    double d5 = paramArrayOfDouble[(paramInt + 6)];
    double d6 = paramArrayOfDouble[(paramInt + 7)];
    double d7 = paramArrayOfDouble[(paramInt + 8)];
    double d8 = paramArrayOfDouble[(paramInt + 9)];
    double d9 = paramArrayOfDouble[(paramInt + 10)];
    double d10 = paramArrayOfDouble[(paramInt + 11)];
    double d11 = paramArrayOfDouble[(paramInt + 12)];
    double d12 = paramArrayOfDouble[(paramInt + 13)];
    double d13 = paramArrayOfDouble[(paramInt + 14)];
    double d14 = paramArrayOfDouble[(paramInt + 15)];
    paramArrayOfDouble[(paramInt + 2)] = d13;
    paramArrayOfDouble[(paramInt + 3)] = d14;
    paramArrayOfDouble[(paramInt + 4)] = d5;
    paramArrayOfDouble[(paramInt + 5)] = d6;
    paramArrayOfDouble[(paramInt + 6)] = d9;
    paramArrayOfDouble[(paramInt + 7)] = d10;
    paramArrayOfDouble[(paramInt + 8)] = d1;
    paramArrayOfDouble[(paramInt + 9)] = d2;
    paramArrayOfDouble[(paramInt + 10)] = d11;
    paramArrayOfDouble[(paramInt + 11)] = d12;
    paramArrayOfDouble[(paramInt + 12)] = d3;
    paramArrayOfDouble[(paramInt + 13)] = d4;
    paramArrayOfDouble[(paramInt + 14)] = d7;
    paramArrayOfDouble[(paramInt + 15)] = d8;
  }

  private void cftf1st(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3)
  {
    int i4 = paramInt1 >> 3;
    int i3 = 2 * i4;
    int k = i3;
    int m = k + i3;
    int i1 = m + i3;
    int i6 = paramInt2 + k;
    int i7 = paramInt2 + m;
    int i8 = paramInt2 + i1;
    double d12 = paramArrayOfDouble1[paramInt2] + paramArrayOfDouble1[i7];
    double d13 = paramArrayOfDouble1[(paramInt2 + 1)] + paramArrayOfDouble1[(i7 + 1)];
    double d14 = paramArrayOfDouble1[paramInt2] - paramArrayOfDouble1[i7];
    double d15 = paramArrayOfDouble1[(paramInt2 + 1)] - paramArrayOfDouble1[(i7 + 1)];
    double d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
    double d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
    double d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
    double d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
    paramArrayOfDouble1[paramInt2] = (d12 + d16);
    paramArrayOfDouble1[(paramInt2 + 1)] = (d13 + d17);
    paramArrayOfDouble1[i6] = (d12 - d16);
    paramArrayOfDouble1[(i6 + 1)] = (d13 - d17);
    paramArrayOfDouble1[i7] = (d14 - d19);
    paramArrayOfDouble1[(i7 + 1)] = (d15 + d18);
    paramArrayOfDouble1[i8] = (d14 + d19);
    paramArrayOfDouble1[(i8 + 1)] = (d15 - d18);
    double d1 = paramArrayOfDouble2[(paramInt3 + 1)];
    double d2 = paramArrayOfDouble2[(paramInt3 + 2)];
    double d3 = paramArrayOfDouble2[(paramInt3 + 3)];
    double d8 = 1.0D;
    double d9 = 0.0D;
    double d10 = 1.0D;
    double d11 = 0.0D;
    int i2 = 0;
    for (int i = 2; i < i4 - 2; i += 4)
    {
      i2 += 4;
      int i9 = paramInt3 + i2;
      d4 = d2 * (d8 + paramArrayOfDouble2[i9]);
      d5 = d2 * (d9 + paramArrayOfDouble2[(i9 + 1)]);
      d6 = d3 * (d10 + paramArrayOfDouble2[(i9 + 2)]);
      d7 = d3 * (d11 + paramArrayOfDouble2[(i9 + 3)]);
      d8 = paramArrayOfDouble2[i9];
      d9 = paramArrayOfDouble2[(i9 + 1)];
      d10 = paramArrayOfDouble2[(i9 + 2)];
      d11 = paramArrayOfDouble2[(i9 + 3)];
      k = i + i3;
      m = k + i3;
      i1 = m + i3;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      int i10 = paramInt2 + i;
      d12 = paramArrayOfDouble1[i10] + paramArrayOfDouble1[i7];
      d13 = paramArrayOfDouble1[(i10 + 1)] + paramArrayOfDouble1[(i7 + 1)];
      d14 = paramArrayOfDouble1[i10] - paramArrayOfDouble1[i7];
      d15 = paramArrayOfDouble1[(i10 + 1)] - paramArrayOfDouble1[(i7 + 1)];
      double d20 = paramArrayOfDouble1[(i10 + 2)] + paramArrayOfDouble1[(i7 + 2)];
      double d21 = paramArrayOfDouble1[(i10 + 3)] + paramArrayOfDouble1[(i7 + 3)];
      double d22 = paramArrayOfDouble1[(i10 + 2)] - paramArrayOfDouble1[(i7 + 2)];
      double d23 = paramArrayOfDouble1[(i10 + 3)] - paramArrayOfDouble1[(i7 + 3)];
      d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
      d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
      d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
      d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
      double d24 = paramArrayOfDouble1[(i6 + 2)] + paramArrayOfDouble1[(i8 + 2)];
      double d25 = paramArrayOfDouble1[(i6 + 3)] + paramArrayOfDouble1[(i8 + 3)];
      double d26 = paramArrayOfDouble1[(i6 + 2)] - paramArrayOfDouble1[(i8 + 2)];
      double d27 = paramArrayOfDouble1[(i6 + 3)] - paramArrayOfDouble1[(i8 + 3)];
      paramArrayOfDouble1[i10] = (d12 + d16);
      paramArrayOfDouble1[(i10 + 1)] = (d13 + d17);
      paramArrayOfDouble1[(i10 + 2)] = (d20 + d24);
      paramArrayOfDouble1[(i10 + 3)] = (d21 + d25);
      paramArrayOfDouble1[i6] = (d12 - d16);
      paramArrayOfDouble1[(i6 + 1)] = (d13 - d17);
      paramArrayOfDouble1[(i6 + 2)] = (d20 - d24);
      paramArrayOfDouble1[(i6 + 3)] = (d21 - d25);
      d12 = d14 - d19;
      d13 = d15 + d18;
      paramArrayOfDouble1[i7] = (d4 * d12 - d5 * d13);
      paramArrayOfDouble1[(i7 + 1)] = (d4 * d13 + d5 * d12);
      d12 = d22 - d27;
      d13 = d23 + d26;
      paramArrayOfDouble1[(i7 + 2)] = (d8 * d12 - d9 * d13);
      paramArrayOfDouble1[(i7 + 3)] = (d8 * d13 + d9 * d12);
      d12 = d14 + d19;
      d13 = d15 - d18;
      paramArrayOfDouble1[i8] = (d6 * d12 + d7 * d13);
      paramArrayOfDouble1[(i8 + 1)] = (d6 * d13 - d7 * d12);
      d12 = d22 + d27;
      d13 = d23 - d26;
      paramArrayOfDouble1[(i8 + 2)] = (d10 * d12 + d11 * d13);
      paramArrayOfDouble1[(i8 + 3)] = (d10 * d13 - d11 * d12);
      j = i3 - i;
      k = j + i3;
      m = k + i3;
      i1 = m + i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      d12 = paramArrayOfDouble1[i5] + paramArrayOfDouble1[i7];
      d13 = paramArrayOfDouble1[(i5 + 1)] + paramArrayOfDouble1[(i7 + 1)];
      d14 = paramArrayOfDouble1[i5] - paramArrayOfDouble1[i7];
      d15 = paramArrayOfDouble1[(i5 + 1)] - paramArrayOfDouble1[(i7 + 1)];
      d20 = paramArrayOfDouble1[(i5 - 2)] + paramArrayOfDouble1[(i7 - 2)];
      d21 = paramArrayOfDouble1[(i5 - 1)] + paramArrayOfDouble1[(i7 - 1)];
      d22 = paramArrayOfDouble1[(i5 - 2)] - paramArrayOfDouble1[(i7 - 2)];
      d23 = paramArrayOfDouble1[(i5 - 1)] - paramArrayOfDouble1[(i7 - 1)];
      d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
      d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
      d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
      d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
      d24 = paramArrayOfDouble1[(i6 - 2)] + paramArrayOfDouble1[(i8 - 2)];
      d25 = paramArrayOfDouble1[(i6 - 1)] + paramArrayOfDouble1[(i8 - 1)];
      d26 = paramArrayOfDouble1[(i6 - 2)] - paramArrayOfDouble1[(i8 - 2)];
      d27 = paramArrayOfDouble1[(i6 - 1)] - paramArrayOfDouble1[(i8 - 1)];
      paramArrayOfDouble1[i5] = (d12 + d16);
      paramArrayOfDouble1[(i5 + 1)] = (d13 + d17);
      paramArrayOfDouble1[(i5 - 2)] = (d20 + d24);
      paramArrayOfDouble1[(i5 - 1)] = (d21 + d25);
      paramArrayOfDouble1[i6] = (d12 - d16);
      paramArrayOfDouble1[(i6 + 1)] = (d13 - d17);
      paramArrayOfDouble1[(i6 - 2)] = (d20 - d24);
      paramArrayOfDouble1[(i6 - 1)] = (d21 - d25);
      d12 = d14 - d19;
      d13 = d15 + d18;
      paramArrayOfDouble1[i7] = (d5 * d12 - d4 * d13);
      paramArrayOfDouble1[(i7 + 1)] = (d5 * d13 + d4 * d12);
      d12 = d22 - d27;
      d13 = d23 + d26;
      paramArrayOfDouble1[(i7 - 2)] = (d9 * d12 - d8 * d13);
      paramArrayOfDouble1[(i7 - 1)] = (d9 * d13 + d8 * d12);
      d12 = d14 + d19;
      d13 = d15 - d18;
      paramArrayOfDouble1[i8] = (d7 * d12 + d6 * d13);
      paramArrayOfDouble1[(i8 + 1)] = (d7 * d13 - d6 * d12);
      d12 = d22 + d27;
      d13 = d23 - d26;
      paramArrayOfDouble1[(paramInt2 + i1 - 2)] = (d11 * d12 + d10 * d13);
      paramArrayOfDouble1[(paramInt2 + i1 - 1)] = (d11 * d13 - d10 * d12);
    }
    double d4 = d2 * (d8 + d1);
    double d5 = d2 * (d9 + d1);
    double d6 = d3 * (d10 - d1);
    double d7 = d3 * (d11 - d1);
    int j = i4;
    k = j + i3;
    m = k + i3;
    i1 = m + i3;
    int i5 = paramInt2 + j;
    i6 = paramInt2 + k;
    i7 = paramInt2 + m;
    i8 = paramInt2 + i1;
    d12 = paramArrayOfDouble1[(i5 - 2)] + paramArrayOfDouble1[(i7 - 2)];
    d13 = paramArrayOfDouble1[(i5 - 1)] + paramArrayOfDouble1[(i7 - 1)];
    d14 = paramArrayOfDouble1[(i5 - 2)] - paramArrayOfDouble1[(i7 - 2)];
    d15 = paramArrayOfDouble1[(i5 - 1)] - paramArrayOfDouble1[(i7 - 1)];
    d16 = paramArrayOfDouble1[(i6 - 2)] + paramArrayOfDouble1[(i8 - 2)];
    d17 = paramArrayOfDouble1[(i6 - 1)] + paramArrayOfDouble1[(i8 - 1)];
    d18 = paramArrayOfDouble1[(i6 - 2)] - paramArrayOfDouble1[(i8 - 2)];
    d19 = paramArrayOfDouble1[(i6 - 1)] - paramArrayOfDouble1[(i8 - 1)];
    paramArrayOfDouble1[(i5 - 2)] = (d12 + d16);
    paramArrayOfDouble1[(i5 - 1)] = (d13 + d17);
    paramArrayOfDouble1[(i6 - 2)] = (d12 - d16);
    paramArrayOfDouble1[(i6 - 1)] = (d13 - d17);
    d12 = d14 - d19;
    d13 = d15 + d18;
    paramArrayOfDouble1[(i7 - 2)] = (d4 * d12 - d5 * d13);
    paramArrayOfDouble1[(i7 - 1)] = (d4 * d13 + d5 * d12);
    d12 = d14 + d19;
    d13 = d15 - d18;
    paramArrayOfDouble1[(i8 - 2)] = (d6 * d12 + d7 * d13);
    paramArrayOfDouble1[(i8 - 1)] = (d6 * d13 - d7 * d12);
    d12 = paramArrayOfDouble1[i5] + paramArrayOfDouble1[i7];
    d13 = paramArrayOfDouble1[(i5 + 1)] + paramArrayOfDouble1[(i7 + 1)];
    d14 = paramArrayOfDouble1[i5] - paramArrayOfDouble1[i7];
    d15 = paramArrayOfDouble1[(i5 + 1)] - paramArrayOfDouble1[(i7 + 1)];
    d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
    d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
    d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
    d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
    paramArrayOfDouble1[i5] = (d12 + d16);
    paramArrayOfDouble1[(i5 + 1)] = (d13 + d17);
    paramArrayOfDouble1[i6] = (d12 - d16);
    paramArrayOfDouble1[(i6 + 1)] = (d13 - d17);
    d12 = d14 - d19;
    d13 = d15 + d18;
    paramArrayOfDouble1[i7] = (d1 * (d12 - d13));
    paramArrayOfDouble1[(i7 + 1)] = (d1 * (d13 + d12));
    d12 = d14 + d19;
    d13 = d15 - d18;
    paramArrayOfDouble1[i8] = (-d1 * (d12 + d13));
    paramArrayOfDouble1[(i8 + 1)] = (-d1 * (d13 - d12));
    d12 = paramArrayOfDouble1[(i5 + 2)] + paramArrayOfDouble1[(i7 + 2)];
    d13 = paramArrayOfDouble1[(i5 + 3)] + paramArrayOfDouble1[(i7 + 3)];
    d14 = paramArrayOfDouble1[(i5 + 2)] - paramArrayOfDouble1[(i7 + 2)];
    d15 = paramArrayOfDouble1[(i5 + 3)] - paramArrayOfDouble1[(i7 + 3)];
    d16 = paramArrayOfDouble1[(i6 + 2)] + paramArrayOfDouble1[(i8 + 2)];
    d17 = paramArrayOfDouble1[(i6 + 3)] + paramArrayOfDouble1[(i8 + 3)];
    d18 = paramArrayOfDouble1[(i6 + 2)] - paramArrayOfDouble1[(i8 + 2)];
    d19 = paramArrayOfDouble1[(i6 + 3)] - paramArrayOfDouble1[(i8 + 3)];
    paramArrayOfDouble1[(i5 + 2)] = (d12 + d16);
    paramArrayOfDouble1[(i5 + 3)] = (d13 + d17);
    paramArrayOfDouble1[(i6 + 2)] = (d12 - d16);
    paramArrayOfDouble1[(i6 + 3)] = (d13 - d17);
    d12 = d14 - d19;
    d13 = d15 + d18;
    paramArrayOfDouble1[(i7 + 2)] = (d5 * d12 - d4 * d13);
    paramArrayOfDouble1[(i7 + 3)] = (d5 * d13 + d4 * d12);
    d12 = d14 + d19;
    d13 = d15 - d18;
    paramArrayOfDouble1[(i8 + 2)] = (d7 * d12 + d6 * d13);
    paramArrayOfDouble1[(i8 + 3)] = (d7 * d13 - d6 * d12);
  }

  private void cftb1st(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3)
  {
    int i4 = paramInt1 >> 3;
    int i3 = 2 * i4;
    int k = i3;
    int m = k + i3;
    int i1 = m + i3;
    int i6 = paramInt2 + k;
    int i7 = paramInt2 + m;
    int i8 = paramInt2 + i1;
    double d12 = paramArrayOfDouble1[paramInt2] + paramArrayOfDouble1[i7];
    double d13 = -paramArrayOfDouble1[(paramInt2 + 1)] - paramArrayOfDouble1[(i7 + 1)];
    double d14 = paramArrayOfDouble1[paramInt2] - paramArrayOfDouble1[i7];
    double d15 = -paramArrayOfDouble1[(paramInt2 + 1)] + paramArrayOfDouble1[(i7 + 1)];
    double d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
    double d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
    double d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
    double d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
    paramArrayOfDouble1[paramInt2] = (d12 + d16);
    paramArrayOfDouble1[(paramInt2 + 1)] = (d13 - d17);
    paramArrayOfDouble1[i6] = (d12 - d16);
    paramArrayOfDouble1[(i6 + 1)] = (d13 + d17);
    paramArrayOfDouble1[i7] = (d14 + d19);
    paramArrayOfDouble1[(i7 + 1)] = (d15 + d18);
    paramArrayOfDouble1[i8] = (d14 - d19);
    paramArrayOfDouble1[(i8 + 1)] = (d15 - d18);
    double d1 = paramArrayOfDouble2[(paramInt3 + 1)];
    double d2 = paramArrayOfDouble2[(paramInt3 + 2)];
    double d3 = paramArrayOfDouble2[(paramInt3 + 3)];
    double d8 = 1.0D;
    double d9 = 0.0D;
    double d10 = 1.0D;
    double d11 = 0.0D;
    int i2 = 0;
    for (int i = 2; i < i4 - 2; i += 4)
    {
      i2 += 4;
      int i9 = paramInt3 + i2;
      d4 = d2 * (d8 + paramArrayOfDouble2[i9]);
      d5 = d2 * (d9 + paramArrayOfDouble2[(i9 + 1)]);
      d6 = d3 * (d10 + paramArrayOfDouble2[(i9 + 2)]);
      d7 = d3 * (d11 + paramArrayOfDouble2[(i9 + 3)]);
      d8 = paramArrayOfDouble2[i9];
      d9 = paramArrayOfDouble2[(i9 + 1)];
      d10 = paramArrayOfDouble2[(i9 + 2)];
      d11 = paramArrayOfDouble2[(i9 + 3)];
      k = i + i3;
      m = k + i3;
      i1 = m + i3;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      int i10 = paramInt2 + i;
      d12 = paramArrayOfDouble1[i10] + paramArrayOfDouble1[i7];
      d13 = -paramArrayOfDouble1[(i10 + 1)] - paramArrayOfDouble1[(i7 + 1)];
      d14 = paramArrayOfDouble1[i10] - paramArrayOfDouble1[(paramInt2 + m)];
      d15 = -paramArrayOfDouble1[(i10 + 1)] + paramArrayOfDouble1[(i7 + 1)];
      double d20 = paramArrayOfDouble1[(i10 + 2)] + paramArrayOfDouble1[(i7 + 2)];
      double d21 = -paramArrayOfDouble1[(i10 + 3)] - paramArrayOfDouble1[(i7 + 3)];
      double d22 = paramArrayOfDouble1[(i10 + 2)] - paramArrayOfDouble1[(i7 + 2)];
      double d23 = -paramArrayOfDouble1[(i10 + 3)] + paramArrayOfDouble1[(i7 + 3)];
      d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
      d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
      d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
      d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
      double d24 = paramArrayOfDouble1[(i6 + 2)] + paramArrayOfDouble1[(i8 + 2)];
      double d25 = paramArrayOfDouble1[(i6 + 3)] + paramArrayOfDouble1[(i8 + 3)];
      double d26 = paramArrayOfDouble1[(i6 + 2)] - paramArrayOfDouble1[(i8 + 2)];
      double d27 = paramArrayOfDouble1[(i6 + 3)] - paramArrayOfDouble1[(i8 + 3)];
      paramArrayOfDouble1[i10] = (d12 + d16);
      paramArrayOfDouble1[(i10 + 1)] = (d13 - d17);
      paramArrayOfDouble1[(i10 + 2)] = (d20 + d24);
      paramArrayOfDouble1[(i10 + 3)] = (d21 - d25);
      paramArrayOfDouble1[i6] = (d12 - d16);
      paramArrayOfDouble1[(i6 + 1)] = (d13 + d17);
      paramArrayOfDouble1[(i6 + 2)] = (d20 - d24);
      paramArrayOfDouble1[(i6 + 3)] = (d21 + d25);
      d12 = d14 + d19;
      d13 = d15 + d18;
      paramArrayOfDouble1[i7] = (d4 * d12 - d5 * d13);
      paramArrayOfDouble1[(i7 + 1)] = (d4 * d13 + d5 * d12);
      d12 = d22 + d27;
      d13 = d23 + d26;
      paramArrayOfDouble1[(i7 + 2)] = (d8 * d12 - d9 * d13);
      paramArrayOfDouble1[(i7 + 3)] = (d8 * d13 + d9 * d12);
      d12 = d14 - d19;
      d13 = d15 - d18;
      paramArrayOfDouble1[i8] = (d6 * d12 + d7 * d13);
      paramArrayOfDouble1[(i8 + 1)] = (d6 * d13 - d7 * d12);
      d12 = d22 - d27;
      d13 = d23 - d26;
      paramArrayOfDouble1[(i8 + 2)] = (d10 * d12 + d11 * d13);
      paramArrayOfDouble1[(i8 + 3)] = (d10 * d13 - d11 * d12);
      j = i3 - i;
      k = j + i3;
      m = k + i3;
      i1 = m + i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      d12 = paramArrayOfDouble1[i5] + paramArrayOfDouble1[i7];
      d13 = -paramArrayOfDouble1[(i5 + 1)] - paramArrayOfDouble1[(i7 + 1)];
      d14 = paramArrayOfDouble1[i5] - paramArrayOfDouble1[i7];
      d15 = -paramArrayOfDouble1[(i5 + 1)] + paramArrayOfDouble1[(i7 + 1)];
      d20 = paramArrayOfDouble1[(i5 - 2)] + paramArrayOfDouble1[(i7 - 2)];
      d21 = -paramArrayOfDouble1[(i5 - 1)] - paramArrayOfDouble1[(i7 - 1)];
      d22 = paramArrayOfDouble1[(i5 - 2)] - paramArrayOfDouble1[(i7 - 2)];
      d23 = -paramArrayOfDouble1[(i5 - 1)] + paramArrayOfDouble1[(i7 - 1)];
      d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
      d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
      d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
      d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
      d24 = paramArrayOfDouble1[(i6 - 2)] + paramArrayOfDouble1[(i8 - 2)];
      d25 = paramArrayOfDouble1[(i6 - 1)] + paramArrayOfDouble1[(i8 - 1)];
      d26 = paramArrayOfDouble1[(i6 - 2)] - paramArrayOfDouble1[(i8 - 2)];
      d27 = paramArrayOfDouble1[(i6 - 1)] - paramArrayOfDouble1[(i8 - 1)];
      paramArrayOfDouble1[i5] = (d12 + d16);
      paramArrayOfDouble1[(i5 + 1)] = (d13 - d17);
      paramArrayOfDouble1[(i5 - 2)] = (d20 + d24);
      paramArrayOfDouble1[(i5 - 1)] = (d21 - d25);
      paramArrayOfDouble1[i6] = (d12 - d16);
      paramArrayOfDouble1[(i6 + 1)] = (d13 + d17);
      paramArrayOfDouble1[(i6 - 2)] = (d20 - d24);
      paramArrayOfDouble1[(i6 - 1)] = (d21 + d25);
      d12 = d14 + d19;
      d13 = d15 + d18;
      paramArrayOfDouble1[i7] = (d5 * d12 - d4 * d13);
      paramArrayOfDouble1[(i7 + 1)] = (d5 * d13 + d4 * d12);
      d12 = d22 + d27;
      d13 = d23 + d26;
      paramArrayOfDouble1[(i7 - 2)] = (d9 * d12 - d8 * d13);
      paramArrayOfDouble1[(i7 - 1)] = (d9 * d13 + d8 * d12);
      d12 = d14 - d19;
      d13 = d15 - d18;
      paramArrayOfDouble1[i8] = (d7 * d12 + d6 * d13);
      paramArrayOfDouble1[(i8 + 1)] = (d7 * d13 - d6 * d12);
      d12 = d22 - d27;
      d13 = d23 - d26;
      paramArrayOfDouble1[(i8 - 2)] = (d11 * d12 + d10 * d13);
      paramArrayOfDouble1[(i8 - 1)] = (d11 * d13 - d10 * d12);
    }
    double d4 = d2 * (d8 + d1);
    double d5 = d2 * (d9 + d1);
    double d6 = d3 * (d10 - d1);
    double d7 = d3 * (d11 - d1);
    int j = i4;
    k = j + i3;
    m = k + i3;
    i1 = m + i3;
    int i5 = paramInt2 + j;
    i6 = paramInt2 + k;
    i7 = paramInt2 + m;
    i8 = paramInt2 + i1;
    d12 = paramArrayOfDouble1[(i5 - 2)] + paramArrayOfDouble1[(i7 - 2)];
    d13 = -paramArrayOfDouble1[(i5 - 1)] - paramArrayOfDouble1[(i7 - 1)];
    d14 = paramArrayOfDouble1[(i5 - 2)] - paramArrayOfDouble1[(i7 - 2)];
    d15 = -paramArrayOfDouble1[(i5 - 1)] + paramArrayOfDouble1[(i7 - 1)];
    d16 = paramArrayOfDouble1[(i6 - 2)] + paramArrayOfDouble1[(i8 - 2)];
    d17 = paramArrayOfDouble1[(i6 - 1)] + paramArrayOfDouble1[(i8 - 1)];
    d18 = paramArrayOfDouble1[(i6 - 2)] - paramArrayOfDouble1[(i8 - 2)];
    d19 = paramArrayOfDouble1[(i6 - 1)] - paramArrayOfDouble1[(i8 - 1)];
    paramArrayOfDouble1[(i5 - 2)] = (d12 + d16);
    paramArrayOfDouble1[(i5 - 1)] = (d13 - d17);
    paramArrayOfDouble1[(i6 - 2)] = (d12 - d16);
    paramArrayOfDouble1[(i6 - 1)] = (d13 + d17);
    d12 = d14 + d19;
    d13 = d15 + d18;
    paramArrayOfDouble1[(i7 - 2)] = (d4 * d12 - d5 * d13);
    paramArrayOfDouble1[(i7 - 1)] = (d4 * d13 + d5 * d12);
    d12 = d14 - d19;
    d13 = d15 - d18;
    paramArrayOfDouble1[(i8 - 2)] = (d6 * d12 + d7 * d13);
    paramArrayOfDouble1[(i8 - 1)] = (d6 * d13 - d7 * d12);
    d12 = paramArrayOfDouble1[i5] + paramArrayOfDouble1[i7];
    d13 = -paramArrayOfDouble1[(i5 + 1)] - paramArrayOfDouble1[(i7 + 1)];
    d14 = paramArrayOfDouble1[i5] - paramArrayOfDouble1[i7];
    d15 = -paramArrayOfDouble1[(i5 + 1)] + paramArrayOfDouble1[(i7 + 1)];
    d16 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
    d17 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
    d18 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
    d19 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
    paramArrayOfDouble1[i5] = (d12 + d16);
    paramArrayOfDouble1[(i5 + 1)] = (d13 - d17);
    paramArrayOfDouble1[i6] = (d12 - d16);
    paramArrayOfDouble1[(i6 + 1)] = (d13 + d17);
    d12 = d14 + d19;
    d13 = d15 + d18;
    paramArrayOfDouble1[i7] = (d1 * (d12 - d13));
    paramArrayOfDouble1[(i7 + 1)] = (d1 * (d13 + d12));
    d12 = d14 - d19;
    d13 = d15 - d18;
    paramArrayOfDouble1[i8] = (-d1 * (d12 + d13));
    paramArrayOfDouble1[(i8 + 1)] = (-d1 * (d13 - d12));
    d12 = paramArrayOfDouble1[(i5 + 2)] + paramArrayOfDouble1[(i7 + 2)];
    d13 = -paramArrayOfDouble1[(i5 + 3)] - paramArrayOfDouble1[(i7 + 3)];
    d14 = paramArrayOfDouble1[(i5 + 2)] - paramArrayOfDouble1[(i7 + 2)];
    d15 = -paramArrayOfDouble1[(i5 + 3)] + paramArrayOfDouble1[(i7 + 3)];
    d16 = paramArrayOfDouble1[(i6 + 2)] + paramArrayOfDouble1[(i8 + 2)];
    d17 = paramArrayOfDouble1[(i6 + 3)] + paramArrayOfDouble1[(i8 + 3)];
    d18 = paramArrayOfDouble1[(i6 + 2)] - paramArrayOfDouble1[(i8 + 2)];
    d19 = paramArrayOfDouble1[(i6 + 3)] - paramArrayOfDouble1[(i8 + 3)];
    paramArrayOfDouble1[(i5 + 2)] = (d12 + d16);
    paramArrayOfDouble1[(i5 + 3)] = (d13 - d17);
    paramArrayOfDouble1[(i6 + 2)] = (d12 - d16);
    paramArrayOfDouble1[(i6 + 3)] = (d13 + d17);
    d12 = d14 + d19;
    d13 = d15 + d18;
    paramArrayOfDouble1[(i7 + 2)] = (d5 * d12 - d4 * d13);
    paramArrayOfDouble1[(i7 + 3)] = (d5 * d13 + d4 * d12);
    d12 = d14 - d19;
    d13 = d15 - d18;
    paramArrayOfDouble1[(i8 + 2)] = (d7 * d12 + d6 * d13);
    paramArrayOfDouble1[(i8 + 3)] = (d7 * d13 - d6 * d12);
  }

  private void cftrec4_th(final int paramInt1, final double[] paramArrayOfDouble1, int paramInt2, final int paramInt3, final double[] paramArrayOfDouble2)
  {
    int i1 = 0;
    int m = 2;
    int j = 0;
    int k = paramInt1 >> 1;
    if (paramInt1 > ConcurrencyUtils.getThreadsBeginN_1D_FFT_4Threads())
    {
      m = 4;
      j = 1;
      k >>= 1;
    }
    Future[] arrayOfFuture = new Future[m];
    final int i2 = k;
    final int i3;
    for (int i = 0; i < m; i++)
    {
      i3 = paramInt2 + i * k;
      if (i != j)
        arrayOfFuture[(i1++)] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            int n = i3 + i2;
            int m = paramInt1;
            while (m > 512)
            {
              m >>= 2;
              DoubleFFT_1D.this.cftmdl1(m, paramArrayOfDouble1, n - m, paramArrayOfDouble2, paramInt3 - (m >> 1));
            }
            DoubleFFT_1D.this.cftleaf(m, 1, paramArrayOfDouble1, n - m, paramInt3, paramArrayOfDouble2);
            int k = 0;
            int i1 = i3 - m;
            int j = i2 - m;
            while (j > 0)
            {
              k++;
              int i = DoubleFFT_1D.this.cfttree(m, j, k, paramArrayOfDouble1, i3, paramInt3, paramArrayOfDouble2);
              DoubleFFT_1D.this.cftleaf(m, i, paramArrayOfDouble1, i1 + j, paramInt3, paramArrayOfDouble2);
              j -= m;
            }
          }
        });
      else
        arrayOfFuture[(i1++)] = ConcurrencyUtils.threadPool.submit(new Runnable()
        {
          public void run()
          {
            int n = i3 + i2;
            int k = 1;
            int m = paramInt1;
            while (m > 512)
            {
              m >>= 2;
              k <<= 2;
              DoubleFFT_1D.this.cftmdl2(m, paramArrayOfDouble1, n - m, paramArrayOfDouble2, paramInt3 - m);
            }
            DoubleFFT_1D.this.cftleaf(m, 0, paramArrayOfDouble1, n - m, paramInt3, paramArrayOfDouble2);
            k >>= 1;
            int i1 = i3 - m;
            int j = i2 - m;
            while (j > 0)
            {
              k++;
              int i = DoubleFFT_1D.this.cfttree(m, j, k, paramArrayOfDouble1, i3, paramInt3, paramArrayOfDouble2);
              DoubleFFT_1D.this.cftleaf(m, i, paramArrayOfDouble1, i1 + j, paramInt3, paramArrayOfDouble2);
              j -= m;
            }
          }
        });
    }
    try
    {
      for (i3 = 0; i3 < m; i3++)
        arrayOfFuture[i3].get();
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

  private void cftrec4(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2)
  {
    int m = paramInt1;
    int i1 = paramInt2 + paramInt1;
    while (m > 512)
    {
      m >>= 2;
      cftmdl1(m, paramArrayOfDouble1, i1 - m, paramArrayOfDouble2, paramInt3 - (m >> 1));
    }
    cftleaf(m, 1, paramArrayOfDouble1, i1 - m, paramInt3, paramArrayOfDouble2);
    int k = 0;
    int i2 = paramInt2 - m;
    int j = paramInt1 - m;
    while (j > 0)
    {
      k++;
      int i = cfttree(m, j, k, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
      cftleaf(m, i, paramArrayOfDouble1, i2 + j, paramInt3, paramArrayOfDouble2);
      j -= m;
    }
  }

  private int cfttree(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, int paramInt4, int paramInt5, double[] paramArrayOfDouble2)
  {
    int m = paramInt4 - paramInt1;
    int j;
    if ((paramInt3 & 0x3) != 0)
    {
      j = paramInt3 & 0x1;
      if (j != 0)
        cftmdl1(paramInt1, paramArrayOfDouble1, m + paramInt2, paramArrayOfDouble2, paramInt5 - (paramInt1 >> 1));
      else
        cftmdl2(paramInt1, paramArrayOfDouble1, m + paramInt2, paramArrayOfDouble2, paramInt5 - paramInt1);
    }
    else
    {
      int k = paramInt1;
      int i = paramInt3;
      while ((i & 0x3) == 0)
      {
        k <<= 2;
        i >>= 2;
      }
      j = i & 0x1;
      int i1 = paramInt4 + paramInt2;
      if (j != 0)
        while (k > 128)
        {
          cftmdl1(k, paramArrayOfDouble1, i1 - k, paramArrayOfDouble2, paramInt5 - (k >> 1));
          k >>= 2;
        }
      while (k > 128)
      {
        cftmdl2(k, paramArrayOfDouble1, i1 - k, paramArrayOfDouble2, paramInt5 - k);
        k >>= 2;
      }
    }
    return j;
  }

  private void cftleaf(int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2)
  {
    if (paramInt1 == 512)
    {
      cftmdl1(128, paramArrayOfDouble1, paramInt3, paramArrayOfDouble2, paramInt4 - 64);
      cftf161(paramArrayOfDouble1, paramInt3, paramArrayOfDouble2, paramInt4 - 8);
      cftf162(paramArrayOfDouble1, paramInt3 + 32, paramArrayOfDouble2, paramInt4 - 32);
      cftf161(paramArrayOfDouble1, paramInt3 + 64, paramArrayOfDouble2, paramInt4 - 8);
      cftf161(paramArrayOfDouble1, paramInt3 + 96, paramArrayOfDouble2, paramInt4 - 8);
      cftmdl2(128, paramArrayOfDouble1, paramInt3 + 128, paramArrayOfDouble2, paramInt4 - 128);
      cftf161(paramArrayOfDouble1, paramInt3 + 128, paramArrayOfDouble2, paramInt4 - 8);
      cftf162(paramArrayOfDouble1, paramInt3 + 160, paramArrayOfDouble2, paramInt4 - 32);
      cftf161(paramArrayOfDouble1, paramInt3 + 192, paramArrayOfDouble2, paramInt4 - 8);
      cftf162(paramArrayOfDouble1, paramInt3 + 224, paramArrayOfDouble2, paramInt4 - 32);
      cftmdl1(128, paramArrayOfDouble1, paramInt3 + 256, paramArrayOfDouble2, paramInt4 - 64);
      cftf161(paramArrayOfDouble1, paramInt3 + 256, paramArrayOfDouble2, paramInt4 - 8);
      cftf162(paramArrayOfDouble1, paramInt3 + 288, paramArrayOfDouble2, paramInt4 - 32);
      cftf161(paramArrayOfDouble1, paramInt3 + 320, paramArrayOfDouble2, paramInt4 - 8);
      cftf161(paramArrayOfDouble1, paramInt3 + 352, paramArrayOfDouble2, paramInt4 - 8);
      if (paramInt2 != 0)
      {
        cftmdl1(128, paramArrayOfDouble1, paramInt3 + 384, paramArrayOfDouble2, paramInt4 - 64);
        cftf161(paramArrayOfDouble1, paramInt3 + 480, paramArrayOfDouble2, paramInt4 - 8);
      }
      else
      {
        cftmdl2(128, paramArrayOfDouble1, paramInt3 + 384, paramArrayOfDouble2, paramInt4 - 128);
        cftf162(paramArrayOfDouble1, paramInt3 + 480, paramArrayOfDouble2, paramInt4 - 32);
      }
      cftf161(paramArrayOfDouble1, paramInt3 + 384, paramArrayOfDouble2, paramInt4 - 8);
      cftf162(paramArrayOfDouble1, paramInt3 + 416, paramArrayOfDouble2, paramInt4 - 32);
      cftf161(paramArrayOfDouble1, paramInt3 + 448, paramArrayOfDouble2, paramInt4 - 8);
    }
    else
    {
      cftmdl1(64, paramArrayOfDouble1, paramInt3, paramArrayOfDouble2, paramInt4 - 32);
      cftf081(paramArrayOfDouble1, paramInt3, paramArrayOfDouble2, paramInt4 - 8);
      cftf082(paramArrayOfDouble1, paramInt3 + 16, paramArrayOfDouble2, paramInt4 - 8);
      cftf081(paramArrayOfDouble1, paramInt3 + 32, paramArrayOfDouble2, paramInt4 - 8);
      cftf081(paramArrayOfDouble1, paramInt3 + 48, paramArrayOfDouble2, paramInt4 - 8);
      cftmdl2(64, paramArrayOfDouble1, paramInt3 + 64, paramArrayOfDouble2, paramInt4 - 64);
      cftf081(paramArrayOfDouble1, paramInt3 + 64, paramArrayOfDouble2, paramInt4 - 8);
      cftf082(paramArrayOfDouble1, paramInt3 + 80, paramArrayOfDouble2, paramInt4 - 8);
      cftf081(paramArrayOfDouble1, paramInt3 + 96, paramArrayOfDouble2, paramInt4 - 8);
      cftf082(paramArrayOfDouble1, paramInt3 + 112, paramArrayOfDouble2, paramInt4 - 8);
      cftmdl1(64, paramArrayOfDouble1, paramInt3 + 128, paramArrayOfDouble2, paramInt4 - 32);
      cftf081(paramArrayOfDouble1, paramInt3 + 128, paramArrayOfDouble2, paramInt4 - 8);
      cftf082(paramArrayOfDouble1, paramInt3 + 144, paramArrayOfDouble2, paramInt4 - 8);
      cftf081(paramArrayOfDouble1, paramInt3 + 160, paramArrayOfDouble2, paramInt4 - 8);
      cftf081(paramArrayOfDouble1, paramInt3 + 176, paramArrayOfDouble2, paramInt4 - 8);
      if (paramInt2 != 0)
      {
        cftmdl1(64, paramArrayOfDouble1, paramInt3 + 192, paramArrayOfDouble2, paramInt4 - 32);
        cftf081(paramArrayOfDouble1, paramInt3 + 240, paramArrayOfDouble2, paramInt4 - 8);
      }
      else
      {
        cftmdl2(64, paramArrayOfDouble1, paramInt3 + 192, paramArrayOfDouble2, paramInt4 - 64);
        cftf082(paramArrayOfDouble1, paramInt3 + 240, paramArrayOfDouble2, paramInt4 - 8);
      }
      cftf081(paramArrayOfDouble1, paramInt3 + 192, paramArrayOfDouble2, paramInt4 - 8);
      cftf082(paramArrayOfDouble1, paramInt3 + 208, paramArrayOfDouble2, paramInt4 - 8);
      cftf081(paramArrayOfDouble1, paramInt3 + 224, paramArrayOfDouble2, paramInt4 - 8);
    }
  }

  private void cftmdl1(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3)
  {
    int i4 = paramInt1 >> 3;
    int i3 = 2 * i4;
    int k = i3;
    int m = k + i3;
    int i1 = m + i3;
    int i6 = paramInt2 + k;
    int i7 = paramInt2 + m;
    int i8 = paramInt2 + i1;
    double d6 = paramArrayOfDouble1[paramInt2] + paramArrayOfDouble1[i7];
    double d7 = paramArrayOfDouble1[(paramInt2 + 1)] + paramArrayOfDouble1[(i7 + 1)];
    double d8 = paramArrayOfDouble1[paramInt2] - paramArrayOfDouble1[i7];
    double d9 = paramArrayOfDouble1[(paramInt2 + 1)] - paramArrayOfDouble1[(i7 + 1)];
    double d10 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
    double d11 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
    double d12 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
    double d13 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
    paramArrayOfDouble1[paramInt2] = (d6 + d10);
    paramArrayOfDouble1[(paramInt2 + 1)] = (d7 + d11);
    paramArrayOfDouble1[i6] = (d6 - d10);
    paramArrayOfDouble1[(i6 + 1)] = (d7 - d11);
    paramArrayOfDouble1[i7] = (d8 - d13);
    paramArrayOfDouble1[(i7 + 1)] = (d9 + d12);
    paramArrayOfDouble1[i8] = (d8 + d13);
    paramArrayOfDouble1[(i8 + 1)] = (d9 - d12);
    double d1 = paramArrayOfDouble2[(paramInt3 + 1)];
    int i2 = 0;
    for (int i = 2; i < i4; i += 2)
    {
      i2 += 4;
      int i9 = paramInt3 + i2;
      double d2 = paramArrayOfDouble2[i9];
      double d3 = paramArrayOfDouble2[(i9 + 1)];
      double d4 = paramArrayOfDouble2[(i9 + 2)];
      double d5 = paramArrayOfDouble2[(i9 + 3)];
      k = i + i3;
      m = k + i3;
      i1 = m + i3;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      int i10 = paramInt2 + i;
      d6 = paramArrayOfDouble1[i10] + paramArrayOfDouble1[i7];
      d7 = paramArrayOfDouble1[(i10 + 1)] + paramArrayOfDouble1[(i7 + 1)];
      d8 = paramArrayOfDouble1[i10] - paramArrayOfDouble1[i7];
      d9 = paramArrayOfDouble1[(i10 + 1)] - paramArrayOfDouble1[(i7 + 1)];
      d10 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
      d11 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
      d12 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
      d13 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
      paramArrayOfDouble1[i10] = (d6 + d10);
      paramArrayOfDouble1[(i10 + 1)] = (d7 + d11);
      paramArrayOfDouble1[i6] = (d6 - d10);
      paramArrayOfDouble1[(i6 + 1)] = (d7 - d11);
      d6 = d8 - d13;
      d7 = d9 + d12;
      paramArrayOfDouble1[i7] = (d2 * d6 - d3 * d7);
      paramArrayOfDouble1[(i7 + 1)] = (d2 * d7 + d3 * d6);
      d6 = d8 + d13;
      d7 = d9 - d12;
      paramArrayOfDouble1[i8] = (d4 * d6 + d5 * d7);
      paramArrayOfDouble1[(i8 + 1)] = (d4 * d7 - d5 * d6);
      j = i3 - i;
      k = j + i3;
      m = k + i3;
      i1 = m + i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      d6 = paramArrayOfDouble1[i5] + paramArrayOfDouble1[i7];
      d7 = paramArrayOfDouble1[(i5 + 1)] + paramArrayOfDouble1[(i7 + 1)];
      d8 = paramArrayOfDouble1[i5] - paramArrayOfDouble1[i7];
      d9 = paramArrayOfDouble1[(i5 + 1)] - paramArrayOfDouble1[(i7 + 1)];
      d10 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
      d11 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
      d12 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
      d13 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
      paramArrayOfDouble1[i5] = (d6 + d10);
      paramArrayOfDouble1[(i5 + 1)] = (d7 + d11);
      paramArrayOfDouble1[i6] = (d6 - d10);
      paramArrayOfDouble1[(i6 + 1)] = (d7 - d11);
      d6 = d8 - d13;
      d7 = d9 + d12;
      paramArrayOfDouble1[i7] = (d3 * d6 - d2 * d7);
      paramArrayOfDouble1[(i7 + 1)] = (d3 * d7 + d2 * d6);
      d6 = d8 + d13;
      d7 = d9 - d12;
      paramArrayOfDouble1[i8] = (d5 * d6 + d4 * d7);
      paramArrayOfDouble1[(i8 + 1)] = (d5 * d7 - d4 * d6);
    }
    int j = i4;
    k = j + i3;
    m = k + i3;
    i1 = m + i3;
    int i5 = paramInt2 + j;
    i6 = paramInt2 + k;
    i7 = paramInt2 + m;
    i8 = paramInt2 + i1;
    d6 = paramArrayOfDouble1[i5] + paramArrayOfDouble1[i7];
    d7 = paramArrayOfDouble1[(i5 + 1)] + paramArrayOfDouble1[(i7 + 1)];
    d8 = paramArrayOfDouble1[i5] - paramArrayOfDouble1[i7];
    d9 = paramArrayOfDouble1[(i5 + 1)] - paramArrayOfDouble1[(i7 + 1)];
    d10 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[i8];
    d11 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[(i8 + 1)];
    d12 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[i8];
    d13 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[(i8 + 1)];
    paramArrayOfDouble1[i5] = (d6 + d10);
    paramArrayOfDouble1[(i5 + 1)] = (d7 + d11);
    paramArrayOfDouble1[i6] = (d6 - d10);
    paramArrayOfDouble1[(i6 + 1)] = (d7 - d11);
    d6 = d8 - d13;
    d7 = d9 + d12;
    paramArrayOfDouble1[i7] = (d1 * (d6 - d7));
    paramArrayOfDouble1[(i7 + 1)] = (d1 * (d7 + d6));
    d6 = d8 + d13;
    d7 = d9 - d12;
    paramArrayOfDouble1[i8] = (-d1 * (d6 + d7));
    paramArrayOfDouble1[(i8 + 1)] = (-d1 * (d7 - d6));
  }

  private void cftmdl2(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3)
  {
    int i5 = paramInt1 >> 3;
    int i4 = 2 * i5;
    double d1 = paramArrayOfDouble2[(paramInt3 + 1)];
    int k = i4;
    int m = k + i4;
    int i1 = m + i4;
    int i7 = paramInt2 + k;
    int i8 = paramInt2 + m;
    int i9 = paramInt2 + i1;
    double d10 = paramArrayOfDouble1[paramInt2] - paramArrayOfDouble1[(i8 + 1)];
    double d11 = paramArrayOfDouble1[(paramInt2 + 1)] + paramArrayOfDouble1[i8];
    double d12 = paramArrayOfDouble1[paramInt2] + paramArrayOfDouble1[(i8 + 1)];
    double d13 = paramArrayOfDouble1[(paramInt2 + 1)] - paramArrayOfDouble1[i8];
    double d14 = paramArrayOfDouble1[i7] - paramArrayOfDouble1[(i9 + 1)];
    double d15 = paramArrayOfDouble1[(i7 + 1)] + paramArrayOfDouble1[i9];
    double d16 = paramArrayOfDouble1[i7] + paramArrayOfDouble1[(i9 + 1)];
    double d17 = paramArrayOfDouble1[(i7 + 1)] - paramArrayOfDouble1[i9];
    double d18 = d1 * (d14 - d15);
    double d19 = d1 * (d15 + d14);
    paramArrayOfDouble1[paramInt2] = (d10 + d18);
    paramArrayOfDouble1[(paramInt2 + 1)] = (d11 + d19);
    paramArrayOfDouble1[i7] = (d10 - d18);
    paramArrayOfDouble1[(i7 + 1)] = (d11 - d19);
    d18 = d1 * (d16 - d17);
    d19 = d1 * (d17 + d16);
    paramArrayOfDouble1[i8] = (d12 - d19);
    paramArrayOfDouble1[(i8 + 1)] = (d13 + d18);
    paramArrayOfDouble1[i9] = (d12 + d19);
    paramArrayOfDouble1[(i9 + 1)] = (d13 - d18);
    int i2 = 0;
    int i3 = 2 * i4;
    for (int i = 2; i < i5; i += 2)
    {
      i2 += 4;
      int i10 = paramInt3 + i2;
      d2 = paramArrayOfDouble2[i10];
      d3 = paramArrayOfDouble2[(i10 + 1)];
      double d4 = paramArrayOfDouble2[(i10 + 2)];
      double d5 = paramArrayOfDouble2[(i10 + 3)];
      i3 -= 4;
      int i11 = paramInt3 + i3;
      double d7 = paramArrayOfDouble2[i11];
      double d6 = paramArrayOfDouble2[(i11 + 1)];
      double d9 = paramArrayOfDouble2[(i11 + 2)];
      double d8 = paramArrayOfDouble2[(i11 + 3)];
      k = i + i4;
      m = k + i4;
      i1 = m + i4;
      i7 = paramInt2 + k;
      i8 = paramInt2 + m;
      i9 = paramInt2 + i1;
      int i12 = paramInt2 + i;
      d10 = paramArrayOfDouble1[i12] - paramArrayOfDouble1[(i8 + 1)];
      d11 = paramArrayOfDouble1[(i12 + 1)] + paramArrayOfDouble1[i8];
      d12 = paramArrayOfDouble1[i12] + paramArrayOfDouble1[(i8 + 1)];
      d13 = paramArrayOfDouble1[(i12 + 1)] - paramArrayOfDouble1[i8];
      d14 = paramArrayOfDouble1[i7] - paramArrayOfDouble1[(i9 + 1)];
      d15 = paramArrayOfDouble1[(i7 + 1)] + paramArrayOfDouble1[i9];
      d16 = paramArrayOfDouble1[i7] + paramArrayOfDouble1[(i9 + 1)];
      d17 = paramArrayOfDouble1[(i7 + 1)] - paramArrayOfDouble1[i9];
      d18 = d2 * d10 - d3 * d11;
      d19 = d2 * d11 + d3 * d10;
      d20 = d6 * d14 - d7 * d15;
      d21 = d6 * d15 + d7 * d14;
      paramArrayOfDouble1[i12] = (d18 + d20);
      paramArrayOfDouble1[(i12 + 1)] = (d19 + d21);
      paramArrayOfDouble1[i7] = (d18 - d20);
      paramArrayOfDouble1[(i7 + 1)] = (d19 - d21);
      d18 = d4 * d12 + d5 * d13;
      d19 = d4 * d13 - d5 * d12;
      d20 = d8 * d16 + d9 * d17;
      d21 = d8 * d17 - d9 * d16;
      paramArrayOfDouble1[i8] = (d18 + d20);
      paramArrayOfDouble1[(i8 + 1)] = (d19 + d21);
      paramArrayOfDouble1[i9] = (d18 - d20);
      paramArrayOfDouble1[(i9 + 1)] = (d19 - d21);
      j = i4 - i;
      k = j + i4;
      m = k + i4;
      i1 = m + i4;
      i6 = paramInt2 + j;
      i7 = paramInt2 + k;
      i8 = paramInt2 + m;
      i9 = paramInt2 + i1;
      d10 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[(i8 + 1)];
      d11 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[i8];
      d12 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[(i8 + 1)];
      d13 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[i8];
      d14 = paramArrayOfDouble1[i7] - paramArrayOfDouble1[(i9 + 1)];
      d15 = paramArrayOfDouble1[(i7 + 1)] + paramArrayOfDouble1[i9];
      d16 = paramArrayOfDouble1[i7] + paramArrayOfDouble1[(i9 + 1)];
      d17 = paramArrayOfDouble1[(i7 + 1)] - paramArrayOfDouble1[i9];
      d18 = d7 * d10 - d6 * d11;
      d19 = d7 * d11 + d6 * d10;
      d20 = d3 * d14 - d2 * d15;
      d21 = d3 * d15 + d2 * d14;
      paramArrayOfDouble1[i6] = (d18 + d20);
      paramArrayOfDouble1[(i6 + 1)] = (d19 + d21);
      paramArrayOfDouble1[i7] = (d18 - d20);
      paramArrayOfDouble1[(i7 + 1)] = (d19 - d21);
      d18 = d9 * d12 + d8 * d13;
      d19 = d9 * d13 - d8 * d12;
      d20 = d5 * d16 + d4 * d17;
      d21 = d5 * d17 - d4 * d16;
      paramArrayOfDouble1[i8] = (d18 + d20);
      paramArrayOfDouble1[(i8 + 1)] = (d19 + d21);
      paramArrayOfDouble1[i9] = (d18 - d20);
      paramArrayOfDouble1[(i9 + 1)] = (d19 - d21);
    }
    double d2 = paramArrayOfDouble2[(paramInt3 + i4)];
    double d3 = paramArrayOfDouble2[(paramInt3 + i4 + 1)];
    int j = i5;
    k = j + i4;
    m = k + i4;
    i1 = m + i4;
    int i6 = paramInt2 + j;
    i7 = paramInt2 + k;
    i8 = paramInt2 + m;
    i9 = paramInt2 + i1;
    d10 = paramArrayOfDouble1[i6] - paramArrayOfDouble1[(i8 + 1)];
    d11 = paramArrayOfDouble1[(i6 + 1)] + paramArrayOfDouble1[i8];
    d12 = paramArrayOfDouble1[i6] + paramArrayOfDouble1[(i8 + 1)];
    d13 = paramArrayOfDouble1[(i6 + 1)] - paramArrayOfDouble1[i8];
    d14 = paramArrayOfDouble1[i7] - paramArrayOfDouble1[(i9 + 1)];
    d15 = paramArrayOfDouble1[(i7 + 1)] + paramArrayOfDouble1[i9];
    d16 = paramArrayOfDouble1[i7] + paramArrayOfDouble1[(i9 + 1)];
    d17 = paramArrayOfDouble1[(i7 + 1)] - paramArrayOfDouble1[i9];
    d18 = d2 * d10 - d3 * d11;
    d19 = d2 * d11 + d3 * d10;
    double d20 = d3 * d14 - d2 * d15;
    double d21 = d3 * d15 + d2 * d14;
    paramArrayOfDouble1[i6] = (d18 + d20);
    paramArrayOfDouble1[(i6 + 1)] = (d19 + d21);
    paramArrayOfDouble1[i7] = (d18 - d20);
    paramArrayOfDouble1[(i7 + 1)] = (d19 - d21);
    d18 = d3 * d12 - d2 * d13;
    d19 = d3 * d13 + d2 * d12;
    d20 = d2 * d16 - d3 * d17;
    d21 = d2 * d17 + d3 * d16;
    paramArrayOfDouble1[i8] = (d18 - d20);
    paramArrayOfDouble1[(i8 + 1)] = (d19 - d21);
    paramArrayOfDouble1[i9] = (d18 + d20);
    paramArrayOfDouble1[(i9 + 1)] = (d19 + d21);
  }

  private void cftfx41(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2)
  {
    if (paramInt1 == 128)
    {
      cftf161(paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, paramInt3 - 8);
      cftf162(paramArrayOfDouble1, paramInt2 + 32, paramArrayOfDouble2, paramInt3 - 32);
      cftf161(paramArrayOfDouble1, paramInt2 + 64, paramArrayOfDouble2, paramInt3 - 8);
      cftf161(paramArrayOfDouble1, paramInt2 + 96, paramArrayOfDouble2, paramInt3 - 8);
    }
    else
    {
      cftf081(paramArrayOfDouble1, paramInt2, paramArrayOfDouble2, paramInt3 - 8);
      cftf082(paramArrayOfDouble1, paramInt2 + 16, paramArrayOfDouble2, paramInt3 - 8);
      cftf081(paramArrayOfDouble1, paramInt2 + 32, paramArrayOfDouble2, paramInt3 - 8);
      cftf081(paramArrayOfDouble1, paramInt2 + 48, paramArrayOfDouble2, paramInt3 - 8);
    }
  }

  private void cftf161(double[] paramArrayOfDouble1, int paramInt1, double[] paramArrayOfDouble2, int paramInt2)
  {
    double d1 = paramArrayOfDouble2[(paramInt2 + 1)];
    double d2 = paramArrayOfDouble2[(paramInt2 + 2)];
    double d3 = paramArrayOfDouble2[(paramInt2 + 3)];
    double d4 = paramArrayOfDouble1[paramInt1] + paramArrayOfDouble1[(paramInt1 + 16)];
    double d5 = paramArrayOfDouble1[(paramInt1 + 1)] + paramArrayOfDouble1[(paramInt1 + 17)];
    double d6 = paramArrayOfDouble1[paramInt1] - paramArrayOfDouble1[(paramInt1 + 16)];
    double d7 = paramArrayOfDouble1[(paramInt1 + 1)] - paramArrayOfDouble1[(paramInt1 + 17)];
    double d8 = paramArrayOfDouble1[(paramInt1 + 8)] + paramArrayOfDouble1[(paramInt1 + 24)];
    double d9 = paramArrayOfDouble1[(paramInt1 + 9)] + paramArrayOfDouble1[(paramInt1 + 25)];
    double d10 = paramArrayOfDouble1[(paramInt1 + 8)] - paramArrayOfDouble1[(paramInt1 + 24)];
    double d11 = paramArrayOfDouble1[(paramInt1 + 9)] - paramArrayOfDouble1[(paramInt1 + 25)];
    double d12 = d4 + d8;
    double d13 = d5 + d9;
    double d20 = d4 - d8;
    double d21 = d5 - d9;
    double d28 = d6 - d11;
    double d29 = d7 + d10;
    double d36 = d6 + d11;
    double d37 = d7 - d10;
    d4 = paramArrayOfDouble1[(paramInt1 + 2)] + paramArrayOfDouble1[(paramInt1 + 18)];
    d5 = paramArrayOfDouble1[(paramInt1 + 3)] + paramArrayOfDouble1[(paramInt1 + 19)];
    d6 = paramArrayOfDouble1[(paramInt1 + 2)] - paramArrayOfDouble1[(paramInt1 + 18)];
    d7 = paramArrayOfDouble1[(paramInt1 + 3)] - paramArrayOfDouble1[(paramInt1 + 19)];
    d8 = paramArrayOfDouble1[(paramInt1 + 10)] + paramArrayOfDouble1[(paramInt1 + 26)];
    d9 = paramArrayOfDouble1[(paramInt1 + 11)] + paramArrayOfDouble1[(paramInt1 + 27)];
    d10 = paramArrayOfDouble1[(paramInt1 + 10)] - paramArrayOfDouble1[(paramInt1 + 26)];
    d11 = paramArrayOfDouble1[(paramInt1 + 11)] - paramArrayOfDouble1[(paramInt1 + 27)];
    double d14 = d4 + d8;
    double d15 = d5 + d9;
    double d22 = d4 - d8;
    double d23 = d5 - d9;
    d4 = d6 - d11;
    d5 = d7 + d10;
    double d30 = d2 * d4 - d3 * d5;
    double d31 = d2 * d5 + d3 * d4;
    d4 = d6 + d11;
    d5 = d7 - d10;
    double d38 = d3 * d4 - d2 * d5;
    double d39 = d3 * d5 + d2 * d4;
    d4 = paramArrayOfDouble1[(paramInt1 + 4)] + paramArrayOfDouble1[(paramInt1 + 20)];
    d5 = paramArrayOfDouble1[(paramInt1 + 5)] + paramArrayOfDouble1[(paramInt1 + 21)];
    d6 = paramArrayOfDouble1[(paramInt1 + 4)] - paramArrayOfDouble1[(paramInt1 + 20)];
    d7 = paramArrayOfDouble1[(paramInt1 + 5)] - paramArrayOfDouble1[(paramInt1 + 21)];
    d8 = paramArrayOfDouble1[(paramInt1 + 12)] + paramArrayOfDouble1[(paramInt1 + 28)];
    d9 = paramArrayOfDouble1[(paramInt1 + 13)] + paramArrayOfDouble1[(paramInt1 + 29)];
    d10 = paramArrayOfDouble1[(paramInt1 + 12)] - paramArrayOfDouble1[(paramInt1 + 28)];
    d11 = paramArrayOfDouble1[(paramInt1 + 13)] - paramArrayOfDouble1[(paramInt1 + 29)];
    double d16 = d4 + d8;
    double d17 = d5 + d9;
    double d24 = d4 - d8;
    double d25 = d5 - d9;
    d4 = d6 - d11;
    d5 = d7 + d10;
    double d32 = d1 * (d4 - d5);
    double d33 = d1 * (d5 + d4);
    d4 = d6 + d11;
    d5 = d7 - d10;
    double d40 = d1 * (d4 + d5);
    double d41 = d1 * (d5 - d4);
    d4 = paramArrayOfDouble1[(paramInt1 + 6)] + paramArrayOfDouble1[(paramInt1 + 22)];
    d5 = paramArrayOfDouble1[(paramInt1 + 7)] + paramArrayOfDouble1[(paramInt1 + 23)];
    d6 = paramArrayOfDouble1[(paramInt1 + 6)] - paramArrayOfDouble1[(paramInt1 + 22)];
    d7 = paramArrayOfDouble1[(paramInt1 + 7)] - paramArrayOfDouble1[(paramInt1 + 23)];
    d8 = paramArrayOfDouble1[(paramInt1 + 14)] + paramArrayOfDouble1[(paramInt1 + 30)];
    d9 = paramArrayOfDouble1[(paramInt1 + 15)] + paramArrayOfDouble1[(paramInt1 + 31)];
    d10 = paramArrayOfDouble1[(paramInt1 + 14)] - paramArrayOfDouble1[(paramInt1 + 30)];
    d11 = paramArrayOfDouble1[(paramInt1 + 15)] - paramArrayOfDouble1[(paramInt1 + 31)];
    double d18 = d4 + d8;
    double d19 = d5 + d9;
    double d26 = d4 - d8;
    double d27 = d5 - d9;
    d4 = d6 - d11;
    d5 = d7 + d10;
    double d34 = d3 * d4 - d2 * d5;
    double d35 = d3 * d5 + d2 * d4;
    d4 = d6 + d11;
    d5 = d7 - d10;
    double d42 = d2 * d4 - d3 * d5;
    double d43 = d2 * d5 + d3 * d4;
    d4 = d36 - d40;
    d5 = d37 - d41;
    d6 = d36 + d40;
    d7 = d37 + d41;
    d8 = d38 - d42;
    d9 = d39 - d43;
    d10 = d38 + d42;
    d11 = d39 + d43;
    paramArrayOfDouble1[(paramInt1 + 24)] = (d4 + d8);
    paramArrayOfDouble1[(paramInt1 + 25)] = (d5 + d9);
    paramArrayOfDouble1[(paramInt1 + 26)] = (d4 - d8);
    paramArrayOfDouble1[(paramInt1 + 27)] = (d5 - d9);
    paramArrayOfDouble1[(paramInt1 + 28)] = (d6 - d11);
    paramArrayOfDouble1[(paramInt1 + 29)] = (d7 + d10);
    paramArrayOfDouble1[(paramInt1 + 30)] = (d6 + d11);
    paramArrayOfDouble1[(paramInt1 + 31)] = (d7 - d10);
    d4 = d28 + d32;
    d5 = d29 + d33;
    d6 = d28 - d32;
    d7 = d29 - d33;
    d8 = d30 + d34;
    d9 = d31 + d35;
    d10 = d30 - d34;
    d11 = d31 - d35;
    paramArrayOfDouble1[(paramInt1 + 16)] = (d4 + d8);
    paramArrayOfDouble1[(paramInt1 + 17)] = (d5 + d9);
    paramArrayOfDouble1[(paramInt1 + 18)] = (d4 - d8);
    paramArrayOfDouble1[(paramInt1 + 19)] = (d5 - d9);
    paramArrayOfDouble1[(paramInt1 + 20)] = (d6 - d11);
    paramArrayOfDouble1[(paramInt1 + 21)] = (d7 + d10);
    paramArrayOfDouble1[(paramInt1 + 22)] = (d6 + d11);
    paramArrayOfDouble1[(paramInt1 + 23)] = (d7 - d10);
    d4 = d22 - d27;
    d5 = d23 + d26;
    d8 = d1 * (d4 - d5);
    d9 = d1 * (d5 + d4);
    d4 = d22 + d27;
    d5 = d23 - d26;
    d10 = d1 * (d4 - d5);
    d11 = d1 * (d5 + d4);
    d4 = d20 - d25;
    d5 = d21 + d24;
    d6 = d20 + d25;
    d7 = d21 - d24;
    paramArrayOfDouble1[(paramInt1 + 8)] = (d4 + d8);
    paramArrayOfDouble1[(paramInt1 + 9)] = (d5 + d9);
    paramArrayOfDouble1[(paramInt1 + 10)] = (d4 - d8);
    paramArrayOfDouble1[(paramInt1 + 11)] = (d5 - d9);
    paramArrayOfDouble1[(paramInt1 + 12)] = (d6 - d11);
    paramArrayOfDouble1[(paramInt1 + 13)] = (d7 + d10);
    paramArrayOfDouble1[(paramInt1 + 14)] = (d6 + d11);
    paramArrayOfDouble1[(paramInt1 + 15)] = (d7 - d10);
    d4 = d12 + d16;
    d5 = d13 + d17;
    d6 = d12 - d16;
    d7 = d13 - d17;
    d8 = d14 + d18;
    d9 = d15 + d19;
    d10 = d14 - d18;
    d11 = d15 - d19;
    paramArrayOfDouble1[paramInt1] = (d4 + d8);
    paramArrayOfDouble1[(paramInt1 + 1)] = (d5 + d9);
    paramArrayOfDouble1[(paramInt1 + 2)] = (d4 - d8);
    paramArrayOfDouble1[(paramInt1 + 3)] = (d5 - d9);
    paramArrayOfDouble1[(paramInt1 + 4)] = (d6 - d11);
    paramArrayOfDouble1[(paramInt1 + 5)] = (d7 + d10);
    paramArrayOfDouble1[(paramInt1 + 6)] = (d6 + d11);
    paramArrayOfDouble1[(paramInt1 + 7)] = (d7 - d10);
  }

  private void cftf162(double[] paramArrayOfDouble1, int paramInt1, double[] paramArrayOfDouble2, int paramInt2)
  {
    double d1 = paramArrayOfDouble2[(paramInt2 + 1)];
    double d2 = paramArrayOfDouble2[(paramInt2 + 4)];
    double d3 = paramArrayOfDouble2[(paramInt2 + 5)];
    double d6 = paramArrayOfDouble2[(paramInt2 + 6)];
    double d7 = -paramArrayOfDouble2[(paramInt2 + 7)];
    double d4 = paramArrayOfDouble2[(paramInt2 + 8)];
    double d5 = paramArrayOfDouble2[(paramInt2 + 9)];
    double d10 = paramArrayOfDouble1[paramInt1] - paramArrayOfDouble1[(paramInt1 + 17)];
    double d11 = paramArrayOfDouble1[(paramInt1 + 1)] + paramArrayOfDouble1[(paramInt1 + 16)];
    double d8 = paramArrayOfDouble1[(paramInt1 + 8)] - paramArrayOfDouble1[(paramInt1 + 25)];
    double d9 = paramArrayOfDouble1[(paramInt1 + 9)] + paramArrayOfDouble1[(paramInt1 + 24)];
    double d12 = d1 * (d8 - d9);
    double d13 = d1 * (d9 + d8);
    double d14 = d10 + d12;
    double d15 = d11 + d13;
    double d22 = d10 - d12;
    double d23 = d11 - d13;
    d10 = paramArrayOfDouble1[paramInt1] + paramArrayOfDouble1[(paramInt1 + 17)];
    d11 = paramArrayOfDouble1[(paramInt1 + 1)] - paramArrayOfDouble1[(paramInt1 + 16)];
    d8 = paramArrayOfDouble1[(paramInt1 + 8)] + paramArrayOfDouble1[(paramInt1 + 25)];
    d9 = paramArrayOfDouble1[(paramInt1 + 9)] - paramArrayOfDouble1[(paramInt1 + 24)];
    d12 = d1 * (d8 - d9);
    d13 = d1 * (d9 + d8);
    double d30 = d10 - d13;
    double d31 = d11 + d12;
    double d38 = d10 + d13;
    double d39 = d11 - d12;
    d8 = paramArrayOfDouble1[(paramInt1 + 2)] - paramArrayOfDouble1[(paramInt1 + 19)];
    d9 = paramArrayOfDouble1[(paramInt1 + 3)] + paramArrayOfDouble1[(paramInt1 + 18)];
    d10 = d2 * d8 - d3 * d9;
    d11 = d2 * d9 + d3 * d8;
    d8 = paramArrayOfDouble1[(paramInt1 + 10)] - paramArrayOfDouble1[(paramInt1 + 27)];
    d9 = paramArrayOfDouble1[(paramInt1 + 11)] + paramArrayOfDouble1[(paramInt1 + 26)];
    d12 = d7 * d8 - d6 * d9;
    d13 = d7 * d9 + d6 * d8;
    double d16 = d10 + d12;
    double d17 = d11 + d13;
    double d24 = d10 - d12;
    double d25 = d11 - d13;
    d8 = paramArrayOfDouble1[(paramInt1 + 2)] + paramArrayOfDouble1[(paramInt1 + 19)];
    d9 = paramArrayOfDouble1[(paramInt1 + 3)] - paramArrayOfDouble1[(paramInt1 + 18)];
    d10 = d6 * d8 - d7 * d9;
    d11 = d6 * d9 + d7 * d8;
    d8 = paramArrayOfDouble1[(paramInt1 + 10)] + paramArrayOfDouble1[(paramInt1 + 27)];
    d9 = paramArrayOfDouble1[(paramInt1 + 11)] - paramArrayOfDouble1[(paramInt1 + 26)];
    d12 = d2 * d8 + d3 * d9;
    d13 = d2 * d9 - d3 * d8;
    double d32 = d10 - d12;
    double d33 = d11 - d13;
    double d40 = d10 + d12;
    double d41 = d11 + d13;
    d8 = paramArrayOfDouble1[(paramInt1 + 4)] - paramArrayOfDouble1[(paramInt1 + 21)];
    d9 = paramArrayOfDouble1[(paramInt1 + 5)] + paramArrayOfDouble1[(paramInt1 + 20)];
    d10 = d4 * d8 - d5 * d9;
    d11 = d4 * d9 + d5 * d8;
    d8 = paramArrayOfDouble1[(paramInt1 + 12)] - paramArrayOfDouble1[(paramInt1 + 29)];
    d9 = paramArrayOfDouble1[(paramInt1 + 13)] + paramArrayOfDouble1[(paramInt1 + 28)];
    d12 = d5 * d8 - d4 * d9;
    d13 = d5 * d9 + d4 * d8;
    double d18 = d10 + d12;
    double d19 = d11 + d13;
    double d26 = d10 - d12;
    double d27 = d11 - d13;
    d8 = paramArrayOfDouble1[(paramInt1 + 4)] + paramArrayOfDouble1[(paramInt1 + 21)];
    d9 = paramArrayOfDouble1[(paramInt1 + 5)] - paramArrayOfDouble1[(paramInt1 + 20)];
    d10 = d5 * d8 - d4 * d9;
    d11 = d5 * d9 + d4 * d8;
    d8 = paramArrayOfDouble1[(paramInt1 + 12)] + paramArrayOfDouble1[(paramInt1 + 29)];
    d9 = paramArrayOfDouble1[(paramInt1 + 13)] - paramArrayOfDouble1[(paramInt1 + 28)];
    d12 = d4 * d8 - d5 * d9;
    d13 = d4 * d9 + d5 * d8;
    double d34 = d10 - d12;
    double d35 = d11 - d13;
    double d42 = d10 + d12;
    double d43 = d11 + d13;
    d8 = paramArrayOfDouble1[(paramInt1 + 6)] - paramArrayOfDouble1[(paramInt1 + 23)];
    d9 = paramArrayOfDouble1[(paramInt1 + 7)] + paramArrayOfDouble1[(paramInt1 + 22)];
    d10 = d6 * d8 - d7 * d9;
    d11 = d6 * d9 + d7 * d8;
    d8 = paramArrayOfDouble1[(paramInt1 + 14)] - paramArrayOfDouble1[(paramInt1 + 31)];
    d9 = paramArrayOfDouble1[(paramInt1 + 15)] + paramArrayOfDouble1[(paramInt1 + 30)];
    d12 = d3 * d8 - d2 * d9;
    d13 = d3 * d9 + d2 * d8;
    double d20 = d10 + d12;
    double d21 = d11 + d13;
    double d28 = d10 - d12;
    double d29 = d11 - d13;
    d8 = paramArrayOfDouble1[(paramInt1 + 6)] + paramArrayOfDouble1[(paramInt1 + 23)];
    d9 = paramArrayOfDouble1[(paramInt1 + 7)] - paramArrayOfDouble1[(paramInt1 + 22)];
    d10 = d3 * d8 + d2 * d9;
    d11 = d3 * d9 - d2 * d8;
    d8 = paramArrayOfDouble1[(paramInt1 + 14)] + paramArrayOfDouble1[(paramInt1 + 31)];
    d9 = paramArrayOfDouble1[(paramInt1 + 15)] - paramArrayOfDouble1[(paramInt1 + 30)];
    d12 = d7 * d8 - d6 * d9;
    d13 = d7 * d9 + d6 * d8;
    double d36 = d10 + d12;
    double d37 = d11 + d13;
    double d44 = d10 - d12;
    double d45 = d11 - d13;
    d10 = d14 + d18;
    d11 = d15 + d19;
    d12 = d16 + d20;
    d13 = d17 + d21;
    paramArrayOfDouble1[paramInt1] = (d10 + d12);
    paramArrayOfDouble1[(paramInt1 + 1)] = (d11 + d13);
    paramArrayOfDouble1[(paramInt1 + 2)] = (d10 - d12);
    paramArrayOfDouble1[(paramInt1 + 3)] = (d11 - d13);
    d10 = d14 - d18;
    d11 = d15 - d19;
    d12 = d16 - d20;
    d13 = d17 - d21;
    paramArrayOfDouble1[(paramInt1 + 4)] = (d10 - d13);
    paramArrayOfDouble1[(paramInt1 + 5)] = (d11 + d12);
    paramArrayOfDouble1[(paramInt1 + 6)] = (d10 + d13);
    paramArrayOfDouble1[(paramInt1 + 7)] = (d11 - d12);
    d10 = d22 - d27;
    d11 = d23 + d26;
    d8 = d24 - d29;
    d9 = d25 + d28;
    d12 = d1 * (d8 - d9);
    d13 = d1 * (d9 + d8);
    paramArrayOfDouble1[(paramInt1 + 8)] = (d10 + d12);
    paramArrayOfDouble1[(paramInt1 + 9)] = (d11 + d13);
    paramArrayOfDouble1[(paramInt1 + 10)] = (d10 - d12);
    paramArrayOfDouble1[(paramInt1 + 11)] = (d11 - d13);
    d10 = d22 + d27;
    d11 = d23 - d26;
    d8 = d24 + d29;
    d9 = d25 - d28;
    d12 = d1 * (d8 - d9);
    d13 = d1 * (d9 + d8);
    paramArrayOfDouble1[(paramInt1 + 12)] = (d10 - d13);
    paramArrayOfDouble1[(paramInt1 + 13)] = (d11 + d12);
    paramArrayOfDouble1[(paramInt1 + 14)] = (d10 + d13);
    paramArrayOfDouble1[(paramInt1 + 15)] = (d11 - d12);
    d10 = d30 + d34;
    d11 = d31 + d35;
    d12 = d32 - d36;
    d13 = d33 - d37;
    paramArrayOfDouble1[(paramInt1 + 16)] = (d10 + d12);
    paramArrayOfDouble1[(paramInt1 + 17)] = (d11 + d13);
    paramArrayOfDouble1[(paramInt1 + 18)] = (d10 - d12);
    paramArrayOfDouble1[(paramInt1 + 19)] = (d11 - d13);
    d10 = d30 - d34;
    d11 = d31 - d35;
    d12 = d32 + d36;
    d13 = d33 + d37;
    paramArrayOfDouble1[(paramInt1 + 20)] = (d10 - d13);
    paramArrayOfDouble1[(paramInt1 + 21)] = (d11 + d12);
    paramArrayOfDouble1[(paramInt1 + 22)] = (d10 + d13);
    paramArrayOfDouble1[(paramInt1 + 23)] = (d11 - d12);
    d10 = d38 - d43;
    d11 = d39 + d42;
    d8 = d40 + d45;
    d9 = d41 - d44;
    d12 = d1 * (d8 - d9);
    d13 = d1 * (d9 + d8);
    paramArrayOfDouble1[(paramInt1 + 24)] = (d10 + d12);
    paramArrayOfDouble1[(paramInt1 + 25)] = (d11 + d13);
    paramArrayOfDouble1[(paramInt1 + 26)] = (d10 - d12);
    paramArrayOfDouble1[(paramInt1 + 27)] = (d11 - d13);
    d10 = d38 + d43;
    d11 = d39 - d42;
    d8 = d40 - d45;
    d9 = d41 + d44;
    d12 = d1 * (d8 - d9);
    d13 = d1 * (d9 + d8);
    paramArrayOfDouble1[(paramInt1 + 28)] = (d10 - d13);
    paramArrayOfDouble1[(paramInt1 + 29)] = (d11 + d12);
    paramArrayOfDouble1[(paramInt1 + 30)] = (d10 + d13);
    paramArrayOfDouble1[(paramInt1 + 31)] = (d11 - d12);
  }

  private void cftf081(double[] paramArrayOfDouble1, int paramInt1, double[] paramArrayOfDouble2, int paramInt2)
  {
    double d1 = paramArrayOfDouble2[(paramInt2 + 1)];
    double d2 = paramArrayOfDouble1[paramInt1] + paramArrayOfDouble1[(paramInt1 + 8)];
    double d3 = paramArrayOfDouble1[(paramInt1 + 1)] + paramArrayOfDouble1[(paramInt1 + 9)];
    double d4 = paramArrayOfDouble1[paramInt1] - paramArrayOfDouble1[(paramInt1 + 8)];
    double d5 = paramArrayOfDouble1[(paramInt1 + 1)] - paramArrayOfDouble1[(paramInt1 + 9)];
    double d6 = paramArrayOfDouble1[(paramInt1 + 4)] + paramArrayOfDouble1[(paramInt1 + 12)];
    double d7 = paramArrayOfDouble1[(paramInt1 + 5)] + paramArrayOfDouble1[(paramInt1 + 13)];
    double d8 = paramArrayOfDouble1[(paramInt1 + 4)] - paramArrayOfDouble1[(paramInt1 + 12)];
    double d9 = paramArrayOfDouble1[(paramInt1 + 5)] - paramArrayOfDouble1[(paramInt1 + 13)];
    double d10 = d2 + d6;
    double d11 = d3 + d7;
    double d14 = d2 - d6;
    double d15 = d3 - d7;
    double d12 = d4 - d9;
    double d13 = d5 + d8;
    double d16 = d4 + d9;
    double d17 = d5 - d8;
    d2 = paramArrayOfDouble1[(paramInt1 + 2)] + paramArrayOfDouble1[(paramInt1 + 10)];
    d3 = paramArrayOfDouble1[(paramInt1 + 3)] + paramArrayOfDouble1[(paramInt1 + 11)];
    d4 = paramArrayOfDouble1[(paramInt1 + 2)] - paramArrayOfDouble1[(paramInt1 + 10)];
    d5 = paramArrayOfDouble1[(paramInt1 + 3)] - paramArrayOfDouble1[(paramInt1 + 11)];
    d6 = paramArrayOfDouble1[(paramInt1 + 6)] + paramArrayOfDouble1[(paramInt1 + 14)];
    d7 = paramArrayOfDouble1[(paramInt1 + 7)] + paramArrayOfDouble1[(paramInt1 + 15)];
    d8 = paramArrayOfDouble1[(paramInt1 + 6)] - paramArrayOfDouble1[(paramInt1 + 14)];
    d9 = paramArrayOfDouble1[(paramInt1 + 7)] - paramArrayOfDouble1[(paramInt1 + 15)];
    double d18 = d2 + d6;
    double d19 = d3 + d7;
    double d22 = d2 - d6;
    double d23 = d3 - d7;
    d2 = d4 - d9;
    d3 = d5 + d8;
    d6 = d4 + d9;
    d7 = d5 - d8;
    double d20 = d1 * (d2 - d3);
    double d21 = d1 * (d2 + d3);
    double d24 = d1 * (d6 - d7);
    double d25 = d1 * (d6 + d7);
    paramArrayOfDouble1[(paramInt1 + 8)] = (d12 + d20);
    paramArrayOfDouble1[(paramInt1 + 9)] = (d13 + d21);
    paramArrayOfDouble1[(paramInt1 + 10)] = (d12 - d20);
    paramArrayOfDouble1[(paramInt1 + 11)] = (d13 - d21);
    paramArrayOfDouble1[(paramInt1 + 12)] = (d16 - d25);
    paramArrayOfDouble1[(paramInt1 + 13)] = (d17 + d24);
    paramArrayOfDouble1[(paramInt1 + 14)] = (d16 + d25);
    paramArrayOfDouble1[(paramInt1 + 15)] = (d17 - d24);
    paramArrayOfDouble1[paramInt1] = (d10 + d18);
    paramArrayOfDouble1[(paramInt1 + 1)] = (d11 + d19);
    paramArrayOfDouble1[(paramInt1 + 2)] = (d10 - d18);
    paramArrayOfDouble1[(paramInt1 + 3)] = (d11 - d19);
    paramArrayOfDouble1[(paramInt1 + 4)] = (d14 - d23);
    paramArrayOfDouble1[(paramInt1 + 5)] = (d15 + d22);
    paramArrayOfDouble1[(paramInt1 + 6)] = (d14 + d23);
    paramArrayOfDouble1[(paramInt1 + 7)] = (d15 - d22);
  }

  private void cftf082(double[] paramArrayOfDouble1, int paramInt1, double[] paramArrayOfDouble2, int paramInt2)
  {
    double d1 = paramArrayOfDouble2[(paramInt2 + 1)];
    double d2 = paramArrayOfDouble2[(paramInt2 + 2)];
    double d3 = paramArrayOfDouble2[(paramInt2 + 3)];
    double d8 = paramArrayOfDouble1[paramInt1] - paramArrayOfDouble1[(paramInt1 + 9)];
    double d9 = paramArrayOfDouble1[(paramInt1 + 1)] + paramArrayOfDouble1[(paramInt1 + 8)];
    double d10 = paramArrayOfDouble1[paramInt1] + paramArrayOfDouble1[(paramInt1 + 9)];
    double d11 = paramArrayOfDouble1[(paramInt1 + 1)] - paramArrayOfDouble1[(paramInt1 + 8)];
    double d4 = paramArrayOfDouble1[(paramInt1 + 4)] - paramArrayOfDouble1[(paramInt1 + 13)];
    double d5 = paramArrayOfDouble1[(paramInt1 + 5)] + paramArrayOfDouble1[(paramInt1 + 12)];
    double d12 = d1 * (d4 - d5);
    double d13 = d1 * (d5 + d4);
    d4 = paramArrayOfDouble1[(paramInt1 + 4)] + paramArrayOfDouble1[(paramInt1 + 13)];
    d5 = paramArrayOfDouble1[(paramInt1 + 5)] - paramArrayOfDouble1[(paramInt1 + 12)];
    double d14 = d1 * (d4 - d5);
    double d15 = d1 * (d5 + d4);
    d4 = paramArrayOfDouble1[(paramInt1 + 2)] - paramArrayOfDouble1[(paramInt1 + 11)];
    d5 = paramArrayOfDouble1[(paramInt1 + 3)] + paramArrayOfDouble1[(paramInt1 + 10)];
    double d16 = d2 * d4 - d3 * d5;
    double d17 = d2 * d5 + d3 * d4;
    d4 = paramArrayOfDouble1[(paramInt1 + 2)] + paramArrayOfDouble1[(paramInt1 + 11)];
    d5 = paramArrayOfDouble1[(paramInt1 + 3)] - paramArrayOfDouble1[(paramInt1 + 10)];
    double d18 = d3 * d4 - d2 * d5;
    double d19 = d3 * d5 + d2 * d4;
    d4 = paramArrayOfDouble1[(paramInt1 + 6)] - paramArrayOfDouble1[(paramInt1 + 15)];
    d5 = paramArrayOfDouble1[(paramInt1 + 7)] + paramArrayOfDouble1[(paramInt1 + 14)];
    double d20 = d3 * d4 - d2 * d5;
    double d21 = d3 * d5 + d2 * d4;
    d4 = paramArrayOfDouble1[(paramInt1 + 6)] + paramArrayOfDouble1[(paramInt1 + 15)];
    d5 = paramArrayOfDouble1[(paramInt1 + 7)] - paramArrayOfDouble1[(paramInt1 + 14)];
    double d22 = d2 * d4 - d3 * d5;
    double d23 = d2 * d5 + d3 * d4;
    d4 = d8 + d12;
    d5 = d9 + d13;
    double d6 = d16 + d20;
    double d7 = d17 + d21;
    paramArrayOfDouble1[paramInt1] = (d4 + d6);
    paramArrayOfDouble1[(paramInt1 + 1)] = (d5 + d7);
    paramArrayOfDouble1[(paramInt1 + 2)] = (d4 - d6);
    paramArrayOfDouble1[(paramInt1 + 3)] = (d5 - d7);
    d4 = d8 - d12;
    d5 = d9 - d13;
    d6 = d16 - d20;
    d7 = d17 - d21;
    paramArrayOfDouble1[(paramInt1 + 4)] = (d4 - d7);
    paramArrayOfDouble1[(paramInt1 + 5)] = (d5 + d6);
    paramArrayOfDouble1[(paramInt1 + 6)] = (d4 + d7);
    paramArrayOfDouble1[(paramInt1 + 7)] = (d5 - d6);
    d4 = d10 - d15;
    d5 = d11 + d14;
    d6 = d18 - d22;
    d7 = d19 - d23;
    paramArrayOfDouble1[(paramInt1 + 8)] = (d4 + d6);
    paramArrayOfDouble1[(paramInt1 + 9)] = (d5 + d7);
    paramArrayOfDouble1[(paramInt1 + 10)] = (d4 - d6);
    paramArrayOfDouble1[(paramInt1 + 11)] = (d5 - d7);
    d4 = d10 + d15;
    d5 = d11 - d14;
    d6 = d18 + d22;
    d7 = d19 + d23;
    paramArrayOfDouble1[(paramInt1 + 12)] = (d4 - d7);
    paramArrayOfDouble1[(paramInt1 + 13)] = (d5 + d6);
    paramArrayOfDouble1[(paramInt1 + 14)] = (d4 + d7);
    paramArrayOfDouble1[(paramInt1 + 15)] = (d5 - d6);
  }

  private void cftf040(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[paramInt] + paramArrayOfDouble[(paramInt + 4)];
    double d2 = paramArrayOfDouble[(paramInt + 1)] + paramArrayOfDouble[(paramInt + 5)];
    double d3 = paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 4)];
    double d4 = paramArrayOfDouble[(paramInt + 1)] - paramArrayOfDouble[(paramInt + 5)];
    double d5 = paramArrayOfDouble[(paramInt + 2)] + paramArrayOfDouble[(paramInt + 6)];
    double d6 = paramArrayOfDouble[(paramInt + 3)] + paramArrayOfDouble[(paramInt + 7)];
    double d7 = paramArrayOfDouble[(paramInt + 2)] - paramArrayOfDouble[(paramInt + 6)];
    double d8 = paramArrayOfDouble[(paramInt + 3)] - paramArrayOfDouble[(paramInt + 7)];
    paramArrayOfDouble[paramInt] = (d1 + d5);
    paramArrayOfDouble[(paramInt + 1)] = (d2 + d6);
    paramArrayOfDouble[(paramInt + 2)] = (d3 - d8);
    paramArrayOfDouble[(paramInt + 3)] = (d4 + d7);
    paramArrayOfDouble[(paramInt + 4)] = (d1 - d5);
    paramArrayOfDouble[(paramInt + 5)] = (d2 - d6);
    paramArrayOfDouble[(paramInt + 6)] = (d3 + d8);
    paramArrayOfDouble[(paramInt + 7)] = (d4 - d7);
  }

  private void cftb040(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[paramInt] + paramArrayOfDouble[(paramInt + 4)];
    double d2 = paramArrayOfDouble[(paramInt + 1)] + paramArrayOfDouble[(paramInt + 5)];
    double d3 = paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 4)];
    double d4 = paramArrayOfDouble[(paramInt + 1)] - paramArrayOfDouble[(paramInt + 5)];
    double d5 = paramArrayOfDouble[(paramInt + 2)] + paramArrayOfDouble[(paramInt + 6)];
    double d6 = paramArrayOfDouble[(paramInt + 3)] + paramArrayOfDouble[(paramInt + 7)];
    double d7 = paramArrayOfDouble[(paramInt + 2)] - paramArrayOfDouble[(paramInt + 6)];
    double d8 = paramArrayOfDouble[(paramInt + 3)] - paramArrayOfDouble[(paramInt + 7)];
    paramArrayOfDouble[paramInt] = (d1 + d5);
    paramArrayOfDouble[(paramInt + 1)] = (d2 + d6);
    paramArrayOfDouble[(paramInt + 2)] = (d3 + d8);
    paramArrayOfDouble[(paramInt + 3)] = (d4 - d7);
    paramArrayOfDouble[(paramInt + 4)] = (d1 - d5);
    paramArrayOfDouble[(paramInt + 5)] = (d2 - d6);
    paramArrayOfDouble[(paramInt + 6)] = (d3 - d8);
    paramArrayOfDouble[(paramInt + 7)] = (d4 + d7);
  }

  private void cftx020(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 2)];
    double d2 = -paramArrayOfDouble[(paramInt + 1)] + paramArrayOfDouble[(paramInt + 3)];
    paramArrayOfDouble[paramInt] += paramArrayOfDouble[(paramInt + 2)];
    paramArrayOfDouble[(paramInt + 1)] += paramArrayOfDouble[(paramInt + 3)];
    paramArrayOfDouble[(paramInt + 2)] = d1;
    paramArrayOfDouble[(paramInt + 3)] = d2;
  }

  private void cftxb020(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 2)];
    double d2 = paramArrayOfDouble[(paramInt + 1)] - paramArrayOfDouble[(paramInt + 3)];
    paramArrayOfDouble[paramInt] += paramArrayOfDouble[(paramInt + 2)];
    paramArrayOfDouble[(paramInt + 1)] += paramArrayOfDouble[(paramInt + 3)];
    paramArrayOfDouble[(paramInt + 2)] = d1;
    paramArrayOfDouble[(paramInt + 3)] = d2;
  }

  private void cftxc020(double[] paramArrayOfDouble, int paramInt)
  {
    double d1 = paramArrayOfDouble[paramInt] - paramArrayOfDouble[(paramInt + 2)];
    double d2 = paramArrayOfDouble[(paramInt + 1)] + paramArrayOfDouble[(paramInt + 3)];
    paramArrayOfDouble[paramInt] += paramArrayOfDouble[(paramInt + 2)];
    paramArrayOfDouble[(paramInt + 1)] -= paramArrayOfDouble[(paramInt + 3)];
    paramArrayOfDouble[(paramInt + 2)] = d1;
    paramArrayOfDouble[(paramInt + 3)] = d2;
  }

  private void rftfsub(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4)
  {
    int i1 = paramInt1 >> 1;
    int m = 2 * paramInt3 / i1;
    int k = 0;
    for (int i = 2; i < i1; i += 2)
    {
      int j = paramInt1 - i;
      k += m;
      double d1 = 0.5D - paramArrayOfDouble2[(paramInt4 + paramInt3 - k)];
      double d2 = paramArrayOfDouble2[(paramInt4 + k)];
      int i2 = paramInt2 + i;
      int i3 = paramInt2 + j;
      double d3 = paramArrayOfDouble1[i2] - paramArrayOfDouble1[i3];
      double d4 = paramArrayOfDouble1[(i2 + 1)] + paramArrayOfDouble1[(i3 + 1)];
      double d5 = d1 * d3 - d2 * d4;
      double d6 = d1 * d4 + d2 * d3;
      paramArrayOfDouble1[i2] -= d5;
      paramArrayOfDouble1[(i2 + 1)] = (d6 - paramArrayOfDouble1[(i2 + 1)]);
      paramArrayOfDouble1[i3] += d5;
      paramArrayOfDouble1[(i3 + 1)] = (d6 - paramArrayOfDouble1[(i3 + 1)]);
    }
    paramArrayOfDouble1[(paramInt2 + i1 + 1)] = (-paramArrayOfDouble1[(paramInt2 + i1 + 1)]);
  }

  private void rftbsub(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4)
  {
    int i1 = paramInt1 >> 1;
    int m = 2 * paramInt3 / i1;
    int k = 0;
    for (int i = 2; i < i1; i += 2)
    {
      int j = paramInt1 - i;
      k += m;
      double d1 = 0.5D - paramArrayOfDouble2[(paramInt4 + paramInt3 - k)];
      double d2 = paramArrayOfDouble2[(paramInt4 + k)];
      int i2 = paramInt2 + i;
      int i3 = paramInt2 + j;
      double d3 = paramArrayOfDouble1[i2] - paramArrayOfDouble1[i3];
      double d4 = paramArrayOfDouble1[(i2 + 1)] + paramArrayOfDouble1[(i3 + 1)];
      double d5 = d1 * d3 - d2 * d4;
      double d6 = d1 * d4 + d2 * d3;
      paramArrayOfDouble1[i2] -= d5;
      paramArrayOfDouble1[(i2 + 1)] -= d6;
      paramArrayOfDouble1[i3] += d5;
      paramArrayOfDouble1[(i3 + 1)] -= d6;
    }
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
 * JD-Core Version:    0.6.1
 */