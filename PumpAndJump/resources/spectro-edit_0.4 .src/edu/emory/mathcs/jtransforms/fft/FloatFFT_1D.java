package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatFFT_1D
{
  private int n;
  private int[] ip;
  private float[] w;

  public FloatFFT_1D(int paramInt)
  {
    if (!ConcurrencyUtils.isPowerOf2(paramInt))
      throw new IllegalArgumentException("n must be power of two number");
    if (paramInt < 1)
      throw new IllegalArgumentException("n must be greater than 0");
    this.n = paramInt;
    this.ip = new int[2 + (int)Math.ceil(2 + (1 << (int)(Math.log(paramInt + 0.5D) / Math.log(2.0D)) / 2))];
    this.w = new float[paramInt / 2];
  }

  public FloatFFT_1D(int paramInt, int[] paramArrayOfInt, float[] paramArrayOfFloat)
  {
    if (!ConcurrencyUtils.isPowerOf2(paramInt))
      throw new IllegalArgumentException("n must be power of two number");
    if (paramInt < 1)
      throw new IllegalArgumentException("n must be greater than 0");
    this.n = paramInt;
    this.ip = paramArrayOfInt;
    this.w = paramArrayOfFloat;
  }

  public void complexForward(float[] paramArrayOfFloat)
  {
    complexForward(paramArrayOfFloat, 0);
  }

  public void complexForward(float[] paramArrayOfFloat, int paramInt)
  {
    int j = 2 * this.n;
    int i = this.ip[0];
    if (j > i << 2)
    {
      i = j >> 2;
      makewt(i);
    }
    cftbsub(j, paramArrayOfFloat, paramInt, this.ip, i, this.w);
  }

  public void complexInverse(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    complexInverse(paramArrayOfFloat, 0, paramBoolean);
  }

  public void complexInverse(float[] paramArrayOfFloat, int paramInt, boolean paramBoolean)
  {
    int j = 2 * this.n;
    int i = this.ip[0];
    if (j > i << 2)
    {
      i = j >> 2;
      makewt(i);
    }
    cftfsub(j, paramArrayOfFloat, paramInt, this.ip, i, this.w);
    if (paramBoolean)
      scale(this.n, paramArrayOfFloat, paramInt, true);
  }

  public void realForward(float[] paramArrayOfFloat)
  {
    realForward(paramArrayOfFloat, 0);
  }

  public void realForward(float[] paramArrayOfFloat, int paramInt)
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
      cftfsub(this.n, paramArrayOfFloat, paramInt, this.ip, i, this.w);
      rftfsub(this.n, paramArrayOfFloat, paramInt, j, this.w, i);
    }
    else if (this.n == 4)
    {
      cftx020(paramArrayOfFloat, paramInt);
    }
    float f = paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 1)];
    paramArrayOfFloat[paramInt] += paramArrayOfFloat[(paramInt + 1)];
    paramArrayOfFloat[(paramInt + 1)] = f;
  }

  public void realForwardFull(float[] paramArrayOfFloat)
  {
    realForwardFull(paramArrayOfFloat, 0);
  }

  public void realForwardFull(final float[] paramArrayOfFloat, final int paramInt)
  {
    realForward(paramArrayOfFloat, paramInt);
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
              paramArrayOfFloat[(paramInt + (i - i) % i)] = paramArrayOfFloat[(paramInt + i)];
              paramArrayOfFloat[(paramInt + ((i - i) % i + 1))] = (-paramArrayOfFloat[(paramInt + i + 1)]);
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
        paramArrayOfFloat[(paramInt + (i - k) % i)] = paramArrayOfFloat[(paramInt + k)];
        paramArrayOfFloat[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfFloat[(paramInt + k + 1)]);
        k = 2 * (m + 1);
        paramArrayOfFloat[(paramInt + (i - k) % i)] = paramArrayOfFloat[(paramInt + k)];
        paramArrayOfFloat[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfFloat[(paramInt + k + 1)]);
      }
    }
    paramArrayOfFloat[(paramInt + this.n)] = (-paramArrayOfFloat[(paramInt + 1)]);
    paramArrayOfFloat[(paramInt + 1)] = 0.0F;
  }

  public void realInverse(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    realInverse(paramArrayOfFloat, 0, paramBoolean);
  }

  public void realInverse(float[] paramArrayOfFloat, int paramInt, boolean paramBoolean)
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
    paramArrayOfFloat[(paramInt + 1)] = (float)(0.5D * paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 1)]);
    paramArrayOfFloat[paramInt] -= paramArrayOfFloat[(paramInt + 1)];
    if (this.n > 4)
    {
      rftfsub(this.n, paramArrayOfFloat, paramInt, j, this.w, i);
      cftbsub(this.n, paramArrayOfFloat, paramInt, this.ip, i, this.w);
    }
    else if (this.n == 4)
    {
      cftxc020(paramArrayOfFloat, paramInt);
    }
    if (paramBoolean)
      scale(this.n / 2, paramArrayOfFloat, paramInt, false);
  }

  public void realInverseFull(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    realInverseFull(paramArrayOfFloat, 0, paramBoolean);
  }

  public void realInverseFull(final float[] paramArrayOfFloat, final int paramInt, boolean paramBoolean)
  {
    realInverse2(paramArrayOfFloat, paramInt, paramBoolean);
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
              paramArrayOfFloat[(paramInt + (i - i) % i)] = paramArrayOfFloat[(paramInt + i)];
              paramArrayOfFloat[(paramInt + ((i - i) % i + 1))] = (-paramArrayOfFloat[(paramInt + i + 1)]);
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
        paramArrayOfFloat[(paramInt + (i - k) % i)] = paramArrayOfFloat[(paramInt + k)];
        paramArrayOfFloat[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfFloat[(paramInt + k + 1)]);
        k = 2 * (m + 1);
        paramArrayOfFloat[(paramInt + (i - k) % i)] = paramArrayOfFloat[(paramInt + k)];
        paramArrayOfFloat[(paramInt + ((i - k) % i + 1))] = (-paramArrayOfFloat[(paramInt + k + 1)]);
      }
    }
    paramArrayOfFloat[(paramInt + this.n)] = (-paramArrayOfFloat[(paramInt + 1)]);
    paramArrayOfFloat[(paramInt + 1)] = 0.0F;
  }

  protected void realInverse2(float[] paramArrayOfFloat, int paramInt, boolean paramBoolean)
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
      cftfsub(this.n, paramArrayOfFloat, paramInt, this.ip, i, this.w);
      rftbsub(this.n, paramArrayOfFloat, paramInt, j, this.w, i);
    }
    else if (this.n == 4)
    {
      cftbsub(this.n, paramArrayOfFloat, paramInt, this.ip, i, this.w);
    }
    float f = paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 1)];
    paramArrayOfFloat[paramInt] += paramArrayOfFloat[(paramInt + 1)];
    paramArrayOfFloat[(paramInt + 1)] = f;
    if (paramBoolean)
      scale(this.n, paramArrayOfFloat, paramInt, false);
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
            int i1 = k + 2 * i;
            int i2 = m + i;
            f3 = this.w[i1];
            f4 = this.w[(i1 + 1)];
            f5 = this.w[(i1 + 2)];
            float f6 = this.w[(i1 + 3)];
            this.w[i2] = f3;
            this.w[(i2 + 1)] = f4;
            this.w[(i2 + 2)] = f5;
            this.w[(i2 + 3)] = f6;
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

  private void cftfsub(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int[] paramArrayOfInt, int paramInt3, float[] paramArrayOfFloat2)
  {
    if (paramInt1 > 8)
    {
      if (paramInt1 > 32)
      {
        cftf1st(paramInt1, paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3 - (paramInt1 >> 2));
        if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (paramInt1 > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
          cftrec4_th(paramInt1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        else if (paramInt1 > 512)
          cftrec4(paramInt1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        else if (paramInt1 > 128)
          cftleaf(paramInt1, 1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        else
          cftfx41(paramInt1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        bitrv2(paramInt1, paramArrayOfInt, paramArrayOfFloat1, paramInt2);
      }
      else if (paramInt1 == 32)
      {
        cftf161(paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3 - 8);
        bitrv216(paramArrayOfFloat1, paramInt2);
      }
      else
      {
        cftf081(paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, 0);
        bitrv208(paramArrayOfFloat1, paramInt2);
      }
    }
    else if (paramInt1 == 8)
      cftf040(paramArrayOfFloat1, paramInt2);
    else if (paramInt1 == 4)
      cftxb020(paramArrayOfFloat1, paramInt2);
  }

  private void cftbsub(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int[] paramArrayOfInt, int paramInt3, float[] paramArrayOfFloat2)
  {
    if (paramInt1 > 8)
    {
      if (paramInt1 > 32)
      {
        cftb1st(paramInt1, paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3 - (paramInt1 >> 2));
        if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (paramInt1 > ConcurrencyUtils.getThreadsBeginN_1D_FFT_2Threads()))
          cftrec4_th(paramInt1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        else if (paramInt1 > 512)
          cftrec4(paramInt1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        else if (paramInt1 > 128)
          cftleaf(paramInt1, 1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        else
          cftfx41(paramInt1, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
        bitrv2conj(paramInt1, paramArrayOfInt, paramArrayOfFloat1, paramInt2);
      }
      else if (paramInt1 == 32)
      {
        cftf161(paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3 - 8);
        bitrv216neg(paramArrayOfFloat1, paramInt2);
      }
      else
      {
        cftf081(paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, 0);
        bitrv208neg(paramArrayOfFloat1, paramInt2);
      }
    }
    else if (paramInt1 == 8)
      cftb040(paramArrayOfFloat1, paramInt2);
    else if (paramInt1 == 4)
      cftxb020(paramArrayOfFloat1, paramInt2);
  }

  private void bitrv2(int paramInt1, int[] paramArrayOfInt, float[] paramArrayOfFloat, int paramInt2)
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
    float f1;
    float f2;
    float f3;
    float f4;
    if (i1 == 8)
      for (k = 0; k < i2; k++)
      {
        for (i = 0; i < k; i++)
        {
          j = 4 * i + 2 * paramArrayOfInt[(i2 + k)];
          m = 4 * k + 2 * paramArrayOfInt[(i2 + i)];
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i3;
          m += 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += 2;
          m += i3;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i3;
          m -= 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
        }
        m = 4 * k + 2 * paramArrayOfInt[(i2 + k)];
        j = m + 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i4;
        m += 2 * i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= 2;
        m -= i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i3 + 2;
        m += i3 + 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i3 - i4;
        m += 2 * i4 - 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
      }
    for (int k = 0; k < i2; k++)
    {
      for (i = 0; i < k; i++)
      {
        j = 4 * i + paramArrayOfInt[(i2 + k)];
        m = 4 * k + paramArrayOfInt[(i2 + i)];
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i3;
        m += 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i3;
        m -= 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
      }
      m = 4 * k + paramArrayOfInt[(i2 + k)];
      j = m + 2;
      m += i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      f1 = paramArrayOfFloat[i5];
      f2 = paramArrayOfFloat[(i5 + 1)];
      f3 = paramArrayOfFloat[i6];
      f4 = paramArrayOfFloat[(i6 + 1)];
      paramArrayOfFloat[i5] = f3;
      paramArrayOfFloat[(i5 + 1)] = f4;
      paramArrayOfFloat[i6] = f1;
      paramArrayOfFloat[(i6 + 1)] = f2;
      j += i4;
      m += i4;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      f1 = paramArrayOfFloat[i5];
      f2 = paramArrayOfFloat[(i5 + 1)];
      f3 = paramArrayOfFloat[i6];
      f4 = paramArrayOfFloat[(i6 + 1)];
      paramArrayOfFloat[i5] = f3;
      paramArrayOfFloat[(i5 + 1)] = f4;
      paramArrayOfFloat[i6] = f1;
      paramArrayOfFloat[(i6 + 1)] = f2;
    }
  }

  private void bitrv2conj(int paramInt1, int[] paramArrayOfInt, float[] paramArrayOfFloat, int paramInt2)
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
    float f1;
    float f2;
    float f3;
    float f4;
    if (i1 == 8)
      for (k = 0; k < i2; k++)
      {
        for (i = 0; i < k; i++)
        {
          j = 4 * i + 2 * paramArrayOfInt[(i2 + k)];
          m = 4 * k + 2 * paramArrayOfInt[(i2 + i)];
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i3;
          m += 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += 2;
          m += i3;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m -= i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j += i4;
          m += 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i3;
          m -= 2;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m += i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
          j -= i4;
          m -= 2 * i4;
          i5 = paramInt2 + j;
          i6 = paramInt2 + m;
          f1 = paramArrayOfFloat[i5];
          f2 = -paramArrayOfFloat[(i5 + 1)];
          f3 = paramArrayOfFloat[i6];
          f4 = -paramArrayOfFloat[(i6 + 1)];
          paramArrayOfFloat[i5] = f3;
          paramArrayOfFloat[(i5 + 1)] = f4;
          paramArrayOfFloat[i6] = f1;
          paramArrayOfFloat[(i6 + 1)] = f2;
        }
        m = 4 * k + 2 * paramArrayOfInt[(i2 + k)];
        j = m + 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        paramArrayOfFloat[(i5 - 1)] = (-paramArrayOfFloat[(i5 - 1)]);
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        paramArrayOfFloat[(i6 + 3)] = (-paramArrayOfFloat[(i6 + 3)]);
        j += i4;
        m += 2 * i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= 2;
        m -= i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i3 + 2;
        m += i3 + 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i3 - i4;
        m += 2 * i4 - 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        paramArrayOfFloat[(i5 - 1)] = (-paramArrayOfFloat[(i5 - 1)]);
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        paramArrayOfFloat[(i6 + 3)] = (-paramArrayOfFloat[(i6 + 3)]);
      }
    for (int k = 0; k < i2; k++)
    {
      for (i = 0; i < k; i++)
      {
        j = 4 * i + paramArrayOfInt[(i2 + k)];
        m = 4 * k + paramArrayOfInt[(i2 + i)];
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i3;
        m += 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += 2;
        m += i3;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j += i4;
        m += i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i3;
        m -= 2;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
        j -= i4;
        m -= i4;
        i5 = paramInt2 + j;
        i6 = paramInt2 + m;
        f1 = paramArrayOfFloat[i5];
        f2 = -paramArrayOfFloat[(i5 + 1)];
        f3 = paramArrayOfFloat[i6];
        f4 = -paramArrayOfFloat[(i6 + 1)];
        paramArrayOfFloat[i5] = f3;
        paramArrayOfFloat[(i5 + 1)] = f4;
        paramArrayOfFloat[i6] = f1;
        paramArrayOfFloat[(i6 + 1)] = f2;
      }
      m = 4 * k + paramArrayOfInt[(i2 + k)];
      j = m + 2;
      m += i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      paramArrayOfFloat[(i5 - 1)] = (-paramArrayOfFloat[(i5 - 1)]);
      f1 = paramArrayOfFloat[i5];
      f2 = -paramArrayOfFloat[(i5 + 1)];
      f3 = paramArrayOfFloat[i6];
      f4 = -paramArrayOfFloat[(i6 + 1)];
      paramArrayOfFloat[i5] = f3;
      paramArrayOfFloat[(i5 + 1)] = f4;
      paramArrayOfFloat[i6] = f1;
      paramArrayOfFloat[(i6 + 1)] = f2;
      paramArrayOfFloat[(i6 + 3)] = (-paramArrayOfFloat[(i6 + 3)]);
      j += i4;
      m += i4;
      i5 = paramInt2 + j;
      i6 = paramInt2 + m;
      paramArrayOfFloat[(i5 - 1)] = (-paramArrayOfFloat[(i5 - 1)]);
      f1 = paramArrayOfFloat[i5];
      f2 = -paramArrayOfFloat[(i5 + 1)];
      f3 = paramArrayOfFloat[i6];
      f4 = -paramArrayOfFloat[(i6 + 1)];
      paramArrayOfFloat[i5] = f3;
      paramArrayOfFloat[(i5 + 1)] = f4;
      paramArrayOfFloat[i6] = f1;
      paramArrayOfFloat[(i6 + 1)] = f2;
      paramArrayOfFloat[(i6 + 3)] = (-paramArrayOfFloat[(i6 + 3)]);
    }
  }

  private void bitrv216(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[(paramInt + 2)];
    float f2 = paramArrayOfFloat[(paramInt + 3)];
    float f3 = paramArrayOfFloat[(paramInt + 4)];
    float f4 = paramArrayOfFloat[(paramInt + 5)];
    float f5 = paramArrayOfFloat[(paramInt + 6)];
    float f6 = paramArrayOfFloat[(paramInt + 7)];
    float f7 = paramArrayOfFloat[(paramInt + 8)];
    float f8 = paramArrayOfFloat[(paramInt + 9)];
    float f9 = paramArrayOfFloat[(paramInt + 10)];
    float f10 = paramArrayOfFloat[(paramInt + 11)];
    float f11 = paramArrayOfFloat[(paramInt + 14)];
    float f12 = paramArrayOfFloat[(paramInt + 15)];
    float f13 = paramArrayOfFloat[(paramInt + 16)];
    float f14 = paramArrayOfFloat[(paramInt + 17)];
    float f15 = paramArrayOfFloat[(paramInt + 20)];
    float f16 = paramArrayOfFloat[(paramInt + 21)];
    float f17 = paramArrayOfFloat[(paramInt + 22)];
    float f18 = paramArrayOfFloat[(paramInt + 23)];
    float f19 = paramArrayOfFloat[(paramInt + 24)];
    float f20 = paramArrayOfFloat[(paramInt + 25)];
    float f21 = paramArrayOfFloat[(paramInt + 26)];
    float f22 = paramArrayOfFloat[(paramInt + 27)];
    float f23 = paramArrayOfFloat[(paramInt + 28)];
    float f24 = paramArrayOfFloat[(paramInt + 29)];
    paramArrayOfFloat[(paramInt + 2)] = f13;
    paramArrayOfFloat[(paramInt + 3)] = f14;
    paramArrayOfFloat[(paramInt + 4)] = f7;
    paramArrayOfFloat[(paramInt + 5)] = f8;
    paramArrayOfFloat[(paramInt + 6)] = f19;
    paramArrayOfFloat[(paramInt + 7)] = f20;
    paramArrayOfFloat[(paramInt + 8)] = f3;
    paramArrayOfFloat[(paramInt + 9)] = f4;
    paramArrayOfFloat[(paramInt + 10)] = f15;
    paramArrayOfFloat[(paramInt + 11)] = f16;
    paramArrayOfFloat[(paramInt + 14)] = f23;
    paramArrayOfFloat[(paramInt + 15)] = f24;
    paramArrayOfFloat[(paramInt + 16)] = f1;
    paramArrayOfFloat[(paramInt + 17)] = f2;
    paramArrayOfFloat[(paramInt + 20)] = f9;
    paramArrayOfFloat[(paramInt + 21)] = f10;
    paramArrayOfFloat[(paramInt + 22)] = f21;
    paramArrayOfFloat[(paramInt + 23)] = f22;
    paramArrayOfFloat[(paramInt + 24)] = f5;
    paramArrayOfFloat[(paramInt + 25)] = f6;
    paramArrayOfFloat[(paramInt + 26)] = f17;
    paramArrayOfFloat[(paramInt + 27)] = f18;
    paramArrayOfFloat[(paramInt + 28)] = f11;
    paramArrayOfFloat[(paramInt + 29)] = f12;
  }

  private void bitrv216neg(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[(paramInt + 2)];
    float f2 = paramArrayOfFloat[(paramInt + 3)];
    float f3 = paramArrayOfFloat[(paramInt + 4)];
    float f4 = paramArrayOfFloat[(paramInt + 5)];
    float f5 = paramArrayOfFloat[(paramInt + 6)];
    float f6 = paramArrayOfFloat[(paramInt + 7)];
    float f7 = paramArrayOfFloat[(paramInt + 8)];
    float f8 = paramArrayOfFloat[(paramInt + 9)];
    float f9 = paramArrayOfFloat[(paramInt + 10)];
    float f10 = paramArrayOfFloat[(paramInt + 11)];
    float f11 = paramArrayOfFloat[(paramInt + 12)];
    float f12 = paramArrayOfFloat[(paramInt + 13)];
    float f13 = paramArrayOfFloat[(paramInt + 14)];
    float f14 = paramArrayOfFloat[(paramInt + 15)];
    float f15 = paramArrayOfFloat[(paramInt + 16)];
    float f16 = paramArrayOfFloat[(paramInt + 17)];
    float f17 = paramArrayOfFloat[(paramInt + 18)];
    float f18 = paramArrayOfFloat[(paramInt + 19)];
    float f19 = paramArrayOfFloat[(paramInt + 20)];
    float f20 = paramArrayOfFloat[(paramInt + 21)];
    float f21 = paramArrayOfFloat[(paramInt + 22)];
    float f22 = paramArrayOfFloat[(paramInt + 23)];
    float f23 = paramArrayOfFloat[(paramInt + 24)];
    float f24 = paramArrayOfFloat[(paramInt + 25)];
    float f25 = paramArrayOfFloat[(paramInt + 26)];
    float f26 = paramArrayOfFloat[(paramInt + 27)];
    float f27 = paramArrayOfFloat[(paramInt + 28)];
    float f28 = paramArrayOfFloat[(paramInt + 29)];
    float f29 = paramArrayOfFloat[(paramInt + 30)];
    float f30 = paramArrayOfFloat[(paramInt + 31)];
    paramArrayOfFloat[(paramInt + 2)] = f29;
    paramArrayOfFloat[(paramInt + 3)] = f30;
    paramArrayOfFloat[(paramInt + 4)] = f13;
    paramArrayOfFloat[(paramInt + 5)] = f14;
    paramArrayOfFloat[(paramInt + 6)] = f21;
    paramArrayOfFloat[(paramInt + 7)] = f22;
    paramArrayOfFloat[(paramInt + 8)] = f5;
    paramArrayOfFloat[(paramInt + 9)] = f6;
    paramArrayOfFloat[(paramInt + 10)] = f25;
    paramArrayOfFloat[(paramInt + 11)] = f26;
    paramArrayOfFloat[(paramInt + 12)] = f9;
    paramArrayOfFloat[(paramInt + 13)] = f10;
    paramArrayOfFloat[(paramInt + 14)] = f17;
    paramArrayOfFloat[(paramInt + 15)] = f18;
    paramArrayOfFloat[(paramInt + 16)] = f1;
    paramArrayOfFloat[(paramInt + 17)] = f2;
    paramArrayOfFloat[(paramInt + 18)] = f27;
    paramArrayOfFloat[(paramInt + 19)] = f28;
    paramArrayOfFloat[(paramInt + 20)] = f11;
    paramArrayOfFloat[(paramInt + 21)] = f12;
    paramArrayOfFloat[(paramInt + 22)] = f19;
    paramArrayOfFloat[(paramInt + 23)] = f20;
    paramArrayOfFloat[(paramInt + 24)] = f3;
    paramArrayOfFloat[(paramInt + 25)] = f4;
    paramArrayOfFloat[(paramInt + 26)] = f23;
    paramArrayOfFloat[(paramInt + 27)] = f24;
    paramArrayOfFloat[(paramInt + 28)] = f7;
    paramArrayOfFloat[(paramInt + 29)] = f8;
    paramArrayOfFloat[(paramInt + 30)] = f15;
    paramArrayOfFloat[(paramInt + 31)] = f16;
  }

  private void bitrv208(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[(paramInt + 2)];
    float f2 = paramArrayOfFloat[(paramInt + 3)];
    float f3 = paramArrayOfFloat[(paramInt + 6)];
    float f4 = paramArrayOfFloat[(paramInt + 7)];
    float f5 = paramArrayOfFloat[(paramInt + 8)];
    float f6 = paramArrayOfFloat[(paramInt + 9)];
    float f7 = paramArrayOfFloat[(paramInt + 12)];
    float f8 = paramArrayOfFloat[(paramInt + 13)];
    paramArrayOfFloat[(paramInt + 2)] = f5;
    paramArrayOfFloat[(paramInt + 3)] = f6;
    paramArrayOfFloat[(paramInt + 6)] = f7;
    paramArrayOfFloat[(paramInt + 7)] = f8;
    paramArrayOfFloat[(paramInt + 8)] = f1;
    paramArrayOfFloat[(paramInt + 9)] = f2;
    paramArrayOfFloat[(paramInt + 12)] = f3;
    paramArrayOfFloat[(paramInt + 13)] = f4;
  }

  private void bitrv208neg(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[(paramInt + 2)];
    float f2 = paramArrayOfFloat[(paramInt + 3)];
    float f3 = paramArrayOfFloat[(paramInt + 4)];
    float f4 = paramArrayOfFloat[(paramInt + 5)];
    float f5 = paramArrayOfFloat[(paramInt + 6)];
    float f6 = paramArrayOfFloat[(paramInt + 7)];
    float f7 = paramArrayOfFloat[(paramInt + 8)];
    float f8 = paramArrayOfFloat[(paramInt + 9)];
    float f9 = paramArrayOfFloat[(paramInt + 10)];
    float f10 = paramArrayOfFloat[(paramInt + 11)];
    float f11 = paramArrayOfFloat[(paramInt + 12)];
    float f12 = paramArrayOfFloat[(paramInt + 13)];
    float f13 = paramArrayOfFloat[(paramInt + 14)];
    float f14 = paramArrayOfFloat[(paramInt + 15)];
    paramArrayOfFloat[(paramInt + 2)] = f13;
    paramArrayOfFloat[(paramInt + 3)] = f14;
    paramArrayOfFloat[(paramInt + 4)] = f5;
    paramArrayOfFloat[(paramInt + 5)] = f6;
    paramArrayOfFloat[(paramInt + 6)] = f9;
    paramArrayOfFloat[(paramInt + 7)] = f10;
    paramArrayOfFloat[(paramInt + 8)] = f1;
    paramArrayOfFloat[(paramInt + 9)] = f2;
    paramArrayOfFloat[(paramInt + 10)] = f11;
    paramArrayOfFloat[(paramInt + 11)] = f12;
    paramArrayOfFloat[(paramInt + 12)] = f3;
    paramArrayOfFloat[(paramInt + 13)] = f4;
    paramArrayOfFloat[(paramInt + 14)] = f7;
    paramArrayOfFloat[(paramInt + 15)] = f8;
  }

  private void cftf1st(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3)
  {
    int i4 = paramInt1 >> 3;
    int i3 = 2 * i4;
    int k = i3;
    int m = k + i3;
    int i1 = m + i3;
    int i6 = paramInt2 + k;
    int i7 = paramInt2 + m;
    int i8 = paramInt2 + i1;
    float f12 = paramArrayOfFloat1[paramInt2] + paramArrayOfFloat1[i7];
    float f13 = paramArrayOfFloat1[(paramInt2 + 1)] + paramArrayOfFloat1[(i7 + 1)];
    float f14 = paramArrayOfFloat1[paramInt2] - paramArrayOfFloat1[i7];
    float f15 = paramArrayOfFloat1[(paramInt2 + 1)] - paramArrayOfFloat1[(i7 + 1)];
    float f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
    float f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
    float f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
    float f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
    paramArrayOfFloat1[paramInt2] = (f12 + f16);
    paramArrayOfFloat1[(paramInt2 + 1)] = (f13 + f17);
    paramArrayOfFloat1[i6] = (f12 - f16);
    paramArrayOfFloat1[(i6 + 1)] = (f13 - f17);
    paramArrayOfFloat1[i7] = (f14 - f19);
    paramArrayOfFloat1[(i7 + 1)] = (f15 + f18);
    paramArrayOfFloat1[i8] = (f14 + f19);
    paramArrayOfFloat1[(i8 + 1)] = (f15 - f18);
    float f1 = paramArrayOfFloat2[(paramInt3 + 1)];
    float f2 = paramArrayOfFloat2[(paramInt3 + 2)];
    float f3 = paramArrayOfFloat2[(paramInt3 + 3)];
    float f8 = 1.0F;
    float f9 = 0.0F;
    float f10 = 1.0F;
    float f11 = 0.0F;
    int i2 = 0;
    for (int i = 2; i < i4 - 2; i += 4)
    {
      i2 += 4;
      int i9 = paramInt3 + i2;
      f4 = f2 * (f8 + paramArrayOfFloat2[i9]);
      f5 = f2 * (f9 + paramArrayOfFloat2[(i9 + 1)]);
      f6 = f3 * (f10 + paramArrayOfFloat2[(i9 + 2)]);
      f7 = f3 * (f11 + paramArrayOfFloat2[(i9 + 3)]);
      f8 = paramArrayOfFloat2[i9];
      f9 = paramArrayOfFloat2[(i9 + 1)];
      f10 = paramArrayOfFloat2[(i9 + 2)];
      f11 = paramArrayOfFloat2[(i9 + 3)];
      k = i + i3;
      m = k + i3;
      i1 = m + i3;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      int i10 = paramInt2 + i;
      f12 = paramArrayOfFloat1[i10] + paramArrayOfFloat1[i7];
      f13 = paramArrayOfFloat1[(i10 + 1)] + paramArrayOfFloat1[(i7 + 1)];
      f14 = paramArrayOfFloat1[i10] - paramArrayOfFloat1[i7];
      f15 = paramArrayOfFloat1[(i10 + 1)] - paramArrayOfFloat1[(i7 + 1)];
      float f20 = paramArrayOfFloat1[(i10 + 2)] + paramArrayOfFloat1[(i7 + 2)];
      float f21 = paramArrayOfFloat1[(i10 + 3)] + paramArrayOfFloat1[(i7 + 3)];
      float f22 = paramArrayOfFloat1[(i10 + 2)] - paramArrayOfFloat1[(i7 + 2)];
      float f23 = paramArrayOfFloat1[(i10 + 3)] - paramArrayOfFloat1[(i7 + 3)];
      f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
      f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
      f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
      f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
      float f24 = paramArrayOfFloat1[(i6 + 2)] + paramArrayOfFloat1[(i8 + 2)];
      float f25 = paramArrayOfFloat1[(i6 + 3)] + paramArrayOfFloat1[(i8 + 3)];
      float f26 = paramArrayOfFloat1[(i6 + 2)] - paramArrayOfFloat1[(i8 + 2)];
      float f27 = paramArrayOfFloat1[(i6 + 3)] - paramArrayOfFloat1[(i8 + 3)];
      paramArrayOfFloat1[i10] = (f12 + f16);
      paramArrayOfFloat1[(i10 + 1)] = (f13 + f17);
      paramArrayOfFloat1[(i10 + 2)] = (f20 + f24);
      paramArrayOfFloat1[(i10 + 3)] = (f21 + f25);
      paramArrayOfFloat1[i6] = (f12 - f16);
      paramArrayOfFloat1[(i6 + 1)] = (f13 - f17);
      paramArrayOfFloat1[(i6 + 2)] = (f20 - f24);
      paramArrayOfFloat1[(i6 + 3)] = (f21 - f25);
      f12 = f14 - f19;
      f13 = f15 + f18;
      paramArrayOfFloat1[i7] = (f4 * f12 - f5 * f13);
      paramArrayOfFloat1[(i7 + 1)] = (f4 * f13 + f5 * f12);
      f12 = f22 - f27;
      f13 = f23 + f26;
      paramArrayOfFloat1[(i7 + 2)] = (f8 * f12 - f9 * f13);
      paramArrayOfFloat1[(i7 + 3)] = (f8 * f13 + f9 * f12);
      f12 = f14 + f19;
      f13 = f15 - f18;
      paramArrayOfFloat1[i8] = (f6 * f12 + f7 * f13);
      paramArrayOfFloat1[(i8 + 1)] = (f6 * f13 - f7 * f12);
      f12 = f22 + f27;
      f13 = f23 - f26;
      paramArrayOfFloat1[(i8 + 2)] = (f10 * f12 + f11 * f13);
      paramArrayOfFloat1[(i8 + 3)] = (f10 * f13 - f11 * f12);
      j = i3 - i;
      k = j + i3;
      m = k + i3;
      i1 = m + i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      f12 = paramArrayOfFloat1[i5] + paramArrayOfFloat1[i7];
      f13 = paramArrayOfFloat1[(i5 + 1)] + paramArrayOfFloat1[(i7 + 1)];
      f14 = paramArrayOfFloat1[i5] - paramArrayOfFloat1[i7];
      f15 = paramArrayOfFloat1[(i5 + 1)] - paramArrayOfFloat1[(i7 + 1)];
      f20 = paramArrayOfFloat1[(i5 - 2)] + paramArrayOfFloat1[(i7 - 2)];
      f21 = paramArrayOfFloat1[(i5 - 1)] + paramArrayOfFloat1[(i7 - 1)];
      f22 = paramArrayOfFloat1[(i5 - 2)] - paramArrayOfFloat1[(i7 - 2)];
      f23 = paramArrayOfFloat1[(i5 - 1)] - paramArrayOfFloat1[(i7 - 1)];
      f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
      f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
      f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
      f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
      f24 = paramArrayOfFloat1[(i6 - 2)] + paramArrayOfFloat1[(i8 - 2)];
      f25 = paramArrayOfFloat1[(i6 - 1)] + paramArrayOfFloat1[(i8 - 1)];
      f26 = paramArrayOfFloat1[(i6 - 2)] - paramArrayOfFloat1[(i8 - 2)];
      f27 = paramArrayOfFloat1[(i6 - 1)] - paramArrayOfFloat1[(i8 - 1)];
      paramArrayOfFloat1[i5] = (f12 + f16);
      paramArrayOfFloat1[(i5 + 1)] = (f13 + f17);
      paramArrayOfFloat1[(i5 - 2)] = (f20 + f24);
      paramArrayOfFloat1[(i5 - 1)] = (f21 + f25);
      paramArrayOfFloat1[i6] = (f12 - f16);
      paramArrayOfFloat1[(i6 + 1)] = (f13 - f17);
      paramArrayOfFloat1[(i6 - 2)] = (f20 - f24);
      paramArrayOfFloat1[(i6 - 1)] = (f21 - f25);
      f12 = f14 - f19;
      f13 = f15 + f18;
      paramArrayOfFloat1[i7] = (f5 * f12 - f4 * f13);
      paramArrayOfFloat1[(i7 + 1)] = (f5 * f13 + f4 * f12);
      f12 = f22 - f27;
      f13 = f23 + f26;
      paramArrayOfFloat1[(i7 - 2)] = (f9 * f12 - f8 * f13);
      paramArrayOfFloat1[(i7 - 1)] = (f9 * f13 + f8 * f12);
      f12 = f14 + f19;
      f13 = f15 - f18;
      paramArrayOfFloat1[i8] = (f7 * f12 + f6 * f13);
      paramArrayOfFloat1[(i8 + 1)] = (f7 * f13 - f6 * f12);
      f12 = f22 + f27;
      f13 = f23 - f26;
      paramArrayOfFloat1[(paramInt2 + i1 - 2)] = (f11 * f12 + f10 * f13);
      paramArrayOfFloat1[(paramInt2 + i1 - 1)] = (f11 * f13 - f10 * f12);
    }
    float f4 = f2 * (f8 + f1);
    float f5 = f2 * (f9 + f1);
    float f6 = f3 * (f10 - f1);
    float f7 = f3 * (f11 - f1);
    int j = i4;
    k = j + i3;
    m = k + i3;
    i1 = m + i3;
    int i5 = paramInt2 + j;
    i6 = paramInt2 + k;
    i7 = paramInt2 + m;
    i8 = paramInt2 + i1;
    f12 = paramArrayOfFloat1[(i5 - 2)] + paramArrayOfFloat1[(i7 - 2)];
    f13 = paramArrayOfFloat1[(i5 - 1)] + paramArrayOfFloat1[(i7 - 1)];
    f14 = paramArrayOfFloat1[(i5 - 2)] - paramArrayOfFloat1[(i7 - 2)];
    f15 = paramArrayOfFloat1[(i5 - 1)] - paramArrayOfFloat1[(i7 - 1)];
    f16 = paramArrayOfFloat1[(i6 - 2)] + paramArrayOfFloat1[(i8 - 2)];
    f17 = paramArrayOfFloat1[(i6 - 1)] + paramArrayOfFloat1[(i8 - 1)];
    f18 = paramArrayOfFloat1[(i6 - 2)] - paramArrayOfFloat1[(i8 - 2)];
    f19 = paramArrayOfFloat1[(i6 - 1)] - paramArrayOfFloat1[(i8 - 1)];
    paramArrayOfFloat1[(i5 - 2)] = (f12 + f16);
    paramArrayOfFloat1[(i5 - 1)] = (f13 + f17);
    paramArrayOfFloat1[(i6 - 2)] = (f12 - f16);
    paramArrayOfFloat1[(i6 - 1)] = (f13 - f17);
    f12 = f14 - f19;
    f13 = f15 + f18;
    paramArrayOfFloat1[(i7 - 2)] = (f4 * f12 - f5 * f13);
    paramArrayOfFloat1[(i7 - 1)] = (f4 * f13 + f5 * f12);
    f12 = f14 + f19;
    f13 = f15 - f18;
    paramArrayOfFloat1[(i8 - 2)] = (f6 * f12 + f7 * f13);
    paramArrayOfFloat1[(i8 - 1)] = (f6 * f13 - f7 * f12);
    f12 = paramArrayOfFloat1[i5] + paramArrayOfFloat1[i7];
    f13 = paramArrayOfFloat1[(i5 + 1)] + paramArrayOfFloat1[(i7 + 1)];
    f14 = paramArrayOfFloat1[i5] - paramArrayOfFloat1[i7];
    f15 = paramArrayOfFloat1[(i5 + 1)] - paramArrayOfFloat1[(i7 + 1)];
    f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
    f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
    f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
    f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
    paramArrayOfFloat1[i5] = (f12 + f16);
    paramArrayOfFloat1[(i5 + 1)] = (f13 + f17);
    paramArrayOfFloat1[i6] = (f12 - f16);
    paramArrayOfFloat1[(i6 + 1)] = (f13 - f17);
    f12 = f14 - f19;
    f13 = f15 + f18;
    paramArrayOfFloat1[i7] = (f1 * (f12 - f13));
    paramArrayOfFloat1[(i7 + 1)] = (f1 * (f13 + f12));
    f12 = f14 + f19;
    f13 = f15 - f18;
    paramArrayOfFloat1[i8] = (-f1 * (f12 + f13));
    paramArrayOfFloat1[(i8 + 1)] = (-f1 * (f13 - f12));
    f12 = paramArrayOfFloat1[(i5 + 2)] + paramArrayOfFloat1[(i7 + 2)];
    f13 = paramArrayOfFloat1[(i5 + 3)] + paramArrayOfFloat1[(i7 + 3)];
    f14 = paramArrayOfFloat1[(i5 + 2)] - paramArrayOfFloat1[(i7 + 2)];
    f15 = paramArrayOfFloat1[(i5 + 3)] - paramArrayOfFloat1[(i7 + 3)];
    f16 = paramArrayOfFloat1[(i6 + 2)] + paramArrayOfFloat1[(i8 + 2)];
    f17 = paramArrayOfFloat1[(i6 + 3)] + paramArrayOfFloat1[(i8 + 3)];
    f18 = paramArrayOfFloat1[(i6 + 2)] - paramArrayOfFloat1[(i8 + 2)];
    f19 = paramArrayOfFloat1[(i6 + 3)] - paramArrayOfFloat1[(i8 + 3)];
    paramArrayOfFloat1[(i5 + 2)] = (f12 + f16);
    paramArrayOfFloat1[(i5 + 3)] = (f13 + f17);
    paramArrayOfFloat1[(i6 + 2)] = (f12 - f16);
    paramArrayOfFloat1[(i6 + 3)] = (f13 - f17);
    f12 = f14 - f19;
    f13 = f15 + f18;
    paramArrayOfFloat1[(i7 + 2)] = (f5 * f12 - f4 * f13);
    paramArrayOfFloat1[(i7 + 3)] = (f5 * f13 + f4 * f12);
    f12 = f14 + f19;
    f13 = f15 - f18;
    paramArrayOfFloat1[(i8 + 2)] = (f7 * f12 + f6 * f13);
    paramArrayOfFloat1[(i8 + 3)] = (f7 * f13 - f6 * f12);
  }

  private void cftb1st(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3)
  {
    int i4 = paramInt1 >> 3;
    int i3 = 2 * i4;
    int k = i3;
    int m = k + i3;
    int i1 = m + i3;
    int i6 = paramInt2 + k;
    int i7 = paramInt2 + m;
    int i8 = paramInt2 + i1;
    float f12 = paramArrayOfFloat1[paramInt2] + paramArrayOfFloat1[i7];
    float f13 = -paramArrayOfFloat1[(paramInt2 + 1)] - paramArrayOfFloat1[(i7 + 1)];
    float f14 = paramArrayOfFloat1[paramInt2] - paramArrayOfFloat1[i7];
    float f15 = -paramArrayOfFloat1[(paramInt2 + 1)] + paramArrayOfFloat1[(i7 + 1)];
    float f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
    float f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
    float f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
    float f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
    paramArrayOfFloat1[paramInt2] = (f12 + f16);
    paramArrayOfFloat1[(paramInt2 + 1)] = (f13 - f17);
    paramArrayOfFloat1[i6] = (f12 - f16);
    paramArrayOfFloat1[(i6 + 1)] = (f13 + f17);
    paramArrayOfFloat1[i7] = (f14 + f19);
    paramArrayOfFloat1[(i7 + 1)] = (f15 + f18);
    paramArrayOfFloat1[i8] = (f14 - f19);
    paramArrayOfFloat1[(i8 + 1)] = (f15 - f18);
    float f1 = paramArrayOfFloat2[(paramInt3 + 1)];
    float f2 = paramArrayOfFloat2[(paramInt3 + 2)];
    float f3 = paramArrayOfFloat2[(paramInt3 + 3)];
    float f8 = 1.0F;
    float f9 = 0.0F;
    float f10 = 1.0F;
    float f11 = 0.0F;
    int i2 = 0;
    for (int i = 2; i < i4 - 2; i += 4)
    {
      i2 += 4;
      int i9 = paramInt3 + i2;
      f4 = f2 * (f8 + paramArrayOfFloat2[i9]);
      f5 = f2 * (f9 + paramArrayOfFloat2[(i9 + 1)]);
      f6 = f3 * (f10 + paramArrayOfFloat2[(i9 + 2)]);
      f7 = f3 * (f11 + paramArrayOfFloat2[(i9 + 3)]);
      f8 = paramArrayOfFloat2[i9];
      f9 = paramArrayOfFloat2[(i9 + 1)];
      f10 = paramArrayOfFloat2[(i9 + 2)];
      f11 = paramArrayOfFloat2[(i9 + 3)];
      k = i + i3;
      m = k + i3;
      i1 = m + i3;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      int i10 = paramInt2 + i;
      f12 = paramArrayOfFloat1[i10] + paramArrayOfFloat1[i7];
      f13 = -paramArrayOfFloat1[(i10 + 1)] - paramArrayOfFloat1[(i7 + 1)];
      f14 = paramArrayOfFloat1[i10] - paramArrayOfFloat1[(paramInt2 + m)];
      f15 = -paramArrayOfFloat1[(i10 + 1)] + paramArrayOfFloat1[(i7 + 1)];
      float f20 = paramArrayOfFloat1[(i10 + 2)] + paramArrayOfFloat1[(i7 + 2)];
      float f21 = -paramArrayOfFloat1[(i10 + 3)] - paramArrayOfFloat1[(i7 + 3)];
      float f22 = paramArrayOfFloat1[(i10 + 2)] - paramArrayOfFloat1[(i7 + 2)];
      float f23 = -paramArrayOfFloat1[(i10 + 3)] + paramArrayOfFloat1[(i7 + 3)];
      f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
      f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
      f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
      f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
      float f24 = paramArrayOfFloat1[(i6 + 2)] + paramArrayOfFloat1[(i8 + 2)];
      float f25 = paramArrayOfFloat1[(i6 + 3)] + paramArrayOfFloat1[(i8 + 3)];
      float f26 = paramArrayOfFloat1[(i6 + 2)] - paramArrayOfFloat1[(i8 + 2)];
      float f27 = paramArrayOfFloat1[(i6 + 3)] - paramArrayOfFloat1[(i8 + 3)];
      paramArrayOfFloat1[i10] = (f12 + f16);
      paramArrayOfFloat1[(i10 + 1)] = (f13 - f17);
      paramArrayOfFloat1[(i10 + 2)] = (f20 + f24);
      paramArrayOfFloat1[(i10 + 3)] = (f21 - f25);
      paramArrayOfFloat1[i6] = (f12 - f16);
      paramArrayOfFloat1[(i6 + 1)] = (f13 + f17);
      paramArrayOfFloat1[(i6 + 2)] = (f20 - f24);
      paramArrayOfFloat1[(i6 + 3)] = (f21 + f25);
      f12 = f14 + f19;
      f13 = f15 + f18;
      paramArrayOfFloat1[i7] = (f4 * f12 - f5 * f13);
      paramArrayOfFloat1[(i7 + 1)] = (f4 * f13 + f5 * f12);
      f12 = f22 + f27;
      f13 = f23 + f26;
      paramArrayOfFloat1[(i7 + 2)] = (f8 * f12 - f9 * f13);
      paramArrayOfFloat1[(i7 + 3)] = (f8 * f13 + f9 * f12);
      f12 = f14 - f19;
      f13 = f15 - f18;
      paramArrayOfFloat1[i8] = (f6 * f12 + f7 * f13);
      paramArrayOfFloat1[(i8 + 1)] = (f6 * f13 - f7 * f12);
      f12 = f22 - f27;
      f13 = f23 - f26;
      paramArrayOfFloat1[(i8 + 2)] = (f10 * f12 + f11 * f13);
      paramArrayOfFloat1[(i8 + 3)] = (f10 * f13 - f11 * f12);
      j = i3 - i;
      k = j + i3;
      m = k + i3;
      i1 = m + i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      f12 = paramArrayOfFloat1[i5] + paramArrayOfFloat1[i7];
      f13 = -paramArrayOfFloat1[(i5 + 1)] - paramArrayOfFloat1[(i7 + 1)];
      f14 = paramArrayOfFloat1[i5] - paramArrayOfFloat1[i7];
      f15 = -paramArrayOfFloat1[(i5 + 1)] + paramArrayOfFloat1[(i7 + 1)];
      f20 = paramArrayOfFloat1[(i5 - 2)] + paramArrayOfFloat1[(i7 - 2)];
      f21 = -paramArrayOfFloat1[(i5 - 1)] - paramArrayOfFloat1[(i7 - 1)];
      f22 = paramArrayOfFloat1[(i5 - 2)] - paramArrayOfFloat1[(i7 - 2)];
      f23 = -paramArrayOfFloat1[(i5 - 1)] + paramArrayOfFloat1[(i7 - 1)];
      f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
      f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
      f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
      f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
      f24 = paramArrayOfFloat1[(i6 - 2)] + paramArrayOfFloat1[(i8 - 2)];
      f25 = paramArrayOfFloat1[(i6 - 1)] + paramArrayOfFloat1[(i8 - 1)];
      f26 = paramArrayOfFloat1[(i6 - 2)] - paramArrayOfFloat1[(i8 - 2)];
      f27 = paramArrayOfFloat1[(i6 - 1)] - paramArrayOfFloat1[(i8 - 1)];
      paramArrayOfFloat1[i5] = (f12 + f16);
      paramArrayOfFloat1[(i5 + 1)] = (f13 - f17);
      paramArrayOfFloat1[(i5 - 2)] = (f20 + f24);
      paramArrayOfFloat1[(i5 - 1)] = (f21 - f25);
      paramArrayOfFloat1[i6] = (f12 - f16);
      paramArrayOfFloat1[(i6 + 1)] = (f13 + f17);
      paramArrayOfFloat1[(i6 - 2)] = (f20 - f24);
      paramArrayOfFloat1[(i6 - 1)] = (f21 + f25);
      f12 = f14 + f19;
      f13 = f15 + f18;
      paramArrayOfFloat1[i7] = (f5 * f12 - f4 * f13);
      paramArrayOfFloat1[(i7 + 1)] = (f5 * f13 + f4 * f12);
      f12 = f22 + f27;
      f13 = f23 + f26;
      paramArrayOfFloat1[(i7 - 2)] = (f9 * f12 - f8 * f13);
      paramArrayOfFloat1[(i7 - 1)] = (f9 * f13 + f8 * f12);
      f12 = f14 - f19;
      f13 = f15 - f18;
      paramArrayOfFloat1[i8] = (f7 * f12 + f6 * f13);
      paramArrayOfFloat1[(i8 + 1)] = (f7 * f13 - f6 * f12);
      f12 = f22 - f27;
      f13 = f23 - f26;
      paramArrayOfFloat1[(i8 - 2)] = (f11 * f12 + f10 * f13);
      paramArrayOfFloat1[(i8 - 1)] = (f11 * f13 - f10 * f12);
    }
    float f4 = f2 * (f8 + f1);
    float f5 = f2 * (f9 + f1);
    float f6 = f3 * (f10 - f1);
    float f7 = f3 * (f11 - f1);
    int j = i4;
    k = j + i3;
    m = k + i3;
    i1 = m + i3;
    int i5 = paramInt2 + j;
    i6 = paramInt2 + k;
    i7 = paramInt2 + m;
    i8 = paramInt2 + i1;
    f12 = paramArrayOfFloat1[(i5 - 2)] + paramArrayOfFloat1[(i7 - 2)];
    f13 = -paramArrayOfFloat1[(i5 - 1)] - paramArrayOfFloat1[(i7 - 1)];
    f14 = paramArrayOfFloat1[(i5 - 2)] - paramArrayOfFloat1[(i7 - 2)];
    f15 = -paramArrayOfFloat1[(i5 - 1)] + paramArrayOfFloat1[(i7 - 1)];
    f16 = paramArrayOfFloat1[(i6 - 2)] + paramArrayOfFloat1[(i8 - 2)];
    f17 = paramArrayOfFloat1[(i6 - 1)] + paramArrayOfFloat1[(i8 - 1)];
    f18 = paramArrayOfFloat1[(i6 - 2)] - paramArrayOfFloat1[(i8 - 2)];
    f19 = paramArrayOfFloat1[(i6 - 1)] - paramArrayOfFloat1[(i8 - 1)];
    paramArrayOfFloat1[(i5 - 2)] = (f12 + f16);
    paramArrayOfFloat1[(i5 - 1)] = (f13 - f17);
    paramArrayOfFloat1[(i6 - 2)] = (f12 - f16);
    paramArrayOfFloat1[(i6 - 1)] = (f13 + f17);
    f12 = f14 + f19;
    f13 = f15 + f18;
    paramArrayOfFloat1[(i7 - 2)] = (f4 * f12 - f5 * f13);
    paramArrayOfFloat1[(i7 - 1)] = (f4 * f13 + f5 * f12);
    f12 = f14 - f19;
    f13 = f15 - f18;
    paramArrayOfFloat1[(i8 - 2)] = (f6 * f12 + f7 * f13);
    paramArrayOfFloat1[(i8 - 1)] = (f6 * f13 - f7 * f12);
    f12 = paramArrayOfFloat1[i5] + paramArrayOfFloat1[i7];
    f13 = -paramArrayOfFloat1[(i5 + 1)] - paramArrayOfFloat1[(i7 + 1)];
    f14 = paramArrayOfFloat1[i5] - paramArrayOfFloat1[i7];
    f15 = -paramArrayOfFloat1[(i5 + 1)] + paramArrayOfFloat1[(i7 + 1)];
    f16 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
    f17 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
    f18 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
    f19 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
    paramArrayOfFloat1[i5] = (f12 + f16);
    paramArrayOfFloat1[(i5 + 1)] = (f13 - f17);
    paramArrayOfFloat1[i6] = (f12 - f16);
    paramArrayOfFloat1[(i6 + 1)] = (f13 + f17);
    f12 = f14 + f19;
    f13 = f15 + f18;
    paramArrayOfFloat1[i7] = (f1 * (f12 - f13));
    paramArrayOfFloat1[(i7 + 1)] = (f1 * (f13 + f12));
    f12 = f14 - f19;
    f13 = f15 - f18;
    paramArrayOfFloat1[i8] = (-f1 * (f12 + f13));
    paramArrayOfFloat1[(i8 + 1)] = (-f1 * (f13 - f12));
    f12 = paramArrayOfFloat1[(i5 + 2)] + paramArrayOfFloat1[(i7 + 2)];
    f13 = -paramArrayOfFloat1[(i5 + 3)] - paramArrayOfFloat1[(i7 + 3)];
    f14 = paramArrayOfFloat1[(i5 + 2)] - paramArrayOfFloat1[(i7 + 2)];
    f15 = -paramArrayOfFloat1[(i5 + 3)] + paramArrayOfFloat1[(i7 + 3)];
    f16 = paramArrayOfFloat1[(i6 + 2)] + paramArrayOfFloat1[(i8 + 2)];
    f17 = paramArrayOfFloat1[(i6 + 3)] + paramArrayOfFloat1[(i8 + 3)];
    f18 = paramArrayOfFloat1[(i6 + 2)] - paramArrayOfFloat1[(i8 + 2)];
    f19 = paramArrayOfFloat1[(i6 + 3)] - paramArrayOfFloat1[(i8 + 3)];
    paramArrayOfFloat1[(i5 + 2)] = (f12 + f16);
    paramArrayOfFloat1[(i5 + 3)] = (f13 - f17);
    paramArrayOfFloat1[(i6 + 2)] = (f12 - f16);
    paramArrayOfFloat1[(i6 + 3)] = (f13 + f17);
    f12 = f14 + f19;
    f13 = f15 + f18;
    paramArrayOfFloat1[(i7 + 2)] = (f5 * f12 - f4 * f13);
    paramArrayOfFloat1[(i7 + 3)] = (f5 * f13 + f4 * f12);
    f12 = f14 - f19;
    f13 = f15 - f18;
    paramArrayOfFloat1[(i8 + 2)] = (f7 * f12 + f6 * f13);
    paramArrayOfFloat1[(i8 + 3)] = (f7 * f13 - f6 * f12);
  }

  private void cftrec4_th(final int paramInt1, final float[] paramArrayOfFloat1, int paramInt2, final int paramInt3, final float[] paramArrayOfFloat2)
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
              FloatFFT_1D.this.cftmdl1(m, paramArrayOfFloat1, n - m, paramArrayOfFloat2, paramInt3 - (m >> 1));
            }
            FloatFFT_1D.this.cftleaf(m, 1, paramArrayOfFloat1, n - m, paramInt3, paramArrayOfFloat2);
            int k = 0;
            int i1 = i3 - m;
            int j = i2 - m;
            while (j > 0)
            {
              k++;
              int i = FloatFFT_1D.this.cfttree(m, j, k, paramArrayOfFloat1, i3, paramInt3, paramArrayOfFloat2);
              FloatFFT_1D.this.cftleaf(m, i, paramArrayOfFloat1, i1 + j, paramInt3, paramArrayOfFloat2);
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
              FloatFFT_1D.this.cftmdl2(m, paramArrayOfFloat1, n - m, paramArrayOfFloat2, paramInt3 - m);
            }
            FloatFFT_1D.this.cftleaf(m, 0, paramArrayOfFloat1, n - m, paramInt3, paramArrayOfFloat2);
            k >>= 1;
            int i1 = i3 - m;
            int j = i2 - m;
            while (j > 0)
            {
              k++;
              int i = FloatFFT_1D.this.cfttree(m, j, k, paramArrayOfFloat1, i3, paramInt3, paramArrayOfFloat2);
              FloatFFT_1D.this.cftleaf(m, i, paramArrayOfFloat1, i1 + j, paramInt3, paramArrayOfFloat2);
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

  private void cftrec4(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2)
  {
    int m = paramInt1;
    int i1 = paramInt2 + paramInt1;
    while (m > 512)
    {
      m >>= 2;
      cftmdl1(m, paramArrayOfFloat1, i1 - m, paramArrayOfFloat2, paramInt3 - (m >> 1));
    }
    cftleaf(m, 1, paramArrayOfFloat1, i1 - m, paramInt3, paramArrayOfFloat2);
    int k = 0;
    int i2 = paramInt2 - m;
    int j = paramInt1 - m;
    while (j > 0)
    {
      k++;
      int i = cfttree(m, j, k, paramArrayOfFloat1, paramInt2, paramInt3, paramArrayOfFloat2);
      cftleaf(m, i, paramArrayOfFloat1, i2 + j, paramInt3, paramArrayOfFloat2);
      j -= m;
    }
  }

  private int cfttree(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, int paramInt4, int paramInt5, float[] paramArrayOfFloat2)
  {
    int m = paramInt4 - paramInt1;
    int j;
    if ((paramInt3 & 0x3) != 0)
    {
      j = paramInt3 & 0x1;
      if (j != 0)
        cftmdl1(paramInt1, paramArrayOfFloat1, m + paramInt2, paramArrayOfFloat2, paramInt5 - (paramInt1 >> 1));
      else
        cftmdl2(paramInt1, paramArrayOfFloat1, m + paramInt2, paramArrayOfFloat2, paramInt5 - paramInt1);
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
          cftmdl1(k, paramArrayOfFloat1, i1 - k, paramArrayOfFloat2, paramInt5 - (k >> 1));
          k >>= 2;
        }
      while (k > 128)
      {
        cftmdl2(k, paramArrayOfFloat1, i1 - k, paramArrayOfFloat2, paramInt5 - k);
        k >>= 2;
      }
    }
    return j;
  }

  private void cftleaf(int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2)
  {
    if (paramInt1 == 512)
    {
      cftmdl1(128, paramArrayOfFloat1, paramInt3, paramArrayOfFloat2, paramInt4 - 64);
      cftf161(paramArrayOfFloat1, paramInt3, paramArrayOfFloat2, paramInt4 - 8);
      cftf162(paramArrayOfFloat1, paramInt3 + 32, paramArrayOfFloat2, paramInt4 - 32);
      cftf161(paramArrayOfFloat1, paramInt3 + 64, paramArrayOfFloat2, paramInt4 - 8);
      cftf161(paramArrayOfFloat1, paramInt3 + 96, paramArrayOfFloat2, paramInt4 - 8);
      cftmdl2(128, paramArrayOfFloat1, paramInt3 + 128, paramArrayOfFloat2, paramInt4 - 128);
      cftf161(paramArrayOfFloat1, paramInt3 + 128, paramArrayOfFloat2, paramInt4 - 8);
      cftf162(paramArrayOfFloat1, paramInt3 + 160, paramArrayOfFloat2, paramInt4 - 32);
      cftf161(paramArrayOfFloat1, paramInt3 + 192, paramArrayOfFloat2, paramInt4 - 8);
      cftf162(paramArrayOfFloat1, paramInt3 + 224, paramArrayOfFloat2, paramInt4 - 32);
      cftmdl1(128, paramArrayOfFloat1, paramInt3 + 256, paramArrayOfFloat2, paramInt4 - 64);
      cftf161(paramArrayOfFloat1, paramInt3 + 256, paramArrayOfFloat2, paramInt4 - 8);
      cftf162(paramArrayOfFloat1, paramInt3 + 288, paramArrayOfFloat2, paramInt4 - 32);
      cftf161(paramArrayOfFloat1, paramInt3 + 320, paramArrayOfFloat2, paramInt4 - 8);
      cftf161(paramArrayOfFloat1, paramInt3 + 352, paramArrayOfFloat2, paramInt4 - 8);
      if (paramInt2 != 0)
      {
        cftmdl1(128, paramArrayOfFloat1, paramInt3 + 384, paramArrayOfFloat2, paramInt4 - 64);
        cftf161(paramArrayOfFloat1, paramInt3 + 480, paramArrayOfFloat2, paramInt4 - 8);
      }
      else
      {
        cftmdl2(128, paramArrayOfFloat1, paramInt3 + 384, paramArrayOfFloat2, paramInt4 - 128);
        cftf162(paramArrayOfFloat1, paramInt3 + 480, paramArrayOfFloat2, paramInt4 - 32);
      }
      cftf161(paramArrayOfFloat1, paramInt3 + 384, paramArrayOfFloat2, paramInt4 - 8);
      cftf162(paramArrayOfFloat1, paramInt3 + 416, paramArrayOfFloat2, paramInt4 - 32);
      cftf161(paramArrayOfFloat1, paramInt3 + 448, paramArrayOfFloat2, paramInt4 - 8);
    }
    else
    {
      cftmdl1(64, paramArrayOfFloat1, paramInt3, paramArrayOfFloat2, paramInt4 - 32);
      cftf081(paramArrayOfFloat1, paramInt3, paramArrayOfFloat2, paramInt4 - 8);
      cftf082(paramArrayOfFloat1, paramInt3 + 16, paramArrayOfFloat2, paramInt4 - 8);
      cftf081(paramArrayOfFloat1, paramInt3 + 32, paramArrayOfFloat2, paramInt4 - 8);
      cftf081(paramArrayOfFloat1, paramInt3 + 48, paramArrayOfFloat2, paramInt4 - 8);
      cftmdl2(64, paramArrayOfFloat1, paramInt3 + 64, paramArrayOfFloat2, paramInt4 - 64);
      cftf081(paramArrayOfFloat1, paramInt3 + 64, paramArrayOfFloat2, paramInt4 - 8);
      cftf082(paramArrayOfFloat1, paramInt3 + 80, paramArrayOfFloat2, paramInt4 - 8);
      cftf081(paramArrayOfFloat1, paramInt3 + 96, paramArrayOfFloat2, paramInt4 - 8);
      cftf082(paramArrayOfFloat1, paramInt3 + 112, paramArrayOfFloat2, paramInt4 - 8);
      cftmdl1(64, paramArrayOfFloat1, paramInt3 + 128, paramArrayOfFloat2, paramInt4 - 32);
      cftf081(paramArrayOfFloat1, paramInt3 + 128, paramArrayOfFloat2, paramInt4 - 8);
      cftf082(paramArrayOfFloat1, paramInt3 + 144, paramArrayOfFloat2, paramInt4 - 8);
      cftf081(paramArrayOfFloat1, paramInt3 + 160, paramArrayOfFloat2, paramInt4 - 8);
      cftf081(paramArrayOfFloat1, paramInt3 + 176, paramArrayOfFloat2, paramInt4 - 8);
      if (paramInt2 != 0)
      {
        cftmdl1(64, paramArrayOfFloat1, paramInt3 + 192, paramArrayOfFloat2, paramInt4 - 32);
        cftf081(paramArrayOfFloat1, paramInt3 + 240, paramArrayOfFloat2, paramInt4 - 8);
      }
      else
      {
        cftmdl2(64, paramArrayOfFloat1, paramInt3 + 192, paramArrayOfFloat2, paramInt4 - 64);
        cftf082(paramArrayOfFloat1, paramInt3 + 240, paramArrayOfFloat2, paramInt4 - 8);
      }
      cftf081(paramArrayOfFloat1, paramInt3 + 192, paramArrayOfFloat2, paramInt4 - 8);
      cftf082(paramArrayOfFloat1, paramInt3 + 208, paramArrayOfFloat2, paramInt4 - 8);
      cftf081(paramArrayOfFloat1, paramInt3 + 224, paramArrayOfFloat2, paramInt4 - 8);
    }
  }

  private void cftmdl1(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3)
  {
    int i4 = paramInt1 >> 3;
    int i3 = 2 * i4;
    int k = i3;
    int m = k + i3;
    int i1 = m + i3;
    int i6 = paramInt2 + k;
    int i7 = paramInt2 + m;
    int i8 = paramInt2 + i1;
    float f6 = paramArrayOfFloat1[paramInt2] + paramArrayOfFloat1[i7];
    float f7 = paramArrayOfFloat1[(paramInt2 + 1)] + paramArrayOfFloat1[(i7 + 1)];
    float f8 = paramArrayOfFloat1[paramInt2] - paramArrayOfFloat1[i7];
    float f9 = paramArrayOfFloat1[(paramInt2 + 1)] - paramArrayOfFloat1[(i7 + 1)];
    float f10 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
    float f11 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
    float f12 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
    float f13 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
    paramArrayOfFloat1[paramInt2] = (f6 + f10);
    paramArrayOfFloat1[(paramInt2 + 1)] = (f7 + f11);
    paramArrayOfFloat1[i6] = (f6 - f10);
    paramArrayOfFloat1[(i6 + 1)] = (f7 - f11);
    paramArrayOfFloat1[i7] = (f8 - f13);
    paramArrayOfFloat1[(i7 + 1)] = (f9 + f12);
    paramArrayOfFloat1[i8] = (f8 + f13);
    paramArrayOfFloat1[(i8 + 1)] = (f9 - f12);
    float f1 = paramArrayOfFloat2[(paramInt3 + 1)];
    int i2 = 0;
    for (int i = 2; i < i4; i += 2)
    {
      i2 += 4;
      int i9 = paramInt3 + i2;
      float f2 = paramArrayOfFloat2[i9];
      float f3 = paramArrayOfFloat2[(i9 + 1)];
      float f4 = paramArrayOfFloat2[(i9 + 2)];
      float f5 = paramArrayOfFloat2[(i9 + 3)];
      k = i + i3;
      m = k + i3;
      i1 = m + i3;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      int i10 = paramInt2 + i;
      f6 = paramArrayOfFloat1[i10] + paramArrayOfFloat1[i7];
      f7 = paramArrayOfFloat1[(i10 + 1)] + paramArrayOfFloat1[(i7 + 1)];
      f8 = paramArrayOfFloat1[i10] - paramArrayOfFloat1[i7];
      f9 = paramArrayOfFloat1[(i10 + 1)] - paramArrayOfFloat1[(i7 + 1)];
      f10 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
      f11 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
      f12 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
      f13 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
      paramArrayOfFloat1[i10] = (f6 + f10);
      paramArrayOfFloat1[(i10 + 1)] = (f7 + f11);
      paramArrayOfFloat1[i6] = (f6 - f10);
      paramArrayOfFloat1[(i6 + 1)] = (f7 - f11);
      f6 = f8 - f13;
      f7 = f9 + f12;
      paramArrayOfFloat1[i7] = (f2 * f6 - f3 * f7);
      paramArrayOfFloat1[(i7 + 1)] = (f2 * f7 + f3 * f6);
      f6 = f8 + f13;
      f7 = f9 - f12;
      paramArrayOfFloat1[i8] = (f4 * f6 + f5 * f7);
      paramArrayOfFloat1[(i8 + 1)] = (f4 * f7 - f5 * f6);
      j = i3 - i;
      k = j + i3;
      m = k + i3;
      i1 = m + i3;
      i5 = paramInt2 + j;
      i6 = paramInt2 + k;
      i7 = paramInt2 + m;
      i8 = paramInt2 + i1;
      f6 = paramArrayOfFloat1[i5] + paramArrayOfFloat1[i7];
      f7 = paramArrayOfFloat1[(i5 + 1)] + paramArrayOfFloat1[(i7 + 1)];
      f8 = paramArrayOfFloat1[i5] - paramArrayOfFloat1[i7];
      f9 = paramArrayOfFloat1[(i5 + 1)] - paramArrayOfFloat1[(i7 + 1)];
      f10 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
      f11 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
      f12 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
      f13 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
      paramArrayOfFloat1[i5] = (f6 + f10);
      paramArrayOfFloat1[(i5 + 1)] = (f7 + f11);
      paramArrayOfFloat1[i6] = (f6 - f10);
      paramArrayOfFloat1[(i6 + 1)] = (f7 - f11);
      f6 = f8 - f13;
      f7 = f9 + f12;
      paramArrayOfFloat1[i7] = (f3 * f6 - f2 * f7);
      paramArrayOfFloat1[(i7 + 1)] = (f3 * f7 + f2 * f6);
      f6 = f8 + f13;
      f7 = f9 - f12;
      paramArrayOfFloat1[i8] = (f5 * f6 + f4 * f7);
      paramArrayOfFloat1[(i8 + 1)] = (f5 * f7 - f4 * f6);
    }
    int j = i4;
    k = j + i3;
    m = k + i3;
    i1 = m + i3;
    int i5 = paramInt2 + j;
    i6 = paramInt2 + k;
    i7 = paramInt2 + m;
    i8 = paramInt2 + i1;
    f6 = paramArrayOfFloat1[i5] + paramArrayOfFloat1[i7];
    f7 = paramArrayOfFloat1[(i5 + 1)] + paramArrayOfFloat1[(i7 + 1)];
    f8 = paramArrayOfFloat1[i5] - paramArrayOfFloat1[i7];
    f9 = paramArrayOfFloat1[(i5 + 1)] - paramArrayOfFloat1[(i7 + 1)];
    f10 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[i8];
    f11 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[(i8 + 1)];
    f12 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[i8];
    f13 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[(i8 + 1)];
    paramArrayOfFloat1[i5] = (f6 + f10);
    paramArrayOfFloat1[(i5 + 1)] = (f7 + f11);
    paramArrayOfFloat1[i6] = (f6 - f10);
    paramArrayOfFloat1[(i6 + 1)] = (f7 - f11);
    f6 = f8 - f13;
    f7 = f9 + f12;
    paramArrayOfFloat1[i7] = (f1 * (f6 - f7));
    paramArrayOfFloat1[(i7 + 1)] = (f1 * (f7 + f6));
    f6 = f8 + f13;
    f7 = f9 - f12;
    paramArrayOfFloat1[i8] = (-f1 * (f6 + f7));
    paramArrayOfFloat1[(i8 + 1)] = (-f1 * (f7 - f6));
  }

  private void cftmdl2(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3)
  {
    int i5 = paramInt1 >> 3;
    int i4 = 2 * i5;
    float f1 = paramArrayOfFloat2[(paramInt3 + 1)];
    int k = i4;
    int m = k + i4;
    int i1 = m + i4;
    int i7 = paramInt2 + k;
    int i8 = paramInt2 + m;
    int i9 = paramInt2 + i1;
    float f10 = paramArrayOfFloat1[paramInt2] - paramArrayOfFloat1[(i8 + 1)];
    float f11 = paramArrayOfFloat1[(paramInt2 + 1)] + paramArrayOfFloat1[i8];
    float f12 = paramArrayOfFloat1[paramInt2] + paramArrayOfFloat1[(i8 + 1)];
    float f13 = paramArrayOfFloat1[(paramInt2 + 1)] - paramArrayOfFloat1[i8];
    float f14 = paramArrayOfFloat1[i7] - paramArrayOfFloat1[(i9 + 1)];
    float f15 = paramArrayOfFloat1[(i7 + 1)] + paramArrayOfFloat1[i9];
    float f16 = paramArrayOfFloat1[i7] + paramArrayOfFloat1[(i9 + 1)];
    float f17 = paramArrayOfFloat1[(i7 + 1)] - paramArrayOfFloat1[i9];
    float f18 = f1 * (f14 - f15);
    float f19 = f1 * (f15 + f14);
    paramArrayOfFloat1[paramInt2] = (f10 + f18);
    paramArrayOfFloat1[(paramInt2 + 1)] = (f11 + f19);
    paramArrayOfFloat1[i7] = (f10 - f18);
    paramArrayOfFloat1[(i7 + 1)] = (f11 - f19);
    f18 = f1 * (f16 - f17);
    f19 = f1 * (f17 + f16);
    paramArrayOfFloat1[i8] = (f12 - f19);
    paramArrayOfFloat1[(i8 + 1)] = (f13 + f18);
    paramArrayOfFloat1[i9] = (f12 + f19);
    paramArrayOfFloat1[(i9 + 1)] = (f13 - f18);
    int i2 = 0;
    int i3 = 2 * i4;
    for (int i = 2; i < i5; i += 2)
    {
      i2 += 4;
      int i10 = paramInt3 + i2;
      f2 = paramArrayOfFloat2[i10];
      f3 = paramArrayOfFloat2[(i10 + 1)];
      float f4 = paramArrayOfFloat2[(i10 + 2)];
      float f5 = paramArrayOfFloat2[(i10 + 3)];
      i3 -= 4;
      int i11 = paramInt3 + i3;
      float f7 = paramArrayOfFloat2[i11];
      float f6 = paramArrayOfFloat2[(i11 + 1)];
      float f9 = paramArrayOfFloat2[(i11 + 2)];
      float f8 = paramArrayOfFloat2[(i11 + 3)];
      k = i + i4;
      m = k + i4;
      i1 = m + i4;
      i7 = paramInt2 + k;
      i8 = paramInt2 + m;
      i9 = paramInt2 + i1;
      int i12 = paramInt2 + i;
      f10 = paramArrayOfFloat1[i12] - paramArrayOfFloat1[(i8 + 1)];
      f11 = paramArrayOfFloat1[(i12 + 1)] + paramArrayOfFloat1[i8];
      f12 = paramArrayOfFloat1[i12] + paramArrayOfFloat1[(i8 + 1)];
      f13 = paramArrayOfFloat1[(i12 + 1)] - paramArrayOfFloat1[i8];
      f14 = paramArrayOfFloat1[i7] - paramArrayOfFloat1[(i9 + 1)];
      f15 = paramArrayOfFloat1[(i7 + 1)] + paramArrayOfFloat1[i9];
      f16 = paramArrayOfFloat1[i7] + paramArrayOfFloat1[(i9 + 1)];
      f17 = paramArrayOfFloat1[(i7 + 1)] - paramArrayOfFloat1[i9];
      f18 = f2 * f10 - f3 * f11;
      f19 = f2 * f11 + f3 * f10;
      f20 = f6 * f14 - f7 * f15;
      f21 = f6 * f15 + f7 * f14;
      paramArrayOfFloat1[i12] = (f18 + f20);
      paramArrayOfFloat1[(i12 + 1)] = (f19 + f21);
      paramArrayOfFloat1[i7] = (f18 - f20);
      paramArrayOfFloat1[(i7 + 1)] = (f19 - f21);
      f18 = f4 * f12 + f5 * f13;
      f19 = f4 * f13 - f5 * f12;
      f20 = f8 * f16 + f9 * f17;
      f21 = f8 * f17 - f9 * f16;
      paramArrayOfFloat1[i8] = (f18 + f20);
      paramArrayOfFloat1[(i8 + 1)] = (f19 + f21);
      paramArrayOfFloat1[i9] = (f18 - f20);
      paramArrayOfFloat1[(i9 + 1)] = (f19 - f21);
      j = i4 - i;
      k = j + i4;
      m = k + i4;
      i1 = m + i4;
      i6 = paramInt2 + j;
      i7 = paramInt2 + k;
      i8 = paramInt2 + m;
      i9 = paramInt2 + i1;
      f10 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[(i8 + 1)];
      f11 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[i8];
      f12 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[(i8 + 1)];
      f13 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[i8];
      f14 = paramArrayOfFloat1[i7] - paramArrayOfFloat1[(i9 + 1)];
      f15 = paramArrayOfFloat1[(i7 + 1)] + paramArrayOfFloat1[i9];
      f16 = paramArrayOfFloat1[i7] + paramArrayOfFloat1[(i9 + 1)];
      f17 = paramArrayOfFloat1[(i7 + 1)] - paramArrayOfFloat1[i9];
      f18 = f7 * f10 - f6 * f11;
      f19 = f7 * f11 + f6 * f10;
      f20 = f3 * f14 - f2 * f15;
      f21 = f3 * f15 + f2 * f14;
      paramArrayOfFloat1[i6] = (f18 + f20);
      paramArrayOfFloat1[(i6 + 1)] = (f19 + f21);
      paramArrayOfFloat1[i7] = (f18 - f20);
      paramArrayOfFloat1[(i7 + 1)] = (f19 - f21);
      f18 = f9 * f12 + f8 * f13;
      f19 = f9 * f13 - f8 * f12;
      f20 = f5 * f16 + f4 * f17;
      f21 = f5 * f17 - f4 * f16;
      paramArrayOfFloat1[i8] = (f18 + f20);
      paramArrayOfFloat1[(i8 + 1)] = (f19 + f21);
      paramArrayOfFloat1[i9] = (f18 - f20);
      paramArrayOfFloat1[(i9 + 1)] = (f19 - f21);
    }
    float f2 = paramArrayOfFloat2[(paramInt3 + i4)];
    float f3 = paramArrayOfFloat2[(paramInt3 + i4 + 1)];
    int j = i5;
    k = j + i4;
    m = k + i4;
    i1 = m + i4;
    int i6 = paramInt2 + j;
    i7 = paramInt2 + k;
    i8 = paramInt2 + m;
    i9 = paramInt2 + i1;
    f10 = paramArrayOfFloat1[i6] - paramArrayOfFloat1[(i8 + 1)];
    f11 = paramArrayOfFloat1[(i6 + 1)] + paramArrayOfFloat1[i8];
    f12 = paramArrayOfFloat1[i6] + paramArrayOfFloat1[(i8 + 1)];
    f13 = paramArrayOfFloat1[(i6 + 1)] - paramArrayOfFloat1[i8];
    f14 = paramArrayOfFloat1[i7] - paramArrayOfFloat1[(i9 + 1)];
    f15 = paramArrayOfFloat1[(i7 + 1)] + paramArrayOfFloat1[i9];
    f16 = paramArrayOfFloat1[i7] + paramArrayOfFloat1[(i9 + 1)];
    f17 = paramArrayOfFloat1[(i7 + 1)] - paramArrayOfFloat1[i9];
    f18 = f2 * f10 - f3 * f11;
    f19 = f2 * f11 + f3 * f10;
    float f20 = f3 * f14 - f2 * f15;
    float f21 = f3 * f15 + f2 * f14;
    paramArrayOfFloat1[i6] = (f18 + f20);
    paramArrayOfFloat1[(i6 + 1)] = (f19 + f21);
    paramArrayOfFloat1[i7] = (f18 - f20);
    paramArrayOfFloat1[(i7 + 1)] = (f19 - f21);
    f18 = f3 * f12 - f2 * f13;
    f19 = f3 * f13 + f2 * f12;
    f20 = f2 * f16 - f3 * f17;
    f21 = f2 * f17 + f3 * f16;
    paramArrayOfFloat1[i8] = (f18 - f20);
    paramArrayOfFloat1[(i8 + 1)] = (f19 - f21);
    paramArrayOfFloat1[i9] = (f18 + f20);
    paramArrayOfFloat1[(i9 + 1)] = (f19 + f21);
  }

  private void cftfx41(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2)
  {
    if (paramInt1 == 128)
    {
      cftf161(paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3 - 8);
      cftf162(paramArrayOfFloat1, paramInt2 + 32, paramArrayOfFloat2, paramInt3 - 32);
      cftf161(paramArrayOfFloat1, paramInt2 + 64, paramArrayOfFloat2, paramInt3 - 8);
      cftf161(paramArrayOfFloat1, paramInt2 + 96, paramArrayOfFloat2, paramInt3 - 8);
    }
    else
    {
      cftf081(paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3 - 8);
      cftf082(paramArrayOfFloat1, paramInt2 + 16, paramArrayOfFloat2, paramInt3 - 8);
      cftf081(paramArrayOfFloat1, paramInt2 + 32, paramArrayOfFloat2, paramInt3 - 8);
      cftf081(paramArrayOfFloat1, paramInt2 + 48, paramArrayOfFloat2, paramInt3 - 8);
    }
  }

  private void cftf161(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2)
  {
    float f1 = paramArrayOfFloat2[(paramInt2 + 1)];
    float f2 = paramArrayOfFloat2[(paramInt2 + 2)];
    float f3 = paramArrayOfFloat2[(paramInt2 + 3)];
    float f4 = paramArrayOfFloat1[paramInt1] + paramArrayOfFloat1[(paramInt1 + 16)];
    float f5 = paramArrayOfFloat1[(paramInt1 + 1)] + paramArrayOfFloat1[(paramInt1 + 17)];
    float f6 = paramArrayOfFloat1[paramInt1] - paramArrayOfFloat1[(paramInt1 + 16)];
    float f7 = paramArrayOfFloat1[(paramInt1 + 1)] - paramArrayOfFloat1[(paramInt1 + 17)];
    float f8 = paramArrayOfFloat1[(paramInt1 + 8)] + paramArrayOfFloat1[(paramInt1 + 24)];
    float f9 = paramArrayOfFloat1[(paramInt1 + 9)] + paramArrayOfFloat1[(paramInt1 + 25)];
    float f10 = paramArrayOfFloat1[(paramInt1 + 8)] - paramArrayOfFloat1[(paramInt1 + 24)];
    float f11 = paramArrayOfFloat1[(paramInt1 + 9)] - paramArrayOfFloat1[(paramInt1 + 25)];
    float f12 = f4 + f8;
    float f13 = f5 + f9;
    float f20 = f4 - f8;
    float f21 = f5 - f9;
    float f28 = f6 - f11;
    float f29 = f7 + f10;
    float f36 = f6 + f11;
    float f37 = f7 - f10;
    f4 = paramArrayOfFloat1[(paramInt1 + 2)] + paramArrayOfFloat1[(paramInt1 + 18)];
    f5 = paramArrayOfFloat1[(paramInt1 + 3)] + paramArrayOfFloat1[(paramInt1 + 19)];
    f6 = paramArrayOfFloat1[(paramInt1 + 2)] - paramArrayOfFloat1[(paramInt1 + 18)];
    f7 = paramArrayOfFloat1[(paramInt1 + 3)] - paramArrayOfFloat1[(paramInt1 + 19)];
    f8 = paramArrayOfFloat1[(paramInt1 + 10)] + paramArrayOfFloat1[(paramInt1 + 26)];
    f9 = paramArrayOfFloat1[(paramInt1 + 11)] + paramArrayOfFloat1[(paramInt1 + 27)];
    f10 = paramArrayOfFloat1[(paramInt1 + 10)] - paramArrayOfFloat1[(paramInt1 + 26)];
    f11 = paramArrayOfFloat1[(paramInt1 + 11)] - paramArrayOfFloat1[(paramInt1 + 27)];
    float f14 = f4 + f8;
    float f15 = f5 + f9;
    float f22 = f4 - f8;
    float f23 = f5 - f9;
    f4 = f6 - f11;
    f5 = f7 + f10;
    float f30 = f2 * f4 - f3 * f5;
    float f31 = f2 * f5 + f3 * f4;
    f4 = f6 + f11;
    f5 = f7 - f10;
    float f38 = f3 * f4 - f2 * f5;
    float f39 = f3 * f5 + f2 * f4;
    f4 = paramArrayOfFloat1[(paramInt1 + 4)] + paramArrayOfFloat1[(paramInt1 + 20)];
    f5 = paramArrayOfFloat1[(paramInt1 + 5)] + paramArrayOfFloat1[(paramInt1 + 21)];
    f6 = paramArrayOfFloat1[(paramInt1 + 4)] - paramArrayOfFloat1[(paramInt1 + 20)];
    f7 = paramArrayOfFloat1[(paramInt1 + 5)] - paramArrayOfFloat1[(paramInt1 + 21)];
    f8 = paramArrayOfFloat1[(paramInt1 + 12)] + paramArrayOfFloat1[(paramInt1 + 28)];
    f9 = paramArrayOfFloat1[(paramInt1 + 13)] + paramArrayOfFloat1[(paramInt1 + 29)];
    f10 = paramArrayOfFloat1[(paramInt1 + 12)] - paramArrayOfFloat1[(paramInt1 + 28)];
    f11 = paramArrayOfFloat1[(paramInt1 + 13)] - paramArrayOfFloat1[(paramInt1 + 29)];
    float f16 = f4 + f8;
    float f17 = f5 + f9;
    float f24 = f4 - f8;
    float f25 = f5 - f9;
    f4 = f6 - f11;
    f5 = f7 + f10;
    float f32 = f1 * (f4 - f5);
    float f33 = f1 * (f5 + f4);
    f4 = f6 + f11;
    f5 = f7 - f10;
    float f40 = f1 * (f4 + f5);
    float f41 = f1 * (f5 - f4);
    f4 = paramArrayOfFloat1[(paramInt1 + 6)] + paramArrayOfFloat1[(paramInt1 + 22)];
    f5 = paramArrayOfFloat1[(paramInt1 + 7)] + paramArrayOfFloat1[(paramInt1 + 23)];
    f6 = paramArrayOfFloat1[(paramInt1 + 6)] - paramArrayOfFloat1[(paramInt1 + 22)];
    f7 = paramArrayOfFloat1[(paramInt1 + 7)] - paramArrayOfFloat1[(paramInt1 + 23)];
    f8 = paramArrayOfFloat1[(paramInt1 + 14)] + paramArrayOfFloat1[(paramInt1 + 30)];
    f9 = paramArrayOfFloat1[(paramInt1 + 15)] + paramArrayOfFloat1[(paramInt1 + 31)];
    f10 = paramArrayOfFloat1[(paramInt1 + 14)] - paramArrayOfFloat1[(paramInt1 + 30)];
    f11 = paramArrayOfFloat1[(paramInt1 + 15)] - paramArrayOfFloat1[(paramInt1 + 31)];
    float f18 = f4 + f8;
    float f19 = f5 + f9;
    float f26 = f4 - f8;
    float f27 = f5 - f9;
    f4 = f6 - f11;
    f5 = f7 + f10;
    float f34 = f3 * f4 - f2 * f5;
    float f35 = f3 * f5 + f2 * f4;
    f4 = f6 + f11;
    f5 = f7 - f10;
    float f42 = f2 * f4 - f3 * f5;
    float f43 = f2 * f5 + f3 * f4;
    f4 = f36 - f40;
    f5 = f37 - f41;
    f6 = f36 + f40;
    f7 = f37 + f41;
    f8 = f38 - f42;
    f9 = f39 - f43;
    f10 = f38 + f42;
    f11 = f39 + f43;
    paramArrayOfFloat1[(paramInt1 + 24)] = (f4 + f8);
    paramArrayOfFloat1[(paramInt1 + 25)] = (f5 + f9);
    paramArrayOfFloat1[(paramInt1 + 26)] = (f4 - f8);
    paramArrayOfFloat1[(paramInt1 + 27)] = (f5 - f9);
    paramArrayOfFloat1[(paramInt1 + 28)] = (f6 - f11);
    paramArrayOfFloat1[(paramInt1 + 29)] = (f7 + f10);
    paramArrayOfFloat1[(paramInt1 + 30)] = (f6 + f11);
    paramArrayOfFloat1[(paramInt1 + 31)] = (f7 - f10);
    f4 = f28 + f32;
    f5 = f29 + f33;
    f6 = f28 - f32;
    f7 = f29 - f33;
    f8 = f30 + f34;
    f9 = f31 + f35;
    f10 = f30 - f34;
    f11 = f31 - f35;
    paramArrayOfFloat1[(paramInt1 + 16)] = (f4 + f8);
    paramArrayOfFloat1[(paramInt1 + 17)] = (f5 + f9);
    paramArrayOfFloat1[(paramInt1 + 18)] = (f4 - f8);
    paramArrayOfFloat1[(paramInt1 + 19)] = (f5 - f9);
    paramArrayOfFloat1[(paramInt1 + 20)] = (f6 - f11);
    paramArrayOfFloat1[(paramInt1 + 21)] = (f7 + f10);
    paramArrayOfFloat1[(paramInt1 + 22)] = (f6 + f11);
    paramArrayOfFloat1[(paramInt1 + 23)] = (f7 - f10);
    f4 = f22 - f27;
    f5 = f23 + f26;
    f8 = f1 * (f4 - f5);
    f9 = f1 * (f5 + f4);
    f4 = f22 + f27;
    f5 = f23 - f26;
    f10 = f1 * (f4 - f5);
    f11 = f1 * (f5 + f4);
    f4 = f20 - f25;
    f5 = f21 + f24;
    f6 = f20 + f25;
    f7 = f21 - f24;
    paramArrayOfFloat1[(paramInt1 + 8)] = (f4 + f8);
    paramArrayOfFloat1[(paramInt1 + 9)] = (f5 + f9);
    paramArrayOfFloat1[(paramInt1 + 10)] = (f4 - f8);
    paramArrayOfFloat1[(paramInt1 + 11)] = (f5 - f9);
    paramArrayOfFloat1[(paramInt1 + 12)] = (f6 - f11);
    paramArrayOfFloat1[(paramInt1 + 13)] = (f7 + f10);
    paramArrayOfFloat1[(paramInt1 + 14)] = (f6 + f11);
    paramArrayOfFloat1[(paramInt1 + 15)] = (f7 - f10);
    f4 = f12 + f16;
    f5 = f13 + f17;
    f6 = f12 - f16;
    f7 = f13 - f17;
    f8 = f14 + f18;
    f9 = f15 + f19;
    f10 = f14 - f18;
    f11 = f15 - f19;
    paramArrayOfFloat1[paramInt1] = (f4 + f8);
    paramArrayOfFloat1[(paramInt1 + 1)] = (f5 + f9);
    paramArrayOfFloat1[(paramInt1 + 2)] = (f4 - f8);
    paramArrayOfFloat1[(paramInt1 + 3)] = (f5 - f9);
    paramArrayOfFloat1[(paramInt1 + 4)] = (f6 - f11);
    paramArrayOfFloat1[(paramInt1 + 5)] = (f7 + f10);
    paramArrayOfFloat1[(paramInt1 + 6)] = (f6 + f11);
    paramArrayOfFloat1[(paramInt1 + 7)] = (f7 - f10);
  }

  private void cftf162(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2)
  {
    float f1 = paramArrayOfFloat2[(paramInt2 + 1)];
    float f2 = paramArrayOfFloat2[(paramInt2 + 4)];
    float f3 = paramArrayOfFloat2[(paramInt2 + 5)];
    float f6 = paramArrayOfFloat2[(paramInt2 + 6)];
    float f7 = -paramArrayOfFloat2[(paramInt2 + 7)];
    float f4 = paramArrayOfFloat2[(paramInt2 + 8)];
    float f5 = paramArrayOfFloat2[(paramInt2 + 9)];
    float f10 = paramArrayOfFloat1[paramInt1] - paramArrayOfFloat1[(paramInt1 + 17)];
    float f11 = paramArrayOfFloat1[(paramInt1 + 1)] + paramArrayOfFloat1[(paramInt1 + 16)];
    float f8 = paramArrayOfFloat1[(paramInt1 + 8)] - paramArrayOfFloat1[(paramInt1 + 25)];
    float f9 = paramArrayOfFloat1[(paramInt1 + 9)] + paramArrayOfFloat1[(paramInt1 + 24)];
    float f12 = f1 * (f8 - f9);
    float f13 = f1 * (f9 + f8);
    float f14 = f10 + f12;
    float f15 = f11 + f13;
    float f22 = f10 - f12;
    float f23 = f11 - f13;
    f10 = paramArrayOfFloat1[paramInt1] + paramArrayOfFloat1[(paramInt1 + 17)];
    f11 = paramArrayOfFloat1[(paramInt1 + 1)] - paramArrayOfFloat1[(paramInt1 + 16)];
    f8 = paramArrayOfFloat1[(paramInt1 + 8)] + paramArrayOfFloat1[(paramInt1 + 25)];
    f9 = paramArrayOfFloat1[(paramInt1 + 9)] - paramArrayOfFloat1[(paramInt1 + 24)];
    f12 = f1 * (f8 - f9);
    f13 = f1 * (f9 + f8);
    float f30 = f10 - f13;
    float f31 = f11 + f12;
    float f38 = f10 + f13;
    float f39 = f11 - f12;
    f8 = paramArrayOfFloat1[(paramInt1 + 2)] - paramArrayOfFloat1[(paramInt1 + 19)];
    f9 = paramArrayOfFloat1[(paramInt1 + 3)] + paramArrayOfFloat1[(paramInt1 + 18)];
    f10 = f2 * f8 - f3 * f9;
    f11 = f2 * f9 + f3 * f8;
    f8 = paramArrayOfFloat1[(paramInt1 + 10)] - paramArrayOfFloat1[(paramInt1 + 27)];
    f9 = paramArrayOfFloat1[(paramInt1 + 11)] + paramArrayOfFloat1[(paramInt1 + 26)];
    f12 = f7 * f8 - f6 * f9;
    f13 = f7 * f9 + f6 * f8;
    float f16 = f10 + f12;
    float f17 = f11 + f13;
    float f24 = f10 - f12;
    float f25 = f11 - f13;
    f8 = paramArrayOfFloat1[(paramInt1 + 2)] + paramArrayOfFloat1[(paramInt1 + 19)];
    f9 = paramArrayOfFloat1[(paramInt1 + 3)] - paramArrayOfFloat1[(paramInt1 + 18)];
    f10 = f6 * f8 - f7 * f9;
    f11 = f6 * f9 + f7 * f8;
    f8 = paramArrayOfFloat1[(paramInt1 + 10)] + paramArrayOfFloat1[(paramInt1 + 27)];
    f9 = paramArrayOfFloat1[(paramInt1 + 11)] - paramArrayOfFloat1[(paramInt1 + 26)];
    f12 = f2 * f8 + f3 * f9;
    f13 = f2 * f9 - f3 * f8;
    float f32 = f10 - f12;
    float f33 = f11 - f13;
    float f40 = f10 + f12;
    float f41 = f11 + f13;
    f8 = paramArrayOfFloat1[(paramInt1 + 4)] - paramArrayOfFloat1[(paramInt1 + 21)];
    f9 = paramArrayOfFloat1[(paramInt1 + 5)] + paramArrayOfFloat1[(paramInt1 + 20)];
    f10 = f4 * f8 - f5 * f9;
    f11 = f4 * f9 + f5 * f8;
    f8 = paramArrayOfFloat1[(paramInt1 + 12)] - paramArrayOfFloat1[(paramInt1 + 29)];
    f9 = paramArrayOfFloat1[(paramInt1 + 13)] + paramArrayOfFloat1[(paramInt1 + 28)];
    f12 = f5 * f8 - f4 * f9;
    f13 = f5 * f9 + f4 * f8;
    float f18 = f10 + f12;
    float f19 = f11 + f13;
    float f26 = f10 - f12;
    float f27 = f11 - f13;
    f8 = paramArrayOfFloat1[(paramInt1 + 4)] + paramArrayOfFloat1[(paramInt1 + 21)];
    f9 = paramArrayOfFloat1[(paramInt1 + 5)] - paramArrayOfFloat1[(paramInt1 + 20)];
    f10 = f5 * f8 - f4 * f9;
    f11 = f5 * f9 + f4 * f8;
    f8 = paramArrayOfFloat1[(paramInt1 + 12)] + paramArrayOfFloat1[(paramInt1 + 29)];
    f9 = paramArrayOfFloat1[(paramInt1 + 13)] - paramArrayOfFloat1[(paramInt1 + 28)];
    f12 = f4 * f8 - f5 * f9;
    f13 = f4 * f9 + f5 * f8;
    float f34 = f10 - f12;
    float f35 = f11 - f13;
    float f42 = f10 + f12;
    float f43 = f11 + f13;
    f8 = paramArrayOfFloat1[(paramInt1 + 6)] - paramArrayOfFloat1[(paramInt1 + 23)];
    f9 = paramArrayOfFloat1[(paramInt1 + 7)] + paramArrayOfFloat1[(paramInt1 + 22)];
    f10 = f6 * f8 - f7 * f9;
    f11 = f6 * f9 + f7 * f8;
    f8 = paramArrayOfFloat1[(paramInt1 + 14)] - paramArrayOfFloat1[(paramInt1 + 31)];
    f9 = paramArrayOfFloat1[(paramInt1 + 15)] + paramArrayOfFloat1[(paramInt1 + 30)];
    f12 = f3 * f8 - f2 * f9;
    f13 = f3 * f9 + f2 * f8;
    float f20 = f10 + f12;
    float f21 = f11 + f13;
    float f28 = f10 - f12;
    float f29 = f11 - f13;
    f8 = paramArrayOfFloat1[(paramInt1 + 6)] + paramArrayOfFloat1[(paramInt1 + 23)];
    f9 = paramArrayOfFloat1[(paramInt1 + 7)] - paramArrayOfFloat1[(paramInt1 + 22)];
    f10 = f3 * f8 + f2 * f9;
    f11 = f3 * f9 - f2 * f8;
    f8 = paramArrayOfFloat1[(paramInt1 + 14)] + paramArrayOfFloat1[(paramInt1 + 31)];
    f9 = paramArrayOfFloat1[(paramInt1 + 15)] - paramArrayOfFloat1[(paramInt1 + 30)];
    f12 = f7 * f8 - f6 * f9;
    f13 = f7 * f9 + f6 * f8;
    float f36 = f10 + f12;
    float f37 = f11 + f13;
    float f44 = f10 - f12;
    float f45 = f11 - f13;
    f10 = f14 + f18;
    f11 = f15 + f19;
    f12 = f16 + f20;
    f13 = f17 + f21;
    paramArrayOfFloat1[paramInt1] = (f10 + f12);
    paramArrayOfFloat1[(paramInt1 + 1)] = (f11 + f13);
    paramArrayOfFloat1[(paramInt1 + 2)] = (f10 - f12);
    paramArrayOfFloat1[(paramInt1 + 3)] = (f11 - f13);
    f10 = f14 - f18;
    f11 = f15 - f19;
    f12 = f16 - f20;
    f13 = f17 - f21;
    paramArrayOfFloat1[(paramInt1 + 4)] = (f10 - f13);
    paramArrayOfFloat1[(paramInt1 + 5)] = (f11 + f12);
    paramArrayOfFloat1[(paramInt1 + 6)] = (f10 + f13);
    paramArrayOfFloat1[(paramInt1 + 7)] = (f11 - f12);
    f10 = f22 - f27;
    f11 = f23 + f26;
    f8 = f24 - f29;
    f9 = f25 + f28;
    f12 = f1 * (f8 - f9);
    f13 = f1 * (f9 + f8);
    paramArrayOfFloat1[(paramInt1 + 8)] = (f10 + f12);
    paramArrayOfFloat1[(paramInt1 + 9)] = (f11 + f13);
    paramArrayOfFloat1[(paramInt1 + 10)] = (f10 - f12);
    paramArrayOfFloat1[(paramInt1 + 11)] = (f11 - f13);
    f10 = f22 + f27;
    f11 = f23 - f26;
    f8 = f24 + f29;
    f9 = f25 - f28;
    f12 = f1 * (f8 - f9);
    f13 = f1 * (f9 + f8);
    paramArrayOfFloat1[(paramInt1 + 12)] = (f10 - f13);
    paramArrayOfFloat1[(paramInt1 + 13)] = (f11 + f12);
    paramArrayOfFloat1[(paramInt1 + 14)] = (f10 + f13);
    paramArrayOfFloat1[(paramInt1 + 15)] = (f11 - f12);
    f10 = f30 + f34;
    f11 = f31 + f35;
    f12 = f32 - f36;
    f13 = f33 - f37;
    paramArrayOfFloat1[(paramInt1 + 16)] = (f10 + f12);
    paramArrayOfFloat1[(paramInt1 + 17)] = (f11 + f13);
    paramArrayOfFloat1[(paramInt1 + 18)] = (f10 - f12);
    paramArrayOfFloat1[(paramInt1 + 19)] = (f11 - f13);
    f10 = f30 - f34;
    f11 = f31 - f35;
    f12 = f32 + f36;
    f13 = f33 + f37;
    paramArrayOfFloat1[(paramInt1 + 20)] = (f10 - f13);
    paramArrayOfFloat1[(paramInt1 + 21)] = (f11 + f12);
    paramArrayOfFloat1[(paramInt1 + 22)] = (f10 + f13);
    paramArrayOfFloat1[(paramInt1 + 23)] = (f11 - f12);
    f10 = f38 - f43;
    f11 = f39 + f42;
    f8 = f40 + f45;
    f9 = f41 - f44;
    f12 = f1 * (f8 - f9);
    f13 = f1 * (f9 + f8);
    paramArrayOfFloat1[(paramInt1 + 24)] = (f10 + f12);
    paramArrayOfFloat1[(paramInt1 + 25)] = (f11 + f13);
    paramArrayOfFloat1[(paramInt1 + 26)] = (f10 - f12);
    paramArrayOfFloat1[(paramInt1 + 27)] = (f11 - f13);
    f10 = f38 + f43;
    f11 = f39 - f42;
    f8 = f40 - f45;
    f9 = f41 + f44;
    f12 = f1 * (f8 - f9);
    f13 = f1 * (f9 + f8);
    paramArrayOfFloat1[(paramInt1 + 28)] = (f10 - f13);
    paramArrayOfFloat1[(paramInt1 + 29)] = (f11 + f12);
    paramArrayOfFloat1[(paramInt1 + 30)] = (f10 + f13);
    paramArrayOfFloat1[(paramInt1 + 31)] = (f11 - f12);
  }

  private void cftf081(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2)
  {
    float f1 = paramArrayOfFloat2[(paramInt2 + 1)];
    float f2 = paramArrayOfFloat1[paramInt1] + paramArrayOfFloat1[(paramInt1 + 8)];
    float f3 = paramArrayOfFloat1[(paramInt1 + 1)] + paramArrayOfFloat1[(paramInt1 + 9)];
    float f4 = paramArrayOfFloat1[paramInt1] - paramArrayOfFloat1[(paramInt1 + 8)];
    float f5 = paramArrayOfFloat1[(paramInt1 + 1)] - paramArrayOfFloat1[(paramInt1 + 9)];
    float f6 = paramArrayOfFloat1[(paramInt1 + 4)] + paramArrayOfFloat1[(paramInt1 + 12)];
    float f7 = paramArrayOfFloat1[(paramInt1 + 5)] + paramArrayOfFloat1[(paramInt1 + 13)];
    float f8 = paramArrayOfFloat1[(paramInt1 + 4)] - paramArrayOfFloat1[(paramInt1 + 12)];
    float f9 = paramArrayOfFloat1[(paramInt1 + 5)] - paramArrayOfFloat1[(paramInt1 + 13)];
    float f10 = f2 + f6;
    float f11 = f3 + f7;
    float f14 = f2 - f6;
    float f15 = f3 - f7;
    float f12 = f4 - f9;
    float f13 = f5 + f8;
    float f16 = f4 + f9;
    float f17 = f5 - f8;
    f2 = paramArrayOfFloat1[(paramInt1 + 2)] + paramArrayOfFloat1[(paramInt1 + 10)];
    f3 = paramArrayOfFloat1[(paramInt1 + 3)] + paramArrayOfFloat1[(paramInt1 + 11)];
    f4 = paramArrayOfFloat1[(paramInt1 + 2)] - paramArrayOfFloat1[(paramInt1 + 10)];
    f5 = paramArrayOfFloat1[(paramInt1 + 3)] - paramArrayOfFloat1[(paramInt1 + 11)];
    f6 = paramArrayOfFloat1[(paramInt1 + 6)] + paramArrayOfFloat1[(paramInt1 + 14)];
    f7 = paramArrayOfFloat1[(paramInt1 + 7)] + paramArrayOfFloat1[(paramInt1 + 15)];
    f8 = paramArrayOfFloat1[(paramInt1 + 6)] - paramArrayOfFloat1[(paramInt1 + 14)];
    f9 = paramArrayOfFloat1[(paramInt1 + 7)] - paramArrayOfFloat1[(paramInt1 + 15)];
    float f18 = f2 + f6;
    float f19 = f3 + f7;
    float f22 = f2 - f6;
    float f23 = f3 - f7;
    f2 = f4 - f9;
    f3 = f5 + f8;
    f6 = f4 + f9;
    f7 = f5 - f8;
    float f20 = f1 * (f2 - f3);
    float f21 = f1 * (f2 + f3);
    float f24 = f1 * (f6 - f7);
    float f25 = f1 * (f6 + f7);
    paramArrayOfFloat1[(paramInt1 + 8)] = (f12 + f20);
    paramArrayOfFloat1[(paramInt1 + 9)] = (f13 + f21);
    paramArrayOfFloat1[(paramInt1 + 10)] = (f12 - f20);
    paramArrayOfFloat1[(paramInt1 + 11)] = (f13 - f21);
    paramArrayOfFloat1[(paramInt1 + 12)] = (f16 - f25);
    paramArrayOfFloat1[(paramInt1 + 13)] = (f17 + f24);
    paramArrayOfFloat1[(paramInt1 + 14)] = (f16 + f25);
    paramArrayOfFloat1[(paramInt1 + 15)] = (f17 - f24);
    paramArrayOfFloat1[paramInt1] = (f10 + f18);
    paramArrayOfFloat1[(paramInt1 + 1)] = (f11 + f19);
    paramArrayOfFloat1[(paramInt1 + 2)] = (f10 - f18);
    paramArrayOfFloat1[(paramInt1 + 3)] = (f11 - f19);
    paramArrayOfFloat1[(paramInt1 + 4)] = (f14 - f23);
    paramArrayOfFloat1[(paramInt1 + 5)] = (f15 + f22);
    paramArrayOfFloat1[(paramInt1 + 6)] = (f14 + f23);
    paramArrayOfFloat1[(paramInt1 + 7)] = (f15 - f22);
  }

  private void cftf082(float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, int paramInt2)
  {
    float f1 = paramArrayOfFloat2[(paramInt2 + 1)];
    float f2 = paramArrayOfFloat2[(paramInt2 + 2)];
    float f3 = paramArrayOfFloat2[(paramInt2 + 3)];
    float f8 = paramArrayOfFloat1[paramInt1] - paramArrayOfFloat1[(paramInt1 + 9)];
    float f9 = paramArrayOfFloat1[(paramInt1 + 1)] + paramArrayOfFloat1[(paramInt1 + 8)];
    float f10 = paramArrayOfFloat1[paramInt1] + paramArrayOfFloat1[(paramInt1 + 9)];
    float f11 = paramArrayOfFloat1[(paramInt1 + 1)] - paramArrayOfFloat1[(paramInt1 + 8)];
    float f4 = paramArrayOfFloat1[(paramInt1 + 4)] - paramArrayOfFloat1[(paramInt1 + 13)];
    float f5 = paramArrayOfFloat1[(paramInt1 + 5)] + paramArrayOfFloat1[(paramInt1 + 12)];
    float f12 = f1 * (f4 - f5);
    float f13 = f1 * (f5 + f4);
    f4 = paramArrayOfFloat1[(paramInt1 + 4)] + paramArrayOfFloat1[(paramInt1 + 13)];
    f5 = paramArrayOfFloat1[(paramInt1 + 5)] - paramArrayOfFloat1[(paramInt1 + 12)];
    float f14 = f1 * (f4 - f5);
    float f15 = f1 * (f5 + f4);
    f4 = paramArrayOfFloat1[(paramInt1 + 2)] - paramArrayOfFloat1[(paramInt1 + 11)];
    f5 = paramArrayOfFloat1[(paramInt1 + 3)] + paramArrayOfFloat1[(paramInt1 + 10)];
    float f16 = f2 * f4 - f3 * f5;
    float f17 = f2 * f5 + f3 * f4;
    f4 = paramArrayOfFloat1[(paramInt1 + 2)] + paramArrayOfFloat1[(paramInt1 + 11)];
    f5 = paramArrayOfFloat1[(paramInt1 + 3)] - paramArrayOfFloat1[(paramInt1 + 10)];
    float f18 = f3 * f4 - f2 * f5;
    float f19 = f3 * f5 + f2 * f4;
    f4 = paramArrayOfFloat1[(paramInt1 + 6)] - paramArrayOfFloat1[(paramInt1 + 15)];
    f5 = paramArrayOfFloat1[(paramInt1 + 7)] + paramArrayOfFloat1[(paramInt1 + 14)];
    float f20 = f3 * f4 - f2 * f5;
    float f21 = f3 * f5 + f2 * f4;
    f4 = paramArrayOfFloat1[(paramInt1 + 6)] + paramArrayOfFloat1[(paramInt1 + 15)];
    f5 = paramArrayOfFloat1[(paramInt1 + 7)] - paramArrayOfFloat1[(paramInt1 + 14)];
    float f22 = f2 * f4 - f3 * f5;
    float f23 = f2 * f5 + f3 * f4;
    f4 = f8 + f12;
    f5 = f9 + f13;
    float f6 = f16 + f20;
    float f7 = f17 + f21;
    paramArrayOfFloat1[paramInt1] = (f4 + f6);
    paramArrayOfFloat1[(paramInt1 + 1)] = (f5 + f7);
    paramArrayOfFloat1[(paramInt1 + 2)] = (f4 - f6);
    paramArrayOfFloat1[(paramInt1 + 3)] = (f5 - f7);
    f4 = f8 - f12;
    f5 = f9 - f13;
    f6 = f16 - f20;
    f7 = f17 - f21;
    paramArrayOfFloat1[(paramInt1 + 4)] = (f4 - f7);
    paramArrayOfFloat1[(paramInt1 + 5)] = (f5 + f6);
    paramArrayOfFloat1[(paramInt1 + 6)] = (f4 + f7);
    paramArrayOfFloat1[(paramInt1 + 7)] = (f5 - f6);
    f4 = f10 - f15;
    f5 = f11 + f14;
    f6 = f18 - f22;
    f7 = f19 - f23;
    paramArrayOfFloat1[(paramInt1 + 8)] = (f4 + f6);
    paramArrayOfFloat1[(paramInt1 + 9)] = (f5 + f7);
    paramArrayOfFloat1[(paramInt1 + 10)] = (f4 - f6);
    paramArrayOfFloat1[(paramInt1 + 11)] = (f5 - f7);
    f4 = f10 + f15;
    f5 = f11 - f14;
    f6 = f18 + f22;
    f7 = f19 + f23;
    paramArrayOfFloat1[(paramInt1 + 12)] = (f4 - f7);
    paramArrayOfFloat1[(paramInt1 + 13)] = (f5 + f6);
    paramArrayOfFloat1[(paramInt1 + 14)] = (f4 + f7);
    paramArrayOfFloat1[(paramInt1 + 15)] = (f5 - f6);
  }

  private void cftf040(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[paramInt] + paramArrayOfFloat[(paramInt + 4)];
    float f2 = paramArrayOfFloat[(paramInt + 1)] + paramArrayOfFloat[(paramInt + 5)];
    float f3 = paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 4)];
    float f4 = paramArrayOfFloat[(paramInt + 1)] - paramArrayOfFloat[(paramInt + 5)];
    float f5 = paramArrayOfFloat[(paramInt + 2)] + paramArrayOfFloat[(paramInt + 6)];
    float f6 = paramArrayOfFloat[(paramInt + 3)] + paramArrayOfFloat[(paramInt + 7)];
    float f7 = paramArrayOfFloat[(paramInt + 2)] - paramArrayOfFloat[(paramInt + 6)];
    float f8 = paramArrayOfFloat[(paramInt + 3)] - paramArrayOfFloat[(paramInt + 7)];
    paramArrayOfFloat[paramInt] = (f1 + f5);
    paramArrayOfFloat[(paramInt + 1)] = (f2 + f6);
    paramArrayOfFloat[(paramInt + 2)] = (f3 - f8);
    paramArrayOfFloat[(paramInt + 3)] = (f4 + f7);
    paramArrayOfFloat[(paramInt + 4)] = (f1 - f5);
    paramArrayOfFloat[(paramInt + 5)] = (f2 - f6);
    paramArrayOfFloat[(paramInt + 6)] = (f3 + f8);
    paramArrayOfFloat[(paramInt + 7)] = (f4 - f7);
  }

  private void cftb040(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[paramInt] + paramArrayOfFloat[(paramInt + 4)];
    float f2 = paramArrayOfFloat[(paramInt + 1)] + paramArrayOfFloat[(paramInt + 5)];
    float f3 = paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 4)];
    float f4 = paramArrayOfFloat[(paramInt + 1)] - paramArrayOfFloat[(paramInt + 5)];
    float f5 = paramArrayOfFloat[(paramInt + 2)] + paramArrayOfFloat[(paramInt + 6)];
    float f6 = paramArrayOfFloat[(paramInt + 3)] + paramArrayOfFloat[(paramInt + 7)];
    float f7 = paramArrayOfFloat[(paramInt + 2)] - paramArrayOfFloat[(paramInt + 6)];
    float f8 = paramArrayOfFloat[(paramInt + 3)] - paramArrayOfFloat[(paramInt + 7)];
    paramArrayOfFloat[paramInt] = (f1 + f5);
    paramArrayOfFloat[(paramInt + 1)] = (f2 + f6);
    paramArrayOfFloat[(paramInt + 2)] = (f3 + f8);
    paramArrayOfFloat[(paramInt + 3)] = (f4 - f7);
    paramArrayOfFloat[(paramInt + 4)] = (f1 - f5);
    paramArrayOfFloat[(paramInt + 5)] = (f2 - f6);
    paramArrayOfFloat[(paramInt + 6)] = (f3 - f8);
    paramArrayOfFloat[(paramInt + 7)] = (f4 + f7);
  }

  private void cftx020(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 2)];
    float f2 = -paramArrayOfFloat[(paramInt + 1)] + paramArrayOfFloat[(paramInt + 3)];
    paramArrayOfFloat[paramInt] += paramArrayOfFloat[(paramInt + 2)];
    paramArrayOfFloat[(paramInt + 1)] += paramArrayOfFloat[(paramInt + 3)];
    paramArrayOfFloat[(paramInt + 2)] = f1;
    paramArrayOfFloat[(paramInt + 3)] = f2;
  }

  private void cftxb020(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 2)];
    float f2 = paramArrayOfFloat[(paramInt + 1)] - paramArrayOfFloat[(paramInt + 3)];
    paramArrayOfFloat[paramInt] += paramArrayOfFloat[(paramInt + 2)];
    paramArrayOfFloat[(paramInt + 1)] += paramArrayOfFloat[(paramInt + 3)];
    paramArrayOfFloat[(paramInt + 2)] = f1;
    paramArrayOfFloat[(paramInt + 3)] = f2;
  }

  private void cftxc020(float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramArrayOfFloat[paramInt] - paramArrayOfFloat[(paramInt + 2)];
    float f2 = paramArrayOfFloat[(paramInt + 1)] + paramArrayOfFloat[(paramInt + 3)];
    paramArrayOfFloat[paramInt] += paramArrayOfFloat[(paramInt + 2)];
    paramArrayOfFloat[(paramInt + 1)] -= paramArrayOfFloat[(paramInt + 3)];
    paramArrayOfFloat[(paramInt + 2)] = f1;
    paramArrayOfFloat[(paramInt + 3)] = f2;
  }

  private void rftfsub(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4)
  {
    int i1 = paramInt1 >> 1;
    int m = 2 * paramInt3 / i1;
    int k = 0;
    for (int i = 2; i < i1; i += 2)
    {
      int j = paramInt1 - i;
      k += m;
      float f1 = (float)(0.5D - paramArrayOfFloat2[(paramInt4 + paramInt3 - k)]);
      float f2 = paramArrayOfFloat2[(paramInt4 + k)];
      int i2 = paramInt2 + i;
      int i3 = paramInt2 + j;
      float f3 = paramArrayOfFloat1[i2] - paramArrayOfFloat1[i3];
      float f4 = paramArrayOfFloat1[(i2 + 1)] + paramArrayOfFloat1[(i3 + 1)];
      float f5 = f1 * f3 - f2 * f4;
      float f6 = f1 * f4 + f2 * f3;
      paramArrayOfFloat1[i2] -= f5;
      paramArrayOfFloat1[(i2 + 1)] = (f6 - paramArrayOfFloat1[(i2 + 1)]);
      paramArrayOfFloat1[i3] += f5;
      paramArrayOfFloat1[(i3 + 1)] = (f6 - paramArrayOfFloat1[(i3 + 1)]);
    }
    paramArrayOfFloat1[(paramInt2 + i1 + 1)] = (-paramArrayOfFloat1[(paramInt2 + i1 + 1)]);
  }

  private void rftbsub(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4)
  {
    int i1 = paramInt1 >> 1;
    int m = 2 * paramInt3 / i1;
    int k = 0;
    for (int i = 2; i < i1; i += 2)
    {
      int j = paramInt1 - i;
      k += m;
      float f1 = (float)(0.5D - paramArrayOfFloat2[(paramInt4 + paramInt3 - k)]);
      float f2 = paramArrayOfFloat2[(paramInt4 + k)];
      int i2 = paramInt2 + i;
      int i3 = paramInt2 + j;
      float f3 = paramArrayOfFloat1[i2] - paramArrayOfFloat1[i3];
      float f4 = paramArrayOfFloat1[(i2 + 1)] + paramArrayOfFloat1[(i3 + 1)];
      float f5 = f1 * f3 - f2 * f4;
      float f6 = f1 * f4 + f2 * f3;
      paramArrayOfFloat1[i2] -= f5;
      paramArrayOfFloat1[(i2 + 1)] -= f6;
      paramArrayOfFloat1[i3] += f5;
      paramArrayOfFloat1[(i3 + 1)] -= f6;
    }
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.FloatFFT_1D
 * JD-Core Version:    0.6.1
 */