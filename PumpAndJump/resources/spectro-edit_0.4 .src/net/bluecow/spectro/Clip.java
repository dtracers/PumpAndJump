/*     */ package net.bluecow.spectro;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ import javax.swing.event.UndoableEditListener;
/*     */ import javax.swing.undo.UndoableEditSupport;
/*     */ 
/*     */ public class Clip
/*     */ {
/*  43 */   private static final Logger logger = Logger.getLogger(Clip.class.getName());
/*     */ 
/*  49 */   private static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100.0F, 16, 1, true, true);
/*     */ 
/*  51 */   private final List<Frame> frames = new ArrayList();
/*     */ 
/*  57 */   private int frameSize = 1024;
/*     */ 
/*  64 */   private int overlap = 2;
/*     */ 
/*  70 */   private double spectralScale = 10000.0D;
/*     */   private ClipDataEdit currentEdit;
/*  77 */   private final UndoableEditSupport undoEventSupport = new UndoableEditSupport();
/*     */ 
/* 354 */   private final List<ClipDataChangeListener> clipDataChangeListeners = new ArrayList();
/*     */ 
/*     */   public Clip(File file)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/*  97 */     WindowFunction windowFunc = new VorbisWindowFunction(this.frameSize);
/*  98 */     AudioFormat desiredFormat = AUDIO_FORMAT;
/*  99 */     BufferedInputStream in = new BufferedInputStream(AudioFileUtils.readAsMono(desiredFormat, file));
/* 100 */     byte[] buf = new byte[this.frameSize * 2];
/*     */ 
/* 102 */     in.mark(buf.length * 2);
/*     */     int n;
/* 103 */     while ((n = readFully(in, buf)) != -1) {
/* 104 */       logger.finest("Read " + n + " bytes");
/* 105 */       if (n != buf.length)
/*     */       {
/* 107 */         logger.warning("Only read " + n + " of " + buf.length + " bytes at frame " + this.frames.size());
/*     */ 
/* 110 */         for (int i = n; i < buf.length; i++) {
/* 111 */           buf[i] = 0;
/*     */         }
/*     */       }
/* 114 */       double[] samples = new double[this.frameSize];
/* 115 */       for (int i = 0; i < this.frameSize; i++) {
/* 116 */         int hi = buf[(2 * i)];
/* 117 */         int low = buf[(2 * i + 1)] & 0xFF;
/* 118 */         int sampVal = hi << 8 | low;
/* 119 */         samples[i] = (sampVal / this.spectralScale);
/*     */       }
/*     */ 
/* 122 */       this.frames.add(new Frame(samples, windowFunc));
/* 123 */       in.reset();
/* 124 */       long bytesToSkip = this.frameSize * 2 / this.overlap;
/*     */       long bytesSkipped;
/* 126 */       if ((bytesSkipped = in.skip(bytesToSkip)) != bytesToSkip) {
/* 127 */         logger.info("Skipped " + bytesSkipped + " bytes, but wanted " + bytesToSkip + " at frame " + this.frames.size());
/*     */       }
/* 129 */       in.mark(buf.length * 2);
/*     */     }
/*     */ 
/* 132 */     logger.info(String.format("Read %d frames from %s (%d bytes). frameSize=%d overlap=%d\n", new Object[] { Integer.valueOf(this.frames.size()), file.getAbsolutePath(), Integer.valueOf(this.frames.size() * buf.length), Integer.valueOf(this.frameSize), Integer.valueOf(this.overlap) }));
/*     */   }
/*     */ 
/*     */   private int readFully(InputStream in, byte[] buf)
/*     */     throws IOException
/*     */   {
/* 147 */     int offset = 0;
/* 148 */     int length = buf.length;
/* 149 */     int bytesRead = 0;
/* 150 */     while ((offset < buf.length) && ((bytesRead = in.read(buf, offset, length)) != -1)) {
/* 151 */       logger.finest("read " + bytesRead + " bytes at offset " + offset);
/* 152 */       length -= bytesRead;
/* 153 */       offset += bytesRead;
/*     */     }
/* 155 */     if (offset > 0) {
/* 156 */       logger.fine("Returning " + offset + " bytes read into buf");
/* 157 */       return offset;
/*     */     }
/* 159 */     logger.fine("Returning EOF");
/* 160 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getFrameTimeSamples()
/*     */   {
/* 167 */     return this.frameSize;
/*     */   }
/*     */ 
/*     */   public int getFrameFreqSamples()
/*     */   {
/* 174 */     return this.frameSize;
/*     */   }
/*     */ 
/*     */   public int getFrameCount()
/*     */   {
/* 182 */     return this.frames.size();
/*     */   }
/*     */ 
/*     */   public Frame getFrame(int i)
/*     */   {
/* 193 */     return (Frame)this.frames.get(i);
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudio()
/*     */   {
/* 201 */     return getAudio(0);
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudio(int sample)
/*     */   {
/* 207 */     final int initialFrame = sample / getFrameTimeSamples();
/*     */ 
/* 209 */     InputStream audioData = new InputStream()
/*     */     {
/* 214 */       int nextFrame = initialFrame;
/*     */ 
/* 220 */       OverlapBuffer overlapBuffer = new OverlapBuffer(Clip.this.frameSize, Clip.this.overlap);
/*     */       int currentSample;
/* 224 */       boolean currentByteHigh = true;
/*     */ 
/* 226 */       int emptyFrameCount = 0;
/*     */ 
/*     */       public int available() throws IOException
/*     */       {
/* 230 */         return 2147483647;
/*     */       }
/*     */ 
/*     */       public int read() throws IOException
/*     */       {
/* 235 */         if (this.overlapBuffer.needsNewFrame()) {
/* 236 */           if (this.nextFrame < Clip.this.frames.size()) {
/* 237 */             Frame f = (Frame)Clip.this.frames.get(this.nextFrame++);
/* 238 */             this.overlapBuffer.addFrame(f.asTimeData());
/*     */           } else {
/* 240 */             this.overlapBuffer.addEmptyFrame();
/* 241 */             this.emptyFrameCount += 1;
/*     */           }
/*     */         }
/*     */ 
/* 245 */         if (this.emptyFrameCount >= Clip.this.overlap)
/* 246 */           return -1;
/* 247 */         if (this.currentByteHigh) {
/* 248 */           this.currentSample = (int)(this.overlapBuffer.next() * Clip.this.spectralScale);
/* 249 */           this.currentByteHigh = false;
/* 250 */           return this.currentSample >> 8 & 0xFF;
/*     */         }
/* 252 */         this.currentByteHigh = true;
/* 253 */         return this.currentSample & 0xFF;
/*     */       }
/*     */     };
/* 259 */     int length = getFrameCount() * getFrameTimeSamples() * (AUDIO_FORMAT.getSampleSizeInBits() / 8) / this.overlap;
/* 260 */     return new AudioInputStream(audioData, AUDIO_FORMAT, length);
/*     */   }
/*     */ 
/*     */   public void beginEdit(Rectangle region, String description)
/*     */   {
/* 283 */     if (this.currentEdit != null) {
/* 284 */       throw new IllegalStateException("Already in an edit: " + this.currentEdit);
/*     */     }
/* 286 */     this.currentEdit = new ClipDataEdit(this, region.x, region.y, region.width, region.height);
/*     */   }
/*     */ 
/*     */   public void endEdit()
/*     */   {
/* 296 */     if (this.currentEdit == null) {
/* 297 */       throw new IllegalStateException("No edit is in progress");
/*     */     }
/* 299 */     this.currentEdit.captureNewData();
/* 300 */     this.undoEventSupport.postEdit(this.currentEdit);
/* 301 */     regionChanged(this.currentEdit.getRegion());
/* 302 */     this.currentEdit = null;
/*     */   }
/*     */ 
/*     */   public void beginCompoundEdit(String presentationName)
/*     */   {
/* 320 */     this.undoEventSupport.beginUpdate();
/*     */   }
/*     */ 
/*     */   public void endCompoundEdit()
/*     */   {
/* 328 */     this.undoEventSupport.endUpdate();
/*     */   }
/*     */ 
/*     */   public void regionChanged(Rectangle region)
/*     */   {
/* 348 */     fireClipDataChangeEvent(region);
/*     */   }
/*     */ 
/*     */   public void addClipDataChangeListener(ClipDataChangeListener l)
/*     */   {
/* 358 */     this.clipDataChangeListeners.add(l);
/*     */   }
/*     */ 
/*     */   public void removeClipDataChangeListener(ClipDataChangeListener l) {
/* 362 */     this.clipDataChangeListeners.remove(l);
/*     */   }
/*     */ 
/*     */   private void fireClipDataChangeEvent(Rectangle region) {
/* 366 */     ClipDataChangeEvent e = new ClipDataChangeEvent(this, region);
/* 367 */     for (int i = this.clipDataChangeListeners.size() - 1; i >= 0; i--)
/* 368 */       ((ClipDataChangeListener)this.clipDataChangeListeners.get(i)).clipDataChanged(e);
/*     */   }
/*     */ 
/*     */   public void addUndoableEditListener(UndoableEditListener l)
/*     */   {
/* 376 */     this.undoEventSupport.addUndoableEditListener(l);
/*     */   }
/*     */ 
/*     */   public UndoableEditListener[] getUndoableEditListeners() {
/* 380 */     return this.undoEventSupport.getUndoableEditListeners();
/*     */   }
/*     */ 
/*     */   public void removeUndoableEditListener(UndoableEditListener l) {
/* 384 */     this.undoEventSupport.removeUndoableEditListener(l);
/*     */   }
/*     */ 
/*     */   public double getSamplingRate() {
/* 388 */     return AUDIO_FORMAT.getSampleRate();
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.Clip
 * JD-Core Version:    0.6.1
 */