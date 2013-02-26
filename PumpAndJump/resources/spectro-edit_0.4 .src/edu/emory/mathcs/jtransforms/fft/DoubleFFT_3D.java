package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DoubleFFT_3D
{
  private int n1;
  private int n2;
  private int n3;
  private int sliceStride;
  private int rowStride;
  private int[] ip;
  private double[] w;
  private double[] t;
  private DoubleFFT_1D fftn1;
  private DoubleFFT_1D fftn2;
  private DoubleFFT_1D fftn3;
  private int oldNthreads;
  private int nt;

  public DoubleFFT_3D(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((!ConcurrencyUtils.isPowerOf2(paramInt1)) || (!ConcurrencyUtils.isPowerOf2(paramInt2)) || (!ConcurrencyUtils.isPowerOf2(paramInt3)))
      throw new IllegalArgumentException("n1, n2 and n3 must be power of two numbers");
    if ((paramInt1 <= 1) || (paramInt2 <= 1) || (paramInt3 <= 1))
      throw new IllegalArgumentException("n1, n2 and n3 must be greater than 1");
    this.n1 = paramInt1;
    this.n2 = paramInt2;
    this.n3 = paramInt3;
    this.sliceStride = (paramInt2 * paramInt3);
    this.rowStride = paramInt3;
    this.ip = new int[2 + (int)Math.ceil(Math.sqrt(Math.max(Math.max(paramInt1, paramInt2), paramInt3)))];
    this.w = new double[(int)Math.ceil(Math.max(Math.max(Math.max(paramInt1 / 2, paramInt2 / 2), paramInt3 / 2), Math.max(Math.max(paramInt1 / 2, paramInt2 / 2), paramInt3 / 4) + paramInt3 / 4))];
    this.fftn1 = new DoubleFFT_1D(paramInt1, this.ip, this.w);
    this.fftn2 = new DoubleFFT_1D(paramInt2, this.ip, this.w);
    this.fftn3 = new DoubleFFT_1D(paramInt3, this.ip, this.w);
    this.oldNthreads = ConcurrencyUtils.getNumberOfProcessors();
    this.nt = paramInt1;
    if (this.nt < paramInt2)
      this.nt = paramInt2;
    this.nt *= 8;
    if (this.oldNthreads > 1)
      this.nt *= this.oldNthreads;
    if (2 * paramInt3 == 4)
      this.nt >>= 1;
    else if (2 * paramInt3 < 4)
      this.nt >>= 2;
    this.t = new double[this.nt];
  }

  public void complexForward(double[] paramArrayOfDouble)
  {
    int j = this.n3;
    this.n3 = (2 * this.n3);
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    if (i > this.ip[0] << 2)
      makewt(i >> 2);
    int k = ConcurrencyUtils.getNumberOfProcessors();
    if (k != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (k > 1)
        this.nt *= k;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, -1, paramArrayOfDouble, true);
      cdft3db_subth(-1, paramArrayOfDouble, true);
    }
    else
    {
      xdft3da_sub2(0, -1, paramArrayOfDouble, true);
      cdft3db_sub(-1, paramArrayOfDouble, true);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void complexForward(double[][][] paramArrayOfDouble)
  {
    int j = this.n3;
    this.n3 = (2 * this.n3);
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    if (i > this.ip[0] << 2)
      makewt(i >> 2);
    int k = ConcurrencyUtils.getNumberOfProcessors();
    if (k != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (k > 1)
        this.nt *= k;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, -1, paramArrayOfDouble, true);
      cdft3db_subth(-1, paramArrayOfDouble, true);
    }
    else
    {
      xdft3da_sub2(0, -1, paramArrayOfDouble, true);
      cdft3db_sub(-1, paramArrayOfDouble, true);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void complexInverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int j = this.n3;
    this.n3 = (2 * this.n3);
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    if (i > this.ip[0] << 2)
      makewt(i >> 2);
    int k = ConcurrencyUtils.getNumberOfProcessors();
    if (k != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (k > 1)
        this.nt *= k;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      xdft3da_sub2(0, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_sub(1, paramArrayOfDouble, paramBoolean);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void complexInverse(double[][][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j = this.n3;
    this.n3 = (2 * this.n3);
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    if (i > this.ip[0] << 2)
      makewt(i >> 2);
    int k = ConcurrencyUtils.getNumberOfProcessors();
    if (k != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (k > 1)
        this.nt *= k;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      xdft3da_sub2(0, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_sub(1, paramArrayOfDouble, paramBoolean);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void realForward(double[] paramArrayOfDouble)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth1(1, -1, paramArrayOfDouble, true);
      cdft3db_subth(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    else
    {
      xdft3da_sub1(1, -1, paramArrayOfDouble, true);
      cdft3db_sub(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
  }

  public void realForward(double[][][] paramArrayOfDouble)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth1(1, -1, paramArrayOfDouble, true);
      cdft3db_subth(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    else
    {
      xdft3da_sub1(1, -1, paramArrayOfDouble, true);
      cdft3db_sub(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
  }

  public void realForwardFull(double[] paramArrayOfDouble)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int i3 = ConcurrencyUtils.getNumberOfProcessors();
    if (i3 != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (i3 > 1)
        this.nt *= i3;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, -1, paramArrayOfDouble, true);
      cdft3db_subth(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    else
    {
      xdft3da_sub2(1, -1, paramArrayOfDouble, true);
      cdft3db_sub(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    int i2 = 2 * this.n3;
    int i4 = this.n2 / 2;
    int i5 = this.n1 / 2;
    int i6 = this.n2 * i2;
    int i7 = i2;
    int i1;
    for (int m = this.n1 - 1; m >= 1; m--)
      for (n = 0; n < this.n2; n++)
        for (i1 = 0; i1 < this.n3; i1 += 2)
        {
          i8 = m * this.sliceStride + n * this.rowStride + i1;
          i9 = m * i6 + n * i7 + i1;
          paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
          paramArrayOfDouble[i8] = 0.0D;
          i8++;
          i9++;
          paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
          paramArrayOfDouble[i8] = 0.0D;
        }
    for (int n = 1; n < this.n2; n++)
      for (i1 = 0; i1 < this.n3; i1 += 2)
      {
        i8 = (this.n2 - n) * this.rowStride + i1;
        i9 = (this.n2 - n) * i7 + i1;
        paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
        paramArrayOfDouble[i8] = 0.0D;
        i8++;
        i9++;
        paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
        paramArrayOfDouble[i8] = 0.0D;
      }
    int i11;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (i1 = 1; i1 < this.n3; i1 += 2)
          {
            i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) % this.n2 * i7 + i2 - i1;
            i9 = m * i6 + n * i7 + i1;
            paramArrayOfDouble[i8] = (-paramArrayOfDouble[(i9 + 2)]);
            paramArrayOfDouble[(i8 - 1)] = paramArrayOfDouble[(i9 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          i8 = (this.n1 - m) % this.n1 * i6 + n * i7 + this.n3;
          i9 = m * i6 + (this.n2 - n) * i7 + this.n3;
          i10 = m * i6 + (this.n2 - n) * i7 + 1;
          i11 = m * i6 + (this.n2 - n) * i7;
          paramArrayOfDouble[i8] = paramArrayOfDouble[i10];
          paramArrayOfDouble[i9] = paramArrayOfDouble[i10];
          paramArrayOfDouble[(i8 + 1)] = (-paramArrayOfDouble[i11]);
          paramArrayOfDouble[(i9 + 1)] = paramArrayOfDouble[i11];
        }
    }
    for (m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) * i7;
        i9 = m * i6 + n * i7;
        paramArrayOfDouble[i8] = paramArrayOfDouble[i9];
        paramArrayOfDouble[(i8 + 1)] = (-paramArrayOfDouble[(i9 + 1)]);
      }
    for (m = 1; m < i5; m++)
    {
      i8 = m * i6;
      i9 = (this.n1 - m) * i6;
      i11 = m * i6 + i4 * i7;
      int i12 = (this.n1 - m) * i6 + i4 * i7;
      paramArrayOfDouble[(i8 + this.n3)] = paramArrayOfDouble[(i9 + 1)];
      paramArrayOfDouble[(i9 + this.n3)] = paramArrayOfDouble[(i9 + 1)];
      paramArrayOfDouble[(i8 + this.n3 + 1)] = (-paramArrayOfDouble[i9]);
      paramArrayOfDouble[(i9 + this.n3 + 1)] = paramArrayOfDouble[i9];
      paramArrayOfDouble[(i11 + this.n3)] = paramArrayOfDouble[(i12 + 1)];
      paramArrayOfDouble[(i12 + this.n3)] = paramArrayOfDouble[(i12 + 1)];
      paramArrayOfDouble[(i11 + this.n3 + 1)] = (-paramArrayOfDouble[i12]);
      paramArrayOfDouble[(i12 + this.n3 + 1)] = paramArrayOfDouble[i12];
      paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
      paramArrayOfDouble[(i9 + 1)] = (-paramArrayOfDouble[(i8 + 1)]);
      paramArrayOfDouble[i12] = paramArrayOfDouble[i11];
      paramArrayOfDouble[(i12 + 1)] = (-paramArrayOfDouble[(i11 + 1)]);
    }
    paramArrayOfDouble[this.n3] = paramArrayOfDouble[1];
    paramArrayOfDouble[1] = 0.0D;
    int i8 = i4 * i7;
    int i9 = i5 * i6;
    int i10 = i8 + i9;
    paramArrayOfDouble[(i8 + this.n3)] = paramArrayOfDouble[(i8 + 1)];
    paramArrayOfDouble[(i8 + 1)] = 0.0D;
    paramArrayOfDouble[(i9 + this.n3)] = paramArrayOfDouble[(i9 + 1)];
    paramArrayOfDouble[(i9 + 1)] = 0.0D;
    paramArrayOfDouble[(i10 + this.n3)] = paramArrayOfDouble[(i10 + 1)];
    paramArrayOfDouble[(i10 + 1)] = 0.0D;
    paramArrayOfDouble[(i9 + this.n3 + 1)] = 0.0D;
    paramArrayOfDouble[(i10 + this.n3 + 1)] = 0.0D;
  }

  public void realForwardFull(double[][][] paramArrayOfDouble)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int i3 = ConcurrencyUtils.getNumberOfProcessors();
    if (i3 != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (i3 > 1)
        this.nt *= i3;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, -1, paramArrayOfDouble, true);
      cdft3db_subth(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    else
    {
      xdft3da_sub2(1, -1, paramArrayOfDouble, true);
      cdft3db_sub(-1, paramArrayOfDouble, true);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    int i2 = 2 * this.n3;
    int i4 = this.n2 / 2;
    int i5 = this.n1 / 2;
    int n;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (int i1 = 1; i1 < this.n3; i1 += 2)
          {
            paramArrayOfDouble[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1)] = (-paramArrayOfDouble[m][n][(i1 + 2)]);
            paramArrayOfDouble[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1 - 1)] = paramArrayOfDouble[m][n][(i1 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          paramArrayOfDouble[((this.n1 - m) % this.n1)][n][this.n3] = paramArrayOfDouble[m][(this.n2 - n)][1];
          paramArrayOfDouble[m][(this.n2 - n)][this.n3] = paramArrayOfDouble[m][(this.n2 - n)][1];
          paramArrayOfDouble[((this.n1 - m) % this.n1)][n][(this.n3 + 1)] = (-paramArrayOfDouble[m][(this.n2 - n)][0]);
          paramArrayOfDouble[m][(this.n2 - n)][(this.n3 + 1)] = paramArrayOfDouble[m][(this.n2 - n)][0];
        }
    }
    for (int m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        paramArrayOfDouble[((this.n1 - m) % this.n1)][(this.n2 - n)][0] = paramArrayOfDouble[m][n][0];
        paramArrayOfDouble[((this.n1 - m) % this.n1)][(this.n2 - n)][1] = (-paramArrayOfDouble[m][n][1]);
      }
    for (m = 1; m < i5; m++)
    {
      paramArrayOfDouble[m][0][this.n3] = paramArrayOfDouble[(this.n1 - m)][0][1];
      paramArrayOfDouble[(this.n1 - m)][0][this.n3] = paramArrayOfDouble[(this.n1 - m)][0][1];
      paramArrayOfDouble[m][0][(this.n3 + 1)] = (-paramArrayOfDouble[(this.n1 - m)][0][0]);
      paramArrayOfDouble[(this.n1 - m)][0][(this.n3 + 1)] = paramArrayOfDouble[(this.n1 - m)][0][0];
      paramArrayOfDouble[m][i4][this.n3] = paramArrayOfDouble[(this.n1 - m)][i4][1];
      paramArrayOfDouble[(this.n1 - m)][i4][this.n3] = paramArrayOfDouble[(this.n1 - m)][i4][1];
      paramArrayOfDouble[m][i4][(this.n3 + 1)] = (-paramArrayOfDouble[(this.n1 - m)][i4][0]);
      paramArrayOfDouble[(this.n1 - m)][i4][(this.n3 + 1)] = paramArrayOfDouble[(this.n1 - m)][i4][0];
      paramArrayOfDouble[(this.n1 - m)][0][0] = paramArrayOfDouble[m][0][0];
      paramArrayOfDouble[(this.n1 - m)][0][1] = (-paramArrayOfDouble[m][0][1]);
      paramArrayOfDouble[(this.n1 - m)][i4][0] = paramArrayOfDouble[m][i4][0];
      paramArrayOfDouble[(this.n1 - m)][i4][1] = (-paramArrayOfDouble[m][i4][1]);
    }
    paramArrayOfDouble[0][0][this.n3] = paramArrayOfDouble[0][0][1];
    paramArrayOfDouble[0][0][1] = 0.0D;
    paramArrayOfDouble[0][i4][this.n3] = paramArrayOfDouble[0][i4][1];
    paramArrayOfDouble[0][i4][1] = 0.0D;
    paramArrayOfDouble[i5][0][this.n3] = paramArrayOfDouble[i5][0][1];
    paramArrayOfDouble[i5][0][1] = 0.0D;
    paramArrayOfDouble[i5][i4][this.n3] = paramArrayOfDouble[i5][i4][1];
    paramArrayOfDouble[i5][i4][1] = 0.0D;
    paramArrayOfDouble[i5][0][(this.n3 + 1)] = 0.0D;
    paramArrayOfDouble[i5][i4][(this.n3 + 1)] = 0.0D;
  }

  public void realInverse(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      rdft3d_sub(-1, paramArrayOfDouble);
      cdft3db_subth(1, paramArrayOfDouble, paramBoolean);
      xdft3da_subth1(1, 1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      rdft3d_sub(-1, paramArrayOfDouble);
      cdft3db_sub(1, paramArrayOfDouble, paramBoolean);
      xdft3da_sub1(1, 1, paramArrayOfDouble, paramBoolean);
    }
  }

  public void realInverse(double[][][] paramArrayOfDouble, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      rdft3d_sub(-1, paramArrayOfDouble);
      cdft3db_subth(1, paramArrayOfDouble, paramBoolean);
      xdft3da_subth1(1, 1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      rdft3d_sub(-1, paramArrayOfDouble);
      cdft3db_sub(1, paramArrayOfDouble, paramBoolean);
      xdft3da_sub1(1, 1, paramArrayOfDouble, paramBoolean);
    }
  }

  public void realInverseFull(double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int i3 = ConcurrencyUtils.getNumberOfProcessors();
    if (i3 != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (i3 > 1)
        this.nt *= i3;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_subth(1, paramArrayOfDouble, paramBoolean);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    else
    {
      xdft3da_sub2(1, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_sub(1, paramArrayOfDouble, paramBoolean);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    int i2 = 2 * this.n3;
    int i4 = this.n2 / 2;
    int i5 = this.n1 / 2;
    int i6 = this.n2 * i2;
    int i7 = i2;
    int i1;
    for (int m = this.n1 - 1; m >= 1; m--)
      for (n = 0; n < this.n2; n++)
        for (i1 = 0; i1 < this.n3; i1 += 2)
        {
          i8 = m * this.sliceStride + n * this.rowStride + i1;
          i9 = m * i6 + n * i7 + i1;
          paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
          paramArrayOfDouble[i8] = 0.0D;
          i8++;
          i9++;
          paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
          paramArrayOfDouble[i8] = 0.0D;
        }
    for (int n = 1; n < this.n2; n++)
      for (i1 = 0; i1 < this.n3; i1 += 2)
      {
        i8 = (this.n2 - n) * this.rowStride + i1;
        i9 = (this.n2 - n) * i7 + i1;
        paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
        paramArrayOfDouble[i8] = 0.0D;
        i8++;
        i9++;
        paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
        paramArrayOfDouble[i8] = 0.0D;
      }
    int i11;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (i1 = 1; i1 < this.n3; i1 += 2)
          {
            i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) % this.n2 * i7 + i2 - i1;
            i9 = m * i6 + n * i7 + i1;
            paramArrayOfDouble[i8] = (-paramArrayOfDouble[(i9 + 2)]);
            paramArrayOfDouble[(i8 - 1)] = paramArrayOfDouble[(i9 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          i8 = (this.n1 - m) % this.n1 * i6 + n * i7 + this.n3;
          i9 = m * i6 + (this.n2 - n) * i7 + this.n3;
          i10 = m * i6 + (this.n2 - n) * i7 + 1;
          i11 = m * i6 + (this.n2 - n) * i7;
          paramArrayOfDouble[i8] = paramArrayOfDouble[i10];
          paramArrayOfDouble[i9] = paramArrayOfDouble[i10];
          paramArrayOfDouble[(i8 + 1)] = (-paramArrayOfDouble[i11]);
          paramArrayOfDouble[(i9 + 1)] = paramArrayOfDouble[i11];
        }
    }
    for (m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) * i7;
        i9 = m * i6 + n * i7;
        paramArrayOfDouble[i8] = paramArrayOfDouble[i9];
        paramArrayOfDouble[(i8 + 1)] = (-paramArrayOfDouble[(i9 + 1)]);
      }
    for (m = 1; m < i5; m++)
    {
      i8 = m * i6;
      i9 = (this.n1 - m) * i6;
      i11 = m * i6 + i4 * i7;
      int i12 = (this.n1 - m) * i6 + i4 * i7;
      paramArrayOfDouble[(i8 + this.n3)] = paramArrayOfDouble[(i9 + 1)];
      paramArrayOfDouble[(i9 + this.n3)] = paramArrayOfDouble[(i9 + 1)];
      paramArrayOfDouble[(i8 + this.n3 + 1)] = (-paramArrayOfDouble[i9]);
      paramArrayOfDouble[(i9 + this.n3 + 1)] = paramArrayOfDouble[i9];
      paramArrayOfDouble[(i11 + this.n3)] = paramArrayOfDouble[(i12 + 1)];
      paramArrayOfDouble[(i12 + this.n3)] = paramArrayOfDouble[(i12 + 1)];
      paramArrayOfDouble[(i11 + this.n3 + 1)] = (-paramArrayOfDouble[i12]);
      paramArrayOfDouble[(i12 + this.n3 + 1)] = paramArrayOfDouble[i12];
      paramArrayOfDouble[i9] = paramArrayOfDouble[i8];
      paramArrayOfDouble[(i9 + 1)] = (-paramArrayOfDouble[(i8 + 1)]);
      paramArrayOfDouble[i12] = paramArrayOfDouble[i11];
      paramArrayOfDouble[(i12 + 1)] = (-paramArrayOfDouble[(i11 + 1)]);
    }
    paramArrayOfDouble[this.n3] = paramArrayOfDouble[1];
    paramArrayOfDouble[1] = 0.0D;
    int i8 = i4 * i7;
    int i9 = i5 * i6;
    int i10 = i8 + i9;
    paramArrayOfDouble[(i8 + this.n3)] = paramArrayOfDouble[(i8 + 1)];
    paramArrayOfDouble[(i8 + 1)] = 0.0D;
    paramArrayOfDouble[(i9 + this.n3)] = paramArrayOfDouble[(i9 + 1)];
    paramArrayOfDouble[(i9 + 1)] = 0.0D;
    paramArrayOfDouble[(i10 + this.n3)] = paramArrayOfDouble[(i10 + 1)];
    paramArrayOfDouble[(i10 + 1)] = 0.0D;
    paramArrayOfDouble[(i9 + this.n3 + 1)] = 0.0D;
    paramArrayOfDouble[(i10 + this.n3 + 1)] = 0.0D;
  }

  public void realInverseFull(double[][][] paramArrayOfDouble, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    i <<= 1;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (this.n3 > k << 2)
    {
      k = this.n3 >> 2;
      makect(k, this.w, j);
    }
    int i3 = ConcurrencyUtils.getNumberOfProcessors();
    if (i3 != this.oldNthreads)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 8;
      if (i3 > 1)
        this.nt *= i3;
      if (this.n3 == 4)
        this.nt >>= 1;
      else if (this.n3 < 4)
        this.nt >>= 2;
      this.t = new double[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_subth(1, paramArrayOfDouble, paramBoolean);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    else
    {
      xdft3da_sub2(1, 1, paramArrayOfDouble, paramBoolean);
      cdft3db_sub(1, paramArrayOfDouble, paramBoolean);
      rdft3d_sub(1, paramArrayOfDouble);
    }
    int i2 = 2 * this.n3;
    int i4 = this.n2 / 2;
    int i5 = this.n1 / 2;
    int n;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfDouble);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (int i1 = 1; i1 < this.n3; i1 += 2)
          {
            paramArrayOfDouble[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1)] = (-paramArrayOfDouble[m][n][(i1 + 2)]);
            paramArrayOfDouble[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1 - 1)] = paramArrayOfDouble[m][n][(i1 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          paramArrayOfDouble[((this.n1 - m) % this.n1)][n][this.n3] = paramArrayOfDouble[m][(this.n2 - n)][1];
          paramArrayOfDouble[m][(this.n2 - n)][this.n3] = paramArrayOfDouble[m][(this.n2 - n)][1];
          paramArrayOfDouble[((this.n1 - m) % this.n1)][n][(this.n3 + 1)] = (-paramArrayOfDouble[m][(this.n2 - n)][0]);
          paramArrayOfDouble[m][(this.n2 - n)][(this.n3 + 1)] = paramArrayOfDouble[m][(this.n2 - n)][0];
        }
    }
    for (int m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        paramArrayOfDouble[((this.n1 - m) % this.n1)][(this.n2 - n)][0] = paramArrayOfDouble[m][n][0];
        paramArrayOfDouble[((this.n1 - m) % this.n1)][(this.n2 - n)][1] = (-paramArrayOfDouble[m][n][1]);
      }
    for (m = 1; m < i5; m++)
    {
      paramArrayOfDouble[m][0][this.n3] = paramArrayOfDouble[(this.n1 - m)][0][1];
      paramArrayOfDouble[(this.n1 - m)][0][this.n3] = paramArrayOfDouble[(this.n1 - m)][0][1];
      paramArrayOfDouble[m][0][(this.n3 + 1)] = (-paramArrayOfDouble[(this.n1 - m)][0][0]);
      paramArrayOfDouble[(this.n1 - m)][0][(this.n3 + 1)] = paramArrayOfDouble[(this.n1 - m)][0][0];
      paramArrayOfDouble[m][i4][this.n3] = paramArrayOfDouble[(this.n1 - m)][i4][1];
      paramArrayOfDouble[(this.n1 - m)][i4][this.n3] = paramArrayOfDouble[(this.n1 - m)][i4][1];
      paramArrayOfDouble[m][i4][(this.n3 + 1)] = (-paramArrayOfDouble[(this.n1 - m)][i4][0]);
      paramArrayOfDouble[(this.n1 - m)][i4][(this.n3 + 1)] = paramArrayOfDouble[(this.n1 - m)][i4][0];
      paramArrayOfDouble[(this.n1 - m)][0][0] = paramArrayOfDouble[m][0][0];
      paramArrayOfDouble[(this.n1 - m)][0][1] = (-paramArrayOfDouble[m][0][1]);
      paramArrayOfDouble[(this.n1 - m)][i4][0] = paramArrayOfDouble[m][i4][0];
      paramArrayOfDouble[(this.n1 - m)][i4][1] = (-paramArrayOfDouble[m][i4][1]);
    }
    paramArrayOfDouble[0][0][this.n3] = paramArrayOfDouble[0][0][1];
    paramArrayOfDouble[0][0][1] = 0.0D;
    paramArrayOfDouble[0][i4][this.n3] = paramArrayOfDouble[0][i4][1];
    paramArrayOfDouble[0][i4][1] = 0.0D;
    paramArrayOfDouble[i5][0][this.n3] = paramArrayOfDouble[i5][0][1];
    paramArrayOfDouble[i5][0][1] = 0.0D;
    paramArrayOfDouble[i5][i4][this.n3] = paramArrayOfDouble[i5][i4][1];
    paramArrayOfDouble[i5][i4][1] = 0.0D;
    paramArrayOfDouble[i5][0][(this.n3 + 1)] = 0.0D;
    paramArrayOfDouble[i5][i4][(this.n3 + 1)] = 0.0D;
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

  private void xdft3da_sub1(int paramInt1, int paramInt2, double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int m;
    int j;
    int k;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    if (paramInt2 == -1)
      for (i = 0; i < this.n1; i++)
      {
        m = i * this.sliceStride;
        if (paramInt1 == 0)
          for (j = 0; j < this.n2; j++)
            this.fftn3.complexForward(paramArrayOfDouble, m + j * this.rowStride);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realInverse(paramArrayOfDouble, m + j * this.rowStride, paramBoolean);
        if (this.n3 > 4)
          for (k = 0; k < this.n3; k += 8)
          {
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = 2 * j;
              i2 = 2 * this.n2 + 2 * j;
              i3 = i2 + 2 * this.n2;
              i4 = i3 + 2 * this.n2;
              this.t[i1] = paramArrayOfDouble[n];
              this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
              this.t[i2] = paramArrayOfDouble[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
              this.t[i3] = paramArrayOfDouble[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
              this.t[i4] = paramArrayOfDouble[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
            }
            this.fftn2.complexForward(this.t, 0);
            this.fftn2.complexForward(this.t, 2 * this.n2);
            this.fftn2.complexForward(this.t, 4 * this.n2);
            this.fftn2.complexForward(this.t, 6 * this.n2);
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = 2 * j;
              i2 = 2 * this.n2 + 2 * j;
              i3 = i2 + 2 * this.n2;
              i4 = i3 + 2 * this.n2;
              paramArrayOfDouble[n] = this.t[i1];
              paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfDouble[(n + 2)] = this.t[i2];
              paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfDouble[(n + 4)] = this.t[i3];
              paramArrayOfDouble[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfDouble[(n + 6)] = this.t[i4];
              paramArrayOfDouble[(n + 7)] = this.t[(i4 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
            this.t[i2] = paramArrayOfDouble[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(n + 2)] = this.t[i2];
            paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      m = i * this.sliceStride;
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfDouble, m + j * this.rowStride, paramBoolean);
      if (this.n3 > 4)
        for (k = 0; k < this.n3; k += 8)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            i3 = i2 + 2 * this.n2;
            i4 = i3 + 2 * this.n2;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
            this.t[i2] = paramArrayOfDouble[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
            this.t[i3] = paramArrayOfDouble[(n + 4)];
            this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
            this.t[i4] = paramArrayOfDouble[(n + 6)];
            this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
          }
          this.fftn2.complexInverse(this.t, 0, paramBoolean);
          this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 4 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 6 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            i3 = i2 + 2 * this.n2;
            i4 = i3 + 2 * this.n2;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(n + 2)] = this.t[i2];
            paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
            paramArrayOfDouble[(n + 4)] = this.t[i3];
            paramArrayOfDouble[(n + 5)] = this.t[(i3 + 1)];
            paramArrayOfDouble[(n + 6)] = this.t[i4];
            paramArrayOfDouble[(n + 7)] = this.t[(i4 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          this.t[i1] = paramArrayOfDouble[n];
          this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
          this.t[i2] = paramArrayOfDouble[(n + 2)];
          this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          paramArrayOfDouble[n] = this.t[i1];
          paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
          paramArrayOfDouble[(n + 2)] = this.t[i2];
          paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          this.t[i1] = paramArrayOfDouble[n];
          this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          paramArrayOfDouble[n] = this.t[i1];
          paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
        }
      }
      if (paramInt1 != 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfDouble, m + j * this.rowStride);
    }
  }

  private void xdft3da_sub2(int paramInt1, int paramInt2, double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int m;
    int j;
    int k;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    if (paramInt2 == -1)
      for (i = 0; i < this.n1; i++)
      {
        m = i * this.sliceStride;
        if (paramInt1 == 0)
          for (j = 0; j < this.n2; j++)
            this.fftn3.complexForward(paramArrayOfDouble, m + j * this.rowStride);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfDouble, m + j * this.rowStride);
        if (this.n3 > 4)
          for (k = 0; k < this.n3; k += 8)
          {
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = 2 * j;
              i2 = 2 * this.n2 + 2 * j;
              i3 = i2 + 2 * this.n2;
              i4 = i3 + 2 * this.n2;
              this.t[i1] = paramArrayOfDouble[n];
              this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
              this.t[i2] = paramArrayOfDouble[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
              this.t[i3] = paramArrayOfDouble[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
              this.t[i4] = paramArrayOfDouble[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
            }
            this.fftn2.complexForward(this.t, 0);
            this.fftn2.complexForward(this.t, 2 * this.n2);
            this.fftn2.complexForward(this.t, 4 * this.n2);
            this.fftn2.complexForward(this.t, 6 * this.n2);
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = 2 * j;
              i2 = 2 * this.n2 + 2 * j;
              i3 = i2 + 2 * this.n2;
              i4 = i3 + 2 * this.n2;
              paramArrayOfDouble[n] = this.t[i1];
              paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfDouble[(n + 2)] = this.t[i2];
              paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfDouble[(n + 4)] = this.t[i3];
              paramArrayOfDouble[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfDouble[(n + 6)] = this.t[i4];
              paramArrayOfDouble[(n + 7)] = this.t[(i4 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
            this.t[i2] = paramArrayOfDouble[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(n + 2)] = this.t[i2];
            paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      m = i * this.sliceStride;
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfDouble, m + j * this.rowStride, paramBoolean);
      for (j = 0; j < this.n2; j++)
        this.fftn3.realInverse2(paramArrayOfDouble, m + j * this.rowStride, paramBoolean);
      if (this.n3 > 4)
        for (k = 0; k < this.n3; k += 8)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            i3 = i2 + 2 * this.n2;
            i4 = i3 + 2 * this.n2;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
            this.t[i2] = paramArrayOfDouble[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
            this.t[i3] = paramArrayOfDouble[(n + 4)];
            this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
            this.t[i4] = paramArrayOfDouble[(n + 6)];
            this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
          }
          this.fftn2.complexInverse(this.t, 0, paramBoolean);
          this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 4 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 6 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            i3 = i2 + 2 * this.n2;
            i4 = i3 + 2 * this.n2;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(n + 2)] = this.t[i2];
            paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
            paramArrayOfDouble[(n + 4)] = this.t[i3];
            paramArrayOfDouble[(n + 5)] = this.t[(i3 + 1)];
            paramArrayOfDouble[(n + 6)] = this.t[i4];
            paramArrayOfDouble[(n + 7)] = this.t[(i4 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          this.t[i1] = paramArrayOfDouble[n];
          this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
          this.t[i2] = paramArrayOfDouble[(n + 2)];
          this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          paramArrayOfDouble[n] = this.t[i1];
          paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
          paramArrayOfDouble[(n + 2)] = this.t[i2];
          paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          this.t[i1] = paramArrayOfDouble[n];
          this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          paramArrayOfDouble[n] = this.t[i1];
          paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
        }
      }
    }
  }

  private void xdft3da_sub1(int paramInt1, int paramInt2, double[][][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    if (paramInt2 == -1)
      for (i = 0; i < this.n1; i++)
      {
        if (paramInt1 == 0)
          for (j = 0; j < this.n2; j++)
            this.fftn3.complexForward(paramArrayOfDouble[i][j]);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realInverse(paramArrayOfDouble[i][j], 0, paramBoolean);
        if (this.n3 > 4)
          for (k = 0; k < this.n3; k += 8)
          {
            for (j = 0; j < this.n2; j++)
            {
              m = 2 * j;
              n = 2 * this.n2 + 2 * j;
              i1 = n + 2 * this.n2;
              i2 = i1 + 2 * this.n2;
              this.t[m] = paramArrayOfDouble[i][j][k];
              this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
              this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
              this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
              this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
            }
            this.fftn2.complexForward(this.t, 0);
            this.fftn2.complexForward(this.t, 2 * this.n2);
            this.fftn2.complexForward(this.t, 4 * this.n2);
            this.fftn2.complexForward(this.t, 6 * this.n2);
            for (j = 0; j < this.n2; j++)
            {
              m = 2 * j;
              n = 2 * this.n2 + 2 * j;
              i1 = n + 2 * this.n2;
              i2 = i1 + 2 * this.n2;
              paramArrayOfDouble[i][j][k] = this.t[m];
              paramArrayOfDouble[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfDouble[i][j][(k + 2)] = this.t[n];
              paramArrayOfDouble[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfDouble[i][j][(k + 4)] = this.t[i1];
              paramArrayOfDouble[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfDouble[i][j][(k + 6)] = this.t[i2];
              paramArrayOfDouble[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
            this.t[n] = paramArrayOfDouble[i][j][2];
            this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
            paramArrayOfDouble[i][j][2] = this.t[n];
            paramArrayOfDouble[i][j][3] = this.t[(n + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfDouble[i][j], paramBoolean);
      if (this.n3 > 4)
        for (k = 0; k < this.n3; k += 8)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            i1 = n + 2 * this.n2;
            i2 = i1 + 2 * this.n2;
            this.t[m] = paramArrayOfDouble[i][j][k];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
            this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
            this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
            this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
            this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
          }
          this.fftn2.complexInverse(this.t, 0, paramBoolean);
          this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 4 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 6 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            i1 = n + 2 * this.n2;
            i2 = i1 + 2 * this.n2;
            paramArrayOfDouble[i][j][k] = this.t[m];
            paramArrayOfDouble[i][j][(k + 1)] = this.t[(m + 1)];
            paramArrayOfDouble[i][j][(k + 2)] = this.t[n];
            paramArrayOfDouble[i][j][(k + 3)] = this.t[(n + 1)];
            paramArrayOfDouble[i][j][(k + 4)] = this.t[i1];
            paramArrayOfDouble[i][j][(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfDouble[i][j][(k + 6)] = this.t[i2];
            paramArrayOfDouble[i][j][(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          this.t[m] = paramArrayOfDouble[i][j][0];
          this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
          this.t[n] = paramArrayOfDouble[i][j][2];
          this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          paramArrayOfDouble[i][j][0] = this.t[m];
          paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
          paramArrayOfDouble[i][j][2] = this.t[n];
          paramArrayOfDouble[i][j][3] = this.t[(n + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          this.t[m] = paramArrayOfDouble[i][j][0];
          this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          paramArrayOfDouble[i][j][0] = this.t[m];
          paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
        }
      }
      if (paramInt1 != 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfDouble[i][j], 0);
    }
  }

  private void xdft3da_sub2(int paramInt1, int paramInt2, double[][][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    if (paramInt2 == -1)
      for (i = 0; i < this.n1; i++)
      {
        if (paramInt1 == 0)
          for (j = 0; j < this.n2; j++)
            this.fftn3.complexForward(paramArrayOfDouble[i][j]);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfDouble[i][j]);
        if (this.n3 > 4)
          for (k = 0; k < this.n3; k += 8)
          {
            for (j = 0; j < this.n2; j++)
            {
              m = 2 * j;
              n = 2 * this.n2 + 2 * j;
              i1 = n + 2 * this.n2;
              i2 = i1 + 2 * this.n2;
              this.t[m] = paramArrayOfDouble[i][j][k];
              this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
              this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
              this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
              this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
            }
            this.fftn2.complexForward(this.t, 0);
            this.fftn2.complexForward(this.t, 2 * this.n2);
            this.fftn2.complexForward(this.t, 4 * this.n2);
            this.fftn2.complexForward(this.t, 6 * this.n2);
            for (j = 0; j < this.n2; j++)
            {
              m = 2 * j;
              n = 2 * this.n2 + 2 * j;
              i1 = n + 2 * this.n2;
              i2 = i1 + 2 * this.n2;
              paramArrayOfDouble[i][j][k] = this.t[m];
              paramArrayOfDouble[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfDouble[i][j][(k + 2)] = this.t[n];
              paramArrayOfDouble[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfDouble[i][j][(k + 4)] = this.t[i1];
              paramArrayOfDouble[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfDouble[i][j][(k + 6)] = this.t[i2];
              paramArrayOfDouble[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
            this.t[n] = paramArrayOfDouble[i][j][2];
            this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
            paramArrayOfDouble[i][j][2] = this.t[n];
            paramArrayOfDouble[i][j][3] = this.t[(n + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfDouble[i][j], paramBoolean);
      for (j = 0; j < this.n2; j++)
        this.fftn3.realInverse2(paramArrayOfDouble[i][j], 0, paramBoolean);
      if (this.n3 > 4)
        for (k = 0; k < this.n3; k += 8)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            i1 = n + 2 * this.n2;
            i2 = i1 + 2 * this.n2;
            this.t[m] = paramArrayOfDouble[i][j][k];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
            this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
            this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
            this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
            this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
          }
          this.fftn2.complexInverse(this.t, 0, paramBoolean);
          this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 4 * this.n2, paramBoolean);
          this.fftn2.complexInverse(this.t, 6 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            i1 = n + 2 * this.n2;
            i2 = i1 + 2 * this.n2;
            paramArrayOfDouble[i][j][k] = this.t[m];
            paramArrayOfDouble[i][j][(k + 1)] = this.t[(m + 1)];
            paramArrayOfDouble[i][j][(k + 2)] = this.t[n];
            paramArrayOfDouble[i][j][(k + 3)] = this.t[(n + 1)];
            paramArrayOfDouble[i][j][(k + 4)] = this.t[i1];
            paramArrayOfDouble[i][j][(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfDouble[i][j][(k + 6)] = this.t[i2];
            paramArrayOfDouble[i][j][(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          this.t[m] = paramArrayOfDouble[i][j][0];
          this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
          this.t[n] = paramArrayOfDouble[i][j][2];
          this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          paramArrayOfDouble[i][j][0] = this.t[m];
          paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
          paramArrayOfDouble[i][j][2] = this.t[n];
          paramArrayOfDouble[i][j][3] = this.t[(n + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          this.t[m] = paramArrayOfDouble[i][j][0];
          this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          paramArrayOfDouble[i][j][0] = this.t[m];
          paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
        }
      }
    }
  }

  private void cdft3db_sub(int paramInt, double[] paramArrayOfDouble, boolean paramBoolean)
  {
    int j;
    int m;
    int k;
    int i;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    if (paramInt == -1)
    {
      if (this.n3 > 4)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (k = 0; k < this.n3; k += 8)
          {
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = 2 * i;
              i2 = 2 * this.n1 + 2 * i;
              i3 = i2 + 2 * this.n1;
              i4 = i3 + 2 * this.n1;
              this.t[i1] = paramArrayOfDouble[n];
              this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
              this.t[i2] = paramArrayOfDouble[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
              this.t[i3] = paramArrayOfDouble[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
              this.t[i4] = paramArrayOfDouble[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
            }
            this.fftn1.complexForward(this.t, 0);
            this.fftn1.complexForward(this.t, 2 * this.n1);
            this.fftn1.complexForward(this.t, 4 * this.n1);
            this.fftn1.complexForward(this.t, 6 * this.n1);
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = 2 * i;
              i2 = 2 * this.n1 + 2 * i;
              i3 = i2 + 2 * this.n1;
              i4 = i3 + 2 * this.n1;
              paramArrayOfDouble[n] = this.t[i1];
              paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfDouble[(n + 2)] = this.t[i2];
              paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfDouble[(n + 4)] = this.t[i3];
              paramArrayOfDouble[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfDouble[(n + 6)] = this.t[i4];
              paramArrayOfDouble[(n + 7)] = this.t[(i4 + 1)];
            }
          }
        }
      if (this.n3 == 4)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            i2 = 2 * this.n1 + 2 * i;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
            this.t[i2] = paramArrayOfDouble[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
          }
          this.fftn1.complexForward(this.t, 0);
          this.fftn1.complexForward(this.t, 2 * this.n1);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            i2 = 2 * this.n1 + 2 * i;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(n + 2)] = this.t[i2];
            paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
          }
          this.fftn1.complexForward(this.t, 0);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
          }
        }
    }
    else
    {
      if (this.n3 > 4)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (k = 0; k < this.n3; k += 8)
          {
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = 2 * i;
              i2 = 2 * this.n1 + 2 * i;
              i3 = i2 + 2 * this.n1;
              i4 = i3 + 2 * this.n1;
              this.t[i1] = paramArrayOfDouble[n];
              this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
              this.t[i2] = paramArrayOfDouble[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
              this.t[i3] = paramArrayOfDouble[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
              this.t[i4] = paramArrayOfDouble[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
            }
            this.fftn1.complexInverse(this.t, 0, paramBoolean);
            this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
            this.fftn1.complexInverse(this.t, 4 * this.n1, paramBoolean);
            this.fftn1.complexInverse(this.t, 6 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = 2 * i;
              i2 = 2 * this.n1 + 2 * i;
              i3 = i2 + 2 * this.n1;
              i4 = i3 + 2 * this.n1;
              paramArrayOfDouble[n] = this.t[i1];
              paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfDouble[(n + 2)] = this.t[i2];
              paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfDouble[(n + 4)] = this.t[i3];
              paramArrayOfDouble[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfDouble[(n + 6)] = this.t[i4];
              paramArrayOfDouble[(n + 7)] = this.t[(i4 + 1)];
            }
          }
        }
      if (this.n3 == 4)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            i2 = 2 * this.n1 + 2 * i;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
            this.t[i2] = paramArrayOfDouble[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            i2 = 2 * this.n1 + 2 * i;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfDouble[(n + 2)] = this.t[i2];
            paramArrayOfDouble[(n + 3)] = this.t[(i2 + 1)];
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            this.t[i1] = paramArrayOfDouble[n];
            this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            paramArrayOfDouble[n] = this.t[i1];
            paramArrayOfDouble[(n + 1)] = this.t[(i1 + 1)];
          }
        }
    }
  }

  private void cdft3db_sub(int paramInt, double[][][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j;
    int k;
    int i;
    int m;
    int n;
    int i1;
    int i2;
    if (paramInt == -1)
    {
      if (this.n3 > 4)
        for (j = 0; j < this.n2; j++)
          for (k = 0; k < this.n3; k += 8)
          {
            for (i = 0; i < this.n1; i++)
            {
              m = 2 * i;
              n = 2 * this.n1 + 2 * i;
              i1 = n + 2 * this.n1;
              i2 = i1 + 2 * this.n1;
              this.t[m] = paramArrayOfDouble[i][j][k];
              this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
              this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
              this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
              this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
            }
            this.fftn1.complexForward(this.t, 0);
            this.fftn1.complexForward(this.t, 2 * this.n1);
            this.fftn1.complexForward(this.t, 4 * this.n1);
            this.fftn1.complexForward(this.t, 6 * this.n1);
            for (i = 0; i < this.n1; i++)
            {
              m = 2 * i;
              n = 2 * this.n1 + 2 * i;
              i1 = n + 2 * this.n1;
              i2 = i1 + 2 * this.n1;
              paramArrayOfDouble[i][j][k] = this.t[m];
              paramArrayOfDouble[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfDouble[i][j][(k + 2)] = this.t[n];
              paramArrayOfDouble[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfDouble[i][j][(k + 4)] = this.t[i1];
              paramArrayOfDouble[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfDouble[i][j][(k + 6)] = this.t[i2];
              paramArrayOfDouble[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
      if (this.n3 == 4)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
            this.t[n] = paramArrayOfDouble[i][j][2];
            this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
          }
          this.fftn1.complexForward(this.t, 0);
          this.fftn1.complexForward(this.t, 2 * this.n1);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
            paramArrayOfDouble[i][j][2] = this.t[n];
            paramArrayOfDouble[i][j][3] = this.t[(n + 1)];
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
          }
          this.fftn1.complexForward(this.t, 0);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
          }
        }
    }
    else
    {
      if (this.n3 > 4)
        for (j = 0; j < this.n2; j++)
          for (k = 0; k < this.n3; k += 8)
          {
            for (i = 0; i < this.n1; i++)
            {
              m = 2 * i;
              n = 2 * this.n1 + 2 * i;
              i1 = n + 2 * this.n1;
              i2 = i1 + 2 * this.n1;
              this.t[m] = paramArrayOfDouble[i][j][k];
              this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
              this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
              this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
              this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
            }
            this.fftn1.complexInverse(this.t, 0, paramBoolean);
            this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
            this.fftn1.complexInverse(this.t, 4 * this.n1, paramBoolean);
            this.fftn1.complexInverse(this.t, 6 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              m = 2 * i;
              n = 2 * this.n1 + 2 * i;
              i1 = n + 2 * this.n1;
              i2 = i1 + 2 * this.n1;
              paramArrayOfDouble[i][j][k] = this.t[m];
              paramArrayOfDouble[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfDouble[i][j][(k + 2)] = this.t[n];
              paramArrayOfDouble[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfDouble[i][j][(k + 4)] = this.t[i1];
              paramArrayOfDouble[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfDouble[i][j][(k + 6)] = this.t[i2];
              paramArrayOfDouble[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
      if (this.n3 == 4)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
            this.t[n] = paramArrayOfDouble[i][j][2];
            this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
            paramArrayOfDouble[i][j][2] = this.t[n];
            paramArrayOfDouble[i][j][3] = this.t[(n + 1)];
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            this.t[m] = paramArrayOfDouble[i][j][0];
            this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            paramArrayOfDouble[i][j][0] = this.t[m];
            paramArrayOfDouble[i][j][1] = this.t[(m + 1)];
          }
        }
    }
  }

  private void xdft3da_subth1(final int paramInt1, final int paramInt2, final double[] paramArrayOfDouble, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n1)
      i = this.n1;
    int j = 8 * this.n2;
    if (this.n3 == 4)
      j >>= 1;
    else if (this.n3 < 4)
      j >>= 2;
    final int m = i;
    Future[] arrayOfFuture = new Future[i];
    final int n;
    for (int k = 0; k < i; k++)
    {
      n = k;
      final int i1 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int m;
          int j;
          int k;
          int n;
          int i1;
          int i2;
          int i3;
          int i4;
          if (paramInt2 == -1)
          {
            i = n;
            while (i < DoubleFFT_3D.this.n1)
            {
              m = i * DoubleFFT_3D.this.sliceStride;
              if (paramInt1 == 0)
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  DoubleFFT_3D.this.fftn3.complexForward(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.realInverse(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride, paramBoolean);
              if (DoubleFFT_3D.this.n3 > 4)
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    n = m + j * DoubleFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                    DoubleFFT_3D.this.t[i3] = paramArrayOfDouble[(n + 4)];
                    DoubleFFT_3D.this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
                    DoubleFFT_3D.this.t[i4] = paramArrayOfDouble[(n + 6)];
                    DoubleFFT_3D.this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
                  }
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2);
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    n = m + j * DoubleFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                    paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfDouble[(n + 4)] = DoubleFFT_3D.this.t[i3];
                    paramArrayOfDouble[(n + 5)] = DoubleFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfDouble[(n + 6)] = DoubleFFT_3D.this.t[i4];
                    paramArrayOfDouble[(n + 7)] = DoubleFFT_3D.this.t[(i4 + 1)];
                  }
                }
              if (DoubleFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                }
              }
              if (DoubleFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < DoubleFFT_3D.this.n1)
          {
            m = i * DoubleFFT_3D.this.sliceStride;
            if (paramInt1 == 0)
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.complexInverse(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride, paramBoolean);
            if (DoubleFFT_3D.this.n3 > 4)
              for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                  i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                  DoubleFFT_3D.this.t[i3] = paramArrayOfDouble[(n + 4)];
                  DoubleFFT_3D.this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
                  DoubleFFT_3D.this.t[i4] = paramArrayOfDouble[(n + 6)];
                  DoubleFFT_3D.this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
                }
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                  i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                  paramArrayOfDouble[(n + 4)] = DoubleFFT_3D.this.t[i3];
                  paramArrayOfDouble[(n + 5)] = DoubleFFT_3D.this.t[(i3 + 1)];
                  paramArrayOfDouble[(n + 6)] = DoubleFFT_3D.this.t[i4];
                  paramArrayOfDouble[(n + 7)] = DoubleFFT_3D.this.t[(i4 + 1)];
                }
              }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
              }
            }
            if (paramInt1 != 0)
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.realForward(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride);
            i += m;
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

  private void xdft3da_subth2(final int paramInt1, final int paramInt2, final double[] paramArrayOfDouble, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n1)
      i = this.n1;
    int j = 8 * this.n2;
    if (this.n3 == 4)
      j >>= 1;
    else if (this.n3 < 4)
      j >>= 2;
    final int m = i;
    Future[] arrayOfFuture = new Future[i];
    final int n;
    for (int k = 0; k < i; k++)
    {
      n = k;
      final int i1 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int m;
          int j;
          int k;
          int n;
          int i1;
          int i2;
          int i3;
          int i4;
          if (paramInt2 == -1)
          {
            i = n;
            while (i < DoubleFFT_3D.this.n1)
            {
              m = i * DoubleFFT_3D.this.sliceStride;
              if (paramInt1 == 0)
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  DoubleFFT_3D.this.fftn3.complexForward(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.realForward(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride);
              if (DoubleFFT_3D.this.n3 > 4)
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    n = m + j * DoubleFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                    DoubleFFT_3D.this.t[i3] = paramArrayOfDouble[(n + 4)];
                    DoubleFFT_3D.this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
                    DoubleFFT_3D.this.t[i4] = paramArrayOfDouble[(n + 6)];
                    DoubleFFT_3D.this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
                  }
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2);
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    n = m + j * DoubleFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                    paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfDouble[(n + 4)] = DoubleFFT_3D.this.t[i3];
                    paramArrayOfDouble[(n + 5)] = DoubleFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfDouble[(n + 6)] = DoubleFFT_3D.this.t[i4];
                    paramArrayOfDouble[(n + 7)] = DoubleFFT_3D.this.t[(i4 + 1)];
                  }
                }
              if (DoubleFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                }
              }
              if (DoubleFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < DoubleFFT_3D.this.n1)
          {
            m = i * DoubleFFT_3D.this.sliceStride;
            if (paramInt1 == 0)
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.complexInverse(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride, paramBoolean);
            for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              DoubleFFT_3D.this.fftn3.realInverse2(paramArrayOfDouble, m + j * DoubleFFT_3D.this.rowStride, paramBoolean);
            if (DoubleFFT_3D.this.n3 > 4)
              for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                  i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                  DoubleFFT_3D.this.t[i3] = paramArrayOfDouble[(n + 4)];
                  DoubleFFT_3D.this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
                  DoubleFFT_3D.this.t[i4] = paramArrayOfDouble[(n + 6)];
                  DoubleFFT_3D.this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
                }
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  n = m + j * DoubleFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * DoubleFFT_3D.this.n2;
                  i4 = i3 + 2 * DoubleFFT_3D.this.n2;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                  paramArrayOfDouble[(n + 4)] = DoubleFFT_3D.this.t[i3];
                  paramArrayOfDouble[(n + 5)] = DoubleFFT_3D.this.t[(i3 + 1)];
                  paramArrayOfDouble[(n + 6)] = DoubleFFT_3D.this.t[i4];
                  paramArrayOfDouble[(n + 7)] = DoubleFFT_3D.this.t[(i4 + 1)];
                }
              }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                n = m + j * DoubleFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
              }
            }
            i += m;
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

  private void xdft3da_subth1(final int paramInt1, final int paramInt2, final double[][][] paramArrayOfDouble, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n1)
      i = this.n1;
    int j = 8 * this.n2;
    if (this.n3 == 4)
      j >>= 1;
    else if (this.n3 < 4)
      j >>= 2;
    final int m = i;
    Future[] arrayOfFuture = new Future[i];
    final int n;
    for (int k = 0; k < i; k++)
    {
      n = k;
      final int i1 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          int k;
          int m;
          int n;
          int i1;
          int i2;
          if (paramInt2 == -1)
          {
            i = n;
            while (i < DoubleFFT_3D.this.n1)
            {
              if (paramInt1 == 0)
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  DoubleFFT_3D.this.fftn3.complexForward(paramArrayOfDouble[i][j]);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.realInverse(paramArrayOfDouble[i][j], 0, paramBoolean);
              if (DoubleFFT_3D.this.n3 > 4)
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * DoubleFFT_3D.this.n2;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                    DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][k];
                    DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
                    DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
                    DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
                  }
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2);
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * DoubleFFT_3D.this.n2;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                    paramArrayOfDouble[i][j][k] = DoubleFFT_3D.this.t[m];
                    paramArrayOfDouble[i][j][(k + 1)] = DoubleFFT_3D.this.t[(m + 1)];
                    paramArrayOfDouble[i][j][(k + 2)] = DoubleFFT_3D.this.t[n];
                    paramArrayOfDouble[i][j][(k + 3)] = DoubleFFT_3D.this.t[(n + 1)];
                    paramArrayOfDouble[i][j][(k + 4)] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[i][j][(k + 5)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[i][j][(k + 6)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[i][j][(k + 7)] = DoubleFFT_3D.this.t[(i2 + 1)];
                  }
                }
              if (DoubleFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                  DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][2];
                  DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                  paramArrayOfDouble[i][j][2] = DoubleFFT_3D.this.t[n];
                  paramArrayOfDouble[i][j][3] = DoubleFFT_3D.this.t[(n + 1)];
                }
              }
              if (DoubleFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < DoubleFFT_3D.this.n1)
          {
            if (paramInt1 == 0)
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.complexInverse(paramArrayOfDouble[i][j], paramBoolean);
            if (DoubleFFT_3D.this.n3 > 4)
              for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * DoubleFFT_3D.this.n2;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][k];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
                  DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
                  DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
                }
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * DoubleFFT_3D.this.n2;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                  paramArrayOfDouble[i][j][k] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][(k + 1)] = DoubleFFT_3D.this.t[(m + 1)];
                  paramArrayOfDouble[i][j][(k + 2)] = DoubleFFT_3D.this.t[n];
                  paramArrayOfDouble[i][j][(k + 3)] = DoubleFFT_3D.this.t[(n + 1)];
                  paramArrayOfDouble[i][j][(k + 4)] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[i][j][(k + 5)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[i][j][(k + 6)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[i][j][(k + 7)] = DoubleFFT_3D.this.t[(i2 + 1)];
                }
              }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][2];
                DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                paramArrayOfDouble[i][j][2] = DoubleFFT_3D.this.t[n];
                paramArrayOfDouble[i][j][3] = DoubleFFT_3D.this.t[(n + 1)];
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
              }
            }
            if (paramInt1 != 0)
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.realForward(paramArrayOfDouble[i][j]);
            i += m;
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

  private void xdft3da_subth2(final int paramInt1, final int paramInt2, final double[][][] paramArrayOfDouble, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n1)
      i = this.n1;
    int j = 8 * this.n2;
    if (this.n3 == 4)
      j >>= 1;
    else if (this.n3 < 4)
      j >>= 2;
    final int m = i;
    Future[] arrayOfFuture = new Future[i];
    final int n;
    for (int k = 0; k < i; k++)
    {
      n = k;
      final int i1 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          int k;
          int m;
          int n;
          int i1;
          int i2;
          if (paramInt2 == -1)
          {
            i = n;
            while (i < DoubleFFT_3D.this.n1)
            {
              if (paramInt1 == 0)
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  DoubleFFT_3D.this.fftn3.complexForward(paramArrayOfDouble[i][j]);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.realForward(paramArrayOfDouble[i][j]);
              if (DoubleFFT_3D.this.n3 > 4)
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * DoubleFFT_3D.this.n2;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                    DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][k];
                    DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
                    DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
                    DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
                  }
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2);
                  DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2);
                  for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * DoubleFFT_3D.this.n2;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                    paramArrayOfDouble[i][j][k] = DoubleFFT_3D.this.t[m];
                    paramArrayOfDouble[i][j][(k + 1)] = DoubleFFT_3D.this.t[(m + 1)];
                    paramArrayOfDouble[i][j][(k + 2)] = DoubleFFT_3D.this.t[n];
                    paramArrayOfDouble[i][j][(k + 3)] = DoubleFFT_3D.this.t[(n + 1)];
                    paramArrayOfDouble[i][j][(k + 4)] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[i][j][(k + 5)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[i][j][(k + 6)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[i][j][(k + 7)] = DoubleFFT_3D.this.t[(i2 + 1)];
                  }
                }
              if (DoubleFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                  DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][2];
                  DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                  paramArrayOfDouble[i][j][2] = DoubleFFT_3D.this.t[n];
                  paramArrayOfDouble[i][j][3] = DoubleFFT_3D.this.t[(n + 1)];
                }
              }
              if (DoubleFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                }
                DoubleFFT_3D.this.fftn2.complexForward(DoubleFFT_3D.this.t, i1);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < DoubleFFT_3D.this.n1)
          {
            if (paramInt1 == 0)
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                DoubleFFT_3D.this.fftn3.complexInverse(paramArrayOfDouble[i][j], paramBoolean);
            for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              DoubleFFT_3D.this.fftn3.realInverse2(paramArrayOfDouble[i][j], 0, paramBoolean);
            if (DoubleFFT_3D.this.n3 > 4)
              for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * DoubleFFT_3D.this.n2;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][k];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
                  DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
                  DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
                }
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n2, paramBoolean);
                DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < DoubleFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * DoubleFFT_3D.this.n2;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n2;
                  paramArrayOfDouble[i][j][k] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][(k + 1)] = DoubleFFT_3D.this.t[(m + 1)];
                  paramArrayOfDouble[i][j][(k + 2)] = DoubleFFT_3D.this.t[n];
                  paramArrayOfDouble[i][j][(k + 3)] = DoubleFFT_3D.this.t[(n + 1)];
                  paramArrayOfDouble[i][j][(k + 4)] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[i][j][(k + 5)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[i][j][(k + 6)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[i][j][(k + 7)] = DoubleFFT_3D.this.t[(i2 + 1)];
                }
              }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][2];
                DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * DoubleFFT_3D.this.n2 + 2 * j;
                paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                paramArrayOfDouble[i][j][2] = DoubleFFT_3D.this.t[n];
                paramArrayOfDouble[i][j][3] = DoubleFFT_3D.this.t[(n + 1)];
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
              }
              DoubleFFT_3D.this.fftn2.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
              }
            }
            i += m;
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

  private void cdft3db_subth(final int paramInt, final double[] paramArrayOfDouble, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n2)
      i = this.n2;
    int j = 8 * this.n1;
    if (this.n3 == 4)
      j >>= 1;
    else if (this.n3 < 4)
      j >>= 2;
    final int m = i;
    Future[] arrayOfFuture = new Future[i];
    final int n;
    for (int k = 0; k < i; k++)
    {
      n = k;
      final int i1 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          int m;
          int k;
          int i;
          int n;
          int i1;
          int i2;
          int i3;
          int i4;
          if (paramInt == -1)
          {
            if (DoubleFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                m = j * DoubleFFT_3D.this.rowStride;
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    n = i * DoubleFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n1;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n1;
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                    DoubleFFT_3D.this.t[i3] = paramArrayOfDouble[(n + 4)];
                    DoubleFFT_3D.this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
                    DoubleFFT_3D.this.t[i4] = paramArrayOfDouble[(n + 6)];
                    DoubleFFT_3D.this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
                  }
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1);
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1);
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n1);
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n1);
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    n = i * DoubleFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n1;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n1;
                    paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfDouble[(n + 4)] = DoubleFFT_3D.this.t[i3];
                    paramArrayOfDouble[(n + 5)] = DoubleFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfDouble[(n + 6)] = DoubleFFT_3D.this.t[i4];
                    paramArrayOfDouble[(n + 7)] = DoubleFFT_3D.this.t[(i4 + 1)];
                  }
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                m = j * DoubleFFT_3D.this.rowStride;
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                }
                DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1);
                DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                m = j * DoubleFFT_3D.this.rowStride;
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                }
                DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                }
                j += m;
              }
            }
          }
          else
          {
            if (DoubleFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                m = j * DoubleFFT_3D.this.rowStride;
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    n = i * DoubleFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n1;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n1;
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                    DoubleFFT_3D.this.t[i3] = paramArrayOfDouble[(n + 4)];
                    DoubleFFT_3D.this.t[(i3 + 1)] = paramArrayOfDouble[(n + 5)];
                    DoubleFFT_3D.this.t[i4] = paramArrayOfDouble[(n + 6)];
                    DoubleFFT_3D.this.t[(i4 + 1)] = paramArrayOfDouble[(n + 7)];
                  }
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1, paramBoolean);
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n1, paramBoolean);
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n1, paramBoolean);
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    n = i * DoubleFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * DoubleFFT_3D.this.n1;
                    i4 = i3 + 2 * DoubleFFT_3D.this.n1;
                    paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfDouble[(n + 4)] = DoubleFFT_3D.this.t[i3];
                    paramArrayOfDouble[(n + 5)] = DoubleFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfDouble[(n + 6)] = DoubleFFT_3D.this.t[i4];
                    paramArrayOfDouble[(n + 7)] = DoubleFFT_3D.this.t[(i4 + 1)];
                  }
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                m = j * DoubleFFT_3D.this.rowStride;
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                  DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[(n + 2)];
                  DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[(n + 3)];
                }
                DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1, paramBoolean);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfDouble[(n + 2)] = DoubleFFT_3D.this.t[i2];
                  paramArrayOfDouble[(n + 3)] = DoubleFFT_3D.this.t[(i2 + 1)];
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                m = j * DoubleFFT_3D.this.rowStride;
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[n];
                  DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[(n + 1)];
                }
                DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  n = i * DoubleFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  paramArrayOfDouble[n] = DoubleFFT_3D.this.t[i1];
                  paramArrayOfDouble[(n + 1)] = DoubleFFT_3D.this.t[(i1 + 1)];
                }
                j += m;
              }
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

  private void cdft3db_subth(final int paramInt, final double[][][] paramArrayOfDouble, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n2)
      i = this.n2;
    int j = 8 * this.n1;
    if (this.n3 == 4)
      j >>= 1;
    else if (this.n3 < 4)
      j >>= 2;
    final int m = i;
    Future[] arrayOfFuture = new Future[i];
    final int n;
    for (int k = 0; k < i; k++)
    {
      n = k;
      final int i1 = j * k;
      arrayOfFuture[k] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          int k;
          int i;
          int m;
          int n;
          int i1;
          int i2;
          if (paramInt == -1)
          {
            if (DoubleFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * DoubleFFT_3D.this.n1;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1;
                    DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][k];
                    DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
                    DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
                    DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
                  }
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1);
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1);
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n1);
                  DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n1);
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * DoubleFFT_3D.this.n1;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1;
                    paramArrayOfDouble[i][j][k] = DoubleFFT_3D.this.t[m];
                    paramArrayOfDouble[i][j][(k + 1)] = DoubleFFT_3D.this.t[(m + 1)];
                    paramArrayOfDouble[i][j][(k + 2)] = DoubleFFT_3D.this.t[n];
                    paramArrayOfDouble[i][j][(k + 3)] = DoubleFFT_3D.this.t[(n + 1)];
                    paramArrayOfDouble[i][j][(k + 4)] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[i][j][(k + 5)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[i][j][(k + 6)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[i][j][(k + 7)] = DoubleFFT_3D.this.t[(i2 + 1)];
                  }
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                  DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][2];
                  DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
                }
                DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1);
                DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                  paramArrayOfDouble[i][j][2] = DoubleFFT_3D.this.t[n];
                  paramArrayOfDouble[i][j][3] = DoubleFFT_3D.this.t[(n + 1)];
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                }
                DoubleFFT_3D.this.fftn1.complexForward(DoubleFFT_3D.this.t, i1);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                }
                j += m;
              }
            }
          }
          else
          {
            if (DoubleFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                for (k = 0; k < DoubleFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * DoubleFFT_3D.this.n1;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1;
                    DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][k];
                    DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][(k + 1)];
                    DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][(k + 2)];
                    DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][(k + 3)];
                    DoubleFFT_3D.this.t[i1] = paramArrayOfDouble[i][j][(k + 4)];
                    DoubleFFT_3D.this.t[(i1 + 1)] = paramArrayOfDouble[i][j][(k + 5)];
                    DoubleFFT_3D.this.t[i2] = paramArrayOfDouble[i][j][(k + 6)];
                    DoubleFFT_3D.this.t[(i2 + 1)] = paramArrayOfDouble[i][j][(k + 7)];
                  }
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1, paramBoolean);
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 4 * DoubleFFT_3D.this.n1, paramBoolean);
                  DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 6 * DoubleFFT_3D.this.n1, paramBoolean);
                  for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * DoubleFFT_3D.this.n1;
                    i2 = i1 + 2 * DoubleFFT_3D.this.n1;
                    paramArrayOfDouble[i][j][k] = DoubleFFT_3D.this.t[m];
                    paramArrayOfDouble[i][j][(k + 1)] = DoubleFFT_3D.this.t[(m + 1)];
                    paramArrayOfDouble[i][j][(k + 2)] = DoubleFFT_3D.this.t[n];
                    paramArrayOfDouble[i][j][(k + 3)] = DoubleFFT_3D.this.t[(n + 1)];
                    paramArrayOfDouble[i][j][(k + 4)] = DoubleFFT_3D.this.t[i1];
                    paramArrayOfDouble[i][j][(k + 5)] = DoubleFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfDouble[i][j][(k + 6)] = DoubleFFT_3D.this.t[i2];
                    paramArrayOfDouble[i][j][(k + 7)] = DoubleFFT_3D.this.t[(i2 + 1)];
                  }
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                  DoubleFFT_3D.this.t[n] = paramArrayOfDouble[i][j][2];
                  DoubleFFT_3D.this.t[(n + 1)] = paramArrayOfDouble[i][j][3];
                }
                DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1 + 2 * DoubleFFT_3D.this.n1, paramBoolean);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * DoubleFFT_3D.this.n1 + 2 * i;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                  paramArrayOfDouble[i][j][2] = DoubleFFT_3D.this.t[n];
                  paramArrayOfDouble[i][j][3] = DoubleFFT_3D.this.t[(n + 1)];
                }
                j += m;
              }
            }
            if (DoubleFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < DoubleFFT_3D.this.n2)
              {
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  DoubleFFT_3D.this.t[m] = paramArrayOfDouble[i][j][0];
                  DoubleFFT_3D.this.t[(m + 1)] = paramArrayOfDouble[i][j][1];
                }
                DoubleFFT_3D.this.fftn1.complexInverse(DoubleFFT_3D.this.t, i1, paramBoolean);
                for (i = 0; i < DoubleFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  paramArrayOfDouble[i][j][0] = DoubleFFT_3D.this.t[m];
                  paramArrayOfDouble[i][j][1] = DoubleFFT_3D.this.t[(m + 1)];
                }
                j += m;
              }
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

  private void rdft3d_sub(int paramInt, double[] paramArrayOfDouble)
  {
    int i = this.n1 >> 1;
    int j = this.n2 >> 1;
    int m;
    int i2;
    int i3;
    int i4;
    int i5;
    int i1;
    if (paramInt < 0)
    {
      double d;
      for (k = 1; k < i; k++)
      {
        m = this.n1 - k;
        i2 = k * this.sliceStride;
        i3 = m * this.sliceStride;
        i4 = k * this.sliceStride + j * this.rowStride;
        i5 = m * this.sliceStride + j * this.rowStride;
        d = paramArrayOfDouble[i2] - paramArrayOfDouble[i3];
        paramArrayOfDouble[i2] += paramArrayOfDouble[i3];
        paramArrayOfDouble[i3] = d;
        d = paramArrayOfDouble[(i3 + 1)] - paramArrayOfDouble[(i2 + 1)];
        paramArrayOfDouble[(i2 + 1)] += paramArrayOfDouble[(i3 + 1)];
        paramArrayOfDouble[(i3 + 1)] = d;
        d = paramArrayOfDouble[i4] - paramArrayOfDouble[i5];
        paramArrayOfDouble[i4] += paramArrayOfDouble[i5];
        paramArrayOfDouble[i5] = d;
        d = paramArrayOfDouble[(i5 + 1)] - paramArrayOfDouble[(i4 + 1)];
        paramArrayOfDouble[(i4 + 1)] += paramArrayOfDouble[(i5 + 1)];
        paramArrayOfDouble[(i5 + 1)] = d;
        for (n = 1; n < j; n++)
        {
          i1 = this.n2 - n;
          i2 = k * this.sliceStride + n * this.rowStride;
          i3 = m * this.sliceStride + i1 * this.rowStride;
          d = paramArrayOfDouble[i2] - paramArrayOfDouble[i3];
          paramArrayOfDouble[i2] += paramArrayOfDouble[i3];
          paramArrayOfDouble[i3] = d;
          d = paramArrayOfDouble[(i3 + 1)] - paramArrayOfDouble[(i2 + 1)];
          paramArrayOfDouble[(i2 + 1)] += paramArrayOfDouble[(i3 + 1)];
          paramArrayOfDouble[(i3 + 1)] = d;
          i4 = m * this.sliceStride + n * this.rowStride;
          i5 = k * this.sliceStride + i1 * this.rowStride;
          d = paramArrayOfDouble[i4] - paramArrayOfDouble[i5];
          paramArrayOfDouble[i4] += paramArrayOfDouble[i5];
          paramArrayOfDouble[i5] = d;
          d = paramArrayOfDouble[(i5 + 1)] - paramArrayOfDouble[(i4 + 1)];
          paramArrayOfDouble[(i4 + 1)] += paramArrayOfDouble[(i5 + 1)];
          paramArrayOfDouble[(i5 + 1)] = d;
        }
      }
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        i2 = n * this.rowStride;
        i3 = i1 * this.rowStride;
        d = paramArrayOfDouble[i2] - paramArrayOfDouble[i3];
        paramArrayOfDouble[i2] += paramArrayOfDouble[i3];
        paramArrayOfDouble[i3] = d;
        d = paramArrayOfDouble[(i3 + 1)] - paramArrayOfDouble[(i2 + 1)];
        paramArrayOfDouble[(i2 + 1)] += paramArrayOfDouble[(i3 + 1)];
        paramArrayOfDouble[(i3 + 1)] = d;
        i4 = i * this.sliceStride + n * this.rowStride;
        i5 = i * this.sliceStride + i1 * this.rowStride;
        d = paramArrayOfDouble[i4] - paramArrayOfDouble[i5];
        paramArrayOfDouble[i4] += paramArrayOfDouble[i5];
        paramArrayOfDouble[i5] = d;
        d = paramArrayOfDouble[(i5 + 1)] - paramArrayOfDouble[(i4 + 1)];
        paramArrayOfDouble[(i4 + 1)] += paramArrayOfDouble[(i5 + 1)];
        paramArrayOfDouble[(i5 + 1)] = d;
      }
    }
    for (int k = 1; k < i; k++)
    {
      m = this.n1 - k;
      i2 = m * this.sliceStride;
      i3 = k * this.sliceStride;
      paramArrayOfDouble[i2] = (0.5D * (paramArrayOfDouble[i3] - paramArrayOfDouble[i2]));
      paramArrayOfDouble[i3] -= paramArrayOfDouble[i2];
      paramArrayOfDouble[(i2 + 1)] = (0.5D * (paramArrayOfDouble[(i3 + 1)] + paramArrayOfDouble[(i2 + 1)]));
      paramArrayOfDouble[(i3 + 1)] -= paramArrayOfDouble[(i2 + 1)];
      i4 = m * this.sliceStride + j * this.rowStride;
      i5 = k * this.sliceStride + j * this.rowStride;
      paramArrayOfDouble[i4] = (0.5D * (paramArrayOfDouble[i5] - paramArrayOfDouble[i4]));
      paramArrayOfDouble[i5] -= paramArrayOfDouble[i4];
      paramArrayOfDouble[(i4 + 1)] = (0.5D * (paramArrayOfDouble[(i5 + 1)] + paramArrayOfDouble[(i4 + 1)]));
      paramArrayOfDouble[(i5 + 1)] -= paramArrayOfDouble[(i4 + 1)];
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        i2 = m * this.sliceStride + i1 * this.rowStride;
        i3 = k * this.sliceStride + n * this.rowStride;
        paramArrayOfDouble[i2] = (0.5D * (paramArrayOfDouble[i3] - paramArrayOfDouble[i2]));
        paramArrayOfDouble[i3] -= paramArrayOfDouble[i2];
        paramArrayOfDouble[(i2 + 1)] = (0.5D * (paramArrayOfDouble[(i3 + 1)] + paramArrayOfDouble[(i2 + 1)]));
        paramArrayOfDouble[(i3 + 1)] -= paramArrayOfDouble[(i2 + 1)];
        i4 = k * this.sliceStride + i1 * this.rowStride;
        i5 = m * this.sliceStride + n * this.rowStride;
        paramArrayOfDouble[i4] = (0.5D * (paramArrayOfDouble[i5] - paramArrayOfDouble[i4]));
        paramArrayOfDouble[i5] -= paramArrayOfDouble[i4];
        paramArrayOfDouble[(i4 + 1)] = (0.5D * (paramArrayOfDouble[(i5 + 1)] + paramArrayOfDouble[(i4 + 1)]));
        paramArrayOfDouble[(i5 + 1)] -= paramArrayOfDouble[(i4 + 1)];
      }
    }
    for (int n = 1; n < j; n++)
    {
      i1 = this.n2 - n;
      i2 = i1 * this.rowStride;
      i3 = n * this.rowStride;
      paramArrayOfDouble[i2] = (0.5D * (paramArrayOfDouble[i3] - paramArrayOfDouble[i2]));
      paramArrayOfDouble[i3] -= paramArrayOfDouble[i2];
      paramArrayOfDouble[(i2 + 1)] = (0.5D * (paramArrayOfDouble[(i3 + 1)] + paramArrayOfDouble[(i2 + 1)]));
      paramArrayOfDouble[(i3 + 1)] -= paramArrayOfDouble[(i2 + 1)];
      i4 = i * this.sliceStride + i1 * this.rowStride;
      i5 = i * this.sliceStride + n * this.rowStride;
      paramArrayOfDouble[i4] = (0.5D * (paramArrayOfDouble[i5] - paramArrayOfDouble[i4]));
      paramArrayOfDouble[i5] -= paramArrayOfDouble[i4];
      paramArrayOfDouble[(i4 + 1)] = (0.5D * (paramArrayOfDouble[(i5 + 1)] + paramArrayOfDouble[(i4 + 1)]));
      paramArrayOfDouble[(i5 + 1)] -= paramArrayOfDouble[(i4 + 1)];
    }
  }

  private void rdft3d_sub(int paramInt, double[][][] paramArrayOfDouble)
  {
    int i = this.n1 >> 1;
    int j = this.n2 >> 1;
    int m;
    int i1;
    if (paramInt < 0)
    {
      double d;
      for (k = 1; k < i; k++)
      {
        m = this.n1 - k;
        d = paramArrayOfDouble[k][0][0] - paramArrayOfDouble[m][0][0];
        paramArrayOfDouble[k][0][0] += paramArrayOfDouble[m][0][0];
        paramArrayOfDouble[m][0][0] = d;
        d = paramArrayOfDouble[m][0][1] - paramArrayOfDouble[k][0][1];
        paramArrayOfDouble[k][0][1] += paramArrayOfDouble[m][0][1];
        paramArrayOfDouble[m][0][1] = d;
        d = paramArrayOfDouble[k][j][0] - paramArrayOfDouble[m][j][0];
        paramArrayOfDouble[k][j][0] += paramArrayOfDouble[m][j][0];
        paramArrayOfDouble[m][j][0] = d;
        d = paramArrayOfDouble[m][j][1] - paramArrayOfDouble[k][j][1];
        paramArrayOfDouble[k][j][1] += paramArrayOfDouble[m][j][1];
        paramArrayOfDouble[m][j][1] = d;
        for (n = 1; n < j; n++)
        {
          i1 = this.n2 - n;
          d = paramArrayOfDouble[k][n][0] - paramArrayOfDouble[m][i1][0];
          paramArrayOfDouble[k][n][0] += paramArrayOfDouble[m][i1][0];
          paramArrayOfDouble[m][i1][0] = d;
          d = paramArrayOfDouble[m][i1][1] - paramArrayOfDouble[k][n][1];
          paramArrayOfDouble[k][n][1] += paramArrayOfDouble[m][i1][1];
          paramArrayOfDouble[m][i1][1] = d;
          d = paramArrayOfDouble[m][n][0] - paramArrayOfDouble[k][i1][0];
          paramArrayOfDouble[m][n][0] += paramArrayOfDouble[k][i1][0];
          paramArrayOfDouble[k][i1][0] = d;
          d = paramArrayOfDouble[k][i1][1] - paramArrayOfDouble[m][n][1];
          paramArrayOfDouble[m][n][1] += paramArrayOfDouble[k][i1][1];
          paramArrayOfDouble[k][i1][1] = d;
        }
      }
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        d = paramArrayOfDouble[0][n][0] - paramArrayOfDouble[0][i1][0];
        paramArrayOfDouble[0][n][0] += paramArrayOfDouble[0][i1][0];
        paramArrayOfDouble[0][i1][0] = d;
        d = paramArrayOfDouble[0][i1][1] - paramArrayOfDouble[0][n][1];
        paramArrayOfDouble[0][n][1] += paramArrayOfDouble[0][i1][1];
        paramArrayOfDouble[0][i1][1] = d;
        d = paramArrayOfDouble[i][n][0] - paramArrayOfDouble[i][i1][0];
        paramArrayOfDouble[i][n][0] += paramArrayOfDouble[i][i1][0];
        paramArrayOfDouble[i][i1][0] = d;
        d = paramArrayOfDouble[i][i1][1] - paramArrayOfDouble[i][n][1];
        paramArrayOfDouble[i][n][1] += paramArrayOfDouble[i][i1][1];
        paramArrayOfDouble[i][i1][1] = d;
      }
    }
    for (int k = 1; k < i; k++)
    {
      m = this.n1 - k;
      paramArrayOfDouble[m][0][0] = (0.5D * (paramArrayOfDouble[k][0][0] - paramArrayOfDouble[m][0][0]));
      paramArrayOfDouble[k][0][0] -= paramArrayOfDouble[m][0][0];
      paramArrayOfDouble[m][0][1] = (0.5D * (paramArrayOfDouble[k][0][1] + paramArrayOfDouble[m][0][1]));
      paramArrayOfDouble[k][0][1] -= paramArrayOfDouble[m][0][1];
      paramArrayOfDouble[m][j][0] = (0.5D * (paramArrayOfDouble[k][j][0] - paramArrayOfDouble[m][j][0]));
      paramArrayOfDouble[k][j][0] -= paramArrayOfDouble[m][j][0];
      paramArrayOfDouble[m][j][1] = (0.5D * (paramArrayOfDouble[k][j][1] + paramArrayOfDouble[m][j][1]));
      paramArrayOfDouble[k][j][1] -= paramArrayOfDouble[m][j][1];
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        paramArrayOfDouble[m][i1][0] = (0.5D * (paramArrayOfDouble[k][n][0] - paramArrayOfDouble[m][i1][0]));
        paramArrayOfDouble[k][n][0] -= paramArrayOfDouble[m][i1][0];
        paramArrayOfDouble[m][i1][1] = (0.5D * (paramArrayOfDouble[k][n][1] + paramArrayOfDouble[m][i1][1]));
        paramArrayOfDouble[k][n][1] -= paramArrayOfDouble[m][i1][1];
        paramArrayOfDouble[k][i1][0] = (0.5D * (paramArrayOfDouble[m][n][0] - paramArrayOfDouble[k][i1][0]));
        paramArrayOfDouble[m][n][0] -= paramArrayOfDouble[k][i1][0];
        paramArrayOfDouble[k][i1][1] = (0.5D * (paramArrayOfDouble[m][n][1] + paramArrayOfDouble[k][i1][1]));
        paramArrayOfDouble[m][n][1] -= paramArrayOfDouble[k][i1][1];
      }
    }
    for (int n = 1; n < j; n++)
    {
      i1 = this.n2 - n;
      paramArrayOfDouble[0][i1][0] = (0.5D * (paramArrayOfDouble[0][n][0] - paramArrayOfDouble[0][i1][0]));
      paramArrayOfDouble[0][n][0] -= paramArrayOfDouble[0][i1][0];
      paramArrayOfDouble[0][i1][1] = (0.5D * (paramArrayOfDouble[0][n][1] + paramArrayOfDouble[0][i1][1]));
      paramArrayOfDouble[0][n][1] -= paramArrayOfDouble[0][i1][1];
      paramArrayOfDouble[i][i1][0] = (0.5D * (paramArrayOfDouble[i][n][0] - paramArrayOfDouble[i][i1][0]));
      paramArrayOfDouble[i][n][0] -= paramArrayOfDouble[i][i1][0];
      paramArrayOfDouble[i][i1][1] = (0.5D * (paramArrayOfDouble[i][n][1] + paramArrayOfDouble[i][i1][1]));
      paramArrayOfDouble[i][n][1] -= paramArrayOfDouble[i][i1][1];
    }
  }

  private void fillSymmetric(final double[] paramArrayOfDouble)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    Future[] arrayOfFuture = new Future[i];
    int j = this.n1 / i;
    final int k = this.n3 * 2;
    final int m = this.n2 * k;
    final int n = k;
    final int i1 = this.n2 / 2;
    for (int i2 = 0; i2 < i; i2++)
    {
      final int i3 = i2 * j;
      final int i4 = i3 + j;
      arrayOfFuture[i2] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          int m;
          int n;
          for (int i = i3; i < i4; i++)
            for (j = 0; j < DoubleFFT_3D.this.n2; j++)
              for (int k = 1; k < DoubleFFT_3D.this.n3; k += 2)
              {
                m = (DoubleFFT_3D.this.n1 - i) % DoubleFFT_3D.this.n1 * m + (DoubleFFT_3D.this.n2 - j) % DoubleFFT_3D.this.n2 * n + k - k;
                n = i * m + j * n + k;
                paramArrayOfDouble[m] = (-paramArrayOfDouble[(n + 2)]);
                paramArrayOfDouble[(m - 1)] = paramArrayOfDouble[(n + 1)];
              }
          for (i = i3; i < i4; i++)
            for (j = 1; j < i1; j++)
            {
              m = (DoubleFFT_3D.this.n1 - i) % DoubleFFT_3D.this.n1 * m + j * n + DoubleFFT_3D.this.n3;
              n = i * m + (DoubleFFT_3D.this.n2 - j) * n;
              int i1 = i * m + (DoubleFFT_3D.this.n2 - j) * n + DoubleFFT_3D.this.n3;
              paramArrayOfDouble[m] = paramArrayOfDouble[(n + 1)];
              paramArrayOfDouble[i1] = paramArrayOfDouble[(n + 1)];
              paramArrayOfDouble[(m + 1)] = (-paramArrayOfDouble[n]);
              paramArrayOfDouble[(i1 + 1)] = paramArrayOfDouble[n];
            }
        }
      });
    }
    try
    {
      for (i2 = 0; i2 < i; i2++)
        arrayOfFuture[i2].get();
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

  private void fillSymmetric(final double[][][] paramArrayOfDouble)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    Future[] arrayOfFuture = new Future[i];
    int j = this.n1 / i;
    final int k = this.n3 * 2;
    final int m = this.n2 / 2;
    for (int n = 0; n < i; n++)
    {
      final int i1 = n * j;
      final int i2 = i1 + j;
      arrayOfFuture[n] = ConcurrencyUtils.threadPool.submit(new Runnable()
      {
        public void run()
        {
          int j;
          for (int i = i1; i < i2; i++)
            for (j = 0; j < DoubleFFT_3D.this.n2; j++)
            {
              int k = 1;
              while (k < DoubleFFT_3D.this.n3)
              {
                paramArrayOfDouble[((DoubleFFT_3D.this.n1 - i) % DoubleFFT_3D.this.n1)][((DoubleFFT_3D.this.n2 - j) % DoubleFFT_3D.this.n2)][(k - k)] = (-paramArrayOfDouble[i][j][(k + 2)]);
                paramArrayOfDouble[((DoubleFFT_3D.this.n1 - i) % DoubleFFT_3D.this.n1)][((DoubleFFT_3D.this.n2 - j) % DoubleFFT_3D.this.n2)][(k - k - 1)] = paramArrayOfDouble[i][j][(k + 1)];
                k += 2;
              }
            }
          for (i = i1; i < i2; i++)
            for (j = 1; j < m; j++)
            {
              paramArrayOfDouble[((DoubleFFT_3D.this.n1 - i) % DoubleFFT_3D.this.n1)][j][DoubleFFT_3D.this.n3] = paramArrayOfDouble[i][(DoubleFFT_3D.this.n2 - j)][1];
              paramArrayOfDouble[i][(DoubleFFT_3D.this.n2 - j)][DoubleFFT_3D.this.n3] = paramArrayOfDouble[i][(DoubleFFT_3D.this.n2 - j)][1];
              paramArrayOfDouble[((DoubleFFT_3D.this.n1 - i) % DoubleFFT_3D.this.n1)][j][(DoubleFFT_3D.this.n3 + 1)] = (-paramArrayOfDouble[i][(DoubleFFT_3D.this.n2 - j)][0]);
              paramArrayOfDouble[i][(DoubleFFT_3D.this.n2 - j)][(DoubleFFT_3D.this.n3 + 1)] = paramArrayOfDouble[i][(DoubleFFT_3D.this.n2 - j)][0];
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.DoubleFFT_3D
 * JD-Core Version:    0.6.1
 */