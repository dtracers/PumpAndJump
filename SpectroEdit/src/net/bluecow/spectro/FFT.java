/*     */ package net.bluecow.spectro;
/*     */
/*     */ import java.io.PrintStream;
/*     */ import java.text.DecimalFormat;
/*     */
/*     */ class FFT
/*     */ {
/*     */   public static double[][] fft_1d(double[][] array)
/*     */   {
/*  42 */     int n = array.length;
/*  43 */     int ln = (int)(Math.log(n) / Math.log(2.0D) + 0.5D);
/*  44 */     int nv2 = n / 2;
/*  45 */     int j = 1;
/*  46 */     for (int i = 1; i < n; i++) {
/*  47 */       if (i < j) {
/*  48 */         double t_r = array[(i - 1)][0];
/*  49 */         double t_i = array[(i - 1)][1];
/*  50 */         array[(i - 1)][0] = array[(j - 1)][0];
/*  51 */         array[(i - 1)][1] = array[(j - 1)][1];
/*  52 */         array[(j - 1)][0] = t_r;
/*  53 */         array[(j - 1)][1] = t_i;
/*     */       }
/*  55 */       int k = nv2;
/*  56 */       while (k < j) {
/*  57 */         j -= k;
/*  58 */         k /= 2;
/*     */       }
/*  60 */       j += k;
/*     */     }
/*     */
/*  63 */     for (int l = 1; l <= ln; l++)
/*     */     {
/*  65 */       int le = (int)(Math.exp(l * Math.log(2.0D)) + 0.5D);
/*  66 */       int le1 = le / 2;
/*  67 */       double u_r = 1.0D;
/*  68 */       double u_i = 0.0D;
/*  69 */       double w_r = Math.cos(3.141592653589793D / le1);
/*  70 */       double w_i = -Math.sin(3.141592653589793D / le1);
/*  71 */       for (j = 1; j <= le1; j++)
/*     */       {
/*  73 */         for (int i = j; i <= n; i += le)
/*     */         {
/*  75 */           int ip = i + le1;
/*  76 */           double t_r = array[(ip - 1)][0] * u_r - u_i * array[(ip - 1)][1];
/*  77 */           double t_i = array[(ip - 1)][1] * u_r + u_i * array[(ip - 1)][0];
/*     */
/*  79 */           array[(i - 1)][0] -= t_r;
/*  80 */           array[(i - 1)][1] -= t_i;
/*     */
/*  82 */           array[(i - 1)][0] += t_r;
/*  83 */           array[(i - 1)][1] += t_i;
/*     */         }
/*  85 */         double t_r = u_r * w_r - w_i * u_i;
/*  86 */         u_i = w_r * u_i + w_i * u_r;
/*  87 */         u_r = t_r;
/*     */       }
/*     */     }
/*  90 */     return array;
/*     */   }
/*     */
/*     */   public static void main(String[] args)
/*     */     throws NumberFormatException
/*     */   {
/* 100 */     double[][] fftBuf = new double[args.length][2];
/* 101 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 103 */       fftBuf[i][0] = Double.parseDouble(args[i]);
/* 104 */       fftBuf[i][1] = 0.0D;
/*     */     }
/*     */
/* 107 */     printArray(fftBuf);
/* 108 */     fft_1d(fftBuf);
/* 109 */     printArray(fftBuf);
/* 110 */     fft_1d(fftBuf);
/* 111 */     scaleArray(fftBuf, 1.0D / fftBuf.length);
/* 112 */     reverseArray(fftBuf);
/* 113 */     printArray(fftBuf);
/*     */   }
/*     */
/*     */   public static void printArray(double[][] fftBuf) {
/* 117 */     DecimalFormat real = new DecimalFormat("0.000000000");
/* 118 */     real.setPositivePrefix(" ");
/* 119 */     DecimalFormat imag = new DecimalFormat("0.000000000");
/* 120 */     imag.setNegativePrefix(" - ");
/* 121 */     imag.setPositivePrefix(" + ");
/*     */
/* 123 */     System.out.println("{");
/* 124 */     for (int i = 0; i < fftBuf.length; i++) {
/* 125 */       System.out.print(" (");
/* 126 */       System.out.print(real.format(fftBuf[i][0]));
/* 127 */       System.out.print(imag.format(fftBuf[i][1]));
/* 128 */       System.out.println("i)");
/*     */     }
/* 130 */     System.out.println("}");
/*     */   }
/*     */
/*     */   public static void scaleArray(double[][] fftBuf, double scalar) {
/* 134 */     for (int i = 0; i < fftBuf.length; i++) {
/* 135 */       fftBuf[i][0] *= scalar;
/* 136 */       fftBuf[i][1] *= scalar;
/*     */     }
/*     */   }
/*     */
/*     */   public static void reverseArray(double[][] fftBuf) {
/* 141 */     int i = 1; for (int n = fftBuf.length; i < n / 2; i++) {
/* 142 */       double[] temp = fftBuf[i];
/* 143 */       fftBuf[i] = fftBuf[(n - i)];
/* 144 */       fftBuf[(n - i)] = temp;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.FFT
 * JD-Core Version:    0.6.1
 */