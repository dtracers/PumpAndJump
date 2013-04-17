package net.bluecow.spectro.detection.tempo;

import java.util.ArrayList;

import net.bluecow.spectro.detection.Beat;

public class RegressionDetection extends TempoDetector
{

	public RegressionDetection(ArrayList<Beat> beats) {
		super(beats);
	}

	@Override
	public void detectTempo(int startIndex) {
	}

	@Override
	public void setTempoBeats() {
	}

}
