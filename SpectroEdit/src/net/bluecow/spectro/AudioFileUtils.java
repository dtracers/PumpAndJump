package net.bluecow.spectro;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFileUtils
{
  private static final Logger logger = Logger.getLogger(AudioFileUtils.class.getName());

  public static AudioInputStream readAsMono(final AudioFormat desiredFormat, File file)
    throws UnsupportedAudioFileException, IOException
  {
    if (desiredFormat.getSampleSizeInBits() != 16) {
      throw new UnsupportedOperationException("Only 16-bit samples are supported at the moment (you requested " + desiredFormat.getSampleSizeInBits() + ")");
    }

    if (desiredFormat.getChannels() != 1) {
      throw new UnsupportedOperationException("Desired number of channels should be 1 (you requested " + desiredFormat.getChannels() + ")");
    }

    AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
    if (fileFormat.getFormat().getChannels() == 1)
      return AudioSystem.getAudioInputStream(desiredFormat, AudioSystem.getAudioInputStream(file));
    if (fileFormat.getFormat().getChannels() == 2) {
      AudioFormat stereoDesiredFormat = new AudioFormat(desiredFormat.getEncoding(), desiredFormat.getSampleRate(), 16, 2, 4, desiredFormat.getFrameRate(), desiredFormat.isBigEndian(), desiredFormat.properties());

      final AudioInputStream stereoIn = AudioSystem.getAudioInputStream(stereoDesiredFormat, AudioSystem.getAudioInputStream(file));

      InputStream mixed = new InputStream()
      {
        byte[] monobuf = new byte[16384];
        int offset = 0;
        int length = 0;

        long bytesRead = 0L;

        public int read() throws IOException
        {
          if (this.offset < this.length) {
            this.bytesRead += 1L;
            return this.monobuf[(this.offset++)] & 0xFF;
          }
          this.length = stereoIn.read(this.monobuf);
          if (this.length <= 0) {
            AudioFileUtils.logger.fine("reached EOF on original input stream (read " + this.length + " bytes)");
            return -1;
          }

          for (int i = 0; i < this.length; i += 4)
          {
            int rl;
            int lh;
            int ll;
            int rh;
            if (desiredFormat.isBigEndian()) {
              lh = this.monobuf[(i + 0)];
              ll = this.monobuf[(i + 1)] & 0xFF;
              rh = this.monobuf[(i + 2)];
              rl = this.monobuf[(i + 3)] & 0xFF;
            } else {
              lh = this.monobuf[(i + 1)];
              ll = this.monobuf[(i + 0)] & 0xFF;
              rh = this.monobuf[(i + 3)];
              rl = this.monobuf[(i + 2)] & 0xFF;
            }
            int left = lh << 8 | ll;
            int right = rh << 8 | rl;
            int mixed = (left + right) / 2;
            if (desiredFormat.isBigEndian()) {
              this.monobuf[(i / 2 + 1)] = (byte)(mixed & 0xFF);
              this.monobuf[(i / 2 + 0)] = (byte)(mixed >> 8 & 0xFF);
            } else {
              this.monobuf[(i / 2 + 0)] = (byte)(mixed & 0xFF);
              this.monobuf[(i / 2 + 1)] = (byte)(mixed >> 8 & 0xFF);
            }
          }
          this.length /= 2;
          this.offset = 0;
          return this.monobuf[(this.offset++)] & 0xFF;
        }

        public synchronized void mark(int readlimit)
        {
          throw new UnsupportedOperationException("Mark not supported");
        }
      };
      logger.info("Creating 1-channel mixed input stream from stereo source");
      return new AudioInputStream(mixed, desiredFormat, stereoIn.getFrameLength());
    }
    throw new UnsupportedAudioFileException("Unsupported number of channels: " + fileFormat.getFormat().getChannels());
  }
}