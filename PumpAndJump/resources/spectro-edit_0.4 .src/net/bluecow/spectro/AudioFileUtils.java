/*     */ package net.bluecow.spectro;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sound.sampled.AudioFileFormat;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ 
/*     */ public class AudioFileUtils
/*     */ {
/*  32 */   private static final Logger logger = Logger.getLogger(AudioFileUtils.class.getName());
/*     */ 
/*     */   public static AudioInputStream readAsMono(final AudioFormat desiredFormat, File file)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/*  49 */     if (desiredFormat.getSampleSizeInBits() != 16) {
/*  50 */       throw new UnsupportedOperationException("Only 16-bit samples are supported at the moment (you requested " + desiredFormat.getSampleSizeInBits() + ")");
/*     */     }
/*     */ 
/*  54 */     if (desiredFormat.getChannels() != 1) {
/*  55 */       throw new UnsupportedOperationException("Desired number of channels should be 1 (you requested " + desiredFormat.getChannels() + ")");
/*     */     }
/*     */ 
/*  58 */     AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
/*  59 */     if (fileFormat.getFormat().getChannels() == 1)
/*  60 */       return AudioSystem.getAudioInputStream(desiredFormat, AudioSystem.getAudioInputStream(file));
/*  61 */     if (fileFormat.getFormat().getChannels() == 2) {
/*  62 */       AudioFormat stereoDesiredFormat = new AudioFormat(desiredFormat.getEncoding(), desiredFormat.getSampleRate(), 16, 2, 4, desiredFormat.getFrameRate(), desiredFormat.isBigEndian(), desiredFormat.properties());
/*     */ 
/*  71 */       AudioInputStream stereoIn = AudioSystem.getAudioInputStream(stereoDesiredFormat, AudioSystem.getAudioInputStream(file));
/*     */ 
/*  73 */       InputStream mixed = new InputStream()
/*     */       {
/*  75 */         byte[] monobuf = new byte[16384];
/*  76 */         int offset = 0;
/*  77 */         int length = 0;
/*     */ 
/*  79 */         long bytesRead = 0L;
/*     */ 
/*     */         public int read() throws IOException
/*     */         {
/*  83 */           if (this.offset < this.length) {
/*  84 */             this.bytesRead += 1L;
/*  85 */             return this.monobuf[(this.offset++)] & 0xFF;
/*     */           }
/*  87 */           this.length = this.val$stereoIn.read(this.monobuf);
/*  88 */           if (this.length <= 0) {
/*  89 */             AudioFileUtils.logger.fine("reached EOF on original input stream (read " + this.length + " bytes)");
/*  90 */             return -1;
/*     */           }
/*     */ 
/*  93 */           for (int i = 0; i < this.length; i += 4)
/*     */           {
/*     */             int rl;
/*     */             int lh;
/*     */             int ll;
/*     */             int rh;
/*     */             int rl;
/*  95 */             if (desiredFormat.isBigEndian()) {
/*  96 */               int lh = this.monobuf[(i + 0)];
/*  97 */               int ll = this.monobuf[(i + 1)] & 0xFF;
/*  98 */               int rh = this.monobuf[(i + 2)];
/*  99 */               rl = this.monobuf[(i + 3)] & 0xFF;
/*     */             } else {
/* 101 */               lh = this.monobuf[(i + 1)];
/* 102 */               ll = this.monobuf[(i + 0)] & 0xFF;
/* 103 */               rh = this.monobuf[(i + 3)];
/* 104 */               rl = this.monobuf[(i + 2)] & 0xFF;
/*     */             }
/* 106 */             int left = lh << 8 | ll;
/* 107 */             int right = rh << 8 | rl;
/* 108 */             int mixed = (left + right) / 2;
/* 109 */             if (desiredFormat.isBigEndian()) {
/* 110 */               this.monobuf[(i / 2 + 1)] = (byte)(mixed & 0xFF);
/* 111 */               this.monobuf[(i / 2 + 0)] = (byte)(mixed >> 8 & 0xFF);
/*     */             } else {
/* 113 */               this.monobuf[(i / 2 + 0)] = (byte)(mixed & 0xFF);
/* 114 */               this.monobuf[(i / 2 + 1)] = (byte)(mixed >> 8 & 0xFF);
/*     */             }
/*     */           }
/* 117 */           this.length /= 2;
/* 118 */           this.offset = 0;
/* 119 */           return this.monobuf[(this.offset++)] & 0xFF;
/*     */         }
/*     */ 
/*     */         public synchronized void mark(int readlimit)
/*     */         {
/* 124 */           throw new UnsupportedOperationException("Mark not supported");
/*     */         }
/*     */       };
/* 127 */       logger.info("Creating 1-channel mixed input stream from stereo source");
/* 128 */       return new AudioInputStream(mixed, desiredFormat, stereoIn.getFrameLength());
/*     */     }
/* 130 */     throw new UnsupportedAudioFileException("Unsupported number of channels: " + fileFormat.getFormat().getChannels());
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.AudioFileUtils
 * JD-Core Version:    0.6.1
 */