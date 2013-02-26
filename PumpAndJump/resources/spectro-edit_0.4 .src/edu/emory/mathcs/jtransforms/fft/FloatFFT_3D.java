package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatFFT_3D
{
  private int n1;
  private int n2;
  private int n3;
  private int sliceStride;
  private int rowStride;
  private int[] ip;
  private float[] w;
  private float[] t;
  private FloatFFT_1D fftn1;
  private FloatFFT_1D fftn2;
  private FloatFFT_1D fftn3;
  private int oldNthreads;
  private int nt;

  public FloatFFT_3D(int paramInt1, int paramInt2, int paramInt3)
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
    this.w = new float[(int)Math.ceil(Math.max(Math.max(Math.max(paramInt1 / 2, paramInt2 / 2), paramInt3 / 2), Math.max(Math.max(paramInt1 / 2, paramInt2 / 2), paramInt3 / 4) + paramInt3 / 4))];
    this.fftn1 = new FloatFFT_1D(paramInt1, this.ip, this.w);
    this.fftn2 = new FloatFFT_1D(paramInt2, this.ip, this.w);
    this.fftn3 = new FloatFFT_1D(paramInt3, this.ip, this.w);
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
    this.t = new float[this.nt];
  }

  public void complexForward(float[] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, -1, paramArrayOfFloat, true);
      cdft3db_subth(-1, paramArrayOfFloat, true);
    }
    else
    {
      xdft3da_sub2(0, -1, paramArrayOfFloat, true);
      cdft3db_sub(-1, paramArrayOfFloat, true);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void complexForward(float[][][] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, -1, paramArrayOfFloat, true);
      cdft3db_subth(-1, paramArrayOfFloat, true);
    }
    else
    {
      xdft3da_sub2(0, -1, paramArrayOfFloat, true);
      cdft3db_sub(-1, paramArrayOfFloat, true);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void complexInverse(float[] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      xdft3da_sub2(0, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_sub(1, paramArrayOfFloat, paramBoolean);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void complexInverse(float[][][] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthreads = k;
    }
    if ((k > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(0, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      xdft3da_sub2(0, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_sub(1, paramArrayOfFloat, paramBoolean);
    }
    this.n3 = j;
    this.sliceStride = (this.n2 * this.n3);
    this.rowStride = this.n3;
  }

  public void realForward(float[] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth1(1, -1, paramArrayOfFloat, true);
      cdft3db_subth(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    else
    {
      xdft3da_sub1(1, -1, paramArrayOfFloat, true);
      cdft3db_sub(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
    }
  }

  public void realForward(float[][][] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth1(1, -1, paramArrayOfFloat, true);
      cdft3db_subth(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    else
    {
      xdft3da_sub1(1, -1, paramArrayOfFloat, true);
      cdft3db_sub(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
    }
  }

  public void realForwardFull(float[] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, -1, paramArrayOfFloat, true);
      cdft3db_subth(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    else
    {
      xdft3da_sub2(1, -1, paramArrayOfFloat, true);
      cdft3db_sub(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
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
          paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
          paramArrayOfFloat[i8] = 0.0F;
          i8++;
          i9++;
          paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
          paramArrayOfFloat[i8] = 0.0F;
        }
    for (int n = 1; n < this.n2; n++)
      for (i1 = 0; i1 < this.n3; i1 += 2)
      {
        i8 = (this.n2 - n) * this.rowStride + i1;
        i9 = (this.n2 - n) * i7 + i1;
        paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
        paramArrayOfFloat[i8] = 0.0F;
        i8++;
        i9++;
        paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
        paramArrayOfFloat[i8] = 0.0F;
      }
    int i11;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (i1 = 1; i1 < this.n3; i1 += 2)
          {
            i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) % this.n2 * i7 + i2 - i1;
            i9 = m * i6 + n * i7 + i1;
            paramArrayOfFloat[i8] = (-paramArrayOfFloat[(i9 + 2)]);
            paramArrayOfFloat[(i8 - 1)] = paramArrayOfFloat[(i9 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          i8 = (this.n1 - m) % this.n1 * i6 + n * i7 + this.n3;
          i9 = m * i6 + (this.n2 - n) * i7 + this.n3;
          i10 = m * i6 + (this.n2 - n) * i7 + 1;
          i11 = m * i6 + (this.n2 - n) * i7;
          paramArrayOfFloat[i8] = paramArrayOfFloat[i10];
          paramArrayOfFloat[i9] = paramArrayOfFloat[i10];
          paramArrayOfFloat[(i8 + 1)] = (-paramArrayOfFloat[i11]);
          paramArrayOfFloat[(i9 + 1)] = paramArrayOfFloat[i11];
        }
    }
    for (m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) * i7;
        i9 = m * i6 + n * i7;
        paramArrayOfFloat[i8] = paramArrayOfFloat[i9];
        paramArrayOfFloat[(i8 + 1)] = (-paramArrayOfFloat[(i9 + 1)]);
      }
    for (m = 1; m < i5; m++)
    {
      i8 = m * i6;
      i9 = (this.n1 - m) * i6;
      i11 = m * i6 + i4 * i7;
      int i12 = (this.n1 - m) * i6 + i4 * i7;
      paramArrayOfFloat[(i8 + this.n3)] = paramArrayOfFloat[(i9 + 1)];
      paramArrayOfFloat[(i9 + this.n3)] = paramArrayOfFloat[(i9 + 1)];
      paramArrayOfFloat[(i8 + this.n3 + 1)] = (-paramArrayOfFloat[i9]);
      paramArrayOfFloat[(i9 + this.n3 + 1)] = paramArrayOfFloat[i9];
      paramArrayOfFloat[(i11 + this.n3)] = paramArrayOfFloat[(i12 + 1)];
      paramArrayOfFloat[(i12 + this.n3)] = paramArrayOfFloat[(i12 + 1)];
      paramArrayOfFloat[(i11 + this.n3 + 1)] = (-paramArrayOfFloat[i12]);
      paramArrayOfFloat[(i12 + this.n3 + 1)] = paramArrayOfFloat[i12];
      paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
      paramArrayOfFloat[(i9 + 1)] = (-paramArrayOfFloat[(i8 + 1)]);
      paramArrayOfFloat[i12] = paramArrayOfFloat[i11];
      paramArrayOfFloat[(i12 + 1)] = (-paramArrayOfFloat[(i11 + 1)]);
    }
    paramArrayOfFloat[this.n3] = paramArrayOfFloat[1];
    paramArrayOfFloat[1] = 0.0F;
    int i8 = i4 * i7;
    int i9 = i5 * i6;
    int i10 = i8 + i9;
    paramArrayOfFloat[(i8 + this.n3)] = paramArrayOfFloat[(i8 + 1)];
    paramArrayOfFloat[(i8 + 1)] = 0.0F;
    paramArrayOfFloat[(i9 + this.n3)] = paramArrayOfFloat[(i9 + 1)];
    paramArrayOfFloat[(i9 + 1)] = 0.0F;
    paramArrayOfFloat[(i10 + this.n3)] = paramArrayOfFloat[(i10 + 1)];
    paramArrayOfFloat[(i10 + 1)] = 0.0F;
    paramArrayOfFloat[(i9 + this.n3 + 1)] = 0.0F;
    paramArrayOfFloat[(i10 + this.n3 + 1)] = 0.0F;
  }

  public void realForwardFull(float[][][] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, -1, paramArrayOfFloat, true);
      cdft3db_subth(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    else
    {
      xdft3da_sub2(1, -1, paramArrayOfFloat, true);
      cdft3db_sub(-1, paramArrayOfFloat, true);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    int i2 = 2 * this.n3;
    int i4 = this.n2 / 2;
    int i5 = this.n1 / 2;
    int n;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (int i1 = 1; i1 < this.n3; i1 += 2)
          {
            paramArrayOfFloat[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1)] = (-paramArrayOfFloat[m][n][(i1 + 2)]);
            paramArrayOfFloat[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1 - 1)] = paramArrayOfFloat[m][n][(i1 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          paramArrayOfFloat[((this.n1 - m) % this.n1)][n][this.n3] = paramArrayOfFloat[m][(this.n2 - n)][1];
          paramArrayOfFloat[m][(this.n2 - n)][this.n3] = paramArrayOfFloat[m][(this.n2 - n)][1];
          paramArrayOfFloat[((this.n1 - m) % this.n1)][n][(this.n3 + 1)] = (-paramArrayOfFloat[m][(this.n2 - n)][0]);
          paramArrayOfFloat[m][(this.n2 - n)][(this.n3 + 1)] = paramArrayOfFloat[m][(this.n2 - n)][0];
        }
    }
    for (int m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        paramArrayOfFloat[((this.n1 - m) % this.n1)][(this.n2 - n)][0] = paramArrayOfFloat[m][n][0];
        paramArrayOfFloat[((this.n1 - m) % this.n1)][(this.n2 - n)][1] = (-paramArrayOfFloat[m][n][1]);
      }
    for (m = 1; m < i5; m++)
    {
      paramArrayOfFloat[m][0][this.n3] = paramArrayOfFloat[(this.n1 - m)][0][1];
      paramArrayOfFloat[(this.n1 - m)][0][this.n3] = paramArrayOfFloat[(this.n1 - m)][0][1];
      paramArrayOfFloat[m][0][(this.n3 + 1)] = (-paramArrayOfFloat[(this.n1 - m)][0][0]);
      paramArrayOfFloat[(this.n1 - m)][0][(this.n3 + 1)] = paramArrayOfFloat[(this.n1 - m)][0][0];
      paramArrayOfFloat[m][i4][this.n3] = paramArrayOfFloat[(this.n1 - m)][i4][1];
      paramArrayOfFloat[(this.n1 - m)][i4][this.n3] = paramArrayOfFloat[(this.n1 - m)][i4][1];
      paramArrayOfFloat[m][i4][(this.n3 + 1)] = (-paramArrayOfFloat[(this.n1 - m)][i4][0]);
      paramArrayOfFloat[(this.n1 - m)][i4][(this.n3 + 1)] = paramArrayOfFloat[(this.n1 - m)][i4][0];
      paramArrayOfFloat[(this.n1 - m)][0][0] = paramArrayOfFloat[m][0][0];
      paramArrayOfFloat[(this.n1 - m)][0][1] = (-paramArrayOfFloat[m][0][1]);
      paramArrayOfFloat[(this.n1 - m)][i4][0] = paramArrayOfFloat[m][i4][0];
      paramArrayOfFloat[(this.n1 - m)][i4][1] = (-paramArrayOfFloat[m][i4][1]);
    }
    paramArrayOfFloat[0][0][this.n3] = paramArrayOfFloat[0][0][1];
    paramArrayOfFloat[0][0][1] = 0.0F;
    paramArrayOfFloat[0][i4][this.n3] = paramArrayOfFloat[0][i4][1];
    paramArrayOfFloat[0][i4][1] = 0.0F;
    paramArrayOfFloat[i5][0][this.n3] = paramArrayOfFloat[i5][0][1];
    paramArrayOfFloat[i5][0][1] = 0.0F;
    paramArrayOfFloat[i5][i4][this.n3] = paramArrayOfFloat[i5][i4][1];
    paramArrayOfFloat[i5][i4][1] = 0.0F;
    paramArrayOfFloat[i5][0][(this.n3 + 1)] = 0.0F;
    paramArrayOfFloat[i5][i4][(this.n3 + 1)] = 0.0F;
  }

  public void realInverse(float[] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      rdft3d_sub(-1, paramArrayOfFloat);
      cdft3db_subth(1, paramArrayOfFloat, paramBoolean);
      xdft3da_subth1(1, 1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      rdft3d_sub(-1, paramArrayOfFloat);
      cdft3db_sub(1, paramArrayOfFloat, paramBoolean);
      xdft3da_sub1(1, 1, paramArrayOfFloat, paramBoolean);
    }
  }

  public void realInverse(float[][][] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthreads = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      rdft3d_sub(-1, paramArrayOfFloat);
      cdft3db_subth(1, paramArrayOfFloat, paramBoolean);
      xdft3da_subth1(1, 1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      rdft3d_sub(-1, paramArrayOfFloat);
      cdft3db_sub(1, paramArrayOfFloat, paramBoolean);
      xdft3da_sub1(1, 1, paramArrayOfFloat, paramBoolean);
    }
  }

  public void realInverseFull(float[] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_subth(1, paramArrayOfFloat, paramBoolean);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    else
    {
      xdft3da_sub2(1, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_sub(1, paramArrayOfFloat, paramBoolean);
      rdft3d_sub(1, paramArrayOfFloat);
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
          paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
          paramArrayOfFloat[i8] = 0.0F;
          i8++;
          i9++;
          paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
          paramArrayOfFloat[i8] = 0.0F;
        }
    for (int n = 1; n < this.n2; n++)
      for (i1 = 0; i1 < this.n3; i1 += 2)
      {
        i8 = (this.n2 - n) * this.rowStride + i1;
        i9 = (this.n2 - n) * i7 + i1;
        paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
        paramArrayOfFloat[i8] = 0.0F;
        i8++;
        i9++;
        paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
        paramArrayOfFloat[i8] = 0.0F;
      }
    int i11;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (i1 = 1; i1 < this.n3; i1 += 2)
          {
            i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) % this.n2 * i7 + i2 - i1;
            i9 = m * i6 + n * i7 + i1;
            paramArrayOfFloat[i8] = (-paramArrayOfFloat[(i9 + 2)]);
            paramArrayOfFloat[(i8 - 1)] = paramArrayOfFloat[(i9 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          i8 = (this.n1 - m) % this.n1 * i6 + n * i7 + this.n3;
          i9 = m * i6 + (this.n2 - n) * i7 + this.n3;
          i10 = m * i6 + (this.n2 - n) * i7 + 1;
          i11 = m * i6 + (this.n2 - n) * i7;
          paramArrayOfFloat[i8] = paramArrayOfFloat[i10];
          paramArrayOfFloat[i9] = paramArrayOfFloat[i10];
          paramArrayOfFloat[(i8 + 1)] = (-paramArrayOfFloat[i11]);
          paramArrayOfFloat[(i9 + 1)] = paramArrayOfFloat[i11];
        }
    }
    for (m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        i8 = (this.n1 - m) % this.n1 * i6 + (this.n2 - n) * i7;
        i9 = m * i6 + n * i7;
        paramArrayOfFloat[i8] = paramArrayOfFloat[i9];
        paramArrayOfFloat[(i8 + 1)] = (-paramArrayOfFloat[(i9 + 1)]);
      }
    for (m = 1; m < i5; m++)
    {
      i8 = m * i6;
      i9 = (this.n1 - m) * i6;
      i11 = m * i6 + i4 * i7;
      int i12 = (this.n1 - m) * i6 + i4 * i7;
      paramArrayOfFloat[(i8 + this.n3)] = paramArrayOfFloat[(i9 + 1)];
      paramArrayOfFloat[(i9 + this.n3)] = paramArrayOfFloat[(i9 + 1)];
      paramArrayOfFloat[(i8 + this.n3 + 1)] = (-paramArrayOfFloat[i9]);
      paramArrayOfFloat[(i9 + this.n3 + 1)] = paramArrayOfFloat[i9];
      paramArrayOfFloat[(i11 + this.n3)] = paramArrayOfFloat[(i12 + 1)];
      paramArrayOfFloat[(i12 + this.n3)] = paramArrayOfFloat[(i12 + 1)];
      paramArrayOfFloat[(i11 + this.n3 + 1)] = (-paramArrayOfFloat[i12]);
      paramArrayOfFloat[(i12 + this.n3 + 1)] = paramArrayOfFloat[i12];
      paramArrayOfFloat[i9] = paramArrayOfFloat[i8];
      paramArrayOfFloat[(i9 + 1)] = (-paramArrayOfFloat[(i8 + 1)]);
      paramArrayOfFloat[i12] = paramArrayOfFloat[i11];
      paramArrayOfFloat[(i12 + 1)] = (-paramArrayOfFloat[(i11 + 1)]);
    }
    paramArrayOfFloat[this.n3] = paramArrayOfFloat[1];
    paramArrayOfFloat[1] = 0.0F;
    int i8 = i4 * i7;
    int i9 = i5 * i6;
    int i10 = i8 + i9;
    paramArrayOfFloat[(i8 + this.n3)] = paramArrayOfFloat[(i8 + 1)];
    paramArrayOfFloat[(i8 + 1)] = 0.0F;
    paramArrayOfFloat[(i9 + this.n3)] = paramArrayOfFloat[(i9 + 1)];
    paramArrayOfFloat[(i9 + 1)] = 0.0F;
    paramArrayOfFloat[(i10 + this.n3)] = paramArrayOfFloat[(i10 + 1)];
    paramArrayOfFloat[(i10 + 1)] = 0.0F;
    paramArrayOfFloat[(i9 + this.n3 + 1)] = 0.0F;
    paramArrayOfFloat[(i10 + this.n3 + 1)] = 0.0F;
  }

  public void realInverseFull(float[][][] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthreads = i3;
    }
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      xdft3da_subth2(1, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_subth(1, paramArrayOfFloat, paramBoolean);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    else
    {
      xdft3da_sub2(1, 1, paramArrayOfFloat, paramBoolean);
      cdft3db_sub(1, paramArrayOfFloat, paramBoolean);
      rdft3d_sub(1, paramArrayOfFloat);
    }
    int i2 = 2 * this.n3;
    int i4 = this.n2 / 2;
    int i5 = this.n1 / 2;
    int n;
    if ((i3 > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      for (m = 0; m < this.n1; m++)
        for (n = 0; n < this.n2; n++)
          for (int i1 = 1; i1 < this.n3; i1 += 2)
          {
            paramArrayOfFloat[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1)] = (-paramArrayOfFloat[m][n][(i1 + 2)]);
            paramArrayOfFloat[((this.n1 - m) % this.n1)][((this.n2 - n) % this.n2)][(i2 - i1 - 1)] = paramArrayOfFloat[m][n][(i1 + 1)];
          }
      for (m = 0; m < this.n1; m++)
        for (n = 1; n < i4; n++)
        {
          paramArrayOfFloat[((this.n1 - m) % this.n1)][n][this.n3] = paramArrayOfFloat[m][(this.n2 - n)][1];
          paramArrayOfFloat[m][(this.n2 - n)][this.n3] = paramArrayOfFloat[m][(this.n2 - n)][1];
          paramArrayOfFloat[((this.n1 - m) % this.n1)][n][(this.n3 + 1)] = (-paramArrayOfFloat[m][(this.n2 - n)][0]);
          paramArrayOfFloat[m][(this.n2 - n)][(this.n3 + 1)] = paramArrayOfFloat[m][(this.n2 - n)][0];
        }
    }
    for (int m = 0; m < this.n1; m++)
      for (n = 1; n < i4; n++)
      {
        paramArrayOfFloat[((this.n1 - m) % this.n1)][(this.n2 - n)][0] = paramArrayOfFloat[m][n][0];
        paramArrayOfFloat[((this.n1 - m) % this.n1)][(this.n2 - n)][1] = (-paramArrayOfFloat[m][n][1]);
      }
    for (m = 1; m < i5; m++)
    {
      paramArrayOfFloat[m][0][this.n3] = paramArrayOfFloat[(this.n1 - m)][0][1];
      paramArrayOfFloat[(this.n1 - m)][0][this.n3] = paramArrayOfFloat[(this.n1 - m)][0][1];
      paramArrayOfFloat[m][0][(this.n3 + 1)] = (-paramArrayOfFloat[(this.n1 - m)][0][0]);
      paramArrayOfFloat[(this.n1 - m)][0][(this.n3 + 1)] = paramArrayOfFloat[(this.n1 - m)][0][0];
      paramArrayOfFloat[m][i4][this.n3] = paramArrayOfFloat[(this.n1 - m)][i4][1];
      paramArrayOfFloat[(this.n1 - m)][i4][this.n3] = paramArrayOfFloat[(this.n1 - m)][i4][1];
      paramArrayOfFloat[m][i4][(this.n3 + 1)] = (-paramArrayOfFloat[(this.n1 - m)][i4][0]);
      paramArrayOfFloat[(this.n1 - m)][i4][(this.n3 + 1)] = paramArrayOfFloat[(this.n1 - m)][i4][0];
      paramArrayOfFloat[(this.n1 - m)][0][0] = paramArrayOfFloat[m][0][0];
      paramArrayOfFloat[(this.n1 - m)][0][1] = (-paramArrayOfFloat[m][0][1]);
      paramArrayOfFloat[(this.n1 - m)][i4][0] = paramArrayOfFloat[m][i4][0];
      paramArrayOfFloat[(this.n1 - m)][i4][1] = (-paramArrayOfFloat[m][i4][1]);
    }
    paramArrayOfFloat[0][0][this.n3] = paramArrayOfFloat[0][0][1];
    paramArrayOfFloat[0][0][1] = 0.0F;
    paramArrayOfFloat[0][i4][this.n3] = paramArrayOfFloat[0][i4][1];
    paramArrayOfFloat[0][i4][1] = 0.0F;
    paramArrayOfFloat[i5][0][this.n3] = paramArrayOfFloat[i5][0][1];
    paramArrayOfFloat[i5][0][1] = 0.0F;
    paramArrayOfFloat[i5][i4][this.n3] = paramArrayOfFloat[i5][i4][1];
    paramArrayOfFloat[i5][i4][1] = 0.0F;
    paramArrayOfFloat[i5][0][(this.n3 + 1)] = 0.0F;
    paramArrayOfFloat[i5][i4][(this.n3 + 1)] = 0.0F;
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

  private void xdft3da_sub1(int paramInt1, int paramInt2, float[] paramArrayOfFloat, boolean paramBoolean)
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
            this.fftn3.complexForward(paramArrayOfFloat, m + j * this.rowStride);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realInverse(paramArrayOfFloat, m + j * this.rowStride, paramBoolean);
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
              this.t[i1] = paramArrayOfFloat[n];
              this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
              this.t[i2] = paramArrayOfFloat[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
              this.t[i3] = paramArrayOfFloat[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
              this.t[i4] = paramArrayOfFloat[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
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
              paramArrayOfFloat[n] = this.t[i1];
              paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfFloat[(n + 2)] = this.t[i2];
              paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfFloat[(n + 4)] = this.t[i3];
              paramArrayOfFloat[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfFloat[(n + 6)] = this.t[i4];
              paramArrayOfFloat[(n + 7)] = this.t[(i4 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
            this.t[i2] = paramArrayOfFloat[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(n + 2)] = this.t[i2];
            paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      m = i * this.sliceStride;
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfFloat, m + j * this.rowStride, paramBoolean);
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
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
            this.t[i2] = paramArrayOfFloat[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
            this.t[i3] = paramArrayOfFloat[(n + 4)];
            this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
            this.t[i4] = paramArrayOfFloat[(n + 6)];
            this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
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
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(n + 2)] = this.t[i2];
            paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
            paramArrayOfFloat[(n + 4)] = this.t[i3];
            paramArrayOfFloat[(n + 5)] = this.t[(i3 + 1)];
            paramArrayOfFloat[(n + 6)] = this.t[i4];
            paramArrayOfFloat[(n + 7)] = this.t[(i4 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          this.t[i1] = paramArrayOfFloat[n];
          this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
          this.t[i2] = paramArrayOfFloat[(n + 2)];
          this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          paramArrayOfFloat[n] = this.t[i1];
          paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
          paramArrayOfFloat[(n + 2)] = this.t[i2];
          paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          this.t[i1] = paramArrayOfFloat[n];
          this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          paramArrayOfFloat[n] = this.t[i1];
          paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
        }
      }
      if (paramInt1 != 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfFloat, m + j * this.rowStride);
    }
  }

  private void xdft3da_sub2(int paramInt1, int paramInt2, float[] paramArrayOfFloat, boolean paramBoolean)
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
            this.fftn3.complexForward(paramArrayOfFloat, m + j * this.rowStride);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfFloat, m + j * this.rowStride);
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
              this.t[i1] = paramArrayOfFloat[n];
              this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
              this.t[i2] = paramArrayOfFloat[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
              this.t[i3] = paramArrayOfFloat[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
              this.t[i4] = paramArrayOfFloat[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
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
              paramArrayOfFloat[n] = this.t[i1];
              paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfFloat[(n + 2)] = this.t[i2];
              paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfFloat[(n + 4)] = this.t[i3];
              paramArrayOfFloat[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfFloat[(n + 6)] = this.t[i4];
              paramArrayOfFloat[(n + 7)] = this.t[(i4 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
            this.t[i2] = paramArrayOfFloat[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            i2 = 2 * this.n2 + 2 * j;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(n + 2)] = this.t[i2];
            paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            i1 = 2 * j;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      m = i * this.sliceStride;
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfFloat, m + j * this.rowStride, paramBoolean);
      for (j = 0; j < this.n2; j++)
        this.fftn3.realInverse2(paramArrayOfFloat, m + j * this.rowStride, paramBoolean);
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
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
            this.t[i2] = paramArrayOfFloat[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
            this.t[i3] = paramArrayOfFloat[(n + 4)];
            this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
            this.t[i4] = paramArrayOfFloat[(n + 6)];
            this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
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
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(n + 2)] = this.t[i2];
            paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
            paramArrayOfFloat[(n + 4)] = this.t[i3];
            paramArrayOfFloat[(n + 5)] = this.t[(i3 + 1)];
            paramArrayOfFloat[(n + 6)] = this.t[i4];
            paramArrayOfFloat[(n + 7)] = this.t[(i4 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          this.t[i1] = paramArrayOfFloat[n];
          this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
          this.t[i2] = paramArrayOfFloat[(n + 2)];
          this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          i2 = 2 * this.n2 + 2 * j;
          paramArrayOfFloat[n] = this.t[i1];
          paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
          paramArrayOfFloat[(n + 2)] = this.t[i2];
          paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          this.t[i1] = paramArrayOfFloat[n];
          this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          i1 = 2 * j;
          paramArrayOfFloat[n] = this.t[i1];
          paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
        }
      }
    }
  }

  private void xdft3da_sub1(int paramInt1, int paramInt2, float[][][] paramArrayOfFloat, boolean paramBoolean)
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
            this.fftn3.complexForward(paramArrayOfFloat[i][j]);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realInverse(paramArrayOfFloat[i][j], 0, paramBoolean);
        if (this.n3 > 4)
          for (k = 0; k < this.n3; k += 8)
          {
            for (j = 0; j < this.n2; j++)
            {
              m = 2 * j;
              n = 2 * this.n2 + 2 * j;
              i1 = n + 2 * this.n2;
              i2 = i1 + 2 * this.n2;
              this.t[m] = paramArrayOfFloat[i][j][k];
              this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
              this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
              this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
              this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
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
              paramArrayOfFloat[i][j][k] = this.t[m];
              paramArrayOfFloat[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfFloat[i][j][(k + 2)] = this.t[n];
              paramArrayOfFloat[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfFloat[i][j][(k + 4)] = this.t[i1];
              paramArrayOfFloat[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfFloat[i][j][(k + 6)] = this.t[i2];
              paramArrayOfFloat[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
            this.t[n] = paramArrayOfFloat[i][j][2];
            this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
            paramArrayOfFloat[i][j][2] = this.t[n];
            paramArrayOfFloat[i][j][3] = this.t[(n + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfFloat[i][j], paramBoolean);
      if (this.n3 > 4)
        for (k = 0; k < this.n3; k += 8)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            i1 = n + 2 * this.n2;
            i2 = i1 + 2 * this.n2;
            this.t[m] = paramArrayOfFloat[i][j][k];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
            this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
            this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
            this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
            this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
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
            paramArrayOfFloat[i][j][k] = this.t[m];
            paramArrayOfFloat[i][j][(k + 1)] = this.t[(m + 1)];
            paramArrayOfFloat[i][j][(k + 2)] = this.t[n];
            paramArrayOfFloat[i][j][(k + 3)] = this.t[(n + 1)];
            paramArrayOfFloat[i][j][(k + 4)] = this.t[i1];
            paramArrayOfFloat[i][j][(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfFloat[i][j][(k + 6)] = this.t[i2];
            paramArrayOfFloat[i][j][(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          this.t[m] = paramArrayOfFloat[i][j][0];
          this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
          this.t[n] = paramArrayOfFloat[i][j][2];
          this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          paramArrayOfFloat[i][j][0] = this.t[m];
          paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
          paramArrayOfFloat[i][j][2] = this.t[n];
          paramArrayOfFloat[i][j][3] = this.t[(n + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          this.t[m] = paramArrayOfFloat[i][j][0];
          this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          paramArrayOfFloat[i][j][0] = this.t[m];
          paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
        }
      }
      if (paramInt1 != 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfFloat[i][j], 0);
    }
  }

  private void xdft3da_sub2(int paramInt1, int paramInt2, float[][][] paramArrayOfFloat, boolean paramBoolean)
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
            this.fftn3.complexForward(paramArrayOfFloat[i][j]);
        for (j = 0; j < this.n2; j++)
          this.fftn3.realForward(paramArrayOfFloat[i][j]);
        if (this.n3 > 4)
          for (k = 0; k < this.n3; k += 8)
          {
            for (j = 0; j < this.n2; j++)
            {
              m = 2 * j;
              n = 2 * this.n2 + 2 * j;
              i1 = n + 2 * this.n2;
              i2 = i1 + 2 * this.n2;
              this.t[m] = paramArrayOfFloat[i][j][k];
              this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
              this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
              this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
              this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
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
              paramArrayOfFloat[i][j][k] = this.t[m];
              paramArrayOfFloat[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfFloat[i][j][(k + 2)] = this.t[n];
              paramArrayOfFloat[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfFloat[i][j][(k + 4)] = this.t[i1];
              paramArrayOfFloat[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfFloat[i][j][(k + 6)] = this.t[i2];
              paramArrayOfFloat[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
        if (this.n3 == 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
            this.t[n] = paramArrayOfFloat[i][j][2];
            this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
          }
          this.fftn2.complexForward(this.t, 0);
          this.fftn2.complexForward(this.t, 2 * this.n2);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
            paramArrayOfFloat[i][j][2] = this.t[n];
            paramArrayOfFloat[i][j][3] = this.t[(n + 1)];
          }
        }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
          }
          this.fftn2.complexForward(this.t, 0);
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      if (paramInt1 == 0)
        for (j = 0; j < this.n2; j++)
          this.fftn3.complexInverse(paramArrayOfFloat[i][j], paramBoolean);
      for (j = 0; j < this.n2; j++)
        this.fftn3.realInverse2(paramArrayOfFloat[i][j], 0, paramBoolean);
      if (this.n3 > 4)
        for (k = 0; k < this.n3; k += 8)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = 2 * j;
            n = 2 * this.n2 + 2 * j;
            i1 = n + 2 * this.n2;
            i2 = i1 + 2 * this.n2;
            this.t[m] = paramArrayOfFloat[i][j][k];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
            this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
            this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
            this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
            this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
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
            paramArrayOfFloat[i][j][k] = this.t[m];
            paramArrayOfFloat[i][j][(k + 1)] = this.t[(m + 1)];
            paramArrayOfFloat[i][j][(k + 2)] = this.t[n];
            paramArrayOfFloat[i][j][(k + 3)] = this.t[(n + 1)];
            paramArrayOfFloat[i][j][(k + 4)] = this.t[i1];
            paramArrayOfFloat[i][j][(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfFloat[i][j][(k + 6)] = this.t[i2];
            paramArrayOfFloat[i][j][(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n3 == 4)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          this.t[m] = paramArrayOfFloat[i][j][0];
          this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
          this.t[n] = paramArrayOfFloat[i][j][2];
          this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        this.fftn2.complexInverse(this.t, 2 * this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          n = 2 * this.n2 + 2 * j;
          paramArrayOfFloat[i][j][0] = this.t[m];
          paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
          paramArrayOfFloat[i][j][2] = this.t[n];
          paramArrayOfFloat[i][j][3] = this.t[(n + 1)];
        }
      }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          this.t[m] = paramArrayOfFloat[i][j][0];
          this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
        }
        this.fftn2.complexInverse(this.t, 0, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          m = 2 * j;
          paramArrayOfFloat[i][j][0] = this.t[m];
          paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
        }
      }
    }
  }

  private void cdft3db_sub(int paramInt, float[] paramArrayOfFloat, boolean paramBoolean)
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
              this.t[i1] = paramArrayOfFloat[n];
              this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
              this.t[i2] = paramArrayOfFloat[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
              this.t[i3] = paramArrayOfFloat[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
              this.t[i4] = paramArrayOfFloat[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
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
              paramArrayOfFloat[n] = this.t[i1];
              paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfFloat[(n + 2)] = this.t[i2];
              paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfFloat[(n + 4)] = this.t[i3];
              paramArrayOfFloat[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfFloat[(n + 6)] = this.t[i4];
              paramArrayOfFloat[(n + 7)] = this.t[(i4 + 1)];
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
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
            this.t[i2] = paramArrayOfFloat[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
          }
          this.fftn1.complexForward(this.t, 0);
          this.fftn1.complexForward(this.t, 2 * this.n1);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            i2 = 2 * this.n1 + 2 * i;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(n + 2)] = this.t[i2];
            paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
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
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
          }
          this.fftn1.complexForward(this.t, 0);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
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
              this.t[i1] = paramArrayOfFloat[n];
              this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
              this.t[i2] = paramArrayOfFloat[(n + 2)];
              this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
              this.t[i3] = paramArrayOfFloat[(n + 4)];
              this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
              this.t[i4] = paramArrayOfFloat[(n + 6)];
              this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
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
              paramArrayOfFloat[n] = this.t[i1];
              paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
              paramArrayOfFloat[(n + 2)] = this.t[i2];
              paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
              paramArrayOfFloat[(n + 4)] = this.t[i3];
              paramArrayOfFloat[(n + 5)] = this.t[(i3 + 1)];
              paramArrayOfFloat[(n + 6)] = this.t[i4];
              paramArrayOfFloat[(n + 7)] = this.t[(i4 + 1)];
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
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
            this.t[i2] = paramArrayOfFloat[(n + 2)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            i2 = 2 * this.n1 + 2 * i;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(n + 2)] = this.t[i2];
            paramArrayOfFloat[(n + 3)] = this.t[(i2 + 1)];
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
            this.t[i1] = paramArrayOfFloat[n];
            this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            i1 = 2 * i;
            paramArrayOfFloat[n] = this.t[i1];
            paramArrayOfFloat[(n + 1)] = this.t[(i1 + 1)];
          }
        }
    }
  }

  private void cdft3db_sub(int paramInt, float[][][] paramArrayOfFloat, boolean paramBoolean)
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
              this.t[m] = paramArrayOfFloat[i][j][k];
              this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
              this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
              this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
              this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
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
              paramArrayOfFloat[i][j][k] = this.t[m];
              paramArrayOfFloat[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfFloat[i][j][(k + 2)] = this.t[n];
              paramArrayOfFloat[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfFloat[i][j][(k + 4)] = this.t[i1];
              paramArrayOfFloat[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfFloat[i][j][(k + 6)] = this.t[i2];
              paramArrayOfFloat[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
      if (this.n3 == 4)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
            this.t[n] = paramArrayOfFloat[i][j][2];
            this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
          }
          this.fftn1.complexForward(this.t, 0);
          this.fftn1.complexForward(this.t, 2 * this.n1);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
            paramArrayOfFloat[i][j][2] = this.t[n];
            paramArrayOfFloat[i][j][3] = this.t[(n + 1)];
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
          }
          this.fftn1.complexForward(this.t, 0);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
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
              this.t[m] = paramArrayOfFloat[i][j][k];
              this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
              this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
              this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
              this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
              this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
              this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
              this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
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
              paramArrayOfFloat[i][j][k] = this.t[m];
              paramArrayOfFloat[i][j][(k + 1)] = this.t[(m + 1)];
              paramArrayOfFloat[i][j][(k + 2)] = this.t[n];
              paramArrayOfFloat[i][j][(k + 3)] = this.t[(n + 1)];
              paramArrayOfFloat[i][j][(k + 4)] = this.t[i1];
              paramArrayOfFloat[i][j][(k + 5)] = this.t[(i1 + 1)];
              paramArrayOfFloat[i][j][(k + 6)] = this.t[i2];
              paramArrayOfFloat[i][j][(k + 7)] = this.t[(i2 + 1)];
            }
          }
      if (this.n3 == 4)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
            this.t[n] = paramArrayOfFloat[i][j][2];
            this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            n = 2 * this.n1 + 2 * i;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
            paramArrayOfFloat[i][j][2] = this.t[n];
            paramArrayOfFloat[i][j][3] = this.t[(n + 1)];
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            this.t[m] = paramArrayOfFloat[i][j][0];
            this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
          }
          this.fftn1.complexInverse(this.t, 0, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            m = 2 * i;
            paramArrayOfFloat[i][j][0] = this.t[m];
            paramArrayOfFloat[i][j][1] = this.t[(m + 1)];
          }
        }
    }
  }

  private void xdft3da_subth1(final int paramInt1, final int paramInt2, final float[] paramArrayOfFloat, final boolean paramBoolean)
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
            while (i < FloatFFT_3D.this.n1)
            {
              m = i * FloatFFT_3D.this.sliceStride;
              if (paramInt1 == 0)
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  FloatFFT_3D.this.fftn3.complexForward(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.realInverse(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride, paramBoolean);
              if (FloatFFT_3D.this.n3 > 4)
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    n = m + j * FloatFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * FloatFFT_3D.this.n2;
                    i4 = i3 + 2 * FloatFFT_3D.this.n2;
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                    FloatFFT_3D.this.t[i3] = paramArrayOfFloat[(n + 4)];
                    FloatFFT_3D.this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
                    FloatFFT_3D.this.t[i4] = paramArrayOfFloat[(n + 6)];
                    FloatFFT_3D.this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
                  }
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2);
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    n = m + j * FloatFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * FloatFFT_3D.this.n2;
                    i4 = i3 + 2 * FloatFFT_3D.this.n2;
                    paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfFloat[(n + 4)] = FloatFFT_3D.this.t[i3];
                    paramArrayOfFloat[(n + 5)] = FloatFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfFloat[(n + 6)] = FloatFFT_3D.this.t[i4];
                    paramArrayOfFloat[(n + 7)] = FloatFFT_3D.this.t[(i4 + 1)];
                  }
                }
              if (FloatFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                }
              }
              if (FloatFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < FloatFFT_3D.this.n1)
          {
            m = i * FloatFFT_3D.this.sliceStride;
            if (paramInt1 == 0)
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.complexInverse(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride, paramBoolean);
            if (FloatFFT_3D.this.n3 > 4)
              for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * FloatFFT_3D.this.n2;
                  i4 = i3 + 2 * FloatFFT_3D.this.n2;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                  FloatFFT_3D.this.t[i3] = paramArrayOfFloat[(n + 4)];
                  FloatFFT_3D.this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
                  FloatFFT_3D.this.t[i4] = paramArrayOfFloat[(n + 6)];
                  FloatFFT_3D.this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
                }
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * FloatFFT_3D.this.n2;
                  i4 = i3 + 2 * FloatFFT_3D.this.n2;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                  paramArrayOfFloat[(n + 4)] = FloatFFT_3D.this.t[i3];
                  paramArrayOfFloat[(n + 5)] = FloatFFT_3D.this.t[(i3 + 1)];
                  paramArrayOfFloat[(n + 6)] = FloatFFT_3D.this.t[i4];
                  paramArrayOfFloat[(n + 7)] = FloatFFT_3D.this.t[(i4 + 1)];
                }
              }
            if (FloatFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
              }
            }
            if (paramInt1 != 0)
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.realForward(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride);
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

  private void xdft3da_subth2(final int paramInt1, final int paramInt2, final float[] paramArrayOfFloat, final boolean paramBoolean)
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
            while (i < FloatFFT_3D.this.n1)
            {
              m = i * FloatFFT_3D.this.sliceStride;
              if (paramInt1 == 0)
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  FloatFFT_3D.this.fftn3.complexForward(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.realForward(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride);
              if (FloatFFT_3D.this.n3 > 4)
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    n = m + j * FloatFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * FloatFFT_3D.this.n2;
                    i4 = i3 + 2 * FloatFFT_3D.this.n2;
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                    FloatFFT_3D.this.t[i3] = paramArrayOfFloat[(n + 4)];
                    FloatFFT_3D.this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
                    FloatFFT_3D.this.t[i4] = paramArrayOfFloat[(n + 6)];
                    FloatFFT_3D.this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
                  }
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2);
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    n = m + j * FloatFFT_3D.this.rowStride + k;
                    i1 = i1 + 2 * j;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i3 = i2 + 2 * FloatFFT_3D.this.n2;
                    i4 = i3 + 2 * FloatFFT_3D.this.n2;
                    paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfFloat[(n + 4)] = FloatFFT_3D.this.t[i3];
                    paramArrayOfFloat[(n + 5)] = FloatFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfFloat[(n + 6)] = FloatFFT_3D.this.t[i4];
                    paramArrayOfFloat[(n + 7)] = FloatFFT_3D.this.t[(i4 + 1)];
                  }
                }
              if (FloatFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                }
              }
              if (FloatFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride;
                  i1 = i1 + 2 * j;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < FloatFFT_3D.this.n1)
          {
            m = i * FloatFFT_3D.this.sliceStride;
            if (paramInt1 == 0)
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.complexInverse(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride, paramBoolean);
            for (j = 0; j < FloatFFT_3D.this.n2; j++)
              FloatFFT_3D.this.fftn3.realInverse2(paramArrayOfFloat, m + j * FloatFFT_3D.this.rowStride, paramBoolean);
            if (FloatFFT_3D.this.n3 > 4)
              for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * FloatFFT_3D.this.n2;
                  i4 = i3 + 2 * FloatFFT_3D.this.n2;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                  FloatFFT_3D.this.t[i3] = paramArrayOfFloat[(n + 4)];
                  FloatFFT_3D.this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
                  FloatFFT_3D.this.t[i4] = paramArrayOfFloat[(n + 6)];
                  FloatFFT_3D.this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
                }
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  n = m + j * FloatFFT_3D.this.rowStride + k;
                  i1 = i1 + 2 * j;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i3 = i2 + 2 * FloatFFT_3D.this.n2;
                  i4 = i3 + 2 * FloatFFT_3D.this.n2;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                  paramArrayOfFloat[(n + 4)] = FloatFFT_3D.this.t[i3];
                  paramArrayOfFloat[(n + 5)] = FloatFFT_3D.this.t[(i3 + 1)];
                  paramArrayOfFloat[(n + 6)] = FloatFFT_3D.this.t[i4];
                  paramArrayOfFloat[(n + 7)] = FloatFFT_3D.this.t[(i4 + 1)];
                }
              }
            if (FloatFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                i2 = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                n = m + j * FloatFFT_3D.this.rowStride;
                i1 = i1 + 2 * j;
                paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
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

  private void xdft3da_subth1(final int paramInt1, final int paramInt2, final float[][][] paramArrayOfFloat, final boolean paramBoolean)
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
            while (i < FloatFFT_3D.this.n1)
            {
              if (paramInt1 == 0)
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  FloatFFT_3D.this.fftn3.complexForward(paramArrayOfFloat[i][j]);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.realInverse(paramArrayOfFloat[i][j], 0, paramBoolean);
              if (FloatFFT_3D.this.n3 > 4)
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * FloatFFT_3D.this.n2;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2;
                    FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][k];
                    FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
                    FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
                    FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
                  }
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2);
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * FloatFFT_3D.this.n2;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2;
                    paramArrayOfFloat[i][j][k] = FloatFFT_3D.this.t[m];
                    paramArrayOfFloat[i][j][(k + 1)] = FloatFFT_3D.this.t[(m + 1)];
                    paramArrayOfFloat[i][j][(k + 2)] = FloatFFT_3D.this.t[n];
                    paramArrayOfFloat[i][j][(k + 3)] = FloatFFT_3D.this.t[(n + 1)];
                    paramArrayOfFloat[i][j][(k + 4)] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[i][j][(k + 5)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[i][j][(k + 6)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[i][j][(k + 7)] = FloatFFT_3D.this.t[(i2 + 1)];
                  }
                }
              if (FloatFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                  FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][2];
                  FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                  paramArrayOfFloat[i][j][2] = FloatFFT_3D.this.t[n];
                  paramArrayOfFloat[i][j][3] = FloatFFT_3D.this.t[(n + 1)];
                }
              }
              if (FloatFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < FloatFFT_3D.this.n1)
          {
            if (paramInt1 == 0)
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.complexInverse(paramArrayOfFloat[i][j], paramBoolean);
            if (FloatFFT_3D.this.n3 > 4)
              for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * FloatFFT_3D.this.n2;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][k];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
                  FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
                  FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
                }
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * FloatFFT_3D.this.n2;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2;
                  paramArrayOfFloat[i][j][k] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][(k + 1)] = FloatFFT_3D.this.t[(m + 1)];
                  paramArrayOfFloat[i][j][(k + 2)] = FloatFFT_3D.this.t[n];
                  paramArrayOfFloat[i][j][(k + 3)] = FloatFFT_3D.this.t[(n + 1)];
                  paramArrayOfFloat[i][j][(k + 4)] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[i][j][(k + 5)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[i][j][(k + 6)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[i][j][(k + 7)] = FloatFFT_3D.this.t[(i2 + 1)];
                }
              }
            if (FloatFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][2];
                FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                paramArrayOfFloat[i][j][2] = FloatFFT_3D.this.t[n];
                paramArrayOfFloat[i][j][3] = FloatFFT_3D.this.t[(n + 1)];
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
              }
            }
            if (paramInt1 != 0)
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.realForward(paramArrayOfFloat[i][j]);
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

  private void xdft3da_subth2(final int paramInt1, final int paramInt2, final float[][][] paramArrayOfFloat, final boolean paramBoolean)
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
            while (i < FloatFFT_3D.this.n1)
            {
              if (paramInt1 == 0)
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  FloatFFT_3D.this.fftn3.complexForward(paramArrayOfFloat[i][j]);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.realForward(paramArrayOfFloat[i][j]);
              if (FloatFFT_3D.this.n3 > 4)
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * FloatFFT_3D.this.n2;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2;
                    FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][k];
                    FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
                    FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
                    FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
                  }
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2);
                  FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2);
                  for (j = 0; j < FloatFFT_3D.this.n2; j++)
                  {
                    m = i1 + 2 * j;
                    n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                    i1 = n + 2 * FloatFFT_3D.this.n2;
                    i2 = i1 + 2 * FloatFFT_3D.this.n2;
                    paramArrayOfFloat[i][j][k] = FloatFFT_3D.this.t[m];
                    paramArrayOfFloat[i][j][(k + 1)] = FloatFFT_3D.this.t[(m + 1)];
                    paramArrayOfFloat[i][j][(k + 2)] = FloatFFT_3D.this.t[n];
                    paramArrayOfFloat[i][j][(k + 3)] = FloatFFT_3D.this.t[(n + 1)];
                    paramArrayOfFloat[i][j][(k + 4)] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[i][j][(k + 5)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[i][j][(k + 6)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[i][j][(k + 7)] = FloatFFT_3D.this.t[(i2 + 1)];
                  }
                }
              if (FloatFFT_3D.this.n3 == 4)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                  FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][2];
                  FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                  paramArrayOfFloat[i][j][2] = FloatFFT_3D.this.t[n];
                  paramArrayOfFloat[i][j][3] = FloatFFT_3D.this.t[(n + 1)];
                }
              }
              if (FloatFFT_3D.this.n3 == 2)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                }
                FloatFFT_3D.this.fftn2.complexForward(FloatFFT_3D.this.t, i1);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                }
              }
              i += m;
            }
          }
          int i = n;
          while (i < FloatFFT_3D.this.n1)
          {
            if (paramInt1 == 0)
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
                FloatFFT_3D.this.fftn3.complexInverse(paramArrayOfFloat[i][j], paramBoolean);
            for (j = 0; j < FloatFFT_3D.this.n2; j++)
              FloatFFT_3D.this.fftn3.realInverse2(paramArrayOfFloat[i][j], 0, paramBoolean);
            if (FloatFFT_3D.this.n3 > 4)
              for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
              {
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * FloatFFT_3D.this.n2;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][k];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
                  FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
                  FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
                }
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n2, paramBoolean);
                FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n2, paramBoolean);
                for (j = 0; j < FloatFFT_3D.this.n2; j++)
                {
                  m = i1 + 2 * j;
                  n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                  i1 = n + 2 * FloatFFT_3D.this.n2;
                  i2 = i1 + 2 * FloatFFT_3D.this.n2;
                  paramArrayOfFloat[i][j][k] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][(k + 1)] = FloatFFT_3D.this.t[(m + 1)];
                  paramArrayOfFloat[i][j][(k + 2)] = FloatFFT_3D.this.t[n];
                  paramArrayOfFloat[i][j][(k + 3)] = FloatFFT_3D.this.t[(n + 1)];
                  paramArrayOfFloat[i][j][(k + 4)] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[i][j][(k + 5)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[i][j][(k + 6)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[i][j][(k + 7)] = FloatFFT_3D.this.t[(i2 + 1)];
                }
              }
            if (FloatFFT_3D.this.n3 == 4)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][2];
                FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n2, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                n = i1 + 2 * FloatFFT_3D.this.n2 + 2 * j;
                paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                paramArrayOfFloat[i][j][2] = FloatFFT_3D.this.t[n];
                paramArrayOfFloat[i][j][3] = FloatFFT_3D.this.t[(n + 1)];
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
              }
              FloatFFT_3D.this.fftn2.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
              for (j = 0; j < FloatFFT_3D.this.n2; j++)
              {
                m = i1 + 2 * j;
                paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
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

  private void cdft3db_subth(final int paramInt, final float[] paramArrayOfFloat, final boolean paramBoolean)
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
            if (FloatFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                m = j * FloatFFT_3D.this.rowStride;
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    n = i * FloatFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * FloatFFT_3D.this.n1;
                    i4 = i3 + 2 * FloatFFT_3D.this.n1;
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                    FloatFFT_3D.this.t[i3] = paramArrayOfFloat[(n + 4)];
                    FloatFFT_3D.this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
                    FloatFFT_3D.this.t[i4] = paramArrayOfFloat[(n + 6)];
                    FloatFFT_3D.this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
                  }
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1);
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1);
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n1);
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n1);
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    n = i * FloatFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * FloatFFT_3D.this.n1;
                    i4 = i3 + 2 * FloatFFT_3D.this.n1;
                    paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfFloat[(n + 4)] = FloatFFT_3D.this.t[i3];
                    paramArrayOfFloat[(n + 5)] = FloatFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfFloat[(n + 6)] = FloatFFT_3D.this.t[i4];
                    paramArrayOfFloat[(n + 7)] = FloatFFT_3D.this.t[(i4 + 1)];
                  }
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                m = j * FloatFFT_3D.this.rowStride;
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                }
                FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1);
                FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                m = j * FloatFFT_3D.this.rowStride;
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                }
                FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                }
                j += m;
              }
            }
          }
          else
          {
            if (FloatFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                m = j * FloatFFT_3D.this.rowStride;
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    n = i * FloatFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * FloatFFT_3D.this.n1;
                    i4 = i3 + 2 * FloatFFT_3D.this.n1;
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                    FloatFFT_3D.this.t[i3] = paramArrayOfFloat[(n + 4)];
                    FloatFFT_3D.this.t[(i3 + 1)] = paramArrayOfFloat[(n + 5)];
                    FloatFFT_3D.this.t[i4] = paramArrayOfFloat[(n + 6)];
                    FloatFFT_3D.this.t[(i4 + 1)] = paramArrayOfFloat[(n + 7)];
                  }
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1, paramBoolean);
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n1, paramBoolean);
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n1, paramBoolean);
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    n = i * FloatFFT_3D.this.sliceStride + m + k;
                    i1 = i1 + 2 * i;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i3 = i2 + 2 * FloatFFT_3D.this.n1;
                    i4 = i3 + 2 * FloatFFT_3D.this.n1;
                    paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                    paramArrayOfFloat[(n + 4)] = FloatFFT_3D.this.t[i3];
                    paramArrayOfFloat[(n + 5)] = FloatFFT_3D.this.t[(i3 + 1)];
                    paramArrayOfFloat[(n + 6)] = FloatFFT_3D.this.t[i4];
                    paramArrayOfFloat[(n + 7)] = FloatFFT_3D.this.t[(i4 + 1)];
                  }
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                m = j * FloatFFT_3D.this.rowStride;
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                  FloatFFT_3D.this.t[i2] = paramArrayOfFloat[(n + 2)];
                  FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[(n + 3)];
                }
                FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1, paramBoolean);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  i2 = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(n + 2)] = FloatFFT_3D.this.t[i2];
                  paramArrayOfFloat[(n + 3)] = FloatFFT_3D.this.t[(i2 + 1)];
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                m = j * FloatFFT_3D.this.rowStride;
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  FloatFFT_3D.this.t[i1] = paramArrayOfFloat[n];
                  FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[(n + 1)];
                }
                FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  n = i * FloatFFT_3D.this.sliceStride + m;
                  i1 = i1 + 2 * i;
                  paramArrayOfFloat[n] = FloatFFT_3D.this.t[i1];
                  paramArrayOfFloat[(n + 1)] = FloatFFT_3D.this.t[(i1 + 1)];
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

  private void cdft3db_subth(final int paramInt, final float[][][] paramArrayOfFloat, final boolean paramBoolean)
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
            if (FloatFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * FloatFFT_3D.this.n1;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1;
                    FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][k];
                    FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
                    FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
                    FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
                  }
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1);
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1);
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n1);
                  FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n1);
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * FloatFFT_3D.this.n1;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1;
                    paramArrayOfFloat[i][j][k] = FloatFFT_3D.this.t[m];
                    paramArrayOfFloat[i][j][(k + 1)] = FloatFFT_3D.this.t[(m + 1)];
                    paramArrayOfFloat[i][j][(k + 2)] = FloatFFT_3D.this.t[n];
                    paramArrayOfFloat[i][j][(k + 3)] = FloatFFT_3D.this.t[(n + 1)];
                    paramArrayOfFloat[i][j][(k + 4)] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[i][j][(k + 5)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[i][j][(k + 6)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[i][j][(k + 7)] = FloatFFT_3D.this.t[(i2 + 1)];
                  }
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                  FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][2];
                  FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
                }
                FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1);
                FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                  paramArrayOfFloat[i][j][2] = FloatFFT_3D.this.t[n];
                  paramArrayOfFloat[i][j][3] = FloatFFT_3D.this.t[(n + 1)];
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                }
                FloatFFT_3D.this.fftn1.complexForward(FloatFFT_3D.this.t, i1);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                }
                j += m;
              }
            }
          }
          else
          {
            if (FloatFFT_3D.this.n3 > 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                for (k = 0; k < FloatFFT_3D.this.n3; k += 8)
                {
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * FloatFFT_3D.this.n1;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1;
                    FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][k];
                    FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][(k + 1)];
                    FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][(k + 2)];
                    FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][(k + 3)];
                    FloatFFT_3D.this.t[i1] = paramArrayOfFloat[i][j][(k + 4)];
                    FloatFFT_3D.this.t[(i1 + 1)] = paramArrayOfFloat[i][j][(k + 5)];
                    FloatFFT_3D.this.t[i2] = paramArrayOfFloat[i][j][(k + 6)];
                    FloatFFT_3D.this.t[(i2 + 1)] = paramArrayOfFloat[i][j][(k + 7)];
                  }
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1, paramBoolean);
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 4 * FloatFFT_3D.this.n1, paramBoolean);
                  FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 6 * FloatFFT_3D.this.n1, paramBoolean);
                  for (i = 0; i < FloatFFT_3D.this.n1; i++)
                  {
                    m = i1 + 2 * i;
                    n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                    i1 = n + 2 * FloatFFT_3D.this.n1;
                    i2 = i1 + 2 * FloatFFT_3D.this.n1;
                    paramArrayOfFloat[i][j][k] = FloatFFT_3D.this.t[m];
                    paramArrayOfFloat[i][j][(k + 1)] = FloatFFT_3D.this.t[(m + 1)];
                    paramArrayOfFloat[i][j][(k + 2)] = FloatFFT_3D.this.t[n];
                    paramArrayOfFloat[i][j][(k + 3)] = FloatFFT_3D.this.t[(n + 1)];
                    paramArrayOfFloat[i][j][(k + 4)] = FloatFFT_3D.this.t[i1];
                    paramArrayOfFloat[i][j][(k + 5)] = FloatFFT_3D.this.t[(i1 + 1)];
                    paramArrayOfFloat[i][j][(k + 6)] = FloatFFT_3D.this.t[i2];
                    paramArrayOfFloat[i][j][(k + 7)] = FloatFFT_3D.this.t[(i2 + 1)];
                  }
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 4)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                  FloatFFT_3D.this.t[n] = paramArrayOfFloat[i][j][2];
                  FloatFFT_3D.this.t[(n + 1)] = paramArrayOfFloat[i][j][3];
                }
                FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1 + 2 * FloatFFT_3D.this.n1, paramBoolean);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  n = i1 + 2 * FloatFFT_3D.this.n1 + 2 * i;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
                  paramArrayOfFloat[i][j][2] = FloatFFT_3D.this.t[n];
                  paramArrayOfFloat[i][j][3] = FloatFFT_3D.this.t[(n + 1)];
                }
                j += m;
              }
            }
            if (FloatFFT_3D.this.n3 == 2)
            {
              j = n;
              while (j < FloatFFT_3D.this.n2)
              {
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  FloatFFT_3D.this.t[m] = paramArrayOfFloat[i][j][0];
                  FloatFFT_3D.this.t[(m + 1)] = paramArrayOfFloat[i][j][1];
                }
                FloatFFT_3D.this.fftn1.complexInverse(FloatFFT_3D.this.t, i1, paramBoolean);
                for (i = 0; i < FloatFFT_3D.this.n1; i++)
                {
                  m = i1 + 2 * i;
                  paramArrayOfFloat[i][j][0] = FloatFFT_3D.this.t[m];
                  paramArrayOfFloat[i][j][1] = FloatFFT_3D.this.t[(m + 1)];
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

  private void rdft3d_sub(int paramInt, float[] paramArrayOfFloat)
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
      float f;
      for (k = 1; k < i; k++)
      {
        m = this.n1 - k;
        i2 = k * this.sliceStride;
        i3 = m * this.sliceStride;
        i4 = k * this.sliceStride + j * this.rowStride;
        i5 = m * this.sliceStride + j * this.rowStride;
        f = paramArrayOfFloat[i2] - paramArrayOfFloat[i3];
        paramArrayOfFloat[i2] += paramArrayOfFloat[i3];
        paramArrayOfFloat[i3] = f;
        f = paramArrayOfFloat[(i3 + 1)] - paramArrayOfFloat[(i2 + 1)];
        paramArrayOfFloat[(i2 + 1)] += paramArrayOfFloat[(i3 + 1)];
        paramArrayOfFloat[(i3 + 1)] = f;
        f = paramArrayOfFloat[i4] - paramArrayOfFloat[i5];
        paramArrayOfFloat[i4] += paramArrayOfFloat[i5];
        paramArrayOfFloat[i5] = f;
        f = paramArrayOfFloat[(i5 + 1)] - paramArrayOfFloat[(i4 + 1)];
        paramArrayOfFloat[(i4 + 1)] += paramArrayOfFloat[(i5 + 1)];
        paramArrayOfFloat[(i5 + 1)] = f;
        for (n = 1; n < j; n++)
        {
          i1 = this.n2 - n;
          i2 = k * this.sliceStride + n * this.rowStride;
          i3 = m * this.sliceStride + i1 * this.rowStride;
          f = paramArrayOfFloat[i2] - paramArrayOfFloat[i3];
          paramArrayOfFloat[i2] += paramArrayOfFloat[i3];
          paramArrayOfFloat[i3] = f;
          f = paramArrayOfFloat[(i3 + 1)] - paramArrayOfFloat[(i2 + 1)];
          paramArrayOfFloat[(i2 + 1)] += paramArrayOfFloat[(i3 + 1)];
          paramArrayOfFloat[(i3 + 1)] = f;
          i4 = m * this.sliceStride + n * this.rowStride;
          i5 = k * this.sliceStride + i1 * this.rowStride;
          f = paramArrayOfFloat[i4] - paramArrayOfFloat[i5];
          paramArrayOfFloat[i4] += paramArrayOfFloat[i5];
          paramArrayOfFloat[i5] = f;
          f = paramArrayOfFloat[(i5 + 1)] - paramArrayOfFloat[(i4 + 1)];
          paramArrayOfFloat[(i4 + 1)] += paramArrayOfFloat[(i5 + 1)];
          paramArrayOfFloat[(i5 + 1)] = f;
        }
      }
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        i2 = n * this.rowStride;
        i3 = i1 * this.rowStride;
        f = paramArrayOfFloat[i2] - paramArrayOfFloat[i3];
        paramArrayOfFloat[i2] += paramArrayOfFloat[i3];
        paramArrayOfFloat[i3] = f;
        f = paramArrayOfFloat[(i3 + 1)] - paramArrayOfFloat[(i2 + 1)];
        paramArrayOfFloat[(i2 + 1)] += paramArrayOfFloat[(i3 + 1)];
        paramArrayOfFloat[(i3 + 1)] = f;
        i4 = i * this.sliceStride + n * this.rowStride;
        i5 = i * this.sliceStride + i1 * this.rowStride;
        f = paramArrayOfFloat[i4] - paramArrayOfFloat[i5];
        paramArrayOfFloat[i4] += paramArrayOfFloat[i5];
        paramArrayOfFloat[i5] = f;
        f = paramArrayOfFloat[(i5 + 1)] - paramArrayOfFloat[(i4 + 1)];
        paramArrayOfFloat[(i4 + 1)] += paramArrayOfFloat[(i5 + 1)];
        paramArrayOfFloat[(i5 + 1)] = f;
      }
    }
    for (int k = 1; k < i; k++)
    {
      m = this.n1 - k;
      i2 = m * this.sliceStride;
      i3 = k * this.sliceStride;
      paramArrayOfFloat[i2] = (0.5F * (paramArrayOfFloat[i3] - paramArrayOfFloat[i2]));
      paramArrayOfFloat[i3] -= paramArrayOfFloat[i2];
      paramArrayOfFloat[(i2 + 1)] = (0.5F * (paramArrayOfFloat[(i3 + 1)] + paramArrayOfFloat[(i2 + 1)]));
      paramArrayOfFloat[(i3 + 1)] -= paramArrayOfFloat[(i2 + 1)];
      i4 = m * this.sliceStride + j * this.rowStride;
      i5 = k * this.sliceStride + j * this.rowStride;
      paramArrayOfFloat[i4] = (0.5F * (paramArrayOfFloat[i5] - paramArrayOfFloat[i4]));
      paramArrayOfFloat[i5] -= paramArrayOfFloat[i4];
      paramArrayOfFloat[(i4 + 1)] = (0.5F * (paramArrayOfFloat[(i5 + 1)] + paramArrayOfFloat[(i4 + 1)]));
      paramArrayOfFloat[(i5 + 1)] -= paramArrayOfFloat[(i4 + 1)];
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        i2 = m * this.sliceStride + i1 * this.rowStride;
        i3 = k * this.sliceStride + n * this.rowStride;
        paramArrayOfFloat[i2] = (0.5F * (paramArrayOfFloat[i3] - paramArrayOfFloat[i2]));
        paramArrayOfFloat[i3] -= paramArrayOfFloat[i2];
        paramArrayOfFloat[(i2 + 1)] = (0.5F * (paramArrayOfFloat[(i3 + 1)] + paramArrayOfFloat[(i2 + 1)]));
        paramArrayOfFloat[(i3 + 1)] -= paramArrayOfFloat[(i2 + 1)];
        i4 = k * this.sliceStride + i1 * this.rowStride;
        i5 = m * this.sliceStride + n * this.rowStride;
        paramArrayOfFloat[i4] = (0.5F * (paramArrayOfFloat[i5] - paramArrayOfFloat[i4]));
        paramArrayOfFloat[i5] -= paramArrayOfFloat[i4];
        paramArrayOfFloat[(i4 + 1)] = (0.5F * (paramArrayOfFloat[(i5 + 1)] + paramArrayOfFloat[(i4 + 1)]));
        paramArrayOfFloat[(i5 + 1)] -= paramArrayOfFloat[(i4 + 1)];
      }
    }
    for (int n = 1; n < j; n++)
    {
      i1 = this.n2 - n;
      i2 = i1 * this.rowStride;
      i3 = n * this.rowStride;
      paramArrayOfFloat[i2] = (0.5F * (paramArrayOfFloat[i3] - paramArrayOfFloat[i2]));
      paramArrayOfFloat[i3] -= paramArrayOfFloat[i2];
      paramArrayOfFloat[(i2 + 1)] = (0.5F * (paramArrayOfFloat[(i3 + 1)] + paramArrayOfFloat[(i2 + 1)]));
      paramArrayOfFloat[(i3 + 1)] -= paramArrayOfFloat[(i2 + 1)];
      i4 = i * this.sliceStride + i1 * this.rowStride;
      i5 = i * this.sliceStride + n * this.rowStride;
      paramArrayOfFloat[i4] = (0.5F * (paramArrayOfFloat[i5] - paramArrayOfFloat[i4]));
      paramArrayOfFloat[i5] -= paramArrayOfFloat[i4];
      paramArrayOfFloat[(i4 + 1)] = (0.5F * (paramArrayOfFloat[(i5 + 1)] + paramArrayOfFloat[(i4 + 1)]));
      paramArrayOfFloat[(i5 + 1)] -= paramArrayOfFloat[(i4 + 1)];
    }
  }

  private void rdft3d_sub(int paramInt, float[][][] paramArrayOfFloat)
  {
    int i = this.n1 >> 1;
    int j = this.n2 >> 1;
    int m;
    int i1;
    if (paramInt < 0)
    {
      float f;
      for (k = 1; k < i; k++)
      {
        m = this.n1 - k;
        f = paramArrayOfFloat[k][0][0] - paramArrayOfFloat[m][0][0];
        paramArrayOfFloat[k][0][0] += paramArrayOfFloat[m][0][0];
        paramArrayOfFloat[m][0][0] = f;
        f = paramArrayOfFloat[m][0][1] - paramArrayOfFloat[k][0][1];
        paramArrayOfFloat[k][0][1] += paramArrayOfFloat[m][0][1];
        paramArrayOfFloat[m][0][1] = f;
        f = paramArrayOfFloat[k][j][0] - paramArrayOfFloat[m][j][0];
        paramArrayOfFloat[k][j][0] += paramArrayOfFloat[m][j][0];
        paramArrayOfFloat[m][j][0] = f;
        f = paramArrayOfFloat[m][j][1] - paramArrayOfFloat[k][j][1];
        paramArrayOfFloat[k][j][1] += paramArrayOfFloat[m][j][1];
        paramArrayOfFloat[m][j][1] = f;
        for (n = 1; n < j; n++)
        {
          i1 = this.n2 - n;
          f = paramArrayOfFloat[k][n][0] - paramArrayOfFloat[m][i1][0];
          paramArrayOfFloat[k][n][0] += paramArrayOfFloat[m][i1][0];
          paramArrayOfFloat[m][i1][0] = f;
          f = paramArrayOfFloat[m][i1][1] - paramArrayOfFloat[k][n][1];
          paramArrayOfFloat[k][n][1] += paramArrayOfFloat[m][i1][1];
          paramArrayOfFloat[m][i1][1] = f;
          f = paramArrayOfFloat[m][n][0] - paramArrayOfFloat[k][i1][0];
          paramArrayOfFloat[m][n][0] += paramArrayOfFloat[k][i1][0];
          paramArrayOfFloat[k][i1][0] = f;
          f = paramArrayOfFloat[k][i1][1] - paramArrayOfFloat[m][n][1];
          paramArrayOfFloat[m][n][1] += paramArrayOfFloat[k][i1][1];
          paramArrayOfFloat[k][i1][1] = f;
        }
      }
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        f = paramArrayOfFloat[0][n][0] - paramArrayOfFloat[0][i1][0];
        paramArrayOfFloat[0][n][0] += paramArrayOfFloat[0][i1][0];
        paramArrayOfFloat[0][i1][0] = f;
        f = paramArrayOfFloat[0][i1][1] - paramArrayOfFloat[0][n][1];
        paramArrayOfFloat[0][n][1] += paramArrayOfFloat[0][i1][1];
        paramArrayOfFloat[0][i1][1] = f;
        f = paramArrayOfFloat[i][n][0] - paramArrayOfFloat[i][i1][0];
        paramArrayOfFloat[i][n][0] += paramArrayOfFloat[i][i1][0];
        paramArrayOfFloat[i][i1][0] = f;
        f = paramArrayOfFloat[i][i1][1] - paramArrayOfFloat[i][n][1];
        paramArrayOfFloat[i][n][1] += paramArrayOfFloat[i][i1][1];
        paramArrayOfFloat[i][i1][1] = f;
      }
    }
    for (int k = 1; k < i; k++)
    {
      m = this.n1 - k;
      paramArrayOfFloat[m][0][0] = (0.5F * (paramArrayOfFloat[k][0][0] - paramArrayOfFloat[m][0][0]));
      paramArrayOfFloat[k][0][0] -= paramArrayOfFloat[m][0][0];
      paramArrayOfFloat[m][0][1] = (0.5F * (paramArrayOfFloat[k][0][1] + paramArrayOfFloat[m][0][1]));
      paramArrayOfFloat[k][0][1] -= paramArrayOfFloat[m][0][1];
      paramArrayOfFloat[m][j][0] = (0.5F * (paramArrayOfFloat[k][j][0] - paramArrayOfFloat[m][j][0]));
      paramArrayOfFloat[k][j][0] -= paramArrayOfFloat[m][j][0];
      paramArrayOfFloat[m][j][1] = (0.5F * (paramArrayOfFloat[k][j][1] + paramArrayOfFloat[m][j][1]));
      paramArrayOfFloat[k][j][1] -= paramArrayOfFloat[m][j][1];
      for (n = 1; n < j; n++)
      {
        i1 = this.n2 - n;
        paramArrayOfFloat[m][i1][0] = (0.5F * (paramArrayOfFloat[k][n][0] - paramArrayOfFloat[m][i1][0]));
        paramArrayOfFloat[k][n][0] -= paramArrayOfFloat[m][i1][0];
        paramArrayOfFloat[m][i1][1] = (0.5F * (paramArrayOfFloat[k][n][1] + paramArrayOfFloat[m][i1][1]));
        paramArrayOfFloat[k][n][1] -= paramArrayOfFloat[m][i1][1];
        paramArrayOfFloat[k][i1][0] = (0.5F * (paramArrayOfFloat[m][n][0] - paramArrayOfFloat[k][i1][0]));
        paramArrayOfFloat[m][n][0] -= paramArrayOfFloat[k][i1][0];
        paramArrayOfFloat[k][i1][1] = (0.5F * (paramArrayOfFloat[m][n][1] + paramArrayOfFloat[k][i1][1]));
        paramArrayOfFloat[m][n][1] -= paramArrayOfFloat[k][i1][1];
      }
    }
    for (int n = 1; n < j; n++)
    {
      i1 = this.n2 - n;
      paramArrayOfFloat[0][i1][0] = (0.5F * (paramArrayOfFloat[0][n][0] - paramArrayOfFloat[0][i1][0]));
      paramArrayOfFloat[0][n][0] -= paramArrayOfFloat[0][i1][0];
      paramArrayOfFloat[0][i1][1] = (0.5F * (paramArrayOfFloat[0][n][1] + paramArrayOfFloat[0][i1][1]));
      paramArrayOfFloat[0][n][1] -= paramArrayOfFloat[0][i1][1];
      paramArrayOfFloat[i][i1][0] = (0.5F * (paramArrayOfFloat[i][n][0] - paramArrayOfFloat[i][i1][0]));
      paramArrayOfFloat[i][n][0] -= paramArrayOfFloat[i][i1][0];
      paramArrayOfFloat[i][i1][1] = (0.5F * (paramArrayOfFloat[i][n][1] + paramArrayOfFloat[i][i1][1]));
      paramArrayOfFloat[i][n][1] -= paramArrayOfFloat[i][i1][1];
    }
  }

  private void fillSymmetric(final float[] paramArrayOfFloat)
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
            for (j = 0; j < FloatFFT_3D.this.n2; j++)
              for (int k = 1; k < FloatFFT_3D.this.n3; k += 2)
              {
                m = (FloatFFT_3D.this.n1 - i) % FloatFFT_3D.this.n1 * m + (FloatFFT_3D.this.n2 - j) % FloatFFT_3D.this.n2 * n + k - k;
                n = i * m + j * n + k;
                paramArrayOfFloat[m] = (-paramArrayOfFloat[(n + 2)]);
                paramArrayOfFloat[(m - 1)] = paramArrayOfFloat[(n + 1)];
              }
          for (i = i3; i < i4; i++)
            for (j = 1; j < i1; j++)
            {
              m = (FloatFFT_3D.this.n1 - i) % FloatFFT_3D.this.n1 * m + j * n + FloatFFT_3D.this.n3;
              n = i * m + (FloatFFT_3D.this.n2 - j) * n;
              int i1 = i * m + (FloatFFT_3D.this.n2 - j) * n + FloatFFT_3D.this.n3;
              paramArrayOfFloat[m] = paramArrayOfFloat[(n + 1)];
              paramArrayOfFloat[i1] = paramArrayOfFloat[(n + 1)];
              paramArrayOfFloat[(m + 1)] = (-paramArrayOfFloat[n]);
              paramArrayOfFloat[(i1 + 1)] = paramArrayOfFloat[n];
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

  private void fillSymmetric(final float[][][] paramArrayOfFloat)
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
            for (j = 0; j < FloatFFT_3D.this.n2; j++)
            {
              int k = 1;
              while (k < FloatFFT_3D.this.n3)
              {
                paramArrayOfFloat[((FloatFFT_3D.this.n1 - i) % FloatFFT_3D.this.n1)][((FloatFFT_3D.this.n2 - j) % FloatFFT_3D.this.n2)][(k - k)] = (-paramArrayOfFloat[i][j][(k + 2)]);
                paramArrayOfFloat[((FloatFFT_3D.this.n1 - i) % FloatFFT_3D.this.n1)][((FloatFFT_3D.this.n2 - j) % FloatFFT_3D.this.n2)][(k - k - 1)] = paramArrayOfFloat[i][j][(k + 1)];
                k += 2;
              }
            }
          for (i = i1; i < i2; i++)
            for (j = 1; j < m; j++)
            {
              paramArrayOfFloat[((FloatFFT_3D.this.n1 - i) % FloatFFT_3D.this.n1)][j][FloatFFT_3D.this.n3] = paramArrayOfFloat[i][(FloatFFT_3D.this.n2 - j)][1];
              paramArrayOfFloat[i][(FloatFFT_3D.this.n2 - j)][FloatFFT_3D.this.n3] = paramArrayOfFloat[i][(FloatFFT_3D.this.n2 - j)][1];
              paramArrayOfFloat[((FloatFFT_3D.this.n1 - i) % FloatFFT_3D.this.n1)][j][(FloatFFT_3D.this.n3 + 1)] = (-paramArrayOfFloat[i][(FloatFFT_3D.this.n2 - j)][0]);
              paramArrayOfFloat[i][(FloatFFT_3D.this.n2 - j)][(FloatFFT_3D.this.n3 + 1)] = paramArrayOfFloat[i][(FloatFFT_3D.this.n2 - j)][0];
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.FloatFFT_3D
 * JD-Core Version:    0.6.1
 */