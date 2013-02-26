package edu.emory.mathcs.jtransforms.dct;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatDCT_3D
{
  private int n1;
  private int n2;
  private int n3;
  private int sliceStride;
  private int rowStride;
  private int[] ip;
  private float[] w;
  private float[] t;
  private FloatDCT_1D dctn1;
  private FloatDCT_1D dctn2;
  private FloatDCT_1D dctn3;
  private int oldNthread;
  private int nt;

  public FloatDCT_3D(int paramInt1, int paramInt2, int paramInt3)
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
    this.ip = new int[2 + (int)Math.ceil(Math.sqrt(Math.max(Math.max(paramInt1 / 2, paramInt2 / 2), paramInt3 / 2)))];
    this.w = new float[(int)Math.ceil(Math.max(Math.max(paramInt1 * 1.5D, paramInt2 * 1.5D), paramInt3 * 1.5D))];
    this.dctn1 = new FloatDCT_1D(paramInt1, this.ip, this.w);
    this.dctn2 = new FloatDCT_1D(paramInt2, this.ip, this.w);
    this.dctn3 = new FloatDCT_1D(paramInt3, this.ip, this.w);
    this.oldNthread = ConcurrencyUtils.getNumberOfProcessors();
    this.nt = paramInt1;
    if (this.nt < paramInt2)
      this.nt = paramInt2;
    this.nt *= 4;
    if (this.oldNthread > 1)
      this.nt *= this.oldNthread;
    if (paramInt3 == 2)
      this.nt >>= 1;
    this.t = new float[this.nt];
  }

  public void forward(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (i > k)
    {
      k = i;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthread)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 4;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 2)
        this.nt >>= 1;
      this.t = new float[this.nt];
      this.oldNthread = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(-1, paramArrayOfFloat, paramBoolean);
      ddxt3db_subth(-1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt3da_sub(-1, paramArrayOfFloat, paramBoolean);
      ddxt3db_sub(-1, paramArrayOfFloat, paramBoolean);
    }
  }

  public void forward(float[][][] paramArrayOfFloat, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (i > k)
    {
      k = i;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthread)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 4;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 2)
        this.nt >>= 1;
      this.t = new float[this.nt];
      this.oldNthread = m;
    }
    if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(-1, paramArrayOfFloat, paramBoolean);
      ddxt3db_subth(-1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt3da_sub(-1, paramArrayOfFloat, paramBoolean);
      ddxt3db_sub(-1, paramArrayOfFloat, paramBoolean);
    }
  }

  public void inverse(float[] paramArrayOfFloat, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (i > k)
    {
      k = i;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthread)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 4;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 2)
        this.nt >>= 1;
      this.t = new float[this.nt];
      this.oldNthread = m;
    }
    if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt3da_sub(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_sub(1, paramArrayOfFloat, paramBoolean);
    }
  }

  public void inverse(float[][][] paramArrayOfFloat, boolean paramBoolean)
  {
    int i = this.n1;
    if (i < this.n2)
      i = this.n2;
    if (i < this.n3)
      i = this.n3;
    int j = this.ip[0];
    if (i > j << 2)
    {
      j = i >> 2;
      makewt(j);
    }
    int k = this.ip[1];
    if (i > k)
    {
      k = i;
      makect(k, this.w, j);
    }
    int m = ConcurrencyUtils.getNumberOfProcessors();
    if (m != this.oldNthread)
    {
      this.nt = this.n1;
      if (this.nt < this.n2)
        this.nt = this.n2;
      this.nt *= 4;
      if (m > 1)
        this.nt *= m;
      if (this.n3 == 2)
        this.nt >>= 1;
      this.t = new float[this.nt];
      this.oldNthread = m;
    }
    if ((ConcurrencyUtils.getNumberOfProcessors() > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt3da_sub(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_sub(1, paramArrayOfFloat, paramBoolean);
    }
  }

  private void ddxt3da_sub(int paramInt, float[] paramArrayOfFloat, boolean paramBoolean)
  {
    int m;
    int j;
    int k;
    int n;
    int i1;
    if (paramInt == -1)
      for (i = 0; i < this.n1; i++)
      {
        m = i * this.sliceStride;
        for (j = 0; j < this.n2; j++)
          this.dctn3.forward(paramArrayOfFloat, m + j * this.rowStride, paramBoolean);
        if (this.n3 > 2)
          for (k = 0; k < this.n3; k += 4)
          {
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = this.n2 + j;
              this.t[j] = paramArrayOfFloat[n];
              this.t[i1] = paramArrayOfFloat[(n + 1)];
              this.t[(i1 + this.n2)] = paramArrayOfFloat[(n + 2)];
              this.t[(i1 + 2 * this.n2)] = paramArrayOfFloat[(n + 3)];
            }
            this.dctn2.forward(this.t, 0, paramBoolean);
            this.dctn2.forward(this.t, this.n2, paramBoolean);
            this.dctn2.forward(this.t, 2 * this.n2, paramBoolean);
            this.dctn2.forward(this.t, 3 * this.n2, paramBoolean);
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = this.n2 + j;
              paramArrayOfFloat[n] = this.t[j];
              paramArrayOfFloat[(n + 1)] = this.t[i1];
              paramArrayOfFloat[(n + 2)] = this.t[(i1 + this.n2)];
              paramArrayOfFloat[(n + 3)] = this.t[(i1 + 2 * this.n2)];
            }
          }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            this.t[j] = paramArrayOfFloat[n];
            this.t[(this.n2 + j)] = paramArrayOfFloat[(n + 1)];
          }
          this.dctn2.forward(this.t, 0, paramBoolean);
          this.dctn2.forward(this.t, this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            paramArrayOfFloat[n] = this.t[j];
            paramArrayOfFloat[(n + 1)] = this.t[(this.n2 + j)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      m = i * this.sliceStride;
      for (j = 0; j < this.n2; j++)
        this.dctn3.inverse(paramArrayOfFloat, m + j * this.rowStride, paramBoolean);
      if (this.n3 > 2)
        for (k = 0; k < this.n3; k += 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = this.n2 + j;
            this.t[j] = paramArrayOfFloat[n];
            this.t[i1] = paramArrayOfFloat[(n + 1)];
            this.t[(i1 + this.n2)] = paramArrayOfFloat[(n + 2)];
            this.t[(i1 + 2 * this.n2)] = paramArrayOfFloat[(n + 3)];
          }
          this.dctn2.inverse(this.t, 0, paramBoolean);
          this.dctn2.inverse(this.t, this.n2, paramBoolean);
          this.dctn2.inverse(this.t, 2 * this.n2, paramBoolean);
          this.dctn2.inverse(this.t, 3 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = this.n2 + j;
            paramArrayOfFloat[n] = this.t[j];
            paramArrayOfFloat[(n + 1)] = this.t[i1];
            paramArrayOfFloat[(n + 2)] = this.t[(i1 + this.n2)];
            paramArrayOfFloat[(n + 3)] = this.t[(i1 + 2 * this.n2)];
          }
        }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          this.t[j] = paramArrayOfFloat[n];
          this.t[(this.n2 + j)] = paramArrayOfFloat[(n + 1)];
        }
        this.dctn2.inverse(this.t, 0, paramBoolean);
        this.dctn2.inverse(this.t, this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          paramArrayOfFloat[n] = this.t[j];
          paramArrayOfFloat[(n + 1)] = this.t[(this.n2 + j)];
        }
      }
    }
  }

  private void ddxt3da_sub(int paramInt, float[][][] paramArrayOfFloat, boolean paramBoolean)
  {
    int j;
    int k;
    int m;
    if (paramInt == -1)
      for (i = 0; i < this.n1; i++)
      {
        for (j = 0; j < this.n2; j++)
          this.dctn3.forward(paramArrayOfFloat[i][j], paramBoolean);
        if (this.n3 > 2)
          for (k = 0; k < this.n3; k += 4)
          {
            for (j = 0; j < this.n2; j++)
            {
              m = this.n2 + j;
              this.t[j] = paramArrayOfFloat[i][j][k];
              this.t[m] = paramArrayOfFloat[i][j][(k + 1)];
              this.t[(m + this.n2)] = paramArrayOfFloat[i][j][(k + 2)];
              this.t[(m + 2 * this.n2)] = paramArrayOfFloat[i][j][(k + 3)];
            }
            this.dctn2.forward(this.t, 0, paramBoolean);
            this.dctn2.forward(this.t, this.n2, paramBoolean);
            this.dctn2.forward(this.t, 2 * this.n2, paramBoolean);
            this.dctn2.forward(this.t, 3 * this.n2, paramBoolean);
            for (j = 0; j < this.n2; j++)
            {
              m = this.n2 + j;
              paramArrayOfFloat[i][j][k] = this.t[j];
              paramArrayOfFloat[i][j][(k + 1)] = this.t[m];
              paramArrayOfFloat[i][j][(k + 2)] = this.t[(m + this.n2)];
              paramArrayOfFloat[i][j][(k + 3)] = this.t[(m + 2 * this.n2)];
            }
          }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            this.t[j] = paramArrayOfFloat[i][j][0];
            this.t[(this.n2 + j)] = paramArrayOfFloat[i][j][1];
          }
          this.dctn2.forward(this.t, 0, paramBoolean);
          this.dctn2.forward(this.t, this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            paramArrayOfFloat[i][j][0] = this.t[j];
            paramArrayOfFloat[i][j][1] = this.t[(this.n2 + j)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      for (j = 0; j < this.n2; j++)
        this.dctn3.inverse(paramArrayOfFloat[i][j], paramBoolean);
      if (this.n3 > 2)
        for (k = 0; k < this.n3; k += 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = this.n2 + j;
            this.t[j] = paramArrayOfFloat[i][j][k];
            this.t[m] = paramArrayOfFloat[i][j][(k + 1)];
            this.t[(m + this.n2)] = paramArrayOfFloat[i][j][(k + 2)];
            this.t[(m + 2 * this.n2)] = paramArrayOfFloat[i][j][(k + 3)];
          }
          this.dctn2.inverse(this.t, 0, paramBoolean);
          this.dctn2.inverse(this.t, this.n2, paramBoolean);
          this.dctn2.inverse(this.t, 2 * this.n2, paramBoolean);
          this.dctn2.inverse(this.t, 3 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            m = this.n2 + j;
            paramArrayOfFloat[i][j][k] = this.t[j];
            paramArrayOfFloat[i][j][(k + 1)] = this.t[m];
            paramArrayOfFloat[i][j][(k + 2)] = this.t[(m + this.n2)];
            paramArrayOfFloat[i][j][(k + 3)] = this.t[(m + 2 * this.n2)];
          }
        }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          this.t[j] = paramArrayOfFloat[i][j][0];
          this.t[(this.n2 + j)] = paramArrayOfFloat[i][j][1];
        }
        this.dctn2.inverse(this.t, 0, paramBoolean);
        this.dctn2.inverse(this.t, this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          paramArrayOfFloat[i][j][0] = this.t[j];
          paramArrayOfFloat[i][j][1] = this.t[(this.n2 + j)];
        }
      }
    }
  }

  private void ddxt3db_sub(int paramInt, float[] paramArrayOfFloat, boolean paramBoolean)
  {
    int j;
    int m;
    int k;
    int i;
    int n;
    int i1;
    if (paramInt == -1)
    {
      if (this.n3 > 2)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (k = 0; k < this.n3; k += 4)
          {
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = this.n1 + i;
              this.t[i] = paramArrayOfFloat[n];
              this.t[i1] = paramArrayOfFloat[(n + 1)];
              this.t[(i1 + this.n1)] = paramArrayOfFloat[(n + 2)];
              this.t[(i1 + 2 * this.n1)] = paramArrayOfFloat[(n + 3)];
            }
            this.dctn1.forward(this.t, 0, paramBoolean);
            this.dctn1.forward(this.t, this.n1, paramBoolean);
            this.dctn1.forward(this.t, 2 * this.n1, paramBoolean);
            this.dctn1.forward(this.t, 3 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = this.n1 + i;
              paramArrayOfFloat[n] = this.t[i];
              paramArrayOfFloat[(n + 1)] = this.t[i1];
              paramArrayOfFloat[(n + 2)] = this.t[(i1 + this.n1)];
              paramArrayOfFloat[(n + 3)] = this.t[(i1 + 2 * this.n1)];
            }
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            this.t[i] = paramArrayOfFloat[n];
            this.t[(this.n1 + i)] = paramArrayOfFloat[(n + 1)];
          }
          this.dctn1.forward(this.t, 0, paramBoolean);
          this.dctn1.forward(this.t, this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            paramArrayOfFloat[n] = this.t[i];
            paramArrayOfFloat[(n + 1)] = this.t[(this.n1 + i)];
          }
        }
    }
    else
    {
      if (this.n3 > 2)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (k = 0; k < this.n3; k += 4)
          {
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = this.n1 + i;
              this.t[i] = paramArrayOfFloat[n];
              this.t[i1] = paramArrayOfFloat[(n + 1)];
              this.t[(i1 + this.n1)] = paramArrayOfFloat[(n + 2)];
              this.t[(i1 + 2 * this.n1)] = paramArrayOfFloat[(n + 3)];
            }
            this.dctn1.inverse(this.t, 0, paramBoolean);
            this.dctn1.inverse(this.t, this.n1, paramBoolean);
            this.dctn1.inverse(this.t, 2 * this.n1, paramBoolean);
            this.dctn1.inverse(this.t, 3 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = this.n1 + i;
              paramArrayOfFloat[n] = this.t[i];
              paramArrayOfFloat[(n + 1)] = this.t[i1];
              paramArrayOfFloat[(n + 2)] = this.t[(i1 + this.n1)];
              paramArrayOfFloat[(n + 3)] = this.t[(i1 + 2 * this.n1)];
            }
          }
        }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          m = j * this.rowStride;
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            this.t[i] = paramArrayOfFloat[n];
            this.t[(this.n1 + i)] = paramArrayOfFloat[(n + 1)];
          }
          this.dctn1.inverse(this.t, 0, paramBoolean);
          this.dctn1.inverse(this.t, this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            paramArrayOfFloat[n] = this.t[i];
            paramArrayOfFloat[(n + 1)] = this.t[(this.n1 + i)];
          }
        }
    }
  }

  private void ddxt3db_sub(int paramInt, float[][][] paramArrayOfFloat, boolean paramBoolean)
  {
    int j;
    int k;
    int i;
    int m;
    if (paramInt == -1)
    {
      if (this.n3 > 2)
        for (j = 0; j < this.n2; j++)
          for (k = 0; k < this.n3; k += 4)
          {
            for (i = 0; i < this.n1; i++)
            {
              m = this.n1 + i;
              this.t[i] = paramArrayOfFloat[i][j][k];
              this.t[m] = paramArrayOfFloat[i][j][(k + 1)];
              this.t[(m + this.n1)] = paramArrayOfFloat[i][j][(k + 2)];
              this.t[(m + 2 * this.n1)] = paramArrayOfFloat[i][j][(k + 3)];
            }
            this.dctn1.forward(this.t, 0, paramBoolean);
            this.dctn1.forward(this.t, this.n1, paramBoolean);
            this.dctn1.forward(this.t, 2 * this.n1, paramBoolean);
            this.dctn1.forward(this.t, 3 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              m = this.n1 + i;
              paramArrayOfFloat[i][j][k] = this.t[i];
              paramArrayOfFloat[i][j][(k + 1)] = this.t[m];
              paramArrayOfFloat[i][j][(k + 2)] = this.t[(m + this.n1)];
              paramArrayOfFloat[i][j][(k + 3)] = this.t[(m + 2 * this.n1)];
            }
          }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            this.t[i] = paramArrayOfFloat[i][j][0];
            this.t[(this.n1 + i)] = paramArrayOfFloat[i][j][1];
          }
          this.dctn1.forward(this.t, 0, paramBoolean);
          this.dctn1.forward(this.t, this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            paramArrayOfFloat[i][j][0] = this.t[i];
            paramArrayOfFloat[i][j][1] = this.t[(this.n1 + i)];
          }
        }
    }
    else
    {
      if (this.n3 > 2)
        for (j = 0; j < this.n2; j++)
          for (k = 0; k < this.n3; k += 4)
          {
            for (i = 0; i < this.n1; i++)
            {
              m = this.n1 + i;
              this.t[i] = paramArrayOfFloat[i][j][k];
              this.t[m] = paramArrayOfFloat[i][j][(k + 1)];
              this.t[(m + this.n1)] = paramArrayOfFloat[i][j][(k + 2)];
              this.t[(m + 2 * this.n1)] = paramArrayOfFloat[i][j][(k + 3)];
            }
            this.dctn1.inverse(this.t, 0, paramBoolean);
            this.dctn1.inverse(this.t, this.n1, paramBoolean);
            this.dctn1.inverse(this.t, 2 * this.n1, paramBoolean);
            this.dctn1.inverse(this.t, 3 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              m = this.n1 + i;
              paramArrayOfFloat[i][j][k] = this.t[i];
              paramArrayOfFloat[i][j][(k + 1)] = this.t[m];
              paramArrayOfFloat[i][j][(k + 2)] = this.t[(m + this.n1)];
              paramArrayOfFloat[i][j][(k + 3)] = this.t[(m + 2 * this.n1)];
            }
          }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            this.t[i] = paramArrayOfFloat[i][j][0];
            this.t[(this.n1 + i)] = paramArrayOfFloat[i][j][1];
          }
          this.dctn1.inverse(this.t, 0, paramBoolean);
          this.dctn1.inverse(this.t, this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            paramArrayOfFloat[i][j][0] = this.t[i];
            paramArrayOfFloat[i][j][1] = this.t[(this.n1 + i)];
          }
        }
    }
  }

  private void ddxt3da_subth(final int paramInt, final float[] paramArrayOfFloat, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n1)
      i = this.n1;
    int j = 4 * this.n2;
    if (this.n3 == 2)
      j >>= 1;
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
          int i;
          int n;
          int i1;
          int j;
          int k;
          if (paramInt == -1)
          {
            m = n;
            while (m < FloatDCT_3D.this.n1)
            {
              i = m * FloatDCT_3D.this.sliceStride;
              for (n = 0; n < FloatDCT_3D.this.n2; n++)
                FloatDCT_3D.this.dctn3.forward(paramArrayOfFloat, i + n * FloatDCT_3D.this.rowStride, paramBoolean);
              if (FloatDCT_3D.this.n3 > 2)
              {
                for (n = 0; n < FloatDCT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < FloatDCT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDCT_3D.this.rowStride + n;
                    k = i1 + FloatDCT_3D.this.n2 + i1;
                    FloatDCT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                    FloatDCT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                    FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n2)] = paramArrayOfFloat[(j + 2)];
                    FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n2)] = paramArrayOfFloat[(j + 3)];
                  }
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n2, paramBoolean);
                  for (i1 = 0; i1 < FloatDCT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDCT_3D.this.rowStride + n;
                    k = i1 + FloatDCT_3D.this.n2 + i1;
                    paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + i1)];
                    paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[k];
                    paramArrayOfFloat[(j + 2)] = FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n2)];
                    paramArrayOfFloat[(j + 3)] = FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDCT_3D.this.n3 == 2)
              {
                for (n = 0; n < FloatDCT_3D.this.n2; n++)
                {
                  j = i + n * FloatDCT_3D.this.rowStride;
                  FloatDCT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                  FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + n)] = paramArrayOfFloat[(j + 1)];
                }
                FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                for (n = 0; n < FloatDCT_3D.this.n2; n++)
                {
                  j = i + n * FloatDCT_3D.this.rowStride;
                  paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + n)];
                  paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + n)];
                }
              }
              m += m;
            }
          }
          else
          {
            m = n;
            while (m < FloatDCT_3D.this.n1)
            {
              i = m * FloatDCT_3D.this.sliceStride;
              for (n = 0; n < FloatDCT_3D.this.n2; n++)
                FloatDCT_3D.this.dctn3.inverse(paramArrayOfFloat, i + n * FloatDCT_3D.this.rowStride, paramBoolean);
              if (FloatDCT_3D.this.n3 > 2)
              {
                for (n = 0; n < FloatDCT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < FloatDCT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDCT_3D.this.rowStride + n;
                    k = i1 + FloatDCT_3D.this.n2 + i1;
                    FloatDCT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                    FloatDCT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                    FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n2)] = paramArrayOfFloat[(j + 2)];
                    FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n2)] = paramArrayOfFloat[(j + 3)];
                  }
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n2, paramBoolean);
                  for (i1 = 0; i1 < FloatDCT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDCT_3D.this.rowStride + n;
                    k = i1 + FloatDCT_3D.this.n2 + i1;
                    paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + i1)];
                    paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[k];
                    paramArrayOfFloat[(j + 2)] = FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n2)];
                    paramArrayOfFloat[(j + 3)] = FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDCT_3D.this.n3 == 2)
              {
                for (n = 0; n < FloatDCT_3D.this.n2; n++)
                {
                  j = i + n * FloatDCT_3D.this.rowStride;
                  FloatDCT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                  FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + n)] = paramArrayOfFloat[(j + 1)];
                }
                FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                for (n = 0; n < FloatDCT_3D.this.n2; n++)
                {
                  j = i + n * FloatDCT_3D.this.rowStride;
                  paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + n)];
                  paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + n)];
                }
              }
              m += m;
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

  private void ddxt3da_subth(final int paramInt, final float[][][] paramArrayOfFloat, final boolean paramBoolean)
  {
    int m = ConcurrencyUtils.getNumberOfProcessors();
    final int i;
    if (m > this.n1)
      i = this.n1;
    else
      i = m;
    int j = 4 * this.n2;
    if (this.n3 == 2)
      j >>= 1;
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
          int i;
          if (paramInt == -1)
          {
            j = n;
            while (j < FloatDCT_3D.this.n1)
            {
              for (k = 0; k < FloatDCT_3D.this.n2; k++)
                FloatDCT_3D.this.dctn3.forward(paramArrayOfFloat[j][k], paramBoolean);
              if (FloatDCT_3D.this.n3 > 2)
              {
                for (k = 0; k < FloatDCT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < FloatDCT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDCT_3D.this.n2 + m;
                    FloatDCT_3D.this.t[(i1 + m)] = paramArrayOfFloat[j][m][k];
                    FloatDCT_3D.this.t[i] = paramArrayOfFloat[j][m][(k + 1)];
                    FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 2)];
                    FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 3)];
                  }
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n2, paramBoolean);
                  for (m = 0; m < FloatDCT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDCT_3D.this.n2 + m;
                    paramArrayOfFloat[j][m][k] = FloatDCT_3D.this.t[(i1 + m)];
                    paramArrayOfFloat[j][m][(k + 1)] = FloatDCT_3D.this.t[i];
                    paramArrayOfFloat[j][m][(k + 2)] = FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n2)];
                    paramArrayOfFloat[j][m][(k + 3)] = FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDCT_3D.this.n3 == 2)
              {
                for (k = 0; k < FloatDCT_3D.this.n2; k++)
                {
                  FloatDCT_3D.this.t[(i1 + k)] = paramArrayOfFloat[j][k][0];
                  FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + k)] = paramArrayOfFloat[j][k][1];
                }
                FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn2.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                for (k = 0; k < FloatDCT_3D.this.n2; k++)
                {
                  paramArrayOfFloat[j][k][0] = FloatDCT_3D.this.t[(i1 + k)];
                  paramArrayOfFloat[j][k][1] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + k)];
                }
              }
              j += i;
            }
          }
          else
          {
            j = n;
            while (j < FloatDCT_3D.this.n1)
            {
              for (k = 0; k < FloatDCT_3D.this.n2; k++)
                FloatDCT_3D.this.dctn3.inverse(paramArrayOfFloat[j][k], paramBoolean);
              if (FloatDCT_3D.this.n3 > 2)
              {
                for (k = 0; k < FloatDCT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < FloatDCT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDCT_3D.this.n2 + m;
                    FloatDCT_3D.this.t[(i1 + m)] = paramArrayOfFloat[j][m][k];
                    FloatDCT_3D.this.t[i] = paramArrayOfFloat[j][m][(k + 1)];
                    FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 2)];
                    FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 3)];
                  }
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n2, paramBoolean);
                  FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n2, paramBoolean);
                  for (m = 0; m < FloatDCT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDCT_3D.this.n2 + m;
                    paramArrayOfFloat[j][m][k] = FloatDCT_3D.this.t[(i1 + m)];
                    paramArrayOfFloat[j][m][(k + 1)] = FloatDCT_3D.this.t[i];
                    paramArrayOfFloat[j][m][(k + 2)] = FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n2)];
                    paramArrayOfFloat[j][m][(k + 3)] = FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDCT_3D.this.n3 == 2)
              {
                for (k = 0; k < FloatDCT_3D.this.n2; k++)
                {
                  FloatDCT_3D.this.t[(i1 + k)] = paramArrayOfFloat[j][k][0];
                  FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + k)] = paramArrayOfFloat[j][k][1];
                }
                FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn2.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n2, paramBoolean);
                for (k = 0; k < FloatDCT_3D.this.n2; k++)
                {
                  paramArrayOfFloat[j][k][0] = FloatDCT_3D.this.t[(i1 + k)];
                  paramArrayOfFloat[j][k][1] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n2 + k)];
                }
              }
              j += i;
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

  private void ddxt3db_subth(final int paramInt, final float[] paramArrayOfFloat, final boolean paramBoolean)
  {
    int i = ConcurrencyUtils.getNumberOfProcessors();
    if (i > this.n2)
      i = this.n2;
    int j = 4 * this.n1;
    if (this.n3 == 2)
      j >>= 1;
    Future[] arrayOfFuture = new Future[i];
    final int m = i;
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
          int i;
          int n;
          int i1;
          int j;
          int k;
          if (paramInt == -1)
          {
            if (FloatDCT_3D.this.n3 > 2)
            {
              m = n;
              while (m < FloatDCT_3D.this.n2)
              {
                i = m * FloatDCT_3D.this.rowStride;
                for (n = 0; n < FloatDCT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < FloatDCT_3D.this.n1; i1++)
                  {
                    j = i1 * FloatDCT_3D.this.sliceStride + i + n;
                    k = i1 + FloatDCT_3D.this.n1 + i1;
                    FloatDCT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                    FloatDCT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                    FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n1)] = paramArrayOfFloat[(j + 2)];
                    FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n1)] = paramArrayOfFloat[(j + 3)];
                  }
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n1, paramBoolean);
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n1, paramBoolean);
                  for (i1 = 0; i1 < FloatDCT_3D.this.n1; i1++)
                  {
                    j = i1 * FloatDCT_3D.this.sliceStride + i + n;
                    k = i1 + FloatDCT_3D.this.n1 + i1;
                    paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + i1)];
                    paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[k];
                    paramArrayOfFloat[(j + 2)] = FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n1)];
                    paramArrayOfFloat[(j + 3)] = FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n1)];
                  }
                }
                m += m;
              }
            }
            else if (FloatDCT_3D.this.n3 == 2)
            {
              m = n;
              while (m < FloatDCT_3D.this.n2)
              {
                i = m * FloatDCT_3D.this.rowStride;
                for (n = 0; n < FloatDCT_3D.this.n1; n++)
                {
                  j = n * FloatDCT_3D.this.sliceStride + i;
                  FloatDCT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                  FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + n)] = paramArrayOfFloat[(j + 1)];
                }
                FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
                for (n = 0; n < FloatDCT_3D.this.n1; n++)
                {
                  j = n * FloatDCT_3D.this.sliceStride + i;
                  paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + n)];
                  paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + n)];
                }
                m += m;
              }
            }
          }
          else if (FloatDCT_3D.this.n3 > 2)
          {
            m = n;
            while (m < FloatDCT_3D.this.n2)
            {
              i = m * FloatDCT_3D.this.rowStride;
              for (n = 0; n < FloatDCT_3D.this.n3; n += 4)
              {
                for (i1 = 0; i1 < FloatDCT_3D.this.n1; i1++)
                {
                  j = i1 * FloatDCT_3D.this.sliceStride + i + n;
                  k = i1 + FloatDCT_3D.this.n1 + i1;
                  FloatDCT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                  FloatDCT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                  FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n1)] = paramArrayOfFloat[(j + 2)];
                  FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n1)] = paramArrayOfFloat[(j + 3)];
                }
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n1, paramBoolean);
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n1, paramBoolean);
                for (i1 = 0; i1 < FloatDCT_3D.this.n1; i1++)
                {
                  j = i1 * FloatDCT_3D.this.sliceStride + i + n;
                  k = i1 + FloatDCT_3D.this.n1 + i1;
                  paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + i1)];
                  paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[k];
                  paramArrayOfFloat[(j + 2)] = FloatDCT_3D.this.t[(k + FloatDCT_3D.this.n1)];
                  paramArrayOfFloat[(j + 3)] = FloatDCT_3D.this.t[(k + 2 * FloatDCT_3D.this.n1)];
                }
              }
              m += m;
            }
          }
          else if (FloatDCT_3D.this.n3 == 2)
          {
            m = n;
            while (m < FloatDCT_3D.this.n2)
            {
              i = m * FloatDCT_3D.this.rowStride;
              for (n = 0; n < FloatDCT_3D.this.n1; n++)
              {
                j = n * FloatDCT_3D.this.sliceStride + i;
                FloatDCT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + n)] = paramArrayOfFloat[(j + 1)];
              }
              FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
              FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
              for (n = 0; n < FloatDCT_3D.this.n1; n++)
              {
                j = n * FloatDCT_3D.this.sliceStride + i;
                paramArrayOfFloat[j] = FloatDCT_3D.this.t[(i1 + n)];
                paramArrayOfFloat[(j + 1)] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + n)];
              }
              m += m;
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

  private void ddxt3db_subth(final int paramInt, final float[][][] paramArrayOfFloat, final boolean paramBoolean)
  {
    int m = ConcurrencyUtils.getNumberOfProcessors();
    final int i;
    if (m > this.n2)
      i = this.n2;
    else
      i = m;
    int j = 4 * this.n1;
    if (this.n3 == 2)
      j >>= 1;
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
          int i;
          if (paramInt == -1)
          {
            if (FloatDCT_3D.this.n3 > 2)
            {
              j = n;
              while (j < FloatDCT_3D.this.n2)
              {
                for (k = 0; k < FloatDCT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < FloatDCT_3D.this.n1; m++)
                  {
                    i = i1 + FloatDCT_3D.this.n1 + m;
                    FloatDCT_3D.this.t[(i1 + m)] = paramArrayOfFloat[m][j][k];
                    FloatDCT_3D.this.t[i] = paramArrayOfFloat[m][j][(k + 1)];
                    FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 2)];
                    FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 3)];
                  }
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n1, paramBoolean);
                  FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n1, paramBoolean);
                  for (m = 0; m < FloatDCT_3D.this.n1; m++)
                  {
                    i = i1 + FloatDCT_3D.this.n1 + m;
                    paramArrayOfFloat[m][j][k] = FloatDCT_3D.this.t[(i1 + m)];
                    paramArrayOfFloat[m][j][(k + 1)] = FloatDCT_3D.this.t[i];
                    paramArrayOfFloat[m][j][(k + 2)] = FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n1)];
                    paramArrayOfFloat[m][j][(k + 3)] = FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n1)];
                  }
                }
                j += i;
              }
            }
            else if (FloatDCT_3D.this.n3 == 2)
            {
              j = n;
              while (j < FloatDCT_3D.this.n2)
              {
                for (k = 0; k < FloatDCT_3D.this.n1; k++)
                {
                  FloatDCT_3D.this.t[(i1 + k)] = paramArrayOfFloat[k][j][0];
                  FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + k)] = paramArrayOfFloat[k][j][1];
                }
                FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn1.forward(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
                for (k = 0; k < FloatDCT_3D.this.n1; k++)
                {
                  paramArrayOfFloat[k][j][0] = FloatDCT_3D.this.t[(i1 + k)];
                  paramArrayOfFloat[k][j][1] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + k)];
                }
                j += i;
              }
            }
          }
          else if (FloatDCT_3D.this.n3 > 2)
          {
            j = n;
            while (j < FloatDCT_3D.this.n2)
            {
              for (k = 0; k < FloatDCT_3D.this.n3; k += 4)
              {
                for (m = 0; m < FloatDCT_3D.this.n1; m++)
                {
                  i = i1 + FloatDCT_3D.this.n1 + m;
                  FloatDCT_3D.this.t[(i1 + m)] = paramArrayOfFloat[m][j][k];
                  FloatDCT_3D.this.t[i] = paramArrayOfFloat[m][j][(k + 1)];
                  FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 2)];
                  FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 3)];
                }
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + 2 * FloatDCT_3D.this.n1, paramBoolean);
                FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + 3 * FloatDCT_3D.this.n1, paramBoolean);
                for (m = 0; m < FloatDCT_3D.this.n1; m++)
                {
                  i = i1 + FloatDCT_3D.this.n1 + m;
                  paramArrayOfFloat[m][j][k] = FloatDCT_3D.this.t[(i1 + m)];
                  paramArrayOfFloat[m][j][(k + 1)] = FloatDCT_3D.this.t[i];
                  paramArrayOfFloat[m][j][(k + 2)] = FloatDCT_3D.this.t[(i + FloatDCT_3D.this.n1)];
                  paramArrayOfFloat[m][j][(k + 3)] = FloatDCT_3D.this.t[(i + 2 * FloatDCT_3D.this.n1)];
                }
              }
              j += i;
            }
          }
          else if (FloatDCT_3D.this.n3 == 2)
          {
            j = n;
            while (j < FloatDCT_3D.this.n2)
            {
              for (k = 0; k < FloatDCT_3D.this.n1; k++)
              {
                FloatDCT_3D.this.t[(i1 + k)] = paramArrayOfFloat[k][j][0];
                FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + k)] = paramArrayOfFloat[k][j][1];
              }
              FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1, paramBoolean);
              FloatDCT_3D.this.dctn1.inverse(FloatDCT_3D.this.t, i1 + FloatDCT_3D.this.n1, paramBoolean);
              for (k = 0; k < FloatDCT_3D.this.n1; k++)
              {
                paramArrayOfFloat[k][j][0] = FloatDCT_3D.this.t[(i1 + k)];
                paramArrayOfFloat[k][j][1] = FloatDCT_3D.this.t[(i1 + FloatDCT_3D.this.n1 + k)];
              }
              j += i;
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
 * Qualified Name:     edu.emory.mathcs.jtransforms.dct.FloatDCT_3D
 * JD-Core Version:    0.6.1
 */