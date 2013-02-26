package edu.emory.mathcs.jtransforms.dht;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FloatDHT_3D
{
  private int n1;
  private int n2;
  private int n3;
  private int sliceStride;
  private int rowStride;
  private int[] ip;
  private float[] w;
  private float[] t;
  private FloatDHT_1D dhtn1;
  private FloatDHT_1D dhtn2;
  private FloatDHT_1D dhtn3;
  private int oldNthread;
  private int nt;

  public FloatDHT_3D(int paramInt1, int paramInt2, int paramInt3)
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
    this.dhtn1 = new FloatDHT_1D(paramInt1, this.ip, this.w);
    this.dhtn2 = new FloatDHT_1D(paramInt2, this.ip, this.w);
    this.dhtn3 = new FloatDHT_1D(paramInt3, this.ip, this.w);
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

  public void forward(float[] paramArrayOfFloat)
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
      ddxt3da_subth(-1, paramArrayOfFloat, true);
      ddxt3db_subth(-1, paramArrayOfFloat, true);
    }
    else
    {
      ddxt3da_sub(-1, paramArrayOfFloat, true);
      ddxt3db_sub(-1, paramArrayOfFloat, true);
    }
    y_transform(paramArrayOfFloat);
  }

  public void forward(float[][][] paramArrayOfFloat)
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
      ddxt3da_subth(-1, paramArrayOfFloat, true);
      ddxt3db_subth(-1, paramArrayOfFloat, true);
    }
    else
    {
      ddxt3da_sub(-1, paramArrayOfFloat, true);
      ddxt3db_sub(-1, paramArrayOfFloat, true);
    }
    y_transform(paramArrayOfFloat);
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
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt3da_sub(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_sub(1, paramArrayOfFloat, paramBoolean);
    }
    y_transform(paramArrayOfFloat);
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
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_subth(1, paramArrayOfFloat, paramBoolean);
    }
    else
    {
      ddxt3da_sub(1, paramArrayOfFloat, paramBoolean);
      ddxt3db_sub(1, paramArrayOfFloat, paramBoolean);
    }
    y_transform(paramArrayOfFloat);
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
          this.dhtn3.forward(paramArrayOfFloat, m + j * this.rowStride);
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
            this.dhtn2.forward(this.t, 0);
            this.dhtn2.forward(this.t, this.n2);
            this.dhtn2.forward(this.t, 2 * this.n2);
            this.dhtn2.forward(this.t, 3 * this.n2);
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
          this.dhtn2.forward(this.t, 0);
          this.dhtn2.forward(this.t, this.n2);
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
        this.dhtn3.inverse(paramArrayOfFloat, m + j * this.rowStride, paramBoolean);
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
          this.dhtn2.inverse(this.t, 0, paramBoolean);
          this.dhtn2.inverse(this.t, this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 2 * this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 3 * this.n2, paramBoolean);
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
        this.dhtn2.inverse(this.t, 0, paramBoolean);
        this.dhtn2.inverse(this.t, this.n2, paramBoolean);
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
          this.dhtn3.forward(paramArrayOfFloat[i][j]);
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
            this.dhtn2.forward(this.t, 0);
            this.dhtn2.forward(this.t, this.n2);
            this.dhtn2.forward(this.t, 2 * this.n2);
            this.dhtn2.forward(this.t, 3 * this.n2);
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
          this.dhtn2.forward(this.t, 0);
          this.dhtn2.forward(this.t, this.n2);
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
        this.dhtn3.inverse(paramArrayOfFloat[i][j], paramBoolean);
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
          this.dhtn2.inverse(this.t, 0, paramBoolean);
          this.dhtn2.inverse(this.t, this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 2 * this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 3 * this.n2, paramBoolean);
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
        this.dhtn2.inverse(this.t, 0, paramBoolean);
        this.dhtn2.inverse(this.t, this.n2, paramBoolean);
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
            this.dhtn1.forward(this.t, 0);
            this.dhtn1.forward(this.t, this.n1);
            this.dhtn1.forward(this.t, 2 * this.n1);
            this.dhtn1.forward(this.t, 3 * this.n1);
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
          this.dhtn1.forward(this.t, 0);
          this.dhtn1.forward(this.t, this.n1);
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
            this.dhtn1.inverse(this.t, 0, paramBoolean);
            this.dhtn1.inverse(this.t, this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 2 * this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 3 * this.n1, paramBoolean);
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
          this.dhtn1.inverse(this.t, 0, paramBoolean);
          this.dhtn1.inverse(this.t, this.n1, paramBoolean);
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
            this.dhtn1.forward(this.t, 0);
            this.dhtn1.forward(this.t, this.n1);
            this.dhtn1.forward(this.t, 2 * this.n1);
            this.dhtn1.forward(this.t, 3 * this.n1);
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
          this.dhtn1.forward(this.t, 0);
          this.dhtn1.forward(this.t, this.n1);
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
            this.dhtn1.inverse(this.t, 0, paramBoolean);
            this.dhtn1.inverse(this.t, this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 2 * this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 3 * this.n1, paramBoolean);
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
          this.dhtn1.inverse(this.t, 0, paramBoolean);
          this.dhtn1.inverse(this.t, this.n1, paramBoolean);
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
            while (m < FloatDHT_3D.this.n1)
            {
              i = m * FloatDHT_3D.this.sliceStride;
              for (n = 0; n < FloatDHT_3D.this.n2; n++)
                FloatDHT_3D.this.dhtn3.forward(paramArrayOfFloat, i + n * FloatDHT_3D.this.rowStride);
              if (FloatDHT_3D.this.n3 > 2)
              {
                for (n = 0; n < FloatDHT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < FloatDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDHT_3D.this.rowStride + n;
                    k = i1 + FloatDHT_3D.this.n2 + i1;
                    FloatDHT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                    FloatDHT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                    FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n2)] = paramArrayOfFloat[(j + 2)];
                    FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n2)] = paramArrayOfFloat[(j + 3)];
                  }
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1);
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2);
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n2);
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n2);
                  for (i1 = 0; i1 < FloatDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDHT_3D.this.rowStride + n;
                    k = i1 + FloatDHT_3D.this.n2 + i1;
                    paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + i1)];
                    paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[k];
                    paramArrayOfFloat[(j + 2)] = FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n2)];
                    paramArrayOfFloat[(j + 3)] = FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDHT_3D.this.n3 == 2)
              {
                for (n = 0; n < FloatDHT_3D.this.n2; n++)
                {
                  j = i + n * FloatDHT_3D.this.rowStride;
                  FloatDHT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                  FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + n)] = paramArrayOfFloat[(j + 1)];
                }
                FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1);
                FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2);
                for (n = 0; n < FloatDHT_3D.this.n2; n++)
                {
                  j = i + n * FloatDHT_3D.this.rowStride;
                  paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + n)];
                  paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + n)];
                }
              }
              m += m;
            }
          }
          else
          {
            m = n;
            while (m < FloatDHT_3D.this.n1)
            {
              i = m * FloatDHT_3D.this.sliceStride;
              for (n = 0; n < FloatDHT_3D.this.n2; n++)
                FloatDHT_3D.this.dhtn3.inverse(paramArrayOfFloat, i + n * FloatDHT_3D.this.rowStride, paramBoolean);
              if (FloatDHT_3D.this.n3 > 2)
              {
                for (n = 0; n < FloatDHT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < FloatDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDHT_3D.this.rowStride + n;
                    k = i1 + FloatDHT_3D.this.n2 + i1;
                    FloatDHT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                    FloatDHT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                    FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n2)] = paramArrayOfFloat[(j + 2)];
                    FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n2)] = paramArrayOfFloat[(j + 3)];
                  }
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2, paramBoolean);
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n2, paramBoolean);
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n2, paramBoolean);
                  for (i1 = 0; i1 < FloatDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * FloatDHT_3D.this.rowStride + n;
                    k = i1 + FloatDHT_3D.this.n2 + i1;
                    paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + i1)];
                    paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[k];
                    paramArrayOfFloat[(j + 2)] = FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n2)];
                    paramArrayOfFloat[(j + 3)] = FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDHT_3D.this.n3 == 2)
              {
                for (n = 0; n < FloatDHT_3D.this.n2; n++)
                {
                  j = i + n * FloatDHT_3D.this.rowStride;
                  FloatDHT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                  FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + n)] = paramArrayOfFloat[(j + 1)];
                }
                FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
                FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2, paramBoolean);
                for (n = 0; n < FloatDHT_3D.this.n2; n++)
                {
                  j = i + n * FloatDHT_3D.this.rowStride;
                  paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + n)];
                  paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + n)];
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
          int j;
          int k;
          int m;
          int i;
          if (paramInt == -1)
          {
            j = n;
            while (j < FloatDHT_3D.this.n1)
            {
              for (k = 0; k < FloatDHT_3D.this.n2; k++)
                FloatDHT_3D.this.dhtn3.forward(paramArrayOfFloat[j][k]);
              if (FloatDHT_3D.this.n3 > 2)
              {
                for (k = 0; k < FloatDHT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < FloatDHT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDHT_3D.this.n2 + m;
                    FloatDHT_3D.this.t[(i1 + m)] = paramArrayOfFloat[j][m][k];
                    FloatDHT_3D.this.t[i] = paramArrayOfFloat[j][m][(k + 1)];
                    FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 2)];
                    FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 3)];
                  }
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1);
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2);
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n2);
                  FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n2);
                  for (m = 0; m < FloatDHT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDHT_3D.this.n2 + m;
                    paramArrayOfFloat[j][m][k] = FloatDHT_3D.this.t[(i1 + m)];
                    paramArrayOfFloat[j][m][(k + 1)] = FloatDHT_3D.this.t[i];
                    paramArrayOfFloat[j][m][(k + 2)] = FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n2)];
                    paramArrayOfFloat[j][m][(k + 3)] = FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDHT_3D.this.n3 == 2)
              {
                for (k = 0; k < FloatDHT_3D.this.n2; k++)
                {
                  FloatDHT_3D.this.t[(i1 + k)] = paramArrayOfFloat[j][k][0];
                  FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + k)] = paramArrayOfFloat[j][k][1];
                }
                FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1);
                FloatDHT_3D.this.dhtn2.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2);
                for (k = 0; k < FloatDHT_3D.this.n2; k++)
                {
                  paramArrayOfFloat[j][k][0] = FloatDHT_3D.this.t[(i1 + k)];
                  paramArrayOfFloat[j][k][1] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + k)];
                }
              }
              j += m;
            }
          }
          else
          {
            j = n;
            while (j < FloatDHT_3D.this.n1)
            {
              for (k = 0; k < FloatDHT_3D.this.n2; k++)
                FloatDHT_3D.this.dhtn3.inverse(paramArrayOfFloat[j][k], paramBoolean);
              if (FloatDHT_3D.this.n3 > 2)
              {
                for (k = 0; k < FloatDHT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < FloatDHT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDHT_3D.this.n2 + m;
                    FloatDHT_3D.this.t[(i1 + m)] = paramArrayOfFloat[j][m][k];
                    FloatDHT_3D.this.t[i] = paramArrayOfFloat[j][m][(k + 1)];
                    FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 2)];
                    FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n2)] = paramArrayOfFloat[j][m][(k + 3)];
                  }
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2, paramBoolean);
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n2, paramBoolean);
                  FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n2, paramBoolean);
                  for (m = 0; m < FloatDHT_3D.this.n2; m++)
                  {
                    i = i1 + FloatDHT_3D.this.n2 + m;
                    paramArrayOfFloat[j][m][k] = FloatDHT_3D.this.t[(i1 + m)];
                    paramArrayOfFloat[j][m][(k + 1)] = FloatDHT_3D.this.t[i];
                    paramArrayOfFloat[j][m][(k + 2)] = FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n2)];
                    paramArrayOfFloat[j][m][(k + 3)] = FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n2)];
                  }
                }
              }
              else if (FloatDHT_3D.this.n3 == 2)
              {
                for (k = 0; k < FloatDHT_3D.this.n2; k++)
                {
                  FloatDHT_3D.this.t[(i1 + k)] = paramArrayOfFloat[j][k][0];
                  FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + k)] = paramArrayOfFloat[j][k][1];
                }
                FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
                FloatDHT_3D.this.dhtn2.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n2, paramBoolean);
                for (k = 0; k < FloatDHT_3D.this.n2; k++)
                {
                  paramArrayOfFloat[j][k][0] = FloatDHT_3D.this.t[(i1 + k)];
                  paramArrayOfFloat[j][k][1] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n2 + k)];
                }
              }
              j += m;
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
            if (FloatDHT_3D.this.n3 > 2)
            {
              m = n;
              while (m < FloatDHT_3D.this.n2)
              {
                i = m * FloatDHT_3D.this.rowStride;
                for (n = 0; n < FloatDHT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < FloatDHT_3D.this.n1; i1++)
                  {
                    j = i1 * FloatDHT_3D.this.sliceStride + i + n;
                    k = i1 + FloatDHT_3D.this.n1 + i1;
                    FloatDHT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                    FloatDHT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                    FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n1)] = paramArrayOfFloat[(j + 2)];
                    FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n1)] = paramArrayOfFloat[(j + 3)];
                  }
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1);
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1);
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n1);
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n1);
                  for (i1 = 0; i1 < FloatDHT_3D.this.n1; i1++)
                  {
                    j = i1 * FloatDHT_3D.this.sliceStride + i + n;
                    k = i1 + FloatDHT_3D.this.n1 + i1;
                    paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + i1)];
                    paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[k];
                    paramArrayOfFloat[(j + 2)] = FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n1)];
                    paramArrayOfFloat[(j + 3)] = FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n1)];
                  }
                }
                m += m;
              }
            }
            else if (FloatDHT_3D.this.n3 == 2)
            {
              m = n;
              while (m < FloatDHT_3D.this.n2)
              {
                i = m * FloatDHT_3D.this.rowStride;
                for (n = 0; n < FloatDHT_3D.this.n1; n++)
                {
                  j = n * FloatDHT_3D.this.sliceStride + i;
                  FloatDHT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                  FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + n)] = paramArrayOfFloat[(j + 1)];
                }
                FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1);
                FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1);
                for (n = 0; n < FloatDHT_3D.this.n1; n++)
                {
                  j = n * FloatDHT_3D.this.sliceStride + i;
                  paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + n)];
                  paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + n)];
                }
                m += m;
              }
            }
          }
          else if (FloatDHT_3D.this.n3 > 2)
          {
            m = n;
            while (m < FloatDHT_3D.this.n2)
            {
              i = m * FloatDHT_3D.this.rowStride;
              for (n = 0; n < FloatDHT_3D.this.n3; n += 4)
              {
                for (i1 = 0; i1 < FloatDHT_3D.this.n1; i1++)
                {
                  j = i1 * FloatDHT_3D.this.sliceStride + i + n;
                  k = i1 + FloatDHT_3D.this.n1 + i1;
                  FloatDHT_3D.this.t[(i1 + i1)] = paramArrayOfFloat[j];
                  FloatDHT_3D.this.t[k] = paramArrayOfFloat[(j + 1)];
                  FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n1)] = paramArrayOfFloat[(j + 2)];
                  FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n1)] = paramArrayOfFloat[(j + 3)];
                }
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1, paramBoolean);
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n1, paramBoolean);
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n1, paramBoolean);
                for (i1 = 0; i1 < FloatDHT_3D.this.n1; i1++)
                {
                  j = i1 * FloatDHT_3D.this.sliceStride + i + n;
                  k = i1 + FloatDHT_3D.this.n1 + i1;
                  paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + i1)];
                  paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[k];
                  paramArrayOfFloat[(j + 2)] = FloatDHT_3D.this.t[(k + FloatDHT_3D.this.n1)];
                  paramArrayOfFloat[(j + 3)] = FloatDHT_3D.this.t[(k + 2 * FloatDHT_3D.this.n1)];
                }
              }
              m += m;
            }
          }
          else if (FloatDHT_3D.this.n3 == 2)
          {
            m = n;
            while (m < FloatDHT_3D.this.n2)
            {
              i = m * FloatDHT_3D.this.rowStride;
              for (n = 0; n < FloatDHT_3D.this.n1; n++)
              {
                j = n * FloatDHT_3D.this.sliceStride + i;
                FloatDHT_3D.this.t[(i1 + n)] = paramArrayOfFloat[j];
                FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + n)] = paramArrayOfFloat[(j + 1)];
              }
              FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
              FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1, paramBoolean);
              for (n = 0; n < FloatDHT_3D.this.n1; n++)
              {
                j = n * FloatDHT_3D.this.sliceStride + i;
                paramArrayOfFloat[j] = FloatDHT_3D.this.t[(i1 + n)];
                paramArrayOfFloat[(j + 1)] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + n)];
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
          int j;
          int k;
          int m;
          int i;
          if (paramInt == -1)
          {
            if (FloatDHT_3D.this.n3 > 2)
            {
              j = n;
              while (j < FloatDHT_3D.this.n2)
              {
                for (k = 0; k < FloatDHT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < FloatDHT_3D.this.n1; m++)
                  {
                    i = i1 + FloatDHT_3D.this.n1 + m;
                    FloatDHT_3D.this.t[(i1 + m)] = paramArrayOfFloat[m][j][k];
                    FloatDHT_3D.this.t[i] = paramArrayOfFloat[m][j][(k + 1)];
                    FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 2)];
                    FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 3)];
                  }
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1);
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1);
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n1);
                  FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n1);
                  for (m = 0; m < FloatDHT_3D.this.n1; m++)
                  {
                    i = i1 + FloatDHT_3D.this.n1 + m;
                    paramArrayOfFloat[m][j][k] = FloatDHT_3D.this.t[(i1 + m)];
                    paramArrayOfFloat[m][j][(k + 1)] = FloatDHT_3D.this.t[i];
                    paramArrayOfFloat[m][j][(k + 2)] = FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n1)];
                    paramArrayOfFloat[m][j][(k + 3)] = FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n1)];
                  }
                }
                j += m;
              }
            }
            else if (FloatDHT_3D.this.n3 == 2)
            {
              j = n;
              while (j < FloatDHT_3D.this.n2)
              {
                for (k = 0; k < FloatDHT_3D.this.n1; k++)
                {
                  FloatDHT_3D.this.t[(i1 + k)] = paramArrayOfFloat[k][j][0];
                  FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + k)] = paramArrayOfFloat[k][j][1];
                }
                FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1);
                FloatDHT_3D.this.dhtn1.forward(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1);
                for (k = 0; k < FloatDHT_3D.this.n1; k++)
                {
                  paramArrayOfFloat[k][j][0] = FloatDHT_3D.this.t[(i1 + k)];
                  paramArrayOfFloat[k][j][1] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + k)];
                }
                j += m;
              }
            }
          }
          else if (FloatDHT_3D.this.n3 > 2)
          {
            j = n;
            while (j < FloatDHT_3D.this.n2)
            {
              for (k = 0; k < FloatDHT_3D.this.n3; k += 4)
              {
                for (m = 0; m < FloatDHT_3D.this.n1; m++)
                {
                  i = i1 + FloatDHT_3D.this.n1 + m;
                  FloatDHT_3D.this.t[(i1 + m)] = paramArrayOfFloat[m][j][k];
                  FloatDHT_3D.this.t[i] = paramArrayOfFloat[m][j][(k + 1)];
                  FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 2)];
                  FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n1)] = paramArrayOfFloat[m][j][(k + 3)];
                }
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1, paramBoolean);
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + 2 * FloatDHT_3D.this.n1, paramBoolean);
                FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + 3 * FloatDHT_3D.this.n1, paramBoolean);
                for (m = 0; m < FloatDHT_3D.this.n1; m++)
                {
                  i = i1 + FloatDHT_3D.this.n1 + m;
                  paramArrayOfFloat[m][j][k] = FloatDHT_3D.this.t[(i1 + m)];
                  paramArrayOfFloat[m][j][(k + 1)] = FloatDHT_3D.this.t[i];
                  paramArrayOfFloat[m][j][(k + 2)] = FloatDHT_3D.this.t[(i + FloatDHT_3D.this.n1)];
                  paramArrayOfFloat[m][j][(k + 3)] = FloatDHT_3D.this.t[(i + 2 * FloatDHT_3D.this.n1)];
                }
              }
              j += m;
            }
          }
          else if (FloatDHT_3D.this.n3 == 2)
          {
            j = n;
            while (j < FloatDHT_3D.this.n2)
            {
              for (k = 0; k < FloatDHT_3D.this.n1; k++)
              {
                FloatDHT_3D.this.t[(i1 + k)] = paramArrayOfFloat[k][j][0];
                FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + k)] = paramArrayOfFloat[k][j][1];
              }
              FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1, paramBoolean);
              FloatDHT_3D.this.dhtn1.inverse(FloatDHT_3D.this.t, i1 + FloatDHT_3D.this.n1, paramBoolean);
              for (k = 0; k < FloatDHT_3D.this.n1; k++)
              {
                paramArrayOfFloat[k][j][0] = FloatDHT_3D.this.t[(i1 + k)];
                paramArrayOfFloat[k][j][1] = FloatDHT_3D.this.t[(i1 + FloatDHT_3D.this.n1 + k)];
              }
              j += m;
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

  private void y_transform(float[] paramArrayOfFloat)
  {
    for (int i7 = 0; i7 <= this.n1 / 2; i7++)
    {
      int k = (this.n1 - i7) % this.n1;
      for (int i8 = 0; i8 <= this.n2 / 2; i8++)
      {
        int j = (this.n2 - i8) % this.n2;
        for (int i9 = 0; i9 <= this.n3 / 2; i9++)
        {
          int i = (this.n3 - i9) % this.n3;
          int m = i7 * this.sliceStride + j * this.rowStride + i9;
          int n = i7 * this.sliceStride + i8 * this.rowStride + i;
          int i1 = k * this.sliceStride + i8 * this.rowStride + i9;
          int i2 = k * this.sliceStride + j * this.rowStride + i;
          int i3 = k * this.sliceStride + j * this.rowStride + i9;
          int i4 = k * this.sliceStride + i8 * this.rowStride + i;
          int i5 = i7 * this.sliceStride + i8 * this.rowStride + i9;
          int i6 = i7 * this.sliceStride + j * this.rowStride + i;
          float f1 = paramArrayOfFloat[m];
          float f2 = paramArrayOfFloat[n];
          float f3 = paramArrayOfFloat[i1];
          float f4 = paramArrayOfFloat[i2];
          float f5 = paramArrayOfFloat[i3];
          float f6 = paramArrayOfFloat[i4];
          float f7 = paramArrayOfFloat[i5];
          float f8 = paramArrayOfFloat[i6];
          paramArrayOfFloat[i5] = ((f1 + f2 + f3 - f4) / 2.0F);
          paramArrayOfFloat[i1] = ((f5 + f6 + f7 - f8) / 2.0F);
          paramArrayOfFloat[m] = ((f7 + f8 + f5 - f6) / 2.0F);
          paramArrayOfFloat[i3] = ((f3 + f4 + f1 - f2) / 2.0F);
          paramArrayOfFloat[n] = ((f8 + f7 + f6 - f5) / 2.0F);
          paramArrayOfFloat[i4] = ((f4 + f3 + f2 - f1) / 2.0F);
          paramArrayOfFloat[i6] = ((f2 + f1 + f4 - f3) / 2.0F);
          paramArrayOfFloat[i2] = ((f6 + f5 + f8 - f7) / 2.0F);
        }
      }
    }
  }

  private void y_transform(float[][][] paramArrayOfFloat)
  {
    for (int m = 0; m <= this.n1 / 2; m++)
    {
      int k = (this.n1 - m) % this.n1;
      for (int n = 0; n <= this.n2 / 2; n++)
      {
        int j = (this.n2 - n) % this.n2;
        for (int i1 = 0; i1 <= this.n3 / 2; i1++)
        {
          int i = (this.n3 - i1) % this.n3;
          float f1 = paramArrayOfFloat[m][j][i1];
          float f2 = paramArrayOfFloat[m][n][i];
          float f3 = paramArrayOfFloat[k][n][i1];
          float f4 = paramArrayOfFloat[k][j][i];
          float f5 = paramArrayOfFloat[k][j][i1];
          float f6 = paramArrayOfFloat[k][n][i];
          float f7 = paramArrayOfFloat[m][n][i1];
          float f8 = paramArrayOfFloat[m][j][i];
          paramArrayOfFloat[m][n][i1] = ((f1 + f2 + f3 - f4) / 2.0F);
          paramArrayOfFloat[k][n][i1] = ((f5 + f6 + f7 - f8) / 2.0F);
          paramArrayOfFloat[m][j][i1] = ((f7 + f8 + f5 - f6) / 2.0F);
          paramArrayOfFloat[k][j][i1] = ((f3 + f4 + f1 - f2) / 2.0F);
          paramArrayOfFloat[m][n][i] = ((f8 + f7 + f6 - f5) / 2.0F);
          paramArrayOfFloat[k][n][i] = ((f4 + f3 + f2 - f1) / 2.0F);
          paramArrayOfFloat[m][j][i] = ((f2 + f1 + f4 - f3) / 2.0F);
          paramArrayOfFloat[k][j][i] = ((f6 + f5 + f8 - f7) / 2.0F);
        }
      }
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dht.FloatDHT_3D
 * JD-Core Version:    0.6.1
 */