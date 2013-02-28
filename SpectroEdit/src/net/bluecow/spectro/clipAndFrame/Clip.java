package net.bluecow.spectro.clipAndFrame;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;

import ddf.minim.effects.BandPass;
import ddf.minim.effects.IIRFilter;

import net.bluecow.spectro.math.AudioFileUtils;
import net.bluecow.spectro.math.OverlapBuffer;
import net.bluecow.spectro.painting.ClipDataChangeEvent;
import net.bluecow.spectro.painting.ClipDataChangeListener;
import net.bluecow.spectro.painting.ClipDataEdit;
import net.bluecow.spectro.windowFunctions.NullWindowFunction;
import net.bluecow.spectro.windowFunctions.VorbisWindowFunction;
import net.bluecow.spectro.windowFunctions.WindowFunction;

public class Clip
{
	private static final Logger logger = Logger.getLogger(Clip.class.getName());

	private static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100.0F, 16, 1, true, true);

	private final List<Frame> frames = new ArrayList();

	private int frameSize = 1024;

	private int overlap = 2;

	private double spectralScale = 10000.0D;
	private ClipDataEdit currentEdit;
	private final UndoableEditSupport undoEventSupport = new UndoableEditSupport();

	private final List<ClipDataChangeListener> clipDataChangeListeners = new ArrayList<ClipDataChangeListener> ();

	private WindowFunction preWindowFunction = new VorbisWindowFunction(this.frameSize);;
	private WindowFunction postWindowFunction = new NullWindowFunction();
	private IIRFilter filter = new BandPass(100.0F,20.0F,44100.0F);

	/*
	public Clip(File file)
		    throws UnsupportedAudioFileException, IOException
	{
		WindowFunction windowFunc = new VorbisWindowFunction(this.frameSize);
		AudioFormat desiredFormat = AUDIO_FORMAT;
		BufferedInputStream in = new BufferedInputStream(AudioFileUtils.readAsMono(desiredFormat, file));
		byte[] buf = new byte[this.frameSize * 2];

		in.mark(buf.length * 2);
		int n;
		while ((n = readFully(in, buf)) != -1)
		{
			logger.finest("Read " + n + " bytes");
			if (n != buf.length)
			{
				logger.warning("Only read " + n + " of " + buf.length + " bytes at frame " + this.frames.size());

				for (int i = n; i < buf.length; i++)
				{
					buf[i] = 0;
				}
			}
			double[] samples = new double[this.frameSize];
			for (int i = 0; i < this.frameSize; i++)
			{
				int hi = buf[(2 * i)];
				int low = buf[(2 * i + 1)] & 0xFF;
				int sampVal = hi << 8 | low;
				samples[i] = (sampVal / this.spectralScale);
			}
			this.frames.add(new Frame(samples, windowFunc));
			in.reset();
			long bytesToSkip = this.frameSize * 2 / this.overlap;
			long bytesSkipped;
			if ((bytesSkipped = in.skip(bytesToSkip)) != bytesToSkip)
			{
				logger.info("Skipped " + bytesSkipped + " bytes, but wanted " + bytesToSkip + " at frame " + this.frames.size());
			}
			in.mark(buf.length * 2);
		}


		logger.info(String.format("Read %d frames from %s (%d bytes). frameSize=%d overlap=%d\n", new Object[] { Integer.valueOf(this.frames.size()), file.getAbsolutePath(), Integer.valueOf(this.frames.size() * buf.length), Integer.valueOf(this.frameSize), Integer.valueOf(this.overlap) }));
	}
	*/
	public Clip(File file)
		    throws UnsupportedAudioFileException, IOException
	{
		BufferedInputStream in = createInputStream(file);

		float[] wholeArray = readEntireArray(in);
		prefilter(wholeArray);
		int index = 0;
		ArrayList<double[]> backToBuffers = new ArrayList<double[]>();
		for(int k = 0; k<wholeArray.length/this.frameSize;k++)
		{
			double[] samples = new double[this.frameSize];
			for(int i = 0; i<samples.length; i++)
			{
				samples[i] =(double) wholeArray[index];
				index++;
			}
			backToBuffers.add(samples);
		}
		while(backToBuffers.size()>=1)
		{
			this.frames.add(new Frame(backToBuffers.remove(0), preWindowFunction,postWindowFunction));
		}
		logger.info(String.format("Read %d frames from %s (%d bytes). frameSize=%d overlap=%d\n", new Object[] { Integer.valueOf(this.frames.size()), file.getAbsolutePath(), Integer.valueOf(this.frames.size() * this.frameSize*2), Integer.valueOf(this.frameSize), Integer.valueOf(this.overlap) }));
	}

	/**
	 * Creates an input stream from the file and the desired audio format
	 * @param file
	 * @return
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	private BufferedInputStream createInputStream(File file) throws UnsupportedAudioFileException, IOException
	{
		AudioFormat desiredFormat = AUDIO_FORMAT;
		BufferedInputStream in = new BufferedInputStream(AudioFileUtils.readAsMono(desiredFormat, file));

		return in;
	}

	/**
	 * Puts the entire song into a single buffer (memory expensive yes but then the filter should work better)
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private float[] readEntireArray(BufferedInputStream in) throws IOException
	{
		ArrayList<double[]> buffers = new ArrayList<double[]>();
		byte[] buf = new byte[this.frameSize * 2];

		in.mark(buf.length * 2);
		int n;
		while ((n = readFully(in, buf)) != -1)
		{
			logger.finest("Read " + n + " bytes");
			if (n != buf.length)
			{
				logger.warning("Only read " + n + " of " + buf.length + " bytes at frame " + this.frames.size());

				for (int i = n; i < buf.length; i++)
				{
					buf[i] = 0;
				}
			}
			double[] samples = new double[this.frameSize];
			for (int i = 0; i < this.frameSize; i++)
			{
				int hi = buf[(2 * i)];
				int low = buf[(2 * i + 1)] & 0xFF;
				int sampVal = hi << 8 | low;
				samples[i] = (sampVal / this.spectralScale);
			}
			buffers.add(samples);
			//this.frames.add(new Frame(samples, windowFunc));
			in.reset();
			long bytesToSkip = this.frameSize * 2 / this.overlap;
			long bytesSkipped;
			if ((bytesSkipped = in.skip(bytesToSkip)) != bytesToSkip)
			{
				logger.info("Skipped " + bytesSkipped + " bytes, but wanted " + bytesToSkip + " at frame " + this.frames.size());
			}
			in.mark(buf.length * 2);
		}
		float[] totalBuffer = new float[buffers.size()*this.frameSize];
		int index = 0 ;

		//copy the entire thing over
		for(double[] tempBuffer:buffers)
		{
			for(int k=0;k<tempBuffer.length;k++)
			{
				totalBuffer[index] = (float)tempBuffer[k];
				index++;
			}
		}
		buffers.clear();
		buffers = null;
		System.gc();
		return totalBuffer;
	}

	/**
	 * runs the entire array through a filter to filter out certain sounds
	 * @param in
	 * @return
	 */
	private void prefilter(float[] input)
	{
		filter.process(input);
	}
	private int readFully(InputStream in, byte[] buf)
	    throws IOException
	{
		int offset = 0;
		int length = buf.length;
		int bytesRead = 0;
		while ((offset < buf.length) && ((bytesRead = in.read(buf, offset, length)) != -1))
		{
			length -= bytesRead;
			offset += bytesRead;
		}
		if (offset > 0) {
			logger.fine("Returning " + offset + " bytes read into buf");
			return offset;
		}
		logger.fine("Returning EOF");
		return -1;
	}

	  public int getFrameTimeSamples()
	  {
	    return this.frameSize;
	  }

	  public int getFrameFreqSamples()
	  {
	    return this.frameSize;
	  }

	  public int getFrameCount()
	  {
	    return this.frames.size();
	  }

	  public Frame getFrame(int i)
	  {
	    return (Frame)this.frames.get(i);
	  }

	  public AudioInputStream getAudio()
	  {
	    return getAudio(0);
	  }

	  public AudioInputStream getAudio(int sample)
	  {
	    final int initialFrame = sample / getFrameTimeSamples();

	    InputStream audioData = new InputStream()
	    {
	      int nextFrame = initialFrame;

	      OverlapBuffer overlapBuffer = new OverlapBuffer(Clip.this.frameSize, Clip.this.overlap);
	      int currentSample;
	      boolean currentByteHigh = true;

	      int emptyFrameCount = 0;

	      public int available() throws IOException
	      {
	        return 2147483647;
	      }

	      public int read() throws IOException
	      {
	        if (this.overlapBuffer.needsNewFrame()) {
	          if (this.nextFrame < Clip.this.frames.size()) {
	            Frame f = (Frame)Clip.this.frames.get(this.nextFrame++);
	            this.overlapBuffer.addFrame(f.asTimeData());
	          } else {
	            this.overlapBuffer.addEmptyFrame();
	            this.emptyFrameCount += 1;
	          }
	        }

	        if (this.emptyFrameCount >= Clip.this.overlap)
	          return -1;
	        if (this.currentByteHigh) {
	          this.currentSample = (int)(this.overlapBuffer.next() * Clip.this.spectralScale);
	          this.currentByteHigh = false;
	          return this.currentSample >> 8 & 0xFF;
	        }
	        this.currentByteHigh = true;
	        return this.currentSample & 0xFF;
	      }
	    };
	    int length = getFrameCount() * getFrameTimeSamples() * (AUDIO_FORMAT.getSampleSizeInBits() / 8) / this.overlap;
	    return new AudioInputStream(audioData, AUDIO_FORMAT, length);
	  }

	  public void beginEdit(Rectangle region, String description)
	  {
	    if (this.currentEdit != null) {
	      throw new IllegalStateException("Already in an edit: " + this.currentEdit);
	    }
	    this.currentEdit = new ClipDataEdit(this, region.x, region.y, region.width, region.height);
	  }

	  public void endEdit()
	  {
	    if (this.currentEdit == null) {
	      throw new IllegalStateException("No edit is in progress");
	    }
	    this.currentEdit.captureNewData();
	    this.undoEventSupport.postEdit(this.currentEdit);
	    regionChanged(this.currentEdit.getRegion());
	    this.currentEdit = null;
	  }

	  public void beginCompoundEdit(String presentationName)
	  {
	    this.undoEventSupport.beginUpdate();
	  }

	  public void endCompoundEdit()
	  {
	    this.undoEventSupport.endUpdate();
	  }

	  public void regionChanged(Rectangle region)
	  {
	    fireClipDataChangeEvent(region);
	  }

	  public void addClipDataChangeListener(ClipDataChangeListener l)
	  {
	    this.clipDataChangeListeners.add(l);
	  }

	  public void removeClipDataChangeListener(ClipDataChangeListener l) {
	    this.clipDataChangeListeners.remove(l);
	  }

	  private void fireClipDataChangeEvent(Rectangle region) {
	    ClipDataChangeEvent e = new ClipDataChangeEvent(this, region);
	    for (int i = this.clipDataChangeListeners.size() - 1; i >= 0; i--)
	      ((ClipDataChangeListener)this.clipDataChangeListeners.get(i)).clipDataChanged(e);
	  }

	  public void addUndoableEditListener(UndoableEditListener l)
	  {
	    this.undoEventSupport.addUndoableEditListener(l);
	  }

	  public UndoableEditListener[] getUndoableEditListeners() {
	    return this.undoEventSupport.getUndoableEditListeners();
	  }

	  public void removeUndoableEditListener(UndoableEditListener l) {
	    this.undoEventSupport.removeUndoableEditListener(l);
	  }

	  public double getSamplingRate() {
	    return AUDIO_FORMAT.getSampleRate();
	  }
	}