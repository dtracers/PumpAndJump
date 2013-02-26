package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DoubleFFT_2D
{
  private int n1;
  private int n2;
  private int[] ip;
  private double[] w;
  private double[] t;
  private DoubleFFT_1D fftn2;
  private DoubleFFT_1D fftn1;
  private int oldNthread;
  private int nt;

  public DoubleFFT_2D(int paramInt1, int paramInt2)
  {
    if ((!ConcurrencyUtils.isPowerOf2(paramInt1)) || (!ConcurrencyUtils.isPowerOf2(paramInt2)))
      throw new IllegalArgumentException("n1, n2 must be power of two numbers");
    if ((paramInt1 <= 1) || (paramInt2 <= 1))
      throw new IllegalArgumentException("n1, n2 must be greater than 1");
    this.n1 = paramInt1;
    this.n2 = paramInt2;
    this.ip = new int[2 + (int)Math.ceil(Math.sqrt(Math.max(paramInt1, paramInt2)))];
    this.w = new double[(int)Math.ceil(Math.max(Math.max(paramInt1 / 2, paramInt2 / 2), Math.max(paramInt1 / 2, paramInt2 / 4) + paramInt2 / 4))];
    this.fftn2 = new DoubleFFT_1D(paramInt2, this.ip, this.w);
    this.fftn1 = new DoubleFFT_1D(paramInt1, this.ip, this.w);
    this.oldNthread = ConcurrencyUtils.getNumberOfProcessors();
    this.nt = (8 * this.oldNthread * paramInt1);
    if (2 * paramInt2 == 4 * this.oldNthread)
      this.nt >>= 1;
    else if (2 * paramInt2 < 4 * this.oldNthread)
      this.nt >>= 2;
    this.t = new double[this.nt];
  }

  public void complexForward(double[] paramArrayOfDouble)
  {
    int m = this.n2;
    this.n2 = (2 * this.n2);
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    if (j > this.ip[0] << 2)
      makewt(j >> 2);
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, -1, paramArrayOfDouble, true);
      cdft2d_subth(-1, paramArrayOfDouble, true);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexForward(paramArrayOfDouble, k * this.n2);
      cdft2d_sub(-1, paramArrayOfDouble, true);
    }
    this.n2 = m;
  }

  public void complexForward(double[][] paramArrayOfDouble)
  {
    int m = this.n2;
    this.n2 = (2 * this.n2);
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    if (j > this.ip[0] << 2)
      makewt(j >> 2);
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, -1, paramArrayOfDouble, true);
      cdft2d_subth(-1, paramArrayOfDouble, true);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexForward(paramArrayOfDouble[k]);
      cdft2d_sub(-1, paramArrayOfDouble, true);
    }
    this.n2 = m;
  }

  public void complexInverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int m = this.n2;
    this.n2 = (2 * this.n2);
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    if (j > this.ip[0] << 2)
      makewt(j >> 2);
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, 1, paramArrayOfDouble, paramBoolean);
      cdft2d_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexInverse(paramArrayOfDouble, k * this.n2, paramBoolean);
      cdft2d_sub(1, paramArrayOfDouble, paramBoolean);
    }
    this.n2 = m;
  }

  public void complexInverse(double[][] paramArrayOfDouble, boolean paramBoolean)
  {
    int m = this.n2;
    this.n2 = (2 * this.n2);
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    if (j > this.ip[0] << 2)
      makewt(j >> 2);
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, 1, paramArrayOfDouble, paramBoolean);
      cdft2d_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexInverse(paramArrayOfDouble[k], paramBoolean);
      cdft2d_sub(1, paramArrayOfDouble, paramBoolean);
    }
    this.n2 = m;
  }

  public void realForward(double[] paramArrayOfDouble)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfDouble, true);
      cdft2d_subth(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfDouble, n * this.n2);
      cdft2d_sub(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
  }

  public void realForward(double[][] paramArrayOfDouble)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfDouble, true);
      cdft2d_subth(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfDouble[n]);
      cdft2d_sub(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
  }

  public void realForwardFull(double[] paramArrayOfDouble)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfDouble, true);
      cdft2d_subth(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfDouble, n * this.n2);
      cdft2d_sub(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    int i1 = 2 * this.n2;
    int i5 = this.n1 / 2;
    int i3;
    int i7;
    for (int i6 = this.n1 - 1; i6 >= 1; i6--)
    {
      int i2 = i6 * this.n2;
      i3 = 2 * i2;
      for (i7 = 0; i7 < this.n2; i7 += 2)
      {
        paramArrayOfDouble[(i3 + i7)] = paramArrayOfDouble[(i2 + i7)];
        paramArrayOfDouble[(i2 + i7)] = 0.0D;
        paramArrayOfDouble[(i3 + i7 + 1)] = paramArrayOfDouble[(i2 + i7 + 1)];
        paramArrayOfDouble[(i2 + i7 + 1)] = 0.0D;
      }
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      int i4;
      for (i6 = 1; i6 < i5; i6++)
      {
        i3 = i6 * i1;
        i4 = (this.n1 - i6) * i1;
        paramArrayOfDouble[(i3 + this.n2)] = paramArrayOfDouble[(i4 + 1)];
        paramArrayOfDouble[(i3 + this.n2 + 1)] = (-paramArrayOfDouble[i4]);
      }
      for (i6 = 1; i6 < i5; i6++)
        for (i7 = this.n2 + 2; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1;
          i4 = (this.n1 - i6) * i1;
          paramArrayOfDouble[(i3 + i7)] = paramArrayOfDouble[(i4 + i1 - i7)];
          paramArrayOfDouble[(i3 + i7 + 1)] = (-paramArrayOfDouble[(i4 + i1 - i7 + 1)]);
        }
      for (i6 = 0; i6 <= this.n1 / 2; i6++)
        for (i7 = 0; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1 + i7;
          i4 = (this.n1 - i6) % this.n1 * i1 + (i1 - i7) % i1;
          paramArrayOfDouble[i4] = paramArrayOfDouble[i3];
          paramArrayOfDouble[(i4 + 1)] = (-paramArrayOfDouble[(i3 + 1)]);
        }
    }
    paramArrayOfDouble[this.n2] = (-paramArrayOfDouble[1]);
    paramArrayOfDouble[1] = 0.0D;
    paramArrayOfDouble[(i5 * i1 + this.n2)] = (-paramArrayOfDouble[(i5 * i1 + 1)]);
    paramArrayOfDouble[(i5 * i1 + 1)] = 0.0D;
    paramArrayOfDouble[(i5 * i1 + this.n2 + 1)] = 0.0D;
  }

  public void realForwardFull(double[][] paramArrayOfDouble)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfDouble, true);
      cdft2d_subth(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfDouble[n]);
      cdft2d_sub(-1, paramArrayOfDouble, true);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    int i1 = 2 * this.n2;
    int i2 = this.n1 / 2;
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      for (int i3 = 1; i3 < i2; i3++)
      {
        paramArrayOfDouble[i3][this.n2] = paramArrayOfDouble[(this.n1 - i3)][1];
        paramArrayOfDouble[i3][(this.n2 + 1)] = (-paramArrayOfDouble[(this.n1 - i3)][0]);
      }
      int i4;
      for (i3 = 1; i3 < i2; i3++)
        for (i4 = this.n2 + 2; i4 < i1; i4 += 2)
        {
          paramArrayOfDouble[i3][i4] = paramArrayOfDouble[(this.n1 - i3)][(i1 - i4)];
          paramArrayOfDouble[i3][(i4 + 1)] = (-paramArrayOfDouble[(this.n1 - i3)][(i1 - i4 + 1)]);
        }
      for (i3 = 0; i3 <= this.n1 / 2; i3++)
        for (i4 = 0; i4 < i1; i4 += 2)
        {
          paramArrayOfDouble[((this.n1 - i3) % this.n1)][((i1 - i4) % i1)] = paramArrayOfDouble[i3][i4];
          paramArrayOfDouble[((this.n1 - i3) % this.n1)][((i1 - i4) % i1 + 1)] = (-paramArrayOfDouble[i3][(i4 + 1)]);
        }
    }
    paramArrayOfDouble[0][this.n2] = (-paramArrayOfDouble[0][1]);
    paramArrayOfDouble[0][1] = 0.0D;
    paramArrayOfDouble[i2][this.n2] = (-paramArrayOfDouble[i2][1]);
    paramArrayOfDouble[i2][1] = 0.0D;
    paramArrayOfDouble[i2][(this.n2 + 1)] = 0.0D;
  }

  public void realInverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      rdft2d_sub(-1, paramArrayOfDouble);
      cdft2d_subth(1, paramArrayOfDouble, paramBoolean);
      xdft2d0_subth1(1, -1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      rdft2d_sub(-1, paramArrayOfDouble);
      cdft2d_sub(1, paramArrayOfDouble, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse(paramArrayOfDouble, n * this.n2, paramBoolean);
    }
  }

  public void realInverse(double[][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      rdft2d_sub(-1, paramArrayOfDouble);
      cdft2d_subth(1, paramArrayOfDouble, paramBoolean);
      xdft2d0_subth1(1, -1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      rdft2d_sub(-1, paramArrayOfDouble);
      cdft2d_sub(1, paramArrayOfDouble, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse(paramArrayOfDouble[n], paramBoolean);
    }
  }

  public void realInverseFull(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth2(1, -1, paramArrayOfDouble, paramBoolean);
      cdft2d_subth(1, paramArrayOfDouble, paramBoolean);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse2(paramArrayOfDouble, n * this.n2, paramBoolean);
      cdft2d_sub(1, paramArrayOfDouble, paramBoolean);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    int i1 = 2 * this.n2;
    int i5 = this.n1 / 2;
    int i3;
    int i7;
    for (int i6 = this.n1 - 1; i6 >= 1; i6--)
    {
      int i2 = i6 * this.n2;
      i3 = 2 * i2;
      for (i7 = 0; i7 < this.n2; i7 += 2)
      {
        paramArrayOfDouble[(i3 + i7)] = paramArrayOfDouble[(i2 + i7)];
        paramArrayOfDouble[(i2 + i7)] = 0.0D;
        paramArrayOfDouble[(i3 + i7 + 1)] = paramArrayOfDouble[(i2 + i7 + 1)];
        paramArrayOfDouble[(i2 + i7 + 1)] = 0.0D;
      }
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      int i4;
      for (i6 = 1; i6 < i5; i6++)
      {
        i3 = i6 * i1;
        i4 = (this.n1 - i6) * i1;
        paramArrayOfDouble[(i3 + this.n2)] = paramArrayOfDouble[(i4 + 1)];
        paramArrayOfDouble[(i3 + this.n2 + 1)] = (-paramArrayOfDouble[i4]);
      }
      for (i6 = 1; i6 < i5; i6++)
        for (i7 = this.n2 + 2; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1;
          i4 = (this.n1 - i6) * i1;
          paramArrayOfDouble[(i3 + i7)] = paramArrayOfDouble[(i4 + i1 - i7)];
          paramArrayOfDouble[(i3 + i7 + 1)] = (-paramArrayOfDouble[(i4 + i1 - i7 + 1)]);
        }
      for (i6 = 0; i6 <= this.n1 / 2; i6++)
        for (i7 = 0; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1 + i7;
          i4 = (this.n1 - i6) % this.n1 * i1 + (i1 - i7) % i1;
          paramArrayOfDouble[i4] = paramArrayOfDouble[i3];
          paramArrayOfDouble[(i4 + 1)] = (-paramArrayOfDouble[(i3 + 1)]);
        }
    }
    paramArrayOfDouble[this.n2] = (-paramArrayOfDouble[1]);
    paramArrayOfDouble[1] = 0.0D;
    paramArrayOfDouble[(i5 * i1 + this.n2)] = (-paramArrayOfDouble[(i5 * i1 + 1)]);
    paramArrayOfDouble[(i5 * i1 + 1)] = 0.0D;
    paramArrayOfDouble[(i5 * i1 + this.n2 + 1)] = 0.0D;
  }

  public void realInverseFull(double[][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j = this.n1 << 1;
    if (j < this.n2)
      j = this.n2;
    int k = this.ip[0];
    if (j > k << 2)
    {
      k = j >> 2;
      makewt(k);
    }
    int m = this.ip[1];
    if (this.n2 > m << 2)
    {
      m = this.n2 >> 2;
      makect(m, this.w, k);
    }
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i != this.oldNthread)
    {
      this.nt = (8 * i * this.n1);
      if (this.n2 == 4 * i)
        this.nt >>= 1;
      else if (this.n2 < 4 * i)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth2(1, -1, paramArrayOfDouble, paramBoolean);
      cdft2d_subth(1, paramArrayOfDouble, paramBoolean);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse2(paramArrayOfDouble[n], 0, paramBoolean);
      cdft2d_sub(1, paramArrayOfDouble, paramBoolean);
      rdft2d_sub(1, paramArrayOfDouble);
    }
    int i1 = 2 * this.n2;
    int i2 = this.n1 / 2;
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      for (int i3 = 1; i3 < i2; i3++)
      {
        paramArrayOfDouble[i3][this.n2] = paramArrayOfDouble[(this.n1 - i3)][1];
        paramArrayOfDouble[i3][(this.n2 + 1)] = (-paramArrayOfDouble[(this.n1 - i3)][0]);
      }
      int i4;
      for (i3 = 1; i3 < i2; i3++)
        for (i4 = this.n2 + 2; i4 < i1; i4 += 2)
        {
          paramArrayOfDouble[i3][i4] = paramArrayOfDouble[(this.n1 - i3)][(i1 - i4)];
          paramArrayOfDouble[i3][(i4 + 1)] = (-paramArrayOfDouble[(this.n1 - i3)][(i1 - i4 + 1)]);
        }
      for (i3 = 0; i3 <= this.n1 / 2; i3++)
        for (i4 = 0; i4 < i1; i4 += 2)
        {
          paramArrayOfDouble[((this.n1 - i3) % this.n1)][((i1 - i4) % i1)] = paramArrayOfDouble[i3][i4];
          paramArrayOfDouble[((this.n1 - i3) % this.n1)][((i1 - i4) % i1 + 1)] = (-paramArrayOfDouble[i3][(i4 + 1)]);
        }
    }
    paramArrayOfDouble[0][this.n2] = (-paramArrayOfDouble[0][1]);
    paramArrayOfDouble[0][1] = 0.0D;
    paramArrayOfDouble[i2][this.n2] = (-paramArrayOfDouble[i2][1]);
    paramArrayOfDouble[i2][1] = 0.0D;
    paramArrayOfDouble[i2][(this.n2 + 1)] = 0.0D;
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

  private void rdft2d_sub(int paramInt, double[] paramArrayOfDouble)
  {
    int i = this.n1 >> 1;
    int k;
    int m;
    int n;
    if (paramInt < 0)
      for (j = 1; j < i; j++)
      {
        k = this.n1 - j;
        m = j * this.n2;
        n = k * this.n2;
        double d = paramArrayOfDouble[m] - paramArrayOfDouble[n];
        paramArrayOfDouble[m] += paramArrayOfDouble[n];
        paramArrayOfDouble[n] = d;
        d = paramArrayOfDouble[(n + 1)] - paramArrayOfDouble[(m + 1)];
        paramArrayOfDouble[(m + 1)] += paramArrayOfDouble[(n + 1)];
        paramArrayOfDouble[(n + 1)] = d;
      }
    for (int j = 1; j < i; j++)
    {
      k = this.n1 - j;
      m = j * this.n2;
      n = k * this.n2;
      paramArrayOfDouble[n] = (0.5D * (paramArrayOfDouble[m] - paramArrayOfDouble[n]));
      paramArrayOfDouble[m] -= paramArrayOfDouble[n];
      paramArrayOfDouble[(n + 1)] = (0.5D * (paramArrayOfDouble[(m + 1)] + paramArrayOfDouble[(n + 1)]));
      paramArrayOfDouble[(m + 1)] -= paramArrayOfDouble[(n + 1)];
    }
  }

  private void rdft2d_sub(int paramInt, double[][] paramArrayOfDouble)
  {
    int i = this.n1 >> 1;
    int k;
    if (paramInt < 0)
      for (j = 1; j < i; j++)
      {
        k = this.n1 - j;
        double d = paramArrayOfDouble[j][0] - paramArrayOfDouble[k][0];
        paramArrayOfDouble[j][0] += paramArrayOfDouble[k][0];
        paramArrayOfDouble[k][0] = d;
        d = paramArrayOfDouble[k][1] - paramArrayOfDouble[j][1];
        paramArrayOfDouble[j][1] += paramArrayOfDouble[k][1];
        paramArrayOfDouble[k][1] = d;
      }
    for (int j = 1; j < i; j++)
    {
      k = this.n1 - j;
      paramArrayOfDouble[k][0] = (0.5D * (paramArrayOfDouble[j][0] - paramArrayOfDouble[k][0]));
      paramArrayOfDouble[j][0] -= paramArrayOfDouble[k][0];
      paramArrayOfDouble[k][1] = (0.5D * (paramArrayOfDouble[j][1] + paramArrayOfDouble[k][1]));
      paramArrayOfDouble[j][1] -= paramArrayOfDouble[k][1];
    }
  }

  private void cdft2d_sub(int paramInt, double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int j;
    int i;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    if (paramInt == -1)
    {
      if (this.n2 > 4)
        for (j = 0; j < this.n2; j += 8)
        {
          for (i = 0; i < this.n1; i++)
          {
            k = i * this.n2 + j;
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            i1 = n + 2 * this.n1;
            i2 = i1 + 2 * this.n1;
            this.t[m] = paramArrayOfDouble[k];
            this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
            this.t[n] = paramArrayOfDouble[(k + 2)];
            this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
            this.t[i1] = paramArrayOfDouble[(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfDouble[(k + 5)];
            this.t[i2] = paramArrayOfDouble[(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(k + 7)];
          }
          this.fftn1.complexForward(this.t, 0);
          this.fftn1.complexForward(this.t, 2 * this.n1);
          this.fftn1.complexForward(this.t, 4 * this.n1);
          this.fftn1.complexForward(this.t, 6 * this.n1);
          for (i = 0; i < this.n1; i++)
          {
            k = i * this.n2 + j;
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            i1 = n + 2 * this.n1;
            i2 = i1 + 2 * this.n1;
            paramArrayOfDouble[k] = this.t[m];
            paramArrayOfDouble[(k + 1)] = this.t[(m + 1)];
            paramArrayOfDouble[(k + 2)] = this.t[n];
            paramArrayOfDouble[(k + 3)] = this.t[(n + 1)];
            paramArrayOfDouble[(k + 4)] = this.t[i1];
            paramArrayOfDouble[(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(k + 6)] = this.t[i2];
            paramArrayOfDouble[(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          this.t[m] = paramArrayOfDouble[k];
          this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
          this.t[n] = paramArrayOfDouble[(k + 2)];
          this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
        }
        this.fftn1.complexForward(this.t, 0);
        this.fftn1.complexForward(this.t, 2 * this.n1);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          paramArrayOfDouble[k] = this.t[m];
          paramArrayOfDouble[(k + 1)] = this.t[(m + 1)];
          paramArrayOfDouble[(k + 2)] = this.t[n];
          paramArrayOfDouble[(k + 3)] = this.t[(n + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          this.t[m] = paramArrayOfDouble[k];
          this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
        }
        this.fftn1.complexForward(this.t, 0);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          paramArrayOfDouble[k] = this.t[m];
          paramArrayOfDouble[(k + 1)] = this.t[(m + 1)];
        }
      }
    }
    else
    {
      if (this.n2 > 4)
        for (j = 0; j < this.n2; j += 8)
        {
          for (i = 0; i < this.n1; i++)
          {
            k = i * this.n2 + j;
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            i1 = n + 2 * this.n1;
            i2 = i1 + 2 * this.n1;
            this.t[m] = paramArrayOfDouble[k];
            this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
            this.t[n] = paramArrayOfDouble[(k + 2)];
            this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
            this.t[i1] = paramArrayOfDouble[(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfDouble[(k + 5)];
            this.t[i2] = paramArrayOfDouble[(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(k + 7)];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
          this.fftn1.complexInverse(this.t, 4 * this.n1, paramBoolean);
          this.fftn1.complexInverse(this.t, 6 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            k = i * this.n2 + j;
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            i1 = n + 2 * this.n1;
            i2 = i1 + 2 * this.n1;
            paramArrayOfDouble[k] = this.t[m];
            paramArrayOfDouble[(k + 1)] = this.t[(m + 1)];
            paramArrayOfDouble[(k + 2)] = this.t[n];
            paramArrayOfDouble[(k + 3)] = this.t[(n + 1)];
            paramArrayOfDouble[(k + 4)] = this.t[i1];
            paramArrayOfDouble[(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(k + 6)] = this.t[i2];
            paramArrayOfDouble[(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          this.t[m] = paramArrayOfDouble[k];
          this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
          this.t[n] = paramArrayOfDouble[(k + 2)];
          this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          paramArrayOfDouble[k] = this.t[m];
          paramArrayOfDouble[(k + 1)] = this.t[(m + 1)];
          paramArrayOfDouble[(k + 2)] = this.t[n];
          paramArrayOfDouble[(k + 3)] = this.t[(n + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          this.t[m] = paramArrayOfDouble[k];
          this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          paramArrayOfDouble[k] = this.t[m];
          paramArrayOfDouble[(k + 1)] = this.t[(m + 1)];
        }
      }
    }
  }

  private void cdft2d_sub(int paramInt, double[][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j;
    int i;
    int k;
    int m;
    int n;
    int i1;
    if (paramInt == -1)
    {
      if (this.n2 > 4)
        for (j = 0; j < this.n2; j += 8)
        {
          for (i = 0; i < this.n1; i++)
          {
            k = 2 * i;
            m = 2 * this.n1 + 2 * i;
            n = m + 2 * this.n1;
            i1 = n + 2 * this.n1;
            this.t[k] = paramArrayOfDouble[i][j];
            this.t[(k + 1)] = paramArrayOfDouble[i][(j + 1)];
            this.t[m] = paramArrayOfDouble[i][(j + 2)];
            this.t[(m + 1)] = paramArrayOfDouble[i][(j + 3)];
            this.t[n] = paramArrayOfDouble[i][(j + 4)];
            this.t[(n + 1)] = paramArrayOfDouble[i][(j + 5)];
            this.t[i1] = paramArrayOfDouble[i][(j + 6)];
            this.t[(i1 + 1)] = paramArrayOfDouble[i][(j + 7)];
          }
          this.fftn1.complexForward(this.t, 0);
          this.fftn1.complexForward(this.t, 2 * this.n1);
          this.fftn1.complexForward(this.t, 4 * this.n1);
          this.fftn1.complexForward(this.t, 6 * this.n1);
          for (i = 0; i < this.n1; i++)
          {
            k = 2 * i;
            m = 2 * this.n1 + 2 * i;
            n = m + 2 * this.n1;
            i1 = n + 2 * this.n1;
            paramArrayOfDouble[i][j] = this.t[k];
            paramArrayOfDouble[i][(j + 1)] = this.t[(k + 1)];
            paramArrayOfDouble[i][(j + 2)] = this.t[m];
            paramArrayOfDouble[i][(j + 3)] = this.t[(m + 1)];
            paramArrayOfDouble[i][(j + 4)] = this.t[n];
            paramArrayOfDouble[i][(j + 5)] = this.t[(n + 1)];
            paramArrayOfDouble[i][(j + 6)] = this.t[i1];
            paramArrayOfDouble[i][(j + 7)] = this.t[(i1 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          this.t[k] = paramArrayOfDouble[i][0];
          this.t[(k + 1)] = paramArrayOfDouble[i][1];
          this.t[m] = paramArrayOfDouble[i][2];
          this.t[(m + 1)] = paramArrayOfDouble[i][3];
        }
        this.fftn1.complexForward(this.t, 0);
        this.fftn1.complexForward(this.t, 2 * this.n1);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          paramArrayOfDouble[i][0] = this.t[k];
          paramArrayOfDouble[i][1] = this.t[(k + 1)];
          paramArrayOfDouble[i][2] = this.t[m];
          paramArrayOfDouble[i][3] = this.t[(m + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          this.t[k] = paramArrayOfDouble[i][0];
          this.t[(k + 1)] = paramArrayOfDouble[i][1];
        }
        this.fftn1.complexForward(this.t, 0);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          paramArrayOfDouble[i][0] = this.t[k];
          paramArrayOfDouble[i][1] = this.t[(k + 1)];
        }
      }
    }
    else
    {
      if (this.n2 > 4)
        for (j = 0; j < this.n2; j += 8)
        {
          for (i = 0; i < this.n1; i++)
          {
            k = 2 * i;
            m = 2 * this.n1 + 2 * i;
            n = m + 2 * this.n1;
            i1 = n + 2 * this.n1;
            this.t[k] = paramArrayOfDouble[i][j];
            this.t[(k + 1)] = paramArrayOfDouble[i][(j + 1)];
            this.t[m] = paramArrayOfDouble[i][(j + 2)];
            this.t[(m + 1)] = paramArrayOfDouble[i][(j + 3)];
            this.t[n] = paramArrayOfDouble[i][(j + 4)];
            this.t[(n + 1)] = paramArrayOfDouble[i][(j + 5)];
            this.t[i1] = paramArrayOfDouble[i][(j + 6)];
            this.t[(i1 + 1)] = paramArrayOfDouble[i][(j + 7)];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
          this.fftn1.complexInverse(this.t, 4 * this.n1, paramBoolean);
          this.fftn1.complexInverse(this.t, 6 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            k = 2 * i;
            m = 2 * this.n1 + 2 * i;
            n = m + 2 * this.n1;
            i1 = n + 2 * this.n1;
            paramArrayOfDouble[i][j] = this.t[k];
            paramArrayOfDouble[i][(j + 1)] = this.t[(k + 1)];
            paramArrayOfDouble[i][(j + 2)] = this.t[m];
            paramArrayOfDouble[i][(j + 3)] = this.t[(m + 1)];
            paramArrayOfDouble[i][(j + 4)] = this.t[n];
            paramArrayOfDouble[i][(j + 5)] = this.t[(n + 1)];
            paramArrayOfDouble[i][(j + 6)] = this.t[i1];
            paramArrayOfDouble[i][(j + 7)] = this.t[(i1 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          this.t[k] = paramArrayOfDouble[i][0];
          this.t[(k + 1)] = paramArrayOfDouble[i][1];
          this.t[m] = paramArrayOfDouble[i][2];
          this.t[(m + 1)] = paramArrayOfDouble[i][3];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          paramArrayOfDouble[i][0] = this.t[k];
          paramArrayOfDouble[i][1] = this.t[(k + 1)];
          paramArrayOfDouble[i][2] = this.t[m];
          paramArrayOfDouble[i][3] = this.t[(m + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          this.t[k] = paramArrayOfDouble[i][0];
          this.t[(k + 1)] = paramArrayOfDouble[i][1];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          paramArrayOfDouble[i][0] = this.t[k];
          paramArrayOfDouble[i][1] = this.t[(k + 1)];
        }
      }
    }
  }

  private void xdft2d0_subth1(final int paramInt1, final int paramInt2, final double[] paramArrayOfDouble, final boolean paramBoolean)
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
          if (paramInt1 == 0)
          {
            if (paramInt2 == -1)
            {
              i = m;
              while (i < DoubleFFT_2D.this.n1)
              {
                DoubleFFT_2D.this.fftn2.complexForward(paramArrayOfDouble, i * DoubleFFT_2D.this.n2);
                i += i;
              }
            }
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.complexInverse(paramArrayOfDouble, i * DoubleFFT_2D.this.n2, paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.realForward(paramArrayOfDouble, i * DoubleFFT_2D.this.n2);
              i += i;
            }
          }
          int i = m;
          while (i < DoubleFFT_2D.this.n1)
          {
            DoubleFFT_2D.this.fftn2.realInverse(paramArrayOfDouble, i * DoubleFFT_2D.this.n2, paramBoolean);
            i += i;
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

  private void xdft2d0_subth2(final int paramInt1, final int paramInt2, final double[] paramArrayOfDouble, final boolean paramBoolean)
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
          if (paramInt1 == 0)
          {
            if (paramInt2 == -1)
            {
              i = m;
              while (i < DoubleFFT_2D.this.n1)
              {
                DoubleFFT_2D.this.fftn2.complexForward(paramArrayOfDouble, i * DoubleFFT_2D.this.n2);
                i += i;
              }
            }
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.complexInverse(paramArrayOfDouble, i * DoubleFFT_2D.this.n2, paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.realForward(paramArrayOfDouble, i * DoubleFFT_2D.this.n2);
              i += i;
            }
          }
          int i = m;
          while (i < DoubleFFT_2D.this.n1)
          {
            DoubleFFT_2D.this.fftn2.realInverse2(paramArrayOfDouble, i * DoubleFFT_2D.this.n2, paramBoolean);
            i += i;
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

  private void xdft2d0_subth1(final int paramInt1, final int paramInt2, final double[][] paramArrayOfDouble, final boolean paramBoolean)
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
          if (paramInt1 == 0)
          {
            if (paramInt2 == -1)
            {
              i = m;
              while (i < DoubleFFT_2D.this.n1)
              {
                DoubleFFT_2D.this.fftn2.complexForward(paramArrayOfDouble[i]);
                i += i;
              }
            }
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.complexInverse(paramArrayOfDouble[i], paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.realForward(paramArrayOfDouble[i]);
              i += i;
            }
          }
          int i = m;
          while (i < DoubleFFT_2D.this.n1)
          {
            DoubleFFT_2D.this.fftn2.realInverse(paramArrayOfDouble[i], paramBoolean);
            i += i;
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

  private void xdft2d0_subth2(final int paramInt1, final int paramInt2, final double[][] paramArrayOfDouble, final boolean paramBoolean)
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
          if (paramInt1 == 0)
          {
            if (paramInt2 == -1)
            {
              i = m;
              while (i < DoubleFFT_2D.this.n1)
              {
                DoubleFFT_2D.this.fftn2.complexForward(paramArrayOfDouble[i]);
                i += i;
              }
            }
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.complexInverse(paramArrayOfDouble[i], paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < DoubleFFT_2D.this.n1)
            {
              DoubleFFT_2D.this.fftn2.realForward(paramArrayOfDouble[i]);
              i += i;
            }
          }
          int i = m;
          while (i < DoubleFFT_2D.this.n1)
          {
            DoubleFFT_2D.this.fftn2.realInverse2(paramArrayOfDouble[i], 0, paramBoolean);
            i += i;
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

  private void cdft2d_subth(final int paramInt, final double[] paramArrayOfDouble, final boolean paramBoolean)
  {
    int m = ConcurrencyUtils.getNumberOfProcessors();
    int i = m;
    int j = 8 * this.n1;
    if (this.n2 == 4 * m)
    {
      j >>= 1;
    }
    else if (this.n2 < 4 * m)
    {
      i = this.n2 >> 1;
      j >>= 2;
    }
    Future[] arrayOfFuture = new Future[i];
    final int n = i;
    final int i1;
    for (int k = 0; k < i; k++)
    {
      i1 = k;
      final int i2 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          int i;
          int k;
          int m;
          int n;
          int i1;
          int i2;
          if (paramInt == -1)
          {
            if (DoubleFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < DoubleFFT_2D.this.n2)
              {
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i * DoubleFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  i2 = i1 + 2 * DoubleFFT_2D.this.n1;
                  DoubleFFT_2D.this.t[m] = paramArrayOfDouble[k];
                  DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
                  DoubleFFT_2D.this.t[n] = paramArrayOfDouble[(k + 2)];
                  DoubleFFT_2D.this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
                  DoubleFFT_2D.this.t[i1] = paramArrayOfDouble[(k + 4)];
                  DoubleFFT_2D.this.t[(i1 + 1)] = paramArrayOfDouble[(k + 5)];
                  DoubleFFT_2D.this.t[i2] = paramArrayOfDouble[(k + 6)];
                  DoubleFFT_2D.this.t[(i2 + 1)] = paramArrayOfDouble[(k + 7)];
                }
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2);
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1);
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 4 * DoubleFFT_2D.this.n1);
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 6 * DoubleFFT_2D.this.n1);
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i * DoubleFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  i2 = i1 + 2 * DoubleFFT_2D.this.n1;
                  paramArrayOfDouble[k] = DoubleFFT_2D.this.t[m];
                  paramArrayOfDouble[(k + 1)] = DoubleFFT_2D.this.t[(m + 1)];
                  paramArrayOfDouble[(k + 2)] = DoubleFFT_2D.this.t[n];
                  paramArrayOfDouble[(k + 3)] = DoubleFFT_2D.this.t[(n + 1)];
                  paramArrayOfDouble[(k + 4)] = DoubleFFT_2D.this.t[i1];
                  paramArrayOfDouble[(k + 5)] = DoubleFFT_2D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(k + 6)] = DoubleFFT_2D.this.t[i2];
                  paramArrayOfDouble[(k + 7)] = DoubleFFT_2D.this.t[(i2 + 1)];
                }
                j += 8 * n;
              }
            }
            if (DoubleFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                DoubleFFT_2D.this.t[m] = paramArrayOfDouble[k];
                DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
                DoubleFFT_2D.this.t[n] = paramArrayOfDouble[(k + 2)];
                DoubleFFT_2D.this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
              }
              DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2);
              DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                paramArrayOfDouble[k] = DoubleFFT_2D.this.t[m];
                paramArrayOfDouble[(k + 1)] = DoubleFFT_2D.this.t[(m + 1)];
                paramArrayOfDouble[(k + 2)] = DoubleFFT_2D.this.t[n];
                paramArrayOfDouble[(k + 3)] = DoubleFFT_2D.this.t[(n + 1)];
              }
            }
            if (DoubleFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                DoubleFFT_2D.this.t[m] = paramArrayOfDouble[k];
                DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
              }
              DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                paramArrayOfDouble[k] = DoubleFFT_2D.this.t[m];
                paramArrayOfDouble[(k + 1)] = DoubleFFT_2D.this.t[(m + 1)];
              }
            }
          }
          else
          {
            if (DoubleFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < DoubleFFT_2D.this.n2)
              {
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i * DoubleFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  i2 = i1 + 2 * DoubleFFT_2D.this.n1;
                  DoubleFFT_2D.this.t[m] = paramArrayOfDouble[k];
                  DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
                  DoubleFFT_2D.this.t[n] = paramArrayOfDouble[(k + 2)];
                  DoubleFFT_2D.this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
                  DoubleFFT_2D.this.t[i1] = paramArrayOfDouble[(k + 4)];
                  DoubleFFT_2D.this.t[(i1 + 1)] = paramArrayOfDouble[(k + 5)];
                  DoubleFFT_2D.this.t[i2] = paramArrayOfDouble[(k + 6)];
                  DoubleFFT_2D.this.t[(i2 + 1)] = paramArrayOfDouble[(k + 7)];
                }
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2, paramBoolean);
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1, paramBoolean);
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 4 * DoubleFFT_2D.this.n1, paramBoolean);
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 6 * DoubleFFT_2D.this.n1, paramBoolean);
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i * DoubleFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  i2 = i1 + 2 * DoubleFFT_2D.this.n1;
                  paramArrayOfDouble[k] = DoubleFFT_2D.this.t[m];
                  paramArrayOfDouble[(k + 1)] = DoubleFFT_2D.this.t[(m + 1)];
                  paramArrayOfDouble[(k + 2)] = DoubleFFT_2D.this.t[n];
                  paramArrayOfDouble[(k + 3)] = DoubleFFT_2D.this.t[(n + 1)];
                  paramArrayOfDouble[(k + 4)] = DoubleFFT_2D.this.t[i1];
                  paramArrayOfDouble[(k + 5)] = DoubleFFT_2D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(k + 6)] = DoubleFFT_2D.this.t[i2];
                  paramArrayOfDouble[(k + 7)] = DoubleFFT_2D.this.t[(i2 + 1)];
                }
                j += 8 * n;
              }
            }
            if (DoubleFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                DoubleFFT_2D.this.t[m] = paramArrayOfDouble[k];
                DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
                DoubleFFT_2D.this.t[n] = paramArrayOfDouble[(k + 2)];
                DoubleFFT_2D.this.t[(n + 1)] = paramArrayOfDouble[(k + 3)];
              }
              DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2, paramBoolean);
              DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1, paramBoolean);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                paramArrayOfDouble[k] = DoubleFFT_2D.this.t[m];
                paramArrayOfDouble[(k + 1)] = DoubleFFT_2D.this.t[(m + 1)];
                paramArrayOfDouble[(k + 2)] = DoubleFFT_2D.this.t[n];
                paramArrayOfDouble[(k + 3)] = DoubleFFT_2D.this.t[(n + 1)];
              }
            }
            if (DoubleFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                DoubleFFT_2D.this.t[m] = paramArrayOfDouble[k];
                DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[(k + 1)];
              }
              DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2, paramBoolean);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i * DoubleFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                paramArrayOfDouble[k] = DoubleFFT_2D.this.t[m];
                paramArrayOfDouble[(k + 1)] = DoubleFFT_2D.this.t[(m + 1)];
              }
            }
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

  private void cdft2d_subth(final int paramInt, final double[][] paramArrayOfDouble, final boolean paramBoolean)
  {
    int m = ConcurrencyUtils.getNumberOfProcessors();
    int i = m;
    int j = 8 * this.n1;
    if (this.n2 == 4 * m)
    {
      j >>= 1;
    }
    else if (this.n2 < 4 * m)
    {
      i = this.n2 >> 1;
      j >>= 2;
    }
    Future[] arrayOfFuture = new Future[i];
    final int n = i;
    final int i1;
    for (int k = 0; k < i; k++)
    {
      i1 = k;
      final int i2 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          int i;
          int k;
          int m;
          int n;
          int i1;
          if (paramInt == -1)
          {
            if (DoubleFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < DoubleFFT_2D.this.n2)
              {
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * DoubleFFT_2D.this.n1;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  DoubleFFT_2D.this.t[k] = paramArrayOfDouble[i][j];
                  DoubleFFT_2D.this.t[(k + 1)] = paramArrayOfDouble[i][(j + 1)];
                  DoubleFFT_2D.this.t[m] = paramArrayOfDouble[i][(j + 2)];
                  DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[i][(j + 3)];
                  DoubleFFT_2D.this.t[n] = paramArrayOfDouble[i][(j + 4)];
                  DoubleFFT_2D.this.t[(n + 1)] = paramArrayOfDouble[i][(j + 5)];
                  DoubleFFT_2D.this.t[i1] = paramArrayOfDouble[i][(j + 6)];
                  DoubleFFT_2D.this.t[(i1 + 1)] = paramArrayOfDouble[i][(j + 7)];
                }
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2);
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1);
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 4 * DoubleFFT_2D.this.n1);
                DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 6 * DoubleFFT_2D.this.n1);
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * DoubleFFT_2D.this.n1;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  paramArrayOfDouble[i][j] = DoubleFFT_2D.this.t[k];
                  paramArrayOfDouble[i][(j + 1)] = DoubleFFT_2D.this.t[(k + 1)];
                  paramArrayOfDouble[i][(j + 2)] = DoubleFFT_2D.this.t[m];
                  paramArrayOfDouble[i][(j + 3)] = DoubleFFT_2D.this.t[(m + 1)];
                  paramArrayOfDouble[i][(j + 4)] = DoubleFFT_2D.this.t[n];
                  paramArrayOfDouble[i][(j + 5)] = DoubleFFT_2D.this.t[(n + 1)];
                  paramArrayOfDouble[i][(j + 6)] = DoubleFFT_2D.this.t[i1];
                  paramArrayOfDouble[i][(j + 7)] = DoubleFFT_2D.this.t[(i1 + 1)];
                }
                j += 8 * n;
              }
            }
            if (DoubleFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                DoubleFFT_2D.this.t[k] = paramArrayOfDouble[i][(4 * i1)];
                DoubleFFT_2D.this.t[(k + 1)] = paramArrayOfDouble[i][(4 * i1 + 1)];
                DoubleFFT_2D.this.t[m] = paramArrayOfDouble[i][(4 * i1 + 2)];
                DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[i][(4 * i1 + 3)];
              }
              DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2);
              DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                paramArrayOfDouble[i][(4 * i1)] = DoubleFFT_2D.this.t[k];
                paramArrayOfDouble[i][(4 * i1 + 1)] = DoubleFFT_2D.this.t[(k + 1)];
                paramArrayOfDouble[i][(4 * i1 + 2)] = DoubleFFT_2D.this.t[m];
                paramArrayOfDouble[i][(4 * i1 + 3)] = DoubleFFT_2D.this.t[(m + 1)];
              }
            }
            if (DoubleFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                DoubleFFT_2D.this.t[k] = paramArrayOfDouble[i][(2 * i1)];
                DoubleFFT_2D.this.t[(k + 1)] = paramArrayOfDouble[i][(2 * i1 + 1)];
              }
              DoubleFFT_2D.this.fftn1.complexForward(DoubleFFT_2D.this.t, i2);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                paramArrayOfDouble[i][(2 * i1)] = DoubleFFT_2D.this.t[k];
                paramArrayOfDouble[i][(2 * i1 + 1)] = DoubleFFT_2D.this.t[(k + 1)];
              }
            }
          }
          else
          {
            if (DoubleFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < DoubleFFT_2D.this.n2)
              {
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * DoubleFFT_2D.this.n1;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  DoubleFFT_2D.this.t[k] = paramArrayOfDouble[i][j];
                  DoubleFFT_2D.this.t[(k + 1)] = paramArrayOfDouble[i][(j + 1)];
                  DoubleFFT_2D.this.t[m] = paramArrayOfDouble[i][(j + 2)];
                  DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[i][(j + 3)];
                  DoubleFFT_2D.this.t[n] = paramArrayOfDouble[i][(j + 4)];
                  DoubleFFT_2D.this.t[(n + 1)] = paramArrayOfDouble[i][(j + 5)];
                  DoubleFFT_2D.this.t[i1] = paramArrayOfDouble[i][(j + 6)];
                  DoubleFFT_2D.this.t[(i1 + 1)] = paramArrayOfDouble[i][(j + 7)];
                }
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2, paramBoolean);
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1, paramBoolean);
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 4 * DoubleFFT_2D.this.n1, paramBoolean);
                DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 6 * DoubleFFT_2D.this.n1, paramBoolean);
                for (i = 0; i < DoubleFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * DoubleFFT_2D.this.n1;
                  i1 = n + 2 * DoubleFFT_2D.this.n1;
                  paramArrayOfDouble[i][j] = DoubleFFT_2D.this.t[k];
                  paramArrayOfDouble[i][(j + 1)] = DoubleFFT_2D.this.t[(k + 1)];
                  paramArrayOfDouble[i][(j + 2)] = DoubleFFT_2D.this.t[m];
                  paramArrayOfDouble[i][(j + 3)] = DoubleFFT_2D.this.t[(m + 1)];
                  paramArrayOfDouble[i][(j + 4)] = DoubleFFT_2D.this.t[n];
                  paramArrayOfDouble[i][(j + 5)] = DoubleFFT_2D.this.t[(n + 1)];
                  paramArrayOfDouble[i][(j + 6)] = DoubleFFT_2D.this.t[i1];
                  paramArrayOfDouble[i][(j + 7)] = DoubleFFT_2D.this.t[(i1 + 1)];
                }
                j += 8 * n;
              }
            }
            if (DoubleFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                DoubleFFT_2D.this.t[k] = paramArrayOfDouble[i][(4 * i1)];
                DoubleFFT_2D.this.t[(k + 1)] = paramArrayOfDouble[i][(4 * i1 + 1)];
                DoubleFFT_2D.this.t[m] = paramArrayOfDouble[i][(4 * i1 + 2)];
                DoubleFFT_2D.this.t[(m + 1)] = paramArrayOfDouble[i][(4 * i1 + 3)];
              }
              DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2, paramBoolean);
              DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2 + 2 * DoubleFFT_2D.this.n1, paramBoolean);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * DoubleFFT_2D.this.n1 + 2 * i;
                paramArrayOfDouble[i][(4 * i1)] = DoubleFFT_2D.this.t[k];
                paramArrayOfDouble[i][(4 * i1 + 1)] = DoubleFFT_2D.this.t[(k + 1)];
                paramArrayOfDouble[i][(4 * i1 + 2)] = DoubleFFT_2D.this.t[m];
                paramArrayOfDouble[i][(4 * i1 + 3)] = DoubleFFT_2D.this.t[(m + 1)];
              }
            }
            if (DoubleFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                DoubleFFT_2D.this.t[k] = paramArrayOfDouble[i][(2 * i1)];
                DoubleFFT_2D.this.t[(k + 1)] = paramArrayOfDouble[i][(2 * i1 + 1)];
              }
              DoubleFFT_2D.this.fftn1.complexInverse(DoubleFFT_2D.this.t, i2, paramBoolean);
              for (i = 0; i < DoubleFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                paramArrayOfDouble[i][(2 * i1)] = DoubleFFT_2D.this.t[k];
                paramArrayOfDouble[i][(2 * i1 + 1)] = DoubleFFT_2D.this.t[(k + 1)];
              }
            }
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

  private void fillSymmetric(final double[] paramArrayOfDouble)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    Future[] arrayOfFuture = new Future[i];
    int j = this.n1 / 2;
    int k = j / i;
    final int m = 2 * this.n2;
    for (int n = 0; n < i; n++)
    {
      final int i1;
      if (n == 0)
        i1 = n * k + 1;
      else
        i1 = n * k;
      final int i2 = n * k + k;
      final int i3 = n * k;
      final int i4;
      if (n == i - 1)
        i4 = n * k + k + 1;
      else
        i4 = n * k + k;
      arrayOfFuture[n] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int i;
          int j;
          for (int k = i1; k < i2; k++)
          {
            i = k * m;
            j = (DoubleFFT_2D.this.n1 - k) * m;
            paramArrayOfDouble[(i + DoubleFFT_2D.this.n2)] = paramArrayOfDouble[(j + 1)];
            paramArrayOfDouble[(i + DoubleFFT_2D.this.n2 + 1)] = (-paramArrayOfDouble[j]);
          }
          int m;
          for (k = i1; k < i2; k++)
          {
            m = DoubleFFT_2D.this.n2 + 2;
            while (m < m)
            {
              i = k * m;
              j = (DoubleFFT_2D.this.n1 - k) * m + m - m;
              paramArrayOfDouble[(i + m)] = paramArrayOfDouble[j];
              paramArrayOfDouble[(i + m + 1)] = (-paramArrayOfDouble[(j + 1)]);
              m += 2;
            }
          }
          for (k = i3; k < i4; k++)
          {
            m = 0;
            while (m < m)
            {
              i = (DoubleFFT_2D.this.n1 - k) % DoubleFFT_2D.this.n1 * m + (m - m) % m;
              j = k * m + m;
              paramArrayOfDouble[i] = paramArrayOfDouble[j];
              paramArrayOfDouble[(i + 1)] = (-paramArrayOfDouble[(j + 1)]);
              m += 2;
            }
          }
        }
      });
    }
    try
    {
      for (n = 0; n < i; n++)
        arrayOfFuture[n].get();
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

  private void fillSymmetric(final double[][] paramArrayOfDouble)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    Future[] arrayOfFuture = new Future[i];
    int j = this.n1 / 2;
    int k = j / i;
    final int m = 2 * this.n2;
    for (int n = 0; n < i; n++)
    {
      final int i1;
      if (n == 0)
        i1 = n * k + 1;
      else
        i1 = n * k;
      final int i2 = n * k + k;
      final int i3 = n * k;
      final int i4;
      if (n == i - 1)
        i4 = n * k + k + 1;
      else
        i4 = n * k + k;
      arrayOfFuture[n] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          for (int i = i1; i < i2; i++)
          {
            paramArrayOfDouble[i][DoubleFFT_2D.this.n2] = paramArrayOfDouble[(DoubleFFT_2D.this.n1 - i)][1];
            paramArrayOfDouble[i][(DoubleFFT_2D.this.n2 + 1)] = (-paramArrayOfDouble[(DoubleFFT_2D.this.n1 - i)][0]);
          }
          int j;
          for (i = i1; i < i2; i++)
          {
            j = DoubleFFT_2D.this.n2 + 2;
            while (j < m)
            {
              paramArrayOfDouble[i][j] = paramArrayOfDouble[(DoubleFFT_2D.this.n1 - i)][(m - j)];
              paramArrayOfDouble[i][(j + 1)] = (-paramArrayOfDouble[(DoubleFFT_2D.this.n1 - i)][(m - j + 1)]);
              j += 2;
            }
          }
          for (i = i3; i < i4; i++)
          {
            j = 0;
            while (j < m)
            {
              paramArrayOfDouble[((DoubleFFT_2D.this.n1 - i) % DoubleFFT_2D.this.n1)][((m - j) % m)] = paramArrayOfDouble[i][j];
              paramArrayOfDouble[((DoubleFFT_2D.this.n1 - i) % DoubleFFT_2D.this.n1)][((m - j) % m + 1)] = (-paramArrayOfDouble[i][(j + 1)]);
              j += 2;
            }
          }
        }
      });
    }
    try
    {
      for (n = 0; n < i; n++)
        arrayOfFuture[n].get();
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
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D
 * JD-Core Version:    0.6.1
 */