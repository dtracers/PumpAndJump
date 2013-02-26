package edu.emory.mathcs.jtransforms.dht;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DoubleDHT_3D
{
  private int n1;
  private int n2;
  private int n3;
  private int sliceStride;
  private int rowStride;
  private int[] ip;
  private double[] w;
  private double[] t;
  private DoubleDHT_1D dhtn1;
  private DoubleDHT_1D dhtn2;
  private DoubleDHT_1D dhtn3;
  private int oldNthread;
  private int nt;

  public DoubleDHT_3D(int paramInt1, int paramInt2, int paramInt3)
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
    this.w = new double[(int)Math.ceil(Math.max(Math.max(paramInt1 * 1.5D, paramInt2 * 1.5D), paramInt3 * 1.5D))];
    this.dhtn1 = new DoubleDHT_1D(paramInt1, this.ip, this.w);
    this.dhtn2 = new DoubleDHT_1D(paramInt2, this.ip, this.w);
    this.dhtn3 = new DoubleDHT_1D(paramInt3, this.ip, this.w);
    this.oldNthread = ConcurrencyUtils.getNumberOfProcessors();
    this.nt = paramInt1;
    if (this.nt < paramInt2)
      this.nt = paramInt2;
    this.nt *= 4;
    if (this.oldNthread > 1)
      this.nt *= this.oldNthread;
    if (paramInt3 == 2)
      this.nt >>= 1;
    this.t = new double[this.nt];
  }

  public void forward(double[] paramArrayOfDouble)
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
      this.t = new double[this.nt];
      this.oldNthread = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(-1, paramArrayOfDouble, true);
      ddxt3db_subth(-1, paramArrayOfDouble, true);
    }
    else
    {
      ddxt3da_sub(-1, paramArrayOfDouble, true);
      ddxt3db_sub(-1, paramArrayOfDouble, true);
    }
    y_transform(paramArrayOfDouble);
  }

  public void forward(double[][][] paramArrayOfDouble)
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
      this.t = new double[this.nt];
      this.oldNthread = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(-1, paramArrayOfDouble, true);
      ddxt3db_subth(-1, paramArrayOfDouble, true);
    }
    else
    {
      ddxt3da_sub(-1, paramArrayOfDouble, true);
      ddxt3db_sub(-1, paramArrayOfDouble, true);
    }
    y_transform(paramArrayOfDouble);
  }

  public void inverse(double[] paramArrayOfDouble, boolean paramBoolean)
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
      this.t = new double[this.nt];
      this.oldNthread = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(1, paramArrayOfDouble, paramBoolean);
      ddxt3db_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      ddxt3da_sub(1, paramArrayOfDouble, paramBoolean);
      ddxt3db_sub(1, paramArrayOfDouble, paramBoolean);
    }
    y_transform(paramArrayOfDouble);
  }

  public void inverse(double[][][] paramArrayOfDouble, boolean paramBoolean)
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
      this.t = new double[this.nt];
      this.oldNthread = m;
    }
    if ((m > 1) && (this.n1 * this.n2 * this.n3 >= ConcurrencyUtils.getThreadsBeginN_3D()))
    {
      ddxt3da_subth(1, paramArrayOfDouble, paramBoolean);
      ddxt3db_subth(1, paramArrayOfDouble, paramBoolean);
    }
    else
    {
      ddxt3da_sub(1, paramArrayOfDouble, paramBoolean);
      ddxt3db_sub(1, paramArrayOfDouble, paramBoolean);
    }
    y_transform(paramArrayOfDouble);
  }

  private void ddxt3da_sub(int paramInt, double[] paramArrayOfDouble, boolean paramBoolean)
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
          this.dhtn3.forward(paramArrayOfDouble, m + j * this.rowStride);
        if (this.n3 > 2)
          for (k = 0; k < this.n3; k += 4)
          {
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = this.n2 + j;
              this.t[j] = paramArrayOfDouble[n];
              this.t[i1] = paramArrayOfDouble[(n + 1)];
              this.t[(i1 + this.n2)] = paramArrayOfDouble[(n + 2)];
              this.t[(i1 + 2 * this.n2)] = paramArrayOfDouble[(n + 3)];
            }
            this.dhtn2.forward(this.t, 0);
            this.dhtn2.forward(this.t, this.n2);
            this.dhtn2.forward(this.t, 2 * this.n2);
            this.dhtn2.forward(this.t, 3 * this.n2);
            for (j = 0; j < this.n2; j++)
            {
              n = m + j * this.rowStride + k;
              i1 = this.n2 + j;
              paramArrayOfDouble[n] = this.t[j];
              paramArrayOfDouble[(n + 1)] = this.t[i1];
              paramArrayOfDouble[(n + 2)] = this.t[(i1 + this.n2)];
              paramArrayOfDouble[(n + 3)] = this.t[(i1 + 2 * this.n2)];
            }
          }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            this.t[j] = paramArrayOfDouble[n];
            this.t[(this.n2 + j)] = paramArrayOfDouble[(n + 1)];
          }
          this.dhtn2.forward(this.t, 0);
          this.dhtn2.forward(this.t, this.n2);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride;
            paramArrayOfDouble[n] = this.t[j];
            paramArrayOfDouble[(n + 1)] = this.t[(this.n2 + j)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      m = i * this.sliceStride;
      for (j = 0; j < this.n2; j++)
        this.dhtn3.inverse(paramArrayOfDouble, m + j * this.rowStride, paramBoolean);
      if (this.n3 > 2)
        for (k = 0; k < this.n3; k += 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = this.n2 + j;
            this.t[j] = paramArrayOfDouble[n];
            this.t[i1] = paramArrayOfDouble[(n + 1)];
            this.t[(i1 + this.n2)] = paramArrayOfDouble[(n + 2)];
            this.t[(i1 + 2 * this.n2)] = paramArrayOfDouble[(n + 3)];
          }
          this.dhtn2.inverse(this.t, 0, paramBoolean);
          this.dhtn2.inverse(this.t, this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 2 * this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 3 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            n = m + j * this.rowStride + k;
            i1 = this.n2 + j;
            paramArrayOfDouble[n] = this.t[j];
            paramArrayOfDouble[(n + 1)] = this.t[i1];
            paramArrayOfDouble[(n + 2)] = this.t[(i1 + this.n2)];
            paramArrayOfDouble[(n + 3)] = this.t[(i1 + 2 * this.n2)];
          }
        }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          this.t[j] = paramArrayOfDouble[n];
          this.t[(this.n2 + j)] = paramArrayOfDouble[(n + 1)];
        }
        this.dhtn2.inverse(this.t, 0, paramBoolean);
        this.dhtn2.inverse(this.t, this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          n = m + j * this.rowStride;
          paramArrayOfDouble[n] = this.t[j];
          paramArrayOfDouble[(n + 1)] = this.t[(this.n2 + j)];
        }
      }
    }
  }

  private void ddxt3da_sub(int paramInt, double[][][] paramArrayOfDouble, boolean paramBoolean)
  {
    int j;
    int k;
    int m;
    if (paramInt == -1)
      for (i = 0; i < this.n1; i++)
      {
        for (j = 0; j < this.n2; j++)
          this.dhtn3.forward(paramArrayOfDouble[i][j]);
        if (this.n3 > 2)
          for (k = 0; k < this.n3; k += 4)
          {
            for (j = 0; j < this.n2; j++)
            {
              m = this.n2 + j;
              this.t[j] = paramArrayOfDouble[i][j][k];
              this.t[m] = paramArrayOfDouble[i][j][(k + 1)];
              this.t[(m + this.n2)] = paramArrayOfDouble[i][j][(k + 2)];
              this.t[(m + 2 * this.n2)] = paramArrayOfDouble[i][j][(k + 3)];
            }
            this.dhtn2.forward(this.t, 0);
            this.dhtn2.forward(this.t, this.n2);
            this.dhtn2.forward(this.t, 2 * this.n2);
            this.dhtn2.forward(this.t, 3 * this.n2);
            for (j = 0; j < this.n2; j++)
            {
              m = this.n2 + j;
              paramArrayOfDouble[i][j][k] = this.t[j];
              paramArrayOfDouble[i][j][(k + 1)] = this.t[m];
              paramArrayOfDouble[i][j][(k + 2)] = this.t[(m + this.n2)];
              paramArrayOfDouble[i][j][(k + 3)] = this.t[(m + 2 * this.n2)];
            }
          }
        if (this.n3 == 2)
        {
          for (j = 0; j < this.n2; j++)
          {
            this.t[j] = paramArrayOfDouble[i][j][0];
            this.t[(this.n2 + j)] = paramArrayOfDouble[i][j][1];
          }
          this.dhtn2.forward(this.t, 0);
          this.dhtn2.forward(this.t, this.n2);
          for (j = 0; j < this.n2; j++)
          {
            paramArrayOfDouble[i][j][0] = this.t[j];
            paramArrayOfDouble[i][j][1] = this.t[(this.n2 + j)];
          }
        }
      }
    for (int i = 0; i < this.n1; i++)
    {
      for (j = 0; j < this.n2; j++)
        this.dhtn3.inverse(paramArrayOfDouble[i][j], paramBoolean);
      if (this.n3 > 2)
        for (k = 0; k < this.n3; k += 4)
        {
          for (j = 0; j < this.n2; j++)
          {
            m = this.n2 + j;
            this.t[j] = paramArrayOfDouble[i][j][k];
            this.t[m] = paramArrayOfDouble[i][j][(k + 1)];
            this.t[(m + this.n2)] = paramArrayOfDouble[i][j][(k + 2)];
            this.t[(m + 2 * this.n2)] = paramArrayOfDouble[i][j][(k + 3)];
          }
          this.dhtn2.inverse(this.t, 0, paramBoolean);
          this.dhtn2.inverse(this.t, this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 2 * this.n2, paramBoolean);
          this.dhtn2.inverse(this.t, 3 * this.n2, paramBoolean);
          for (j = 0; j < this.n2; j++)
          {
            m = this.n2 + j;
            paramArrayOfDouble[i][j][k] = this.t[j];
            paramArrayOfDouble[i][j][(k + 1)] = this.t[m];
            paramArrayOfDouble[i][j][(k + 2)] = this.t[(m + this.n2)];
            paramArrayOfDouble[i][j][(k + 3)] = this.t[(m + 2 * this.n2)];
          }
        }
      if (this.n3 == 2)
      {
        for (j = 0; j < this.n2; j++)
        {
          this.t[j] = paramArrayOfDouble[i][j][0];
          this.t[(this.n2 + j)] = paramArrayOfDouble[i][j][1];
        }
        this.dhtn2.inverse(this.t, 0, paramBoolean);
        this.dhtn2.inverse(this.t, this.n2, paramBoolean);
        for (j = 0; j < this.n2; j++)
        {
          paramArrayOfDouble[i][j][0] = this.t[j];
          paramArrayOfDouble[i][j][1] = this.t[(this.n2 + j)];
        }
      }
    }
  }

  private void ddxt3db_sub(int paramInt, double[] paramArrayOfDouble, boolean paramBoolean)
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
              this.t[i] = paramArrayOfDouble[n];
              this.t[i1] = paramArrayOfDouble[(n + 1)];
              this.t[(i1 + this.n1)] = paramArrayOfDouble[(n + 2)];
              this.t[(i1 + 2 * this.n1)] = paramArrayOfDouble[(n + 3)];
            }
            this.dhtn1.forward(this.t, 0);
            this.dhtn1.forward(this.t, this.n1);
            this.dhtn1.forward(this.t, 2 * this.n1);
            this.dhtn1.forward(this.t, 3 * this.n1);
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = this.n1 + i;
              paramArrayOfDouble[n] = this.t[i];
              paramArrayOfDouble[(n + 1)] = this.t[i1];
              paramArrayOfDouble[(n + 2)] = this.t[(i1 + this.n1)];
              paramArrayOfDouble[(n + 3)] = this.t[(i1 + 2 * this.n1)];
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
            this.t[i] = paramArrayOfDouble[n];
            this.t[(this.n1 + i)] = paramArrayOfDouble[(n + 1)];
          }
          this.dhtn1.forward(this.t, 0);
          this.dhtn1.forward(this.t, this.n1);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            paramArrayOfDouble[n] = this.t[i];
            paramArrayOfDouble[(n + 1)] = this.t[(this.n1 + i)];
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
              this.t[i] = paramArrayOfDouble[n];
              this.t[i1] = paramArrayOfDouble[(n + 1)];
              this.t[(i1 + this.n1)] = paramArrayOfDouble[(n + 2)];
              this.t[(i1 + 2 * this.n1)] = paramArrayOfDouble[(n + 3)];
            }
            this.dhtn1.inverse(this.t, 0, paramBoolean);
            this.dhtn1.inverse(this.t, this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 2 * this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 3 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              n = i * this.sliceStride + m + k;
              i1 = this.n1 + i;
              paramArrayOfDouble[n] = this.t[i];
              paramArrayOfDouble[(n + 1)] = this.t[i1];
              paramArrayOfDouble[(n + 2)] = this.t[(i1 + this.n1)];
              paramArrayOfDouble[(n + 3)] = this.t[(i1 + 2 * this.n1)];
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
            this.t[i] = paramArrayOfDouble[n];
            this.t[(this.n1 + i)] = paramArrayOfDouble[(n + 1)];
          }
          this.dhtn1.inverse(this.t, 0, paramBoolean);
          this.dhtn1.inverse(this.t, this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            n = i * this.sliceStride + m;
            paramArrayOfDouble[n] = this.t[i];
            paramArrayOfDouble[(n + 1)] = this.t[(this.n1 + i)];
          }
        }
    }
  }

  private void ddxt3db_sub(int paramInt, double[][][] paramArrayOfDouble, boolean paramBoolean)
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
              this.t[i] = paramArrayOfDouble[i][j][k];
              this.t[m] = paramArrayOfDouble[i][j][(k + 1)];
              this.t[(m + this.n1)] = paramArrayOfDouble[i][j][(k + 2)];
              this.t[(m + 2 * this.n1)] = paramArrayOfDouble[i][j][(k + 3)];
            }
            this.dhtn1.forward(this.t, 0);
            this.dhtn1.forward(this.t, this.n1);
            this.dhtn1.forward(this.t, 2 * this.n1);
            this.dhtn1.forward(this.t, 3 * this.n1);
            for (i = 0; i < this.n1; i++)
            {
              m = this.n1 + i;
              paramArrayOfDouble[i][j][k] = this.t[i];
              paramArrayOfDouble[i][j][(k + 1)] = this.t[m];
              paramArrayOfDouble[i][j][(k + 2)] = this.t[(m + this.n1)];
              paramArrayOfDouble[i][j][(k + 3)] = this.t[(m + 2 * this.n1)];
            }
          }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            this.t[i] = paramArrayOfDouble[i][j][0];
            this.t[(this.n1 + i)] = paramArrayOfDouble[i][j][1];
          }
          this.dhtn1.forward(this.t, 0);
          this.dhtn1.forward(this.t, this.n1);
          for (i = 0; i < this.n1; i++)
          {
            paramArrayOfDouble[i][j][0] = this.t[i];
            paramArrayOfDouble[i][j][1] = this.t[(this.n1 + i)];
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
              this.t[i] = paramArrayOfDouble[i][j][k];
              this.t[m] = paramArrayOfDouble[i][j][(k + 1)];
              this.t[(m + this.n1)] = paramArrayOfDouble[i][j][(k + 2)];
              this.t[(m + 2 * this.n1)] = paramArrayOfDouble[i][j][(k + 3)];
            }
            this.dhtn1.inverse(this.t, 0, paramBoolean);
            this.dhtn1.inverse(this.t, this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 2 * this.n1, paramBoolean);
            this.dhtn1.inverse(this.t, 3 * this.n1, paramBoolean);
            for (i = 0; i < this.n1; i++)
            {
              m = this.n1 + i;
              paramArrayOfDouble[i][j][k] = this.t[i];
              paramArrayOfDouble[i][j][(k + 1)] = this.t[m];
              paramArrayOfDouble[i][j][(k + 2)] = this.t[(m + this.n1)];
              paramArrayOfDouble[i][j][(k + 3)] = this.t[(m + 2 * this.n1)];
            }
          }
      if (this.n3 == 2)
        for (j = 0; j < this.n2; j++)
        {
          for (i = 0; i < this.n1; i++)
          {
            this.t[i] = paramArrayOfDouble[i][j][0];
            this.t[(this.n1 + i)] = paramArrayOfDouble[i][j][1];
          }
          this.dhtn1.inverse(this.t, 0, paramBoolean);
          this.dhtn1.inverse(this.t, this.n1, paramBoolean);
          for (i = 0; i < this.n1; i++)
          {
            paramArrayOfDouble[i][j][0] = this.t[i];
            paramArrayOfDouble[i][j][1] = this.t[(this.n1 + i)];
          }
        }
    }
  }

  private void ddxt3da_subth(final int paramInt, final double[] paramArrayOfDouble, final boolean paramBoolean)
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
            while (m < DoubleDHT_3D.this.n1)
            {
              i = m * DoubleDHT_3D.this.sliceStride;
              for (n = 0; n < DoubleDHT_3D.this.n2; n++)
                DoubleDHT_3D.this.dhtn3.forward(paramArrayOfDouble, i + n * DoubleDHT_3D.this.rowStride);
              if (DoubleDHT_3D.this.n3 > 2)
              {
                for (n = 0; n < DoubleDHT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < DoubleDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * DoubleDHT_3D.this.rowStride + n;
                    k = i1 + DoubleDHT_3D.this.n2 + i1;
                    DoubleDHT_3D.this.t[(i1 + i1)] = paramArrayOfDouble[j];
                    DoubleDHT_3D.this.t[k] = paramArrayOfDouble[(j + 1)];
                    DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n2)] = paramArrayOfDouble[(j + 2)];
                    DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n2)] = paramArrayOfDouble[(j + 3)];
                  }
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1);
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2);
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n2);
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n2);
                  for (i1 = 0; i1 < DoubleDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * DoubleDHT_3D.this.rowStride + n;
                    k = i1 + DoubleDHT_3D.this.n2 + i1;
                    paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + i1)];
                    paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[k];
                    paramArrayOfDouble[(j + 2)] = DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n2)];
                    paramArrayOfDouble[(j + 3)] = DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n2)];
                  }
                }
              }
              else if (DoubleDHT_3D.this.n3 == 2)
              {
                for (n = 0; n < DoubleDHT_3D.this.n2; n++)
                {
                  j = i + n * DoubleDHT_3D.this.rowStride;
                  DoubleDHT_3D.this.t[(i1 + n)] = paramArrayOfDouble[j];
                  DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + n)] = paramArrayOfDouble[(j + 1)];
                }
                DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1);
                DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2);
                for (n = 0; n < DoubleDHT_3D.this.n2; n++)
                {
                  j = i + n * DoubleDHT_3D.this.rowStride;
                  paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + n)];
                  paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + n)];
                }
              }
              m += m;
            }
          }
          else
          {
            m = n;
            while (m < DoubleDHT_3D.this.n1)
            {
              i = m * DoubleDHT_3D.this.sliceStride;
              for (n = 0; n < DoubleDHT_3D.this.n2; n++)
                DoubleDHT_3D.this.dhtn3.inverse(paramArrayOfDouble, i + n * DoubleDHT_3D.this.rowStride, paramBoolean);
              if (DoubleDHT_3D.this.n3 > 2)
              {
                for (n = 0; n < DoubleDHT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < DoubleDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * DoubleDHT_3D.this.rowStride + n;
                    k = i1 + DoubleDHT_3D.this.n2 + i1;
                    DoubleDHT_3D.this.t[(i1 + i1)] = paramArrayOfDouble[j];
                    DoubleDHT_3D.this.t[k] = paramArrayOfDouble[(j + 1)];
                    DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n2)] = paramArrayOfDouble[(j + 2)];
                    DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n2)] = paramArrayOfDouble[(j + 3)];
                  }
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2, paramBoolean);
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n2, paramBoolean);
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n2, paramBoolean);
                  for (i1 = 0; i1 < DoubleDHT_3D.this.n2; i1++)
                  {
                    j = i + i1 * DoubleDHT_3D.this.rowStride + n;
                    k = i1 + DoubleDHT_3D.this.n2 + i1;
                    paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + i1)];
                    paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[k];
                    paramArrayOfDouble[(j + 2)] = DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n2)];
                    paramArrayOfDouble[(j + 3)] = DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n2)];
                  }
                }
              }
              else if (DoubleDHT_3D.this.n3 == 2)
              {
                for (n = 0; n < DoubleDHT_3D.this.n2; n++)
                {
                  j = i + n * DoubleDHT_3D.this.rowStride;
                  DoubleDHT_3D.this.t[(i1 + n)] = paramArrayOfDouble[j];
                  DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + n)] = paramArrayOfDouble[(j + 1)];
                }
                DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
                DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2, paramBoolean);
                for (n = 0; n < DoubleDHT_3D.this.n2; n++)
                {
                  j = i + n * DoubleDHT_3D.this.rowStride;
                  paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + n)];
                  paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + n)];
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

  private void ddxt3da_subth(final int paramInt, final double[][][] paramArrayOfDouble, final boolean paramBoolean)
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
            while (j < DoubleDHT_3D.this.n1)
            {
              for (k = 0; k < DoubleDHT_3D.this.n2; k++)
                DoubleDHT_3D.this.dhtn3.forward(paramArrayOfDouble[j][k]);
              if (DoubleDHT_3D.this.n3 > 2)
              {
                for (k = 0; k < DoubleDHT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < DoubleDHT_3D.this.n2; m++)
                  {
                    i = i1 + DoubleDHT_3D.this.n2 + m;
                    DoubleDHT_3D.this.t[(i1 + m)] = paramArrayOfDouble[j][m][k];
                    DoubleDHT_3D.this.t[i] = paramArrayOfDouble[j][m][(k + 1)];
                    DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n2)] = paramArrayOfDouble[j][m][(k + 2)];
                    DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n2)] = paramArrayOfDouble[j][m][(k + 3)];
                  }
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1);
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2);
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n2);
                  DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n2);
                  for (m = 0; m < DoubleDHT_3D.this.n2; m++)
                  {
                    i = i1 + DoubleDHT_3D.this.n2 + m;
                    paramArrayOfDouble[j][m][k] = DoubleDHT_3D.this.t[(i1 + m)];
                    paramArrayOfDouble[j][m][(k + 1)] = DoubleDHT_3D.this.t[i];
                    paramArrayOfDouble[j][m][(k + 2)] = DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n2)];
                    paramArrayOfDouble[j][m][(k + 3)] = DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n2)];
                  }
                }
              }
              else if (DoubleDHT_3D.this.n3 == 2)
              {
                for (k = 0; k < DoubleDHT_3D.this.n2; k++)
                {
                  DoubleDHT_3D.this.t[(i1 + k)] = paramArrayOfDouble[j][k][0];
                  DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + k)] = paramArrayOfDouble[j][k][1];
                }
                DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1);
                DoubleDHT_3D.this.dhtn2.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2);
                for (k = 0; k < DoubleDHT_3D.this.n2; k++)
                {
                  paramArrayOfDouble[j][k][0] = DoubleDHT_3D.this.t[(i1 + k)];
                  paramArrayOfDouble[j][k][1] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + k)];
                }
              }
              j += m;
            }
          }
          else
          {
            j = n;
            while (j < DoubleDHT_3D.this.n1)
            {
              for (k = 0; k < DoubleDHT_3D.this.n2; k++)
                DoubleDHT_3D.this.dhtn3.inverse(paramArrayOfDouble[j][k], paramBoolean);
              if (DoubleDHT_3D.this.n3 > 2)
              {
                for (k = 0; k < DoubleDHT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < DoubleDHT_3D.this.n2; m++)
                  {
                    i = i1 + DoubleDHT_3D.this.n2 + m;
                    DoubleDHT_3D.this.t[(i1 + m)] = paramArrayOfDouble[j][m][k];
                    DoubleDHT_3D.this.t[i] = paramArrayOfDouble[j][m][(k + 1)];
                    DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n2)] = paramArrayOfDouble[j][m][(k + 2)];
                    DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n2)] = paramArrayOfDouble[j][m][(k + 3)];
                  }
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2, paramBoolean);
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n2, paramBoolean);
                  DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n2, paramBoolean);
                  for (m = 0; m < DoubleDHT_3D.this.n2; m++)
                  {
                    i = i1 + DoubleDHT_3D.this.n2 + m;
                    paramArrayOfDouble[j][m][k] = DoubleDHT_3D.this.t[(i1 + m)];
                    paramArrayOfDouble[j][m][(k + 1)] = DoubleDHT_3D.this.t[i];
                    paramArrayOfDouble[j][m][(k + 2)] = DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n2)];
                    paramArrayOfDouble[j][m][(k + 3)] = DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n2)];
                  }
                }
              }
              else if (DoubleDHT_3D.this.n3 == 2)
              {
                for (k = 0; k < DoubleDHT_3D.this.n2; k++)
                {
                  DoubleDHT_3D.this.t[(i1 + k)] = paramArrayOfDouble[j][k][0];
                  DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + k)] = paramArrayOfDouble[j][k][1];
                }
                DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
                DoubleDHT_3D.this.dhtn2.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n2, paramBoolean);
                for (k = 0; k < DoubleDHT_3D.this.n2; k++)
                {
                  paramArrayOfDouble[j][k][0] = DoubleDHT_3D.this.t[(i1 + k)];
                  paramArrayOfDouble[j][k][1] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n2 + k)];
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

  private void ddxt3db_subth(final int paramInt, final double[] paramArrayOfDouble, final boolean paramBoolean)
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
            if (DoubleDHT_3D.this.n3 > 2)
            {
              m = n;
              while (m < DoubleDHT_3D.this.n2)
              {
                i = m * DoubleDHT_3D.this.rowStride;
                for (n = 0; n < DoubleDHT_3D.this.n3; n += 4)
                {
                  for (i1 = 0; i1 < DoubleDHT_3D.this.n1; i1++)
                  {
                    j = i1 * DoubleDHT_3D.this.sliceStride + i + n;
                    k = i1 + DoubleDHT_3D.this.n1 + i1;
                    DoubleDHT_3D.this.t[(i1 + i1)] = paramArrayOfDouble[j];
                    DoubleDHT_3D.this.t[k] = paramArrayOfDouble[(j + 1)];
                    DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n1)] = paramArrayOfDouble[(j + 2)];
                    DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n1)] = paramArrayOfDouble[(j + 3)];
                  }
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1);
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1);
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n1);
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n1);
                  for (i1 = 0; i1 < DoubleDHT_3D.this.n1; i1++)
                  {
                    j = i1 * DoubleDHT_3D.this.sliceStride + i + n;
                    k = i1 + DoubleDHT_3D.this.n1 + i1;
                    paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + i1)];
                    paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[k];
                    paramArrayOfDouble[(j + 2)] = DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n1)];
                    paramArrayOfDouble[(j + 3)] = DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n1)];
                  }
                }
                m += m;
              }
            }
            else if (DoubleDHT_3D.this.n3 == 2)
            {
              m = n;
              while (m < DoubleDHT_3D.this.n2)
              {
                i = m * DoubleDHT_3D.this.rowStride;
                for (n = 0; n < DoubleDHT_3D.this.n1; n++)
                {
                  j = n * DoubleDHT_3D.this.sliceStride + i;
                  DoubleDHT_3D.this.t[(i1 + n)] = paramArrayOfDouble[j];
                  DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + n)] = paramArrayOfDouble[(j + 1)];
                }
                DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1);
                DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1);
                for (n = 0; n < DoubleDHT_3D.this.n1; n++)
                {
                  j = n * DoubleDHT_3D.this.sliceStride + i;
                  paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + n)];
                  paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + n)];
                }
                m += m;
              }
            }
          }
          else if (DoubleDHT_3D.this.n3 > 2)
          {
            m = n;
            while (m < DoubleDHT_3D.this.n2)
            {
              i = m * DoubleDHT_3D.this.rowStride;
              for (n = 0; n < DoubleDHT_3D.this.n3; n += 4)
              {
                for (i1 = 0; i1 < DoubleDHT_3D.this.n1; i1++)
                {
                  j = i1 * DoubleDHT_3D.this.sliceStride + i + n;
                  k = i1 + DoubleDHT_3D.this.n1 + i1;
                  DoubleDHT_3D.this.t[(i1 + i1)] = paramArrayOfDouble[j];
                  DoubleDHT_3D.this.t[k] = paramArrayOfDouble[(j + 1)];
                  DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n1)] = paramArrayOfDouble[(j + 2)];
                  DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n1)] = paramArrayOfDouble[(j + 3)];
                }
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1, paramBoolean);
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n1, paramBoolean);
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n1, paramBoolean);
                for (i1 = 0; i1 < DoubleDHT_3D.this.n1; i1++)
                {
                  j = i1 * DoubleDHT_3D.this.sliceStride + i + n;
                  k = i1 + DoubleDHT_3D.this.n1 + i1;
                  paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + i1)];
                  paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[k];
                  paramArrayOfDouble[(j + 2)] = DoubleDHT_3D.this.t[(k + DoubleDHT_3D.this.n1)];
                  paramArrayOfDouble[(j + 3)] = DoubleDHT_3D.this.t[(k + 2 * DoubleDHT_3D.this.n1)];
                }
              }
              m += m;
            }
          }
          else if (DoubleDHT_3D.this.n3 == 2)
          {
            m = n;
            while (m < DoubleDHT_3D.this.n2)
            {
              i = m * DoubleDHT_3D.this.rowStride;
              for (n = 0; n < DoubleDHT_3D.this.n1; n++)
              {
                j = n * DoubleDHT_3D.this.sliceStride + i;
                DoubleDHT_3D.this.t[(i1 + n)] = paramArrayOfDouble[j];
                DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + n)] = paramArrayOfDouble[(j + 1)];
              }
              DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
              DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1, paramBoolean);
              for (n = 0; n < DoubleDHT_3D.this.n1; n++)
              {
                j = n * DoubleDHT_3D.this.sliceStride + i;
                paramArrayOfDouble[j] = DoubleDHT_3D.this.t[(i1 + n)];
                paramArrayOfDouble[(j + 1)] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + n)];
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

  private void ddxt3db_subth(final int paramInt, final double[][][] paramArrayOfDouble, final boolean paramBoolean)
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
            if (DoubleDHT_3D.this.n3 > 2)
            {
              j = n;
              while (j < DoubleDHT_3D.this.n2)
              {
                for (k = 0; k < DoubleDHT_3D.this.n3; k += 4)
                {
                  for (m = 0; m < DoubleDHT_3D.this.n1; m++)
                  {
                    i = i1 + DoubleDHT_3D.this.n1 + m;
                    DoubleDHT_3D.this.t[(i1 + m)] = paramArrayOfDouble[m][j][k];
                    DoubleDHT_3D.this.t[i] = paramArrayOfDouble[m][j][(k + 1)];
                    DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n1)] = paramArrayOfDouble[m][j][(k + 2)];
                    DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n1)] = paramArrayOfDouble[m][j][(k + 3)];
                  }
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1);
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1);
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n1);
                  DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n1);
                  for (m = 0; m < DoubleDHT_3D.this.n1; m++)
                  {
                    i = i1 + DoubleDHT_3D.this.n1 + m;
                    paramArrayOfDouble[m][j][k] = DoubleDHT_3D.this.t[(i1 + m)];
                    paramArrayOfDouble[m][j][(k + 1)] = DoubleDHT_3D.this.t[i];
                    paramArrayOfDouble[m][j][(k + 2)] = DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n1)];
                    paramArrayOfDouble[m][j][(k + 3)] = DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n1)];
                  }
                }
                j += m;
              }
            }
            else if (DoubleDHT_3D.this.n3 == 2)
            {
              j = n;
              while (j < DoubleDHT_3D.this.n2)
              {
                for (k = 0; k < DoubleDHT_3D.this.n1; k++)
                {
                  DoubleDHT_3D.this.t[(i1 + k)] = paramArrayOfDouble[k][j][0];
                  DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + k)] = paramArrayOfDouble[k][j][1];
                }
                DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1);
                DoubleDHT_3D.this.dhtn1.forward(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1);
                for (k = 0; k < DoubleDHT_3D.this.n1; k++)
                {
                  paramArrayOfDouble[k][j][0] = DoubleDHT_3D.this.t[(i1 + k)];
                  paramArrayOfDouble[k][j][1] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + k)];
                }
                j += m;
              }
            }
          }
          else if (DoubleDHT_3D.this.n3 > 2)
          {
            j = n;
            while (j < DoubleDHT_3D.this.n2)
            {
              for (k = 0; k < DoubleDHT_3D.this.n3; k += 4)
              {
                for (m = 0; m < DoubleDHT_3D.this.n1; m++)
                {
                  i = i1 + DoubleDHT_3D.this.n1 + m;
                  DoubleDHT_3D.this.t[(i1 + m)] = paramArrayOfDouble[m][j][k];
                  DoubleDHT_3D.this.t[i] = paramArrayOfDouble[m][j][(k + 1)];
                  DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n1)] = paramArrayOfDouble[m][j][(k + 2)];
                  DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n1)] = paramArrayOfDouble[m][j][(k + 3)];
                }
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1, paramBoolean);
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + 2 * DoubleDHT_3D.this.n1, paramBoolean);
                DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + 3 * DoubleDHT_3D.this.n1, paramBoolean);
                for (m = 0; m < DoubleDHT_3D.this.n1; m++)
                {
                  i = i1 + DoubleDHT_3D.this.n1 + m;
                  paramArrayOfDouble[m][j][k] = DoubleDHT_3D.this.t[(i1 + m)];
                  paramArrayOfDouble[m][j][(k + 1)] = DoubleDHT_3D.this.t[i];
                  paramArrayOfDouble[m][j][(k + 2)] = DoubleDHT_3D.this.t[(i + DoubleDHT_3D.this.n1)];
                  paramArrayOfDouble[m][j][(k + 3)] = DoubleDHT_3D.this.t[(i + 2 * DoubleDHT_3D.this.n1)];
                }
              }
              j += m;
            }
          }
          else if (DoubleDHT_3D.this.n3 == 2)
          {
            j = n;
            while (j < DoubleDHT_3D.this.n2)
            {
              for (k = 0; k < DoubleDHT_3D.this.n1; k++)
              {
                DoubleDHT_3D.this.t[(i1 + k)] = paramArrayOfDouble[k][j][0];
                DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + k)] = paramArrayOfDouble[k][j][1];
              }
              DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1, paramBoolean);
              DoubleDHT_3D.this.dhtn1.inverse(DoubleDHT_3D.this.t, i1 + DoubleDHT_3D.this.n1, paramBoolean);
              for (k = 0; k < DoubleDHT_3D.this.n1; k++)
              {
                paramArrayOfDouble[k][j][0] = DoubleDHT_3D.this.t[(i1 + k)];
                paramArrayOfDouble[k][j][1] = DoubleDHT_3D.this.t[(i1 + DoubleDHT_3D.this.n1 + k)];
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

  private void y_transform(double[] paramArrayOfDouble)
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
          double d1 = paramArrayOfDouble[m];
          double d2 = paramArrayOfDouble[n];
          double d3 = paramArrayOfDouble[i1];
          double d4 = paramArrayOfDouble[i2];
          double d5 = paramArrayOfDouble[i3];
          double d6 = paramArrayOfDouble[i4];
          double d7 = paramArrayOfDouble[i5];
          double d8 = paramArrayOfDouble[i6];
          paramArrayOfDouble[i5] = ((d1 + d2 + d3 - d4) / 2.0D);
          paramArrayOfDouble[i1] = ((d5 + d6 + d7 - d8) / 2.0D);
          paramArrayOfDouble[m] = ((d7 + d8 + d5 - d6) / 2.0D);
          paramArrayOfDouble[i3] = ((d3 + d4 + d1 - d2) / 2.0D);
          paramArrayOfDouble[n] = ((d8 + d7 + d6 - d5) / 2.0D);
          paramArrayOfDouble[i4] = ((d4 + d3 + d2 - d1) / 2.0D);
          paramArrayOfDouble[i6] = ((d2 + d1 + d4 - d3) / 2.0D);
          paramArrayOfDouble[i2] = ((d6 + d5 + d8 - d7) / 2.0D);
        }
      }
    }
  }

  private void y_transform(double[][][] paramArrayOfDouble)
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
          double d1 = paramArrayOfDouble[m][j][i1];
          double d2 = paramArrayOfDouble[m][n][i];
          double d3 = paramArrayOfDouble[k][n][i1];
          double d4 = paramArrayOfDouble[k][j][i];
          double d5 = paramArrayOfDouble[k][j][i1];
          double d6 = paramArrayOfDouble[k][n][i];
          double d7 = paramArrayOfDouble[m][n][i1];
          double d8 = paramArrayOfDouble[m][j][i];
          paramArrayOfDouble[m][n][i1] = ((d1 + d2 + d3 - d4) / 2.0D);
          paramArrayOfDouble[k][n][i1] = ((d5 + d6 + d7 - d8) / 2.0D);
          paramArrayOfDouble[m][j][i1] = ((d7 + d8 + d5 - d6) / 2.0D);
          paramArrayOfDouble[k][j][i1] = ((d3 + d4 + d1 - d2) / 2.0D);
          paramArrayOfDouble[m][n][i] = ((d8 + d7 + d6 - d5) / 2.0D);
          paramArrayOfDouble[k][n][i] = ((d4 + d3 + d2 - d1) / 2.0D);
          paramArrayOfDouble[m][j][i] = ((d2 + d1 + d4 - d3) / 2.0D);
          paramArrayOfDouble[k][j][i] = ((d6 + d5 + d8 - d7) / 2.0D);
        }
      }
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dht.DoubleDHT_3D
 * JD-Core Version:    0.6.1
 */