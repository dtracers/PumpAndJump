package edu.emory.mathcs.jtransforms.fft;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatFFT_2D
{
  private int n1;
  private int n2;
  private int[] ip;
  private float[] w;
  private float[] t;
  private FloatFFT_1D fftn2;
  private FloatFFT_1D fftn1;
  private int oldNthread;
  private int nt;

  public FloatFFT_2D(int paramInt1, int paramInt2)
  {
    if ((!ConcurrencyUtils.isPowerOf2(paramInt1)) || (!ConcurrencyUtils.isPowerOf2(paramInt2)))
      throw new IllegalArgumentException("n1, n2 must be power of two numbers");
    if ((paramInt1 <= 1) || (paramInt2 <= 1))
      throw new IllegalArgumentException("n1, n2 must be greater than 1");
    this.n1 = paramInt1;
    this.n2 = paramInt2;
    this.ip = new int[2 + (int)Math.ceil(Math.sqrt(Math.max(paramInt1, paramInt2)))];
    this.w = new float[(int)Math.ceil(Math.max(Math.max(paramInt1 / 2, paramInt2 / 2), Math.max(paramInt1 / 2, paramInt2 / 4) + paramInt2 / 4))];
    this.fftn2 = new FloatFFT_1D(paramInt2, this.ip, this.w);
    this.fftn1 = new FloatFFT_1D(paramInt1, this.ip, this.w);
    this.oldNthread = ConcurrencyUtils.getNumberOfProcessors();
    this.nt = (8 * this.oldNthread * paramInt1);
    if (2 * paramInt2 == 4 * this.oldNthread)
      this.nt >>= 1;
    else if (2 * paramInt2 < 4 * this.oldNthread)
      this.nt >>= 2;
    this.t = new float[this.nt];
  }

  public void complexForward(float[] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, -1, paramArrayOfFloat, true);
      cdft2d_subth(-1, paramArrayOfFloat, true);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexForward(paramArrayOfFloat, k * this.n2);
      cdft2d_sub(-1, paramArrayOfFloat, true);
    }
    this.n2 = m;
  }

  public void complexForward(float[][] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, -1, paramArrayOfFloat, true);
      cdft2d_subth(-1, paramArrayOfFloat, true);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexForward(paramArrayOfFloat[k]);
      cdft2d_sub(-1, paramArrayOfFloat, true);
    }
    this.n2 = m;
  }

  public void complexInverse(float[] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, 1, paramArrayOfFloat, paramBoolean);
      cdft2d_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexInverse(paramArrayOfFloat, k * this.n2, paramBoolean);
      cdft2d_sub(1, paramArrayOfFloat, paramBoolean);
    }
    this.n2 = m;
  }

  public void complexInverse(float[][] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * m >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(0, 1, paramArrayOfFloat, paramBoolean);
      cdft2d_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      for (int k = 0; k < this.n1; k++)
        this.fftn2.complexInverse(paramArrayOfFloat[k], paramBoolean);
      cdft2d_sub(1, paramArrayOfFloat, paramBoolean);
    }
    this.n2 = m;
  }

  public void realForward(float[] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfFloat, true);
      cdft2d_subth(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfFloat, n * this.n2);
      cdft2d_sub(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
    }
  }

  public void realForward(float[][] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfFloat, true);
      cdft2d_subth(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfFloat[n]);
      cdft2d_sub(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
    }
  }

  public void realForwardFull(float[] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfFloat, true);
      cdft2d_subth(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfFloat, n * this.n2);
      cdft2d_sub(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
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
        paramArrayOfFloat[(i3 + i7)] = paramArrayOfFloat[(i2 + i7)];
        paramArrayOfFloat[(i2 + i7)] = 0.0F;
        paramArrayOfFloat[(i3 + i7 + 1)] = paramArrayOfFloat[(i2 + i7 + 1)];
        paramArrayOfFloat[(i2 + i7 + 1)] = 0.0F;
      }
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      int i4;
      for (i6 = 1; i6 < i5; i6++)
      {
        i3 = i6 * i1;
        i4 = (this.n1 - i6) * i1;
        paramArrayOfFloat[(i3 + this.n2)] = paramArrayOfFloat[(i4 + 1)];
        paramArrayOfFloat[(i3 + this.n2 + 1)] = (-paramArrayOfFloat[i4]);
      }
      for (i6 = 1; i6 < i5; i6++)
        for (i7 = this.n2 + 2; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1;
          i4 = (this.n1 - i6) * i1;
          paramArrayOfFloat[(i3 + i7)] = paramArrayOfFloat[(i4 + i1 - i7)];
          paramArrayOfFloat[(i3 + i7 + 1)] = (-paramArrayOfFloat[(i4 + i1 - i7 + 1)]);
        }
      for (i6 = 0; i6 <= this.n1 / 2; i6++)
        for (i7 = 0; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1 + i7;
          i4 = (this.n1 - i6) % this.n1 * i1 + (i1 - i7) % i1;
          paramArrayOfFloat[i4] = paramArrayOfFloat[i3];
          paramArrayOfFloat[(i4 + 1)] = (-paramArrayOfFloat[(i3 + 1)]);
        }
    }
    paramArrayOfFloat[this.n2] = (-paramArrayOfFloat[1]);
    paramArrayOfFloat[1] = 0.0F;
    paramArrayOfFloat[(i5 * i1 + this.n2)] = (-paramArrayOfFloat[(i5 * i1 + 1)]);
    paramArrayOfFloat[(i5 * i1 + 1)] = 0.0F;
    paramArrayOfFloat[(i5 * i1 + this.n2 + 1)] = 0.0F;
  }

  public void realForwardFull(float[][] paramArrayOfFloat)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth1(1, 1, paramArrayOfFloat, true);
      cdft2d_subth(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realForward(paramArrayOfFloat[n]);
      cdft2d_sub(-1, paramArrayOfFloat, true);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    int i1 = 2 * this.n2;
    int i2 = this.n1 / 2;
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      for (int i3 = 1; i3 < i2; i3++)
      {
        paramArrayOfFloat[i3][this.n2] = paramArrayOfFloat[(this.n1 - i3)][1];
        paramArrayOfFloat[i3][(this.n2 + 1)] = (-paramArrayOfFloat[(this.n1 - i3)][0]);
      }
      int i4;
      for (i3 = 1; i3 < i2; i3++)
        for (i4 = this.n2 + 2; i4 < i1; i4 += 2)
        {
          paramArrayOfFloat[i3][i4] = paramArrayOfFloat[(this.n1 - i3)][(i1 - i4)];
          paramArrayOfFloat[i3][(i4 + 1)] = (-paramArrayOfFloat[(this.n1 - i3)][(i1 - i4 + 1)]);
        }
      for (i3 = 0; i3 <= this.n1 / 2; i3++)
        for (i4 = 0; i4 < i1; i4 += 2)
        {
          paramArrayOfFloat[((this.n1 - i3) % this.n1)][((i1 - i4) % i1)] = paramArrayOfFloat[i3][i4];
          paramArrayOfFloat[((this.n1 - i3) % this.n1)][((i1 - i4) % i1 + 1)] = (-paramArrayOfFloat[i3][(i4 + 1)]);
        }
    }
    paramArrayOfFloat[0][this.n2] = (-paramArrayOfFloat[0][1]);
    paramArrayOfFloat[0][1] = 0.0F;
    paramArrayOfFloat[i2][this.n2] = (-paramArrayOfFloat[i2][1]);
    paramArrayOfFloat[i2][1] = 0.0F;
    paramArrayOfFloat[i2][(this.n2 + 1)] = 0.0F;
  }

  public void realInverse(float[] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      rdft2d_sub(-1, paramArrayOfFloat);
      cdft2d_subth(1, paramArrayOfFloat, paramBoolean);
      xdft2d0_subth1(1, -1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      rdft2d_sub(-1, paramArrayOfFloat);
      cdft2d_sub(1, paramArrayOfFloat, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse(paramArrayOfFloat, n * this.n2, paramBoolean);
    }
  }

  public void realInverse(float[][] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      rdft2d_sub(-1, paramArrayOfFloat);
      cdft2d_subth(1, paramArrayOfFloat, paramBoolean);
      xdft2d0_subth1(1, -1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      rdft2d_sub(-1, paramArrayOfFloat);
      cdft2d_sub(1, paramArrayOfFloat, paramBoolean);
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse(paramArrayOfFloat[n], paramBoolean);
    }
  }

  public void realInverseFull(float[] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth2(1, -1, paramArrayOfFloat, paramBoolean);
      cdft2d_subth(1, paramArrayOfFloat, paramBoolean);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse2(paramArrayOfFloat, n * this.n2, paramBoolean);
      cdft2d_sub(1, paramArrayOfFloat, paramBoolean);
      rdft2d_sub(1, paramArrayOfFloat);
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
        paramArrayOfFloat[(i3 + i7)] = paramArrayOfFloat[(i2 + i7)];
        paramArrayOfFloat[(i2 + i7)] = 0.0F;
        paramArrayOfFloat[(i3 + i7 + 1)] = paramArrayOfFloat[(i2 + i7 + 1)];
        paramArrayOfFloat[(i2 + i7 + 1)] = 0.0F;
      }
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      int i4;
      for (i6 = 1; i6 < i5; i6++)
      {
        i3 = i6 * i1;
        i4 = (this.n1 - i6) * i1;
        paramArrayOfFloat[(i3 + this.n2)] = paramArrayOfFloat[(i4 + 1)];
        paramArrayOfFloat[(i3 + this.n2 + 1)] = (-paramArrayOfFloat[i4]);
      }
      for (i6 = 1; i6 < i5; i6++)
        for (i7 = this.n2 + 2; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1;
          i4 = (this.n1 - i6) * i1;
          paramArrayOfFloat[(i3 + i7)] = paramArrayOfFloat[(i4 + i1 - i7)];
          paramArrayOfFloat[(i3 + i7 + 1)] = (-paramArrayOfFloat[(i4 + i1 - i7 + 1)]);
        }
      for (i6 = 0; i6 <= this.n1 / 2; i6++)
        for (i7 = 0; i7 < i1; i7 += 2)
        {
          i3 = i6 * i1 + i7;
          i4 = (this.n1 - i6) % this.n1 * i1 + (i1 - i7) % i1;
          paramArrayOfFloat[i4] = paramArrayOfFloat[i3];
          paramArrayOfFloat[(i4 + 1)] = (-paramArrayOfFloat[(i3 + 1)]);
        }
    }
    paramArrayOfFloat[this.n2] = (-paramArrayOfFloat[1]);
    paramArrayOfFloat[1] = 0.0F;
    paramArrayOfFloat[(i5 * i1 + this.n2)] = (-paramArrayOfFloat[(i5 * i1 + 1)]);
    paramArrayOfFloat[(i5 * i1 + 1)] = 0.0F;
    paramArrayOfFloat[(i5 * i1 + this.n2 + 1)] = 0.0F;
  }

  public void realInverseFull(float[][] paramArrayOfFloat, boolean paramBoolean)
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
      this.t = new float[this.nt];
      this.oldNthread = i;
    }
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      xdft2d0_subth2(1, -1, paramArrayOfFloat, paramBoolean);
      cdft2d_subth(1, paramArrayOfFloat, paramBoolean);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    else
    {
      for (int n = 0; n < this.n1; n++)
        this.fftn2.realInverse2(paramArrayOfFloat[n], 0, paramBoolean);
      cdft2d_sub(1, paramArrayOfFloat, paramBoolean);
      rdft2d_sub(1, paramArrayOfFloat);
    }
    int i1 = 2 * this.n2;
    int i2 = this.n1 / 2;
    if ((i > 1) && (this.n1 * this.n2 >= ConcurrencyUtils.getThreadsBeginN_2D()))
    {
      fillSymmetric(paramArrayOfFloat);
    }
    else
    {
      for (int i3 = 1; i3 < i2; i3++)
      {
        paramArrayOfFloat[i3][this.n2] = paramArrayOfFloat[(this.n1 - i3)][1];
        paramArrayOfFloat[i3][(this.n2 + 1)] = (-paramArrayOfFloat[(this.n1 - i3)][0]);
      }
      int i4;
      for (i3 = 1; i3 < i2; i3++)
        for (i4 = this.n2 + 2; i4 < i1; i4 += 2)
        {
          paramArrayOfFloat[i3][i4] = paramArrayOfFloat[(this.n1 - i3)][(i1 - i4)];
          paramArrayOfFloat[i3][(i4 + 1)] = (-paramArrayOfFloat[(this.n1 - i3)][(i1 - i4 + 1)]);
        }
      for (i3 = 0; i3 <= this.n1 / 2; i3++)
        for (i4 = 0; i4 < i1; i4 += 2)
        {
          paramArrayOfFloat[((this.n1 - i3) % this.n1)][((i1 - i4) % i1)] = paramArrayOfFloat[i3][i4];
          paramArrayOfFloat[((this.n1 - i3) % this.n1)][((i1 - i4) % i1 + 1)] = (-paramArrayOfFloat[i3][(i4 + 1)]);
        }
    }
    paramArrayOfFloat[0][this.n2] = (-paramArrayOfFloat[0][1]);
    paramArrayOfFloat[0][1] = 0.0F;
    paramArrayOfFloat[i2][this.n2] = (-paramArrayOfFloat[i2][1]);
    paramArrayOfFloat[i2][1] = 0.0F;
    paramArrayOfFloat[i2][(this.n2 + 1)] = 0.0F;
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

  private void rdft2d_sub(int paramInt, float[] paramArrayOfFloat)
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
        float f = paramArrayOfFloat[m] - paramArrayOfFloat[n];
        paramArrayOfFloat[m] += paramArrayOfFloat[n];
        paramArrayOfFloat[n] = f;
        f = paramArrayOfFloat[(n + 1)] - paramArrayOfFloat[(m + 1)];
        paramArrayOfFloat[(m + 1)] += paramArrayOfFloat[(n + 1)];
        paramArrayOfFloat[(n + 1)] = f;
      }
    for (int j = 1; j < i; j++)
    {
      k = this.n1 - j;
      m = j * this.n2;
      n = k * this.n2;
      paramArrayOfFloat[n] = (0.5F * (paramArrayOfFloat[m] - paramArrayOfFloat[n]));
      paramArrayOfFloat[m] -= paramArrayOfFloat[n];
      paramArrayOfFloat[(n + 1)] = (0.5F * (paramArrayOfFloat[(m + 1)] + paramArrayOfFloat[(n + 1)]));
      paramArrayOfFloat[(m + 1)] -= paramArrayOfFloat[(n + 1)];
    }
  }

  private void rdft2d_sub(int paramInt, float[][] paramArrayOfFloat)
  {
    int i = this.n1 >> 1;
    int k;
    if (paramInt < 0)
      for (j = 1; j < i; j++)
      {
        k = this.n1 - j;
        float f = paramArrayOfFloat[j][0] - paramArrayOfFloat[k][0];
        paramArrayOfFloat[j][0] += paramArrayOfFloat[k][0];
        paramArrayOfFloat[k][0] = f;
        f = paramArrayOfFloat[k][1] - paramArrayOfFloat[j][1];
        paramArrayOfFloat[j][1] += paramArrayOfFloat[k][1];
        paramArrayOfFloat[k][1] = f;
      }
    for (int j = 1; j < i; j++)
    {
      k = this.n1 - j;
      paramArrayOfFloat[k][0] = (0.5F * (paramArrayOfFloat[j][0] - paramArrayOfFloat[k][0]));
      paramArrayOfFloat[j][0] -= paramArrayOfFloat[k][0];
      paramArrayOfFloat[k][1] = (0.5F * (paramArrayOfFloat[j][1] + paramArrayOfFloat[k][1]));
      paramArrayOfFloat[j][1] -= paramArrayOfFloat[k][1];
    }
  }

  private void cdft2d_sub(int paramInt, float[] paramArrayOfFloat, boolean paramBoolean)
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
            this.t[m] = paramArrayOfFloat[k];
            this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
            this.t[n] = paramArrayOfFloat[(k + 2)];
            this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
            this.t[i1] = paramArrayOfFloat[(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfFloat[(k + 5)];
            this.t[i2] = paramArrayOfFloat[(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(k + 7)];
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
            paramArrayOfFloat[k] = this.t[m];
            paramArrayOfFloat[(k + 1)] = this.t[(m + 1)];
            paramArrayOfFloat[(k + 2)] = this.t[n];
            paramArrayOfFloat[(k + 3)] = this.t[(n + 1)];
            paramArrayOfFloat[(k + 4)] = this.t[i1];
            paramArrayOfFloat[(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(k + 6)] = this.t[i2];
            paramArrayOfFloat[(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          this.t[m] = paramArrayOfFloat[k];
          this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
          this.t[n] = paramArrayOfFloat[(k + 2)];
          this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
        }
        this.fftn1.complexForward(this.t, 0);
        this.fftn1.complexForward(this.t, 2 * this.n1);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          paramArrayOfFloat[k] = this.t[m];
          paramArrayOfFloat[(k + 1)] = this.t[(m + 1)];
          paramArrayOfFloat[(k + 2)] = this.t[n];
          paramArrayOfFloat[(k + 3)] = this.t[(n + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          this.t[m] = paramArrayOfFloat[k];
          this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
        }
        this.fftn1.complexForward(this.t, 0);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          paramArrayOfFloat[k] = this.t[m];
          paramArrayOfFloat[(k + 1)] = this.t[(m + 1)];
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
            this.t[m] = paramArrayOfFloat[k];
            this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
            this.t[n] = paramArrayOfFloat[(k + 2)];
            this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
            this.t[i1] = paramArrayOfFloat[(k + 4)];
            this.t[(i1 + 1)] = paramArrayOfFloat[(k + 5)];
            this.t[i2] = paramArrayOfFloat[(k + 6)];
            this.t[(i2 + 1)] = paramArrayOfFloat[(k + 7)];
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
            paramArrayOfFloat[k] = this.t[m];
            paramArrayOfFloat[(k + 1)] = this.t[(m + 1)];
            paramArrayOfFloat[(k + 2)] = this.t[n];
            paramArrayOfFloat[(k + 3)] = this.t[(n + 1)];
            paramArrayOfFloat[(k + 4)] = this.t[i1];
            paramArrayOfFloat[(k + 5)] = this.t[(i1 + 1)];
            paramArrayOfFloat[(k + 6)] = this.t[i2];
            paramArrayOfFloat[(k + 7)] = this.t[(i2 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          this.t[m] = paramArrayOfFloat[k];
          this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
          this.t[n] = paramArrayOfFloat[(k + 2)];
          this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          n = 2 * this.n1 + 2 * i;
          paramArrayOfFloat[k] = this.t[m];
          paramArrayOfFloat[(k + 1)] = this.t[(m + 1)];
          paramArrayOfFloat[(k + 2)] = this.t[n];
          paramArrayOfFloat[(k + 3)] = this.t[(n + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          this.t[m] = paramArrayOfFloat[k];
          this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = i * this.n2;
          m = 2 * i;
          paramArrayOfFloat[k] = this.t[m];
          paramArrayOfFloat[(k + 1)] = this.t[(m + 1)];
        }
      }
    }
  }

  private void cdft2d_sub(int paramInt, float[][] paramArrayOfFloat, boolean paramBoolean)
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
            this.t[k] = paramArrayOfFloat[i][j];
            this.t[(k + 1)] = paramArrayOfFloat[i][(j + 1)];
            this.t[m] = paramArrayOfFloat[i][(j + 2)];
            this.t[(m + 1)] = paramArrayOfFloat[i][(j + 3)];
            this.t[n] = paramArrayOfFloat[i][(j + 4)];
            this.t[(n + 1)] = paramArrayOfFloat[i][(j + 5)];
            this.t[i1] = paramArrayOfFloat[i][(j + 6)];
            this.t[(i1 + 1)] = paramArrayOfFloat[i][(j + 7)];
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
            paramArrayOfFloat[i][j] = this.t[k];
            paramArrayOfFloat[i][(j + 1)] = this.t[(k + 1)];
            paramArrayOfFloat[i][(j + 2)] = this.t[m];
            paramArrayOfFloat[i][(j + 3)] = this.t[(m + 1)];
            paramArrayOfFloat[i][(j + 4)] = this.t[n];
            paramArrayOfFloat[i][(j + 5)] = this.t[(n + 1)];
            paramArrayOfFloat[i][(j + 6)] = this.t[i1];
            paramArrayOfFloat[i][(j + 7)] = this.t[(i1 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          this.t[k] = paramArrayOfFloat[i][0];
          this.t[(k + 1)] = paramArrayOfFloat[i][1];
          this.t[m] = paramArrayOfFloat[i][2];
          this.t[(m + 1)] = paramArrayOfFloat[i][3];
        }
        this.fftn1.complexForward(this.t, 0);
        this.fftn1.complexForward(this.t, 2 * this.n1);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          paramArrayOfFloat[i][0] = this.t[k];
          paramArrayOfFloat[i][1] = this.t[(k + 1)];
          paramArrayOfFloat[i][2] = this.t[m];
          paramArrayOfFloat[i][3] = this.t[(m + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          this.t[k] = paramArrayOfFloat[i][0];
          this.t[(k + 1)] = paramArrayOfFloat[i][1];
        }
        this.fftn1.complexForward(this.t, 0);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          paramArrayOfFloat[i][0] = this.t[k];
          paramArrayOfFloat[i][1] = this.t[(k + 1)];
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
            this.t[k] = paramArrayOfFloat[i][j];
            this.t[(k + 1)] = paramArrayOfFloat[i][(j + 1)];
            this.t[m] = paramArrayOfFloat[i][(j + 2)];
            this.t[(m + 1)] = paramArrayOfFloat[i][(j + 3)];
            this.t[n] = paramArrayOfFloat[i][(j + 4)];
            this.t[(n + 1)] = paramArrayOfFloat[i][(j + 5)];
            this.t[i1] = paramArrayOfFloat[i][(j + 6)];
            this.t[(i1 + 1)] = paramArrayOfFloat[i][(j + 7)];
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
            paramArrayOfFloat[i][j] = this.t[k];
            paramArrayOfFloat[i][(j + 1)] = this.t[(k + 1)];
            paramArrayOfFloat[i][(j + 2)] = this.t[m];
            paramArrayOfFloat[i][(j + 3)] = this.t[(m + 1)];
            paramArrayOfFloat[i][(j + 4)] = this.t[n];
            paramArrayOfFloat[i][(j + 5)] = this.t[(n + 1)];
            paramArrayOfFloat[i][(j + 6)] = this.t[i1];
            paramArrayOfFloat[i][(j + 7)] = this.t[(i1 + 1)];
          }
        }
      if (this.n2 == 4)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          this.t[k] = paramArrayOfFloat[i][0];
          this.t[(k + 1)] = paramArrayOfFloat[i][1];
          this.t[m] = paramArrayOfFloat[i][2];
          this.t[(m + 1)] = paramArrayOfFloat[i][3];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        this.fftn1.complexInverse(this.t, 2 * this.n1, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          m = 2 * this.n1 + 2 * i;
          paramArrayOfFloat[i][0] = this.t[k];
          paramArrayOfFloat[i][1] = this.t[(k + 1)];
          paramArrayOfFloat[i][2] = this.t[m];
          paramArrayOfFloat[i][3] = this.t[(m + 1)];
        }
      }
      if (this.n2 == 2)
      {
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          this.t[k] = paramArrayOfFloat[i][0];
          this.t[(k + 1)] = paramArrayOfFloat[i][1];
        }
        this.fftn1.complexInverse(this.t, 0, paramBoolean);
        for (i = 0; i < this.n1; i++)
        {
          k = 2 * i;
          paramArrayOfFloat[i][0] = this.t[k];
          paramArrayOfFloat[i][1] = this.t[(k + 1)];
        }
      }
    }
  }

  private void xdft2d0_subth1(final int paramInt1, final int paramInt2, final float[] paramArrayOfFloat, final boolean paramBoolean)
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
              while (i < FloatFFT_2D.this.n1)
              {
                FloatFFT_2D.this.fftn2.complexForward(paramArrayOfFloat, i * FloatFFT_2D.this.n2);
                i += i;
              }
            }
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.complexInverse(paramArrayOfFloat, i * FloatFFT_2D.this.n2, paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.realForward(paramArrayOfFloat, i * FloatFFT_2D.this.n2);
              i += i;
            }
          }
          int i = m;
          while (i < FloatFFT_2D.this.n1)
          {
            FloatFFT_2D.this.fftn2.realInverse(paramArrayOfFloat, i * FloatFFT_2D.this.n2, paramBoolean);
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

  private void xdft2d0_subth2(final int paramInt1, final int paramInt2, final float[] paramArrayOfFloat, final boolean paramBoolean)
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
              while (i < FloatFFT_2D.this.n1)
              {
                FloatFFT_2D.this.fftn2.complexForward(paramArrayOfFloat, i * FloatFFT_2D.this.n2);
                i += i;
              }
            }
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.complexInverse(paramArrayOfFloat, i * FloatFFT_2D.this.n2, paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.realForward(paramArrayOfFloat, i * FloatFFT_2D.this.n2);
              i += i;
            }
          }
          int i = m;
          while (i < FloatFFT_2D.this.n1)
          {
            FloatFFT_2D.this.fftn2.realInverse2(paramArrayOfFloat, i * FloatFFT_2D.this.n2, paramBoolean);
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

  private void xdft2d0_subth1(final int paramInt1, final int paramInt2, final float[][] paramArrayOfFloat, final boolean paramBoolean)
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
              while (i < FloatFFT_2D.this.n1)
              {
                FloatFFT_2D.this.fftn2.complexForward(paramArrayOfFloat[i]);
                i += i;
              }
            }
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.complexInverse(paramArrayOfFloat[i], paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.realForward(paramArrayOfFloat[i]);
              i += i;
            }
          }
          int i = m;
          while (i < FloatFFT_2D.this.n1)
          {
            FloatFFT_2D.this.fftn2.realInverse(paramArrayOfFloat[i], paramBoolean);
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

  private void xdft2d0_subth2(final int paramInt1, final int paramInt2, final float[][] paramArrayOfFloat, final boolean paramBoolean)
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
              while (i < FloatFFT_2D.this.n1)
              {
                FloatFFT_2D.this.fftn2.complexForward(paramArrayOfFloat[i]);
                i += i;
              }
            }
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.complexInverse(paramArrayOfFloat[i], paramBoolean);
              i += i;
            }
          }
          if (paramInt2 == 1)
          {
            i = m;
            while (i < FloatFFT_2D.this.n1)
            {
              FloatFFT_2D.this.fftn2.realForward(paramArrayOfFloat[i]);
              i += i;
            }
          }
          int i = m;
          while (i < FloatFFT_2D.this.n1)
          {
            FloatFFT_2D.this.fftn2.realInverse2(paramArrayOfFloat[i], 0, paramBoolean);
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

  private void cdft2d_subth(final int paramInt, final float[] paramArrayOfFloat, final boolean paramBoolean)
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
            if (FloatFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < FloatFFT_2D.this.n2)
              {
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i * FloatFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  i2 = i1 + 2 * FloatFFT_2D.this.n1;
                  FloatFFT_2D.this.t[m] = paramArrayOfFloat[k];
                  FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
                  FloatFFT_2D.this.t[n] = paramArrayOfFloat[(k + 2)];
                  FloatFFT_2D.this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
                  FloatFFT_2D.this.t[i1] = paramArrayOfFloat[(k + 4)];
                  FloatFFT_2D.this.t[(i1 + 1)] = paramArrayOfFloat[(k + 5)];
                  FloatFFT_2D.this.t[i2] = paramArrayOfFloat[(k + 6)];
                  FloatFFT_2D.this.t[(i2 + 1)] = paramArrayOfFloat[(k + 7)];
                }
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2);
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1);
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 4 * FloatFFT_2D.this.n1);
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 6 * FloatFFT_2D.this.n1);
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i * FloatFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  i2 = i1 + 2 * FloatFFT_2D.this.n1;
                  paramArrayOfFloat[k] = FloatFFT_2D.this.t[m];
                  paramArrayOfFloat[(k + 1)] = FloatFFT_2D.this.t[(m + 1)];
                  paramArrayOfFloat[(k + 2)] = FloatFFT_2D.this.t[n];
                  paramArrayOfFloat[(k + 3)] = FloatFFT_2D.this.t[(n + 1)];
                  paramArrayOfFloat[(k + 4)] = FloatFFT_2D.this.t[i1];
                  paramArrayOfFloat[(k + 5)] = FloatFFT_2D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(k + 6)] = FloatFFT_2D.this.t[i2];
                  paramArrayOfFloat[(k + 7)] = FloatFFT_2D.this.t[(i2 + 1)];
                }
                j += 8 * n;
              }
            }
            if (FloatFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                FloatFFT_2D.this.t[m] = paramArrayOfFloat[k];
                FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
                FloatFFT_2D.this.t[n] = paramArrayOfFloat[(k + 2)];
                FloatFFT_2D.this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
              }
              FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2);
              FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                paramArrayOfFloat[k] = FloatFFT_2D.this.t[m];
                paramArrayOfFloat[(k + 1)] = FloatFFT_2D.this.t[(m + 1)];
                paramArrayOfFloat[(k + 2)] = FloatFFT_2D.this.t[n];
                paramArrayOfFloat[(k + 3)] = FloatFFT_2D.this.t[(n + 1)];
              }
            }
            if (FloatFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                FloatFFT_2D.this.t[m] = paramArrayOfFloat[k];
                FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
              }
              FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                paramArrayOfFloat[k] = FloatFFT_2D.this.t[m];
                paramArrayOfFloat[(k + 1)] = FloatFFT_2D.this.t[(m + 1)];
              }
            }
          }
          else
          {
            if (FloatFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < FloatFFT_2D.this.n2)
              {
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i * FloatFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  i2 = i1 + 2 * FloatFFT_2D.this.n1;
                  FloatFFT_2D.this.t[m] = paramArrayOfFloat[k];
                  FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
                  FloatFFT_2D.this.t[n] = paramArrayOfFloat[(k + 2)];
                  FloatFFT_2D.this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
                  FloatFFT_2D.this.t[i1] = paramArrayOfFloat[(k + 4)];
                  FloatFFT_2D.this.t[(i1 + 1)] = paramArrayOfFloat[(k + 5)];
                  FloatFFT_2D.this.t[i2] = paramArrayOfFloat[(k + 6)];
                  FloatFFT_2D.this.t[(i2 + 1)] = paramArrayOfFloat[(k + 7)];
                }
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2, paramBoolean);
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1, paramBoolean);
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 4 * FloatFFT_2D.this.n1, paramBoolean);
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 6 * FloatFFT_2D.this.n1, paramBoolean);
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i * FloatFFT_2D.this.n2 + j;
                  m = i2 + 2 * i;
                  n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  i2 = i1 + 2 * FloatFFT_2D.this.n1;
                  paramArrayOfFloat[k] = FloatFFT_2D.this.t[m];
                  paramArrayOfFloat[(k + 1)] = FloatFFT_2D.this.t[(m + 1)];
                  paramArrayOfFloat[(k + 2)] = FloatFFT_2D.this.t[n];
                  paramArrayOfFloat[(k + 3)] = FloatFFT_2D.this.t[(n + 1)];
                  paramArrayOfFloat[(k + 4)] = FloatFFT_2D.this.t[i1];
                  paramArrayOfFloat[(k + 5)] = FloatFFT_2D.this.t[(i1 + 1)];
                  paramArrayOfFloat[(k + 6)] = FloatFFT_2D.this.t[i2];
                  paramArrayOfFloat[(k + 7)] = FloatFFT_2D.this.t[(i2 + 1)];
                }
                j += 8 * n;
              }
            }
            if (FloatFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                FloatFFT_2D.this.t[m] = paramArrayOfFloat[k];
                FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
                FloatFFT_2D.this.t[n] = paramArrayOfFloat[(k + 2)];
                FloatFFT_2D.this.t[(n + 1)] = paramArrayOfFloat[(k + 3)];
              }
              FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2, paramBoolean);
              FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1, paramBoolean);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 4 * i1;
                m = i2 + 2 * i;
                n = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                paramArrayOfFloat[k] = FloatFFT_2D.this.t[m];
                paramArrayOfFloat[(k + 1)] = FloatFFT_2D.this.t[(m + 1)];
                paramArrayOfFloat[(k + 2)] = FloatFFT_2D.this.t[n];
                paramArrayOfFloat[(k + 3)] = FloatFFT_2D.this.t[(n + 1)];
              }
            }
            if (FloatFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                FloatFFT_2D.this.t[m] = paramArrayOfFloat[k];
                FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[(k + 1)];
              }
              FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2, paramBoolean);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i * FloatFFT_2D.this.n2 + 2 * i1;
                m = i2 + 2 * i;
                paramArrayOfFloat[k] = FloatFFT_2D.this.t[m];
                paramArrayOfFloat[(k + 1)] = FloatFFT_2D.this.t[(m + 1)];
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

  private void cdft2d_subth(final int paramInt, final float[][] paramArrayOfFloat, final boolean paramBoolean)
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
            if (FloatFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < FloatFFT_2D.this.n2)
              {
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * FloatFFT_2D.this.n1;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  FloatFFT_2D.this.t[k] = paramArrayOfFloat[i][j];
                  FloatFFT_2D.this.t[(k + 1)] = paramArrayOfFloat[i][(j + 1)];
                  FloatFFT_2D.this.t[m] = paramArrayOfFloat[i][(j + 2)];
                  FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[i][(j + 3)];
                  FloatFFT_2D.this.t[n] = paramArrayOfFloat[i][(j + 4)];
                  FloatFFT_2D.this.t[(n + 1)] = paramArrayOfFloat[i][(j + 5)];
                  FloatFFT_2D.this.t[i1] = paramArrayOfFloat[i][(j + 6)];
                  FloatFFT_2D.this.t[(i1 + 1)] = paramArrayOfFloat[i][(j + 7)];
                }
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2);
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1);
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 4 * FloatFFT_2D.this.n1);
                FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 6 * FloatFFT_2D.this.n1);
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * FloatFFT_2D.this.n1;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  paramArrayOfFloat[i][j] = FloatFFT_2D.this.t[k];
                  paramArrayOfFloat[i][(j + 1)] = FloatFFT_2D.this.t[(k + 1)];
                  paramArrayOfFloat[i][(j + 2)] = FloatFFT_2D.this.t[m];
                  paramArrayOfFloat[i][(j + 3)] = FloatFFT_2D.this.t[(m + 1)];
                  paramArrayOfFloat[i][(j + 4)] = FloatFFT_2D.this.t[n];
                  paramArrayOfFloat[i][(j + 5)] = FloatFFT_2D.this.t[(n + 1)];
                  paramArrayOfFloat[i][(j + 6)] = FloatFFT_2D.this.t[i1];
                  paramArrayOfFloat[i][(j + 7)] = FloatFFT_2D.this.t[(i1 + 1)];
                }
                j += 8 * n;
              }
            }
            if (FloatFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                FloatFFT_2D.this.t[k] = paramArrayOfFloat[i][(4 * i1)];
                FloatFFT_2D.this.t[(k + 1)] = paramArrayOfFloat[i][(4 * i1 + 1)];
                FloatFFT_2D.this.t[m] = paramArrayOfFloat[i][(4 * i1 + 2)];
                FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[i][(4 * i1 + 3)];
              }
              FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2);
              FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                paramArrayOfFloat[i][(4 * i1)] = FloatFFT_2D.this.t[k];
                paramArrayOfFloat[i][(4 * i1 + 1)] = FloatFFT_2D.this.t[(k + 1)];
                paramArrayOfFloat[i][(4 * i1 + 2)] = FloatFFT_2D.this.t[m];
                paramArrayOfFloat[i][(4 * i1 + 3)] = FloatFFT_2D.this.t[(m + 1)];
              }
            }
            if (FloatFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                FloatFFT_2D.this.t[k] = paramArrayOfFloat[i][(2 * i1)];
                FloatFFT_2D.this.t[(k + 1)] = paramArrayOfFloat[i][(2 * i1 + 1)];
              }
              FloatFFT_2D.this.fftn1.complexForward(FloatFFT_2D.this.t, i2);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                paramArrayOfFloat[i][(2 * i1)] = FloatFFT_2D.this.t[k];
                paramArrayOfFloat[i][(2 * i1 + 1)] = FloatFFT_2D.this.t[(k + 1)];
              }
            }
          }
          else
          {
            if (FloatFFT_2D.this.n2 > 4 * n)
            {
              j = 8 * i1;
              while (j < FloatFFT_2D.this.n2)
              {
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * FloatFFT_2D.this.n1;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  FloatFFT_2D.this.t[k] = paramArrayOfFloat[i][j];
                  FloatFFT_2D.this.t[(k + 1)] = paramArrayOfFloat[i][(j + 1)];
                  FloatFFT_2D.this.t[m] = paramArrayOfFloat[i][(j + 2)];
                  FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[i][(j + 3)];
                  FloatFFT_2D.this.t[n] = paramArrayOfFloat[i][(j + 4)];
                  FloatFFT_2D.this.t[(n + 1)] = paramArrayOfFloat[i][(j + 5)];
                  FloatFFT_2D.this.t[i1] = paramArrayOfFloat[i][(j + 6)];
                  FloatFFT_2D.this.t[(i1 + 1)] = paramArrayOfFloat[i][(j + 7)];
                }
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2, paramBoolean);
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1, paramBoolean);
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 4 * FloatFFT_2D.this.n1, paramBoolean);
                FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 6 * FloatFFT_2D.this.n1, paramBoolean);
                for (i = 0; i < FloatFFT_2D.this.n1; i++)
                {
                  k = i2 + 2 * i;
                  m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                  n = m + 2 * FloatFFT_2D.this.n1;
                  i1 = n + 2 * FloatFFT_2D.this.n1;
                  paramArrayOfFloat[i][j] = FloatFFT_2D.this.t[k];
                  paramArrayOfFloat[i][(j + 1)] = FloatFFT_2D.this.t[(k + 1)];
                  paramArrayOfFloat[i][(j + 2)] = FloatFFT_2D.this.t[m];
                  paramArrayOfFloat[i][(j + 3)] = FloatFFT_2D.this.t[(m + 1)];
                  paramArrayOfFloat[i][(j + 4)] = FloatFFT_2D.this.t[n];
                  paramArrayOfFloat[i][(j + 5)] = FloatFFT_2D.this.t[(n + 1)];
                  paramArrayOfFloat[i][(j + 6)] = FloatFFT_2D.this.t[i1];
                  paramArrayOfFloat[i][(j + 7)] = FloatFFT_2D.this.t[(i1 + 1)];
                }
                j += 8 * n;
              }
            }
            if (FloatFFT_2D.this.n2 == 4 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                FloatFFT_2D.this.t[k] = paramArrayOfFloat[i][(4 * i1)];
                FloatFFT_2D.this.t[(k + 1)] = paramArrayOfFloat[i][(4 * i1 + 1)];
                FloatFFT_2D.this.t[m] = paramArrayOfFloat[i][(4 * i1 + 2)];
                FloatFFT_2D.this.t[(m + 1)] = paramArrayOfFloat[i][(4 * i1 + 3)];
              }
              FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2, paramBoolean);
              FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2 + 2 * FloatFFT_2D.this.n1, paramBoolean);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                m = i2 + 2 * FloatFFT_2D.this.n1 + 2 * i;
                paramArrayOfFloat[i][(4 * i1)] = FloatFFT_2D.this.t[k];
                paramArrayOfFloat[i][(4 * i1 + 1)] = FloatFFT_2D.this.t[(k + 1)];
                paramArrayOfFloat[i][(4 * i1 + 2)] = FloatFFT_2D.this.t[m];
                paramArrayOfFloat[i][(4 * i1 + 3)] = FloatFFT_2D.this.t[(m + 1)];
              }
            }
            if (FloatFFT_2D.this.n2 == 2 * n)
            {
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                FloatFFT_2D.this.t[k] = paramArrayOfFloat[i][(2 * i1)];
                FloatFFT_2D.this.t[(k + 1)] = paramArrayOfFloat[i][(2 * i1 + 1)];
              }
              FloatFFT_2D.this.fftn1.complexInverse(FloatFFT_2D.this.t, i2, paramBoolean);
              for (i = 0; i < FloatFFT_2D.this.n1; i++)
              {
                k = i2 + 2 * i;
                paramArrayOfFloat[i][(2 * i1)] = FloatFFT_2D.this.t[k];
                paramArrayOfFloat[i][(2 * i1 + 1)] = FloatFFT_2D.this.t[(k + 1)];
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

  private void fillSymmetric(final float[] paramArrayOfFloat)
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
            j = (FloatFFT_2D.this.n1 - k) * m;
            paramArrayOfFloat[(i + FloatFFT_2D.this.n2)] = paramArrayOfFloat[(j + 1)];
            paramArrayOfFloat[(i + FloatFFT_2D.this.n2 + 1)] = (-paramArrayOfFloat[j]);
          }
          int m;
          for (k = i1; k < i2; k++)
          {
            m = FloatFFT_2D.this.n2 + 2;
            while (m < m)
            {
              i = k * m;
              j = (FloatFFT_2D.this.n1 - k) * m + m - m;
              paramArrayOfFloat[(i + m)] = paramArrayOfFloat[j];
              paramArrayOfFloat[(i + m + 1)] = (-paramArrayOfFloat[(j + 1)]);
              m += 2;
            }
          }
          for (k = i3; k < i4; k++)
          {
            m = 0;
            while (m < m)
            {
              i = (FloatFFT_2D.this.n1 - k) % FloatFFT_2D.this.n1 * m + (m - m) % m;
              j = k * m + m;
              paramArrayOfFloat[i] = paramArrayOfFloat[j];
              paramArrayOfFloat[(i + 1)] = (-paramArrayOfFloat[(j + 1)]);
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

  private void fillSymmetric(final float[][] paramArrayOfFloat)
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
            paramArrayOfFloat[i][FloatFFT_2D.this.n2] = paramArrayOfFloat[(FloatFFT_2D.this.n1 - i)][1];
            paramArrayOfFloat[i][(FloatFFT_2D.this.n2 + 1)] = (-paramArrayOfFloat[(FloatFFT_2D.this.n1 - i)][0]);
          }
          int j;
          for (i = i1; i < i2; i++)
          {
            j = FloatFFT_2D.this.n2 + 2;
            while (j < m)
            {
              paramArrayOfFloat[i][j] = paramArrayOfFloat[(FloatFFT_2D.this.n1 - i)][(m - j)];
              paramArrayOfFloat[i][(j + 1)] = (-paramArrayOfFloat[(FloatFFT_2D.this.n1 - i)][(m - j + 1)]);
              j += 2;
            }
          }
          for (i = i3; i < i4; i++)
          {
            j = 0;
            while (j < m)
            {
              paramArrayOfFloat[((FloatFFT_2D.this.n1 - i) % FloatFFT_2D.this.n1)][((m - j) % m)] = paramArrayOfFloat[i][j];
              paramArrayOfFloat[((FloatFFT_2D.this.n1 - i) % FloatFFT_2D.this.n1)][((m - j) % m + 1)] = (-paramArrayOfFloat[i][(j + 1)]);
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.fft.FloatFFT_2D
 * JD-Core Version:    0.6.1
 */