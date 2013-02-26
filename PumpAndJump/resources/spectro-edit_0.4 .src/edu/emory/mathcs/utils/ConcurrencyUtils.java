package edu.emory.mathcs.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ConcurrencyUtils
{
  public static ExecutorService threadPool = Executors.newCachedThreadPool(new CustomThreadFactory(new CustomExceptionHandler(null)));
  private static int THREADS_BEGIN_N_1D_FFT_2THREADS = 8192;
  private static int THREADS_BEGIN_N_1D_FFT_4THREADS = 65536;
  private static int THREADS_BEGIN_N_2D = 65536;
  private static int THREADS_BEGIN_N_3D = 65536;
  private static int np = concurrency();

  public static int concurrency()
  {
    int i = Runtime.getRuntime().availableProcessors();
    if (i > 1)
      return prevPow2(i);
    return 1;
  }

  public static int getNumberOfProcessors()
  {
    return np;
  }

  public static int setNumberOfProcessors(int paramInt)
  {
    if (isPowerOf2(paramInt))
      np = paramInt;
    else
      np = prevPow2(paramInt);
    return np;
  }

  public static int getThreadsBeginN_1D_FFT_2Threads()
  {
    return THREADS_BEGIN_N_1D_FFT_2THREADS;
  }

  public static int getThreadsBeginN_1D_FFT_4Threads()
  {
    return THREADS_BEGIN_N_1D_FFT_4THREADS;
  }

  public static int getThreadsBeginN_2D()
  {
    return THREADS_BEGIN_N_2D;
  }

  public static int getThreadsBeginN_3D()
  {
    return THREADS_BEGIN_N_3D;
  }

  public static void setThreadsBeginN_1D_FFT_2Threads(int paramInt)
  {
    if (paramInt < 512)
      THREADS_BEGIN_N_1D_FFT_2THREADS = 512;
    else
      THREADS_BEGIN_N_1D_FFT_2THREADS = paramInt;
  }

  public static void setThreadsBeginN_1D_FFT_4Threads(int paramInt)
  {
    if (paramInt < 512)
      THREADS_BEGIN_N_1D_FFT_4THREADS = 512;
    else
      THREADS_BEGIN_N_1D_FFT_4THREADS = paramInt;
  }

  public static void setThreadsBeginN_2D(int paramInt)
  {
    THREADS_BEGIN_N_2D = paramInt;
  }

  public static void setThreadsBeginN_3D(int paramInt)
  {
    THREADS_BEGIN_N_3D = paramInt;
  }

  public static void resetThreadsBeginN_FFT()
  {
    THREADS_BEGIN_N_1D_FFT_2THREADS = 8192;
    THREADS_BEGIN_N_1D_FFT_4THREADS = 65536;
  }

  public static void resetThreadsBeginN()
  {
    THREADS_BEGIN_N_2D = 65536;
    THREADS_BEGIN_N_3D = 65536;
  }

  public static int nextPow2(int paramInt)
  {
    if (paramInt < 1)
      throw new IllegalArgumentException("x must be greater or equal 1");
    paramInt |= paramInt >>> 1;
    paramInt |= paramInt >>> 2;
    paramInt |= paramInt >>> 4;
    paramInt |= paramInt >>> 8;
    paramInt |= paramInt >>> 16;
    paramInt |= paramInt >>> 32;
    return paramInt + 1;
  }

  public static int prevPow2(int paramInt)
  {
    if (paramInt < 1)
      throw new IllegalArgumentException("x must be greater or equal 1");
    return (int)Math.pow(2.0D, Math.floor(Math.log(paramInt) / Math.log(2.0D)));
  }

  public static boolean isPowerOf2(int paramInt)
  {
    if (paramInt <= 0)
      return false;
    return (paramInt & paramInt - 1) == 0;
  }

  private static class CustomThreadFactory
    implements ThreadFactory
  {
    private static final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
    private final Thread.UncaughtExceptionHandler handler;

    CustomThreadFactory(Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler)
    {
      this.handler = paramUncaughtExceptionHandler;
    }

    public Thread newThread(Runnable paramRunnable)
    {
      Thread localThread = defaultFactory.newThread(paramRunnable);
      localThread.setUncaughtExceptionHandler(this.handler);
      return localThread;
    }
  }

  private static class CustomExceptionHandler
    implements Thread.UncaughtExceptionHandler
  {
    public void uncaughtException(Thread paramThread, Throwable paramThrowable)
    {
      paramThrowable.printStackTrace();
    }
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.utils.ConcurrencyUtils
 * JD-Core Version:    0.6.1
 */